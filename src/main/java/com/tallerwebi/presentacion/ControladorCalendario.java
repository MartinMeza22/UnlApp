package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.servicios.ServicioEmail;
import com.tallerwebi.dominio.servicios.ServicioEvento;
import com.tallerwebi.dominio.servicios.ServicioMateria;
import com.tallerwebi.dominio.servicios.ServicioUsuarioMateria;
import com.tallerwebi.repositorioInterfaz.RepositorioMateria;
import com.tallerwebi.repositorioInterfaz.RepositorioUsuario;
import com.tallerwebi.dominio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ControladorCalendario {

    private ServicioEvento servicioEvento;
    private ServicioMateria servicioMateria;
    private ServicioUsuarioMateria servicioUsuarioMateria;
    private RepositorioUsuario repositorioUsuario;
    private ServicioEmail servicioEmail;
    private RepositorioMateria repositorioMateria;

    @Autowired
    public ControladorCalendario(ServicioEvento servicioEvento, ServicioMateria servicioMateria, ServicioUsuarioMateria servicioUsuarioMateria, RepositorioUsuario repositorioUsuario, ServicioEmail servicioEmail) {
        this.servicioEvento = servicioEvento;
        this.servicioMateria = servicioMateria;
        this.servicioUsuarioMateria = servicioUsuarioMateria;
        this.repositorioUsuario = repositorioUsuario;
        this.servicioEmail = servicioEmail;
    }

    // Página principal del calendario
    @RequestMapping(path = "/calendario", method = RequestMethod.GET)
    public ModelAndView irACalendario(HttpServletRequest request) {
        ModelMap modelo = new ModelMap();

        Long usuarioId = (Long) request.getSession().getAttribute("ID");

        // Para obtener el id de la carrera
        String idCarrera = this.servicioUsuarioMateria.obtenerUsuario(usuarioId).getCarreraID().toString();

        // Obtener datos para la vista
        List<Evento> eventosHoy = servicioEvento.obtenerEventosHoy(usuarioId);
        List<Evento> proximosEventos = servicioEvento.obtenerProximosEventos(usuarioId, 5);
        ServicioEvento.ResumenEventos resumen = servicioEvento.obtenerResumenHoy(usuarioId);

        // Obtener materias para el dropdown
        List<Materia> materias = servicioMateria.obtenerTodasLasMaterias(idCarrera);

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
                                    @RequestParam(name = "notificar", defaultValue = "false") Boolean notificar,
                                    HttpServletRequest request) {
        ModelMap modelo = new ModelMap();

        Long usuarioId = (Long) request.getSession().getAttribute("ID");

        Usuario usuario = this.repositorioUsuario.buscarPorId(usuarioId);
        Materia materia = this.servicioMateria.buscarMateriaPorId(materiaId);


        try {
            // Crear el evento usando el servicio
            if (materiaId != null && materiaId > 0) {
                // Evento académico
                if ("EXAMEN".equals(tipo)) {
                    servicioEvento.crearExamen(titulo, fechaInicio, usuarioId, materiaId, notificar);
                } else if ("TAREA".equals(tipo)) {
                    servicioEvento.crearTarea(titulo, fechaInicio, usuarioId, materiaId, descripcion, notificar);
                } else {
                    servicioEvento.crearSesionEstudio(titulo, fechaInicio, fechaFin, usuarioId, materiaId, notificar);
                }
            } else {
                // Evento personal
                servicioEvento.crearEventoPersonal(titulo, fechaInicio, usuarioId, tipo, notificar);
            }

            String asuntoEmail = "\uD83C\uDF89 Evento confirmado con éxito";
            String mensajeEmail = "Hola " + usuario.getNombre() + " ,\n" +
                    "\n" +
                    "¡Tu evento ha sido creado exitosamente! \uD83C\uDF89 Ya está todo listo para que comiences a organizarte.\n" +
                    "\n" +
                    "Detalles del evento: \uD83D\uDDD3\uFE0F Fecha: " + fechaInicio.getDayOfMonth() + "/" + fechaInicio.getMonth() + "/" + fechaInicio.getYear() +  " \uD83D\uDD52 Hora: " + fechaInicio.getHour() + "hs \uD83D\uDCCD Lugar: " + ubicacion + " \uD83D\uDCDD Descripción: " + descripcion + "\n" +
                    "\n" +
                    "Si necesitás hacer algún cambio o agregar información adicional, podés hacerlo desde tu panel de eventos en cualquier momento.";

            servicioEmail.enviarEmailAUsuario(usuario.getEmail(), asuntoEmail, mensajeEmail);
            modelo.put("mensaje", "Evento creado exitosamente");
        } catch (Exception e) {
            System.out.println("❌ Error al crear evento: " + e.getMessage());
            e.printStackTrace();

            // Fallback: crear evento básico directamente
            try {
                System.out.println("Intentando crear evento básico...");
                if (usuario != null) {
                    Evento eventoBasico = new Evento(titulo, fechaInicio, usuario);
                    eventoBasico.setTipo(tipo);
                    if (descripcion != null) eventoBasico.setDescripcion(descripcion);
                    if (ubicacion != null) eventoBasico.setUbicacion(ubicacion);
                    eventoBasico.setNotificarRecordatorio(notificar);
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
