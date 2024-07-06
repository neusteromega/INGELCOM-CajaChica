package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ingelcom.cajachica.Herramientas.Utilidades;

public class RegistrarEditarIngreso extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_editar_ingreso);
    }

    public void confirmar(View view) {
        Utilidades.iniciarActivity(this, GastoIngresoRegistrado.class, false);
    }
}