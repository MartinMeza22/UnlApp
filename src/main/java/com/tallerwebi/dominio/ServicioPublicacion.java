package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.PublicacionInexistente;

import java.util.List;

public interface ServicioPublicacion {
    List<Publicacion> buscarPublicaciones(Carrera carrera, Long idMateria, String orden);
    void crearPublicacion(String titulo, String descripcion, Usuario usuario, Long idMateria);
    Publicacion obtenerPublicacion(Long idPublicacion) throws PublicacionInexistente;
}