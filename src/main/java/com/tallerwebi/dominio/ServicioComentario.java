package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.AccesoDenegado;
import com.tallerwebi.dominio.excepcion.ComentarioInexistente;
import com.tallerwebi.dominio.excepcion.PublicacionInexistente;

public interface ServicioComentario {
    void crearComentario(Long idPublicacion, Usuario usuario, String descripcion) throws PublicacionInexistente;

    void modificarComentario(Long idComentario, String descripcion, Long idUsuario) throws ComentarioInexistente, AccesoDenegado;

    void eliminarComentario(Long idComentario, Long idUsuarioQueElimina) throws ComentarioInexistente, AccesoDenegado;
}