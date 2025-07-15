package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.servicios.ResumenUsuario;
import com.tallerwebi.repositorioInterfaz.RepositorioResumenUsuario;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RepositorioResumenUsuarioImpl implements RepositorioResumenUsuario {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void guardar(ResumenUsuario resumen) {
        sessionFactory.getCurrentSession().save(resumen);
    }

    @Override
    public List<ResumenUsuario> obtenerPorUsuario(Usuario usuario) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM ResumenUsuario WHERE usuario = :usuario ORDER BY fechaGeneracion DESC", ResumenUsuario.class)
                .setParameter("usuario", usuario)
                .getResultList();
    }

    @Override
    public List<ResumenUsuario> obtenerPorUsuarioId(Long usuarioId) {
        String hql = "FROM ResumenUsuario WHERE usuario.id = :usuarioId ORDER BY id DESC";
        return sessionFactory.getCurrentSession()
                .createQuery(hql, ResumenUsuario.class)
                .setParameter("usuarioId", usuarioId)
                .getResultList();
    }

}

