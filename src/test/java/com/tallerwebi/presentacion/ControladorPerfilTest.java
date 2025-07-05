package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.UsuarioMateria;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.dominio.servicios.ServicioUsuarioMateria;
import com.tallerwebi.servicioInterfaz.ServicioUsuario;
import com.tallerwebi.serviciosImplementacion.ServicioCvImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class ControladorPerfilTest {

    private ServicioUsuario servicioUsuario;
    private ServicioUsuarioMateria servicioUsuarioMateria;
    private ServicioCvImpl servicioCv;
    private ControladorPerfil controladorPerfil;

    private HttpServletRequest request;
    private HttpSession session;

    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        servicioUsuario = mock(ServicioUsuario.class);
        servicioUsuarioMateria = mock(ServicioUsuarioMateria.class);
        servicioCv = mock(ServicioCvImpl.class);
        controladorPerfil = new ControladorPerfil(servicioUsuario, servicioUsuarioMateria, servicioCv);

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
    public void queSePuedaVisualizarElPerfilConSesionActiva() throws UsuarioNoEncontrado {
        when(session.getAttribute("ID")).thenReturn(1L);
        when(servicioUsuario.obtenerUsuario(1L)).thenReturn(usuario);

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
    public void queElControladorLlameAlServicioParaActualizarElPerfil() throws UsuarioNoEncontrado {
        when(session.getAttribute("ID")).thenReturn(1L);

        ModelAndView mav = controladorPerfil.actualizarPerfil(
                "Franco", "Nadal", "nuevo@email.com", "", request
        );

        assertThat(mav.getViewName(), is("redirect:/perfil"));
        verify(servicioUsuario).actualizarPerfil(1L, "Franco", "Nadal", "nuevo@email.com", "");
    }

    @Test
    public void queElControladorDelegueLaActualizacionDePasswordAlServicio() throws UsuarioNoEncontrado {
        when(session.getAttribute("ID")).thenReturn(1L);

        ModelAndView mav = controladorPerfil.actualizarPerfil(
                "Franco", "Nadal", "franco@test.com", "nuevaPassword123", request
        );

        assertThat(mav.getViewName(), is("redirect:/perfil"));
        verify(servicioUsuario).actualizarPerfil(1L, "Franco", "Nadal", "franco@test.com", "nuevaPassword123");
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
    public void queElControladorDelegueLaActualizacionDelNombreAlServicio() throws UsuarioNoEncontrado {
        when(session.getAttribute("ID")).thenReturn(1L);

        ModelAndView mav = controladorPerfil.actualizarPerfil(
                "NuevoNombre", "Nadal", "franco@test.com", null, request
        );

        assertThat(mav.getViewName(), is("redirect:/perfil"));
        verify(servicioUsuario).actualizarPerfil(1L, "NuevoNombre", "Nadal", "franco@test.com", null);
    }

    @Test
    public void queElControladorDelegueLaActualizacionDelApellidoAlServicio() throws UsuarioNoEncontrado {
        when(session.getAttribute("ID")).thenReturn(1L);

        ModelAndView mav = controladorPerfil.actualizarPerfil(
                "Franco", "NuevoApellido", "franco@test.com", null, request
        );

        assertThat(mav.getViewName(), is("redirect:/perfil"));
        verify(servicioUsuario).actualizarPerfil(1L, "Franco", "NuevoApellido", "franco@test.com", null);
    }


    // NUEVO TEST para el endpoint /perfil/generar-cv
    @Test
    public void queSeGenereElCVCorrectamente() throws Exception {
        when(session.getAttribute("ID")).thenReturn(1L);

        UsuarioMateria materiaAprobada = mock(UsuarioMateria.class);
        when(materiaAprobada.estaAprobada()).thenReturn(true);

        List<UsuarioMateria> materias = Collections.singletonList(materiaAprobada);
        when(servicioUsuarioMateria.mostrarMateriasDeUsuario(null, 1L)).thenReturn(materias);
        when(servicioCv.generarYGuardarCV(1L, materias)).thenReturn("Texto del CV generado");

        ResponseEntity<String> response = controladorPerfil.generarCV(session);

        assertThat(response.getStatusCodeValue(), is(200));
        assertThat(response.getBody(), is("Texto del CV generado"));
        verify(servicioCv).generarYGuardarCV(1L, materias);
    }


    @Test
    public void queGenerarCVDevuelva401SiNoHaySesion() {
        when(session.getAttribute("ID")).thenReturn(null);

        ResponseEntity<String> response = controladorPerfil.generarCV(session);

        assertThat(response.getStatusCodeValue(), is(401));
        assertThat(response.getBody(), containsString("Usuario no autenticado"));
    }

}
