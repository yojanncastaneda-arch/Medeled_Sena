package com.sena.configuracion;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Datos generales que aparecen en la documentación del API.
 * Con el servidor encendido se consulta en:
 *   http://localhost:8080/swagger-ui.html  (página interactiva)
 *   http://localhost:8080/v3/api-docs      (especificación OpenAPI en JSON)
 */
@Configuration
public class DocumentacionApi {

    @Bean
    public OpenAPI informacionApi() {
        return new OpenAPI().info(new Info()
                .title("API Medeled CRM")
                .version("1.0")
                .description("Servicios web de Medeled: registro e inicio de sesión de usuarios, "
                        + "gestión de clientes y ventas. Proyecto SENA - Análisis y Desarrollo de Software."));
    }
}
