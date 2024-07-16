package com.ingelcom.cajachica.DAO;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;

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
                        double dinero = 0.0; //Creamos una variable double donde se guardará la conversión de "valor"

                        if (valor instanceof Long) //Verificamos si "valor" es una instancia de Long
                            dinero = ((Long) valor).doubleValue(); //Si lo es, lo convertimos a Double y que se guarde en "dinero"
                        else if (valor instanceof Double) //También verificamos si "valor" es una instancia de Double
                            dinero = (Double) valor; //Si lo es, los casteamos con Double y que lo guarde en "dinero"

                        //Condición que determina qué operación se hará, si es un "Ingreso" se hará una suma, si es un "Gasto" se hará una resta
                        if (operacion.contentEquals("Ingreso"))
                            dinero += total; //Sumamemos el ingreso registrado por el administrador, y que está guardado en la variable "total"
                        else if (operacion.contentEquals("Gasto"))
                            dinero -= total; //Restamos el gasto registrado, y que está guardado en la variable "total"

                        //Creamos el HashMap y le insertamos el nuevo valor de "dinero" para el campo "Dinero"
                        Map<String,Object> datosNuevos = new HashMap<>();
                        datosNuevos.put("Dinero", dinero);

                        //Llamamos al método "agregarRegistrosColeccion" de la clase FirestoreOperaciones. Le mandamos el nombre de la colección, el campo a buscar, el dato a buscar, el HashMap con los nuevos campos y datos (o los campos existentes para actualizar su contenido) e invocamos la interfaz "FirestoreInsertCallback"
                        oper.agregarRegistrosColeccion("cuadrillas", "Nombre", cuadrilla, datosNuevos, new FirestoreCallbacks.FirestoreInsertCallback() {
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
    public void obtenerCuadrillaUsuario() {
        FirebaseUser user = Utilidades.obtenerUsuario(); //Obtenemos el usuario actual llamando el método utilitario "obtenerUsuario"
        String correoActual = user.getEmail();

        //Llamamos el método "obtenerUnRegistro" de la clase "FirestoreOperaciones", este nos ayudará a buscar el usuario dependiendo su correo
        oper.obtenerUnRegistro("usuarios", "Correo", correoActual, new FirestoreCallbacks.FirestoreDocumentCallback() {
            @Override
            public void onCallback(Map<String, Object> documento) {
                if (documento != null) { //Si "documento" no es nulo, quiere decir que encontró el usuario mediante el correo
                    String cuadrilla = (String) documento.get("Cuadrilla");
                }
                else { //Si "documento" es nulo, no se encontró el usuario en la colección, y entrará en este else
                    Log.w("CuadrillaUsuario", "No se encontró el usuario.");
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.w("BuscarDocumento", "Error al obtener el documento", e);
            }
        });
    }

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