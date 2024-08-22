package com.ingelcom.cajachica.Fragmentos;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ingelcom.cajachica.Adaptadores.InicioAdapter;
import com.ingelcom.cajachica.AdmDatosCuadrilla;
import com.ingelcom.cajachica.DAO.Cuadrilla;
import com.ingelcom.cajachica.DAO.Gasto;
import com.ingelcom.cajachica.DAO.Ingreso;
import com.ingelcom.cajachica.DetalleGastoIngreso;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.ListadoGastos;
import com.ingelcom.cajachica.ListadoIngresosDeducciones;
import com.ingelcom.cajachica.Modelos.CuadrillasItems;
import com.ingelcom.cajachica.Modelos.GastosItems;
import com.ingelcom.cajachica.Modelos.IngresosItems;
import com.ingelcom.cajachica.R;

import java.util.HashMap;
import java.util.List;

public class FragAdmInicio extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private RecyclerView rvIngresos, rvGastos, rvCuadrillas;
    private TextView btnVerIngresos, btnVerGastos;
    private SwipeRefreshLayout swlRecargar;
    private View viewNoInternet;
    private TextView btnReintentarConexion;
    private ProgressBar pbReintentarConexion;

    private Ingreso ingr;
    private Gasto gast;
    private Cuadrilla cuad;

    public FragAdmInicio() {
        // Required empty public constructor
    }

    public static FragAdmInicio newInstance(String param1, String param2) {
        FragAdmInicio fragment = new FragAdmInicio();
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
        View view = inflater.inflate(R.layout.fragment_adm_inicio, container, false);

        inicializarElementos(view);

        //Llamamos el método "desactivarSwipeDuranteScroll" tres veces, y en cada llamado mandamos un RecyclerView diferente
        desactivarSwipeDuranteScroll(rvIngresos);
        desactivarSwipeDuranteScroll(rvGastos);
        desactivarSwipeDuranteScroll(rvCuadrillas);

        obtenerDatos();

        btnVerIngresos.setOnClickListener(v -> {
            Utilidades.iniciarActivityConString(getContext(), ListadoIngresosDeducciones.class, "ActivityLID", "ListadoIngresosTodos", false);
        });

        btnVerGastos.setOnClickListener(v -> {
            Utilidades.iniciarActivityConString(getContext(), ListadoGastos.class, "ActivityLG", "ListadoGastosTodos", false);
        });

        btnReintentarConexion.setOnClickListener(v -> {
            Utilidades.mostrarMensajePorInternetCaido(getContext(), viewNoInternet); //Llamamos el método utilitario "mostrarMensajePorInternetCaido" donde mandamos la vista "viewNoInternet" donde se hará visible cuando no haya conexión a internet y se ocultará cuando si haya
        });

        return view;
    }

    private void inicializarElementos(View view) {
        rvIngresos = view.findViewById(R.id.rvIngresosInicio);
        rvGastos = view.findViewById(R.id.rvGastosInicio);
        rvCuadrillas = view.findViewById(R.id.rvCuadrillasInicio);
        btnVerIngresos = view.findViewById(R.id.btnVerTodosIngresosInicio);
        btnVerGastos = view.findViewById(R.id.btnVerTodosGastosInicio);
        swlRecargar = view.findViewById(R.id.swipeRefreshLayoutInicio);
        viewNoInternet = view.findViewById(R.id.viewNoInternetInicio);
        btnReintentarConexion = view.findViewById(R.id.btnReintentarConexion);
        pbReintentarConexion = view.findViewById(R.id.pbReintentarConexion);

        ingr = new Ingreso(getContext());
        gast = new Gasto(getContext());
        cuad = new Cuadrilla(getContext());

        swlRecargar.setOnRefreshListener(this); //Llamada al método "onRefresh"
        swlRecargar.setColorSchemeResources(R.color.clr_fuente_primario); //Color del SwipeRefreshLayout

        Utilidades.mostrarMensajePorInternetCaido(getContext(), viewNoInternet); //Llamamos el método utilitario "mostrarMensajePorInternetCaido" donde mandamos la vista "viewNoInternet" donde se hará visible cuando no haya conexión a internet y se ocultará cuando si haya
    }

    private void obtenerDatos() {
        obtenerIngresos();
        obtenerGastos();
        obtenerCuadrillas();
    }

    //Método que permite obtener los Ingresos de Firestore
    private void obtenerIngresos() {
        try {
            //Llamamos el método "obtenerIngresos" de la clase "Ingreso", le mandamos la "cuadrilla" y el "mes" vacíos para indicar que no se haga un filtrado de ingresos. Con esto se podrán obtener todos los ingresos hechos por los administradores
            ingr.obtenerIngresos("", "", new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<IngresosItems>() {
                @Override
                public void onCallback(List<IngresosItems> items) { //En esta lista "items" están todos los ingresos
                    if (items != null) {//Si "items" no es null, que entre al if
                        items = Utilidades.ordenarListaPorFechaHora(items, "fechaHora", "Descendente"); //Llamamos el método utilitario "ordenarListaPorFechaHora". Le mandamos la lista "items", el nombre del campo double "fechaHora", y el tipo de orden "Descendente". Este método retorna la lista ya ordenada y la guardamos en "items"
                        items = Utilidades.obtenerPrimerosItemsLista(items, 8); //Llamamos el método utilitario "obtenerPrimerosItemsLista" que nos devolverá los primeros elementos de la lista "items" (esta lista ya está ordenada por la fecha más reciente) y también le pasamos la cantidad de los primeros elementos que devolverá, en este caso son 8
                        inicializarRecyclerView(items, "Ingreso"); //Llamamos el método "inicializarRecyclerView" de abajo, le mandamos la lista "items" y el tipo "Ingreso" indicando que debe inicializar el rvIngresos
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.w("ObtenerIngresos", e);
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerIngresos", e);
        }
    }

    //Método que permite obtener los Gastos de Firestore
    private void obtenerGastos() {
        try {
            //Llamamos el método "obtenerGastos" de la clase "Gasto", le mandamos la "cuadrilla", el "rol" y el "mes" vacíos para indicar que no se haga un filtrado de gastos. Con esto se podrán obtener todos los gastos hechos por los administradores y empleados
            gast.obtenerGastos("", "", "", new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<GastosItems>() {
                @Override
                public void onCallback(List<GastosItems> items) { //En esta lista "items" están todos los gastos
                    if (items != null) {//Si "items" no es null, que entre al if
                        items = Utilidades.ordenarListaPorFechaHora(items, "fechaHora", "Descendente"); //Llamamos el método utilitario "ordenarListaPorFechaHora". Le mandamos la lista "items", el nombre del campo double "fechaHora", y el tipo de orden "Descendente". Este método retorna la lista ya ordenada y la guardamos en "items"
                        items = Utilidades.obtenerPrimerosItemsLista(items, 8); //Llamamos el método utilitario "obtenerPrimerosItemsLista" que nos devolverá los primeros elementos de la lista "items" (esta lista ya está ordenada por la fecha más reciente) y también le pasamos la cantidad de los primeros elementos que devolverá, en este caso son 8
                        inicializarRecyclerView(items, "Gasto"); //Llamamos el método "inicializarRecyclerView" de abajo, le mandamos la lista "items" y el tipo "Gastos" indicando que debe inicializar el rvGastos
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.w("ObtenerGastos", e);
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerGastos", e);
        }
    }

    //Método que permite obtener las Cuadrillas de Firestore
    private void obtenerCuadrillas() {
        try {
            //Llamamos el método "obtenerCuadrillas" de la clase "Cuadrilla". Con esto se podrán obtener todas las cuadrillas y su dinero disponible
            cuad.obtenerCuadrillas(new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<CuadrillasItems>() {
                @Override
                public void onCallback(List<CuadrillasItems> items) { //En esta lista "items" están todas las cuadrillas
                    if (items != null) {//Si "items" no es null, que entre al if
                        items = Utilidades.ordenarListaPorDouble(items, "dinero", "Ascendente"); //Llamamos el método utilitario "ordenarListaPorDouble". Le mandamos la lista "items", el nombre del campo double "dinero", y el tipo de orden "Descendente". Este método retorna la lista ya ordenada y la guardamos en "items"
                        inicializarRecyclerView(items, "Cuadrilla"); //Llamamos el método "inicializarRecyclerView" de abajo, le mandamos la lista "items" ya ordenada y el tipo "Cuadrilla" indicando que debe inicializar el rvCuadrillas
                    }
                }
                @Override
                public void onFailure(Exception e) {
                    Log.w("ObtenerCuadrillas", e);
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerCuadrillas", e);
        }
    }

    //Método genérico que nos ayuda a inicializar los RecyclerView
    private <T> void inicializarRecyclerView(List<T> items, String tipo) { //Recibe una lista de tipo "T", esto quiere decir que puede ser cualquier tipo de lista
        LinearLayoutManager manager = new LinearLayoutManager(getContext()); //Creamos un nuevo LinearLayoutManager para que el RecyclerView se vea en forma de tarjetas
        manager.setOrientation(LinearLayoutManager.HORIZONTAL); //Asignamos que la orientación de los recyclerviews sea horizontal, estableciendo esa propiedad en el "LinearLayoutManager"

        if (tipo.contentEquals("Ingreso")) { //Si "tipo" es "Ingreso", que asigne los elementos al rvIngresos
            rvIngresos.setLayoutManager(manager); //Creamos un nuevo LinearLayoutManager para que el RecyclerView "rvIngresos" se vea en forma de tarjetas

            InicioAdapter<T> adapterIngresos = new InicioAdapter<>(items); //Creamos un adapter al instanciar la clase genérica "InicioAdapter" y le mandamos la lista "items"
            rvIngresos.setAdapter(adapterIngresos); //Asignamos el adapter al recyclerView de Ingresos

            adapterIngresos.setOnClickListener(new View.OnClickListener() { //Usando el objeto de "adapterIngresos" llamamos al método "setOnClickListener" de la clase InicioAdapter
                @Override
                public void onClick(View view) { //Al dar clic en una tarjeta del RecyclerView, se realizará lo siguiente
                    try {
                        int posicion = rvIngresos.getChildAdapterPosition(view); //Obtenemos la posición del elemento clickeado en el RecyclerView
                        T item = items.get(posicion); //De la lista "items" (que se utiliza para mostrar el RecyclerView) obtenemos el elemento clickeado utilizando la "posicion" obtenida arriba. Y este elemento clickeado lo guardamos en una variable de tipo "T" ya que "items" también es de tipo "T"
                        HashMap<String,Object> datosIngreso = new HashMap<>(); //Creamos un HashMap para guardar los datos que se enviarán al siguiente Activity

                        //Agregamos las claves y datos al HashMap
                        datosIngreso.put("ActivityDGI", "DetalleIngreso");

                        //Llamamos el método utilitario "obtenerCampo" y le mandamos el "item" clickeado, y los nombres de los métodos getter de la clase IngresosItems para que nos retorne los valores que devuelven estos métodos y poder guardarlos en el HashMap
                        datosIngreso.put("ID", Utilidades.obtenerCampo(item, "getId"));
                        /*datosIngreso.put("Usuario", Utilidades.obtenerCampo(item, "getUsuario"));
                        datosIngreso.put("FechaHora", Utilidades.obtenerCampo(item, "getFechaHora"));
                        datosIngreso.put("Cuadrilla", Utilidades.obtenerCampo(item, "getCuadrilla"));
                        datosIngreso.put("Transferencia", Utilidades.obtenerCampo(item, "getTransferencia"));
                        datosIngreso.put("Imagen", Utilidades.obtenerCampo(item, "getImagen"));
                        datosIngreso.put("Total", String.format("%.2f", Utilidades.obtenerCampo(item, "getTotal")));*/

                        //Llamamos el método "iniciarActivityConDatos" de la clase Utilidades y le mandamos el contexto, el activity siguiente y el HashMap con los datos a enviar
                        Utilidades.iniciarActivityConDatos(getActivity(), DetalleGastoIngreso.class, datosIngreso);
                    }
                    catch (Exception e) {
                        Log.w("ObtenerIngreso", e);
                    }
                }
            });
        }
        else if (tipo.contentEquals("Gasto")) { //En cambio, si "tipo" es "Gasto", que asigne los elementos al rvGastos
            rvGastos.setLayoutManager(manager); //Creamos un nuevo LinearLayoutManager para que el RecyclerView "rvGastos" se vea en forma de tarjetas

            InicioAdapter<T> adapterGastos = new InicioAdapter<>(items); //Creamos un adapter al instanciar la clase genérica "InicioAdapter" y le mandamos la lista "items"
            rvGastos.setAdapter(adapterGastos); //Asignamos el adapter al recyclerView de Gastos

            adapterGastos.setOnClickListener(new View.OnClickListener() { //Usando el objeto de "adapterGastos" llamamos al método "setOnClickListener" de la clase InicioAdapter
                @Override
                public void onClick(View view) { //Al dar clic en una tarjeta del RecyclerView, se realizará lo siguiente
                    try {
                        int posicion = rvGastos.getChildAdapterPosition(view); //Obtenemos la posición del elemento clickeado en el RecyclerView
                        T item = items.get(posicion); //De la lista "items" (que se utiliza para mostrar el RecyclerView) obtenemos el elemento clickeado utilizando la "posicion" obtenida arriba. Y este elemento clickeado lo guardamos en una variable de tipo "T" ya que "items" también es de tipo "T"
                        HashMap<String, Object> datosGasto = new HashMap<>(); //Creamos un HashMap para guardar los datos que se enviarán al siguiente Activity

                        //Agregamos las claves y datos al HashMap
                        datosGasto.put("ActivityDGI", "DetalleGastoSupervisores");

                        //Llamamos el método utilitario "obtenerCampo" y le mandamos el "item" clickeado, y los nombres de los métodos getter de la clase GastosItems para que nos retorne los valores que devuelven estos métodos y poder guardarlos en el HashMap
                        datosGasto.put("ID", Utilidades.obtenerCampo(item, "getId"));
                        /*datosGasto.put("FechaHora", Utilidades.obtenerCampo(item, "getFechaHora"));
                        datosGasto.put("Cuadrilla", Utilidades.obtenerCampo(item, "getCuadrilla"));
                        datosGasto.put("LugarCompra", Utilidades.obtenerCampo(item, "getLugarCompra"));
                        datosGasto.put("TipoCompra", Utilidades.obtenerCampo(item, "getTipoCompra"));
                        datosGasto.put("Descripcion", Utilidades.obtenerCampo(item, "getDescripcion"));
                        datosGasto.put("NumeroFactura", Utilidades.obtenerCampo(item, "getNumeroFactura"));
                        datosGasto.put("Usuario", Utilidades.obtenerCampo(item, "getUsuario"));
                        datosGasto.put("Rol", Utilidades.obtenerCampo(item, "getRol"));
                        datosGasto.put("Imagen", Utilidades.obtenerCampo(item, "getImagen"));
                        datosGasto.put("Total", String.format("%.2f", Utilidades.obtenerCampo(item, "getTotal")));*/

                        //Llamamos el método "iniciarActivityConDatos" de la clase Utilidades y le mandamos el contexto, el activity siguiente y el HashMap con los datos a enviar
                        Utilidades.iniciarActivityConDatos(getActivity(), DetalleGastoIngreso.class, datosGasto);
                    }
                    catch (Exception e) {
                        Log.w("ObtenerGasto", e);
                    }
                }
            });
        }
        else if (tipo.contentEquals("Cuadrilla")) { //En cambio, si "tipo" es "Cuadrilla", que asigne los elementos al rvCuadrillas
            rvCuadrillas.setLayoutManager(manager); //Creamos un nuevo LinearLayoutManager para que el RecyclerView "rvCuadrillas" se vea en forma de tarjetas

            InicioAdapter<T> adapterCuadrillas = new InicioAdapter<>(items); //Creamos un adapter al instanciar la clase genérica "InicioAdapter" y le mandamos la lista "items"
            rvCuadrillas.setAdapter(adapterCuadrillas); //Asignamos el adapter al recyclerView de Cuadrillas

            adapterCuadrillas.setOnClickListener(new View.OnClickListener() { //Usando el objeto de "adapterCuadrillas" llamamos al método "setOnClickListener" de la clase InicioAdapter
                @Override
                public void onClick(View view) { //Al dar clic en una tarjeta del RecyclerView, se realizará lo siguiente
                    try {
                        int posicion = rvGastos.getChildAdapterPosition(view); //Obtenemos la posición del elemento clickeado en el RecyclerView
                        T item = items.get(posicion); //De la lista "items" (que se utiliza para mostrar el RecyclerView) obtenemos el elemento clickeado utilizando la "posicion" obtenida arriba. Y este elemento clickeado lo guardamos en una variable de tipo "T" ya que "items" también es de tipo "T"
                        HashMap<String, Object> datosCuadrilla = new HashMap<>(); //Creamos un HashMap para guardar los datos que se enviarán al siguiente Activity

                        //Llamamos el método utilitario "obtenerCampo" y le mandamos el "item" clickeado, y los nombres de los métodos getter de la clase IngresosItems para que nos retorne los valores que devuelven estos métodos y poder guardarlos en el HashMap
                        datosCuadrilla.put("Cuadrilla", Utilidades.obtenerCampo(item, "getCuadrilla"));
                        datosCuadrilla.put("DineroDisponible", String.format("%.2f", Utilidades.obtenerCampo(item, "getDinero"))); //El resultado de "getDinero" lo convertimos a String en formato para que tenga dos décimales

                        //Llamamos el método "iniciarActivityConDatos" de la clase Utilidades y le mandamos el contexto, el activity siguiente y el HashMap con los datos a enviar
                        Utilidades.iniciarActivityConDatos(getActivity(), AdmDatosCuadrilla.class, datosCuadrilla);
                    }
                    catch (Exception e) {
                        Log.w("ObtenerCuadrilla", e);
                    }
                }
            });
        }
    }

    private void desactivarSwipeDuranteScroll(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    // Desactiva el SwipeRefreshLayout cuando el RecyclerView está siendo arrastrado
                    swlRecargar.setEnabled(false);
                }
                else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // Reactiva el SwipeRefreshLayout cuando el RecyclerView deja de arrastrarse
                    swlRecargar.setEnabled(true);
                }
            }
        });
    }

    @Override
    public void onRefresh() { //Método que detecta cuando se recarga la pantalla con SwipeRefreshLayout
        //Creamos una nueva instancia de "Handler", que está vinculada al Looper principal (el hilo principal de la aplicación). Esto asegura que cualquier operación realizada dentro de este Handler se ejecute en el hilo principal
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { //El "Handler" utiliza el método "postDelayed" para ejecutar el "Runnable" que contiene las acciones a realizar después de un retraso especificado (en este caso, 1500 milisegundos, es decir, 1.5 segundos)
            @Override
            public void run() {
                Utilidades.mostrarMensajePorInternetCaido(getContext(), viewNoInternet); //Llamamos el método utilitario "mostrarMensajePorInternetCaido" donde mandamos la vista "viewNoInternet" donde se hará visible cuando no haya conexión a internet y se ocultará cuando si haya
                obtenerDatos();
                swlRecargar.setRefreshing(false); //Llamamos a este método para detener la animación de refresco
            }
        }, 1000);
    }
}