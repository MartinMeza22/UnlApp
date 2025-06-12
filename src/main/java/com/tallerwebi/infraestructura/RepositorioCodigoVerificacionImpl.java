package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.CodigoVerificacion;
import com.tallerwebi.dominio.RepositorioCodigoVerificacion;
import com.tallerwebi.dominio.Usuario;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public class RepositorioCodigoVerificacionImpl  implements RepositorioCodigoVerificacion {

    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioCodigoVerificacionImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void guardar(CodigoVerificacion codigoVerificacion) {
        this.sessionFactory.getCurrentSession().save(codigoVerificacion);
    }

    @Override
    public CodigoVerificacion buscarPorUsuarioYCodigo(Usuario usuario, String codigo) {
        return (CodigoVerificacion) sessionFactory.getCurrentSession().createCriteria(CodigoVerificacion.class)
                .add(Restrictions.eq("usuario", usuario))
                .add(Restrictions.eq("codigo", codigo))
                .uniqueResult();
    }

    @Override
    public void eliminar(CodigoVerificacion codigoVerificacion) {
        sessionFactory.getCurrentSession().delete(codigoVerificacion);
    }

    @Override
    public CodigoVerificacion buscarPorUsuario(Usuario usuario) {
        return (CodigoVerificacion)  sessionFactory.getCurrentSession().createCriteria(CodigoVerificacion.class)
                .add(Restrictions.eq("usuario", usuario))
                .uniqueResult();
    }
}
