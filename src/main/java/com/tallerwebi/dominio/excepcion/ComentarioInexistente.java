package com.tallerwebi.dominio.excepcion;

public class ComentarioInexistente extends RuntimeException {
    public ComentarioInexistente(String message) {
        super(message);
    }
}
