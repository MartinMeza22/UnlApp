package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RepositorioResumenInteligente;
import com.tallerwebi.dominio.ResumenInteligente;
import com.tallerwebi.dominio.Usuario;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class RepositorioResumenInteligenteImpl implements RepositorioResumenInteligente {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void guardar(ResumenInteligente resumen) {
        sessionFactory.getCurrentSession().save(resumen);
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<ResumenInteligente> obtenerPorUsuario(Usuario usuario) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM ResumenInteligente WHERE usuario = :usuario ORDER BY fechaGeneracion ASC", ResumenInteligente.class)
                .setParameter("usuario", usuario)
                .getResultList();
    }

}
