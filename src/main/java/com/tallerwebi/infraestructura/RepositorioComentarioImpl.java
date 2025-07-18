package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Comentario;
import com.tallerwebi.repositorioInterfaz.RepositorioComentario;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository("repositorioComentario")
@Transactional
public class RepositorioComentarioImpl implements RepositorioComentario {
    private final SessionFactory sessionFactory;

    @Autowired
    public RepositorioComentarioImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void guardar(Comentario comentario) {
        sessionFactory.getCurrentSession().save(comentario);
    }
    @Override
    public Comentario buscarPorId(Long id) {
        return sessionFactory.getCurrentSession().get(Comentario.class, id);
    }
    @Override
    public void eliminar(Comentario comentario) {
        sessionFactory.getCurrentSession().delete(comentario);
    }
}