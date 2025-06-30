package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.DTO.MateriaDTO;
import com.tallerwebi.dominio.ServicioProgreso;
import com.tallerwebi.dominio.ServicioResumenInteligente;
import com.tallerwebi.dominio.ServicioUsuarioMateria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class ControladorResumenInteligente {

    @Autowired
    private ServicioResumenInteligente servicioResumenInteligente;
    private ServicioUsuarioMateria  servicioUsuarioMateria;

    @Autowired
    private ServicioProgreso servicioProgreso;

    public ControladorResumenInteligente(ServicioResumenInteligente servicioResumenInteligente, ServicioUsuarioMateria servicioUsuarioMateria, ServicioProgreso servicioProgreso) {
        this.servicioResumenInteligente = servicioResumenInteligente;
        this.servicioUsuarioMateria = servicioUsuarioMateria;
        this.servicioProgreso = servicioProgreso;
    }

    @GetMapping("/resumen-inteligente")
    public String generarResumenAcademico(HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("ID");

        if (usuarioId == null) {
            return "Error: usuario no autenticado.";
        }

        String idCarrera = this.servicioUsuarioMateria.obtenerUsuario(usuarioId).getCarreraID().toString();

        List<MateriaDTO> materias = servicioProgreso.materias(idCarrera, usuarioId);
        Double progreso = servicioProgreso.obtenerProgresoDeCarrera(idCarrera, usuarioId);

        String prompt = servicioResumenInteligente.generarPrompt(materias, progreso);
        return servicioResumenInteligente.generarResumenDesdePrompt(prompt);
    }
}