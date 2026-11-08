package com.example.medeeledpantallainicial;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Pruebas unitarias (JUnit 4) de la clase {@link Validaciones}.
 */
public class ValidacionesTest {

    // --- esCorreoValido ---

    @Test
    public void correoValido_retornaTrue() {
        assertTrue(Validaciones.esCorreoValido("usuario@empresa.com"));
    }

    @Test
    public void correoSinArroba_retornaFalse() {
        assertFalse(Validaciones.esCorreoValido("usuarioempresa.com"));
    }

    @Test
    public void correoSinDominio_retornaFalse() {
        assertFalse(Validaciones.esCorreoValido("usuario@"));
    }

    @Test
    public void correoVacio_retornaFalse() {
        assertFalse(Validaciones.esCorreoValido(""));
    }

    // --- estaVacio ---

    @Test
    public void campoConEspacios_seConsideraVacio() {
        assertTrue(Validaciones.estaVacio("   "));
    }

    @Test
    public void campoNulo_seConsideraVacio() {
        assertTrue(Validaciones.estaVacio(null));
    }

    // --- esContrasenaValida ---

    @Test
    public void contrasenaCorta_retornaFalse() {
        assertFalse(Validaciones.esContrasenaValida("123"));
    }

    @Test
    public void contrasenaDeSeisCaracteres_retornaTrue() {
        assertTrue(Validaciones.esContrasenaValida("abc123"));
    }
}
