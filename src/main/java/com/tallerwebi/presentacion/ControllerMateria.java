package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.MateriaDB;
import com.tallerwebi.dominio.ServicioMateria;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/materias")
public class ControllerMateria {

    private final ServicioMateria servicioMateria;

    public ControllerMateria(ServicioMateria servicioMateria) {
        this.servicioMateria = servicioMateria;
    }

    @GetMapping
    public ModelMap listarMaterias(@RequestParam(value = "dificultad", required = false) String dificultadFiltro,
                                   @RequestParam(value = "cuatrimestre", required = false) Integer cuatrimestreFiltro,
                                   ModelMap modelo) {

        // Obtener todas las materias y agruparlas por cuatrimestre
        Map<Integer, List<MateriaDB>> materiasPorCuatrimestre = servicioMateria.obtenerMateriasAgrupadasPorCuatrimestre();

        // Aplicar filtro de cuatrimestre si está presente
        if (cuatrimestreFiltro != null) {
            materiasPorCuatrimestre = materiasPorCuatrimestre.entrySet().stream()
                .filter(entry -> entry.getKey().equals(cuatrimestreFiltro))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        // Aplicar filtro de dificultad si está presente
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
        modelo.addAttribute("cuatrimestreSeleccionado", cuatrimestreFiltro);

        return modelo;
    }

    /**
     * Método auxiliar para determinar dificultad basada en carga horaria
     */
    private String getDificultadFromCargaHoraria(Integer cargaHoraria) {
        if (cargaHoraria == null) return "Media";
        if (cargaHoraria <= 4) return "Facil";
        if (cargaHoraria <= 6) return "Media";
        return "Alta";
    }

    /**
     * Método auxiliar para obtener las correlativas como lista de strings
     */
    private List<String> getCorrelativasAsList(MateriaDB materia) {
        return List.of(
            materia.getCorrelativa1(),
            materia.getCorrelativa2(),
            materia.getCorrelativa3(),
            materia.getCorrelativa4(),
            materia.getCorrelativa5(),
            materia.getCorrelativa6()
        ).stream()
        .filter(correlativa -> correlativa != null && !correlativa.isEmpty())
        .collect(Collectors.toList());
    }
}