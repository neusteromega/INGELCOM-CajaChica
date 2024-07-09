package com.ingelcom.cajachica.Herramientas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.ingelcom.cajachica.R;

import java.util.ArrayList;
import java.util.List;

public class Utilidades {

    //Método que permita abrir un nuevo Activity, y si es necesario, finalizar el activity actual
    public static void iniciarActivity(Context contexto, Class<?> activityClase, boolean finalizarActivity) {
        Intent intent = new Intent(contexto, activityClase); //Creamos el intent y le establecemos el contexto y el nombre del activity
        contexto.startActivity(intent); //Iniciamos el activity

        //Si el contexto es una instancia de un Activity y "finalizarActivity" es verdadero, cierra la actividad actual
        if (finalizarActivity && contexto instanceof Activity) {
            ((Activity) contexto).finish();
        }
    }

    //Método que permite enviar un dato String a un activity
    public static void iniciarActivityConDatos(Context contexto, Class<?> activityClase, String clave, String valor) {
        Intent intent = new Intent(contexto, activityClase); //Creamos el intent y le establecemos el contexto y el nombre del activity
        intent.putExtra(clave, valor); //Usando "putExtra" le establecemos una clave al envío de datos, y le mandamos el texto guardado en "valor"
        contexto.startActivity(intent); //Iniciamos el activity
    }

    //Método para obtener el texto (String) de un putExtra
    public static String obtenerStringExtra(Activity activity, String clave) {
        Intent intent = activity.getIntent(); //Creamos el intent usando el Activity que se recibe como parámetro (desde el activity, ponemos "this" para enviar este parámetro)

        //Si el intent no es nulo, y si la clave guardada en "clave" contiene datos (hasExtra(clave)), que entre al if
        if (intent != null && intent.hasExtra(clave)) {
            return intent.getStringExtra(clave); //Retornamos el texto extraído
        }

        return null; //Si no entra al if, que retorne un null
    }

    /*public static void spinnerConHint(Context contexto, Spinner spinner, List<String> lista, String nombreHint) {
        List<String> listaHint = new ArrayList<>();
        listaHint.add(nombreHint);
        listaHint.addAll(lista);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, R.layout.spinner_items, listaHint) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0; // Deshabilitar el primer elemento, en este caso, el hint
            }

            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;

                if (position == 0) {
                    tv.setTextColor(contexto.getResources().getColor(android.R.color.darker_gray)); // Cambiar el color del hint
                }

                return view;
            }
        };

        spinner.setAdapter(adapter);
        spinner.setSelection(0); // Mostrar el hint inicialmente
    }*/
}
