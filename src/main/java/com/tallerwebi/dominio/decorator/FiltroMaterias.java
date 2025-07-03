package com.tallerwebi.dominio.decorator;

import com.tallerwebi.dominio.DTO.MateriaDTO;

import java.util.List;

public interface FiltroMaterias {
    List<MateriaDTO> filtrar(String idCarrera, Long usuarioId);
}
