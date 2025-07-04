package com.tallerwebi.dominio.servicios;

import com.tallerwebi.dominio.Carrera;
import com.tallerwebi.dominio.Materia;
import com.tallerwebi.repositorioInterfaz.RepositorioMateria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("servicioMateria")
@Transactional
public class ServicioMateria {
    @Autowired
    private RepositorioMateria repositorioMateria;

    public ServicioMateria() {
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

    public List<Materia> obtenerTodasLasMaterias(String idCarrera) {
        return repositorioMateria.obtenerMateriasDeUnaCarrera(idCarrera);
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

    public List<Materia> obtenerMateriasPorCarrera(Carrera carrera) {
        return repositorioMateria.buscarPorCarrera(carrera);
    }

    public List<Materia> obtenerTodasLasMateriasPorNombre() {
        return repositorioMateria.obtenerTodasLasMateriasPorNombre();
    }

    public List<Materia> obtenerMateriasPorCarrera(String idCarrera) {
        return this.repositorioMateria.obtenerMateriasDeUnaCarrera(idCarrera);
    }

    public List<Integer> obtenerCantidadDeCuatrimestres(){
        return repositorioMateria.obtenerCantidadDeCuatrimestres();
    }

    public Long obtenerCantidadDeMateriasDeLaCarrera(){
        return repositorioMateria.obtenerCantidadDeMateriasDeLaCarrera();
    }
}