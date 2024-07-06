package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ingelcom.cajachica.Herramientas.Utilidades;

public class AdmDatosCuadrilla extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adm_datos_cuadrilla);
    }

    public void verIngresos(View view) {
        Utilidades.iniciarActivity(this, ListadoIngresos.class, false);
    }

    public void verGastos(View view) {
        Utilidades.iniciarActivity(this, ListadoGastos.class, false);
    }
}