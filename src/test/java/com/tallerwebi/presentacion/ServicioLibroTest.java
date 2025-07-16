package com.tallerwebi.presentacion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallerwebi.dominio.LibroFavorito;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.FavoritoYaExisteException;
import com.tallerwebi.dominio.googleBookAPI.ApiResponse;
import com.tallerwebi.dominio.googleBookAPI.Item;
import com.tallerwebi.repositorioInterfaz.RepositorioLibroFavorito;
import com.tallerwebi.repositorioInterfaz.RepositorioUsuario;
import com.tallerwebi.serviciosImplementacion.ServicioLibroImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServicioLibroTest {

    private RepositorioLibroFavorito repositorioLibroFavoritoMock;
    private RepositorioUsuario repositorioUsuarioMock;
    private HttpClient httpClientMock;
    private ObjectMapper objectMapperMock;
    private HttpResponse httpResponseMock;

    private ServicioLibroImpl servicioLibro;
    private final String FAKE_API_KEY = "test-key";

    @BeforeEach
    public void init() throws NoSuchFieldException, IllegalAccessException {
        repositorioLibroFavoritoMock = mock(RepositorioLibroFavorito.class);
        repositorioUsuarioMock = mock(RepositorioUsuario.class);
        httpClientMock = mock(HttpClient.class);
        objectMapperMock = mock(ObjectMapper.class);
        httpResponseMock = mock(HttpResponse.class);

        servicioLibro = new ServicioLibroImpl(httpClientMock, objectMapperMock, repositorioLibroFavoritoMock, repositorioUsuarioMock);

        Field apiKeyField = ServicioLibroImpl.class.getDeclaredField("apiKey");
        apiKeyField.setAccessible(true);
        apiKeyField.set(servicioLibro, FAKE_API_KEY);
    }

    @Test
    public void queAlAgregarUnLibroAFavoritosSeGuardeCorrectamente() throws FavoritoYaExisteException {
        Long usuarioId = 1L;
        String idGoogleBook = "book123";
        Usuario usuarioMock = new Usuario();
        when(repositorioUsuarioMock.buscarPorId(usuarioId)).thenReturn(usuarioMock);

        servicioLibro.agregarLibroAFavoritos(usuarioId, idGoogleBook);


        ArgumentCaptor<LibroFavorito> captor = ArgumentCaptor.forClass(LibroFavorito.class);
        verify(repositorioLibroFavoritoMock).guardar(captor.capture());

        LibroFavorito libroGuardado = captor.getValue();
        assertEquals(usuarioMock, libroGuardado.getUsuario());
        assertEquals(idGoogleBook, libroGuardado.getIdGoogleBook());
    }


    @Test
    public void queAlObtenerIdsDeFavoritosDevuelvaLosIdsCorrectosParaUnUsuario() {
        Long usuarioId = 1L;
        Usuario usuarioMock = new Usuario();
        List<String> idsEsperados = List.of("id1", "id2", "id3");

        when(repositorioUsuarioMock.buscarPorId(usuarioId)).thenReturn(usuarioMock);
        when(repositorioLibroFavoritoMock.obtenerIdsPorUsuario(usuarioMock)).thenReturn(idsEsperados);

        Set<String> idsObtenidos = servicioLibro.obtenerIdsDeLibrosFavoritos(usuarioId);

        assertNotNull(idsObtenidos);
        assertEquals(3, idsObtenidos.size());
        assertTrue(idsObtenidos.containsAll(idsEsperados));
    }

    @Test
    public void queAlEliminarUnFavoritoSeLlameAlRepositorioConLosDatosCorrectos() {
        Long usuarioId = 1L;
        String idGoogleBook = "book123";
        Usuario usuarioMock = new Usuario();
        when(repositorioUsuarioMock.buscarPorId(usuarioId)).thenReturn(usuarioMock);

        servicioLibro.eliminarLibroDeFavoritos(usuarioId, idGoogleBook);

        verify(repositorioLibroFavoritoMock).eliminar(usuarioMock, idGoogleBook);
    }

    @Test
    public void queAlObtenerLibrosSeDevuelvaUnaListaCuandoLaApiRespondeCorrectamente() throws IOException, InterruptedException {
        String jsonRespuesta = "{\"items\":[{\"id\":\"book1\"}]}";
        ApiResponse apiResponseMock = new ApiResponse();
        apiResponseMock.setItems(List.of(new Item()));

        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(jsonRespuesta);
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponseMock);
        when(objectMapperMock.readValue(jsonRespuesta, ApiResponse.class)).thenReturn(apiResponseMock);

        List<Item> libros = servicioLibro.obtenerLibros();

        assertNotNull(libros);
        assertEquals(1, libros.size());
        verify(httpClientMock).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    public void queAlObtenerLibrosSeDevuelvaListaVaciaSiLaApiFalla() throws IOException, InterruptedException {
        when(httpResponseMock.statusCode()).thenReturn(500);
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponseMock);

        List<Item> libros = servicioLibro.obtenerLibros();

        assertNotNull(libros);
        assertTrue(libros.isEmpty());
    }

    @Test
    public void queAlObtenerLibrosFavoritosDelUsuarioSeDevuelvaLaListaDeLibrosCompleta() throws IOException, InterruptedException {
        Long usuarioId = 1L;
        Usuario usuarioMock = new Usuario();
        Set<String> idsFavoritos = Set.of("id1", "id2");

        Item libro1 = new Item();
        libro1.setId("id1");
        Item libro2 = new Item();
        libro2.setId("id2");

        when(repositorioUsuarioMock.buscarPorId(usuarioId)).thenReturn(usuarioMock);
        when(repositorioLibroFavoritoMock.obtenerIdsPorUsuario(usuarioMock)).thenReturn(List.copyOf(idsFavoritos));

        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(200);


        when(objectMapperMock.readValue((String) httpResponseMock.body(), Item.class))
                .thenReturn(libro1)
                .thenReturn(libro2);

        List<Item> librosFavoritos = servicioLibro.obtenerLibrosFavoritosDelUsuario(usuarioId);

        assertEquals(2, librosFavoritos.size());
        assertTrue(librosFavoritos.stream().anyMatch(l -> l.getId().equals("id1")));
        assertTrue(librosFavoritos.stream().anyMatch(l -> l.getId().equals("id2")));
        verify(httpClientMock, times(2)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }
}