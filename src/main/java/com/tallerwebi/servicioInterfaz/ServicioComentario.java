package com.tallerwebi.servicioInterfaz;

import com.tallerwebi.dominio.Comentario;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.AccesoDenegado;
import com.tallerwebi.dominio.excepcion.ComentarioInexistente;
import com.tallerwebi.dominio.excepcion.PublicacionInexistente;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;

public interface ServicioComentario {
    Comentario crearComentario(Long idPublicacion, Usuario usuario, String descripcion) throws PublicacionInexistente, AccesoDenegado;

    void modificarComentario(Long idComentario, String descripcion, Long idUsuario) throws ComentarioInexistente, AccesoDenegado, UsuarioNoEncontrado;

    void eliminarComentario(Long idComentario, Long idUsuarioQueElimina) throws ComentarioInexistente, AccesoDenegado, UsuarioNoEncontrado;

    Comentario obtenerComentarioPorId(Long id) throws ComentarioInexistente;
}