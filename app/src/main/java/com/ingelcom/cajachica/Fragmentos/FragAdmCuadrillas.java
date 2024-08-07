package com.ingelcom.cajachica.Fragmentos;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.ingelcom.cajachica.Adaptadores.CuadrillasAdapter;
import com.ingelcom.cajachica.AdmDatosCuadrilla;
import com.ingelcom.cajachica.DAO.Cuadrilla;
import com.ingelcom.cajachica.DAO.FirestoreOperaciones;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.Modelos.CuadrillasItems;
import com.ingelcom.cajachica.R;

import java.util.HashMap;
import java.util.List;

public class FragAdmCuadrillas extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ImageView btnOrdenar;
    private GridView gvCuadrillas;
    private CuadrillasAdapter customAdapter; //Objeto de la clase "CuadrillasAdapter"
    private List<CuadrillasItems> items;
    private FirestoreOperaciones oper;

    public FragAdmCuadrillas() {
        // Required empty public constructor
    }

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
        Cuadrilla cuad = new Cuadrilla(getContext()); //Ponemos la instancia de la clase "Cuadrilla" aquí y no de forma global ya que el "getContext()" se genera cuando se crea el fragment, aquí en "onCreateView"

        gvCuadrillas = view.findViewById(R.id.gvCuadrillas);
        btnOrdenar = view.findViewById(R.id.imgOrdenarCua);
        obtenerCuadrillas(cuad); //Llamamos el método "obtenerCuadrillas" de abajo y le mandamos el objeto "cuad"

        btnOrdenar.setOnClickListener(v -> {
            try {
                PopupMenu popup = new PopupMenu(getActivity(), v); //Objeto de tipo "PopupMenu"
                popup.setOnMenuItemClickListener(this); //Indicamos que asigne el evento "OnMenuItemClick" para que haga algo cada vez que se dé click a una opción del menú
                popup.inflate(R.menu.popupmenu_filtradocuadrillas); //Inflamos la vista del menú indicando la ruta de dicha vista gráfica

                popup.show(); //Mostramos el menú ya inflado
            }
            catch (Exception e) {
                Log.w("MenuOrdenar", e);
            }
        });

        return view;
    }

    //Método que nos ayuda a obtener las cuadrillas de la colección "cuadrillas" de Firestore y asignarlas al RecyclerView
    private void obtenerCuadrillas(Cuadrilla cuad) {
        try {
            //Llamamos el método "obtenerCuadrillas" de la clase "Cuadrilla" donde se obtienen todas las cuadrillas, e invocamos a la interfaz "FirestoreAllSpecialDocumentsCallback" y le decimos que debe recibir un "CuadrillasItems"
            cuad.obtenerCuadrillas(new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<CuadrillasItems>() {
                @Override
                public void onCallback(List<CuadrillasItems> lista) { //En esta lista "items" están todos las cuadrillas y sus datos (Nombre y Dinero)
                    if (lista != null) //Si "items" no es null, que entre al if
                        items = lista; //Inicializamos la lista global "items" con la "lista" de cuadrillas extraída de Firestore
                        inicializarGridView(lista); //Llamamos el método "inicializarGridView" y le mandamos la lista "items"
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
        customAdapter = new CuadrillasAdapter(items, getContext()); //Creamos una nueva instancia de la clase "CuadrillasAdapter" en donde mandamos la lista "items" y un contexto, y esta instancia la igualamos al objeto "customAdapter"
        gvCuadrillas.setAdapter(customAdapter); //Asignamos el customAdapter al gridView de cuadrillas

        gvCuadrillas.setOnItemClickListener(new AdapterView.OnItemClickListener() { //Al GridView "gvCuadrillas" le creamos un evento "setOnItemClickListener" que detecta cuando se la da clic en un item del GridView
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap<String,Object> datos = new HashMap<>(); //Creamos un HashMap para guardar los datos que se enviarán al siguiente Activity
                String dineroDisponible = String.format("%.2f", items.get(i).getDinero()); //Convertimos el double "items.get(i).getDinero()" a String en formato para que tenga dos décimales, y este lo guardamos en "dineroDisponible"

                //Agregamos las claves y datos al HashMap
                datos.put("Cuadrilla", items.get(i).getCuadrilla());
                datos.put("DineroDisponible", dineroDisponible);

                //Llamamos el método "iniciarActivityConDatos" de la clase Utilidades y le mandamos el contexto, el activity siguiente y el HashMap con los datos a enviar
                Utilidades.iniciarActivityConDatos(getActivity(), AdmDatosCuadrilla.class, datos);
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) { //Parte lógica de lo que queremos que haga cada opción del popup menú
        switch (menuItem.getItemId()) {
            case R.id.menuOrdenAlfabetico:
                items = Utilidades.ordenarListaPorAlfabetico(items, "cuadrilla", "Ascendente"); //Llamamos el método utilitario "ordenarListaPorAlfabetico". Le mandamos la lista "items", el nombre del campo String "cuadrilla", y el tipo de orden "Ascendente". Este método retorna la lista ya ordenada y la guardamos en "items"
                customAdapter.notifyDataSetChanged(); //Esto indica al "customAdapter" que sus datos han sido cambiados
                return true;

            case R.id.menuMenosDinero:
                items = Utilidades.ordenarListaPorDouble(items, "dinero", "Ascendente"); //Llamamos el método utilitario "ordenarListaPorDouble". Le mandamos la lista "items", el nombre del campo double "dinero", y el tipo de orden "Ascendente". Este método retorna la lista ya ordenada y la guardamos en "items"
                customAdapter.notifyDataSetChanged(); //Esto indica al "customAdapter" que sus datos han sido cambiados
                return true;

            case R.id.menuMasDinero:
                items = Utilidades.ordenarListaPorDouble(items, "dinero", "Descendente"); //Llamamos el método utilitario "ordenarListaPorDouble". Le mandamos la lista "items", el nombre del campo double "dinero", y el tipo de orden "Descendente". Este método retorna la lista ya ordenada y la guardamos en "items"
                customAdapter.notifyDataSetChanged(); //Esto indica al "customAdapter" que sus datos han sido cambiados
                return true;

            default:
                return false;
        }
    }
}