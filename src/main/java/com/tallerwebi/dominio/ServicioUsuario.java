package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;

public interface ServicioUsuario {
    Usuario obtenerUsuario(Long id) throws UsuarioNoEncontrado;
}
