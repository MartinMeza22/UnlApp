package com.tallerwebi.repositorioInterfaz;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.servicios.ResumenUsuario;

import java.util.List;

public interface RepositorioResumenUsuario {
    void guardar(ResumenUsuario resumen);

    List<ResumenUsuario> obtenerPorUsuario(Usuario usuario);
    List<ResumenUsuario> obtenerPorUsuarioId(Long usuarioId);

}

