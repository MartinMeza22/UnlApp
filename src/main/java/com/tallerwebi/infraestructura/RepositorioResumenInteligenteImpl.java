package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RepositorioResumenInteligente;
import com.tallerwebi.dominio.ResumenInteligente;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class RepositorioResumenInteligenteImpl implements RepositorioResumenInteligente {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void guardar(ResumenInteligente resumen) {
        sessionFactory.getCurrentSession().save(resumen);
    }
}
