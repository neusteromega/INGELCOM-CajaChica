package com.ingelcom.cajachica;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ingelcom.cajachica.DAO.FirestoreOperaciones;
import com.ingelcom.cajachica.DAO.Usuario;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;

import java.util.HashMap;
import java.util.Map;

public class Perfil extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirestoreOperaciones oper = new FirestoreOperaciones();
    private TextView btnReintentarConexion, lblTitulo, lblSeparadorTelCua, lblNombre, lblCorreo, lblIdentidad, lblTelefono, lblCuadrilla, btnEditarPerfil;
    private LinearLayout llCuadrilla;
    private SwipeRefreshLayout swlRecargar;
    private View viewNoInternet;
    private ProgressBar pbReintentarConexion;
    private String emailActual, nombreActivity, nombre, correo, identidad, telefono, cuadrilla, rol, estado;

    private Usuario usu = new Usuario(Perfil.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        inicializarElementos();
        obtenerDatos();
        establecerElementos();

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

            ocultarBotonEditarNoInternet();
        });
    }

    private void inicializarElementos() {
        mAuth = FirebaseAuth.getInstance(); //Creamos la instancia de Firebase Authentication
        currentUser = mAuth.getCurrentUser(); //Obtenemos el usuario actual
        llCuadrilla = findViewById(R.id.LLCuadrillaPerfil);
        lblSeparadorTelCua = findViewById(R.id.lblSepTelefonoCuadrillaPerfil);

        lblTitulo = findViewById(R.id.lblTituloPerfil);
        lblNombre = findViewById(R.id.lblNombrePerfil);
        lblCorreo = findViewById(R.id.lblCorreoPerfil);
        lblIdentidad = findViewById(R.id.lblIdentidadPerfil);
        lblTelefono = findViewById(R.id.lblTelefonoPerfil);
        lblCuadrilla = findViewById(R.id.lblCuadrillaPerfil);
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        swlRecargar = findViewById(R.id.swipeRefreshLayoutPerfil);
        viewNoInternet = findViewById(R.id.viewNoInternetPerfil);
        btnReintentarConexion = findViewById(R.id.btnReintentarConexion);
        pbReintentarConexion = findViewById(R.id.pbReintentarConexion);

        swlRecargar.setOnRefreshListener(this); //Llamada al método "onRefresh"
        swlRecargar.setColorSchemeResources(R.color.clr_fuente_primario); //Color del SwipeRefreshLayout

        ocultarBotonEditarNoInternet();
    }

    private void obtenerDatos() {
        emailActual = currentUser.getEmail(); //Obtenemos el email del usuario actual

        nombreActivity = Utilidades.obtenerStringExtra(this, "ActivityPerfil");
        nombre = Utilidades.obtenerStringExtra(this, "Nombre");
        correo = Utilidades.obtenerStringExtra(this, "Correo");
        identidad = Utilidades.obtenerStringExtra(this, "Identidad");
        telefono = Utilidades.obtenerStringExtra(this, "Telefono");
        cuadrilla = Utilidades.obtenerStringExtra(this, "Cuadrilla");
        rol = Utilidades.obtenerStringExtra(this, "Rol");
        estado = Utilidades.obtenerStringExtra(this, "Estado");
    }

    private void establecerElementos() {
        if (nombreActivity != null) { //Que entre al if si "nombreActivity" no es nulo
            //El "nombreActivity" nos sirve para saber la pantalla con la que trabajaremos
            switch (nombreActivity) {
                //Establecemos los elementos gráficos si la pantalla es "PerfilAdmin"
                case "PerfilAdmin":
                    perfilAdmin();
                    break;

                //Establecemos los elementos gráficos si la pantalla es "PerfilEmpleadoAdmin"
                case "PerfilEmpleadoAdmin":
                    perfilEmpleadoAdmin();
                    break;

                //Establecemos los elementos gráficos si la pantalla es "PerfilEmpleado"
                case "PerfilEmpleado":
                    perfilEmpleado();
                    break;
            }
        }
    }

    private void perfilAdmin() {
        lblTitulo.setText("Mi Perfil"); //Asignamos el titulo

        //Ocultamos el separador entre Teléfono y Cuadrilla, y el LinearLayout de Cuadrilla
        lblSeparadorTelCua.setVisibility(View.GONE);
        llCuadrilla.setVisibility(View.GONE);

        try {
            usu.obtenerUsuarioActual(new FirestoreCallbacks.FirestoreDocumentCallback() {
                @Override
                public void onCallback(Map<String, Object> documento) {
                    if (documento != null) { //Si "documento" no es nulo, quiere decir que encontró el usuario mediante el correo
                        //Asignamos la información del usuario en los elementos gráficos de la pantalla. Esta información se extrae del hashMap "documento"
                        lblNombre.setText((String) documento.get("Nombre"));
                        lblCorreo.setText((String) documento.get("Correo"));
                        lblIdentidad.setText((String) documento.get("Identidad"));
                        lblTelefono.setText((String) documento.get("Telefono"));
                    }
                    else { //Si "documento" es nulo, no se encontró el usuario en la colección, y entrará en este else
                        Toast.makeText(Perfil.this, "USUARIO NO ENCONTRADO", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.w("BuscarDocumento", "Error al obtener el documento", e);
                }
            });
        }
        catch (Exception e) {
            Log.w("PerfilAdmin", e);
        }
    }

    private void perfilEmpleadoAdmin() {
        lblTitulo.setText("Perfil de Empleado"); //Asignamos el titulo

        try {
            usu.obtenerUnUsuario(identidad, new FirestoreCallbacks.FirestoreDocumentCallback() {
                @Override
                public void onCallback(Map<String, Object> documento) {
                    if (documento != null) { //Si "documento" no es nulo, quiere decir que encontró el usuario mediante la identidad
                        String correoElec = (String) documento.get("Correo");

                        lblNombre.setText((String) documento.get("Nombre"));
                        lblIdentidad.setText((String) documento.get("Identidad"));
                        lblTelefono.setText((String) documento.get("Telefono"));
                        lblCuadrilla.setText((String) documento.get("Cuadrilla"));

                        if (correoElec.isEmpty())
                            lblCorreo.setText("Sin Correo");
                        else
                            lblCorreo.setText((String) documento.get("Correo"));
                    }
                    else { //Si "documento" es nulo, no se encontró el usuario en la colección, y entrará en este else
                        Toast.makeText(Perfil.this, "USUARIO NO ENCONTRADO", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.w("BuscarDocumento", "Error al obtener el documento", e);
                }
            });
        }
        catch (Exception e) {
            Log.w("PerfilEmpleadoAdmin", e);
        }
    }

    private void perfilEmpleado() {
        //Asignamos el titulo
        lblTitulo.setText("Mi Perfil");

        try {
            usu.obtenerUsuarioActual(new FirestoreCallbacks.FirestoreDocumentCallback() {
                @Override
                public void onCallback(Map<String, Object> documento) {
                    if (documento != null) { //Si "documento" no es nulo, quiere decir que encontró el usuario mediante el correo
                        nombre = (String) documento.get("Nombre");
                        correo = (String) documento.get("Correo");
                        identidad = (String) documento.get("Identidad");
                        telefono = (String) documento.get("Telefono");
                        cuadrilla = (String) documento.get("Cuadrilla");

                        lblNombre.setText(nombre);
                        lblCorreo.setText(correo);
                        lblIdentidad.setText(identidad);
                        lblTelefono.setText(telefono);
                        lblCuadrilla.setText(cuadrilla);
                    }
                    else { //Si "documento" es nulo, no se encontró el usuario en la colección, y entrará en este else
                        Toast.makeText(Perfil.this, "USUARIO NO ENCONTRADO", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.w("Buscar Documento", "Error al obtener el documento", e);
                }
            });
        }
        catch (Exception e) {
            Log.w("PerfilEmpleado", e);
        }
    }

    //Método que redirecciona al activity "AgregarEditarPerfil" donde manda los datos necesarios dependiendo del contenido recibido del Activity anterior y guardado en la variable global "nombreActivity"
    public void editarPerfil(View view) {
        //Que entre al if si "nombreActivity" no es nulo
        if (nombreActivity != null) {
            //El "nombreActivity" nos sirve para saber la pantalla en la que estamos
            switch (nombreActivity) { //Dependiendo la pantalla en que estemos, al dar clic en el botón "Editar Perfil", que mande un String al Activity "AgregarEditarPerfil" indicando qué tipo de usuario es quien está editando el perfil
                case "PerfilAdmin":
                    HashMap<String,Object> datosAdmin = new HashMap<>(); //Creamos un HashMap para guardar el nombre del activity
                    datosAdmin.put("ActivityAEP", "EditarAdmin"); //Guardamos el nombre del activity en el HashMap

                    //Llamamos el método utilitario "iniciarActivityConDatosStartResult" para que el siguiente activity se inicie con "startActivityForResult". Aquí también mandamos el requestCode 1
                    Utilidades.iniciarActivityConDatosStartResult(this, AgregarEditarPerfil.class, datosAdmin, 1);
                    break;

                case "PerfilEmpleadoAdmin":
                    HashMap<String,Object> datosEmpAdmin = new HashMap<>(); //Creamos un HashMap para guardar los nombres de los campos y los datos

                    //Guardamos las claves y datos en el HashMap
                    datosEmpAdmin.put("ActivityAEP", "EditarEmpleadoAdmin");
                    datosEmpAdmin.put("Nombre", nombre);
                    datosEmpAdmin.put("Identidad", identidad);
                    datosEmpAdmin.put("Correo", correo);
                    datosEmpAdmin.put("Telefono", telefono);
                    datosEmpAdmin.put("Cuadrilla", cuadrilla);
                    datosEmpAdmin.put("Rol", rol);

                    //Llamamos el método utilitario "iniciarActivityConDatosStartResult" para que el siguiente activity se inicie con "startActivityForResult". Aquí también mandamos el requestCode 1
                    Utilidades.iniciarActivityConDatosStartResult(this, AgregarEditarPerfil.class, datosEmpAdmin, 1);
                    break;

                case "PerfilEmpleado":
                    HashMap<String,Object> datosEmpleado = new HashMap<>(); //Creamos un HashMap para guardar el nombre del activity
                    datosEmpleado.put("ActivityAEP", "EditarEmpleado"); //Guardamos el nombre del activity en el HashMap

                    //Llamamos el método utilitario "iniciarActivityConDatosStartResult" para que el siguiente activity se inicie con "startActivityForResult". Aquí también mandamos el requestCode 1
                    Utilidades.iniciarActivityConDatosStartResult(this, AgregarEditarPerfil.class, datosEmpleado, 1);
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    private void ocultarBotonEditarNoInternet() {
        //Llamamos el método utilitario "mostrarMensajePorInternetCaidoBoolean" donde mandamos la vista "viewNoInternet" donde se hará visible cuando no haya conexión a internet y se ocultará cuando si haya. Este retorna un booleano indicando si hay internet o no
        boolean internetDisponible = Utilidades.mostrarMensajePorInternetCaidoBoolean(this, viewNoInternet);

        if (internetDisponible) //Si el booleano guardado en "internetDisponible" es true, significa que si hay internet, entonces que entre al if
            btnEditarPerfil.setVisibility(View.VISIBLE); //Mostramos el botón de editar perfil
        else //Pero si es un false, significa que no hay internet, entonces que entre al else
            btnEditarPerfil.setVisibility(View.GONE); //Ocultamos el botón de editar perfil
    }

    @Override
    public void onRefresh() { //Método que detecta cuando se recarga la pantalla con SwipeRefreshLayout
        swlRecargar.bringToFront(); //Traer para el enfrente el SwipeRefreshLayout

        //Creamos una nueva instancia de "Handler", que está vinculada al Looper principal (el hilo principal de la aplicación). Esto asegura que cualquier operación realizada dentro de este Handler se ejecute en el hilo principal
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { //El "Handler" utiliza el método "postDelayed" para ejecutar el "Runnable" que contiene las acciones a realizar después de un retraso especificado (en este caso, 1000 milisegundos, es decir, 1 segundo)
            @Override
            public void run() {
                //Mandar para atrás el SwipeRefreshLayout luego de que cargue
                ViewGroup parent = (ViewGroup) swlRecargar.getParent();
                parent.removeView(swlRecargar);
                parent.addView(swlRecargar, 0);

                ocultarBotonEditarNoInternet();
                establecerElementos();
                swlRecargar.setRefreshing(false); //Llamamos a este método para detener la animación de refresco
            }
        }, 1000);
    }

    //Método que permite retroceder a la pantalla anterior
    public void retroceder(View view) {
        onBackPressed();
    }
}