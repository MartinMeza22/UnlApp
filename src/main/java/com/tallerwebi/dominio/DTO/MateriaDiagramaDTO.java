package com.tallerwebi.dominio.DTO;

import java.util.ArrayList;
import java.util.List;

public class MateriaDiagramaDTO {
    private Long id;
    private String nombre;
    private Integer cuatrimestre;
    private List<Long> correlativas; // IDs de las materias correlativas

    // Constructor
    public MateriaDiagramaDTO(Long id, String nombre, Integer cuatrimestre) {
        this.id = id;
        this.nombre = nombre;
        this.cuatrimestre = cuatrimestre;
        this.correlativas = new ArrayList<>();
    }

    // Getters and Setters
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

    public Integer getCuatrimestre() {
        return cuatrimestre;
    }

    public void setCuatrimestre(Integer cuatrimestre) {
        this.cuatrimestre = cuatrimestre;
    }

    public List<Long> getCorrelativas() {
        return correlativas;
    }

    public void setCorrelativas(List<Long> correlativas) {
        this.correlativas = correlativas;
    }

    public void addCorrelativa(Long idCorrelativa) {
        this.correlativas.add(idCorrelativa);
    }

}