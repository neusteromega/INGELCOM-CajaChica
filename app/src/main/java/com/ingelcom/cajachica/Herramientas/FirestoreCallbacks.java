package com.ingelcom.cajachica.Herramientas;

import java.util.List;
import java.util.Map;

public class FirestoreCallbacks {

    //Interfaces "callback" que nos ayudan a realizar operaciones que puedan tomar un tiempo en completarse, como las operaciones que requieren internet y pueden tardar un poco en realizarse debido a la conexión a la red
    public interface FirestoreListCallback {
        void onCallback(List<String> lista);
        void onFailure(Exception e);
    }

    public interface FirestoreDocumentCallback {
        void onCallback(Map<String, Object> documento);
        void onFailure(Exception e);
    }

    public interface FirestoreAllDocumentsCallback {
        void onCallback(List<Map<String, Object>> documentos);
        void onFailure(Exception e);
    }

    public interface FirestoreAllSpecialDocumentsCallback<T> { //Esta interfaz es genérica ya que uno al invocarla, establece el tipo de dato que recibirá (<T>)
        void onCallback(List<T> items);
        void onFailure(Exception e);
    }

    public interface FirestoreTextCallback {
        void onSuccess(String texto);
        void onFailure(Exception e);
    }

    public interface FirestoreValidationCallback {
        void onResultado(boolean esValido); //Recibe un valor booleano
    }
}
