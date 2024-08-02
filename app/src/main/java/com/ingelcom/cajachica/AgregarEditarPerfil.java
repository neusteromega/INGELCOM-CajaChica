package com.ingelcom.cajachica;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.List;
import java.util.Map;

public class AgregarEditarPerfil extends AppCompatActivity {

    private LinearLayout llNombreApellido, llIdentidad, llTelefono, llCorreo, llRol, llCuadrilla, llEstado;
    private EditText txtNombreApellido, txtIdentidad, txtTelefono, txtCorreo;
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

        inicializarElementos();
        obtenerDatos();
        establecerElementos();
        inicializarSpinners();
        ocultarCuadrillas();
    }

    private void inicializarElementos() {
        llNombreApellido = findViewById(R.id.LLNombreApellidoAEP);
        llIdentidad = findViewById(R.id.LLIdentidadAEP);
        llTelefono = findViewById(R.id.LLTelefonoAEP);
        llCorreo = findViewById(R.id.LLCorreoAEP);
        llRol = findViewById(R.id.LLRolAEP);
        llCuadrilla = findViewById(R.id.LLCuadrillaAEP);
        llEstado = findViewById(R.id.LLEstadoAEP);

        txtNombreApellido = findViewById(R.id.txtNombreApellidoAEP);
        txtIdentidad = findViewById(R.id.txtIdentidadAEP);
        txtTelefono = findViewById(R.id.txtTelefonoAEP);
        txtCorreo = findViewById(R.id.txtCorreoAEP);

        lblTitulo = findViewById(R.id.lblTituloAEP);
        btnRegresar = findViewById(R.id.imgRegresarAEP);
        btnConfirmar = findViewById(R.id.btnConfirmarAEP);

        spRoles = findViewById(R.id.spRolAEP);
        spCuadrillas = findViewById(R.id.spCuadrillaAEP);
        spEstado = findViewById(R.id.spEstadoAEP);
    }

    private void obtenerDatos() {
        //Obtenemos el nombre del activity que se envía desde el activity anterior, lo hacemos llamando a la función "obtenerStringExtra" de la clase "Utilidades", y le mandamos "this" para referenciar esta actividad y "Activity" como clave del putExtra
        nombreActivity = Utilidades.obtenerStringExtra(this, "ActivityAEP");

        switch (nombreActivity) {
            case "EditarAdmin":

            case "EditarEmpleado":

                break;
        }
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
                    llEstado.setVisibility(View.GONE);
                    break;

                //Establecemos los elementos gráficos si la pantalla es "EditarAdmin"
                case "EditarAdmin":
                //Establecemos los elementos gráficos si la pantalla es "EditarEmpleado"
                case "EditarEmpleado":
                    try {
                        usu.obtenerUsuarioActual(new FirestoreCallbacks.FirestoreDocumentCallback() {
                            @Override
                            public void onCallback(Map<String, Object> documento) {
                                if (documento != null) { //Si "documento" no es nulo, quiere decir que encontró el usuario mediante el correo
                                    txtNombreApellido.setText((String) documento.get("Nombre"));
                                    txtIdentidad.setText((String) documento.get("Identidad"));
                                    txtTelefono.setText((String) documento.get("Telefono"));
                                    txtCorreo.setText((String) documento.get("Correo"));
                                }
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.w("BuscarDocumento", "Error al obtener el documento", e);
                            }
                        });
                    }
                    catch (Exception e) {
                        Log.w("ObtenerUsuario", e);
                    }

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
        oper.obtenerRegistrosCampo("roles", "Nombre", new FirestoreCallbacks.FirestoreListCallback() {
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
        oper.obtenerRegistrosCampo("cuadrillas", "Nombre", new FirestoreCallbacks.FirestoreListCallback() {
            @Override
            public void onCallback(List<String> lista) {
                /*List<String> listaCuadrillas = new ArrayList<>(); //Lista que tendrá todas las cuadrillas, más la opción de "No Pertenece"
                listaCuadrillas.add("No Pertenece"); //Agregamos la opción de "No Pertenece"
                listaCuadrillas.addAll(lista); //Anexamos la lista de cuadrillas de Firestore que está en la variable "lista"*/

                //Creamos un adapter de tipo ArrayAdapter el cual le pasamos el contexto de este Activity, la vista layout de las opciones del Spinner (R.layout.spinner_items), y la lista de valores que se recibe en "lista" al llamar a la interfaz FirestoreCallback
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AgregarEditarPerfil.this, R.layout.spinner_items, lista);
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
                                agregarUsuario();
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() { //Si se seleccionó la opción negativa, entrará aquí y solamente mostrará un mensaje en Logcat
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.d("Mensaje", "Se canceló la acción"); //Se muestra un mensaje en el Logcat indicando que se canceló la acción
                                }
                            }).show();
                    break;

                case "EditarAdmin":

                    //Creamos un alertDialog que pregunte si se desea editar el perfil del Administrador
                    new AlertDialog.Builder(this).setTitle("EDITAR PERFIL").setMessage("¿Está seguro que desea modificar los datos de su perfil")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { //Si se selecciona la opción positiva, entrará aquí y al método "editarPerfilAdmin()"
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    editarPerfilAdmin();
                                }
                            }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() { //Si se seleccionó la opción negativa, entrará aquí y solamente mostrará un mensaje en Logcat
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.d("Mensaje", "Se canceló la acción"); //Se muestra un mensaje en el Logcat indicando que se canceló la acción
                                }
                            }).show();
                    break;

                case "EditarEmpleado":

                    break;

                case "EditarEmpleadoAdmin":

                    break;
            }
        }
    }

    private void agregarUsuario() {
        //Enlazamos los EditText con las siguientes variables String
        String nombre = txtNombreApellido.getText().toString();
        String identidad = txtIdentidad.getText().toString();
        String telefono = txtTelefono.getText().toString();

        //Obtenemos la selección hecha en los Spinners de Roles y Cuadrillas
        String rol = spRoles.getSelectedItem().toString();

        if (rol.equalsIgnoreCase("Empleado")) { //Si el rol seleccionado fue "Empleado", que agarre la cuadrilla del "spCuadrillas"
            String cuadrilla = spCuadrillas.getSelectedItem().toString();
            usu.insertarUsuario(nombre, identidad, telefono, rol, cuadrilla); //Llamamos el método "insertarUsuario" de la clase "Usuario" donde se hará el proceso de inserción a Firestore y le mandamos los textboxes y selecciones de los spinners de esta pantalla
        }
        else if (rol.equalsIgnoreCase("Administrador")) //En cambio, si el rol seleccionado fue "Administrador", que mande la cuadrilla vacía
            usu.insertarUsuario(nombre, identidad, telefono, rol, ""); //Llamamos el método "insertarUsuario" de la clase "Usuario" donde se hará el proceso de inserción a Firestore y le mandamos los textboxes y selecciones de los spinners de esta pantalla
    }

    private void editarPerfilAdmin() {
        //Enlazamos los EditText con las siguientes variables String
        String nombre = txtNombreApellido.getText().toString();
        String identidadNueva = txtIdentidad.getText().toString();
        String telefono = txtTelefono.getText().toString();
        String correoNuevo = txtCorreo.getText().toString();

        try {
            usu.obtenerUsuarioActual(new FirestoreCallbacks.FirestoreDocumentCallback() {
                @Override
                public void onCallback(Map<String, Object> documento) {
                    if (documento != null) { //Si "documento" no es nulo, quiere decir que encontró el usuario mediante el correo
                        String correoViejo = (String) documento.get("Correo");
                        String identidadVieja = (String) documento.get("Identidad");

                        usu.editarUsuario("PerfilAdmin", nombre, identidadVieja, identidadNueva, telefono, correoViejo, correoNuevo, "", "", null);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.w("BuscarDocumento", "Error al obtener el documento", e);
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerUsuario", e);
        }
    }

    private void ocultarCuadrillas() {
        try {
            //Evento que detecta la selección hecha en el "spRoles"
            spRoles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    String rol = adapterView.getItemAtPosition(position).toString(); //Obtenemos la selección hecha en el Spinner y la guardamos en la variable "rol"

                    if (rol.equalsIgnoreCase("Administrador")) //Si el rol obtenido del Spinner es "Administrador" que entre al if
                        llCuadrilla.setVisibility(View.GONE); //Ocultamos el "llCuadrilla" cuando el rol sea "Administrador" ya que los administradores no pueden pertenecer a una cuadrilla
                    else if (rol.equalsIgnoreCase("Empleado")) //Si el rol obtenido del Spinner es "Empleado" que entre al if
                        llCuadrilla.setVisibility(View.VISIBLE); //Mostramos el "llCuadrilla" cuando el rol sea "Empleado" ya que los empleados si pueden pertenecer a una cuadrilla
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
        catch (Exception e) {
            Log.w("DetectarRol", e);
        }
    }
}