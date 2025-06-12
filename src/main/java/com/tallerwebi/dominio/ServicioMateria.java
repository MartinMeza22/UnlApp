package com.tallerwebi.dominio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("servicioMateria")
@Transactional
public class ServicioMateria {
    @Autowired
    private RepositorioMateria repositorioMateria;

    @Autowired
    public ServicioMateria(RepositorioMateria repositorioMateria) {
        this.repositorioMateria = repositorioMateria;
    }

    // Basic CRUD operations
    public void crearMateria(Materia materia) {
        if (materia.getNombre() == null || materia.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la materia es obligatorio");
        }
        repositorioMateria.guardar(materia);
    }

    public Materia buscarMateriaPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return repositorioMateria.buscarPorId(id);
    }

    public List<Materia> obtenerTodasLasMaterias() {
        return repositorioMateria.buscarTodas();
    }

    public void actualizarMateria(Materia materia) {
        if (materia == null || materia.getId() == null) {
            throw new IllegalArgumentException("La materia y su ID son obligatorios para actualizar");
        }
        repositorioMateria.actualizar(materia);
    }

    public void eliminarMateria(Long id) {
        Materia materia = buscarMateriaPorId(id);
        if (materia != null) {
            repositorioMateria.eliminar(materia);
        }
    }

    public List<Materia> obtenerTodasLasMateriasPorNombre() {
        return repositorioMateria.obtenerTodasLasMateriasPorNombre();
    }

    public List<Materia> obtenerMateriasPorCarrera(String idCarrera){
        return this.repositorioMateria.obtenerMateriasDeUnaCarrera(idCarrera);
    }
}