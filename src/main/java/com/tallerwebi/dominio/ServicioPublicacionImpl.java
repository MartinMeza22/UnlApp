package com.tallerwebi.dominio;

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