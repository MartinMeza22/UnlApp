package com.tallerwebi.repositorioInterfaz;

import com.tallerwebi.dominio.CodigoVerificacion;
import com.tallerwebi.dominio.Usuario;

public interface RepositorioCodigoVerificacion {
    void guardar(CodigoVerificacion codigoVerificacion);
    CodigoVerificacion buscarPorUsuarioYCodigo(Usuario usuario, String codigo);
    void eliminar(CodigoVerificacion codigoVerificacion);
    CodigoVerificacion buscarPorUsuario(Usuario usuario);
}