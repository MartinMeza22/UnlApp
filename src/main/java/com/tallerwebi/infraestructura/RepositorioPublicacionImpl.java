package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Carrera;
import com.tallerwebi.dominio.Materia;
import com.tallerwebi.dominio.Publicacion;
import com.tallerwebi.repositorioInterfaz.RepositorioPublicacion;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository("repositorioPublicacion")
public class RepositorioPublicacionImpl implements RepositorioPublicacion {
    private final SessionFactory sessionFactory;

    @Autowired
    public RepositorioPublicacionImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void guardar(Publicacion publicacion) {
        sessionFactory.getCurrentSession().saveOrUpdate(publicacion);
    }
    @Override
    public void eliminar(Publicacion publicacion) {
        sessionFactory.getCurrentSession().delete(publicacion);
    }
    @Override
    public Publicacion buscarPorId(Long id) {
        return sessionFactory.getCurrentSession().get(Publicacion.class, id);
    }

    @Override
    public List<Publicacion> buscarPublicaciones(Carrera carrera, Materia materia, String orden)  {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Publicacion.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        criteria.setFetchMode("comentarios", FetchMode.JOIN);
        criteria.setFetchMode("usuariosQueDieronLike", FetchMode.JOIN);
        criteria.setFetchMode("reportes", FetchMode.JOIN);


        criteria.setFetchMode("comentarios.reportes", FetchMode.JOIN);

        criteria.createAlias("materia", "m");
        criteria.add(Restrictions.eq("m.carrera", carrera));

        if (materia != null) {
            criteria.add(Restrictions.eq("materia", materia));
        }
        if ("likes".equalsIgnoreCase(orden)) {
            criteria.addOrder(Order.desc("likes"));
        } else {
            criteria.addOrder(Order.desc("fechaCreacion"));
        }
        return criteria.list();
    }

}