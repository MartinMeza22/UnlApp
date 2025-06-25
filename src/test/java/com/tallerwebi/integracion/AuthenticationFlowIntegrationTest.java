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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SpringWebTestConfig.class, HibernateTestConfig.class})
public class AuthenticationFlowIntegrationTest {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

//    @Test
//    public void debePermitirFlujosCompletos() throws Exception {
//        // Step 1: User tries to access protected route without login
//        MvcResult result = this.mockMvc.perform(get("/home"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/login"))
//                .andReturn();
//
//        // Step 2: User goes to login page
//        this.mockMvc.perform(get("/login"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("login"));
//
//        // Step 3: User attempts login with wrong credentials
//        this.mockMvc.perform(post("/validar-login")
//                        .param("email", "wrong@email.com")
//                        .param("password", "wrongpassword"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("login"))
//                .andExpect(model().attributeExists("error"));
//
//        // Step 4: User attempts login with correct credentials
//        MvcResult loginResult = this.mockMvc.perform(post("/validar-login")
//                        .param("email", "user@gmail.com")
//                        .param("password", "123"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/home"))
//                .andReturn();
//
//        // Get the session from the login result
//        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession();
//
//        // Step 5: Verify session has the required attributes
//        assertNotNull(session.getAttribute("ID"));
//        assertNotNull(session.getAttribute("ROL"));
//        assertNotNull(session.getAttribute("NOMBRE"));
//
//        // Step 6: User can now access protected routes
//        this.mockMvc.perform(get("/home").session(session))
//                .andExpect(status().isOk())
//                .andExpect(view().name("home"));
//
//        this.mockMvc.perform(get("/calendario").session(session))
//                .andExpect(status().isOk());
//
//        // Step 7: User logs out
//        this.mockMvc.perform(get("/logout").session(session))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/login"));
//
//        // Step 8: After logout, user can't access protected routes
//        this.mockMvc.perform(get("/home").session(session))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/login"));
//    }

//    @Test
//    public void debePermitirRegistroCompletoSinAutenticacion() throws Exception {
//        // Step 1: Access registration form
//        this.mockMvc.perform(get("/nuevo-usuario"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("nuevo-usuario"));
//
//        // Step 2: Submit registration (will likely fail due to missing services in test, but should not be blocked by interceptor)
//        this.mockMvc.perform(post("/registrarme")
//                        .param("nombre", "Test")
//                        .param("apellido", "User")
//                        .param("email", "testuser@test.com")
//                        .param("password", "testpassword"))
//                .andExpect(status().isOk()); // Should not redirect to login
//
//        this.mockMvc.perform(post("/registrarme/paso2"))
//                .andExpect(status().isOk());
//        // Step 3: Access step 2 of registration
//        this.mockMvc.perform(post("/registrarme/paso2"))
//                .andExpect(status().isOk()); // Should not redirect to login
//    }

    @Test
    public void debeBloquearAccesoSelectivoAControladores() throws Exception {
        // Test that specific controller methods are protected
        String[] protectedEndpoints = {
                "/home",
                "/calendario",
                "/foro",
                "/progreso",
                "/materias"
        };

        // All should redirect to login without authentication
        for (String endpoint : protectedEndpoints) {
            this.mockMvc.perform(get(endpoint))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"));
        }

        // Create authenticated session
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("ID", 1L);
        session.setAttribute("ROL", "ALUMNO");
        session.setAttribute("NOMBRE", "Test User");

        // All should be accessible with authentication (may have different response codes based on controller logic)
        for (String endpoint : protectedEndpoints) {
            this.mockMvc.perform(get(endpoint).session(session))
                    .andExpect(status().is(200)); // Should not redirect
        }
    }

    @Test
    public void debePermitirRutasPublicasSinRestriccion() throws Exception {
        String[] publicEndpoints = {
                "/login",
                "/nuevo-usuario",
        };

        // All should be accessible without authentication
        for (String endpoint : publicEndpoints) {
            this.mockMvc.perform(get(endpoint))
                    .andExpect(status().is2xxSuccessful()); // Should not redirect to login
        }
    }

    @Test
    public void debeManejaDiferentesTiposDeID() throws Exception {
        // Test with different ID types
        Object[] validIds = {1, 0, -1, 99999, "1", "test"};

        for (Object id : validIds) {
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("ID", id);
            session.setAttribute("ROL", "ALUMNO");
            session.setAttribute("NOMBRE", "Test User");

            this.mockMvc.perform(get("/home").session(session))
                    .andExpect(status().isOk())
                    .andExpect(view().name("home"));
        }
    }

    @Test
    public void debeBloquearAccesoConSessionIncompleta() throws Exception {
        // Test with session that has only some attributes
        MockHttpSession sessionWithoutId = new MockHttpSession();
        sessionWithoutId.setAttribute("ROL", "ALUMNO");
        sessionWithoutId.setAttribute("NOMBRE", "Test User");
        // Missing ID

        this.mockMvc.perform(get("/home").session(sessionWithoutId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        // Test with session that has ID as null
        MockHttpSession sessionWithNullId = new MockHttpSession();
        sessionWithNullId.setAttribute("ID", null);
        sessionWithNullId.setAttribute("ROL", "ALUMNO");
        sessionWithNullId.setAttribute("NOMBRE", "Test User");

        this.mockMvc.perform(get("/home").session(sessionWithNullId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
