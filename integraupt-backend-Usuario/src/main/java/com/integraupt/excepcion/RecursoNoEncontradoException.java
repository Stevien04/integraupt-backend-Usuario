package com.integraupt.excepcion;

/**
 * Excepción personalizada para recursos no encontrados en la API.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}