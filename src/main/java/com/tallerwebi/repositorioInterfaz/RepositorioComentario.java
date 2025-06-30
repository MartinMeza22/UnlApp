package com.tallerwebi.repositorioInterfaz;

import com.tallerwebi.dominio.Comentario;

public interface RepositorioComentario {
    void guardar(Comentario comentario);

    void eliminar(Comentario comentario);

    Comentario buscarPorId(Long idComentario);
}