package com.tallerwebi.dominio;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Clase que representa una materia según la estructura de mockMateriasDB.json
 */
public class MateriaDB {

    @JsonProperty("id")
    private String id;

    @JsonProperty("carreraId")
    private String carreraId;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("descripcion")
    private String descripcion;

    @JsonProperty("tipo")
    private String tipo;

    @JsonProperty("cargaHoraria")
    private Integer cargaHoraria;

    @JsonProperty("cuatrimestre")
    private Integer cuatrimestre;

    @JsonProperty("correlativa1")
    private String correlativa1;

    @JsonProperty("correlativa2")
    private String correlativa2;

    @JsonProperty("correlativa3")
    private String correlativa3;

    @JsonProperty("correlativa4")
    private String correlativa4;

    @JsonProperty("correlativa5")
    private String correlativa5;

    @JsonProperty("correlativa6")
    private String correlativa6;

    // Constructor por defecto
    public MateriaDB() {}

    // Getters básicos
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getCuatrimestre() { return cuatrimestre; }
    public void setCuatrimestre(Integer cuatrimestre) { this.cuatrimestre = cuatrimestre; }

    public Integer getCargaHoraria() { return cargaHoraria; }
    public void setCargaHoraria(Integer cargaHoraria) { this.cargaHoraria = cargaHoraria; }

    public String getCarreraId() { return carreraId; }
    public void setCarreraId(String carreraId) { this.carreraId = carreraId; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getCorrelativa1() { return correlativa1; }
    public void setCorrelativa1(String correlativa1) { this.correlativa1 = correlativa1; }

    public String getCorrelativa2() { return correlativa2; }
    public void setCorrelativa2(String correlativa2) { this.correlativa2 = correlativa2; }

    public String getCorrelativa3() { return correlativa3; }
    public void setCorrelativa3(String correlativa3) { this.correlativa3 = correlativa3; }

    public String getCorrelativa4() { return correlativa4; }
    public void setCorrelativa4(String correlativa4) { this.correlativa4 = correlativa4; }

    public String getCorrelativa5() { return correlativa5; }
    public void setCorrelativa5(String correlativa5) { this.correlativa5 = correlativa5; }

    public String getCorrelativa6() { return correlativa6; }
    public void setCorrelativa6(String correlativa6) { this.correlativa6 = correlativa6; }
}