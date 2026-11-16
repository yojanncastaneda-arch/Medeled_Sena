package com.sena.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Datos (JSON) que recibe el servicio de inicio de sesión.
 * Solo se verifica que vengan llenos; las reglas de formato aplican al registrarse.
 */
public record SolicitudLogin(

    @NotBlank(message = "El usuario es obligatorio")
    String usuario,

    @NotBlank(message = "La contraseña es obligatoria")
    String contrasena
) {
}
