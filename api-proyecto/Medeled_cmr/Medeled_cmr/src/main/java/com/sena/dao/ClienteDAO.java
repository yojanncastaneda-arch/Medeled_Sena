package com.sena.dao;

import com.sena.conexion.Conexion;
import com.sena.modelo.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * Acceso a la tabla cliente. Los errores de base de datos se lanzan (throws)
 * para que el API pueda responder un error en lugar de fallar en silencio.
 */
@Repository
public class ClienteDAO {

    // Inserta el cliente y devuelve el id que le asignó MySQL
    public int insertar(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente (nombre, producto, fecha_compra, estado, observaciones) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getProducto());
            ps.setString(3, cliente.getFechaCompra());
            ps.setString(4, cliente.getEstado());
            ps.setString(5, cliente.getObservaciones());
            ps.executeUpdate();
            try (ResultSet llaves = ps.getGeneratedKeys()) {
                llaves.next();
                return llaves.getInt(1);
            }
        }
    }

    // Lista todos los clientes; si estado no es null, solo los de ese estado
    public List<Cliente> listar(String estado) throws SQLException {
        String sql = estado == null
                ? "SELECT * FROM cliente ORDER BY idcliente"
                : "SELECT * FROM cliente WHERE estado = ? ORDER BY idcliente";
        List<Cliente> lista = new ArrayList<>();
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (estado != null) {
                ps.setString(1, estado);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(leer(rs));
                }
            }
        }
        return lista;
    }

    // Busca un cliente por id; devuelve null si no existe
    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE idcliente = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leer(rs) : null;
            }
        }
    }

    // Devuelve true si encontró el cliente y lo actualizó
    public boolean actualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE cliente SET nombre=?, producto=?, fecha_compra=?, estado=?, observaciones=? WHERE idcliente=?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getProducto());
            ps.setString(3, cliente.getFechaCompra());
            ps.setString(4, cliente.getEstado());
            ps.setString(5, cliente.getObservaciones());
            ps.setInt(6, cliente.getIdCliente());
            return ps.executeUpdate() > 0;
        }
    }

    // Devuelve true si encontró el cliente y lo eliminó
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM cliente WHERE idcliente=?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // Convierte la fila actual del ResultSet en un objeto Cliente
    private Cliente leer(ResultSet rs) throws SQLException {
        return new Cliente(
            rs.getInt("idcliente"),
            rs.getString("nombre"),
            rs.getString("producto"),
            rs.getString("fecha_compra"),
            rs.getString("estado"),
            rs.getString("observaciones")
        );
    }
}
