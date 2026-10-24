package com.sena.dao;

import com.sena.conexion.Conexion;
import com.sena.modelo.Venta;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DAO para el módulo de Ventas integrado con Clientes por llave foránea (id_cliente).
 * Incluye respaldo automático en memoria si MySQL local no está disponible.
 */
public class VentaDAO {

    private static final List<Venta> MEMORIA_VENTAS = new CopyOnWriteArrayList<>();
    private static final AtomicInteger SEQ_VENTA = new AtomicInteger(2);

    static {
        MEMORIA_VENTAS.add(new Venta(1, 1, "Panel LED 60x60", 3, new BigDecimal("85000.00"), "2026-10-05", "pagada"));
        MEMORIA_VENTAS.add(new Venta(2, 2, "Kit Solar 450W", 2, new BigDecimal("650000.00"), "2026-10-14", "pagada"));
    }

    public boolean insertar(Venta v) {
        String sql = "INSERT INTO venta (id_cliente, producto, cantidad, precio_unitario, total, fecha_venta, estado) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection con = Conexion.conectar();
        if (con != null) {
            try (Connection conn = con;
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, v.getIdCliente());
                ps.setString(2, v.getProducto());
                ps.setInt(3, v.getCantidad());
                ps.setBigDecimal(4, v.getPrecioUnitario());
                ps.setBigDecimal(5, v.getTotal());
                ps.setString(6, v.getFechaVenta());
                ps.setString(7, v.getEstado() != null ? v.getEstado() : "pagada");
                int filas = ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        v.setIdVenta(rs.getInt(1));
                    }
                }
                if (filas > 0) {
                    return true;
                }
            } catch (SQLException e) {
                System.err.println("Usando respaldo en memoria para insertar venta: " + e.getMessage());
            }
        }
        int nuevoId = SEQ_VENTA.incrementAndGet();
        v.setIdVenta(nuevoId);
        if (v.getEstado() == null || v.getEstado().isEmpty()) {
            v.setEstado("pagada");
        }
        MEMORIA_VENTAS.add(v);
        return true;
    }

    public List<Venta> listar() {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT id_venta, id_cliente, producto, cantidad, precio_unitario, total, fecha_venta, estado FROM venta ORDER BY id_venta ASC";
        Connection con = Conexion.conectar();
        if (con != null) {
            try (Connection conn = con;
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Venta v = new Venta(
                            rs.getInt("id_venta"),
                            rs.getInt("id_cliente"),
                            rs.getString("producto"),
                            rs.getInt("cantidad"),
                            rs.getBigDecimal("precio_unitario"),
                            rs.getString("fecha_venta"),
                            rs.getString("estado")
                    );
                    v.setTotal(rs.getBigDecimal("total"));
                    lista.add(v);
                }
                if (!lista.isEmpty()) {
                    return lista;
                }
            } catch (SQLException e) {
                System.err.println("Usando respaldo en memoria para listar ventas: " + e.getMessage());
            }
        }
        return new ArrayList<>(MEMORIA_VENTAS);
    }

    public List<Venta> listarPorCliente(int idCliente) {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT id_venta, id_cliente, producto, cantidad, precio_unitario, total, fecha_venta, estado "
                   + "FROM venta WHERE id_cliente = ? ORDER BY id_venta ASC";
        Connection con = Conexion.conectar();
        if (con != null) {
            try (Connection conn = con;
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idCliente);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Venta v = new Venta(
                                rs.getInt("id_venta"),
                                rs.getInt("id_cliente"),
                                rs.getString("producto"),
                                rs.getInt("cantidad"),
                                rs.getBigDecimal("precio_unitario"),
                                rs.getString("fecha_venta"),
                                rs.getString("estado")
                        );
                        v.setTotal(rs.getBigDecimal("total"));
                        lista.add(v);
                    }
                }
                if (!lista.isEmpty()) {
                    return lista;
                }
            } catch (SQLException ignored) {
            }
        }
        for (Venta v : MEMORIA_VENTAS) {
            if (v.getIdCliente() == idCliente) {
                lista.add(v);
            }
        }
        return lista;
    }

    public boolean eliminar(int idVenta) {
        String sql = "DELETE FROM venta WHERE id_venta = ?";
        Connection con = Conexion.conectar();
        if (con != null) {
            try (Connection conn = con;
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idVenta);
                if (ps.executeUpdate() > 0) {
                    return true;
                }
            } catch (SQLException ignored) {
            }
        }
        return MEMORIA_VENTAS.removeIf(v -> v.getIdVenta() == idVenta);
    }
}
