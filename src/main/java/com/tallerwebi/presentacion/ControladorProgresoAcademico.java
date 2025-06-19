package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dto.MateriasDTO;
import com.tallerwebi.dto.MateriasWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.tallerwebi.dominio.DTO.MateriaDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class ControladorProgresoAcademico {

    @Autowired
    private ServicioUsuarioMateria servicioUsuarioMateria;
    private ServicioProgreso servicioProgreso;
    private ServicioMateria servicioMateria;
    private UsuarioMateria usuarioMateria;

    public ControladorProgresoAcademico(ServicioProgreso servicioProgreso, ServicioMateria servicioMateria) {
        this.servicioProgreso = servicioProgreso;
        this.servicioMateria = servicioMateria;
    }

    @RequestMapping(path = "/progreso", method = RequestMethod.GET)
    public ModelAndView verProgreso(@RequestParam(name = "condicion", required = false) String condicion,
                                    @RequestParam(name = "cuatrimestre", required = false) Integer cuatrimestre,
                                    HttpSession session) {
        ModelMap model = new ModelMap();//key / value
        List<Integer> cuatrimestresDisponibles = servicioMateria.obtenerCantidadDeCuatrimestres();
        Integer cuatri = 0;
        for (Integer i : cuatrimestresDisponibles) {
            cuatri = i+1;
        }

        Long usuarioId = (Long) session.getAttribute("ID");

        List<MateriaDTO> materias = new ArrayList<>();

        if(condicion != null && !condicion.isEmpty() && cuatrimestre != null) {
            materias = this.servicioProgreso.filtrarPorCuatrimestreYEstado(cuatrimestre, condicion, usuarioId);
        }else if (condicion != null && !condicion.isEmpty()) {
            materias = this.servicioProgreso.filtrarPor(condicion, usuarioId);
        } else if (cuatrimestre != null) {
            materias = this.servicioProgreso.filtrarPorCuatrimestre(cuatrimestre, usuarioId);
        }else {
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

        model.put("cuatrimestresDisponibles", cuatri);
        model.put("selectedCuatrimestre", cuatrimestresDisponibles);

        return new ModelAndView("progreso", model);
    }


    @PostMapping("/pruebaDeDatos")
    public ModelAndView guardarMateria(@ModelAttribute MateriasWrapper listadoMaterias,
                                       @ModelAttribute("datosLogin") DatosLogin datosLogin) {
            for( MateriasDTO materias : listadoMaterias.getMaterias()) {
                if(materias.getNota() != null && materias.getDificultad() != null) {
                    servicioUsuarioMateria.asignarMateria(3L, materias.getId(), materias.getNota(), materias.getDificultad(), materias.getEstado());
                }else if(materias.getEstado() == 2){ //estado == 2 (cursando)
                    servicioUsuarioMateria.asignarMateria(3L, materias.getId(), materias.getEstado());
                }
            }
        return new ModelAndView("login");
    }

    @RequestMapping(path = "/progreso/actualizar-materia", method = RequestMethod.POST)
    public String actualizarDatosMateria(
            @RequestParam(name = "nota", required = false) Integer nota,
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

    @RequestMapping(path = "/progreso/dejar-materia", method = RequestMethod.POST)
    public String dejarMateria(@RequestParam(name = "materiaId") Long idMateria, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("ID");
        this.servicioUsuarioMateria.eliminarMateria(usuarioId, idMateria);
        return "redirect:/progreso";
    }

}
