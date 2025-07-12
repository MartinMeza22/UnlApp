package com.tallerwebi.servicioInterfaz;

import java.util.Map;

public interface ServicioAdmin {
    /** Cantidad total de usuarios agrupados por carrera (nombreCarrera → cantidad). */
    Map<String, Long> obtenerUsuariosPorCarrera();



    Map<String, Long> obtenerUsuariosPorSituacionLaboral();

    /** Cantidad total de publicaciones agrupadas por carrera (nombreCarrera → cantidad). */
    Map<String, Long> obtenerPublicacionesPorCarrera();
}
