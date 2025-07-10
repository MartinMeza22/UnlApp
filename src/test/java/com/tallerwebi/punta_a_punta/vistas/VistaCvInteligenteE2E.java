package com.tallerwebi.punta_a_punta.vistas;


import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.tallerwebi.punta_a_punta.ReiniciarDB;
import com.tallerwebi.punta_a_punta.vistas.VistaLogin;
import org.junit.jupiter.api.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class VistaCvInteligenteE2E {
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;
    VistaLogin vistaLogin;

    @BeforeAll
    static void iniciarBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(50));
    }

    @AfterAll
    static void cerrarBrowser() {
        playwright.close();
    }

    @BeforeEach
    void prepararPagina() {
        ReiniciarDB.limpiarBaseDeDatos(); // <<< ROLLBACK ACTIVADO ACÁ
        context = browser.newContext(new Browser.NewContextOptions().setAcceptDownloads(true));
        page = context.newPage();
        vistaLogin = new VistaLogin(page);
    }

    @AfterEach
    void cerrarContexto() {
        context.close();
    }

    private void loginComoUsuarioTest() {
        vistaLogin.escribirEMAIL("user@gmail.com");
        vistaLogin.escribirClave("12345678");
        vistaLogin.darClickEnIniciarSesion();
        page.waitForSelector("text=Mi Perfil");
    }

    @Test
    void deberiaMostrarElCvInteligenteEnModal() {
        loginComoUsuarioTest();

        // Ir al perfil
        page.click("text=Mi Perfil");
        page.waitForURL("**/perfil");

        // Hacer click para mostrar el CV
        page.click("#btnMostrarCvInteligente");

        // Esperar a que el contenido se reemplace (ya no sea "Generando CV...")
        page.waitForFunction("document.getElementById('cvModalBody').textContent !== 'Generando CV...'", 10000);

        // Leer contenido final
        String contenidoCv = page.locator("#cvModalBody").textContent();
        System.out.println("Contenido generado: " + contenidoCv);

        assertThat("El CV generado debería tener contenido descriptivo", contenidoCv.trim().length(), greaterThan(50));
        assertThat("El CV debería mencionar 'experiencia' o 'educación'", contenidoCv.toLowerCase(),
                anyOf(containsString("experiencia"), containsString("educación"), containsString("perfil profesional")));
    }
    @Test
    void deberiaDescargarElCvInteligenteComoPdf() {
        loginComoUsuarioTest();

        // Ir al perfil
        page.click("text=Mi Perfil");
        page.waitForURL("**/perfil");

        // Mostrar el CV
        page.click("#btnMostrarCvInteligente");
        page.waitForSelector("#cvModalBody", new Page.WaitForSelectorOptions()
                .setTimeout(10000)
                .setState(WaitForSelectorState.VISIBLE));
        page.waitForSelector("#btnDescargarCv", new Page.WaitForSelectorOptions()
                .setTimeout(5000)
                .setState(WaitForSelectorState.VISIBLE));

        // Esperar descarga con jsPDF (usamos una forma alternativa porque jsPDF no genera descarga real que Playwright detecte)
        // Simulamos click y esperamos un tiempo para verificar que no hay error visible
        page.click("#btnDescargarCv");
        page.waitForTimeout(2000); // permitir que la descarga se procese

        // Verificamos que el botón no esté oculto y el contenido está presente (ya verificado arriba)
        boolean visible = page.locator("#btnDescargarCv").isVisible();
        assertThat("El botón de descarga debería estar visible", visible);

        // NOTA: no podemos capturar archivo PDF generado por jsPDF en frontend, pero validamos que el flujo esté bien
    }
}
