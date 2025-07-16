package com.tallerwebi.infraestructura;

import com.tallerwebi.repositorioInterfaz.RepositorioAnalitico;
import com.tallerwebi.dominio.UsuarioMateria;
import com.tallerwebi.dominio.Usuario;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("repositorioAnalitico")
public class RepositorioAnaliticoImpl implements RepositorioAnalitico {

    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioAnaliticoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UsuarioMateria> obtenerDatosCompletosParaAnalitico(Long usuarioId) {
        final String hql = "FROM UsuarioMateria um " +
                "JOIN FETCH um.usuario u " +
                "JOIN FETCH u.carrera c " +
                "JOIN FETCH um.materia m " +
                "WHERE um.usuario.id = :usuarioId " +
                "ORDER BY m.cuatrimestre ASC, m.nombre ASC";

        return sessionFactory.getCurrentSession()
                .createQuery(hql)
                .setParameter("usuarioId", usuarioId)
                .list();
    }

    @Override
    public Usuario obtenerUsuarioConCarrera(Long usuarioId) {
        final String hql = "FROM Usuario u " +
                "JOIN FETCH u.carrera c " +
                "WHERE u.id = :usuarioId";

        return (Usuario) sessionFactory.getCurrentSession()
                .createQuery(hql)
                .setParameter("usuarioId", usuarioId)
                .uniqueResult();
    }

    @Override
    public Long contarTotalMateriasDeCarrera(Long usuarioId) {
        final String hql = "SELECT COUNT(m.id) " +
                "FROM Materia m, Usuario u " +
                "WHERE u.id = :usuarioId " +
                "AND m.carrera.id = u.carrera.id " +
                "AND m.activa = true";

        return (Long) sessionFactory.getCurrentSession()
                .createQuery(hql)
                .setParameter("usuarioId", usuarioId)
                .uniqueResult();
    }

    @Override
    public Object[] obtenerEstadisticasGenerales(Long usuarioId) {
        final String hql = "SELECT " +
                "COUNT(um.id) as totalCursadas, " +
                "COUNT(CASE WHEN um.estado = 3 THEN 1 END) as aprobadas, " +
                "COUNT(CASE WHEN um.estado = 4 THEN 1 END) as desaprobadas, " +
                "COUNT(CASE WHEN um.estado = 2 THEN 1 END) as cursando, " +
                "AVG(CASE WHEN um.nota IS NOT NULL THEN um.nota END) as promedio, " +
                "SUM(CASE WHEN um.estado = 3 THEN m.cargaHoraria ELSE 0 END) as horasAprobadas " +
                "FROM UsuarioMateria um " +
                "JOIN um.materia m " +
                "WHERE um.usuario.id = :usuarioId";

        return (Object[]) sessionFactory.getCurrentSession()
                .createQuery(hql)
                .setParameter("usuarioId", usuarioId)
                .uniqueResult();
    }
}