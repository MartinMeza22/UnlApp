package com.tallerwebi.repositorioInterfaz;

import com.tallerwebi.dominio.Carrera;

import java.util.List;

public interface RepositorioCarrera {

    void guardar(Carrera carrera);
    Carrera buscarCarreraPorIds(Long id);
    List<Carrera> obtenerTodasLasCarreras();
    void actualizarCarrera(Carrera carrera);
    void eliminarUnaCarrera(Long id);
    List<Carrera> getCarreras();
}
