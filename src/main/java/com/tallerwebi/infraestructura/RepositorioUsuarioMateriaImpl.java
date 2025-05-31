package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RepositorioUsuarioMateria;
import com.tallerwebi.dominio.UsuarioMateria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("repositorioUsuarioMateria")
public class RepositorioUsuarioMateriaImpl implements RepositorioUsuarioMateria {

    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioUsuarioMateriaImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void guardar(UsuarioMateria usuarioMateria) {
        sessionFactory.getCurrentSession().save(usuarioMateria);
    }

    @Override
    public UsuarioMateria buscarPorId(Long id) {
        return sessionFactory.getCurrentSession().get(UsuarioMateria.class, id);
    }

    @Override
    public UsuarioMateria buscarPorUsuarioYMateria(Long usuarioId, Long materiaId) {
        return (UsuarioMateria) sessionFactory.getCurrentSession()
                .createCriteria(UsuarioMateria.class)
                .createAlias("usuario", "u")
                .createAlias("materia", "m")
                .add(Restrictions.eq("u.id", usuarioId))
                .add(Restrictions.eq("m.id", materiaId))
                .uniqueResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UsuarioMateria> buscarPorUsuario(Long usuarioId) {
        return sessionFactory.getCurrentSession()
                .createCriteria(UsuarioMateria.class)
                .createAlias("usuario", "u")
                .add(Restrictions.eq("u.id", usuarioId))
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UsuarioMateria> buscarTodas() {
        final String hql = "FROM UsuarioMateria um " +
                          "JOIN FETCH um.usuario u " +
                          "JOIN FETCH um.materia m " +
                          "ORDER BY u.email, m.nombre";
        return sessionFactory.getCurrentSession()
                .createQuery(hql)
                .list();
    }

    @Override
    public void actualizar(UsuarioMateria usuarioMateria) {
        sessionFactory.getCurrentSession().update(usuarioMateria);
    }

    @Override
    public void eliminar(UsuarioMateria usuarioMateria) {
        sessionFactory.getCurrentSession().delete(usuarioMateria);
    }

    @Override
    public boolean existe(Long usuarioId, Long materiaId) {
        UsuarioMateria resultado = buscarPorUsuarioYMateria(usuarioId, materiaId);
        return resultado != null;
    }
}