package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Materia;
import com.tallerwebi.dominio.ServicioMateria;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/materias")
public class ControllerMateria {

    private final ServicioMateria servicioMateria;

    public ControllerMateria(ServicioMateria servicioMateria) {
        this.servicioMateria = servicioMateria;
    }

    @GetMapping
    public String listarMaterias(ModelMap modelo) {
        // Simple: just get all materias and display them
        List<Materia> materias = servicioMateria.obtenerTodasLasMaterias();

        modelo.addAttribute("materias", materias);

        return "materias"; // This will look for materias.html template
    }
}