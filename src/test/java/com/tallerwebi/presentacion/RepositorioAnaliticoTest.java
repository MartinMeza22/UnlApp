package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.UsuarioMateria;
import com.tallerwebi.infraestructura.RepositorioAnaliticoImpl;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class RepositorioAnaliticoTest {

    private SessionFactory sessionFactoryMock;
    private Session sessionMock;
    private Query queryMock;
    private RepositorioAnaliticoImpl repositorioAnalitico;

    private final Long USUARIO_ID = 1L;

    @BeforeEach
    public void init() {
        sessionFactoryMock = mock(SessionFactory.class);
        sessionMock = mock(Session.class);
        queryMock = mock(Query.class);
        repositorioAnalitico = new RepositorioAnaliticoImpl(sessionFactoryMock);

        when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);
        when(sessionMock.createQuery(anyString())).thenReturn(queryMock);
        when(queryMock.setParameter(anyString(), any())).thenReturn(queryMock);
    }

    @Test
    public void alObtenerDatosCompletosParaAnaliticoDeberiaLlamarACreateQueryYSetParameter() {
        // Preparación
        List<UsuarioMateria> resultadoEsperado = Arrays.asList(mock(UsuarioMateria.class));
        when(queryMock.list()).thenReturn(resultadoEsperado);

        // Ejecución
        repositorioAnalitico.obtenerDatosCompletosParaAnalitico(USUARIO_ID);

        // Validación
        verify(sessionFactoryMock, times(1)).getCurrentSession();
        verify(sessionMock, times(1)).createQuery(anyString());
        verify(queryMock, times(1)).setParameter("usuarioId", USUARIO_ID);
        verify(queryMock, times(1)).list();
    }

    @Test
    public void alObtenerDatosCompletosDeberiaUsarLaQueryHQLCorrecta() {
        // Preparación
        String expectedHQL = "FROM UsuarioMateria um " +
                "JOIN FETCH um.usuario u " +
                "JOIN FETCH u.carrera c " +
                "JOIN FETCH um.materia m " +
                "WHERE um.usuario.id = :usuarioId " +
                "ORDER BY m.cuatrimestre ASC, m.nombre ASC";

        // Ejecución
        repositorioAnalitico.obtenerDatosCompletosParaAnalitico(USUARIO_ID);

        // Validación
        verify(sessionMock, times(1)).createQuery(expectedHQL);
    }

    @Test
    public void alObtenerUsuarioConCarreraDeberiaLlamarACreateQueryYUniqueResult() {
        // Preparación
        Usuario usuarioEsperado = mock(Usuario.class);
        when(queryMock.uniqueResult()).thenReturn(usuarioEsperado);

        // Ejecución
        repositorioAnalitico.obtenerUsuarioConCarrera(USUARIO_ID);

        // Validación
        verify(sessionFactoryMock, times(1)).getCurrentSession();
        verify(sessionMock, times(1)).createQuery(anyString());
        verify(queryMock, times(1)).setParameter("usuarioId", USUARIO_ID);
        verify(queryMock, times(1)).uniqueResult();
    }

    @Test
    public void alObtenerUsuarioConCarreraDeberiaUsarLaQueryHQLCorrecta() {
        // Preparación
        String expectedHQL = "FROM Usuario u " +
                "JOIN FETCH u.carrera c " +
                "WHERE u.id = :usuarioId";

        // Ejecución
        repositorioAnalitico.obtenerUsuarioConCarrera(USUARIO_ID);

        // Validación
        verify(sessionMock, times(1)).createQuery(expectedHQL);
    }

    @Test
    public void alContarTotalMateriasDeCarreraDeberiaLlamarACreateQueryYUniqueResult() {
        // Preparación
        Long conteoEsperado = 10L;
        when(queryMock.uniqueResult()).thenReturn(conteoEsperado);

        // Ejecución
        repositorioAnalitico.contarTotalMateriasDeCarrera(USUARIO_ID);

        // Validación
        verify(sessionFactoryMock, times(1)).getCurrentSession();
        verify(sessionMock, times(1)).createQuery(anyString());
        verify(queryMock, times(1)).setParameter("usuarioId", USUARIO_ID);
        verify(queryMock, times(1)).uniqueResult();
    }

    @Test
    public void alContarTotalMateriasDeCarreraDeberiaUsarLaQueryHQLCorrecta() {
        // Preparación
        String expectedHQL = "SELECT COUNT(m.id) " +
                "FROM Materia m, Usuario u " +
                "WHERE u.id = :usuarioId " +
                "AND m.carrera.id = u.carrera.id " +
                "AND m.activa = true";

        // Ejecución
        repositorioAnalitico.contarTotalMateriasDeCarrera(USUARIO_ID);

        // Validación
        verify(sessionMock, times(1)).createQuery(expectedHQL);
    }

    @Test
    public void alObtenerEstadisticasGeneralesDeberiaLlamarACreateQueryYUniqueResult() {
        // Preparación
        Object[] estadisticasEsperadas = {5L, 3L, 1L, 1L, 7.5, 24L};
        when(queryMock.uniqueResult()).thenReturn(estadisticasEsperadas);

        // Ejecución
        repositorioAnalitico.obtenerEstadisticasGenerales(USUARIO_ID);

        // Validación
        verify(sessionFactoryMock, times(1)).getCurrentSession();
        verify(sessionMock, times(1)).createQuery(anyString());
        verify(queryMock, times(1)).setParameter("usuarioId", USUARIO_ID);
        verify(queryMock, times(1)).uniqueResult();
    }

    @Test
    public void alObtenerEstadisticasGeneralesDeberiaUsarLaQueryHQLCorrecta() {
        // Preparación
        String expectedHQL = "SELECT " +
                "COUNT(um.id) as totalCursadas, " +
                "COUNT(CASE WHEN um.estado = 3 THEN 1 END) as aprobadas, " +
                "COUNT(CASE WHEN um.estado = 4 THEN 1 END) as desaprobadas, " +
                "COUNT(CASE WHEN um.estado = 2 THEN 1 END) as cursando, " +
                "AVG(CASE WHEN um.nota IS NOT NULL THEN um.nota END) as promedio, " +
                "SUM(CASE WHEN um.estado = 3 THEN m.cargaHoraria ELSE 0 END) as horasAprobadas " +
                "FROM UsuarioMateria um " +
                "JOIN um.materia m " +
                "WHERE um.usuario.id = :usuarioId";

        // Ejecución
        repositorioAnalitico.obtenerEstadisticasGenerales(USUARIO_ID);

        // Validación
        verify(sessionMock, times(1)).createQuery(expectedHQL);
    }
}