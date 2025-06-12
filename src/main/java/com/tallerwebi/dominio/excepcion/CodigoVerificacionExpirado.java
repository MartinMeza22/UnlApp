package com.tallerwebi.dominio.excepcion;

public class CodigoVerificacionExpirado  extends RuntimeException{
    public CodigoVerificacionExpirado(String mensaje) {
        super(mensaje);
    }
}
