package com.tallerwebi.dominio.excepcion;

public class ReporteInexistente extends RuntimeException {
    public ReporteInexistente(String message) {
        super(message);
    }
}
