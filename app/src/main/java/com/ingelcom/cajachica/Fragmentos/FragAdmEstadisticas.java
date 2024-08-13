package com.ingelcom.cajachica.Fragmentos;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ingelcom.cajachica.EstadisticasGastosIngresos;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.R;
import com.ingelcom.cajachica.RegistrarEditarGasto;
import com.ingelcom.cajachica.RegistrarEditarIngresoDeduccion;

public class FragAdmEstadisticas extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private LinearLayout btnIngreso, btnGasto;

    public FragAdmEstadisticas() {
        // Required empty public constructor
    }

    public static FragAdmEstadisticas newInstance(String param1, String param2) {
        FragAdmEstadisticas fragment = new FragAdmEstadisticas();
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
        View view =  inflater.inflate(R.layout.fragment_adm_estadisticas, container, false);

        btnIngreso = view.findViewById(R.id.LLIngresosEst);
        btnGasto = view.findViewById(R.id.LLGastosEst);

        //Eventos Clic de botones
        btnIngreso.setOnClickListener(v -> {
            //Redireccionamos al usuario al activity de "RegistrarEditarIngresoDeduccion" llamando el método utilitario "iniciarActivityConString" donde indicamos que el activity a mostrar será "RegistrarIngreso"
            Utilidades.iniciarActivityConString(getActivity(), EstadisticasGastosIngresos.class, "ActivityEGI", "Ingresos", false);
        });

        btnGasto.setOnClickListener(v -> {
            //Redireccionamos al usuario al activity de "RegistrarEditarGasto" llamando el método utilitario "iniciarActivityConString" donde indicamos que el activity a mostrar será "RegistrarGastoAdmin"
            Utilidades.iniciarActivityConString(getActivity(), EstadisticasGastosIngresos.class, "ActivityEGI", "Gastos", false);
        });

        return view;
    }
}