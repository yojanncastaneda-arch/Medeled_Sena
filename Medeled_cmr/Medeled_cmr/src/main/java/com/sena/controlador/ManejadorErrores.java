package com.sena.controlador;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.sena.dto.RespuestaApi;
import com.sena.servicio.RecursoNoEncontradoException;
import com.sena.servicio.ReglaNegocioException;
import com.sena.servicio.UsuarioYaExisteException;
import java.sql.SQLException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Convierte los errores en respuestas JSON con el código HTTP adecuado,
 * para que el cliente siempre reciba el mismo formato (RespuestaApi).
 */
@RestControllerAdvice
public class ManejadorErrores {

    // 400: alguna validación de los datos de entrada falló
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaApi> validacion(MethodArgumentNotValidException e) {
        List<String> errores = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .sorted()
                .toList();
        return ResponseEntity.badRequest().body(RespuestaApi.error("Datos inválidos", errores));
    }

    // 400: el cuerpo no es un JSON válido, viene vacío o un campo tiene un tipo/formato equivocado
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RespuestaApi> jsonInvalido(HttpMessageNotReadableException e) {
        if (e.getCause() instanceof InvalidFormatException formato && !formato.getPath().isEmpty()) {
            String campo = formato.getPath().get(0).getFieldName();
            return ResponseEntity.badRequest().body(RespuestaApi.error("Formato inválido en el campo '" + campo
                    + "' (fechas: yyyy-MM-dd, números sin comillas ni separadores de miles)"));
        }
        return ResponseEntity.badRequest().body(RespuestaApi.error("El cuerpo de la petición debe ser un JSON válido"));
    }

    // 400: el id de la URL no es un número, por ejemplo /api/clientes/abc
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<RespuestaApi> parametroInvalido(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.badRequest().body(RespuestaApi.error("El parámetro '" + e.getName() + "' debe ser numérico"));
    }

    // 404: el cliente o la venta no existe
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<RespuestaApi> noEncontrado(RecursoNoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(RespuestaApi.error(e.getMessage()));
    }

    // 400 o 409: los datos rompen una regla del negocio
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<RespuestaApi> reglaNegocio(ReglaNegocioException e) {
        HttpStatus codigo = e.esConflicto() ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(codigo).body(RespuestaApi.error(e.getMessage()));
    }

    // 409: el usuario ya estaba registrado
    @ExceptionHandler(UsuarioYaExisteException.class)
    public ResponseEntity<RespuestaApi> usuarioExistente(UsuarioYaExisteException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(RespuestaApi.error(e.getMessage()));
    }

    // 500: problema con la base de datos (no se expone el detalle técnico al cliente)
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<RespuestaApi> baseDeDatos(SQLException e) {
        System.out.println("Error de base de datos: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(RespuestaApi.error("Error interno, intente más tarde"));
    }
}
