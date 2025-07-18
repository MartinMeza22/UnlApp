package com.tallerwebi.dominio.DTO;

public class MateriaDTO {

    private Long id;
    private String nombre;
    private String dificultad;
    private String estado;
    private Integer nota;
    private Integer cuatrimestre;
    private Boolean esCursable;

    public MateriaDTO() {
    }

    public MateriaDTO(Long id, String nombre, String dificultad, String estado, Integer nota, Integer cuatrimestre) {
        this.id = id;
        this.nombre = nombre;
        this.dificultad = dificultad;
        this.estado = estado;
        this.nota = nota;
        this.cuatrimestre = cuatrimestre;
        this.esCursable = false;
    }

    public MateriaDTO(Long id, String nombre, String dificultad, String estado, Integer nota, Integer cuatrimestre, Boolean esCursable) {
        this.id = id;
        this.nombre = nombre;
        this.dificultad = dificultad;
        this.estado = estado;
        this.nota = nota;
        this.cuatrimestre = cuatrimestre;
        this.esCursable = esCursable;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDificultad() {
        return dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public Integer getCuatrimestre() {
        return cuatrimestre;
    }

    public void setCuatrimestre(Integer cuatrimestre) {
        this.cuatrimestre = cuatrimestre;
    }

    public Boolean getEsCursable() {
        return esCursable;
    }

    public void setEsCursable(Boolean esCursable) {
        this.esCursable = esCursable;
    }
}
