package com.sena.servicio;

/**
 * Se lanza cuando se intenta registrar un usuario que ya existe.
 */
public class UsuarioYaExisteException extends RuntimeException {

    public UsuarioYaExisteException(String usuario) {
        super("El usuario '" + usuario + "' ya está registrado");
    }
}
