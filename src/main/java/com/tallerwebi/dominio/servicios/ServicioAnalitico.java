package com.tallerwebi.dominio.servicios;

import com.tallerwebi.repositorioInterfaz.RepositorioAnalitico;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.UsuarioMateria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service("servicioAnalitico")
@Transactional
public class ServicioAnalitico {

    private RepositorioAnalitico repositorioAnalitico;

    @Autowired
    public ServicioAnalitico(RepositorioAnalitico repositorioAnalitico) {
        this.repositorioAnalitico = repositorioAnalitico;
    }

    /**
     * Genera el analítico académico completo para un usuario
     */
    public Map<String, Object> generarAnaliticoCompleto(Long usuarioId) {
        try {
            // Validar usuario
            Usuario usuario = repositorioAnalitico.obtenerUsuarioConCarrera(usuarioId);
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado con ID: " + usuarioId);
            }

            // Obtener todos los datos necesarios
            List<UsuarioMateria> materiasUsuario = repositorioAnalitico.obtenerDatosCompletosParaAnalitico(usuarioId);
            Object[] estadisticas = repositorioAnalitico.obtenerEstadisticasGenerales(usuarioId);
            Long totalMateriasCarrera = repositorioAnalitico.contarTotalMateriasDeCarrera(usuarioId);

            // Estructurar el analítico
            Map<String, Object> analitico = new HashMap<>();

            // Información del estudiante
            analitico.put("estudiante", crearDatosEstudiante(usuario));

            // Historial académico
            analitico.put("materias", procesarMaterias(materiasUsuario));

            // Estadísticas académicas
            analitico.put("estadisticas", calcularEstadisticas(estadisticas, totalMateriasCarrera, materiasUsuario));

            // Metadatos del documento
            analitico.put("metadatos", crearMetadatos());

            return analitico;

        } catch (Exception e) {
            System.err.println("Error al generar analítico para usuario " + usuarioId + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al generar el analítico académico: " + e.getMessage(), e);
        }
    }

    /**
     * Crea la información del estudiante
     */
    private Map<String, Object> crearDatosEstudiante(Usuario usuario) {
        Map<String, Object> estudiante = new HashMap<>();

        estudiante.put("nombre", usuario.getNombre() + " " + usuario.getApellido());
        estudiante.put("email", usuario.getEmail());
        estudiante.put("telefono", usuario.getTelefono() != null ? usuario.getTelefono() : "No especificado");
        estudiante.put("situacionLaboral", usuario.getSituacionLaboral() != null ? usuario.getSituacionLaboral() : "No especificada");

        // Información de la carrera
        Map<String, Object> carrera = new HashMap<>();
        if (usuario.getCarrera() != null) {
            carrera.put("nombre", usuario.getCarrera().getNombre());
            carrera.put("descripcion", usuario.getCarrera().getDescripcion());
        } else {
            carrera.put("nombre", "No especificada");
            carrera.put("descripcion", "");
        }
        estudiante.put("carrera", carrera);

        return estudiante;
    }

    /**
     * Procesa las materias para el historial académico
     */
    private List<Map<String, Object>> procesarMaterias(List<UsuarioMateria> materiasUsuario) {
        List<Map<String, Object>> materias = new ArrayList<>();

        for (UsuarioMateria um : materiasUsuario) {
            Map<String, Object> materia = new HashMap<>();

            // Información básica de la materia
            materia.put("nombre", um.getMateria().getNombre());
            materia.put("cuatrimestre", um.getMateria().getCuatrimestre());
            materia.put("tipo", um.getMateria().getTipo());
            materia.put("cargaHoraria", um.getMateria().getCargaHoraria());

            // Información del cursado
            materia.put("estado", convertirEstadoATexto(um.getEstado()));
            materia.put("nota", um.getNota());
            materia.put("dificultadPercibida", convertirDificultadATexto(um.getDificultad()));

            // Fechas
            materia.put("fechaAsignacion", formatearFecha(um.getFechaAsignacion()));
            materia.put("fechaModificacion", formatearFecha(um.getFechaModificacion()));

            materias.add(materia);
        }

        return materias;
    }

