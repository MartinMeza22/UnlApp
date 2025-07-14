package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.AccesoDenegado;
import com.tallerwebi.dominio.excepcion.PublicacionInexistente;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.dominio.servicios.ServicioEmail;
import com.tallerwebi.dominio.servicios.ServicioMateria;
import com.tallerwebi.servicioInterfaz.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ControladorForoTest {

    private ServicioUsuario servicioUsuarioMock;
    private ServicioPublicacion servicioPublicacionMock;
    private ServicioComentario servicioComentarioMock;
    private ServicioMateria servicioMateriaMock;
    private ServletContext servletContextMock;
    private ServicioReporte servicioReporteMock;
    private ServicioEmail servicioEmailMock;
    private ServicioNotificacion servicioNotificacionMock;

    private ControladorForo controladorForo;
    private HttpSession sessionMock;
    private Usuario usuarioMock;
    private Carrera carreraMock;
    private RedirectAttributes redirectAttributes;
    private final Long ID_USUARIO = 1L;
    private MultipartFile archivoMock;
    private MultipartFile archivoNoPermitidoMock;


    @BeforeEach
    void init() throws UsuarioNoEncontrado {
        servicioUsuarioMock = mock(ServicioUsuario.class);
        servicioPublicacionMock = mock(ServicioPublicacion.class);
        servicioComentarioMock = mock(ServicioComentario.class);
        servicioMateriaMock = mock(ServicioMateria.class);
        servletContextMock = mock(ServletContext.class);
        servicioReporteMock = mock(ServicioReporte.class);
        servicioEmailMock = mock(ServicioEmail.class);
        servicioNotificacionMock = mock(ServicioNotificacion.class);

        sessionMock = mock(HttpSession.class);
        usuarioMock = mock(Usuario.class);
        carreraMock = mock(Carrera.class);
        redirectAttributes = new RedirectAttributesModelMap();

        archivoMock = new MockMultipartFile(
                "archivo",
                "test-file.png",
                "image/png",
                "Este es el contenido del archivo".getBytes()
        );

        archivoNoPermitidoMock = new MockMultipartFile(
                "archivo",
                "test-file.exe",
                "application/octet-stream",
                "Contenido malicioso".getBytes()
        );


        controladorForo = new ControladorForo(
                servicioUsuarioMock, servicioPublicacionMock, servicioComentarioMock,
                servicioMateriaMock, servicioReporteMock, servletContextMock,
                servicioEmailMock, servicioNotificacionMock
        );

        when(sessionMock.getAttribute("ID")).thenReturn(ID_USUARIO);
        when(usuarioMock.getCarrera()).thenReturn(carreraMock);
        when(usuarioMock.getId()).thenReturn(ID_USUARIO);
        when(servicioUsuarioMock.obtenerUsuario(ID_USUARIO)).thenReturn(usuarioMock);
        when(servletContextMock.getRealPath(anyString())).thenReturn("src/main/webapp/uploads");
        // Simular la creación del directorio para evitar NullPointerException
        File uploadDir = new File("src/main/webapp/uploads");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    @Test
    public void siUsuarioNoEstaLogueadoAlMostrarForoRedirigeALogin() {
        when(sessionMock.getAttribute("ID")).thenReturn(null);
        ModelAndView mav = controladorForo.mostrarForo(null, "fecha", sessionMock);
        assertThat(mav.getViewName(), equalTo("redirect:/login"));
    }

    @Test
    public void siUsuarioNoTieneCarreraAsignadaMuestraMensajeDeError() throws UsuarioNoEncontrado {
        when(usuarioMock.getCarrera()).thenReturn(null);
        when(servicioUsuarioMock.obtenerUsuario(ID_USUARIO)).thenReturn(usuarioMock);

        ModelAndView mav = controladorForo.mostrarForo(null, "fecha", sessionMock);

        assertThat(mav.getViewName(), equalTo("foro"));
        assertThat(mav.getModel().get("error").toString(), containsString("No tienes una carrera asignada"));
    }

    @Test
    public void alMostrarForoCorrectamenteCargaPublicacionesYMateriasYNotificaciones() {
        when(servicioPublicacionMock.buscarPublicaciones(any(Carrera.class), any(), anyString())).thenReturn(Collections.emptyList());
        when(servicioMateriaMock.obtenerMateriasPorCarrera(any(Carrera.class))).thenReturn(Collections.emptyList());
        when(servicioNotificacionMock.obtenerNotificacionesNoLeidas(any(Usuario.class))).thenReturn(Collections.emptyList());

        ModelAndView mav = controladorForo.mostrarForo(null, "fecha", sessionMock);

        assertThat(mav.getViewName(), equalTo("foro"));
        assertThat(mav.getModel().containsKey("publicaciones"), is(true));
        assertThat(mav.getModel().containsKey("materias"), is(true));
        assertThat(mav.getModel().containsKey("notificaciones"), is(true));
        assertThat(mav.getModel().get("usuario"), equalTo(usuarioMock));
    }

    @Test
    public void alCrearComentarioCorrectamenteSeCreaNotificacion() throws Exception {
        Long idPublicacion = 1L;
        String descripcion = "Test comentario";
        Comentario comentarioMock = mock(Comentario.class);
        when(servicioComentarioMock.crearComentario(idPublicacion, usuarioMock, descripcion)).thenReturn(comentarioMock);

        ModelAndView mav = controladorForo.crearComentario(idPublicacion, descripcion, sessionMock, redirectAttributes);

        assertThat(mav.getViewName(), equalTo("redirect:/foro"));
        assertThat(redirectAttributes.getFlashAttributes().get("exito"), notNullValue());
        verify(servicioNotificacionMock, times(1)).crearNotificacionComentario(comentarioMock);
    }

    @Test
    public void siFallaCreacionDePublicacionRedirigeAForoConMensajeDeError() throws Exception {
        doThrow(new RuntimeException("Error de BD")).when(servicioPublicacionMock).crearPublicacion(anyString(), anyString(), any(Usuario.class), anyLong(), anyString());
        ModelAndView mav = controladorForo.crearPublicacion("Titulo", "Desc", 1L, archivoMock, sessionMock, redirectAttributes);
        assertThat(mav.getViewName(), equalTo("redirect:/foro"));
        assertThat(redirectAttributes.getFlashAttributes().get("error"), notNullValue());
    }

    @Test
    public void alCrearPublicacionConExtensionNoPermitidaDeberiaRetornarError() throws Exception {
        ModelAndView mav = controladorForo.crearPublicacion("Titulo", "Desc", 1L, archivoNoPermitidoMock, sessionMock, redirectAttributes);

        assertThat(mav.getViewName(), equalTo("redirect:/foro"));
        assertThat(redirectAttributes.getFlashAttributes().get("error"), equalTo("Tipo de archivo no permitido."));
        verify(servicioPublicacionMock, never()).crearPublicacion(any(), any(), any(), any(), any());
    }


    @Test
    public void alCrearPublicacionCorrectamenteRedirigeAForoConMensajeDeExito() throws Exception {
        ModelAndView mav = controladorForo.crearPublicacion("Titulo", "Desc", 1L, archivoMock, sessionMock, redirectAttributes);
        assertThat(mav.getViewName(), equalTo("redirect:/foro"));
        assertThat(redirectAttributes.getFlashAttributes().get("exito"), notNullValue());
        verify(servicioPublicacionMock, times(1)).crearPublicacion(eq("Titulo"), eq("Desc"), eq(usuarioMock), eq(1L), eq(archivoMock.getOriginalFilename()));
    }

    @Test
    public void alEliminarPublicacionPropiaRedirigeAForoConMensajeDeExito() throws PublicacionInexistente, AccesoDenegado {
        Long idPublicacion = 1L;
        doNothing().when(servicioPublicacionMock).eliminarPublicacion(idPublicacion, ID_USUARIO);


        ModelAndView mav = controladorForo.eliminarPublicacion(idPublicacion, null, sessionMock, redirectAttributes);

        assertThat(mav.getViewName(), equalTo("redirect:/foro"));
        assertThat(redirectAttributes.getFlashAttributes().get("exito"), notNullValue());
    }

    @Test
    public void unAdminPuedeEliminarUnaPublicacionReportadaYNotificaAlAutor() throws Exception {
        Long idPublicacion = 2L;
        Long idReporte = 3L;
        String emailAutor = "autor@test.com";
        String nombreAutor = "Autor Original";

        when(sessionMock.getAttribute("ROL")).thenReturn("ADMIN");

        Usuario autorMock = mock(Usuario.class);
        when(autorMock.getEmail()).thenReturn(emailAutor);
        when(autorMock.getNombre()).thenReturn(nombreAutor);

        Publicacion publicacionMock = mock(Publicacion.class);
        when(publicacionMock.getUsuario()).thenReturn(autorMock);
        when(publicacionMock.getTitulo()).thenReturn("Publicación a eliminar");

        Reporte reporteMock = mock(Reporte.class);
        when(reporteMock.getMotivo()).thenReturn("Contenido inapropiado");

        when(servicioPublicacionMock.obtenerPublicacion(idPublicacion)).thenReturn(publicacionMock);
        when(servicioReporteMock.obtenerReportePorId(idReporte)).thenReturn(reporteMock);

        ModelAndView mav = controladorForo.eliminarPublicacion(idPublicacion, idReporte, sessionMock, redirectAttributes);

        assertThat(mav.getViewName(), equalTo("redirect:/admin/panel"));
        assertThat(redirectAttributes.getFlashAttributes().get("exito"), notNullValue());
        verify(servicioPublicacionMock, times(1)).eliminarPublicacion(idPublicacion, ID_USUARIO);
        verify(servicioReporteMock, times(1)).eliminarReportesDePublicacion(idPublicacion);
        verify(servicioEmailMock, times(1)).enviarEmailAUsuario(eq(emailAutor), anyString(), anyString());
    }

    @Test
    public void alIntentarEliminarPublicacionAjenaRedirigeAForoConMensajeDeError() throws PublicacionInexistente, AccesoDenegado {
        Long idPublicacion = 2L;
        String mensajeError = "No tienes permiso";
        doThrow(new AccesoDenegado(mensajeError)).when(servicioPublicacionMock).eliminarPublicacion(idPublicacion, ID_USUARIO);


        ModelAndView mav = controladorForo.eliminarPublicacion(idPublicacion, null, sessionMock, redirectAttributes);

        assertThat(mav.getViewName(), equalTo("redirect:/foro"));
        assertThat(redirectAttributes.getFlashAttributes().get("error").toString(), containsString(mensajeError));
    }

    @Test
    public void alEditarComentarioCorrectamenteRedirigeAForoConMensajeExito() throws Exception {
        Long idComentario = 1L;
        String nuevaDesc = "Comentario editado";
        doNothing().when(servicioComentarioMock).modificarComentario(idComentario, nuevaDesc, ID_USUARIO);
        ModelAndView mav = controladorForo.editarComentario(idComentario, nuevaDesc, sessionMock, redirectAttributes);
        assertThat(mav.getViewName(), equalTo("redirect:/foro"));
        assertThat(redirectAttributes.getFlashAttributes().get("exito"), notNullValue());
        verify(servicioComentarioMock, times(1)).modificarComentario(idComentario, nuevaDesc, ID_USUARIO);
    }

    @Test
    public void unAdminPuedeEliminarUnComentarioReportadoYNotificaAlAutor() throws Exception {
        Long idComentario = 2L;
        Long idReporte = 3L;
        String emailAutor = "autor@test.com";
        String nombreAutor = "Autor Original";

        when(sessionMock.getAttribute("ROL")).thenReturn("ADMIN");

        Usuario autorMock = mock(Usuario.class);
        when(autorMock.getEmail()).thenReturn(emailAutor);
        when(autorMock.getNombre()).thenReturn(nombreAutor);

        Comentario comentarioMock = mock(Comentario.class);
        when(comentarioMock.getUsuario()).thenReturn(autorMock);
        when(comentarioMock.getDescripcion()).thenReturn("Comentario a eliminar");

        Reporte reporteMock = mock(Reporte.class);
        when(reporteMock.getMotivo()).thenReturn("Contenido ofensivo");

        when(servicioComentarioMock.obtenerComentarioPorId(idComentario)).thenReturn(comentarioMock);
        when(servicioReporteMock.obtenerReportePorId(idReporte)).thenReturn(reporteMock);

        ModelAndView mav = controladorForo.eliminarComentario(idComentario, idReporte, sessionMock, redirectAttributes);

        assertThat(mav.getViewName(), equalTo("redirect:/admin/panel"));
        assertThat(redirectAttributes.getFlashAttributes().get("exito"), notNullValue());
        verify(servicioComentarioMock, times(1)).eliminarComentario(idComentario, ID_USUARIO);
        verify(servicioReporteMock, times(1)).eliminarReportesDeComentario(idComentario);
        verify(servicioEmailMock, times(1)).enviarEmailAUsuario(eq(emailAutor), anyString(), anyString());
    }


    @Test
    public void alDarLikeAUnaPublicacionRedirigeAlForoYSeCreaNotificacion() throws Exception {
        Long idPublicacion = 5L;
        Publicacion publicacionMock = mock(Publicacion.class);

        when(servicioPublicacionMock.obtenerPublicacion(idPublicacion)).thenReturn(publicacionMock);
        when(publicacionMock.usuarioDioLike(usuarioMock)).thenReturn(true);

        ModelAndView mav = controladorForo.darLike(idPublicacion, sessionMock, redirectAttributes);

        assertThat(mav.getViewName(), equalTo("redirect:/foro"));
        verify(servicioPublicacionMock, times(1)).cambiarEstadoLike(idPublicacion, ID_USUARIO);
        verify(servicioNotificacionMock, times(1)).crearNotificacionLike(publicacionMock, usuarioMock);
        assertThat(redirectAttributes.getFlashAttributes().isEmpty(), is(true));
    }

    @Test
    public void alEditarPublicacionPropiaRedirigeAForoConMensajeDeExito() throws Exception {
        Long idPublicacion = 1L;
        String nuevoTitulo = "Titulo Editado";
        String nuevaDesc = "Descripcion Editada";

        doNothing().when(servicioPublicacionMock).modificarPublicacion(idPublicacion, nuevoTitulo, nuevaDesc, ID_USUARIO);
        ModelAndView mav = controladorForo.editarPublicacion(idPublicacion, nuevoTitulo, nuevaDesc, sessionMock, redirectAttributes);
        assertThat(mav.getViewName(), equalTo("redirect:/foro"));
        assertThat(redirectAttributes.getFlashAttributes().get("exito"), notNullValue());
        verify(servicioPublicacionMock, times(1)).modificarPublicacion(idPublicacion, nuevoTitulo, nuevaDesc, ID_USUARIO);
    }

    @Test
    public void siFallaLaEdicionDePublicacionRedirigeAForoConMensajeDeError() throws Exception {
        Long idPublicacion = 1L;
        String nuevoTitulo = "Titulo Editado";
        String nuevaDesc = "Descripcion Editada";
        String mensajeError = "Error al modificar";

        doThrow(new RuntimeException(mensajeError)).when(servicioPublicacionMock).modificarPublicacion(idPublicacion, nuevoTitulo, nuevaDesc, ID_USUARIO);
        ModelAndView mav = controladorForo.editarPublicacion(idPublicacion, nuevoTitulo, nuevaDesc, sessionMock, redirectAttributes);
        assertThat(mav.getViewName(), equalTo("redirect:/foro"));
        assertThat(redirectAttributes.getFlashAttributes().get("error").toString(), containsString(mensajeError));
    }

    @Test
    public void alAccederANuevaPublicacionSinEstarLogueadoRedirigeALogin() {
        when(sessionMock.getAttribute("ID")).thenReturn(null);
        ModelAndView mav = controladorForo.mostrarFormularioNuevaPublicacion(sessionMock);
        assertThat(mav.getViewName(), equalTo("redirect:/login"));
    }

    @Test
    public void siServicioUsuarioLanzaExcepcionAlBuscarUsuarioRedirigeALogin() throws UsuarioNoEncontrado {
        when(servicioUsuarioMock.obtenerUsuario(ID_USUARIO)).thenThrow(new UsuarioNoEncontrado());
        ModelAndView mav = controladorForo.mostrarForo(null, "fecha", sessionMock);
        assertThat(mav.getViewName(), equalTo("redirect:/login"));
    }

    @Test
    public void alReportarContenidoCorrectamenteRedirigeAForoConMensajeExitoYNotifica() throws Exception {
        Long idPublicacion = 1L;
        String motivo = "inapropiado";
        Reporte reporteMock = mock(Reporte.class);
        when(servicioReporteMock.crearReporteParaPublicacion(idPublicacion, ID_USUARIO, motivo, null)).thenReturn(reporteMock);

        ModelAndView mav = controladorForo.reportarContenido(idPublicacion, null, motivo, null, sessionMock, redirectAttributes);

        assertThat(mav.getViewName(), equalTo("redirect:/foro"));
        assertThat(redirectAttributes.getFlashAttributes().get("exito"), equalTo("Reporte enviado correctamente."));
        verify(servicioReporteMock, times(1)).crearReporteParaPublicacion(idPublicacion, ID_USUARIO, motivo, null);
        verify(servicioNotificacionMock, times(1)).crearNotificacionReporte(reporteMock);
    }

    @Test
    public void siUsuarioNoLogueadoIntentaReportarRedirigeALogin() {
        when(sessionMock.getAttribute("ID")).thenReturn(null);
        ModelAndView mav = controladorForo.reportarContenido(1L, null, "ofensivo", null, sessionMock, redirectAttributes);
        assertThat(mav.getViewName(), equalTo("redirect:/login"));
    }

    @Test
    public void siFallaAlReportarContenidoRedirigeAForoConMensajeDeError() throws Exception {
        Long idPublicacion = 1L;
        String motivo = "inapropiado";
        String mensajeError = "Error al reportar";
        doThrow(new RuntimeException(mensajeError)).when(servicioReporteMock).crearReporteParaPublicacion(anyLong(), anyLong(), anyString(), any());
        ModelAndView mav = controladorForo.reportarContenido(idPublicacion, null, motivo, null, sessionMock, redirectAttributes);
        assertThat(mav.getViewName(), equalTo("redirect:/foro"));
        assertThat(redirectAttributes.getFlashAttributes().get("error").toString(), containsString(mensajeError));
    }

    @Test
    public void alMarcarNotificacionesComoLeidasDevuelveStatusOK() {
        ResponseEntity<Void> response = controladorForo.marcarNotificacionesComoLeidas(sessionMock);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        verify(servicioNotificacionMock, times(1)).marcarTodasComoLeidas(usuarioMock);
    }

    @Test
    public void siUsuarioNoLogueadoIntentaMarcarNotificacionesComoLeidasDevuelveStatusUnauthorized() {
        when(sessionMock.getAttribute("ID")).thenReturn(null);
        ResponseEntity<Void> response = controladorForo.marcarNotificacionesComoLeidas(sessionMock);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    public void siUsuarioNoEsEncontradoAlMarcarNotificacionesDevuelveStatusNotFound() throws UsuarioNoEncontrado {
        when(servicioUsuarioMock.obtenerUsuario(ID_USUARIO)).thenThrow(new UsuarioNoEncontrado());
        ResponseEntity<Void> response = controladorForo.marcarNotificacionesComoLeidas(sessionMock);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }
}