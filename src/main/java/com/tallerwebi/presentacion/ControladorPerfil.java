package com.tallerwebi.presentacion;

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

    private ServicioUsuario servicioUsuario;
    private RepositorioUsuario repositorioUsuario;
    private ServicioUsuarioMateria servicioUsuarioMateria;

    @Autowired
    public ControladorPerfil(ServicioUsuario servicioUsuario, RepositorioUsuario repositorioUsuario,ServicioUsuarioMateria servicioUsuarioMateria) {
        this.servicioUsuario = servicioUsuario;
        this.repositorioUsuario = repositorioUsuario;
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

        Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);
        if (usuario == null) {
            modelo.put("error", "Usuario no encontrado");
            return new ModelAndView("perfil", modelo);
        }

        modelo.put("usuario", usuario);
        modelo.put("carrera", usuario.getCarrera());
        List<UsuarioMateria> materias = servicioUsuarioMateria
                .mostrarMateriasDeUsuario(
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
    public ModelAndView actualizarPerfil(@RequestParam("nombre") String nombre,
                                         @RequestParam("apellido") String apellido,
                                         @RequestParam("email") String email,
                                         @RequestParam(value = "nuevaPassword", required = false) String nuevaPassword,
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

            // Solo cambiar la contraseña si el campo fue completado
            if (nuevaPassword != null && !nuevaPassword.trim().isEmpty()) {
                usuario.setPassword(nuevaPassword);
            }

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
