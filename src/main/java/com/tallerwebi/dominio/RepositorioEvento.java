package com.tallerwebi.dominio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RepositorioEvento {
    
    // CRUD básico
    void guardar(Evento evento);
    Evento buscarPorId(Long id);
    void actualizar(Evento evento);
    void eliminar(Long id);
    List<Evento> buscarTodos();
    
    // Búsquedas por usuario
    List<Evento> buscarPorUsuario(Usuario usuario);
    List<Evento> buscarPorUsuarioId(Long usuarioId);
    
    // Búsquedas por materia
    List<Evento> buscarPorMateria(Materia materia);
    List<Evento> buscarPorMateriaId(Long materiaId);
    List<Evento> buscarEventosAcademicos(Long usuarioId);
    List<Evento> buscarEventosPersonales(Long usuarioId);
    
    // Búsquedas por fecha
    List<Evento> buscarPorFecha(Long usuarioId, LocalDate fecha);
    List<Evento> buscarPorRangoFechas(Long usuarioId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    List<Evento> buscarEventosHoy(Long usuarioId);
    List<Evento> buscarEventosSemana(Long usuarioId);
    List<Evento> buscarEventosMes(Long usuarioId, int mes, int año);
    
    // Búsquedas por estado
    List<Evento> buscarEventosPendientes(Long usuarioId);
    List<Evento> buscarEventosCompletados(Long usuarioId);
    List<Evento> buscarEventosVencidos(Long usuarioId);
    
    // Búsquedas por tipo
    List<Evento> buscarPorTipo(Long usuarioId, String tipo);
    List<Evento> buscarExamenes(Long usuarioId);
    List<Evento> buscarTareas(Long usuarioId);
    
    // Búsquedas combinadas
    List<Evento> buscarPorUsuarioYMateria(Long usuarioId, Long materiaId);
    List<Evento> buscarProximosEventos(Long usuarioId, int cantidad);
    
    // Estadísticas
    Long contarEventosPorUsuario(Long usuarioId);
    Long contarEventosCompletados(Long usuarioId);
    Long contarEventosPendientes(Long usuarioId);

    public List<Evento> buscarEventosParaNotificar(LocalDateTime minFechaInicio, LocalDateTime maxFechaInicio);
}
