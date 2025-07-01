package com.tallerwebi.infraestructura;

import com.tallerwebi.repositorioInterfaz.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("repositorioUsuario")
public class RepositorioUsuarioImpl implements RepositorioUsuario {

    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioUsuarioImpl(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public Usuario buscarUsuario(String email, String password) {

        final Session session = sessionFactory.getCurrentSession();
        return (Usuario) session.createCriteria(Usuario.class)
                .add(Restrictions.eq("email", email))
                .add(Restrictions.eq("password", password))
                .uniqueResult();
    }

    @Override
    @Transactional
    public void guardar(Usuario usuario) {
        sessionFactory.getCurrentSession().save(usuario);
    }

    @Override
    @Transactional
    public Usuario buscar(String email) {
        return (Usuario) sessionFactory.getCurrentSession().createCriteria(Usuario.class)
                .add(Restrictions.eq("email", email))
                .uniqueResult();
    }

    @Override
    @Transactional
    public void modificar(Usuario usuario) {
        sessionFactory.getCurrentSession().update(usuario);
    }

    @Override
    @Transactional
    public Usuario buscarPorId(Long id) {
        return sessionFactory.getCurrentSession().get(Usuario.class, id);
    }
    @Override
    @Transactional
    public void eliminar(Usuario usuario) {
        Session session = sessionFactory.getCurrentSession();

        // Eliminar relaciones usuario_materia antes de eliminar al usuario
        String hqlUM = "DELETE FROM UsuarioMateria um WHERE um.usuario.id = :usuarioId";
        session.createQuery(hqlUM)
                .setParameter("usuarioId", usuario.getId())
                .executeUpdate();

        // Eliminar eventos del usuario
        String hqlEvento = "DELETE FROM Evento e WHERE e.usuario.id = :usuarioId";
        session.createQuery(hqlEvento)
                .setParameter("usuarioId", usuario.getId())
                .executeUpdate();

        // Eliminar comentarios de las publicaciones de este usuario
        String hqlComentarios = "DELETE FROM Comentario c WHERE c.publicacion.usuario.id = :usuarioId";
        session.createQuery(hqlComentarios)
                .setParameter("usuarioId", usuario.getId())
                .executeUpdate();

        // Eliminar publicaciones del usuario
        String hqlPublicaciones = "DELETE FROM Publicacion p WHERE p.usuario.id = :usuarioId";
        session.createQuery(hqlPublicaciones)
                .setParameter("usuarioId", usuario.getId())
                .executeUpdate();

        // Finalmente, eliminar al usuario
        session.delete(session.contains(usuario) ? usuario : session.merge(usuario));
    }



}
