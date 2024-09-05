package com.ingelcom.cajachica.Fragmentos;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
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
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class FragAdmCuadrillas extends Fragment implements SwipeRefreshLayout.OnRefreshListener, PopupMenu.OnMenuItemClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView btnReintentarConexion;
    private ImageView btnOrdenar;
    private GridView gvCuadrillas;
    private SwipeRefreshLayout swlRecargar;
    private View viewNoInternet;
    private ProgressBar pbReintentarConexion;
    private CuadrillasAdapter customAdapter; //Objeto de la clase "CuadrillasAdapter"
    private List<CuadrillasItems> items;
    private Cuadrilla cuad;
    private String tipoOrden = "";

    public FragAdmCuadrillas() {

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adm_cuadrillas, container, false); //Guardamos la vista inflada del fragment en una variable tipo "view"

        //Enlazamos las variables globales con los elementos gráficos
        gvCuadrillas = view.findViewById(R.id.gvCuadrillas);
        btnOrdenar = view.findViewById(R.id.imgOrdenarCua);
        swlRecargar = view.findViewById(R.id.swipeRefreshLayoutCua);
        viewNoInternet = view.findViewById(R.id.viewNoInternetCua);
        btnReintentarConexion = view.findViewById(R.id.btnReintentarConexion);
        pbReintentarConexion = view.findViewById(R.id.pbReintentarConexion);

        cuad = new Cuadrilla(getContext()); //Ponemos la instancia de la clase "Cuadrilla" aquí y no de forma global ya que el "getContext()" se genera cuando se crea el fragment, aquí en "onCreateView"

        swlRecargar.setOnRefreshListener(this); //Llamada al método "onRefresh"
        swlRecargar.setColorSchemeResources(R.color.clr_fuente_primario); //Color del SwipeRefreshLayout

        Utilidades.mostrarMensajePorInternetCaido(getContext(), viewNoInternet); //Llamamos el método utilitario "mostrarMensajePorInternetCaido" donde mandamos la vista "viewNoInternet" donde se hará visible cuando no haya conexión a internet y se ocultará cuando si haya
        desactivarSwipeGridView();
        obtenerCuadrillas();

        //Evento clic del botón "Ordenar"
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

        //Evento clic del botón "Reintentar" de la vista "viewNoInternet"
        btnReintentarConexion.setOnClickListener(v -> {
            pbReintentarConexion.setVisibility(View.VISIBLE); //Mostramos el ProgressBar

            //Creamos una nueva instancia de "Handler", que está vinculada al Looper principal (el hilo principal de la aplicación). Esto asegura que cualquier operación realizada dentro de este Handler se ejecute en el hilo principal
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { //El "Handler" utiliza el método "postDelayed" para ejecutar el "Runnable" que contiene las acciones a realizar después de un retraso especificado (en este caso, 1000 milisegundos, es decir, 1 segundo)
                @Override
                public void run() {
                    pbReintentarConexion.setVisibility(View.GONE); //Después de un segundo, ocultamos el ProgressBar
                }
            }, 1000);

            Utilidades.mostrarMensajePorInternetCaido(getContext(), viewNoInternet); //Llamamos el método utilitario "mostrarMensajePorInternetCaido" donde mandamos la vista "viewNoInternet" donde se hará visible cuando no haya conexión a internet y se ocultará cuando si haya
        });

        return view;
    }

    //Método que nos ayuda a obtener las cuadrillas de la colección "cuadrillas" de Firestore y asignarlas al RecyclerView
    private void obtenerCuadrillas() {
        try {
            //Llamamos el método "obtenerCuadrillas" de la clase "Cuadrilla" donde se obtienen todas las cuadrillas, e invocamos a la interfaz "FirestoreAllSpecialDocumentsCallback" y le decimos que debe recibir un "CuadrillasItems"
            cuad.obtenerCuadrillas(new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<CuadrillasItems>() {
                @Override
                public void onCallback(List<CuadrillasItems> lista) { //En esta lista "items" están todas las cuadrillas y sus datos (Nombre y Dinero)
                    if (lista != null) {
                        items = lista; //Inicializamos la lista global "items" con la "lista" de cuadrillas extraída de Firestore
                        items = Utilidades.ordenarListaPorAlfabetico(items, "cuadrilla", "Ascendente"); //Siempre al inicio que se carguen las cuadrillas, que se muestren en orden alfabético. Llamamos el método utilitario "ordenarListaPorAlfabetico". Le mandamos la lista "items", el nombre del campo String "cuadrilla", y el tipo de orden "Ascendente". Este método retorna la lista ya ordenada y la guardamos en "items"

                        //Estos if ordenan la lista "items" dependiendo del contenido de la variable global "tipoOrden" que su contenido solo cambia cuando el usuario da clic en una opción del PopupMenu para ordenar cuadrillas
                        if (tipoOrden.equalsIgnoreCase("Alfabetico")) {
                            items = Utilidades.ordenarListaPorAlfabetico(items, "cuadrilla", "Ascendente"); //Llamamos el método utilitario "ordenarListaPorAlfabetico". Le mandamos la lista "items", el nombre del campo String "cuadrilla", y el tipo de orden "Ascendente". Este método retorna la lista ya ordenada y la guardamos en "items"
                        }
                        else if (tipoOrden.equalsIgnoreCase("MenosDinero")) {
                            items = Utilidades.ordenarListaPorDouble(items, "dinero", "Ascendente"); //Llamamos el método utilitario "ordenarListaPorDouble". Le mandamos la lista "items", el nombre del campo double "dinero", y el tipo de orden "Ascendente". Este método retorna la lista ya ordenada y la guardamos en "items"
                        }
                        else if (tipoOrden.equalsIgnoreCase("MasDinero")) {
                            items = Utilidades.ordenarListaPorDouble(items, "dinero", "Descendente"); //Llamamos el método utilitario "ordenarListaPorDouble". Le mandamos la lista "items", el nombre del campo double "dinero", y el tipo de orden "Descendente". Este método retorna la lista ya ordenada y la guardamos en "items"
                        }

                        inicializarGridView(lista); //Llamamos el método "inicializarGridView" y le mandamos la lista "items"
                    }
                }

                @Override
                public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                    Toast.makeText(getContext(), "ERROR AL CARGAR LAS CUADRILLAS", Toast.LENGTH_SHORT).show();
                    Log.e("ObtenerCuadrillas", "Error al obtener las cuadrillas", e);
                }
            });
        }
        catch (Exception e) {
            Log.e("ObtenerCuadrillas", "Error al obtener las cuadrillas", e);
        }
    }

    private void inicializarGridView(List<CuadrillasItems> items) {
        customAdapter = new CuadrillasAdapter(items, getContext()); //Creamos una nueva instancia de la clase "CuadrillasAdapter" en donde mandamos la lista "items" y un contexto; y esta instancia la igualamos al objeto "customAdapter"
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
                tipoOrden = "Alfabetico";
                items = Utilidades.ordenarListaPorAlfabetico(items, "cuadrilla", "Ascendente"); //Llamamos el método utilitario "ordenarListaPorAlfabetico". Le mandamos la lista "items", el nombre del campo String "cuadrilla", y el tipo de orden "Ascendente". Este método retorna la lista ya ordenada y la guardamos en "items"
                customAdapter.notifyDataSetChanged(); //Esto indica al "customAdapter" que sus datos han sido cambiados
                return true;

            case R.id.menuMenosDinero:
                tipoOrden = "MenosDinero";
                items = Utilidades.ordenarListaPorDouble(items, "dinero", "Ascendente"); //Llamamos el método utilitario "ordenarListaPorDouble". Le mandamos la lista "items", el nombre del campo double "dinero", y el tipo de orden "Ascendente". Este método retorna la lista ya ordenada y la guardamos en "items"
                customAdapter.notifyDataSetChanged(); //Esto indica al "customAdapter" que sus datos han sido cambiados
                return true;

            case R.id.menuMasDinero:
                tipoOrden = "MasDinero";
                items = Utilidades.ordenarListaPorDouble(items, "dinero", "Descendente"); //Llamamos el método utilitario "ordenarListaPorDouble". Le mandamos la lista "items", el nombre del campo double "dinero", y el tipo de orden "Descendente". Este método retorna la lista ya ordenada y la guardamos en "items"
                customAdapter.notifyDataSetChanged(); //Esto indica al "customAdapter" que sus datos han sido cambiados
                return true;

            default:
                return false;
        }
    }

    //Método que desactiva el SwipeRefreshLayout mientras se está desplazando hacia arriba el GridView de Cuadrillas
    private void desactivarSwipeGridView() {
        gvCuadrillas.setOnScrollChangeListener(new View.OnScrollChangeListener() { //Detectamos los cambios en el desplazamiento vertical del gridView "gvCuadrillas"
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) { //Este método que se ejecuta cada vez que ocurre un cambio en el desplazamiento del gridView
                //El "canScrollVertically(-1)" del "gvCuadrillas" devuelve un true si el gridView puede seguir desplazándose hacia arriba (si aún no llegamos al limite superior del gridView). Usamos "canScrollVertically" para verificar si la vista "gvCuadrillas" puede seguir desplazándose verticalmente en una dirección específica; y el "-1" indica la dirección "hacia arriba". Si regresa un true, que entre al if
                if (gvCuadrillas.canScrollVertically(-1)) {
                    swlRecargar.setEnabled(false); //Si entró al if significa que el gridView puede seguir desplazándose hacia arriba, por lo tanto, desactivamos el "SwipeRefreshLayout" para que no interfiera en el scroll del gridView
                }
                else { //Si entra a este else, significa que "canScrollVertically(-1)" devuelve un false, esto indica que el gridView llegó a su limite superior y no puede desplazarse más hacia arriba
                    swlRecargar.setEnabled(true); //Como el gridView no puede seguir desplazándose hacia arriba, activamos el "SwipeRefreshLayout" para que ahora si permita recargar la pantalla
                }
            }
        });
    }

    @Override
    public void onRefresh() { //Método que detecta cuando se recarga la pantalla con SwipeRefreshLayout
        //Creamos una nueva instancia de "Handler", que está vinculada al Looper principal (el hilo principal de la aplicación). Esto asegura que cualquier operación realizada dentro de este Handler se ejecute en el hilo principal
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { //El "Handler" utiliza el método "postDelayed" para ejecutar el "Runnable" que contiene las acciones a realizar después de un retraso especificado (en este caso, 1000 milisegundos, es decir, 1 segundo)
            @Override
            public void run() {
                Utilidades.mostrarMensajePorInternetCaido(getContext(), viewNoInternet); //Llamamos el método utilitario "mostrarMensajePorInternetCaido" donde mandamos la vista "viewNoInternet" donde se hará visible cuando no haya conexión a internet y se ocultará cuando si haya
                obtenerCuadrillas();
                swlRecargar.setRefreshing(false); //Llamamos a este método para detener la animación de refresco
            }
        }, 1000);
    }
}