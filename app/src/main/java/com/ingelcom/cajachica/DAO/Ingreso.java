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
import com.ingelcom.cajachica.Modelos.IngresosItems;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class Ingreso {

    public Context contexto;
    private FirestoreOperaciones oper = new FirestoreOperaciones();
    private StorageOperaciones stor = new StorageOperaciones();
    private ProgressDialog progressDialog;

    public Ingreso(Context contexto) {
        this.contexto = contexto;
    }

    //Método que nos permitirá obtener todos los ingresos, pero dividiéndolos por la cuadrilla, y por el mes y año sólo si se desea filtrar los mismos
    public void obtenerIngresos(String datoCuadrilla, String mesAnio, boolean datosEmpleado, FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<IngresosItems> callback) { //Recibe la cuadrilla, el mes o año para la obtención de los ingresos, un boolean llamado "datosEmpleado" para saber si es un usuario con rol "empleado" que desea obtener los ingresos (un empleado sólo verá los ingresos del mes actual y el mes anterior, no todos), más el callback de la interfaz "FirestoreAllSpecialDocumentsCallback<IngresosItems>"
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
                        String imagen = (String) documento.get("Imagen");

                        //Comprobamos la cuadrilla a la cual se le desea ver sus ingresos; si la cuadrilla se encuentra en el "ingreso", y si "datosEmpleado" es falso, entrará al if, y por ende, habrán ingresos para visualizar en el ListadoIngresos
                        if (cuadrilla.equalsIgnoreCase(datoCuadrilla) && !datosEmpleado) {
                            if (mesAnio.isEmpty() || mesAnio.equalsIgnoreCase("Seleccionar...")) { //Si el "mesAnio" está vacío o si contiene el texto "Seleccionar...", significa que no se hará ningún filtrado de ingresos por mes o año, y se obtendrán todos los ingresos por cuadrilla
                                IngresosItems ingreso = new IngresosItems(id, usuario, fechaHora, cuadrilla, transferencia, imagen, total); //Creamos un objeto de tipo "IngresosItems" en el cual guardamos los datos extraídos arriba
                                listaIngresos.add(ingreso); //El objeto de tipo "IngresosItems" lo guardamos en la lista "listaIngresos"
                            }
                            else { //Si "mesAnio" no está vacío, ni contiene el texto "Seleccionar...", significa que está recibiendo un mes (por ejemplo, "Julio - 2024") o un año, por lo tanto, se está deseando filtrar los ingresos del RecyclerView por mes o año
                                if (mesAnio.matches("^\\d{4}$")) { //if que verifica si "mesAnio" tiene solamente 4 digitos numéricos en su contenido, esto significa que está recibiendo sólo un año y no un Mes - Año
                                    String year = Utilidades.extraerYearDeFechaHora(fechaHora); //Creamos un String donde se guarda el retorno del método utilitario "extraerYearDeFechaHora" al que le mandamos la variable "fechaHora". Este método retorna un String con el formato deseado del año de la fecha extraída de Firestore

                                    if (mesAnio.equalsIgnoreCase(year)) { //Si el contenido de "mesAnio" es igual al contenido de "year" (ignorando mayúsculas y minúsculas), significa que la selección del año hecha por el usuario se encuentra entre las fechas de los gastos obtenidos, por lo tanto que entre al if y pueda obtener el gasto correspondiente al año seleccionado
                                        IngresosItems ingreso = new IngresosItems(id, usuario, fechaHora, cuadrilla, transferencia, imagen, total); //Creamos un objeto de tipo "IngresosItems" en el cual guardamos los datos extraídos arriba
                                        listaIngresos.add(ingreso); //El objeto de tipo "IngresosItems" lo guardamos en la lista "listaIngresos"lista "listaGastos"
                                    }
                                }
                                else { //Pero si "mesAnio" no tiene 4 digitos numéricos en su contenido, esto significa que está recibiendo un Mes - Año
                                    String fechaFormateada = Utilidades.convertirFechaAFormatoMonthYear(fechaHora); //Creamos un String donde se guarda el retorno del método utilitario "convertirFechaAFormatoMonthYear" al que le mandamos la variable "fechaHora". Este método retorna un String con el formato deseado (Mes - Año) de la fecha extraída de Firestore

                                    if (mesAnio.equalsIgnoreCase(fechaFormateada)) { //Si el contenido de "mesAnio" es igual al contenido de "fechaFormateada" (ignorando mayúsculas y minúsculas), significa que la selección del "Mes - Año" hecha por el usuario en el activity ListadoIngresos se encuentra entre las fechas de los ingresos obtenidos, por lo tanto que entre al if y pueda obtener el ingreso correspondiente al "Mes - Año" seleccionado
                                        IngresosItems ingreso = new IngresosItems(id, usuario, fechaHora, cuadrilla, transferencia, imagen, total); //Creamos un objeto de tipo "IngresosItems" en el cual guardamos los datos extraídos arriba
                                        listaIngresos.add(ingreso); //El objeto de tipo "IngresosItems" lo guardamos en la lista "listaIngresos"
                                    }
                                }
                            }
                        }
                        else if (datoCuadrilla.isEmpty() && !datosEmpleado) { //Si "datoCuadrilla" está vacío y "datosEmpleado" es falso, significa que queremos obtener todos los ingresos sin filtrar
                            if (mesAnio.isEmpty() || mesAnio.equalsIgnoreCase("Seleccionar...")) { //Si el "mesAnio" está vacío o si contiene el texto "Seleccionar...", significa que no se hará ningún filtrado de ingresos por mes o año, y se obtendrán todos los ingresos por cuadrilla
                                IngresosItems ingreso = new IngresosItems(id, usuario, fechaHora, cuadrilla, transferencia, imagen, total); //Creamos un objeto de tipo "IngresosItems" en el cual guardamos los datos extraídos arriba
                                listaIngresos.add(ingreso); //El objeto de tipo "IngresosItems" lo guardamos en la lista "listaIngresos"
                            }
                            else { //Si "mesAnio" no está vacío, ni contiene el texto "Seleccionar...", significa que está recibiendo un mes (por ejemplo, "Julio - 2024") o un año, por lo tanto, se está deseando filtrar los ingresos del RecyclerView por mes o año
                                if (mesAnio.matches("^\\d{4}$")) { //if que verifica si "mesAnio" tiene solamente 4 digitos numéricos en su contenido, esto significa que está recibiendo sólo un año y no un Mes - Año
                                    String year = Utilidades.extraerYearDeFechaHora(fechaHora); //Creamos un String donde se guarda el retorno del método utilitario "extraerYearDeFechaHora" al que le mandamos la variable "fechaHora". Este método retorna un String con el formato deseado del año de la fecha extraída de Firestore

                                    if (mesAnio.equalsIgnoreCase(year)) { //Si el contenido de "mesAnio" es igual al contenido de "year" (ignorando mayúsculas y minúsculas), significa que la selección del año hecha por el usuario se encuentra entre las fechas de los gastos obtenidos, por lo tanto que entre al if y pueda obtener el gasto correspondiente al año seleccionado
                                        IngresosItems ingreso = new IngresosItems(id, usuario, fechaHora, cuadrilla, transferencia, imagen, total); //Creamos un objeto de tipo "IngresosItems" en el cual guardamos los datos extraídos arriba
                                        listaIngresos.add(ingreso); //El objeto de tipo "IngresosItems" lo guardamos en la lista "listaIngresos"lista "listaGastos"
                                    }
                                }
                                else { //Pero si "mesAnio" no tiene 4 digitos numéricos en su contenido, esto significa que está recibiendo un Mes - Año
                                    String fechaFormateada = Utilidades.convertirFechaAFormatoMonthYear(fechaHora); //Creamos un String donde se guarda el retorno del método utilitario "convertirFechaAFormatoMonthYear" al que le mandamos la variable "fechaHora". Este método retorna un String con el formato deseado (Mes - Año) de la fecha extraída de Firestore

                                    if (mesAnio.equalsIgnoreCase(fechaFormateada)) { //Si el contenido de "mes" es igual al contenido de "fechaFormateada" (ignorando mayúsculas y minúsculas), significa que la selección del "Mes - Año" hecha por el usuario en el activity ListadoIngresos se encuentra entre las fechas de los ingresos obtenidos, por lo tanto que entre al if y pueda obtener el ingreso correspondiente al "Mes - Año" seleccionado
                                        IngresosItems ingreso = new IngresosItems(id, usuario, fechaHora, cuadrilla, transferencia, imagen, total); //Creamos un objeto de tipo "IngresosItems" en el cual guardamos los datos extraídos arriba
                                        listaIngresos.add(ingreso); //El objeto de tipo "IngresosItems" lo guardamos en la lista "listaIngresos"
                                    }
                                }
                            }
                        }
                        else if (cuadrilla.equalsIgnoreCase(datoCuadrilla) && datosEmpleado) { //Comprobamos la cuadrilla a la cual se le desea ver sus ingresos; si la cuadrilla se encuentra en el "ingreso", y si "datosEmpleado" es verdadero, entrará al else if. Este es para el "ListadoIngresosEmpleado" en el cual, solamente puede ver los ingresos recibidos en el mes actual y el anterior, nada más
                            if (mesAnio.isEmpty() || mesAnio.equalsIgnoreCase("Seleccionar...")) { //Si el "mesAnio" está vacío o si contiene el texto "Seleccionar...", significa que no se hará ningún filtrado de ingresos por mes o año, y se obtendrán todos los ingresos por cuadrilla
                                Calendar calendar = Calendar.getInstance(); //Instancia de la clase "Calendar"

                                //Configuramos el formato de fecha en español
                                SimpleDateFormat sdf = new SimpleDateFormat("MMMM - yyyy", new Locale("es", "ES"));
                                String mesActual = sdf.format(calendar.getTime());

                                calendar.add(Calendar.MONTH, -1);
                                String mesAnterior = sdf.format(calendar.getTime());

                                mesActual = mesActual.substring(0, 1).toUpperCase() + mesActual.substring(1); //Aquí establecemos en mayúscula la primera letra del mes
                                mesAnterior = mesAnterior.substring(0, 1).toUpperCase() + mesAnterior.substring(1); //Aquí establecemos en mayúscula la primera letra del mes

                                String fechaFormateada = Utilidades.convertirFechaAFormatoMonthYear(fechaHora); //Creamos un String donde se guarda el retorno del método utilitario "convertirFechaAFormatoMonthYear" al que le mandamos la variable "fechaHora". Este método retorna un String con el formato deseado (Mes - Año) de la fecha extraída de Firestore

                                if (mesActual.equalsIgnoreCase(fechaFormateada) || mesAnterior.equalsIgnoreCase(fechaFormateada)) { //Si el contenido de "mesActual" o "mesAnterior" es igual al contenido de "fechaFormateada" (ignorando mayúsculas y minúsculas), significa que el mes actual y el anterior se encuentra entre las fechas de los ingresos obtenidos, por lo tanto que entre al if y pueda obtener el ingreso correspondiente al mes actual o mes anterior
                                    IngresosItems ingreso = new IngresosItems(id, usuario, fechaHora, cuadrilla, transferencia, imagen, total); //Creamos un objeto de tipo "IngresosItems" en el cual guardamos los datos extraídos arriba
                                    listaIngresos.add(ingreso); //El objeto de tipo "IngresosItems" lo guardamos en la lista "listaIngresos"
                                }
                            }
                            else { //Si "mesAnio" no está vacío, ni contiene el texto "Seleccionar Mes", significa que está recibiendo un mes (por ejemplo, "Julio - 2024") o un año, por lo tanto, se está deseando filtrar los ingresos del RecyclerView por mes o año
                                String fechaFormateada = Utilidades.convertirFechaAFormatoMonthYear(fechaHora); //Creamos un String donde se guarda el retorno del método utilitario "convertirFechaAFormatoMonthYear" al que le mandamos la variable "fechaHora". Este método retorna un String con el formato deseado (Mes - Año) de la fecha extraída de Firestore

                                if (mesAnio.equalsIgnoreCase(fechaFormateada)) { //Si el contenido de "mesAnio" es igual al contenido de "fechaFormateada" (ignorando mayúsculas y minúsculas), significa que la selección del "Mes - Año" hecha por el usuario en el activity ListadoIngresos se encuentra entre las fechas de los ingresos obtenidos, por lo tanto que entre al if y pueda obtener el ingreso correspondiente al "Mes - Año" seleccionado
                                    IngresosItems ingreso = new IngresosItems(id, usuario, fechaHora, cuadrilla, transferencia, imagen, total); //Creamos un objeto de tipo "IngresosItems" en el cual guardamos los datos extraídos arriba
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
                    Log.e("ObtenerIngresos", "Error al obtener los ingresos", e);
                    callback.onFailure(e);
                }
            });
        }
        catch (Exception e) {
            Log.e("ObtenerIngresos", "Error al obtener los ingresos", e);
        }
    }

    //Método que nos permite obtener el documento de un ingreso específico mediante su "ID", e invocamos el "FirestoreDocumentCallback" para que al llamar este método, se pueda recibir el "documento" con el contenido del ingreso
    public void obtenerUnIngreso(String id, FirestoreCallbacks.FirestoreDocumentCallback callback) {
        try {
            //Llamamos el método "obtenerUnRegistro" de la clase FirestoreOperaciones y le mamdamos el nombre de la colección, el campo, y el dato a buscar, e invocamos el "FirestoreDocumentCallback"
            oper.obtenerUnRegistro("ingresos", "ID", id, new FirestoreCallbacks.FirestoreDocumentCallback() {
                @Override
                public void onCallback(Map<String, Object> documento) {
                    if (documento != null) //Si "documento" no es nulo, quiere decir que encontró el ingreso
                        callback.onCallback(documento); //Guardamos el "documento" con los datos del ingreso en el "onCallback"
                    else //Si "documento" es nulo, no se encontró el ingreso en la colección, y entrará en este else
                        Log.w("ObtenerIngreso", "Ingreso no encontrado");
                }

                @Override
                public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                    Log.e("ObtenerIngreso", "Error al obtener el ingreso", e);
                    callback.onFailure(e);
                }
            });
        }
        catch (Exception e) {
            Log.e("ObtenerIngreso", "Error al obtener el ingreso", e);
        }
    }

    //Método que nos permite registrar un Ingreso en Firestore
    public void registrarIngreso(String usuario, Timestamp fechaHora, String cuadrilla, String transferencia, String total, Uri uri) {
        if (fechaHora != null && !transferencia.isEmpty() && !total.isEmpty()) { //Verificamos que las dos cajas de texto no estén vacías, y que el Timestamp "fechaHora" no sea nulo para que entre al if (el timestamp sólo será nulo si la pantalla no es "EditarIngreso", y si es "RegistrarIngreso", será nulo cuando el usuario no haya seleccionado una fecha y hora)
            if (uri != null) { //Si el "uri" de la imagen recibida no es nulo, significa que si se ha cargado una imagen, por lo tanto, que entre al if
                try {
                    Cuadrilla cuad = new Cuadrilla(contexto); //Objeto de la clase "Cuadrilla"

                    String idDocumento = UUID.randomUUID().toString(); //Generamos un UUID que es un elemento único y lo guardamos en la variable "idDocumento". Esto nos servirá para que el documento que se cree al insertar los datos, tenga un identificador único
                    double totalIngreso = Double.parseDouble(total); //Convertimos la variable String "total" en double y su contenido lo guardamos en "totalIngreso"

                    String fechaHoraString = Utilidades.convertirTimestampAString(fechaHora, "dd-MM-yyyy - HH:mm"); //Llamamos el método utilitario "convertirTimestampAString" donde mandamos el Timestamp "fechaHora" y el formato para convertirlo a String (dd-MM-yyyy - HH:mm)
                    String rutaImagen = "Imagenes/Ingresos/" + fechaHoraString; //Creamos el nombre de la carpeta y lo concatenamos con el nombre de la imagen (fechaHoraString) a subir en Firebase Storage
                    Map<String, Object> datos = new HashMap<>(); //Creamos un HashMap para guardar los nombres de los campos y los datos a insertar

                    //Establecemos los datos en el HashMap usando ".put", indicando entre comillas el nombre del campo, y después de la coma, el valor a insertar
                    datos.put("ID", idDocumento);
                    datos.put("Usuario", usuario);
                    datos.put("Fecha", fechaHora);
                    datos.put("Cuadrilla", cuadrilla);
                    datos.put("Transferencia", transferencia);
                    datos.put("Total", totalIngreso);
                    datos.put("Imagen", rutaImagen);

                    //Llamamos el método "insertarRegistros" de la clase "FirestoreOperaciones" y le mandamos el nombre de la colección, el HashMap con los datos a insertar. También invocamos los métodos "onSuccess" y "onFailure" de la interfaz FirestoreInsertCallback
                    oper.insertarRegistros("ingresos", datos, new FirestoreCallbacks.FirestoreTextCallback() {
                        @Override
                        public void onSuccess(String texto) {
                            //Creamos un "ProgressDialog" por mientras se están subiendo los datos a Firebase
                            progressDialog = new ProgressDialog(contexto);
                            progressDialog.setTitle("Confirmando Ingreso de Dinero...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            cuad.actualizarDineroCuadrilla(cuadrilla, totalIngreso, "Ingreso"); //Llamamos el método "actualizarDineroCuadrilla" de la clase "Cuadrilla" y le mandamos el nombre de la cuadrilla, el total ingresado y la palabra "Ingreso" para indicar que se hizo un Ingreso y no un Gasto o Deducción

                            //Llamamos el método "subirFoto" de la clase "StorageOperaciones", donde le mandamos el URI de la imagen, el nombre de la carpeta donde se almacenerá la foto concatenado con el nombre de la imagen a subir, y este texto está guardado en la variable "carpetaImagen" (por ejemplo, "Imagenes/Ingresos/04-08-2024 - 12:00"), e invocamos la interfaz "StorageCallback" para hacer la tarea asíncrona
                            stor.subirActualizarImagen(uri, rutaImagen, "Agregar", new StorageCallbacks.StorageCallback() {
                                @Override
                                public void onCallback(String texto) {
                                    if (progressDialog.isShowing()) //Si "progressDialog" se está mostrando, que entre al if
                                        progressDialog.dismiss(); //Eliminamos el "progressDialog" ya cuando el proceso de inserción de la imagen a Storage ha sido exitoso

                                    Utilidades.iniciarActivityConString(contexto, GastoIngresoRegistrado.class, "ActivityGIR", "IngresoRegistrado", true); //Redireccionamos a la clase "GastoIngresoRegistrado" y mandamos el mensaje "IngresoRegistrado" para indicar que fue un Ingreso el que se registró, y mandamos un "true" para indicar que debe finalizar el activity de RegistrarEditarIngresoDeduccion
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    if (progressDialog.isShowing()) //Si "progressDialog" se está mostrando, que entre al if
                                        progressDialog.dismiss(); //Eliminamos el "progressDialog" ya cuando el proceso de inserción de la imagen a Storage haya fallado

                                    Log.w("SubirFotoStorage", e);
                                }
                            });
                        }

                        @Override
                        public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                            if (progressDialog.isShowing()) //Si "progressDialog" se está mostrando, que entre al if
                                progressDialog.dismiss(); //Eliminamos el "progressDialog" ya cuando el proceso de inserción haya fallado

                            Toast.makeText(contexto, "ERROR AL REGISTRAR EL INGRESO", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                catch (Exception e) {
                    Log.e("RegistrarIngreso", "Error al registrar el ingreso", e);
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
    public void editarIngreso(String id, Timestamp fechaHora, String cuadrillaVieja, String cuadrilla, String transferencia, String carpetaImagen, String totalViejo, String totalNuevo, Uri uriVieja, Uri uriNueva) {
        if (fechaHora != null && !transferencia.isEmpty() && !totalNuevo.isEmpty()) { //Verificamos que las dos cajas de texto no estén vacías, y que el Timestamp "fechaHora" no sea nulo para que entre al if (el timestamp sólo será nulo si la pantalla no es "EditarIngreso", y si es "RegistrarIngreso", será nulo cuando el usuario no haya seleccionado una fecha y hora)
            if (uriVieja != null || uriNueva != null) { //Con que uno de los dos URIs que se reciben en los parámetros no sea nulo, que entre al if, en cambio, si ambos son nulos significa que no hay ninguna imagen lista para subir a Firebase Storage, entonces no podrá entrar al if
                try {
                    Cuadrilla cuad = new Cuadrilla(contexto); //Objeto de la clase "Cuadrilla"
                    Map<String, Object> datos = new HashMap<>(); //Creamos un HashMap para guardar los nombres de los campos y los datos

                    //Convertimos las variables String "totalViejo" y "totalNuevo" en double
                    double primerTotal = Double.parseDouble(totalViejo);
                    double segundoTotal = Double.parseDouble(totalNuevo);

                    if (cuadrillaVieja.equalsIgnoreCase(cuadrilla)) { //Si las cuadrillas recibidas son iguales, significa que en la modificación del Ingreso no se está cambiando la cuadrilla, entonces que sólo reste o sume la posible diferencia entre totales
                        double diferenciaTotales = segundoTotal - primerTotal; //Restamos el segundoTotal con el primerTotal y la diferencia la guardamos en "diferenciaTotales"

                        if (diferenciaTotales != 0) //Si "diferenciaTotales" no es 0, significa que si hay una diferencia de dinero entre ambos totales, en ese caso, que proceda a actualizar el dinero de la cuadrilla
                            cuad.actualizarDineroCuadrilla(cuadrilla, diferenciaTotales, "Ingreso");
                    }
                    else { //En cambio, si las cuadrillas recibidas son distintas, significa que en la modificación del Ingreso si se está cambiando la cuadrilla, por lo tanto, el dinero del Ingreso se le debe restar a la cuadrilla vieja, y sumar a la nueva cuadrilla
                        cuad.actualizarDineroCuadrilla(cuadrillaVieja, primerTotal, "Gasto"); //Mandamos la cuadrillaVieja, el primerTotal (mandamos el primerTotal ya que ese es el total original que pertenecía a la cuadrillaVieja) y la palabra "Gasto" al método "actualizarDineroCuadrilla" para que reste el dinero del Ingreso a la cuadrilla vieja porque hubo un error y ese dinero no le pertenece
                        cuad.actualizarDineroCuadrilla(cuadrilla, segundoTotal, "Ingreso"); //Mandamos la cuadrillaNueva, el segundoTotal y la palabra "Ingreso" al método "actualizarDineroCuadrilla" para que sume el nuevo total (segundoTotal) a la cuadrilla seleccionada en el Spinner al momento de editar, cuyo nombre se encuentra en la variable "cuadrilla"
                    }

                    //Establecemos los datos en el HashMap usando ".put", indicando entre comillas el nombre del campo, y después de la coma, el nuevo valor
                    datos.put("Fecha", fechaHora);
                    datos.put("Cuadrilla", cuadrilla);
                    datos.put("Transferencia", transferencia);
                    datos.put("Total", segundoTotal);

                    //Creamos un "ProgressDialog" por mientras se están subiendo los datos a Firebase
                    progressDialog = new ProgressDialog(contexto);
                    progressDialog.setTitle("Confirmando Ingreso de Dinero...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    //Llamamos al método "agregarActualizarRegistrosColeccion" de la clase FirestoreOperaciones. Le mandamos el nombre de la colección, el campo a buscar, el dato a buscar, el HashMap con los nuevos campos y datos (o los campos existentes para actualizar su contenido) e invocamos la interfaz "FirestoreInsertCallback"
                    oper.agregarActualizarRegistrosColeccion("ingresos", "ID", id, datos, new FirestoreCallbacks.FirestoreTextCallback() {
                        @Override
                        public void onSuccess(String texto) {
                            if (uriNueva != null) { //Si la "uriNueva" no es nula, significa que se quiere actualizar una imagen de Firebase Storage con una imagen nueva, por lo tanto, que entre al if y proceda con la actualización de la imagen
                                //Llamamos el método "subirActualizarImagen" de la clase "StorageOperaciones", donde le mandamos el URI de la imagen, el nombre de la carpeta donde se almacenerá la foto concatenado con el nombre de la imagen a subir, y este texto está guardado en la variable "carpetaImagen" (por ejemplo, "Imagenes/Gastos/04-08-2024 - 12:00"), la palabra "Actualizar" para indicar que se actualizará una imagen, e invocamos la interfaz "StorageCallback" para hacer la tarea asíncrona
                                stor.subirActualizarImagen(uriNueva, carpetaImagen, "Actualizar", new StorageCallbacks.StorageCallback() {
                                    @Override
                                    public void onCallback(String texto) {
                                        if (progressDialog.isShowing()) //Si "progressDialog" se está mostrando, que entre al if
                                            progressDialog.dismiss(); //Eliminamos el "progressDialog" ya cuando el proceso de actualización de la imagen a Storage ha sido exitoso

                                        Utilidades.iniciarActivityConString(contexto, GastoIngresoRegistrado.class, "ActivityGIR", "IngresoEditado", true); //Redireccionamos a la clase "GastoIngresoRegistrado" y mandamos el mensaje "IngresoEditado" para indicar que fue un Ingreso el que se modificó, y mandamos un "true" para indicar que debe finalizar el activity de RegistrarEditarIngresoDeduccion
                                    }

                                    @Override
                                    public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                                        if (progressDialog.isShowing()) //Si "progressDialog" se está mostrando, que entre al if
                                            progressDialog.dismiss(); //Eliminamos el "progressDialog" ya cuando el proceso de actualización de la imagen a Storage haya fallado

                                        Log.e("SubirFotoStorage", "Error al actualizar la foto: ", e);
                                    }
                                });
                            }
                            else {
                                if (progressDialog.isShowing()) //Si "progressDialog" se está mostrando, que entre al if
                                    progressDialog.dismiss(); //Eliminamos el "progressDialog" ya cuando el proceso de actualización ha sido exitoso

                                Utilidades.iniciarActivityConString(contexto, GastoIngresoRegistrado.class, "ActivityGIR", "IngresoEditado", true); //Redireccionamos a la clase "GastoIngresoRegistrado" y mandamos el mensaje "IngresoEditado" para indicar que fue un Ingreso el que se modificó, y mandamos un "true" para indicar que debe finalizar el activity de RegistrarEditarIngresoDeduccion
                            }
                        }

                        @Override
                        public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                            if (progressDialog.isShowing()) //Si "progressDialog" se está mostrando, que entre al if
                                progressDialog.dismiss(); //Eliminamos el "progressDialog" ya cuando el proceso de actualización haya fallado

                            Toast.makeText(contexto, "ERROR AL MODIFICAR EL INGRESO", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                catch (Exception e) {
                    Log.e("ActualizarIngreso", "Error al actualizar el ingreso", e);
                }
            }
            else {
                Toast.makeText(contexto, "DEBE SUBIR UNA IMAGEN DEL RECIBO O FACTURA DEL GASTO", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(contexto, "TODOS LOS CAMPOS DEBEN ESTAR LLENOS", Toast.LENGTH_SHORT).show();
        }
    }
}