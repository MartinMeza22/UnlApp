package com.tallerwebi.repositorioInterfaz;

import com.tallerwebi.dominio.Carrera;
import com.tallerwebi.dominio.Materia;
import com.tallerwebi.dominio.MateriasConPromedios;

import java.util.List;

public interface RepositorioMateria {

    // Basic CRUD operations
    void guardar(Materia materia);

    Materia buscarPorId(Long id);

    List<Materia> buscarTodas();

    void actualizar(Materia materia);

    void eliminar(Materia materia);

    List<Materia> buscarPorCarrera(Carrera carrera);
  
    List<Materia> obtenerTodasLasMateriasPorNombre();

    List<Materia> obtenerMateriasDeUnaCarrera(String idCarrera);

    public List<Integer> obtenerCantidadDeCuatrimestres();

    Long obtenerCantidadDeMateriasDeLaCarrera();

    List<Materia> buscarMateriasQueTienenComoCorrelativas(Long idMateria);

    List<MateriasConPromedios> obtenerMateriasConPromediosPorCarrera(String idCarrera);
}