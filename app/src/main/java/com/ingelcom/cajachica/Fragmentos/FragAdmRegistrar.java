package com.ingelcom.cajachica.Fragmentos;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.Perfil;
import com.ingelcom.cajachica.R;
import com.ingelcom.cajachica.RegistrarEditarGasto;
import com.ingelcom.cajachica.RegistrarEditarIngreso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragAdmRegistrar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragAdmRegistrar extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LinearLayout btnIngreso, btnGasto;

    public FragAdmRegistrar() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragAdmRegistrar.
     */
    // TODO: Rename and change types and number of parameters
    public static FragAdmRegistrar newInstance(String param1, String param2) {
        FragAdmRegistrar fragment = new FragAdmRegistrar();
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
        View view = inflater.inflate(R.layout.fragment_adm_registrar, container, false);

        btnIngreso = view.findViewById(R.id.LLIngresoReg);
        btnGasto = view.findViewById(R.id.LLGastoReg);

        //Eventos Clic de botones
        btnIngreso.setOnClickListener(v -> {
            //Redireccionamos al usuario al activity de "RegistrarEditarIngreso" llamando el método utilitario "iniciarActivityConString" donde indicamos que el activity a mostrar será "RegistrarIngreso"
            Utilidades.iniciarActivityConString(getActivity(), RegistrarEditarIngreso.class, "ActivityREI", "RegistrarIngreso", false);
        });

        btnGasto.setOnClickListener(v -> {
            //Redireccionamos al usuario al activity de "RegistrarEditarGasto" llamando el método utilitario "iniciarActivity"
            Utilidades.iniciarActivity(getActivity(), RegistrarEditarGasto.class, false);
        });

        return view;
    }
}