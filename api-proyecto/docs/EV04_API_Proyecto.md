# Documentación API del Proyecto – GA7-220501096-AA5-EV04

Proyecto: **Medeled CRM** · SENA – Análisis y Desarrollo de Software

## 1. Descripción

API REST del proyecto Medeled. Permite gestionar los **clientes** (con su reclamo postventa)
y las **ventas** asociadas a cada cliente. Complementa el API de autenticación de la evidencia
EV02 ([EV02_API_Autenticacion.md](EV02_API_Autenticacion.md)).

| Dato               | Valor                                         |
|--------------------|-----------------------------------------------|
| Tecnología         | Java 21 · Spring Boot 3.5 · MySQL 8 · JDBC    |
| URL base (local)   | `http://localhost:8080`                       |
| Formato            | JSON (`Content-Type: application/json`)       |
| Documentación viva | `http://localhost:8080/swagger-ui.html`       |
| Especificación     | `http://localhost:8080/v3/api-docs` (OpenAPI) |
| Repositorio        | https://github.com/yojanncastaneda-arch/evidencia-04-Medeled |

Arquitectura por capas:

```
Cliente HTTP (Postman) → controlador (REST/JSON) → servicio (reglas de negocio) → dao (JDBC) → MySQL
```

## 2. Endpoints del proyecto

| #  | Método | Endpoint                                            | Descripción                              | Éxito |
|----|--------|-----------------------------------------------------|------------------------------------------|-------|
| 1  | POST   | `http://localhost:8080/api/auth/registro`           | Registrar usuario                        | 201   |
| 2  | POST   | `http://localhost:8080/api/auth/login`              | Iniciar sesión                           | 200   |
| 3  | GET    | `http://localhost:8080/api/clientes`                | Listar clientes (`?estado=` opcional)    | 200   |
| 4  | GET    | `http://localhost:8080/api/clientes/{id}`           | Consultar un cliente                     | 200   |
| 5  | GET    | `http://localhost:8080/api/clientes/{id}/ventas`    | Ventas de un cliente                     | 200   |
| 6  | POST   | `http://localhost:8080/api/clientes`                | Crear cliente                            | 201   |
| 7  | PUT    | `http://localhost:8080/api/clientes/{id}`           | Actualizar cliente                       | 200   |
| 8  | DELETE | `http://localhost:8080/api/clientes/{id}`           | Eliminar cliente                         | 200   |
| 9  | GET    | `http://localhost:8080/api/ventas`                  | Listar ventas                            | 200   |
| 10 | GET    | `http://localhost:8080/api/ventas/{id}`             | Consultar una venta                      | 200   |
| 11 | POST   | `http://localhost:8080/api/ventas`                  | Registrar venta                          | 201   |
| 12 | PUT    | `http://localhost:8080/api/ventas/{id}`             | Actualizar venta                         | 200   |
| 13 | DELETE | `http://localhost:8080/api/ventas/{id}`             | Eliminar venta                           | 200   |

### Códigos de respuesta

| Código | Significado                                                                 |
|--------|-----------------------------------------------------------------------------|
| 200    | Operación correcta                                                          |
| 201    | Recurso creado (incluye encabezado `Location` con la URL del nuevo recurso) |
| 400    | Datos inválidos, JSON mal formado, id no numérico o cliente inexistente en una venta |
| 404    | El cliente o la venta no existe                                             |
| 409    | Conflicto: eliminar un cliente que tiene ventas                             |
| 500    | Error de base de datos (no se expone el detalle técnico)                   |

Los errores siempre tienen este formato:

```json
{ "exito": false, "mensaje": "Datos inválidos", "errores": ["El nombre es obligatorio"] }
```

## 3. Clientes

### Modelo

