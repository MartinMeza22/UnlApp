package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.infraestructura.RepositorioUsuarioImpl;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class RepositorioUsuarioTest {

    // Mocks para simular el comportamiento de Hibernate
    private SessionFactory sessionFactoryMock;
    private Session sessionMock;
    private Criteria criteriaMock;
    private Query queryMock;
    private RepositorioUsuarioImpl repositorioUsuario;

    @BeforeEach
    public void init() {
        // Creamos los mocks
        sessionFactoryMock = mock(SessionFactory.class);
        sessionMock = mock(Session.class);
        criteriaMock = mock(Criteria.class);
        queryMock = mock(Query.class);

        // Instanciamos el repositorio pasándole la factory mockeada
        repositorioUsuario = new RepositorioUsuarioImpl(sessionFactoryMock);

        // Cuando se pida la sesión actual, devolvemos nuestro session mock
        when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);
    }

    @Test
    public void deberiaGuardarUsuarioCuandoSeLlamaAlMetodoGuardar() {
        Usuario usuario = new Usuario(); // Creamos un usuario

        // Llamamos al método a testear
        repositorioUsuario.guardar(usuario);

        // Verificamos que se haya invocado session.save una vez con ese usuario
        verify(sessionMock, times(1)).save(usuario);
    }

    @Test
    public void deberiaBuscarUsuarioPorEmailYPasswordCorrectamente() {
        // Datos simulados
        String email = "test@correo.com";
        String password = "1234";
        Usuario usuarioMock = new Usuario();

        // Configuramos el mock del criteria
        when(sessionMock.createCriteria(Usuario.class)).thenReturn(criteriaMock);
        when(criteriaMock.add(any())).thenReturn(criteriaMock);
        when(criteriaMock.uniqueResult()).thenReturn(usuarioMock);

        // Ejecutamos la búsqueda
        Usuario resultado = repositorioUsuario.buscarUsuario(email, password);

        // Verificamos que se haya construido el criteria correctamente
        verify(sessionMock).createCriteria(Usuario.class);
        verify(criteriaMock, times(2)).add(any()); // se agregan email y password
        verify(criteriaMock).uniqueResult();

        // Verificamos que devuelva el usuario esperado
        assertEquals(usuarioMock, resultado);
    }

    @Test
    public void deberiaBuscarUsuarioSoloPorEmailYRetornarCorrecto() {
        String email = "user@email.com";
        Usuario usuarioEsperado = new Usuario();

        // Mock del criteria
        when(sessionMock.createCriteria(Usuario.class)).thenReturn(criteriaMock);
        when(criteriaMock.add(any())).thenReturn(criteriaMock);
        when(criteriaMock.uniqueResult()).thenReturn(usuarioEsperado);

        // Ejecutamos
        Usuario resultado = repositorioUsuario.buscar(email);

        // Verificaciones
        verify(sessionMock).createCriteria(Usuario.class);
        verify(criteriaMock).add(any());
        verify(criteriaMock).uniqueResult();

        assertEquals(usuarioEsperado, resultado);
    }

    @Test
    public void deberiaActualizarUsuarioCuandoSeLlamaAlMetodoModificar() {
        Usuario usuario = new Usuario();

        // Llamamos al método
        repositorioUsuario.modificar(usuario);

        // Verificamos que se llame session.update
        verify(sessionMock).update(usuario);
    }

    @Test
    public void deberiaBuscarUsuarioPorIdCorrectamente() {
        Long id = 10L;
        Usuario usuario = new Usuario();

        // Configuramos el retorno del mock
        when(sessionMock.get(Usuario.class, id)).thenReturn(usuario);

        // Ejecutamos
        Usuario resultado = repositorioUsuario.buscarPorId(id);

        // Verificamos que se llame a get con la clase y el id
        verify(sessionMock).get(Usuario.class, id);
        assertEquals(usuario, resultado);
    }

    @Test
    public void deberiaEliminarUsuarioYSusRelacionesCorrectamente() {
        // Creamos usuario con ID
        Usuario usuario = new Usuario();
        usuario.setId(5L);

        // Configuramos mocks de queries
        Query queryMock = mock(Query.class);
        when(sessionMock.createQuery(anyString())).thenReturn(queryMock);
        when(queryMock.setParameter(anyString(), any())).thenReturn(queryMock);
        when(queryMock.executeUpdate()).thenReturn(1);
        when(sessionMock.contains(usuario)).thenReturn(true); // simula que el usuario ya está en sesión

        // Ejecutamos
        repositorioUsuario.eliminar(usuario);

        // Verificamos que se ejecuten las 4 consultas para eliminar relaciones
        verify(sessionMock, times(4)).createQuery(anyString());
        verify(queryMock, times(4)).setParameter("usuarioId", usuario.getId());
        verify(queryMock, times(4)).executeUpdate();

        // Verificamos que se haya llamado a delete
        verify(sessionMock).delete(usuario);
    }
}
