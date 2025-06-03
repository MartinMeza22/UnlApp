package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Materia;
import com.tallerwebi.dominio.ServicioMateria;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/materias")
public class ControladorPlanDeEstudios {

    private final ServicioMateria servicioMateria;

    public ControladorPlanDeEstudios(ServicioMateria servicioMateria) {
        this.servicioMateria = servicioMateria;
    }

    @GetMapping
    public String listarMaterias(ModelMap modelo) {

        List<Materia> materias = servicioMateria.obtenerTodasLasMaterias();

        modelo.addAttribute("materias", materias);

        return "materias";
    }
}