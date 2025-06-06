package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Materia;
import com.tallerwebi.dominio.RepositorioMateria;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("repositorioMateria")
public class RepositorioMateriaImpl implements RepositorioMateria {

    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioMateriaImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void guardar(Materia materia) {
        sessionFactory.getCurrentSession().save(materia);
    }

    @Override
    public Materia buscarPorId(Long id) {
        return sessionFactory.getCurrentSession().get(Materia.class, id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Materia> buscarTodas() {
        final String hql = "FROM Materia ORDER BY cuatrimestre, nombre";
        return sessionFactory.getCurrentSession()
                .createQuery(hql)
                .list();
    }

    @Override
    public void actualizar(Materia materia) {
        sessionFactory.getCurrentSession().update(materia);
    }

    @Override
    public void eliminar(Materia materia) {
        sessionFactory.getCurrentSession().delete(materia);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Materia> obtenerTodasLasMateriasPorNombre(){
        final String hql = "FROM Materia ORDER BY nombre";
                return sessionFactory.getCurrentSession().createQuery(hql).list();
    }
}