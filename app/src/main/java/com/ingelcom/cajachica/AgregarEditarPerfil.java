package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ingelcom.cajachica.DAO.FirestoreOperaciones;
import com.ingelcom.cajachica.Herramientas.Utilidades;

import java.util.List;

public class AgregarEditarPerfil extends AppCompatActivity {

    private LinearLayout llNombreApellido, llIdentidad, llTelefono, llCorreo, llContra, llConfContra, llRol, llCuadrilla, llEstado;
    private TextView lblTitulo, btnConfirmar;
    private Spinner spRoles, spCuadrillas, spEstado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_editar_perfil);

        llNombreApellido = findViewById(R.id.LLNombreApellidoAEP);
        llIdentidad = findViewById(R.id.LLIdentidadAEP);
        llTelefono = findViewById(R.id.LLTelefonoAEP);
        llCorreo = findViewById(R.id.LLCorreoAEP);
        llContra = findViewById(R.id.LLContrasenaAEP);
        llConfContra = findViewById(R.id.LLConfContrasenaAEP);
        llRol = findViewById(R.id.LLRolAEP);
        llCuadrilla = findViewById(R.id.LLCuadrillaAEP);
        llEstado = findViewById(R.id.LLEstadoAEP);

        lblTitulo = findViewById(R.id.lblTituloAEP);
        btnConfirmar = findViewById(R.id.btnConfirmarAEP);

        spRoles = findViewById(R.id.spRolAEP);
        spCuadrillas = findViewById(R.id.spCuadrillaAEP);
        spEstado = findViewById(R.id.spEstadoAEP);

        establecerElementos();
        inicializarSpinners();
    }

    private void establecerElementos() {
        //Obtenemos el nombre del activity que se envía desde el activity anterior, lo hacemos llamando a la función "obtenerStringExtra" de la clase "Utilidades", y le mandamos "this" para referenciar esta actividad y "Activity" como clave del putExtra
        String nombreActivity = Utilidades.obtenerStringExtra(this, "Activity");

        //Que entre al if si "nombreActivity" no es nulo
        if (nombreActivity != null) {
            //El "nombreActivity" nos sirve para saber la pantalla con la que trabajaremos
            switch (nombreActivity) {
                case "AgregarUsuario": //Establecemos los elementos gráficos si la pantalla es "AgregarUsuario"
                    //Asignamos el título
                    lblTitulo.setText("Agregar Usuario");

                    //Ocultamos con "GONE" los elementos que no son necesarios
                    llCorreo.setVisibility(View.GONE);
                    llContra.setVisibility(View.GONE);
                    llConfContra.setVisibility(View.GONE);
                    llEstado.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private void inicializarSpinners() {
        FirestoreOperaciones oper = new FirestoreOperaciones();

        //Para inicializar los spinners, llamamos al método "obtenerRegistros" de la clase "FirestoreOperaciones" a la cual le mandamos el nombre de la colección y el nombre del campo de Firestore de los cuales queremos obtener los registros. También invocamos los métodos "onCallback" y "onFailure" de la interfaz FirestoreCallback
        //ROLES
        oper.obtenerRegistros("roles", "Nombre", new FirestoreOperaciones.FirestoreCallback() {
            @Override
            public void onCallback(List<String> lista) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AgregarEditarPerfil.this, R.layout.spinner_items, lista);
                spRoles.setAdapter(adapter);
            }

            @Override
            public void onFailure(Exception e) {
                Log.w("Activity", "Error al obtener los roles.", e);
            }
        });

        //CUADRILLAS
        oper.obtenerRegistros("cuadrillas", "Nombre", new FirestoreOperaciones.FirestoreCallback() {
            @Override
            public void onCallback(List<String> lista) {
                //Creamos el adapter para el spinner y le establecemos la vista de los items que es "R.layout.spinner_items" y la lista de elementos que es la variable "lista"
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AgregarEditarPerfil.this, R.layout.spinner_items, lista);
                spCuadrillas.setAdapter(adapter); //Asignamos el adapter a "spCuadrillas"
                //Utilidades.spinnerConHint(AgregarEditarPerfil.this, spCuadrillas, lista, "Cuadrillas");
            }

            @Override
            public void onFailure(Exception e) {
                Log.w("Activity", "Error al obtener las cuadrillas.", e);
            }
        });
    }
}