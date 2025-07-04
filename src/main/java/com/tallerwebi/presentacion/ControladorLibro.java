package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.googleBookAPI.ApiResponse;
import com.tallerwebi.dominio.servicios.ServicioLibro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControladorLibro {

    private final ServicioLibro servicioLibro;

    @Autowired
    public ControladorLibro(ServicioLibro servicioLibro) {
        this.servicioLibro = servicioLibro;
    }

    @GetMapping("/biblioteca-digital")
    public String mostrarLibros(Model model) {

        ApiResponse response = servicioLibro.obtenerLibros();

        if(response != null && response.getItems() != null) {
            model.addAttribute("libros", response.getItems());
        }

        return "vista-libros";
    }

}
