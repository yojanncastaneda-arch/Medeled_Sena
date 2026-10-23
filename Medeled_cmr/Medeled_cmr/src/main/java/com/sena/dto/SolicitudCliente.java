package com.sena.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Datos (JSON) para crear o actualizar un cliente.
 * Ejemplo: {"nombre":"Carlos Pérez","producto":"Panel solar","fechaCompra":"2026-09-01",
 *           "estado":"pendiente","observaciones":"Reclamo por ruido"}
 */
public record SolicitudCliente(

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    String nombre,

    @NotBlank(message = "El producto es obligatorio")
    @Size(max = 100, message = "El producto no puede superar 100 caracteres")
    String producto,

    // Formato yyyy-MM-dd; no se aceptan fechas futuras
    @NotNull(message = "La fecha de compra es obligatoria")
    @PastOrPresent(message = "La fecha de compra no puede ser futura")
    LocalDate fechaCompra,

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "^$|pendiente|en_proceso|resuelto", message = "El estado debe ser: pendiente, en_proceso o resuelto")
    String estado,

    // Opcional
    @Size(max = 255, message = "Las observaciones no pueden superar 255 caracteres")
    String observaciones
) {
}
