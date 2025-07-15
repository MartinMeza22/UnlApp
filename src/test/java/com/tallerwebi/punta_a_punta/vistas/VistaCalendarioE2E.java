package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.tallerwebi.punta_a_punta.ReiniciarDB;
import org.junit.jupiter.api.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VistaCalendarioE2E {
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    VistaLogin vistaLogin;
    Page page;

    @BeforeAll
    static void abrirNavegador() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)       // Importante: que no sea headless si querés ver algo
        );
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
        page.waitForSelector("text=Calendario");
    }

    @Test
    public void deberiaPermitirAgregarUnEvento() {
        realizarLoginComoTestUser();
        page.click("text=Calendario");
        page.waitForURL("**/calendario");

        // Abrir modal
        page.click("text=Agregar Evento");
        page.locator(".abrir-modal").click();

        // Datos del evento
        LocalDateTime fechaDeInicio = LocalDateTime.of(2025, 7, 20, 10, 0);
        LocalDateTime fechaDeFinalizacion = LocalDateTime.of(2025, 7, 30, 12, 0);
        String tituloEvento = "Examen cercano de Seguridad y Redes";
        String tipoEvento = "EXAMEN";
        String descripcion = "Estudiar TCP/IP, OSI, temas relacionados";

        // Lógica para llenar y guardar
        VistaCalendario vistaCalendario = new VistaCalendario(page);
        page.waitForSelector("#eventModal.show", new Page.WaitForSelectorOptions().setTimeout(5000));


        page.waitForTimeout(1000);
        vistaCalendario.editarTituloTipoFechaYDescripcion(
                tituloEvento, tipoEvento, descripcion, fechaDeInicio, fechaDeFinalizacion
        );

        Locator eventoCreado = page.locator(".evento-title", new Page.LocatorOptions().setHasText(tituloEvento));
        assertTrue(eventoCreado.isVisible(), "El evento debería aparecer en la lista del calendario");
    }

}
