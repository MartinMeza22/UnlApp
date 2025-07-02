package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Comentario;
import com.tallerwebi.infraestructura.RepositorioComentarioImpl;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class RepositorioComentarioTest {

    private SessionFactory sessionFactoryMock;
    private Session sessionMock;
    private RepositorioComentarioImpl repositorioComentario;

    @BeforeEach
    public void init() {
        sessionFactoryMock = mock(SessionFactory.class);
        sessionMock = mock(Session.class);
        repositorioComentario = new RepositorioComentarioImpl(sessionFactoryMock);

        when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);
    }

    @Test
    public void alGuardarUnComentarioDeberiaLlamarAlMetodoSaveDeLaSesion() {
        // Preparación
        Comentario comentario = new Comentario();

        // Ejecución
        repositorioComentario.guardar(comentario);

        // Validación
        verify(sessionMock, times(1)).save(comentario);
    }

    @Test
    public void alBuscarUnComentarioPorIdDeberiaLlamarAlMetodoGetDeLaSesion() {
        // Preparación
        Long idBuscado = 1L;

        // Ejecución
        repositorioComentario.buscarPorId(idBuscado);

        // Validación
        verify(sessionMock, times(1)).get(Comentario.class, idBuscado);
    }

    @Test
    public void alEliminarUnComentarioDeberiaLlamarAlMetodoDeleteDeLaSesion() {
        // Preparación
        Comentario comentarioAEliminar = new Comentario();

        // Ejecución
        repositorioComentario.eliminar(comentarioAEliminar);

        // Validación
        verify(sessionMock, times(1)).delete(comentarioAEliminar);
    }

}