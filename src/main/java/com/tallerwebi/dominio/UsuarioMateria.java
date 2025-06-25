package com.tallerwebi.dominio;

import javax.persistence.*;
import java.time.LocalDate;

@Entity //creando la entidad (se plasmará en la BDD)
@Table(name = "usuario_materia") //Se crea la tabla con el nombre que se asigno
public class UsuarioMateria {

    @Id //Determina id de cada instancia de clase
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //El ManyToOne determina N a 1. En Usuario (en este caso). "LAZY" quiere decir que no se carga el usuario automáticamente, solo cuando se lo accede.
    @JoinColumn(name = "usuario_id", nullable = false) //Determina el nombre de esa columna (donde estará usuario_id), no puede ser null
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materia_id", nullable = false)
    private Materia materia;

    @Column(precision = 4, scale = 2) //el column se utiliza para determinar una columna.
    // El precision es para ver cuantos números van en total. Y "scale" son los números después de la ,
    private Integer nota; // null = cursando, >= 4 = aprobada, < 4 = desaprobada

    @Column
    private Integer dificultad; // Nivel de dificultad 1-10

    @Column
    private Integer estado; // del 0 al 4

    @Column(name = "fecha_asignacion")
    private LocalDate fechaAsignacion;

    @Column(name = "fecha_modificacion")
    private LocalDate fechaModificacion;

    // Constructors
    public UsuarioMateria() {
        this.fechaAsignacion = LocalDate.now();
        this.fechaModificacion = LocalDate.now();
        // nota = null significa que está cursando
    }

    public UsuarioMateria(Usuario usuario, Materia materia) {
        this();
        this.usuario = usuario;
        this.materia = materia;
    }

    public UsuarioMateria(Usuario usuario, Materia materia, Integer nota) {
        this(usuario, materia);
        this.nota = nota;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Materia getMateria() { return materia; }
    public void setMateria(Materia materia) { this.materia = materia; }

    public Integer getNota() { return nota; }
    public void setNota(Integer nota) {
        this.nota = nota;
        this.fechaModificacion = LocalDate.now();
    }

    public Integer getDificultad() { return dificultad; }
    public void setDificultad(Integer dificultad) {
        this.dificultad = dificultad;
        this.fechaModificacion = LocalDate.now();
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public LocalDate getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(LocalDate fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }

    public LocalDate getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDate fechaModificacion) { this.fechaModificacion = fechaModificacion; }

    // Utility Methods - Estado calculado basado en la nota
    public boolean estaAprobada() {
        return nota != null && nota >= 4.0;
    }

    public boolean estaCursando() {
        return nota == null;
    }

    public boolean estaDesaprobada() {
        return nota != null && nota < 4.0;
    }

    public String getEstadoo() {
        if (estaCursando()) return "CURSANDO";
        if (estaAprobada()) return "APROBADA";
        return "DESAPROBADA";
    }

    public void aprobar(Integer nota) {
        if (nota == null || nota < 4) {
            throw new IllegalArgumentException("Para aprobar, la nota debe ser mayor o igual a 4.0");
        }
        this.nota = nota;
        this.fechaModificacion = LocalDate.now();
    }

    public void desaprobar(Integer nota) {
        if (nota == null || nota >= 4) {
            throw new IllegalArgumentException("Para desaprobar, la nota debe ser menor a 4.0");
        }
        this.nota = nota;
        this.fechaModificacion = LocalDate.now();
    }

    public void reiniciarCursada() {
        this.nota = null; // null = cursando
        this.fechaModificacion = LocalDate.now();
    }

    public boolean esDificil() {
        return dificultad != null && dificultad >= 7;
    }

    public boolean esFacil() {
        return dificultad != null && dificultad <= 3;
    }

    public boolean esModerada() {
        return dificultad != null && dificultad >= 4 && dificultad <= 6;
    }

    @Override
    public String toString() {
        return "UsuarioMateria{" +
                "id=" + id +
                ", usuario=" + (usuario != null ? usuario.getEmail() : "null") +
                ", materia=" + (materia != null ? materia.getNombre() : "null") +
                ", estado='" + getEstadoo() + '\'' +
                ", nota=" + getNota() +
                ", dificultad=" + dificultad +
                ", fechaAsignacion=" + fechaAsignacion +
                '}';
    }

    // Equals and HashCode based on usuario and materia (business key)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsuarioMateria)) return false;

        UsuarioMateria that = (UsuarioMateria) o;

        if (usuario != null ? !usuario.equals(that.usuario) : that.usuario != null) return false;
        return materia != null ? materia.equals(that.materia) : that.materia == null;
    }

    @Override
    public int hashCode() {
        int result = usuario != null ? usuario.hashCode() : 0;
        result = 31 * result + (materia != null ? materia.hashCode() : 0);
        return result;
    }
}