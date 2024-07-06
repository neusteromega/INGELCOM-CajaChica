package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ingelcom.cajachica.Herramientas.Utilidades;

public class IniciarSesion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);
    }

    public void crearContrasena(View view) {
        Utilidades.iniciarActivity(this, CompletarUsuario.class, false);
    }

    public void acceder(View view) {
        Utilidades.iniciarActivity(this, EmpMenuPrincipal.class, false);
    }

    public void accederAdmin(View view) {
        Utilidades.iniciarActivity(this, AdmPantallas.class, false);
    }
}