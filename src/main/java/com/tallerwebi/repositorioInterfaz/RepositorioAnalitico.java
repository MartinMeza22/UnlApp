package com.tallerwebi.repositorioInterfaz;

import com.tallerwebi.dominio.UsuarioMateria;
import com.tallerwebi.dominio.Usuario;

import java.util.List;

public interface RepositorioAnalitico {

    /**
     * Obtiene todos los datos necesarios para generar el analítico académico
     * en una sola consulta optimizada con JOINs
     */
    List<UsuarioMateria> obtenerDatosCompletosParaAnalitico(Long usuarioId);

    /**
     * Obtiene los datos básicos del usuario con información de carrera
     */
    Usuario obtenerUsuarioConCarrera(Long usuarioId);

    /**
     * Cuenta el total de materias de la carrera del usuario
     */
    Long contarTotalMateriasDeCarrera(Long usuarioId);

    /**
     * Obtiene estadísticas agregadas del usuario para el analítico
     */
    Object[] obtenerEstadisticasGenerales(Long usuarioId);
}