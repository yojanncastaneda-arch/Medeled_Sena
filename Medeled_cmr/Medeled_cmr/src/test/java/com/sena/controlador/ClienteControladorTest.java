package com.sena.controlador;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sena.dao.ClienteDAO;
import com.sena.dao.VentaDAO;
import com.sena.modelo.Cliente;
import com.sena.modelo.Venta;
import com.sena.servicio.ClienteServicio;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Pruebas del API de clientes con el servicio real y los DAO simulados (sin MySQL).
 */
@WebMvcTest(ClienteControlador.class)
@Import(ClienteServicio.class)
class ClienteControladorTest {

    private static final String CLIENTE_VALIDO = """
            {"nombre":" Carlos Pérez ","producto":"Panel solar","fechaCompra":"2026-09-01",
             "estado":"pendiente","observaciones":"Reclamo por ruido"}""";

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ClienteDAO clienteDAO;

    @MockitoBean
    private VentaDAO ventaDAO;

    private Cliente cliente(int id) {
        return new Cliente(id, "Carlos Pérez", "Panel solar", "2026-09-01", "pendiente", "Reclamo por ruido");
    }

    @Test
    void listarDevuelveLosClientes() throws Exception {
        when(clienteDAO.listar(null)).thenReturn(List.of(cliente(1), cliente(2)));

        mvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Carlos Pérez"));
    }

    @Test
    void listarConEstadoInvalidoDevuelve400() throws Exception {
        mvc.perform(get("/api/clientes").param("estado", "cerrado"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarClienteInexistenteDevuelve404() throws Exception {
        when(clienteDAO.buscarPorId(99)).thenReturn(null);

        mvc.perform(get("/api/clientes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No existe cliente con id 99"));
    }

    @Test
    void idNoNumericoDevuelve400() throws Exception {
        mvc.perform(get("/api/clientes/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El parámetro 'id' debe ser numérico"));
    }

    @Test
    void crearClienteDevuelve201ConId() throws Exception {
        when(clienteDAO.insertar(any())).thenReturn(7);

        mvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON).content(CLIENTE_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/clientes/7"))
                .andExpect(jsonPath("$.idCliente").value(7))
                .andExpect(jsonPath("$.nombre").value("Carlos Pérez"));
    }

    @Test
    void crearClienteConDatosInvalidosDevuelve400() throws Exception {
        String json = """
                {"nombre":"","producto":"Panel","fechaCompra":"2999-01-01","estado":"cerrado"}""";

        mvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.length()").value(3));
        verify(clienteDAO, never()).insertar(any());
    }

    @Test
    void crearClienteConFechaMalEscritaDevuelve400() throws Exception {
        String json = CLIENTE_VALIDO.replace("2026-09-01", "01/09/2026");

        mvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(org.hamcrest.Matchers.startsWith("Formato inválido en el campo 'fechaCompra'")));
    }

    @Test
    void actualizarClienteInexistenteDevuelve404() throws Exception {
        when(clienteDAO.actualizar(any())).thenReturn(false);

        mvc.perform(put("/api/clientes/99").contentType(MediaType.APPLICATION_JSON).content(CLIENTE_VALIDO))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizarClienteGuardaLosDatos() throws Exception {
        when(clienteDAO.actualizar(any())).thenReturn(true);

        mvc.perform(put("/api/clientes/3").contentType(MediaType.APPLICATION_JSON)
                        .content(CLIENTE_VALIDO.replace("pendiente", "resuelto")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("resuelto"));
        verify(clienteDAO).actualizar(argThat(c -> c.getIdCliente() == 3 && c.getEstado().equals("resuelto")));
    }

    @Test
    void eliminarClienteConVentasDevuelve409() throws Exception {
        when(clienteDAO.buscarPorId(1)).thenReturn(cliente(1));
        when(ventaDAO.listarPorCliente(1)).thenReturn(List.of(
                new Venta(5, 1, "Panel", 1, new BigDecimal("100.00"), "2026-09-16", "pagada")));

        mvc.perform(delete("/api/clientes/1"))
                .andExpect(status().isConflict());
        verify(clienteDAO, never()).eliminar(1);
    }

    @Test
    void eliminarClienteSinVentasDevuelve200() throws Exception {
        when(clienteDAO.buscarPorId(2)).thenReturn(cliente(2));
        when(ventaDAO.listarPorCliente(2)).thenReturn(List.of());
        when(clienteDAO.eliminar(2)).thenReturn(true);

        mvc.perform(delete("/api/clientes/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Cliente 2 eliminado correctamente"));
    }
}
