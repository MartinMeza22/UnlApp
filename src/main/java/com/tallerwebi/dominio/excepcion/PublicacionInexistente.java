package com.tallerwebi.dominio.excepcion;

public class PublicacionInexistente extends Exception {
    public PublicacionInexistente(String mensaje) {
        super("La publicación solicitada no existe.");
    }
}
