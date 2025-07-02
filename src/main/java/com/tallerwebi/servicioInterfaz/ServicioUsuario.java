package com.tallerwebi.servicioInterfaz;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;

public interface ServicioUsuario {
    void eliminarUsuario(Long id) throws UsuarioNoEncontrado;

    Usuario obtenerUsuario(Long id) throws UsuarioNoEncontrado;

    void actualizarPerfil(Long id, String nombre, String apellido, String email, String nuevaPassword) throws UsuarioNoEncontrado;
}
