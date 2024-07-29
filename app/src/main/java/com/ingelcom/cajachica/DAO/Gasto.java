package com.ingelcom.cajachica.DAO;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.ingelcom.cajachica.GastoIngresoRegistrado;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.Modelos.GastosItems;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class Gasto {

    public Context contexto;
    private FirestoreOperaciones oper = new FirestoreOperaciones();

    public Gasto(Context contexto) {
        this.contexto = contexto;
    }

    //Método que nos permitirá obtener todos los gastos, pero diviéndolos por los roles de Empleado y Administrador, por la cuadrilla, y por el mes y año sólo si se desea filtrar los mismos
    public void obtenerGastos(String datoCuadrilla, String datoRol, String mes, FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<GastosItems> callback) { //Recibe la cuadrilla, el rol y el mes para la obtención de los gastos, más el callback de la interfaz "FirestoreAllSpecialDocumentsCallback<GastosItems>"
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
                        String rol = (String) documento.get("RolUsuario");
                        double total = Utilidades.convertirObjectADouble(documento.get("Total")); //En este campo, al ser un number (o double) y no un String, llamamos al método utilitario "convertirObjectADouble" que convierte un object de Firestore y retorna un double

                        //Comprobamos la cuadrilla a la cual se le desea ver sus gastos, y el rol que en el listado de "gastosCuadrilla" será "Empleado" y en el listado de "gastosSupervisores" será "Administrador". Si ambos, cuadrilla y rol están en el gasto, entrará al if, y por ende, habrán gastos para visualizar en el ListadoGastos
                        if (cuadrilla.equalsIgnoreCase(datoCuadrilla) && rol.equalsIgnoreCase(datoRol)) {
                            if (mes.isEmpty() || mes.equalsIgnoreCase("Seleccionar Mes")) { //Si el "mes" está vacío o si contiene el texto "Seleccionar Mes", significa que no se hará ningún filtrado de gastos por mes, y se obtendrán todos los gastos por cuadrilla y rol
                                GastosItems gasto = new GastosItems(id, fechaHora, cuadrilla, lugarCompra, tipoCompra, descripcion, numeroFactura, usuario, rol, total); //Creamos un objeto de tipo "GastosItems" en el cual guardamos los datos extraídos arriba
                                listaGastos.add(gasto); //El objeto de tipo "GastosItems" lo guardamos en la lista "listaGastos"
                            }
                            else { //Si "mes" no está vacío, ni contiene el texto "Seleccionar Mes", significa que está recibiendo un mes (por ejemplo, "Julio - 2024"), por lo tanto, se está deseando filtrar los gastos del RecyclerView por mes
                                String fechaFormateada = Utilidades.convertirFechaAFormatoMonthYear(fechaHora); //Creamos un String donde se guarda el retorno del método utilitario "convertirFechaAFormatoMonthYear" al que le mandamos la variable "fechaHora". Este método retorna un String con el formato deseado (Mes - Año) de la fecha extraída de Firestore

                                if (mes.equalsIgnoreCase(fechaFormateada)) { //Si el contenido de "mes" es igual al contenido de "fechaFormateada" (ignorando mayúsculas y minúsculas), significa que la selección del "Mes - Año" hecha por el usuario en el activity ListadoGastos se encuentra entre las fechas de los gastos obtenidos, por lo tanto que entre al if y pueda obtener el gasto correspondiente al "Mes - Año" seleccionado
                                    GastosItems gasto = new GastosItems(id, fechaHora, cuadrilla, lugarCompra, tipoCompra, descripcion, numeroFactura, usuario, rol, total); //Creamos un objeto de tipo "GastosItems" en el cual guardamos los datos extraídos arriba
                                    listaGastos.add(gasto); //El objeto de tipo "GastosItems" lo guardamos en la lista "listaGastos"
                                }
                            }
                        }

                        if (datoCuadrilla.isEmpty()) { //Si "datoCuadrilla" está vacío, significa que queremos obtener todos los gastos sin filtrar
                            GastosItems gasto = new GastosItems(id, fechaHora, cuadrilla, lugarCompra, tipoCompra, descripcion, numeroFactura, usuario, rol, total); //Creamos un objeto de tipo "GastosItems" en el cual guardamos los datos extraídos arriba
                            listaGastos.add(gasto); //El objeto de tipo "GastosItems" lo guardamos en la lista "listaGastos"
                        }
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

    public void registrarGasto(String usuario, Timestamp fechaHora, String rol, String cuadrilla, String lugar, String tipo, String descripcion, String factura, String total, boolean actualizarDinero, boolean fechaSeleccionada) {
        if (!lugar.isEmpty() && !descripcion.isEmpty() && !factura.isEmpty() && !total.isEmpty()) { //Verificamos que las cajas de texto no estén vacías
            if ((fechaSeleccionada && fechaHora != null) || (!fechaSeleccionada && fechaHora == null)) { //Si se cumple la primera condición: "(fechaSeleccionada && fechaHora != null)", entrará al if
                try {
                    Cuadrilla cuad = new Cuadrilla(contexto); //Objeto de la clase "Cuadrilla"

                    String idDocumento = UUID.randomUUID().toString(); //Generamos un UUID que es un elemento único y lo guardamos en la variable "idDocumento". Esto nos servirá para que el documento que se cree al insertar los datos, tenga un identificador único
                    double totalGasto = Double.parseDouble(total); //Convertimos la variable String "total" en double y su contenido lo guardamos en "totalGasto"
                    Map<String, Object> datos = new HashMap<>(); //Creamos un HashMap para guardar los nombres de los campos y los datos a insertar

                    if (!fechaSeleccionada) {
                        Calendar calendar = Calendar.getInstance(); //Obtenemos una instancia de la clase "Calendar"
                        Date fechaYHora = calendar.getTime(); //"calendar.getTime()" devuelve un objeto Date que representa la fecha y hora actual contenida en el objeto Calendar, esto lo guardamos en "fechaHora"
                        Timestamp timestamp = new Timestamp(fechaYHora); //Convertimos "fechaHora" en un objeto "Timestamp" para que sea compatible con Firestore
                    }

                    //Insertamos los datos en el HashMap usando ".put", indicando entre comillas el nombre del campo, y después de la coma, el valor a insertar
                    datos.put("ID", idDocumento);
                    datos.put("Usuario", usuario);
                    datos.put("RolUsuario", rol);
                    datos.put("Fecha", fechaHora);
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
                            if (actualizarDinero) {//Entrará al if si "actualizarDinero" es true, esto significa que el gasto está siendo efectuado por un usuario con rol "Empleado". Si fuera un usuario Administrador, no se actualizará el dinero de la cuadrilla ya que los admins usan un dinero aparte
                                cuad.actualizarDineroCuadrilla(cuadrilla, totalGasto, "Gasto"); //Llamamos el método "actualizarDineroCuadrilla" de la clase "Cuadrilla" y le mandamos el nombre de la cuadrilla, el total gastado y la palabra "Gasto" para indicar que se hizo un Gasto y no un Ingreso
                                Utilidades.iniciarActivityConString(contexto, GastoIngresoRegistrado.class, "ActivityGIR", "GastoRegistradoEmpleado", true); //Redireccionamos a la clase "GastoIngresoRegistrado" y mandamos el mensaje "GastoRegistradoEmpleado" para indicar que fue un Gasto hecho por un Empleado y no un Ingreso el que se registró, y mandamos un "true" para indicar que debe finalizar el activity de RegistrarEditarGasto
                            } else {
                                Utilidades.iniciarActivityConString(contexto, GastoIngresoRegistrado.class, "ActivityGIR", "GastoRegistradoAdmin", true); //Redireccionamos a la clase "GastoIngresoRegistrado" y mandamos el mensaje "GastoRegistradoAdmin" para indicar que fue un Gasto hecho por un Administrador y no un Ingreso el que se registró, y mandamos un "true" para indicar que debe finalizar el activity de RegistrarEditarGasto
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(contexto, "ERROR AL REGISTRAR EL GASTO", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    Log.w("RegistrarGasto", e);
                }
            }
            else {
                Toast.makeText(contexto, "TODOS LOS CAMPOS DEBEN LLENARSE", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(contexto, "TODOS LOS CAMPOS DEBEN LLENARSE", Toast.LENGTH_SHORT).show();
        }
    }
}
