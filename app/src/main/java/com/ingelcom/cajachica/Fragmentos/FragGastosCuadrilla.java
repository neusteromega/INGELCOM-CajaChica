package com.ingelcom.cajachica.Fragmentos;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
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
import com.ingelcom.cajachica.DetalleGasto;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.ListadoEmpleados;
import com.ingelcom.cajachica.Modelos.GastosItems;
import com.ingelcom.cajachica.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragGastosCuadrilla#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragGastosCuadrilla extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView rvGastos;
    private List<GastosItems> itemsGastos;
    private TextView lblTotalGastos;

    public FragGastosCuadrilla() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragGastosCuadrilla.
     */
    // TODO: Rename and change types and number of parameters
    public static FragGastosCuadrilla newInstance(String param1, String param2) {
        FragGastosCuadrilla fragment = new FragGastosCuadrilla();
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
        View view = inflater.inflate(R.layout.fragment_gastos_cuadrilla, container, false);

        //Ponemos las instancias de las clases aquí y no de forma global ya que el "getContext()" se genera cuando se crea el fragment, aquí en "onCreateView"
        Gasto gast = new Gasto(getContext());
        Usuario usu = new Usuario(getContext());

        //Enlazamos los componentes gráficos del fragment a sus respectivas variables
        lblTotalGastos = view.findViewById(R.id.lblTotalGastosCua);
        rvGastos = view.findViewById(R.id.rvGastosCuadrilla);

        rvGastos.setLayoutManager(new LinearLayoutManager(getContext()));

        obtenerGastos(usu, gast);

        return view;
    }

    private void obtenerGastos(Usuario usu, Gasto gast) {
        usu.obtenerUnUsuario(new FirestoreCallbacks.FirestoreDocumentCallback() {
            @Override
            public void onCallback(Map<String, Object> documento) {
                if (documento != null) { //Si "documento" no es nulo, quiere decir que encontró el usuario mediante el correo
                    String cuadrilla = (String) documento.get("Cuadrilla"); //Obtenemos la cuadrilla de "documento"

                    gast.obtenerGastos(true, cuadrilla, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<GastosItems>() {
                        @Override
                        public void onCallback(List<GastosItems> items) {
                            if (items != null)
                                inicializarRecyclerView(items);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(getContext(), "ERROR AL CARGAR LOS GASTOS", Toast.LENGTH_SHORT).show();
                            Log.w("ObtenerGastos", e);
                        }
                    });
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

    private void inicializarRecyclerView(List<GastosItems> items) {
        GastosAdapter adapter = new GastosAdapter(items); //Creamos un nuevo objeto de tipo GastosAdapter en el cual enviamos la lista "items", y dicho objeto lo igualamos al otro objeto de tipo GastosAdapter llamado "adapter"
        rvGastos.setAdapter(adapter); //Asignamos el adapter al recyclerView de Gastos
        double totalGastos = 0;

        for (GastosItems item : items) {
            totalGastos += item.getTotal();
        }

        lblTotalGastos.setText("L. " + String.format("%.2f", totalGastos));

        adapter.setOnClickListener(new View.OnClickListener() { //Usando el objeto de "adapter" llamamos al método "setOnClickListener" de la clase GastosAdapter
            @Override
            public void onClick(View view) { //Al dar clic en una tarjeta del RecyclerView, se realizará lo siguiente
                HashMap<String,Object> datosGasto = new HashMap<>(); //Creamos un HashMap para guardar los datos que se enviarán al siguiente Activity

                //Agregamos las claves y datos al HashMap
                datosGasto.put("ActivityDG", "DetalleGastoEmpleado");
                datosGasto.put("ID", items.get(rvGastos.getChildAdapterPosition(view)).getId());
                datosGasto.put("FechaHora", items.get(rvGastos.getChildAdapterPosition(view)).getFechaHora());
                datosGasto.put("Cuadrilla", items.get(rvGastos.getChildAdapterPosition(view)).getCuadrilla());
                datosGasto.put("LugarCompra", items.get(rvGastos.getChildAdapterPosition(view)).getLugarCompra());
                datosGasto.put("TipoCompra", items.get(rvGastos.getChildAdapterPosition(view)).getTipoCompra());
                datosGasto.put("Descripcion", items.get(rvGastos.getChildAdapterPosition(view)).getDescripcion());
                datosGasto.put("NumeroFactura", items.get(rvGastos.getChildAdapterPosition(view)).getNumeroFactura());
                datosGasto.put("Usuario", items.get(rvGastos.getChildAdapterPosition(view)).getUsuario());
                datosGasto.put("Rol", items.get(rvGastos.getChildAdapterPosition(view)).getRol());
                datosGasto.put("Total", items.get(rvGastos.getChildAdapterPosition(view)).getTotal());

                //Llamamos el método "iniciarActivityConDatos" de la clase Utilidades y le mandamos el contexto, el activity siguiente y el HashMap con los datos a enviar
                Utilidades.iniciarActivityConDatos(getActivity(), DetalleGasto.class, datosGasto);
            }
        });
    }
}