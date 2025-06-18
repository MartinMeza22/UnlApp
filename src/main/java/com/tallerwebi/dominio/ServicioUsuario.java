package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;

public interface ServicioUsuario {
    void eliminarUsuario(Long id) throws UsuarioNoEncontrado;

    Usuario obtenerUsuario(Long id) throws UsuarioNoEncontrado;
}
