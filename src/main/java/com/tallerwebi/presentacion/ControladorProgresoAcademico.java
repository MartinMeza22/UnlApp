package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dto.MateriasDTO;
import com.tallerwebi.dto.MateriasWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class ControladorProgresoAcademico {

    @Autowired
    private ServicioUsuarioMateria servicioUsuarioMateria;

    @RequestMapping(path = "/progreso", method = RequestMethod.GET)
    public ModelAndView verProgreso() {
        ModelMap model = new ModelMap();//key / value
        Carrera carrera = new Carrera();
        carrera.setNombre("Desarrollo Web");
        model.put("carrera", carrera);
        return new ModelAndView("progreso", model);
    }

//    @PostMapping("/progresoDesdeElRegistro") //metodo util
//    public ModelAndView cargarMaterias(@RequestParam Map<String, String> datos) {
//        String id = datos.get("id");
//        String nota = datos.get("nota");
//        String dificultad = datos.get("dificultad");
//        String materia = datos.get("materia");
//        String usuario = datos.get("usuario");
//
//        Double idNota = Double.parseDouble(nota);
//        Integer dificultadParse = Integer.parseInt(dificultad);
//        Long idMateria = Long.parseLong(materia);
//        Long idUsuario = Long.parseLong(usuario);
//
//        String observaciones = datos.get("observaciones");
//        servicioUsuarioMateria.asignarMateria(idUsuario, idMateria,dificultadParse);
//        return new ModelAndView("home");
//    }

//    @PostMapping("/pruebaDeDatos")
//    public ModelAndView pruebaDeDatosLoca(@RequestParam Long usuarioID,
//                                          @RequestParam Long materiaID,
//                                          @RequestParam Integer dificultad,
//                                          @RequestParam Double nota,
//                                          @RequestParam String observaciones) {
//        servicioUsuarioMateria.asignarMateria(usuarioID,materiaID,dificultad);
//
//        return new ModelAndView("home");
//    }

    @PostMapping("/pruebaDeDatos")
    public ModelAndView guardarMateria(@ModelAttribute MateriasWrapper listadoMaterias,
                                       @ModelAttribute("datosLogin") DatosLogin datosLogin) {
            for( MateriasDTO materias : listadoMaterias.getMaterias()) {
                servicioUsuarioMateria.asignarMateria(3L, materias.getId(), materias.getNota(), materias.getDificultad());
            }
        return new ModelAndView("login");
    }
}
