package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioUsuarioMateria {

    /**
     * Guarda una nueva relación usuario-materia
     */
    void guardar(UsuarioMateria usuarioMateria);

    /**
     * Busca una relación específica por ID
     */
    UsuarioMateria buscarPorId(Long id);

    /**
     * Busca una relación específica por usuario y materia
     */
    UsuarioMateria buscarPorUsuarioYMateria(Long usuarioId, Long materiaId);

    /**
     * Obtiene todas las materias de un usuario específico
     */
    List<UsuarioMateria> buscarPorUsuario(String idCarrera, Long usuarioId);

    /**
     * Obtiene todas las relaciones usuario-materia
     */
    List<UsuarioMateria> buscarTodas();

    /**
     * Actualiza una relación existente
     */
    void actualizar(UsuarioMateria usuarioMateria);

    /**
     * Elimina una relación específica
     */
    void eliminar(UsuarioMateria usuarioMateria);

    /**
     * Verifica si existe una relación entre usuario y materia
     */
    boolean existe(Long usuarioId, Long materiaId);

    UsuarioMateria buscarProgresoPersonal(Long usuarioId, Long materiaId);
    Long contadorDeMateriasPorUsuario(Long usuarioId);
    Long contadorDeMateriasAprobadasPorUsuario(Long usuarioId);
    Long contadorDeMateriasDesaprobadasPorUsuario(Long usuarioId);
    Long contadorDeMateriasCursandoPorUsuario(Long usuarioId);

    Long contadorDeMateriasAprobadasGeneralPorCarrera(Long carreraId);
    Long contadorDeMateriasDesaprobadasGeneralPorCarrera(Long carreraId);
    Long contadorDeMateriasCursandoGeneralPorCarrera(Long carreraId);

    Long contadorDeTotalDeUsuariosPorCarrera(Long carreraId);
}