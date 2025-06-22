package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioCarrera {

    void guardar(Carrera carrera);
    Carrera buscarCarreraPorIds(Long id);
    List<Carrera> obtenerTodasLasCarreras();
    void actualizarCarrera(Carrera carrera);
    void eliminarUnaCarrera(Long id);
    List<Carrera> getCarreras();
}
