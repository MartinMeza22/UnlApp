package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.LibroFavorito;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.infraestructura.RepositorioLibroFavoritoImpl;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RepositorioLibroFavoritoTest {

    private SessionFactory sessionFactoryMock;
    private Session sessionMock;
    private Query queryMock;
    private RepositorioLibroFavoritoImpl repositorio;

    @BeforeEach
    public void init() {
        sessionFactoryMock = mock(SessionFactory.class);
        sessionMock = mock(Session.class);
        queryMock = mock(Query.class);

        repositorio = new RepositorioLibroFavoritoImpl(sessionFactoryMock);

        when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);
    }

    @Test
    public void queAlGuardarUnLibroFavoritoSeLlameAlMetodoSaveDeLaSesion() {
        LibroFavorito libroFavorito = new LibroFavorito();

        repositorio.guardar(libroFavorito);

        verify(sessionMock, times(1)).save(libroFavorito);
    }

    @Test
    public void queAlVerificarSiExisteUnFavoritoSeConstruyaLaQueryCorrectaYDevuelvaTrueSiExiste() {
        Usuario usuario = new Usuario();
        String idGoogleBook = "book123";
        when(queryMock.getSingleResult()).thenReturn(1L);
        when(sessionMock.createQuery(anyString(), eq(Long.class))).thenReturn(queryMock);

        boolean existe = repositorio.existe(usuario, idGoogleBook);

        assertTrue(existe);
        verify(queryMock).setParameter("usuario", usuario);
        verify(queryMock).setParameter("idGoogleBook", idGoogleBook);
    }

    @Test
    public void queAlVerificarSiExisteUnFavoritoDevuelvaFalseSiNoExiste() {
        Usuario usuario = new Usuario();
        String idGoogleBook = "book123";
        when(queryMock.getSingleResult()).thenReturn(0L);
        when(sessionMock.createQuery(anyString(), eq(Long.class))).thenReturn(queryMock);

        boolean existe = repositorio.existe(usuario, idGoogleBook);

        assertFalse(existe);
    }

    @Test
    public void queAlObtenerIdsPorUsuarioSeDevuelvaLaListaCorrecta() {
        Usuario usuario = new Usuario();
        List<String> idsEsperados = List.of("id1", "id2");
        when(queryMock.getResultList()).thenReturn(idsEsperados);
        when(sessionMock.createQuery(anyString(), eq(String.class))).thenReturn(queryMock);

        List<String> idsObtenidos = repositorio.obtenerIdsPorUsuario(usuario);

        assertNotNull(idsObtenidos);
        assertEquals(2, idsObtenidos.size());
        assertEquals(idsEsperados, idsObtenidos);
        verify(queryMock).setParameter("usuario", usuario);
    }

    @Test
    public void queAlEliminarUnFavoritoSeEjecuteUnaQueryDeUpdate() {
        Usuario usuario = new Usuario();
        String idGoogleBook = "book123";
        when(sessionMock.createQuery(anyString())).thenReturn(queryMock);
        when(queryMock.setParameter(anyString(), any())).thenReturn(queryMock);

        repositorio.eliminar(usuario, idGoogleBook);

        verify(queryMock, times(1)).executeUpdate();

        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(sessionMock).createQuery(queryCaptor.capture());

        String queryString = queryCaptor.getValue();
        assertTrue(queryString.contains("DELETE FROM LibroFavorito"));
        assertTrue(queryString.contains("lf.usuario = :usuario"));
        assertTrue(queryString.contains("lf.idGoogleBook = :idGoogleBook"));
    }
}