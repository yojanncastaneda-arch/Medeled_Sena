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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DAO para el módulo de Clientes con soporte dual:
 * opera sobre MySQL cuando está activo y mantiene respaldo funcional en memoria
 * para que la interfaz web en Tomcat siempre cargue y permita CRUD en vivo.
 */
public class ClienteDAO {

    private static final List<Cliente> MEMORIA_CLIENTES = new CopyOnWriteArrayList<>();
    private static final AtomicInteger SEQ_ID = new AtomicInteger(3);

    static {
        MEMORIA_CLIENTES.add(new Cliente(1, "Laura Gómez", "Panel LED 60x60", "2026-10-01", "pendiente", "El panel parpadea después de 2 horas"));
        MEMORIA_CLIENTES.add(new Cliente(2, "Carlos Pérez", "Kit Solar 450W", "2026-10-12", "en proceso", "Instalación programada en sede norte"));
        MEMORIA_CLIENTES.add(new Cliente(3, "Ana Martínez", "Reflector Solar LED 200W", "2026-10-18", "resuelto", "Mantenimiento preventivo completado"));
    }

    public boolean insertar(Cliente c) {
        String sql = "INSERT INTO clientes (nombre, producto, fecha_compra, estado, observaciones) VALUES (?, ?, ?, ?, ?)";
        Connection con = Conexion.conectar();
        if (con != null) {
            try (Connection conn = con;
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, c.getNombre());
                ps.setString(2, c.getProducto());
                ps.setString(3, c.getFechaCompra());
                ps.setString(4, c.getEstado() != null ? c.getEstado() : "pendiente");
                ps.setString(5, c.getObservaciones() != null ? c.getObservaciones() : "");
                int filas = ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        c.setIdCliente(rs.getInt(1));
                    }
                }
                if (filas > 0) {
                    return true;
                }
            } catch (SQLException e) {
                System.err.println("Usando respaldo en memoria para insertar cliente: " + e.getMessage());
            }
        }
        int nuevoId = SEQ_ID.incrementAndGet();
        c.setIdCliente(nuevoId);
        if (c.getEstado() == null || c.getEstado().isEmpty()) {
            c.setEstado("pendiente");
        }
        MEMORIA_CLIENTES.add(c);
        return true;
    }

    public List<Cliente> listar() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT id_cliente, nombre, producto, fecha_compra, estado, observaciones FROM clientes ORDER BY id_cliente ASC";
        Connection con = Conexion.conectar();
        if (con != null) {
            try (Connection conn = con;
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cliente c = new Cliente();
                    c.setIdCliente(rs.getInt("id_cliente"));
                    c.setNombre(rs.getString("nombre"));
                    c.setProducto(rs.getString("producto"));
                    c.setFechaCompra(rs.getString("fecha_compra"));
                    c.setEstado(rs.getString("estado"));
                    c.setObservaciones(rs.getString("observaciones"));
                    lista.add(c);
                }
                if (!lista.isEmpty()) {
                    return lista;
                }
            } catch (SQLException e) {
                System.err.println("Usando respaldo en memoria para listar clientes: " + e.getMessage());
            }
        }
        return new ArrayList<>(MEMORIA_CLIENTES);
    }

    public Cliente obtenerPorId(int idCliente) {
        String sql = "SELECT id_cliente, nombre, producto, fecha_compra, estado, observaciones FROM clientes WHERE id_cliente = ?";
        Connection con = Conexion.conectar();
        if (con != null) {
            try (Connection conn = con;
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idCliente);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Cliente(
                                rs.getInt("id_cliente"),
                                rs.getString("nombre"),
                                rs.getString("producto"),
                                rs.getString("fecha_compra"),
                                rs.getString("estado"),
                                rs.getString("observaciones")
                        );
                    }
                }
            } catch (SQLException ignored) {
            }
        }
        for (Cliente c : MEMORIA_CLIENTES) {
            if (c.getIdCliente() == idCliente) {
                return c;
            }
        }
        return null;
    }

    public boolean actualizar(Cliente c) {
        String sql = "UPDATE clientes SET estado = ?, observaciones = ? WHERE id_cliente = ?";
        Connection con = Conexion.conectar();
        if (con != null) {
            try (Connection conn = con;
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, c.getEstado());
                ps.setString(2, c.getObservaciones());
                ps.setInt(3, c.getIdCliente());
                if (ps.executeUpdate() > 0) {
                    return true;
                }
            } catch (SQLException ignored) {
            }
        }
        for (Cliente item : MEMORIA_CLIENTES) {
            if (item.getIdCliente() == c.getIdCliente()) {
                item.setEstado(c.getEstado());
                item.setObservaciones(c.getObservaciones());
                return true;
            }
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM clientes WHERE id_cliente = ?";
        Connection con = Conexion.conectar();
        if (con != null) {
            try (Connection conn = con;
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                if (ps.executeUpdate() > 0) {
                    return true;
                }
            } catch (SQLException ignored) {
            }
        }
        return MEMORIA_CLIENTES.removeIf(c -> c.getIdCliente() == id);
    }
}
