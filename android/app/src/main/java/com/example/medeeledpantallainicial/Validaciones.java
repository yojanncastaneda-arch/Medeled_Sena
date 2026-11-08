package com.example.medeeledpantallainicial;

import java.util.regex.Pattern;

/**
 * Reglas de validación del formulario de inicio de sesión.
 * Es Java puro (sin dependencias de Android) para poder probarla con JUnit local.
 */
public final class Validaciones {

    public static final int LONGITUD_MINIMA_CONTRASENA = 6;

    private static final Pattern PATRON_CORREO =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private Validaciones() {
    }

    /** Un campo nulo o con solo espacios se considera vacío. */
    public static boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    public static boolean esCorreoValido(String correo) {
        return !estaVacio(correo) && PATRON_CORREO.matcher(correo.trim()).matches();
    }

    public static boolean esContrasenaValida(String contrasena) {
        return contrasena != null && contrasena.length() >= LONGITUD_MINIMA_CONTRASENA;
    }
}
