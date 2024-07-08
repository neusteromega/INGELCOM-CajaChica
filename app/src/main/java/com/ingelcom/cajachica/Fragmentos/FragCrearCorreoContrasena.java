package com.ingelcom.cajachica.Fragmentos;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ingelcom.cajachica.AdmPantallas;
import com.ingelcom.cajachica.EmpMenuPrincipal;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.IniciarSesion;
import com.ingelcom.cajachica.Perfil;
import com.ingelcom.cajachica.R;
import com.ingelcom.cajachica.RegistrarEditarIngreso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragCrearCorreoContrasena#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragCrearCorreoContrasena extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Variables para los componentes gráficos
    private EditText txtCorreo, txtContra, txtConfContra;
    private TextView btnConfirmar;
    private ProgressBar pbConfirmar;

    private FirebaseAuth mAuth; //Objeto que verifica la autenticación del usuario con Firebase

    public FragCrearCorreoContrasena() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragCrearContrasena.
     */
    // TODO: Rename and change types and number of parameters
    public static FragCrearCorreoContrasena newInstance(String param1, String param2) {
        FragCrearCorreoContrasena fragment = new FragCrearCorreoContrasena();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_crear_correocontrasena, container, false);

        //Inicializamos la autenticación con Firebase
        mAuth = FirebaseAuth.getInstance();

        //Enlazamos las variables globales con los elementos gráficos
        txtCorreo = view.findViewById(R.id.txtCorreoCCC);
        txtContra = view.findViewById(R.id.txtContrasenaCCC);
        txtConfContra = view.findViewById(R.id.txtConfContrasenaCCC);
        btnConfirmar = view.findViewById(R.id.btnConfirmarCCC);
        pbConfirmar = view.findViewById(R.id.pbConfirmarCCC);

        //Evento clic del botón Confirmar
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarUsuario();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser(); // Obtiene el usuario actual

        //CAMBIAR CUANDO SE ESTABLEZCAN LOS ROLES. Verificamos que el usuario no sea null, si no lo es, que mande a la pantalla inicial
        if (currentUser != null) {
            Utilidades.iniciarActivity(getActivity(), EmpMenuPrincipal.class, false);
        }
    }

    //Función en la que se creará el usuario con Firebase Auth
    private void registrarUsuario() {
        pbConfirmar.setVisibility(View.VISIBLE); //Ponemos visible la ProgressBar cuando se esté haciendo el registro del usuario al dar clic en "Confirmar"

        //Guardamos el contenido de los EditText en las siguientes variables
        String correo = txtCorreo.getText().toString();
        String contra = txtContra.getText().toString();
        String confContra = txtConfContra.getText().toString();

        if (!correo.isEmpty() && !contra.isEmpty() && !confContra.isEmpty()) { //Si las cajas de texto no están vacías, que entre al if
            if (confContra.contentEquals(contra)) { //Si los campos de contraseña y confirmar contraseña no coinciden, no podrá entrar al if
                //Esto lo proporciona Firebase incluído en el objeto "mAuth" de tipo "FirebaseAuth", y permite registrar un nuevo usuario, "correo" y "contra" son los registros proporcionados por el usuario
                mAuth.createUserWithEmailAndPassword(correo, contra)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                pbConfirmar.setVisibility(View.GONE); //Cuando el registro se haga, que se oculte la ProgressBar
                                if (task.isSuccessful()) { //Si el registro fue exitoso, entrará aquí
                                    //FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(getActivity(), "USUARIO REGISTRADO", Toast.LENGTH_SHORT).show();
                                    Utilidades.iniciarActivity(getActivity(), IniciarSesion.class, true);
                                }
                                else { //Si el registro falló, entrará aquí
                                    //CAMBIAR EL MENSAJE DE ERROR
                                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Error desconocido";
                                    Log.e("FragCrearCorreoContrasena", "Error al registrarse: " + errorMessage);
                                    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
            else { //Si las contraseñas son distintas, que entre aquí
                pbConfirmar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "LAS CONTRASEÑAS NO COINCIDEN", Toast.LENGTH_SHORT).show();
            }
        }
        else { //Si alguna caja de texto está vacía, que entre aquí y muestre un mensaje emergente de advertencia
            pbConfirmar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "TODOS LOS CAMPOS DEBEN LLENARSE", Toast.LENGTH_SHORT).show();
        }
    }
}