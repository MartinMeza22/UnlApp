package com.tallerwebi.serviciosImplementacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.repositorioInterfaz.RepositorioNotificacion;
import com.tallerwebi.servicioInterfaz.ServicioNotificacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;

@Service("servicioNotificacion")
@Transactional
public class ServicioNotificacionImpl implements ServicioNotificacion {

    private final RepositorioNotificacion repositorioNotificacion;

    @Autowired
    public ServicioNotificacionImpl(RepositorioNotificacion repositorioNotificacion) {
        this.repositorioNotificacion = repositorioNotificacion;
    }

    @Override
    public void crearNotificacionLike(Publicacion publicacion, Usuario usuarioQueDioLike) {
        Usuario destinatario = publicacion.getUsuario();
        if (destinatario.equals(usuarioQueDioLike)) return;

        String titulo = publicacion.getTitulo().substring(0, Math.min(publicacion.getTitulo().length(), 25));
        String mensaje = usuarioQueDioLike.getNombre() + " indicó que le gusta tu publicación: \"" + titulo + "...\"";
        Notificacion notificacion = new Notificacion(destinatario, mensaje);
        repositorioNotificacion.guardar(notificacion);
    }

    @Override
    public void crearNotificacionComentario(Comentario comentario) {
        Usuario destinatario = comentario.getPublicacion().getUsuario();
        Usuario autorComentario = comentario.getUsuario();
        if (destinatario.equals(autorComentario)) return;

        String titulo = comentario.getPublicacion().getTitulo().substring(0, Math.min(comentario.getPublicacion().getTitulo().length(), 25));
        String mensaje = autorComentario.getNombre() + " comentó en tu publicación: \"" + titulo + "...\"";
        Notificacion notificacion = new Notificacion(destinatario, mensaje);
        repositorioNotificacion.guardar(notificacion);
    }

    @Override
    public void crearNotificacionReporte(Reporte reporte) {
        Usuario destinatario;
        String contenido;
        if (reporte.getPublicacion() != null) {
            destinatario = reporte.getPublicacion().getUsuario();
            String titulo = reporte.getPublicacion().getTitulo().substring(0, Math.min(reporte.getPublicacion().getTitulo().length(), 25));
            contenido = "tu publicación \"" + titulo + "...\"";
        } else if (reporte.getComentario() != null) {
            destinatario = reporte.getComentario().getUsuario();
            contenido = "tu comentario";
        } else {
            return;
        }

        String mensaje = "Se ha recibido un reporte sobre " + contenido + ".";
        Notificacion notificacion = new Notificacion(destinatario, mensaje);
        repositorioNotificacion.guardar(notificacion);
    }

    @Override
    public List<Notificacion> obtenerNotificacionesNoLeidas(Usuario usuario) {
        return repositorioNotificacion.obtenerNoLeidasPorUsuario(usuario);
    }
    @Override
    public void marcarTodasComoLeidas(Usuario usuario) {
        repositorioNotificacion.marcarTodasComoLeidas(usuario);
    }

}