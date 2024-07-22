package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ingelcom.cajachica.Adaptadores.EmpleadosAdapter;
import com.ingelcom.cajachica.DAO.FirestoreOperaciones;
import com.ingelcom.cajachica.DAO.Usuario;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.Modelos.EmpleadosItems;

import java.util.HashMap;
import java.util.List;

public class ListadoEmpleados extends AppCompatActivity {

    private FirestoreOperaciones oper = new FirestoreOperaciones();
    private Usuario usuario = new Usuario(ListadoEmpleados.this);
    private EmpleadosAdapter adapter;
    private RecyclerView rvEmpleados;
    private List<EmpleadosItems> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_empleados);

        rvEmpleados = findViewById(R.id.rvListadoEmpleados);

        obtenerValores();
    }

    private void obtenerValores() {
        LinearLayoutManager managerEmp = new LinearLayoutManager(this); //Creamos un manager para que el RecyclerView se vea en forma de tarjetas
        rvEmpleados.setLayoutManager(managerEmp); //Asignamos el manager al RecyclerView

        try {
            //Llamamos el método "obtenerEmpleados" de la clase "Usuario" donde invocamos la interfaz "FirestoreAllSpecialDocumentsCallback" y le indicamos que debe ser de tipo "EmpleadosItems"
            usuario.obtenerEmpleados(new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<EmpleadosItems>() {
                @Override
                public void onCallback(List<EmpleadosItems> items) { //Esto nos devuelve la lista de tipo "EmpleadosItems" ya con los empleados extraídos de Firestore
                    if (items != null)
                        inicializarRecyclerView(items);
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(ListadoEmpleados.this, "ERROR AL CARGAR LOS EMPLEADOS", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerEmpleados", e);
        }
    }

    private void inicializarRecyclerView(List<EmpleadosItems> items) {
        adapter = new EmpleadosAdapter(items); //Instanciamos el objeto de tipo EmpleadosAdapter en el cual enviamos la lista "items" (este "adapter" fue inicializado arriba de forma global)
        rvEmpleados.setAdapter(adapter); //Asignamos el adapter al recyclerView de Empleados

        adapter.setOnClickListener(new View.OnClickListener() { //Usando el objeto de "adapter" llamamos al método "setOnClickListener" de la clase EmpleadosAdapter
            @Override
            public void onClick(View view) { //Al dar clic en una tarjeta del RecyclerView, se realizará lo siguiente
                HashMap<String,Object> datosPerfil = new HashMap<>(); //Creamos un HashMap para guardar los datos que se enviarán al siguiente Activity

                //Agregamos las claves y datos al HashMap
                datosPerfil.put("ActivityPerfil", "PerfilEmpleadoAdmin");
                datosPerfil.put("Nombre", items.get(rvEmpleados.getChildAdapterPosition(view)).getNombre());
                datosPerfil.put("Correo", items.get(rvEmpleados.getChildAdapterPosition(view)).getCorreo());
                datosPerfil.put("Cuadrilla", items.get(rvEmpleados.getChildAdapterPosition(view)).getCuadrilla());
                datosPerfil.put("Identidad", items.get(rvEmpleados.getChildAdapterPosition(view)).getIdentidad());
                datosPerfil.put("Telefono", items.get(rvEmpleados.getChildAdapterPosition(view)).getTelefono());
                datosPerfil.put("Rol", items.get(rvEmpleados.getChildAdapterPosition(view)).getRol());
                datosPerfil.put("Estado", items.get(rvEmpleados.getChildAdapterPosition(view)).getEstado());

                //Llamamos el método "iniciarActivityConDatos" de la clase Utilidades y le mandamos el contexto, el activity siguiente y el HashMap con los datos a enviar
                Utilidades.iniciarActivityConDatos(ListadoEmpleados.this, Perfil.class, datosPerfil);
            }
        });
    }
}