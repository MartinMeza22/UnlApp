package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.PublicacionInexistente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;

@Service("servicioPublicacion")
@Transactional
public class ServicioPublicacionImpl implements ServicioPublicacion {
    private final RepositorioPublicacion repositorioPublicacion;
    private final RepositorioMateria repositorioMateria;

    @Autowired
    public ServicioPublicacionImpl(RepositorioPublicacion repositorioPublicacion, RepositorioMateria repositorioMateria) {
        this.repositorioPublicacion = repositorioPublicacion;
        this.repositorioMateria = repositorioMateria;
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
}