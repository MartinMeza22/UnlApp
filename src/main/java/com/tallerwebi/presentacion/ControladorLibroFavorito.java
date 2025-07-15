//package com.tallerwebi.presentacion;
//
//import com.tallerwebi.dominio.excepcion.FavoritoYaExisteException;
//import com.tallerwebi.servicioInterfaz.ServicioLibro;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import javax.servlet.http.HttpSession;
//
//@Controller
//public class ControladorLibroFavorito {
//
//    private final ServicioLibro servicioLibro;
//
//    public ControladorLibroFavorito(ServicioLibro servicioLibro) {
//        this.servicioLibro = servicioLibro;
//    }
//
//    @PostMapping("/agregar-favoritos")
//    public String agregarFavoritos(@RequestParam(name = "favorito") String idLibroFavorito, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
//        Long usuarioId = (Long) session.getAttribute("ID");
//
//        System.out.println("EL ID DEL LIBRO ES: " + idLibroFavorito);
//
//        // try {
//            this.servicioLibro.agregarLibroAFavoritos(usuarioId, idLibroFavorito);
//       // }
////        catch (FavoritoYaExisteException e){
////            redirectAttributes.addFlashAttribute("info");
////        }
//
//        return "redirect:/biblioteca-digital";
//    }
//
//    @PostMapping("/eliminar-favorito")
//    public String eliminarFavorito(@RequestParam(name = "favorito") String idLibroFavorito,
//                                   HttpSession session,
//                                   RedirectAttributes redirectAttributes) {
//        Long usuarioId = (Long) session.getAttribute("ID");
//
//        try {
//            servicioLibro.eliminarLibroDeFavoritos(usuarioId, idLibroFavorito);
//            redirectAttributes.addFlashAttribute("exito", "Libro eliminado de tus favoritos.");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar el favorito.");
//        }
//
//        return "redirect:/biblioteca-digital";
//    }
//}
