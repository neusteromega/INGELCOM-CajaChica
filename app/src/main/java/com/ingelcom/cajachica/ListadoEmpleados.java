package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

public class ListadoEmpleados extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private FirestoreOperaciones oper = new FirestoreOperaciones();
    private Usuario usuario = new Usuario(ListadoEmpleados.this);
    private EmpleadosAdapter adapter;
    private RecyclerView rvEmpleados;
    private SwipeRefreshLayout swlRecargar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_empleados);

        rvEmpleados = findViewById(R.id.rvListadoEmpleados);
        swlRecargar = findViewById(R.id.swipeRefreshLayoutLE);

        swlRecargar.setOnRefreshListener(this); //Llamada al método "onRefresh"
        swlRecargar.setColorSchemeResources(R.color.clr_fuente_primario); //Color del SwipeRefreshLayout

        obtenerDatos();
    }

    @Override
    public void onBackPressed() {
        Utilidades.iniciarActivity(ListadoEmpleados.this, AdmPantallas.class, true);
    }

    private void obtenerDatos() {
        LinearLayoutManager managerEmp = new LinearLayoutManager(this); //Creamos un manager para que el RecyclerView se vea en forma de tarjetas
        rvEmpleados.setLayoutManager(managerEmp); //Asignamos el manager al RecyclerView

        try {
            //Llamamos el método "obtenerEmpleados" de la clase "Usuario" donde invocamos la interfaz "FirestoreAllSpecialDocumentsCallback" y le indicamos que debe ser de tipo "EmpleadosItems"
            usuario.obtenerEmpleados(new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<EmpleadosItems>() {
                @Override
                public void onCallback(List<EmpleadosItems> items) { //Esto nos devuelve la lista de tipo "EmpleadosItems" ya con los empleados extraídos de Firestore
                    if (items != null) { //Si "items" no es null, que entre al if
                        items = Utilidades.ordenarListaPorAlfabetico(items, "nombre", "Ascendente"); //Llamamos el método utilitario "ordenarListaPorAlfabetico". Le mandamos la lista "items", el nombre del campo String "nombre", y el tipo de orden "Ascendente". Este método retorna la lista ya ordenada y la guardamos en "items"
                        inicializarRecyclerView(items); //Llamamos el método "inicializarGridView" y le mandamos la lista "items"
                    }
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

    @Override
    public void onRefresh() { //Método que detecta cuando se recarga la pantalla con SwipeRefreshLayout
        //Creamos una nueva instancia de "Handler", que está vinculada al Looper principal (el hilo principal de la aplicación). Esto asegura que cualquier operación realizada dentro de este Handler se ejecute en el hilo principal
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { //El "Handler" utiliza el método "postDelayed" para ejecutar el "Runnable" que contiene las acciones a realizar después de un retraso especificado (en este caso, 1500 milisegundos, es decir, 1.5 segundos)
            @Override
            public void run() {
                obtenerDatos();
                swlRecargar.setRefreshing(false); //Llamamos a este método para detener la animación de refresco
            }
        }, 1500);
    }

    public void retroceder(View view) {
        onBackPressed();
    }
}