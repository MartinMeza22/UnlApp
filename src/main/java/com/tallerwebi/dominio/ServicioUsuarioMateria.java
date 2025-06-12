package com.tallerwebi.dominio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("servicioUsuarioMateria")
@Transactional
public class ServicioUsuarioMateria {

    private RepositorioUsuarioMateria repositorioUsuarioMateria;
    private RepositorioUsuario repositorioUsuario;
    private RepositorioMateria repositorioMateria;

    @Autowired
    public ServicioUsuarioMateria(RepositorioUsuarioMateria repositorioUsuarioMateria,
                                  RepositorioUsuario repositorioUsuario,
                                  RepositorioMateria repositorioMateria) {
        this.repositorioUsuarioMateria = repositorioUsuarioMateria;
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioMateria = repositorioMateria;
    }

    /**
     * Asigna una materia a un usuario (empieza cursando = nota null)
     */
    public UsuarioMateria asignarMateria(Long usuarioId, Long materiaId, Integer dificultad) {
        // Validaciones básicas
        validarUsuarioYMateria(usuarioId, materiaId);

        // Verificar que el usuario existe
        Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario con ID " + usuarioId + " no existe");
        }

        // Verificar que la materia existe
        Materia materia = repositorioMateria.buscarPorId(materiaId);
        if (materia == null) {
            throw new IllegalArgumentException("La materia con ID " + materiaId + " no existe");
        }

        // Verificar que no esté ya asignada
        if (repositorioUsuarioMateria.existe(usuarioId, materiaId)) {
            throw new IllegalArgumentException("El usuario ya tiene asignada esta materia");
        }

        // Validar dificultad
        validarDificultad(dificultad);

        // Crear la relación (nota = null = cursando)
        UsuarioMateria usuarioMateria = new UsuarioMateria(usuario, materia);
        usuarioMateria.setDificultad(dificultad);

        // Guardar
        repositorioUsuarioMateria.guardar(usuarioMateria);

