package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ingelcom.cajachica.Herramientas.Utilidades;

public class AdmDatosCuadrilla extends AppCompatActivity {

    private TextView lblCuadrilla, lblDinero, lblIngresos, lblGastos;
    private String nombreCuadrilla, dineroDisponible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adm_datos_cuadrilla);

        inicializarElementos();
        establecerElementos();
    }

    private void inicializarElementos() {
        //Obtenemos el nombre de la cuadrilla y el dinero disponible de la misma que se envía desde el activity anterior, lo hacemos llamando a la función "obtenerStringExtra" de la clase "Utilidades", y les mandamos "this" para referenciar esta actividad y los nombres de las claves del putExtra
        nombreCuadrilla = Utilidades.obtenerStringExtra(this, "Cuadrilla");
        dineroDisponible = Utilidades.obtenerStringExtra(this, "DineroDisponible");

        lblCuadrilla = findViewById(R.id.lblNombreCuadrillaDC);
        lblDinero = findViewById(R.id.lblCantDineroDC);
        lblIngresos = findViewById(R.id.lblCantIngresosDC);
        lblGastos = findViewById(R.id.lblCantGastosDC);
    }

    private void establecerElementos() {
        //Asignamos el nombre de la cuadrilla y su dinero disponible tras dar clic en la cuadrilla corresponiente en el GridView de "AdmCuadrillas" (La pantalla anterior)
        lblCuadrilla.setText(nombreCuadrilla);
        lblDinero.setText("L. " + dineroDisponible);
    }

    public void verIngresos(View view) {
        Utilidades.iniciarActivity(this, ListadoIngresos.class, false);
    }

    public void verGastos(View view) {
        //Utilidades.iniciarActivity(this, ListadoGastos.class, false);
        Utilidades.iniciarActivityConString(AdmDatosCuadrilla.this, ListadoGastos.class, "Cuadrilla", nombreCuadrilla, false);
    }
}