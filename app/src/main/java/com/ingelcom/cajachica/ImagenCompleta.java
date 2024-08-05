package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImagenCompleta extends AppCompatActivity {

    private ImageView imgCompleta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagen_completa);

        imgCompleta = findViewById(R.id.imgCompleta);

        Uri imageUri = getIntent().getParcelableExtra("imageUri"); //Obtenemos el URI de la imagen desde el intent

        if (imageUri != null) { //Si el URI obtenido no es nulo, que entre al if
            Glide.with(ImagenCompleta.this).load(imageUri).into(imgCompleta); //Asignamos el URI de la imagen obtenida al "imgCompleta", pero usando la biblioteca "Glide" para evitar errores
        }
    }

    public void retroceder(View view) {
        onBackPressed();
    }
}