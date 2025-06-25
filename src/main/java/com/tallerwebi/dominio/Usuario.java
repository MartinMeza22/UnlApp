package com.tallerwebi.dominio;

import javax.persistence.*;
import java.util.Set;
import java.util.Objects;


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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "carreraID")
    private Carrera carrera;

    //relacion con likes de las publicaciones
    @ManyToMany(mappedBy = "usuariosQueDieronLike")
    private Set<Publicacion> publicacionesLikeadas;

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

    // Metodo para que no rompa mucho el ID de la carrera (codigo viejo)
    public Long getCarreraID() {
        return carrera != null ? carrera.getId() : null;
    }

    public void setCarreraID(Long id) {
        if (id != null) {
            this.carrera = new Carrera();
            this.carrera.setId(id);
        } else {
            this.carrera = null;
        }
    }

    //metodos para saber los likes del usuario
    public Set<Publicacion> getPublicacionesLikeadas() {
        return publicacionesLikeadas;
    }

    public void setPublicacionesLikeadas(Set<Publicacion> publicacionesLikeadas) {
        this.publicacionesLikeadas = publicacionesLikeadas;
    }

//    public void setCarrera(Carrera carrera) {
//        this.carrera = carrera;
//    }
//
//    // Método de conveniencia para obtener el ID de la carrera
//    public Long getCarreraID() {
//        return carrera != null ? carrera.getId() : null;
//    }

    public boolean activo() {
        return activo;
    }

    public void activar() {
        activo = true;
    }

    /*esto es para que el programa pueda saber si el usuario le dio like a la publicacion o no
      porque no comparaba los ids correctamente
    */
    @Override
    public boolean equals(Object o) {

        if (this == o) return true;


        if (o == null || getClass() != o.getClass()) return false;


        Usuario usuario = (Usuario) o;


        return id != null && id.equals(usuario.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}