package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.DTO.UsuarioYMateriasDTO;
import com.tallerwebi.dominio.ExportadoraDeExcel;
import com.tallerwebi.dominio.Reporte;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.dominio.servicios.ServicioUsuarioMateria;
import com.tallerwebi.servicioInterfaz.ServicioAdmin;
import com.tallerwebi.servicioInterfaz.ServicioReporte;
import com.tallerwebi.servicioInterfaz.ServicioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class ControladorAdmin {

    private final ServicioUsuario servicioUsuario;
    private final ServicioReporte servicioReporte;
    private final ServicioAdmin servicioAdmin;
    private final ServicioUsuarioMateria servicioUsuarioMateria;

    @Autowired
    public ControladorAdmin(ServicioUsuario servicioUsuario, ServicioReporte servicioReporte,ServicioAdmin servicioAdmin, ServicioUsuarioMateria servicioUsuarioMateria) {
        this.servicioUsuario = servicioUsuario;
        this.servicioReporte = servicioReporte;
        this.servicioAdmin = servicioAdmin;
        this.servicioUsuarioMateria = servicioUsuarioMateria;
    }

    @GetMapping("/panel")
    public ModelAndView mostrarPanelDeReportes(HttpSession session) {
        String rol = (String) session.getAttribute("ROL");
        Long idUsuario = (Long) session.getAttribute("ID");

        if (idUsuario == null || !"ADMIN".equals(rol)) {
            return new ModelAndView("redirect:/home");
        }

        ModelAndView mav = new ModelAndView("admin-panel");
        try {
            Usuario admin = servicioUsuario.obtenerUsuario(idUsuario);
            List<Reporte> reportes = servicioReporte.obtenerReportesPorCarrera(admin.getCarrera());
            mav.addObject("reportes", reportes);
            mav.addObject("admin", admin);
        } catch (UsuarioNoEncontrado e) {
            return new ModelAndView("redirect:/login");
        }

        return mav;
    }
    @PostMapping("/reporte/eliminar")
    public ModelAndView eliminarReporte(@RequestParam Long idReporte, RedirectAttributes redirectAttributes) {
        try {
            servicioReporte.eliminarReporte(idReporte);
            redirectAttributes.addFlashAttribute("exito", "El reporte fue descartado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al descartar el reporte: " + e.getMessage());
        }
        return new ModelAndView("redirect:/admin/panel");
    }

    @GetMapping("/graficos")
    public ModelAndView mostrarGraficos(HttpSession session) {
        String rol = (String) session.getAttribute("ROL");
        Long id = (Long) session.getAttribute("ID");

        if (id == null || !"ADMIN".equals(rol)) {
            return new ModelAndView("redirect:/home");
        }

        ModelAndView mav = new ModelAndView("admin-graficos");

        mav.addObject("usuariosPorCarrera", servicioAdmin.obtenerUsuariosPorCarrera());
        mav.addObject("usuariosPorSituacionLaboral", servicioAdmin.obtenerUsuariosPorSituacionLaboral());
        mav.addObject("publicacionesPorCarrera", servicioAdmin.obtenerPublicacionesPorCarrera());

        return mav;
    }

    @GetMapping("/exportar-excel")
    public void exportarProgreso(HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=progreso.xlsx");

            List<UsuarioYMateriasDTO> datos = servicioUsuarioMateria.obtenerUsuariosConMaterias();

            ExportadoraDeExcel.exportarProgresoUsuarios(datos, response.getOutputStream());

            response.flushBuffer();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al exportar Excel", e);
        }
    }
}
