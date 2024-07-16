package com.ingelcom.cajachica.DAO;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.ingelcom.cajachica.GastoIngresoRegistrado;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.RegistrarEditarIngreso;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Ingreso {

    public Context contexto;
    private FirestoreOperaciones oper = new FirestoreOperaciones();

    public Ingreso(Context contexto) {
        this.contexto = contexto;
    }

    public void registrarIngreso(String cuadrilla, String transferencia, String total) {
        if (!transferencia.isEmpty() && !total.isEmpty()) { //Verificamos que las dos cajas de texto no estén vacías para que entre al if
            try {
                Cuadrilla cuad = new Cuadrilla(contexto); //Objeto de la clase "Cuadrilla"

                String idDocumento = UUID.randomUUID().toString(); //Generamos un UUID que es un elemento único y lo guardamos en la variable "idDocumento". Esto nos servirá para que el documento que se cree al insertar los datos, tenga un identificador único
                double totalIngreso = Double.parseDouble(total); //Convertimos la variable String "total" en double y su contenido lo guardamos en "totalIngreso"
                Map<String,Object> datos = new HashMap<>(); //Creamos un HashMap para guardar los nombres de los campos y los datos a insertar

                Calendar calendar = Calendar.getInstance(); //Obtenemos una instancia de la clase "Calendar"
                //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()); //Creamos un formato para la fecha y hora. "Locale.getDefault()" especifica que el formato debe seguir las convenciones predeterminadas del sistema
                //fechaHoraActual = sdf.format(calendar.getTime()); //Obtenemos la fecha y hora actual con "calendar.getTime()" y lo convertimos al formato guardado en "sdf" y esto lo asignamos al String "fechaHoraActual"
                Date fechaHora = calendar.getTime(); //"calendar.getTime()" devuelve un objeto Date que representa la fecha y hora actual contenida en el objeto Calendar, esto lo guardamos en "fechaHora"
                Timestamp timestamp = new Timestamp(fechaHora); //Convertimos "fechaHora" en un objeto "Timestamp" para que sea compatible con Firestore

                //Insertamos los datos en el HashMap usando ".put", indicando entre comillas el nombre del campo, y después de la coma, el valor a insertar
                datos.put("ID", idDocumento);
                datos.put("Fecha", timestamp);
                datos.put("Cuadrilla", cuadrilla);
                datos.put("Transferencia", transferencia);
                datos.put("Total", totalIngreso);

                //Llamamos el método "insertarRegistros" de la clase "FirestoreOperaciones" y le mandamos el nombre de la colección, el HashMap con los datos a insertar. También invocamos los métodos "onSuccess" y "onFailure" de la interfaz FirestoreInsertCallback
                oper.insertarRegistros("ingresos", datos, new FirestoreCallbacks.FirestoreInsertCallback() {
                    @Override
                    public void onSuccess(String texto) {
                        //Toast.makeText(RegistrarEditarIngreso.this, "INGRESO REGISTRADO EXITOSAMENTE", Toast.LENGTH_SHORT).show();
                        cuad.actualizarDineroCuadrilla(cuadrilla, totalIngreso, "Ingreso"); //Llamamos el método "actualizarDineroCuadrilla" de la clase "Cuadrilla" y le mandamos el nombre de la cuadrilla, el total ingresado y la palabra "Ingreso" para indicar que se hizo un Ingreso y no un Gasto
                        Utilidades.iniciarActivityConString(contexto, GastoIngresoRegistrado.class, "ActivityGIR", "IngresoRegistrado", true); //Redireccionamos a la clase "GastoIngresoRegistrado" y mandamos el mensaje "IngresoRegistrado" para indicar que fue un Ingreso y no un Gasto el que se registró, y mandamos un "true" para indicar que debe finalizar el activity de RegistrarEditarIngreso
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(contexto, "ERROR AL REGISTRAR EL INGRESO", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch (Exception e) {
                Log.w("RegistrarIngreso", e);
            }
        }
        else {
            Toast.makeText(contexto, "TODOS LOS CAMPOS DEBEN LLENARSE", Toast.LENGTH_SHORT).show();
        }
    }
}
