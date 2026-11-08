package com.example.medeeledpantallainicial;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Casos de prueba funcionales CP01–CP08 (Espresso). Requieren emulador o dispositivo.
 */
@RunWith(AndroidJUnit4.class)
public class CasosPruebaFuncionalesTest {

    private static final String CORREO_VALIDO = "usuario@empresa.com";
    private static final String CONTRASENA_VALIDA = "abc123";

    @Rule
    public ActivityScenarioRule<MainActivity> regla =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void cp01_abrirAplicacion_muestraPantallaBienvenida() {
        onView(withText("Energía Solar Sostenible")).check(matches(isDisplayed()));
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.btnRegistro)).check(matches(isDisplayed()));
    }

    @Test
    public void cp02_presionarLogin_abrePantallaInicioSesion() {
        abrirLogin();
        onView(withText("Bienvenido de nuevo")).check(matches(isDisplayed()));
        onView(withId(R.id.btnIniciarSesion)).check(matches(isDisplayed()));
    }

    @Test
    public void cp03_camposVacios_muestraCorreoObligatorio() {
        abrirLogin();
        onView(withId(R.id.btnIniciarSesion)).perform(click());
        onView(withId(R.id.txtCorreo)).check(matches(hasErrorText("El correo es obligatorio")));
    }

    @Test
    public void cp04_correoInvalido_muestraIngreseCorreoValido() {
        abrirLogin();
        escribir(R.id.txtCorreo, "usuario.com");
        onView(withId(R.id.btnIniciarSesion)).perform(click());
        onView(withId(R.id.txtCorreo)).check(matches(hasErrorText("Ingrese un correo válido")));
    }

    @Test
    public void cp05_contrasenaVacia_muestraContrasenaObligatoria() {
        abrirLogin();
        escribir(R.id.txtCorreo, CORREO_VALIDO);
        onView(withId(R.id.btnIniciarSesion)).perform(click());
        onView(withId(R.id.txtPassword)).check(matches(hasErrorText("La contraseña es obligatoria")));
    }

    @Test
    public void cp06_contrasenaCorta_muestraMinimoSeisCaracteres() {
        abrirLogin();
        escribir(R.id.txtCorreo, CORREO_VALIDO);
        escribir(R.id.txtPassword, "123");
        onView(withId(R.id.btnIniciarSesion)).perform(click());
        onView(withId(R.id.txtPassword)).check(matches(hasErrorText("Mínimo 6 caracteres")));
    }

    @Test
    public void cp07_credencialesValidas_abrePanelGestion() {
        iniciarSesionValida();
        onView(withId(R.id.tituloDashboard)).check(matches(withText("Panel de gestión")));
        onView(withId(R.id.tituloDashboard)).check(matches(isDisplayed()));
    }

    @Test
    public void cp08_presionarModulo_muestraAvisoEnConstruccion() {
        iniciarSesionValida();
        onView(withId(R.id.btnClientes)).perform(scrollTo(), click());
        onView(withText("Módulo Clientes en construcción")).check(matches(isDisplayed()));
    }

    // --- Utilidades ---

    private void abrirLogin() {
        onView(withId(R.id.btnLogin)).perform(click());
    }

    private void escribir(int idCampo, String texto) {
        onView(withId(idCampo)).perform(typeText(texto));
        closeSoftKeyboard();
    }

    private void iniciarSesionValida() {
        abrirLogin();
        escribir(R.id.txtCorreo, CORREO_VALIDO);
        escribir(R.id.txtPassword, CONTRASENA_VALIDA);
        onView(withId(R.id.btnIniciarSesion)).perform(click());
    }
}
