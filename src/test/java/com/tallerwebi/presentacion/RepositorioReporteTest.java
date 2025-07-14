package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.infraestructura.RepositorioReporteImpl;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class RepositorioReporteTest {

    private SessionFactory sessionFactoryMock;
    private Session sessionMock;
    private RepositorioReporteImpl repositorioReporte;

    @BeforeEach
    void init() {
        sessionFactoryMock = mock(SessionFactory.class);
        sessionMock = mock(Session.class);
        repositorioReporte = new RepositorioReporteImpl(sessionFactoryMock);
        when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);
    }

    @Test
    public void alGuardarUnReporteDebeLlamarASaveDeLaSesion() {
        Reporte reporte = new Reporte();
        repositorioReporte.guardar(reporte);
        verify(sessionMock, times(1)).save(reporte);
    }

    @Test
    public void alBuscarReportePorUsuarioYPublicacionDebeUsarLasRestriccionesCorrectas() {
        Usuario usuario = new Usuario();
        Publicacion publicacion = new Publicacion();
        Criteria criteriaMock = mock(Criteria.class);
        ArgumentCaptor<Criterion> captor = ArgumentCaptor.forClass(Criterion.class);
        when(sessionMock.createCriteria(Reporte.class)).thenReturn(criteriaMock);
        when(criteriaMock.add(any(Criterion.class))).thenReturn(criteriaMock);

        repositorioReporte.buscarReportePorUsuarioYPublicacion(usuario, publicacion);

        verify(criteriaMock, times(2)).add(captor.capture());
        List<Criterion> restrictions = captor.getAllValues();
        assertThat(restrictions.get(0).toString(), equalTo("usuario=" + usuario));
        assertThat(restrictions.get(1).toString(), equalTo("publicacion=" + publicacion));
    }

    @Test
    public void alBuscarReportePorUsuarioYComentarioDebeUsarLasRestriccionesCorrectas() {
        Usuario usuario = new Usuario();
        Comentario comentario = new Comentario();
        Criteria criteriaMock = mock(Criteria.class);
        ArgumentCaptor<Criterion> captor = ArgumentCaptor.forClass(Criterion.class);
        when(sessionMock.createCriteria(Reporte.class)).thenReturn(criteriaMock);
        when(criteriaMock.add(any(Criterion.class))).thenReturn(criteriaMock);

        repositorioReporte.buscarReportePorUsuarioYComentario(usuario, comentario);

        verify(criteriaMock, times(2)).add(captor.capture());
        List<Criterion> restrictions = captor.getAllValues();
        assertThat(restrictions.get(0).toString(), equalTo("usuario=" + usuario));
        assertThat(restrictions.get(1).toString(), equalTo("comentario=" + comentario));
    }

    @Test
    public void alBuscarPorIdDebeLlamarAGetDeLaSesion() {
        Long idReporte = 1L;
        repositorioReporte.buscarPorId(idReporte);
        verify(sessionMock, times(1)).get(Reporte.class, idReporte);
    }

    @Test
    public void alEliminarUnReporteDebeLlamarADeleteDeLaSesion() {
        Reporte reporte = new Reporte();
        repositorioReporte.eliminar(reporte);
        verify(sessionMock, times(1)).delete(reporte);
    }

    @Test
    public void alBuscarReportesPorCarreraDebeEjecutarLaQueryHQLCorrecta() {
        Carrera carrera = new Carrera();
        Query<Reporte> queryMock = mock(Query.class);
        String hqlEsperado = "SELECT r FROM Reporte r " +
                "LEFT JOIN r.publicacion p " +
                "LEFT JOIN r.comentario c " +
                "LEFT JOIN p.materia pm " +
                "LEFT JOIN c.publicacion cp " +
                "LEFT JOIN cp.materia cpm " +
                "WHERE pm.carrera = :carrera OR cpm.carrera = :carrera " +
                "ORDER BY r.fechaCreacion DESC";

        when(sessionMock.createQuery(hqlEsperado, Reporte.class)).thenReturn(queryMock);
        when(queryMock.setParameter(eq("carrera"), any(Carrera.class))).thenReturn(queryMock);

        repositorioReporte.buscarReportesPorCarrera(carrera);

        verify(sessionMock, times(1)).createQuery(hqlEsperado, Reporte.class);
        verify(queryMock, times(1)).setParameter("carrera", carrera);
        verify(queryMock, times(1)).getResultList();
    }

    @Test
    public void alBuscarPorPublicacionDebeUsarLaRestriccionCorrecta() {
        Long idPublicacion = 1L;
        Criteria criteriaMock = mock(Criteria.class);
        when(sessionMock.createCriteria(Reporte.class)).thenReturn(criteriaMock);
        when(criteriaMock.add(any(Criterion.class))).thenReturn(criteriaMock);

        repositorioReporte.buscarPorPublicacion(idPublicacion);

        ArgumentCaptor<Criterion> captor = ArgumentCaptor.forClass(Criterion.class);
        verify(criteriaMock, times(1)).add(captor.capture());
        assertThat(captor.getValue().toString(), equalTo("publicacion.id=" + idPublicacion));
    }

    @Test
    public void alBuscarPorComentarioDebeUsarLaRestriccionCorrecta() {
        Long idComentario = 1L;
        Criteria criteriaMock = mock(Criteria.class);
        when(sessionMock.createCriteria(Reporte.class)).thenReturn(criteriaMock);
        when(criteriaMock.add(any(Criterion.class))).thenReturn(criteriaMock);

        repositorioReporte.buscarPorComentario(idComentario);

        ArgumentCaptor<Criterion> captor = ArgumentCaptor.forClass(Criterion.class);
        verify(criteriaMock, times(1)).add(captor.capture());
        assertThat(captor.getValue().toString(), equalTo("comentario.id=" + idComentario));
    }
}