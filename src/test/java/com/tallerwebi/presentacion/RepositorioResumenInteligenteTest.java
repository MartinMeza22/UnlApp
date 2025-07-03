package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ResumenInteligente;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.infraestructura.RepositorioResumenInteligenteImpl;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class RepositorioResumenInteligenteTest {

    private SessionFactory sessionFactoryMock;
    private Session sessionMock;
    private Query<ResumenInteligente> queryMock;
    private RepositorioResumenInteligenteImpl repositorio;

    @BeforeEach
    public void setUp() {
        sessionFactoryMock = mock(SessionFactory.class);
        sessionMock = mock(Session.class);
        queryMock = mock(Query.class);

        when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);

        repositorio = new RepositorioResumenInteligenteImpl();
        // Simular inyección del SessionFactory
        // Si no tenés setter, hay que usar reflección para inyectar el mock.
        // repositorio.setSessionFactory(sessionFactoryMock);
        injectSessionFactoryByReflection(repositorio, sessionFactoryMock);
    }

    @Test
    public void alGuardarUnResumenDeberiaLlamarAlMetodoSaveDeLaSesion() {
        // Arrange
        ResumenInteligente resumen = new ResumenInteligente();

        // Act
        repositorio.guardar(resumen);

        // Assert
        verify(sessionMock, times(1)).save(resumen);
    }

    @Test
    public void alObtenerResúmenesPorUsuarioDeberiaEjecutarLaConsultaConElUsuario() {
        // Arrange
        Usuario usuario = new Usuario();
        List<ResumenInteligente> listaEsperada = List.of(new ResumenInteligente());

        when(sessionMock.createQuery(anyString(), eq(ResumenInteligente.class))).thenReturn(queryMock);
        when(queryMock.setParameter(eq("usuario"), eq(usuario))).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(listaEsperada);

        // Act
        List<ResumenInteligente> resultado = repositorio.obtenerPorUsuario(usuario);

        // Assert
        verify(sessionMock).createQuery("FROM ResumenInteligente WHERE usuario = :usuario ORDER BY fechaGeneracion ASC", ResumenInteligente.class);
        verify(queryMock).setParameter("usuario", usuario);
        verify(queryMock).getResultList();
        assertEquals(listaEsperada, resultado);
    }

    // Utilidad para inyectar SessionFactory si no hay setter
    private void injectSessionFactoryByReflection(RepositorioResumenInteligenteImpl repo, SessionFactory mock) {
        try {
            var field = RepositorioResumenInteligenteImpl.class.getDeclaredField("sessionFactory");
            field.setAccessible(true);
            field.set(repo, mock);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo inyectar sessionFactory en el test", e);
        }
    }
}

