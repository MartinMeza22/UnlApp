package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.DTO.MateriaDTO;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.dominio.servicios.ServicioProgreso;
import com.tallerwebi.dominio.servicios.ServicioResumenInteligente;
import com.tallerwebi.dominio.servicios.ServicioUsuarioMateria;
import com.tallerwebi.dominio.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class ControladorResumenInteligente {

    private final ServicioResumenInteligente servicioResumenInteligente;
    private final ServicioUsuarioMateria servicioUsuarioMateria;
    private final ServicioProgreso servicioProgreso;

    @Autowired
    public ControladorResumenInteligente(
            ServicioResumenInteligente servicioResumenInteligente,
            ServicioUsuarioMateria servicioUsuarioMateria,
            ServicioProgreso servicioProgreso
    ) {
        this.servicioResumenInteligente = servicioResumenInteligente;
        this.servicioUsuarioMateria = servicioUsuarioMateria;
        this.servicioProgreso = servicioProgreso;
    }

    @GetMapping("/resumen-inteligente")
    public String generarResumenAcademico(HttpSession session) throws UsuarioNoEncontrado {
        Long usuarioId = (Long) session.getAttribute("ID");

        if (usuarioId == null) {
            return "Error: usuario no autenticado.";
        }

        String idCarrera = this.servicioUsuarioMateria.obtenerUsuario(usuarioId).getCarreraID().toString();
        List<MateriaDTO> materias = servicioProgreso.materias(idCarrera, usuarioId);
        Double progreso = servicioProgreso.obtenerProgresoDeCarrera(idCarrera, usuarioId);

        return servicioResumenInteligente.generarYGuardarResumen(usuarioId, materias, progreso);
    }

    @GetMapping("/resumen-inteligente-historico")
    public String generarResumenHistorico(HttpSession session) throws UsuarioNoEncontrado {
        Long usuarioId = (Long) session.getAttribute("ID");

        if (usuarioId == null) {
            return "Error: usuario no autenticado.";
        }

        return servicioResumenInteligente.generarResumenHistorico(usuarioId);
    }

    @GetMapping("/nombre-usuario")
    @ResponseBody
    public String obtenerNombreUsuario(HttpSession session) {
        Usuario usuario = servicioUsuarioMateria.obtenerUsuario((Long) session.getAttribute("ID"));
        return usuario.getNombre();
    }

}
