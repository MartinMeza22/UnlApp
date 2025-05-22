package com.tallerwebi.dominio;

import java.io.Serializable; // Es buena práctica para objetos en sesión
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Estudiante implements Serializable { // Implementar Serializable es recomendable para la sesión
    private String id;
    private List<Materia> materiasAprobadas;
    private List<Materia> materiasCursando;

    public Estudiante(String id) {
        this.id = id;
        this.materiasAprobadas = new ArrayList<>();
        this.materiasCursando = new ArrayList<>();
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Materia> getMateriasAprobadas() {
        return materiasAprobadas;
    }

    public void setMateriasAprobadas(List<Materia> materiasAprobadas) {
        this.materiasAprobadas = materiasAprobadas;
    }

    public List<Materia> getMateriasCursando() {
        return materiasCursando;
    }

    public void setMateriasCursando(List<Materia> materiasCursando) {
        this.materiasCursando = materiasCursando;
    }

    // Métodos de ayuda para la lógica
    public boolean yaAprobo(Materia materia) {
        return materiasAprobadas.stream().anyMatch(m -> Objects.equals(m.getCodigo(), materia.getCodigo()));
    }

    public boolean estaCursando(Materia materia) {
        return materiasCursando.stream().anyMatch(m -> Objects.equals(m.getCodigo(), materia.getCodigo()));
    }

    public boolean cumpleCorrelativas(Materia materia) {
        if (materia.getCorrelatividad() == null || materia.getCorrelatividad().isEmpty()) {
            return true; // No tiene correlativas, se puede cursar
        }
        // Todas las correlativas deben estar aprobadas
        return materia.getCorrelatividad().stream()
                .allMatch(correlativaCodigo -> materiasAprobadas.stream()
                        .anyMatch(aprobada -> Objects.equals(aprobada.getCodigo(), correlativaCodigo)));
    }
}