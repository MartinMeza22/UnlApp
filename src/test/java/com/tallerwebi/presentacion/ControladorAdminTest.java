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



   
}
