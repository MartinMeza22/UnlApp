package com.tallerwebi.presentacion;


import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.dominio.servicios.ResumenUsuario;
import com.tallerwebi.dominio.servicios.ServicioGeneradorResumen;
import com.tallerwebi.repositorioInterfaz.RepositorioResumenUsuario;
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



public class ServicioGeneradorResumenTest {

    private ServicioGeneradorResumen servicio;
    private RestTemplate restTemplateMock;
    private ServicioUsuario servicioUsuarioMock;
    private RepositorioResumenUsuario repositorioResumenMock;

    @BeforeEach
    public void init() {
        restTemplateMock = mock(RestTemplate.class);
        servicioUsuarioMock = mock(ServicioUsuario.class);
        repositorioResumenMock = mock(RepositorioResumenUsuario.class);

        servicio = new ServicioGeneradorResumen(
                restTemplateMock,
                servicioUsuarioMock,
                repositorioResumenMock
        );
    }

    @Test
    public void queSeGenereUnResumenCorrectamenteDesdeLaApi() {
        // Arrange
        Map<String, Object> body = new HashMap<>();
        body.put("candidates", List.of(Map.of(
                "content", Map.of("parts", List.of(Map.of("text", "Resumen generado sobre el tema")))
        )));

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplateMock.postForEntity(
                anyString(),
                org.mockito.ArgumentMatchers.<HttpEntity<Map<String, Object>>>any(),
                eq(Map.class)
        )).thenReturn(responseEntity);

        // Act
        String resumen = servicio.obtenerResumenDesdeGemini("Tema de prueba");

        // Assert
        assertThat(resumen, is(notNullValue()));
        assertThat(resumen.trim(), not(isEmptyString()));
    }

    @Test
    public void queSeGuardeElResumenCorrectamente() throws UsuarioNoEncontrado {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");

        when(servicioUsuarioMock.obtenerUsuario(anyLong())).thenReturn(usuario);

        Map<String, Object> body = new HashMap<>();
        body.put("candidates", List.of(Map.of(
                "content", Map.of("parts", List.of(Map.of("text", "Resumen generado")))
        )));

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplateMock.postForEntity(
                anyString(),
                org.mockito.ArgumentMatchers.<HttpEntity<Map<String, Object>>>any(),
                eq(Map.class)
        )).thenReturn(responseEntity);

        // Act
        String resultado = servicio.generarYGuardarResumen("Tema importante", 1L);

        // Assert
        assertThat(resultado, is(notNullValue()));
        verify(repositorioResumenMock).guardar(org.mockito.ArgumentMatchers.any(ResumenUsuario.class));
    }

    @Test
    public void queSePuedanObtenerResumenesSiExisten() throws UsuarioNoEncontrado {
        // Arrange
        Long usuarioId = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        ResumenUsuario resumen = new ResumenUsuario();
        resumen.setId(1L);
        resumen.setUsuario(usuario);
        resumen.setFechaGeneracion(LocalDate.now().atStartOfDay());
        resumen.setTextoOriginal("Tema prueba");
        resumen.setResumenGenerado("Resumen de prueba");

        when(servicioUsuarioMock.obtenerUsuario(usuarioId)).thenReturn(usuario);
        when(repositorioResumenMock.obtenerPorUsuarioId(usuarioId)).thenReturn(List.of(resumen));

        // Act
        List<ResumenUsuario> resultados = servicio.obtenerResumenesDelUsuario(usuarioId);

        // Assert
        assertThat(resultados, hasSize(1));
        assertThat(resultados.get(0).getResumenGenerado(), is("Resumen de prueba"));
    }
}