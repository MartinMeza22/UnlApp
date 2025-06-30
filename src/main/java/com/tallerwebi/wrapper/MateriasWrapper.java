package com.tallerwebi.wrapper;

import com.tallerwebi.dto.MateriasDTO;

import java.util.List;

public class MateriasWrapper {

    private List<MateriasDTO> materias;

    public MateriasWrapper() {
    }

    public List<MateriasDTO> getMaterias() {
        return materias;
    }

    public void setMaterias(List<MateriasDTO> materias) {
        this.materias = materias;
    }
}
