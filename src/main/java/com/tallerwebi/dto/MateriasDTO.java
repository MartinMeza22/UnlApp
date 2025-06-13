package com.tallerwebi.dto;

public class MateriasDTO {

        private Long id; //materia ID
        private Integer dificultad;
        private Integer nota;
        private Integer estado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public Integer getDificultad() {
        return dificultad;
    }

    public void setDificultad(Integer dificultad) {
        this.dificultad = dificultad;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = Integer.parseInt(String.valueOf(estado));
    }

    @Override
    public String toString() {
        return "MateriasDTO{" +
                "id=" + id +
                ", dificultad=" + dificultad +
                ", nota=" + nota +
                ", estado=" + estado +
                '}';
    }
}
