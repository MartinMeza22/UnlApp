package com.tallerwebi.repositorioInterfaz;

import com.tallerwebi.dominio.Usuario;

public interface RepositorioUsuario {

    Usuario buscarUsuario(String email, String password);
    void guardar(Usuario usuario);
    Usuario buscar(String email);
    void modificar(Usuario usuario);
    Usuario buscarPorId(Long id);

    void eliminar(Usuario usuario);
}

