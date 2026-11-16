package com.sena.servicio;

import com.sena.dao.ClienteDAO;
import com.sena.dao.VentaDAO;
import com.sena.dto.SolicitudVenta;
import com.sena.modelo.Venta;
import java.sql.SQLException;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Reglas de negocio para la gestión de ventas.
 */
@Service
public class VentaServicio {

    private final VentaDAO ventaDAO;
    private final ClienteDAO clienteDAO;

    public VentaServicio(VentaDAO ventaDAO, ClienteDAO clienteDAO) {
        this.ventaDAO = ventaDAO;
        this.clienteDAO = clienteDAO;
    }

    public List<Venta> listar() throws SQLException {
        return ventaDAO.listar();
    }

    public Venta buscar(int id) throws SQLException {
        Venta venta = ventaDAO.buscarPorId(id);
        if (venta == null) {
            throw new RecursoNoEncontradoException("venta", id);
        }
        return venta;
    }

    public Venta crear(SolicitudVenta datos) throws SQLException {
        validarCliente(datos.idCliente());
        Venta venta = aModelo(0, datos);
        venta.setIdVenta(ventaDAO.insertar(venta));
        return venta;
    }

    public Venta actualizar(int id, SolicitudVenta datos) throws SQLException {
        buscar(id);
        validarCliente(datos.idCliente());
        Venta venta = aModelo(id, datos);
        ventaDAO.actualizar(venta);
        return venta;
    }

    public void eliminar(int id) throws SQLException {
        if (!ventaDAO.eliminar(id)) {
            throw new RecursoNoEncontradoException("venta", id);
        }
    }

    // Una venta siempre debe pertenecer a un cliente que exista
    private void validarCliente(int idCliente) throws SQLException {
        if (clienteDAO.buscarPorId(idCliente) == null) {
            throw new ReglaNegocioException("El cliente con id " + idCliente + " no existe", false);
        }
    }

    private Venta aModelo(int id, SolicitudVenta datos) {
        return new Venta(id, datos.idCliente(), datos.producto().trim(), datos.cantidad(),
                datos.precioUnitario(), datos.fechaVenta().toString(), datos.estado());
    }
}
