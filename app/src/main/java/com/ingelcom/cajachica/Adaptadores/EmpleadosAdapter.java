package com.ingelcom.cajachica.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ingelcom.cajachica.Modelos.EmpleadosItems;
import com.ingelcom.cajachica.R;

import java.util.List;

public class EmpleadosAdapter extends RecyclerView.Adapter<EmpleadosAdapter.RecyclerHolder> implements View.OnClickListener {

    private List<EmpleadosItems> items;
    private View.OnClickListener listener;

    public EmpleadosAdapter(List<EmpleadosItems> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_empleados, parent, false); //Inflamos la vista que utilizaremos para las tarjetas del RecyclerView
        view.setOnClickListener(this);
        return new RecyclerHolder(view); //Retornamos un nuevo objeto de tipo RecyclerHolder (La clase estática de abajo) y le mandamos la vista de la variable "view"
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {
        EmpleadosItems item = items.get(position); //Creamos una lista de tipo EmpleadosItems llamada "item" la cual igualamos a la otra lista "items" extrayendo posición por posición

        //Haciendo uso del objeto "holder", asignamos los textos a las diferentes variables que se encuentran en la clase estática "RecyclerHolder"
        holder.tvNombre.setText(item.getNombre());
        holder.tvCorreo.setText(item.getCorreo());
        holder.tvCuadrilla.setText(item.getCuadrilla());
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
        private TextView tvNombre;
        private TextView tvCorreo;
        private TextView tvCuadrilla;

        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);

            //Referenciamos los elementos de la vista de las tarjetas del RecyclerView a las variables de arriba
            tvNombre = itemView.findViewById(R.id.lblNombreApellidoEmp);
            tvCorreo = itemView.findViewById(R.id.lblCorreoEmp);
            tvCuadrilla = itemView.findViewById(R.id.lblCuadrillaEmp);
        }
    }
}
