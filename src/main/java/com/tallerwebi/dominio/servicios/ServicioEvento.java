package com.tallerwebi.dominio.servicios;

import com.tallerwebi.dominio.Evento;
import com.tallerwebi.dominio.Materia;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.repositorioInterfaz.RepositorioEvento;
import com.tallerwebi.repositorioInterfaz.RepositorioMateria;
import com.tallerwebi.repositorioInterfaz.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service("servicioEvento")
@Transactional
public class ServicioEvento {

    private RepositorioEvento repositorioEvento;
    private RepositorioUsuario repositorioUsuario;
    private RepositorioMateria repositorioMateria;

    @Autowired
    public ServicioEvento(RepositorioEvento repositorioEvento, 
                         RepositorioUsuario repositorioUsuario,
                         RepositorioMateria repositorioMateria) {
        this.repositorioEvento = repositorioEvento;
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioMateria = repositorioMateria;
    }

    // CRUD operations
    public void crearEvento(Evento evento) {
        validarEvento(evento);
        repositorioEvento.guardar(evento);
    }

    public Evento obtenerEventoPorId(Long id) {
        return repositorioEvento.buscarPorId(id);
    }

    public void actualizarEvento(Evento evento) {
        validarEvento(evento);
        repositorioEvento.actualizar(evento);
    }

    public void eliminarEvento(Long id) {
        repositorioEvento.eliminar(id);
    }

    // Crear eventos específicos
    public void crearExamen(String titulo, LocalDateTime fecha, Long usuarioId, Long materiaId, Boolean notificarRecordatorio) {
        Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId);
        }
        
        Materia materia = repositorioMateria.buscarPorId(materiaId);
        if (materia == null) {
            throw new IllegalArgumentException("Materia no encontrada con ID: " + materiaId);
        }
        
        Evento examen = new Evento(titulo, fecha, usuario, materia);
        examen.setTipo("EXAMEN");
        examen.setDescripcion("Examen de " + materia.getNombre());
        examen.setNotificarRecordatorio(notificarRecordatorio);
        crearEvento(examen);
    }

    public void crearTarea(String titulo, LocalDateTime fechaEntrega, Long usuarioId, Long materiaId, String descripcion, Boolean notificarRecordatorio) {
        Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId);
        }
        
        Materia materia = repositorioMateria.buscarPorId(materiaId);
        if (materia == null) {
            throw new IllegalArgumentException("Materia no encontrada con ID: " + materiaId);
        }
        
        Evento tarea = new Evento(titulo, fechaEntrega, usuario, materia);
        tarea.setTipo("TAREA");
        tarea.setDescripcion(descripcion);
        tarea.setNotificarRecordatorio(notificarRecordatorio);
        crearEvento(tarea);
    }

    public void crearSesionEstudio(String titulo, LocalDateTime fechaInicio, LocalDateTime fechaFin, Long usuarioId, Long materiaId, Boolean notificarRecordatorio) {
        Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId);
        }
        
        Materia materia = repositorioMateria.buscarPorId(materiaId);
        if (materia == null) {
            throw new IllegalArgumentException("Materia no encontrada con ID: " + materiaId);
        }
        
        Evento sesion = new Evento(titulo, fechaInicio, usuario, materia);
        sesion.setTipo("ESTUDIO");
        sesion.setFechaFin(fechaFin);
        sesion.setDescripcion("Sesión de estudio para " + materia.getNombre());
        sesion.setNotificarRecordatorio(notificarRecordatorio);
        crearEvento(sesion);
    }

    public Evento crearEventoPersonal(String titulo, LocalDateTime fecha, Long usuarioId, String tipo, Boolean notificarRecordatorio) {
        Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId);
        }
        
        Evento evento = new Evento(titulo, fecha, usuario);
        evento.setTipo(tipo != null ? tipo : "PERSONAL");
        evento.setNotificarRecordatorio(notificarRecordatorio);
        crearEvento(evento);
        return evento;
    }

    // Búsquedas por fecha
    public List<Evento> obtenerEventosHoy(Long usuarioId) {
        return repositorioEvento.buscarEventosHoy(usuarioId);
    }

    // Próximos eventos
    public List<Evento> obtenerProximosEventos(Long usuarioId, int cantidad) {
        return repositorioEvento.buscarProximosEventos(usuarioId, cantidad);
    }

    // Gestión de estado
    public void marcarComoCompletado(Long eventoId) {
        Evento evento = repositorioEvento.buscarPorId(eventoId);
        if (evento != null) {
            evento.marcarComoCompletado();
            repositorioEvento.actualizar(evento);
        }
    }

    // Resumen del día
    public ResumenEventos obtenerResumenHoy(Long usuarioId) {
        List<Evento> eventosHoy = obtenerEventosHoy(usuarioId);
        
        ResumenEventos resumen = new ResumenEventos();
        resumen.setEventosHoy(eventosHoy);
        resumen.setTotalEventos(eventosHoy.size());
        resumen.setEventosCompletados((int) eventosHoy.stream().filter(Evento::getCompletado).count());
        resumen.setEventosPendientes(resumen.getTotalEventos() - resumen.getEventosCompletados());
        
        return resumen;
    }

    // Validaciones
    private void validarEvento(Evento evento) {
        if (evento.getTitulo() == null || evento.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título del evento es obligatorio");
        }
        
        if (evento.getFechaInicio() == null) {
            throw new IllegalArgumentException("La fecha de inicio es obligatoria");
        }
        
        if (evento.getUsuario() == null) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }
        
        if (evento.getFechaFin() != null && evento.getFechaFin().isBefore(evento.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
    }

    // Clase interna para resumen
    public static class ResumenEventos {
        private List<Evento> eventosHoy;
        private int totalEventos;
        private int eventosCompletados;
        private int eventosPendientes;

        // Getters and setters
        public List<Evento> getEventosHoy() { return eventosHoy; }
        public void setEventosHoy(List<Evento> eventosHoy) { this.eventosHoy = eventosHoy; }

        public int getTotalEventos() { return totalEventos; }
        public void setTotalEventos(int totalEventos) { this.totalEventos = totalEventos; }

        public int getEventosCompletados() { return eventosCompletados; }
        public void setEventosCompletados(int eventosCompletados) { this.eventosCompletados = eventosCompletados; }

        public int getEventosPendientes() { return eventosPendientes; }
        public void setEventosPendientes(int eventosPendientes) { this.eventosPendientes = eventosPendientes; }
    }
}
