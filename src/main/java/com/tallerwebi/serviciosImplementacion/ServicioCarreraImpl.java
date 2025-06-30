package com.tallerwebi.serviciosImplementacion;

import com.tallerwebi.dominio.Carrera;
import com.tallerwebi.repositorioInterfaz.RepositorioCarrera;
import com.tallerwebi.servicioInterfaz.ServicioCarrera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ServicioCarreraImpl implements ServicioCarrera {

    private RepositorioCarrera repositorioCarrera;

    public ServicioCarreraImpl() {}

    @Autowired
    public ServicioCarreraImpl(RepositorioCarrera repositorioCarrera) {
        this.repositorioCarrera = repositorioCarrera;
    }

    @Override
    public void crearCarrera(Carrera carrera) {
        this.repositorioCarrera.guardar(carrera);
    }

    @Override
    public Carrera buscarCarreraPorId(Long id) {
        return this.repositorioCarrera.buscarCarreraPorIds(id);
    }

    @Override
    public List<Carrera> obtenerTodasLasCarreras() {
        return this.repositorioCarrera.obtenerTodasLasCarreras();
    }

    @Override
    public void actualizarCarrera(Carrera carrera) {
        this.repositorioCarrera.actualizarCarrera(carrera);
    }

    @Override
    public List<Carrera> getCarreras() {
        return repositorioCarrera.getCarreras();
    }

    @Override
    public void eliminarUnaCarrera(Long id) {
        this.repositorioCarrera.eliminarUnaCarrera(id);
    }
}
