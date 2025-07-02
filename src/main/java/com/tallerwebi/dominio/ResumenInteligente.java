package com.tallerwebi.dominio;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.Objects;

@Entity
public class ResumenInteligente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT") //Creo esto porque sino lo crea como un varchar con un maximo de 255 caracteres.
    private String resumen;

    private LocalDate fechaGeneracion;

    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public LocalDate getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDate fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
