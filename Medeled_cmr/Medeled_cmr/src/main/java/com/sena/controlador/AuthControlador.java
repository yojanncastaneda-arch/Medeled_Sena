package com.sena.controlador;

import com.sena.dto.RespuestaApi;
import com.sena.dto.SolicitudLogin;
import com.sena.dto.SolicitudRegistro;
import com.sena.servicio.AuthServicio;
import jakarta.validation.Valid;
import java.sql.SQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servicios web de autenticación.
 *   POST /api/auth/registro -> crea un usuario
 *   POST /api/auth/login    -> verifica usuario y contraseña
 * Ambos reciben y responden JSON.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthControlador {

    private final AuthServicio authServicio;

    public AuthControlador(AuthServicio authServicio) {
        this.authServicio = authServicio;
    }

    // @Valid ejecuta las validaciones del DTO antes de entrar al método
    @PostMapping("/registro")
    public ResponseEntity<RespuestaApi> registrar(@Valid @RequestBody SolicitudRegistro solicitud) throws SQLException {
        authServicio.registrar(solicitud.usuario(), solicitud.contrasena());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RespuestaApi.ok("Usuario registrado correctamente"));
    }

    @PostMapping("/login")
    public ResponseEntity<RespuestaApi> iniciarSesion(@Valid @RequestBody SolicitudLogin solicitud) throws SQLException {
        if (authServicio.autenticar(solicitud.usuario(), solicitud.contrasena())) {
            return ResponseEntity.ok(RespuestaApi.ok("Autenticación satisfactoria"));
        }
        // Mismo mensaje si el usuario no existe o si la contraseña está mal,
        // para no revelar qué usuarios están registrados
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(RespuestaApi.error("Error en la autenticación: usuario o contraseña incorrectos"));
    }
}
