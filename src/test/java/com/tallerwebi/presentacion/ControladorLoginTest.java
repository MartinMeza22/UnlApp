package com.tallerwebi.presentacion;
import org.springframework.ui.ModelMap;
import com.tallerwebi.dominio.excepcion.CodigoVerificacionExpirado;
import com.tallerwebi.dominio.excepcion.CodigoVerificacionIncorrecto;
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
import org.mockito.internal.matchers.InstanceOf;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.MatcherAssert.assertThat;


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

        controladorLogin = new ControladorLogin(repositorioLoginMock, servicioEmailMock, repositorioUsuarioMock, servicioUsuarioMateriaMock, servicioCarreraMock,  servicioMateriaMock);

        requestMock = mock(HttpServletRequest.class);
        sessionMock = mock(HttpSession.class);
        when(requestMock.getSession()).thenReturn(sessionMock);
        this.servicioMateriaMock = servicioMateriaMock;
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
        ModelAndView mav = controladorLogin.inicio();
        assert mav != null;
        assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/login"));
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
        usuario.setEmail("martin@alumno.unlam.edu.ar");
        usuario.setPassword("1234");
        usuario.setNombre("Martin");
        usuario.setApellido("Meza");
        usuario.setCarreraID(1L);
        usuario.setSituacionLaboral("Empleado");
        usuario.setDisponibilidadHoraria(20);

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setId(4L);
        usuarioGuardado.setEmail("martin@alumno.unlam.edu.ar");

        when(servicioUsuarioMock.obtenerUsuario(4L)).thenReturn(usuarioGuardado);
        when(repositorioLoginMock.consultarUsuario("martin@alumno.unlam.edu.ar", "1234")).thenReturn(usuarioGuardado);
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
        ModelAndView mav = controladorLogin.registrarme(usuario,requestMock);

        assertThat(mav.getViewName(), equalToIgnoringCase("nuevo-usuario"));
    }
    //Register
    @Test
    public void registrarme_conUsuarioExistente_redirigeANuevoUsuarioConError() throws UsuarioExistente {
        Usuario usuario = new Usuario();
        usuario.setEmail("martin@alumno.unlam.edu.ar");
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

    @Test
    public void registrarseConEmailInstitucionalEsValido() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setEmail("martin@alumno.unlam.edu.ar");
        usuario.setPassword("1234");
        usuario.setNombre("Martin");
        usuario.setApellido("Meza");
        usuario.setCarreraID(1L);
        usuario.setSituacionLaboral("Empleado");
        usuario.setDisponibilidadHoraria(20);

        Usuario usuarioPersistido = new Usuario();
        usuarioPersistido.setId(1L);

        when(repositorioLoginMock.consultarUsuario(any(), any())).thenReturn(usuarioPersistido);
        doNothing().when(repositorioLoginMock).registrar(any());
        doNothing().when(servicioEmailMock).guardarYEnviarCodigoDeVerificacion(any());
        when(requestMock.getSession()).thenReturn(sessionMock);

        ModelAndView mav = controladorLogin.registrarme(usuario, requestMock);

        assertThat(mav.getViewName(), is("verificar-token"));
        verify(repositorioLoginMock).registrar(usuario);
    }


    @Test
    public void registrarseConTodosLosCamposPeroYConUnMailSinLaExtencionAlumnoUnlamEduArDevuelveError() throws Exception{
        Usuario usuario = new Usuario();
        usuario.setEmail("martin@gmail.com");
        usuario.setPassword("1234");
        usuario.setNombre("Martin");
        usuario.setApellido("Meza");
        usuario.setCarreraID(1L);
        usuario.setSituacionLaboral("Empleado");
        usuario.setDisponibilidadHoraria(20);

        ModelAndView mav = controladorLogin.registrarme(usuario, requestMock);

        assertThat(mav.getViewName(), is("nuevo-usuario"));
        assertThat(mav.getModel().get("error"), is("El email debe ser institucional (@alumno.unlam.edu.ar)"));

        // Verificamos que NO se haya intentado registrar
        verify(repositorioLoginMock, never()).registrar(any());
    }
    //Register
    @Test
    public void registrarseConTodosLosCamposPeroSinIngresarContraseniaDevuelveError() throws Exception{
        Usuario usuario = new Usuario();
        usuario.setEmail("martin@alumno.unlam.edu.ar");
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

        usuario.setEmail("martin@alumno.unlam.edu.ar");
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
        usuario.setEmail("martin@alumno.unlam.edu.ar");
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
        usuario.setEmail("martin@alumno.unlam.edu.ar");
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
        usuario.setEmail("martin@alumno.unlam.edu.ar");
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
        usuario.setEmail("martin@alumno.unlam.edu.ar");
        usuario.setPassword("1234");
        usuario.setCarreraID(1L);
        usuario.setSituacionLaboral("Empleado");

        doThrow(new IllegalArgumentException("La disponibilidad horaria es obligatoria"))
                .when(repositorioLoginMock).registrar(usuario);
        ModelAndView mav = controladorLogin.registrarme(usuario, requestMock);

        assertThat(mav.getViewName(), equalTo("nuevo-usuario"));
        assertThat(mav.getModel().get("error"), is("La disponibilidad horaria es obligatoria"));
    }

    //Revisar
    @Test
    public void registrarseConUnMailYaRegistradoDevuelveError() throws UsuarioExistente {
        // Nuevo usuario con mismo mail
        Usuario usuarioNuevo = new Usuario();
        usuarioNuevo.setEmail("mmartin@alumno.unlam.edu.ar");
        usuarioNuevo.setPassword("1234");
        usuarioNuevo.setNombre("Marttin");
        usuarioNuevo.setApellido("Mezza");
        usuarioNuevo.setCarreraID(1L);
        usuarioNuevo.setSituacionLaboral("Desempleado");
        usuarioNuevo.setDisponibilidadHoraria(20);

        doThrow(new UsuarioExistente()).when(repositorioLoginMock).registrar(any(Usuario.class));

        ModelAndView mv = controladorLogin.registrarme(usuarioNuevo, requestMock);

        assertThat(mv.getViewName(), is("nuevo-usuario"));
        assertThat(mv.getModel().get("error"), is("El usuario ya existe"));
    }


    @Test
    public void elCodigoDeVerificacionSeEnviaCorrectamente() throws UsuarioExistente {
        Usuario usuario = new Usuario();
        usuario.setEmail("martin@alumno.unlam.edu.ar");
        usuario.setPassword("1234");
        usuario.setNombre("Martín");
        usuario.setApellido("Meza");
        usuario.setCarreraID(1L);
        usuario.setSituacionLaboral("Estudiante");
        usuario.setDisponibilidadHoraria(20);

        Usuario usuarioPersistido = new Usuario();
        usuarioPersistido.setId(1L);

        //comportamiento
        doNothing().when(repositorioLoginMock).registrar(any());//doNothing?
        when(repositorioLoginMock.consultarUsuario("martin@alumno.unlam.edu.ar", "1234")).thenReturn(usuarioPersistido);
        doNothing().when(servicioEmailMock).guardarYEnviarCodigoDeVerificacion(any());

        ModelAndView mv = controladorLogin.registrarme(usuario, requestMock);


        verify(repositorioLoginMock).registrar(usuario);
        verify(repositorioLoginMock).consultarUsuario("martin@alumno.unlam.edu.ar", "1234");
        verify(servicioEmailMock).guardarYEnviarCodigoDeVerificacion(usuario);

        assertThat(mv.getViewName(), is("verificar-token"));
    }


    @Test
    public void cuandoSeCreaElUSuarioElMismoTieneAccesoATodasLasMateriasDeLaCarrera(){
        Usuario usuario = new Usuario();
        List<Carrera> carreras = List.of(new Carrera("Tuko"),
        new Carrera("Tukin"));

        when(servicioCarreraMock.obtenerTodasLasCarreras()).thenReturn(carreras);

        ModelAndView mav = controladorLogin.nuevoUsuario();

        assertThat(mav.getViewName(), is("nuevo-usuario")); //Verifico el nombre de la vista
        assertThat(mav.getModel().get("usuario"), instanceOf(Usuario.class)); //Verifico si el usuario del modelo es un objeto Usuario
        assertThat(mav.getModel().get("carreras"), is(carreras)); //Verifico si el modelo de carreras tiene las carreras que ingresé
        verify(servicioCarreraMock).obtenerTodasLasCarreras(); //Verifico que se haya llamado al servicio
    }

    @Test
    public void cuandoSeSeleccionaCerrarSesionLaSesionSeCierra(){
        Usuario usuario = new Usuario();
        usuario.setId(4L);
        usuario.setApellido("Meza");
        usuario.setNombre("Martin");
        usuario.setEmail("martin@alumno.unlam.edu.ar");
        usuario.setPassword("1234");
        usuario.setCarreraID(1L);
        usuario.setSituacionLaboral("Desempleado");
        usuario.setDisponibilidadHoraria(20);

        when(requestMock.getSession(false)).thenReturn(sessionMock);

        ModelAndView mav = controladorLogin.logout(requestMock);

        verify(sessionMock).invalidate();
        assertThat(mav.getViewName(), is("redirect:/login"));
    }

    @Test
    public void cuandoSeVerificaElTokenMeRedirijeAElPaso2Materias(){
        Usuario usuario = new Usuario();
        usuario.setId(4L);
        String codigo = servicioEmailMock.generarCodigoVerificacion();

        when(servicioEmailMock.generarCodigoVerificacion()).thenReturn(codigo);

        ModelAndView mav = controladorLogin.verificarToken(codigo, usuario.getId());
        assertThat(mav.getViewName(), is("redirect:/registrarme/paso2"));
        assertThat(mav.getModel().get("idUser"), is(usuario.getId()));
        verify(servicioEmailMock).verificarCodigo(usuario.getId(), codigo);
    }

    @Test
    public void cuandoSeVerificaElTokenYEsIncorrectoMeDevuelveError() throws CodigoVerificacionIncorrecto{
        Long idUsuario = 4L;
        String codigo = "codigoIncorrecto";

        // Simulamos que el servicio lanza la excepción cuando se verifica un código inválido
        doThrow(new CodigoVerificacionIncorrecto("El código ingresado es incorrecto"))
                .when(servicioEmailMock).verificarCodigo(idUsuario, codigo);


        Usuario usuario = new Usuario();
        usuario.setId(idUsuario);
        when(servicioUsuarioMateriaMock.obtenerUsuario(idUsuario)).thenReturn(usuario);

        ModelAndView mav = controladorLogin.verificarToken(codigo, idUsuario);

        assertThat(mav.getViewName(), is("verificar-token"));
        assertThat(mav.getModel().get("error"), is("El código ingresado es incorrecto"));
        assertThat(((Usuario) mav.getModel().get("usuario")).getId(), is(idUsuario));

        // Verificar si se llamó al metodo
        verify(servicioEmailMock).verificarCodigo(idUsuario, codigo);
    }

    @Test
    public void cuandoSeVerificaElTokenYYaExpiroMeDevuelveError() throws CodigoVerificacionExpirado{
        Long idUsuario = 4L;
        String codigo = "codigoExpirado";

        // Simulamos que el servicio lanza la excepción cuando se verifica un código inválido
        doThrow(new CodigoVerificacionExpirado("El código ya expiró"))
                .when(servicioEmailMock).verificarCodigo(idUsuario, codigo);


        Usuario usuario = new Usuario();
        usuario.setId(idUsuario);
        when(servicioUsuarioMateriaMock.obtenerUsuario(idUsuario)).thenReturn(usuario);

        ModelAndView mav = controladorLogin.verificarToken(codigo, idUsuario);

        assertThat(mav.getViewName(), is("nuevo-usuario"));
        assertThat(mav.getModel().get("error"), is("El código ya expiró"));
        assertThat(((Usuario) mav.getModel().get("usuario")).getId(), is(idUsuario));

        // Verificar si se llamó al metodo
        verify(servicioEmailMock).verificarCodigo(idUsuario, codigo);
    }

    @Test
    public void cuandoSeVerificaElTokenHayUnErrorDeVerificacionYDevuelveError() throws Exception {
        Long idUsuario = 4L;
        String codigo = "codigoIncorrecto";

        // Simulamos que el servicio lanza la excepción cuando se verifica un código inválido
        doThrow(new CodigoVerificacionIncorrecto("Error de verificación"))
                .when(servicioEmailMock).verificarCodigo(idUsuario, codigo);


        Usuario usuario = new Usuario();
        usuario.setId(idUsuario);
        when(servicioUsuarioMateriaMock.obtenerUsuario(idUsuario)).thenReturn(usuario);

        ModelAndView mav = controladorLogin.verificarToken(codigo, idUsuario);

        assertThat(mav.getViewName(), is("verificar-token"));
        assertThat(mav.getModel().get("error"), is("Error de verificación"));
        assertThat(((Usuario) mav.getModel().get("usuario")).getId(), is(idUsuario));

        // Verificar si se llamó al metodo
        verify(servicioEmailMock).verificarCodigo(idUsuario, codigo);
    }

    @Test
    public void irAlFormularioDeMaterias() {
        // Datos de prueba
        Long idUsuario = 4L;
        Long idCarrera = 1L;

        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(idUsuario);
        usuarioMock.setCarreraID(idCarrera);

        Carrera carreras = new Carrera();

        List<Materia> materiasMock = Arrays.asList(
                new Materia("Matematica", carreras, 1),
                new Materia("Programacion", carreras, 1)
        );

        // Mocks
        when(servicioUsuarioMateriaMock.obtenerUsuario(idUsuario)).thenReturn(usuarioMock);
        when(servicioMateriaMock.obtenerMateriasPorCarrera("1")).thenReturn(materiasMock);

        ModelMap model = new ModelMap();

        // Ejecutar
        ModelAndView mav = controladorLogin.mostrarFormularioDeMaterias(model, idUsuario);

        // Verificar
        assertEquals("registroMateriasUsuario", mav.getViewName());
        assertTrue(mav.getModel().containsKey("materias"));
        assertEquals(materiasMock, mav.getModel().get("materias"));
    }

}



//Preparás los datos y mocks (Setup).
//
//Ejecutás el método que querés testear (Act).
//
//Verificás lo que esperás que pase (Assert).