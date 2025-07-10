package com.tallerwebi.presentacion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallerwebi.dominio.googleBookAPI.ApiResponse;
import com.tallerwebi.dominio.googleBookAPI.Item;
import com.tallerwebi.dominio.servicios.ServicioLibro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations; // Importante: Importar esta clase
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ServicioLibroTest {

    @Mock
    private HttpClient httpClientMock;

    @Mock
    private ObjectMapper objectMapperMock;

    @InjectMocks
    private ServicioLibro servicioLibro;

    @Mock
    private HttpResponse<String> httpResponseMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ReflectionTestUtils.setField(servicioLibro, "apiKey", "test-api-key");
    }

    @Test
    public void queAlConsultarLibrosConExitoDevuelvaUnaListaDeItems() throws IOException, InterruptedException {
        String jsonRespuestaSimulado = "{ \"items\": [ { \"id\": \"1\" }, { \"id\": \"2\" } ] }";
        ApiResponse apiResponseSimulada = new ApiResponse();
        List<Item> listaDeItemsEsperada = Arrays.asList(new Item(), new Item());
        apiResponseSimulada.setItems(listaDeItemsEsperada);

        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(jsonRespuestaSimulado);
        when(objectMapperMock.readValue(jsonRespuestaSimulado, ApiResponse.class))
                .thenReturn(apiResponseSimulada);

        List<Item> resultado = servicioLibro.obtenerLibros();

        assertThat(resultado, is(notNullValue()));
        assertThat(resultado.size(), is(2));
        assertThat(resultado, is(equalTo(listaDeItemsEsperada)));
        verify(httpClientMock).send(any(HttpRequest.class), any());
        verify(objectMapperMock).readValue(jsonRespuestaSimulado, ApiResponse.class);
    }

    @Test
    public void queSiLaApiDevuelveUnErrorDeServidorDevuelvaNull() throws IOException, InterruptedException {
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(500);

        List<Item> resultado = servicioLibro.obtenerLibros();

        assertThat(resultado, is(nullValue()));
        verify(objectMapperMock, never()).readValue(anyString(), any(Class.class));
    }

    @Test
    public void queSiOcurreUnaExcepcionAlEnviarLaPeticionSeLanceRuntimeException() throws IOException, InterruptedException {
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("Error de red simulado"));

        assertThrows(RuntimeException.class, () -> {
            servicioLibro.obtenerLibros();
        });

        verify(objectMapperMock, never()).readValue(anyString(), any(Class.class));
    }

    @Test
    public void queSiLaRespuestaEsExitosaPeroLaListaDeItemsEsNulaDevuelvaNull() throws IOException, InterruptedException {
        String jsonRespuestaSimulado = "{ \"items\": null }";
        ApiResponse apiResponseSimulada = new ApiResponse();
        apiResponseSimulada.setItems(null);

        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(jsonRespuestaSimulado);
        when(objectMapperMock.readValue(jsonRespuestaSimulado, ApiResponse.class))
                .thenReturn(apiResponseSimulada);

        List<Item> resultado = servicioLibro.obtenerLibros();

        assertThat(resultado, is(nullValue()));
    }

    @Test
    public void queSiLaRespuestaEsExitosaPeroLaListaDeItemsEstaVaciaDevuelvaUnaListaVacia() throws IOException, InterruptedException {
        String jsonRespuestaSimulado = "{ \"items\": [] }";
        ApiResponse apiResponseSimulada = new ApiResponse();
        apiResponseSimulada.setItems(Collections.emptyList());

        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(jsonRespuestaSimulado);
        when(objectMapperMock.readValue(jsonRespuestaSimulado, ApiResponse.class))
                .thenReturn(apiResponseSimulada);

        List<Item> resultado = servicioLibro.obtenerLibros();

        assertThat(resultado, is(notNullValue()));
        assertThat(resultado, is(empty()));
    }

    @Test
    public void queSiElCuerpoDeLaRespuestaEsInvalidoSeLanceRuntimeException() throws IOException, InterruptedException {
        String cuerpoInvalido = "esto no es un json";
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(cuerpoInvalido);
        when(objectMapperMock.readValue(cuerpoInvalido, ApiResponse.class))
                .thenThrow(new JsonProcessingException("Error de parseo simulado"){});

        assertThrows(RuntimeException.class, () -> {
            servicioLibro.obtenerLibros();
        });
    }

    @Test
    public void queSeConstruyaLaUriCorrectamenteConLosParametrosEsperados() throws IOException, InterruptedException {
        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(500);

        servicioLibro.obtenerLibros();

        verify(httpClientMock).send(requestCaptor.capture(), any());
        HttpRequest peticionCapturada = requestCaptor.getValue();
        String uri = peticionCapturada.uri().toString();

        assertThat(uri, containsString("https://www.googleapis.com/books/v1/volumes"));
        assertThat(uri, containsString("q=desarrollo%20web"));
        assertThat(uri, containsString("maxResult=40"));
        assertThat(uri, containsString("key=test-api-key"));
    }
}
