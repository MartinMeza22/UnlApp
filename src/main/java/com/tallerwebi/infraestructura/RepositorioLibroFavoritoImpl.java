package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.LibroFavorito;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.repositorioInterfaz.RepositorioLibroFavorito;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("repositorioLibroFavorito")
public class RepositorioLibroFavoritoImpl implements RepositorioLibroFavorito {
    private final SessionFactory sessionFactory;

    @Autowired
    public RepositorioLibroFavoritoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void guardar(LibroFavorito libroFavorito) {
        sessionFactory.getCurrentSession().save(libroFavorito);
    }

    @Override
    public boolean existe(Usuario usuario, String idGoogleBook) {
        final Session session = sessionFactory.getCurrentSession();

        Query<Long> query = session.createQuery(
                "SELECT count(lf.id) FROM LibroFavorito lf WHERE lf.usuario = :usuario AND lf.idGoogleBook = :idGoogleBook",
                Long.class
        );

        query.setParameter("usuario", usuario);
        query.setParameter("idGoogleBook", idGoogleBook);

        return query.getSingleResult() > 0;
    }

    @Override
    public List<String> obtenerIdsPorUsuario(Usuario usuario) {
        final Session session = sessionFactory.getCurrentSession();
        Query<String> query = session.createQuery(
                "SELECT lf.idGoogleBook FROM LibroFavorito lf WHERE lf.usuario = :usuario",
                String.class
        );
        query.setParameter("usuario", usuario);
        return query.getResultList();
    }

    @Override
    public void eliminar(Usuario usuario, String idGoogleBook) {
        final Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "DELETE FROM LibroFavorito lf WHERE lf.usuario = :usuario AND lf.idGoogleBook = :idGoogleBook"
        );
        query.setParameter("usuario", usuario);
        query.setParameter("idGoogleBook", idGoogleBook);
        query.executeUpdate();

    }
}
