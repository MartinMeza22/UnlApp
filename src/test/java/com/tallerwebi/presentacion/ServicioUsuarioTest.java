package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.repositorioInterfaz.RepositorioUsuario;
import com.tallerwebi.serviciosImplementacion.ServicioUsuarioImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServicioUsuarioTest {

    @Mock
    private RepositorioUsuario repositorioUsuario;

    @InjectMocks
    private ServicioUsuarioImpl servicioUsuario;

    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Franco");
        usuario.setApellido("Nadal");
        usuario.setEmail("franco@test.com");
        usuario.setPassword("1234");
    }

    @Test
    public void queElServicioElimineUsuarioExistente() throws UsuarioNoEncontrado {
        when(repositorioUsuario.buscarPorId(1L)).thenReturn(usuario);

        servicioUsuario.eliminarUsuario(1L);

        verify(repositorioUsuario).eliminar(usuario);
    }

    @Test
    public void queElServicioLanceExcepcionAlEliminarUsuarioInexistente() {
        when(repositorioUsuario.buscarPorId(1L)).thenReturn(null);

        assertThrows(UsuarioNoEncontrado.class, () -> {
            servicioUsuario.eliminarUsuario(1L);
        });

        verify(repositorioUsuario, never()).eliminar(any());
    }

    @Test
    public void queElServicioRetorneUsuarioExistenteAlSolicitarlo() throws UsuarioNoEncontrado {
        when(repositorioUsuario.buscarPorId(1L)).thenReturn(usuario);

        Usuario resultado = servicioUsuario.obtenerUsuario(1L);

        assertEquals(usuario, resultado);
    }

    @Test
    public void queElServicioLanceExcepcionAlSolicitarUsuarioInexistente() {
        when(repositorioUsuario.buscarPorId(1L)).thenReturn(null);

        assertThrows(UsuarioNoEncontrado.class, () -> {
            servicioUsuario.obtenerUsuario(1L);
        });
    }

    @Test
    public void queElServicioActualicePerfilConNuevaPassword() throws UsuarioNoEncontrado {
        when(repositorioUsuario.buscarPorId(1L)).thenReturn(usuario);

        servicioUsuario.actualizarPerfil(1L, "NuevoNombre", "NuevoApellido", "nuevo@email.com", "nuevaPass");

        assertEquals("NuevoNombre", usuario.getNombre());
        assertEquals("NuevoApellido", usuario.getApellido());
        assertEquals("nuevo@email.com", usuario.getEmail());
        assertEquals("nuevaPass", usuario.getPassword());

        verify(repositorioUsuario).modificar(usuario);
    }

    @Test
    public void queElServicioActualicePerfilSinCambiarPassword() throws UsuarioNoEncontrado {
        when(repositorioUsuario.buscarPorId(1L)).thenReturn(usuario);

        servicioUsuario.actualizarPerfil(1L, "NuevoNombre", "NuevoApellido", "nuevo@email.com", null);

        assertEquals("NuevoNombre", usuario.getNombre());
        assertEquals("NuevoApellido", usuario.getApellido());
        assertEquals("nuevo@email.com", usuario.getEmail());
        assertEquals("1234", usuario.getPassword()); // password no cambia

        verify(repositorioUsuario).modificar(usuario);
    }

    @Test
    public void queElServicioLanceExcepcionAlActualizarPerfilDeUsuarioInexistente() {
        when(repositorioUsuario.buscarPorId(1L)).thenReturn(null);

        assertThrows(UsuarioNoEncontrado.class, () -> {
            servicioUsuario.actualizarPerfil(1L, "NuevoNombre", "NuevoApellido", "nuevo@email.com", "pass");
        });

        verify(repositorioUsuario, never()).modificar(any());
    }
}
