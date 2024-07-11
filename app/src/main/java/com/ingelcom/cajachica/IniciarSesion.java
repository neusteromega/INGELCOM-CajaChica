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
    private FirestoreOperaciones oper = new FirestoreOperaciones();
    private Utilidades util = new Utilidades();

    /*@Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser(); //Obtiene el usuario actual

        //Verificamos que el usuario no sea null, si no lo es, que mande a la pantalla inicial dependiendo su rol
        if (currentUser != null){
            String correoInicial = currentUser.getEmail();
            util.redireccionarUsuario(IniciarSesion.this, correoInicial);
            //Utilidades.iniciarActivity(IniciarSesion.this, EmpMenuPrincipal.class, false);
        }
    }*/

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
            mAuth.signInWithEmailAndPassword(correo, contra)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pbAcceder.setVisibility(View.GONE); //Cuando se inicie sesión, que se oculte la ProgressBar
                            if (task.isSuccessful()) {
                                util.redireccionarUsuario(IniciarSesion.this, correo);

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

    /*public void redireccionarUsuario(String correoInicial) {
        oper.obtenerUnRegistro("usuarios", "Correo", correoInicial, new FirestoreOperaciones.FirestoreDocumentCallback() {
            @Override
            public void onCallback(Map<String, Object> documento) {
                if (documento != null) { //Si el HashMap "documento" no es nulo, quiere decir que si se encontró el registro en la colección, por lo tanto, entrará al if
                    String rol = (String) documento.get("Rol"); //Extraemos el rol del HashMap "documento"

                    if (rol.contentEquals("Administrador")) {
                        Utilidades.iniciarActivity(IniciarSesion.this, AdmPantallas.class, true);
                        //Toast.makeText(IniciarSesion.this, "BIENVENIDO", Toast.LENGTH_SHORT).show();
                    }
                    else if (rol.contentEquals("Empleado")) {
                        Utilidades.iniciarActivity(IniciarSesion.this, EmpMenuPrincipal.class, true);
                        //Toast.makeText(IniciarSesion.this, "BIENVENIDO", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(IniciarSesion.this, "ERROR AL OBTENER EL ROL DEL USUARIO", Toast.LENGTH_SHORT).show();
                    }
                }
                else { //Si "documento" es nulo, no se encontró el registro en la colección, y entrará en este else
                    Toast.makeText(IniciarSesion.this, "NO SE ENCONTRÓ EL USUARIO", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.w("Activity", "Error al obtener los roles.", e);
            }
        });
    }*/

    /*public void accederAdmin(View view) {
        Utilidades.iniciarActivity(this, AdmPantallas.class, false);
    }*/

    public void mostrarOcultarContra(View view) {
        clicks = Utilidades.mostrarOcultarContrasena(clicks, txtContra, imgContra); //Llamamos el método "mostrarOcultarContrasena" de la clase Utilidades que nos ayudará a mostrar y ocultar la contraseña
    }
}