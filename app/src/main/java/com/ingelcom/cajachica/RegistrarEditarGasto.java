package com.ingelcom.cajachica;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.ingelcom.cajachica.DAO.Cuadrilla;
import com.ingelcom.cajachica.DAO.FirestoreOperaciones;
import com.ingelcom.cajachica.DAO.Gasto;
import com.ingelcom.cajachica.DAO.StorageOperaciones;
import com.ingelcom.cajachica.DAO.Usuario;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.StorageCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RegistrarEditarGasto extends AppCompatActivity {

    private LinearLayout llFecha, llCuadrilla, llDinero;
    private TextView btnReintentarConexion, lblTitulo, lblDinero, lblFecha, btnSubirCambiarFoto, btnConfirmar;
    private EditText txtLugar, txtDescripcion, txtFactura, txtTotal;
    private ImageView imgFoto, btnEliminarFoto;
    private Spinner spCuadrillas, spTipoCompras;
    private ProgressBar pbCargar;
    private View viewNoInternet;

    private String nombreActivity, dineroDisponible, id, fechaHora, cuadrilla, lugarCompra, tipoCompra, descripcion, numeroFactura, usuario, rol, imagen, total;
    private Timestamp timestamp = null;
    private Uri imageUri = null, imageUriVieja;

    private FirestoreOperaciones oper = new FirestoreOperaciones();
    private StorageOperaciones stor = new StorageOperaciones();
    private Cuadrilla cuad = new Cuadrilla(RegistrarEditarGasto.this);
    private Gasto gast = new Gasto(RegistrarEditarGasto.this);
    private Usuario usu = new Usuario(RegistrarEditarGasto.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_editar_gasto);

        inicializarElementos();
        obtenerDatos();
        establecerElementos();
        inicializarSpinners();

        //Evento Click del botón "Reintentar" de la vista "viewNoInternet"
        btnReintentarConexion.setOnClickListener(v -> {
            Utilidades.mostrarMensajePorInternetCaido(this, viewNoInternet); //Llamamos el método utilitario "mostrarMensajePorInternetCaido" donde mandamos la vista "viewNoInternet" donde se hará visible cuando no haya conexión a internet y se ocultará cuando si haya
        });
    }

    private void inicializarElementos() {
        llFecha = findViewById(R.id.LLFechaRG);
        llCuadrilla = findViewById(R.id.LLCuadrillaRG);
        llDinero = findViewById(R.id.LLDineroRG);

        lblTitulo = findViewById(R.id.lblTituloRG);
        lblDinero = findViewById(R.id.lblCantDineroRG);
        lblFecha = findViewById(R.id.lblFechaRG); //POSIBLE ELIMINACIÓN
        txtLugar = findViewById(R.id.txtLugarCompraRG);
        txtDescripcion = findViewById(R.id.txtDescripcionRG);
        txtFactura = findViewById(R.id.txtFacturaRG);
        txtTotal = findViewById(R.id.txtTotalRG);

        spCuadrillas = findViewById(R.id.spCuadrillaRG);
        spTipoCompras = findViewById(R.id.spTipoCompraRG);
        imgFoto = findViewById(R.id.imgFotoEvidenciaRG);

        pbCargar = findViewById(R.id.pbCargarREG);
        btnEliminarFoto = findViewById(R.id.imgEliminarFotoRG);
        btnSubirCambiarFoto = findViewById(R.id.btnSubirCambiarFotoRG);
        btnConfirmar = findViewById(R.id.btnConfirmarRG);
        viewNoInternet = findViewById(R.id.viewNoInternetRG);
        btnReintentarConexion = findViewById(R.id.btnReintentarConexion);

        Utilidades.mostrarMensajePorInternetCaido(this, viewNoInternet); //Llamamos el método utilitario "mostrarMensajePorInternetCaido" donde mandamos la vista "viewNoInternet" donde se hará visible cuando no haya conexión a internet y se ocultará cuando si haya
    }

    private void obtenerDatos() {
        //Obtenemos el nombre del activity y el dinero disponible que se envía desde el activity anterior, lo hacemos llamando a la función "obtenerStringExtra" de la clase "Utilidades", y le mandamos "this" para referenciar esta actividad y los nombres de las claves del putExtra
        nombreActivity = Utilidades.obtenerStringExtra(this, "ActivityREG");
        dineroDisponible = Utilidades.obtenerStringExtra(this, "DineroDisponible");

        switch (nombreActivity) {
            case "EditarGastoEmpleado":

            case "EditarGastoAdmin":
                id = Utilidades.obtenerStringExtra(this, "ID");
                fechaHora = Utilidades.obtenerStringExtra(this, "FechaHora");
                cuadrilla = Utilidades.obtenerStringExtra(this, "Cuadrilla");
                lugarCompra = Utilidades.obtenerStringExtra(this, "LugarCompra");
                tipoCompra = Utilidades.obtenerStringExtra(this, "TipoCompra");
                descripcion = Utilidades.obtenerStringExtra(this, "Descripcion");
                numeroFactura = Utilidades.obtenerStringExtra(this, "NumeroFactura");
                usuario = Utilidades.obtenerStringExtra(this, "Usuario");
                imagen = Utilidades.obtenerStringExtra(this, "Imagen");
                total = Utilidades.obtenerStringExtra(this, "Total");
                break;
        }
    }

    private void establecerElementos() {
        if (nombreActivity != null) { //Que entre al if si "nombreActivity" no es nulo
            switch (nombreActivity) { //El "nombreActivity" nos sirve para saber la pantalla con la que trabajaremos
                case "RegistrarGastoEmpleado": //Establecemos los elementos gráficos si la pantalla es "RegistrarGastoEmpleado"
                    lblTitulo.setText("Registrar Gasto");
                    lblDinero.setText("L. " + dineroDisponible);

                    //Ocultamos estos dos elementos (Fecha y Cuadrilla) para que el empleado no pueda verlos
                    llFecha.setVisibility(View.GONE);
                    llCuadrilla.setVisibility(View.GONE);
                    break;

                case "RegistrarGastoAdmin": //Establecemos los elementos gráficos si la pantalla es "RegistrarGastoAdmin"
                    lblTitulo.setText("Registrar Gasto");
                    llDinero.setVisibility(View.GONE);
                    break;

                case "EditarGastoEmpleado": //Establecemos los elementos gráficos si la pantalla es "EditarGastoEmpleado"
                    lblTitulo.setText("Editar Gasto");
                    lblDinero.setText("L. " + dineroDisponible);
                    txtLugar.setText(lugarCompra);
                    txtDescripcion.setText(descripcion);
                    txtFactura.setText(numeroFactura);
                    txtTotal.setText(total);

                    //Ocultamos estos dos elementos (Fecha y Cuadrilla) para que el empleado no pueda verlos
                    llFecha.setVisibility(View.GONE);
                    llCuadrilla.setVisibility(View.GONE);

                    imgFoto.setVisibility(View.VISIBLE);
                    btnEliminarFoto.setVisibility(View.VISIBLE);
                    btnSubirCambiarFoto.setText("Cambiar Fotografía");

                    pbCargar.setVisibility(View.VISIBLE); //Ponemos visible el progressBar

                    try {
                        //Llamamos el método "obtenerImagen" de la clase StorageOperaciones al cual le mandamos la ruta de la imagen a obtener guardada en la variable global "imagen", y realizamos una invocación a la interfaz "StorageURICallback"
                        stor.obtenerImagen(imagen, new StorageCallbacks.StorageURICallback() {
                            @Override
                            public void onCallback(Uri uri) { //En este "Uri" se encuentra el URI de la imagen obtenida de Firebase Storage
                                imageUriVieja = uri; //Como la imagen ya se cargó, asignamos el URI de la imagen obtenido a la variable global "imageUriVieja" que indica que es la imagen que ya viene de Firebase Storage, no la que cargara ahorita el usuario
                                Glide.with(RegistrarEditarGasto.this).load(uri).into(imgFoto); //Asignamos el URI de la imagen obtenida al "imgFoto", pero usando la biblioteca "Glide" para evitar errores
                                pbCargar.setVisibility(View.GONE); //Ocultamos el progressBar ya cuando la imagen se ha cargado
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.w("ObtenerImagen", "Error al obtener el URI de la imagen: " + e);
                            }
                        });
                    }
                    catch (Exception e) {
                        Log.w("ObtenerImagenStorage", e);
                    }
                    break;

                case "EditarGastoAdmin":
                    lblTitulo.setText("Editar Gasto");
                    llDinero.setVisibility(View.GONE);
                    lblFecha.setText(fechaHora);
                    txtLugar.setText(lugarCompra);
                    txtDescripcion.setText(descripcion);
                    txtFactura.setText(numeroFactura);
                    txtTotal.setText(total);

                    //Como la fechaHora se obtiene en formato String, usamos el método utilitario "convertirFechaHoraATimestamp" para convertirlo a Timestamp y el resultado lo guardamos en la variable global "timestamp"
                    timestamp = Utilidades.convertirFechaHoraATimestamp(fechaHora);

                    imgFoto.setVisibility(View.VISIBLE);
                    btnEliminarFoto.setVisibility(View.VISIBLE);
                    btnSubirCambiarFoto.setText("Cambiar Fotografía");

                    pbCargar.setVisibility(View.VISIBLE); //Ponemos visible el progressBar

                    try {
                        //Llamamos el método "obtenerImagen" de la clase StorageOperaciones al cual le mandamos la ruta de la imagen a obtener guardada en la variable global "imagen", y realizamos una invocación a la interfaz "StorageURICallback"
                        stor.obtenerImagen(imagen, new StorageCallbacks.StorageURICallback() {
                            @Override
                            public void onCallback(Uri uri) { //En este "Uri" se encuentra el URI de la imagen obtenida de Firebase Storage
                                imageUriVieja = uri; //Como la imagen ya se cargó, asignamos el URI de la imagen obtenido a la variable global "imageUriVieja" que indica que es la imagen que ya viene de Firebase Storage, no la que cargara ahorita el usuario
                                Glide.with(RegistrarEditarGasto.this).load(uri).into(imgFoto); //Asignamos el URI de la imagen obtenida al "imgFoto", pero usando la biblioteca "Glide" para evitar errores
                                pbCargar.setVisibility(View.GONE); //Ocultamos el progressBar ya cuando la imagen se ha cargado
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.w("ObtenerImagen", "Error al obtener el URI de la imagen: " + e);
                            }
                        });
                    }
                    catch (Exception e) {
                        Log.w("ObtenerImagenStorage", e);
                    }
                    break;
            }
        }
    }

    //Método Click del LinearLayout de Fecha, el cual al dar clic en él, se mostrará un calendario emergente para seleccionar una fecha, y luego un reloj para seleccionar la hora
    public void seleccionarFecha(View view) {
        final Calendar calendar = Calendar.getInstance(); //Creamos una instancia de Calendar que representa la fecha y hora actuales. Y en ella se irán almacenando las selecciones de fecha y hora que haga el usuario en el calendario y reloj

        //Inicializamos el DatePickerDialog con el año, mes y día actuales
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                //Actualizamos el "calendar" con el año, mes y día seleccionados por el usuario
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                //Inicializamos el TimePickerDialog después de seleccionar la fecha
                TimePickerDialog timePickerDialog = new TimePickerDialog(RegistrarEditarGasto.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        //Actualizamos el "calendar" con la hora y los minutos seleccionados por el usuario
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        //Formateamos y mostramos la fecha y hora seleccionada en el TextView
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault());
                        String fechaHoraSeleccionada = sdf.format(calendar.getTime());
                        lblFecha.setText(fechaHoraSeleccionada);

                        Date fechaHoraDate = calendar.getTime(); //Convertimos la fecha y hora seleccionada a un objeto Date
                        timestamp = new Timestamp(fechaHoraDate); //Convertimos el objeto Date a un objeto Timestamp (Variable global inicializada en null arriba)
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

                timePickerDialog.show(); //Mostramos el TimePickerDialog
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show(); //Mostramos el DatePickerDialog
    }

    private void inicializarSpinners() {
        try {
            //Para inicializar los spinners, llamamos al método "obtenerRegistros" de la clase "FirestoreOperaciones" a la cual le mandamos el nombre de la colección y el nombre del campo de Firestore de los cuales queremos obtener los registros. También invocamos los métodos "onCallback" y "onFailure" de la interfaz FirestoreCallback
            //CUADRILLAS
            oper.obtenerRegistrosCampo("cuadrillas", "Nombre", new FirestoreCallbacks.FirestoreListCallback() {
                @Override
                public void onCallback(List<String> lista) {
                    lista = Utilidades.ordenarListaPorAlfabetico(lista, "Nombre", "Ascendente");
                    //Creamos un adapter de tipo ArrayAdapter el cual le pasamos el contexto de este Activity, la vista layout de las opciones del Spinner (R.layout.spinner_items), y la lista de valores que se recibe en "lista" al llamar a la interfaz FirestoreCallback
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(RegistrarEditarGasto.this, R.layout.spinner_items, lista);
                    spCuadrillas.setAdapter(adapter); //Asignamos el adapter al Spinner "spCuadrillas"

                    if (cuadrilla != null && !cuadrilla.isEmpty()) { //Si "cuadrilla" (la variable global que recibe la cuadrilla del Activity anterior) no es nula y no está vacía, significa que si está recibiendo una Cuadrilla del activity anterior, por lo tanto, que entre al if
                        int posicionCuadrilla = adapter.getPosition(cuadrilla); //Obtenemos la posición de la cuadrilla recibida en el Spinner, y guardamos dicha posición en una variable int
                        spCuadrillas.setSelection(posicionCuadrilla); //Una vez obtenemos la posición de la cuadrilla recibida en el Spinner, la asignamos al "spCuadrillas" para que al cargar el activity, ya esté seleccionada la cuadrilla específica en el Spinner
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.w("Activity", "Error al obtener las cuadrillas.", e);
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerCuadrillas", e);
        }

        try {
            //TIPO DE COMPRAS
            oper.obtenerRegistrosCampo("tipoCompras", "Nombre", new FirestoreCallbacks.FirestoreListCallback() {
                @Override
                public void onCallback(List<String> lista) {
                    //Creamos un adapter de tipo ArrayAdapter el cual le pasamos el contexto de este Activity, la vista layout de las opciones del Spinner (R.layout.spinner_items), y la lista de valores que se recibe en "lista" al llamar a la interfaz FirestoreCallback
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(RegistrarEditarGasto.this, R.layout.spinner_items, lista);
                    spTipoCompras.setAdapter(adapter); //Asignamos el adapter al Spinner "spTipoCompras"

                    if (tipoCompra != null && !tipoCompra.isEmpty()) { //Si "tipoCompra" (la variable global que recibe el tipo de compra del Activity anterior) no es nula y no está vacía, significa que si está recibiendo un Tipo de Compra del activity anterior, por lo tanto, que entre al if
                        int posicionCompra = adapter.getPosition(tipoCompra); //Obtenemos la posición del tipo de compra recibida en el Spinner, y guardamos dicha posición en una variable int
                        spTipoCompras.setSelection(posicionCompra); //Una vez obtenemos la posición del tipo de compra recibido en el Spinner, la asignamos al "spTipoCompra" para que al cargar el activity, ya esté seleccionada el tipo de compra específico en el Spinner
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.w("Activity", "Error al obtener los tipos de compras.", e);
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerTipoCompras", e);
        }
    }

    public void subirFoto(View view) {
        mostrarDialogBottomSheet();
    }

    private void mostrarDialogBottomSheet() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_foto); //Asignamos la vista que tendrá el bottomSheet, en este caso, es el elemento layout "bottomsheet_foto"

        LinearLayout tomarFoto = dialog.findViewById(R.id.LLTomarFotoBSFoto);
        LinearLayout seleccionarFoto = dialog.findViewById(R.id.LLSeleccionarFotoBSFoto);

        tomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                abrirCamara();
            }
        });

        seleccionarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                seleccionarImagen();
            }
        });

        dialog.show(); //Mostramos el "dialog" con ".show();"
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); //Asignamos el "Layout" que tendrá el dialog
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //Asignamos el color transparente para el background del dialog
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Asignamos las animaciones para el dialog, dichas animaciones están asignadas en "R.style.DialogAnimation"
        dialog.getWindow().setGravity(Gravity.BOTTOM); //Asignamos un "Gravity.BOTTOM" para que el dialog se muestre en la parte inferior
    }

    private void abrirCamara() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //Creamos un nuevo intent con la acción de "Capturar Imagen" (Image Capture) de "MediaStore"

            //Verificamos si el intent de la cámara puede manejar la captura de la imagen
            if (intent.resolveActivity(getPackageManager()) != null) {
                //Creamos un ContentValues para almacenar los metadatos de la imagen
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "Nueva Imagen");
                values.put(MediaStore.Images.Media.DESCRIPTION, "Desde la cámara");

                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values); //Insertamos la imagen en el MediaStore y obtenemos el URI
                imageUriVieja = null; //Establecemos null en la "imageUriVieja" para indicar que la imagen inicial ya no existe

                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); //Pasamos el URI al intent de la cámara
                startActivityForResult(intent, 101); //Iniciamos la actividad de la cámara
            }
        }
        catch (Exception e) {
            Log.e("AbrirCamara", "Error al abrir la cámara: ", e);
        }
    }

    private void seleccionarImagen() {
        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT); //Asignamos que la acción del Intent será "Obtener Contenido" (Get Content)
            startActivityForResult(intent, 100);
        }
        catch (Exception e) {
            Log.e("ElegirImagen", "Error al abrir la galería: ", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == 100 && data != null & data.getData() != null) { //Si el "requestCode" es 100, significa que se está seleccionando una imagen de la galería
                imageUri = data.getData(); //Obtenemos el URI de la imagen seleccionada y lo guardamos en la variable "imageUri" de tipo URI
                imageUriVieja = null; //Establecemos null en la "imageUriVieja" para indicar que la imagen inicial ya no existe

                //Mostramos el imageView con la foto recién seleccionada, el botón de eliminar foto y asignamos el texto "Cambiar Fotografía" al botón de subir y cambiar foto
                imgFoto.setVisibility(View.VISIBLE);
                btnEliminarFoto.setVisibility(View.VISIBLE);
                btnSubirCambiarFoto.setText("Cambiar Fotografía");
                imgFoto.setImageURI(imageUri);

                if (getCurrentFocus() != null) { //Si "getCurrentFocus" no es nulo, significa que el focus está ubicado en algún elemento como un EditText
                    getCurrentFocus().clearFocus(); //Limpiamos el focus para que al cargar la imagen, el focus no se muestre en ningún EditText y muestre el teclado
                }
            }
            else if (requestCode == 101 && resultCode == RESULT_OK) { //En cambio, si el "requestCode" es 101, significa que se está tomando una fotografía
                //Mostramos el imageView con la foto recién seleccionada, el botón de eliminar foto y asignamos el texto "Cambiar Fotografía" al botón de subir y cambiar foto
                imgFoto.setVisibility(View.VISIBLE);
                btnEliminarFoto.setVisibility(View.VISIBLE);
                btnSubirCambiarFoto.setText("Cambiar Fotografía");
                imgFoto.setImageURI(imageUri);

                if (getCurrentFocus() != null) { //Si "getCurrentFocus" no es nulo, significa que el focus está ubicado en algún elemento como un EditText
                    getCurrentFocus().clearFocus(); //Limpiamos el focus para que al cargar la imagen, el focus no se muestre en ningún EditText y muestre el teclado
                }
            }
        }
        catch (Exception e) {
            Log.w("SeleccionarImagen", e);
        }
    }

    public void mostrarImagenCompleta(View view) {
        HashMap<String, Object> datosImagen = new HashMap<>();

        /*Intent intent = new Intent(this, ImagenCompleta.class);
        intent.putExtra("imageUri", imageUri); // Enviar el URI de la imagen
        startActivity(intent);*/

        if (imageUri != null) //Si "imageUri" no es nulo, que muestre en pantalla completa la foto recién cargada
            datosImagen.put("imageUri", imageUri); //Enviamos el URI de la imagen
        else //En cambio, si "imageUri" si es nulo, que muestre en pantalla completa la foto extraída de Firebase Storage al querer editar un gasto
            datosImagen.put("imageUri", imageUriVieja); //Enviamos el URI de la imagen

        datosImagen.put("tipoImagen", ""); //Mandamos el tipoImagen vacío ya que no queremos que la imagen se pueda descargar ya que aún no ha sido subida a Firebase Storage

        Utilidades.iniciarActivityConDatos(RegistrarEditarGasto.this, ImagenCompleta.class, datosImagen);
    }

    public void eliminarFoto(View view) {
        //Creamos un alertDialog que pregunte si se desea eliminar la imagen seleccionada
        new AlertDialog.Builder(this).setTitle("ELIMINAR IMAGEN").setMessage("¿Está seguro que desea eliminar la imagen?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { //Si se selecciona la opción positiva, entrará aquí
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Establecemos ambos "imageUri" en null para recalcar que no hay ninguna imagen cargada
                        imageUri = null;
                        imageUriVieja = null;

                        //Ocultamos el imageView con la foto, el botón de eliminar Foto, y le cambiamos el texto al botón de subir y cambiar foto
                        imgFoto.setVisibility(View.GONE);
                        btnEliminarFoto.setVisibility(View.GONE);
                        btnSubirCambiarFoto.setText("Subir Fotografía");
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() { //Si se seleccionó la opción negativa, entrará aquí y solamente mostrará un mensaje en el Logcat
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("Mensaje", "Se canceló la acción"); //Se muestra un mensaje en el Logcat indicando que se canceló la acción
                    }
                }).show();
    }

    public void confirmar(View view) {
        //Llamamos el método utilitario "mostrarMensajePorInternetCaidoBoolean" donde mandamos la vista "viewNoInternet" donde se hará visible cuando no haya conexión a internet y se ocultará cuando si haya. Este retorna un booleano indicando si hay internet o no
        boolean internetDisponible = Utilidades.mostrarMensajePorInternetCaidoBoolean(this, viewNoInternet);

        if (internetDisponible) { //Si el booleano guardado en "internetDisponible" es true, significa que si hay internet, entonces que entre al if para hacer las operaciones de confirmación. Pero si es un false, significa que no hay internet, entonces que no entre al if y muestre la vista "view_nointernet" (esto se muestra en el método utilitario)
            if (nombreActivity != null) { //Que entre al if si "nombreActivity" no es nulo
                switch (nombreActivity) { //El "nombreActivity" nos sirve para saber la pantalla con la que trabajaremos
                    case "RegistrarGastoAdmin": //Si estamos en la pantalla de "RegistrarGastoAdmin", al dar clic en el botón "Confirmar" que realice las operaciones de este case

                        //Creamos un alertDialog que pregunte si se desea registrar el gasto de dinero a la cuadrilla seleccionada
                        new AlertDialog.Builder(this).setTitle("REGISTRAR GASTO").setMessage("¿Está seguro que desea registrar el gasto de dinero a la cuadrilla seleccionada?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { //Si se selecciona la opción positiva, entrará aquí y al método "insertarGasto()"
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        insertarGasto("GastoAdmin");
                                    }
                                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() { //Si se seleccionó la opción negativa, entrará aquí y solamente mostrará un mensaje en Logcat
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Log.d("Mensaje", "Se canceló la acción"); //Se muestra un mensaje en el Logcat indicando que se canceló la acción
                                    }
                                }).show();
                        break;

                    case "RegistrarGastoEmpleado":

                        //Creamos un alertDialog que pregunte si se desea registrar el gasto de dinero
                        new AlertDialog.Builder(this).setTitle("REGISTRAR GASTO").setMessage("¿Está seguro que desea registrar el gasto de dinero?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { //Si se selecciona la opción positiva, entrará aquí y al método "insertarGasto()"
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        insertarGasto("GastoEmpleado");
                                    }
                                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() { //Si se seleccionó la opción negativa, entrará aquí y solamente mostrará un mensaje en Logcat
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Log.d("Mensaje", "Se canceló la acción"); //Se muestra un mensaje en el Logcat indicando que se canceló la acción
                                    }
                                }).show();
                        break;

                    case "EditarGastoEmpleado":

                        //Creamos un alertDialog que pregunte si se desea editar el ingreso de dinero
                        new AlertDialog.Builder(this).setTitle("EDITAR GASTO").setMessage("¿Está seguro que desea modificar los datos del gasto?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { //Si se selecciona la opción positiva, entrará aquí y al método "editarGasto()"
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        editarGasto("Empleado"); //Llamamos el método "editarGasto" de abajo para que se complete la modificación de los datos, y le mandamos la palabra "Empleado" para que sepa que modificará un gasto hecho por un empleado
                                    }
                                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() { //Si se seleccionó la opción negativa, entrará aquí y solamente mostrará un mensaje en el Logcat
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Log.d("Mensaje", "Se canceló la acción"); //Se muestra un mensaje en el Logcat indicando que se canceló la acción
                                    }
                                }).show();
                        break;

                    case "EditarGastoAdmin":

                        //Creamos un alertDialog que pregunte si se desea editar el gasto de dinero a la cuadrilla seleccionada
                        new AlertDialog.Builder(this).setTitle("EDITAR GASTO").setMessage("¿Está seguro que desea modificar los datos del gasto?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { //Si se selecciona la opción positiva, entrará aquí y al método "editarGasto()"
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        editarGasto("Admin"); //Llamamos el método "editarGasto" de abajo para que se complete la modificación de los datos, y le mandamos la palabra "Admin" para que sepa que modificará un gasto hecho por un administrador
                                    }
                                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() { //Si se seleccionó la opción negativa, entrará aquí y solamente mostrará un mensaje en el Logcat
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Log.d("Mensaje", "Se canceló la acción"); //Se muestra un mensaje en el Logcat indicando que se canceló la acción
                                    }
                                }).show();
                        break;
                }
            }
        }
    }

    private void insertarGasto(String tipoGasto) {
        //Enlazamos los EditText con las siguientes variables String
        String lugarCompra = txtLugar.getText().toString();
        String descripcion = txtDescripcion.getText().toString();
        String factura = txtFactura.getText().toString();
        String total = txtTotal.getText().toString();

        //Obtenemos la selección hecha en los Spinners
        String cuadrillaTXT = spCuadrillas.getSelectedItem().toString();
        String tipoCompra = spTipoCompras.getSelectedItem().toString();

        try {
            //Llamamos el método "obtenerUsuarioActual" de la clase "Usuario" y creamos una invocación a la interfaz "FirestoreDocumentCallback"
            usu.obtenerUsuarioActual(new FirestoreCallbacks.FirestoreDocumentCallback() {
                @Override
                public void onCallback(Map<String, Object> documento) { //Los datos del usuario están guardados en el HashMap "documento"
                    if (documento != null) { //Si "documento" no es nulo, quiere decir que encontró el usuario mediante el correo
                        //Obtenemos estos 3 datos del usuario y los guardamos en sus respectivas variables
                        String nombre = (String) documento.get("Nombre");
                        String cuadrillaBDD = (String) documento.get("Cuadrilla");
                        String rol = (String) documento.get("Rol");

                        //Dentro de ambas condiciones, llamamos el método "registrarGasto" donde se hará el proceso de inserción a Firestore, le mandamos los textboxes y selecciones de los spinners de esta pantalla
                        if (tipoGasto.equalsIgnoreCase("GastoAdmin"))
                            gast.registrarGasto(nombre, timestamp, rol, cuadrillaTXT, lugarCompra, tipoCompra, descripcion, factura, total, imageUri, false, true); //Si el gasto lo registra un admin, mandamos "cuadrillaTXT" que es la selección de la cuadrilla en el Spinner, y un "false" indicando que no debe restar el gasto del dinero disponible de la cuadrilla ya que el gasto lo está registrando un admin con un dinero aparte
                        else if (tipoGasto.equalsIgnoreCase("GastoEmpleado"))
                            gast.registrarGasto(nombre, timestamp, rol, cuadrillaBDD, lugarCompra, tipoCompra, descripcion, factura, total, imageUri, true, false); //Si el gasto lo registra un empleado, mandamos "cuadrillaBDD" que es la extracción de la cuadrilla a la que pertenece el usuario actual, y un true indicando que SI debe restar el gasto registrado del dinero disponible de la cuadrilla del usuario
                    }
                    else { //Si "documento" es nulo, no se encontró el usuario en la colección, y entrará en este else
                        Log.w("ObtenerUsuario", "Usuario no encontrado");
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.w("BuscarUsuario", "Error al obtener el usuario", e);
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerUsuario", e);
        }
    }

    //Método que permite editar un gasto para un empleado o un administrador
    private void editarGasto(String tipo) {
        //Enlazamos los EditText con las siguientes variables String
        String lugarCompra = txtLugar.getText().toString();
        String descripcion = txtDescripcion.getText().toString();
        String factura = txtFactura.getText().toString();
        String totalNuevo = txtTotal.getText().toString();

        //Obtenemos la selección hecha en los Spinners
        String cuadrillaNueva = spCuadrillas.getSelectedItem().toString();
        String tipoCompra = spTipoCompras.getSelectedItem().toString();

        //Si "tipo" es igual (ignorando mayúsculas y minúsculas) a la palabra "Empleado", que entre al if
        if (tipo.equalsIgnoreCase("Empleado")) {
            //Llamamos el método "editarGasto" de la clase Gasto donde se hará el proceso de modificación de los datos del gasto; para ello le mandamos el "id", un "null" para indicar que la fecha no se cambiará, la cuadrilla no se cambia así que se coloca la misma que se recibe del activity anterior (DetalleGastoIngreso), el resto de datos de los EditTexts, el Spinner de tipoCompra, la ruta de la imagen guardada en "imagen", el total anterior guardado en la variable global "total" y el "totalNuevo" que se extrae del EditText, los URIs de la imagen cargada de Firebase Storage y de la posible nueva imagen que subirá el usuario para reemplazar la imagen anterior, un "true" indicando que debe actualizar el dinero de la cuadrilla ya que se está editando un gasto de un empleado, y un "false" indicando que no se ha seleccionado una nueva fecha
            gast.editarGasto(id, null, cuadrilla, lugarCompra, tipoCompra, descripcion, factura, imagen, total, totalNuevo, imageUriVieja, imageUri, true, false);
        }
        else if (tipo.equalsIgnoreCase("Admin")) { //En cambio, si "tipo" es igual (ignorando mayúsculas y minúsculas) a la palabra "Admin", que entre al else if
            //Llamamos el método "editarGasto" de la clase Gasto donde se hará el proceso de modificación de los datos del gasto; para ello le mandamos el "id", la variable global "timestamp" que guarda la fecha seleccionada, la cuadrilla y el tipoCompra extraídos de los Spinners, el resto de datos de los EditTexts, la ruta de la imagen guardada en "imagen", el total anterior guardado en la variable global "total" y el "totalNuevo" que se extrae del EditText, los URIs de la imagen cargada de Firebase Storage y de la posible nueva imagen que subirá el usuario para reemplazar la imagen anterior, un "false" indicando que no debe actualizar el dinero de la cuadrilla ya que se está editando un gasto de un administrador, y un "true" indicando que se ha seleccionado una nueva fecha
            gast.editarGasto(id, timestamp, cuadrillaNueva, lugarCompra, tipoCompra, descripcion, factura, imagen, total, totalNuevo, imageUriVieja, imageUri, false, true);
        }
    }

    public void retroceder(View view) {
        onBackPressed();
    }
}