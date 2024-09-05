package com.ingelcom.cajachica.Herramientas;

import android.net.Uri;

public class StorageCallbacks {

    //Interfaces "callback" que nos ayudan a realizar operaciones que puedan tomar un tiempo en completarse, como las operaciones que requieren internet y pueden tardar un poco en realizarse debido a la conexión a la red
    public interface StorageCallback {
        void onCallback(String texto); //Se invoca cuando la operacion de subida de una fotografia a Firebase Storage ha sido exitosa, y devuelve un String con un mensaje de éxito
        void onFailure(Exception e);
    }

    public interface StorageURICallback {
        void onCallback(Uri uri); //Se invoca cuando la operación de obtener una fotografía de Firebase Storage ha sido exitoso, y devuelve un URI con la imagen obtenida
        void onFailure(Exception e);
    }
}
