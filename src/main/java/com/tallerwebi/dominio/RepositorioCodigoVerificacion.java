package com.tallerwebi.dominio;

public interface RepositorioCodigoVerificacion {
    void guardar(CodigoVerificacion codigoVerificacion);
    CodigoVerificacion buscarPorUsuarioYCodigo(Usuario usuario, String codigo);
    void eliminar(CodigoVerificacion codigoVerificacion);
    CodigoVerificacion buscarPorUsuario(Usuario usuario);
}