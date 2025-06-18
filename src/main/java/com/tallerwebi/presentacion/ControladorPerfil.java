package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.ServicioUsuario;
import com.tallerwebi.dominio.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ControladorPerfil {

    private ServicioUsuario servicioUsuario;
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    public ControladorPerfil(ServicioUsuario servicioUsuario, RepositorioUsuario repositorioUsuario) {
        this.servicioUsuario = servicioUsuario;
        this.repositorioUsuario = repositorioUsuario;
    }

    // Mostrar perfil del usuario logueado
    @RequestMapping(path = "/perfil", method = RequestMethod.GET)
    public ModelAndView verPerfil(HttpServletRequest request) {
        ModelMap modelo = new ModelMap();

        Long usuarioId = (Long) request.getSession().getAttribute("ID");
        if (usuarioId == null) {
            return new ModelAndView("redirect:/login");
        }

        Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);
        if (usuario == null) {
            modelo.put("error", "Usuario no encontrado");
            return new ModelAndView("perfil", modelo);
        }

        modelo.put("usuario", usuario);
        return new ModelAndView("perfil", modelo);
    }

    // Actualizar información del perfil
    @RequestMapping(path = "/perfil/actualizar", method = RequestMethod.POST)
    public ModelAndView actualizarPerfil(@RequestParam("nombre") String nombre,
                                         @RequestParam("apellido") String apellido,
                                         @RequestParam("email") String email,
                                         HttpServletRequest request) {
        Long usuarioId = (Long) request.getSession().getAttribute("ID");
        if (usuarioId == null) {
            return new ModelAndView("redirect:/login");
        }

        Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);
        if (usuario == null) {
            return new ModelAndView("redirect:/perfil");
        }

        try {
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setEmail(email);
// 🔁 ACTUALIZAR LOS DATOS EN SESIÓN
            request.getSession().setAttribute("NOMBRE", usuario.getNombre());
            repositorioUsuario.modificar(usuario);
        } catch (Exception e) {
            e.printStackTrace();
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ModelAndView("redirect:/");
    }
}
