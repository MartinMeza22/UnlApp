package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Notificacion;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.repositorioInterfaz.RepositorioNotificacion;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository("repositorioNotificacion")
public class RepositorioNotificacionImpl implements RepositorioNotificacion {

    private final SessionFactory sessionFactory;

    @Autowired
    public RepositorioNotificacionImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void guardar(Notificacion notificacion) {
        sessionFactory.getCurrentSession().save(notificacion);
    }

    @Override
    public List<Notificacion> obtenerNoLeidasPorUsuario(Usuario usuario) {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(Notificacion.class)
                .add(Restrictions.eq("usuarioDestinatario", usuario))
                .add(Restrictions.eq("leida", false))
                .addOrder(Order.desc("fechaCreacion"))
                .list();
    }
    @Override
    public void marcarTodasComoLeidas(Usuario usuario) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "UPDATE Notificacion SET leida = true WHERE usuarioDestinatario = :usuario AND leida = false";
        session.createQuery(hql)
                .setParameter("usuario", usuario)
                .executeUpdate();
    }
}
