package com.tallerwebi.dominio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ServicioCarreraImpl implements ServicioCarrera {

    private final RepositorioCarrera repositorioCarrera;

    @Autowired
    public ServicioCarreraImpl(RepositorioCarrera repositorioCarrera) {
        this.repositorioCarrera = repositorioCarrera;
    }

    @Override
    public List<Carrera> getCarreras() {
        return repositorioCarrera.getCarreras();
    }
}