package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.googleBookAPI.Item;
import com.tallerwebi.servicioInterfaz.ServicioLibro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ControladorLibrosFavoritos {

    private final ServicioLibro servicioLibro;

    @Autowired
    public ControladorLibrosFavoritos(ServicioLibro servicioLibro) {
        this.servicioLibro = servicioLibro;
    }

    @GetMapping("/libros-favoritos")
    public ModelAndView mostrarMisFavoritos(HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("ID");
        Model model;

        ModelAndView mav = new ModelAndView("libros-favoritos");

        List<Item> librosFavoritos = servicioLibro.obtenerLibrosFavoritosDelUsuario(usuarioId);

        mav.addObject("libros", librosFavoritos);
        return mav;
    }

}
