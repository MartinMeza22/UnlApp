package com.tallerwebi.dominio.DTO;

public class ProgresoDTO {

    private Long totalMaterias;
    private  Long materiasAprobadasPorUsuario;
    private  Long materiasDesaprobadasPorUsuario;
    private  Long materiasTotalesPorUsuario;
    private  Long materiasCursandoPorUsuario;
    private  Long materiasPendientes;

    public ProgresoDTO(Long totalMaterias, Long materiasCursandoPorUsuario, Long materiasTotalesPorUsuario,
                       Long materiasDesaprobadasPorUsuario, Long materiasAprobadasPorUsuario, Long materiasPendientes) {
        this.totalMaterias = totalMaterias;
        this.materiasCursandoPorUsuario = materiasCursandoPorUsuario;
        this.materiasTotalesPorUsuario = materiasTotalesPorUsuario;
        this.materiasDesaprobadasPorUsuario = materiasDesaprobadasPorUsuario;
        this.materiasAprobadasPorUsuario = materiasAprobadasPorUsuario;
        this.materiasPendientes = materiasPendientes;
    }

    public Long getTotalMaterias() {
        return totalMaterias;
    }

    public void setTotalMaterias(Long totalMaterias) {
        this.totalMaterias = totalMaterias;
    }

    public Long getMateriasCursandoPorUsuario() {
        return materiasCursandoPorUsuario;
    }

    public void setMateriasCursandoPorUsuario(Long materiasCursandoPorUsuario) {
        this.materiasCursandoPorUsuario = materiasCursandoPorUsuario;
    }

    public Long getMateriasTotalesPorUsuario() {
        return materiasTotalesPorUsuario;
    }

    public void setMateriasTotalesPorUsuario(Long materiasTotalesPorUsuario) {
        this.materiasTotalesPorUsuario = materiasTotalesPorUsuario;
    }

    public Long getMateriasDesaprobadasPorUsuario() {
        return materiasDesaprobadasPorUsuario;
    }

    public void setMateriasDesaprobadasPorUsuario(Long materiasDesaprobadasPorUsuario) {
        this.materiasDesaprobadasPorUsuario = materiasDesaprobadasPorUsuario;
    }

    public Long getMateriasAprobadasPorUsuario() {
        return materiasAprobadasPorUsuario;
    }

    public void setMateriasAprobadasPorUsuario(Long materiasAprobadasPorUsuario) {
        this.materiasAprobadasPorUsuario = materiasAprobadasPorUsuario;
    }

    public Long getMateriasPendientes() {
        return materiasPendientes;
    }

    public void setMateriasPendientes(Long materiasPendientes) {
        this.materiasPendientes = materiasPendientes;
    }
}
