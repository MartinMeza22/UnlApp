package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.PublicacionInexistente;

public interface ServicioComentario {
    void crearComentario(Long idPublicacion, Usuario usuario, String descripcion) throws PublicacionInexistente;
}