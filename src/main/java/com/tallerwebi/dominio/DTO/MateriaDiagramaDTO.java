package com.tallerwebi.dominio.DTO;

import java.util.ArrayList;
import java.util.List;

public class MateriaDiagramaDTO {
    private Long id;
    private String nombre;
    private Integer cuatrimestre;
    private List<Long> correlativas;
    private String estado;
    private Boolean aprobada;

    // Constructor
    public MateriaDiagramaDTO(Long id, String nombre, Integer cuatrimestre) {
        this.id = id;
        this.nombre = nombre;
        this.cuatrimestre = cuatrimestre;
        this.correlativas = new ArrayList<>();
        this.estado = "PENDIENTE";
        this.aprobada = false;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Boolean getAprobada() {
        return aprobada;
    }

    public void setAprobada(Boolean aprobada) {
        this.aprobada = aprobada;
    }

    @Override
    public String toString() {
        return "MateriaDiagramaDTO{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", cuatrimestre=" + cuatrimestre +
                ", correlativas=" + correlativas +
                ", estado='" + estado + '\'' +
                '}';
    }
}