    /**
     * Calcula las estadísticas académicas
     */
    private Map<String, Object> calcularEstadisticas(Object[] estadisticas, Long totalMateriasCarrera, List<UsuarioMateria> materiasUsuario) {
        Map<String, Object> stats = new HashMap<>();

        // Extraer datos de la consulta agregada
        Long totalCursadas = (Long) estadisticas[0];
        Long aprobadas = (Long) estadisticas[1];
        Long desaprobadas = (Long) estadisticas[2];
        Long cursando = (Long) estadisticas[3];
        Double promedio = (Double) estadisticas[4];
        Long horasAprobadas = (Long) estadisticas[5];

        // Valores seguros (evitar nulls)
        totalCursadas = totalCursadas != null ? totalCursadas : 0L;
        aprobadas = aprobadas != null ? aprobadas : 0L;
        desaprobadas = desaprobadas != null ? desaprobadas : 0L;
        cursando = cursando != null ? cursando : 0L;
        promedio = promedio != null ? promedio : 0.0;
        horasAprobadas = horasAprobadas != null ? horasAprobadas : 0L;
        totalMateriasCarrera = totalMateriasCarrera != null ? totalMateriasCarrera : 0L;

        // Cálculos principales
        Long materiasesPendientes = totalMateriasCarrera - totalCursadas;
        Double porcentajeAvance = totalMateriasCarrera > 0 ? (aprobadas.doubleValue() / totalMateriasCarrera.doubleValue()) * 100 : 0.0;

        // Llenar estadísticas
        stats.put("promedioGeneral", Math.round(promedio * 100.0) / 100.0);
        stats.put("materiasAprobadas", aprobadas);
        stats.put("materiasDesaprobadas", desaprobadas);
        stats.put("materiasCursando", cursando);
        stats.put("materiasPendientes", materiasesPendientes);
        stats.put("totalMateriasCarrera", totalMateriasCarrera);
        stats.put("porcentajeAvance", Math.round(porcentajeAvance * 100.0) / 100.0);
        stats.put("horasAcademicasAprobadas", horasAprobadas);

        // Estadísticas adicionales
        stats.put("materiasConNota", contarMateriasConNota(materiasUsuario));
        stats.put("materiasDeAltoRendimiento", contarMateriasAltoRendimiento(materiasUsuario));

        return stats;
    }

    /**
     * Crea los metadatos del documento
     */
    private Map<String, Object> crearMetadatos() {
        Map<String, Object> metadatos = new HashMap<>();

        // Usar Date tradicional para evitar problemas de serialización con Jackson
        Date ahora = new Date();
        String fechaEmision = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(ahora);

        metadatos.put("fechaEmision", fechaEmision);
        metadatos.put("version", "1.0");
        metadatos.put("validez", "Este documento es válido por 30 días desde su emisión");
        metadatos.put("institucion", "Universidad Nacional de La Matanza");

        return metadatos;
    }

    // ========== MÉTODOS AUXILIARES ==========

    private String convertirEstadoATexto(Integer estado) {
        if (estado == null) return "PENDIENTE";
        switch (estado) {
            case 1: return "PENDIENTE";
            case 2: return "CURSANDO";
            case 3: return "APROBADA";
            case 4: return "DESAPROBADA";
            default: return "ESTADO_DESCONOCIDO";
        }
    }

    private String convertirDificultadATexto(Integer dificultad) {
        if (dificultad == null) return "No especificada";
        switch (dificultad) {
            case 1: return "Fácil";
            case 2: return "Moderada";
            case 3: return "Difícil";
            default: return "No especificada";
        }
    }

    private String formatearFecha(Object fecha) {
        if (fecha == null) return "No especificada";

        try {
            // Si es Date tradicional
            if (fecha instanceof Date) {
                return new java.text.SimpleDateFormat("dd/MM/yyyy").format((Date) fecha);
            }

            // Si es LocalDate (Java 8+)
            if (fecha instanceof java.time.LocalDate) {
                java.time.LocalDate localDate = (java.time.LocalDate) fecha;
                return localDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }

            // Si es LocalDateTime (Java 8+)
            if (fecha instanceof java.time.LocalDateTime) {
                java.time.LocalDateTime localDateTime = (java.time.LocalDateTime) fecha;
                return localDateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            }

            // Fallback: usar toString
            return fecha.toString();

        } catch (Exception e) {
            System.err.println("Error al formatear fecha: " + e.getMessage());
            return "Fecha inválida";
        }
    }

    private Long contarMateriasConNota(List<UsuarioMateria> materias) {
        return materias.stream()
                .filter(um -> um.getNota() != null)
                .count();
    }

    private Long contarMateriasAltoRendimiento(List<UsuarioMateria> materias) {
        return materias.stream()
                .filter(um -> um.getNota() != null && um.getNota() >= 8)
                .count();
    }
}