package com.example.medeeledpantallainicial;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText txtCorreo;
    private EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        txtCorreo = findViewById(R.id.txtCorreo);
        txtPassword = findViewById(R.id.txtPassword);
        Button btnIniciarSesion = findViewById(R.id.btnIniciarSesion);

        btnIniciarSesion.setOnClickListener(v -> {
            if (formularioValido()) {
                Intent intent = new Intent(
                        LoginActivity.this,
                        DashboardActivity.class
                );

                startActivity(intent);
            }
        });
    }

    private boolean formularioValido() {
        String correo = txtCorreo.getText().toString();
        String password = txtPassword.getText().toString();

        if (Validaciones.estaVacio(correo)) {
            mostrarError(txtCorreo, "El correo es obligatorio");
            return false;
        }
        if (!Validaciones.esCorreoValido(correo)) {
            mostrarError(txtCorreo, "Ingrese un correo válido");
            return false;
        }
        if (Validaciones.estaVacio(password)) {
            mostrarError(txtPassword, "La contraseña es obligatoria");
            return false;
        }
        if (!Validaciones.esContrasenaValida(password)) {
            mostrarError(txtPassword, "Mínimo 6 caracteres");
            return false;
        }
        return true;
    }

    private void mostrarError(EditText campo, String mensaje) {
        campo.setError(mensaje);
        campo.requestFocus();
    }
}
