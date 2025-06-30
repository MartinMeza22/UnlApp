package com.tallerwebi.serviciosImplementacion;

import com.tallerwebi.dominio.Comentario;
import com.tallerwebi.dominio.Publicacion;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.repositorioInterfaz.RepositorioComentario;
import com.tallerwebi.dominio.excepcion.AccesoDenegado;
import com.tallerwebi.dominio.excepcion.ComentarioInexistente;
import com.tallerwebi.dominio.excepcion.PublicacionInexistente;
import com.tallerwebi.servicioInterfaz.ServicioComentario;
import com.tallerwebi.servicioInterfaz.ServicioPublicacion;
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
    @Override
    public void modificarComentario(Long idComentario, String descripcion, Long idUsuario) throws ComentarioInexistente, AccesoDenegado {
        Comentario comentario = repositorioComentario.buscarPorId(idComentario);
        if (comentario == null) {
            throw new ComentarioInexistente("El comentario que intentás modificar no existe.");
        }

        // ¡Verificación de seguridad!
        if (!comentario.getUsuario().getId().equals(idUsuario)) {
            throw new AccesoDenegado("No tenés permiso para modificar este comentario.");
        }

        comentario.setDescripcion(descripcion);
        repositorioComentario.guardar(comentario); // guardar usa saveOrUpdate, por lo que actualiza
    }
    @Override
    public void eliminarComentario(Long idComentario, Long idUsuarioQueElimina) throws ComentarioInexistente, AccesoDenegado {
        Comentario comentario = repositorioComentario.buscarPorId(idComentario); // Necesitarás añadir buscarPorId al repositorio
        if (comentario == null) {
            throw new ComentarioInexistente("El comentario que intentas eliminar no existe.");
        }
        // ¡COMPROBACIÓN DE SEGURIDAD CRÍTICA!
        if (!comentario.getUsuario().getId().equals(idUsuarioQueElimina)) {
            throw new AccesoDenegado("No tienes permiso para eliminar este comentario.");
        }
        repositorioComentario.eliminar(comentario);
    }
}