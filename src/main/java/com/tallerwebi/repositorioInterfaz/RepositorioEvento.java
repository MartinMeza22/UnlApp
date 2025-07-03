package com.tallerwebi.repositorioInterfaz;

import com.tallerwebi.dominio.Evento;
import com.tallerwebi.dominio.Materia;
import com.tallerwebi.dominio.Usuario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RepositorioEvento {
    
    // CRUD básico
    void guardar(Evento evento);
    Evento buscarPorId(Long id);
    void actualizar(Evento evento);
    void eliminar(Long id);

    // Búsquedas por usuario
    List<Evento> buscarPorUsuario(Usuario usuario);

    // Búsquedas por fecha
    List<Evento> buscarPorFecha(Long usuarioId, LocalDate fecha);
    List<Evento> buscarPorRangoFechas(Long usuarioId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    List<Evento> buscarEventosHoy(Long usuarioId);

    // Búsquedas combinadas
    List<Evento> buscarProximosEventos(Long usuarioId, int cantidad);

    public List<Evento> buscarEventosParaNotificar(LocalDateTime minFechaInicio, LocalDateTime maxFechaInicio);
}
