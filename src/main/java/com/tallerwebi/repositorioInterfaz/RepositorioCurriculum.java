package com.tallerwebi.repositorioInterfaz;

import com.tallerwebi.dominio.Curriculum;
import com.tallerwebi.dominio.Usuario;

import java.util.List;

public interface RepositorioCurriculum {
    void guardar(Curriculum cv);
    List<Curriculum> obtenerPorUsuario(Usuario usuario);
}
