package com.tallerwebi.dominio;

import javax.persistence.*;

@Entity
@Table(name = "Materia")
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "carrera_id")
    private String carreraId;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    @Column(length = 50)
    private String tipo;

    @Column(name = "carga_horaria")
    private Integer cargaHoraria;

    @Column
    private Integer cuatrimestre;

    @Column(name = "correlativa_1")
    private String correlativa1;

    @Column(name = "correlativa_2")
    private String correlativa2;

    @Column(name = "correlativa_3")
    private String correlativa3;

    @Column(name = "correlativa_4")
    private String correlativa4;

    @Column(name = "correlativa_5")
    private String correlativa5;

    @Column(name = "correlativa_6")
    private String correlativa6;

    @Column
    private Boolean activa = true;

    // Constructors
    public Materia() {}

    public Materia(String nombre, String carreraId, Integer cuatrimestre) {
        this.nombre = nombre;
        this.carreraId = carreraId;
        this.cuatrimestre = cuatrimestre;
        this.activa = true;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCarreraId() { return carreraId; }
    public void setCarreraId(String carreraId) { this.carreraId = carreraId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Integer getCargaHoraria() { return cargaHoraria; }
    public void setCargaHoraria(Integer cargaHoraria) { this.cargaHoraria = cargaHoraria; }

    public Integer getCuatrimestre() { return cuatrimestre; }
    public void setCuatrimestre(Integer cuatrimestre) { this.cuatrimestre = cuatrimestre; }

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

    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }

    // Utility methods
    public boolean estaActiva() {
        return activa != null && activa;
    }

    public void activar() {
        this.activa = true;
    }

    public void desactivar() {
        this.activa = false;
    }

    @Override
    public String toString() {
        return "Materia{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", carreraId='" + carreraId + '\'' +
                ", cuatrimestre=" + cuatrimestre +
                ", activa=" + activa +
                '}';
    }
}