package com.sena.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Datos (JSON) para crear o actualizar una venta.
 * Ejemplo: {"idCliente":1,"producto":"Panel solar 450W","cantidad":4,
 *           "precioUnitario":850000,"fechaVenta":"2026-09-16","estado":"pendiente"}
 */
public record SolicitudVenta(

    // Además de ser positivo, el servicio verifica que el cliente exista
    @NotNull(message = "El idCliente es obligatorio")
    @Positive(message = "El idCliente debe ser un número positivo")
    Integer idCliente,

    @NotBlank(message = "El producto es obligatorio")
    @Size(max = 100, message = "El producto no puede superar 100 caracteres")
    String producto,

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Max(value = 10000, message = "La cantidad no puede superar 10000")
    Integer cantidad,

    // La columna es DECIMAL(10,2): hasta 8 enteros y 2 decimales
    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor que 0")
    @Digits(integer = 8, fraction = 2, message = "El precio unitario admite hasta 8 enteros y 2 decimales")
    BigDecimal precioUnitario,

    @NotNull(message = "La fecha de venta es obligatoria")
    @PastOrPresent(message = "La fecha de venta no puede ser futura")
    LocalDate fechaVenta,

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "^$|pendiente|pagada|anulada", message = "El estado debe ser: pendiente, pagada o anulada")
    String estado
) {
}
