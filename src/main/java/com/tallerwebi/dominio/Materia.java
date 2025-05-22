package com.tallerwebi.dominio;

import java.util.List;

public class Materia {

    private String nombre;
    private Integer codigo;
    private String dificultad;
    private String profesor;
    private List<Integer> correlatividad;
    private String regimen;
    private Integer horasSemanales;

    public Materia() {
    }

    public Materia(String nombre, Integer codigo, String dificultad, String profesor, List<Integer> correlatividad, String regimen, Integer horasSemanales) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.dificultad = dificultad;
        this.profesor = profesor;
        this.correlatividad = correlatividad;
        this.regimen = regimen;
        this.horasSemanales = horasSemanales;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDificultad() {
        return dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }

    public String getProfesor() {
        return profesor;
    }

    public void setProfesor(String profesor) {
        this.profesor = profesor;
    }

    public List<Integer> getCorrelatividad() {
        return correlatividad;
    }

    public void setCorrelatividad(List<Integer> correlatividad) {
        this.correlatividad = correlatividad;
    }

    public String getRegimen() {
        return regimen;
    }

    public void setRegimen(String regimen) {
        this.regimen = regimen;
    }

    public Integer getHorasSemanales() {
        return horasSemanales;
    }

    public void setHorasSemanales(Integer horasSemanales) {
        this.horasSemanales = horasSemanales;
    }
}
