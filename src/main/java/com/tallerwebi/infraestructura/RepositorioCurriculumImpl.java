package com.tallerwebi.infraestructura;


import com.tallerwebi.dominio.Curriculum;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.repositorioInterfaz.RepositorioCurriculum;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class RepositorioCurriculumImpl implements RepositorioCurriculum {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void guardar(Curriculum cv) {
        sessionFactory.getCurrentSession().save(cv);
    }

    @Override
    public List<Curriculum> obtenerPorUsuario(Usuario usuario) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Curriculum WHERE usuario = :usuario ORDER BY fechaGeneracion DESC", Curriculum.class)
                .setParameter("usuario", usuario)
                .getResultList();
    }
}
