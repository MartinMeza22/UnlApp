package com.tallerwebi.dominio.DTO;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.UsuarioMateria;

import java.util.List;
import java.util.Objects;

public class UsuarioYMateriasDTO {
    private Usuario usuario;
    private List<UsuarioMateria> materias;

    public UsuarioYMateriasDTO() {
        // Constructor vacío para frameworks y tests
    }

    public UsuarioYMateriasDTO(Usuario usuario, List<UsuarioMateria> materias) {
        this.usuario = usuario;
        this.materias = materias;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<UsuarioMateria> getMaterias() {
        return materias;
    }

    public void setMaterias(List<UsuarioMateria> materias) {
        this.materias = materias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsuarioYMateriasDTO)) return false;
        UsuarioYMateriasDTO that = (UsuarioYMateriasDTO) o;
        return Objects.equals(usuario, that.usuario) &&
                Objects.equals(materias, that.materias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuario, materias);
    }

    @Override
    public String toString() {
        return "UsuarioYMateriasDTO{" +
                "usuario=" + usuario +
                ", materias=" + materias +
                '}';
    }
}
