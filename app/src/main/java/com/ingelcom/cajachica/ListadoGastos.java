package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ingelcom.cajachica.Adaptadores.VPGastosAdapter;
import com.ingelcom.cajachica.Herramientas.Utilidades;

public class ListadoGastos extends AppCompatActivity {

    ViewPager2 vpGastos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_gastos);

        vpGastos = findViewById(R.id.vpListadoGastos); //Relacionamos la variable "vpGastos" con el ViewPager

        VPGastosAdapter vpAdapter = new VPGastosAdapter(this); //Creamos un objeto de "VPGastosAdapter" y le mandamos el contexto "this" de este Activity
        vpGastos.setAdapter(vpAdapter); //Asignamos el adaptador al vpGastos
    }

    //Eliminar cuando se esté programando
    public void DetalleGasto(View view) {
        Utilidades.iniciarActivity(this, DetalleGasto.class, false);
    }
}