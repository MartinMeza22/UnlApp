package com.tallerwebi.dominio.decorator;

import com.tallerwebi.dominio.DTO.MateriaDTO;
import com.tallerwebi.dominio.servicios.ServicioProgreso;

import java.util.List;

public class FiltroBaseMaterias implements FiltroMaterias{

    private ServicioProgreso servicioProgreso;

    public FiltroBaseMaterias(ServicioProgreso servicioProgreso) {
        this.servicioProgreso = servicioProgreso;
    }

    @Override
    public List<MateriaDTO> filtrar(String idCarrera, Long usuarioId) {
        return servicioProgreso.materias(idCarrera, usuarioId);
    }
}
