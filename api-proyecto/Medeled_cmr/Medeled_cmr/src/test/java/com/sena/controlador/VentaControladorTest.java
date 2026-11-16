package com.sena.controlador;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sena.dao.ClienteDAO;
import com.sena.dao.VentaDAO;
import com.sena.modelo.Cliente;
import com.sena.modelo.Venta;
import com.sena.servicio.VentaServicio;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Pruebas del API de ventas con el servicio real y los DAO simulados (sin MySQL).
 */
@WebMvcTest(VentaControlador.class)
@Import(VentaServicio.class)
class VentaControladorTest {

    private static final String VENTA_VALIDA = """
            {"idCliente":1,"producto":"Panel solar 450W","cantidad":4,
             "precioUnitario":850000.00,"fechaVenta":"2026-09-16","estado":"pendiente"}""";

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private VentaDAO ventaDAO;

    @MockitoBean
    private ClienteDAO clienteDAO;

    private Venta venta(int id) {
        return new Venta(id, 1, "Panel solar 450W", 4, new BigDecimal("850000.00"), "2026-09-16", "pendiente");
    }

    @Test
    void consultarVentaIncluyeElTotal() throws Exception {
        when(ventaDAO.buscarPorId(3)).thenReturn(venta(3));

        mvc.perform(get("/api/ventas/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(3400000.00));
    }

    @Test
    void crearVentaDevuelve201() throws Exception {
        when(clienteDAO.buscarPorId(1)).thenReturn(new Cliente(1, "Carlos", "Panel", "2026-09-01", "pendiente", null));
        when(ventaDAO.insertar(any())).thenReturn(10);

        mvc.perform(post("/api/ventas").contentType(MediaType.APPLICATION_JSON).content(VENTA_VALIDA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idVenta").value(10));
    }

    @Test
    void crearVentaParaClienteInexistenteDevuelve400() throws Exception {
        when(clienteDAO.buscarPorId(1)).thenReturn(null);

        mvc.perform(post("/api/ventas").contentType(MediaType.APPLICATION_JSON).content(VENTA_VALIDA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El cliente con id 1 no existe"));
        verify(ventaDAO, never()).insertar(any());
    }

    @Test
    void crearVentaConValoresInvalidosDevuelve400() throws Exception {
        String json = """
                {"idCliente":-1,"producto":"","cantidad":0,"precioUnitario":0,
                 "fechaVenta":"2026-09-16","estado":"regalada"}""";

        mvc.perform(post("/api/ventas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.length()").value(5));
    }

    @Test
    void actualizarVentaInexistenteDevuelve404() throws Exception {
        when(ventaDAO.buscarPorId(99)).thenReturn(null);

        mvc.perform(put("/api/ventas/99").contentType(MediaType.APPLICATION_JSON).content(VENTA_VALIDA))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminarVentaInexistenteDevuelve404() throws Exception {
        when(ventaDAO.eliminar(99)).thenReturn(false);

        mvc.perform(delete("/api/ventas/99"))
                .andExpect(status().isNotFound());
    }
}
