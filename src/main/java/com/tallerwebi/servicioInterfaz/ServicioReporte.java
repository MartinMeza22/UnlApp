package com.tallerwebi.servicioInterfaz;

import com.tallerwebi.dominio.Carrera;
import com.tallerwebi.dominio.Reporte;
import com.tallerwebi.dominio.excepcion.*;
import java.util.List;

public interface ServicioReporte {
    void crearReporteParaPublicacion(Long idPublicacion, Long idUsuario, String motivo, String desc) throws PublicacionInexistente, UsuarioNoEncontrado, ReporteExistente;
    void crearReporteParaComentario(Long idComentario, Long idUsuario, String motivo, String desc) throws ComentarioInexistente, UsuarioNoEncontrado, ReporteExistente;
    List<Reporte> obtenerReportesPorCarrera(Carrera carrera);
    void eliminarReporte(Long idReporte) throws ReporteInexistente;
}
