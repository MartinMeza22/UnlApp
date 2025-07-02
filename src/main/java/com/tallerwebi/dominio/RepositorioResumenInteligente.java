package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioResumenInteligente {
    void guardar(ResumenInteligente resumen);
    List<ResumenInteligente> obtenerPorUsuario(Usuario usuario);
}
