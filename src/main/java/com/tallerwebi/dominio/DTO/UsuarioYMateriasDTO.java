package com.tallerwebi.dominio.DTO;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.UsuarioMateria;

import java.util.List;

public class UsuarioYMateriasDTO {
    private Usuario usuario;
    private List<UsuarioMateria> materias;

    public UsuarioYMateriasDTO(Usuario usuario, List<UsuarioMateria> materias) {
        this.usuario = usuario;
        this.materias = materias;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public List<UsuarioMateria> getMaterias() {
        return materias;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setMaterias(List<UsuarioMateria> materias) {
        this.materias = materias;
    }
}
