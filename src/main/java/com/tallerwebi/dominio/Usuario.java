package com.tallerwebi.dominio;

import javax.persistence.*;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String rol;
    private String nombre;
    private String apellido;
    private String telefono;
    private String situacionLaboral;
    private Integer disponibilidadHoraria;
    private Boolean activo = false;

    // Relación con Carrera
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carreraID")
    private Carrera carrera;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getSituacionLaboral() {
        return situacionLaboral;
    }

    public void setSituacionLaboral(String situacionLaboral) {
        this.situacionLaboral = situacionLaboral;
    }

    public Integer getDisponibilidadHoraria() {
        return disponibilidadHoraria;
    }

    public void setDisponibilidadHoraria(Integer disponibilidadHoraria) {
        this.disponibilidadHoraria = disponibilidadHoraria;
    }

    // Getter y Setter para la relación con Carrera
    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    // Método de conveniencia para obtener el ID de la carrera
    public Long getCarreraID() {
        return carrera != null ? carrera.getId() : null;
    }

    public boolean activo() {
        return activo;
    }

    public void activar() {
        activo = true;
    }
}