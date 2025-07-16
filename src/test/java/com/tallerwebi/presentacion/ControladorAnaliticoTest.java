package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.servicios.ServicioAnalitico;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ControladorAnaliticoTest {

    private ServicioAnalitico servicioAnaliticoMock;
    private ObjectMapper objectMapperMock;
    private ControladorAnalitico controladorAnalitico;
    private HttpSession sessionMock;
    private final Long ID_USUARIO = 1L;

    @BeforeEach
    void init() {
        servicioAnaliticoMock = mock(ServicioAnalitico.class);
        objectMapperMock = mock(ObjectMapper.class);
        sessionMock = mock(HttpSession.class);

        controladorAnalitico = new ControladorAnalitico();
        controladorAnalitico.servicioAnalitico = servicioAnaliticoMock;
        controladorAnalitico.objectMapper = objectMapperMock;
    }

    @Test
    public void siUsuarioNoEstaLogueadoRetornaUnauthorized() throws Exception {
        // Given
        when(sessionMock.getAttribute("ID")).thenReturn(null);

        // When
        ResponseEntity<String> response = controladorAnalitico.generarAnalitico(sessionMock);

        // Then
        assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
        assertThat(response.getBody(), containsString("Usuario no autenticado"));
        assertThat(response.getHeaders().getFirst("Content-Type"), equalTo("application/json"));
        verify(servicioAnaliticoMock, never()).generarAnaliticoCompleto(anyLong());
    }

    @Test
    public void siUsuarioEstaLogueadoGeneraAnaliticoCorrectamente() throws Exception {
        // Given
        when(sessionMock.getAttribute("ID")).thenReturn(ID_USUARIO);

        Map<String, Object> analiticoCompleto = new HashMap<>();
        analiticoCompleto.put("estudiante", "Juan Pérez");
        analiticoCompleto.put("promedio", 8.5);

        String jsonEsperado = "{\"estudiante\":\"Juan Pérez\",\"promedio\":8.5}";

        when(servicioAnaliticoMock.generarAnaliticoCompleto(ID_USUARIO)).thenReturn(analiticoCompleto);
        when(objectMapperMock.writeValueAsString(analiticoCompleto)).thenReturn(jsonEsperado);

        // When
        ResponseEntity<String> response = controladorAnalitico.generarAnalitico(sessionMock);

        // Then
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getBody(), equalTo(jsonEsperado));
        assertThat(response.getHeaders().getFirst("Content-Type"), equalTo("application/json"));
        verify(servicioAnaliticoMock, times(1)).generarAnaliticoCompleto(ID_USUARIO);
        verify(objectMapperMock, times(1)).writeValueAsString(analiticoCompleto);
    }

    @Test
    public void siServicioLanzaIllegalArgumentExceptionRetornaBadRequest() throws Exception {
        // Given
        when(sessionMock.getAttribute("ID")).thenReturn(ID_USUARIO);
        String mensajeError = "Datos inválidos";
        when(servicioAnaliticoMock.generarAnaliticoCompleto(ID_USUARIO))
                .thenThrow(new IllegalArgumentException(mensajeError));

        // When
        ResponseEntity<String> response = controladorAnalitico.generarAnalitico(sessionMock);

        // Then
        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), containsString("Datos inválidos"));
        assertThat(response.getHeaders().getFirst("Content-Type"), equalTo("application/json"));
    }

    @Test
    public void siServicioLanzaRuntimeExceptionRetornaNotFound() throws Exception {
        // Given
        when(sessionMock.getAttribute("ID")).thenReturn(ID_USUARIO);
        String mensajeError = "Usuario no encontrado";
        when(servicioAnaliticoMock.generarAnaliticoCompleto(ID_USUARIO))
                .thenThrow(new RuntimeException(mensajeError));

        // When
        ResponseEntity<String> response = controladorAnalitico.generarAnalitico(sessionMock);

        // Then
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat(response.getBody(), containsString(mensajeError));
        assertThat(response.getHeaders().getFirst("Content-Type"), equalTo("application/json"));
    }
}