package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Carrera;
import com.tallerwebi.dominio.RepositorioCarrera;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository("repositorioCarrera")
public class RepositorioCarreraImpl implements RepositorioCarrera {


    private final SessionFactory sessionFactory;

    @Autowired
    public RepositorioCarreraImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Carrera> getCarreras() {

        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("FROM Carrera", Carrera.class).list();
    }
}
