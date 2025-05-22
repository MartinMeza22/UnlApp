package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Cuatrimestre;
import com.tallerwebi.dominio.Estudiante;
import com.tallerwebi.dominio.Materia;
import com.tallerwebi.dominio.ServicioMateria;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Sigue siendo necesario para addFlashAttribute

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/materias")
@SessionAttributes("estudiante")
public class ControllerMateria {

    private final ServicioMateria servicioMateria;

    public ControllerMateria(ServicioMateria servicioMateria) {
        this.servicioMateria = servicioMateria;
    }

    @GetMapping
    public ModelMap listarMaterias(@RequestParam(value = "dificultad", required = false) String dificultadFiltro,
                                   ModelMap modelo, // Quitamos @RequestParam(value = "mensaje", required = false) String mensaje
                                   @SessionAttribute(value = "estudiante", required = false) Estudiante estudianteSesion) {

        if (estudianteSesion == null) {
            estudianteSesion = new Estudiante(UUID.randomUUID().toString());
            modelo.addAttribute("estudiante", estudianteSesion);
        } else {
            modelo.addAttribute("estudiante", estudianteSesion);
        }

        List<Cuatrimestre> todosLosCuatrimestres = this.servicioMateria.obtenerMateriasPorCuatrimestre();
        List<Cuatrimestre> cuatrimestresFiltrados = new ArrayList<>();

        if (dificultadFiltro != null && !dificultadFiltro.isEmpty()) {
            for (Cuatrimestre cuatrimestre : todosLosCuatrimestres) {
                List<Materia> materiasFiltradasPorCuatrimestre = cuatrimestre.getMaterias().stream()
                        .filter(materia -> materia.getDificultad().equalsIgnoreCase(dificultadFiltro))
                        .collect(Collectors.toList());
                if (!materiasFiltradasPorCuatrimestre.isEmpty()) {
                    Cuatrimestre nuevoCuatrimestre = new Cuatrimestre();
                    nuevoCuatrimestre.setCuatrimestre(cuatrimestre.getCuatrimestre());
                    nuevoCuatrimestre.setMaterias(materiasFiltradasPorCuatrimestre);
                    cuatrimestresFiltrados.add(nuevoCuatrimestre);
                }
            }
        } else {
            cuatrimestresFiltrados = todosLosCuatrimestres;
        }

        modelo.addAttribute("cuatrimestres", cuatrimestresFiltrados);
        modelo.addAttribute("dificultadSeleccionada", dificultadFiltro);
        // El mensaje ahora se leerá directamente del modelo flash de Spring si existe
        return modelo;
    }

    @PostMapping("/cursando")
    public String marcarCursando(@RequestParam("codigoMateria") Integer codigoMateria,
                                 @SessionAttribute("estudiante") Estudiante estudiante,
                                 RedirectAttributes redirectAttributes) {
        String resultado = servicioMateria.marcarComoCursando(estudiante, codigoMateria);
        redirectAttributes.addFlashAttribute("mensaje", resultado); // ¡Cambiado aquí!
        return "redirect:/materias";
    }

    @PostMapping("/aprobada")
    public String marcarAprobada(@RequestParam("codigoMateria") Integer codigoMateria,
                                 @SessionAttribute("estudiante") Estudiante estudiante,
                                 RedirectAttributes redirectAttributes) {
        String resultado = servicioMateria.marcarComoAprobada(estudiante, codigoMateria);
        redirectAttributes.addFlashAttribute("mensaje", resultado); // ¡Cambiado aquí!
        return "redirect:/materias";
    }

    @PostMapping("/quitarCursando")
    public String quitarCursando(@RequestParam("codigoMateria") Integer codigoMateria,
                                 @SessionAttribute("estudiante") Estudiante estudiante,
                                 RedirectAttributes redirectAttributes) {
        String resultado = servicioMateria.quitarDeCursando(estudiante, codigoMateria);
        redirectAttributes.addFlashAttribute("mensaje", resultado); // ¡Cambiado aquí!
        return "redirect:/materias";
    }

    @PostMapping("/quitarAprobada")
    public String quitarAprobada(@RequestParam("codigoMateria") Integer codigoMateria,
                                 @SessionAttribute("estudiante") Estudiante estudiante,
                                 RedirectAttributes redirectAttributes) {
        String resultado = servicioMateria.quitarDeAprobadas(estudiante, codigoMateria);
        redirectAttributes.addFlashAttribute("mensaje", resultado); // ¡Cambiado aquí!
        return "redirect:/materias";
    }

    @PostMapping("/reiniciarEstudiante")
    public String reiniciarEstudiante(SessionStatus sessionStatus, RedirectAttributes redirectAttributes) {
        sessionStatus.setComplete();
        redirectAttributes.addFlashAttribute("mensaje", "Estado del estudiante reiniciado."); // ¡Cambiado aquí!
        return "redirect:/materias";
    }
}