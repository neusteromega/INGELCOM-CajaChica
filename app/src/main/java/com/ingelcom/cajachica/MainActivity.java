package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ingelcom.cajachica.Herramientas.Utilidades;

public class MainActivity extends AppCompatActivity {

    //private FirebaseAuth mAuth; //Objeto que verifica la autenticación del usuario con Firebase
    private boolean splash = true; //Nos servirá para saber si redireccionamos al usuario a "IniciarSesion"

    @Override
    public void onStart() {
        super.onStart();
        //mAuth = FirebaseAuth.getInstance(); //Instanciamos el "mAuth"
        //FirebaseUser currentUser = mAuth.getCurrentUser(); //Obtenemos el usuario actual
        FirebaseUser currentUser = Utilidades.obtenerUsuario(); //Obtenemos el usuario actual llamando el método utilitario "obtenerUsuario"

        //Verificamos que el usuario no sea null
        if (currentUser != null){
            splash = false; //Convertimos el "splash" a false, para que no redireccione al usuario a "IniciarSesion"
            String correoInicial = currentUser.getEmail(); //Guardamos el email del usuario en la variable "correoInicial"
            Utilidades.redireccionarUsuario(MainActivity.this, correoInicial); //Llamamos el método "redireccionarUsuario" de la clase Utilidades y le mandamos un contexto y el correo del usuario actual
            //Utilidades.iniciarActivity(IniciarSesion.this, EmpMenuPrincipal.class, false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Nos permite crear la pantalla Splash de inicio
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (splash) { //Verificamos si "splash" es verdadero, si lo es, significa que el "currentUser" es nulo, por lo tanto, que redireccione al usuario a "IniciarSesion"
                    //Llamamos el método "iniciarActivity" para que redireccione al Activity de "IniciarSesion" y mandamos "true" para que cierre la actividad actual
                    Utilidades.iniciarActivity(MainActivity.this, IniciarSesion.class, true);
                }
            }
        }, 2500); //Establecemos que la pantalla splash debe durar 2.5 segundos
    }
}