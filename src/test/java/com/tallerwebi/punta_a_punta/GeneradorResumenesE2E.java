package com.tallerwebi.punta_a_punta;

import com.microsoft.playwright.*;
import com.tallerwebi.punta_a_punta.vistas.VistaLogin;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GeneradorResumenesE2E {

    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;
    VistaLogin vistaLogin;

    @BeforeAll
    static void abrirNavegador() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
        );
    }

    @AfterAll
    static void cerrarNavegador() {
        playwright.close();
    }

    @BeforeEach
    void crearContextoYPagina() {
        ReiniciarDB.limpiarBaseDeDatos();
        context = browser.newContext();
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
        page.waitForSelector("text=Generador de Resúmenes");
    }

    @Test
    public void deberiaGenerarYMostrarUnResumen() {
        realizarLoginComoTestUser();

        page.click("text=Generador de Resúmenes");
        page.waitForURL("**/generar-resumen");

        String tema = "La Segunda Guerra Mundial y sus causas principales";

        // Llenar textarea y enviar formulario
        Locator textarea = page.locator("textarea#tema");
        textarea.fill(tema);

        page.click("button.btn-primary"); // Botón "Generar resumen"

        // Esperar el resumen generado
        Locator resumenGenerado = page.locator("h4:has-text('Resumen generado:') + p");
        resumenGenerado.waitFor(new Locator.WaitForOptions().setTimeout(10000));  // Por si la API demora

        assertTrue(resumenGenerado.isVisible(), "El resumen generado debería mostrarse");

        // Verificar que ahora "Mis resúmenes" muestre el tema ingresado
        Locator tituloResumen = page.locator("h5", new Page.LocatorOptions().setHasText(tema));
        assertTrue(tituloResumen.isVisible(), "El resumen generado debería aparecer en la lista de Mis resúmenes");
    }
}

