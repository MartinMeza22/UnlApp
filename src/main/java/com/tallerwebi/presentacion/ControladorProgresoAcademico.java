package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Carrera;
import com.tallerwebi.dominio.Materia;
import com.tallerwebi.dominio.ServicioMateria;
import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.DTO.MateriaDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
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

        Double porcentajeProgreso = this.servicioProgreso.obtenerProgresoDeCarrera(usuarioId);
        Integer cantidadDeMateriasAprobadas = this.servicioProgreso.filtrarPor("aprobadas", usuarioId).size();
        Integer cantidadMateriasTotal = this.servicioProgreso.materias(usuarioId).size();
        Integer materiasEnCurso = this.servicioProgreso.filtrarPor("cursando", usuarioId).size();

        Carrera carrera = new Carrera();
        carrera.setNombre("Desarrollo Web");

//        List<Materia> materias = this.servicioMateria.obtenerTodasLasMaterias();

        model.put("carrera", carrera);
//      model.put("materias", materias);
        model.put("usuarioId", usuarioId);
        model.put("materiasTotales", materias);
        model.put("selectedCondicion", condicion);
        model.put("porcentajeCarrera", porcentajeProgreso);
        model.put("materiasEnCurso", materiasEnCurso);
        model.put("cantidadMateriasTotales", cantidadMateriasTotal);
        model.put("cantidadMateriasAprobadas", cantidadDeMateriasAprobadas);
        return new ModelAndView("progreso", model);
    }

    @RequestMapping(path = "/progreso/actualizar-materia", method = RequestMethod.POST)
    public String actualizarDatosMateria(
            @RequestParam(name = "nota", required = false) Double nota,
            @RequestParam(name = "dificultad", required = false) Integer dificultad,
            @RequestParam(name = "id") Long idMateria,
            HttpSession session,
            RedirectAttributes redirectAttributes // Para redireccionamiento a /progreso, ModelAndView no me funciono aca
    ) {
        Long usuarioId = (Long) session.getAttribute("ID");

        // Pude haber utilizado el servicio de UsuarioMateria, el metodo modificar, pero le falta le id del usuario al metodo modificar
        this.servicioProgreso.actualizarDatosMateria(usuarioId, idMateria, nota, dificultad);

        return "redirect:/progreso";
    }

}
