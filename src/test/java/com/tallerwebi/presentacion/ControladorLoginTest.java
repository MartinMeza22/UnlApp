package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.servicios.ServicioEmail;
import com.tallerwebi.dominio.servicios.ServicioMateria;
import com.tallerwebi.dominio.servicios.ServicioUsuarioMateria;
import com.tallerwebi.repositorioInterfaz.RepositorioLogin;
import com.tallerwebi.repositorioInterfaz.RepositorioUsuario;
import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.servicioInterfaz.ServicioCarrera;
import com.tallerwebi.servicioInterfaz.ServicioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class ControladorLoginTest {
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
    private MockMvc mockMvc;

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
        mockMvc = mock(MockMvc.class);

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
        //Login
    @Test
    public void debeLoguearseCorrectamenteEIrAHome(){
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
        //Login
    @Test
    public void validarLogin_conCredencialesInvalidas_redirigeAlLoginConError() {
        DatosLogin datosLogin = new DatosLogin("fail@mail.com", "wrong");

        when(repositorioLoginMock.consultarUsuario("fail@mail.com", "wrong")).thenReturn(null);

        ModelAndView mav = controladorLogin.validarLogin(datosLogin, requestMock);

        assertThat(mav.getViewName(), equalToIgnoringCase("login"));
        assertThat(mav.getModel().get("error").toString(), equalToIgnoringCase("Usuario o clave incorrecta"));
    }

        //Login
    @Test
    public void inicio_redirigeALogin() {
        ModelAndView mav = controladorLogin.inicio();

        assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/login"));
    }

        //Login - Cualquier vista
    @Test
    public void debeRetornarLaPaginaLoginCuandoSeNavegaALaRaiz() throws Exception {
        ModelAndView mav = controladorLogin.irALogin();
        assert mav != null;
        assertThat(mav.getViewName(), equalToIgnoringCase("login"));
    }
        //Login
    @Test
    public void debeRetornarLaPaginaLoginCuandoSeNavegaALLogin() {

        when(requestMock.getSession()).thenReturn(sessionMock);

        ModelAndView mav = controladorLogin.irALogin();

        assertThat(mav.getViewName(), equalToIgnoringCase("login"));
        assertThat(mav.getModel().get("datosLogin").toString(), containsString("com.tallerwebi.dominio.DatosLogin"));
    }


    //Register
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
    //Register
    @Test
    public void nuevoUsuario_devuelveVistaConFormulario() {
        when(servicioCarreraMock.getCarreras()).thenReturn(List.of());
        ModelAndView mav = controladorLogin.nuevoUsuario();

        assertThat(mav.getViewName(), equalToIgnoringCase("nuevo-usuario"));
        assertTrue(mav.getModel().containsKey("usuario"));
    }
    //Register
    @Test
    public void registrarme_conUsuarioNuevo_redirigeAFormularioDeMaterias() throws UsuarioExistente {
        Usuario usuario = new Usuario();
        ModelAndView mav = controladorLogin.registrarme(usuario,mockHttpServletRequestMock);

        assertThat(mav.getViewName(), equalToIgnoringCase("nuevo-usuario"));
    }
    //Register
    @Test
    public void registrarme_conUsuarioExistente_redirigeANuevoUsuarioConError() throws UsuarioExistente {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@unlam.com");
        usuario.setPassword("1234");
        usuario.setNombre("Martin");
        usuario.setApellido("Meza");
        usuario.setCarreraID(1L);
        usuario.setSituacionLaboral("Empleado");
        usuario.setDisponibilidadHoraria(20);
        // Muy importante: usar any(Usuario.class) para que el mock funcione
        doThrow(UsuarioExistente.class).when(repositorioLoginMock).registrar(usuario);

        ModelAndView mav = controladorLogin.registrarme(usuario, requestMock); // <-- pasás el requestMock

        assertThat(mav.getViewName(), equalToIgnoringCase("nuevo-usuario"));
        assertThat(mav.getModel().get("error"), is("El usuario ya existe"));
    }
    //Register
    @Test
        public void registrarseConTodosLosCamposPeroSinIngresarMailDevuelveError() throws Exception{
            Usuario usuario = new Usuario();

            usuario.setPassword("1234");
            usuario.setNombre("Martin");
            usuario.setApellido("Meza");
            usuario.setCarreraID(1L);
            usuario.setSituacionLaboral("Empleado");
            usuario.setDisponibilidadHoraria(20);

            doThrow(new IllegalArgumentException("El email es obligatorio"))
                .when(repositorioLoginMock).registrar(usuario);
        ModelAndView mav = controladorLogin.registrarme(usuario, requestMock);

        assertThat(mav.getViewName(), equalTo("nuevo-usuario"));
        assertThat(mav.getModel().get("error"), is("El email es obligatorio"));
    }
    //Register
    @Test
    public void registrarseConTodosLosCamposPeroSinIngresarContraseniaDevuelveError() throws Exception{
        Usuario usuario = new Usuario();
        usuario.setEmail("martin@gmail.com");
        usuario.setNombre("Martin");
        usuario.setApellido("Meza");
        usuario.setCarreraID(1L);
        usuario.setSituacionLaboral("Empleado");
        usuario.setDisponibilidadHoraria(20);

        doThrow(new IllegalArgumentException("La contraseña es obligatoria"))
                .when(repositorioLoginMock).registrar(usuario);
        ModelAndView mav = controladorLogin.registrarme(usuario, requestMock);

        assertThat(mav.getViewName(), equalTo("nuevo-usuario"));
        assertThat(mav.getModel().get("error"), is("La contraseña es obligatoria"));
    }
    //Register
    @Test
    public void registrarseConTodosLosCamposPeroSinIngresarNombreDevuelveError() throws Exception{
        Usuario usuario = new Usuario();

        usuario.setEmail("martin@gmail.com");
        usuario.setPassword("1234");
        usuario.setApellido("Meza");
        usuario.setCarreraID(1L);
        usuario.setSituacionLaboral("Empleado");
        usuario.setDisponibilidadHoraria(20);

        doThrow(new IllegalArgumentException("El nombre es obligatorio"))
                .when(repositorioLoginMock).registrar(usuario);
        ModelAndView mav = controladorLogin.registrarme(usuario, requestMock);

        assertThat(mav.getViewName(), equalTo("nuevo-usuario"));
        assertThat(mav.getModel().get("error"), is("El nombre es obligatorio"));
    }
    //Register
    @Test
    public void registrarseConTodosLosCamposPeroSinIngresarApellidoDevuelveError() throws Exception{
        Usuario usuario = new Usuario();

        usuario.setNombre("Martin");
        usuario.setEmail("martin@gmail.com");
        usuario.setPassword("1234");
        usuario.setCarreraID(1L);
        usuario.setSituacionLaboral("Empleado");
        usuario.setDisponibilidadHoraria(20);

        doThrow(new IllegalArgumentException("El apellido es obligatorio"))
                .when(repositorioLoginMock).registrar(usuario);
        ModelAndView mav = controladorLogin.registrarme(usuario, requestMock);

        assertThat(mav.getViewName(), equalTo("nuevo-usuario"));
        assertThat(mav.getModel().get("error"), is("El apellido es obligatorio"));
    }
    //Register
    @Test
    public void registrarseConTodosLosCamposPeroSinIngresarSinSituacionLaboralDevuelveError() throws Exception{
        Usuario usuario = new Usuario();

        usuario.setApellido("Meza");
        usuario.setNombre("Martin");
        usuario.setEmail("martin@gmail.com");
        usuario.setPassword("1234");
        usuario.setCarreraID(1L);
        usuario.setDisponibilidadHoraria(20);

        doThrow(new IllegalArgumentException("La situación laboral es obligatoria"))
                .when(repositorioLoginMock).registrar(usuario);
        ModelAndView mav = controladorLogin.registrarme(usuario, requestMock);

        assertThat(mav.getViewName(), equalTo("nuevo-usuario"));
        assertThat(mav.getModel().get("error"), is("La situación laboral es obligatoria"));
    }
    //Register
    @Test
    public void registrarseConTodosLosCamposPeroSinIngresarSinCarreraDevuelveError() throws Exception{
        Usuario usuario = new Usuario();

        usuario.setApellido("Meza");
        usuario.setNombre("Martin");
        usuario.setEmail("martin@gmail.com");
        usuario.setPassword("1234");
        usuario.setSituacionLaboral("Empleado");
        usuario.setDisponibilidadHoraria(20);

        doThrow(new IllegalArgumentException("Debés seleccionar una carrera"))
                .when(repositorioLoginMock).registrar(usuario);
        ModelAndView mav = controladorLogin.registrarme(usuario, requestMock);

        assertThat(mav.getViewName(), equalTo("nuevo-usuario"));
        assertThat(mav.getModel().get("error"), is("Debés seleccionar una carrera"));
    }
    //Register
    @Test
    public void registrarseConTodosLosCamposPeroSinIngresarSinDisponibilidadHorariaDevuelveError() throws Exception{
        Usuario usuario = new Usuario();

        usuario.setApellido("Meza");
        usuario.setNombre("Martin");
        usuario.setEmail("martin@gmail.com");
        usuario.setPassword("1234");
        usuario.setCarreraID(1L);
        usuario.setSituacionLaboral("Empleado");

        doThrow(new IllegalArgumentException("La disponibilidad horaria es obligatoria"))
                .when(repositorioLoginMock).registrar(usuario);
        ModelAndView mav = controladorLogin.registrarme(usuario, requestMock);

        assertThat(mav.getViewName(), equalTo("nuevo-usuario"));
        assertThat(mav.getModel().get("error"), is("La disponibilidad horaria es obligatoria"));
    }
}


//Preparás los datos y mocks (Setup).
//
//Ejecutás el método que querés testear (Act).
//
//Verificás lo que esperás que pase (Assert).