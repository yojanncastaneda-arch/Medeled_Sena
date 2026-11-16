package com.sena.servicio;

import com.sena.dao.UsuarioDAO;
import com.sena.modelo.Usuario;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Lógica reutilizable de registro e inicio de sesión.
 * El controlador solo traduce HTTP; aquí están las reglas del negocio.
 */
@Service
public class AuthServicio {

    private final UsuarioDAO usuarioDAO;
    // BCrypt agrega una "sal" aleatoria a cada contraseña antes de cifrarla
    private final BCryptPasswordEncoder cifrador = new BCryptPasswordEncoder();

    public AuthServicio(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Registra un usuario nuevo guardando solo el hash de su contraseña.
     * Lanza UsuarioYaExisteException si el nombre de usuario está ocupado.
     */
    public void registrar(String nombreUsuario, String contrasena) throws SQLException {
        String usuarioNormalizado = normalizar(nombreUsuario);
        if (usuarioDAO.existe(usuarioNormalizado)) {
            throw new UsuarioYaExisteException(usuarioNormalizado);
        }
        try {
            usuarioDAO.insertar(new Usuario(0, usuarioNormalizado, cifrador.encode(contrasena)));
        } catch (SQLIntegrityConstraintViolationException e) {
            // Otro registro con el mismo usuario entró justo antes (columna UNIQUE)
            throw new UsuarioYaExisteException(usuarioNormalizado);
        }
    }

    /**
     * Verifica usuario y contraseña. Devuelve true solo si ambos son correctos.
     */
    public boolean autenticar(String nombreUsuario, String contrasena) throws SQLException {
        Usuario usuario = usuarioDAO.buscarPorUsuario(normalizar(nombreUsuario));
        if (usuario == null) {
            return false;
        }
        // Compara la contraseña recibida contra el hash guardado
        return cifrador.matches(contrasena, usuario.getContrasenaHash());
    }

    // El usuario no distingue mayúsculas ni espacios alrededor: " Ana " y "ana" son el mismo
    private String normalizar(String nombreUsuario) {
        return nombreUsuario.trim().toLowerCase();
    }
}
