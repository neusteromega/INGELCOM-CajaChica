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

public class FragGastosSupervisores extends Fragment {

    private static final String ARG_NOMBRE_CUADRILLA = "cuadrilla_key";

    private String nombreCuadrilla;
    private RecyclerView rvGastos;
    private TextView lblTotalGastos;

    //Instancia de la clase "SharedViewGastosModel" que nos ayuda a compartir datos con diferentes componentes de la interfaz de usuario, como ser fragmentos y actividades y que estos datos sobreviven a cambios de configuración como las rotaciones de pantalla
    private SharedViewGastosModel svmGastos;

    public FragGastosSupervisores() {
        // Required empty public constructor
    }

    public static FragGastosSupervisores newInstance(String nombreCuadrilla) {
        FragGastosSupervisores fragment = new FragGastosSupervisores();
        Bundle args = new Bundle();
        args.putString(ARG_NOMBRE_CUADRILLA, nombreCuadrilla);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nombreCuadrilla = getArguments().getString(ARG_NOMBRE_CUADRILLA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gastos_supervisores, container, false);

        //Ponemos las instancias de las clases aquí y no de forma global ya que el "getContext()" se genera cuando se crea el fragment, aquí en "onCreateView"
        Gasto gast = new Gasto(getContext());
        Usuario usu = new Usuario(getContext());

        //Enlazamos los componentes gráficos del fragment a sus respectivas variables
        lblTotalGastos = view.findViewById(R.id.lblTotalGastosSup);
        rvGastos = view.findViewById(R.id.rvGastosSupervisores);

        rvGastos.setLayoutManager(new LinearLayoutManager(getContext())); //Creamos un nuevo LinearLayoutManager para que el RecyclerView se vea en forma de tarjetas
        obtenerGastos(usu, gast, ""); //Llamamos al método "obtenerGastos" y le mandamos la instancias de las clases Usuario y Gasto, y el "mes" vacío, ya que al crear el fragment no se realiza un filtrado de gastos por mes
        obtenerMes(usu, gast); //Llamamos al método "obtenerMes" para recibir la selección del mes hecha por el usuario

        return view;
    }

