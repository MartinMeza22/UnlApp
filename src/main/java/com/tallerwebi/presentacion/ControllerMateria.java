package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.MateriaDB;
import com.tallerwebi.dominio.Estudiante;
import com.tallerwebi.dominio.ServicioMateria;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
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
                                   ModelMap modelo,
                                   @SessionAttribute(value = "estudiante", required = false) Estudiante estudianteSesion) {

        if (estudianteSesion == null) {
            estudianteSesion = new Estudiante(UUID.randomUUID().toString());
            modelo.addAttribute("estudiante", estudianteSesion);
        } else {
            modelo.addAttribute("estudiante", estudianteSesion);
        }

        // Obtener todas las materias y agruparlas por cuatrimestre
        Map<Integer, List<MateriaDB>> materiasPorCuatrimestre = servicioMateria.obtenerMateriasAgrupadasPorCuatrimestre();

        // Si hay filtro de dificultad, aplicarlo
        if (dificultadFiltro != null && !dificultadFiltro.isEmpty()) {
            materiasPorCuatrimestre = materiasPorCuatrimestre.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().stream()
                        .filter(materia -> dificultadFiltro.equalsIgnoreCase(getDificultadFromCargaHoraria(materia.getCargaHoraria())))
                        .collect(Collectors.toList())
                ))
                .entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        modelo.addAttribute("materiasPorCuatrimestre", materiasPorCuatrimestre);
        modelo.addAttribute("dificultadSeleccionada", dificultadFiltro);

        return modelo;
    }

    /**
     * Método auxiliar para determinar dificultad basada en carga horaria
     * (ya que el nuevo JSON no tiene campo "dificultad")
     */
    private String getDificultadFromCargaHoraria(Integer cargaHoraria) {
        if (cargaHoraria == null) return "Media";
        if (cargaHoraria <= 4) return "Facil";
        if (cargaHoraria <= 6) return "Media";
        return "Alta";
    }

//     @PostMapping("/cursando")
//     public String marcarCursando(@RequestParam("codigoMateria") Integer codigoMateria,
//                                  @SessionAttribute("estudiante") Estudiante estudiante,
//                                  RedirectAttributes redirectAttributes) {
//         String resultado = servicioMateria.marcarComoCursando(estudiante, codigoMateria);
//         redirectAttributes.addFlashAttribute("mensaje", resultado);
//         return "redirect:/materias";
//     }
//
//     @PostMapping("/aprobada")
//     public String marcarAprobada(@RequestParam("codigoMateria") Integer codigoMateria,
//                                  @SessionAttribute("estudiante") Estudiante estudiante,
//                                  RedirectAttributes redirectAttributes) {
//         String resultado = servicioMateria.marcarComoAprobada(estudiante, codigoMateria);
//         redirectAttributes.addFlashAttribute("mensaje", resultado);
//         return "redirect:/materias";
//     }
//
//     @PostMapping("/quitarCursando")
//     public String quitarCursando(@RequestParam("codigoMateria") Integer codigoMateria,
//                                  @SessionAttribute("estudiante") Estudiante estudiante,
//                                  RedirectAttributes redirectAttributes) {
//         String resultado = servicioMateria.quitarDeCursando(estudiante, codigoMateria);
//         redirectAttributes.addFlashAttribute("mensaje", resultado);
//         return "redirect:/materias";
//     }
//
//     @PostMapping("/quitarAprobada")
//     public String quitarAprobada(@RequestParam("codigoMateria") Integer codigoMateria,
//                                  @SessionAttribute("estudiante") Estudiante estudiante,
//                                  RedirectAttributes redirectAttributes) {
//         String resultado = servicioMateria.quitarDeAprobadas(estudiante, codigoMateria);
//         redirectAttributes.addFlashAttribute("mensaje", resultado);
//         return "redirect:/materias";
//     }
//
//     @PostMapping("/reiniciarEstudiante")
//     public String reiniciarEstudiante(SessionStatus sessionStatus, RedirectAttributes redirectAttributes) {
//         sessionStatus.setComplete();
//         redirectAttributes.addFlashAttribute("mensaje", "Estado del estudiante reiniciado.");
//         return "redirect:/materias";
//     }
}