| Campo           | Tipo   | Reglas                                                    |
|-----------------|--------|-----------------------------------------------------------|
| `idCliente`     | número | Lo asigna la base de datos (solo en respuestas)           |
| `nombre`        | texto  | Obligatorio · máx. 100                                    |
| `producto`      | texto  | Obligatorio · máx. 100                                    |
| `fechaCompra`   | fecha  | Obligatoria · formato `yyyy-MM-dd` · no puede ser futura  |
| `estado`        | texto  | Obligatorio · `pendiente`, `en_proceso` o `resuelto`      |
| `observaciones` | texto  | Opcional · máx. 255                                       |

### POST /api/clientes

```json
{
  "nombre": "Carlos Pérez",
  "producto": "Panel solar 450W",
  "fechaCompra": "2026-09-01",
  "estado": "pendiente",
  "observaciones": "Reclamo por bajo rendimiento"
}
```

Respuesta **201** · `Location: /api/clientes/3`

```json
{
  "idCliente": 3,
  "nombre": "Carlos Pérez",
  "producto": "Panel solar 450W",
  "fechaCompra": "2026-09-01",
  "estado": "pendiente",
  "observaciones": "Reclamo por bajo rendimiento"
}
```

Respuesta **400** (datos inválidos):

```json
{
  "exito": false,
  "mensaje": "Datos inválidos",
  "errores": [
    "El estado debe ser: pendiente, en_proceso o resuelto",
    "El nombre es obligatorio",
    "La fecha de compra no puede ser futura"
  ]
}
```

### GET /api/clientes?estado=pendiente

Respuesta **200**: lista de clientes (`[ {...}, {...} ]`). Un estado no permitido responde 400.

### GET /api/clientes/{id}

Respuesta **200** con el cliente, o **404**: `{"exito":false,"mensaje":"No existe cliente con id 99"}`

### GET /api/clientes/{id}/ventas

Respuesta **200** con la lista de ventas del cliente, o **404** si el cliente no existe.

### PUT /api/clientes/{id}

Mismo cuerpo que el POST. Respuesta **200** con el cliente actualizado, o **404**.

### DELETE /api/clientes/{id}

| Código | Respuesta |
|--------|-----------|
| 200    | `{"exito":true,"mensaje":"Cliente 3 eliminado correctamente"}` |
| 404    | `{"exito":false,"mensaje":"No existe cliente con id 3"}` |
| 409    | `{"exito":false,"mensaje":"No se puede eliminar el cliente 3 porque tiene ventas registradas"}` |

## 4. Ventas

### Modelo

| Campo            | Tipo    | Reglas                                                        |
|------------------|---------|---------------------------------------------------------------|
| `idVenta`        | número  | Lo asigna la base de datos (solo en respuestas)               |
| `idCliente`      | número  | Obligatorio · positivo · **el cliente debe existir**          |
| `producto`       | texto   | Obligatorio · máx. 100                                        |
| `cantidad`       | número  | Obligatorio · de 1 a 10000                                    |
| `precioUnitario` | decimal | Obligatorio · mayor que 0 · máx. 8 enteros y 2 decimales      |
| `fechaVenta`     | fecha   | Obligatoria · formato `yyyy-MM-dd` · no puede ser futura      |
| `estado`         | texto   | Obligatorio · `pendiente`, `pagada` o `anulada`               |
| `total`          | decimal | Calculado: `cantidad × precioUnitario` (solo en respuestas)   |

### POST /api/ventas

```json
{
  "idCliente": 3,
  "producto": "Inversor 3kW",
  "cantidad": 2,
  "precioUnitario": 1250000.50,
  "fechaVenta": "2026-10-22",
  "estado": "pendiente"
}
```

Respuesta **201** · `Location: /api/ventas/8`

```json
{
  "idVenta": 8,
  "idCliente": 3,
  "producto": "Inversor 3kW",
  "cantidad": 2,
  "precioUnitario": 1250000.50,
  "fechaVenta": "2026-10-22",
  "estado": "pendiente",
  "total": 2500001.00
}
```

Si el cliente no existe → **400** `{"exito":false,"mensaje":"El cliente con id 999 no existe"}`

### GET /api/ventas · GET /api/ventas/{id} · PUT /api/ventas/{id} · DELETE /api/ventas/{id}

