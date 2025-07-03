package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Carrera;
import com.tallerwebi.dominio.Materia;
import com.tallerwebi.repositorioInterfaz.RepositorioMateria;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
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

    public List<Materia> buscarPorCarrera(Carrera carrera) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Materia.class);
        criteria.add(Restrictions.eq("carrera", carrera));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<Materia> obtenerTodasLasMateriasPorNombre() {
        final String hql = "FROM Materia ORDER BY nombre";
        return sessionFactory.getCurrentSession().createQuery(hql).list();

    }

    @Override
    public List<Materia> obtenerMateriasDeUnaCarrera(String idCarrera) {
        final String hql = "FROM Materia WHERE carrera.id = :idCarrera ORDER BY cuatrimestre, nombre";
        return sessionFactory.getCurrentSession()
                .createQuery(hql)
                .setParameter("idCarrera", Long.valueOf(idCarrera))
                .list();
    }

    @SuppressWarnings("unchecked")
    public List<Integer> obtenerCantidadDeCuatrimestres() {
        final String hql = "SELECT DISTINCT m.cuatrimestre FROM Materia m ORDER BY m.cuatrimestre";
        return sessionFactory.getCurrentSession().createQuery(hql).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Long obtenerCantidadDeMateriasDeLaCarrera() {
        final String hql = "SELECT COUNT(m.id) FROM Materia m";
        return (Long) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
    }

    @Override
    public List<Materia> buscarMateriasQueTienenComoCorrelativas(Long idMateria) {
        String idMateriaStr = String.valueOf(idMateria);
        String query = "FROM Materia m WHERE m.correlativa1 = :id OR m.correlativa2 = :id OR " +
                "m.correlativa3 = :id OR m.correlativa4 = :id OR " +
                "m.correlativa5 = :id OR m.correlativa6 = :id";
        return this.sessionFactory.getCurrentSession()
                .createQuery(query, Materia.class)
                .setParameter("id", idMateriaStr)
                .getResultList();
    }

}