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

public class FragAdmEstadisticas extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private LinearLayout btnIngreso, btnGasto;

    public FragAdmEstadisticas() {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_adm_estadisticas, container, false); //Guardamos la vista inflada del fragment en una variable tipo "view"

        //Enlazamos las variables con los elementos gráficos
        btnIngreso = view.findViewById(R.id.LLIngresosEst);
        btnGasto = view.findViewById(R.id.LLGastosEst);

        //Eventos clic de botones
        btnIngreso.setOnClickListener(v -> {
            //Redireccionamos al usuario al activity de "EstadisticasGastosIngresos" llamando el método utilitario "iniciarActivityConString" donde indicamos que las estadísticas a mostrar serán los "Ingresos"
            Utilidades.iniciarActivityConString(getActivity(), EstadisticasGastosIngresos.class, "ActivityEGI", "Ingresos", false);
        });

        btnGasto.setOnClickListener(v -> {
            //Redireccionamos al usuario al activity de "EstadisticasGastosIngresos" llamando el método utilitario "iniciarActivityConString" donde indicamos que las estadísticas a mostrar serán los "Gastos"
            Utilidades.iniciarActivityConString(getActivity(), EstadisticasGastosIngresos.class, "ActivityEGI", "Gastos", false);
        });

        return view;
    }
}