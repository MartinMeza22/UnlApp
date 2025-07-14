package com.tallerwebi.presentacion;


import com.tallerwebi.dominio.*;
import com.tallerwebi.repositorioInterfaz.RepositorioNotificacion;
import com.tallerwebi.serviciosImplementacion.ServicioNotificacionImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.*;

public class ServicioNotificacionTest {

    private RepositorioNotificacion repositorioNotificacionMock;
    private ServicioNotificacionImpl servicioNotificacion;
    private Usuario usuarioDestinatarioMock;
    private Usuario usuarioAccionMock;
    private Publicacion publicacionMock;
    private Comentario comentarioMock;

    @BeforeEach
    void init() {
        repositorioNotificacionMock = mock(RepositorioNotificacion.class);
        servicioNotificacion = new ServicioNotificacionImpl(repositorioNotificacionMock);

        usuarioDestinatarioMock = mock(Usuario.class);
        usuarioAccionMock = mock(Usuario.class);
        publicacionMock = mock(Publicacion.class);
        comentarioMock = mock(Comentario.class);

        when(usuarioAccionMock.getNombre()).thenReturn("UsuarioTest");
        when(publicacionMock.getTitulo()).thenReturn("Este es un titulo de prueba muy largo");
        when(publicacionMock.getUsuario()).thenReturn(usuarioDestinatarioMock);
        when(comentarioMock.getPublicacion()).thenReturn(publicacionMock);
        when(comentarioMock.getUsuario()).thenReturn(usuarioAccionMock);
    }

    @Test
    public void alCrearNotificacionDeLikeSeGuardaConElMensajeCorrecto() {
        servicioNotificacion.crearNotificacionLike(publicacionMock, usuarioAccionMock);

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(repositorioNotificacionMock).guardar(captor.capture());

        Notificacion notificacionGuardada = captor.getValue();
        assertThat(notificacionGuardada.getUsuarioDestinatario(), equalTo(usuarioDestinatarioMock));
        assertThat(notificacionGuardada.getMensaje(), equalTo("UsuarioTest indicó que le gusta tu publicación: \"Este es un titulo de prue...\""));
    }

    @Test
    public void noSeCreaNotificacionDeLikeSiElUsuarioSeDaLikeASiMismo() {
        when(publicacionMock.getUsuario()).thenReturn(usuarioAccionMock);
        servicioNotificacion.crearNotificacionLike(publicacionMock, usuarioAccionMock);
        verify(repositorioNotificacionMock, never()).guardar(any(Notificacion.class));
    }

    @Test
    public void alCrearNotificacionDeComentarioSeGuardaConElMensajeCorrecto() {
        servicioNotificacion.crearNotificacionComentario(comentarioMock);

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(repositorioNotificacionMock).guardar(captor.capture());

        Notificacion notificacionGuardada = captor.getValue();
        assertThat(notificacionGuardada.getUsuarioDestinatario(), equalTo(usuarioDestinatarioMock));
        assertThat(notificacionGuardada.getMensaje(), equalTo("UsuarioTest comentó en tu publicación: \"Este es un titulo de prue...\""));
    }

    @Test
    public void noSeCreaNotificacionDeComentarioSiElUsuarioComentaSuPropiaPublicacion() {
        when(comentarioMock.getUsuario()).thenReturn(usuarioDestinatarioMock);
        servicioNotificacion.crearNotificacionComentario(comentarioMock);
        verify(repositorioNotificacionMock, never()).guardar(any(Notificacion.class));
    }

    @Test
    public void alCrearNotificacionDeReporteDePublicacionSeGuardaConMensajeCorrecto() {
        Reporte reporteMock = mock(Reporte.class);
        when(reporteMock.getPublicacion()).thenReturn(publicacionMock);
        when(reporteMock.getComentario()).thenReturn(null);

        servicioNotificacion.crearNotificacionReporte(reporteMock);

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(repositorioNotificacionMock).guardar(captor.capture());
        Notificacion notificacionGuardada = captor.getValue();
        assertThat(notificacionGuardada.getUsuarioDestinatario(), equalTo(usuarioDestinatarioMock));
        assertThat(notificacionGuardada.getMensaje(), startsWith("Se ha recibido un reporte sobre tu publicación"));
    }

    @Test
    public void alCrearNotificacionDeReporteDeComentarioSeGuardaConMensajeCorrecto() {
        Reporte reporteMock = mock(Reporte.class);
        when(reporteMock.getPublicacion()).thenReturn(null);
        when(reporteMock.getComentario()).thenReturn(comentarioMock);
        when(comentarioMock.getUsuario()).thenReturn(usuarioDestinatarioMock);


        servicioNotificacion.crearNotificacionReporte(reporteMock);

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(repositorioNotificacionMock).guardar(captor.capture());
        Notificacion notificacionGuardada = captor.getValue();
        assertThat(notificacionGuardada.getUsuarioDestinatario(), equalTo(usuarioDestinatarioMock));
        assertThat(notificacionGuardada.getMensaje(), equalTo("Se ha recibido un reporte sobre tu comentario."));
    }

    @Test
    public void obtenerNotificacionesNoLeidasLlamaAlRepositorioCorrectamente() {
        servicioNotificacion.obtenerNotificacionesNoLeidas(usuarioDestinatarioMock);
        verify(repositorioNotificacionMock, times(1)).obtenerNoLeidasPorUsuario(usuarioDestinatarioMock);
    }

    @Test
    public void marcarTodasComoLeidasLlamaAlRepositorioCorrectamente() {
        servicioNotificacion.marcarTodasComoLeidas(usuarioDestinatarioMock);
        verify(repositorioNotificacionMock, times(1)).marcarTodasComoLeidas(usuarioDestinatarioMock);
    }
}