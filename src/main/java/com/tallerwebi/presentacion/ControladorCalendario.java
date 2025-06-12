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

}
