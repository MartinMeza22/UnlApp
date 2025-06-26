package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ModelMap;
import static org.hamcrest.Matchers.*;
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
    private ServicioEmail servicioEmailMock;
    private RepositorioUsuario repositorioUsuarioMock;
    private ServicioUsuarioMateria servicioUsuarioMateriaMock;
    private ServicioCarrera servicioCarreraMock;
    private ServicioUsuario servicioUsuarioMock;
    private HttpServletRequest requestMock;
    private HttpSession sessionMock;
    private HttpServletRequest mockHttpServletRequestMock;

    @BeforeEach
    public void init() {

        servicioEmailMock = mock(ServicioEmail.class);
        repositorioUsuarioMock = mock(RepositorioUsuario.class);
        servicioUsuarioMateriaMock = mock(ServicioUsuarioMateria.class);
        servicioCarreraMock = mock(ServicioCarrera.class);
        repositorioLoginMock = mock(RepositorioLogin.class);
        servicioMateriaMock = mock(ServicioMateria.class);
        servicioCarreraMock = mock(ServicioCarrera.class);
        servicioUsuarioMock = mock(ServicioUsuario.class);

        controladorLogin = new ControladorLogin(repositorioLoginMock, servicioEmailMock, repositorioUsuarioMock, servicioUsuarioMateriaMock, servicioCarreraMock);

        requestMock = mock(HttpServletRequest.class);
        sessionMock = mock(HttpSession.class);
        mockHttpServletRequestMock = mock(HttpServletRequest.class);
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
        usuarioMock.setId(5L);
        usuarioMock.setActivo(true);

        when(repositorioLoginMock.consultarUsuario("test@mail.com", "1234")).thenReturn(usuarioMock);
        when(requestMock.getSession()).thenReturn(sessionMock);

        ModelAndView mav = controladorLogin.validarLogin(datosLogin, requestMock);

        assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/home"));
        verify(sessionMock).setAttribute("ROL", "USER");
        verify(sessionMock).setAttribute("NOMBRE", "Franco");
        verify(sessionMock).setAttribute("ID", 5L);
    }

    @Test
    public void validarLogin_conCredencialesInvalidas_redirigeAlLoginConError() {
        DatosLogin datosLogin = new DatosLogin("fail@mail.com", "wrong");

        when(repositorioLoginMock.consultarUsuario("fail@mail.com", "wrong")).thenReturn(null);

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
        ModelAndView mav = controladorLogin.registrarme(usuario,mockHttpServletRequestMock);

        assertThat(mav.getViewName(), equalToIgnoringCase("nuevo-usuario"));
    }

    @Test
    public void registrarme_conUsuarioExistente_redirigeANuevoUsuarioConError() throws UsuarioExistente {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@unlam.com");
        usuario.setPassword("1234");

        // Muy importante: usar any(Usuario.class) para que el mock funcione
        doThrow(UsuarioExistente.class).when(repositorioLoginMock).registrar(usuario);

        ModelAndView mav = controladorLogin.registrarme(usuario, requestMock); // <-- pasás el requestMock

        assertThat(mav.getViewName(), equalToIgnoringCase("nuevo-usuario"));
        assertThat(mav.getModel().get("error").toString(), equalToIgnoringCase("El usuario ya existe"));
    }

    @Test
    public void inicio_redirigeALogin() {
        ModelAndView mav = controladorLogin.inicio();

        assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/login"));
    }


    @Test
    public void registrarmeExitosamente() throws UsuarioExistente, UsuarioNoEncontrado {
        Usuario usuario = new Usuario();
        usuario.setEmail("martin@gmail.com");
        usuario.setPassword("1234");
        usuario.setNombre("Martin");
        usuario.setApellido("Meza");
        usuario.setCarreraID(1L);
        usuario.setSituacionLaboral("Empleado");
        usuario.setDisponibilidadHoraria(20);

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setId(4L);
        usuarioGuardado.setEmail("martin@gmail.com");

        when(servicioUsuarioMock.obtenerUsuario(4L)).thenReturn(usuarioGuardado);
        when(repositorioLoginMock.consultarUsuario("martin@gmail.com", "1234")).thenReturn(usuarioGuardado);
        when(requestMock.getSession()).thenReturn(sessionMock);

        ModelAndView mav = controladorLogin.registrarme(usuario, requestMock);
        verify(repositorioLoginMock).registrar(usuario);
        verify(sessionMock).setAttribute("ID", usuarioGuardado.getId());
    }

}


//Preparás los datos y mocks (Setup).
//
//Ejecutás el método que querés testear (Act).
//
//Verificás lo que esperás que pase (Assert).