package com.tallerwebi.dominio;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Publicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Usuario usuario;
    @ManyToOne
    private Materia materia;
    @Column(nullable = false)
    private String titulo;
    @Column(nullable = false, length = 5000)
    private String descripcion;
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
    @Column(nullable = true)
    private Integer likes = 0;
    @OneToMany(mappedBy = "publicacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios;

    // usuarios que dieron like a esta publicación
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "Usuario_Publicacion_Like", // Nombre de la tabla intermedia
            joinColumns = @JoinColumn(name = "publicacion_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private Set<Usuario> usuariosQueDieronLike = new HashSet<>();


    public Publicacion() {
        this.fechaCreacion = LocalDateTime.now();
        this.likes = 0;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Materia getMateria() { return materia; }
    public void setMateria(Materia materia) { this.materia = materia; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public Integer getLikes() { return likes; }
    public void setLikes(Integer likes) { this.likes = likes; }
    public List<Comentario> getComentarios() { return comentarios; }
    public void setComentarios(List<Comentario> comentarios) { this.comentarios = comentarios; }

    @Transient
    public String getFechaCreacionFormateada() {

        if (this.fechaCreacion == null) {
            return "Fecha no disponible";
        }
        // Creamos el objeto que define el formato deseado
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        return this.fechaCreacion.format(formatter);
    }
    public void agregarLike(Usuario usuario) {
        if (this.usuariosQueDieronLike.add(usuario)) {this.likes++;}}

    public void quitarLike(Usuario usuario) {
        if (this.usuariosQueDieronLike.remove(usuario)) {this.likes--;}}

    public boolean usuarioDioLike(Usuario usuario) {return this.usuariosQueDieronLike.contains(usuario);}

    public Set<Usuario> getUsuariosQueDieronLike() {return usuariosQueDieronLike;}

    public void setUsuariosQueDieronLike(Set<Usuario> usuariosQueDieronLike){this.usuariosQueDieronLike = usuariosQueDieronLike;}
}