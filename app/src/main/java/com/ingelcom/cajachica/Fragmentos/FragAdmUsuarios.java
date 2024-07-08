package com.ingelcom.cajachica.Fragmentos;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ingelcom.cajachica.AgregarEditarPerfil;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.ListadoEmpleados;
import com.ingelcom.cajachica.ListadoIngresos;
import com.ingelcom.cajachica.Perfil;
import com.ingelcom.cajachica.R;
import com.ingelcom.cajachica.RegistrarEditarGasto;
import com.ingelcom.cajachica.RegistrarEditarIngreso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragAdmUsuarios#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragAdmUsuarios extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LinearLayout btnMiPerfil, btnAgregarUsuario, btnListadoEmpleados, btnCerrarSesión;

    private FirebaseAuth auth; //Objeto que verifica la autenticación del usuario con Firebase
    private FirebaseUser user; //Objeto que obtiene el usuario actual

    public FragAdmUsuarios() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragAdmUsuarios.
     */
    // TODO: Rename and change types and number of parameters
    public static FragAdmUsuarios newInstance(String param1, String param2) {
        FragAdmUsuarios fragment = new FragAdmUsuarios();
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
        View view = inflater.inflate(R.layout.fragment_adm_usuarios, container, false);

        //Inicializamos la autenticación con Firebase
        auth = FirebaseAuth.getInstance();

        //Enlazamos las variables globales con los elementos gráficos
        btnMiPerfil = view.findViewById(R.id.LLMiPerfilUsu);
        btnAgregarUsuario = view.findViewById(R.id.LLAgregarUsuarioUsu);
        btnListadoEmpleados = view.findViewById(R.id.LLListadoEmpleadosUsu);
        btnCerrarSesión = view.findViewById(R.id.LLCerrarSesionUsu);

        //Eventos Clic de botones
        btnMiPerfil.setOnClickListener(v -> {
            //Redireccionamos al usuario al activity de "Perfil"
            Utilidades.iniciarActivity(getActivity(), Perfil.class, false);
        });

        btnAgregarUsuario.setOnClickListener(v -> {
            //Redireccionamos al usuario al activity de "AgregarEditarPerfil" y le indicamos que deberá reflejar el contenido de "AgregarUsuario"
            //Utilidades.iniciarActivity(getActivity(), AgregarEditarPerfil.class, false);
            Utilidades.iniciarActivityConDatos(getActivity(), AgregarEditarPerfil.class, "Activity", "AgregarUsuario");
        });

        btnListadoEmpleados.setOnClickListener(v -> {
            //Redireccionamos al usuario al activity de "ListadoEmpleados"
            Utilidades.iniciarActivity(getActivity(), ListadoEmpleados.class, false);
        });

        return view;
    }
}