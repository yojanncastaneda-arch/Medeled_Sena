package com.sena.controlador;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sena.dao.UsuarioDAO;
import com.sena.modelo.Usuario;
import com.sena.servicio.AuthServicio;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Pruebas de los servicios web de registro e inicio de sesión.
 * Se usa el servicio real y se simula (mock) el DAO, así no se necesita MySQL.
 */
@WebMvcTest(AuthControlador.class)
@Import(AuthServicio.class)
class AuthControladorTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private UsuarioDAO usuarioDAO;

    private ResultActions enviar(String ruta, String json) throws Exception {
        return mvc.perform(post(ruta).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    // ---------- Registro ----------

    @Test
    void registroCorrectoGuardaContrasenaCifrada() throws Exception {
        when(usuarioDAO.existe("ana.zuluaga")).thenReturn(false);

        enviar("/api/auth/registro", "{\"usuario\":\"Ana.Zuluaga\",\"contrasena\":\"Clave2026\"}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Usuario registrado correctamente"));

        // Se guarda en minúsculas y con hash BCrypt, nunca la contraseña en texto plano
        verify(usuarioDAO).insertar(argThat(u -> u.getUsuario().equals("ana.zuluaga")
                && !u.getContrasenaHash().equals("Clave2026")
                && new BCryptPasswordEncoder().matches("Clave2026", u.getContrasenaHash())));
    }

    @Test
    void registroConUsuarioExistenteDevuelve409() throws Exception {
        when(usuarioDAO.existe("anam")).thenReturn(true);

        enviar("/api/auth/registro", "{\"usuario\":\"anam\",\"contrasena\":\"Clave2026\"}")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.exito").value(false));
        verify(usuarioDAO, never()).insertar(any());
    }

    @Test
    void registroConCamposVaciosDevuelve400() throws Exception {
        enviar("/api/auth/registro", "{\"usuario\":\"\",\"contrasena\":\"\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Datos inválidos"))
                .andExpect(jsonPath("$.errores.length()").value(4));
        verify(usuarioDAO, never()).insertar(any());
    }

    @Test
    void registroConContrasenaDebilDevuelve400() throws Exception {
        enviar("/api/auth/registro", "{\"usuario\":\"anam\",\"contrasena\":\"solo letras\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores[0]").value("La contraseña debe tener al menos una letra y un número"));
    }

    @Test
    void registroConUsuarioConCaracteresInvalidosDevuelve400() throws Exception {
        enviar("/api/auth/registro", "{\"usuario\":\"ana' OR 1=1\",\"contrasena\":\"Clave2026\"}")
                .andExpect(status().isBadRequest());
    }

    @Test
    void registroConJsonMalFormadoDevuelve400() throws Exception {
        enviar("/api/auth/registro", "esto no es json")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El cuerpo de la petición debe ser un JSON válido"));
    }

    // ---------- Inicio de sesión ----------

    @Test
    void loginCorrectoDevuelveAutenticacionSatisfactoria() throws Exception {
        String hash = new BCryptPasswordEncoder().encode("Clave2026");
        when(usuarioDAO.buscarPorUsuario("anam")).thenReturn(new Usuario(1, "anam", hash));

        enviar("/api/auth/login", "{\"usuario\":\"ANAM\",\"contrasena\":\"Clave2026\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Autenticación satisfactoria"));
    }

    @Test
    void loginConContrasenaIncorrectaDevuelve401() throws Exception {
        String hash = new BCryptPasswordEncoder().encode("Clave2026");
        when(usuarioDAO.buscarPorUsuario("anam")).thenReturn(new Usuario(1, "anam", hash));

        enviar("/api/auth/login", "{\"usuario\":\"anam\",\"contrasena\":\"OtraClave1\"}")
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.mensaje").value("Error en la autenticación: usuario o contraseña incorrectos"));
    }

    @Test
    void loginConUsuarioInexistenteDevuelve401() throws Exception {
        when(usuarioDAO.buscarPorUsuario("nadie")).thenReturn(null);

        enviar("/api/auth/login", "{\"usuario\":\"nadie\",\"contrasena\":\"Clave2026\"}")
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginSinContrasenaDevuelve400() throws Exception {
        enviar("/api/auth/login", "{\"usuario\":\"anam\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores[0]").value("La contraseña es obligatoria"));
    }

    @Test
    void errorDeBaseDeDatosDevuelve500() throws Exception {
        when(usuarioDAO.buscarPorUsuario("anam")).thenThrow(new SQLException("sin conexión"));

        enviar("/api/auth/login", "{\"usuario\":\"anam\",\"contrasena\":\"Clave2026\"}")
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensaje").value("Error interno, intente más tarde"));
    }
}
