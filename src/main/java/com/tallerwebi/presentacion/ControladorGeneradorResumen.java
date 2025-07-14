package com.tallerwebi.presentacion;


import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.dominio.servicios.ServicioGeneradorResumen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class ControladorGeneradorResumen {

    @Autowired
    private ServicioGeneradorResumen servicioGeneradorResumen;

    @PostMapping("/generar-resumen")
    public String generarResumen(@RequestParam("tema") String tema,
                                 HttpSession session,
                                 Model modelo) throws UsuarioNoEncontrado {

        Long usuarioId = (Long) session.getAttribute("ID");
        if (usuarioId == null) {
            return "redirect:/login";
        }

        String resumen = servicioGeneradorResumen.generarYGuardarResumen(tema, usuarioId);
        modelo.addAttribute("resumenGenerado", resumen);

        return "generar-resumen";
    }

    @GetMapping("/generar-resumen")
    public String mostrarFormulario() {
        return "generar-resumen";
    }
}
