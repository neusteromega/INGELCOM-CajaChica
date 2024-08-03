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

    private StorageReference storageReference;

    public void subirFoto(Uri imageUri, String carpeta, Timestamp fechaHora, final StorageCallbacks.StorageCallback callback) {
        String fechaHoraString = Utilidades.convertirTimestampAString(fechaHora, "dd-MM-yyyy - HH:mm");
        storageReference = FirebaseStorage.getInstance().getReference("Imagenes/" + carpeta + fechaHoraString);

        storageReference.putFile(imageUri)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    callback.onCallback("FOTOGRAFIA SUBIDA EXITOSAMENTE A FIREBASE STORAGE");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.onFailure(e);
                }
            });
    }
}
