package com.ingelcom.cajachica.Fragmentos;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ingelcom.cajachica.Adaptadores.GastosAdapter;
import com.ingelcom.cajachica.DAO.Gasto;
import com.ingelcom.cajachica.DAO.Usuario;
import com.ingelcom.cajachica.DetalleGastoIngreso;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.SharedViewGastosModel;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.Modelos.GastosItems;
import com.ingelcom.cajachica.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragGastosCuadrilla extends Fragment {

    private static final String ARG_NOMBRE_CUADRILLA = "cuadrilla_key";
    private static final String ARG_NOMBRE_ACTIVITY = "activity_key";

    private String nombreCuadrilla, nombreActivity, fechaSeleccionada = "", userCompra = "", recargar = "";
    private RecyclerView rvGastos;
    private TextView lblTotalGastos;
    private Gasto gast;
    private Usuario usu;

    private SharedViewGastosModel svmGastos; //Instancia de la clase "SharedViewGastosModel" que nos ayuda a compartir datos con diferentes componentes de la interfaz de usuario, como ser fragmentos y actividades y que estos datos sobreviven a cambios de configuración como las rotaciones de pantalla

    public FragGastosCuadrilla() {

    }

    public static FragGastosCuadrilla newInstance(String nombreCuadrilla, String nombreActivity) {
        FragGastosCuadrilla fragment = new FragGastosCuadrilla();
        Bundle args = new Bundle();
        args.putString(ARG_NOMBRE_CUADRILLA, nombreCuadrilla);
        args.putString(ARG_NOMBRE_ACTIVITY, nombreActivity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nombreCuadrilla = getArguments().getString(ARG_NOMBRE_CUADRILLA);
            nombreActivity = getArguments().getString(ARG_NOMBRE_ACTIVITY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gastos_cuadrilla, container, false); //Guardamos la vista inflada del fragment en una variable tipo "view"

        //Ponemos las instancias de las clases aquí y no de forma global ya que el "getContext()" se genera cuando se crea el fragment, aquí en "onCreateView"
        gast = new Gasto(getContext());
        usu = new Usuario(getContext());

        //Enlazamos los componentes gráficos del fragment a sus respectivas variables
        lblTotalGastos = view.findViewById(R.id.lblTotalGastosCua);
        rvGastos = view.findViewById(R.id.rvGastosCuadrilla);

        obtenerGastos(""); //Llamamos al método "obtenerGastos" y le mandamos el "mesAnio" vacío, ya que al crear el fragment no se realiza un filtrado de gastos por mes o año
        obtenerDatos(); //Llamamos al método "obtenerDatos" para recibir la selección del mesAnio hecha por el usuario

        return view;
    }

    //Método que nos ayuda a obtener los gastos de la colección "gastos" de Firestore y asignarlos al RecyclerView
    private void obtenerGastos(String mesAnio) { //Recibe el String con el mes y año (por ejemplo, "Julio - 2024") o sólo el año, para hacer el filtrado de gastos
        try {
            //Llamamos el método "obtenerUnUsuario" de la clase "Usuario" que obtiene el usuario actual
            usu.obtenerUsuarioActual(new FirestoreCallbacks.FirestoreDocumentCallback() {
                @Override
                public void onCallback(Map<String, Object> documento) {
                    if (documento != null) { //Si "documento" no es nulo, quiere decir que encontró el usuario actual
                        String cuadrilla = (String) documento.get("Cuadrilla"); //Obtenemos la cuadrilla de "documento"

                        if (nombreActivity.equalsIgnoreCase("ListadoGastosEmpleado")) { //Si "nombreActivity" contiene el texto "ListadoGastosEmpleado", quiere decir que un empleado está solicitando ver sus gastos
                            //Llamamos el método "obtenerGastos" de la clase "Gastos", le mandamos la cuadrilla del usuario actual, el rol "Empleado" (ya que queremos ver los gastos hechos por la cuadrilla y en la cuadrilla todos los usuarios tienen rol "Empleado"), el "datoUsuario" vacío, la variable "userCompra" donde va el "datoCompra" (porque el "ListadoGastosEmpleado" puede filtrar por tipo de compra) y el "mesAnio". Con esto se podrán obtener todos los gastos hechos por los empleados de la cuadrilla del usuario actual
                            gast.obtenerGastos(cuadrilla, "Empleado", "", userCompra, mesAnio, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<GastosItems>() {
                                @Override
                                public void onCallback(List<GastosItems> items) { //En esta lista "items" están todos los gastos ya filtrados
                                    if (items != null) {
                                        items = Utilidades.ordenarListaPorFechaHora(items, "fechaHora", "Descendente"); //Llamamos el método utilitario "ordenarListaPorFechaHora". Le mandamos la lista "items", el nombre del campo double "fechaHora", y el tipo de orden "Descendente". Este método retorna la lista ya ordenada y la guardamos en "items"
                                        inicializarRecyclerView(items); //Llamamos el método "inicializarRecyclerView" y le mandamos la lista "items"
                                    }
                                }

                                @Override
                                public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                                    Toast.makeText(getContext(), "ERROR AL CARGAR LOS GASTOS", Toast.LENGTH_SHORT).show();
                                    Log.e("ObtenerGastos", "Error al obtener los gastos", e);
                                }
                            });
                        }
                        else if (nombreActivity.equalsIgnoreCase("ListadoGastosAdmin")) { //En cambio, si "nombreActivity" contiene el texto "ListadoGastosAdmin" significa que es un admin que desea ver los gastos de una cuadrilla específica
                            //Llamamos el método "obtenerGastos" de la clase "Gastos", le mandamos la cuadrilla recibida como parámetro en este fragment, el rol "Empleado" (ya que queremos ver los gastos hechos por la cuadrilla y en la cuadrilla todos los usuarios tienen rol "Empleado"), el "datoUsuario" vacío, la variable "userCompra" donde va el "datoCompra" (porque el "ListadoGastosAdmin" puede filtrar por tipo de compra) y el "mesAnio". Con esto se podrán obtener todos los gastos hechos por los empleados de la cuadrilla
                            gast.obtenerGastos(nombreCuadrilla, "Empleado", "", userCompra, mesAnio, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<GastosItems>() {
                                @Override
                                public void onCallback(List<GastosItems> items) { //En esta lista "items" están todos los gastos ya filtrados
                                    if (items != null) {
                                        items = Utilidades.ordenarListaPorFechaHora(items, "fechaHora", "Descendente"); //Llamamos el método utilitario "ordenarListaPorFechaHora". Le mandamos la lista "items", el nombre del campo double "fechaHora", y el tipo de orden "Descendente". Este método retorna la lista ya ordenada y la guardamos en "items"
                                        inicializarRecyclerView(items); //Llamamos el método "inicializarRecyclerView" y le mandamos la lista "items"
                                    }
                                }

                                @Override
                                public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                                    Toast.makeText(getContext(), "ERROR AL CARGAR LOS GASTOS", Toast.LENGTH_SHORT).show();
                                    Log.e("ObtenerGastos", "Error al obtener los gastos", e);
                                }
                            });
                        }
                        else if (nombreActivity.equalsIgnoreCase("ListadoGastosTodos")) { //Por último, si "nombreActivity" tiene el texto "ListadoGastosTodos", significa que un administrador quiere ver todos los gastos de todas las cuadrillas
                            //Llamamos el método "obtenerGastos" de la clase "Gastos", le mandamos la cuadrilla vacía porque queremos obtener todos los gastos, el rol "Empleado" (ya que queremos ver los gastos hechos por las cuadrillas y en las cuadrillas todos los usuarios tienen rol "Empleado"), la variable global "userCompra" donde va el "datoUsuario" (porque el "ListadoGastosTodos" puede filtrar por usuario), el "datoCompra" vacío y el "mesAnio". Con esto se podrán obtener todos los gastos hechos por los empleados de todas las cuadrillas
                            gast.obtenerGastos("", "Empleado", userCompra, "", mesAnio, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<GastosItems>() {
                                @Override
                                public void onCallback(List<GastosItems> items) { //En esta lista "items" están todos los gastos ya filtrados
                                    if (items != null) {
                                        items = Utilidades.ordenarListaPorFechaHora(items, "fechaHora", "Descendente"); //Llamamos el método utilitario "ordenarListaPorFechaHora". Le mandamos la lista "items", el nombre del campo double "fechaHora", y el tipo de orden "Descendente". Este método retorna la lista ya ordenada y la guardamos en "items"
                                        inicializarRecyclerView(items); //Llamamos el método "inicializarRecyclerView" y le mandamos la lista "items"
                                    }
                                }

                                @Override
                                public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                                    Toast.makeText(getContext(), "ERROR AL CARGAR LOS GASTOS", Toast.LENGTH_SHORT).show();
                                    Log.e("ObtenerGastos", "Error al obtener los gastos", e);
                                }
                            });
                        }
                    }
                    else { //Si "documento" es nulo, no se encontró el usuario en la colección, y entrará en este else
                        Log.w("ObtenerUsuario", "Usuario no encontrado");
                    }
                }

                @Override
                public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                    Log.e("ObtenerUsuario", "Error al obtener el usuario", e);
                }
            });
        }
        catch (Exception e) {
            Log.e("ObtenerUsuario", "Error al obtener el usuario", e);
        }
    }

    //Método que nos ayuda a inicializar el RecyclerView de gastos
    private void inicializarRecyclerView(List<GastosItems> items) { //Recibe una lista de tipo "GastosItems" con los gastos a mostrar en el RecyclerView
        rvGastos.setLayoutManager(new LinearLayoutManager(getContext())); //Creamos un nuevo LinearLayoutManager para que el RecyclerView se vea en forma de tarjetas

        GastosAdapter adapter = new GastosAdapter(items, nombreActivity); //Creamos un nuevo objeto de tipo GastosAdapter en el cual enviamos la lista "items"
        rvGastos.setAdapter(adapter); //Asignamos el adapter al recyclerView de Gastos
        double totalGastos = 0; //Variable que nos servirá para calcular el total de los gastos que se muestren en el RecyclerView

        //Recorremos la lista "items" y cada elemento de ella se guardará en la variable temporal "item" de tipo "GastosItems"
        for (GastosItems item : items) {
            totalGastos += item.getTotal(); //Obtenemos el "total" de cada elemento de la lista "items" y lo vamos sumando en la variable "totalGastos"
        }

        lblTotalGastos.setText("L. " + String.format("%.2f", totalGastos)); //Asignamos el totalGastos al TextView "lblTotalGastos" y formateamos la variable "totalGastos" para que se muestre con dos digitos después del punto decimal

        adapter.setOnClickListener(new View.OnClickListener() { //Usando el objeto de "adapter" llamamos al método "setOnClickListener" de la clase GastosAdapter
            @Override
            public void onClick(View view) { //Al dar clic en una tarjeta del RecyclerView, se realizará lo siguiente
                HashMap<String,Object> datosGasto = new HashMap<>(); //Creamos un HashMap para guardar los datos que se enviarán al siguiente Activity

                //Agregamos las claves y datos al HashMap
                datosGasto.put("ActivityDGI", "DetalleGastoCuadrilla");
                datosGasto.put("ID", items.get(rvGastos.getChildAdapterPosition(view)).getId());
                datosGasto.put("FechaHora", items.get(rvGastos.getChildAdapterPosition(view)).getFechaHora());
                datosGasto.put("Cuadrilla", items.get(rvGastos.getChildAdapterPosition(view)).getCuadrilla());
                datosGasto.put("LugarCompra", items.get(rvGastos.getChildAdapterPosition(view)).getLugarCompra());
                datosGasto.put("TipoCompra", items.get(rvGastos.getChildAdapterPosition(view)).getTipoCompra());
                datosGasto.put("Descripcion", items.get(rvGastos.getChildAdapterPosition(view)).getDescripcion());
                datosGasto.put("NumeroFactura", items.get(rvGastos.getChildAdapterPosition(view)).getNumeroFactura());
                datosGasto.put("Usuario", items.get(rvGastos.getChildAdapterPosition(view)).getUsuario());
                datosGasto.put("Rol", items.get(rvGastos.getChildAdapterPosition(view)).getRol());
                datosGasto.put("Imagen", items.get(rvGastos.getChildAdapterPosition(view)).getImagen());
                datosGasto.put("Total", String.format("%.2f", items.get(rvGastos.getChildAdapterPosition(view)).getTotal()));

                //Llamamos el método "iniciarActivityConDatos" de la clase Utilidades y le mandamos el contexto, el activity siguiente y el HashMap con los datos a enviar
                Utilidades.iniciarActivityConDatos(getActivity(), DetalleGastoIngreso.class, datosGasto);
            }
        });
    }

    //Método que nos permite obtener el Mes - Año, o sólo el año seleccionado por el usuario. Aquí se obtiene literalmente el contenido del TextView "lblFecha" del Activity ListadoGastos, y se obtiene cada vez que el contenido de dicho TextView cambia
    private void obtenerDatos() {
        try {
            svmGastos = new ViewModelProvider(getActivity()).get(SharedViewGastosModel.class); //Obtenemos el ViewModel compartido, haciendo referencia a la clase "SharedViewGastosModel"

            //Llamamos el método "getFecha" del usando la instancia "svmGastos" de la clase "SharedViewGastosModel" que devuelve un "LiveData<String>", esto proporciona una referencia observable al valor de fecha
            svmGastos.getFecha().observe(getViewLifecycleOwner(), new Observer<String>() { //Configuramos un observador del "LiveData<String>" que devuelve "getFecha()". "getViewLifecycleOwner()" se usa para obtener el ciclo de vida del fragmento actual, asegurando que las actualizaciones solo se envíen cuando el fragmento esté en un estado activo (es decir, no cuando está destruido o detenido)
                @Override
                public void onChanged(String fecha) { //Este método se llamará cada vez que el valor "fecha" cambie en el "LiveData<String>"
                    fechaSeleccionada = fecha; //Guardamos el mes o año recibido en la variable global "fechaSeleccionada"
                    obtenerGastos(fecha); //Llamamos el método "obtenerGastos" de arriba y le mandamos "fecha" que contiene el "lblFecha" del activity ListadoGastos
                }
            });

            //Llamamos el método "getUserCompra" del usando la instancia "svmGastos" de la clase "SharedViewGastosModel" que devuelve un "LiveData<String>", esto proporciona una referencia observable al valor de "userCompra"
            svmGastos.getUserCompra().observe(getViewLifecycleOwner(), new Observer<String>() {
                @Override
                public void onChanged(String s) { //Este método se llamará cada vez que el valor "userCompra" cambie en el "LiveData<String>"
                    userCompra = s; //Guardamos el texto recibido en la variable global "userCompra"
                    obtenerGastos(fechaSeleccionada);
                }
            });

            //Llamamos el método "getRecargar" del usando la instancia "svmGastos" de la clase "SharedViewGastosModel" que devuelve un "LiveData<String>", esto proporciona una referencia observable al valor de "recargar"
            svmGastos.getRecargar().observe(getViewLifecycleOwner(), new Observer<String>() {
                @Override
                public void onChanged(String s) { //Este método se llamará cada vez que el valor "recargar" cambie en el "LiveData<String>"
                    recargar = s; //Guardamos el texto recibido en la variable global "recargar"

                    if (recargar.equalsIgnoreCase("Recargar"))
                        obtenerGastos(fechaSeleccionada); //Llamamos al método "obtenerGastos" para que se extraigan los datos al recargar
                }
            });
        }
        catch (Exception e) {
            Log.e("ObtenerDatos", "Error al obtener los datos con el SharedViewGastosModel", e);
        }
    }
}