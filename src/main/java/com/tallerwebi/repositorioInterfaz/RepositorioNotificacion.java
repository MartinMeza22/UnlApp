package com.tallerwebi.repositorioInterfaz;

import com.tallerwebi.dominio.Notificacion;
import com.tallerwebi.dominio.Usuario;
import java.util.List;

public interface RepositorioNotificacion {
    void guardar(Notificacion notificacion);
    List<Notificacion> obtenerNoLeidasPorUsuario(Usuario usuario);
    void marcarTodasComoLeidas(Usuario usuario);
}