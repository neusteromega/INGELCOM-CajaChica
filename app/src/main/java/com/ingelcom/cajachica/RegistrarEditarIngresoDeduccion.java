package com.ingelcom.cajachica;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.ingelcom.cajachica.DAO.Cuadrilla;
import com.ingelcom.cajachica.DAO.Deduccion;
import com.ingelcom.cajachica.DAO.FirestoreOperaciones;
import com.ingelcom.cajachica.DAO.Ingreso;
import com.ingelcom.cajachica.DAO.Usuario;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RegistrarEditarIngresoDeduccion extends AppCompatActivity {

    private LinearLayout llFecha, llDinero, llTransferencia;
    private TextView lblTitulo, lblFecha, lblDinero, btnSubirCambiarFoto;
    private EditText txtTransferencia, txtTotal;
    private ImageView imgFoto, btnEliminarFoto;
    private Spinner spCuadrillas;
    //private SwipeRefreshLayout swlRecargar;
    private int day, month, year;
    private String nombreActivity, id, fechaHora, cuadrilla, usuario, transferencia, total;
    private Timestamp timestamp = null;
    private Uri imageUri = null;

    private FirestoreOperaciones oper = new FirestoreOperaciones();
    private Cuadrilla cuad = new Cuadrilla(RegistrarEditarIngresoDeduccion.this);
    private Ingreso ingr = new Ingreso(RegistrarEditarIngresoDeduccion.this);
    private Deduccion deduc = new Deduccion(RegistrarEditarIngresoDeduccion.this);
    private Usuario usu = new Usuario(RegistrarEditarIngresoDeduccion.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_editar_ingreso_deduccion);

        inicializarElementos();
        obtenerDatos();
        inicializarSpinner();
        establecerElementos();
        cambioCuadrilla();
    }

    private void inicializarElementos() {
        final Calendar c = Calendar.getInstance(); //Creamos un objeto de tipo Calendar que representa la fecha y hora actuales en el dispositivo donde se está ejecutando el código
        year = c.get(Calendar.YEAR); //Obtenemos el año actual
        month = c.get(Calendar.MONTH); //Obtenemos el mes actual
        day = c.get(Calendar.DAY_OF_MONTH); //Obtenemos el día actual

        llFecha = findViewById(R.id.LLFechaRI);
        llDinero = findViewById(R.id.LLDineroRI);
        llTransferencia = findViewById(R.id.LLTransferenciaRI);
        lblTitulo = findViewById(R.id.lblTituloRI);
        lblFecha = findViewById(R.id.lblFechaRI);
        lblDinero = findViewById(R.id.lblCantDineroRI);
        txtTransferencia = findViewById(R.id.txtTransferenciaRI);
        txtTotal = findViewById(R.id.txtTotalRI);
        spCuadrillas = findViewById(R.id.spCuadrillaRI);
        //swlRecargar = findViewById(R.id.swipeRefreshLayoutRI);

        imgFoto = findViewById(R.id.imgFotoEvidenciaRI);

        btnEliminarFoto = findViewById(R.id.imgEliminarFotoRI);
        btnSubirCambiarFoto = findViewById(R.id.btnSubirCambiarFotoRI);

        //swlRecargar.setOnRefreshListener(this); //Llamada al método "onRefresh"
    }

    private void obtenerDatos() {
        //Obtenemos el nombre del activity que se envía desde el activity anterior, lo hacemos llamando a la función "obtenerStringExtra" de la clase "Utilidades", y le mandamos "this" para referenciar esta actividad y "Activity" como clave del putExtra
        nombreActivity = Utilidades.obtenerStringExtra(this, "ActivityREID");

        switch (nombreActivity) {
            case "EditarIngreso":
                id = Utilidades.obtenerStringExtra(this, "ID");
                fechaHora = Utilidades.obtenerStringExtra(this, "FechaHora");
                cuadrilla = Utilidades.obtenerStringExtra(this, "Cuadrilla");
                usuario = Utilidades.obtenerStringExtra(this, "Usuario");
                transferencia = Utilidades.obtenerStringExtra(this, "Transferencia");
                total = Utilidades.obtenerStringExtra(this, "Total");
                break;

            case "EditarDeduccion":
                id = Utilidades.obtenerStringExtra(this, "ID");
                fechaHora = Utilidades.obtenerStringExtra(this, "FechaHora");
                cuadrilla = Utilidades.obtenerStringExtra(this, "Cuadrilla");
                usuario = Utilidades.obtenerStringExtra(this, "Usuario");
                total = Utilidades.obtenerStringExtra(this, "Total");
                break;
        }
    }

    private void inicializarSpinner() {
        try {
            //Para inicializar el spinner, llamamos al método "obtenerRegistros" de la clase "FirestoreOperaciones" a la cual le mandamos el nombre de la colección y el nombre del campo de Firestore de los cuales queremos obtener los registros. También invocamos los métodos "onCallback" y "onFailure" de la interfaz FirestoreCallback
            //CUADRILLAS
            oper.obtenerRegistrosCampo("cuadrillas", "Nombre", new FirestoreCallbacks.FirestoreListCallback() {
                @Override
                public void onCallback(List<String> lista) {
                    //Creamos un adapter de tipo ArrayAdapter el cual le pasamos el contexto de este Activity, la vista layout de las opciones del Spinner (R.layout.spinner_items), y la lista de valores que se recibe en "lista" al llamar a la interfaz FirestoreCallback
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(RegistrarEditarIngresoDeduccion.this, R.layout.spinner_items, lista);
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
            Log.w("InicializarCuadrillas", e);
        }
    }

    private void establecerElementos() {
        //swlRecargar.setColorSchemeResources(R.color.clr_fuente_primario); //Color del SwipeRefreshLayout

        if (nombreActivity != null) { //Que entre al if si "nombreActivity" no es nulo
            switch (nombreActivity) { //El "nombreActivity" nos sirve para saber la pantalla con la que trabajaremos
                //Establecemos los elementos gráficos dependiendo de la pantalla
                case "RegistrarIngreso":
                    lblTitulo.setText("Registrar Ingreso");//Asignamos el titulo
                    break;

                case "RegistrarDeduccion":
                    lblTitulo.setText("Registrar Deducción"); //Asignamos el titulo

                    llTransferencia.setVisibility(View.GONE);
                    btnEliminarFoto.setVisibility(View.GONE);
                    btnSubirCambiarFoto.setVisibility(View.GONE);
                    imgFoto.setVisibility(View.GONE);
                    break;

                case "EditarIngreso":
                    try {
                        lblTitulo.setText("Editar Ingreso"); //Asignamos el titulo
                        lblFecha.setText(fechaHora);
                        txtTransferencia.setText(transferencia);
                        txtTotal.setText(total);

                        //Como la fechaHora se obtiene en formato String, usamos el método utilitario "convertirFechaHoraATimestamp" para convertirlo a Timestamp y el resultado lo guardamos en la variable global "timestamp"
                        timestamp = Utilidades.convertirFechaHoraATimestamp(fechaHora);
                    }
                    catch (Exception e) {
                        Log.w("ObtenerIngreso", e);
                    }
                    break;

                case "EditarDeduccion":
                    lblTitulo.setText("Editar Deducción"); //Asignamos el titulo
                    lblFecha.setText(fechaHora);
                    txtTotal.setText(total);

                    llTransferencia.setVisibility(View.GONE);
                    btnEliminarFoto.setVisibility(View.GONE);
                    btnSubirCambiarFoto.setVisibility(View.GONE);
                    imgFoto.setVisibility(View.GONE);

                    //Como la fechaHora se obtiene en formato String, usamos el método utilitario "convertirFechaHoraATimestamp" para convertirlo a Timestamp y el resultado lo guardamos en la variable global "timestamp"
                    timestamp = Utilidades.convertirFechaHoraATimestamp(fechaHora);
                    break;
            }
        }
    }

    //Método que permite obtener el dinero de la cuadrilla guardada en la variable global "cuadrilla"
    private void obtenerDineroCuadrilla() {
        try {
            //Llamamos el método "obtenerUnaCuadrilla" de la clase "Cuadrilla", le mandamos la cuadrilla y creamos una invocación a la interfaz "FirestoreDocumentCallback"
            cuad.obtenerUnaCuadrilla(cuadrilla, new FirestoreCallbacks.FirestoreDocumentCallback() {
                @Override
                public void onCallback(Map<String, Object> documento) {
                    if (documento != null) { //Si "documento" no es nulo, quiere decir que encontró la cuadrilla en Firestore
                        double dinero = Utilidades.convertirObjectADouble(documento.get("Dinero")); //Obtenemos el dinero de la cuadrilla actual en Firestore, y lo convertimos a double llamando el método utilitario "convertirObjectADouble"
                        lblDinero.setText("L. " + String.format("%.2f", dinero)); //Asignamos el dinero extraído de Firestore de la cuadrilla al "lblDinero" con un formato para que tenga dos decimales después del punto y una "L. " al inicio
                    }
                }
                @Override
                public void onFailure(Exception e) {
                    Log.w("ObtenerCuadrilla", "Error al obtener la cuadrilla", e);
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerCuadrilla", e);
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(RegistrarEditarIngresoDeduccion.this, new TimePickerDialog.OnTimeSetListener() {
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
        /*ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Nueva Imagen");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Desde la Cámara");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 101);*/

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //Creamos un nuevo intent con la acción de "Capturar Imagen" (Image Capture) de "MediaStore"

        //Verificamos si el intent de la cámara puede manejar la captura de la imagen
        if (intent.resolveActivity(getPackageManager()) != null) {
            //Creamos un ContentValues para almacenar los metadatos de la imagen
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Nueva Imagen");
            values.put(MediaStore.Images.Media.DESCRIPTION, "Desde la cámara");

            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values); //Insertamos la imagen en el MediaStore y obtenemos el URI

            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); //Pasamos el URI al intent de la cámara
            startActivityForResult(intent, 101); //Iniciamos la actividad de la cámara
        }
    }

    private void seleccionarImagen() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT); //Asignamos que la acción del Intent será "Obtener Contenido" (Get Content)
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == 100 && data != null & data.getData() != null) { //Si el "requestCode" es 100, significa que se está seleccionando una imagen de la galería
                imageUri = data.getData(); //Obtenemos el URI de la imagen seleccionada y lo guardamos en la variable "imageUri" de tipo URI

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

    //Método Click que al dar clic en la imagen cargada, nos manda al Activity "ImagenCompleta" donde también envía el URI de la imagen cargada para mostrarla en pantalla completa
    public void mostrarImagenCompleta(View view) {
        HashMap<String, Object> datosImagen = new HashMap<>();

        /*Intent intent = new Intent(this, ImagenCompleta.class);
        intent.putExtra("imageUri", imageUri); //Enviamos el URI de la imagen
        startActivity(intent); //Iniciamos el activity*/

        datosImagen.put("imageUri", imageUri); //Enviamos el URI de la imagen
        datosImagen.put("tipoImagen", ""); //Mandamos el tipoImagen vacío ya que no queremos que la imagen se pueda descargar ya que aún no ha sido subida a Firebase Storage

        Utilidades.iniciarActivityConDatos(RegistrarEditarIngresoDeduccion.this, ImagenCompleta.class, datosImagen);
    }

    public void eliminarFoto(View view) {
        //Creamos un alertDialog que pregunte si se desea eliminar la imagen seleccionada
        new AlertDialog.Builder(this).setTitle("ELIMINAR IMAGEN").setMessage("¿Está seguro que desea eliminar la imagen?")
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { //Si se selecciona la opción positiva, entrará aquí
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    imageUri = null; //Establecemos el "imageUri" en null

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

    //Evento Clic del botón "Confirmar"
    public void confirmar(View view) {
        if (nombreActivity != null) { //Que entre al if si "nombreActivity" no es nulo
            switch (nombreActivity) { //El "nombreActivity" nos sirve para saber la pantalla con la que trabajaremos
                case "RegistrarIngreso": //Si estamos en la pantalla de "Registrar Ingreso", al dar clic en el botón "Confirmar" que realice las operaciones de este case

                    //Creamos un alertDialog que pregunte si se desea registrar el ingreso de dinero a la cuadrilla seleccionada
                    new AlertDialog.Builder(this).setTitle("REGISTRAR INGRESO").setMessage("¿Está seguro que desea registrar el ingreso de dinero a la cuadrilla seleccionada?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { //Si se selecciona la opción positiva, entrará aquí y al método "agregarIngresoDeduccion()"
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                agregarIngresoDeduccion("Ingreso"); //Llamamos el método "agregarIngresoDeduccion" de abajo para que se complete la inserción de los datos
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() { //Si se seleccionó la opción negativa, entrará aquí y solamente mostrará un mensaje en el Logcat
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.d("Mensaje", "Se canceló la acción"); //Se muestra un mensaje en el Logcat indicando que se canceló la acción
                                }
                            }).show();
                    break;

                case "RegistrarDeduccion":

                    //Creamos un alertDialog que pregunte si se desea registrar la deducción por planilla a la cuadrilla seleccionada
                    new AlertDialog.Builder(this).setTitle("REGISTRAR DEDUCCIÓN").setMessage("¿Está seguro que desea registrar la deducción por planilla a la cuadrilla seleccionada?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { //Si se selecciona la opción positiva, entrará aquí y al método "agregarIngresoDeduccion()"
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                agregarIngresoDeduccion("Deduccion"); //Llamamos el método "agregarIngresoDeduccion" de abajo para que se complete la inserción de los datos
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() { //Si se seleccionó la opción negativa, entrará aquí y solamente mostrará un mensaje en el Logcat
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.d("Mensaje", "Se canceló la acción"); //Se muestra un mensaje en el Logcat indicando que se canceló la acción
                                }
                            }).show();
                    break;

                case "EditarIngreso":

                    //Creamos un alertDialog que pregunte si se desea editar el ingreso de dinero a la cuadrilla seleccionada
                    new AlertDialog.Builder(this).setTitle("EDITAR INGRESO").setMessage("¿Está seguro que desea modificar los datos del ingreso?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { //Si se selecciona la opción positiva, entrará aquí y al método "editarIngresoDeduccion()"
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                editarIngresoDeduccion("Ingreso"); //Llamamos el método "editarIngresoDeduccion" de abajo para que se complete la modificación de los datos, y le mandamos la palabra "Ingreso" para que sepa que modificará un ingreso
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() { //Si se seleccionó la opción negativa, entrará aquí y solamente mostrará un mensaje en el Logcat
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.d("Mensaje", "Se canceló la acción"); //Se muestra un mensaje en el Logcat indicando que se canceló la acción
                                }
                            }).show();
                    break;

                case "EditarDeduccion":

                    //Creamos un alertDialog que pregunte si se desea editar la deducción por planilla a la cuadrilla seleccionada
                    new AlertDialog.Builder(this).setTitle("EDITAR DEDUCCIÓN").setMessage("¿Está seguro que desea modificar los datos de la deducción por planilla?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { //Si se selecciona la opción positiva, entrará aquí y al método "editarIngresoDeduccion()"
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                editarIngresoDeduccion("Deduccion"); //Llamamos el método "editarIngresoDeduccion" de abajo para que se complete la modificación de los datos, y le mandamos la palabra "Deduccion" para que sepa que modificará una deducción por planilla
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

    //Método que permite agregar un nuevo ingreso de dinero y/o una nueva deducción por planilla y almacenar los datos del mismo en Firestore
    private void agregarIngresoDeduccion(String tipo) {
        //Enlazamos los EditText y Spinners con las siguientes variables String
        String cuadrilla = spCuadrillas.getSelectedItem().toString();
        String transferencia = txtTransferencia.getText().toString();
        String total = txtTotal.getText().toString();

        try {
            //Llamamos el método "obtenerUsuarioActual" de la clase "Usuario" y creamos una invocación a la interfaz "FirestoreDocumentCallback"
            usu.obtenerUsuarioActual(new FirestoreCallbacks.FirestoreDocumentCallback() {
                @Override
                public void onCallback(Map<String, Object> documento) { //Los datos del usuario están guardados en el HashMap "documento"
                    if (documento != null) { //Si "documento" no es nulo, quiere decir que encontró el usuario mediante el correo
                        String nombre = (String) documento.get("Nombre"); //Obtenemos el nombre del usuario actual

                        if (tipo.equalsIgnoreCase("Ingreso")) //Si "tipo" es "Ingreso" que registre el ingreso en Firestore
                            ingr.registrarIngreso(nombre, timestamp, cuadrilla, transferencia, total, imageUri); //Llamamos el método "registrarIngreso" donde se hará el proceso de inserción a Firestore y le mandamos el nombre del usuario actual (el usuario que está registrando el ingreso de dinero), la fecha y hora seleccionada, los textboxes y selecciones de los spinners de esta pantalla
                        else if (tipo.equalsIgnoreCase("Deduccion")) //En cambio, si "tipo" es "Deduccion" que registra la deducción en Firestore
                            deduc.registrarDeduccion(nombre, timestamp, cuadrilla, total); //Llamamos el método "registrarDeduccion" donde se hará el proceso de inserción a Firestore y le mandamos el nombre del usuario actual (el usuario que está registrando la deducción por planilla), la fecha y hora seleccionada, los textboxes y selecciones de los spinners de esta pantalla
                    }
                    else { //Si "documento" es nulo, no se encontró el usuario en la colección, y entrará en este else
                        Log.w("ObtenerUsuario", "Usuario no encontrado");
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.w("ObtenerUsuario", "Error al obtener el usuario", e);
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerUsuario", e);
        }
    }

    private void editarIngresoDeduccion(String tipo) {
        //Enlazamos los EditText y Spinners con las siguientes variables String
        String cuadrilla = spCuadrillas.getSelectedItem().toString();
        String transferencia = txtTransferencia.getText().toString();
        String totalNuevo = txtTotal.getText().toString();

        if (tipo.equalsIgnoreCase("Ingreso")) //Si "tipo" es "Ingreso" que modifique el ingreso en Firestore
            ingr.editarIngreso(id, timestamp, cuadrilla, transferencia, total, totalNuevo); //Llamamos el método "editarIngreso" de la clase Ingreso donde se hará el proceso de modificación de los datos del ingreso, para ello le mandamos el id, la fecha y hora guardada en "timestamp", la cuadrilla, el número de transferencia, el total anterior guardado en la variable global "total" y el total nuevo que se extrae del EditText
        else if (tipo.equalsIgnoreCase("Deduccion")) //Si "tipo" es "Deduccion" que modifique la deducción en Firestore
            deduc.editarDeduccion(id, timestamp, cuadrilla, total, totalNuevo); //Llamamos el método "editarDeduccion" de la clase Deduccion donde se hará el proceso de modificación de los datos de la deducción, para ello le mandamos el id, la fecha y hora guardada en "timestamp", la cuadrilla, el total anterior guardado en la variable global "total" y el total nuevo que se extrae del EditText
    }

    private void cambioCuadrilla() {
        try {
            //Evento que detecta la selección hecha en el "spCuadrillas"
            spCuadrillas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    cuadrilla = adapterView.getItemAtPosition(position).toString(); //Obtenemos la selección hecha en el Spinner y la guardamos en la variable global "cuadrilla"
                    obtenerDineroCuadrilla(); //Llamamos el método "obtenerDineroCuadrilla" que obtiene el dinero de la cuadrilla seleccionada en el Spinner
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
        catch (Exception e) {
            Log.w("DetectarCuadrilla", e);
        }
    }

    /*@Override
    public void onRefresh() { //Método que detecta cuando se recarga la pantalla con SwipeRefreshLayout
        //Creamos una nueva instancia de "Handler", que está vinculada al Looper principal (el hilo principal de la aplicación). Esto asegura que cualquier operación realizada dentro de este Handler se ejecute en el hilo principal
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { //El "Handler" utiliza el método "postDelayed" para ejecutar el "Runnable" que contiene las acciones a realizar después de un retraso especificado (en este caso, 1500 milisegundos, es decir, 1.5 segundos)
            @Override
            public void run() {
                obtenerDineroCuadrilla();
                swlRecargar.setRefreshing(false); //Llamamos a este método para detener la animación de refresco
            }
        }, 1500);
    }*/

    //Método que permite retroceder a la pantalla anterior
    public void retroceder(View view) {
        onBackPressed();
    }
}