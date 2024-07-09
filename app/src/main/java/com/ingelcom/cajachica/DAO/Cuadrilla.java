package com.ingelcom.cajachica.DAO;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Cuadrilla {

    private FirebaseFirestore db; //Objeto que nos permitirá acceder a Firestore

    //Método Constructor
    public Cuadrilla() {
        db = FirebaseFirestore.getInstance(); //Obtenemos la instancia de Firestore y la almacenamos en "db"
    }

    //Método para obtener los nombres de las cuadrillas almacenados en Firestore
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
    }
}