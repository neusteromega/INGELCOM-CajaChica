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
    private StorageOperaciones stor = new StorageOperaciones();
    private ProgressDialog progressDialog;

    public Gasto(Context contexto) {
        this.contexto = contexto;
    }

    //Método que nos permitirá obtener todos los gastos, pero diviéndolos por los roles de Empleado y Administrador, por la cuadrilla, y por usuario, tipo de compra, el mes y año sólo si se desea filtrar los mismos
    public void obtenerGastos(String datoCuadrilla, String datoRol, String datoUsuario, String datoCompra, String mesAnio, FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<GastosItems> callback) { //Recibe la cuadrilla, el rol, el usuario, el tipo de compra y el mes o año para la obtención de los gastos, más el callback de la interfaz "FirestoreAllSpecialDocumentsCallback<GastosItems>"
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
                        String fechaHora = Utilidades.convertirTimestampAString((Timestamp) documento.get("Fecha"), "dd/MM/yyyy - HH:mm"); //En este campo, al ser un Timestamp y no un String, llamamos al método utilitario "convertirTimestampAString" que convierte un objeto Timestamp y retorna un string. Aquí mandamos el formato "dd/MM/yyyy - HH:mm" para que nos retorne la fecha y hora de esa forma
                        String cuadrilla = (String) documento.get("Cuadrilla");
                        String lugarCompra = (String) documento.get("Lugar");
                        String tipoCompra = (String) documento.get("TipoCompra");
                        String descripcion = (String) documento.get("Descripcion");
                        String numeroFactura = (String) documento.get("NumeroFactura");
                        String usuario = (String) documento.get("Usuario");
                        String rol = (String) documento.get("RolUsuario");
                        String imagen = (String) documento.get("Imagen");
                        double total = Utilidades.convertirObjectADouble(documento.get("Total")); //En este campo, al ser un number (o double) y no un String, llamamos al método utilitario "convertirObjectADouble" que convierte un object de Firestore y retorna un double

                        //Comprobamos la cuadrilla a la cual se le desea ver sus gastos, y el rol que en el listado de "gastosCuadrilla" será "Empleado" y en el listado de "gastosSupervisores" será "Administrador". Si ambos, cuadrilla y rol están en el gasto, entrará al if, y por ende, habrán gastos para visualizar en el ListadoGastos
                        if (cuadrilla.equalsIgnoreCase(datoCuadrilla) && rol.equalsIgnoreCase(datoRol)) {
                            if (datoCompra.isEmpty()) { //Si "datoCompra" está vacío, significa que no queremos filtrar los gastos por tipo de compra
                                GastosItems gasto = filtrarGastos(mesAnio, id, fechaHora, cuadrilla, lugarCompra, tipoCompra, descripcion, numeroFactura, usuario, rol, imagen, total); //Creamos un objeto de tipo "GastosItems" donde guardamos el retorno del método "filtrarGastos" de abajo

                                if (gasto != null)
                                    listaGastos.add(gasto); //El objeto de tipo "GastosItems" lo guardamos en la lista "listaGastos"
                            }
                            else { //Pero si "datoCompra" no está vacío, significa que si queremos filtrar los gastos por tipo de compra, por lo tanto, entrará al else
                                if (datoCompra.equalsIgnoreCase(tipoCompra)) { //Verificamos si el contenido de "datoCompra" es igual al contenido de "tipoCompra" (ignorando mayúsculas y minúsculas)
                                    GastosItems gasto = filtrarGastos(mesAnio, id, fechaHora, cuadrilla, lugarCompra, tipoCompra, descripcion, numeroFactura, usuario, rol, imagen, total); //Creamos un objeto de tipo "GastosItems" donde guardamos el retorno del método "filtrarGastos" de abajo

                                    if (gasto != null)
                                        listaGastos.add(gasto); //El objeto de tipo "GastosItems" lo guardamos en la lista "listaGastos"
                                }
                            }
                        }
                        else if (datoCuadrilla.isEmpty() && rol.equalsIgnoreCase(datoRol)) { //En cambio, si "datoCuadrilla" si está vacío, pero "datoRol" no lo está, significa que queremos obtener todos los gastos sólo filtrados por rol (Esto se usa en ListadoGastosTodos y en Estadísticas)
                            if (datoUsuario.isEmpty()) { //Si "datoUsuario" está vacío, significa que no queremos filtrar los gastos por usuario
                                GastosItems gasto = filtrarGastos(mesAnio, id, fechaHora, cuadrilla, lugarCompra, tipoCompra, descripcion, numeroFactura, usuario, rol, imagen, total); //Creamos un objeto de tipo "GastosItems" donde guardamos el retorno del método "filtrarGastos" de abajo

                                if (gasto != null)
                                    listaGastos.add(gasto); //El objeto de tipo "GastosItems" lo guardamos en la lista "listaGastos"
                            }
                            else { //Pero si "datoUsuario" no está vacío, significa que si queremos filtrar los gastos por usuario, por lo tanto, entrará al else
                                if (datoUsuario.equalsIgnoreCase(usuario)) { //Verificamos si el contenido de "datoUsuario" es igual al contenido de "usuario" (ignorando mayúsculas y minúsculas)
                                    GastosItems gasto = filtrarGastos(mesAnio, id, fechaHora, cuadrilla, lugarCompra, tipoCompra, descripcion, numeroFactura, usuario, rol, imagen, total); //Creamos un objeto de tipo "GastosItems" donde guardamos el retorno del método "filtrarGastos" de abajo

                                    if (gasto != null)
                                        listaGastos.add(gasto); //El objeto de tipo "GastosItems" lo guardamos en la lista "listaGastos"
                                }
                            }
                        }
                        else if (cuadrilla.equalsIgnoreCase(datoCuadrilla) && datoRol.isEmpty()) { //Si "datoRol" está vacío y "datoCuadrilla" no lo está, significa que queremos obtener todos los gastos hechos por los empleados de la cuadrilla y por los supervisores (Esto se usa en ListadoGastos para las Exportaciones)
                            if (datoCompra.isEmpty()) { //Si "datoCompra" está vacío, significa que no queremos filtrar los gastos por tipo de compra
                                GastosItems gasto = filtrarGastos(mesAnio, id, fechaHora, cuadrilla, lugarCompra, tipoCompra, descripcion, numeroFactura, usuario, rol, imagen, total); //Creamos un objeto de tipo "GastosItems" donde guardamos el retorno del método "filtrarGastos" de abajo

                                if (gasto != null)
                                    listaGastos.add(gasto); //El objeto de tipo "GastosItems" lo guardamos en la lista "listaGastos"
                            }
                            else { //Pero si "datoCompra" no está vacío, significa que si queremos filtrar los gastos por tipo de compra, por lo tanto, entrará al else
                                if (datoCompra.equalsIgnoreCase(tipoCompra)) { //Verificamos si el contenido de "datoCompra" es igual al contenido de "tipoCompra" (ignorando mayúsculas y minúsculas)
                                    GastosItems gasto = filtrarGastos(mesAnio, id, fechaHora, cuadrilla, lugarCompra, tipoCompra, descripcion, numeroFactura, usuario, rol, imagen, total); //Creamos un objeto de tipo "GastosItems" donde guardamos el retorno del método "filtrarGastos" de abajo

                                    if (gasto != null)
                                        listaGastos.add(gasto); //El objeto de tipo "GastosItems" lo guardamos en la lista "listaGastos"
                                }
                            }
                        }
                        else if (datoCuadrilla.isEmpty() && datoRol.isEmpty()) { //Si "datoCuadrilla" y "datoRol" están vacíos, significa que queremos obtener todos los gastos sin filtrar (Esto se usa en FragAdmInicio y en ListadoGastos para Exportaciones)
                            if (datoUsuario.isEmpty()) { //Si "datoUsuario" está vacío, significa que no queremos filtrar los gastos por usuario
                                GastosItems gasto = filtrarGastos(mesAnio, id, fechaHora, cuadrilla, lugarCompra, tipoCompra, descripcion, numeroFactura, usuario, rol, imagen, total); //Creamos un objeto de tipo "GastosItems" donde guardamos el retorno del método "filtrarGastos" de abajo

                                if (gasto != null)
                                    listaGastos.add(gasto); //El objeto de tipo "GastosItems" lo guardamos en la lista "listaGastos"
                            }
                            else { //Pero si "datoUsuario" no está vacío, significa que si queremos filtrar los gastos por usuario, por lo tanto, entrará al else
                                if (datoUsuario.equalsIgnoreCase(usuario)) { //Verificamos si el contenido de "datoUsuario" es igual al contenido de "usuario" (ignorando mayúsculas y minúsculas)
                                    GastosItems gasto = filtrarGastos(mesAnio, id, fechaHora, cuadrilla, lugarCompra, tipoCompra, descripcion, numeroFactura, usuario, rol, imagen, total); //Creamos un objeto de tipo "GastosItems" donde guardamos el retorno del método "filtrarGastos" de abajo

                                    if (gasto != null)
                                        listaGastos.add(gasto); //El objeto de tipo "GastosItems" lo guardamos en la lista "listaGastos"
                                }
                            }
                        }
                    }

                    //Cuando salga del "for", ya tendremos todos los gastos en la "listaGastos", y esta lista es la que mandamos al método "onCallback" de la interfaz
                    callback.onCallback(listaGastos);
                }

                @Override
                public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                    Log.e("ObtenerGastos", "Error al obtener los gastos", e);
                    callback.onFailure(e);
                }
            });
        }
        catch (Exception e) {
            Log.e("ObtenerGastos", "Error al obtener los gastos", e);
        }
    }

    //Método que nos ayuda a filtrar los gastos por el contenido de la variable "mesAnio", que pueda tener un Mes - Año o un Año como tal
    private GastosItems filtrarGastos(String mesAnio, String id, String fechaHora, String cuadrilla, String lugarCompra, String tipoCompra, String descripcion, String numeroFactura, String usuario, String rol, String imagen, double total) {
        if (mesAnio.isEmpty() || mesAnio.equalsIgnoreCase("Seleccionar...")) { //Si el "mesAnio" está vacío o si contiene el texto "Seleccionar...", significa que no se hará ningún filtrado de gastos por mes o año, y se obtendrán todos los gastos
            return new GastosItems(id, fechaHora, cuadrilla, lugarCompra, tipoCompra, descripcion, numeroFactura, usuario, rol, imagen, total); //Retornamos un objeto de tipo "GastosItems" en el cual guardamos los datos recibidos como parámetros
        }
        else { //Si "mesAnio" no está vacío, ni contiene el texto "Seleccionar...", significa que está recibiendo un mes (por ejemplo, "Julio - 2024") o un año; por lo tanto, se está deseando filtrar los gastos del RecyclerView por mes o año
            if (mesAnio.matches("^\\d{4}$")) { //if que verifica si "mesAnio" tiene solamente 4 digitos numéricos en su contenido, esto significa que está recibiendo sólo un año y no un Mes - Año
                String year = Utilidades.extraerYearDeFechaHora(fechaHora); //Creamos un String donde se guarda el retorno del método utilitario "extraerYearDeFechaHora" al que le mandamos la variable "fechaHora". Este método retorna un String con el formato deseado del año de la fecha extraída de Firestore

                if (mesAnio.equalsIgnoreCase(year)) { //Si el contenido de "mesAnio" es igual al contenido de "year" (ignorando mayúsculas y minúsculas), significa que la selección del año hecha por el usuario se encuentra entre las fechas de los gastos obtenidos, por lo tanto que entre al if y pueda obtener el gasto correspondiente al año seleccionado
                    return new GastosItems(id, fechaHora, cuadrilla, lugarCompra, tipoCompra, descripcion, numeroFactura, usuario, rol, imagen, total); //Retornamos un objeto de tipo "GastosItems" en el cual guardamos los datos recibidos como parámetros
                }
            }
            else { //Pero si "mesAnio" no tiene 4 digitos numéricos en su contenido, esto significa que está recibiendo un Mes - Año
                String fechaFormateada = Utilidades.convertirFechaAFormatoMonthYear(fechaHora); //Creamos un String donde se guarda el retorno del método utilitario "convertirFechaAFormatoMonthYear" al que le mandamos la variable "fechaHora". Este método retorna un String con el formato deseado (Mes - Año) de la fecha extraída de Firestore

                if (mesAnio.equalsIgnoreCase(fechaFormateada)) { //Si el contenido de "mesAnio" es igual al contenido de "fechaFormateada" (ignorando mayúsculas y minúsculas), significa que la selección del "Mes - Año" hecha por el usuario se encuentra entre las fechas de los gastos obtenidos, por lo tanto que entre al if y pueda obtener el gasto correspondiente al "Mes - Año" seleccionado
                    return new GastosItems(id, fechaHora, cuadrilla, lugarCompra, tipoCompra, descripcion, numeroFactura, usuario, rol, imagen, total); //Retornamos un objeto de tipo "GastosItems" en el cual guardamos los datos recibidos como parámetros
                }
            }

            return null; //Retornamos null por cualquier error
        }
    }

    //Método que nos permite obtener el documento de un gasto específico mediante su "ID", e invocamos el "FirestoreDocumentCallback" para que al llamar este método, se pueda recibir el "documento" con el contenido del gasto
    public void obtenerUnGasto(String id, FirestoreCallbacks.FirestoreDocumentCallback callback) {
        try {
            //Llamamos el método "obtenerUnRegistro" de la clase FirestoreOperaciones y le mamdamos el nombre de la colección, el campo, y el dato a buscar, e invocamos la interfaz "FirestoreDocumentCallback"
            oper.obtenerUnRegistro("gastos", "ID", id, new FirestoreCallbacks.FirestoreDocumentCallback() {
                @Override
                public void onCallback(Map<String, Object> documento) {
                    if (documento != null) //Si "documento" no es nulo, quiere decir que encontró el gasto
                        callback.onCallback(documento); //Guardamos el "documento" con los datos del gasto en el "onCallback"
                    else //Si "documento" es nulo, no se encontró el gasto en la colección, y entrará en este else
                        Log.w("ObtenerGasto", "Gasto no encontrado");
                }

                @Override
                public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                    Log.e("ObtenerGasto", "Error al obtener el gasto", e);
                    callback.onFailure(e);
                }
            });
        }
        catch (Exception e) {
            Log.e("ObtenerGasto", "Error al obtener el gasto", e);
        }
    }

    //Método que nos permite registrar un Gasto en Firestore
    public void registrarGasto(String usuario, Timestamp fechaHora, String rol, String cuadrilla, String lugar, String tipo, String descripcion, String factura, String total, Uri uri, boolean actualizarDinero, boolean fechaSeleccionada) {
        if (!lugar.isEmpty() && !descripcion.isEmpty() && !factura.isEmpty() && !total.isEmpty()) { //Verificamos que las cajas de texto no estén vacías
            if ((fechaSeleccionada && fechaHora != null) || (!fechaSeleccionada && fechaHora == null)) { //Si se cumple una de las condiciones "(fechaSeleccionada && fechaHora != null)" o "(!fechaSeleccionada && fechaHora == null)" entrará al if. La primera condición: "(fechaSeleccionada && fechaHora != null)" indica que hará una modificación de un gasto de administrador ya que "fechaSeleccionada" debe ser true y sólo el admin puede seleccionar una fecha. La segunda condición "(!fechaSeleccionada && fechaHora == null)" indica que se modificará un gasto de empleado ya que "fechaSeleccionada" deberá ser false ya que el empleado no puede seleccionar una fecha al registrar un Gasto
                if (uri != null) { //Si el "uri" de la imagen recibida no es nulo, significa que si se ha cargado una imagen, por lo tanto, que entre al if
                    try {
                        Timestamp timestamp = null; //Creamos un timestamp para la fecha y hora cuando el gasto lo crea un empleado
                        Timestamp fechaHoraFoto; //Creamos un timestamp para la foto a subir
                        Cuadrilla cuad = new Cuadrilla(contexto); //Objeto de la clase "Cuadrilla"

                        String idDocumento = UUID.randomUUID().toString(); //Generamos un UUID que es un elemento único y lo guardamos en la variable "idDocumento". Esto nos servirá para que el documento que se cree al insertar los datos, tenga un identificador único
                        double totalGasto = Double.parseDouble(total); //Convertimos la variable String "total" en double y su contenido lo guardamos en "totalGasto"
                        Map<String, Object> datos = new HashMap<>(); //Creamos un HashMap para guardar los nombres de los campos y los datos a insertar

                        if (!fechaSeleccionada) { //Si "fechaSeleccionada" es false, significa que un empleado quiere registrar un gasto y él no puede seleccionar una fecha y hora, por lo tanto, que entre al if y tome la fecha y hora actual
                            Calendar calendar = Calendar.getInstance(); //Obtenemos una instancia de la clase "Calendar"
                            Date fechaYHora = calendar.getTime(); //"calendar.getTime()" devuelve un objeto Date que representa la fecha y hora actual contenida en el objeto Calendar, esto lo guardamos en "fechaHora"
                            timestamp = new Timestamp(fechaYHora); //Convertimos "fechaHora" en un objeto "Timestamp" para que sea compatible con Firestore
                        }

                        //Insertamos los datos en el HashMap usando ".put", indicando entre comillas el nombre del campo, y después de la coma, el valor a insertar
                        datos.put("ID", idDocumento);
                        datos.put("Usuario", usuario);
                        datos.put("RolUsuario", rol);
                        datos.put("Cuadrilla", cuadrilla);
                        datos.put("Lugar", lugar);
                        datos.put("TipoCompra", tipo);
                        datos.put("Descripcion", descripcion);
                        datos.put("NumeroFactura", factura);
                        datos.put("Total", totalGasto);

                        if (fechaSeleccionada) { //Si "fechaSeleccionada" es true, significa que se está seleccionando una fecha de un DatePicker (un administrador está registrando un gasto), entonces se inserta esa selección que está guardada en la variable "fechaHora" que viene como parámetro
                            datos.put("Fecha", fechaHora);
                            fechaHoraFoto = fechaHora; //Asignamos el timestamp al "fechaHoraFoto"
                        }
                        else { //Si "fechaSeleccionada" es false, quiere decir que un empleado está registrando un gasto, entonces se inserta la fechaHora actual guardada en la variable "timestamp" que se inicializa arriba
                            datos.put("Fecha", timestamp);
                            fechaHoraFoto = timestamp; //Asignamos el timestamp al "fechaHoraFoto"
                        }

                        String fechaHoraString = Utilidades.convertirTimestampAString(fechaHoraFoto, "dd-MM-yyyy - HH:mm"); //Llamamos el método utilitario "convertirTimestampAString" donde mandamos el Timestamp "fechaHora" y el formato para convertirlo a String (dd-MM-yyyy - HH:mm)
                        String carpetaImagen = "Imagenes/Gastos/" + fechaHoraString; //Creamos el nombre de la carpeta y lo concatenamos con el nombre de la imagen (fechaHoraString) a subir en Firebase Storage

                        datos.put("Imagen", carpetaImagen);

                        //Llamamos el método "insertarRegistros" de la clase "FirestoreOperaciones" y le mandamos el nombre de la colección, el HashMap con los datos a insertar. También invocamos los métodos "onSuccess" y "onFailure" de la interfaz FirestoreInsertCallback
                        oper.insertarRegistros("gastos", datos, new FirestoreCallbacks.FirestoreTextCallback() {
                            @Override
                            public void onSuccess(String texto) {
                                //Creamos un "ProgressDialog" por mientras se están subiendo los datos a Firebase
                                progressDialog = new ProgressDialog(contexto);
                                progressDialog.setTitle("Confirmando Gasto de Dinero...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                boolean activityEmpleado; //Variable booleana que nos ayudará a saber si el activity es de Empleado o Administrador

                                if (actualizarDinero) { //Entrará al if si "actualizarDinero" es true, esto significa que el gasto está siendo efectuado por un usuario con rol "Empleado". Si fuera un usuario Administrador, no se actualizará el dinero de la cuadrilla ya que los admins usan un dinero aparte
                                    cuad.actualizarDineroCuadrilla(cuadrilla, totalGasto, "Gasto"); //Llamamos el método "actualizarDineroCuadrilla" de la clase "Cuadrilla" y le mandamos el nombre de la cuadrilla, el total gastado y la palabra "Gasto" para indicar que se hizo un Gasto y no un Ingreso o Deducción
                                    activityEmpleado = true; //Establecemos el "activityEmpleado" en true que indique que es un Empleado quien está registrando el gasto
                                }
                                else {
                                    activityEmpleado = false; //Establecemos el "activityEmpleado" en false que indique que es un Administrador quien está registrando el gasto
                                }

                                //Llamamos el método "subirActualizarImagen" de la clase "StorageOperaciones", donde le mandamos el URI de la imagen, el nombre de la carpeta donde se almacenerá la foto concatenado con el nombre de la imagen a subir, y este texto está guardado en la variable "carpetaImagen" (por ejemplo, "Imagenes/Gastos/04-08-2024 - 12:00"), la palabra "Agregar" para indicar que será una imagen nueva que se subirá, e invocamos la interfaz "StorageCallback" para hacer la tarea asíncrona
                                stor.subirActualizarImagen(uri, carpetaImagen, "Agregar", new StorageCallbacks.StorageCallback() {
                                    @Override
                                    public void onCallback(String texto) {
                                        if (progressDialog.isShowing()) //Si "progressDialog" se está mostrando, que entre al if
                                            progressDialog.dismiss(); //Eliminamos el "progressDialog" ya cuando el proceso de inserción de la imagen a Storage ha sido exitoso

                                        if (activityEmpleado) //Dependiendo del contenido de "activityEmpleado", redireccionamos al empleado al activity "GastoIngresoRegistrado" con el "valor" correspondiente
                                            Utilidades.iniciarActivityConString(contexto, GastoIngresoRegistrado.class, "ActivityGIR", "GastoRegistradoEmpleado", true); //Redireccionamos a la clase "GastoIngresoRegistrado" y mandamos el mensaje "GastoRegistradoEmpleado" para indicar que fue un Gasto hecho por un Empleado y no un Ingreso o Deducción que se registró, y mandamos un "true" para indicar que debe finalizar el activity de RegistrarEditarGasto
                                        else
                                            Utilidades.iniciarActivityConString(contexto, GastoIngresoRegistrado.class, "ActivityGIR", "GastoRegistradoAdmin", true); //Redireccionamos a la clase "GastoIngresoRegistrado" y mandamos el mensaje "GastoRegistradoAdmin" para indicar que fue un Gasto hecho por un Administrador y no un Ingreso o Deducción que se registró, y mandamos un "true" para indicar que debe finalizar el activity de RegistrarEditarGasto
                                    }

                                    @Override
                                    public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                                        if (progressDialog.isShowing()) //Si "progressDialog" se está mostrando, que entre al if
                                            progressDialog.dismiss(); //Eliminamos el "progressDialog" ya cuando el proceso de inserción de la imagen a Storage haya fallado

                                        Log.e("SubirFotoStorage", "Error al subir la foto a Storage", e);
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                                if (progressDialog.isShowing()) //Si "progressDialog" se está mostrando, que entre al if
                                    progressDialog.dismiss(); //Eliminamos el "progressDialog" ya cuando el proceso de inserción haya fallado

                                Toast.makeText(contexto, "ERROR AL REGISTRAR EL GASTO", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    catch (Exception e) {
                        Log.e("RegistrarGasto", "Error al registrar el gasto", e);
                    }
                }
                else {
                    Toast.makeText(contexto, "DEBE SUBIR UNA IMAGEN DEL RECIBO O FACTURA DEL GASTO", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(contexto, "DEBE SELECCIONAR UNA FECHA", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(contexto, "TODOS LOS CAMPOS DEBEN LLENARSE", Toast.LENGTH_SHORT).show();
        }
    }

    //Método que nos permite editar un Gasto existente en Firestore
    public void editarGasto(String id, Timestamp fechaHora, String cuadrilla, String lugar, String tipo, String descripcion, String factura, String carpetaImagen, String totalViejo, String totalNuevo, Uri uriVieja, Uri uriNueva, boolean actualizarDinero, boolean fechaSeleccionada) {
        if (!lugar.isEmpty() && !descripcion.isEmpty() && !factura.isEmpty() && !totalNuevo.isEmpty()) { //Verificamos que las cajas de texto no estén vacías
            if ((fechaSeleccionada && fechaHora != null) || (!fechaSeleccionada && fechaHora == null)) { //Si se cumple una de las condiciones "(fechaSeleccionada && fechaHora != null)" o "(!fechaSeleccionada && fechaHora == null)" entrará al if. La primera condición: "(fechaSeleccionada && fechaHora != null)" indica que hará una modificación de un gasto de administrador ya que "fechaSeleccionada" debe ser true y sólo el admin puede seleccionar una fecha. La segunda condición "(!fechaSeleccionada && fechaHora == null)" indica que se modificará un gasto de empleado ya que "fechaSeleccionada" deberá ser false ya que el empleado no puede seleccionar una fecha al registrar un Gasto
                if (uriVieja != null || uriNueva != null) { //Con que uno de los dos URIs que se reciben en los parámetros no sea nulo, que entre al if, en cambio, si ambos son nulos significa que no hay ninguna imagen lista para subir a Firebase Storage, entonces no podrá entrar al if
                    try {
                        Cuadrilla cuad = new Cuadrilla(contexto); //Objeto de la clase "Cuadrilla"
                        Map<String, Object> datos = new HashMap<>(); //Creamos un HashMap para guardar los nombres de los campos y los datos

                        //Convertimos las variables String "totalViejo" y "totalNuevo" en double
                        double primerTotal = Double.parseDouble(totalViejo);
                        double segundoTotal = Double.parseDouble(totalNuevo);
                        double diferenciaTotales = segundoTotal - primerTotal; //Restamos el segundoTotal con el primerTotal y la diferencia la guardamos en "diferenciaTotales"

                        if (diferenciaTotales != 0 && actualizarDinero) //Si "diferenciaTotales" no es 0, significa que si hay una diferencia de dinero entre ambos totales; y también, si "actualizarDinero" es true (sólo será true cuando se edite un gasto de un empleado), en ese caso, que proceda a actualizar el dinero de la cuadrilla
                            cuad.actualizarDineroCuadrilla(cuadrilla, diferenciaTotales, "Gasto");

                        //Establecemos los datos en el HashMap usando ".put", indicando entre comillas el nombre del campo, y después de la coma, el nuevo valor
                        datos.put("Lugar", lugar);
                        datos.put("TipoCompra", tipo);
                        datos.put("Descripcion", descripcion);
                        datos.put("NumeroFactura", factura);
                        datos.put("Total", segundoTotal);

                        if (fechaSeleccionada) { //Si "fechaSeleccionada" es true, significa que se está seleccionando una fecha de un DatePicker (un administrador está editando un gasto), entonces se inserta esa selección que está guardada en la variable "fechaHora" que viene como parámetro
                            datos.put("Fecha", fechaHora);
                            datos.put("Cuadrilla", cuadrilla);
                        }

                        //Creamos un "ProgressDialog" por mientras se están subiendo los datos a Firebase
                        progressDialog = new ProgressDialog(contexto);
                        progressDialog.setTitle("Confirmando Gasto de Dinero...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        //Llamamos al método "agregarRegistrosColeccion" de la clase FirestoreOperaciones. Le mandamos el nombre de la colección, el campo a buscar, el dato a buscar, el HashMap con los nuevos campos y datos (o los campos existentes para actualizar su contenido) e invocamos la interfaz "FirestoreInsertCallback"
                        oper.agregarActualizarRegistrosColeccion("gastos", "ID", id, datos, new FirestoreCallbacks.FirestoreTextCallback() {
                            @Override
                            public void onSuccess(String texto) {
                                if (uriNueva != null) { //Si la "uriNueva" no es nula, significa que se quiere actualizar una imagen de Firebase Storage con una imagen nueva, por lo tanto, que entre al if y proceda con la actualización de la imagen
                                    //Llamamos el método "subirActualizarImagen" de la clase "StorageOperaciones", donde le mandamos el URI de la imagen, el nombre de la carpeta donde se almacenerá la foto concatenado con el nombre de la imagen a subir, y este texto está guardado en la variable "carpetaImagen" (por ejemplo, "Imagenes/Gastos/04-08-2024 - 12:00"), la palabra "Actualizar" para indicar que se actualizará una imagen, e invocamos la interfaz "StorageCallback" para hacer la tarea asíncrona
                                    stor.subirActualizarImagen(uriNueva, carpetaImagen, "Actualizar", new StorageCallbacks.StorageCallback() {
                                        @Override
                                        public void onCallback(String texto) {
                                            if (progressDialog.isShowing()) //Si "progressDialog" se está mostrando, que entre al if
                                                progressDialog.dismiss(); //Eliminamos el "progressDialog" ya cuando el proceso de actualización de la imagen a Storage ha sido exitoso

                                            Utilidades.iniciarActivityConString(contexto, GastoIngresoRegistrado.class, "ActivityGIR", "GastoEditado", true); //Redireccionamos a la clase "GastoIngresoRegistrado" y mandamos el mensaje "GastoEditado" para indicar que fue un Gasto el que se modificó, y mandamos un "true" para indicar que debe finalizar el activity de RegistrarEditarGasto
                                        }

                                        @Override
                                        public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                                            if (progressDialog.isShowing()) //Si "progressDialog" se está mostrando, que entre al if
                                                progressDialog.dismiss(); //Eliminamos el "progressDialog" ya cuando el proceso de actualización de la imagen a Storage haya fallado

                                            Log.e("SubirFotoStorage", "Error al actualizar la foto", e);
                                        }
                                    });
                                }
                                else { //Pero si la "uriNueva" es nula, significa que no se quiere actualizar una imagen de Firebase Storage, entonces omitimos el proceso de actualización de la imagen y que entre al else
                                    if (progressDialog.isShowing()) //Si "progressDialog" se está mostrando, que entre al if
                                        progressDialog.dismiss(); //Eliminamos el "progressDialog" ya cuando el proceso de actualización ha sido exitoso

                                    Utilidades.iniciarActivityConString(contexto, GastoIngresoRegistrado.class, "ActivityGIR", "GastoEditado", true); //Redireccionamos a la clase "GastoIngresoRegistrado" y mandamos el mensaje "GastoEditado" para indicar que fue un Gasto el que se modificó, y mandamos un "true" para indicar que debe finalizar el activity de RegistrarEditarGasto
                                }
                            }

                            @Override
                            public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                                if (progressDialog.isShowing()) //Si "progressDialog" se está mostrando, que entre al if
                                    progressDialog.dismiss(); //Eliminamos el "progressDialog" ya cuando el proceso de actualización haya fallado

                                Toast.makeText(contexto, "ERROR AL MODIFICAR EL GASTO", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    catch (Exception e) {
                        Log.e("ActualizarGasto", "Error al actualizar el gasto", e);
                    }
                }
                else {
                    Toast.makeText(contexto, "DEBE SUBIR UNA IMAGEN DEL RECIBO O FACTURA DEL GASTO", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else {
            Toast.makeText(contexto, "TODOS LOS CAMPOS DEBEN ESTAR LLENOS", Toast.LENGTH_SHORT).show();
        }
    }
}