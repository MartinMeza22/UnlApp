package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.PublicacionInexistente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

@Service("servicioComentario")
@Transactional
public class ServicioComentarioImpl implements ServicioComentario {
    private final RepositorioComentario repositorioComentario;
    private final ServicioPublicacion servicioPublicacion;

    @Autowired
    public ServicioComentarioImpl(RepositorioComentario repositorioComentario, ServicioPublicacion servicioPublicacion) {
        this.repositorioComentario = repositorioComentario;
        this.servicioPublicacion = servicioPublicacion;
    }

    @Override
    public void crearComentario(Long idPublicacion, Usuario usuario, String descripcion) throws PublicacionInexistente {
        Publicacion publicacion = servicioPublicacion.obtenerPublicacion(idPublicacion);

        if (publicacion == null) {
            throw new PublicacionInexistente("No se encontró la publicación con ID: " + idPublicacion + " para añadir el comentario.");
        }
        Comentario nuevoComentario = new Comentario();
        nuevoComentario.setDescripcion(descripcion);
        nuevoComentario.setUsuario(usuario);
        nuevoComentario.setPublicacion(publicacion);

        repositorioComentario.guardar(nuevoComentario);
    }
}