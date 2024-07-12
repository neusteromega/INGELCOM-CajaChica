package com.ingelcom.cajachica.DAO;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirestoreOperaciones {

    private FirebaseFirestore db; //Objeto que nos permitirá acceder a Firestore

    public FirestoreOperaciones() {
        db = FirebaseFirestore.getInstance(); //Obtenemos la instancia de Firestore y la almacenamos en "db"
    }

    //Método para obtener los registros de un campo específico de todos los documentos de una colección de Firestore
    public void obtenerRegistrosCampo(String nombreColeccion, String campo, final FirestoreCallbacks.FirestoreCallback callback) { //Recibe como parámetros el nombre de la colección, el nombre del campo y el "callback"
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
                        callback.onFailure(task.getException()); //Invocamos la interfaz "FirestoreCallback" con el objeto "callback" y con su método "onFailure", a este le mandamos la excepción obtenida en la variable "task"
                    }
                });
    }

    //Método para obtener un registro (un document con todos los campos y datos) de Firestore, utilizando una sentencia WHERE
    public void obtenerUnRegistro(String nombreColeccion, String campo, String dato, final FirestoreCallbacks.FirestoreDocumentCallback callback) { //Recibe como parámetros el nombre de la colección, el nombre del campo, el dato (estos dos sirven para la sentencia WHERE) y el "callback"
        //Asignamos el nombre de la colección guardado en "nombreColeccion". En el ".whereEqualTo" asignamos el campo y el dato que servirán para la sentencia WHERE
        db.collection(nombreColeccion)
            .whereEqualTo(campo, dato)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) { //Si la extracción de datos fue exitosa, entrará a este if
                        QuerySnapshot querySnapshot = task.getResult(); //Guardamos el resultado obtenido con "task.getResult()" (todos los documents de Firestore obtenidos tras la búsqueda) en la variable "QuerySnapshot" que es un objeto que contiene los resultados de una consulta a Firestore

                        if (!querySnapshot.isEmpty()) { //Si el querySnapshot no está vacío, quiere decir que la sentencia WHERE si encontró el dato en la colección, y entrará a este if
                            QueryDocumentSnapshot documentSnapshot = (QueryDocumentSnapshot) querySnapshot.getDocuments().get(0); //En una variable de tipo "QueryDocumentSnapshot" guardamos los datos del document en la posición 0 (la posición inicial) del "querySnapshot", sólo la posición inicial ya que sólo debería encontrar un document porque los números de identidad no deben repetirse
                            callback.onCallback(documentSnapshot.getData()); //Llamamos la interface "callback" y le mandamos los datos del "documentSnapshot" y estos datos están óptimos para ser guardados en un HashMap
                        }
                        else { //Entrará a este else si el querySnapshot está vacío, y eso quiere decir que la sentencia WHERE no encontró el dato en la colección
                            callback.onCallback(null); //Mandamos un "null" al HashMap de "onCallback". Esto nos servirá para darnos cuenta que no se encontró el dato en la colección
                        }
                    }
                    else {
                        callback.onFailure(task.getException()); //Invocamos la interfaz "FirestoreDocumentCallback" con el objeto "callback" y con su método "onFailure", a este le mandamos la excepción obtenido en la variable "task"
                    }
                }
            });
    }

    public void obtenerRegistros(String nombreColeccion, FirestoreCallbacks.FirestoreAllDocumentsCallback callback) {
        CollectionReference coleccion = db.collection(nombreColeccion);
        coleccion.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Map<String,Object>> listaDocumentos = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        listaDocumentos.add(document.getData());
                    }

                    callback.onCallback(listaDocumentos);
                }
                else {
                    callback.onFailure(task.getException());
                }
            }
        });
    }

    //Método que permite insertar registros (y crear un document) a una colección de Firestore
    public void insertarRegistros(String nombreColeccion, Map<String,Object> registros, final FirestoreCallbacks.FirestoreInsertCallback callback) { //Recibe como parámetros el nombre de la colección, el HashMap con el nombre de los campos y datos a insertar, y el "callback"
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

    public void agregarRegistrosColeccion(String nombreColeccion, String campoBuscar, String datoBuscar, Map<String,Object> nuevosCampos, final FirestoreCallbacks.FirestoreCallback callback) {
        db.collection(nombreColeccion)
                .whereEqualTo(campoBuscar, datoBuscar)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();

                            if (!querySnapshot.isEmpty()) {
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    String docId = document.getId();

                                    db.collection(nombreColeccion).document(docId)
                                        .update(nuevosCampos)
                                        .addOnSuccessListener(aVoid -> callback.onCallback(null))
                                        .addOnFailureListener(e -> callback.onFailure(e));
                                    return;
                                }
                            }
                            else {
                                callback.onCallback(null);
                            }
                        }
                        else {
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }

    //Interfaces "callback" que nos ayudan a realizar operaciones que puedan tomar un tiempo en completarse, como las operaciones que requieren internet y pueden tardar un poco en realizarse debido a la conexión a la red
    /*public interface FirestoreCallback {
        void onCallback(List<String> lista); //Se invoca cuando la operación de extracción de datos de Firestore ha sido exitosa, y recibe como parámetro el listado de registros obtenido de la colección de Firestore cuyo nombre se recibe como parámetro en el método "obtenerRegistros" de arriba
        void onFailure(Exception e); //Se invoca cuando se produce un error durante la operación de extracción de datos, y recibe como parámetro una excepción que describe el error presentado
    }

    public interface FirestoreDocumentCallback {
        void onCallback(Map<String, Object> documento);
        void onFailure(Exception e);
    }

    public interface FirestoreAllDocumentsCallback {
        void onCallback(List<Map<String, Object>> documentos);
        void onFailure(Exception e);
    }

    public interface FirestoreInsertCallback {
        void onSuccess(String idDocumento); //Se invoca cuando la operación de extracción de datos de Firestore ha sido exitosa,, y recibe como parámetro el id del documento recién creado en Firestore tras la inserción de datos, este id se obtiene del método "insertarRegistros" de arriba
        void onFailure(Exception e); //Se invoca cuando se produce un error durante la operación de extracción de datos, y recibe como parámetro una excepción que describe el error presentado
    }*/
}
