package com.ingelcom.cajachica;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ingelcom.cajachica.Herramientas.Utilidades;

public class EmpMenuPrincipal extends AppCompatActivity {

    private FirebaseAuth auth; //Objeto que verifica la autenticación del usuario con Firebase
    private FirebaseUser user; //Objeto que obtiene el usuario actual
    //private TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_menu_principal);

        auth = FirebaseAuth.getInstance(); //Inicializamos la autenticación con Firebase
        user = auth.getCurrentUser(); //Obtenemos el usuario actual usando "auth"
        //email = findViewById(R.id.lblDineroMenuEMP);

        if (user == null) { //Si el usuario es null, que entre al if
            //Mandamos al usuario a la pantalla de Login si su usuario ha sido nulo, y finalizamos el activity actual
            Utilidades.iniciarActivity(this, IniciarSesion.class, true);
        }
        else {
            //email.setText(user.getEmail());
        }
    }

    @Override
    public void onBackPressed() { //Permite salir de la app al presionar el botón de retroceso
        finishAffinity(); //Cierra toda la pila de retroceso para que al dar clic en el botón de retroceso, ya no hayan activities y salga de la app
    }

    //Eventos Click de los botones del menú
    public void registrarGasto(View view) {
        Utilidades.iniciarActivityConString(this, RegistrarEditarGasto.class, "ActivityREG", "RegistrarGastoEmpleado", false);
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