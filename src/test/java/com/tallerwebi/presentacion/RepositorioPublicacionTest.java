package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Carrera;
import com.tallerwebi.dominio.Materia;
import com.tallerwebi.dominio.Publicacion;
import com.tallerwebi.infraestructura.RepositorioPublicacionImpl;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class RepositorioPublicacionTest {

    private SessionFactory sessionFactoryMock;
    private Session sessionMock;
    private Criteria criteriaMock;
    private RepositorioPublicacionImpl repositorio;


    private Carrera carreraMock;
    private Materia materiaMock;


    private ArgumentCaptor<Criterion> criterionCaptor;
    private ArgumentCaptor<Order> orderCaptor;

    @BeforeEach
    public void init() {
        sessionFactoryMock = mock(SessionFactory.class);
        sessionMock = mock(Session.class);
        criteriaMock = mock(Criteria.class);
        repositorio = new RepositorioPublicacionImpl(sessionFactoryMock);


        carreraMock = mock(Carrera.class);
        materiaMock = mock(Materia.class);

        criterionCaptor = ArgumentCaptor.forClass(Criterion.class);
        orderCaptor = ArgumentCaptor.forClass(Order.class);

        when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);
        when(sessionMock.createCriteria(Publicacion.class)).thenReturn(criteriaMock);
        when(criteriaMock.setResultTransformer(any())).thenReturn(criteriaMock);
        when(criteriaMock.setFetchMode(anyString(), any(FetchMode.class))).thenReturn(criteriaMock);
        when(criteriaMock.createAlias(anyString(), anyString())).thenReturn(criteriaMock);
        when(criteriaMock.add(any(Criterion.class))).thenReturn(criteriaMock);
        when(criteriaMock.addOrder(any(Order.class))).thenReturn(criteriaMock);
    }

    @Test
    public void alGuardarPublicacionDeberiaLlamarASaveOrUpdateDeLaSesion() {
        Publicacion publicacion = new Publicacion();
        repositorio.guardar(publicacion);
        verify(sessionMock, times(1)).saveOrUpdate(publicacion);
    }

    @Test
    public void alBuscarPublicacionesDeberiaSiempreFiltrarPorCarrera() {

        repositorio.buscarPublicaciones(this.carreraMock, null, "fecha");

        verify(criteriaMock).add(criterionCaptor.capture());
        Criterion capturedCriterion = criterionCaptor.getValue();
        assertThat(capturedCriterion.toString(), equalTo("m.carrera=" + this.carreraMock));
    }

    @Test
    public void siSePasaUnaMateriaDeberiaAgregarElFiltroDeMateria() {

        repositorio.buscarPublicaciones(this.carreraMock, this.materiaMock, "fecha");

        verify(criteriaMock, times(2)).add(criterionCaptor.capture());
        List<Criterion> capturedCriteria = criterionCaptor.getAllValues();

        assertThat(capturedCriteria.get(0).toString(), equalTo("m.carrera=" + this.carreraMock));
        assertThat(capturedCriteria.get(1).toString(), equalTo("materia=" + this.materiaMock));
    }

    @Test
    public void siElOrdenEsPorLikesDeberiaAgregarOrdenDescendentePorLikes() {
        repositorio.buscarPublicaciones(this.carreraMock, null, "likes");

        verify(criteriaMock).addOrder(orderCaptor.capture());
        Order capturedOrder = orderCaptor.getValue();
        assertThat(capturedOrder.toString(), equalTo("likes desc"));
    }

    @Test
    public void siElOrdenNoEsPorLikesDeberiaAgregarOrdenDescendentePorFecha() {
        repositorio.buscarPublicaciones(this.carreraMock, null, "cualquier_otro_orden");

        verify(criteriaMock).addOrder(orderCaptor.capture());
        Order capturedOrder = orderCaptor.getValue();
        assertThat(capturedOrder.toString(), equalTo("fechaCreacion desc"));
    }
}