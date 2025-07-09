package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Materia;
import com.tallerwebi.dominio.MateriasConPromedios;
import com.tallerwebi.dominio.servicios.ServicioMateria;
import com.tallerwebi.dominio.servicios.ServicioUsuarioMateria;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/materias")
public class ControladorPlanDeEstudios {

    private final ServicioMateria servicioMateria;
    private final ServicioUsuarioMateria servicioUsuarioMateria;

    public ControladorPlanDeEstudios(ServicioMateria servicioMateria, ServicioUsuarioMateria servicioUsuarioMateria) {
        this.servicioMateria = servicioMateria;
        this.servicioUsuarioMateria = servicioUsuarioMateria;
    }

    @GetMapping
    public String listarMaterias(ModelMap modelo, HttpServletRequest request) {
        Long usuarioId = (Long) request.getSession().getAttribute("ID");
        String idCarrera = this.servicioUsuarioMateria.obtenerUsuario(usuarioId).getCarreraID().toString();
        
        List<MateriasConPromedios> materiasConPromedios = servicioMateria.obtenerMateriasConPromediosPorCarrera(idCarrera);
        modelo.addAttribute("materias", materiasConPromedios);
        
        return "materias";
    }
}