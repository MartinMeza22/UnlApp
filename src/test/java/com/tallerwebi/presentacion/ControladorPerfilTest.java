package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class ControladorPerfilTest {

    private ServicioUsuario servicioUsuario;
    private ServicioUsuarioMateria servicioUsuarioMateria;
    private RepositorioUsuario repositorioUsuario;
    private ControladorPerfil controladorPerfil;

    private HttpServletRequest request;
    private HttpSession session;

    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        servicioUsuario = mock(ServicioUsuario.class);
        servicioUsuarioMateria = mock(ServicioUsuarioMateria.class);
        repositorioUsuario = mock(RepositorioUsuario.class);
        controladorPerfil = new ControladorPerfil(servicioUsuario, repositorioUsuario, servicioUsuarioMateria);

        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Franco");
        usuario.setApellido("Nadal");
        usuario.setEmail("franco@test.com");
        usuario.setPassword("1234");
    }

    @Test
    public void queSePuedaVisualizarElPerfilConSesionActiva() {
        when(session.getAttribute("ID")).thenReturn(1L);
        when(repositorioUsuario.buscarPorId(1L)).thenReturn(usuario);
        when(servicioUsuarioMateria.mostrarMateriasDeUsuario(null, 1L)).thenReturn(Collections.emptyList());

        ModelAndView mav = controladorPerfil.verPerfil(request);

        assertThat(mav.getViewName(), is("perfil"));
        assertThat(mav.getModel().get("usuario"), is(usuario));
    }

    @Test
    public void queSeRedirijaAlLoginSiNoHaySesion() {
        when(session.getAttribute("ID")).thenReturn(null);

        ModelAndView mav = controladorPerfil.verPerfil(request);

        assertThat(mav.getViewName(), is("redirect:/login"));
    }

    @Test
    public void queSePuedaModificarElEmail() {
        when(session.getAttribute("ID")).thenReturn(1L);
        when(repositorioUsuario.buscarPorId(1L)).thenReturn(usuario);

        ModelAndView mav = controladorPerfil.actualizarPerfil(
                "Franco", "Nadal", "nuevo@email.com", "", request
        );

        assertThat(usuario.getEmail(), is("nuevo@email.com"));
        assertThat(mav.getViewName(), is("redirect:/perfil"));
        verify(repositorioUsuario).modificar(usuario);
    }

    @Test
    public void queSePuedaModificarLaContrasenia() {
        when(session.getAttribute("ID")).thenReturn(1L);
        when(repositorioUsuario.buscarPorId(1L)).thenReturn(usuario);

        ModelAndView mav = controladorPerfil.actualizarPerfil(
                "Franco", "Nadal", "franco@test.com", "nuevaPassword123", request
        );

        assertThat(usuario.getPassword(), is("nuevaPassword123"));
        assertThat(mav.getViewName(), is("redirect:/perfil"));
        verify(repositorioUsuario).modificar(usuario);
    }

    @Test
    public void queSePuedaEliminarCuentaSiHaySesionActiva() throws UsuarioNoEncontrado {
        when(session.getAttribute("ID")).thenReturn(1L);

        ModelAndView mav = controladorPerfil.eliminarCuenta(request);

        verify(servicioUsuario).eliminarUsuario(1L);
        verify(session).invalidate();
        assertThat(mav.getViewName(), is("redirect:/"));
    }

    @Test
    public void queNoSePuedaEliminarCuentaSinSesion() {
        when(session.getAttribute("ID")).thenReturn(null);

        ModelAndView mav = controladorPerfil.eliminarCuenta(request);

        assertThat(mav.getViewName(), is("redirect:/login"));
    }
    @Test
    public void queSePuedaModificarElNombre() {
        when(session.getAttribute("ID")).thenReturn(1L);
        when(repositorioUsuario.buscarPorId(1L)).thenReturn(usuario);

        ModelAndView mav = controladorPerfil.actualizarPerfil(
                "NuevoNombre", "Nadal", "franco@test.com", null, request
        );

        assertThat(usuario.getNombre(), is("NuevoNombre"));
        assertThat(mav.getViewName(), is("redirect:/perfil"));
        verify(repositorioUsuario).modificar(usuario);
    }

    @Test
    public void queSePuedaModificarElApellido() {
        when(session.getAttribute("ID")).thenReturn(1L);
        when(repositorioUsuario.buscarPorId(1L)).thenReturn(usuario);

        ModelAndView mav = controladorPerfil.actualizarPerfil(
                "Franco", "NuevoApellido", "franco@test.com", null, request
        );

        assertThat(usuario.getApellido(), is("NuevoApellido"));
        assertThat(mav.getViewName(), is("redirect:/perfil"));
        verify(repositorioUsuario).modificar(usuario);
    }

}
