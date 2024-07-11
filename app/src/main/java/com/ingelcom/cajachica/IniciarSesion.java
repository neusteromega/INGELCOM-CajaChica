package com.ingelcom.cajachica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ingelcom.cajachica.DAO.FirestoreOperaciones;
import com.ingelcom.cajachica.Herramientas.Utilidades;

import java.util.Map;

public class IniciarSesion extends AppCompatActivity {

    //Variables para los componentes gráficos
    private EditText txtCorreo, txtContra;
    private ImageView imgContra;
    private TextView btnAcceder;
    private ProgressBar pbAcceder;

    private int clicks = 0; //Se utilizará en la parte de mostrar y ocultar la contraseña
    private FirebaseAuth mAuth; //Objeto que verifica la autenticación del usuario con Firebase
    private Utilidades util = new Utilidades();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        //Inicializamos la autenticación con Firebase
        mAuth = FirebaseAuth.getInstance();

        //Enlazamos las variables globales con los elementos gráficos
        txtCorreo = findViewById(R.id.txtCorreoLogin);
        txtContra = findViewById(R.id.txtContrasenaLogin);
        imgContra = findViewById(R.id.imgVerContrasenaLogin);
        btnAcceder = findViewById(R.id.btnAccederLogin);
        pbAcceder = findViewById(R.id.pbAccederLogin);
    }

    public void crearContrasena(View view) {
        Utilidades.iniciarActivity(this, CompletarUsuario.class, false);
    }

    public void acceder(View view) {
        pbAcceder.setVisibility(View.VISIBLE); //Ponemos visible la ProgressBar cuando se esté iniciando sesión

        //Guardamos el contenido de los EditText en las siguientes variables
        String correo = txtCorreo.getText().toString();
        String contra = txtContra.getText().toString();

        if (!correo.isEmpty() && !contra.isEmpty()) { //Si las cajas de texto no están vacías, que entre al if
            mAuth.signInWithEmailAndPassword(correo, contra) //Mandamos el correo y la contraseña y usamos este método de FirebaseAuth
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pbAcceder.setVisibility(View.GONE); //Cuando se inicie sesión, que se oculte la ProgressBar
                            if (task.isSuccessful()) { //Si el inicio de sesión fue exitoso, entrará en este if
                                Utilidades.redireccionarUsuario(IniciarSesion.this, correo); //Llamamos al método "redireccionarUsuario" de la clase Utilidades y le mandamos un contexto y el correo

                                //FirebaseUser user = mAuth.getCurrentUser();
                            } else {
                                Toast.makeText(IniciarSesion.this, "CORREO O CONTRASEÑA INCORRECTA", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else { //Si alguna caja de texto está vacía, que entre aquí y muestre un mensaje emergente de advertencia
            pbAcceder.setVisibility(View.GONE);
            Toast.makeText(this, "TODOS LOS CAMPOS DEBEN LLENARSE", Toast.LENGTH_SHORT).show();
        }
    }

    public void mostrarOcultarContra(View view) {
        clicks = Utilidades.mostrarOcultarContrasena(clicks, txtContra, imgContra); //Llamamos el método "mostrarOcultarContrasena" de la clase Utilidades que nos ayudará a mostrar y ocultar la contraseña
    }
}