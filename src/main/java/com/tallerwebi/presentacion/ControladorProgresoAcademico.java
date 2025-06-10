package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.DTO.MateriaDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ControladorProgresoAcademico {

    private ServicioProgreso servicioProgreso;
    private ServicioMateria servicioMateria;
    private UsuarioMateria usuarioMateria;

    public ControladorProgresoAcademico(ServicioProgreso servicioProgreso, ServicioMateria servicioMateria) {
        this.servicioProgreso = servicioProgreso;
        this.servicioMateria = servicioMateria;
    }

    @RequestMapping(path = "/progreso", method = RequestMethod.GET)
    public ModelAndView verProgreso(@RequestParam(name = "condicion", required = false) String condicion, HttpSession session) {
        ModelMap model = new ModelMap();//key / value

        Long usuarioId = (Long) session.getAttribute("ID");

        List<MateriaDTO> materias = new ArrayList<>();
        if (condicion != null && !condicion.isEmpty()) {
            materias = this.servicioProgreso.filtrarPor(condicion, usuarioId);
        } else {
            materias = this.servicioProgreso.materias(usuarioId);
        }

        Carrera carrera = new Carrera();
        carrera.setNombre("Desarrollo Web");

//        List<Materia> materias = this.servicioMateria.obtenerTodasLasMaterias();

        model.put("carrera", carrera);
//      model.put("materias", materias);
        model.put("usuarioId", usuarioId);
        model.put("materiasTotales", materias);
        model.put("selectedCondicion", condicion);
        return new ModelAndView("progreso", model);
    }

    @RequestMapping(path = "/progreso/actualizar-materia", method = RequestMethod.POST)
    public String actualizarDatosMateria(
            @RequestParam(name = "nota", required = false) Double nota,
            @RequestParam(name = "dificultad", required = false) Integer dificultad,
            @RequestParam(name = "id") Long idMateria,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Long usuarioId = (Long) session.getAttribute("ID");

        this.servicioProgreso.actualizarDatosMateria(usuarioId, idMateria, nota, dificultad);

        return "redirect:/progreso";
    }

}
