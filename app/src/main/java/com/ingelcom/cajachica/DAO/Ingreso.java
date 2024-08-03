package com.ingelcom.cajachica.DAO;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.ingelcom.cajachica.GastoIngresoRegistrado;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.StorageCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.Modelos.GastosItems;
import com.ingelcom.cajachica.Modelos.IngresosItems;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Ingreso {

    public Context contexto;
    private FirestoreOperaciones oper = new FirestoreOperaciones();
    private StorageOperaciones stor = new StorageOperaciones();
    ProgressDialog progressDialog;

    public Ingreso(Context contexto) {
        this.contexto = contexto;
    }

    //Método que nos permitirá obtener todos los ingresos, pero dividiéndolos por la cuadrilla, y por el mes y año sólo si se desea filtrar los mismos
    public void obtenerIngresos(String datoCuadrilla, String mes, FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<IngresosItems> callback) {
        try {
            //Llamamos el método "obtenerRegistros" de "FirestoreOperaciones", le mandamos el nombre de la colección, e invocamos la interfaz "FirestoreAllDocumentsCallback"
            oper.obtenerRegistros("ingresos", new FirestoreCallbacks.FirestoreAllDocumentsCallback() {
                @Override
                public void onCallback(List<Map<String, Object>> documentos) { //Al invocar la interfaz, nos devuelve una lista de tipo "Map<String,Object>" llamada "documentos" en la cual se almacenarán todos los campos de todos los documentos de la colección
                    List<IngresosItems> listaIngresos = new ArrayList<>(); //Creamos una lista de tipo "IngresosItems"

                    //Hacemos un for que recorra los documentos de la lista "documentos" y los vaya guardando uno por uno en la variable temporal "documento" de tipo "Map<String,Object>"
                    for (Map<String,Object> documento : documentos) {
                        //Extraemos los campos del HashMap "documento", los campos necesarios en "IngresosItems"
                        String id = (String) documento.get("ID");
                        String usuario = (String) documento.get("Usuario");
                        String fechaHora = Utilidades.convertirTimestampAString((Timestamp) documento.get("Fecha"), "dd/MM/yyyy - HH:mm"); //En este campo, al ser un Timestamp y no un String, llamamos al método utilitario "convertirTimestampAString" que convierte un objeto Timestamp y retorna un string. Aquí mandamos el formato "dd/MM/yyyy - HH:mm" para que nos retorne la fecha y hora de esa forma
                        String cuadrilla = (String) documento.get("Cuadrilla");
                        String transferencia = (String) documento.get("Transferencia");
                        double total = Utilidades.convertirObjectADouble(documento.get("Total")); //En este campo, al ser un number (o double) y no un String, llamamos al método utilitario "convertirObjectADouble" que convierte un object de Firestore y retorna un double

                        //Comprobamos la cuadrilla a la cual se le desea ver sus ingresos; si la cuadrilla se encuentra en el "ingreso", entrará al if, y por ende, habrán ingresos para visualizar en el ListadoIngresos
                        if (cuadrilla.equalsIgnoreCase(datoCuadrilla)) {
                            if (mes.isEmpty() || mes.equalsIgnoreCase("Seleccionar Mes")) { //Si el "mes" está vacío o si contiene el texto "Seleccionar Mes", significa que no se hará ningún filtrado de ingresos por mes, y se obtendrán todos los ingresos por cuadrilla
                                IngresosItems ingreso = new IngresosItems(id, usuario, fechaHora, cuadrilla, transferencia, total); //Creamos un objeto de tipo "IngresosItems" en el cual guardamos los datos extraídos arriba
                                listaIngresos.add(ingreso); //El objeto de tipo "IngresosItems" lo guardamos en la lista "listaIngresos"
                            }
                            else { //Si "mes" no está vacío, ni contiene el texto "Seleccionar Mes", significa que está recibiendo un mes (por ejemplo, "Julio - 2024"), por lo tanto, se está deseando filtrar los ingresos del RecyclerView por mes
                                String fechaFormateada = Utilidades.convertirFechaAFormatoMonthYear(fechaHora); //Creamos un String donde se guarda el retorno del método utilitario "convertirFechaAFormatoMonthYear" al que le mandamos la variable "fechaHora". Este método retorna un String con el formato deseado (Mes - Año) de la fecha extraída de Firestore

                                if (mes.equalsIgnoreCase(fechaFormateada)) { //Si el contenido de "mes" es igual al contenido de "fechaFormateada" (ignorando mayúsculas y minúsculas), significa que la selección del "Mes - Año" hecha por el usuario en el activity ListadoIngresos se encuentra entre las fechas de los ingresos obtenidos, por lo tanto que entre al if y pueda obtener el ingreso correspondiente al "Mes - Año" seleccionado
                                    IngresosItems ingreso = new IngresosItems(id, usuario, fechaHora, cuadrilla, transferencia, total); //Creamos un objeto de tipo "IngresosItems" en el cual guardamos los datos extraídos arriba
                                    listaIngresos.add(ingreso); //El objeto de tipo "IngresosItems" lo guardamos en la lista "listaIngresos"
                                }
                            }
                        }
                        else if (datoCuadrilla.isEmpty()) { //Si "datoCuadrilla" está vacío, significa que queremos obtener todos los ingresos sin filtrar
                            if (mes.isEmpty() || mes.equalsIgnoreCase("Seleccionar Mes")) { //Si el "mes" está vacío o si contiene el texto "Seleccionar Mes", significa que no se hará ningún filtrado de ingresos por mes, y se obtendrán todos los ingresos por cuadrilla
                                IngresosItems ingreso = new IngresosItems(id, usuario, fechaHora, cuadrilla, transferencia, total); //Creamos un objeto de tipo "IngresosItems" en el cual guardamos los datos extraídos arriba
                                listaIngresos.add(ingreso); //El objeto de tipo "IngresosItems" lo guardamos en la lista "listaIngresos"
                            }
                            else { //Si "mes" no está vacío, ni contiene el texto "Seleccionar Mes", significa que está recibiendo un mes (por ejemplo, "Julio - 2024"), por lo tanto, se está deseando filtrar los ingresos del RecyclerView por mes
                                String fechaFormateada = Utilidades.convertirFechaAFormatoMonthYear(fechaHora); //Creamos un String donde se guarda el retorno del método utilitario "convertirFechaAFormatoMonthYear" al que le mandamos la variable "fechaHora". Este método retorna un String con el formato deseado (Mes - Año) de la fecha extraída de Firestore

                                if (mes.equalsIgnoreCase(fechaFormateada)) { //Si el contenido de "mes" es igual al contenido de "fechaFormateada" (ignorando mayúsculas y minúsculas), significa que la selección del "Mes - Año" hecha por el usuario en el activity ListadoIngresos se encuentra entre las fechas de los ingresos obtenidos, por lo tanto que entre al if y pueda obtener el ingreso correspondiente al "Mes - Año" seleccionado
                                    IngresosItems ingreso = new IngresosItems(id, usuario, fechaHora, cuadrilla, transferencia, total); //Creamos un objeto de tipo "IngresosItems" en el cual guardamos los datos extraídos arriba
                                    listaIngresos.add(ingreso); //El objeto de tipo "IngresosItems" lo guardamos en la lista "listaIngresos"
                                }
                            }
                        }
                    }
                    //Cuando salga del "for", ya tendremos todos los ingresos en la "listaIngresos", y esta lista es la que mandamos al método "onCallback" de la interfaz
                    callback.onCallback(listaIngresos);
                }

                @Override
                public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                    Log.e("FirestoreError", "Error al obtener los documentos", e);
                    callback.onFailure(e);
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerIngresos", e);
        }
    }

    public void obtenerUnIngreso() {

    }

    //Método que nos permite registrar un Ingreso en Firestore
    public void registrarIngreso(String usuario, Timestamp fechaHora, String cuadrilla, String transferencia, String total, Uri uri) {
        if (fechaHora != null && !transferencia.isEmpty() && !total.isEmpty()) { //Verificamos que las dos cajas de texto no estén vacías, y que el Timestamp "fechaHora" no sea nulo para que entre al if (el timestamp sólo será nulo si la pantalla no es "EditarIngreso", y si es "RegistrarIngreso", será nulo cuando el usuario no haya seleccionado una fecha y hora)
            if (uri != null) {
                try {
                    Cuadrilla cuad = new Cuadrilla(contexto); //Objeto de la clase "Cuadrilla"

                    String idDocumento = UUID.randomUUID().toString(); //Generamos un UUID que es un elemento único y lo guardamos en la variable "idDocumento". Esto nos servirá para que el documento que se cree al insertar los datos, tenga un identificador único
                    double totalIngreso = Double.parseDouble(total); //Convertimos la variable String "total" en double y su contenido lo guardamos en "totalIngreso"
                    Map<String, Object> datos = new HashMap<>(); //Creamos un HashMap para guardar los nombres de los campos y los datos a insertar

                    //Establecemos los datos en el HashMap usando ".put", indicando entre comillas el nombre del campo, y después de la coma, el valor a insertar
                    datos.put("ID", idDocumento);
                    datos.put("Usuario", usuario);
                    datos.put("Fecha", fechaHora);
                    datos.put("Cuadrilla", cuadrilla);
                    datos.put("Transferencia", transferencia);
                    datos.put("Total", totalIngreso);

                    //Llamamos el método "insertarRegistros" de la clase "FirestoreOperaciones" y le mandamos el nombre de la colección, el HashMap con los datos a insertar. También invocamos los métodos "onSuccess" y "onFailure" de la interfaz FirestoreInsertCallback
                    oper.insertarRegistros("ingresos", datos, new FirestoreCallbacks.FirestoreTextCallback() {
                        @Override
                        public void onSuccess(String texto) {
                            progressDialog = new ProgressDialog(contexto);
                            progressDialog.setTitle("Confirmando Ingreso de Dinero...");
                            progressDialog.show();

                            cuad.actualizarDineroCuadrilla(cuadrilla, totalIngreso, "Ingreso"); //Llamamos el método "actualizarDineroCuadrilla" de la clase "Cuadrilla" y le mandamos el nombre de la cuadrilla, el total ingresado y la palabra "Ingreso" para indicar que se hizo un Ingreso y no un Gasto
                            stor.subirFoto(uri, "Ingresos/", fechaHora, new StorageCallbacks.StorageCallback() {
                                @Override
                                public void onCallback(String texto) {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();

                                    Utilidades.iniciarActivityConString(contexto, GastoIngresoRegistrado.class, "ActivityGIR", "IngresoRegistrado", true); //Redireccionamos a la clase "GastoIngresoRegistrado" y mandamos el mensaje "IngresoRegistrado" para indicar que fue un Ingreso el que se registró, y mandamos un "true" para indicar que debe finalizar el activity de RegistrarEditarIngresoDeduccion
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();

                                    Log.w("SubirFotoStorage", e);
                                }
                            });
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(contexto, "ERROR AL REGISTRAR EL INGRESO", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    Log.w("RegistrarIngreso", e);
                }
            }
            else {
                Toast.makeText(contexto, "DEBE SUBIR UNA FOTO DE LA TRANSFERENCIA", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(contexto, "TODOS LOS CAMPOS DEBEN LLENARSE", Toast.LENGTH_SHORT).show();
        }
    }

    //Método que nos permite editar un Ingreso existente en Firestore
    public void editarIngreso(String id, Timestamp fechaHora, String cuadrilla, String transferencia, String totalViejo, String totalNuevo) {
        if (fechaHora != null && !transferencia.isEmpty() && !totalNuevo.isEmpty()) { //Verificamos que las dos cajas de texto no estén vacías, y que el Timestamp "fechaHora" no sea nulo para que entre al if (el timestamp sólo será nulo si la pantalla no es "EditarIngreso", y si es "RegistrarIngreso", será nulo cuando el usuario no haya seleccionado una fecha y hora)
            try {
                Cuadrilla cuad = new Cuadrilla(contexto); //Objeto de la clase "Cuadrilla"
                Map<String,Object> datos = new HashMap<>(); //Creamos un HashMap para guardar los nombres de los campos y los datos

                //Convertimos las variables String "totalViejo" y "totalNuevo" en double
                double primerTotal = Double.parseDouble(totalViejo);
                double segundoTotal = Double.parseDouble(totalNuevo);
                double diferenciaTotales = segundoTotal - primerTotal; //Restamos el segundoTotal con el primerTotal y la diferencia la guardamos en "diferenciaTotales"

                if (diferenciaTotales != 0) //Si "diferenciaTotales" no es 0, significa que si hay una diferencia de dinero entre ambos totales, en ese caso, que proceda a actualizar el dinero de la cuadrilla
                    cuad.actualizarDineroCuadrilla(cuadrilla, diferenciaTotales, "Ingreso");

                //Establecemos los datos en el HashMap usando ".put", indicando entre comillas el nombre del campo, y después de la coma, el nuevo valor
                datos.put("Fecha", fechaHora);
                datos.put("Cuadrilla", cuadrilla);
                datos.put("Transferencia", transferencia);
                datos.put("Total", segundoTotal);

                //Llamamos al método "agregarActualizarRegistrosColeccion" de la clase FirestoreOperaciones. Le mandamos el nombre de la colección, el campo a buscar, el dato a buscar, el HashMap con los nuevos campos y datos (o los campos existentes para actualizar su contenido) e invocamos la interfaz "FirestoreInsertCallback"
                oper.agregarActualizarRegistrosColeccion("ingresos", "ID", id, datos, new FirestoreCallbacks.FirestoreTextCallback() {
                    @Override
                    public void onSuccess(String texto) {
                        Utilidades.iniciarActivityConString(contexto, GastoIngresoRegistrado.class, "ActivityGIR", "IngresoEditado", true); //Redireccionamos a la clase "GastoIngresoRegistrado" y mandamos el mensaje "IngresoEditado" para indicar que fue un Ingreso el que se modificó, y mandamos un "true" para indicar que debe finalizar el activity de RegistrarEditarIngresoDeduccion
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(contexto, "ERROR AL MODIFICAR EL INGRESO", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch (Exception e) {
                Log.w("ActualizarIngreso", e);
            }
        }
        else {
            Toast.makeText(contexto, "TODOS LOS CAMPOS DEBEN ESTAR LLENOS", Toast.LENGTH_SHORT).show();
        }
    }
}
