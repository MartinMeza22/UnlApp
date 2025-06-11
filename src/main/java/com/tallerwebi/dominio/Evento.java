package com.tallerwebi.dominio;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "evento")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(length = 1000)
    private String descripcion;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(length = 50)
    private String tipo; // "EXAMEN", "ENTREGA", "ESTUDIO", "PERSONAL", "CLASE", etc.

    @Column(length = 200)
    private String ubicacion;

    @Column
    private Boolean completado = false;

    @Column
    private Boolean activo = true;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    // Recurring event fields
    @Column(name = "es_recurrente")
    private Boolean esRecurrente = false;

    @Column(name = "tipo_recurrencia")
    private String tipoRecurrencia; // "DIARIO", "SEMANAL", "MENSUAL", "ANUAL"

    @Column(name = "intervalo_recurrencia")
    private Integer intervaloRecurrencia = 1; // cada X días/semanas/meses

    @Column(name = "fecha_fin_recurrencia")
    private LocalDate fechaFinRecurrencia;

    @Column(name = "dias_semana")
    private String diasSemana; // "1,2,3,4,5" para lunes a viernes

    // MANDATORY relationship with Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // NULLABLE relationship with Materia
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materia_id", nullable = true)
    private Materia materia;

    // Constructors
    public Evento() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaModificacion = LocalDateTime.now();
        this.completado = false;
        this.activo = true;
    }

    public Evento(String titulo, LocalDateTime fechaInicio, Usuario usuario) {
        this();
        this.titulo = titulo;
        this.fechaInicio = fechaInicio;
        this.usuario = usuario;
    }

    public Evento(String titulo, LocalDateTime fechaInicio, Usuario usuario, Materia materia) {
        this(titulo, fechaInicio, usuario);
        this.materia = materia;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { 
        this.titulo = titulo;
        updateModificationDate();
    }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { 
        this.descripcion = descripcion;
        updateModificationDate();
    }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { 
        this.fechaInicio = fechaInicio;
        updateModificationDate();
    }

    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { 
        this.fechaFin = fechaFin;
        updateModificationDate();
    }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { 
        this.tipo = tipo;
        updateModificationDate();
    }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { 
        this.ubicacion = ubicacion;
        updateModificationDate();
    }

    public Boolean getCompletado() { return completado; }
    public void setCompletado(Boolean completado) { 
        this.completado = completado;
        updateModificationDate();
    }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { 
        this.activo = activo;
        updateModificationDate();
    }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Materia getMateria() { return materia; }
    public void setMateria(Materia materia) { this.materia = materia; }

    public Boolean getEsRecurrente() { return esRecurrente; }
    public void setEsRecurrente(Boolean esRecurrente) { 
        this.esRecurrente = esRecurrente;
        updateModificationDate();
    }

    public String getTipoRecurrencia() { return tipoRecurrencia; }
    public void setTipoRecurrencia(String tipoRecurrencia) { 
        this.tipoRecurrencia = tipoRecurrencia;
        updateModificationDate();
    }

    public Integer getIntervaloRecurrencia() { return intervaloRecurrencia; }
    public void setIntervaloRecurrencia(Integer intervaloRecurrencia) { 
        this.intervaloRecurrencia = intervaloRecurrencia;
        updateModificationDate();
    }

    public LocalDate getFechaFinRecurrencia() { return fechaFinRecurrencia; }
    public void setFechaFinRecurrencia(LocalDate fechaFinRecurrencia) { 
        this.fechaFinRecurrencia = fechaFinRecurrencia;
        updateModificationDate();
    }

    public String getDiasSemana() { return diasSemana; }
    public void setDiasSemana(String diasSemana) { 
        this.diasSemana = diasSemana;
        updateModificationDate();
    }

    // Utility Methods
    private void updateModificationDate() {
        this.fechaModificacion = LocalDateTime.now();
    }

    public boolean esEventoAcademico() {
        return materia != null;
    }

    public boolean esEventoPersonal() {
        return materia == null;
    }

    public boolean estaVencido() {
        if (fechaFin != null) {
            return LocalDateTime.now().isAfter(fechaFin);
        }
        return LocalDateTime.now().isAfter(fechaInicio);
    }

    public boolean esHoy() {
        return fechaInicio.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }

    public boolean esMañana() {
        return fechaInicio.toLocalDate().equals(LocalDateTime.now().plusDays(1).toLocalDate());
    }

    public void marcarComoCompletado() {
        this.completado = true;
        updateModificationDate();
    }

    public void marcarComoIncompleto() {
        this.completado = false;
        updateModificationDate();
    }

    public void desactivar() {
        this.activo = false;
        updateModificationDate();
    }

    public void activar() {
        this.activo = true;
        updateModificationDate();
    }

    // Business logic methods
    public String getEstado() {
        if (!activo) return "INACTIVO";
        if (completado) return "COMPLETADO";
        if (estaVencido()) return "VENCIDO";
        if (esHoy()) return "HOY";
        if (esMañana()) return "MAÑANA";
        return "PENDIENTE";
    }

    public String getNombreMateria() {
        try {
            return materia != null ? materia.getNombre() : "Sin materia";
        } catch (Exception e) {
            // Handle lazy loading exceptions gracefully
            return "Sin materia";
        }
    }

    // Recurring event utilities
    public void configurarRecurrencia(String tipoRecurrencia, Integer intervalo, LocalDate fechaFin) {
        this.esRecurrente = true;
        this.tipoRecurrencia = tipoRecurrencia;
        this.intervaloRecurrencia = intervalo;
        this.fechaFinRecurrencia = fechaFin;
        updateModificationDate();
    }

    public void configurarRecurrenciaSemanal(String diasSemana, LocalDate fechaFin) {
        configurarRecurrencia("SEMANAL", 1, fechaFin);
        this.diasSemana = diasSemana;
    }

    public void deshabilitarRecurrencia() {
        this.esRecurrente = false;
        this.tipoRecurrencia = null;
        this.intervaloRecurrencia = 1;
        this.fechaFinRecurrencia = null;
        this.diasSemana = null;
        updateModificationDate();
    }

    public boolean esRecurrenteActivo() {
        if (!Boolean.TRUE.equals(esRecurrente)) return false;
        if (fechaFinRecurrencia == null) return true;
        return !LocalDate.now().isAfter(fechaFinRecurrencia);
    }

    public LocalDateTime calcularProximaOcurrencia() {
        if (!esRecurrenteActivo()) return null;
        
        LocalDateTime proxima = fechaInicio;
        LocalDateTime ahora = LocalDateTime.now();
        
        while (proxima.isBefore(ahora)) {
            switch (tipoRecurrencia != null ? tipoRecurrencia : "") {
                case "DIARIO":
                    proxima = proxima.plusDays(intervaloRecurrencia != null ? intervaloRecurrencia : 1);
                    break;
                case "SEMANAL":
                    proxima = proxima.plusWeeks(intervaloRecurrencia != null ? intervaloRecurrencia : 1);
                    break;
                case "MENSUAL":
                    proxima = proxima.plusMonths(intervaloRecurrencia != null ? intervaloRecurrencia : 1);
                    break;
                case "ANUAL":
                    proxima = proxima.plusYears(intervaloRecurrencia != null ? intervaloRecurrencia : 1);
                    break;
                default:
                    return null;
            }
        }
        
        if (fechaFinRecurrencia != null && proxima.toLocalDate().isAfter(fechaFinRecurrencia)) {
            return null;
        }
        
        return proxima;
    }

    @Override
    public String toString() {
        return "Evento{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", fechaInicio=" + fechaInicio +
                ", usuario=" + (usuario != null ? usuario.getEmail() : "null") +
                ", materia=" + (materia != null ? materia.getNombre() : "null") +
                ", estado='" + getEstado() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Evento)) return false;

        Evento evento = (Evento) o;

        if (titulo != null ? !titulo.equals(evento.titulo) : evento.titulo != null) return false;
        if (fechaInicio != null ? !fechaInicio.equals(evento.fechaInicio) : evento.fechaInicio != null) return false;
        return usuario != null ? usuario.equals(evento.usuario) : evento.usuario == null;
    }

    @Override
    public int hashCode() {
        int result = titulo != null ? titulo.hashCode() : 0;
        result = 31 * result + (fechaInicio != null ? fechaInicio.hashCode() : 0);
        result = 31 * result + (usuario != null ? usuario.hashCode() : 0);
        return result;
    }
}
