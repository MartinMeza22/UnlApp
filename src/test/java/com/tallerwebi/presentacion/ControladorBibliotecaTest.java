package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.googleBookAPI.Item;
import com.tallerwebi.servicioInterfaz.ServicioLibro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ControladorBibliotecaTest {

    private ServicioLibro servicioLibroMock;
    private Model modelMock;
    private HttpSession sessionMock;
    private RedirectAttributes redirectAttributesMock;
    private ControladorBiblioteca controladorBiblioteca;

    @BeforeEach
    public void init() {
        servicioLibroMock = mock(ServicioLibro.class);
        modelMock = mock(Model.class);
        sessionMock = mock(HttpSession.class);
        redirectAttributesMock = mock(RedirectAttributes.class);

        controladorBiblioteca = new ControladorBiblioteca(servicioLibroMock);
    }

    @Test
    public void queAlMostrarBibliotecaConUsuarioLogueadoSeAgreguenSusFavoritosAlModelo() {
        Long usuarioId = 1L;
        List<Item> libros = Collections.singletonList(new Item());
        Set<String> idsFavoritos = new HashSet<>(Set.of("id1", "id2"));

        when(sessionMock.getAttribute("ID")).thenReturn(usuarioId);
        when(servicioLibroMock.obtenerLibros()).thenReturn(libros);
        when(servicioLibroMock.obtenerIdsDeLibrosFavoritos(usuarioId)).thenReturn(idsFavoritos);

        String vista = controladorBiblioteca.mostrarLibros(modelMock, sessionMock);

        assertEquals("vista-libros", vista);
        verify(modelMock).addAttribute("libros", libros);
        verify(modelMock).addAttribute("librosFavoritosIds", idsFavoritos);
    }

    @Test
    public void queAlMostrarBibliotecaSinUsuarioLogueadoNoSeBusquenLibrosFavoritos() {
        List<Item> libros = Collections.singletonList(new Item());
        when(sessionMock.getAttribute("ID")).thenReturn(null);
        when(servicioLibroMock.obtenerLibros()).thenReturn(libros);

        String vista = controladorBiblioteca.mostrarLibros(modelMock, sessionMock);

        assertEquals("vista-libros", vista);
        verify(modelMock).addAttribute("libros", libros);
        verify(servicioLibroMock, never()).obtenerIdsDeLibrosFavoritos(anyLong());
    }

    @Test
    public void queAlAgregarFavoritoConUsuarioLogueadoSeGuardeYRedirijaConMensajeDeExito() {
        Long usuarioId = 1L;
        String idLibro = "libro123";
        when(sessionMock.getAttribute("ID")).thenReturn(usuarioId);

        String vista = controladorBiblioteca.agregarFavoritos(idLibro, sessionMock, redirectAttributesMock);

        assertEquals("redirect:/biblioteca-digital", vista);
        verify(servicioLibroMock).agregarLibroAFavoritos(usuarioId, idLibro);
        verify(redirectAttributesMock).addFlashAttribute("exito_agregar", "¡Libro guardado en tus favoritos!");
    }

    @Test
    public void queAlAgregarFavoritoSinEstarLogueadoRedirijaConMensajeDeError() {

        String idLibro = "libro123";
        when(sessionMock.getAttribute("ID")).thenReturn(null);

        String vista = controladorBiblioteca.agregarFavoritos(idLibro, sessionMock, redirectAttributesMock);

        assertEquals("redirect:/biblioteca-digital", vista);
        verify(redirectAttributesMock).addFlashAttribute("error", "Debes iniciar sesión para guardar favoritos.");
        verify(servicioLibroMock, never()).agregarLibroAFavoritos(anyLong(), anyString());
    }

    @Test
    public void queSiFallaAlAgregarFavoritoSeMuestreMensajeDeError() {
        Long usuarioId = 1L;
        String idLibro = "libro123";
        when(sessionMock.getAttribute("ID")).thenReturn(usuarioId);
        doThrow(new RuntimeException("Error en DB")).when(servicioLibroMock).agregarLibroAFavoritos(usuarioId, idLibro);

        String vista = controladorBiblioteca.agregarFavoritos(idLibro, sessionMock, redirectAttributesMock);

        assertEquals("redirect:/biblioteca-digital", vista);
        verify(redirectAttributesMock).addFlashAttribute("error", "No se pudo guardar el libro.");
    }

    @Test
    public void queAlEliminarFavoritoSeRedirijaConMensajeDeExito() {
        Long usuarioId = 1L;
        String idLibro = "libro123";
        when(sessionMock.getAttribute("ID")).thenReturn(usuarioId);

        String vista = controladorBiblioteca.eliminarFavorito(idLibro, sessionMock, redirectAttributesMock);

        assertEquals("redirect:/biblioteca-digital", vista);
        verify(servicioLibroMock).eliminarLibroDeFavoritos(usuarioId, idLibro);
        verify(redirectAttributesMock).addFlashAttribute("exito_eliminar", "Libro eliminado de tus favoritos.");
    }

    @Test
    public void queAlEliminarFavoritoEnPaginaDeFavoritosSeRedirijaCorrectamente() {
        Long usuarioId = 1L;
        String idLibro = "libro123";
        when(sessionMock.getAttribute("ID")).thenReturn(usuarioId);

        String vista = controladorBiblioteca.eliminarFavoritoEnFavoritos(idLibro, sessionMock, redirectAttributesMock);

        assertEquals("redirect:/libros-favoritos", vista);
        verify(servicioLibroMock).eliminarLibroDeFavoritos(usuarioId, idLibro);
        verify(redirectAttributesMock).addFlashAttribute("exito_eliminar", "Libro eliminado de tus favoritos.");
    }
}