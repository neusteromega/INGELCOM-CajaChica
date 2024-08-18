package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
    private TextView lblTitulo, lblSeparadorTelCua, lblNombre, lblCorreo, lblIdentidad, lblTelefono, lblCuadrilla, btnEditarPerfil;
    private LinearLayout llCuadrilla;
    private SwipeRefreshLayout swlRecargar;
    private String emailActual, nombreActivity, nombre, correo, identidad, telefono, cuadrilla, rol, estado;

    private Usuario usu = new Usuario(Perfil.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        inicializarElementos();
        obtenerDatos();
        establecerElementos();
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

        swlRecargar.setOnRefreshListener(this); //Llamada al método "onRefresh"
        swlRecargar.setColorSchemeResources(R.color.clr_fuente_primario); //Color del SwipeRefreshLayout
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

        //Asignamos los datos del empleado a los TextViews
        lblNombre.setText(nombre);
        lblCorreo.setText(correo);
        lblIdentidad.setText(identidad);
        lblTelefono.setText(telefono);
        lblCuadrilla.setText(cuadrilla);
    }

    private void perfilEmpleado() {
        //Asignamos el titulo
        lblTitulo.setText("Mi Perfil");

        try {
            usu.obtenerUsuarioActual(new FirestoreCallbacks.FirestoreDocumentCallback() {
                @Override
                public void onCallback(Map<String, Object> documento) {
                    if (documento != null) { //Si "documento" no es nulo, quiere decir que encontró el usuario mediante el correo
                        //Asignamos la información del usuario en los elementos gráficos de la pantalla. Esta información se extrae del hashMap
                        lblNombre.setText((String) documento.get("Nombre"));
                        lblCorreo.setText((String) documento.get("Correo"));
                        lblIdentidad.setText((String) documento.get("Identidad"));
                        lblTelefono.setText((String) documento.get("Telefono"));
                        lblCuadrilla.setText((String) documento.get("Cuadrilla"));
                    } else { //Si "documento" es nulo, no se encontró el usuario en la colección, y entrará en este else
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
                    Utilidades.iniciarActivityConString(this, AgregarEditarPerfil.class, "ActivityAEP", "EditarAdmin", false);
                    break;

                case "PerfilEmpleadoAdmin":
                    HashMap<String,Object> datos = new HashMap<>(); //Creamos un HashMap para guardar los nombres de los campos y los datos

                    //Guardamos las claves y datos en el HashMap
                    datos.put("ActivityAEP", "EditarEmpleadoAdmin");
                    datos.put("Nombre", nombre);
                    datos.put("Identidad", identidad);
                    datos.put("Telefono", telefono);
                    datos.put("Cuadrilla", cuadrilla);
                    datos.put("Rol", rol);

                    Utilidades.iniciarActivityConDatos(this, AgregarEditarPerfil.class, datos);
                    break;

                case "PerfilEmpleado":
                    Utilidades.iniciarActivityConString(this, AgregarEditarPerfil.class, "ActivityAEP", "EditarEmpleado", false);
                    break;
            }
        }
    }

    public void retroceder(View view) {
        onBackPressed();
    }

    @Override
    public void onRefresh() { //Método que detecta cuando se recarga la pantalla con SwipeRefreshLayout
        //Creamos una nueva instancia de "Handler", que está vinculada al Looper principal (el hilo principal de la aplicación). Esto asegura que cualquier operación realizada dentro de este Handler se ejecute en el hilo principal
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { //El "Handler" utiliza el método "postDelayed" para ejecutar el "Runnable" que contiene las acciones a realizar después de un retraso especificado (en este caso, 1500 milisegundos, es decir, 1.5 segundos)
            @Override
            public void run() {
                establecerElementos();
                swlRecargar.setRefreshing(false); //Llamamos a este método para detener la animación de refresco
            }
        }, 1500);
    }
}