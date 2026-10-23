package com.sena.servicio;

/**
 * Se lanza cuando se pide un cliente o una venta que no existe (HTTP 404).
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String recurso, int id) {
        super("No existe " + recurso + " con id " + id);
    }
}
