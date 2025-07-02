package com.tallerwebi.dominio.decorator;

import com.tallerwebi.dominio.DTO.MateriaDTO;
import com.tallerwebi.dominio.servicios.ServicioProgreso;

import java.util.List;
import java.util.stream.Collectors;

public class FiltroPorCondicionDecorator implements FiltroMaterias {

    private FiltroMaterias filtroMateria;
    private String condicion;

    public FiltroPorCondicionDecorator(FiltroMaterias filtro, String condicion) {
        this.filtroMateria = filtro;
        this.condicion = condicion;
    }

    @Override
    public List<MateriaDTO> filtrar(String idCarrera, Long usuarioId) {
        List<MateriaDTO> materiasPrevias = filtroMateria.filtrar(idCarrera, usuarioId);

        if (condicion.equalsIgnoreCase("aprobadas")) {
            return materiasPrevias.stream()
                    .filter(materia -> materia.getNota() != null && materia.getNota() >= 4)
                    .collect(Collectors.toList());
        } else if (condicion.equalsIgnoreCase("desaprobadas")) {
            return materiasPrevias.stream()
                    .filter(materia -> materia.getNota() != null && materia.getNota() < 4)
                    .collect(Collectors.toList());
        } else if (condicion.equalsIgnoreCase("cursando")) {
            return materiasPrevias.stream()
                    .filter(materia -> "CURSANDO".equalsIgnoreCase(materia.getEstado()))
                    .collect(Collectors.toList());
        } else if (condicion.equalsIgnoreCase("pendientes")) {
            return materiasPrevias.stream()
                    .filter(materia -> "PENDIENTE".equalsIgnoreCase(materia.getEstado()))
                    .collect(Collectors.toList());
        }

        return materiasPrevias;
    }
}
