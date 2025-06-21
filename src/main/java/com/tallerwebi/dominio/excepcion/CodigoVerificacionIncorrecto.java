package com.tallerwebi.dominio.excepcion;

public class CodigoVerificacionIncorrecto extends RuntimeException{
    public CodigoVerificacionIncorrecto(String mensaje) {
        super(mensaje);
    }
}
