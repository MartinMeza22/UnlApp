package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioMateria {

    // Basic CRUD operations
    void guardar(Materia materia);

    Materia buscarPorId(Long id);

    List<Materia> buscarTodas();

    void actualizar(Materia materia);

    void eliminar(Materia materia);
}