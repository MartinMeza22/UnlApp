package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.*;
import com.tallerwebi.repositorioInterfaz.*;
import com.tallerwebi.serviciosImplementacion.ServicioReporteImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ServicioReporteTest {

    private RepositorioReporte repositorioReporteMock;
    private RepositorioPublicacion repositorioPublicacionMock;
    private RepositorioComentario repositorioComentarioMock;
    private RepositorioUsuario repositorioUsuarioMock;
    private ServicioReporteImpl servicioReporte;

    private final Long ID_PUBLICACION = 1L;
    private final Long ID_COMENTARIO = 2L;
    private final Long ID_USUARIO = 3L;
    private final Long ID_REPORTE = 4L;
    private Publicacion publicacionMock;
    private Comentario comentarioMock;
    private Usuario usuarioMock;

    @BeforeEach
    void init() {
        repositorioReporteMock = mock(RepositorioReporte.class);
        repositorioPublicacionMock = mock(RepositorioPublicacion.class);
        repositorioComentarioMock = mock(RepositorioComentario.class);
        repositorioUsuarioMock = mock(RepositorioUsuario.class);
        servicioReporte = new ServicioReporteImpl(repositorioReporteMock, repositorioPublicacionMock, repositorioComentarioMock, repositorioUsuarioMock);

        publicacionMock = mock(Publicacion.class);
        comentarioMock = mock(Comentario.class);
        usuarioMock = mock(Usuario.class);

        when(repositorioPublicacionMock.buscarPorId(ID_PUBLICACION)).thenReturn(publicacionMock);
        when(repositorioComentarioMock.buscarPorId(ID_COMENTARIO)).thenReturn(comentarioMock);
        when(repositorioUsuarioMock.buscarPorId(ID_USUARIO)).thenReturn(usuarioMock);
    }

    @Test
    public void alCrearReporteParaPublicacionCorrectamenteNoLanzaExcepcion() {
        when(repositorioReporteMock.buscarReportePorUsuarioYPublicacion(usuarioMock, publicacionMock)).thenReturn(null);
        when(publicacionMock.getReportes()).thenReturn(new HashSet<>());
        assertDoesNotThrow(() -> servicioReporte.crearReporteParaPublicacion(ID_PUBLICACION, ID_USUARIO, "spam", null));
        verify(repositorioReporteMock, times(1)).guardar(any(Reporte.class));
    }

    @Test
    public void siLaPublicacionNoExisteAlReportarLanzaPublicacionInexistente() {
        when(repositorioPublicacionMock.buscarPorId(ID_PUBLICACION)).thenReturn(null);
        assertThrows(PublicacionInexistente.class, () -> servicioReporte.crearReporteParaPublicacion(ID_PUBLICACION, ID_USUARIO, "spam", null));
    }

    @Test
    public void siElUsuarioYaReportoLaPublicacionLanzaReporteExistente() {
        when(repositorioReporteMock.buscarReportePorUsuarioYPublicacion(usuarioMock, publicacionMock)).thenReturn(new Reporte());
        assertThrows(ReporteExistente.class, () -> servicioReporte.crearReporteParaPublicacion(ID_PUBLICACION, ID_USUARIO, "spam", null));
    }

    @Test
    public void siPublicacionAlcanzaLimiteDeReportesEsEliminada() throws PublicacionInexistente, UsuarioNoEncontrado, ReporteExistente {
        when(repositorioReporteMock.buscarReportePorUsuarioYPublicacion(usuarioMock, publicacionMock)).thenReturn(null);

        Set<Reporte> reportes = new HashSet<>();
        // Se simulan 5 reportes para que la condición '>= 5' se cumpla.
        // La lógica del servicio no añade el nuevo reporte a la colección del mock,
        // por lo que el tamaño de la colección debe ser >= 5 desde el principio del test para que la condición de borrado sea verdadera.
        for (int i = 0; i < 5; i++) {
            reportes.add(new Reporte());
        }
        when(publicacionMock.getReportes()).thenReturn(reportes);

        servicioReporte.crearReporteParaPublicacion(ID_PUBLICACION, ID_USUARIO, "spam", null);

        verify(repositorioPublicacionMock, times(1)).eliminar(publicacionMock);
    }

    @Test
    public void alCrearReporteParaComentarioCorrectamenteNoLanzaExcepcion() {
        when(repositorioReporteMock.buscarReportePorUsuarioYComentario(usuarioMock, comentarioMock)).thenReturn(null);
        when(comentarioMock.getReportes()).thenReturn(new HashSet<>());
        assertDoesNotThrow(() -> servicioReporte.crearReporteParaComentario(ID_COMENTARIO, ID_USUARIO, "spam", null));
        verify(repositorioReporteMock, times(1)).guardar(any(Reporte.class));
    }

    @Test
    public void siComentarioAlcanzaLimiteDeReportesEsEliminado() throws ComentarioInexistente, UsuarioNoEncontrado, ReporteExistente {
        when(repositorioReporteMock.buscarReportePorUsuarioYComentario(usuarioMock, comentarioMock)).thenReturn(null);

        Set<Reporte> reportes = new HashSet<>();
        // Se simulan 5 reportes por la misma razón que en el test de publicación.
        for (int i = 0; i < 5; i++) {
            reportes.add(new Reporte());
        }
        when(comentarioMock.getReportes()).thenReturn(reportes);

        servicioReporte.crearReporteParaComentario(ID_COMENTARIO, ID_USUARIO, "spam", null);

        verify(repositorioComentarioMock, times(1)).eliminar(comentarioMock);
    }

    @Test
    public void siElComentarioNoExisteAlReportarLanzaComentarioInexistente() {
        when(repositorioComentarioMock.buscarPorId(ID_COMENTARIO)).thenReturn(null);
        assertThrows(ComentarioInexistente.class, () -> servicioReporte.crearReporteParaComentario(ID_COMENTARIO, ID_USUARIO, "spam", null));
    }

    @Test
    public void siElUsuarioYaReportoElComentarioLanzaReporteExistente() {
        when(repositorioReporteMock.buscarReportePorUsuarioYComentario(usuarioMock, comentarioMock)).thenReturn(new Reporte());
        assertThrows(ReporteExistente.class, () -> servicioReporte.crearReporteParaComentario(ID_COMENTARIO, ID_USUARIO, "spam", null));
    }

    @Test
    public void alEliminarReporteExistenteNoLanzaExcepcion() {
        when(repositorioReporteMock.buscarPorId(ID_REPORTE)).thenReturn(new Reporte());
        assertDoesNotThrow(() -> servicioReporte.eliminarReporte(ID_REPORTE));
        verify(repositorioReporteMock, times(1)).eliminar(any(Reporte.class));
    }

    @Test
    public void siElReporteAEliminarNoExisteLanzaReporteInexistente() {
        when(repositorioReporteMock.buscarPorId(ID_REPORTE)).thenReturn(null);
        assertThrows(ReporteInexistente.class, () -> servicioReporte.eliminarReporte(ID_REPORTE));
    }
}