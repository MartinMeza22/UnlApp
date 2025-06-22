package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RepositorioUsuarioMateria;
import com.tallerwebi.dominio.UsuarioMateria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("repositorioUsuarioMateria")
public class RepositorioUsuarioMateriaImpl implements RepositorioUsuarioMateria { //Implementamos la interfaz

    private SessionFactory sessionFactory; //Declaramos la session (para conectar la BDD)

    @Autowired
    public RepositorioUsuarioMateriaImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;//Asignamos la conexión de BDD (la trae Spring)
    }

    @Override
    public void guardar(UsuarioMateria usuarioMateria) {
        sessionFactory.getCurrentSession().save(usuarioMateria);//Guarda el usuario en la BDD
    }

    @Override
    public UsuarioMateria buscarPorId(Long id) {
        return sessionFactory.getCurrentSession().get(UsuarioMateria.class, id);
    }

    @Override
    public UsuarioMateria buscarPorUsuarioYMateria(Long usuarioId, Long materiaId) { //Utilización de metodos para hacer consultas SQL
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
        final String hql = "FROM UsuarioMateria um " + //HQL es una consulta con Hibernate
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
    //Actualiza una fila existente en la base con nuevos datos

    @Override
    public void eliminar(UsuarioMateria usuarioMateria) {
        sessionFactory.getCurrentSession().delete(usuarioMateria);
    }
    //Elimina una fila existente de la tabla usuario_materia

    @Override
    public boolean existe(Long usuarioId, Long materiaId) {
        UsuarioMateria resultado = buscarPorUsuarioYMateria(usuarioId, materiaId); //Reutilización de otro metodo
        return resultado != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public UsuarioMateria buscarProgresoPersonal(Long usuarioId, Long materiaId) {
        final String hql = "SELECT um.dificultad, um.estado, um.nota " +
                "FROM UsuarioMateria um " +
                "WHERE um.usuario.id = :usuarioId AND um.materia.id = :materiaId";

        return (UsuarioMateria) (sessionFactory.getCurrentSession()
                .createQuery(hql));
    }

    public Long contadorDeMateriasPorUsuario(Long usuarioId) {
        final String hql = "SELECT COUNT(um.materia) " +
                "FROM UsuarioMateria um " +
                "WHERE um.usuario.id = :usuarioId";

        return (Long) sessionFactory.getCurrentSession()
                .createQuery(hql)
                .setParameter("usuarioId", usuarioId)
                .uniqueResult();
    }

    public Long contadorDeMateriasAprobadasPorUsuario(Long usuarioId) {
        final String hql = "SELECT COUNT(um.materia) " +
                "FROM UsuarioMateria um " +
                "WHERE um.usuario.id = :usuarioId AND um.estado = 3";

        return (Long) sessionFactory.getCurrentSession()
                .createQuery(hql)
                .setParameter("usuarioId", usuarioId)
                .uniqueResult();
    }

    public Long contadorDeMateriasDesaprobadasPorUsuario(Long usuarioId) {
        final String hql = "SELECT COUNT(um.materia) " +
                "FROM UsuarioMateria um " +
                "WHERE um.usuario.id = :usuarioId AND um.estado = 4";

        return (Long) sessionFactory.getCurrentSession()
                .createQuery(hql)
                .setParameter("usuarioId", usuarioId)
                .uniqueResult();
    }

    public Long contadorDeMateriasCursandoPorUsuario(Long usuarioId) {
        final String hql = "SELECT COUNT(um.materia) " +
                "FROM UsuarioMateria um " +
                "WHERE um.usuario.id = :usuarioId AND um.estado = 2";

        return (Long) sessionFactory.getCurrentSession()
                .createQuery(hql)
                .setParameter("usuarioId", usuarioId)
                .uniqueResult();
    }

}