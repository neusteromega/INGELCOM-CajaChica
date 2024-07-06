package com.ingelcom.cajachica.Fragmentos;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ingelcom.cajachica.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragBuscarUsuario#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragBuscarUsuario extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragBuscarUsuario() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragBuscarUsuario.
     */
    // TODO: Rename and change types and number of parameters
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buscar_usuario, container, false); //Guardar la vista inflada del fragment en una variable tipo "view"
        TextView btnBuscar = view.findViewById(R.id.btnBuscarBC); //Enlazamos el botón de Buscar (que en sí es un textview) a la variable de tipo "Button"

        //Evento OnClick del botón de buscar
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.actionBuscarUsuario_CrearContrasena); //Indicamos que al dar clic en el botón Buscar, que redireccione al usuario usando la acción "actionBuscarUsuario_CrearContrasena" que establecimos en el fragment_buscar_usuario en el "nav_graphcrearcontrasena
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}