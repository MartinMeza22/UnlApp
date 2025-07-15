package com.tallerwebi.presentacion;
import com.tallerwebi.dominio.Reporte;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.Carrera;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.servicioInterfaz.ServicioAdmin;
import com.tallerwebi.servicioInterfaz.ServicioReporte;
import com.tallerwebi.servicioInterfaz.ServicioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ControladorAdminTest {

    private ServicioUsuario servicioUsuarioMock;
    private ServicioReporte servicioReporteMock;
    private ServicioAdmin servicioAdminMock;

    private ControladorAdmin controlador;

    @BeforeEach
    void setUp() {
        servicioUsuarioMock = mock(ServicioUsuario.class);
        servicioReporteMock = mock(ServicioReporte.class);
        servicioAdminMock   = mock(ServicioAdmin.class);

        controlador = new ControladorAdmin(
                servicioUsuarioMock,
                servicioReporteMock,
                servicioAdminMock
        );
    }

    @Test
    void debeMostrarPanelSiAdminAutenticado() throws UsuarioNoEncontrado {
        // ---- datos de prueba ----
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("ROL", "ADMIN");
        session.setAttribute("ID", 1L);

        Carrera carrera = new Carrera();
        carrera.setNombre("Ingeniería");
        Usuario admin = new Usuario();
        admin.setId(1L);
        admin.setNombre("Admin");
        admin.setCarrera(carrera);

        List<Reporte> reportes = List.of(new Reporte());

        when(servicioUsuarioMock.obtenerUsuario(1L)).thenReturn(admin);
        when(servicioReporteMock.obtenerReportesPorCarrera(carrera)).thenReturn(reportes);

        // ---- ejecución ----
        ModelAndView mav = controlador.mostrarPanelDeReportes(session);

        // ---- verificaciones ----
        assertThat(mav.getViewName(), is("admin-panel"));
        assertThat(mav.getModel(), hasEntry("admin", admin));
        assertThat(mav.getModel(), hasEntry("reportes", reportes));

        verify(servicioUsuarioMock).obtenerUsuario(1L);
        verify(servicioReporteMock).obtenerReportesPorCarrera(carrera);
    }

    @Test
    void debeRedirigirAHomeSiNoEsAdmin() {
        MockHttpSession session = new MockHttpSession(); // sin atributos

        ModelAndView mav = controlador.mostrarPanelDeReportes(session);

        assertThat(mav.getViewName(), is("redirect:/home"));
        verifyNoInteractions(servicioUsuarioMock, servicioReporteMock);
    }
    @Test
    void debeRedirigirALoginSiUsuarioNoEncontrado() throws UsuarioNoEncontrado {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("ROL", "ADMIN");
        session.setAttribute("ID", 99L);

        when(servicioUsuarioMock.obtenerUsuario(99L)).thenThrow(new UsuarioNoEncontrado());

        ModelAndView mav = controlador.mostrarPanelDeReportes(session);

        assertThat(mav.getViewName(), is("redirect:/login"));
    }
    @Test
    void debeEliminarReporteYAgregarFlashDeExito() {
        RedirectAttributes flashes = new RedirectAttributesModelMap();

        ModelAndView mav = controlador.eliminarReporte(10L, flashes);

        assertThat(mav.getViewName(), is("redirect:/admin/panel"));
        assertThat(flashes.getFlashAttributes(), hasEntry("exito",
                "El reporte fue descartado correctamente."));
        verify(servicioReporteMock).eliminarReporte(10L);
    }

    @Test
    void debeMostrarGraficosConDatos() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("ROL", "ADMIN");
        session.setAttribute("ID", 1L);

        Map<String, Long> usuariosCarrera   = Map.of("Ing", 3L);
        Map<String, Long> usuariosLaboral   = Map.of("Desempleado", 2L);
        Map<String, Long> publicacionesCarr = Map.of("Ing", 1L);

        when(servicioAdminMock.obtenerUsuariosPorCarrera()).thenReturn(usuariosCarrera);
        when(servicioAdminMock.obtenerUsuariosPorSituacionLaboral()).thenReturn(usuariosLaboral);
        when(servicioAdminMock.obtenerPublicacionesPorCarrera()).thenReturn(publicacionesCarr);

        ModelAndView mav = controlador.mostrarGraficos(session);

        assertThat(mav.getViewName(), is("admin-graficos"));
        assertThat(mav.getModel(), allOf(
                hasEntry("usuariosPorCarrera", usuariosCarrera),
                hasEntry("usuariosPorSituacionLaboral", usuariosLaboral),
                hasEntry("publicacionesPorCarrera", publicacionesCarr)
        ));
    }
}
