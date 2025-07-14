package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.*;
import com.tallerwebi.repositorioInterfaz.RepositorioReporte;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository("repositorioReporte")
public class RepositorioReporteImpl implements RepositorioReporte {

    private final SessionFactory sessionFactory;

    @Autowired
    public RepositorioReporteImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void guardar(Reporte reporte) {
        sessionFactory.getCurrentSession().save(reporte);
    }

    @Override
    public Reporte buscarReportePorUsuarioYPublicacion(Usuario usuario, Publicacion publicacion) {
        return (Reporte) sessionFactory.getCurrentSession().createCriteria(Reporte.class)
                .add(Restrictions.eq("usuario", usuario))
                .add(Restrictions.eq("publicacion", publicacion))
                .uniqueResult();
    }

    @Override
    public Reporte buscarReportePorUsuarioYComentario(Usuario usuario, Comentario comentario) {
        return (Reporte) sessionFactory.getCurrentSession().createCriteria(Reporte.class)
                .add(Restrictions.eq("usuario", usuario))
                .add(Restrictions.eq("comentario", comentario))
                .uniqueResult();
    }

    @Override
    public List<Reporte> buscarReportesPorCarrera(Carrera carrera) {
        String hql = "SELECT r FROM Reporte r " +
                "LEFT JOIN r.publicacion p " +
                "LEFT JOIN r.comentario c " +
                "LEFT JOIN p.materia pm " +
                "LEFT JOIN c.publicacion cp " +
                "LEFT JOIN cp.materia cpm " +
                "WHERE pm.carrera = :carrera OR cpm.carrera = :carrera " +
                "ORDER BY r.fechaCreacion DESC";

        return sessionFactory.getCurrentSession()
                .createQuery(hql, Reporte.class)
                .setParameter("carrera", carrera)
                .getResultList();
    }
    @Override
    public Reporte buscarPorId(Long id) {
        return sessionFactory.getCurrentSession().get(Reporte.class, id);
    }

    @Override
    public void eliminar(Reporte reporte) {
        sessionFactory.getCurrentSession().delete(reporte);
    }

    @Override
    public List<Reporte> buscarPorPublicacion(Long idPublicacion) {
        final Session session = sessionFactory.getCurrentSession();
        return (List<Reporte>) session.createCriteria(Reporte.class)
                .add(Restrictions.eq("publicacion.id", idPublicacion))
                .list();
    }

    @Override
    public List<Reporte> buscarPorComentario(Long idComentario) {
        final Session session = sessionFactory.getCurrentSession();
        return (List<Reporte>) session.createCriteria(Reporte.class)
                .add(Restrictions.eq("comentario.id", idComentario))
                .list();
    }

}
