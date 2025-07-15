package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.dominio.servicios.ServicioEmail;
import com.tallerwebi.dominio.servicios.ServicioMateria;
import com.tallerwebi.servicioInterfaz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/foro")
public class ControladorForo {

    private final ServicioUsuario servicioUsuario;
    private final ServicioPublicacion servicioPublicacion;
    private final ServicioComentario servicioComentario;
    private final ServicioMateria servicioMateria;
    private final ServicioReporte servicioReporte;
    private final ServletContext servletContext;
    private final ServicioEmail servicioEmail;
    private final ServicioNotificacion servicioNotificacion;
    private static final List<String> PERMITTED_EXTENSIONS = Arrays.asList("pdf", "ppt", "pptx", "jpg", "jpeg", "png", "mp4", "webm");

    @Autowired
    public ControladorForo(ServicioUsuario servicioUsuario, ServicioPublicacion servicioPublicacion,
                           ServicioComentario servicioComentario, ServicioMateria servicioMateria,
                           ServicioReporte servicioReporte, ServletContext servletContext,
                           ServicioEmail servicioEmail, ServicioNotificacion servicioNotificacion) {
        this.servicioUsuario = servicioUsuario;
        this.servicioPublicacion = servicioPublicacion;
        this.servicioComentario = servicioComentario;
        this.servicioMateria = servicioMateria;
        this.servletContext = servletContext;
        this.servicioReporte = servicioReporte;
        this.servicioEmail = servicioEmail;
        this.servicioNotificacion = servicioNotificacion;
    }

    private Long obtenerIdUsuarioDeSesion(HttpSession session) {
        return (Long) session.getAttribute("ID");
    }

    @GetMapping
    public ModelAndView mostrarForo(@RequestParam(required = false) Long idMateria,
                                    @RequestParam(required = false, defaultValue = "fecha") String ordenar,
                                    HttpSession session) {
        Long idUsuario = obtenerIdUsuarioDeSesion(session);
        if (idUsuario == null) {
            return new ModelAndView("redirect:/login");
        }
        ModelAndView mav = new ModelAndView("foro");
        try {
            Usuario usuario = servicioUsuario.obtenerUsuario(idUsuario);
            Carrera carrera = usuario.getCarrera();
            if (carrera == null) {
                mav.addObject("error", "No tienes una carrera asignada.");
                return mav;
            }

            List<Notificacion> notificaciones = servicioNotificacion.obtenerNotificacionesNoLeidas(usuario);
            mav.addObject("notificaciones", notificaciones);

            mav.addObject("publicaciones", servicioPublicacion.buscarPublicaciones(carrera, idMateria, ordenar));
            mav.addObject("materias", servicioMateria.obtenerMateriasPorCarrera(carrera));
            mav.addObject("usuario", usuario);
            mav.addObject("ordenActual", ordenar);
            mav.addObject("materiaActual", idMateria);

        } catch (UsuarioNoEncontrado e) {
            return new ModelAndView("redirect:/login");
        } catch (Exception e) {
            mav.addObject("error", "Error al cargar el foro: " + e.getMessage());
        }
        return mav;
    }

    @GetMapping("/publicacion/nueva")
    public ModelAndView mostrarFormularioNuevaPublicacion(HttpSession session) {
        Long idUsuario = obtenerIdUsuarioDeSesion(session);
        if (idUsuario == null) {
            return new ModelAndView("redirect:/login");
        }

        ModelAndView mav = new ModelAndView("nueva-publicacion");
        try {
            Usuario usuario = servicioUsuario.obtenerUsuario(idUsuario);
            mav.addObject("materias", servicioMateria.obtenerMateriasPorCarrera(usuario.getCarrera()));
            mav.addObject("usuario", usuario);
        } catch (UsuarioNoEncontrado e) {
            return new ModelAndView("redirect:/login");
        }
        return mav;
    }

    @PostMapping("/publicacion/crear")
    public ModelAndView crearPublicacion(@RequestParam("titulo") String titulo,
                                         @RequestParam("descripcion") String descripcion,
                                         @RequestParam("idMateria") Long idMateria,
                                         @RequestParam("archivo") MultipartFile archivo,
                                         HttpSession session,
                                         RedirectAttributes redirectAttributes) {
        Long idUsuario = obtenerIdUsuarioDeSesion(session);
        if (idUsuario == null) return new ModelAndView("redirect:/login");

        try {
            Usuario usuario = servicioUsuario.obtenerUsuario(idUsuario);
            String nombreArchivoGuardado = null;

            if (archivo != null && !archivo.isEmpty()) {
                String originalFilename = StringUtils.cleanPath(archivo.getOriginalFilename());
                String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
                if (!PERMITTED_EXTENSIONS.contains(extension)) {
                    redirectAttributes.addFlashAttribute("error", "Tipo de archivo no permitido.");
                    return new ModelAndView("redirect:/foro");
                }

                String uploadDirString = new ClassPathResource("static/uploads/").getFile().getAbsolutePath();
                Path uploadPath = Paths.get(uploadDirString);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Path rutaArchivo = uploadPath.resolve(originalFilename);
                Files.write(rutaArchivo, archivo.getBytes());
                nombreArchivoGuardado = originalFilename;
            }

            servicioPublicacion.crearPublicacion(titulo, descripcion, usuario, idMateria, nombreArchivoGuardado);
            redirectAttributes.addFlashAttribute("exito", "Publicación creada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo crear la publicación: " + e.getMessage());
            e.printStackTrace();
        }
        return new ModelAndView("redirect:/foro");
    }

