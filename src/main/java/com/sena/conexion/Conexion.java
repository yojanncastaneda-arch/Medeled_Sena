package com.sena.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase de conexión JDBC para MEDELED CRM con detección automática de credenciales locales
 * e inicialización automática de tablas si aún no existen.
 */
public class Conexion {

    private static final String URL = System.getenv("MEDELED_DB_URL") != null
            ? System.getenv("MEDELED_DB_URL")
            : "jdbc:mysql://localhost:3306/medeled_cmr?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Bogota";

    private static final String USUARIO = System.getenv("MEDELED_DB_USER") != null
            ? System.getenv("MEDELED_DB_USER")
            : "root";

    private static final String[] CLAVES_CANDIDATAS = {
        System.getenv("MEDELED_DB_PASSWORD") != null ? System.getenv("MEDELED_DB_PASSWORD") : "",
        "Medeled",
        "medeled",
        "root",
        "1234",
        "admin",
        ""
    };

    private static String claveActiva = null;
    private static boolean esquemaVerificado = false;

    public static Connection conectar() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            if (claveActiva != null) {
                return DriverManager.getConnection(URL, USUARIO, claveActiva);
            }
            for (String clave : CLAVES_CANDIDATAS) {
                try {
                    Connection con = DriverManager.getConnection(URL, USUARIO, clave);
                    claveActiva = clave;
                    if (!esquemaVerificado) {
                        esquemaVerificado = true;
                        asegurarTablas(con);
                    }
                    return con;
                } catch (SQLException ignored) {
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Aviso de conexión MySQL: " + e.getMessage());
        }
        return null;
    }

    private static void asegurarTablas(Connection con) {
        try (Statement st = con.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS clientes ("
                    + "id_cliente INT AUTO_INCREMENT PRIMARY KEY, "
                    + "nombre VARCHAR(100) NOT NULL, "
                    + "producto VARCHAR(100) NOT NULL, "
                    + "fecha_compra DATE NOT NULL, "
                    + "estado ENUM('pendiente','en proceso','resuelto') DEFAULT 'pendiente', "
                    + "observaciones TEXT)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS venta ("
                    + "id_venta INT AUTO_INCREMENT PRIMARY KEY, "
                    + "id_cliente INT NOT NULL, "
                    + "producto VARCHAR(100) NOT NULL, "
                    + "cantidad INT NOT NULL DEFAULT 1, "
                    + "precio_unitario DECIMAL(10,2) NOT NULL, "
                    + "total DECIMAL(10,2) NOT NULL, "
                    + "fecha_venta DATE NOT NULL, "
                    + "estado ENUM('pendiente','pagada','cancelada') DEFAULT 'pendiente', "
                    + "FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente) ON DELETE CASCADE)");
        } catch (SQLException ignored) {
        }
    }

    public static String getUrlConfigurada() {
        return URL;
    }

    public static String getUsuarioConfigurado() {
        return USUARIO;
    }
}
