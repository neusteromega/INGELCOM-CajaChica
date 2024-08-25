package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ingelcom.cajachica.Fragmentos.FragAdmCuadrillas;
import com.ingelcom.cajachica.Fragmentos.FragAdmEstadisticas;
import com.ingelcom.cajachica.Fragmentos.FragAdmInicio;
import com.ingelcom.cajachica.Fragmentos.FragAdmRegistrar;
import com.ingelcom.cajachica.Fragmentos.FragAdmUsuarios;
import com.ingelcom.cajachica.Herramientas.Utilidades;

public class AdmPantallas extends AppCompatActivity {

    private ImageView imgInicio, imgEstadisticas, imgRegistrar, imgCuadrillas, imgUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adm_pantallas);

        imgInicio = findViewById(R.id.imgInicio);
        imgEstadisticas = findViewById(R.id.imgEstadisticas);
        imgRegistrar = findViewById(R.id.imgRegistrar);
        imgCuadrillas = findViewById(R.id.imgCuadrillas);
        imgUsuarios = findViewById(R.id.imgUsuarios);

        //Recibimos un texto del activity anterior, que en este caso puede ser "ListadoEmpleados"
        String activityAnterior = Utilidades.obtenerStringExtra(this, "ListadoEmpleados"); //Si el texto guardado en la variable "activityAnterior" es nulo, significa que el activity anterior no fue "ListadoEmpleados", ya que sólo desde ahí se envié el texto

        //Si "activityAnterior" no es nulo, y si contiene el texto "SI" significa que al activity anterior fue "ListadoEmpleados", por lo tanto, que entre al if y establecemos el fragmento "FragAdmUsuarios"
        if (activityAnterior != null && activityAnterior.equalsIgnoreCase("SI")) {
            replaceFragment(new FragAdmUsuarios()); //Llamamos al método "replaceFragment" y le mandamos el "FragAdmUsuarios"
            //Cambios de iconos de la barra inferior llamando a "asignarIconos"
            asignarIconos(
                    R.mipmap.ico_azul_iniciovacio,
                    R.mipmap.ico_azul_estadisticasvacio,
                    R.mipmap.ico_azul_registrarvacio,
                    R.mipmap.ico_azul_cuadrillasvacio,
                    R.mipmap.ico_azul_usuariolleno);
        }
    }

    public void verInicio(View view) {
        replaceFragment(new FragAdmInicio()); //Llamamos al método "replaceFragment" y le mandamos el "FragAdmInicio"
        //Cambios de iconos de la barra inferior llamando a "asignarIconos"
        asignarIconos(
                R.mipmap.ico_azul_iniciolleno,
                R.mipmap.ico_azul_estadisticasvacio,
                R.mipmap.ico_azul_registrarvacio,
                R.mipmap.ico_azul_cuadrillasvacio,
                R.mipmap.ico_azul_usuariovacio);
    }

    public void verEstadisticas(View view) {
        replaceFragment(new FragAdmEstadisticas()); //Llamamos al método "replaceFragment" y le mandamos el "FragAdmEstadisticas"
        //Cambios de iconos de la barra inferior llamando a "asignarIconos"
        asignarIconos(
                R.mipmap.ico_azul_iniciovacio,
                R.mipmap.ico_azul_estadisticaslleno,
                R.mipmap.ico_azul_registrarvacio,
                R.mipmap.ico_azul_cuadrillasvacio,
                R.mipmap.ico_azul_usuariovacio);
    }

    public void verRegistrar(View view) {
        replaceFragment(new FragAdmRegistrar()); //Llamamos al método "replaceFragment" y le mandamos el "FragAdmRegistrar"
        //Cambios de iconos de la barra inferior llamando a "asignarIconos"
        asignarIconos(
                R.mipmap.ico_azul_iniciovacio,
                R.mipmap.ico_azul_estadisticasvacio,
                R.mipmap.ico_azul_registrarlleno,
                R.mipmap.ico_azul_cuadrillasvacio,
                R.mipmap.ico_azul_usuariovacio);
    }

    public void verCuadrillas(View view) {
        replaceFragment(new FragAdmCuadrillas()); //Llamamos al método "replaceFragment" y le mandamos el "FragAdmCuadrillas"
        //Cambios de iconos de la barra inferior llamando a "asignarIconos"
        asignarIconos(
                R.mipmap.ico_azul_iniciovacio,
                R.mipmap.ico_azul_estadisticasvacio,
                R.mipmap.ico_azul_registrarvacio,
                R.mipmap.ico_azul_cuadrillaslleno,
                R.mipmap.ico_azul_usuariovacio);
    }

    public void verUsuarios(View view) {
        replaceFragment(new FragAdmUsuarios()); //Llamamos al método "replaceFragment" y le mandamos el "FragAdmUsuarios"
        //Cambios de iconos de la barra inferior llamando a "asignarIconos"
        asignarIconos(
                R.mipmap.ico_azul_iniciovacio,
                R.mipmap.ico_azul_estadisticasvacio,
                R.mipmap.ico_azul_registrarvacio,
                R.mipmap.ico_azul_cuadrillasvacio,
                R.mipmap.ico_azul_usuariolleno);
    }

    @Override
    public void onBackPressed() { //Método que define qué hace la app al presionar el botón de retroceso del celular
        Fragment fragmentActual = getSupportFragmentManager().findFragmentById(R.id.fragAdmPantallas); //Obtenemos el fragment actual y lo guardamos en la variable "fragmentActual"

        if (fragmentActual instanceof FragAdmInicio) { //Si el fragmentActual es "FragAdmInicio" que entre al if
            finishAffinity(); //Cierra toda la pila de retroceso para que al dar clic en el botón de retroceso, ya no hayan activities y salga de la app
        }
        else { //Si el fragmentActual no es "FragAdmInicio", al dar clic en el botón de retroceso, que dirija a este fragment
            replaceFragment(new FragAdmInicio()); //Llamamos al método "replaceFragment" y le mandamos el "FragAdmInicio"
            //Cambios de iconos de la barra inferior llamando a "asignarIconos"
            asignarIconos(
                    R.mipmap.ico_azul_iniciolleno,
                    R.mipmap.ico_azul_estadisticasvacio,
                    R.mipmap.ico_azul_registrarvacio,
                    R.mipmap.ico_azul_cuadrillasvacio,
                    R.mipmap.ico_azul_usuariovacio);
        }
    }

    private void asignarIconos(int resInicio, int resEstadisticas, int resRegistrar, int resCuadrillas, int resUsuarios) {
        imgInicio.setImageResource(resInicio);
        imgEstadisticas.setImageResource(resEstadisticas);
        imgRegistrar.setImageResource(resRegistrar);
        imgCuadrillas.setImageResource(resCuadrillas);
        imgUsuarios.setImageResource(resUsuarios);
    }

    //POSIBLE CAMBIO A CLASE DE UTILIDADES
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager(); //Creamos un objeto de FragmentManager, que es responsable de gestionar los fragmentos dentro de una actividad
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); //Creamos un objeto de FragmentTransaction que permite realizar operaciones como agregar, eliminar, reemplazar, etc., a los fragmentos
        fragmentTransaction.replace(R.id.fragAdmPantallas, fragment); //Reeemplazamos el fragmento en el "fragAdmPantallas" por el nuevo fragmento guardado en la variable "fragment"
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit(); //Confirmamos el "commit" para que la transacción se ejecute y los cambios se reflejen en la interfaz de usuario
    }
}