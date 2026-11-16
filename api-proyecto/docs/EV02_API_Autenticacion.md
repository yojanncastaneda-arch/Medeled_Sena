# Documentación API de Autenticación – GA7-220501096-AA5-EV02

Proyecto: **Medeled CRM** · SENA – Análisis y Desarrollo de Software

## 1. Descripción

Servicio web REST que permite **registrar usuarios** e **iniciar sesión**. Recibe un usuario y
una contraseña; si la autenticación es correcta responde *"Autenticación satisfactoria"*, en caso
contrario responde *"Error en la autenticación"*.

| Dato              | Valor                                         |
|-------------------|-----------------------------------------------|
| Tecnología        | Java 21 · Spring Boot 3.5 · MySQL 8 · JDBC    |
| URL base (local)  | `http://localhost:8080`                       |
| Formato           | JSON (`Content-Type: application/json`)       |
| Documentación viva| `http://localhost:8080/swagger-ui.html`       |
| Especificación    | `http://localhost:8080/v3/api-docs` (OpenAPI) |
| Repositorio       | https://github.com/yojanncastaneda-arch/evidencia-04-Medeled |

## 2. Endpoints

| # | Método | Endpoint                                   | Descripción                    |
|---|--------|--------------------------------------------|--------------------------------|
| 1 | POST   | `http://localhost:8080/api/auth/registro`  | Registra un usuario nuevo      |
| 2 | POST   | `http://localhost:8080/api/auth/login`     | Inicia sesión (autenticación)  |

Todas las respuestas tienen el mismo formato:

```json
{ "exito": true, "mensaje": "texto", "errores": ["solo cuando fallan las validaciones"] }
```

### 2.1 POST /api/auth/registro

**Cuerpo:**

```json
{ "usuario": "ana.zuluaga", "contrasena": "Clave2026" }
```

| Campo        | Reglas de validación                                                  |
|--------------|-----------------------------------------------------------------------|
| `usuario`    | Obligatorio · 4 a 30 caracteres · solo letras, números, `.` y `_` · no distingue mayúsculas |
| `contrasena` | Obligatoria · 8 a 64 caracteres · al menos una letra y un número      |

**Respuestas:**

| Código | Cuándo                         | Ejemplo de respuesta |
|--------|--------------------------------|----------------------|
| 201    | Registro correcto              | `{"exito":true,"mensaje":"Usuario registrado correctamente"}` |
| 400    | No cumple las validaciones     | `{"exito":false,"mensaje":"Datos inválidos","errores":["La contraseña debe tener al menos una letra y un número"]}` |
| 400    | JSON mal formado               | `{"exito":false,"mensaje":"El cuerpo de la petición debe ser un JSON válido"}` |
| 409    | El usuario ya existe           | `{"exito":false,"mensaje":"El usuario 'ana.zuluaga' ya está registrado"}` |
| 500    | Falla la base de datos         | `{"exito":false,"mensaje":"Error interno, intente más tarde"}` |

### 2.2 POST /api/auth/login

**Cuerpo:**

```json
{ "usuario": "ana.zuluaga", "contrasena": "Clave2026" }
```

| Campo        | Reglas de validación |
|--------------|----------------------|
| `usuario`    | Obligatorio          |
| `contrasena` | Obligatoria          |

**Respuestas:**

| Código | Cuándo                                  | Ejemplo de respuesta |
|--------|-----------------------------------------|----------------------|
| 200    | Usuario y contraseña correctos          | `{"exito":true,"mensaje":"Autenticación satisfactoria"}` |
| 400    | Falta usuario o contraseña              | `{"exito":false,"mensaje":"Datos inválidos","errores":["La contraseña es obligatoria"]}` |
| 401    | Usuario no existe o contraseña errada   | `{"exito":false,"mensaje":"Error en la autenticación: usuario o contraseña incorrectos"}` |

> Por seguridad, el mensaje 401 es el mismo si el usuario no existe o si la contraseña está mal,
> así no se revela qué usuarios están registrados.

## 3. Seguridad

- Las contraseñas se guardan cifradas con **BCrypt** (columna `contrasena_hash`), nunca en texto plano.
- Las consultas usan `PreparedStatement`, lo que evita inyección SQL.
- Las claves de la base de datos están en el archivo `.env`, que no se sube al repositorio.

## 4. Pruebas con Postman

Colección: [`postman/Medeled_Auth_AA5_EV02.postman_collection.json`](../postman/Medeled_Auth_AA5_EV02.postman_collection.json)

1. Encender el servidor (`mvn spring-boot:run` en `Medeled_cmr/Medeled_cmr`, o *Run* en NetBeans).
2. En Postman: **Import** → seleccionar el archivo de la colección.
3. Clic derecho sobre la colección → **Run collection** → **Run**.

La colección trae 11 peticiones con 28 verificaciones automáticas (pestaña *Tests* de cada petición):

| #  | Prueba                               | Esperado |
|----|--------------------------------------|----------|
| 1  | Registro exitoso                     | 201      |
| 2  | Registro de usuario repetido         | 409      |
| 3  | Registro con campos vacíos           | 400      |
| 4  | Registro con contraseña débil        | 400      |
| 5  | Registro con usuario inválido        | 400      |
| 6  | Registro con JSON mal formado        | 400      |
| 7  | Login correcto                       | 200      |
| 8  | Login con usuario en MAYÚSCULAS      | 200      |
| 9  | Login con contraseña incorrecta      | 401      |
| 10 | Login con usuario inexistente        | 401      |
| 11 | Login sin contraseña                 | 400      |

Cada corrida crea un usuario nuevo (`prueba_<fecha>`), por eso se puede repetir sin errores.

## 5. Guion para el video (3 a 5 minutos)

1. **Presentación (20 s):** nombre, ficha, evidencia AA5-EV02.
2. **Código (60 s):** en NetBeans mostrar `AuthControlador` (endpoints), `SolicitudRegistro`
   (validaciones) y `AuthServicio` (BCrypt). Mencionar que el código está comentado.
3. **Encender el servidor (20 s):** *Run* y mostrar en consola `Tomcat started on port 8080`.
4. **Postman, una por una (90 s):** registro exitoso (201) → mismo registro (409) →
   contraseña débil (400) → login correcto (*Autenticación satisfactoria*) →
   contraseña mala (*Error en la autenticación*). Mostrar la pestaña *Test Results* en verde.
5. **Run collection (30 s):** ejecutar la colección completa y mostrar 28/28 pruebas aprobadas.
6. **Base de datos (20 s):** en MySQL Workbench `SELECT * FROM usuario;` y señalar que la
   contraseña está cifrada (`$2a$10$...`).
7. **Swagger y GitHub (20 s):** abrir `http://localhost:8080/swagger-ui.html` y el repositorio
   mostrando la rama y los commits.
