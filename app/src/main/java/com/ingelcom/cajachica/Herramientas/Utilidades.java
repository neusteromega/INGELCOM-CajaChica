package com.ingelcom.cajachica.Herramientas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ingelcom.cajachica.AdmPantallas;
import com.ingelcom.cajachica.DAO.FirestoreOperaciones;
import com.ingelcom.cajachica.EmpMenuPrincipal;
import com.ingelcom.cajachica.IniciarSesion;
import com.ingelcom.cajachica.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Utilidades {

    private FirestoreOperaciones oper = new FirestoreOperaciones();

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

    //Método que permite mostrar y ocultar una contraseña
    public static int mostrarOcultarContrasena(int clicks, EditText txtContra, ImageView imgContra) { //Recibe como parámetros la cantidad de clicks, el EditText de la contraseña, y el ImageView de ver y ocultar la contraseña
        //En este if verificamos si la variable "clicks" es divisible entre 2 y si al realizar la división su residuo es 1
        if (clicks % 2 == 1) { //La cantidad de clicks al principio es 0 (Lo inicializamos en 0 en la clase que llama a este método)
            //Si el residuo del número de "cantidadClicks" al dividirlo entre 2 es 1, se ocultará la contraseña y se mostrará el icono del ojo normal
            txtContra.setTransformationMethod(PasswordTransformationMethod.getInstance()); //El EditText ofrece diferentes formas de transformar el texto que se muestra. La clase "PasswordTransformationMethod" es una implementación de "TransformationMethod" diseñada específicamente para ocultar contraseñas al reemplazar el texto visible con caracteres ocultos, como puntos o asteriscos
            imgContra.setImageResource(R.mipmap.ico_azul_mostrarcontrasena); //Cambiamos el icono del ojo
        }
        else {
            //Si el residuo del número de "clicks" al dividirlo entre 2 no es 0, se mostrará la contraseña y también el icono cambiará al ojo tachado
            txtContra.setTransformationMethod(null);
            imgContra.setImageResource(R.mipmap.ico_azul_ocultarcontrasena); //Cambiamos el icono del ojo
        }

        clicks++; //Aquí vamos aumentando la cantidad de clicks cada vez que se entre al método
        txtContra.setSelection(txtContra.getText().length()); //Mover el cursor al final del texto
        return clicks; //Retornamos la cantidad de Clicks
    }

    public void redireccionarUsuario(Context contexto, String correoInicial) {
        oper.obtenerUnRegistro("usuarios", "Correo", correoInicial, new FirestoreOperaciones.FirestoreDocumentCallback() {
            @Override
            public void onCallback(Map<String, Object> documento) {
                if (documento != null) { //Si el HashMap "documento" no es nulo, quiere decir que si se encontró el registro en la colección, por lo tanto, entrará al if
                    String rol = (String) documento.get("Rol"); //Extraemos el rol del HashMap "documento"

                    if (rol.contentEquals("Administrador")) {
                        Utilidades.iniciarActivity(contexto, AdmPantallas.class, true);
                        //Toast.makeText(IniciarSesion.this, "BIENVENIDO", Toast.LENGTH_SHORT).show();
                    }
                    else if (rol.contentEquals("Empleado")) {
                        Utilidades.iniciarActivity(contexto, EmpMenuPrincipal.class, true);
                        //Toast.makeText(IniciarSesion.this, "BIENVENIDO", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(contexto, "ERROR AL OBTENER EL ROL DEL USUARIO", Toast.LENGTH_SHORT).show();
                    }
                }
                else { //Si "documento" es nulo, no se encontró el registro en la colección, y entrará en este else
                    Toast.makeText(contexto, "NO SE ENCONTRÓ EL USUARIO", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.w("Activity", "Error al obtener los roles.", e);
            }
        });
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
