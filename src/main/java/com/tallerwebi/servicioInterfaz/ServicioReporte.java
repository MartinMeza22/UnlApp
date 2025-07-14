package com.tallerwebi.servicioInterfaz;

import com.tallerwebi.dominio.Carrera;
import com.tallerwebi.dominio.Reporte;
import com.tallerwebi.dominio.excepcion.*;
import java.util.List;

public interface ServicioReporte {
    Reporte crearReporteParaPublicacion(Long idPublicacion, Long idUsuario, String motivo, String desc) throws PublicacionInexistente, UsuarioNoEncontrado, ReporteExistente;
    Reporte crearReporteParaComentario(Long idComentario, Long idUsuario, String motivo, String desc) throws ComentarioInexistente, UsuarioNoEncontrado, ReporteExistente;
    List<Reporte> obtenerReportesPorCarrera(Carrera carrera);
    void eliminarReporte(Long idReporte) throws ReporteInexistente;

    Reporte obtenerReportePorId(Long idReporte) throws ReporteInexistente;

    void eliminarReportesDePublicacion(Long idPublicacion);

    void eliminarReportesDeComentario(Long idComentario);
}