Funcionan igual que en clientes: 200 si todo está bien, 404 si la venta no existe y 400 si
los datos no cumplen las validaciones. El DELETE responde
`{"exito":true,"mensaje":"Venta 8 eliminada correctamente"}`.

## 5. Pruebas con Postman

Colección: [`postman/Medeled_Proyecto_AA5_EV04.postman_collection.json`](../postman/Medeled_Proyecto_AA5_EV04.postman_collection.json)

1. Encender el servidor (`mvn spring-boot:run` en `Medeled_cmr/Medeled_cmr`, o *Run* en NetBeans).
2. En Postman: **Import** → seleccionar el archivo.
3. Clic derecho sobre la colección → **Run collection** → **Run** (en orden, sin cambiarlo).

La colección hace el ciclo completo y al final borra lo que creó: **23 peticiones, 60 verificaciones**.
Las peticiones comparten las variables `{{idCliente}}` y `{{idVenta}}`, que se guardan solas.

| #  | Prueba                                       | Esperado |
|----|----------------------------------------------|----------|
| 1  | Crear cliente                                | 201      |
| 2  | Crear cliente con datos inválidos            | 400      |
| 3  | Crear cliente con fecha mal escrita          | 400      |
| 4  | Listar clientes                              | 200      |
| 5  | Listar clientes por estado                   | 200      |
| 6  | Listar con estado inválido                   | 400      |
| 7  | Consultar cliente por id                     | 200      |
| 8  | Consultar cliente inexistente                | 404      |
| 9  | Consultar con id no numérico                 | 400      |
| 10 | Actualizar cliente                           | 200      |
| 11 | Actualizar cliente inexistente               | 404      |
| 12 | Registrar venta (verifica el total)          | 201      |
| 13 | Registrar venta para cliente inexistente     | 400      |
| 14 | Registrar venta con valores inválidos        | 400      |
| 15 | Listar ventas                                | 200      |
| 16 | Consultar venta por id                       | 200      |
| 17 | Ventas de un cliente                         | 200      |
| 18 | Actualizar venta                             | 200      |
| 19 | Eliminar cliente con ventas                  | 409      |
| 20 | Eliminar venta                               | 200      |
| 21 | Eliminar venta ya eliminada                  | 404      |
| 22 | Eliminar cliente                             | 200      |
| 23 | Consultar cliente eliminado                  | 404      |

Además hay 28 pruebas automáticas en Java (`mvn test`) que no necesitan MySQL.

## 6. Guion para el video (5 a 7 minutos)

1. **Presentación (20 s):** nombre, ficha, evidencia AA5-EV04 y qué hace el API.
2. **Código (90 s):** en NetBeans mostrar las capas: `ClienteControlador` y `VentaControlador`
   (endpoints), `SolicitudCliente` y `SolicitudVenta` (validaciones), `ClienteServicio`
   (regla de no eliminar clientes con ventas) y `ManejadorErrores` (códigos HTTP).
3. **Encender el servidor (20 s):** *Run* y mostrar `Tomcat started on port 8080`.
4. **Postman, una por una (2-3 min):**
   - Crear cliente (201) y mostrar cómo `idCliente` queda guardado en las variables.
   - Crear cliente con datos inválidos (400) y leer la lista de errores.
   - Actualizar el cliente a `en_proceso`.
   - Registrar venta y señalar el `total` calculado.
   - Registrar venta para un cliente inexistente (400).
   - Intentar eliminar el cliente con ventas (409) y explicar por qué.
5. **Run collection (40 s):** ejecutar la colección completa y mostrar 60/60 verificaciones aprobadas.
6. **Base de datos (20 s):** en MySQL Workbench mostrar que la limpieza dejó las tablas como estaban.
7. **Swagger y GitHub (30 s):** abrir `http://localhost:8080/swagger-ui.html` con los grupos
   Autenticación, Clientes y Ventas; luego el repositorio con las ramas y los commits.
