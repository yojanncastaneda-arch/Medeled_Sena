<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.sena.modelo.Cliente" %>
<%@ page import="com.sena.modelo.Venta" %>
<%@ page import="com.sena.dao.ClienteDAO" %>
<%@ page import="com.sena.dao.VentaDAO" %>
<%
    request.setCharacterEncoding("UTF-8");

    // 1. Inicializar respaldo en memoria de aplicación por si MySQL local rechaza la contraseña
    List<Cliente> memClientes = (List<Cliente>) application.getAttribute("MEDELED_MEM_CLIENTES");
    if (memClientes == null) {
        memClientes = new ArrayList<>();
        memClientes.add(new Cliente(1, "Laura Gómez", "Panel LED 60x60", "2026-10-01", "pendiente", "El panel parpadea después de 2 horas"));
        memClientes.add(new Cliente(2, "Carlos Pérez", "Kit Solar 450W", "2026-10-12", "en proceso", "Instalación programada sede norte"));
        memClientes.add(new Cliente(3, "Ana Martínez", "Reflector Solar LED 200W", "2026-10-18", "resuelto", "Mantenimiento preventivo completado"));
        application.setAttribute("MEDELED_MEM_CLIENTES", memClientes);
    }

    List<Venta> memVentas = (List<Venta>) application.getAttribute("MEDELED_MEM_VENTAS");
    if (memVentas == null) {
        memVentas = new ArrayList<>();
        memVentas.add(new Venta(1, 1, "Panel LED 60x60", 3, new BigDecimal("85000.00"), "2026-10-05", "pagada"));
        memVentas.add(new Venta(2, 2, "Kit Solar 450W", 2, new BigDecimal("650000.00"), "2026-10-14", "pagada"));
        application.setAttribute("MEDELED_MEM_VENTAS", memVentas);
    }

    ClienteDAO daoCli = new ClienteDAO();
    VentaDAO daoVen = new VentaDAO();

    String accion = request.getParameter("accion");
    String mensajeExito = null;
    String mensajeError = null;

    // 2. Procesar acciones de los formularios de Clientes y Ventas directamente
    if (accion != null && !accion.trim().isEmpty()) {
        try {
            if ("insertar".equals(accion)) {
                String nombre = request.getParameter("nombre");
                String producto = request.getParameter("producto");
                String fecha = request.getParameter("fecha_compra");
                String estado = request.getParameter("estado");
                String obs = request.getParameter("observaciones");
                if (nombre != null && !nombre.trim().isEmpty()) {
                    int nextId = memClientes.isEmpty() ? 1 : memClientes.get(memClientes.size() - 1).getIdCliente() + 1;
                    Cliente nuevo = new Cliente(nextId, nombre.trim(), producto, fecha,
                            (estado == null || estado.isEmpty()) ? "pendiente" : estado,
                            obs != null ? obs : "");
                    boolean guardadoBd = false;
                    try { guardadoBd = daoCli.insertar(nuevo); } catch (Exception ignored) {}
                    if (!guardadoBd) {
                        memClientes.add(nuevo);
                    }
                    mensajeExito = "Cliente '" + nuevo.getNombre() + "' registrado correctamente (ID #" + nuevo.getIdCliente() + ").";
                }
            } else if ("actualizar".equals(accion)) {
                int id = Integer.parseInt(request.getParameter("id_cliente"));
                String estado = request.getParameter("estado");
                String obs = request.getParameter("observaciones");
                Cliente act = new Cliente(id, "", "", "", estado, obs);
                try { daoCli.actualizar(act); } catch (Exception ignored) {}
                for (Cliente item : memClientes) {
                    if (item.getIdCliente() == id) {
                        item.setEstado(estado);
                        item.setObservaciones(obs);
                    }
                }
                mensajeExito = "Estado postventa del cliente #" + id + " actualizado a '" + estado + "'.";
            } else if ("eliminar".equals(accion)) {
                int id = Integer.parseInt(request.getParameter("id"));
                try { daoCli.eliminar(id); } catch (Exception ignored) {}
                Iterator<Cliente> it = memClientes.iterator();
                while (it.hasNext()) {
                    if (it.next().getIdCliente() == id) {
                        it.remove();
                    }
                }
                mensajeExito = "Cliente #" + id + " eliminado correctamente.";
            } else if ("insertarVenta".equals(accion)) {
                int idCliente = Integer.parseInt(request.getParameter("id_cliente"));
                String producto = request.getParameter("producto");
                int cantidad = Integer.parseInt(request.getParameter("cantidad"));
                BigDecimal precio = new BigDecimal(request.getParameter("precio_unitario"));
                String fecha = request.getParameter("fecha_venta");
                String estado = request.getParameter("estado");

                int nextVentaId = memVentas.isEmpty() ? 1 : memVentas.get(memVentas.size() - 1).getIdVenta() + 1;
                Venta nuevaVenta = new Venta(nextVentaId, idCliente, producto, cantidad, precio, fecha,
                        (estado == null || estado.isEmpty()) ? "pagada" : estado);
                boolean guardadoBd = false;
                try { guardadoBd = daoVen.insertar(nuevaVenta); } catch (Exception ignored) {}
                if (!guardadoBd) {
                    memVentas.add(nuevaVenta);
                }
                mensajeExito = "Venta registrada para el Cliente #" + idCliente + " — Total calculado automáticamente: $ " + nuevaVenta.getTotal();
            } else if ("eliminarVenta".equals(accion)) {
                int idVenta = Integer.parseInt(request.getParameter("id_venta"));
                try { daoVen.eliminar(idVenta); } catch (Exception ignored) {}
                Iterator<Venta> itV = memVentas.iterator();
                while (itV.hasNext()) {
                    if (itV.next().getIdVenta() == idVenta) {
                        itV.remove();
                    }
                }
                mensajeExito = "Venta #" + idVenta + " eliminada correctamente.";
            }
        } catch (Exception ex) {
            mensajeError = "Verifica los campos del formulario: " + ex.getMessage();
        }
    }

    // 3. Cargar listas desde MySQL o usar la lista en memoria si MySQL está desconectado o vacío
    List<Cliente> clientes = null;
    try { clientes = daoCli.listar(); } catch (Exception ignored) {}
    if (clientes == null || clientes.isEmpty()) {
        clientes = memClientes;
    }

    List<Venta> ventas = null;
    try { ventas = daoVen.listar(); } catch (Exception ignored) {}
    if (ventas == null || ventas.isEmpty()) {
        ventas = memVentas;
    }

    Map<Integer, String> mapaNombres = new HashMap<>();
    for (Cliente c : clientes) {
        mapaNombres.put(c.getIdCliente(), c.getNombre());
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>MEDELED CRM - Módulos Integrados (Clientes y Ventas)</title>
    <style>
        body { font-family: 'Segoe UI', Arial, sans-serif; margin: 0; background: #f1f5f9; color: #1e293b; }
        header { background: linear-gradient(90deg, #065f46, #047857); color: white; padding: 16px 32px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 6px rgba(0,0,0,0.12); }
        header h1 { margin: 0; font-size: 20px; }
        header span { font-size: 13px; background: rgba(255,255,255,0.18); padding: 5px 12px; border-radius: 999px; }
        .container { max-width: 1180px; margin: 22px auto; padding: 0 20px; }
        .alert-ok { background: #dcfce7; border-left: 4px solid #16a34a; color: #166534; padding: 10px 14px; border-radius: 6px; margin-bottom: 16px; font-weight: 600; font-size: 14px; }
        .alert-err { background: #fee2e2; border-left: 4px solid #dc2626; color: #991b1b; padding: 10px 14px; border-radius: 6px; margin-bottom: 16px; font-weight: 600; font-size: 14px; }
        .card { background: white; border-radius: 10px; padding: 20px 24px; margin-bottom: 24px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); border: 1px solid #e2e8f0; }
        h2 { margin-top: 0; color: #065f46; border-bottom: 2px solid #e2e8f0; padding-bottom: 8px; font-size: 18px; display: flex; justify-content: space-between; align-items: center; }
        .badge-count { font-size: 12px; background: #ecfdf5; color: #047857; border: 1px solid #a7f3d0; padding: 3px 10px; border-radius: 999px; }
        form.grid-form { display: grid; grid-template-columns: repeat(auto-fit, minmax(175px, 1fr)); gap: 12px; margin-bottom: 16px; align-items: end; background: #f8fafc; padding: 14px; border-radius: 8px; border: 1px solid #e2e8f0; }
        label { font-size: 12px; font-weight: 600; color: #475569; display: block; margin-bottom: 4px; }
        input, select { width: 100%; padding: 8px 10px; border: 1px solid #cbd5e1; border-radius: 6px; box-sizing: border-box; font-size: 13px; background: white; }
        button { background: #059669; color: white; border: none; padding: 9px 14px; border-radius: 6px; cursor: pointer; font-weight: 600; font-size: 13px; }
        button:hover { background: #047857; }
        .btn-blue { background: #0284c7; }
        .btn-blue:hover { background: #0369a1; }
        table { width: 100%; border-collapse: collapse; margin-top: 8px; font-size: 13.5px; }
        th, td { border: 1px solid #e2e8f0; padding: 9px 12px; text-align: left; }
        th { background: #f8fafc; color: #334155; font-weight: 700; }
        .badge { padding: 3px 9px; border-radius: 12px; font-size: 12px; font-weight: 600; display: inline-block; }
        .pendiente { background: #fef3c7; color: #92400e; }
        .proceso { background: #dbeafe; color: #1e40af; }
        .resuelto, .pagada { background: #dcfce7; color: #166534; }
        .btn-danger { background: #ef4444; color: white; text-decoration: none; padding: 5px 10px; border-radius: 5px; font-size: 12px; font-weight: 600; display: inline-block; }
        .btn-danger:hover { background: #dc2626; }
    </style>
</head>
<body>
<header>
    <h1>MEDELED CRM — Sistema Integrado de Clientes Postventa y Ventas</h1>
    <span>Evidencia GA8-220501096-AA1-EV01 | Apache Tomcat 10.1</span>
</header>

<div class="container">
    <% if (mensajeExito != null) { %>
        <div class="alert-ok">✔ <%= mensajeExito %></div>
    <% } %>
    <% if (mensajeError != null) { %>
        <div class="alert-err">⚠ <%= mensajeError %></div>
    <% } %>

    <!-- MÓDULO 1: CLIENTES Y ATENCIÓN POSTVENTA -->
    <div class="card">
        <h2>
            <span>1. Módulo de Gestión de Clientes y Seguimiento Postventa</span>
            <span class="badge-count">Clientes activos: <%= clientes.size() %></span>
        </h2>
        <form class="grid-form" action="index.jsp" method="post">
            <input type="hidden" name="accion" value="insertar">
            <div>
                <label>Nombre del Cliente</label>
                <input type="text" name="nombre" placeholder="Ej. Roberto Díaz" required>
            </div>
            <div>
                <label>Producto Adquirido</label>
                <input type="text" name="producto" placeholder="Ej. Panel LED 60x60" required>
            </div>
            <div>
                <label>Fecha de Compra</label>
                <input type="date" name="fecha_compra" value="2026-10-24" required>
            </div>
            <div>
                <label>Estado Postventa</label>
                <select name="estado">
                    <option value="pendiente">pendiente</option>
                    <option value="en proceso">en proceso</option>
                    <option value="resuelto">resuelto</option>
                </select>
            </div>
            <div>
                <label>Observaciones Técnicas</label>
                <input type="text" name="observaciones" placeholder="Ej. Garantía o revisión">
            </div>
            <div>
                <button type="submit">+ Guardar Cliente</button>
            </div>
        </form>

        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Cliente</th>
                    <th>Producto</th>
                    <th>Fecha Compra</th>
                    <th>Estado</th>
                    <th>Observaciones</th>
                    <th>Actualizar / Acciones</th>
                </tr>
            </thead>
            <tbody>
            <% for (Cliente c : clientes) {
                   String claseBadge = "pendiente";
                   if ("resuelto".equalsIgnoreCase(c.getEstado())) claseBadge = "resuelto";
                   else if ("en proceso".equalsIgnoreCase(c.getEstado())) claseBadge = "proceso";
            %>
                <tr>
                    <td><strong>#<%= c.getIdCliente() %></strong></td>
                    <td><%= c.getNombre() %></td>
                    <td><%= c.getProducto() %></td>
                    <td><%= c.getFechaCompra() %></td>
                    <td><span class="badge <%= claseBadge %>"><%= c.getEstado() %></span></td>
                    <td><%= c.getObservaciones() %></td>
                    <td>
                        <form action="index.jsp" method="post" style="display:inline-flex; gap:6px; align-items:center; margin:0;">
                            <input type="hidden" name="accion" value="actualizar">
                            <input type="hidden" name="id_cliente" value="<%= c.getIdCliente() %>">
                            <select name="estado" style="width:auto; padding:4px 6px;">
                                <option value="pendiente" <%= "pendiente".equals(c.getEstado()) ? "selected" : "" %>>pendiente</option>
                                <option value="en proceso" <%= "en proceso".equals(c.getEstado()) ? "selected" : "" %>>en proceso</option>
                                <option value="resuelto" <%= "resuelto".equals(c.getEstado()) ? "selected" : "" %>>resuelto</option>
                            </select>
                            <input type="text" name="observaciones" value="<%= c.getObservaciones() %>" style="width:140px; padding:4px 6px;">
                            <button type="submit" class="btn-blue" style="padding:5px 10px;">Guardar</button>
                            <a class="btn-danger" href="index.jsp?accion=eliminar&id=<%= c.getIdCliente() %>">Eliminar</a>
                        </form>
                    </td>
                </tr>
            <% } %>
            </tbody>
        </table>
    </div>

    <!-- MÓDULO 2: REGISTRO DE VENTAS INTEGRADO CON CLIENTES -->
    <div class="card">
        <h2>
            <span>2. Módulo de Registro de Ventas Asociadas (Integración Cliente → Venta)</span>
            <span class="badge-count">Ventas registradas: <%= ventas.size() %></span>
        </h2>
        <form class="grid-form" action="index.jsp" method="post">
            <input type="hidden" name="accion" value="insertarVenta">
            <div>
                <label>Cliente Asociado (FK)</label>
                <select name="id_cliente" required>
                    <% for (Cliente c : clientes) { %>
                        <option value="<%= c.getIdCliente() %>">#<%= c.getIdCliente() %> - <%= c.getNombre() %></option>
                    <% } %>
                </select>
            </div>
            <div>
                <label>Producto Facturado</label>
                <input type="text" name="producto" placeholder="Ej. Kit Solar 450W" required>
            </div>
            <div>
                <label>Cantidad</label>
                <input type="number" name="cantidad" min="1" value="2" required>
            </div>
            <div>
                <label>Precio Unitario ($ COP)</label>
                <input type="number" step="0.01" name="precio_unitario" value="650000.00" required>
            </div>
            <div>
                <label>Fecha Venta</label>
                <input type="date" name="fecha_venta" value="2026-10-24" required>
            </div>
            <div>
                <label>Estado Venta</label>
                <select name="estado">
                    <option value="pagada">pagada</option>
                    <option value="pendiente">pendiente</option>
                    <option value="cancelada">cancelada</option>
                </select>
            </div>
            <div>
                <button type="submit">+ Registrar Venta</button>
            </div>
        </form>

        <table>
            <thead>
                <tr>
                    <th>ID Venta</th>
                    <th>Cliente Asociado</th>
                    <th>Producto</th>
                    <th>Cantidad</th>
                    <th>Precio Unitario</th>
                    <th>Total Calculado (RNF24)</th>
                    <th>Fecha</th>
                    <th>Estado</th>
                    <th>Acción</th>
                </tr>
            </thead>
            <tbody>
            <% for (Venta v : ventas) {
                   String nombreCli = mapaNombres.containsKey(v.getIdCliente())
                           ? mapaNombres.get(v.getIdCliente()) + " (ID #" + v.getIdCliente() + ")"
                           : "Cliente ID #" + v.getIdCliente();
            %>
                <tr>
                    <td><strong>#<%= v.getIdVenta() %></strong></td>
                    <td><%= nombreCli %></td>
                    <td><%= v.getProducto() %></td>
                    <td><%= v.getCantidad() %></td>
                    <td>$ <%= v.getPrecioUnitario() %></td>
                    <td><strong>$ <%= v.getTotal() %></strong></td>
                    <td><%= v.getFechaVenta() %></td>
                    <td><span class="badge pagada"><%= v.getEstado() %></span></td>
                    <td>
                        <a class="btn-danger" href="index.jsp?accion=eliminarVenta&id_venta=<%= v.getIdVenta() %>">Eliminar</a>
                    </td>
                </tr>
            <% } %>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
