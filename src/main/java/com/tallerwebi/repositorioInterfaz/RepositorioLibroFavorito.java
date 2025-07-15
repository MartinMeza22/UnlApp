package com.tallerwebi.repositorioInterfaz;

import com.tallerwebi.dominio.LibroFavorito;
import com.tallerwebi.dominio.Usuario;

import java.util.List;

public interface RepositorioLibroFavorito {
    void guardar(LibroFavorito libroFavorito);

    boolean existe(Usuario usuario, String idGoogleBook);

    List<String> obtenerIdsPorUsuario(Usuario usuario);

    void eliminar(Usuario usuario, String idGoogleBook);
}
