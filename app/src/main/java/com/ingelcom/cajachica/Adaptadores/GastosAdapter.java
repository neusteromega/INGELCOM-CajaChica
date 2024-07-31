package com.ingelcom.cajachica.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ingelcom.cajachica.Modelos.GastosItems;
import com.ingelcom.cajachica.R;

import java.util.List;

public class GastosAdapter extends RecyclerView.Adapter<GastosAdapter.RecyclerHolder> implements View.OnClickListener {

    private List<GastosItems> items; //Creamos una lista de tipo "GastosItems"
    private View.OnClickListener listener; //Creamos un escuchador (listener) de tipo "View.OnClickListener" que nos servirá para el onClick de cada tarjeta del RecyclerView
    private String tipoListado; //Variable que nos ayudará a saber si el listado mostrado tiene todos los gastos o si está filtrado por una cuadrilla

    public GastosAdapter(List<GastosItems> items, String tipoListado) { //Método Constructor en el cual inicializamos la lista "items"
        this.items = items;
        this.tipoListado = tipoListado;
    }

    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_gastos, parent, false); //Inflamos la vista que utilizaremos para las tarjetas del RecyclerView
        view.setOnClickListener(this);
        return new RecyclerHolder(view); //Retornamos un nuevo objeto de tipo RecyclerHolder (La clase estática de abajo) y le mandamos la vista de la variable "view"
    }

    //Este método se ejecuta las veces que el método "getItemCount" lo indique, o sea, dependiendo del size de la lista "items"
    @Override
    public void onBindViewHolder(@NonNull GastosAdapter.RecyclerHolder holder, int position) {
        GastosItems item = items.get(position); //Creamos un objeto de tipo "GastosItems" llamado "item" el cual igualamos a la lista "items" extrayendo posición por posición

        //Haciendo uso del objeto "holder", asignamos los textos a las diferentes variables que se encuentran en la clase estática "RecyclerHolder"
        if (tipoListado.equalsIgnoreCase("ListadoGastosTodos")) { //Que ingrese a este if si estamos visualizando todos los gastos sin filtro
            holder.tvTipoCompra.setText(item.getTipoCompra());
            holder.tvFecha.setText(item.getFechaHora());
            holder.tvUsuario.setText(item.getUsuario());
            holder.tvCuadrilla.setText(item.getCuadrilla());
            holder.tvTotal.setText("L. " + String.format("%.2f", item.getTotal()));
        }
        else if (tipoListado.equalsIgnoreCase("ListadoGastosAdmin") || tipoListado.equalsIgnoreCase("ListadoGastosEmpleado")) { //Que ingrese a este else if si estamos visualizando todos los gastos filtrados por una cuadrilla
            holder.tvTipoCompra.setText(item.getTipoCompra());
            holder.tvFecha.setText(item.getFechaHora());
            holder.tvUsuario.setText(item.getUsuario());
            holder.tvTotal.setText("L. " + String.format("%.2f", item.getTotal()));

            //Ocultamos estos elementos
            holder.tvCuadrillaTitulo.setVisibility(View.GONE);
            holder.tvCuadrilla.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size(); //Retornamos la cantidad de elementos que tiene la lista "items"
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            listener.onClick(view);
        }
    }

    public static class RecyclerHolder extends RecyclerView.ViewHolder {
        //Variables para cada elemento cambiante de las tarjetas del RecyclerView
        private TextView tvTipoCompra;
        private TextView tvFecha;
        private TextView tvCuadrilla;
        private TextView tvUsuario;
        private TextView tvTotal;
        private TextView tvCuadrillaTitulo;

        public RecyclerHolder(@NonNull View itemView) { //Método Constructor que recibe un View
            super(itemView);

            //Referenciamos los elementos de la vista de las tarjetas del RecyclerView a las variables de arriba
            tvTipoCompra = itemView.findViewById(R.id.lblTipoCompraGst);
            tvFecha = itemView.findViewById(R.id.lblFechaGst);
            tvCuadrilla = itemView.findViewById(R.id.lblCuadrillaGst);
            tvUsuario = itemView.findViewById(R.id.lblEmpleadoCuadrillaGst);
            tvTotal = itemView.findViewById(R.id.lblTotalGst);
            tvCuadrillaTitulo = itemView.findViewById(R.id.lblCuadrillaTituloGst);
        }
    }
}
