package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class LoginControllerTest {

    private ControladorLogin controladorLogin;
    private RepositorioLogin repositorioLoginMock;
    private ServicioMateria servicioMateriaMock;
    private HttpServletRequest requestMock;
    private HttpSession sessionMock;
    private ServicioCarrera servicioCarreraMock;

    @BeforeEach
    public void init() {
        repositorioLoginMock = mock(RepositorioLogin.class);
        servicioMateriaMock = mock(ServicioMateria.class);
        servicioCarreraMock = mock(ServicioCarrera.class);

        controladorLogin = new ControladorLogin(repositorioLoginMock, servicioMateriaMock, servicioCarreraMock);

        requestMock = mock(HttpServletRequest.class);
        sessionMock = mock(HttpSession.class);
    }

    @Test
    public void irALogin_deberiaMostrarFormularioConDatosLoginVacio() {
        ModelAndView mav = controladorLogin.irALogin();

        assertThat(mav.getViewName(), equalToIgnoringCase("login"));
        assertTrue(mav.getModel().containsKey("datosLogin"));
    }

    @Test
    public void validarLogin_conCredencialesValidas_redirigeAHome() {
        DatosLogin datosLogin = new DatosLogin("test@mail.com", "1234");

        Usuario usuarioMock = new Usuario();
        usuarioMock.setNombre("Franco");
        usuarioMock.setRol("USER");
        usuarioMock.setId(1L);

        when(repositorioLoginMock.consultarUsuario("test@mail.com", "1234")).thenReturn(usuarioMock);
        when(requestMock.getSession()).thenReturn(sessionMock);

        ModelAndView mav = controladorLogin.validarLogin(datosLogin, requestMock);

        assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/home"));
        verify(sessionMock).setAttribute("ROL", "USER");
        verify(sessionMock).setAttribute("NOMBRE", "Franco");
        verify(sessionMock).setAttribute("ID", 1L);
    }

    @Test
    public void validarLogin_conCredencialesInvalidas_redirigeAlLoginConError() {
        DatosLogin datosLogin = new DatosLogin("fail@mail.com", "wrong");

        when(repositorioLoginMock.consultarUsuario(anyString(), anyString())).thenReturn(null);

        ModelAndView mav = controladorLogin.validarLogin(datosLogin, requestMock);

        assertThat(mav.getViewName(), equalToIgnoringCase("login"));
        assertThat(mav.getModel().get("error").toString(), equalToIgnoringCase("Usuario o clave incorrecta"));
    }

    @Test
    public void nuevoUsuario_devuelveVistaConFormulario() {
        when(servicioCarreraMock.getCarreras()).thenReturn(List.of());
        ModelAndView mav = controladorLogin.nuevoUsuario();

        assertThat(mav.getViewName(), equalToIgnoringCase("nuevo-usuario"));
        assertTrue(mav.getModel().containsKey("usuario"));
    }

    @Test
    public void registrarme_conUsuarioNuevo_redirigeAFormularioDeMaterias() throws UsuarioExistente {
        Usuario usuario = new Usuario();

        when(servicioMateriaMock.obtenerTodasLasMateriasPorNombre()).thenReturn(List.of());

        ModelAndView mav = controladorLogin.registrarme(usuario);

        assertThat(mav.getViewName(), equalToIgnoringCase("nuevo-usuario"));
    }

    @Test
    public void registrarme_conUsuarioExistente_redirigeANuevoUsuarioConError() throws UsuarioExistente {
        Usuario usuario = new Usuario();

        doThrow(UsuarioExistente.class).when(repositorioLoginMock).registrar(usuario);

        ModelAndView mav = controladorLogin.registrarme(usuario);

        assertThat(mav.getViewName(), equalToIgnoringCase("nuevo-usuario"));
        assertThat(mav.getModel().get("error").toString(), equalToIgnoringCase("El usuario ya existe"));
    }

    @Test
    public void inicio_redirigeALogin() {
        ModelAndView mav = controladorLogin.inicio();

        assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/login"));
    }


}
