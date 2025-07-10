package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.googleBookAPI.ApiResponse;
import com.tallerwebi.dominio.googleBookAPI.Item;
import com.tallerwebi.dominio.servicios.ServicioLibro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class ControladorLibro {

    private final ServicioLibro servicioLibro;

    @Autowired
    public ControladorLibro(ServicioLibro servicioLibro) {
        this.servicioLibro = servicioLibro;
    }

    @GetMapping("/biblioteca-digital")
    public ModelAndView mostrarLibros() {
        ModelAndView mav  = new ModelAndView("vista-libros");
        List<Item> libros = servicioLibro.obtenerLibros();

        if(libros != null) {
            mav.addObject("libros", libros);
        } else {
            mav.addObject("error", "No se encontraron libros");
        }

        return mav;
    }

}
