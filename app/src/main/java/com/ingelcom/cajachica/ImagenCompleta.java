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

    private ImageView imgCompleta, btnDescargar;
    private String tipo, nombreImagen = "";
    private Exportaciones exp = new Exportaciones(ImagenCompleta.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagen_completa);

        imgCompleta = findViewById(R.id.imgCompleta);
        btnDescargar = findViewById(R.id.imgDescargarImg);

        //Obtenemos el URI de la imagen, y el tipo desde el intent
        Uri imageUri = getIntent().getParcelableExtra("imageUri");
        tipo = getIntent().getStringExtra("tipoImagen");

        if (tipo.isEmpty()) { //Si "tipo" está vacío, significa que se quiere visualizar la imagen desde RegistrarGasto o RegistrarIngreso
            btnDescargar.setVisibility(View.GONE); //Ocultamos el botón de descargar ya que la imagen aún no ha sido subida a Firebase Storage porque aún no se confirma el registro
        }
        else { //En cambio, si "tipo" contiene algún texto, esto quiere decir que se quiere visualizar la imagen desde DetalleGastoIngreso
            nombreImagen = getIntent().getStringExtra("nombreImagen"); //Obtenemos el nombre de la imagen el cual es el mismo nombre que tiene en Firebase Storage (el String guardado en nombreImagen es como este ejemplo: "Imagenes/Gastos/00-00-0000 - 00:00")

            nombreImagen = nombreImagen.replaceAll(".*?(\\d{2}-\\d{2}-\\d{4} - \\d{2}:\\d{2}).*", "$1"); //Del "nombreImagen" extraemos sólo la fecha y hora (00-00-0000 - 00:00)
            nombreImagen = nombreImagen.replaceAll("[-:]", "_").replaceAll("[ ]", ""); //Ahora, de la fecha y hora eliminamos los guiones y espacios para evitar conflictos al crear el archivo de la imagen, nos quedaría con este formato: "00_00_0000_00_00"
        }

        if (imageUri != null) { //Si el URI obtenido no es nulo, que entre al if
            Glide.with(ImagenCompleta.this).load(imageUri).into(imgCompleta); //Asignamos el URI de la imagen obtenida al "imgCompleta", pero usando la biblioteca "Glide" para evitar errores
        }
    }

    public void descargarImagen(View view) {
        //Llamamos el método utilitario "verificarPermisosAlmacenamiento" donde mandamos el contexto de esta clase. Si este método devuelve un "true" significa que los permisos de almacenamiento externo ya han sido otorgados, en ese caso que entre al if
        if (Utilidades.verificarPermisosAlmacenamiento(this)) {
            exp.guardarImagen(imgCompleta, tipo, nombreImagen); //Llamamos al método "guardarImagen" de la clase Exportaciones donde mandamos el ImageView "imgCompleta", el tipo (será "Gasto" o "Ingreso"), y el "nombreImagen"
        }
    }

    @Override //Método Override que solicita los permisos de almacenamiento externo al usuario
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //If que llama al método utilitario "manejarResultadoPermisos", y le manda los datos necesarios para verificar si los permisos han sido otorgados, si así lo fue, el método retornará un "true", por lo tanto, que entre al if
        if (Utilidades.manejarResultadoPermisos(requestCode, permissions, grantResults, this)) {
            exp.guardarImagen(imgCompleta, tipo, "Factura"); //Llamamos al método "guardarImagen" de la clase Exportaciones donde mandamos el ImageView "imgCompleta", el tipo (será "Gasto" o "Ingreso"), y el "nombreImagen"
        }
    }

    public void retroceder(View view) {
        onBackPressed();
    }
}