package com.tallerwebi.dto;

import com.tallerwebi.dominio.Materia;

import java.util.ArrayList;
import java.util.List;


public class MateriasWrapper {

    private List<MateriasDTO> materias = new ArrayList<>();

    public List<MateriasDTO> getMaterias() {
        return materias;
    }

    public void setMaterias(List<MateriasDTO> materias) {
        this.materias = materias;
    }

    @Override
    public String toString() {
        return "MateriasWrapper{" +
                "materias=" + materias +
                '}';
    }
}
