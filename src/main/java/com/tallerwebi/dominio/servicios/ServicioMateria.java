package com.tallerwebi.dominio.servicios;

import com.tallerwebi.dominio.Carrera;
import com.tallerwebi.dominio.Materia;
import com.tallerwebi.dominio.MateriasConPromedios;
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

    public Materia buscarMateriaPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return repositorioMateria.buscarPorId(id);
    }

    public List<Materia> obtenerTodasLasMaterias(String idCarrera) {
        return repositorioMateria.obtenerMateriasDeUnaCarrera(idCarrera);
    }

    public List<Materia> obtenerMateriasPorCarrera(Carrera carrera) {
        return repositorioMateria.buscarPorCarrera(carrera);
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

    public List<MateriasConPromedios> obtenerMateriasConPromediosPorCarrera(String idCarrera) {
        return repositorioMateria.obtenerMateriasConPromediosPorCarrera(idCarrera);
    }

}