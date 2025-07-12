package com.tallerwebi.repositorioInterfaz;

import com.tallerwebi.dominio.Usuario;

import java.util.List;

public interface RepositorioUsuario {

    Usuario buscarUsuario(String email, String password);
    void guardar(Usuario usuario);
    Usuario buscar(String email);
    void modificar(Usuario usuario);
    Usuario buscarPorId(Long id);
    // NUEVOS MÉTODOS PARA GRÁFICOS
    List<Object[]> countUsuariosGroupByCarrera();
    List<Object[]> countUsuariosGroupBySituacionLaboral();

    void eliminar(Usuario usuario);
}

