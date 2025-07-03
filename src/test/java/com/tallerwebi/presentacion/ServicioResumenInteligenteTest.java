package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.DTO.MateriaDTO;
import com.tallerwebi.dominio.RepositorioResumenInteligente;
import com.tallerwebi.dominio.ResumenInteligente;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.dominio.servicios.ServicioProgreso;
import com.tallerwebi.dominio.servicios.ServicioResumenInteligente;
import com.tallerwebi.servicioInterfaz.ServicioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class ServicioResumenInteligenteTest {

    private ServicioResumenInteligente servicio;
    private RestTemplate restTemplateMock;
    private ServicioProgreso servicioProgresoMock;
    private ServicioUsuario servicioUsuarioMock;
    private RepositorioResumenInteligente repositorioResumenMock;

    @BeforeEach
    public void init() {
        restTemplateMock = mock(RestTemplate.class);
        servicioProgresoMock = mock(ServicioProgreso.class);
        servicioUsuarioMock = mock(ServicioUsuario.class);
        repositorioResumenMock = mock(RepositorioResumenInteligente.class);

        servicio = new ServicioResumenInteligente(
                restTemplateMock,
                servicioUsuarioMock,
                repositorioResumenMock
        );
    }

    @Test
    public void queSeGenereUnResumenCorrectamenteDesdeLaApi() {
        // Arrange
        MateriaDTO materia = new MateriaDTO(1L, "Matemática", "Dificil", "APROBADA", 10, 1, true);

        // Simular respuesta de la API
        Map<String, Object> body = new HashMap<>();
        body.put("candidates", List.of(Map.of(
                "content", Map.of("parts", List.of(Map.of("text", "Este es un resumen simulado.")))
        )));

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplateMock.postForEntity(
                anyString(),
                org.mockito.ArgumentMatchers.<HttpEntity<Map<String, Object>>>any(),
                eq(Map.class)
        )).thenReturn(responseEntity);

        String prompt = servicio.generarPrompt(List.of(materia), 90.0);
        String resumen = servicio.generarResumenDesdePrompt(prompt);

        assertThat(resumen, is(notNullValue()));
        assertThat(resumen.trim(), not(isEmptyString()));
    }

    @Test
    public void queSeGuardeElResumenCorrectamente() throws UsuarioNoEncontrado {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Agustín");

        when(servicioUsuarioMock.obtenerUsuario(anyLong())).thenReturn(usuario);

        servicio.guardarResumen("Este es un resumen guardado", usuario);

        verify(repositorioResumenMock).guardar(org.mockito.ArgumentMatchers.any(ResumenInteligente.class));    }

    @Test
    public void queNoSeGenereResumenHistoricoSiNoHayResumens() throws UsuarioNoEncontrado {
        // Arrange
        Long usuarioId = 1L;
        Usuario usuario = new Usuario();

        when(servicioUsuarioMock.obtenerUsuario(usuarioId)).thenReturn(usuario);
        when(repositorioResumenMock.obtenerPorUsuario(usuario)).thenReturn(List.of()); // lista vacía

        // Act
        String resultado = servicio.generarResumenHistorico(usuarioId);

        // Assert
        assertThat(resultado, is("Todavía no hay suficientes resúmenes para analizar tu historial."));
    }

    @Test
    public void queSeGenereResumenHistoricoSiHayResumens() throws UsuarioNoEncontrado {
        // Arrange
        Long usuarioId = 1L;
        Usuario usuario = new Usuario();
        ResumenInteligente resumen = new ResumenInteligente();
        resumen.setFechaGeneracion(LocalDate.parse("2025-07-02"));
        resumen.setResumen("Este es un resumen académico histórico.");

        List<ResumenInteligente> listaResumens = List.of(resumen);

        when(servicioUsuarioMock.obtenerUsuario(usuarioId)).thenReturn(usuario);
        when(repositorioResumenMock.obtenerPorUsuario(usuario)).thenReturn(listaResumens);

        // Simular respuesta de la API igual que en tu test de generarResumenDesdePrompt
        Map<String, Object> body = new HashMap<>();
        body.put("candidates", List.of(Map.of(
                "content", Map.of("parts", List.of(Map.of("text", "Este es un resumen simulado.")))
        )));
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplateMock.postForEntity(
                anyString(),
                org.mockito.ArgumentMatchers.<HttpEntity<Map<String, Object>>>any(),
                eq(Map.class)
        )).thenReturn(responseEntity);

        // Act
        String resultado = servicio.generarResumenHistorico(usuarioId);

        // Assert
        assertThat(resultado, is(notNullValue()));
        assertThat(resultado.trim(), not(isEmptyString()));
        assertThat(resultado, containsString("Este es un resumen simulado."));
    }

}
