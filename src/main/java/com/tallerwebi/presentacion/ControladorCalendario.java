package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ControladorCalendario {

    private ServicioEvento servicioEvento;
    private ServicioMateria servicioMateria;
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    public ControladorCalendario(ServicioEvento servicioEvento, ServicioMateria servicioMateria, RepositorioUsuario repositorioUsuario) {
        this.servicioEvento = servicioEvento;
        this.servicioMateria = servicioMateria;
        this.repositorioUsuario = repositorioUsuario;
    }

    // Página principal del calendario
    @RequestMapping(path = "/calendario", method = RequestMethod.GET)
    public ModelAndView irACalendario(HttpServletRequest request) {
        ModelMap modelo = new ModelMap();
        
        Long usuarioId = (Long) request.getSession().getAttribute("ID");
        if (usuarioId == null) {
            return new ModelAndView("redirect:/login");
        }

        // Obtener datos para la vista
        List<Evento> eventosHoy = servicioEvento.obtenerEventosHoy(usuarioId);
        List<Evento> proximosEventos = servicioEvento.obtenerProximosEventos(usuarioId, 5);
        ServicioEvento.ResumenEventos resumen = servicioEvento.obtenerResumenHoy(usuarioId);
        
        // Obtener materias para el dropdown
        List<Materia> materias = servicioMateria.obtenerTodasLasMaterias();
        
        modelo.put("eventosHoy", eventosHoy);
        modelo.put("proximosEventos", proximosEventos);
        modelo.put("resumen", resumen);
        modelo.put("materias", materias);
        
        return new ModelAndView("calendario", modelo);
    }

    // Crear nuevo evento
    @RequestMapping(path = "/evento/crear", method = RequestMethod.POST)
    public ModelAndView crearEvento(@RequestParam("titulo") String titulo,
                                   @RequestParam("tipo") String tipo,
                                   @RequestParam("fechaInicio") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime fechaInicio,
                                   @RequestParam(value = "fechaFin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime fechaFin,
                                   @RequestParam(value = "materiaId", required = false) Long materiaId,
                                   @RequestParam(value = "ubicacion", required = false) String ubicacion,
                                   @RequestParam(value = "descripcion", required = false) String descripcion,

                                   HttpServletRequest request) {
        ModelMap modelo = new ModelMap();
        
        Long usuarioId = (Long) request.getSession().getAttribute("ID");
        if (usuarioId == null) {
            return new ModelAndView("redirect:/login");
        }

        try {
            // Debug logging
            System.out.println("Creando evento:");
            System.out.println("- Titulo: " + titulo);
            System.out.println("- Tipo: " + tipo);
            System.out.println("- FechaInicio: " + fechaInicio);
            System.out.println("- FechaFin: " + fechaFin);
            System.out.println("- MateriaId: " + materiaId);
            System.out.println("- UsuarioId: " + usuarioId);
            
            // Crear el evento usando el servicio
            if (materiaId != null && materiaId > 0) {
                // Evento académico
                if ("EXAMEN".equals(tipo)) {
                    servicioEvento.crearExamen(titulo, fechaInicio, usuarioId, materiaId);
                } else if ("TAREA".equals(tipo)) {
                    servicioEvento.crearTarea(titulo, fechaInicio, usuarioId, materiaId, descripcion);
                } else {
                    servicioEvento.crearSesionEstudio(titulo, fechaInicio, fechaFin, usuarioId, materiaId);
                }
            } else {
                // Evento personal
                System.out.println("Creando evento personal");
                servicioEvento.crearEventoPersonal(titulo, fechaInicio, usuarioId, tipo);
            }
            
            System.out.println("✅ Evento creado exitosamente");
            modelo.put("mensaje", "Evento creado exitosamente");
        } catch (Exception e) {
            System.out.println("❌ Error al crear evento: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback: crear evento básico directamente
            try {
                System.out.println("Intentando crear evento básico...");
                Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);
                if (usuario != null) {
                    Evento eventoBasico = new Evento(titulo, fechaInicio, usuario);
                    eventoBasico.setTipo(tipo);
                    if (descripcion != null) eventoBasico.setDescripcion(descripcion);
                    if (ubicacion != null) eventoBasico.setUbicacion(ubicacion);
                    
                    servicioEvento.crearEvento(eventoBasico);
                    System.out.println("✅ Evento básico creado exitosamente");
                } else {
                    modelo.put("error", "Usuario no encontrado");
                }
            } catch (Exception e2) {
                System.out.println("Error en fallback: " + e2.getMessage());
                modelo.put("error", "Error al crear el evento: " + e.getMessage());
            }
        }
        
        return new ModelAndView("redirect:/calendario");
    }

    // Actualizar evento existente
    @RequestMapping(path = "/evento/{id}/actualizar", method = RequestMethod.POST)
    public ModelAndView actualizarEvento(@PathVariable Long id,
                                         @RequestParam("titulo") String titulo,
                                         @RequestParam("tipo") String tipo,
                                         @RequestParam("fechaInicio") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime fechaInicio,
                                         @RequestParam(value = "fechaFin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime fechaFin,
                                         @RequestParam(value = "materiaId", required = false) Long materiaId,
                                         @RequestParam(value = "ubicacion", required = false) String ubicacion,
                                         @RequestParam(value = "descripcion", required = false) String descripcion,
                                         HttpServletRequest request) {
        
        Long usuarioId = (Long) request.getSession().getAttribute("ID");
        if (usuarioId == null) {
            return new ModelAndView("redirect:/login");
        }

        try {
            // Obtener el evento existente
            Evento evento = servicioEvento.obtenerEventoPorId(id);
            if (evento == null || !evento.getUsuario().getId().equals(usuarioId)) {
                return new ModelAndView("redirect:/calendario");
            }
            
            // Actualizar los campos del evento
            evento.setTitulo(titulo);
            evento.setTipo(tipo);
            evento.setFechaInicio(fechaInicio);
            evento.setFechaFin(fechaFin);
            evento.setUbicacion(ubicacion);
            evento.setDescripcion(descripcion);
            
            // Actualizar la materia si es necesario
            if (materiaId != null && materiaId > 0) {
                Materia materia = servicioMateria.buscarMateriaPorId(materiaId);
                evento.setMateria(materia);
            } else {
                evento.setMateria(null);
            }
            
            servicioEvento.actualizarEvento(evento);
            
            System.out.println("✅ Evento actualizado exitosamente: " + titulo);
            
        } catch (Exception e) {
            System.out.println("❌ Error al actualizar evento: " + e.getMessage());
            e.printStackTrace();
        }
        
        return new ModelAndView("redirect:/calendario");
    }

    // Formulario para crear evento
    @RequestMapping(path = "/evento/nuevo", method = RequestMethod.GET)
    public ModelAndView formularioNuevoEvento(HttpServletRequest request) {
        Long usuarioId = (Long) request.getSession().getAttribute("ID");
        if (usuarioId == null) {
            return new ModelAndView("redirect:/login");
        }

        ModelMap modelo = new ModelMap();
        modelo.put("evento", new Evento());
        
        // Obtener materias del usuario para el dropdown
        try {
            List<Materia> materias = servicioMateria.obtenerTodasLasMaterias();
            modelo.put("materias", materias);
        } catch (Exception e) {
            modelo.put("materias", List.of());
        }
        
        return new ModelAndView("nuevo-evento", modelo);
    }

    // Ver detalle de evento
    @RequestMapping(path = "/evento/{id}", method = RequestMethod.GET)
    public ModelAndView verEvento(@PathVariable Long id, HttpServletRequest request) {
        Long usuarioId = (Long) request.getSession().getAttribute("ID");
        if (usuarioId == null) {
            return new ModelAndView("redirect:/login");
        }

        ModelMap modelo = new ModelMap();
        Evento evento = servicioEvento.obtenerEventoPorId(id);
        
        if (evento == null || !evento.getUsuario().getId().equals(usuarioId)) {
            modelo.put("error", "Evento no encontrado");
            return new ModelAndView("redirect:/calendario");
        }
        
        modelo.put("evento", evento);
        return new ModelAndView("detalle-evento", modelo);
    }

    // Marcar evento como completado
    @RequestMapping(path = "/evento/{id}/completar", method = RequestMethod.POST)
    public ModelAndView completarEvento(@PathVariable Long id, HttpServletRequest request) {
        Long usuarioId = (Long) request.getSession().getAttribute("ID");
        if (usuarioId == null) {
            return new ModelAndView("redirect:/login");
        }

        try {
            Evento evento = servicioEvento.obtenerEventoPorId(id);
            if (evento != null && evento.getUsuario().getId().equals(usuarioId)) {
                servicioEvento.marcarComoCompletado(id);
            }
        } catch (Exception e) {
            // Log error
        }
        
        return new ModelAndView("redirect:/calendario");
    }

    // Eliminar evento
    @RequestMapping(path = "/evento/{id}/eliminar", method = RequestMethod.POST)
    public ModelAndView eliminarEvento(@PathVariable Long id, HttpServletRequest request) {
        Long usuarioId = (Long) request.getSession().getAttribute("ID");
        if (usuarioId == null) {
            return new ModelAndView("redirect:/login");
        }

        try {
            Evento evento = servicioEvento.obtenerEventoPorId(id);
            if (evento != null && evento.getUsuario().getId().equals(usuarioId)) {
                servicioEvento.eliminarEvento(id);
            }
        } catch (Exception e) {
            // Log error
        }
        
        return new ModelAndView("redirect:/calendario");
    }

    // API endpoint para obtener eventos en formato JSON (para el calendario JavaScript)
    @RequestMapping(path = "/api/eventos", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<Evento>> obtenerEventosJson(HttpServletRequest request,
                                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                                           @RequestParam(required = false) Integer mes,
                                                           @RequestParam(required = false) Integer año) {
        Long usuarioId = (Long) request.getSession().getAttribute("ID");
        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<Evento> eventos;
        
        if (fecha != null) {
            eventos = servicioEvento.obtenerEventosPorFecha(usuarioId, fecha);
        } else if (mes != null && año != null) {
            eventos = servicioEvento.obtenerEventosMes(usuarioId, mes, año);
        } else {
            eventos = servicioEvento.obtenerEventosDeUsuario(usuarioId);
        }
        
        return ResponseEntity.ok(eventos);
    }

    // API endpoint para crear evento via AJAX
    @RequestMapping(path = "/api/evento", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> crearEventoAjax(@RequestBody EventoRequest eventoRequest, 
                                                  HttpServletRequest request) {
        Long usuarioId = (Long) request.getSession().getAttribute("ID");
        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        try {
            if (eventoRequest.getMateriaId() != null) {
                // Evento académico
                if ("EXAMEN".equals(eventoRequest.getTipo())) {
                    servicioEvento.crearExamen(eventoRequest.getTitulo(), 
                                             eventoRequest.getFechaInicio(), 
                                             usuarioId, 
                                             eventoRequest.getMateriaId());
                } else if ("TAREA".equals(eventoRequest.getTipo())) {
                    servicioEvento.crearTarea(eventoRequest.getTitulo(), 
                                            eventoRequest.getFechaInicio(), 
                                            usuarioId, 
                                            eventoRequest.getMateriaId(), 
                                            eventoRequest.getDescripcion());
                } else {
                    servicioEvento.crearSesionEstudio(eventoRequest.getTitulo(), 
                                                    eventoRequest.getFechaInicio(), 
                                                    eventoRequest.getFechaFin(), 
                                                    usuarioId, 
                                                    eventoRequest.getMateriaId());
                }
            } else {
                // Evento personal
                servicioEvento.crearEventoPersonal(eventoRequest.getTitulo(), 
                                                 eventoRequest.getFechaInicio(), 
                                                 usuarioId, 
                                                 eventoRequest.getTipo());
            }
            
            return ResponseEntity.ok("Evento creado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Filtros
    @RequestMapping(path = "/eventos/academicos", method = RequestMethod.GET)
    public ModelAndView eventosAcademicos(HttpServletRequest request) {
        Long usuarioId = (Long) request.getSession().getAttribute("ID");
        if (usuarioId == null) {
            return new ModelAndView("redirect:/login");
        }

        ModelMap modelo = new ModelMap();
        List<Evento> eventos = servicioEvento.obtenerEventosAcademicos(usuarioId);
        modelo.put("eventos", eventos);
        modelo.put("tipoFiltro", "Académicos");
        
        return new ModelAndView("lista-eventos", modelo);
    }

    @RequestMapping(path = "/eventos/personales", method = RequestMethod.GET)
    public ModelAndView eventosPersonales(HttpServletRequest request) {
        Long usuarioId = (Long) request.getSession().getAttribute("ID");
        if (usuarioId == null) {
            return new ModelAndView("redirect:/login");
        }

        ModelMap modelo = new ModelMap();
        List<Evento> eventos = servicioEvento.obtenerEventosPersonales(usuarioId);
        modelo.put("eventos", eventos);
        modelo.put("tipoFiltro", "Personales");
        
        return new ModelAndView("lista-eventos", modelo);
    }

    // Clase para recibir datos JSON
    public static class EventoRequest {
        private String titulo;
        private String descripcion;
        private LocalDateTime fechaInicio;
        private LocalDateTime fechaFin;
        private String tipo;
        private Long materiaId;

        // Getters and setters
        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

        public LocalDateTime getFechaInicio() { return fechaInicio; }
        public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

        public LocalDateTime getFechaFin() { return fechaFin; }
        public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }

        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }

        public Long getMateriaId() { return materiaId; }
        public void setMateriaId(Long materiaId) { this.materiaId = materiaId; }
    }
}
