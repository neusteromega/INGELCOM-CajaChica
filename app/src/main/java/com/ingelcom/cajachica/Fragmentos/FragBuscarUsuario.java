package com.ingelcom.cajachica.Fragmentos;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ingelcom.cajachica.DAO.FirestoreOperaciones;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Validaciones;
import com.ingelcom.cajachica.R;

import java.util.Map;

public class FragBuscarUsuario extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FirestoreOperaciones oper;
    private EditText txtIdentidad;
    private TextView btnBuscar;

    public FragBuscarUsuario() {
        // Required empty public constructor
    }

    public static FragBuscarUsuario newInstance(String param1, String param2) {
        FragBuscarUsuario fragment = new FragBuscarUsuario();
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

        oper = new FirestoreOperaciones(); //Creamos la instancia de la clase "FirestoreOperaciones"
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buscar_usuario, container, false); //Guardar la vista inflada del fragment en una variable tipo "view"

        //Enlazamos los componentes gráficos a la variable global
        btnBuscar = view.findViewById(R.id.btnBuscarBC);
        txtIdentidad = view.findViewById(R.id.txtIdentidadBC);

        //Evento OnClick del botón de buscar
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarUsuario(v);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    //Método que permite buscar un usuario en Firestore mediante su identidad. Recibe un "View" que será necesario en la parte de "Navigation" entre fragments
    private void buscarUsuario(View vista) {
        String identidad = txtIdentidad.getText().toString(); //Extraemos el contenido de "txtIdentidad" y lo guardamos en la variable "identidad"

        if (!identidad.isEmpty()) { //Entrará al if si la identidad no está vacía
            if (Validaciones.validarIdentidad(identidad)) {
                //Llamamos al método "obtenerUnRegistro" de la clase FirestoreOperaciones. Le mandamos el nombre de la colección, el campo, el dato a buscar e invocamos la interfaz "FirestoreDocumentCallback"
                oper.obtenerUnRegistro("usuarios", "Identidad", identidad, new FirestoreCallbacks.FirestoreDocumentCallback() {
                    @Override
                    public void onCallback(Map<String, Object> documento) {
                        if (documento != null) { //Si el HashMap "documento" no es nulo, quiere decir que si se encontró el registro en la colección, por lo tanto, entrará al if
                            String correo = (String) documento.get("Correo"); //Extraemos el correo del HashMap "documento"

                            if (correo.isEmpty()) { //Si "correo está vacío, quiere decir que el usuario aún no ha registrado un correo y una contraseña, entonces que entre al if
                                FragCrearCorreoContrasena.identidadUsuario = identidad; //Mandamos la identidad a la variable global estática "identidadUsuario" en el FragCrearCorreoContrasena
                                Navigation.findNavController(vista).navigate(R.id.actionBuscarUsuario_CrearContrasena); //Indicamos que al dar clic en el botón Buscar, que redireccione al usuario usando la acción "actionBuscarUsuario_CrearContrasena" que establecimos en el fragment_buscar_usuario en el "nav_graphcrearcontrasena
                            }
                            else { //En cambio, si "correo" no está vacío, significa que el usuario ya creó un correo y una contraseña, entonces se le muestra un mensaje indicando eso
                                Toast.makeText(getActivity(), "EL USUARIO YA CUENTA CON CORREO Y CONTRASEÑA", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else { //Si "documento" es nulo, no se encontró el registro en la colección, y entrará en este else
                            Toast.makeText(getActivity(), "NO SE ENCONTRÓ EL USUARIO", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.w("Buscar Documento", "Error al obtener el documento", e);
                    }
                });
            }
            else
                Toast.makeText(getActivity(), "LA IDENTIDAD DEBE TENER 13 NÚMEROS", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getActivity(), "INGRESE UN NÚMERO DE IDENTIDAD SIN GUIONES", Toast.LENGTH_SHORT).show();
    }
}