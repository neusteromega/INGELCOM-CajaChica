package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ingelcom.cajachica.Herramientas.Utilidades;

public class DetalleGastoIngreso extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_gasto_ingreso);
    }

    public void EditarGasto(View view) {
        Utilidades.iniciarActivity(this, RegistrarEditarGasto.class, false);
    }
}