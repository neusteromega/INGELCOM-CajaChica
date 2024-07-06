package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ingelcom.cajachica.Herramientas.Utilidades;

public class EmpMenuPrincipal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_menu_principal);
    }

    public void registrarGasto(View view) {
        Utilidades.iniciarActivity(this, RegistrarEditarGasto.class, false);
    }

    public void listadoGastos(View view) {
        Utilidades.iniciarActivity(this, ListadoGastos.class, false);
    }

    public void miPerfil(View view) {
        Utilidades.iniciarActivity(this, Perfil.class, false);
    }
}