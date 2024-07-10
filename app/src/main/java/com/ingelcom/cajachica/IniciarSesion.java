package com.ingelcom.cajachica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ingelcom.cajachica.Herramientas.Utilidades;

public class IniciarSesion extends AppCompatActivity {

    //Variables para los componentes gráficos
    private EditText txtCorreo, txtContra;
    private TextView btnAcceder;
    private ProgressBar pbAcceder;

    private FirebaseAuth mAuth; //Objeto que verifica la autenticación del usuario con Firebase

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser(); //Obtiene el usuario actual

        //CAMBIAR CUANDO SE ESTABLEZCAN LOS ROLES. Verificamos que el usuario no sea null, si no lo es, que mande a la pantalla inicial
        if (currentUser != null){
            Utilidades.iniciarActivity(IniciarSesion.this, EmpMenuPrincipal.class, false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        //Inicializamos la autenticación con Firebase
        mAuth = FirebaseAuth.getInstance();

        //Enlazamos las variables globales con los elementos gráficos
        txtCorreo = findViewById(R.id.txtCorreoLogin);
        txtContra = findViewById(R.id.txtContrasenaLogin);
        btnAcceder = findViewById(R.id.btnAccederLogin);
        pbAcceder = findViewById(R.id.pbAccederLogin);
    }

    public void crearContrasena(View view) {
        Utilidades.iniciarActivity(this, CompletarUsuario.class, false);
    }

    public void acceder(View view) {
        //Utilidades.iniciarActivity(this, EmpMenuPrincipal.class, false);

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
                                //FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(IniciarSesion.this, "BIENVENIDO", Toast.LENGTH_SHORT).show();
                                Utilidades.iniciarActivity(IniciarSesion.this, EmpMenuPrincipal.class, true);
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

    public void accederAdmin(View view) {
        Utilidades.iniciarActivity(this, AdmPantallas.class, false);
    }
}