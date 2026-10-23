package com.sena.conexion;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    // Las claves se leen del archivo .env (que NO se sube a Git).
    // Si el .env no existe o le falta una clave, se usan las variables de entorno del sistema.
    private static final Dotenv ENV = Dotenv.configure().ignoreIfMissing().load();

    private static final String URL = ENV.get("MEDELED_DB_URL", "jdbc:mysql://localhost:3306/medeled_cmr");
    private static final String USUARIO = ENV.get("MEDELED_DB_USER", "root");
    private static final String CONTRASENA = ENV.get("MEDELED_DB_PASSWORD", "");

    public static Connection conectar() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            System.out.println("Conexión exitosa a MySQL");
        } catch (SQLException e) {
            System.out.println("Error al conectar: " + e.getMessage());
        }
        return con;
    }

    // Igual que conectar(), pero si falla lanza la excepción en lugar de devolver null.
    // La usan los DAO del API para poder responder un error al cliente.
    public static Connection obtener() throws SQLException {
        Connection con = conectar();
        if (con == null) {
            throw new SQLException("No fue posible conectar con la base de datos");
        }
        return con;
    }

    public static void main(String[] args) {
        conectar();
    }
}
