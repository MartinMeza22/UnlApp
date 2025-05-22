package com.tallerwebi.dominio;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServicioMateria {

    private final ObjectMapper objectMapper;

    @Autowired
    public ServicioMateria(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<Cuatrimestre> obtenerMateriasPorCuatrimestre() {
        try {
            ClassPathResource classPathResource = new ClassPathResource("materias_cuatrimestre.json");
            InputStream inputStream = classPathResource.getInputStream();
            return objectMapper.readValue(inputStream, new TypeReference<List<Cuatrimestre>>() {});
        } catch (Exception e) {
            // Es buena práctica loguear el error completo para depuración
            e.printStackTrace();
            throw new RuntimeException("Error al leer el archivo JSON de cuatrimestres.", e);
        }
    }

    public List<Materia> obtenerTodasLasMaterias() {
        return obtenerMateriasPorCuatrimestre().stream()
                .flatMap(cuatrimestre -> cuatrimestre.getMaterias().stream())
                .collect(Collectors.toList());
    }

    // --- Métodos para la gestión del estudiante, ahora recibiendo Estudiante por parámetro ---

    public Optional<Materia> encontrarMateriaPorCodigo(Integer codigoMateria) {
        return obtenerTodasLasMaterias().stream()
                .filter(m -> Objects.equals(m.getCodigo(), codigoMateria))
                .findFirst();
    }

    public String marcarComoCursando(Estudiante estudiante, Integer codigoMateria) {
        Optional<Materia> materiaOpt = encontrarMateriaPorCodigo(codigoMateria);
        if (materiaOpt.isEmpty()) {
            return "Error: La materia no existe.";
        }
        Materia materia = materiaOpt.get();

        if (estudiante.yaAprobo(materia)) {
            return "Error: Ya has aprobado esta materia.";
        }
        if (estudiante.estaCursando(materia)) {
            return "Error: Ya estás cursando esta materia.";
        }
        if (!estudiante.cumpleCorrelativas(materia)) {
            return "Error: No cumples con las correlativas para cursar esta materia.";
        }

        estudiante.getMateriasCursando().add(materia);
        return "Materia " + materia.getNombre() + " marcada como cursando.";
    }

    public String marcarComoAprobada(Estudiante estudiante, Integer codigoMateria) {
        Optional<Materia> materiaOpt = encontrarMateriaPorCodigo(codigoMateria);
        if (materiaOpt.isEmpty()) {
            return "Error: La materia no existe.";
        }
        Materia materia = materiaOpt.get();

        if (estudiante.yaAprobo(materia)) {
            return "Error: Esta materia ya está aprobada.";
        }
        if (!estudiante.estaCursando(materia)) {
            return "Error: Para aprobar, debes estar cursando esta materia.";
        }

        // Si la estaba cursando, la removemos de la lista de cursando
        estudiante.getMateriasCursando().removeIf(m -> Objects.equals(m.getCodigo(), materia.getCodigo()));
        estudiante.getMateriasAprobadas().add(materia);
        return "Materia " + materia.getNombre() + " marcada como aprobada.";
    }

    public String quitarDeCursando(Estudiante estudiante, Integer codigoMateria) {
        Optional<Materia> materiaOpt = encontrarMateriaPorCodigo(codigoMateria);
        if (materiaOpt.isEmpty()) {
            return "Error: La materia no existe.";
        }
        Materia materia = materiaOpt.get();

        if (!estudiante.estaCursando(materia)) {
            return "Error: No estás cursando esta materia.";
        }

        estudiante.getMateriasCursando().removeIf(m -> Objects.equals(m.getCodigo(), materia.getCodigo()));
        return "Materia " + materia.getNombre() + " quitada de cursando.";
    }

    public String quitarDeAprobadas(Estudiante estudiante, Integer codigoMateria) {
        Optional<Materia> materiaOpt = encontrarMateriaPorCodigo(codigoMateria);
        if (materiaOpt.isEmpty()) {
            return "Error: La materia no existe.";
        }
        Materia materia = materiaOpt.get();

        if (!estudiante.yaAprobo(materia)) {
            return "Error: Esta materia no está aprobada.";
        }

        // Validación adicional: no permitir desaprobar si es correlativa de algo que está cursando o aprobado
        // (Esto es más complejo y para una demo puede no ser necesario, pero en un sistema real sería vital)
        boolean esCorrelativaDeAlgoCursandoOaprobado = estudiante.getMateriasCursando().stream()
                .anyMatch(m -> m.getCorrelatividad() != null && m.getCorrelatividad().contains(materia.getCodigo())) ||
                estudiante.getMateriasAprobadas().stream()
                        .anyMatch(m -> m.getCorrelatividad() != null && m.getCorrelatividad().contains(materia.getCodigo()));

        if (esCorrelativaDeAlgoCursandoOaprobado) {
            return "Error: No se puede desaprobar esta materia, ya que es correlativa de materias que estás cursando o ya tienes aprobadas.";
        }

        estudiante.getMateriasAprobadas().removeIf(m -> Objects.equals(m.getCodigo(), materia.getCodigo()));
        return "Materia " + materia.getNombre() + " quitada de aprobadas.";
    }
}