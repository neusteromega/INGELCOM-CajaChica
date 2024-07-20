package com.ingelcom.cajachica.Adaptadores;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ingelcom.cajachica.Fragmentos.FragGastosCuadrilla;
import com.ingelcom.cajachica.Fragmentos.FragGastosSupervisores;

public class VPGastosAdapter extends FragmentStateAdapter {

    private String nombreCuadrilla; //Dato que será recibo en los fragmentos implementados en este ViewPager2 (FragGastosCuadrilla y FragGastosSupervisores)

    //Método Constructor que toma como parámetro el contexto (this) del activity donde se encuentra el ViewPager, en este caso, el activity es el "ListadoGastos.java"; y el nombre de la cuadrilla que se recibe para saber cuáles gastos mostrar
    public VPGastosAdapter(@NonNull FragmentActivity fragmentActivity, String nombreCuadrilla) {
        super(fragmentActivity);
        this.nombreCuadrilla = nombreCuadrilla;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) { //Método que crea y devuelve un fragmento basado en la posición actual del ViewPager.
        switch (position) { //Este Switch sirve para determinar qué fragmento crear basado en la posición del ViewPager. Al arrastrar con el dedo el ViewPager, este toma una nueva posición y dependiendo esa posición, retornamos el fragmento correspondiente
            //En estos 3 returns, llamamos el método "newInstance" de los fragmentos y le mandamos el "nombreCuadrilla"
            case 0:
                return FragGastosCuadrilla.newInstance(nombreCuadrilla);
            case 1:
                return FragGastosSupervisores.newInstance(nombreCuadrilla);
            default:
                return FragGastosCuadrilla.newInstance(nombreCuadrilla);
        }
    }

    @Override
    public int getItemCount() { //Método que devuelve el número de elementos (fragmentos) que el adaptador manejará
        return 2; //Devuelve el número de fragmentos que vamos a manejar. En este caso, son 2 fragmentos (FragGastosCuadrilla y FragGastosSupervisores).
    }
}
