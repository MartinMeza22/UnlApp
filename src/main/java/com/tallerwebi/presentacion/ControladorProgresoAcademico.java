package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.DTO.ProgresoDTO;
import com.tallerwebi.dominio.servicios.ServicioMateria;
import com.tallerwebi.dominio.servicios.ServicioProgreso;
import com.tallerwebi.dominio.servicios.ServicioUsuarioMateria;
import com.tallerwebi.dto.MateriasDTO;
import com.tallerwebi.dto.MateriasWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.tallerwebi.dominio.DTO.MateriaDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ControladorProgresoAcademico {

    @Autowired
    private ServicioUsuarioMateria servicioUsuarioMateria;
    private ServicioProgreso servicioProgreso;
    private ServicioMateria servicioMateria;
    private UsuarioMateria usuarioMateria;

    public ControladorProgresoAcademico(ServicioProgreso servicioProgreso, ServicioMateria servicioMateria, ServicioUsuarioMateria servicioUsuarioMateria) {
        this.servicioProgreso = servicioProgreso;
        this.servicioMateria = servicioMateria;
        this.servicioUsuarioMateria = servicioUsuarioMateria;
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
        // Para obtener el id de la carrera
        Carrera carrera = this.servicioUsuarioMateria.obtenerUsuario(usuarioId).getCarrera();
        String idCarrera = carrera.getId().toString();
        List<MateriaDTO> materias = new ArrayList<>();

        if(condicion != null && !condicion.isEmpty() && cuatrimestre != null) {
            materias = this.servicioProgreso.filtrarPorCuatrimestreYEstado(idCarrera, cuatrimestre, condicion, usuarioId);
        }else if (condicion != null && !condicion.isEmpty()) {
            materias = this.servicioProgreso.filtrarPor(idCarrera, condicion, usuarioId);
        } else if (cuatrimestre != null) {
            materias = this.servicioProgreso.filtrarPorCuatrimestre(idCarrera, cuatrimestre, usuarioId);
        }else {
            materias = this.servicioProgreso.materias(idCarrera, usuarioId);
        }

        Double porcentajeProgreso = this.servicioProgreso.obtenerProgresoDeCarrera(idCarrera, usuarioId);
        Integer cantidadDeMateriasAprobadas = this.servicioProgreso.filtrarPor(idCarrera,"aprobadas", usuarioId).size();
        Integer cantidadMateriasTotal = this.servicioProgreso.materias(idCarrera, usuarioId).size();
        Integer materiasEnCurso = this.servicioProgreso.filtrarPor(idCarrera,"cursando", usuarioId).size();
        Double procentajeDesaprobadas = this.servicioProgreso.obtenerPorcentajeDeMateriasDesaprobadas(idCarrera, usuarioId);
        Double procentajeAprobadas = this.servicioProgreso.obtenerPorcentajeDeMateriasAprobadas(idCarrera, usuarioId);

//        List<Materia> materias = this.servicioMateria.obtenerTodasLasMaterias();

        model.put("carrera", carrera);
//      model.put("materias", materias);
        model.put("usuarioId", usuarioId);
        model.put("materiasTotales", materias);
        model.put("selectedCondicion", condicion);
        model.put("porcentajeCarrera", porcentajeProgreso);
        model.put("porcentajeDesaprobadas",  procentajeDesaprobadas);
        model.put("porcentajeAprobadas",  procentajeAprobadas);
        model.put("materiasEnCurso", materiasEnCurso);
        model.put("cantidadMateriasTotales", cantidadMateriasTotal);
        model.put("cantidadMateriasAprobadas", cantidadDeMateriasAprobadas);

        model.put("cuatrimestresDisponibles", cuatri);
        model.put("selectedCuatrimestre", cuatrimestresDisponibles);

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

    @PostMapping("/pruebaDeDatos")
    public ModelAndView guardarMateria(@ModelAttribute MateriasWrapper listadoMaterias,
                                       @ModelAttribute("datosLogin") DatosLogin datosLogin,
                                       HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("ID");
            for( MateriasDTO materias : listadoMaterias.getMaterias()) {
                if(materias.getNota() != null && materias.getDificultad() != null) {
                    servicioUsuarioMateria.asignarMateria(usuarioId, materias.getId(), materias.getNota(), materias.getDificultad(), materias.getEstado());
                }else if(materias.getEstado() == 2){ //estado == 2 (cursando)
                    servicioUsuarioMateria.asignarMateria(usuarioId, materias.getId(), materias.getEstado());
                }
            }
        return new ModelAndView("login");
    }

    @RequestMapping(path = "/progreso/actualizar-materia", method = RequestMethod.POST)
    public String actualizarDatosMateria(
            @RequestParam(name = "nota", required = false) Integer nota,
            @RequestParam(name = "dificultad", required = false) Integer dificultad,
            @RequestParam(name = "action") String action,
            @RequestParam(name = "id") Long idMateria,
            HttpSession session,
            RedirectAttributes redirectAttributes // Para redireccionamiento a /progreso, ModelAndView no me funciono aca
    ) {
        Long usuarioId = (Long) session.getAttribute("ID");

        if("guardarCambios".equalsIgnoreCase(action)) {

            if(nota == null || dificultad == null) {
                redirectAttributes.addFlashAttribute("error", "Los campos no pueden ir vacio");
                return "redirect:/progreso";
            }

            // Pude haber utilizado el servicio de UsuarioMateria, el metodo modificar, pero le falta le id del usuario al metodo modificar
            this.servicioProgreso.actualizarDatosMateria(usuarioId, idMateria, nota, dificultad);
        } else if("dejarDeCursar".equalsIgnoreCase(action)) {
            this.servicioProgreso.marcarMateriaComoPendiente(idMateria, usuarioId);
        }

        return "redirect:/progreso";
    }

    @RequestMapping(path = "/progreso/dejar-materia", method = RequestMethod.POST)
    public String dejarMateria(@RequestParam(name = "materiaId") Long idMateria, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("ID");
        this.servicioUsuarioMateria.eliminarMateria(usuarioId, idMateria);
        return "redirect:/progreso";
    }

//    @PostMapping(path = "/") //METODO PARA UTILIZAR API DE GRAFICOS SE ENCARGA MARTÍN
//
//    public String estadisticasPersonales(@RequestParam(name = "materiaId") Long idMateria,
//                                         HttpSession session) {
//        Long usuarioId = (Long) session.getAttribute("ID");
//
//        ProgresoDTO progreso = this.servicioUsuarioMateria.obtenerEstadisticaPorUsuario(usuarioId, idMateria);
//        return "redirect:/progreso";
//    }
    @GetMapping("/grafico")
    public String mostrarGrafico(Model model, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("ID");
        Long idMateria = 6L;
        ProgresoDTO progreso = servicioUsuarioMateria.obtenerEstadisticaPorUsuario(usuarioId,idMateria);
        System.out.println("DTO: " + progreso);

        model.addAttribute("progreso", progreso);
        return "grafico";
    }

    @RequestMapping(path = "/progreso/cursar-materia", method = RequestMethod.POST)
    public String cursarMateria(@RequestParam(name = "materiaId") Long idMateria, HttpSession session, RedirectAttributes redirectAttributes) {
        Long usuarioId = (Long) session.getAttribute("ID");
        boolean exito = this.servicioProgreso.marcarMateriaComoCursando(usuarioId, idMateria);

        if (!exito) {
            redirectAttributes.addFlashAttribute("error", "No se pudo cursar la materia. Verifique las correlativas.");
        }
        return "redirect:/progreso";
    }
}
