package com.tallerwebi.dominio;

public interface RepositorioComentario {
    void guardar(Comentario comentario);

    void eliminar(Comentario comentario);

    Comentario buscarPorId(Long idComentario);
}