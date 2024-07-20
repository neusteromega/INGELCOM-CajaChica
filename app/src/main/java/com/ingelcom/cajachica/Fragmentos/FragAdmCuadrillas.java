package com.ingelcom.cajachica.Fragmentos;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.ingelcom.cajachica.Adaptadores.CuadrillasAdapter;
import com.ingelcom.cajachica.AdmDatosCuadrilla;
import com.ingelcom.cajachica.DAO.Cuadrilla;
import com.ingelcom.cajachica.DAO.FirestoreOperaciones;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.ListadoIngresos;
import com.ingelcom.cajachica.Modelos.CuadrillasItems;
import com.ingelcom.cajachica.Modelos.GastosItems;
import com.ingelcom.cajachica.R;
import com.ingelcom.cajachica.RegistrarEditarIngreso;

import java.util.HashMap;
import java.util.List;

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
    private GridView gvCuadrillas;
    private CuadrillasAdapter customAdapter;

    private FirestoreOperaciones oper;

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

        oper = new FirestoreOperaciones(); //Creamos la instancia de la clase "FirestoreOperaciones"
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_adm_cuadrillas, container, false);

        Cuadrilla cuad = new Cuadrilla(getContext());

        gvCuadrillas = view.findViewById(R.id.gvCuadrillas);

        obtenerCuadrillas(cuad);

        /*//TEMPORAL
        dinero = view.findViewById(R.id.lblDineroDispCua);
        dinero.setOnClickListener(v -> {
            Utilidades.iniciarActivity(getActivity(), AdmDatosCuadrilla.class, false);
        });*/

        return view;
    }

    private void obtenerCuadrillas(Cuadrilla cuad) {
        try {
            cuad.obtenerCuadrillas(new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<CuadrillasItems>() {
                @Override
                public void onCallback(List<CuadrillasItems> items) {
                    inicializarGridView(items);
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), "ERROR AL CARGAR LAS CUADRILLAS", Toast.LENGTH_SHORT).show();
                    Log.w("ObtenerCuadrillas", e);
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerCuadrillas", e);
        }
    }

    private void inicializarGridView(List<CuadrillasItems> items) {
        customAdapter = new CuadrillasAdapter(items, getContext());
        gvCuadrillas.setAdapter(customAdapter);

        gvCuadrillas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap<String,Object> datos = new HashMap<>(); //Creamos un HashMap para guardar los datos que se enviarán al siguiente Activity

                //Agregamos las claves y datos al HashMap
                datos.put("Cuadrilla", items.get(i).getCuadrilla());
                datos.put("Dinero", items.get(i).getDinero());

                //Llamamos el método "iniciarActivityConDatos" de la clase Utilidades y le mandamos el contexto, el activity siguiente y el HashMap con los datos a enviar
                Utilidades.iniciarActivityConDatos(getActivity(), AdmDatosCuadrilla.class, datos);
            }
        });
    }
}