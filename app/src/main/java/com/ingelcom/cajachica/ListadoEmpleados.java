package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ingelcom.cajachica.Adaptadores.EmpleadosAdapter;
import com.ingelcom.cajachica.DAO.FirestoreOperaciones;
import com.ingelcom.cajachica.DAO.Usuario;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.Modelos.EmpleadosItems;

import java.util.List;

public class ListadoEmpleados extends AppCompatActivity {

    private FirestoreOperaciones oper = new FirestoreOperaciones();
    private Usuario usuario = new Usuario();
    private EmpleadosAdapter adapter;
    private RecyclerView rvEmpleados;
    private List<EmpleadosItems> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_empleados);

        rvEmpleados = findViewById(R.id.rvListadoEmpleados);

        inicializarValores();
    }

    private void inicializarValores() {
        LinearLayoutManager managerEmp = new LinearLayoutManager(this);
        rvEmpleados.setLayoutManager(managerEmp);

        items = usuario.obtenerEmpleados();
        adapter = new EmpleadosAdapter(items);
        rvEmpleados.setAdapter(adapter);
    }

    public void perfil(View view) {
        Utilidades.iniciarActivity(this, Perfil.class, false);
    }
}