        return usuarioMateria;
    }

    public UsuarioMateria asignarMateria(Long usuarioId, Long materiaId, Integer nota, Integer dificultad) {
        // Validaciones básicas
        validarUsuarioYMateria(usuarioId, materiaId);

        // Verificar que el usuario existe
        Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario con ID " + usuarioId + " no existe");
        }

        // Verificar que la materia existe
        Materia materia = repositorioMateria.buscarPorId(materiaId);
        if (materia == null) {
            throw new IllegalArgumentException("La materia con ID " + materiaId + " no existe");
        }

        // Verificar que no esté ya asignada
        if (repositorioUsuarioMateria.existe(usuarioId, materiaId)) {
            throw new IllegalArgumentException("El usuario ya tiene asignada esta materia");
        }

        // Validar dificultad
        validarDificultad(dificultad);
        validarNota(nota);

        // Crear la relación (nota = null = cursando)
        UsuarioMateria usuarioMateria = new UsuarioMateria(usuario, materia);
        usuarioMateria.setDificultad(dificultad);
        usuarioMateria.setNota(nota);
        // Guardar
        repositorioUsuarioMateria.guardar(usuarioMateria);

        return usuarioMateria;
    }

    /**
     * Asigna una materia sin especificar dificultad
     */
    public UsuarioMateria asignarMateria(Long usuarioId, Long materiaId) {
        return asignarMateria(usuarioId, materiaId, null);
    }

    /**
     * Modifica una materia cursada (nota, dificultad, observaciones)
     */
    public UsuarioMateria modificarMateriaCursada(Long usuarioMateriaId, Integer nota,
                                                  Integer dificultad) {
        // Validaciones
        if (usuarioMateriaId == null) {
            throw new IllegalArgumentException("El ID de la relación usuario-materia es obligatorio");
        }

        UsuarioMateria usuarioMateria = repositorioUsuarioMateria.buscarPorId(usuarioMateriaId);
        if (usuarioMateria == null) {
            throw new IllegalArgumentException("La relación usuario-materia con ID " + usuarioMateriaId + " no existe");
        }

        // Validar nota (puede ser null para seguir cursando)
        if (nota != null && (nota < 0 || nota > 10)) {
            throw new IllegalArgumentException("La nota debe estar entre 0 y 10, o null para seguir cursando");
        }

        // Validar dificultad
        validarDificultad(dificultad);
        validarNota(nota);

        // Actualizar campos
        if (nota != null || nota == null) { // Permitir set de null explícitamente
            usuarioMateria.setNota(nota);
        }
        if (dificultad != null) {
            usuarioMateria.setDificultad(dificultad);
        }

        // Guardar cambios
        repositorioUsuarioMateria.actualizar(usuarioMateria);

        return usuarioMateria;
    }

    /**
     * Elimina una materia de un usuario
     */
    public void eliminarMateria(Long usuarioId, Long materiaId) {
        validarUsuarioYMateria(usuarioId, materiaId);

        UsuarioMateria usuarioMateria = repositorioUsuarioMateria.buscarPorUsuarioYMateria(usuarioId, materiaId);
        if (usuarioMateria == null) {
            throw new IllegalArgumentException("No existe relación entre el usuario y la materia especificados");
        }

        repositorioUsuarioMateria.eliminar(usuarioMateria);
    }

    /**
     * Elimina una materia por ID de la relación
     */
    public void eliminarMateria(Long usuarioMateriaId) {
        if (usuarioMateriaId == null) {
            throw new IllegalArgumentException("El ID de la relación usuario-materia es obligatorio");
        }

        UsuarioMateria usuarioMateria = repositorioUsuarioMateria.buscarPorId(usuarioMateriaId);
        if (usuarioMateria == null) {
            throw new IllegalArgumentException("La relación usuario-materia con ID " + usuarioMateriaId + " no existe");
        }

        repositorioUsuarioMateria.eliminar(usuarioMateria);
    }

    /**
     * Muestra todas las materias de todos los usuarios
     */
    public List<UsuarioMateria> mostrarTodasLasMateriasDeUsuarios() {
        return repositorioUsuarioMateria.buscarTodas();
    }

    /**
     * Muestra todas las materias de un usuario específico
     */
    public List<UsuarioMateria> mostrarMateriasDeUsuario(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario es obligatorio");
        }

        return repositorioUsuarioMateria.buscarPorUsuario(usuarioId);
    }

    /**
     * Obtiene estadísticas básicas de un usuario
     */
    public EstadisticasUsuario obtenerEstadisticasUsuario(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario es obligatorio");
        }

        List<UsuarioMateria> todasLasMaterias = mostrarMateriasDeUsuario(usuarioId);

        long aprobadas = todasLasMaterias.stream().filter(UsuarioMateria::estaAprobada).count();
        long cursando = todasLasMaterias.stream().filter(UsuarioMateria::estaCursando).count();
        long desaprobadas = todasLasMaterias.stream().filter(UsuarioMateria::estaDesaprobada).count();

        double promedioNotas = todasLasMaterias.stream()
                .filter(um -> um.getNota() != null)
                .mapToDouble(UsuarioMateria::getNota)
                .average()
                .orElse(0.0);

        return new EstadisticasUsuario(todasLasMaterias.size(), aprobadas, cursando, desaprobadas, promedioNotas);
    }

    // Métodos auxiliares privados de validación
    private void validarUsuarioYMateria(Long usuarioId, Long materiaId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario es obligatorio");
        }
        if (materiaId == null) {
            throw new IllegalArgumentException("El ID de la materia es obligatorio");
        }
    }

    private void validarDificultad(Integer dificultad) {
        if (dificultad != null && (dificultad < 1 || dificultad > 3)) {
            throw new IllegalArgumentException("La dificultad debe estar entre 1 y 3");
        }
    }

    private void validarNota(Integer nota) {
        if (nota != null && (nota < 1 || nota > 10)) {
            throw new IllegalArgumentException("La dificultad debe estar entre 1 y 10");
        }
    }

    // Clase interna para estadísticas
    public static class EstadisticasUsuario {
        private final int totalMaterias;
        private final long materiasAprobadas;
        private final long materiasCursando;
        private final long materiasDesaprobadas;
        private final double promedioNotas;

        public EstadisticasUsuario(int totalMaterias, long materiasAprobadas, long materiasCursando,
                                   long materiasDesaprobadas, double promedioNotas) {
            this.totalMaterias = totalMaterias;
            this.materiasAprobadas = materiasAprobadas;
            this.materiasCursando = materiasCursando;
            this.materiasDesaprobadas = materiasDesaprobadas;
            this.promedioNotas = promedioNotas;
        }

        // Getters
        public int getTotalMaterias() {
            return totalMaterias;
        }

        public long getMateriasAprobadas() {
            return materiasAprobadas;
        }

        public long getMateriasCursando() {
            return materiasCursando;
        }

        public long getMateriasDesaprobadas() {
            return materiasDesaprobadas;
        }

        public double getPromedioNotas() {
            return promedioNotas;
        }

        @Override
        public String toString() {
            return String.format("Estadísticas: %d total, %d aprobadas, %d cursando, %d desaprobadas, promedio: %.2f",
                    totalMaterias, materiasAprobadas, materiasCursando, materiasDesaprobadas, promedioNotas);
        }
    }

    public Usuario obtenerUsuario(Long idUsuario) {

        return this.repositorioUsuario.buscarPorId(idUsuario);
    }

}