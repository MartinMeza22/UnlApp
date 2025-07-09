package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.dominio.servicios.ServicioPlanificadorInteligente;
import com.tallerwebi.dominio.servicios.ServicioUsuarioMateria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import javax.servlet.http.HttpSession;

@Controller
public class ControladorPlanificadorInteligente {

    @Autowired
    private ServicioPlanificadorInteligente servicioPlanificador;

    @Autowired
    private ServicioUsuarioMateria servicioUsuarioMateria;

    @GetMapping("/planificador-inteligente")
    public String verPlanificadorInteligente(Model modelo, HttpSession session) throws UsuarioNoEncontrado {
        Long usuarioId = (Long) session.getAttribute("ID");

        if (usuarioId == null) {
            modelo.addAttribute("error", "Usuario no autenticado.");
            return "planificador-inteligente";
        }

        String idCarrera = servicioUsuarioMateria.obtenerUsuario(usuarioId).getCarreraID().toString();
        String resultado = servicioPlanificador.generarPlanificacionInteligente(idCarrera, usuarioId);

        modelo.addAttribute("planificacion", resultado);
        return "planificador-inteligente";
    }
}