    //Método que nos ayuda a obtener los gastos de la colección "gastos" de Firestore y asignarlos al RecyclerView
    private void obtenerGastos(Usuario usu, Gasto gast, String mes) { //Recibe las instancias de las clases "Usuario" y "Gasto", y el String con el mes y año (por ejemplo, "Julio - 2024") para hacer el filtrado de gastos
        try {
            //Llamamos el método "obtenerUnUsuario" de la clase "Usuario" que obtiene el usuario actual
            usu.obtenerUsuarioActual(new FirestoreCallbacks.FirestoreDocumentCallback() {
                @Override
                public void onCallback(Map<String, Object> documento) {
                    if (documento != null) { //Si "documento" no es nulo, quiere decir que encontró el usuario mediante el correo
                        String cuadrilla = (String) documento.get("Cuadrilla"); //Obtenemos la cuadrilla de "documento"

                        if (!cuadrilla.isEmpty()) { //Si "cuadrilla", que se extrae del documento con los datos del usuario actual, NO está vacía, por lo tanto, es un empleado quién inició sesión, así que se utilizará la misma variable "cuadrilla" para la obtención de los gastos
                            //Llamamos el método "obtenerGastos" de la clase "Gastos", le mandamos la cuadrilla del usuario actual, el rol "Administrador" (ya que queremos ver los gastos hechos por los administrador a la cuadrilla) y el "mes". Con esto se podrán obtener todos los gastos hechos por los administradores a la cuadrilla del usuario actual
                            gast.obtenerGastos(cuadrilla, "Administrador", mes, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<GastosItems>() {
                                @Override
                                public void onCallback(List<GastosItems> items) { //En esta lista "items" están los gastos ya filtrados por cuadrilla y rol
                                    if (items != null) //Si "items" no es null, que entre al if
                                        inicializarRecyclerView(items); //Llamamos el método "inicializarRecyclerView" y le mandamos la lista "items"
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(getContext(), "ERROR AL CARGAR LOS GASTOS", Toast.LENGTH_SHORT).show();
                                    Log.w("ObtenerGastos", e);
                                }
                            });
                        }
                        else { //En cambio, si "cuadrilla", que se extrae del documento con los datos del usuario actual, SI está vacía, quiere decir que es un administrador quien inició sesión, y los administradores no pertenecen a una cuadrilla; en este caso, para la obtención de los datos de la cuadrilla a la que se está visualizando sus gastos, se utiliza la variable "nombreCuadrilla" que se recibe como parámetro en este fragment
                            //Llamamos el método "obtenerGastos" de la clase "Gastos", le mandamos la cuadrilla del usuario actual, el rol "Administrador" (ya que queremos ver los gastos hechos por los administrador a la cuadrilla) y el "mes". Con esto se podrán obtener todos los gastos hechos por los administradores a la cuadrilla del usuario actual
                            gast.obtenerGastos(nombreCuadrilla, "Administrador", mes, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<GastosItems>() {
                                @Override
                                public void onCallback(List<GastosItems> items) { //En esta lista "items" están los gastos ya filtrados por cuadrilla y rol
                                    if (items != null) {//Si "items" no es null, que entre al if
                                        items = Utilidades.ordenarListaPorFechaHora(items, "fechaHora", "Descendente"); //Llamamos el método utilitario "ordenarListaPorFechaHora". Le mandamos la lista "items", el nombre del campo double "fechaHora", y el tipo de orden "Descendente". Este método retorna la lista ya ordenada y la guardamos en "items"
                                        inicializarRecyclerView(items); //Llamamos el método "inicializarRecyclerView" y le mandamos la lista "items"
                                    }
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(getContext(), "ERROR AL CARGAR LOS GASTOS", Toast.LENGTH_SHORT).show();
                                    Log.w("ObtenerGastos", e);
                                }
                            });
                        }
                    }
                    else { //Si "documento" es nulo, no se encontró el usuario en la colección, y entrará en este else
                        Log.w("ObtenerUsuario", "Usuario no encontrado");
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.w("BuscarUsuario", "Error al obtener el usuario", e);
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerUsuario", e);
        }
    }

    //Método que nos ayuda a inicializar el RecyclerView de gastos
    private void inicializarRecyclerView(List<GastosItems> items) { //Recibe una lista de tipo "GastosItems" con los gastos a mostrar en el RecyclerView
        GastosAdapter adapter = new GastosAdapter(items); //Creamos un nuevo objeto de tipo GastosAdapter en el cual enviamos la lista "items"
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
                datosGasto.put("ActivityDGI", "DetalleGastoSupervisores");
                datosGasto.put("ID", items.get(rvGastos.getChildAdapterPosition(view)).getId());
                datosGasto.put("FechaHora", items.get(rvGastos.getChildAdapterPosition(view)).getFechaHora());
                datosGasto.put("Cuadrilla", items.get(rvGastos.getChildAdapterPosition(view)).getCuadrilla());
                datosGasto.put("LugarCompra", items.get(rvGastos.getChildAdapterPosition(view)).getLugarCompra());
                datosGasto.put("TipoCompra", items.get(rvGastos.getChildAdapterPosition(view)).getTipoCompra());
                datosGasto.put("Descripcion", items.get(rvGastos.getChildAdapterPosition(view)).getDescripcion());
                datosGasto.put("NumeroFactura", items.get(rvGastos.getChildAdapterPosition(view)).getNumeroFactura());
                datosGasto.put("Usuario", items.get(rvGastos.getChildAdapterPosition(view)).getUsuario());
                datosGasto.put("Rol", items.get(rvGastos.getChildAdapterPosition(view)).getRol());
                datosGasto.put("Total", String.format("%.2f", items.get(rvGastos.getChildAdapterPosition(view)).getTotal()));

                //Llamamos el método "iniciarActivityConDatos" de la clase Utilidades y le mandamos el contexto, el activity siguiente y el HashMap con los datos a enviar
                Utilidades.iniciarActivityConDatos(getActivity(), DetalleGastoIngreso.class, datosGasto);
            }
        });
    }

    //Método que nos permite obtener el mes y el año seleccionado por el usuario. Aquí se obtiene literalmente el contenido del TextView lblFecha del Activity ListadoGastos, y se obtiene cada vez que el contenido de dicho TextView cambia
    private void obtenerMes(Usuario usu, Gasto gast) { //Recibe las instancias de las clases "Usuario" y "Gasto"
        try {
            svmGastos = new ViewModelProvider(getActivity()).get(SharedViewGastosModel.class); //Obtenemos el ViewModel compartido, haciendo referencia a la clase "SharedViewGastosModel"

            //Llamamos el método "getFecha" del usando la instancia "svmGastos" de la clase "SharedViewGastosModel" que devuelve un "LiveData<String>", esto proporciona una referencia observable al valor de fecha
            svmGastos.getFecha().observe(getViewLifecycleOwner(), new Observer<String>() { //Configuramos un observador del "LiveData<String>" que devuelve "getFecha()". "getViewLifecycleOwner()" se usa para obtener el ciclo de vida del fragmento actual, asegurando que las actualizaciones solo se envíen cuando el fragmento esté en un estado activo (es decir, no cuando está destruido o detenido)
                @Override
                public void onChanged(String mes) { //Este método se llamará cada vez que el valor "fecha" cambie en el "LiveData<String>"
                    obtenerGastos(usu, gast, mes); //Llamamos el método "obtenerGastos" de arriba y le mandamos las instancias de las clases "Usuario" y "Gasto", y "mes" que contiene el "lblFecha" del activity ListadoGastos
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerMes", e);
        }
    }
}