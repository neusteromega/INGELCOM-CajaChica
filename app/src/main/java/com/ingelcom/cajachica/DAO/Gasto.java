package com.ingelcom.cajachica.DAO;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.ingelcom.cajachica.GastoIngresoRegistrado;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Gasto {

    public Context contexto;
    private FirestoreOperaciones oper = new FirestoreOperaciones();

    public Gasto(Context contexto) {
        this.contexto = contexto;
    }

    public void obtenerGastos() {

    }

    public void obtenerUnGasto() {

    }

    public void registrarGasto(String usuario, String cuadrilla, String lugar, String tipo, String descripcion, String factura, String total, boolean actualizarDinero) {
        if (!lugar.isEmpty() && !descripcion.isEmpty() && !factura.isEmpty() && !total.isEmpty()) { //Verificamos que las dos cajas de texto no estén vacías para que entre al if
            try {
                Cuadrilla cuad = new Cuadrilla(contexto); //Objeto de la clase "Cuadrilla"

                String idDocumento = UUID.randomUUID().toString(); //Generamos un UUID que es un elemento único y lo guardamos en la variable "idDocumento". Esto nos servirá para que el documento que se cree al insertar los datos, tenga un identificador único
                double totalGasto = Double.parseDouble(total); //Convertimos la variable String "total" en double y su contenido lo guardamos en "totalGasto"
                Map<String,Object> datos = new HashMap<>(); //Creamos un HashMap para guardar los nombres de los campos y los datos a insertar

                Calendar calendar = Calendar.getInstance(); //Obtenemos una instancia de la clase "Calendar"
                Date fechaHora = calendar.getTime(); //"calendar.getTime()" devuelve un objeto Date que representa la fecha y hora actual contenida en el objeto Calendar, esto lo guardamos en "fechaHora"
                Timestamp timestamp = new Timestamp(fechaHora); //Convertimos "fechaHora" en un objeto "Timestamp" para que sea compatible con Firestore

                //Insertamos los datos en el HashMap usando ".put", indicando entre comillas el nombre del campo, y después de la coma, el valor a insertar
                datos.put("ID", idDocumento);
                datos.put("Usuario", usuario);
                datos.put("Fecha", timestamp);
                datos.put("Cuadrilla", cuadrilla);
                datos.put("Lugar", lugar);
                datos.put("TipoCompra", tipo);
                datos.put("Descripcion", descripcion);
                datos.put("NumeroFactura", factura);
                datos.put("Total", totalGasto);

                //Llamamos el método "insertarRegistros" de la clase "FirestoreOperaciones" y le mandamos el nombre de la colección, el HashMap con los datos a insertar. También invocamos los métodos "onSuccess" y "onFailure" de la interfaz FirestoreInsertCallback
                oper.insertarRegistros("gastos", datos, new FirestoreCallbacks.FirestoreTextCallback() {
                    @Override
                    public void onSuccess(String texto) {
                        if (actualizarDinero) //Entrará al if si "actualizarDinero" es true, esto significa que el gasto está siendo efectuado por un usuario con rol "Empleado". Si fuera un usuario Administrador, no se actualizará el dinero de la cuadrilla ya que los admins usan un dinero aparte
                            cuad.actualizarDineroCuadrilla(cuadrilla, totalGasto, "Gasto"); //Llamamos el método "actualizarDineroCuadrilla" de la clase "Cuadrilla" y le mandamos el nombre de la cuadrilla, el total gastado y la palabra "Gasto" para indicar que se hizo un Gasto y no un Ingreso

                        Utilidades.iniciarActivityConString(contexto, GastoIngresoRegistrado.class, "ActivityGIR", "GastoRegistrado", true); //Redireccionamos a la clase "GastoIngresoRegistrado" y mandamos el mensaje "GastoRegistrado" para indicar que fue un Gasto y no un Ingreso el que se registró, y mandamos un "true" para indicar que debe finalizar el activity de RegistrarEditarGasto
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(contexto, "ERROR AL REGISTRAR EL GASTO", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch (Exception e) {
                Log.w("RegistrarGasto", e);
            }
        }
        else {
            Toast.makeText(contexto, "TODOS LOS CAMPOS DEBEN LLENARSE", Toast.LENGTH_SHORT).show();
        }
    }
}
