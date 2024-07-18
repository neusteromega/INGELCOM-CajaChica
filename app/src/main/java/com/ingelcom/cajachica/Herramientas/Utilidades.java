package com.ingelcom.cajachica.Herramientas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ingelcom.cajachica.AdmPantallas;
import com.ingelcom.cajachica.DAO.FirestoreOperaciones;
import com.ingelcom.cajachica.EmpMenuPrincipal;
import com.ingelcom.cajachica.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Utilidades {

    private static FirestoreOperaciones oper = new FirestoreOperaciones(); //Instancia de la clase "FirestoreOperaciones"

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
    public static void iniciarActivityConString(Context contexto, Class<?> activityClase, String clave, String valor, boolean finalizarActivity) {
        Intent intent = new Intent(contexto, activityClase); //Creamos el intent y le establecemos el contexto y el nombre del activity
        intent.putExtra(clave, valor); //Usando "putExtra" le establecemos una clave al envío de datos, y le mandamos el texto guardado en "valor"
        contexto.startActivity(intent); //Iniciamos el activity

        //Si el contexto es una instancia de un Activity y "finalizarActivity" es verdadero, cierra la actividad actual
        if (finalizarActivity && contexto instanceof Activity) {
            ((Activity) contexto).finish();
        }
    }

    public static void iniciarActivityConDatos(Context contexto, Class<?> activityClase, HashMap<String,Object> datos) {
        Intent intent = new Intent(contexto, activityClase);

        for (Map.Entry<String,Object> dato : datos.entrySet()) {
            String clave = dato.getKey();
            Object valor = dato.getValue();

            if (valor instanceof String) {
                String valorString = (String) valor;
                intent.putExtra(clave, valorString);
            }
        }

        contexto.startActivity(intent);
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

    //Método que nos ayuda a obtener el usuario actual
    public static FirebaseUser obtenerUsuario() {
        FirebaseAuth mAuth; //Objeto que verifica la autenticación del usuario con Firebase
        mAuth = FirebaseAuth.getInstance(); //Instanciamos el "mAuth"
        FirebaseUser currentUser = mAuth.getCurrentUser(); //Obtenemos el usuario actual

        return currentUser; //Retornamos el usuario actual
    }

    //Método que permite convertir una variable de tipo "Object" a "double", pero la variable "Object" debe tener un número para lograr efectuar la conversión
    public static double convertirObjectADouble(Object valor) {
        double valorConvertido = 0.0; //Creamos una variable double donde se guardará la conversión de "valor"

        try {
            if (valor instanceof Long) //Verificamos si "valor" es una instancia de Long
                valorConvertido = ((Long) valor).doubleValue(); //Si lo es, lo convertimos a Double y que se guarde en "valorConvertido"
            else if (valor instanceof Double) //También verificamos si "valor" es una instancia de Double
                valorConvertido = (Double) valor; //Si lo es, los casteamos con Double y que lo guarde en "valorConvertido"
        }
        catch (Exception e) {
            Log.w("ConvertirADouble", e);
        }

        return valorConvertido; //Retornamos el valorConvertido a Double
    }

    //Método que permite convertir una variable de tipo "Timestamp" a un "String" en el que se muestre la fecha y hora
    public static String convertirTimestampAString(Timestamp valor) {
        String fechaHoraString = "";

        try {
            Date fechaHora = valor.toDate(); //Convertimos "valor" a tipo "Date"
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()); //Creamos una instancia de SimpleDateFormat con el formato deseado (dd/MM/yyyy HH:mm:ss)
            fechaHoraString = sdf.format(fechaHora); //Convertimos la fecha y hora a un String estableciendo el formato especificado anteriormente
        }
        catch (Exception e) {
            Log.w("TimestampDog", e);
        }

        return fechaHoraString; //Retornamos la fechaHora convertida a String
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

    //Método que permite redireccionar al Usuario a una pantalla específica dependiendo de su rol. Se usa al iniciar sesión y al comprobar si el usuario tiene una sesión iniciada
    public static void redireccionarUsuario(Context contexto, String correoInicial) { //Recibe un contexto y el correo del usuario
        try {
            //Llamamos al método "obtenerUnRegistro" el cual buscará el user correspondiente en la colección "usuarios" mediante su correo
            oper.obtenerUnRegistro("usuarios", "Correo", correoInicial, new FirestoreCallbacks.FirestoreDocumentCallback() {
                @Override
                public void onCallback(Map<String, Object> documento) {
                    if (documento != null) { //Si el HashMap "documento" no es nulo, quiere decir que si se encontró el registro en la colección, por lo tanto, entrará al if
                        String rol = (String) documento.get("Rol"); //Extraemos el rol del HashMap "documento"

                        //Verificamos el rol, si es "Administrador" que mande al usuario al Activity "AdmPantallas" y cierre el Activity actual
                        if (rol.contentEquals("Administrador")) {
                            Utilidades.iniciarActivity(contexto, AdmPantallas.class, true);
                        } else if (rol.contentEquals("Empleado")) { //Si el rol es "Empleado" que mande al usuario al Activity "EmpMenuPrincipal" y cierre el Activity actual
                            Utilidades.iniciarActivity(contexto, EmpMenuPrincipal.class, true);
                        } else { //Si de casualidad, el rol no es "Administrador" ni "Empleado", que muestre un mensaje de error
                            Toast.makeText(contexto, "ERROR AL OBTENER EL ROL DEL USUARIO", Toast.LENGTH_SHORT).show();
                        }
                    } else { //Si "documento" es nulo, no se encontró el registro en la colección, y entrará en este else
                        Toast.makeText(contexto, "NO SE ENCONTRÓ EL USUARIO", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.w("Activity", "Error al obtener los roles.", e); //Por cualquier error, que muestre la excepción en el Logcat
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerRol", e);
        }
    }

    //Método que nos ayuda a convertir el mes y el año (que primero están en números) en una cadena String (por ejemplo, "Marzo - 2024")
    public static String convertirMonthYearString(int month, int year) {
        return obtenerFormatoMes(month) + " - " + year; //Retornamos la cadena String a mostrar en el lblFechaSeleccionada, para ello primero convertir el número del mes al nombre del mes (valga la redundancia) llamando al método "obtenerFormatoMes" y le enviamos el número del mes "month" como parámetro
    }

    //Método que nos ayuda a obtener el nombre del mes dependiendo su número (1 = Enero, 2 = Febrero, 3 = Marzo...)
    private static String obtenerFormatoMes(int month) {
        switch (month) { //Usamos un switch para trabajar con la variable "month", y en cada case retornará el nombre del mes dependiendo el número
            case 1:
                return "Enero";
            case 2:
                return "Febrero";
            case 3:
                return "Marzo";
            case 4:
                return "Abril";
            case 5:
                return "Mayo";
            case 6:
                return "Junio";
            case 7:
                return "Julio";
            case 8:
                return "Agosto";
            case 9:
                return "Septiembre";
            case 10:
                return "Octubre";
            case 11:
                return "Noviembre";
            case 12:
                return "Diciembre";
            default:
                return "Enero";
        }
    }
}
