package com.ingelcom.cajachica.Herramientas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ingelcom.cajachica.AdmPantallas;
import com.ingelcom.cajachica.DAO.FirestoreOperaciones;
import com.ingelcom.cajachica.EmpMenuPrincipal;
import com.ingelcom.cajachica.R;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    //Método que permite enviar un dato String a un activity e iniciar el mismo
    public static void iniciarActivityConString(Context contexto, Class<?> activityClase, String clave, String valor, boolean finalizarActivity) {
        Intent intent = new Intent(contexto, activityClase); //Creamos el intent y le establecemos el contexto y el nombre del activity
        intent.putExtra(clave, valor); //Usando "putExtra" le establecemos una clave al envío de datos, y le mandamos el texto guardado en "valor"
        contexto.startActivity(intent); //Iniciamos el activity

        //Si el contexto es una instancia de un Activity y "finalizarActivity" es verdadero, cierra la actividad actual
        if (finalizarActivity && contexto instanceof Activity) {
            ((Activity) contexto).finish();
        }
    }

    //Método que permite enviar un HashMap con diferentes datos a un activity e iniciar el mismo
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
    public static String convertirTimestampAString(Timestamp valor, String formato) {
        String fechaHoraString = "";

        try {
            Date fechaHora = valor.toDate(); //Convertimos "valor" a tipo "Date"
            SimpleDateFormat sdf = new SimpleDateFormat(formato, Locale.getDefault()); //Creamos una instancia de SimpleDateFormat con el formato deseado guardado en el parámetro "formato"
            fechaHoraString = sdf.format(fechaHora); //Convertimos la fecha y hora a un String estableciendo el formato especificado anteriormente
        }
        catch (Exception e) {
            Log.w("ConvertirTimestampString", e);
        }

        return fechaHoraString; //Retornamos la fechaHora convertida a String
    }

    //Método que nos permite convertir un String con fecha y hora con el formato "dd/MM/yyyy - HH:mm" a un objeto de tipo "Timestamp"
    public static Timestamp convertirFechaHoraATimestamp(String fechaHoraStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault()); //Con "SimpleDateFormat" creamos el formato que nos ayudará a hacer la conversión

        try {
            Date date = sdf.parse(fechaHoraStr); //Convertimos el String con la fecha y hora a un objeto "Date" usando la variable de tipo "SimpleDateFormat"

            if (date != null) //Si "date" no es nulo (esto quiere decir que la fechaHora recibida como parámetro si tiene el formato "dd/MM/yyyy - HH:mm" y si se realizó bien la conversión), que entre al if
                return new Timestamp(date); //Convertimos el objeto "date" a "Timestamp" y retornamos el mismo
        }
        catch (Exception e) {
            Log.w("ConvertirATimestamp", e);
        }

        return null; //Si no retornó el Timestamp en el if de arriba, quiere decir que hubo un error, entonces que retorne null
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

    //Método que nos ayuda a convertir una FechaHora (00/00/0000 00:00) a un formato de "Mes - Año"
    public static String convertirFechaAFormatoMonthYear(String fechaHora) {
        //Creamos un SimpleDateFormat que nos ayudará a darle formato al contenido de "fechaHora" que se extrae de Firestore. Aquí en "new Locale("es", "ES")" especificamos que queremos un formato de fecha y hora en Español (esto para que los meses sean en español y no en inglés)
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yy - HH:mm", new Locale("es", "ES"));
        String fechaFormateada = ""; //Creamos un String donde se guardará la fecha extraída de Firestore ya con el formato que deseamos (Mes - Año)

        try {
            Date fecha = formato.parse(fechaHora); //Creamos una variable de tipo "Date" donde le asignamos con el formato establecido en "formato" la "fechaHora" extraída de Firestore
            Calendar cal = Calendar.getInstance(); //Creamos una variable de tipo "Calendar"
            cal.setTime(fecha); //A la variable "cal" le asignamos la fecha y hora guardada en "fecha"

            int year = cal.get(Calendar.YEAR); //Del contenido de "cal" extraemos el año y lo guardamos en "year"

            SimpleDateFormat formatoMes = new SimpleDateFormat("MMMM", new Locale("es", "ES")); //Creamos un formato para extraer el mes, y también usamos "Locale("es", "ES")" para especificar que lo queremos en español
            String nombreMes = formatoMes.format(cal.getTime()); //Usando el "formatoMes" y "cal.getTime()", guardamos el nombre del mes en la variable "nombreMes"

            nombreMes = nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1); //Aquí establecemos en mayúscula la primera letra del mes
            fechaFormateada = nombreMes + " - " + year; //Guardamos en "fechaFormateada" el formato del "Mes - Año" que necesitamos para el filtrado
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return fechaFormateada;
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

    //Método genérico que permite ordenar una lista genérica basada en un campo específico de tipo "double" o "Double"
    public static <T> List<T> ordenarListaPorDouble(List<T> items, String nombreCampo, String orden) { //Recibe la lista genérica de tipo "T", el nombreCampo es el campo double por el cual se hará el ordenamiento, y el String "Orden" recibe un "Menor" si el orden es ascendente, y un "Mayor" si el orden es descendente
        //Si la lista es nula, el nombre del campo, y el tipo de orden algunos es nulo o está vacío, se retorna la lista tal como está
        if (items == null || items.isEmpty() || nombreCampo == null || nombreCampo.isEmpty() || orden == null || orden.isEmpty()) {
            return items;
        }

        try {
            //Hacemos una búsqueda en la lista "items" mediante el "nombreCampo", y tras los resultados, obtenemos el elemento encontrado en la posición 0, la primera y única posición porque el nombre del campo guardado en "nombreCampo" no se repite en la lista (por ejemplo, si buscamos el campo "total" en la lista items, ese nombre de campo no se repetirá)
            Field campo = items.get(0).getClass().getDeclaredField(nombreCampo); //Obtenemos el nombre del tipo de dato con "getDeclaredField" y lo guardamos en la variable "campo" de tipo Field
            campo.setAccessible(true); //Lo utilizamos para permitir el acceso a un campo privado o protegido de una clase

            //Verificamos si el tipo del campo es double o Double. Si el campo no es de este tipo, el método no hará nada más
            if (campo.getType() == double.class || campo.getType() == Double.class) {
                //Utilizamos el método "Collections.sort" para ordenar la lista "items"
                Collections.sort(items, new Comparator<T>() { //"Comparator<T>" es una interfaz que se usa para definir la lógica de comparación entre dos objetos del mismo tipo (T)
                    @Override //Implementamos el método "compare" del "Comparator<T>"
                    public int compare(T o1, T o2) { //Este método toma dos objetos "o1" y "o2" de tipo "T" y devuelve un valor entero que indica el orden relativo de los dos objetos. Devuelve un valor negativo si "o1" debe ir antes que "o2". Devuelve 0 si "o1" y "o2" son iguales. Devuelve un valor positivo si "o1" debe ir después que "o2"
                        try {
                            //Obtenemos los valores del campo especificado en "nombreCampo", y estos valores son convertidos a "Double"
                            Double valor1 = (Double) campo.get(o1);
                            Double valor2 = (Double) campo.get(o2);

                            if ("Ascendente".equalsIgnoreCase(orden)) { //Si orden es "Ascendente" (ignorando mayúsculas y minúsculas), que entre al if
                                return valor1.compareTo(valor2); //Comparamos "valor1" con "valor2" de forma ascendente
                            }
                            else if ("Descendente".equalsIgnoreCase(orden)) { //Si orden es "Descendente" (ignorando mayúsculas y minúsculas), que entre al if
                                return valor2.compareTo(valor1); //Comparamos "valor2" con "valor1" de forma descendente
                            }
                            else { //Si orden no es "Menor" ni "Mayor", lanza una excepción
                                throw new IllegalArgumentException("Orden no reconocido: " + orden);
                            }
                        }
                        catch (IllegalAccessException e) {
                            throw new RuntimeException(e); //En caso de error de acceso a campo, lanzamos una excepción de runtime
                        }
                    }
                });
            }
        }
        catch (Exception e) {
            Log.w("OrdenarListaDouble", e);
        }

        return items; //Retornamos la lista "items", que ha sido ordenada si ha ido bien, o la lista original si no se realizó ningún ordenamiento
    }

    //Método genérico que permite ordenar una lista genérica basada en una FechaHora
    public static <T> List<T> ordenarListaPorFechaHora(List<T> items, String nombreCampo, String orden) {
        //Si la lista es nula, el nombre del campo, y el tipo de orden algunos es nulo o está vacío, se retorna la lista tal como está
        if (items == null || items.isEmpty() || nombreCampo == null || nombreCampo.isEmpty() || orden == null || orden.isEmpty()) {
            return items;
        }

        try {
            //Hacemos una búsqueda en la lista "items" mediante el "nombreCampo", y tras los resultados, obtenemos el elemento encontrado en la posición 0, la primera y única posición porque el nombre del campo guardado en "nombreCampo" no se repite en la lista (por ejemplo, si buscamos el campo "fechaHora" en la lista items, ese nombre de campo no se repetirá)
            Field campo = items.get(0).getClass().getDeclaredField(nombreCampo); //Obtenemos el nombre del tipo de dato con "getDeclaredField" y lo guardamos en la variable "campo" de tipo Field
            campo.setAccessible(true); //Lo utilizamos para permitir el acceso a un campo privado o protegido de una clase
            SimpleDateFormat formatoFechaHora = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault()); //Creamos una variable de tipo "SimpleDateFormat" con el formato "dd/MM/yyyy HH:mm"

            //Utilizamos el método "Collections.sort" para ordenar la lista "items"
            Collections.sort(items, new Comparator<T>() { //"Comparator<T>" es una interfaz que se usa para definir la lógica de comparación entre dos objetos del mismo tipo (T)
                @Override //Implementamos el método "compare" del "Comparator<T>"
                public int compare(T o1, T o2) { //Este método toma dos objetos "o1" y "o2" de tipo "T" y devuelve un valor entero que indica el orden relativo de los dos objetos. Devuelve un valor negativo si "o1" debe ir antes que "o2". Devuelve 0 si "o1" y "o2" son iguales. Devuelve un valor positivo si "o1" debe ir después que "o2"
                    try {
                        //Obtenemos los valores del campo especificado en "nombreCampo", y estos valores son convertidos a String
                        String valor1 = (String) campo.get(o1);
                        String valor2 = (String) campo.get(o2);

                        //Convertimos los valores a tipo "Date" usando el "formatoFechaHora"
                        Date fecha1 = formatoFechaHora.parse(valor1);
                        Date fecha2 = formatoFechaHora.parse(valor2);

                        if ("Ascendente".equalsIgnoreCase(orden)) { //Si orden es "Ascendente" (ignorando mayúsculas y minúsculas), que entre al if
                            return fecha1.compareTo(fecha2); //Comparamos "fecha1" con "fecha2" de forma ascendente
                        }
                        else if ("Descendente".equalsIgnoreCase(orden)) { //Si orden es "Descendente" (ignorando mayúsculas y minúsculas), que entre al if
                            return fecha2.compareTo(fecha1); //Comparamos "fecha2" con "fecha1" de forma descendente
                        }
                        else { //Si orden no es "Menor" ni "Mayor", lanza una excepción
                            throw new IllegalArgumentException("Orden no reconocido: " + orden);
                        }
                    }
                    catch (IllegalAccessException | ParseException e) {
                        throw new RuntimeException(e); //En caso de error de acceso a campo, lanzamos una excepción de runtime
                    }
                }
            });

        } catch (Exception e) {
            Log.w("OrdenarListaFechaHora", e);
        }

        return items; //Retornamos la lista "items", que ha sido ordenada si ha ido bien, o la lista original si no se realizó ningún ordenamiento
    }

    //Método genérico que permite ordenar una lista genérica por orden alfabético basada en un String
    public static <T> List<T> ordenarListaPorAlfabetico(List<T> items, String nombreCampo, String orden) {
        //Si la lista es nula, el nombre del campo o el tipo de orden es nulo o está vacío, se retorna la lista tal como está
        if (items == null || items.isEmpty() || nombreCampo == null || nombreCampo.isEmpty() || orden == null || orden.isEmpty()) {
            return items;
        }

        try {
            //Hacemos una búsqueda en la lista "items" mediante el "nombreCampo", y tras los resultados, obtenemos el elemento encontrado en la posición 0, la primera y única posición porque el nombre del campo guardado en "nombreCampo" no se repite en la lista (por ejemplo, si buscamos el campo "fechaHora" en la lista items, ese nombre de campo no se repetirá)
            Field campo = items.get(0).getClass().getDeclaredField(nombreCampo); //Obtenemos el nombre del tipo de dato con "getDeclaredField" y lo guardamos en la variable "campo" de tipo Field
            campo.setAccessible(true); //Lo utilizamos para permitir el acceso a un campo privado o protegido de una clase

            //Utilizamos el método "Collections.sort" para ordenar la lista "items"
            Collections.sort(items, new Comparator<T>() { //"Comparator<T>" es una interfaz que se usa para definir la lógica de comparación entre dos objetos del mismo tipo (T)
                @Override //Implementamos el método "compare" del "Comparator<T>"
                public int compare(T o1, T o2) { //Este método toma dos objetos "o1" y "o2" de tipo "T" y devuelve un valor entero que indica el orden relativo de los dos objetos. Devuelve un valor negativo si "o1" debe ir antes que "o2". Devuelve 0 si "o1" y "o2" son iguales. Devuelve un valor positivo si "o1" debe ir después que "o2"
                    try {
                        //Obtenemos los valores del campo especificado en "nombreCampo", y estos valores son convertidos a String
                        String valor1 = (String) campo.get(o1);
                        String valor2 = (String) campo.get(o2);

                        if ("Ascendente".equalsIgnoreCase(orden)) { //Si orden es "Ascendente" (ignorando mayúsculas y minúsculas), que entre al if
                            return valor1.compareToIgnoreCase(valor2); //Orden ascendente (alfabético)
                        }
                        else if ("Descendente".equalsIgnoreCase(orden)) { //Si orden es "Descendente" (ignorando mayúsculas y minúsculas), que entre al if
                            return valor2.compareToIgnoreCase(valor1); //Orden descendente (alfabético)
                        }
                        else {
                            throw new IllegalArgumentException("Orden no reconocido: " + orden);
                        }
                    }
                    catch (IllegalAccessException e) {
                        throw new RuntimeException(e); //En caso de error de acceso a campo, lanzamos una excepción de runtime
                    }
                }
            });

        } catch (Exception e) {
            Log.w("OrdenarListaAlfabetico", e);
        }

        return items; // Retornamos la lista ordenada o la original si no se realizó ningún ordenamiento
    }

    //Método genérico que recibe una lista y devuelve los últimos elementos de la lista (la cantidad de elementos que devulve están en la variable "cantidadItems")
    public static <T> List<T> obtenerPrimerosItemsLista(List<T> itemsOriginal, int cantidadItems) {
        //Verificamos que "itemsOriginal" no sea nula, que no esté vacía y que tenga menos o la misma cantidad de elementos de "cantidadItems"; si no se cumplen estas condiciones, se retorna la lista sin cambios
        if (itemsOriginal == null || itemsOriginal.isEmpty() || itemsOriginal.size() <= cantidadItems) {
            return itemsOriginal;
        }

        //Retornamos una sublista de "itemsOriginal" que contiene los últimos elementos (la cantidad de elementos específica que retornará está en la variable "cantidadItems")
        return new ArrayList<>(itemsOriginal.subList(0, cantidadItems));
    }

    //Método que utiliza una reflexión para llamar a los métodos getter (por ejemplo, los getter de la clase GastosItems) en los objetos de tipo "T", y retorna un "Object"
    public static Object obtenerCampo(Object obj, String nombreMetodo) { //Recibe un objeto genérico del que se quiere obtener un campo, y el nombre del método getter de la clase modelo
        try {
            //"obj.getClass()" obtiene la clase del objeto "obj"
            Method metodo = obj.getClass().getMethod(nombreMetodo); //"getMethod(nombreMetodo)" busca y obtiene el método público con el nombre "nombreMetodo" en la clase modelo del objeto "obj". Este método debe coincidir exactamente con el nombre proporcionado en "nombreMetodo", incluyendo mayúsculas y minúsculas
            //Retornamos el resultado de la invocación del método (por ejemplo, si el método encontrado fue "public void setId(String id) { this.id = id; }", devolverá el "id" que devuelve este método getter). El resultado es de tipo Object para permitir la máxima flexibilidad en el tipo de retorno
            return metodo.invoke(obj); //Invocamos el método obtenido en el objeto "obj". Si el método getter no requiere argumentos (lo cual es típico), "invoke" se llama con solo el objeto "obj"
        }
        catch (Exception e) {
            Log.w("ObtenerCampo", e);
            return null; //Devuelve null en caso de que ocurra una excepción, indicando que no se pudo obtener el valor del campo
        }
    }

    //Método que obtiene un "SpannableString" con dos colores para establecer el contenido de un TextView con dos colores
    public static SpannableString obtenerStringDosColores(String textoInicial, String textoFinal, int colorInicial, int colorFinal) { //Recibe el texto inicial y el texto final que serán de diferentes colores; también el color inicial (para el texto inicial) y el color final (para el texto final)
        SpannableString spannable = new SpannableString(textoInicial + textoFinal); //Creamos un SpannableString con el texto completo (concatenamos los dos textos). "SpannableString" permite aplicar estilos y formatos a partes específicas del texto, como color, negrita, cursiva, etc.

        //Creamos dos variables de tipo "ForegroundColorSpan" que nos ayuda a aplicar los colores a los textos. A ambos les pasamos los colores guardados en "colorInicial" y "colorFinal"
        ForegroundColorSpan colorInicio = new ForegroundColorSpan(colorInicial);
        ForegroundColorSpan colorFin = new ForegroundColorSpan(colorFinal);

        //Usando la instancia "spannable" de tipo "SpannableString", y usando "setSpan" establecemos los colores específicos que tendrá cada porción del texto
        spannable.setSpan(colorInicio, 0, textoInicial.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); //Indicamos que empiece a aplicar el color desde la posición 0 del String, hasta el "textoInicial.Length()" (El tamaño del textoInicial, o sea, se aplica cuando termina el textoInicial)
        spannable.setSpan(colorFin, textoInicial.length(), spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); //Indicamos que aplique el segundo color desde "textoInicial.Length()" (aquí es donde terminó de aplicar el primer color), hasta "spannable.length()" (el tamaño del "spannable") que sería el final del texto en el SpannableString

        return spannable; //Retornamos el "spannable" ya con los colores establecidos
    }

    //Método auxiliar para crear celdas alineadas a la izquierda y centradas verticalmente. Devuelve una celda de tipo "PdfCell"
    public static PdfPCell crearCelda(String texto, Font font, String alineacion) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));

        if (alineacion.equalsIgnoreCase("Izquierda"))
            cell.setHorizontalAlignment(Element.ALIGN_LEFT); // Alineación horizontal a la izquierda
        else if (alineacion.equalsIgnoreCase("Centro"))
            cell.setHorizontalAlignment(Element.ALIGN_CENTER); // Alineación horizontal al centro

        cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Alineación vertical centrada

        return cell;
    }
}
