package com.sena.servlet;

import com.sena.dao.ClienteDAO;
import com.sena.dao.VentaDAO;
import com.sena.modelo.Cliente;
import com.sena.modelo.Venta;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador web que integra el módulo de Clientes y el módulo de Ventas en Apache Tomcat.
 */
@WebServlet(name = "ClienteServlet", urlPatterns = {"/clientes"})
public class ClienteServlet extends HttpServlet {

    private final ClienteDAO clienteDao = new ClienteDAO();
    private final VentaDAO ventaDao = new VentaDAO();

    public static void procesarAccion(HttpServletRequest request, ClienteDAO clienteDao, VentaDAO ventaDao) {
        String accion = request.getParameter("accion");
        if (accion == null || accion.trim().isEmpty()) {
            return;
        }
        try {
            if ("insertar".equals(accion)) {
                String nombre = request.getParameter("nombre");
                String producto = request.getParameter("producto");
                String fecha = request.getParameter("fecha_compra");
                String estado = request.getParameter("estado");
                String obs = request.getParameter("observaciones");
                if (nombre != null && !nombre.trim().isEmpty()) {
                    Cliente c = new Cliente(0, nombre.trim(), producto, fecha,
                            (estado == null || estado.isEmpty()) ? "pendiente" : estado,
                            obs != null ? obs : "");
                    clienteDao.insertar(c);
                    request.setAttribute("mensajeExito", "Cliente '" + c.getNombre() + "' registrado correctamente (ID #" + c.getIdCliente() + ").");
                }
            } else if ("actualizar".equals(accion)) {
                int id = Integer.parseInt(request.getParameter("id_cliente"));
                String estado = request.getParameter("estado");
                String obs = request.getParameter("observaciones");
                Cliente c = new Cliente(id, "", "", "", estado, obs);
                clienteDao.actualizar(c);
                request.setAttribute("mensajeExito", "Estado del cliente #" + id + " actualizado a '" + estado + "'.");
            } else if ("eliminar".equals(accion)) {
                int id = Integer.parseInt(request.getParameter("id"));
                clienteDao.eliminar(id);
                request.setAttribute("mensajeExito", "Cliente #" + id + " eliminado.");
            } else if ("insertarVenta".equals(accion)) {
                int idCliente = Integer.parseInt(request.getParameter("id_cliente"));
                String producto = request.getParameter("producto");
                int cantidad = Integer.parseInt(request.getParameter("cantidad"));
                BigDecimal precio = new BigDecimal(request.getParameter("precio_unitario"));
                String fecha = request.getParameter("fecha_venta");
                String estado = request.getParameter("estado");

                Venta v = new Venta(0, idCliente, producto, cantidad, precio, fecha,
                        (estado == null || estado.isEmpty()) ? "pagada" : estado);
                ventaDao.insertar(v);
                request.setAttribute("mensajeExito", "Venta registrada para el Cliente #" + idCliente + " — Total calculado: $ " + v.getTotal());
            } else if ("eliminarVenta".equals(accion)) {
                int idVenta = Integer.parseInt(request.getParameter("id_venta"));
                ventaDao.eliminar(idVenta);
                request.setAttribute("mensajeExito", "Venta #" + idVenta + " eliminada.");
            }
        } catch (Exception e) {
            request.setAttribute("mensajeError", "Verifica los datos del formulario: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        procesarAccion(request, clienteDao, ventaDao);

        List<Cliente> clientes = clienteDao.listar();
        List<Venta> ventas = ventaDao.listar();
        request.setAttribute("clientes", clientes);
        request.setAttribute("ventas", ventas);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        procesarAccion(request, clienteDao, ventaDao);

        List<Cliente> clientes = clienteDao.listar();
        List<Venta> ventas = ventaDao.listar();
        request.setAttribute("clientes", clientes);
        request.setAttribute("ventas", ventas);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
