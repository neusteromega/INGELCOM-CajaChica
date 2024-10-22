package com.ingelcom.cajachica.DAO;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ingelcom.cajachica.Herramientas.StorageCallbacks;

public class StorageOperaciones {

    private StorageReference storageReference; //Creamos un objeto de tipo "StorageReference" que nos ayudará en el proceso de subir una imagen a Firebase Storage

    //Método que nos permite subir y actualizar una imagen de Firebase Storage. Recibe el URI de la imagen a subir, la ruta completa de la imagen ya con su nombre incluído, un "tipo" que tendrá los textos "Agregar" o "Actualizar" dependiendo de la operación que se desee realizar, y una invocación a la interfaz "StorageCallback" que permitirá hacer esta operación de una manera asíncrona
    public void subirActualizarImagen(Uri imagenUri, String rutaImagen, String tipo, final StorageCallbacks.StorageCallback callback) {
        storageReference = FirebaseStorage.getInstance().getReference(rutaImagen); //Creamos una instancia de Firebase Storage donde indicamos la ruta completa de la imagen donde se guardará en Firebase Storage

        //Utilizando el método "putFile" de Firebase Storage, en donde indicamos el "imageUri" de la imagen a subir
        storageReference.putFile(imagenUri)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) { //Si la subida de la imagen fue exitosa, entrará a este método
                    if (tipo.equalsIgnoreCase("Agregar"))
                        callback.onCallback("IMAGEN SUBIDA EXITOSAMENTE EN FIREBASE STORAGE"); //Invocamos el "onCallback" de la interfaz "StorageCallback" y le mandamos un mensaje exitoso
                    else if (tipo.equalsIgnoreCase("Actualizar"))
                        callback.onCallback("IMAGEN ACTUALIZADA EXITOSAMENTE EN FIREBASE STORAGE"); //Aquí también invocamos el "onCallback" de la interfaz "StorageCallback" y le mandamos un mensaje exitoso
                }
            })
            .addOnFailureListener(new OnFailureListener() { //Si el proceso falló, entrará aquí
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.onFailure(e); //Invocamos el callback "onFailure" de la interfaz "StorageCallback" donde mandamos la excepción obtenida por si fallo el proceso de subida de la imagen
                }
            });
    }

    //Método que nos permite obtener una imagen de Firebase Storage. Mediante la interfaz "StorageURICallback" retorna el URI de la imagen
    public void obtenerImagen(String rutaImagen, final StorageCallbacks.StorageURICallback callback) {
        storageReference = FirebaseStorage.getInstance().getReference(); //Creamos una instancia de Firebase Storage
        StorageReference imagenRef = storageReference.child(rutaImagen); //Creamos un objeto de tipo StorageReference, y a este la asignamos "storageReference.child(rutaImagen)" donde se encuentra la ruta de la imagen en Firebase Storage

        //Utilizando el método "getDownloadUrl" del "StorageReference", obtenemos la imagen desde Firebase Storage
        imagenRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                callback.onCallback(uri); //Invocamos el "onCallback" de la interfaz "StorageURICallback" y le mandamos el URI de la imagen recibida
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e); //Invocamos el callback "onFailure" de la interfaz "StorageCallback" donde mandamos la excepción obtenida por si fallo el proceso de obtención de la imagen
            }
        });
    }
}