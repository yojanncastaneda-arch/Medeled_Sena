package com.sena.servicio;

/**
 * Se lanza cuando los datos tienen el formato correcto pero rompen una regla
 * del negocio, por ejemplo una venta para un cliente que no existe (HTTP 400)
 * o eliminar un cliente que tiene ventas (HTTP 409, ver "conflicto").
 */
public class ReglaNegocioException extends RuntimeException {

    // true = el problema es el estado actual de los datos (409), no lo enviado (400)
    private final boolean conflicto;

    public ReglaNegocioException(String mensaje, boolean conflicto) {
        super(mensaje);
        this.conflicto = conflicto;
    }

    public boolean esConflicto() {
        return conflicto;
    }
}
