package com.ingelcom.cajachica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ingelcom.cajachica.Herramientas.Exportaciones;
import com.ingelcom.cajachica.Herramientas.Utilidades;

public class ImagenCompleta extends AppCompatActivity {

    private ImageView imgCompleta;
    private String tipo, nombreImagen;
    private Exportaciones exp = new Exportaciones(ImagenCompleta.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagen_completa);

        imgCompleta = findViewById(R.id.imgCompleta);

        Uri imageUri = getIntent().getParcelableExtra("imageUri"); //Obtenemos el URI de la imagen desde el intent
        tipo = getIntent().getStringExtra("tipoImagen");
        nombreImagen = getIntent().getStringExtra("nombreImagen");

        nombreImagen = nombreImagen.replaceAll(".*?(\\d{2}-\\d{2}-\\d{4} - \\d{2}:\\d{2}).*", "$1");
        nombreImagen = nombreImagen.replaceAll("[-:]", "_").replaceAll("[ ]", "");

        if (imageUri != null) { //Si el URI obtenido no es nulo, que entre al if
            Glide.with(ImagenCompleta.this).load(imageUri).into(imgCompleta); //Asignamos el URI de la imagen obtenida al "imgCompleta", pero usando la biblioteca "Glide" para evitar errores
        }
    }

    public void descargarImagen(View view) {
        //Llamamos el método utilitario "verificarPermisosAlmacenamiento" donde mandamos el contexto de esta clase. Si este método devuelve un "true" significa que los permisos de almacenamiento externo ya han sido otorgados, en ese caso que entre al if
        if (Utilidades.verificarPermisosAlmacenamiento(this)) {
            exp.guardarImagen(imgCompleta, "Ingreso", nombreImagen);
        }
    }

    @Override //Método Override que solicita los permisos de almacenamiento externo al usuario
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //If que llama al método utilitario "manejarResultadoPermisos", y le manda los datos necesarios para verificar si los permisos han sido otorgados, si así lo fue, el método retornará un "true", por lo tanto, que entre al if
        if (Utilidades.manejarResultadoPermisos(requestCode, permissions, grantResults, this)) {
            exp.guardarImagen(imgCompleta, tipo, "Factura");
        }
    }

    public void retroceder(View view) {
        onBackPressed();
    }
}