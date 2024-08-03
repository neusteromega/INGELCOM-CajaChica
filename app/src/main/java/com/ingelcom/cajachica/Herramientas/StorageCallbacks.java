package com.ingelcom.cajachica.Herramientas;

public class StorageCallbacks {

    //Interfaces "callback" que nos ayudan a realizar operaciones que puedan tomar un tiempo en completarse, como las operaciones que requieren internet y pueden tardar un poco en realizarse debido a la conexión a la red
    public interface StorageCallback {
        void onCallback(String texto); //Se invoca cuando la operacion de subida de una fotografia a Firebase Storage ha sido exitosa, y devuelve un String con un mensaje de éxito
        void onFailure(Exception e);
    }
}
