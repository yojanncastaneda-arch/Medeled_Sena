package com.sena;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del servicio web de Medeled.
 * Levanta un servidor HTTP (Tomcat embebido, puerto 8080) y registra
 * automáticamente los controladores que estén dentro del paquete com.sena.
 */
@SpringBootApplication
public class MedeledApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedeledApiApplication.class, args);
    }
}
