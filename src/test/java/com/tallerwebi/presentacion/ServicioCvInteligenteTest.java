package com.tallerwebi.presentacion;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.servicios.ServicioUsuarioMateria;
import com.tallerwebi.repositorioInterfaz.RepositorioUsuario;
import com.tallerwebi.serviciosImplementacion.ServicioCvInteligenteImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyString;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ServicioCvInteligenteTest {

    private ServicioCvInteligenteImpl servicio;
    private ServicioUsuarioMateria servicioUsuarioMateriaMock;
    private RepositorioUsuario repositorioUsuarioMock;
    private RestTemplate restTemplateMock;

    // ---------- utilidades ----------

    private Usuario usuarioConCarrera() {
        Carrera carrera = new Carrera();
        carrera.setId(10L);
        carrera.setNombre("Ingeniería en Sistemas");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Agustín");
        usuario.setApellido("Pérez");
        usuario.setEmail("agustin@example.com");
        usuario.setCarrera(carrera);
        return usuario;
    }

    private List<UsuarioMateria> listaMaterias() {
        Materia prog1 = new Materia();
        prog1.setNombre("Programación I");

        UsuarioMateria um = new UsuarioMateria();
        um.setMateria(prog1);

        return List.of(um);
    }

    private ResponseEntity<String> respuestaGemini(String texto) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(
                /* estructura equivalente a:
                   { "candidates": [ { "content": { "parts": [ { "text": texto } ] } } ] } */
                mapper.createObjectNode()
                        .putArray("candidates")
                        .addObject()
                        .putObject("content")
                        .putArray("parts")
                        .addObject()
                        .put("text", texto)
        );
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    // ---------- set‑up ----------

    @BeforeEach
    public void setUp() {
        servicioUsuarioMateriaMock = mock(ServicioUsuarioMateria.class);
        repositorioUsuarioMock    = mock(RepositorioUsuario.class);
        restTemplateMock          = mock(RestTemplate.class);

        servicio = new ServicioCvInteligenteImpl(
                servicioUsuarioMateriaMock,
                repositorioUsuarioMock,
                restTemplateMock
        );
    }

    @Test
    public void queNoGenereCvSiUsuarioNoExiste() {
        Long idUsuario = 99L;
        when(repositorioUsuarioMock.buscarPorId(idUsuario)).thenReturn(null);

        String resultado = servicio.generarCv(idUsuario);

        assertThat(resultado, is("Usuario no encontrado"));
        verifyNoInteractions(servicioUsuarioMateriaMock, restTemplateMock);
    }

    @Test
    public void queNoGenereCvSiUsuarioSinCarrera() {
        Usuario usuario = new Usuario();
        usuario.setId(2L);          // sin carrera
        when(repositorioUsuarioMock.buscarPorId(usuario.getId())).thenReturn(usuario);

        String resultado = servicio.generarCv(usuario.getId());

        assertThat(resultado, is("Usuario no tiene carrera asignada"));
        verifyNoInteractions(servicioUsuarioMateriaMock, restTemplateMock);
    }


    @Test
    public void queDevuelvaMensajeSiGeminiRespondeVacio() {
        Usuario usuario = usuarioConCarrera();

        when(repositorioUsuarioMock.buscarPorId(usuario.getId())).thenReturn(usuario);
        when(servicioUsuarioMateriaMock.mostrarMateriasDeUsuario(anyString(), eq(usuario.getId())))
                .thenReturn(listaMaterias());

        ResponseEntity<String> responseVacio = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplateMock.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseVacio);

        String resultado = servicio.generarCv(usuario.getId());

        assertThat(resultado, is("Respuesta vacía de Gemini."));
    }

    @Test
    public void queDevuelvaMensajeSiGeminiNoDevuelveCandidatos() {
        Usuario usuario = usuarioConCarrera();

        when(repositorioUsuarioMock.buscarPorId(usuario.getId())).thenReturn(usuario);
        when(servicioUsuarioMateriaMock.mostrarMateriasDeUsuario(anyString(), eq(usuario.getId())))
                .thenReturn(listaMaterias());

        String jsonSinCandidatos = "{}"; // estructura inválida
        when(restTemplateMock.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(jsonSinCandidatos, HttpStatus.OK));

        String resultado = servicio.generarCv(usuario.getId());

        assertThat(resultado, is("Respuesta inválida de Gemini: sin candidatos."));
    }
}
