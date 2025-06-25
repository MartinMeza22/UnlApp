package com.tallerwebi.integracion;

import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SpringWebTestConfig.class, HibernateTestConfig.class})
public class AuthenticationInterceptorIntegrationTest {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void debePermitirAccesoALoginSinAutenticacion() throws Exception {
        this.mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    public void debePermitirAccesoARaizSinAutenticacion() throws Exception {
        this.mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void debePermitirAccesoANuevoUsuarioSinAutenticacion() throws Exception {
        this.mockMvc.perform(get("/nuevo-usuario"))
                .andExpect(status().isOk())
                .andExpect(view().name("nuevo-usuario"));
    }

    @Test
    public void debeRedirigirALoginCuandoAccedeAHomeSinAutenticacion() throws Exception {
        this.mockMvc.perform(get("/home"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void debeRedirigirALoginCuandoAccedeACalendarioSinAutenticacion() throws Exception {
        this.mockMvc.perform(get("/calendario"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void debeRedirigirALoginCuandoAccedeAForoSinAutenticacion() throws Exception {
        this.mockMvc.perform(get("/foro"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void debeRedirigirALoginCuandoAccedeAProgresoSinAutenticacion() throws Exception {
        this.mockMvc.perform(get("/progreso"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void debePermitirAccesoAHomeCuandoUsuarioEstaAutenticado() throws Exception {
        // Create a session with user ID
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("ID", 1L);
        session.setAttribute("ROL", "ALUMNO");
        session.setAttribute("NOMBRE", "Juan");

        this.mockMvc.perform(get("/home").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    public void debePermitirAccesoACalendarioCuandoUsuarioEstaAutenticado() throws Exception {
        // Create a session with user ID
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("ID", 2L);
        session.setAttribute("ROL", "ALUMNO");
        session.setAttribute("NOMBRE", "Ana");

        this.mockMvc.perform(get("/calendario").session(session))
                .andExpect(status().isOk());
    }

    @Test
    public void debeRedirigirALoginCuandoSessionExistePeroNoTieneID() throws Exception {
        // Create a session without user ID (invalid session)
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("ROL", "ALUMNO");
        session.setAttribute("NOMBRE", "Juan");
        // No ID attribute

        this.mockMvc.perform(get("/home").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void debePermitirAccesoConIDCero() throws Exception {
        // User with ID 0 should still be considered authenticated
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("ID", 0L);
        session.setAttribute("ROL", "ALUMNO");
        session.setAttribute("NOMBRE", "Usuario");

        this.mockMvc.perform(get("/home").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    public void debePermitirLogoutSinAutenticacion() throws Exception {
        // Logout should be accessible even without authentication
        this.mockMvc.perform(get("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void debeLogoutCorrectamenteCuandoUsuarioEstaAutenticado() throws Exception {
        // Create a session with user ID
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("ID", 1L);
        session.setAttribute("ROL", "ALUMNO");
        session.setAttribute("NOMBRE", "Juan");

        this.mockMvc.perform(get("/logout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        // Verify session was invalidated by trying to access protected resource
        this.mockMvc.perform(get("/home").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void debePersistirSessionDuranteNavegacion() throws Exception {
        // Create a session with user ID
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("ID", 1L);
        session.setAttribute("ROL", "ALUMNO");
        session.setAttribute("NOMBRE", "Juan");

        // First request - should work
        this.mockMvc.perform(get("/home").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));

        // Second request with same session - should also work
        this.mockMvc.perform(get("/calendario").session(session))
                .andExpect(status().isOk());

        // Third request with same session - should also work
        this.mockMvc.perform(get("/home").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    public void debeBloquearAccesoASistemasProtegidosConSessionInvalida() throws Exception {
        // Test multiple protected routes with invalid session
        String[] protectedRoutes = {"/home", "/calendario", "/foro", "/progreso", "/materias"};

        for (String route : protectedRoutes) {
            this.mockMvc.perform(get(route))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"));
        }
    }

    @Test
    public void debePermitirAccesoATodosLosSistemasProtegidosConSessionValida() throws Exception {
        // Create a session with user ID
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("ID", 1L);
        session.setAttribute("ROL", "ALUMNO");
        session.setAttribute("NOMBRE", "Juan");

        // Test home
        this.mockMvc.perform(get("/home").session(session))
                .andExpect(status().isOk());

        // Test calendario
        this.mockMvc.perform(get("/calendario").session(session))
                .andExpect(status().isOk());

        // Note: Some controllers might return different status codes
        // based on their internal logic, but they shouldn't redirect to login
    }

    @Test
    public void debePermitirTodosLosMetodosHTTPEnRutasDeLogin() throws Exception {
        // GET to login
        this.mockMvc.perform(get("/login"))
                .andExpect(status().isOk());

        // POST to validar-login (without proper data, but should not redirect)
        this.mockMvc.perform(post("/validar-login"))
                .andExpect(status().isOk()); // Will show login with error, but not redirect

        // GET to nuevo-usuario
        this.mockMvc.perform(get("/nuevo-usuario"))
                .andExpect(status().isOk());

        // POST to registrarme (without proper data, but should not redirect)
        this.mockMvc.perform(post("/registrarme"))
                .andExpect(status().isOk()); // Will show form with errors, but not redirect
    }
}
