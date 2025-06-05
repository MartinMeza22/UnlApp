package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ControladorProgresoAcademico {

    @Autowired
    private RepositorioUsuarioMateria repositorioUsuarioMateria;

    @RequestMapping(path = "/progreso", method = RequestMethod.GET)
    public ModelAndView verProgreso() {
        ModelMap model = new ModelMap();//key / value
        Carrera carrera = new Carrera();
        carrera.setNombre("Desarrollo Web");
        model.put("carrera", carrera);
        return new ModelAndView("progreso", model);
    }

//    @PostMapping("/progresoDesdeElRegistro")
//    public ModelAndView cargarProgreso(@ModelAttribute) {
//
//        repositorioUsuarioMateria.guardar(usuarioMateria);
//        return new ModelAndView("home");
//    }
}
