package com.tallerwebi.punta_a_punta;

import com.microsoft.playwright.*;
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
    @Test
    void deberiaMostrarYDescargarElResumenInteligente() {
        // Login
        vistaLogin.escribirEMAIL("user@gmail.com");
        vistaLogin.escribirClave("12345678");
        vistaLogin.darClickEnIniciarSesion();

        VistaHome vistaHome = new VistaHome(page);

        // Mostrar resumen
        vistaHome.darClickEnBotonVerResumen();

        // Esperar a que cargue el modal con el texto generado
        page.waitForSelector("#contenidoResumen", new Page.WaitForSelectorOptions().setTimeout(5000));
        String contenido = page.locator("#contenidoResumen").textContent();
        assertThat(contenido, containsString("fortaleza"));

        // Descargar PDF
        Download descarga = page.waitForDownload(() -> {
            vistaHome.darClickEnBotonDescargarPDF();
        });

        Path archivoPDF = descarga.path();
        assertThat("El archivo descargado debe ser un PDF", archivoPDF.toString(), endsWith(".pdf"));
        assertThat("El archivo debe existir", archivoPDF.toFile().exists(), is(true));
    }
}