    @PostMapping("/comentario/crear")
    public ModelAndView crearComentario(@RequestParam("idPublicacion") Long idPublicacion,
                                        @RequestParam("descripcion") String descripcion,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {
        Long idUsuario = obtenerIdUsuarioDeSesion(session);
        if (idUsuario == null) return new ModelAndView("redirect:/login");

        try {
            Usuario usuario = servicioUsuario.obtenerUsuario(idUsuario);
            Comentario comentarioCreado = servicioComentario.crearComentario(idPublicacion, usuario, descripcion);
            servicioNotificacion.crearNotificacionComentario(comentarioCreado);
            redirectAttributes.addFlashAttribute("exito", "Comentario agregado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo agregar el comentario.");
        }
        return new ModelAndView("redirect:/foro");
    }

    @PostMapping("/publicacion/{idPublicacion}/like")
    public ModelAndView darLike(@PathVariable("idPublicacion") Long idPublicacion, HttpSession session, RedirectAttributes redirectAttributes) {
        Long idUsuario = obtenerIdUsuarioDeSesion(session);
        if (idUsuario == null) return new ModelAndView("redirect:/login");

        try {
            servicioPublicacion.cambiarEstadoLike(idPublicacion, idUsuario);
            Publicacion publicacion = servicioPublicacion.obtenerPublicacion(idPublicacion);
            Usuario usuarioQueDioLike = servicioUsuario.obtenerUsuario(idUsuario);
            if(publicacion.usuarioDioLike(usuarioQueDioLike)){
                servicioNotificacion.crearNotificacionLike(publicacion, usuarioQueDioLike);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar el like: " + e.getMessage());
        }
        return new ModelAndView("redirect:/foro");
    }

    @PostMapping("/reportar")
    public ModelAndView reportarContenido(
            @RequestParam(required = false) Long idPublicacion,
            @RequestParam(required = false) Long idComentario,
            @RequestParam String motivo,
            @RequestParam(required = false) String descripcionAdicional,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Long idUsuario = obtenerIdUsuarioDeSesion(session);
        if (idUsuario == null) return new ModelAndView("redirect:/login");

        try {
            Reporte reporteCreado = null;
            if (idPublicacion != null) {
                reporteCreado = servicioReporte.crearReporteParaPublicacion(idPublicacion, idUsuario, motivo, descripcionAdicional);
            } else if (idComentario != null) {
                reporteCreado = servicioReporte.crearReporteParaComentario(idComentario, idUsuario, motivo, descripcionAdicional);
            }
            if(reporteCreado != null) {
                servicioNotificacion.crearNotificacionReporte(reporteCreado);
            }
            redirectAttributes.addFlashAttribute("exito", "Reporte enviado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al enviar el reporte: " + e.getMessage());
        }
        return new ModelAndView("redirect:/foro");
    }

    @PostMapping("/publicacion/eliminar")
    public ModelAndView eliminarPublicacion(@RequestParam("idPublicacion") Long idPublicacion,
                                            @RequestParam(name = "idReporte", required = false) Long idReporte,
                                            HttpSession session,
                                            RedirectAttributes redirectAttributes) {
        Long idUsuarioLogueado = obtenerIdUsuarioDeSesion(session);
        if (idUsuarioLogueado == null) return new ModelAndView("redirect:/login");

        if (idReporte != null && "ADMIN".equals(session.getAttribute("ROL"))) {
            try {
                Publicacion publicacion = servicioPublicacion.obtenerPublicacion(idPublicacion);
                Reporte reporte = servicioReporte.obtenerReportePorId(idReporte);
                Usuario autor = publicacion.getUsuario();

                servicioPublicacion.eliminarPublicacion(idPublicacion, idUsuarioLogueado);
                servicioReporte.eliminarReportesDePublicacion(idPublicacion);

                String asunto = "Notificación: Tu publicación ha sido eliminada";
                String mensaje = "Hola " + autor.getNombre() + ",\n\n" +
                        "Te informamos que tu publicación \"" + publicacion.getTitulo() + "\" ha sido eliminada por un administrador.\n" +
                        "Motivo: " + reporte.getMotivo() + ".\n\n" +
                        "Saludos cordiales,\nEl equipo de UnlApp";

                servicioEmail.enviarEmailAUsuario(autor.getEmail(), asunto, mensaje);

                redirectAttributes.addFlashAttribute("exito", "Publicación eliminada y usuario notificado.");
                return new ModelAndView("redirect:/admin/panel");

            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error en la eliminación: " + e.getMessage());
                return new ModelAndView("redirect:/admin/panel");
            }
        } else {
            try {
                servicioPublicacion.eliminarPublicacion(idPublicacion, idUsuarioLogueado);
                redirectAttributes.addFlashAttribute("exito", "Publicación eliminada correctamente.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error al eliminar la publicación: " + e.getMessage());
            }
        }
        return new ModelAndView("redirect:/foro");
    }

    @PostMapping("/comentario/eliminar")
    public ModelAndView eliminarComentario(@RequestParam("idComentario") Long idComentario,
                                           @RequestParam(name = "idReporte", required = false) Long idReporte,
                                           HttpSession session,
                                           RedirectAttributes redirectAttributes) {
        Long idUsuarioLogueado = obtenerIdUsuarioDeSesion(session);
        if (idUsuarioLogueado == null) return new ModelAndView("redirect:/login");

        if (idReporte != null && "ADMIN".equals(session.getAttribute("ROL"))) {
            try {
                Comentario comentario = servicioComentario.obtenerComentarioPorId(idComentario);
                Reporte reporte = servicioReporte.obtenerReportePorId(idReporte);
                Usuario autor = comentario.getUsuario();
                String descComentario = comentario.getDescripcion();

                servicioComentario.eliminarComentario(idComentario, idUsuarioLogueado);
                servicioReporte.eliminarReportesDeComentario(idComentario);

                String asunto = "Notificación: Tu comentario ha sido eliminado";
                String mensaje = "Hola " + autor.getNombre() + ",\n\n" +
                        "Te informamos que tu comentario (\"" + descComentario.substring(0, Math.min(descComentario.length(), 50)) + "...\") ha sido eliminado por un administrador.\n" +
                        "Motivo: " + reporte.getMotivo() + ".\n\n" +
                        "Saludos cordiales,\nEl equipo de UnlApp";

                servicioEmail.enviarEmailAUsuario(autor.getEmail(), asunto, mensaje);

                redirectAttributes.addFlashAttribute("exito", "Comentario eliminado y usuario notificado.");
                return new ModelAndView("redirect:/admin/panel");

            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error al eliminar el comentario: " + e.getMessage());
                return new ModelAndView("redirect:/admin/panel");
            }
        } else {
            try {
                servicioComentario.eliminarComentario(idComentario, idUsuarioLogueado);
                redirectAttributes.addFlashAttribute("exito", "Comentario eliminado correctamente.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error al eliminar el comentario: " + e.getMessage());
            }
        }
        return new ModelAndView("redirect:/foro");
    }

    @PostMapping("/publicacion/editar")
    public ModelAndView editarPublicacion(@RequestParam("idPublicacion") Long idPublicacion,
                                          @RequestParam("titulo") String titulo,
                                          @RequestParam("descripcion") String descripcion,
                                          HttpSession session,
                                          RedirectAttributes redirectAttributes) {
        Long idUsuario = obtenerIdUsuarioDeSesion(session);
        if (idUsuario == null) return new ModelAndView("redirect:/login");

        try {
            servicioPublicacion.modificarPublicacion(idPublicacion, titulo, descripcion, idUsuario);
            redirectAttributes.addFlashAttribute("exito", "Publicación modificada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al modificar la publicación: " + e.getMessage());
        }
        return new ModelAndView("redirect:/foro");
    }

    @PostMapping("/comentario/editar")
    public ModelAndView editarComentario(@RequestParam("idComentario") Long idComentario,
                                         @RequestParam("descripcion") String descripcion,
                                         HttpSession session,
                                         RedirectAttributes redirectAttributes) {
        Long idUsuario = obtenerIdUsuarioDeSesion(session);
        if (idUsuario == null) return new ModelAndView("redirect:/login");

        try {
            servicioComentario.modificarComentario(idComentario, descripcion, idUsuario);
            redirectAttributes.addFlashAttribute("exito", "Comentario modificado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al modificar el comentario: " + e.getMessage());
        }
        return new ModelAndView("redirect:/foro");
    }

    @PostMapping("/notificaciones/marcar-leidas")
    @ResponseBody // esto indica que la respuesta no es una vista
    public ResponseEntity<Void> marcarNotificacionesComoLeidas(HttpSession session) {
        Long idUsuario = obtenerIdUsuarioDeSesion(session);
        if (idUsuario == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 401 No autorizado
        }
        try {
            Usuario usuario = servicioUsuario.obtenerUsuario(idUsuario);
            servicioNotificacion.marcarTodasComoLeidas(usuario);
            return new ResponseEntity<>(HttpStatus.OK); // 200 OK
        } catch (UsuarioNoEncontrado e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 No encontrado
        }
    }
}