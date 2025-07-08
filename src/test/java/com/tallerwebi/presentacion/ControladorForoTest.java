package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Carrera;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.AccesoDenegado;
import com.tallerwebi.dominio.excepcion.PublicacionInexistente;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.dominio.servicios.ServicioMateria;
import com.tallerwebi.servicioInterfaz.ServicioComentario;
import com.tallerwebi.servicioInterfaz.ServicioPublicacion;
import com.tallerwebi.servicioInterfaz.ServicioReporte;
import com.tallerwebi.servicioInterfaz.ServicioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
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
    private ControladorForo controladorForo;
    private HttpSession sessionMock;
    private Usuario usuarioMock;
    private Carrera carreraMock;
    private RedirectAttributes redirectAttributes;
    private final Long ID_USUARIO = 1L;
    private MultipartFile archivoMock;


    @BeforeEach
    void init() throws UsuarioNoEncontrado {
        servicioUsuarioMock = mock(ServicioUsuario.class);
        servicioPublicacionMock = mock(ServicioPublicacion.class);
        servicioComentarioMock = mock(ServicioComentario.class);
        servicioMateriaMock = mock(ServicioMateria.class);
        servletContextMock = mock(ServletContext.class);
        servicioReporteMock = mock(ServicioReporte.class);
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

        controladorForo = new ControladorForo(servicioUsuarioMock, servicioPublicacionMock, servicioComentarioMock, servicioMateriaMock, servicioReporteMock, servletContextMock);
        when(sessionMock.getAttribute("ID")).thenReturn(ID_USUARIO);
        when(usuarioMock.getCarrera()).thenReturn(carreraMock);
        when(servicioUsuarioMock.obtenerUsuario(ID_USUARIO)).thenReturn(usuarioMock);
        when(servletContextMock.getRealPath(anyString())).thenReturn("src/main/webapp/uploads");
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
    public void alMostrarForoCorrectamenteCargaPublicacionesYMaterias() {
        when(servicioPublicacionMock.buscarPublicaciones(any(Carrera.class), any(), anyString())).thenReturn(Collections.emptyList());
        when(servicioMateriaMock.obtenerMateriasPorCarrera(any(Carrera.class))).thenReturn(Collections.emptyList());

        ModelAndView mav = controladorForo.mostrarForo(null, "fecha", sessionMock);

        assertThat(mav.getViewName(), equalTo("foro"));
        assertThat(mav.getModel().containsKey("publicaciones"), is(true));
        assertThat(mav.getModel().containsKey("materias"), is(true));
        assertThat(mav.getModel().get("usuario"), equalTo(usuarioMock));
    }

    @Test
    public void siFallaCreacionDePublicacionRedirigeAForoConMensajeDeError() throws Exception {
        doThrow(new RuntimeException("Error de BD")).when(servicioPublicacionMock).crearPublicacion(anyString(), anyString(), any(Usuario.class), anyLong(), anyString());

        ModelAndView mav = controladorForo.crearPublicacion("Titulo", "Desc", 1L, archivoMock, sessionMock, redirectAttributes);

        assertThat(mav.getViewName(), equalTo("redirect:/foro"));
        assertThat(redirectAttributes.getFlashAttributes().get("error"), notNullValue());
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

        ModelAndView mav = controladorForo.eliminarPublicacion(idPublicacion, sessionMock, redirectAttributes);

        assertThat(mav.getViewName(), equalTo("redirect:/foro"));
        assertThat(redirectAttributes.getFlashAttributes().get("exito"), notNullValue());
    }

    @Test
    public void alIntentarEliminarPublicacionAjenaRedirigeAForoConMensajeDeError() throws PublicacionInexistente, AccesoDenegado {
        Long idPublicacion = 2L;
        String mensajeError = "No tienes permiso";
        doThrow(new AccesoDenegado(mensajeError)).when(servicioPublicacionMock).eliminarPublicacion(idPublicacion, ID_USUARIO);

        ModelAndView mav = controladorForo.eliminarPublicacion(idPublicacion, sessionMock, redirectAttributes);

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
    public void alDarLikeAUnaPublicacionRedirigeAlForo() throws Exception {
        Long idPublicacion = 5L;
        doNothing().when(servicioPublicacionMock).cambiarEstadoLike(idPublicacion, ID_USUARIO);

        ModelAndView mav = controladorForo.darLike(idPublicacion, sessionMock, redirectAttributes);

        assertThat(mav.getViewName(), equalTo("redirect:/foro"));
        verify(servicioPublicacionMock, times(1)).cambiarEstadoLike(idPublicacion, ID_USUARIO);
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
    public void alReportarContenidoCorrectamenteRedirigeAForoConMensajeExito() throws Exception {
        Long idPublicacion = 1L;
        String motivo = "inapropiado";

        ModelAndView mav = controladorForo.reportarContenido(idPublicacion, null, motivo, null, sessionMock, redirectAttributes);

        assertThat(mav.getViewName(), equalTo("redirect:/foro"));
        assertThat(redirectAttributes.getFlashAttributes().get("exito"), equalTo("Reporte enviado correctamente."));
        verify(servicioReporteMock, times(1)).crearReporteParaPublicacion(idPublicacion, ID_USUARIO, motivo, null);
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
}