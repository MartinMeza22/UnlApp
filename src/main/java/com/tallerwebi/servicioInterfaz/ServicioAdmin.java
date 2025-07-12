package com.tallerwebi.servicioInterfaz;

import java.util.Map;

public interface ServicioAdmin {
    /** Cantidad total de usuarios agrupados por carrera (nombreCarrera → cantidad). */
    Map<String, Long> obtenerUsuariosPorCarrera();

    /** Cantidad total de usuarios agrupados por sexo (M/F/O → cantidad). */
    Map<String, Long> obtenerUsuariosPorGenero();

    /** Cantidad total de publicaciones agrupadas por carrera (nombreCarrera → cantidad). */
    Map<String, Long> obtenerPublicacionesPorCarrera();
}
