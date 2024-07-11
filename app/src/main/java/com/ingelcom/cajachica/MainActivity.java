package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ingelcom.cajachica.Herramientas.Utilidades;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth; //Objeto que verifica la autenticación del usuario con Firebase
    private Utilidades util = new Utilidades();
    private boolean splash = true;

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser(); //Obtiene el usuario actual

        //Verificamos que el usuario no sea null, si no lo es, que mande a la pantalla inicial dependiendo su rol
        if (currentUser != null){
            splash = false;
            String correoInicial = currentUser.getEmail();
            util.redireccionarUsuario(MainActivity.this, correoInicial);
            //Utilidades.iniciarActivity(IniciarSesion.this, EmpMenuPrincipal.class, false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (splash) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (splash) {
                        //Llamamos el método "iniciarActivity" para que redireccione al Activity de "IniciarSesion" y mandamos "true" para que cierre la actividad actual
                        Utilidades.iniciarActivity(MainActivity.this, IniciarSesion.class, true);
                    }
                }
            }, 3000);
        }
    }
}