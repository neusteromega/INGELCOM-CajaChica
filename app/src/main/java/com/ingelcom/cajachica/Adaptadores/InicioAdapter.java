package com.ingelcom.cajachica.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ingelcom.cajachica.R;

import java.util.List;

//Utilizamos <T> para definir que InicioAdapter es una clase genérica
public class InicioAdapter<T> extends RecyclerView.Adapter<InicioAdapter.RecyclerHolder> implements View.OnClickListener {

    private List<T> items; //Creamos una lista de tipo "T"
    private View.OnClickListener listener;

    //El constructor acepta una lista de cualquier tipo T.
    public InicioAdapter(List<T> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_inicio, parent, false); //Inflamos la vista que utilizaremos para las tarjetas del RecyclerView
        view.setOnClickListener(this);
        return new RecyclerHolder(view); //Retornamos un nuevo objeto de tipo RecyclerHolder (La clase estática de abajo) y le mandamos la vista de la variable "view"
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {
        T item = items.get(position); //Creamos un objeto de tipo "T" llamado "item" el cual igualamos a la lista "items" extrayendo posición por posición

        //Haciendo uso del objeto "holder", asignamos los textos a las diferentes variables que se encuentran en la clase estática "RecyclerHolder"
        //holder.tvCuadrilla.setText(item.);
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
        private TextView tvTipoCompra;
        private TextView tvCantidad;

        public RecyclerHolder(@NonNull View itemView) { //Método Constructor que recibe un View
            super(itemView);

            //Referenciamos los elementos de la vista de las tarjetas del RecyclerView a las variables de arriba
            tvCuadrilla = itemView.findViewById(R.id.lblNombreCuadrillaInicio);
            tvTipoCompra = itemView.findViewById(R.id.lblTipoCompraInicio);
            tvCantidad = itemView.findViewById(R.id.lblCantidadInicio);
        }
    }
}
