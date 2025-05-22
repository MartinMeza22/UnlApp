package com.tallerwebi.dominio;

import java.util.List;

public class Cuatrimestre {

    private Integer cuatrimestre;
    private List<Materia> materias;

    public Cuatrimestre() {
    }

    public Integer getCuatrimestre() {
        return cuatrimestre;
    }

    public void setCuatrimestre(Integer cuatrimestre) {
        this.cuatrimestre = cuatrimestre;
    }

    public List<Materia> getMaterias() {
        return materias;
    }

    public void setMaterias(List<Materia> materias) {
        this.materias = materias;
    }
}
