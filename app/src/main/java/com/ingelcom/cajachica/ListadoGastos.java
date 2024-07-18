package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ingelcom.cajachica.Adaptadores.VPGastosAdapter;
import com.ingelcom.cajachica.DAO.Gasto;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.Modelos.GastosItems;

import java.util.List;

public class ListadoGastos extends AppCompatActivity {

    ViewPager2 vpGastos;
    private Gasto gast = new Gasto(ListadoGastos.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_gastos);

        vpGastos = findViewById(R.id.vpListadoGastos); //Relacionamos la variable "vpGastos" con el ViewPager

        VPGastosAdapter vpAdapter = new VPGastosAdapter(this); //Creamos un objeto de "VPGastosAdapter" y le mandamos el contexto "this" de este Activity
        vpGastos.setAdapter(vpAdapter); //Asignamos el adaptador al vpGastos

        gast.obtenerGastos(new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<GastosItems>() {
            @Override
            public void onCallback(List<GastosItems> items) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    //Eliminar cuando se esté programando
    public void DetalleGasto(View view) {
        Utilidades.iniciarActivity(this, DetalleGasto.class, false);
    }
}