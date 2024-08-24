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
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.ingelcom.cajachica.DAO.FirestoreOperaciones;
import com.ingelcom.cajachica.Herramientas.Utilidades;

import java.util.Map;

public class IniciarSesion extends AppCompatActivity {

    //Variables para los componentes gráficos
    private EditText txtCorreo, txtContra;
    private ImageView imgContra;
    private TextView btnReintentarConexion;
    private ProgressBar pbAcceder;
    private View viewNoInternet;

    private int clicks = 0; //Se utilizará en la parte de mostrar y ocultar la contraseña
    private FirebaseAuth mAuth; //Objeto que verifica la autenticación del usuario con Firebase

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
        pbAcceder = findViewById(R.id.pbAccederLogin);
        viewNoInternet = findViewById(R.id.viewNoInternetLogin);
        btnReintentarConexion = findViewById(R.id.btnReintentarConexion);

        Utilidades.mostrarMensajePorInternetCaido(this, viewNoInternet); //Llamamos el método utilitario "mostrarMensajePorInternetCaido" donde mandamos la vista "viewNoInternet" donde se hará visible cuando no haya conexión a internet y se ocultará cuando si haya

        //Evento Click del botón "Reintentar" de la vista "viewNoInternet"
        btnReintentarConexion.setOnClickListener(v -> {
            Utilidades.mostrarMensajePorInternetCaido(this, viewNoInternet); //Llamamos el método utilitario "mostrarMensajePorInternetCaido" donde mandamos la vista "viewNoInternet" donde se hará visible cuando no haya conexión a internet y se ocultará cuando si haya
        });
    }

    @Override
    public void onBackPressed() { //Permite salir de la app al presionar el botón de retroceso
        finishAffinity(); //Cierra toda la pila de retroceso para que al dar clic en el botón de retroceso, ya no hayan activities y salga de la app
    }

    public void crearContrasena(View view) {
        Utilidades.iniciarActivity(this, CompletarUsuario.class, false);
    }

    public void acceder(View view) {
        pbAcceder.setVisibility(View.VISIBLE); //Ponemos visible la ProgressBar cuando se esté iniciando sesión

        //Guardamos el contenido de los EditText en las siguientes variables
        String correo = txtCorreo.getText().toString();
        String contra = txtContra.getText().toString();

        try {
            if (!correo.isEmpty() && !contra.isEmpty()) { //Si las cajas de texto no están vacías, que entre al if
                mAuth.signInWithEmailAndPassword(correo, contra) //Mandamos el correo y la contraseña y usamos este método de FirebaseAuth
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                pbAcceder.setVisibility(View.GONE); //Cuando se inicie sesión, que se oculte la ProgressBar
                                if (task.isSuccessful()) { //Si el inicio de sesión fue exitoso, entrará en este if
                                    Utilidades.redireccionarUsuario(IniciarSesion.this, correo); //Llamamos al método "redireccionarUsuario" de la clase Utilidades y le mandamos un contexto y el correo
                                    //FirebaseUser user = mAuth.getCurrentUser();
                                }
                                else {
                                    String mensajeError = "CORREO O CONTRASEÑA INCORRECTA"; //Variable que servirá para almacenar el mensaje de error correspondiente
                                    Exception exception = task.getException(); //Obtenemos la excepción lanzada tras que el proceso inicio de sesión ha fallado

                                    //Varios condicionales que revisan el tipo de excepción, y tras ello, se guarda el mensaje de error correspondiente en la variable "mensajeError"
                                    if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                        mensajeError = "CORREO O CONTRASEÑA INCORRECTA";
                                    }
                                    else if (exception instanceof FirebaseAuthInvalidUserException) {
                                        mensajeError = "EL USUARIO NO EXISTE O HA SIDO DESHABILITADO";
                                    }
                                    else if (exception instanceof FirebaseNetworkException) {
                                        mensajeError = "ERROR DE RED, VERIFIQUE SU CONEXIÓN A INTERNET";
                                    }
                                    else if (exception instanceof FirebaseTooManyRequestsException) {
                                        mensajeError = "DEMASIADOS INTENTOS FALLIDOS, INTENTE DE NUEVO MÁS TARDE";
                                    }

                                    Log.e("IniciarSesion", "Error al iniciar sesión: " + exception.getMessage()); //Mostramos el mensaje de error en el Logcat
                                    Toast.makeText(IniciarSesion.this, mensajeError, Toast.LENGTH_SHORT).show(); //Mostramos el mensaje de error con un Toast
                                }
                            }
                        });
            }
            else { //Si alguna caja de texto está vacía, que entre aquí y muestre un mensaje emergente de advertencia
                pbAcceder.setVisibility(View.GONE);
                Toast.makeText(this, "TODOS LOS CAMPOS DEBEN LLENARSE", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            Log.w("IniciarSesion", e);
        }
    }

    public void mostrarOcultarContra(View view) {
        clicks = Utilidades.mostrarOcultarContrasena(clicks, txtContra, imgContra); //Llamamos el método "mostrarOcultarContrasena" de la clase Utilidades que nos ayudará a mostrar y ocultar la contraseña
    }
}