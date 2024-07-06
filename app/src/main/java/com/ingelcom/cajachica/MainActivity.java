package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.ingelcom.cajachica.Herramientas.Utilidades;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Llamamos el método "iniciarActivity" para que redireccione al Activity de "IniciarSesion" y mandamos "true" para que cierre la actividad actual
                Utilidades.iniciarActivity(MainActivity.this, IniciarSesion.class, true);
            }
        }, 3000);
    }
}