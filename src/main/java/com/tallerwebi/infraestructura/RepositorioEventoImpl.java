package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Evento;
import com.tallerwebi.dominio.Materia;
import com.tallerwebi.dominio.RepositorioEvento;
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
    public List<Evento> buscarTodos() {
        return sessionFactory.getCurrentSession()
                .createCriteria(Evento.class)
                .add(Restrictions.eq("activo", true))
                .list();
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
    public List<Evento> buscarPorUsuarioId(Long usuarioId) {
        return sessionFactory.getCurrentSession()
                .createQuery("SELECT DISTINCT e FROM Evento e LEFT JOIN FETCH e.materia WHERE e.usuario.id = :usuarioId AND e.activo = true")
                .setParameter("usuarioId", usuarioId)
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Evento> buscarPorMateria(Materia materia) {
        return sessionFactory.getCurrentSession()
                .createCriteria(Evento.class)
                .add(Restrictions.eq("materia", materia))
                .add(Restrictions.eq("activo", true))
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Evento> buscarPorMateriaId(Long materiaId) {
        return sessionFactory.getCurrentSession()
                .createCriteria(Evento.class)
                .add(Restrictions.eq("materia.id", materiaId))
                .add(Restrictions.eq("activo", true))
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Evento> buscarEventosAcademicos(Long usuarioId) {
        return sessionFactory.getCurrentSession()
                .createCriteria(Evento.class)
                .add(Restrictions.eq("usuario.id", usuarioId))
                .add(Restrictions.isNotNull("materia"))
                .add(Restrictions.eq("activo", true))
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Evento> buscarEventosPersonales(Long usuarioId) {
        return sessionFactory.getCurrentSession()
                .createCriteria(Evento.class)
                .add(Restrictions.eq("usuario.id", usuarioId))
                .add(Restrictions.isNull("materia"))
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
    public List<Evento> buscarEventosSemana(Long usuarioId) {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.minusDays(hoy.getDayOfWeek().getValue() - 1);
        LocalDate finSemana = inicioSemana.plusDays(6);
        
        return buscarPorRangoFechas(usuarioId, 
                inicioSemana.atStartOfDay(), 
                finSemana.atTime(23, 59, 59));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Evento> buscarEventosMes(Long usuarioId, int mes, int año) {
        LocalDateTime inicioMes = LocalDateTime.of(año, mes, 1, 0, 0);
        LocalDateTime finMes = inicioMes.plusMonths(1).minusSeconds(1);
        
        return buscarPorRangoFechas(usuarioId, inicioMes, finMes);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Evento> buscarEventosPendientes(Long usuarioId) {
        return sessionFactory.getCurrentSession()
                .createCriteria(Evento.class)
                .add(Restrictions.eq("usuario.id", usuarioId))
                .add(Restrictions.eq("completado", false))
                .add(Restrictions.eq("activo", true))
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Evento> buscarEventosCompletados(Long usuarioId) {
        return sessionFactory.getCurrentSession()
                .createCriteria(Evento.class)
                .add(Restrictions.eq("usuario.id", usuarioId))
                .add(Restrictions.eq("completado", true))
                .add(Restrictions.eq("activo", true))
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Evento> buscarEventosVencidos(Long usuarioId) {
        return sessionFactory.getCurrentSession()
                .createCriteria(Evento.class)
                .add(Restrictions.eq("usuario.id", usuarioId))
                .add(Restrictions.lt("fechaInicio", LocalDateTime.now()))
                .add(Restrictions.eq("completado", false))
                .add(Restrictions.eq("activo", true))
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Evento> buscarPorTipo(Long usuarioId, String tipo) {
        return sessionFactory.getCurrentSession()
                .createCriteria(Evento.class)
                .add(Restrictions.eq("usuario.id", usuarioId))
                .add(Restrictions.eq("tipo", tipo))
                .add(Restrictions.eq("activo", true))
                .list();
    }

    @Override
    public List<Evento> buscarExamenes(Long usuarioId) {
        return buscarPorTipo(usuarioId, "EXAMEN");
    }

    @Override
    public List<Evento> buscarTareas(Long usuarioId) {
        return buscarPorTipo(usuarioId, "TAREA");
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Evento> buscarPorUsuarioYMateria(Long usuarioId, Long materiaId) {
        return sessionFactory.getCurrentSession()
                .createCriteria(Evento.class)
                .add(Restrictions.eq("usuario.id", usuarioId))
                .add(Restrictions.eq("materia.id", materiaId))
                .add(Restrictions.eq("activo", true))
                .list();
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
    public Long contarEventosPorUsuario(Long usuarioId) {
        return (Long) sessionFactory.getCurrentSession()
                .createCriteria(Evento.class)
                .add(Restrictions.eq("usuario.id", usuarioId))
                .add(Restrictions.eq("activo", true))
                .setProjection(org.hibernate.criterion.Projections.rowCount())
                .uniqueResult();
    }

    @Override
    public Long contarEventosCompletados(Long usuarioId) {
        return (Long) sessionFactory.getCurrentSession()
                .createCriteria(Evento.class)
                .add(Restrictions.eq("usuario.id", usuarioId))
                .add(Restrictions.eq("completado", true))
                .add(Restrictions.eq("activo", true))
                .setProjection(org.hibernate.criterion.Projections.rowCount())
                .uniqueResult();
    }

    @Override
    public Long contarEventosPendientes(Long usuarioId) {
        return (Long) sessionFactory.getCurrentSession()
                .createCriteria(Evento.class)
                .add(Restrictions.eq("usuario.id", usuarioId))
                .add(Restrictions.eq("completado", false))
                .add(Restrictions.eq("activo", true))
                .setProjection(org.hibernate.criterion.Projections.rowCount())
                .uniqueResult();
    }
}
