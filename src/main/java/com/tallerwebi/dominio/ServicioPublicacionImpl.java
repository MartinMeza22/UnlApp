package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.AccesoDenegado;
import com.tallerwebi.dominio.excepcion.PublicacionInexistente;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;

@Service("servicioPublicacion")
@Transactional
public class ServicioPublicacionImpl implements ServicioPublicacion {
    private final RepositorioPublicacion repositorioPublicacion;
    private final RepositorioMateria repositorioMateria;
    private final RepositorioUsuario repositorioUsuario;

    @Autowired
    public ServicioPublicacionImpl(RepositorioPublicacion repositorioPublicacion, RepositorioMateria repositorioMateria, RepositorioUsuario repositorioUsuario) {
        this.repositorioPublicacion = repositorioPublicacion;
        this.repositorioMateria = repositorioMateria;
        this.repositorioUsuario = repositorioUsuario;
    }

    @Override
    public List<Publicacion> buscarPublicaciones(Carrera carrera, Long idMateria, String orden) {
        Materia materiaFiltro = null;
        if (idMateria != null) {
            materiaFiltro = repositorioMateria.buscarPorId(idMateria);
        }
        return repositorioPublicacion.buscarPublicaciones(carrera, materiaFiltro, orden);
    }

    @Override
    public void crearPublicacion(String titulo, String descripcion, Usuario usuario, Long idMateria) {
        Materia materia = repositorioMateria.buscarPorId(idMateria);
        Publicacion nuevaPublicacion = new Publicacion();
        nuevaPublicacion.setTitulo(titulo);
        nuevaPublicacion.setDescripcion(descripcion);
        nuevaPublicacion.setUsuario(usuario);
        nuevaPublicacion.setMateria(materia);
        repositorioPublicacion.guardar(nuevaPublicacion);
    }

    @Override
    public Publicacion obtenerPublicacion(Long idPublicacion) throws PublicacionInexistente {
        Publicacion publicacion = repositorioPublicacion.buscarPorId(idPublicacion);
        if (publicacion == null) {
            throw new PublicacionInexistente("La publicación solicitada no existe.");
        }
        return publicacion;
    }
        @Override
        public void modificarPublicacion(Long idPublicacion, String titulo, String descripcion, Long idUsuario) throws PublicacionInexistente, AccesoDenegado {
            Publicacion publicacion = repositorioPublicacion.buscarPorId(idPublicacion);
            if (publicacion == null) {
                throw new PublicacionInexistente("La publicación que intentás modificar no existe.");
            }
            if (!publicacion.getUsuario().getId().equals(idUsuario)) {
                throw new AccesoDenegado("No tenés permiso para modificar esta publicación.");
            }

            publicacion.setTitulo(titulo);
            publicacion.setDescripcion(descripcion);
            repositorioPublicacion.guardar(publicacion);
        }
    @Override
    public void eliminarPublicacion(Long idPublicacion, Long idUsuarioQueElimina) throws PublicacionInexistente, AccesoDenegado {
        Publicacion publicacion = repositorioPublicacion.buscarPorId(idPublicacion);
        if (publicacion == null) {
            throw new PublicacionInexistente("La publicación que intentas eliminar no existe.");
        }
        if (!publicacion.getUsuario().getId().equals(idUsuarioQueElimina)) {
            throw new AccesoDenegado("No tienes permiso para eliminar esta publicación.");
        }
        repositorioPublicacion.eliminar(publicacion);
    }
    @Override
    public void cambiarEstadoLike(Long idPublicacion, Long idUsuario) throws PublicacionInexistente, UsuarioNoEncontrado {
        Publicacion publicacion = repositorioPublicacion.buscarPorId(idPublicacion);
        if (publicacion == null) {
            throw new PublicacionInexistente("No se encontró la publicación.");
        }

        Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);
        if (usuario == null) {
            throw new UsuarioNoEncontrado();
        }

        if (publicacion.usuarioDioLike(usuario)) {
            publicacion.quitarLike(usuario);
        } else {
            publicacion.agregarLike(usuario);
        }

        repositorioPublicacion.guardar(publicacion);
    }
}