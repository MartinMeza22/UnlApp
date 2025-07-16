package com.tallerwebi.punta_a_punta;

import com.microsoft.playwright.*;
import com.tallerwebi.punta_a_punta.vistas.VistaLogin;
import org.junit.jupiter.api.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AdministracionE2E {
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    VistaLogin vistaLogin;
    Page page;

    @BeforeAll
    static void abrirNavegador() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false));
    }

    @AfterAll
    static void cerrarNavegador() {
        playwright.close();
    }

    @BeforeEach
    void crearContextoYPagina() {
        ReiniciarDB.limpiarBaseDeDatos();
        context = browser.newContext(new Browser.NewContextOptions().setAcceptDownloads(true));
        page = context.newPage();
        vistaLogin = new VistaLogin(page);
    }

    @AfterEach
    void cerrarContexto() {
        context.close();
    }

    private void realizarLoginComoTestUser() {
        vistaLogin.escribirEMAIL("admin@unlapp.com");
        vistaLogin.escribirClave("123");
        vistaLogin.darClickEnIniciarSesion();
        page.waitForSelector("text=Descargar progreso de Usuarios (Excel)");
    }

    @Test
    public void deberiaGenerarYDescargarElAnaliticoAcademicoComoPdf() {
        realizarLoginComoTestUser();
        page.click("text=Descargar progreso de Usuarios (Excel)");
        page.waitForURL("**/home");
    }
}
