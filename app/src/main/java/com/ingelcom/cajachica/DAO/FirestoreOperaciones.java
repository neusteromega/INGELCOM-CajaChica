package com.ingelcom.cajachica.DAO;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
                        List<String> listaRegistros = new ArrayList<>(); //Lista donde se almacenarán los registros a obtener de Firestore

                        //For que recorrerá todos los "documents" de la colección cuyo nombre está en "nombreColeccion", y los irá almecenando uno por uno en la variable temporal "document"
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //Si "document" contiene el campo cuyo nombre está guardado en "campo", que entre al if
                            if (document.contains(campo)) {
                                String registro = document.getString(campo); //Extraemos el contenido del campo específico y lo guardamos en la variable "registro"
                                listaRegistros.add(registro); //Añadimos el contenido almacenado en "registro" a la lista "listaRegistros"
                            }
                        }
                        callback.onCallback(listaRegistros); //Invocamos la interfaz "FirestoreCallback" con el objeto "callback" y con su método "onCallback" y le mandamos la lista de registros

                    }
                    else {
                        callback.onFailure(task.getException()); //Invocamos la interfaz "FirestoreCallback" con el objeto "callback" y con su método "onFailure", a este le mandamos la excepción obtenido en la variable "task"
                    }
                });
    }

    public void obtenerUnRegistro(String nombreColeccion, String campo, String dato, final FirestoreDocumentCallback callback) {
        db.collection(nombreColeccion)
            .whereEqualTo(campo, dato)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();

                        if (!querySnapshot.isEmpty()) {
                            QueryDocumentSnapshot documentSnapshot = (QueryDocumentSnapshot) querySnapshot.getDocuments().get(0);
                            callback.onCallback(documentSnapshot.getData());
                        }
                    }
                }
            });
    }

    //Método que permite insertar registros (y crear un document) a una colección de Firestore
    public void insertarRegistros(String nombreColeccion, Map<String,Object> registros, final FirestoreInsertCallback callback) { //Recibe como parámetros el nombre de la colección, el HashMap con el nombre de los campos y datos a insertar, y el "callback"
        //Asignamos el nombre de la colección guardado en "nombreColeccion", y el HashMap "registros"
        db.collection(nombreColeccion)
                .add(registros)
                .addOnSuccessListener(documentReference -> { //Entrará aquí si la inserción fue exitosa
                    callback.onSuccess(documentReference.getId()); //Invocamos la interfaz "FirestoreInsertCallback" con el objeto "callback" y con su método "onSuccess" y le mandamos el id del documento que se crea tras la inserción
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e); //Invocamos la interfaz "FirestoreInsertCallback" con el objeto "callback" y con su método "onFailure", a este le mandamos la excepción obtenida
                });
    }

    //Interfaces "callback" que nos ayuda a realizar operaciones que puedan tomar un tiempo en completarse, como las operaciones que requieren internet y pueden tardar un poco en realizarse debido a la conexión a internet
    public interface FirestoreCallback {
        void onCallback(List<String> lista); //Se invoca cuando la operación de extracción de datos de Firestore ha sido exitosa, y recibe como parámetro el listado de registros obtenido de la colección de Firestore cuyo nombre se recibe como parámetro en el método "obtenerRegistros" de arriba
        void onFailure(Exception e); //Se invoca cuando se produce un error durante la operación de extracción de datos, y recibe como parámetro una excepción que describe el error presentado
    }

    public interface FirestoreDocumentCallback {
        void onCallback(Map<String, Object> documento);
        void onFailure(Exception e);
    }

    public interface FirestoreInsertCallback {
        void onSuccess(String idDocumento); //Se invoca cuando la operación de extracción de datos de Firestore ha sido exitosa,, y recibe como parámetro el id del documento recién creado en Firestore tras la inserción de datos, este id se obtiene del método "insertarRegistros" de arriba
        void onFailure(Exception e); //Se invoca cuando se produce un error durante la operación de extracción de datos, y recibe como parámetro una excepción que describe el error presentado
    }
}
