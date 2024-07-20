package com.ingelcom.cajachica.DAO;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.Modelos.CuadrillasItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cuadrilla {

    public Context contexto;
    private FirestoreOperaciones oper = new FirestoreOperaciones();

    public Cuadrilla(Context contexto) {
        this.contexto = contexto;
    }

    //Método que nos permitirá obtener todas las cuadrillas en Firestore
    public void obtenerCuadrillas(FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<CuadrillasItems> callback) { //Llamamos la interfaz "FirestoreAllSpecialDocumentsCallback" y le indicamos que debe ser de tipo "CuadrillasItems"
        try {
            //Llamamos el método "obtenerRegistros" de "FirestoreOperaciones", le mandamos el nombre de la colección, e invocamos la interfaz "FirestoreAllDocumentsCallback"
            oper.obtenerRegistros("cuadrillas", new FirestoreCallbacks.FirestoreAllDocumentsCallback() {
                @Override
                public void onCallback(List<Map<String, Object>> documentos) { //Al invocar la interfaz, nos devuelve una lista de tipo "Map<String,Object>" llamada "documentos" en la cual se almacenarán todos los campos de todos los documentos de la colección
                    List<CuadrillasItems> listaCuadrillas = new ArrayList<>(); //Creamos una lista de tipo "CuadrillasItems"

                    //Hacemos un for que recorra los documentos de la lista "documentos" y los vaya guardando uno por uno en la variable temporal "documento" de tipo "Map<String,Object>"
                    for (Map<String,Object> documento : documentos) {
                        //Extraemos los campos del HashMap "documento", los campos necesarios en "CuadrillasItems"
                        String nombre = (String) documento.get("Nombre");
                        double dinero = Utilidades.convertirObjectADouble(documento.get("Dinero")); //En este campo, al ser un number (o double) y no un String, llamamos al método utilitario "convertirObjectADouble" que convierte un object de Firestore y retorna un double

                        CuadrillasItems cuadrilla = new CuadrillasItems(nombre, dinero); //Creamos un objeto de tipo "CuadrillasItems" en el cual guardamos los datos extraídos arriba
                        listaCuadrillas.add(cuadrilla); //El objeto de tipo "CuadrillasItems" lo guardamos en la lista "listaCuadrillas"
                    }
                    //Cuando salga del "for", ya tendremos todos los gastos en la "listaCuadrillas", y esta lista es la que mandamos al método "onCallback" de la interfaz
                    callback.onCallback(listaCuadrillas);
                }

                @Override
                public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                    Log.e("FirestoreError", "Error al obtener los documentos", e);
                    callback.onFailure(e);
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerCuadrillas", e);
        }
    }

    //Método que nos permite obtener el documento de una cuadrilla específica mediante su "Nombre", e invocamos el "FirestoreDocumentCallback" para que al llamar este método, se pueda recibir el "documento" con el contenido de la cuadrilla
    public void obtenerUnaCuadrilla(String nombre, FirestoreCallbacks.FirestoreDocumentCallback callback) {
        try {
            //Llamamos el método "obtenerUnRegistro" de la clase FirestoreOperaciones y le mamdamos el nombre de la colección, el campo, y el dato a buscar, e invocamos el "FirestoreDocumentCallback"
            oper.obtenerUnRegistro("cuadrillas", "Nombre", nombre, new FirestoreCallbacks.FirestoreDocumentCallback() {
                @Override
                public void onCallback(Map<String, Object> documento) {
                    if (documento != null) //Si "documento" no es nulo, quiere decir que encontró la cuadrilla
                        callback.onCallback(documento); //Guardamos el "documento" con los datos de la cuadrilla en el "onCallback"
                    else //Si "documento" es nulo, no se encontró la cuadrilla en la colección, y entrará en este else
                        Log.w("ObtenerCuadrilla", "Cuadrilla no encontrada");
                }

                @Override
                public void onFailure(Exception e) {
                    callback.onFailure(e);
                    Log.w("BuscarCuadrilla", "Error al obtener la Cuadrilla", e);
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerCuadrilla", e);
        }
    }

    //Método que nos ayudará a actualizar el dinero de una cuadrilla, dependiendo su recibe un ingreso o si se realiza un gasto
    public void actualizarDineroCuadrilla(String cuadrilla, double total, String operacion) {
        try {
            //Llamamos el método "obtenerUnRegistro" de la clase "FirestoreOperaciones", este nos ayudará a buscar la cuadrilla usando su nombre
            oper.obtenerUnRegistro("cuadrillas", "Nombre", cuadrilla, new FirestoreCallbacks.FirestoreDocumentCallback() {
                @Override
                public void onCallback(Map<String, Object> documento) {
                    if (documento != null) { //Si "documento" no es nulo, quiere decir que encontró la cuadrilla mediante su nombre
                        //Obtenemos el valor guardado en el campo "Dinero" de la colección "cuadrillas" y lo guardamos en la variable Object llamada "valor"
                        Object valor = documento.get("Dinero"); //Lo obtenemos como Object ya que Firestore puede almacenar números en varios formatos (por ejemplo, Long y Double) y esto puede causar problemas con el casting del contenido del campo
                        double dinero = Utilidades.convertirObjectADouble(valor); //Llamamos el método utilitario "convertirObjectADouble" y le mandamos el objeto "valor", y nos retorna este objeto ya convertido a double y lo guardamos en "dinero"

                        //Condición que determina qué operación se hará, si es un "Ingreso" se hará una suma, si es un "Gasto" se hará una resta
                        if (operacion.contentEquals("Ingreso"))
                            dinero += total; //Sumamemos el ingreso registrado por el administrador, y que está guardado en la variable "total"
                        else if (operacion.contentEquals("Gasto"))
                            dinero -= total; //Restamos el gasto registrado, y que está guardado en la variable "total"

                        //Creamos el HashMap y le insertamos el nuevo valor de "dinero" para el campo "Dinero"
                        Map<String,Object> datosNuevos = new HashMap<>();
                        datosNuevos.put("Dinero", dinero);

                        //Llamamos al método "agregarRegistrosColeccion" de la clase FirestoreOperaciones. Le mandamos el nombre de la colección, el campo a buscar, el dato a buscar, el HashMap con los nuevos campos y datos (o los campos existentes para actualizar su contenido) e invocamos la interfaz "FirestoreInsertCallback"
                        oper.agregarRegistrosColeccion("cuadrillas", "Nombre", cuadrilla, datosNuevos, new FirestoreCallbacks.FirestoreTextCallback() {
                            @Override
                            public void onSuccess(String texto) {
                                //Si "texto" no es null, quiere decir que si se actualizó el campo "Dinero" de la cuadrilla, además, si entró a este "onSuccess" también quiere decir que lo realizó
                                if (texto != null)
                                    Log.w("ActualizarDinero", "Dinero actualizado");
                                else
                                    Log.w("ActualizarDinero", "No se encontró la cuadrilla para actualizar su dinero");
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.w("ActualizarDinero", "No se encontró la cuadrilla para actualizar su dinero: " + e);
                            }
                        });
                    }
                    else {
                        Toast.makeText(contexto, "CUADRILLA NO ENCONTRADA", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.w("ObtenerCuadrilla", "Error al obtener la cuadrilla: ", e);
                }
            });
        }
        catch (Exception e) {
            Log.w("DineroCuadrilla", e);
        }
    }

    //Método que permite obtener la cuadrilla a la que pertenece un usuario
    /*public void obtenerCuadrillaUsuario(FirestoreCallbacks.FirestoreTextCallback callback) {
        FirebaseUser user = Utilidades.obtenerUsuario(); //Obtenemos el usuario actual llamando el método utilitario "obtenerUsuario"
        String correoActual = user.getEmail();

        //Llamamos el método "obtenerUnRegistro" de la clase "FirestoreOperaciones", este nos ayudará a buscar el usuario dependiendo su correo
        oper.obtenerUnRegistro("usuarios", "Correo", correoActual, new FirestoreCallbacks.FirestoreDocumentCallback() {
            @Override
            public void onCallback(Map<String, Object> documento) {
                if (documento != null) { //Si "documento" no es nulo, quiere decir que encontró el usuario mediante el correo
                    String cuadrilla = (String) documento.get("Cuadrilla"); //Extraemos la cuadrilla de "documento" y la guardamos en la variable "cuadrilla"
                    callback.onSuccess(cuadrilla); //Invocando el "callback.onSuccess" de la interfaz "FirestoreTextCallback", le mandamos la cuadrilla obtenida
                }
                else { //Si "documento" es nulo, no se encontró el usuario en la colección, y entrará en este else
                    callback.onFailure(new Exception("No se encontró el usuario"));
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.w("BuscarDocumento", "Error al obtener el documento", e);
            }
        });
    }*/

    /*//Método para obtener los nombres de las cuadrillas almacenados en Firestore
    public void obtenerCuadrillas(final FirestoreCallback callback) {
        //El dato "cuadrillas" dentro de "db.collection" es el nombre de la colección de Firestore
        db.collection("cuadrillas")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) { //Si la extracción de datos fue exitosa, entrará a este if
                        List<String> listaCuadrillas = new ArrayList<>(); //Lista donde se almacenarán las cuadrillas

                        //For que recorrerá todos los "documents" de la colección "cuadrillas", y los irá almecenando uno por uno en la variable temporal "document"
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //Si "document" contiene el campo "Nombre", que entre al if
                            if (document.contains("Nombre")) {
                                String cuadrilla = document.getString("Nombre"); //Extraemos el contenido del campo "Nombre" y lo guardamos en la variable "cuadrilla"
                                listaCuadrillas.add(cuadrilla); //Añadimos el contenido almacenado en "cuadrilla" que será el nombre de la cuadrilla
                            }
                        }

                        callback.onCallback(listaCuadrillas);
                    }
                    else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    //Interfaz "callback" que nos ayuda a realizar operaciones que puedan tomar un tiempo en completarse, como las operaciones que requieren internet y pueden tardar un poco en realizarse debido a la conexión a internet
    public interface FirestoreCallback {
        void onCallback(List<String> lista); //Se invoca cuando la operación de extracción de datos de Firestore ha sido exitosa, y recibe como parámetro el listado de cuadrillas obtenido de la colección "cuadrillas" de Firestore
        void onFailure(Exception e); //Se invoca cuando se produce un error durante la operación de extracción de datos, y recibe como parámetro una excepción que describe el error presentado
    }*/
}