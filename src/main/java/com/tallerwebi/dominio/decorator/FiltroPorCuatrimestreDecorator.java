package com.tallerwebi.dominio.decorator;

import com.tallerwebi.dominio.DTO.MateriaDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FiltroPorCuatrimestreDecorator implements  FiltroMaterias{

    private FiltroMaterias filtroMaterias;
    private Integer cuatrimestre;

    public FiltroPorCuatrimestreDecorator(FiltroMaterias filtroMaterias, Integer cuatrimestre) {
        this.filtroMaterias = filtroMaterias;
        this.cuatrimestre = cuatrimestre;
    }

    @Override
    public List<MateriaDTO> filtrar(String idCarrera, Long usuarioId) {
        List<MateriaDTO> materias = filtroMaterias.filtrar(idCarrera, usuarioId);

        return materias.stream()
                .filter(materia -> cuatrimestre.equals(materia.getCuatrimestre()))
                .collect(Collectors.toList());
    }
}
