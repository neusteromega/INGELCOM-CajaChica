package com.ingelcom.cajachica.Adaptadores;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ingelcom.cajachica.Fragmentos.FragGastosCuadrilla;
import com.ingelcom.cajachica.Fragmentos.FragGastosSupervisores;

public class VPGastosAdapter extends FragmentStateAdapter {

    //Método Constructor que toma como parámetro el contexto (this) del activity donde se encuentra el ViewPager, en este caso, el activity es el "ListadoGastos.java"
    public VPGastosAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) { //Método que crea y devuelve un fragmento basado en la posición actual del ViewPager.
        switch (position) { //Este Switch sirve para determinar qué fragmento crear basado en la posición del ViewPager. Al arrastrar con el dedo el ViewPager, este toma una nueva posición y dependiendo esa posición, retornamos el fragmento correspondiente
            case 0:
                return new FragGastosCuadrilla();
            case 1:
                return new FragGastosSupervisores();
            default:
                return new FragGastosCuadrilla();
        }
    }

    @Override
    public int getItemCount() { //Método que devuelve el número de elementos (fragmentos) que el adaptador manejará
        return 2; //Devuelve el número de fragmentos que vamos a manejar. En este caso, son 2 fragmentos (FragGastosCuadrilla y FragGastosSupervisores).
    }
}
