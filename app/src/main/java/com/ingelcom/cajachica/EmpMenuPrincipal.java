package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

    public void registrarGasto(View view) {
        Utilidades.iniciarActivity(this, RegistrarEditarGasto.class, false);
    }

    public void listadoGastos(View view) {
        Utilidades.iniciarActivity(this, ListadoGastos.class, false);
    }

    public void miPerfil(View view) {
        Utilidades.iniciarActivity(this, Perfil.class, false);
    }

    public void cerrarSesion(View view) {
        FirebaseAuth.getInstance().signOut(); //Método de firebase que permite cerrar sesión
        Utilidades.iniciarActivity(this, IniciarSesion.class, true); //Mandamos al usuario a la pantalla de Login y finalizamos el activity actual
    }
}