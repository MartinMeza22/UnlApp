package com.tallerwebi.repositorioInterfaz;

import com.tallerwebi.dominio.*;
import java.util.List;

public interface RepositorioReporte {
    void guardar(Reporte reporte);
    Reporte buscarReportePorUsuarioYPublicacion(Usuario usuario, Publicacion publicacion);
    Reporte buscarReportePorUsuarioYComentario(Usuario usuario, Comentario comentario);
    List<Reporte> buscarReportesPorCarrera(Carrera carrera);
    Reporte buscarPorId(Long id);
    void eliminar(Reporte reporte);
}