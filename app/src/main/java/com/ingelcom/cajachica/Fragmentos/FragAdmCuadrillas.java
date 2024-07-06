package com.ingelcom.cajachica.Fragmentos;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ingelcom.cajachica.AdmDatosCuadrilla;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.ListadoIngresos;
import com.ingelcom.cajachica.R;
import com.ingelcom.cajachica.RegistrarEditarIngreso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragAdmCuadrillas#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragAdmCuadrillas extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView dinero;

    public FragAdmCuadrillas() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragAdmCuadrillas.
     */
    // TODO: Rename and change types and number of parameters
    public static FragAdmCuadrillas newInstance(String param1, String param2) {
        FragAdmCuadrillas fragment = new FragAdmCuadrillas();
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
        View view = inflater.inflate(R.layout.fragment_adm_cuadrillas, container, false);

        //TEMPORAL
        dinero = view.findViewById(R.id.lblDineroDispCua);
        dinero.setOnClickListener(v -> {
            Utilidades.iniciarActivity(getActivity(), AdmDatosCuadrilla.class, false);
        });

        return view;
    }
}