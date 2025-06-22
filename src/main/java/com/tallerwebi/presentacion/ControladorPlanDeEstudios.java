package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Materia;
import com.tallerwebi.dominio.ServicioMateria;
import com.tallerwebi.dominio.ServicioUsuarioMateria;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/materias")
public class ControladorPlanDeEstudios {

    private final ServicioMateria servicioMateria;
    private ServicioUsuarioMateria servicioUsuarioMateria;

    public ControladorPlanDeEstudios(ServicioMateria servicioMateria, ServicioUsuarioMateria servicioUsuarioMateria) {
        this.servicioMateria = servicioMateria;
        this.servicioUsuarioMateria = servicioUsuarioMateria;
    }

    @GetMapping
    public String listarMaterias(ModelMap modelo, HttpServletRequest request) {

        Long usuarioId = (Long) request.getSession().getAttribute("ID");

        // Para obtener el id de la carrera
        String idCarrera = this.servicioUsuarioMateria.obtenerUsuario(usuarioId).getCarreraID().toString();

        List<Materia> materias = servicioMateria.obtenerMateriasPorCarrera(idCarrera);

        modelo.addAttribute("materias", materias);

        return "materias";
    }
}