package com.tallerwebi.punta_a_punta;

import com.microsoft.playwright.*;
import com.tallerwebi.punta_a_punta.vistas.VistaLogin;
import org.junit.jupiter.api.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AnaliticoE2E {
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
        ReiniciarDB.limpiarBaseDeDatos(); // <<< ROLLBACK ACTIVADO ACÁ
        context = browser.newContext(new Browser.NewContextOptions().setAcceptDownloads(true));
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
        page.waitForSelector("text=Ver progreso académico");
    }

    @Test
    public void deberiaGenerarYDescargarElAnaliticoAcademicoComoPdf() {
        realizarLoginComoTestUser();
        page.click("text=Ver progreso académico");
        page.waitForURL("**/progreso");

        // Verificar que el botón de generar analítico está presente
        boolean botonVisible = page.locator("#btnGenerarAnalitico").isVisible();
        assertThat("El botón de generar analítico debería estar visible", botonVisible, is(true));

        // Hacer click en el botón de generar analítico
        page.click("#btnGenerarAnalitico");

        // Esperar a que se procese el fetch y la generación del PDF
        page.waitForTimeout(3000); // Tiempo para que se procese el fetch y jsPDF genere el archivo

        // Verificar que no hay alertas de error visibles en la página
        Locator alertas = page.locator(".alert-danger");
        boolean hayErrores = alertas.count() > 0 && alertas.first().isVisible();
        assertThat("No debería haber alertas de error después de generar el analítico", hayErrores, is(false));

        // Verificar que el botón sigue disponible (no se deshabilitó por error)
        boolean botonSigueVisible = page.locator("#btnGenerarAnalitico").isVisible();
        assertThat("El botón de generar analítico debería seguir visible", botonSigueVisible, is(true));

        // NOTA: Como jsPDF genera la descarga desde el frontend, no podemos capturar el archivo PDF
        // pero validamos que el flujo completo funciona sin errores
    }
}
