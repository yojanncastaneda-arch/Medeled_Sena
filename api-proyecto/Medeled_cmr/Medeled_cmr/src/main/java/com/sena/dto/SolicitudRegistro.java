package com.sena.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos (JSON) que recibe el servicio de registro.
 * Las anotaciones son las validaciones de verificación: si alguna falla,
 * el servicio responde 400 sin tocar la base de datos.
 */
public record SolicitudRegistro(

    @NotBlank(message = "El usuario es obligatorio")
    @Size(min = 4, max = 30, message = "El usuario debe tener entre 4 y 30 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9._]*$", message = "El usuario solo puede tener letras, números, punto o guion bajo")
    String usuario,

    @NotBlank(message = "La contraseña es obligatoria")
    // BCrypt solo usa los primeros 72 bytes, por eso se limita el máximo
    @Size(min = 8, max = 64, message = "La contraseña debe tener entre 8 y 64 caracteres")
    // "^$|" deja pasar el vacío aquí porque ese caso ya lo reporta @NotBlank
    @Pattern(regexp = "^$|^(?=.*[A-Za-z])(?=.*\\d).*$", message = "La contraseña debe tener al menos una letra y un número")
    String contrasena
) {
}
