package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.googleBookAPI.Item;
import com.tallerwebi.servicioInterfaz.ServicioLibro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

@Controller
public class ControladorBiblioteca {

    private final ServicioLibro servicioLibro;

    @Autowired
    public ControladorBiblioteca(ServicioLibro servicioLibro) {
        this.servicioLibro = servicioLibro;
    }

    @GetMapping("/biblioteca-digital")
    public String mostrarLibros(Model model, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("ID");
        List<Item> libros = servicioLibro.obtenerLibros();
        if (usuarioId != null) {
            Set<String> librosFavoritosIds = this.servicioLibro.obtenerIdsDeLibrosFavoritos(usuarioId);
            model.addAttribute("librosFavoritosIds", librosFavoritosIds);
        }
        model.addAttribute("libros", libros);
        return "vista-libros";
    }

    @PostMapping("/agregar-favoritos")
    public String agregarFavoritos(@RequestParam(name = "favorito") String idLibroFavorito, HttpSession session, RedirectAttributes redirectAttributes) {
        Long usuarioId = (Long) session.getAttribute("ID");
        if (usuarioId == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para guardar favoritos.");
            return "redirect:/biblioteca-digital";
        }
        try {
            this.servicioLibro.agregarLibroAFavoritos(usuarioId, idLibroFavorito);
            redirectAttributes.addFlashAttribute("exito_agregar", "¡Libro guardado en tus favoritos!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo guardar el libro.");
        }
        return "redirect:/biblioteca-digital";
    }

    @PostMapping("/eliminar-favorito")
    public String eliminarFavorito(@RequestParam(name = "favorito") String idLibroFavorito, HttpSession session, RedirectAttributes redirectAttributes) {
        Long usuarioId = (Long) session.getAttribute("ID");
        if (usuarioId == null) {
            redirectAttributes.addFlashAttribute("error", "Error de autenticación.");
            return "redirect:/biblioteca-digital";
        }
        try {
            servicioLibro.eliminarLibroDeFavoritos(usuarioId, idLibroFavorito);
            redirectAttributes.addFlashAttribute("exito_eliminar", "Libro eliminado de tus favoritos.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar el favorito.");
        }
        return "redirect:/biblioteca-digital";
    }

    @PostMapping("/eliminar-favorito-en-favoritos")
    public String eliminarFavoritoEnFavoritos(@RequestParam(name = "favorito") String idLibroFavorito,
                                              HttpSession session,
                                              RedirectAttributes redirectAttributes) {
        Long usuarioId = (Long) session.getAttribute("ID");

        try {
            servicioLibro.eliminarLibroDeFavoritos(usuarioId, idLibroFavorito);
            redirectAttributes.addFlashAttribute("exito_eliminar", "Libro eliminado de tus favoritos.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar el favorito.");
        }

        return "redirect:/libros-favoritos";
    }
}