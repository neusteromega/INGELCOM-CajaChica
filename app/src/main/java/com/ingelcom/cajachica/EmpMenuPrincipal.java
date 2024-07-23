package com.ingelcom.cajachica;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ingelcom.cajachica.DAO.Cuadrilla;
import com.ingelcom.cajachica.DAO.Usuario;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;

import java.util.HashMap;
import java.util.Map;

public class EmpMenuPrincipal extends AppCompatActivity {

    private TextView lblDinero;
    private String dineroDisponible;

    private FirebaseAuth auth; //Objeto que verifica la autenticación del usuario con Firebase
    private FirebaseUser user; //Objeto que obtiene el usuario actual

    private Usuario usu = new Usuario(EmpMenuPrincipal.this);
    private Cuadrilla cuad = new Cuadrilla(EmpMenuPrincipal.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_menu_principal);

        lblDinero = findViewById(R.id.lblCantDineroMenuEMP);

        auth = FirebaseAuth.getInstance(); //Inicializamos la autenticación con Firebase
        user = auth.getCurrentUser(); //Obtenemos el usuario actual usando "auth"

        if (user == null) { //Si el usuario es null, que entre al if
            //Mandamos al usuario a la pantalla de Login si su usuario ha sido nulo, y finalizamos el activity actual
            Utilidades.iniciarActivity(this, IniciarSesion.class, true);
        }

        obtenerCuadrillaUsuario(); //Llamamos el método obtenerCuadrillaUsuario de abajo
    }

    @Override
    public void onBackPressed() { //Permite salir de la app al presionar el botón de retroceso
        finishAffinity(); //Cierra toda la pila de retroceso para que al dar clic en el botón de retroceso, ya no hayan activities y salga de la app
    }

    //Método que nos ayuda a obtener la cuadrilla del usuario actual
    private void obtenerCuadrillaUsuario() {
        try {
            //Llamamos el método "obtenerUnUsuario" de la clase "Usuario" y creamos una invocación a la interfaz "FirestoreDocumentCallback"
            usu.obtenerUsuarioActual(new FirestoreCallbacks.FirestoreDocumentCallback() {
                @Override
                public void onCallback(Map<String, Object> documento) {
                    if (documento != null) { //Si "documento" no es nulo, quiere decir que si encontró el usuario
                        String cuadrilla = (String) documento.get("Cuadrilla"); //Obtenemos la cuadrilla del documento y la guardamos en "cuadrilla"

                        obtenerDineroCuadrilla(cuadrilla); //Llamamos el método "obtenerDineroCuadrilla" que está abajo y le mandamos el nombre de la cuadrilla
                    }
                    else { //Si "documento" es nulo, no se encontró el usuario en la colección, y entrará en este else
                        Log.w("ObtenerUsuario", "Usuario no encontrado");
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.w("BuscarUsuario", "Error al obtener el usuario", e);
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerUsuario", e);
        }
    }

    //Método que nos permite obtener el dinero disponible de una cuadrilla
    private void obtenerDineroCuadrilla(String cuadrilla) {
        try {
            //Llamamos el método "obtenerUnaCuadrilla" de la clase "Cuadrilla" y creamos una invocación a la interfaz "FirestoreDocumentCallback"
            cuad.obtenerUnaCuadrilla(cuadrilla, new FirestoreCallbacks.FirestoreDocumentCallback() {
                @Override
                public void onCallback(Map<String, Object> documento) {
                    if (documento != null) { //Si "documento" no es nulo, quiere decir que encontró la cuadrilla
                        //Obtenemos el valor guardado en el campo "Dinero" de la colección "cuadrillas" y lo guardamos en la variable Object llamada "valor"
                        Object valor = documento.get("Dinero"); //Lo obtenemos como Object ya que Firestore puede almacenar números en varios formatos (por ejemplo, Long y Double) y esto puede causar problemas con el casting del contenido del campo
                        double dinero = Utilidades.convertirObjectADouble(valor); //Llamamos el método utilitario "convertirObjectADouble" y le mandamos el objeto "valor", y nos retorna este objeto ya convertido a double y lo guardamos en "dinero"
                        dineroDisponible = String.format("%.2f", dinero); //Convertimos el double "dinero" a String en formato para que tenga dos décimales, y este lo guardamos en "dineroDisponible"

                        lblDinero.setText("L. " + dineroDisponible); //Asignamos el String "dineroDisponible" al TextView "lblDinero"
                    }
                    else { //Si "documento" es nulo, no se encontró el usuario en la colección, y entrará en este else
                        Log.w("ObtenerCuadrilla", "Cuadrilla no encontrada");
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.w("BuscarCuadrilla", "Error al obtener la cuadrilla", e);
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerUsuario", e);
        }
    }

    //Eventos Click de los botones del menú
    public void registrarGasto(View view) {
        HashMap<String,Object> datos = new HashMap<>(); //Creamos un HashMap para guardar los datos que se enviarán al siguiente Activity

        //Agregamos las claves y datos al HashMap
        datos.put("ActivityREG", "RegistrarGastoEmpleado");
        datos.put("DineroDisponible", dineroDisponible);

        //Abrimos el activity "RegistrarEditarGasto" y le mandamos el hashMap "datos"
        Utilidades.iniciarActivityConDatos(EmpMenuPrincipal.this, RegistrarEditarGasto.class, datos);
    }

    public void listadoGastos(View view) {
        Utilidades.iniciarActivity(this, ListadoGastos.class, false);
    }

    public void miPerfil(View view) {
        Utilidades.iniciarActivityConString(this, Perfil.class, "ActivityPerfil", "PerfilEmpleado", false); //Enviamos el texto "PerfilEmpleado" que ayudará a saber que el Activity "Perfil" debe mostrar los datos del Empleado actual
    }

    public void cerrarSesion(View view) {
        //Creamos un nuevo "AlertDialog" que nos pregunte si deseamos cerrar sesión
        new AlertDialog.Builder(this).setTitle("CERRAR SESIÓN").setMessage("¿Está seguro que desea cerrar sesión?")
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { //Aquí se ejecutará una acción si el usuario seleccionó la opción de "Confirmar"
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    FirebaseAuth.getInstance().signOut(); //Método de firebase que permite cerrar sesión
                    Utilidades.iniciarActivity(EmpMenuPrincipal.this, IniciarSesion.class, true); //Al confirmar el cierre de la sesión, mandamos al usuario a la pantalla de Login y finalizamos el activity actual
                }
            }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() { //Aquí se ejecutará una acción si el usuario seleccionó la opción de "Cancelar"
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("Mensaje", "Se canceló la acción"); //Como se canceló el cierre de la sesión, se muestra un mensaje en el Logcat indicando que se canceló la acción
                    }
            }).show();
    }
}