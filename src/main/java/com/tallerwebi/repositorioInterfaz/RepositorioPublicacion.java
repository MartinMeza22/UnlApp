package com.tallerwebi.repositorioInterfaz;

import com.tallerwebi.dominio.Carrera;
import com.tallerwebi.dominio.Materia;
import com.tallerwebi.dominio.Publicacion;

import java.util.List;

public interface RepositorioPublicacion {
    void guardar(Publicacion publicacion);

    void eliminar(Publicacion publicacion);

    Publicacion buscarPorId(Long id);
    List<Publicacion> buscarPublicaciones(Carrera carrera, Materia materia, String orden);
    List<Object[]> countPublicacionesGroupByCarrera();

}