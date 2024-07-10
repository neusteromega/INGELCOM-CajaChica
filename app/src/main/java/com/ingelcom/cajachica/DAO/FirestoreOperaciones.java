package com.ingelcom.cajachica.DAO;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirestoreOperaciones {

    private FirebaseFirestore db; //Objeto que nos permitirá acceder a Firestore

    public FirestoreOperaciones() {
        db = FirebaseFirestore.getInstance(); //Obtenemos la instancia de Firestore y la almacenamos en "db"
    }

    //Método para obtener los registros de un campo específico de todos los documentos de una colección de Firestore
    public void obtenerRegistros(String nombreColeccion, String campo, final FirestoreCallback callback) { //Recibe como parámetros el nombre de la colección, el nombre del campo y el "callback"
        //Asignamos el nombre de la colección guardado en "nombreColeccion"
        db.collection(nombreColeccion)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) { //Si la extracción de datos fue exitosa, entrará a este if
                        List<String> listaRegistros = new ArrayList<>(); //Lista donde se almacenarán los registros

                        //For que recorrerá todos los "documents" de la colección cuyo nombre está en "nombreColeccion", y los irá almecenando uno por uno en la variable temporal "document"
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //Si "document" contiene el campo cuyo nombre está guardado en "campo", que entre al if
                            if (document.contains(campo)) {
                                String registro = document.getString(campo); //Extraemos el contenido del campo específico y lo guardamos en la variable "registro"
                                listaRegistros.add(registro); //Añadimos el contenido almacenado en "registro" a la lista "listaRegistros"
                            }
                        }
                        //Invocamos la interfaz "FirestoreCallback" con el objeto "callback" y con su método "onCallback" y le mandamos la lista de registros
                        callback.onCallback(listaRegistros);
                    }
                    else {
                        callback.onFailure(task.getException()); //Invocamos la interfaz "FirestoreCallback" con el objeto "callback" y con su método "onFailure", a este le mandamos la excepción obtenido en la variable "task"
                    }
                });
    }

    public void insertarRegistros(String nombreColeccion, Map<String,Object> registros, final FirestoreInsertCallback callback) {
        db.collection(nombreColeccion)
                .add(registros)
                .addOnSuccessListener(documentReference -> {
                    callback.onSuccess(documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                });
    }

    //Interfaz "callback" que nos ayuda a realizar operaciones que puedan tomar un tiempo en completarse, como las operaciones que requieren internet y pueden tardar un poco en realizarse debido a la conexión a internet
    public interface FirestoreCallback {
        void onCallback(List<String> lista); //Se invoca cuando la operación de extracción de datos de Firestore ha sido exitosa, y recibe como parámetro el listado de registros obtenido de la colección de Firestore cuyo nombre se recibe como parámetro en el método "obtenerRegistros" de arriba
        void onFailure(Exception e); //Se invoca cuando se produce un error durante la operación de extracción de datos, y recibe como parámetro una excepción que describe el error presentado
    }

    public interface FirestoreInsertCallback {
        void onSuccess(String idDocumento);
        void onFailure(Exception e);
    }
}
