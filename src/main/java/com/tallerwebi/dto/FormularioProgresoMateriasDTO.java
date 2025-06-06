package com.tallerwebi.dto;

import java.util.List;

public class FormularioProgresoMateriasDTO {
    private List<MateriasSeleccionadasDesdeElRegistroDTO> materiasSeleccionadasDesdeElRegistroDTO;

    public List<MateriasSeleccionadasDesdeElRegistroDTO> getMateriasSeleccionadasDesdeElRegistroDTO() {
        return materiasSeleccionadasDesdeElRegistroDTO;
    }

    public void setMateriasSeleccionadasDesdeElRegistroDTO(List<MateriasSeleccionadasDesdeElRegistroDTO> materiasSeleccionadasDesdeElRegistroDTO) {
        this.materiasSeleccionadasDesdeElRegistroDTO = materiasSeleccionadasDesdeElRegistroDTO;
    }
}
