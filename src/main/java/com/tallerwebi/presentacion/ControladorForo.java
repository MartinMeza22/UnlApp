package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.dominio.servicios.ServicioMateria;
import com.tallerwebi.servicioInterfaz.ServicioComentario;
import com.tallerwebi.servicioInterfaz.ServicioPublicacion;
import com.tallerwebi.servicioInterfaz.ServicioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletContext;
import java.io.File;

@Controller
@RequestMapping("/foro")
public class ControladorForo {

    private final ServicioUsuario servicioUsuario;
    private final ServicioPublicacion servicioPublicacion;
    private final ServicioComentario servicioComentario;
    private final ServicioMateria servicioMateria;

    private static final List<String> PERMITTED_EXTENSIONS = Arrays.asList("pdf", "ppt", "pptx", "jpg", "jpeg", "png", "mp4", "webm");
    private final ServletContext servletContext;

    @Autowired
    public ControladorForo(ServicioUsuario servicioUsuario, ServicioPublicacion servicioPublicacion, ServicioComentario servicioComentario, ServicioMateria servicioMateria, ServletContext servletContext) {
        this.servicioUsuario = servicioUsuario;
        this.servicioPublicacion = servicioPublicacion;
        this.servicioComentario = servicioComentario;
        this.servicioMateria = servicioMateria;
        this.servletContext = servletContext;
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
            mav.addObject("carrera", carrera); // pasa la carrera explícitamente también (no carga la carrera del usuario)
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
        if (idUsuario == null) {
            return new ModelAndView("redirect:/login");
        }

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

                // OBTENER LA RUTA REAL Y ABSOLUTA DEL SERVIDOR
                String uploadPath = servletContext.getRealPath("/uploads/");
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs(); // Crea el directorio si no existe
                }

                Path rutaArchivo = Paths.get(uploadPath, originalFilename);
                Files.write(rutaArchivo, archivo.getBytes());
                nombreArchivoGuardado = originalFilename;
            }

            servicioPublicacion.crearPublicacion(titulo, descripcion, usuario, idMateria, nombreArchivoGuardado);
            redirectAttributes.addFlashAttribute("exito", "Publicación creada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo crear la publicación: " + e.getMessage());
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
    @PostMapping("/publicacion/eliminar")
    public ModelAndView eliminarPublicacion(@RequestParam("idPublicacion") Long idPublicacion,
                                            HttpSession session,
                                            RedirectAttributes redirectAttributes) {
        Long idUsuario = obtenerIdUsuarioDeSesion(session);
        if (idUsuario == null) {
            return new ModelAndView("redirect:/login");
        }

        try {
            servicioPublicacion.eliminarPublicacion(idPublicacion, idUsuario);
            redirectAttributes.addFlashAttribute("exito", "Publicación eliminada correctamente.");
        } catch (Exception e) {
            // catchea cualquier excepción (PublicacionInexistente, AccesoDenegado)
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la publicación: " + e.getMessage());
        }

        return new ModelAndView("redirect:/foro");
    }

    @PostMapping("/comentario/eliminar")
    public ModelAndView eliminarComentario(@RequestParam("idComentario") Long idComentario,
                                           HttpSession session,
                                           RedirectAttributes redirectAttributes) {
        Long idUsuario = obtenerIdUsuarioDeSesion(session);
        if (idUsuario == null) {
            return new ModelAndView("redirect:/login");
        }

        try {
            servicioComentario.eliminarComentario(idComentario, idUsuario);
            redirectAttributes.addFlashAttribute("exito", "Comentario eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el comentario: " + e.getMessage());
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
        if (idUsuario == null) {
            return new ModelAndView("redirect:/login");
        }

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
        if (idUsuario == null) {
            return new ModelAndView("redirect:/login");
        }

        try {
            servicioComentario.modificarComentario(idComentario, descripcion, idUsuario);
            redirectAttributes.addFlashAttribute("exito", "Comentario modificado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al modificar el comentario: " + e.getMessage());
        }

        return new ModelAndView("redirect:/foro");
    }
}