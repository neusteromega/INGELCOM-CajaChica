package com.ingelcom.cajachica.DAO;

import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ingelcom.cajachica.Herramientas.StorageCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;

public class StorageOperaciones {

    private StorageReference storageReference; //Creamos un objeto de tipo "StorageReference" que nos ayudará en el proceso de subir una imagen a Firebase Storage

    //Método que nos permite subir una imagen a Firebase Storage. Recibe el URI de la imagen a subir, el nombre de la carpeta, un Timestamp con la fecha y hora que servirá para establecer un nombre único a la imagen, una invocación a la interfaz "StorageCallback" que permitirá hacer esta operación de una manera asíncrona
    public void subirFoto(Uri imageUri, String carpeta, final StorageCallbacks.StorageCallback callback) {
        //String fechaHoraString = Utilidades.convertirTimestampAString(fechaHora, "dd-MM-yyyy - HH:mm"); //Llamamos el método utilitario "convertirTimestampAString" donde mandamos el Timestamp "fechaHora" y el formato para convertirlo a String (dd-MM-yyyy - HH:mm)
        storageReference = FirebaseStorage.getInstance().getReference(carpeta); //Creamos una instancia de Firebase Storage donde indicamos el nombre de la carpeta "Imagenes/", la subcarpeta que se recibe en la variable "carpeta", y el nombre de la imagen a subir el cual será la fecha y hora seleccionada por el usuario y que se encuentra guardada en "fechaHoraString"

        //Utilizando el método "putFile" de Firebase Storage, en donde indicamos el "imageUri" de la imagen a subir
        storageReference.putFile(imageUri)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) { //Si la subida de la imagen fue exitosa, entrará a este método
                    callback.onCallback("FOTOGRAFIA SUBIDA EXITOSAMENTE A FIREBASE STORAGE"); //Invocamos el callback "onCallback" de la interfaz "StorageCallback" y le mandamos un mensaje exitoso
                }
            })
            .addOnFailureListener(new OnFailureListener() { //Si el proceso falló, entrará aquí
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.onFailure(e); //Invocamos el callback "onFailure" de la interfaz "StorageCallback" donde mandamos la excepción obtenida por si fallo el proceso de subida de la imagen
                }
            });
    }
}
