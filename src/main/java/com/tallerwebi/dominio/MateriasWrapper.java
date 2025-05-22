package com.tallerwebi.dominio;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MateriasWrapper {

    @JsonProperty("materias")
    private List<Materia> materias;

    public MateriasWrapper() {
    }

    public List<Materia> getMaterias() {
        return materias;
    }

    public void setMaterias(List<Materia> materias) {
        this.materias = materias;
    }
}
