package com.tallerwebi.dominio;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_destinatario_id")
    private Usuario usuarioDestinatario;

    @Column(nullable = false, length = 300)
    private String mensaje;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private boolean leida = false;


    public Notificacion() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public Notificacion(Usuario usuarioDestinatario, String mensaje) {
        this();
        this.usuarioDestinatario = usuarioDestinatario;
        this.mensaje = mensaje;

    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuarioDestinatario() { return usuarioDestinatario; }
    public void setUsuarioDestinatario(Usuario usuarioDestinatario) { this.usuarioDestinatario = usuarioDestinatario; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public boolean isLeida() { return leida; }
    public void setLeida(boolean leida) { this.leida = leida; }

}
