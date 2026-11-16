package com.sena.controlador;

import com.sena.dto.RespuestaApi;
import com.sena.dto.SolicitudCliente;
import com.sena.modelo.Cliente;
import com.sena.modelo.Venta;
import com.sena.servicio.ClienteServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * API REST de clientes (CRUD completo).
 *   GET    /api/clientes             -> lista (filtro opcional ?estado=)
 *   GET    /api/clientes/{id}        -> consulta uno
 *   GET    /api/clientes/{id}/ventas -> ventas del cliente
 *   POST   /api/clientes             -> crea
 *   PUT    /api/clientes/{id}        -> actualiza
 *   DELETE /api/clientes/{id}        -> elimina
 */
@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Gestión de clientes y sus reclamos postventa")
public class ClienteControlador {

    private final ClienteServicio clienteServicio;

    public ClienteControlador(ClienteServicio clienteServicio) {
        this.clienteServicio = clienteServicio;
    }

    @GetMapping
    @Operation(summary = "Listar clientes", description = "Se puede filtrar por estado: pendiente, en_proceso o resuelto")
    public List<Cliente> listar(
            @Parameter(description = "Estado del cliente (opcional)") @RequestParam(required = false) String estado)
            throws SQLException {
        return clienteServicio.listar(estado);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un cliente por id")
    public Cliente buscar(@PathVariable int id) throws SQLException {
        return clienteServicio.buscar(id);
    }

    @GetMapping("/{id}/ventas")
    @Operation(summary = "Listar las ventas de un cliente")
    public List<Venta> ventas(@PathVariable int id) throws SQLException {
        return clienteServicio.ventasDelCliente(id);
    }

    // Responde 201 con el cliente creado y el encabezado Location apuntando a él
    @PostMapping
    @Operation(summary = "Crear un cliente")
    public ResponseEntity<Cliente> crear(@Valid @RequestBody SolicitudCliente datos) throws SQLException {
        Cliente cliente = clienteServicio.crear(datos);
        return ResponseEntity.created(URI.create("/api/clientes/" + cliente.getIdCliente())).body(cliente);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un cliente")
    public Cliente actualizar(@PathVariable int id, @Valid @RequestBody SolicitudCliente datos) throws SQLException {
        return clienteServicio.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un cliente", description = "No se permite si el cliente tiene ventas registradas")
    public RespuestaApi eliminar(@PathVariable int id) throws SQLException {
        clienteServicio.eliminar(id);
        return RespuestaApi.ok("Cliente " + id + " eliminado correctamente");
    }
}
