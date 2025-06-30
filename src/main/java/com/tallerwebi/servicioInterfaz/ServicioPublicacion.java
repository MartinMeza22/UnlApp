package com.tallerwebi.servicioInterfaz;

import com.tallerwebi.dominio.Carrera;
import com.tallerwebi.dominio.Publicacion;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.AccesoDenegado;
import com.tallerwebi.dominio.excepcion.PublicacionInexistente;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;

import java.util.List;

public interface ServicioPublicacion {
    List<Publicacion> buscarPublicaciones(Carrera carrera, Long idMateria, String orden);
    void crearPublicacion(String titulo, String descripcion, Usuario usuario, Long idMateria);
    Publicacion obtenerPublicacion(Long idPublicacion) throws PublicacionInexistente;

    void cambiarEstadoLike(Long idPublicacion, Long idUsuario) throws PublicacionInexistente, UsuarioNoEncontrado;

    void eliminarPublicacion(Long idPublicacion, Long idUsuarioQueElimina) throws PublicacionInexistente, AccesoDenegado, AccesoDenegado;

    void modificarPublicacion(Long idPublicacion, String titulo, String descripcion, Long idUsuario) throws PublicacionInexistente, AccesoDenegado;
}