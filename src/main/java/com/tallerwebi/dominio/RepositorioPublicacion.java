package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioPublicacion {
    void guardar(Publicacion publicacion);
    Publicacion buscarPorId(Long id);
    List<Publicacion> buscarPublicaciones(Carrera carrera, Materia materia, String orden);
}