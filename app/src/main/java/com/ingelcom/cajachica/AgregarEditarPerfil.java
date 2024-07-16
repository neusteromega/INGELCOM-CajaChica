package com.ingelcom.cajachica;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ingelcom.cajachica.DAO.FirestoreOperaciones;
import com.ingelcom.cajachica.DAO.Usuario;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;

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
    private ImageView btnRegresar;
    private String nombreActivity;

    private FirestoreOperaciones oper = new FirestoreOperaciones();
    private Usuario usu = new Usuario(AgregarEditarPerfil.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_editar_perfil);

        //Obtenemos el nombre del activity que se envía desde el activity anterior, lo hacemos llamando a la función "obtenerStringExtra" de la clase "Utilidades", y le mandamos "this" para referenciar esta actividad y "Activity" como clave del putExtra
        nombreActivity = Utilidades.obtenerStringExtra(this, "ActivityAEP");

        inicializarElementos();
        establecerElementos();
        inicializarSpinners();
    }

    private void inicializarElementos() {
        llNombreApellido = findViewById(R.id.LLNombreApellidoAEP);
        llIdentidad = findViewById(R.id.LLIdentidadAEP);
        llTelefono = findViewById(R.id.LLTelefonoAEP);
        llCorreo = findViewById(R.id.LLCorreoAEP);
        /*llContra = findViewById(R.id.LLContrasenaAEP);
        llConfContra = findViewById(R.id.LLConfContrasenaAEP);*/
        llRol = findViewById(R.id.LLRolAEP);
        llCuadrilla = findViewById(R.id.LLCuadrillaAEP);
        llEstado = findViewById(R.id.LLEstadoAEP);

        txtNombreApellido = findViewById(R.id.txtNombreApellidoAEP);
        txtIdentidad = findViewById(R.id.txtIdentidadAEP);
        txtTelefono = findViewById(R.id.txtTelefonoAEP);
        txtCorreo = findViewById(R.id.txtCorreoAEP);
        /*txtContra = findViewById(R.id.txtContrasenaAEP);
        txtConfContra = findViewById(R.id.txtConfContrasenaAEP);*/

        lblTitulo = findViewById(R.id.lblTituloAEP);
        btnRegresar = findViewById(R.id.imgRegresarAEP);
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
                    /*llContra.setVisibility(View.GONE);
                    llConfContra.setVisibility(View.GONE);*/
                    llEstado.setVisibility(View.GONE);
                    break;

                //Establecemos los elementos gráficos si la pantalla es "EditarAdmin"
                case "EditarAdmin":

                //Establecemos los elementos gráficos si la pantalla es "EditarEmpleado"
                case "EditarEmpleado":
                    llCuadrilla.setVisibility(View.GONE);
                    llRol.setVisibility(View.GONE);
                    llEstado.setVisibility(View.GONE);
                    break;

                //Establecemos los elementos gráficos si la pantalla es "EditarEmpleadoAdmin"
                case "EditarEmpleadoAdmin":
                    //Aquí no ponemos nada ya que esta pantalla tiene todos los elementos gráficos
                    break;
            }
        }
    }

    private void inicializarSpinners() {
        //Para inicializar los spinners, llamamos al método "obtenerRegistros" de la clase "FirestoreOperaciones" a la cual le mandamos el nombre de la colección y el nombre del campo de Firestore de los cuales queremos obtener los registros. También invocamos los métodos "onCallback" y "onFailure" de la interfaz FirestoreCallback
        //ROLES
        oper.obtenerRegistrosCampo("roles", "Nombre", new FirestoreCallbacks.FirestoreCallback() {
            @Override
            public void onCallback(List<String> lista) {
                //Creamos un adapter de tipo ArrayAdapter el cual le pasamos el contexto de este Activity, la vista layout de las opciones del Spinner (R.layout.spinner_items), y la lista de valores que se recibe en "lista" al llamar a la interfaz FirestoreCallback
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AgregarEditarPerfil.this, R.layout.spinner_items, lista);
                spRoles.setAdapter(adapter); //Asignamos el adapter al Spinner "spRoles"
            }

            @Override
            public void onFailure(Exception e) {
                Log.w("Activity", "Error al obtener los roles.", e);
            }
        });

        //CUADRILLAS
        oper.obtenerRegistrosCampo("cuadrillas", "Nombre", new FirestoreCallbacks.FirestoreCallback() {
            @Override
            public void onCallback(List<String> lista) {
                List<String> listaCuadrillas = new ArrayList<>(); //Lista que tendrá todas las cuadrillas, más la opción de "No Pertenece"
                listaCuadrillas.add("No Pertenece"); //Agregamos la opción de "No Pertenece"
                listaCuadrillas.addAll(lista); //Anexamos la lista de cuadrillas de Firestore que está en la variable "lista"

                //Creamos un adapter de tipo ArrayAdapter el cual le pasamos el contexto de este Activity, la vista layout de las opciones del Spinner (R.layout.spinner_items), y la lista de valores que se recibe en "lista" al llamar a la interfaz FirestoreCallback
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
        if (nombreActivity != null) { //Que entre al if si "nombreActivity" no es nulo
            switch (nombreActivity) { //El "nombreActivity" nos sirve para saber la pantalla con la que trabajaremos
                case "AgregarUsuario": //Si estamos en la pantalla de "Agregar Usuario", al dar clic en el botón "Confirmar" que realice las operaciones de este case

                    //Creamos un alertDialog que pregunte si se desea agregar el usuario
                    new AlertDialog.Builder(this).setTitle("AGREGAR USUARIO").setMessage("¿Está seguro que desea agregar el usuario?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { //Si se selecciona la opción positiva, entrará aquí y al método "insertarUsuario()"
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Enlazamos los EditText con las siguientes variables String
                                String nombre = txtNombreApellido.getText().toString();
                                String identidad = txtIdentidad.getText().toString();
                                String telefono = txtTelefono.getText().toString();

                                //Obtenemos la selección hecha en los Spinners de Roles y Cuadrillas
                                String rol = spRoles.getSelectedItem().toString();
                                String cuadrilla = spCuadrillas.getSelectedItem().toString();

                                usu.insertarUsuario(nombre, identidad, telefono, rol, cuadrilla); //Llamamos el método "insertarUsuario" de la clase "Usuario" donde se hará el proceso de inserción a Firestore y le mandamos los textboxes y selecciones de los spinners de esta pantalla
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

    //Interfaz "callback" que nos ayuda a realizar operaciones que puedan tomar un tiempo en completarse, como las operaciones que requieren internet y pueden tardar un poco en realizarse debido a la conexión a internet
    /*public interface ValidacionCallback {
        void onResultado(boolean esValido); //Recibe un valor booleano que determina si la identidad está disponible
    }*/
}