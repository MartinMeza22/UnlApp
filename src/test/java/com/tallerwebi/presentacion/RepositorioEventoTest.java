package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Evento;
import com.tallerwebi.infraestructura.RepositorioEventoImpl;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org. mockito.Mockito.*;

public class RepositorioEventoTest {

    private SessionFactory sessionFactoryMock;
    private Session sessionMock;
    private RepositorioEventoImpl repositorioEvento;

    @BeforeEach
    public void init() {
        sessionFactoryMock = mock(SessionFactory.class);
        sessionMock = mock(Session.class);
        repositorioEvento = new RepositorioEventoImpl(sessionFactoryMock);

        when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);
    }

    @Test
    public void alGuardarUnEventoDeberiaLlamarAlMetodoSaveDeLaSesion() {
        // Preparación
        Evento evento = new Evento();

        // Ejecución
        repositorioEvento.guardar(evento);

        // Validación
        verify(sessionMock, times(1)).save(evento);
    }

    @Test
    public void alBuscarUnEventoPorIdDeberiaLlamarAlMetodoGetDeLaSesion() {
        // Preparación
        Long idBuscado = 1L;

        // Ejecución
        repositorioEvento.buscarPorId(idBuscado);

        // Validación
        verify(sessionMock, times(1)).get(Evento.class, idBuscado);
    }

    @Test
    public void alActualizarUnEventoDeberiaLlamarAlMetodoUpdateDeLaSesion() {
        // Preparación
        Evento eventoAActualizar = new Evento();

        // Ejecución
        repositorioEvento.actualizar(eventoAActualizar);

        // Validación
        verify(sessionMock, times(1)).update(eventoAActualizar);
    }

    @Test
    public void alEliminarUnEventoDeberiaLlamarAlMetodoDeleteDeLaSesion() {
        // Preparación
        Long idAEliminar = 1L;
        Evento eventoMock = new Evento();
        when(sessionMock.get(Evento.class, idAEliminar)).thenReturn(eventoMock);

        // Ejecución
        repositorioEvento.eliminar(idAEliminar);

        // Validación
        verify(sessionMock, times(1)).get(Evento.class, idAEliminar);
        verify(sessionMock, times(1)).delete(eventoMock);
    }
}