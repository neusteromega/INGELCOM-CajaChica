package com.ingelcom.cajachica.DAO;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.ingelcom.cajachica.GastoIngresoRegistrado;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.Modelos.DeduccionesItems;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Deduccion {

    public Context contexto;
    private FirestoreOperaciones oper = new FirestoreOperaciones();

    public Deduccion(Context contexto) {
        this.contexto = contexto;
    }

    //Método que nos permitirá obtener todos los ingresos, pero dividiéndolos por la cuadrilla
    public void obtenerDeducciones(String datoCuadrilla, FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<DeduccionesItems> callback) {
        try {
            //Llamamos el método "obtenerRegistros" de "FirestoreOperaciones", le mandamos el nombre de la colección, e invocamos la interfaz "FirestoreAllDocumentsCallback"
            oper.obtenerRegistros("deducciones", new FirestoreCallbacks.FirestoreAllDocumentsCallback() {
                @Override
                public void onCallback(List<Map<String, Object>> documentos) { //Al invocar la interfaz, nos devuelve una lista de tipo "Map<String,Object>" llamada "documentos" en la cual se almacenarán todos los campos de todos los documentos de la colección
                    List<DeduccionesItems> listaDeducciones = new ArrayList<>(); //Creamos una lista de tipo "DeduccionesItems"

                    //Hacemos un for que recorra los documentos de la lista "documentos" y los vaya guardando uno por uno en la variable temporal "documento" de tipo "Map<String,Object>"
                    for (Map<String,Object> documento : documentos) {
                        //Extraemos los campos del HashMap "documento", los campos necesarios en "DeduccionesItems"
                        String id = (String) documento.get("ID");
                        String usuario = (String) documento.get("Usuario");
                        String fechaHora = Utilidades.convertirTimestampAString((Timestamp) documento.get("Fecha"), "dd/MM/yyyy - HH:mm"); //En este campo, al ser un Timestamp y no un String, llamamos al método utilitario "convertirTimestampAString" que convierte un objeto Timestamp y retorna un string. Aquí mandamos el formato "dd/MM/yyyy - HH:mm" para que nos retorne la fecha y hora de esa forma
                        String cuadrilla = (String) documento.get("Cuadrilla");
                        double total = Utilidades.convertirObjectADouble(documento.get("Total")); //En este campo, al ser un number (o double) y no un String, llamamos al método utilitario "convertirObjectADouble" que convierte un object de Firestore y retorna un double

                        //Comprobamos la cuadrilla a la cual se le desea ver sus deducciones; si la cuadrilla se encuentra en la "deduccion", entrará al if, y por ende, habrán deducciones para visualizar en el ListadoDeducciones
                        if (cuadrilla.contentEquals(datoCuadrilla)) {
                            DeduccionesItems deduccion = new DeduccionesItems(id, usuario, fechaHora, cuadrilla, total); //Creamos un objeto de tipo "DeduccionesItems" en el cual guardamos los datos extraídos arriba
                            listaDeducciones.add(deduccion); //El objeto de tipo "DeduccionesItems" lo guardamos en la lista "listaDeducciones"
                        }
                    }
                    //Cuando salga del "for", ya tendremos todas las deducciones en la "listaDeducciones", y esta lista es la que mandamos al método "onCallback" de la interfaz
                    callback.onCallback(listaDeducciones);
                }

                @Override
                public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                    Log.e("FirestoreError", "Error al obtener los documentos", e);
                    callback.onFailure(e);
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerDeducciones", e);
        }
    }

    //Método que nos permite registrar una Deducción en Firestore
    public void registrarDeduccion(String usuario, Timestamp fechaHora, String cuadrilla, String total) {
        if (fechaHora != null && !total.isEmpty()) { //Verificamos que la caja de texto de "total" no esté vacía, y que el timestamp "fechaHora" no sea nulo para que entre al if
            try {
                Cuadrilla cuad = new Cuadrilla(contexto); //Objeto de la clase "Cuadrilla"

                String idDocumento = UUID.randomUUID().toString(); //Generamos un UUID que es un elemento único y lo guardamos en la variable "idDocumento". Esto nos servirá para que el documento que se cree al insertar los datos, tenga un identificador único
                double totalIngreso = Double.parseDouble(total); //Convertimos la variable String "total" en double y su contenido lo guardamos en "totalIngreso"
                Map<String,Object> datos = new HashMap<>(); //Creamos un HashMap para guardar los nombres de los campos y los datos a insertar

                //Insertamos los datos en el HashMap usando ".put", indicando entre comillas el nombre del campo, y después de la coma, el valor a insertar
                datos.put("ID", idDocumento);
                datos.put("Usuario", usuario);
                datos.put("Fecha", fechaHora);
                datos.put("Cuadrilla", cuadrilla);
                datos.put("Total", totalIngreso);

                //Llamamos el método "insertarRegistros" de la clase "FirestoreOperaciones" y le mandamos el nombre de la colección, el HashMap con los datos a insertar. También invocamos los métodos "onSuccess" y "onFailure" de la interfaz FirestoreInsertCallback
                oper.insertarRegistros("deducciones", datos, new FirestoreCallbacks.FirestoreTextCallback() {
                    @Override
                    public void onSuccess(String texto) {
                        cuad.actualizarDineroCuadrilla(cuadrilla, totalIngreso, "Deduccion"); //Llamamos el método "actualizarDineroCuadrilla" de la clase "Cuadrilla" y le mandamos el nombre de la cuadrilla, el total deducido y la palabra "Deduccion" para indicar que se hizo una Deducción por planilla
                        Utilidades.iniciarActivityConString(contexto, GastoIngresoRegistrado.class, "ActivityGIR", "DeduccionRegistrada", true); //Redireccionamos a la clase "GastoIngresoRegistrado" y mandamos el mensaje "DeduccionRegistrada" para indicar que fue una Deducción la que se registró, y mandamos un "true" para indicar que debe finalizar el activity de RegistrarEditarIngresoDeduccion
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(contexto, "ERROR AL REGISTRAR LA DEDUCCIÓN", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch (Exception e) {
                Log.w("RegistrarDeduccion", e);
            }
        }
        else {
            Toast.makeText(contexto, "TODOS LOS CAMPOS DEBEN LLENARSE", Toast.LENGTH_SHORT).show();
        }
    }

    //Método que nos permite editar una deducción por planilla existente en Firestore
    public void editarDeduccion(String id, Timestamp fechaHora, String cuadrilla, String totalViejo, String totalNuevo) {
        if (fechaHora != null && !totalNuevo.isEmpty()) { //Verificamos que la caja de texto de "totalNuevo" no esté vacía, y que el Timestamp "fechaHora" no sea nulo para que entre al if (el timestamp sólo será nulo si la pantalla no es "EditarDeduccion", y si es "RegistrarDeduccion", será nulo cuando el usuario no haya seleccionado una fecha y hora)
            try {
                Cuadrilla cuad = new Cuadrilla(contexto); //Objeto de la clase "Cuadrilla"
                Map<String,Object> datos = new HashMap<>(); //Creamos un HashMap para guardar los nombres de los campos y los datos

                //Convertimos las variables String "totalViejo" y "totalNuevo" en double
                double primerTotal = Double.parseDouble(totalViejo);
                double segundoTotal = Double.parseDouble(totalNuevo);
                double diferenciaTotales = segundoTotal - primerTotal; //Restamos el segundoTotal con el primerTotal y la diferencia la guardamos en "diferenciaTotales"

                if (diferenciaTotales != 0) //Si "diferenciaTotales" no es 0, significa que si hay una diferencia de dinero entre ambos totales, en ese caso, que proceda a actualizar el dinero de la cuadrilla
                    cuad.actualizarDineroCuadrilla(cuadrilla, diferenciaTotales, "Deduccion");

                //Establecemos los datos en el HashMap usando ".put", indicando entre comillas el nombre del campo, y después de la coma, el nuevo valor
                datos.put("Fecha", fechaHora);
                datos.put("Cuadrilla", cuadrilla);
                datos.put("Total", segundoTotal);

                //Llamamos al método "agregarActualizarRegistrosColeccion" de la clase FirestoreOperaciones. Le mandamos el nombre de la colección, el campo a buscar, el dato a buscar, el HashMap con los nuevos campos y datos (o los campos existentes para actualizar su contenido) e invocamos la interfaz "FirestoreInsertCallback"
                oper.agregarActualizarRegistrosColeccion("deducciones", "ID", id, datos, new FirestoreCallbacks.FirestoreTextCallback() {
                    @Override
                    public void onSuccess(String texto) {
                        Utilidades.iniciarActivityConString(contexto, GastoIngresoRegistrado.class, "ActivityGIR", "DeduccionEditada", true); //Redireccionamos a la clase "GastoIngresoRegistrado" y mandamos el mensaje "DeduccionEditada" para indicar que fue una Deducción el que se modificó, y mandamos un "true" para indicar que debe finalizar el activity de RegistrarEditarIngresoDeduccion
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(contexto, "ERROR AL MODIFICAR LA DEDUCCIÓN", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch (Exception e) {
                Log.w("ActualizarDeduccion", e);
            }
        }
        else {
            Toast.makeText(contexto, "TODOS LOS CAMPOS DEBEN ESTAR LLENOS", Toast.LENGTH_SHORT).show();
        }
    }
}
