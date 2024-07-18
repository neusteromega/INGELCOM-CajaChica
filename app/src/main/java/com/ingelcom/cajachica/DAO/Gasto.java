package com.ingelcom.cajachica.DAO;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.ingelcom.cajachica.GastoIngresoRegistrado;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.Modelos.GastosItems;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Gasto {

    public Context contexto;
    private FirestoreOperaciones oper = new FirestoreOperaciones();

    public Gasto(Context contexto) {
        this.contexto = contexto;
    }

    //Método que nos permitirá obtener todos los gastos, pero diviéndolos por los roles de Empleado y Administrador
    public void obtenerGastos(FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<GastosItems> callback) {
        try {
            //Llamamos el método "obtenerRegistros" de "FirestoreOperaciones", le mandamos el nombre de la colección, e invocamos la interfaz "FirestoreAllDocumentsCallback"
            oper.obtenerRegistros("gastos", new FirestoreCallbacks.FirestoreAllDocumentsCallback() {
                @Override
                public void onCallback(List<Map<String, Object>> documentos) { //Al invocar la interfaz, nos devuelve una lista de tipo "Map<String,Object>" llamada "documentos" en la cual se almacenarán todos los campos de todos los documentos de la colección
                    List<GastosItems> listaGastos = new ArrayList<>(); //Creamos una lista de tipo "GastosItems"

                    //Hacemos un for que recorra los documentos de la lista "documentos" y los vaya guardando uno por uno en la variable temporal "documento" de tipo "Map<String,Object>"
                    for (Map<String,Object> documento : documentos) {
                        //Extraemos los campos del HashMap "documento", los campos necesarios en "GastosItems"
                        String id = (String) documento.get("ID");
                        String fechaHora = Utilidades.convertirTimestampAString((Timestamp) documento.get("Fecha")); //En este campo, al ser un Timestamp y no un String, llamamos al método utilitario "convertirTimestampAString" que convierte un objeto Timestamp y retorna un string
                        String cuadrilla = (String) documento.get("Cuadrilla");
                        String lugarCompra = (String) documento.get("Lugar");
                        String tipoCompra = (String) documento.get("TipoCompra");
                        String descripcion = (String) documento.get("Descripcion");
                        String numeroFactura = (String) documento.get("NumeroFactura");
                        String usuario = (String) documento.get("Usuario");
                        String rol = (String) documento.get("RolEmpleado");
                        double total = Utilidades.convertirObjectADouble(documento.get("Total")); //En este campo, al ser un number (o double) y no un String, llamamos al método utilitario "convertirObjectADouble" que convierte un object de Firestore y retorna un double

                        //Creamos un objeto de tipo "GastosItems" en el cual guardamos los datos extraídos arriba
                        GastosItems gasto = new GastosItems(id, fechaHora, cuadrilla, lugarCompra, tipoCompra, descripcion, numeroFactura, usuario, rol, total);
                        listaGastos.add(gasto); //El objeto de tipo "GastosItems" lo guardamos en la lista "listaGastos"

                        Log.d("Firestore", "Datos del documento: " + documento);
                    }
                    //Cuando salga del "for", ya tendremos todos los gastos en la "listaGastos", y esta lista es la que mandamos al método "onCallback" de la interfaz
                    callback.onCallback(listaGastos);
                }

                @Override
                public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                    Log.e("FirestoreError", "Error al obtener los documentos", e);
                    callback.onFailure(e);
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerGastos", e);
        }
    }

    public void obtenerUnGasto() {

    }

    public void registrarGasto(String usuario, String rol, String cuadrilla, String lugar, String tipo, String descripcion, String factura, String total, boolean actualizarDinero) {
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
                datos.put("RolEmpleado", rol);
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
