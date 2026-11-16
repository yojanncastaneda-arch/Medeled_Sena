package com.sena.dao;

import com.sena.conexion.Conexion;
import com.sena.modelo.Venta;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * Acceso a la tabla venta. Los errores de base de datos se lanzan (throws)
 * para que el API pueda responder un error en lugar de fallar en silencio.
 */
@Repository
public class VentaDAO {

    // Inserta la venta y devuelve el id que le asignó MySQL
    public int insertar(Venta venta) throws SQLException {
        String sql = "INSERT INTO venta (idcliente, producto, cantidad, precio_unitario, fecha_venta, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            llenarParametros(ps, venta);
            ps.executeUpdate();
            try (ResultSet llaves = ps.getGeneratedKeys()) {
                llaves.next();
                return llaves.getInt(1);
            }
        }
    }

    // Lista todas las ventas
    public List<Venta> listar() throws SQLException {
        return consultar("SELECT * FROM venta ORDER BY idventa", null);
    }

    // Lista solo las ventas de un cliente
    public List<Venta> listarPorCliente(int idCliente) throws SQLException {
        return consultar("SELECT * FROM venta WHERE idcliente = ? ORDER BY idventa", idCliente);
    }

    // Busca una venta por id; devuelve null si no existe
    public Venta buscarPorId(int id) throws SQLException {
        List<Venta> resultado = consultar("SELECT * FROM venta WHERE idventa = ?", id);
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    // Devuelve true si encontró la venta y la actualizó
    public boolean actualizar(Venta venta) throws SQLException {
        String sql = "UPDATE venta SET idcliente=?, producto=?, cantidad=?, precio_unitario=?, fecha_venta=?, estado=? WHERE idventa=?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            llenarParametros(ps, venta);
            ps.setInt(7, venta.getIdVenta());
            return ps.executeUpdate() > 0;
        }
    }

    // Devuelve true si encontró la venta y la eliminó
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM venta WHERE idventa=?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // Ejecuta un SELECT con un parámetro entero opcional y arma la lista de ventas
    private List<Venta> consultar(String sql, Integer parametro) throws SQLException {
        List<Venta> lista = new ArrayList<>();
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (parametro != null) {
                ps.setInt(1, parametro);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Venta(
                        rs.getInt("idventa"),
                        rs.getInt("idcliente"),
                        rs.getString("producto"),
                        rs.getInt("cantidad"),
                        rs.getBigDecimal("precio_unitario"),
                        rs.getString("fecha_venta"),
                        rs.getString("estado")
                    ));
                }
            }
        }
        return lista;
    }

    // Los 6 primeros parámetros son iguales en INSERT y UPDATE
    private void llenarParametros(PreparedStatement ps, Venta venta) throws SQLException {
        ps.setInt(1, venta.getIdCliente());
        ps.setString(2, venta.getProducto());
        ps.setInt(3, venta.getCantidad());
        ps.setBigDecimal(4, venta.getPrecioUnitario());
        ps.setString(5, venta.getFechaVenta());
        ps.setString(6, venta.getEstado());
    }
}
