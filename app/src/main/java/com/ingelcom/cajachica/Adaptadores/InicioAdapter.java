package com.ingelcom.cajachica.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ingelcom.cajachica.Modelos.CuadrillasItems;
import com.ingelcom.cajachica.Modelos.GastosItems;
import com.ingelcom.cajachica.Modelos.IngresosItems;
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

        if (item instanceof IngresosItems) { //Verificamos si "item" es una instancia de "IngresosItems"
            IngresosItems ingreso = (IngresosItems) item; //Creamos un objeto de tipo "IngresosItems" y le asignamos el contenido de "item" el cual tiene valor "T", así que los casteamos para que sea de tipo "IngresosItems"

            //Haciendo uso del objeto "holder", asignamos los textos a las diferentes variables que se encuentran en la clase estática "RecyclerHolder"
            holder.tvCuadrilla.setText(ingreso.getCuadrilla());
            holder.tvFechaHora.setText(ingreso.getFechaHora());
            holder.tvCantidad.setText("L. " + String.format("%.2f", ingreso.getTotal()));

            //Ocultamos el "tvTipoCompra" que no se usará en el listado de Ingresos Recientes, y asignamos el color verde al "tvCantidad"
            holder.tvTipoCompra.setVisibility(View.GONE);
            holder.tvCantidad.setTextColor(holder.itemView.getContext().getColor(R.color.clr_fuente_ingresos));
        }
        else if (item instanceof GastosItems) { //Verificamos si "item" es una instancia de "GastosItems"
            GastosItems gasto = (GastosItems) item; //Creamos un objeto de tipo "GastosItems" y le asignamos el contenido de "item" el cual tiene valor "T", así que los casteamos para que sea de tipo "GastosItems"

            //Haciendo uso del objeto "holder", asignamos los textos a las diferentes variables que se encuentran en la clase estática "RecyclerHolder"
            holder.tvCuadrilla.setText(gasto.getCuadrilla());
            holder.tvTipoCompra.setText(gasto.getTipoCompra());
            holder.tvFechaHora.setText(gasto.getFechaHora());
            holder.tvCantidad.setText("L. " + String.format("%.2f", gasto.getTotal()));

            //Asignamos el color rojo al "tvCantidad"
            holder.tvCantidad.setTextColor(holder.itemView.getContext().getColor(R.color.clr_fuente_gastos));
        }
        else if (item instanceof CuadrillasItems) { //Verificamos si "item" es una instancia de "CuadrillasItems"
            CuadrillasItems cuadrilla = (CuadrillasItems) item; //Creamos un objeto de tipo "CuadrillasItems" y le asignamos el contenido de "item" el cual tiene valor "T", así que los casteamos para que sea de tipo "CuadrillasItems"

            //Haciendo uso del objeto "holder", asignamos los textos a las diferentes variables que se encuentran en la clase estática "RecyclerHolder"
            holder.tvCuadrilla.setText(cuadrilla.getCuadrilla());
            holder.tvCantidad.setText("L. " + String.format("%.2f", cuadrilla.getDinero()));

            //Ocultamos el "tvTipoCompra" que no se usará en el listado de Dinero Disponible de Cuadrillas. Aquí no asignamos color al "tvCantidad" ya que por defecto tiene el color lila deseado
            holder.tvTipoCompra.setVisibility(View.GONE);
            holder.tvFechaHora.setVisibility(View.GONE);
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
        private TextView tvTipoCompra;
        private TextView tvFechaHora;
        private TextView tvCantidad;

        public RecyclerHolder(@NonNull View itemView) { //Método Constructor que recibe un View
            super(itemView);

            //Referenciamos los elementos de la vista de las tarjetas del RecyclerView a las variables de arriba
            tvCuadrilla = itemView.findViewById(R.id.lblNombreCuadrillaInicio);
            tvTipoCompra = itemView.findViewById(R.id.lblTipoCompraInicio);
            tvFechaHora = itemView.findViewById(R.id.lblFechaInicio);
            tvCantidad = itemView.findViewById(R.id.lblCantidadInicio);
        }
    }
}
