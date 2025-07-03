package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Evento;
import com.tallerwebi.dominio.Materia;
import com.tallerwebi.repositorioInterfaz.RepositorioEvento;
import com.tallerwebi.dominio.Usuario;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository("repositorioEvento")
public class RepositorioEventoImpl implements RepositorioEvento {

    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioEventoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void guardar(Evento evento) {
        sessionFactory.getCurrentSession().save(evento);
    }

    @Override
    public Evento buscarPorId(Long id) {
        return sessionFactory.getCurrentSession().get(Evento.class, id);
    }

    @Override
    public void actualizar(Evento evento) {
        sessionFactory.getCurrentSession().update(evento);
    }

    @Override
    public void eliminar(Long id) {
        Evento evento = buscarPorId(id);
        if (evento != null) {
            sessionFactory.getCurrentSession().delete(evento);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Evento> buscarPorUsuario(Usuario usuario) {
        return sessionFactory.getCurrentSession()
                .createCriteria(Evento.class)
                .add(Restrictions.eq("usuario", usuario))
                .add(Restrictions.eq("activo", true))
                .list();
    }


    @Override
    @SuppressWarnings("unchecked")
    public List<Evento> buscarPorFecha(Long usuarioId, LocalDate fecha) {
        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.atTime(23, 59, 59);
        
        return sessionFactory.getCurrentSession()
                .createQuery("SELECT DISTINCT e FROM Evento e LEFT JOIN FETCH e.materia WHERE e.usuario.id = :usuarioId AND e.fechaInicio >= :inicio AND e.fechaInicio <= :fin AND e.activo = true")
                .setParameter("usuarioId", usuarioId)
                .setParameter("inicio", inicioDia)
                .setParameter("fin", finDia)
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Evento> buscarPorRangoFechas(Long usuarioId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return sessionFactory.getCurrentSession()
                .createCriteria(Evento.class)
                .add(Restrictions.eq("usuario.id", usuarioId))
                .add(Restrictions.ge("fechaInicio", fechaInicio))
                .add(Restrictions.le("fechaInicio", fechaFin))
                .add(Restrictions.eq("activo", true))
                .list();
    }

    @Override
    public List<Evento> buscarEventosHoy(Long usuarioId) {
        return buscarPorFecha(usuarioId, LocalDate.now());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Evento> buscarProximosEventos(Long usuarioId, int cantidad) {
        return sessionFactory.getCurrentSession()
                .createQuery("SELECT DISTINCT e FROM Evento e LEFT JOIN FETCH e.materia WHERE e.usuario.id = :usuarioId AND e.fechaInicio >= :ahora AND e.activo = true ORDER BY e.fechaInicio ASC")
                .setParameter("usuarioId", usuarioId)
                .setParameter("ahora", LocalDateTime.now())
                .setMaxResults(cantidad)
                .list();
    }

    @Override
    public List<Evento> buscarEventosParaNotificar(LocalDateTime minFechaInicio, LocalDateTime maxFechaInicio) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Evento e WHERE e.notificarRecordatorio = true " +
                        "AND e.fechaInicio BETWEEN :minFechaInicio AND :maxFechaInicio " +
                        "AND e.yaNotificado = false", Evento.class)
                .setParameter("minFechaInicio", minFechaInicio)
                .setParameter("maxFechaInicio", maxFechaInicio)
                .getResultList();
    }
}
