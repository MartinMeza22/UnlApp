package com.tallerwebi.punta_a_punta;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.tallerwebi.punta_a_punta.vistas.VistaLogin;
import com.tallerwebi.punta_a_punta.vistas.VistaProgreso;
import org.junit.jupiter.api.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class VistaProgresoYModalMateriaE2E {

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
    public void deberiaPermitirCursarYPonerNotaAUnaMateria() {
        realizarLoginComoTestUser();
        page.click("text=Ver progreso académico");
        page.waitForURL("**/progreso");

        String materiaACursar = "Programación Básica II";
        String notaFinal = "8";
        String dificultad = "2";

        VistaProgreso vistaProgreso = new VistaProgreso(page);
        vistaProgreso.cursarMateria(materiaACursar);

        page.waitForURL("**/progreso");
        String estadoDespuesDeCursar = vistaProgreso.obtenerEstadoMateria(materiaACursar).trim();
        assertThat("La materia debería estar en estado CURSANDO", estadoDespuesDeCursar, is("CURSANDO"));

        vistaProgreso.abrirModalDeEdicion(materiaACursar);
        page.waitForSelector("#materiaEditModal", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));

        vistaProgreso.editarNotaYDificultadEnModal(notaFinal, dificultad);
        vistaProgreso.guardarCambiosModal();

        page.waitForURL("**/progreso");
        String estadoFinal = vistaProgreso.obtenerEstadoMateria(materiaACursar).trim();
        String notaObtenida = vistaProgreso.obtenerNotaDeMateria(materiaACursar).trim();

        assertThat("La materia debería estar APROBADA", estadoFinal, is("APROBADA"));
        assertThat("La nota debería ser la que se guardó", notaObtenida, is(notaFinal));
    }

    @Test
    public void deberiaPermitirModificarUnaMateriaPonerlaDesaprobadaConDificultadDificilYGuardarla() {
        realizarLoginComoTestUser();
        page.click("text=Ver progreso académico");
        page.waitForURL("**/progreso");

        String materiaAModificar = "Taller Web I";
        String nuevaNota = "3";
        String nuevaDificultad = "3";

        VistaProgreso vistaProgreso = new VistaProgreso(page);

        vistaProgreso.abrirModalDeEdicion(materiaAModificar);
        page.waitForSelector("#materiaEditModal", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));

        vistaProgreso.editarNotaYDificultadEnModal(nuevaNota, nuevaDificultad);
        vistaProgreso.guardarCambiosModal();

        page.waitForURL("**/progreso");
        String estadoFinal = vistaProgreso.obtenerEstadoMateria(materiaAModificar).trim();
        String notaObtenida = vistaProgreso.obtenerNotaDeMateria(materiaAModificar).trim();

        assertThat("La materia debería estar DESAPROBADA", estadoFinal, is("DESAPROBADA"));
        assertThat("La nota debería ser la modificada", notaObtenida, is(nuevaNota));
    }

//    @Test
//    public void queCuandoElUsuarioPongaCursarUnaMateriaPeroSeArrepientaYVuelvaDejarlaMateriaYSuEstadoSeaPendiente() {
//        realizarLoginComoTestUser();
//        page.click("text=Ver progreso académico");
//        page.waitForURL("**/progreso");
//
//        String materiaADejarDeCursar = "Base de Datos II";
//
//        VistaProgreso vistaProgreso = new VistaProgreso(page);
//
//        vistaProgreso.cursarMateria(materiaADejarDeCursar);
//        String obtenerEstadoDeLaMateria = vistaProgreso.obtenerEstadoMateria(materiaADejarDeCursar);
//        assertThat("La materia debería estar CURSANDO", obtenerEstadoDeLaMateria, is("CURSANDO"));
//
//        vistaProgreso.dejarMateria(materiaADejarDeCursar);
//        page.waitForURL("**/progreso");
//
//        String estadoFinal = vistaProgreso.obtenerEstadoMateria(materiaADejarDeCursar).trim();
//        assertThat("La materia debería estar PENDIENTE", estadoFinal, is("PENDIENTE"));
//
//        Boolean botonCursarDisponible = vistaProgreso.botonCursarEstaDisponible(materiaADejarDeCursar);
//        assertThat("El botón de cursar debería estar disponible", botonCursarDisponible, is(true));
//    }

    // @Test
    // public void queSePuedanFiltrarLasMateriasAprobadas() {
    //     realizarLoginComoTestUser();
    //     page.click("text=Ver progreso académico");
    //     page.waitForURL("**/progreso");

    //     VistaProgreso vistaProgreso = new VistaProgreso(page);
    //     Integer totalMaterias = vistaProgreso.obtenerMaterias().count();

    //     vistaProgreso.filtrar("Aprobadas");
    //     page.waitForURL("**/progreso?condicion=Aprobadas");

    //     Integer totalMateriasAprobadas = vistaProgreso.obtenerMaterias().count();
    //     assertThat("Debería haber al menos una materia aprobada", totalMateriasAprobadas, is(greaterThan(0)));

    //     Boolean todasLasMateriasAprobadas = vistaProgreso.todasLasMateriasVisibleTienenEstado("APROBADA");
    //     assertThat("Todas las materias mostradas deberían estar aprobadas", todasLasMateriasAprobadas, is(true));

    //     vistaProgreso.filtrar("");
    //     page.waitForURL("**/progreso");
    // }
}
