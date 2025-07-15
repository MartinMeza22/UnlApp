package com.tallerwebi.dominio.excepcion;

public class FavoritoYaExisteException extends RuntimeException {
    public FavoritoYaExisteException(String message) {
        super(message);
    }
}
