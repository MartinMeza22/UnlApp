package com.tallerwebi.punta_a_punta;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.tallerwebi.punta_a_punta.vistas.VistaLogin;
import org.junit.jupiter.api.*;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class VistaPlanDeEstudiosE2E {

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
    @Sql(scripts = "/reset-db.sql")
    void crearContextoYPagina() {
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
        // Esperar a que aparezca el botón que solo está en el home
        page.waitForSelector("text=Ver progreso académico");
    }

    @Test
    void deberiaCargarLaPaginaDePlanDeEstudios() {
        realizarLoginComoTestUser();

        // Ir al progreso académico
        page.click("text=Ver mi Plan de Estudio");
        page.waitForURL("**/materias");

        // Verificar que el título de la página esté presente
        page.waitForSelector("h1.page-title", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));

        String titulo = page.locator("h1.page-title").textContent();
        assertThat("El título debería ser 'Plan de Estudios por Cuatrimestre'",
                titulo, equalTo("Plan de Estudios por Cuatrimestre"));
    }

    @Test
    void deberiaMostrarLaPrimeraMateriaConSusDetalles() {
        realizarLoginComoTestUser();

        // Ir al progreso académico
        page.click("text=Ver mi Plan de Estudio");
        page.waitForURL("**/materias");

        // Esperar a que aparezca al menos una materia
        page.waitForSelector(".materia-card", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));

        // Obtener la primera materia
        Locator primerMateria = page.locator(".materia-card").first();

        // Verificar que la primera materia tiene nombre
        Locator nombreMateria = primerMateria.locator(".materia-name");
        assertThat("La primera materia debería tener un nombre",
                nombreMateria.textContent(), not(emptyOrNullString()));

        // Verificar que tiene código
        Locator codigoMateria = primerMateria.locator(".badge-id");
        String codigoTexto = codigoMateria.textContent();
        assertThat("La primera materia debería tener un código",
                codigoTexto, containsString("CODIGO:"));

        // Verificar que tiene carga horaria
        Locator cargaHoraria = primerMateria.locator(".badge-carga-horaria");
        String horasTexto = cargaHoraria.textContent();
        assertThat("La primera materia debería tener carga horaria",
                horasTexto, containsString("h"));

        // Verificar información adicional si está disponible
        System.out.println("Nombre de la materia: " + nombreMateria.textContent());
        System.out.println("Código: " + codigoTexto);
        System.out.println("Carga horaria: " + horasTexto);
    }

    @Test
    void deberiaMostrarDificultadYPromedioSiEstaDisponible() {
        realizarLoginComoTestUser();

        // Ir al progreso académico
        page.click("text=Ver mi Plan de Estudio");
        page.waitForURL("**/materias");

        // Esperar a que aparezca al menos una materia
        page.waitForSelector(".materia-card", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));

        // Buscar materias que tengan promedios
        Locator materiasConPromedios = page.locator(".materia-card:has(.badge-nota)");
            Locator materiaConPromedio = materiasConPromedios.first();

            // Verificar que tiene dificultad
            Locator dificultad = materiaConPromedio.locator(".badge-facil, .badge-moderada, .badge-dificil");

            String dificultadTexto = dificultad.textContent();
            assertThat("La materia debería tener una dificultad válida",
                    dificultadTexto, anyOf(
                            containsStringIgnoringCase("fácil"),
                            containsStringIgnoringCase("moderada"),
                            containsStringIgnoringCase("difícil")
                    ));
            System.out.println("Dificultad: " + dificultadTexto);


            // Verificar que tiene promedio
            Locator promedio = materiaConPromedio.locator(".badge-nota");
            String promedioTexto = promedio.textContent();
            assertThat("La materia debería tener un promedio",
                    promedioTexto, containsString("Promedio:"));
            System.out.println("Promedio: " + promedioTexto);

    }

    @Test
    void deberiaMostrarMateriasOrganizadasPorCuatrimestre() {
        realizarLoginComoTestUser();

        // Ir al progreso académico
        page.click("text=Ver mi Plan de Estudio");
        page.waitForURL("**/materias");

        // Verificar que existen encabezados de cuatrimestre
        page.waitForSelector(".cuatrimestre-header", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));

        Locator encabezadosCuatrimestre = page.locator(".cuatrimestre-header");
        int cantidadCuatrimestres = encabezadosCuatrimestre.count();

        assertThat("Debería haber al menos un cuatrimestre",
                cantidadCuatrimestres, greaterThan(0));

        // Verificar que el primer cuatrimestre tiene el título correcto
        Locator primerCuatrimestre = encabezadosCuatrimestre.first();
        Locator tituloCuatrimestre = primerCuatrimestre.locator(".cuatrimestre-title");
        String tituloTexto = tituloCuatrimestre.textContent();

        assertThat("El primer cuatrimestre debería tener el título correcto",
                tituloTexto, containsString("Cuatrimestre"));

        System.out.println("Cantidad de cuatrimestres encontrados: " + cantidadCuatrimestres);
        System.out.println("Título del primer cuatrimestre: " + tituloTexto);
    }

    @Test
    void deberiaMostrarElContadorDeMateriasEnCadaCuatrimestre() {
        realizarLoginComoTestUser();

        // Ir al progreso académico
        page.click("text=Ver mi Plan de Estudio");
        page.waitForURL("**/materias");

        // Verificar que existen badges de cuatrimestre con contador
        page.waitForSelector(".cuatrimestre-badge", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));

        Locator badgesCuatrimestre = page.locator(".cuatrimestre-badge");
        int cantidadBadges = badgesCuatrimestre.count();

        assertThat("Debería haber al menos un badge de cuatrimestre",
                cantidadBadges, greaterThan(0));

        // Verificar que el primer badge tiene el formato correcto
        Locator primerBadge = badgesCuatrimestre.first();
        String badgeTexto = primerBadge.textContent();

        assertThat("El badge debería mostrar el contador de materias",
                badgeTexto, containsString("materias"));

        System.out.println("Contador del primer cuatrimestre: " + badgeTexto);
    }
}