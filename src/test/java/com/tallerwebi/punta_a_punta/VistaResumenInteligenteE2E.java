package com.tallerwebi.punta_a_punta;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.tallerwebi.punta_a_punta.vistas.VistaHome;
import com.tallerwebi.punta_a_punta.vistas.VistaLogin;
import org.junit.jupiter.api.*;

import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
public class VistaResumenInteligenteE2E {
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    VistaLogin vistaLogin;
    Page page;

    @BeforeAll
    static void abrirNavegador() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(50));
    }

    @AfterAll
    static void cerrarNavegador() {
        playwright.close();
    }
    @BeforeEach
    void crearContextoYPagina() {
       // ReiniciarDB.limpiarBaseDeDatos();

        context = browser.newContext(new Browser.NewContextOptions().setAcceptDownloads(true)); // clave para capturar descargas
        page = context.newPage();
        vistaLogin = new VistaLogin(page);
    }

    @AfterEach
    void cerrarContexto() {
        context.close();
    }
    private void realizarLoginComoTestUser() {
        vistaLogin.escribirEMAIL("user@gmail.com");
        vistaLogin.escribirClave("12345678");
        vistaLogin.darClickEnIniciarSesion();
        // Esperar a que aparezca el botón que solo está en el home
        page.waitForSelector("text=Ver progreso académico");
    }

    @Test
    void deberiaMostrarElResumenInteligente() {
        realizarLoginComoTestUser();

        // Ir al progreso académico
        page.click("text=Ver progreso académico");
        page.waitForURL("**/progreso");
//mostrar resuemn
        page.click("#btnMostrarResumen");
        page.waitForSelector(".modal.show >> #contenidoResumen", new Page.WaitForSelectorOptions().setTimeout(10000).setState(WaitForSelectorState.VISIBLE));


        String contenido = page.locator("#contenidoResumen").textContent();

        assertThat("El resumen debería contener información sobre fortalezas",
                contenido.toLowerCase(),
                anyOf(
                        containsString("fortaleza"),
                        containsString("fuerte"),
                        containsString("tu fuerte radica")
                ));
    }

}
