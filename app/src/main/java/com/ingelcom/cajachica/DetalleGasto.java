package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ingelcom.cajachica.Herramientas.Utilidades;

public class DetalleGasto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_gasto);
    }

    public void EditarGasto(View view) {
        Utilidades.iniciarActivity(this, RegistrarEditarGasto.class, false);
    }
}