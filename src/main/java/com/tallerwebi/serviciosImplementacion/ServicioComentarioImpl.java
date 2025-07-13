package com.tallerwebi.serviciosImplementacion;

import com.tallerwebi.dominio.Comentario;
import com.tallerwebi.dominio.Publicacion;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.AccesoDenegado;
import com.tallerwebi.dominio.excepcion.ComentarioInexistente;
import com.tallerwebi.dominio.excepcion.PublicacionInexistente;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.repositorioInterfaz.RepositorioComentario;
import com.tallerwebi.servicioInterfaz.ServicioComentario;
import com.tallerwebi.servicioInterfaz.ServicioPublicacion;
import com.tallerwebi.servicioInterfaz.ServicioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

@Service("servicioComentario")
@Transactional
public class ServicioComentarioImpl implements ServicioComentario {
    private final RepositorioComentario repositorioComentario;
    private final ServicioPublicacion servicioPublicacion;
    private final ServicioUsuario servicioUsuario;

    @Autowired
    public ServicioComentarioImpl(RepositorioComentario repositorioComentario, ServicioPublicacion servicioPublicacion, ServicioUsuario servicioUsuario) {
        this.repositorioComentario = repositorioComentario;
        this.servicioPublicacion = servicioPublicacion;
        this.servicioUsuario = servicioUsuario;
    }

    @Override
    public Comentario crearComentario(Long idPublicacion, Usuario usuario, String descripcion) throws PublicacionInexistente, AccesoDenegado {
        if ("ADMIN".equalsIgnoreCase(usuario.getRol())) {
            throw new AccesoDenegado("Los administradores no pueden crear comentarios.");
        }
        Publicacion publicacion = servicioPublicacion.obtenerPublicacion(idPublicacion);
        Comentario nuevoComentario = new Comentario();
        nuevoComentario.setDescripcion(descripcion);
        nuevoComentario.setUsuario(usuario);
        nuevoComentario.setPublicacion(publicacion);
        repositorioComentario.guardar(nuevoComentario);
        return nuevoComentario;
    }

    @Override
    public void modificarComentario(Long idComentario, String descripcion, Long idUsuario) throws ComentarioInexistente, AccesoDenegado, UsuarioNoEncontrado {
        Comentario comentario = obtenerComentarioPorId(idComentario);
        Usuario usuarioQueModifica = servicioUsuario.obtenerUsuario(idUsuario);

        boolean esDuenio = comentario.getUsuario().getId().equals(idUsuario);
        boolean esAdmin = "ADMIN".equalsIgnoreCase(usuarioQueModifica.getRol());

        if (!esDuenio && !esAdmin) {
            throw new AccesoDenegado("No tenés permiso para modificar este comentario.");
        }

        comentario.setDescripcion(descripcion);
        repositorioComentario.guardar(comentario);
    }

    @Override
    public void eliminarComentario(Long idComentario, Long idUsuarioQueElimina) throws ComentarioInexistente, AccesoDenegado, UsuarioNoEncontrado {
        Comentario comentario = obtenerComentarioPorId(idComentario);
        Usuario usuarioQueElimina = servicioUsuario.obtenerUsuario(idUsuarioQueElimina);

        boolean esDuenio = comentario.getUsuario().getId().equals(idUsuarioQueElimina);
        boolean esAdmin = "ADMIN".equalsIgnoreCase(usuarioQueElimina.getRol());

        if (!esDuenio && !esAdmin) {
            throw new AccesoDenegado("No tienes permiso para eliminar este comentario.");
        }

        repositorioComentario.eliminar(comentario);
    }

    @Override
    public Comentario obtenerComentarioPorId(Long id) throws ComentarioInexistente {
        Comentario comentario = repositorioComentario.buscarPorId(id);
        if(comentario == null){
            throw new ComentarioInexistente("El comentario no existe");
        }
        return comentario;
    }
}