package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioCarrera {
    List<Carrera> getCarreras();

    public void crearCarrera(Carrera carrera);
    public Carrera buscarCarreraPorId(Long id);
    public List<Carrera> obtenerTodasLasCarreras();
    public void actualizarCarrera(Carrera carrera);
    public void eliminarUnaCarrera(Long id);

}
