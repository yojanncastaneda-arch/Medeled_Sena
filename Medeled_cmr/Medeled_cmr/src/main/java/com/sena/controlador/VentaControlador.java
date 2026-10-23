package com.sena.controlador;

import com.sena.dto.RespuestaApi;
import com.sena.dto.SolicitudVenta;
import com.sena.modelo.Venta;
import com.sena.servicio.VentaServicio;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * API REST de ventas (CRUD completo).
 *   GET    /api/ventas      -> lista
 *   GET    /api/ventas/{id} -> consulta una
 *   POST   /api/ventas      -> crea
 *   PUT    /api/ventas/{id} -> actualiza
 *   DELETE /api/ventas/{id} -> elimina
 */
@RestController
@RequestMapping("/api/ventas")
@Tag(name = "Ventas", description = "Gestión de ventas asociadas a clientes")
public class VentaControlador {

    private final VentaServicio ventaServicio;

    public VentaControlador(VentaServicio ventaServicio) {
        this.ventaServicio = ventaServicio;
    }

    @GetMapping
    @Operation(summary = "Listar ventas")
    public List<Venta> listar() throws SQLException {
        return ventaServicio.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar una venta por id")
    public Venta buscar(@PathVariable int id) throws SQLException {
        return ventaServicio.buscar(id);
    }

    // Responde 201 con la venta creada y el encabezado Location apuntando a ella
    @PostMapping
    @Operation(summary = "Registrar una venta", description = "El cliente indicado en idCliente debe existir")
    public ResponseEntity<Venta> crear(@Valid @RequestBody SolicitudVenta datos) throws SQLException {
        Venta venta = ventaServicio.crear(datos);
        return ResponseEntity.created(URI.create("/api/ventas/" + venta.getIdVenta())).body(venta);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una venta")
    public Venta actualizar(@PathVariable int id, @Valid @RequestBody SolicitudVenta datos) throws SQLException {
        return ventaServicio.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una venta")
    public RespuestaApi eliminar(@PathVariable int id) throws SQLException {
        ventaServicio.eliminar(id);
        return RespuestaApi.ok("Venta " + id + " eliminada correctamente");
    }
}
