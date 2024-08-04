package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

public class ImagenCompleta extends AppCompatActivity {

    private ImageView imgCompleta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagen_completa);

        imgCompleta = findViewById(R.id.imgCompleta);

        Uri imageUri = getIntent().getParcelableExtra("imageUri"); //Obtenemos el URI de la imagen desde el intent

        if (imageUri != null) { //Si el URI obtenido no es nulo, que entre al if
            imgCompleta.setImageURI(imageUri); //Asignamos el URI al ImageView "imgCompleta"
        }
    }
}