package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Carrera;
import com.tallerwebi.dominio.RepositorioCarrera;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RepositorioCarreraImpl implements RepositorioCarrera {

    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioCarreraImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void guardar(Carrera carrera) {
        sessionFactory.getCurrentSession().save(carrera);
    }

    @Override
    public Carrera buscarCarreraPorIds(Long id) {
        return sessionFactory.getCurrentSession().get(Carrera.class, id);
    }

    @Override
    public List<Carrera> obtenerTodasLasCarreras() {
        String hql = "FROM Carrera ORDER BY id DESC";
        return sessionFactory.getCurrentSession()
                .createQuery(hql)
                .list();
    }

    @Override
    public void actualizarCarrera(Carrera carrera) {
        sessionFactory.getCurrentSession().update(carrera);
    }

    @Override
    public void eliminarUnaCarrera(Long id) {
        sessionFactory.getCurrentSession().delete(buscarCarreraPorIds(id));
    }
}
