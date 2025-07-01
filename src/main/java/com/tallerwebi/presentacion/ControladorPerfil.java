package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.dominio.servicios.ServicioUsuarioMateria;
import com.tallerwebi.repositorioInterfaz.RepositorioUsuario;
import com.tallerwebi.dominio.*;
import com.tallerwebi.servicioInterfaz.ServicioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ControladorPerfil {

    private final ServicioUsuario servicioUsuario;
    private final ServicioUsuarioMateria servicioUsuarioMateria;

    @Autowired
    public ControladorPerfil(ServicioUsuario servicioUsuario, ServicioUsuarioMateria servicioUsuarioMateria) {
        this.servicioUsuario = servicioUsuario;
        this.servicioUsuarioMateria = servicioUsuarioMateria;
    }

    // Mostrar perfil del usuario logueado
    @RequestMapping(path = "/perfil", method = RequestMethod.GET)
    public ModelAndView verPerfil(HttpServletRequest request) {
        ModelMap modelo = new ModelMap();

        Long usuarioId = (Long) request.getSession().getAttribute("ID");
        if (usuarioId == null) {
            return new ModelAndView("redirect:/login");
        }

        Usuario usuario;
        try {
            usuario = servicioUsuario.obtenerUsuario(usuarioId);
        } catch (UsuarioNoEncontrado e) {
            modelo.put("error", "Usuario no encontrado");
            return new ModelAndView("perfil", modelo);
        }

        modelo.put("usuario", usuario);
        modelo.put("carrera", usuario.getCarrera());

        List<UsuarioMateria> materias = servicioUsuarioMateria.mostrarMateriasDeUsuario(
                usuario.getCarrera() != null ? usuario.getCarrera().getId().toString() : null,
                usuarioId
        );

        List<UsuarioMateria> materiasAprobadas = materias.stream()
                .filter(UsuarioMateria::estaAprobada)
                .collect(Collectors.toList());

        modelo.put("materiasAprobadas", materiasAprobadas);
        return new ModelAndView("perfil", modelo);
    }

    @RequestMapping(path = "/perfil/actualizar", method = RequestMethod.POST)
    public ModelAndView actualizarPerfil(@RequestParam String nombre,
                                         @RequestParam String apellido,
                                         @RequestParam String email,
                                         @RequestParam(required = false) String nuevaPassword,
                                         HttpServletRequest request) {
        Long usuarioId = (Long) request.getSession().getAttribute("ID");
        if (usuarioId == null) {
            return new ModelAndView("redirect:/login");
        }

        try {
            servicioUsuario.actualizarPerfil(usuarioId, nombre, apellido, email, nuevaPassword);
            request.getSession().setAttribute("NOMBRE", nombre);
        } catch (UsuarioNoEncontrado e) {
            return new ModelAndView("redirect:/perfil");
        }

        return new ModelAndView("redirect:/perfil");
    }


    // Eliminar cuenta del usuario
    @RequestMapping(path = "/perfil/eliminar", method = RequestMethod.POST)
    public ModelAndView eliminarCuenta(HttpServletRequest request) {
        Long usuarioId = (Long) request.getSession().getAttribute("ID");
        if (usuarioId == null) {
            return new ModelAndView("redirect:/login");
        }

        try {
            servicioUsuario.eliminarUsuario(usuarioId);
            request.getSession().invalidate();
        } catch (UsuarioNoEncontrado e) {
            return new ModelAndView("redirect:/perfil");
        }

        return new ModelAndView("redirect:/");
    }
}
