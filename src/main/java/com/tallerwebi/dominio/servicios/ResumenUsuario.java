package com.tallerwebi.dominio.servicios;

import com.tallerwebi.dominio.Usuario;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resumen_usuario")
public class ResumenUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Lob //Es para que hibernate maneje la columna según lo que crea correcto
    // lob = large object
    @Column(name = "texto_original", length = 2000)
    private String textoOriginal;

    @Lob //Es para que hibernate maneje la columna según lo que crea correcto
    // lob = large object
    @Column(name = "resumen_generado", length = 3000)
    private String resumenGenerado;

    @Column(name = "fecha_generacion")
    private LocalDateTime fechaGeneracion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTextoOriginal() {
        return textoOriginal;
    }

    public void setTextoOriginal(String textoOriginal) {
        this.textoOriginal = textoOriginal;
    }

    public String getResumenGenerado() {
        return resumenGenerado;
    }

    public void setResumenGenerado(String resumenGenerado) {
        this.resumenGenerado = resumenGenerado;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }
}
