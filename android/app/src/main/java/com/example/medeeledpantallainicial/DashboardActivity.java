package com.example.medeeledpantallainicial;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        configurarModulo(R.id.btnClientes, "Clientes");
        configurarModulo(R.id.btnProyectos, "Proyectos solares");
        configurarModulo(R.id.btnVentas, "Ventas");
        configurarModulo(R.id.btnReportes, "Reportes");
        configurarModulo(R.id.btnAlertas, "Alertas");
    }

    private void configurarModulo(int idBoton, String nombreModulo) {
        findViewById(idBoton).setOnClickListener(v -> mostrarAviso(v, nombreModulo));
    }

    private void mostrarAviso(View origen, String nombreModulo) {
        Snackbar.make(origen, "Módulo " + nombreModulo + " en construcción", Snackbar.LENGTH_SHORT)
                .show();
    }
}
