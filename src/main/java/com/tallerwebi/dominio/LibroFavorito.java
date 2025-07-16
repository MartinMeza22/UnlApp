package com.tallerwebi.dominio;

import javax.persistence.*;

@Entity
public class LibroFavorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "bookApiId", nullable = false)
    private String idGoogleBook;


    public LibroFavorito() {
    }

    public LibroFavorito(Usuario usuario, String idGoogleBook) {
        this.usuario = usuario;
        this.idGoogleBook = idGoogleBook;
    }

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

    public String getIdGoogleBook() {
        return idGoogleBook;
    }

    public void setIdGoogleBook(String idGoogleBook) {
        this.idGoogleBook = idGoogleBook;
    }
}