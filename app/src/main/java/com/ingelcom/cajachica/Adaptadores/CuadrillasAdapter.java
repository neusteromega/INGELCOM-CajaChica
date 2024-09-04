package com.ingelcom.cajachica.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ingelcom.cajachica.Modelos.CuadrillasItems;
import com.ingelcom.cajachica.R;

import java.util.ArrayList;
import java.util.List;

public class CuadrillasAdapter extends BaseAdapter {

    private List<CuadrillasItems> items;
    private Context contexto;

    //Método constructor que recibe la lista de tipo "CuadrillasItems" y un contexto
    public CuadrillasAdapter(List<CuadrillasItems> items, Context contexto) {
        this.items = items;
        this.contexto = contexto;
    }

    @Override //Método que devuelve la cantidad de elementos de "items", esto nos permite saber cuántos elementos se mostrarán en el GridView
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        //Inflamos (creamos) una nueva vista desde el archivo XML "items_cuadrillas"
        LayoutInflater inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.items_cuadrillas, null);

        //Obtenemos los TextViews de la vista inflada
        TextView tvCuadrilla = view.findViewById(R.id.lblNombreCuadrillaCuaItem);
        TextView tvDinero = view.findViewById(R.id.lblDineroCuaItem);

        //Asignamos los datos a los TextViews
        tvCuadrilla.setText(items.get(i).getCuadrilla());
        tvDinero.setText("L. " + String.format("%.2f", items.get(i).getDinero()));

        return view;
    }
}
