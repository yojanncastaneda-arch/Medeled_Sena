CREATE DATABASE IF NOT EXISTS medeled_cmr;
USE medeled_cmr;

CREATE TABLE IF NOT EXISTS cliente (
    idcliente INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    producto VARCHAR(100) NOT NULL,
    fecha_compra DATE NOT NULL,
    estado VARCHAR(30) NOT NULL,
    observaciones VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS venta (
    idventa INT AUTO_INCREMENT PRIMARY KEY,
    idcliente INT NOT NULL,
    producto VARCHAR(100) NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    fecha_venta DATE NOT NULL,
    estado VARCHAR(30) NOT NULL,
    FOREIGN KEY (idcliente) REFERENCES cliente(idcliente)
);

-- Usuarios del sistema para el servicio de registro e inicio de sesión.
-- La contraseña nunca se guarda en texto plano, solo su hash BCrypt.
CREATE TABLE IF NOT EXISTS usuario (
    idusuario INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(30) NOT NULL UNIQUE,
    contrasena_hash VARCHAR(100) NOT NULL,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

SELECT * FROM medeled_cmr.cliente;
