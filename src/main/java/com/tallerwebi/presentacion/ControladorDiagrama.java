package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Carrera;
import com.tallerwebi.dominio.servicios.*;
import com.tallerwebi.dominio.DTO.MateriaDiagramaDTO;
import com.tallerwebi.servicioInterfaz.ServicioCarrera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/carrera/{idCarrera}")
public class ControladorDiagrama {

    private ServicioProgreso servicioProgreso;
    private ServicioCarrera servicioCarrera;

    @Autowired
    public ControladorDiagrama(
            ServicioProgreso servicioProgreso,
            ServicioCarrera servicioCarrera) {
        this.servicioProgreso = servicioProgreso;
        this.servicioCarrera = servicioCarrera;
    }

    @GetMapping("/diagrama")
    public ModelAndView mostrarDiagramaCarrera(@PathVariable String idCarrera, HttpServletRequest request) {
        ModelAndView model = new ModelAndView("diagrama-carrera");
        Long usuarioId = (Long) request.getSession().getAttribute("ID");

        List<MateriaDiagramaDTO> materiasParaDiagrama = servicioProgreso.obtenerMateriasParaDiagrama(idCarrera, usuarioId);
        Long idCarreraLong = Long.parseLong(idCarrera);
        Carrera carrera = servicioCarrera.buscarCarreraPorId(idCarreraLong);

        model.addObject("materiasJson", materiasParaDiagrama);
        model.addObject("carrera", carrera);

        return model;
    }

}
