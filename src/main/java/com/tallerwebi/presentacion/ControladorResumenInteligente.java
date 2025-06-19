package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.DTO.MateriaDTO;
import com.tallerwebi.dominio.ServicioProgreso;
import com.tallerwebi.dominio.ServicioResumenInteligente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class ControladorResumenInteligente {

    @Autowired
    private ServicioResumenInteligente servicioResumenInteligente;

    @Autowired
    private ServicioProgreso servicioProgreso;

    @GetMapping("/resumen-inteligente")
    public String generarResumenAcademico(HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("ID");

        if (usuarioId == null) {
            return "Error: usuario no autenticado.";
        }

        List<MateriaDTO> materias = servicioProgreso.materias(usuarioId);
        Double progreso = servicioProgreso.obtenerProgresoDeCarrera(usuarioId);

        String prompt = servicioResumenInteligente.generarPrompt(materias, progreso);
        return servicioResumenInteligente.generarResumenDesdePrompt(prompt);
    }
}