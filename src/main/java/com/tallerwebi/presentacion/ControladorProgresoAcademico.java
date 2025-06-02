package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Carrera;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorProgresoAcademico {

    @RequestMapping(path = "/progreso", method = RequestMethod.GET)
    public ModelAndView verProgreso() {
        ModelMap model = new ModelMap();//key / value
        Carrera carrera = new Carrera();
        carrera.setNombre("Desarrollo Web");
        model.put("carrera", carrera);
        return new ModelAndView("progreso", model);
    }

}
