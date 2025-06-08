package com.tallerwebi.dominio.DTO;

public class MateriaDTO {

    private Long id;
    private String nombre;
    private String dificultad;
    private String estado;
    private Double nota;
    private Integer cuatrimestre;

    public MateriaDTO() {
    }

    public MateriaDTO(Long id, String nombre, String dificultad, String estado, Double nota, Integer cuatrimestre) {
        this.id = id;
        this.nombre = nombre;
        this.dificultad = dificultad;
        this.estado = estado;
        this.nota = nota;
        this.cuatrimestre = cuatrimestre;
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

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    public Integer getCuatrimestre() {
        return cuatrimestre;
    }

    public void setCuatrimestre(Integer cuatrimestre) {
        this.cuatrimestre = cuatrimestre;
    }
}
