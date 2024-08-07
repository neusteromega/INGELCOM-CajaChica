package com.ingelcom.cajachica.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ingelcom.cajachica.Modelos.IngresosItems;
import com.ingelcom.cajachica.R;

import java.util.List;

public class IngresosAdapter extends RecyclerView.Adapter<IngresosAdapter.RecyclerHolder> implements View.OnClickListener {

    private List<IngresosItems> items; //Creamos una lista de tipo "IngresosItems"
    private View.OnClickListener listener; //Creamos un escuchador (listener) de tipo "View.OnClickListener" que nos servirá para el onClick de cada tarjeta del RecyclerView
    private String tipoListado; //Variable que nos ayudará a saber si el listado mostrado tiene todos los ingresos o si está filtrado por una cuadrilla

    public IngresosAdapter(List<IngresosItems> items, String tipoListado) {
        this.items = items;
        this.tipoListado = tipoListado;
    }

    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_ingresos_deducciones, parent, false); //Inflamos la vista que utilizaremos para las tarjetas del RecyclerView
        view.setOnClickListener(this);
        return new RecyclerHolder(view); //Retornamos un nuevo objeto de tipo RecyclerHolder (La clase estática de abajo) y le mandamos la vista de la variable "view"
    }

    //Este método se ejecuta las veces que el método "getItemCount" lo indique, o sea, dependiendo del size de la lista "items"
    @Override
    public void onBindViewHolder(@NonNull IngresosAdapter.RecyclerHolder holder, int position) {
        IngresosItems item = items.get(position); //Creamos un objeto de tipo "IngresosItems" llamado "item" el cual igualamos a la lista "items" extrayendo posición por posición

        //Haciendo uso del objeto "holder", asignamos los textos a las diferentes variables que se encuentran en la clase estática "RecyclerHolder"
        if (tipoListado.equalsIgnoreCase("ListadoIngresosTodos")) { //Que ingrese a este if si estamos visualizando todos los ingresos sin filtro
            holder.tvCuadrilla.setText(item.getCuadrilla());
            holder.tvFecha.setText(item.getFechaHora());
            holder.tvTotal.setText("L. " + String.format("%.2f", item.getTotal()));
            holder.imgExpandir.setImageResource(R.mipmap.ico_azul_expandir);

            //Ocultamos estos dos elementos
            holder.tvTransfTitulo.setVisibility(View.GONE);
            holder.tvTransferencia.setVisibility(View.GONE);
        }
        else if (tipoListado.equalsIgnoreCase("ListadoIngresosAdmin") || tipoListado.equalsIgnoreCase("ListadoIngresosEmpleado")) { //Que ingrese a este else if si estamos visualizando todos los ingresos filtrados por una cuadrilla
            holder.tvTransferencia.setText(item.getTransferencia());
            holder.tvFecha.setText(item.getFechaHora());
            holder.tvTotal.setText("L. " + String.format("%.2f", item.getTotal()));
            holder.tvTransfTitulo.setText("No. Transferencia:");
            holder.imgExpandir.setImageResource(R.mipmap.ico_azul_expandir);

            //Ocultamos estos dos elementos
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
        private TextView tvCuadrilla;
        private TextView tvTransferencia;
        private TextView tvFecha;
        private TextView tvTotal;
        private TextView tvCuadrillaTitulo;
        private TextView tvTransfTitulo;
        private ImageView imgExpandir;

        public RecyclerHolder(@NonNull View itemView) { //Método Constructor que recibe un View
            super(itemView);

            //Referenciamos los elementos de la vista de las tarjetas del RecyclerView a las variables de arriba
            tvCuadrilla = itemView.findViewById(R.id.lblCuadrillaIng);
            tvTransferencia = itemView.findViewById(R.id.lblTransfUserIng);
            tvFecha = itemView.findViewById(R.id.lblFechaIng);
            tvTotal = itemView.findViewById(R.id.lblTotalIng);
            tvCuadrillaTitulo = itemView.findViewById(R.id.lblCuadrillaTituloIng);
            tvTransfTitulo = itemView.findViewById(R.id.lblTransfUserTituloIng);
            imgExpandir = itemView.findViewById(R.id.imgExpandirEditarIng);
        }
    }
}
