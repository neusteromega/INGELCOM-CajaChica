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

    private List<EmpleadosItems> items; //Creamos una lista de tipo "EmpleadosItems"
    private View.OnClickListener listener; //Creamos un escuchador (listener) de tipo "View.OnClickListener" que nos servirá para el onClick de cada tarjeta del RecyclerView

    public EmpleadosAdapter(List<EmpleadosItems> items) {
        this.items = items;
    }

    @NonNull
    @Override //Método que se llama cuando el RecyclerView necesita crear una nueva vista (tarjeta) para un elemento de la lista
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_empleados, parent, false); //Inflamos la vista que utilizaremos para las tarjetas del RecyclerView
        view.setOnClickListener(this); //Asignamos el listener this a la vista inflada, lo que significa que la clase DeduccionesAdapter manejará los clics en las tarjetas
        return new RecyclerHolder(view); //Retornamos un nuevo objeto de tipo RecyclerHolder (La clase estática de abajo) y le mandamos la vista de la variable "view"
    }

    @Override //Este método se llama para "vincular" los datos de un elemento específico de la lista con la vista correspondiente. Se ejecuta las veces que el método "getItemCount" lo indique, o sea, dependiendo del size de la lista "items"
    public void onBindViewHolder(@NonNull EmpleadosAdapter.RecyclerHolder holder, int position) {
        EmpleadosItems item = items.get(position); //Creamos un objeto de tipo "EmpleadosItems" llamado "item" el cual igualamos a la lista "items" extrayendo posición por posición

        //Haciendo uso del objeto "holder", asignamos los textos a las diferentes variables que se encuentran en la clase estática "RecyclerHolder"
        holder.tvNombre.setText(item.getNombre());
        holder.tvCorreo.setText(item.getCorreo());
        holder.tvCuadrilla.setText(item.getCuadrilla());
    }

    @Override //Método que le indica al RecyclerView cuántas tarjetas debe crear
    public int getItemCount() {
        return items.size(); //Retornamos la cantidad de elementos que tiene la lista "items"
    }

    //Método que se usa para permitir que una actividad o fragmento que contenga el RecyclerView maneje los clics en las tarjetas
    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override //Método se ejecuta cuando se hace clic en una tarjeta del RecyclerView. Si se ha establecido un listener, se ejecuta su método "onClick"
    public void onClick(View view) {
        if (listener != null) {
            listener.onClick(view);
        }
    }

    //Clase interna se utiliza para mantener referencias a los elementos gráficos de una tarjeta del RecyclerView, lo que permite asignarles valores en "onBindViewHolder"
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
