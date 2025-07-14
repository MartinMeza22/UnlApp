package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Notificacion;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.infraestructura.RepositorioNotificacionImpl;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

public class RepositorioNotificacionTest {

    private SessionFactory sessionFactoryMock;
    private Session sessionMock;
    private RepositorioNotificacionImpl repositorioNotificacion;

    @BeforeEach
    void init() {
        sessionFactoryMock = mock(SessionFactory.class);
        sessionMock = mock(Session.class);
        repositorioNotificacion = new RepositorioNotificacionImpl(sessionFactoryMock);
        when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);
    }

    @Test
    public void alGuardarUnaNotificacionDebeLlamarASaveDeLaSesion() {
        Notificacion notificacion = new Notificacion();
        repositorioNotificacion.guardar(notificacion);
        verify(sessionMock, times(1)).save(notificacion);
    }

    @Test
    public void alObtenerNoLeidasPorUsuarioDebeCrearUnCriteriaConLasRestriccionesCorrectas() {
        Usuario usuario = new Usuario();
        Criteria criteriaMock = mock(Criteria.class);
        ArgumentCaptor<Criterion> criterionCaptor = ArgumentCaptor.forClass(Criterion.class);
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        when(sessionMock.createCriteria(Notificacion.class)).thenReturn(criteriaMock);
        when(criteriaMock.add(any(Criterion.class))).thenReturn(criteriaMock);
        when(criteriaMock.addOrder(any(Order.class))).thenReturn(criteriaMock);

        repositorioNotificacion.obtenerNoLeidasPorUsuario(usuario);

        verify(criteriaMock, times(2)).add(criterionCaptor.capture());
        verify(criteriaMock, times(1)).addOrder(orderCaptor.capture());

        List<Criterion> capturedCriteria = criterionCaptor.getAllValues();
        assertThat(capturedCriteria.get(0).toString(), equalTo("usuarioDestinatario=" + usuario));
        assertThat(capturedCriteria.get(1).toString(), equalTo("leida=false"));
        assertThat(orderCaptor.getValue().toString(), equalTo("fechaCreacion desc"));
    }

    @Test
    public void alMarcarTodasComoLeidasDebeEjecutarElUpdateQueryCorrecto() {
        Usuario usuario = new Usuario();
        Query queryMock = mock(Query.class);
        String hqlEsperado = "UPDATE Notificacion SET leida = true WHERE usuarioDestinatario = :usuario AND leida = false";

        when(sessionMock.createQuery(hqlEsperado)).thenReturn(queryMock);
        when(queryMock.setParameter(eq("usuario"), any(Usuario.class))).thenReturn(queryMock);

        repositorioNotificacion.marcarTodasComoLeidas(usuario);

        verify(sessionMock, times(1)).createQuery(hqlEsperado);
        verify(queryMock, times(1)).setParameter("usuario", usuario);
        verify(queryMock, times(1)).executeUpdate();
    }
}