package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioResumenInteligente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControladorResumenInteligente {

    @Autowired
    private ServicioResumenInteligente servicioResumenInteligente;

    @GetMapping("/prueba-gemini")
    public String prueba() {
        String prompt = "Explain how AI works in a few words";
        return servicioResumenInteligente.generarContenido(prompt);
    }
}

