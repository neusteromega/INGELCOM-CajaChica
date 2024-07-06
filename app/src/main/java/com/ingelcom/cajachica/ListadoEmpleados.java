package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ingelcom.cajachica.Herramientas.Utilidades;

public class ListadoEmpleados extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_empleados);
    }

    public void perfil(View view) {
        Utilidades.iniciarActivity(this, Perfil.class, false);
    }
}