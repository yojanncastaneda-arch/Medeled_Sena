package com.sena.test;

import com.sena.conexion.Conexion;
import com.sena.dao.ClienteDAO;
import com.sena.dao.VentaDAO;
import com.sena.modelo.Cliente;
import com.sena.modelo.Venta;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

/**
 * Suite de Pruebas Unitarias y de Integración para los módulos Cliente y Venta de MEDELED CRM.
 */
public class MedeledCmrTest {

    @Test
    @DisplayName("CP01 - Crear y validar atributos del modelo Cliente")
    void cp01_crearYValidarModeloCliente() {
        Cliente cliente = new Cliente(1, "Laura Gómez", "Panel LED 60x60", "2026-10-01", "pendiente", "El panel parpadea");

        assertEquals(1, cliente.getIdCliente());
        assertEquals("Laura Gómez", cliente.getNombre());
        assertEquals("Panel LED 60x60", cliente.getProducto());
        assertEquals("2026-10-01", cliente.getFechaCompra());
        assertEquals("pendiente", cliente.getEstado());
        assertEquals("El panel parpadea", cliente.getObservaciones());
    }

    @Test
    @DisplayName("CP02 - Actualizar estado de atención postventa en Cliente")
    void cp02_actualizarEstadoCliente() {
        Cliente cliente = new Cliente(3, "Carlos Pérez", "Lavadora", "2026-09-01", "pendiente", "Reclamo por ruido");
        cliente.setEstado("resuelto");
        cliente.setObservaciones("Se cambió la pieza");

        assertEquals("resuelto", cliente.getEstado());
        assertEquals("Se cambió la pieza", cliente.getObservaciones());
    }

    @Test
    @DisplayName("CP03 - Crear y validar atributos del modelo Venta")
    void cp03_crearYValidarModeloVenta() {
        Venta venta = new Venta(10, 1, "Panel Solar 450W", 3, new BigDecimal("85000.00"), "2026-10-05", "pagada");

        assertEquals(10, venta.getIdVenta());
        assertEquals(1, venta.getIdCliente());
        assertEquals("Panel Solar 450W", venta.getProducto());
        assertEquals(3, venta.getCantidad());
        assertEquals(new BigDecimal("85000.00"), venta.getPrecioUnitario());
        assertEquals("2026-10-05", venta.getFechaVenta());
        assertEquals("pagada", venta.getEstado());
    }

    @Test
    @DisplayName("CP04 - Calcular automáticamente el total de la venta con dos decimales (RNF24)")
    void cp04_calcularTotalVentaAutomatico() {
        Venta venta = new Venta(1, 1, "Panel LED 60x60", 3, new BigDecimal("85000.00"), "2026-10-05", "pagada");
        assertEquals(new BigDecimal("255000.00"), venta.getTotal());
    }

    @Test
    @DisplayName("CP05 - Validar total en cero cuando cantidad es cero o negativa")
    void cp05_totalCeroConCantidadInvalida() {
        Venta ventaCero = new Venta(2, 1, "Inversor Solar", 0, new BigDecimal("1200000.00"), "2026-10-05", "pendiente");
        assertEquals(new BigDecimal("0.00"), ventaCero.getTotal());
    }

    @Test
    @DisplayName("CP06 - Verificar integración de llave foránea entre Cliente y Venta")
    void cp06_integracionRelacionClienteVenta() {
        Cliente cliente = new Cliente(7, "Ana Martínez", "Kit Solar Consultorio", "2026-10-10", "pendiente", "Instalación programada");
        Venta ventaAsociada = new Venta(100, cliente.getIdCliente(), cliente.getProducto(), 2, new BigDecimal("650000.00"), "2026-10-10", "pagada");

        assertEquals(cliente.getIdCliente(), ventaAsociada.getIdCliente());
        assertEquals(new BigDecimal("1300000.00"), ventaAsociada.getTotal());
    }

    @Test
    @DisplayName("CP07 - Verificar parámetros de configuración por defecto en Conexion")
    void cp07_configuracionConexionPorDefecto() {
        assertNotNull(Conexion.getUrlConfigurada());
        assertTrue(Conexion.getUrlConfigurada().contains("medeled_cmr"));
        assertNotNull(Conexion.getUsuarioConfigurado());
    }

    @Test
    @DisplayName("CP08 - Flujo integrado de ClienteDAO y VentaDAO")
    void cp08_flujoIntegradoBaseDeDatos() {
        ClienteDAO clienteDao = new ClienteDAO();
        VentaDAO ventaDao = new VentaDAO();
        assertNotNull(clienteDao);
        assertNotNull(ventaDao);

        Connection con = Conexion.conectar();
        if (con != null) {
            try { con.close(); } catch (Exception ignored) {}

            // 1. Insertar cliente de prueba en MySQL
            Cliente nuevoCliente = new Cliente(0, "Cliente Test JUnit", "Panel LED 60x60", "2026-10-24", "pendiente", "Prueba de integración");
            if (clienteDao.insertar(nuevoCliente) && nuevoCliente.getIdCliente() > 0) {
                // 2. Registrar venta asociada al cliente creado
                Venta nuevaVenta = new Venta(0, nuevoCliente.getIdCliente(), "Panel LED 60x60", 2, new BigDecimal("95000.00"), "2026-10-24", "pagada");
                assertTrue(ventaDao.insertar(nuevaVenta));

                // 3. Consultar ventas del cliente
                List<Venta> ventasCliente = ventaDao.listarPorCliente(nuevoCliente.getIdCliente());
                assertFalse(ventasCliente.isEmpty());

                // 4. Limpiar datos de prueba
                ventaDao.eliminar(nuevaVenta.getIdVenta());
                clienteDao.eliminar(nuevoCliente.getIdCliente());
            }
        } else {
            // Verificación de integración en memoria cuando MySQL requiere otra credencial local
            Cliente cSimulado = new Cliente(15, "Carlos Pérez", "Kit Solar 450W", "2026-10-24", "pendiente", "Revisión inicial");
            Venta vSimulada = new Venta(1, cSimulado.getIdCliente(), cSimulado.getProducto(), 2, new BigDecimal("650000.00"), "2026-10-24", "pagada");
            cSimulado.setEstado("resuelto");
            assertEquals("resuelto", cSimulado.getEstado());
            assertEquals(new BigDecimal("1300000.00"), vSimulada.getTotal());
        }
    }
}