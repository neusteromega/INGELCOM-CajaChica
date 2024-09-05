package com.ingelcom.cajachica;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.ingelcom.cajachica.DAO.FirestoreOperaciones;
import com.ingelcom.cajachica.DAO.Usuario;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Validaciones;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AgregarEditarPerfil extends AppCompatActivity {

    private LinearLayout llCorreo, llRol, llCuadrilla;
    private EditText txtNombreApellido, txtIdentidad, txtTelefono;
    private TextView btnReintentarConexion, lblTitulo, btnConfirmar;
    private Spinner spRoles, spCuadrillas;
    private ImageView btnRegresar;
    private View viewNoInternet;
    private ProgressBar pbReintentarConexion;
    private String nombreActivity, nombre, identidadVieja, correo, telefono, cuadrilla, rol;

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

        //Evento Click del botón "Reintentar" de la vista "viewNoInternet"
        btnReintentarConexion.setOnClickListener(v -> {
            pbReintentarConexion.setVisibility(View.VISIBLE); //Mostramos el ProgressBar

            //Creamos una nueva instancia de "Handler", que está vinculada al Looper principal (el hilo principal de la aplicación). Esto asegura que cualquier operación realizada dentro de este Handler se ejecute en el hilo principal
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { //El "Handler" utiliza el método "postDelayed" para ejecutar el "Runnable" que contiene las acciones a realizar después de un retraso especificado (en este caso, 1000 milisegundos, es decir, 1 segundo)
                @Override
                public void run() {
                    pbReintentarConexion.setVisibility(View.GONE); //Después de un segundo, ocultamos el ProgressBar
                }
            }, 1000);

            Utilidades.mostrarMensajePorInternetCaido(this, viewNoInternet); //Llamamos el método utilitario "mostrarMensajePorInternetCaido" donde mandamos la vista "viewNoInternet" donde se hará visible cuando no haya conexión a internet y se ocultará cuando si haya
        });
    }

    private void inicializarElementos() {
        //Enlazamos las variables globales con los elementos gráficos
        llCorreo = findViewById(R.id.LLCorreoAEP);
        llRol = findViewById(R.id.LLRolAEP);
        llCuadrilla = findViewById(R.id.LLCuadrillaAEP);

        txtNombreApellido = findViewById(R.id.txtNombreApellidoAEP);
        txtIdentidad = findViewById(R.id.txtIdentidadAEP);
        txtTelefono = findViewById(R.id.txtTelefonoAEP);

        lblTitulo = findViewById(R.id.lblTituloAEP);
        btnRegresar = findViewById(R.id.imgRegresarAEP);
        btnConfirmar = findViewById(R.id.btnConfirmarAEP);
        spRoles = findViewById(R.id.spRolAEP);
        spCuadrillas = findViewById(R.id.spCuadrillaAEP);
        viewNoInternet = findViewById(R.id.viewNoInternetAEP);
        btnReintentarConexion = findViewById(R.id.btnReintentarConexion);
        pbReintentarConexion = findViewById(R.id.pbReintentarConexion);

        Utilidades.mostrarMensajePorInternetCaido(this, viewNoInternet); //Llamamos el método utilitario "mostrarMensajePorInternetCaido" donde mandamos la vista "viewNoInternet" donde se hará visible cuando no haya conexión a internet y se ocultará cuando si haya
    }

    private void obtenerDatos() {
        //Obtenemos el nombre del activity que se envía desde el activity anterior, lo hacemos llamando a la función "obtenerStringExtra" de la clase "Utilidades", y le mandamos "this" para referenciar esta actividad y "ActivityAEP" como clave del putExtra
        nombreActivity = Utilidades.obtenerStringExtra(this, "ActivityAEP");

        switch (nombreActivity) {
            case "EditarEmpleadoAdmin": //Sólo ponemos esta opción ya que desde el activity Perfil, sólo se reciben datos cuando es "EditarEmpleadoAdmin"
                nombre = Utilidades.obtenerStringExtra(this, "Nombre");
                identidadVieja = Utilidades.obtenerStringExtra(this, "Identidad");
                correo = Utilidades.obtenerStringExtra(this, "Correo");
                telefono = Utilidades.obtenerStringExtra(this, "Telefono");
                cuadrilla = Utilidades.obtenerStringExtra(this, "Cuadrilla");
                rol = Utilidades.obtenerStringExtra(this, "Rol");
                break;
        }
    }

    private void establecerElementos() {
        if (nombreActivity != null) { //Que entre al if si "nombreActivity" no es nulo
            switch (nombreActivity) { //El "nombreActivity" nos sirve para saber la pantalla con la que trabajaremos
                //Establecemos los elementos gráficos si la pantalla es "AgregarUsuario"
                case "AgregarUsuario":
                    lblTitulo.setText("Agregar Usuario"); //Asignamos el título
                    break;

                //Establecemos los elementos gráficos si la pantalla es "EditarAdmin" o "EditarEmpleado"
                case "EditarAdmin":
                case "EditarEmpleado":
                    try {
                        //Llamamos el método "obtenerUsuarioActual" de la clase Usuario para obtener los datos del usuario actual
                        usu.obtenerUsuarioActual(new FirestoreCallbacks.FirestoreDocumentCallback() {
                            @Override
                            public void onCallback(Map<String, Object> documento) {
                                if (documento != null) { //Si "documento" no es nulo, quiere decir que encontró el usuario actual
                                    //Asignamos los datos del usuario actual a los EditTexts de la pantalla
                                    txtNombreApellido.setText((String) documento.get("Nombre"));
                                    txtIdentidad.setText((String) documento.get("Identidad"));
                                    txtTelefono.setText((String) documento.get("Telefono"));
                                }
                            }

                            @Override
                            public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                                Log.e("ObtenerUsuario", "Error al obtener el usuario actual", e);
                            }
                        });
                    }
                    catch (Exception e) {
                        Log.e("ObtenerUsuario", "Error al obtener el usuario actual", e);
                    }

                    //Ocultamos estos tres LinearLayouts que no son necesarios en esta pantalla
                    llCorreo.setVisibility(View.GONE);
                    llCuadrilla.setVisibility(View.GONE);
                    llRol.setVisibility(View.GONE);
                    break;

                //Establecemos los elementos gráficos si la pantalla es "EditarEmpleadoAdmin"
                case "EditarEmpleadoAdmin":
                    try {
                        //Llamamos el método "obtenerUnUsuario" de la clase Usuario, donde le mandamos la "identidadVieja" que se recibe de la pantalla anterior (PerfilEmpleadoAdmin)
                        usu.obtenerUnUsuario(identidadVieja, new FirestoreCallbacks.FirestoreDocumentCallback() {
                            @Override
                            public void onCallback(Map<String, Object> documento) {
                                if (documento != null) { //Si "documento" no es nulo, quiere decir que encontró el usuario mediante la identidad
                                    //Asignamos los datos del usuario encontrado en Firestore a los EditTexts de la pantalla
                                    txtNombreApellido.setText((String) documento.get("Nombre"));
                                    txtIdentidad.setText((String) documento.get("Identidad"));
                                    txtTelefono.setText((String) documento.get("Telefono"));
                                }
                            }

                            @Override
                            public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                                Log.e("ObtenerUsuario", "Error al obtener el usuario", e);
                            }
                        });
                    }
                    catch (Exception e) {
                        Log.e("ObtenerUsuario", "Error al obtener el usuario", e);
                    }
                    break;
            }
        }
    }

    private void inicializarSpinners() {
        //Para inicializar los spinners, llamamos al método "obtenerRegistros" de la clase "FirestoreOperaciones" a la cual le mandamos el nombre de la colección y el nombre del campo de Firestore de los cuales queremos obtener los registros. También invocamos los métodos "onCallback" y "onFailure" de la interfaz FirestoreListCallback
        //ROLES
        oper.obtenerRegistrosCampo("roles", "Nombre", new FirestoreCallbacks.FirestoreListCallback() {
            @Override
            public void onCallback(List<String> lista) { //Aquí está la lista con el nombre de los roles
                //Creamos un adapter de tipo ArrayAdapter el cual le pasamos el contexto de este Activity, la vista layout de las opciones del Spinner (R.layout.spinner_items), y la lista de valores que se recibe en "lista" al llamar a la interfaz FirestoreCallback
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AgregarEditarPerfil.this, R.layout.spinner_items, lista);
                spRoles.setAdapter(adapter); //Asignamos el adapter al Spinner "spRoles"

                if (rol != null && !rol.isEmpty()) { //Si "rol" (la variable global que recibe el rol del Activity anterior) no es nulo y no está vacío, significa que si está recibiendo un Rol del activity anterior, por lo tanto, que entre al if
                    int posicionRol = adapter.getPosition(rol); //Obtenemos la posición del rol recibido en el Spinner, y guardamos dicha posición en una variable int
                    spRoles.setSelection(posicionRol); //Una vez obtenemos la posición del rol recibido en el Spinner, la asignamos al "spRoles" para que al cargar el activity, ya esté seleccionado el rol específico en el Spinner
                }
            }

            @Override
            public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                Log.e("ObtenerRoles", "Error al obtener los roles", e);
            }
        });

        //CUADRILLAS
        oper.obtenerRegistrosCampo("cuadrillas", "Nombre", new FirestoreCallbacks.FirestoreListCallback() {
            @Override
            public void onCallback(List<String> lista) { //Aquí está la lista con el nombre de las cuadrillas
                //Ordenamos la "lista" alfabéticamente llamando al método utilitario "ordenarListaPorAlfabetico" donde enviamos la lista, un String vacío ("") y el orden ascendente. El String vacío es para indicar que el "nombreCampo" por el cual se desea realizar el orden de la lista, en este caso no existe ya que es una lista sencilla y no de una clase
                lista = Utilidades.ordenarListaPorAlfabetico(lista, "", "Ascendente");

                //Creamos un adapter de tipo ArrayAdapter el cual le pasamos el contexto de este Activity, la vista layout de las opciones del Spinner (R.layout.spinner_items), y la lista de valores que se recibe en "lista" al llamar a la interfaz FirestoreCallback
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AgregarEditarPerfil.this, R.layout.spinner_items, lista);
                spCuadrillas.setAdapter(adapter); //Asignamos el adapter a "spCuadrillas"

                if (cuadrilla != null && !cuadrilla.isEmpty()) { //Si "cuadrilla" (la variable global que recibe la cuadrilla del Activity anterior) no es nula y no está vacía, significa que si está recibiendo una Cuadrilla del activity anterior, por lo tanto, que entre al if
                    int posicionCuadrilla = adapter.getPosition(cuadrilla); //Obtenemos la posición de la cuadrilla recibida en el Spinner, y guardamos dicha posición en una variable int
                    spCuadrillas.setSelection(posicionCuadrilla); //Una vez obtenemos la posición de la cuadrilla recibida en el Spinner, la asignamos al "spCuadrillas" para que al cargar el activity, ya esté seleccionada la cuadrilla específica en el Spinner
                }
            }

            @Override
            public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                Log.e("ObtenerCuadrillas", "Error al obtener las cuadrillas", e);
            }
        });
    }

    //Evento clic del botón de confirmar
    public void confirmar(View view) {
        //Llamamos el método utilitario "mostrarMensajePorInternetCaidoBoolean" donde mandamos la vista "viewNoInternet" donde se hará visible cuando no haya conexión a internet y se ocultará cuando si haya. Este retorna un booleano indicando si hay internet o no
        boolean internetDisponible = Utilidades.mostrarMensajePorInternetCaidoBoolean(this, viewNoInternet);

        if (internetDisponible) { //Si el booleano guardado en "internetDisponible" es true, significa que si hay internet, entonces que entre al if para hacer las operaciones de confirmación. Pero si es un false, significa que no hay internet, entonces que no entre al if y muestre la vista "view_nointernet" (esto se muestra en el método utilitario)
            if (nombreActivity != null) { //Que entre al if si "nombreActivity" no es nulo
                switch (nombreActivity) { //El "nombreActivity" nos sirve para saber la pantalla con la que trabajaremos
                    case "AgregarUsuario":

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
                                        editarPerfilAdminEmpleado("Administrador"); //Llamamos el método "editarPerfilAdminEmpleado" y le mandamos el texto "Administrador" para indicar que es un administrador quien está editando su perfil
                                    }
                                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() { //Si se seleccionó la opción negativa, entrará aquí y solamente mostrará un mensaje en Logcat
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Log.d("Mensaje", "Se canceló la acción"); //Se muestra un mensaje en el Logcat indicando que se canceló la acción
                                    }
                                }).show();
                        break;

                    case "EditarEmpleado":

                        //Creamos un alertDialog que pregunte si se desea editar el perfil del Empleado
                        new AlertDialog.Builder(this).setTitle("EDITAR PERFIL").setMessage("¿Está seguro que desea modificar los datos de su perfil")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { //Si se selecciona la opción positiva, entrará aquí y al método "editarPerfilAdmin()"
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        editarPerfilAdminEmpleado("Empleado"); //Llamamos el método "editarPerfilAdminEmpleado" y le mandamos el texto "Empleado" para indicar que es un empleado quien está editando su perfil
                                    }
                                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() { //Si se seleccionó la opción negativa, entrará aquí y solamente mostrará un mensaje en Logcat
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Log.d("Mensaje", "Se canceló la acción"); //Se muestra un mensaje en el Logcat indicando que se canceló la acción
                                    }
                                }).show();
                        break;

                    case "EditarEmpleadoAdmin":

                        //Creamos un alertDialog que pregunte si se desea editar el perfil del Empleado por un Administrador
                        new AlertDialog.Builder(this).setTitle("EDITAR PERFIL").setMessage("¿Está seguro que desea modificar los datos del perfil")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { //Si se selecciona la opción positiva, entrará aquí y al método "editarPerfilAdmin()"
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        editarPerfilEmpleadoPorAdmin(); //Llamamos el método "editarPerfilEmpleadoPorAdmin"
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
    }

    //Método que permite a un administrador poder agregar un usuario
    private void agregarUsuario() {
        //Enlazamos los EditText con las siguientes variables String
        String nombre = txtNombreApellido.getText().toString();
        String identidad = txtIdentidad.getText().toString();
        String telefono = txtTelefono.getText().toString();

        //Obtenemos la selección hecha en los Spinners de Roles y Cuadrillas
        String rol = spRoles.getSelectedItem().toString();

        if (Validaciones.validarNombre(nombre)) { //Entrará al if si "validarNombre" retorna true (ese método valida que se registre un nombre correcto)
            if (Validaciones.validarIdentidad(identidad)) { //Entrará al if si "validarIdentidad" retorna true (ese método valida que se registre una identidad correcta)
                if (rol.equalsIgnoreCase("Empleado")) { //Si el rol seleccionado fue "Empleado", que agarre la cuadrilla del "spCuadrillas"
                    String cuadrilla = spCuadrillas.getSelectedItem().toString();
                    usu.insertarUsuario(nombre, identidad, telefono, rol, cuadrilla); //Llamamos el método "insertarUsuario" de la clase "Usuario" donde se hará el proceso de inserción a Firestore y le mandamos los textboxes y selecciones de los spinners de esta pantalla
                }
                else if (rol.equalsIgnoreCase("Administrador")) //En cambio, si el rol seleccionado fue "Administrador", que mande la cuadrilla vacía
                    usu.insertarUsuario(nombre, identidad, telefono, rol, ""); //Llamamos el método "insertarUsuario" de la clase "Usuario" donde se hará el proceso de inserción a Firestore y le mandamos los textboxes y selecciones de los spinners de esta pantalla
            }
            else
                Toast.makeText(this, "INGRESE UNA IDENTIDAD VÁLIDA", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, "INGRESE UN NOMBRE VÁLIDO", Toast.LENGTH_SHORT).show();
    }

    //Método que permite a un administrador o empleado actualizar los datos de su perfil
    private void editarPerfilAdminEmpleado(String tipo) {
        //Enlazamos los EditText con las siguientes variables String
        String nombre = txtNombreApellido.getText().toString();
        String identidadNueva = txtIdentidad.getText().toString();
        String telefono = txtTelefono.getText().toString();

        if (Validaciones.validarNombre(nombre)) { //Entrará al if si "validarNombre" retorna true (ese método valida que se registre un nombre correcto)
            if (Validaciones.validarIdentidad(identidadNueva)) { //Entrará al if si "validarIdentidad" retorna true (ese método valida que se registre una identidad correcta)
                try {
                    //Llamamos el método "obtenerUsuarioActual" de la clase Usuario para obtener los datos del usuario actual
                    usu.obtenerUsuarioActual(new FirestoreCallbacks.FirestoreDocumentCallback() {
                        @Override
                        public void onCallback(Map<String, Object> documento) {
                            if (documento != null) { //Si "documento" no es nulo, quiere decir que encontró el usuario actual
                                String identidadVieja = (String) documento.get("Identidad");
                                String correoActual = (String) documento.get("Correo");

                                if (tipo.equalsIgnoreCase("Administrador")) {
                                    setResult(RESULT_OK); //Con esto llamamos el método "onActivityResult" del Activity anterior (Perfil) y le mandamos "RESULT_OK" (setResult llama al "onActivityResult" de Perfil ya que si entra a este método "editarPerfilAdminEmpleado", si o sí está programado que el activity anterior fue "Perfil" y ahí iniciamos el activity actual con "startActivityForResult"). En el método "onActivityResult" se finaliza con "finish" el activity Perfil, lo finalizamos sólo cuando el usuario haya actualizado los datos de su perfil. Esto lo hacemos para evitar conflictos con el botón de retroceso entre pantallas
                                    usu.editarUsuario("PerfilAdmin", nombre, correoActual, identidadVieja, identidadNueva, telefono, "", "");
                                }
                                else if (tipo.equalsIgnoreCase("Empleado")) {
                                    setResult(RESULT_OK); //Con esto llamamos el método "onActivityResult" del Activity anterior (Perfil) y le mandamos "RESULT_OK" (setResult llama al "onActivityResult" de Perfil ya que si entra a este método "editarPerfilAdminEmpleado", si o sí está programado que el activity anterior fue "Perfil" y ahí iniciamos el activity actual con "startActivityForResult"). En el método "onActivityResult" se finaliza con "finish" el activity Perfil, lo finalizamos sólo cuando el usuario haya actualizado los datos de su perfil. Esto lo hacemos para evitar conflictos con el botón de retroceso entre pantallas
                                    usu.editarUsuario("PerfilEmpleado", nombre, correoActual, identidadVieja, identidadNueva, telefono, "", "");
                                }
                            }
                        }

                        @Override
                        public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                            Log.e("ObtenerUsuario", "Error al obtener el usuario actual", e);
                        }
                    });
                }
                catch (Exception e) {
                    Log.e("ObtenerUsuario", "Error al obtener el usuario actual", e);
                }
            }
            else
                Toast.makeText(this, "INGRESE UNA IDENTIDAD VÁLIDA", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, "INGRESE UN NOMBRE VÁLIDO", Toast.LENGTH_SHORT).show();
    }

    //Método que permite a un administrador poder editar los datos del perfil de un empleado
    private void editarPerfilEmpleadoPorAdmin() {
        //Enlazamos los EditText y Spinners con las siguientes variables String
        String nombre = txtNombreApellido.getText().toString();
        String identidadNueva = txtIdentidad.getText().toString();
        String telefono = txtTelefono.getText().toString();
        String cuadrilla = spCuadrillas.getSelectedItem().toString();
        String rol = spRoles.getSelectedItem().toString();

        if (Validaciones.validarNombre(nombre)) { //Entrará al if si "validarNombre" retorna true (ese método valida que se registre un nombre correcto)
            if (Validaciones.validarIdentidad(identidadNueva)) { //Entrará al if si "validarIdentidad" retorna true (ese método valida que se registre una identidad correcta)
                usu.editarUsuario("", nombre, correo, identidadVieja, identidadNueva, telefono, cuadrilla, rol);
            }
            else
                Toast.makeText(this, "INGRESE UNA IDENTIDAD VÁLIDA", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, "INGRESE UN NOMBRE VÁLIDO", Toast.LENGTH_SHORT).show();
    }

    //Método que permite ocultar el spinner de cuadrillas cuando el rol seleccionado es "Administrador" (los administradores no pueden pertenecer a una cuadrilla)
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
                    //Detecta cuando ningún elemento ha sido seleccionado. Queda vacío
                }
            });
        }
        catch (Exception e) {
            Log.e("DetectarRol", "Error al detectar el rol", e);
        }
    }

    //Método que permite retroceder a la pantalla anterior
    public void retroceder(View view) {
        onBackPressed();
    }
}