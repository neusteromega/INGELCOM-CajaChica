package com.ingelcom.cajachica;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ingelcom.cajachica.DAO.FirestoreOperaciones;
import com.ingelcom.cajachica.Herramientas.Utilidades;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgregarEditarPerfil extends AppCompatActivity {

    private LinearLayout llNombreApellido, llIdentidad, llTelefono, llCorreo, llContra, llConfContra, llRol, llCuadrilla, llEstado;
    private EditText txtNombreApellido, txtIdentidad, txtTelefono, txtCorreo, txtContra, txtConfContra;
    private TextView lblTitulo, btnConfirmar;
    private Spinner spRoles, spCuadrillas, spEstado;
    private String nombreActivity;

    private FirestoreOperaciones oper = new FirestoreOperaciones();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_editar_perfil);

        //Obtenemos el nombre del activity que se envía desde el activity anterior, lo hacemos llamando a la función "obtenerStringExtra" de la clase "Utilidades", y le mandamos "this" para referenciar esta actividad y "Activity" como clave del putExtra
        nombreActivity = Utilidades.obtenerStringExtra(this, "Activity");

        inicializarElementos();
        establecerElementos();
        inicializarSpinners();
    }

    private void inicializarElementos() {
        llNombreApellido = findViewById(R.id.LLNombreApellidoAEP);
        llIdentidad = findViewById(R.id.LLIdentidadAEP);
        llTelefono = findViewById(R.id.LLTelefonoAEP);
        llCorreo = findViewById(R.id.LLCorreoAEP);
        llContra = findViewById(R.id.LLContrasenaAEP);
        llConfContra = findViewById(R.id.LLConfContrasenaAEP);
        llRol = findViewById(R.id.LLRolAEP);
        llCuadrilla = findViewById(R.id.LLCuadrillaAEP);
        llEstado = findViewById(R.id.LLEstadoAEP);

        txtNombreApellido = findViewById(R.id.txtNombreApellidoAEP);
        txtIdentidad = findViewById(R.id.txtIdentidadAEP);
        txtTelefono = findViewById(R.id.txtTelefonoAEP);
        txtCorreo = findViewById(R.id.txtCorreoAEP);
        txtContra = findViewById(R.id.txtContrasenaAEP);
        txtConfContra = findViewById(R.id.txtConfContrasenaAEP);

        lblTitulo = findViewById(R.id.lblTituloAEP);
        btnConfirmar = findViewById(R.id.btnConfirmarAEP);

        spRoles = findViewById(R.id.spRolAEP);
        spCuadrillas = findViewById(R.id.spCuadrillaAEP);
        spEstado = findViewById(R.id.spEstadoAEP);
    }

    private void establecerElementos() {
        //Que entre al if si "nombreActivity" no es nulo
        if (nombreActivity != null) {
            //El "nombreActivity" nos sirve para saber la pantalla con la que trabajaremos
            switch (nombreActivity) {
                //Establecemos los elementos gráficos si la pantalla es "AgregarUsuario"
                case "AgregarUsuario":
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
                List<String> listaCuadrillas = new ArrayList<>(); //Lista que tendrá todas las cuadrillas, más la opción de "No Pertenece"
                listaCuadrillas.add("No Pertenece"); //Agregamos la opción de "No Pertenece"
                listaCuadrillas.addAll(lista); //Anexamos la lista de cuadrillas de Firestore que está en la variable "lista"

                //Creamos el adapter para el spinner y le establecemos la vista de los items que es "R.layout.spinner_items" y la lista de elementos que es la variable "lista"
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AgregarEditarPerfil.this, R.layout.spinner_items, listaCuadrillas);
                spCuadrillas.setAdapter(adapter); //Asignamos el adapter a "spCuadrillas"
                //Utilidades.spinnerConHint(AgregarEditarPerfil.this, spCuadrillas, lista, "Cuadrillas");
            }

            @Override
            public void onFailure(Exception e) {
                Log.w("Activity", "Error al obtener las cuadrillas.", e);
            }
        });

        //ESTADO
        List<String> estados = new ArrayList<>(Arrays.asList("Activo", "Inactivo"));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AgregarEditarPerfil.this, R.layout.spinner_items, estados);
        spEstado.setAdapter(adapter);
    }

    public void confirmar(View view) {
        //Que entre al if si "nombreActivity" no es nulo
        if (nombreActivity != null) {
            //El "nombreActivity" nos sirve para saber la pantalla con la que trabajaremos
            switch (nombreActivity) {
                //Si estamos en la pantalla de "Agregar Usuario", al dar clic en el botón "Confirmar" que realice las operaciones de este case
                case "AgregarUsuario":
                    //Creamos un alertDialog que pregunte si se desea agregar el usuario
                    new AlertDialog.Builder(this).setTitle("AGREGAR USUARIO").setMessage("¿Está seguro que desea agregar el usuario?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { //Si se selecciona la opción positiva, entrará aquí y al método "insertarUsuario()"
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                insertarUsuario(); //Llamamos el método "insertarUsuario" donde se hará el proceso de inserción a Firestore
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() { //Si se seleccionó la opción negativa, entrará aquí y solamente mostrará un mensaje en Logcat
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.d("Mensaje", "Se canceló la acción"); //Se muestra un mensaje en el Logcat indicando que se canceló la acción
                                }
                            }).show();
                    break;
            }
        }
    }

    private void insertarUsuario() {
        //Enlazamos los EditText con las siguientes variables String
        String nombreApellido = txtNombreApellido.getText().toString();
        String identidad = txtIdentidad.getText().toString();
        String telefono = txtTelefono.getText().toString();

        //Obtenemos la selección hecha en los Spinners de Roles y Cuadrillas
        String rol = spRoles.getSelectedItem().toString();
        String cuadrilla = spCuadrillas.getSelectedItem().toString();

        Map<String,Object> datos = new HashMap<>(); //HashMap que nos ayudará a almacenar los nombres de los campos de la colección y los datos a ser insertados en cada campo

        //Insertamos los datos en el HashMap usando ".put", indicando entre comillas el nombre del campo, y después de la coma, el valor a insertar
        datos.put("NombreApellido", nombreApellido);
        datos.put("Identidad", identidad);
        datos.put("Telefono", telefono);
        datos.put("Rol", rol);
        datos.put("Estado", true);

        //Si la selección hecha en el spinner "Cuadrillas" fue "No Pertenece", que inserte un valor vacío ("") en el campo "Cuadrilla" de Firestore
        if (cuadrilla.contentEquals("No Pertenece"))
            datos.put("Cuadrilla", "");
        else //Pero si la selección hecha en el spinner "Cuadrillas" fue otra diferente a "No Pertenece", que inserte esa selección en el campo "Cuadrilla" de Firestore
            datos.put("Cuadrilla", cuadrilla);

        //Llamamos el método "insertarRegistros" de la clase "FirestoreOperaciones" y le mandamos el nombre de la colección, el HashMap con los datos a insertar. También invocamos los métodos "onSuccess" y "onFailure" de la interfaz FirestoreInsertCallback
        oper.insertarRegistros("usuarios", datos, new FirestoreOperaciones.FirestoreInsertCallback() {
            @Override
            public void onSuccess(String idDocumento) {
                Toast.makeText(AgregarEditarPerfil.this, "USUARIO AGREGADO EXITOSAMENTE", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(AgregarEditarPerfil.this, "ERROR AL AGREGAR EL USUARIO", Toast.LENGTH_SHORT).show();
            }
        });
    }
}