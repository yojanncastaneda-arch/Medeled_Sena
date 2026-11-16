package com.sena.servicio;

import com.sena.dao.ClienteDAO;
import com.sena.dao.VentaDAO;
import com.sena.dto.SolicitudCliente;
import com.sena.modelo.Cliente;
import com.sena.modelo.Venta;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * Reglas de negocio para la gestión de clientes (reclamos / postventa).
 */
@Service
public class ClienteServicio {

    private static final Set<String> ESTADOS = Set.of("pendiente", "en_proceso", "resuelto");

    private final ClienteDAO clienteDAO;
    private final VentaDAO ventaDAO;

    public ClienteServicio(ClienteDAO clienteDAO, VentaDAO ventaDAO) {
        this.clienteDAO = clienteDAO;
        this.ventaDAO = ventaDAO;
    }

    // Lista los clientes; el filtro por estado es opcional
    public List<Cliente> listar(String estado) throws SQLException {
        if (estado != null && !ESTADOS.contains(estado)) {
            throw new ReglaNegocioException("El estado debe ser: pendiente, en_proceso o resuelto", false);
        }
        return clienteDAO.listar(estado);
    }

    public Cliente buscar(int id) throws SQLException {
        Cliente cliente = clienteDAO.buscarPorId(id);
        if (cliente == null) {
            throw new RecursoNoEncontradoException("cliente", id);
        }
        return cliente;
    }

    public Cliente crear(SolicitudCliente datos) throws SQLException {
        Cliente cliente = aModelo(0, datos);
        cliente.setIdCliente(clienteDAO.insertar(cliente));
        return cliente;
    }

    public Cliente actualizar(int id, SolicitudCliente datos) throws SQLException {
        Cliente cliente = aModelo(id, datos);
        if (!clienteDAO.actualizar(cliente)) {
            throw new RecursoNoEncontradoException("cliente", id);
        }
        return cliente;
    }

    // No se elimina un cliente que tenga ventas, para no perder el historial
    public void eliminar(int id) throws SQLException {
        buscar(id);
        if (!ventaDAO.listarPorCliente(id).isEmpty()) {
            throw new ReglaNegocioException("No se puede eliminar el cliente " + id + " porque tiene ventas registradas", true);
        }
        try {
            clienteDAO.eliminar(id);
        } catch (SQLIntegrityConstraintViolationException e) {
            // Alguien registró una venta justo antes (llave foránea)
            throw new ReglaNegocioException("No se puede eliminar el cliente " + id + " porque tiene ventas registradas", true);
        }
    }

    // Ventas de un cliente; primero verifica que el cliente exista
    public List<Venta> ventasDelCliente(int id) throws SQLException {
        buscar(id);
        return ventaDAO.listarPorCliente(id);
    }

    // Convierte el JSON recibido en el modelo que usa el DAO (quita espacios sobrantes)
    private Cliente aModelo(int id, SolicitudCliente datos) {
        String observaciones = datos.observaciones() == null || datos.observaciones().isBlank()
                ? null : datos.observaciones().trim();
        return new Cliente(id, datos.nombre().trim(), datos.producto().trim(),
                datos.fechaCompra().toString(), datos.estado(), observaciones);
    }
}
