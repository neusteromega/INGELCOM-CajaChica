package com.ingelcom.cajachica.Fragmentos;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ingelcom.cajachica.AgregarEditarPerfil;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.IniciarSesion;
import com.ingelcom.cajachica.ListadoEmpleados;
import com.ingelcom.cajachica.Perfil;
import com.ingelcom.cajachica.R;

public class FragAdmUsuarios extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private LinearLayout btnMiPerfil, btnAgregarUsuario, btnListadoEmpleados, btnCerrarSesion;
    private TextView btnReintentarConexion;
    private View viewNoInternet;
    private ProgressBar pbReintentarConexion;

    private FirebaseAuth auth; //Objeto que verifica la autenticación del usuario con Firebase
    private FirebaseUser user; //Objeto que obtiene el usuario actual

    public FragAdmUsuarios() {
        // Required empty public constructor
    }

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
        btnCerrarSesion = view.findViewById(R.id.LLCerrarSesionUsu);
        viewNoInternet = view.findViewById(R.id.viewNoInternetUsu);
        btnReintentarConexion = view.findViewById(R.id.btnReintentarConexion);
        pbReintentarConexion = view.findViewById(R.id.pbReintentarConexion);

        ocultarBotonLogoutNoInternet();
        establecerBotones();

        return view;
    }

    private void establecerBotones() {
        //Eventos Clic de botones
        btnMiPerfil.setOnClickListener(v -> {
            //Redireccionamos al usuario al activity de "Perfil"
            Utilidades.iniciarActivityConString(getActivity(), Perfil.class, "ActivityPerfil", "PerfilAdmin", false);
        });

        btnAgregarUsuario.setOnClickListener(v -> {
            //Redireccionamos al usuario al activity de "AgregarEditarPerfil" y le indicamos que deberá reflejar el contenido de "AgregarUsuario"
            Utilidades.iniciarActivityConString(getActivity(), AgregarEditarPerfil.class, "ActivityAEP", "AgregarUsuario", false);
        });

        btnListadoEmpleados.setOnClickListener(v -> {
            //Redireccionamos al usuario al activity de "ListadoEmpleados"
            Utilidades.iniciarActivity(getActivity(), ListadoEmpleados.class, false);
        });

        btnCerrarSesion.setOnClickListener(v -> {
            //Creamos un nuevo "AlertDialog" que nos pregunte si deseamos cerrar sesión
            new AlertDialog.Builder(getActivity()).setTitle("CERRAR SESIÓN").setMessage("¿Está seguro que desea cerrar sesión?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { //Aquí se ejecutará una acción si el usuario seleccionó la opción de "Confirmar"
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseAuth.getInstance().signOut(); //Método de firebase que permite cerrar sesión
                            Utilidades.iniciarActivity(getActivity(), IniciarSesion.class, true); //Al confirmar el cierre de la sesión, mandamos al usuario a la pantalla de Login y finalizamos el activity actual
                        }
                    }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() { //Aquí se ejecutará una acción si el usuario seleccionó la opción de "Cancelar"
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.d("Mensaje", "Se canceló la acción"); //Como se canceló el cierre de la sesión, se muestra un mensaje en el Logcat indicando que se canceló la acción
                        }
                    }).show();
        });

        //Evento Click del botón "Reintentar" de la vista "viewNoInternet"
        btnReintentarConexion.setOnClickListener(v -> {
            pbReintentarConexion.setVisibility(View.VISIBLE); //Mostramos el ProgressBar

            //Creamos una nueva instancia de "Handler", que está vinculada al Looper principal (el hilo principal de la aplicación). Esto asegura que cualquier operación realizada dentro de este Handler se ejecute en el hilo principal
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { //El "Handler" utiliza el método "postDelayed" para ejecutar el "Runnable" que contiene las acciones a realizar después de un retraso especificado (en este caso, 1000 milisegundos, es decir, 1 segundo)
                @Override
                public void run() {
                    pbReintentarConexion.setVisibility(View.GONE); //Después de un segundo, ocultamos el ProgressBar
                }
            }, 1000);

            ocultarBotonLogoutNoInternet();
        });
    }

    //Método que permite ocultar el botón de Cerrar Sesión mientras se muestra la vista "view_nointernet"
    private void ocultarBotonLogoutNoInternet() {
        //Llamamos el método utilitario "mostrarMensajePorInternetCaidoBoolean" donde mandamos la vista "viewNoInternet" donde se hará visible cuando no haya conexión a internet y se ocultará cuando si haya. Este retorna un booleano indicando si hay internet o no
        boolean internetDisponible = Utilidades.mostrarMensajePorInternetCaidoBoolean(getContext(), viewNoInternet);

        if (internetDisponible) //Si el booleano guardado en "internetDisponible" es true, significa que si hay internet, entonces que entre al if
            btnCerrarSesion.setVisibility(View.VISIBLE); //Mostramos el botón de cerrar sesión
        else //Pero si es un false, significa que no hay internet, entonces que entre al else
            btnCerrarSesion.setVisibility(View.GONE); //Ocultamos el botón de cerrar sesión
    }
}