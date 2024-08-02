package com.ingelcom.cajachica;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RegistrarEditarIngresoDeduccion extends AppCompatActivity {

    private LinearLayout llFecha, llDinero, llTransferencia;
    private TextView lblTitulo, lblFecha, lblDinero, btnSubirCambiarFoto;
    private EditText txtTransferencia, txtTotal;
    private ImageView imgFoto, btnEliminar;
    private Spinner spCuadrillas;
    private int day, month, year;
    private String nombreActivity, id, fechaHora, cuadrilla, usuario, transferencia, total;
    private Timestamp timestamp = null;
    private Uri imageUri;

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

        imgFoto = findViewById(R.id.imgFotoEvidenciaRI);

        btnEliminar = findViewById(R.id.imgEliminarFotoRI);
        btnSubirCambiarFoto = findViewById(R.id.btnSubirCambiarFotoRI);
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
        if (nombreActivity != null) { //Que entre al if si "nombreActivity" no es nulo
            switch (nombreActivity) { //El "nombreActivity" nos sirve para saber la pantalla con la que trabajaremos
                //Establecemos los elementos gráficos dependiendo de la pantalla
                case "RegistrarIngreso":
                    lblTitulo.setText("Registrar Ingreso");//Asignamos el titulo
                    //lblFecha.setText(String.format("%02d/%02d/%04d", day, month + 1, year)); //Asignamos la fecha seleccionada con el formato "00/00/0000" al TextView "lblFecha"
                    //llDinero.setVisibility(View.GONE);
                    break;

                case "RegistrarDeduccion":
                    lblTitulo.setText("Registrar Deducción"); //Asignamos el titulo

                    llTransferencia.setVisibility(View.GONE);
                    btnEliminar.setVisibility(View.GONE);
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
                    btnEliminar.setVisibility(View.GONE);
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

    }

    private void seleccionarImagen() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null & data.getData() != null) {
            imageUri = data.getData();
            imgFoto.setImageURI(imageUri);

        }
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
                            ingr.registrarIngreso(nombre, timestamp, cuadrilla, transferencia, total); //Llamamos el método "registrarIngreso" donde se hará el proceso de inserción a Firestore y le mandamos el nombre del usuario actual (el usuario que está registrando el ingreso de dinero), la fecha y hora seleccionada, los textboxes y selecciones de los spinners de esta pantalla
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
}