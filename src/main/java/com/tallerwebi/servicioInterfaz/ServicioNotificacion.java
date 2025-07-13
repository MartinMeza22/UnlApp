package com.tallerwebi.servicioInterfaz;

import com.tallerwebi.dominio.*;
import java.util.List;

public interface ServicioNotificacion {
    void crearNotificacionLike(Publicacion publicacion, Usuario usuarioQueDioLike);
    void crearNotificacionComentario(Comentario comentario);
    void crearNotificacionReporte(Reporte reporte);
    List<Notificacion> obtenerNotificacionesNoLeidas(Usuario usuario);
    void marcarTodasComoLeidas(Usuario usuario);
}