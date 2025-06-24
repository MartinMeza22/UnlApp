package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Controller
@RequestMapping("/foro")
public class ControladorForo {

    private final ServicioUsuario servicioUsuario;
    private final ServicioPublicacion servicioPublicacion;
    private final ServicioComentario servicioComentario;
    private final ServicioMateria servicioMateria;

    @Autowired
    public ControladorForo(ServicioUsuario servicioUsuario, ServicioPublicacion servicioPublicacion, ServicioComentario servicioComentario, ServicioMateria servicioMateria) {
        this.servicioUsuario = servicioUsuario;
        this.servicioPublicacion = servicioPublicacion;
        this.servicioComentario = servicioComentario;
        this.servicioMateria = servicioMateria;
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
                mav.addObject("error", "No tienes una carrera asignada. No se puede mostrar el foro.");
                return mav;
            }

            mav.addObject("publicaciones", servicioPublicacion.buscarPublicaciones(carrera, idMateria, ordenar));
            mav.addObject("materias", servicioMateria.obtenerMateriasPorCarrera(carrera));
            mav.addObject("usuario", usuario); // Contiene carrera y otros datos
            mav.addObject("carrera", carrera); // Puedes pasar la carrera explícitamente también
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
                                         HttpSession session,
                                         RedirectAttributes redirectAttributes) {
        Long idUsuario = obtenerIdUsuarioDeSesion(session);
        if (idUsuario == null) {
            return new ModelAndView("redirect:/login");
        }

        try {
            Usuario usuario = servicioUsuario.obtenerUsuario(idUsuario);
            servicioPublicacion.crearPublicacion(titulo, descripcion, usuario, idMateria);
            redirectAttributes.addFlashAttribute("exito", "Publicación creada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo crear la publicación.");
        }
        return new ModelAndView("redirect:/foro");
    }

    @PostMapping("/comentario/crear")
    public ModelAndView crearComentario(@RequestParam("idPublicacion") Long idPublicacion,
                                        @RequestParam("descripcion") String descripcion,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {
        Long idUsuario = obtenerIdUsuarioDeSesion(session);
        if (idUsuario == null) {
            return new ModelAndView("redirect:/login");
        }

        try {
            Usuario usuario = servicioUsuario.obtenerUsuario(idUsuario);
            servicioComentario.crearComentario(idPublicacion, usuario, descripcion);
            redirectAttributes.addFlashAttribute("exito", "Comentario agregado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo agregar el comentario.");
        }
        return new ModelAndView("redirect:/foro");
    }
    @PostMapping("/publicacion/{idPublicacion}/like")
    public ModelAndView darLike(@PathVariable("idPublicacion") Long idPublicacion, HttpSession session, RedirectAttributes redirectAttributes) {
        Long idUsuario = obtenerIdUsuarioDeSesion(session);
        if (idUsuario == null) {
            return new ModelAndView("redirect:/login");
        }

        try {
            servicioPublicacion.cambiarEstadoLike(idPublicacion, idUsuario);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar el like: " + e.getMessage());
        }

        return new ModelAndView("redirect:/foro");
    }
}