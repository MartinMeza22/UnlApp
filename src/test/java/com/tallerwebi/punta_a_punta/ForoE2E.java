package com.tallerwebi.punta_a_punta;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.SelectOption;
import com.tallerwebi.punta_a_punta.vistas.VistaLogin;
import org.junit.jupiter.api.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ForoE2E {

    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;
    VistaLogin vistaLogin;

    private static String tituloPublicacionUnico;
    private static final String TEXTO_PUBLICACION = "publicación de prueba para foro.";
    private static final String TEXTO_COMENTARIO = "comentario de prueba para foro.";
    private static final String URL_BASE = "http://localhost:8080/tallerwebi-base-1.0-SNAPSHOT";

    @BeforeAll
    static void abrirNavegador() {
        ReiniciarDB.limpiarBaseDeDatos();

        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(700)
        );
    }

    @AfterAll
    static void cerrarNavegador() {
        playwright.close();
    }

    @BeforeEach
    void crearContextoYPagina() {
        context = browser.newContext();
        page = context.newPage();
        vistaLogin = new VistaLogin(page);
    }

    @AfterEach
    void cerrarContexto() {
        context.close();
    }

    private void realizarLoginComoAlumno() {
        vistaLogin.escribirEMAIL("vitinha@alumno.unlam.edu.ar");
        vistaLogin.escribirClave("12345678");
        vistaLogin.darClickEnIniciarSesion();
        page.waitForSelector("text=Foro de materias");
    }

    private void realizarLoginComoAdmin() {
        vistaLogin.escribirEMAIL("admin@unlapp.com");
        vistaLogin.escribirClave("123");
        vistaLogin.darClickEnIniciarSesion();
        page.waitForSelector("text=Ver Reportes");
    }

    @Test
    @Order(1)
    void deberiaCrearUnaPublicacion() {
        realizarLoginComoAlumno();
        tituloPublicacionUnico = "Publicación titulo";

        page.locator("a:has-text('Foro de materias')").click();
        page.waitForURL("**/foro");

        page.locator("button:has-text('Crear Publicación')").click();

        Locator modalCrear = page.locator("#modalCrearPublicacion");
        modalCrear.locator("input[name='titulo']").fill(tituloPublicacionUnico);
        modalCrear.locator("textarea[name='descripcion']").fill(TEXTO_PUBLICACION);
        modalCrear.locator("select[name='idMateria']").selectOption(new SelectOption().setIndex(1));
        modalCrear.locator("button:has-text('Publicar')").click();

        Locator publicacionCreada = page.locator(".publicacion-container:has-text('" + tituloPublicacionUnico + "')");
        assertThat(publicacionCreada).isVisible();
        assertThat(publicacionCreada.locator("p")).hasText(TEXTO_PUBLICACION);
    }

    @Test
    @Order(2)
    void deberiaHacerUnComentarioYDarLike() {
        realizarLoginComoAlumno();

        page.locator("a:has-text('Foro de materias')").click();
        page.waitForURL("**/foro");

        Locator publicacion = page.locator(".publicacion-container:has-text('" + tituloPublicacionUnico + "')");
        assertThat(publicacion).isVisible();

        publicacion.locator("input[name='descripcion']").fill(TEXTO_COMENTARIO);
        publicacion.locator("button:has-text('Comentar')").click();

        Locator likeCount = publicacion.locator("span:has-text('Likes')");
        String likesAntesTexto = likeCount.textContent().trim();
        int likesAntesNum = Integer.parseInt(likesAntesTexto.split(" ")[0]);

        publicacion.locator("button:has-text('Me gusta')").click();
        page.waitForLoadState();

        String likesDespuesTexto = likeCount.textContent().trim();
        int likesDespuesNum = Integer.parseInt(likesDespuesTexto.split(" ")[0]);

        assertThat(likesDespuesNum, equalTo(likesAntesNum + 1));
        assertThat(publicacion.locator(".comentario:has-text('" + TEXTO_COMENTARIO + "')")).isVisible();
    }

    @Test
    @Order(3)
    void deberiaReportarUnaPublicacionYVerlaEnPanelAdmin() {
        realizarLoginComoAlumno();

        page.locator("a:has-text('Foro de materias')").click();
        page.waitForURL("**/foro");

        assertThat("El título de la publicación no debe ser nulo. Asegúrate que el test de crear corra primero.",
                tituloPublicacionUnico, notNullValue());

        Locator publicacionAReportar = page.locator(".publicacion-container:has-text('" + tituloPublicacionUnico + "')");
        assertThat(publicacionAReportar).isVisible();

        publicacionAReportar.locator("button:has-text('Reportar')").click();

        page.locator("#reportarModal select[name='motivo']").selectOption("inapropiado");
        page.locator("#reportarModal button:has-text('Enviar Reporte')").click();

        Locator alertaExito = page.locator(".alert-success");
        assertThat(alertaExito).isVisible();
        assertThat(alertaExito).containsText("Reporte enviado correctamente.");

        vistaLogin.cerrarSesion();
        page.waitForURL("**/login");

        realizarLoginComoAdmin();
        page.locator("a:has-text('Ver Reportes')").click();
        page.waitForURL("**/admin/panel");

        Locator filaReporte = page.locator("tr:has-text('" + tituloPublicacionUnico + "')");
        assertThat(filaReporte).isVisible();
        assertThat(filaReporte).containsText("inapropiado");

    }
}