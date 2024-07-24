package com.ingelcom.cajachica.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ingelcom.cajachica.Modelos.DeduccionesItems;
import com.ingelcom.cajachica.Modelos.IngresosItems;
import com.ingelcom.cajachica.R;

import java.util.List;

public class DeduccionesAdapter extends RecyclerView.Adapter<DeduccionesAdapter.RecyclerHolder> implements View.OnClickListener {

    private List<DeduccionesItems> items; //Creamos una lista de tipo "IngresosItems"
    private View.OnClickListener listener; //Creamos un escuchador (listener) de tipo "View.OnClickListener" que nos servirá para el onClick de cada tarjeta del RecyclerView

    public DeduccionesAdapter(List<DeduccionesItems> items) {
        this.items = items;
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
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {
        DeduccionesItems item = items.get(position); //Creamos un objeto de tipo "DeduccionesItems" llamado "item" el cual igualamos a la lista "items" extrayendo posición por posición

        //Haciendo uso del objeto "holder", asignamos los textos a las diferentes variables que se encuentran en la clase estática "RecyclerHolder"
        holder.tvUsuario.setText(item.getUsuario());
        holder.tvFecha.setText(item.getFechaHora());
        holder.tvTotal.setText("L. " + String.format("%.2f", item.getTotal()));
        holder.tvUsuarioTitulo.setText("Usuario:");
        holder.imgEditar.setImageResource(R.mipmap.ico_azul_editar);
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
        private TextView tvUsuario;
        private TextView tvFecha;
        private TextView tvTotal;
        private TextView tvUsuarioTitulo;
        private ImageView imgEditar;

        public RecyclerHolder(@NonNull View itemView) { //Método Constructor que recibe un View
            super(itemView);

            //Referenciamos los elementos de la vista de las tarjetas del RecyclerView a las variables de arriba
            tvUsuario = itemView.findViewById(R.id.lblTransfUserIng);
            tvFecha = itemView.findViewById(R.id.lblFechaIng);
            tvTotal = itemView.findViewById(R.id.lblTotalIng);
            tvUsuarioTitulo = itemView.findViewById(R.id.lblTransfUserTituloIng);
            imgEditar = itemView.findViewById(R.id.imgExpandirEditarIng);
        }
    }
}
