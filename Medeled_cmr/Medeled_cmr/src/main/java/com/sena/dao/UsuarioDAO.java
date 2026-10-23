package com.sena.dao;

import com.sena.conexion.Conexion;
import com.sena.modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.stereotype.Repository;

/**
 * Acceso a la tabla usuario. A diferencia de los otros DAO, aquí los errores
 * de base de datos se lanzan (throws) para que el servicio web pueda responder
 * con un error en lugar de dar una respuesta falsa al cliente.
 */
@Repository
public class UsuarioDAO {

    // Inserta un usuario nuevo con su contraseña ya cifrada
    public void insertar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (usuario, contrasena_hash) VALUES (?, ?)";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario.getUsuario());
            ps.setString(2, usuario.getContrasenaHash());
            ps.executeUpdate();
        }
    }

    // Busca un usuario por su nombre de usuario; devuelve null si no existe
    public Usuario buscarPorUsuario(String nombreUsuario) throws SQLException {
        String sql = "SELECT idusuario, usuario, contrasena_hash FROM usuario WHERE usuario = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                        rs.getInt("idusuario"),
                        rs.getString("usuario"),
                        rs.getString("contrasena_hash")
                    );
                }
                return null;
            }
        }
    }

    // Indica si ya hay un usuario registrado con ese nombre
    public boolean existe(String nombreUsuario) throws SQLException {
        return buscarPorUsuario(nombreUsuario) != null;
    }

    // Conexion.conectar() devuelve null cuando falla, así que se convierte en excepción
    private Connection obtenerConexion() throws SQLException {
        Connection con = Conexion.conectar();
        if (con == null) {
            throw new SQLException("No fue posible conectar con la base de datos");
        }
        return con;
    }
}
