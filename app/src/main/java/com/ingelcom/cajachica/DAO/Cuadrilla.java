package com.ingelcom.cajachica.DAO;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cuadrilla {

    private FirestoreOperaciones oper = new FirestoreOperaciones();

    public void actualizarDineroCuadrilla(String cuadrilla, double total, String operacion, Context context) {
        //Llamamos el método "obtenerUnRegistro" de la clase "FirestoreOperaciones", este nos ayudará a buscar la cuadrilla usando su nombre
        oper.obtenerUnRegistro("cuadrillas", "Nombre", cuadrilla, new FirestoreCallbacks.FirestoreDocumentCallback() {
            @Override
            public void onCallback(Map<String, Object> documento) {
                if (documento != null) { //Si "documento" no es nulo, quiere decir que encontró la cuadrilla mediante su nombre
                    //AQUÍ ESTÁ EL PROBLEMA
                    /*String dinero = (String) documento.get("Dinero");*/ //Obtenemos el valor guardado en el campo "Dinero" de la colección "cuadrillas" y lo guardamos en la variable double "dinero"

                    Toast.makeText(context, "CUADRILLA: " + (String) documento.get("Nombre"), Toast.LENGTH_SHORT).show();
                    //Condición que determina qué operación se hará, si es un "Ingreso" se hará una suma, si es un "Gasto" se hará una resta
                    /*if (operacion.contentEquals("Ingreso"))
                        dinero += total; //Sumamemos el ingreso registrado por el administrador, y que está guardado en la variable "total"
                    else if (operacion.contentEquals("Gasto"))
                        dinero -= total;*/ //Restamos el gasto registrado, y que está guardado en la variable "total"

                    /*Map<String,Object> datosNuevos = new HashMap<>();
                    datosNuevos.put("Dinero", dinero);

                    Toast.makeText(context, "Dinero: " + dinero, Toast.LENGTH_SHORT).show();*/

                    /*oper.agregarRegistrosColeccion("cuadrillas", "Nombre", cuadrilla, datosNuevos, new FirestoreCallbacks.FirestoreInsertCallback() {
                        @Override
                        public void onSuccess(String texto) {
                            //Si "texto" no es null, quiere decir que si se actualizó el campo "Dinero" de la cuadrilla, además, si entró a este "onSuccess" también quiere decir que lo realizó
                            if (texto != null)
                                Log.w("Actualizar Dinero", "Dinero actualizado");
                            else
                                Log.w("Actualizar Dinero", "No se encontró la cuadrilla para actualizar su dinero");
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.w("Actualizar Dinero", "No se encontró la cuadrilla para actualizar su dinero");
                        }
                    });*/
                }
                else {
                    Toast.makeText(context, "CUADRILLA NO ENCONTRADA", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.w("Obtener Cuadrilla", "Error al obtener la cuadrilla: ", e);
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