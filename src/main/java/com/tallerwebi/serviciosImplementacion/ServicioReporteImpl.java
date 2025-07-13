package com.tallerwebi.serviciosImplementacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.*;
import com.tallerwebi.repositorioInterfaz.RepositorioComentario;
import com.tallerwebi.repositorioInterfaz.RepositorioPublicacion;
import com.tallerwebi.repositorioInterfaz.RepositorioReporte;
import com.tallerwebi.repositorioInterfaz.RepositorioUsuario;
import com.tallerwebi.servicioInterfaz.ServicioReporte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ServicioReporteImpl implements ServicioReporte {

    private final RepositorioReporte repositorioReporte;
    private final RepositorioPublicacion repositorioPublicacion;
    private final RepositorioComentario repositorioComentario;
    private final RepositorioUsuario repositorioUsuario;
    private static final int LIMITE_REPORTES = 5;

    @Autowired
    public ServicioReporteImpl(RepositorioReporte repoReporte, RepositorioPublicacion repoPub, RepositorioComentario repoCom, RepositorioUsuario repoUser) {
        this.repositorioReporte = repoReporte;
        this.repositorioPublicacion = repoPub;
        this.repositorioComentario = repoCom;
        this.repositorioUsuario = repoUser;
    }

    @Override
    public Reporte crearReporteParaPublicacion(Long idPublicacion, Long idUsuario, String motivo, String desc) throws PublicacionInexistente, UsuarioNoEncontrado, ReporteExistente {
        Publicacion publicacion = repositorioPublicacion.buscarPorId(idPublicacion);
        if(publicacion == null) throw new PublicacionInexistente("La publicación no existe");

        Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);
        if(usuario == null) throw new UsuarioNoEncontrado();

        if(repositorioReporte.buscarReportePorUsuarioYPublicacion(usuario, publicacion) != null) {
            throw new ReporteExistente("Ya has reportado esta publicación.");
        }

        Reporte reporte = new Reporte();
        reporte.setUsuario(usuario);
        reporte.setPublicacion(publicacion);
        reporte.setMotivo(motivo);
        if(motivo.equals("otro")) {
            reporte.setDescripcionAdicional(desc);
        }
        repositorioReporte.guardar(reporte);

        if(publicacion.getReportes().size() >= LIMITE_REPORTES) {
            repositorioPublicacion.eliminar(publicacion);
        }
        return reporte;
    }

    @Override
    public Reporte crearReporteParaComentario(Long idComentario, Long idUsuario, String motivo, String desc) throws ComentarioInexistente, UsuarioNoEncontrado, ReporteExistente {
        Comentario comentario = repositorioComentario.buscarPorId(idComentario);
        if(comentario == null) throw new ComentarioInexistente("El comentario no existe");

        Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);
        if(usuario == null) throw new UsuarioNoEncontrado();

        if(repositorioReporte.buscarReportePorUsuarioYComentario(usuario, comentario) != null) {
            throw new ReporteExistente("Ya has reportado este comentario.");
        }

        Reporte reporte = new Reporte();
        reporte.setUsuario(usuario);
        reporte.setComentario(comentario);
        reporte.setMotivo(motivo);
        if(motivo.equals("otro")) {
            reporte.setDescripcionAdicional(desc);
        }
        repositorioReporte.guardar(reporte);

        if(comentario.getReportes().size() >= LIMITE_REPORTES) {
            repositorioComentario.eliminar(comentario);
        }
        return reporte;
    }

    @Override
    public List<Reporte> obtenerReportesPorCarrera(Carrera carrera) {
        return repositorioReporte.buscarReportesPorCarrera(carrera);
    }

    @Override
    public void eliminarReporte(Long idReporte) throws ReporteInexistente {
        Reporte reporte = repositorioReporte.buscarPorId(idReporte);
        if (reporte == null) {
            throw new ReporteInexistente("El reporte que intentas eliminar no existe.");
        }
        repositorioReporte.eliminar(reporte);
    }


    @Override
    public Reporte obtenerReportePorId(Long idReporte) throws ReporteInexistente {
        Reporte reporte = repositorioReporte.buscarPorId(idReporte);
        if (reporte == null) {
            throw new ReporteInexistente("El reporte no existe.");
        }
        return reporte;
    }

    @Override
    public void eliminarReportesDePublicacion(Long idPublicacion) {
        List<Reporte> reportes = repositorioReporte.buscarPorPublicacion(idPublicacion);
        for(Reporte reporte : reportes) {
            repositorioReporte.eliminar(reporte);
        }
    }

    @Override
    public void eliminarReportesDeComentario(Long idComentario) {
        List<Reporte> reportes = repositorioReporte.buscarPorComentario(idComentario);
        for(Reporte reporte : reportes) {
            repositorioReporte.eliminar(reporte);
        }
    }
}