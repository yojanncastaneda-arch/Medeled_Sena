package com.sena.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * Formato único de respuesta de los servicios, por ejemplo:
 * { "exito": true, "mensaje": "Autenticación satisfactoria" }
 * La lista "errores" solo aparece cuando fallan las validaciones.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RespuestaApi(boolean exito, String mensaje, List<String> errores) {

    public static RespuestaApi ok(String mensaje) {
        return new RespuestaApi(true, mensaje, null);
    }

    public static RespuestaApi error(String mensaje) {
        return new RespuestaApi(false, mensaje, null);
    }

    public static RespuestaApi error(String mensaje, List<String> errores) {
        return new RespuestaApi(false, mensaje, errores);
    }
}
