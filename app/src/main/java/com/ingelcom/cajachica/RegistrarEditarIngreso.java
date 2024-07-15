package com.ingelcom.cajachica;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ingelcom.cajachica.DAO.Cuadrilla;
import com.ingelcom.cajachica.DAO.FirestoreOperaciones;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;

import com.google.firebase.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RegistrarEditarIngreso extends AppCompatActivity {

    private LinearLayout llFecha;
    private TextView lblTitulo, lblFecha;
    private EditText txtTransferencia, txtTotal;
    private Spinner spCuadrillas;
    private int day, month, year;
    private String nombreActivity, fechaHoraActual;

    private FirestoreOperaciones oper = new FirestoreOperaciones();
    private Cuadrilla cuad = new Cuadrilla();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_editar_ingreso);

        inicializarElementos();
        establecerElementos();
        inicializarSpinner();
    }

    private void inicializarElementos() {
        //Obtenemos el nombre del activity que se envía desde el activity anterior, lo hacemos llamando a la función "obtenerStringExtra" de la clase "Utilidades", y le mandamos "this" para referenciar esta actividad y "Activity" como clave del putExtra
        nombreActivity = Utilidades.obtenerStringExtra(this, "ActivityREI");

        /*final Calendar c = Calendar.getInstance(); //Creamos un objeto de tipo Calendar que representa la fecha y hora actuales en el dispositivo donde se está ejecutando el código
        year = c.get(Calendar.YEAR); //Obtenemos el año actual
        month = c.get(Calendar.MONTH); //Obtenemos el mes actual
        day = c.get(Calendar.DAY_OF_MONTH); //Obtenemos el día actual*/

        llFecha = findViewById(R.id.LLFechaRI);
        lblTitulo = findViewById(R.id.lblTituloRI);
        lblFecha = findViewById(R.id.lblFechaRI);
        txtTransferencia = findViewById(R.id.txtTransferenciaRI);
        txtTotal = findViewById(R.id.txtTotalRI);
        spCuadrillas = findViewById(R.id.spCuadrillaRI);
    }

    private void establecerElementos() {
        //Que entre al if si "nombreActivity" no es nulo
        if (nombreActivity != null) {
            switch (nombreActivity) {
                case "RegistrarIngreso":
                    lblFecha.setText(String.format("%02d/%02d/%04d", day, month + 1, year)); //Asignamos la fecha seleccionada con el formato "00/00/0000" al TextView "lblFecha"
                    break;

                case "EditarIngreso":
                    lblTitulo.setText("Editar Ingreso"); //Asignamos el titulo
                    break;
            }
        }
    }

    private void inicializarSpinner() {
        //Para inicializar el spinner, llamamos al método "obtenerRegistros" de la clase "FirestoreOperaciones" a la cual le mandamos el nombre de la colección y el nombre del campo de Firestore de los cuales queremos obtener los registros. También invocamos los métodos "onCallback" y "onFailure" de la interfaz FirestoreCallback
        //CUADRILLAS
        oper.obtenerRegistrosCampo("cuadrillas", "Nombre", new FirestoreCallbacks.FirestoreCallback() {
            @Override
            public void onCallback(List<String> lista) {
                //Creamos un adapter de tipo ArrayAdapter el cual le pasamos el contexto de este Activity, la vista layout de las opciones del Spinner (R.layout.spinner_items), y la lista de valores que se recibe en "lista" al llamar a la interfaz FirestoreCallback
                ArrayAdapter<String> adapter = new ArrayAdapter<>(RegistrarEditarIngreso.this, R.layout.spinner_items, lista);
                spCuadrillas.setAdapter(adapter); //Asignamos el adapter al Spinner "spCuadrillas"
            }

            @Override
            public void onFailure(Exception e) {
                Log.w("Activity", "Error al obtener las cuadrillas.", e);
            }
        });
    }

    //Método Click del LinearLayout de Fecha, el cual al dar clic en él, se mostrará un calendario emergente para seleccionar una fecha
    /*public void seleccionarFecha(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                lblFecha.setText(String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year));

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                fechaHoraActual = sdf.format(calendar.getTime());
            }
        }, year, month, day); //Estas tres variables nos ayudan a que la fecha predeterminada en el calendario sea la fecha de hoy

        datePickerDialog.show();
    }*/

    public void confirmar(View view) {
        if (nombreActivity != null) { //Que entre al if si "nombreActivity" no es nulo
            switch (nombreActivity) { //El "nombreActivity" nos sirve para saber la pantalla con la que trabajaremos
                case "RegistrarIngreso": //Si estamos en la pantalla de "Registrar Ingreso", al dar clic en el botón "Confirmar" que realice las operaciones de este case

                    //Creamos un alertDialog que pregunte si se desea registrar el ingreso de dinero a la cuadrilla seleccionada
                    new AlertDialog.Builder(this).setTitle("REGISTRAR INGRESO").setMessage("¿Está seguro que desea registrar el ingreso de dinero a la cuadrilla seleccionada?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { //Si se selecciona la opción positiva, entrará aquí y al método "registrarIngreso()"
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                registrarIngreso(); //Llamamos el método "registrarIngreso" donde se hará el proceso de inserción a Firestore
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() { //Si se seleccionó la opción negativa, entrará aquí y solamente mostrará un mensaje en Logcat
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.d("Mensaje", "Se canceló la acción"); //Se muestra un mensaje en el Logcat indicando que se canceló la acción

                                }
                            }).show();
                    break;

                case "EditarIngreso":
                    break;
            }
        }
        //Utilidades.iniciarActivity(this, GastoIngresoRegistrado.class, false);
    }

    private void registrarIngreso() {
        //Enlazamos los EditText con las siguientes variables String
        String cuadrilla = spCuadrillas.getSelectedItem().toString();
        String transferencia = txtTransferencia.getText().toString();
        String total = txtTotal.getText().toString();

        if (!transferencia.isEmpty() && !total.isEmpty()) { //Verificamos que las dos cajas de texto no estén vacías para que entre al if
            double totalIngreso = Double.parseDouble(total); //Convertimos la variable String "total" en double y su contenido lo guardamos en "totalIngreso"
            Map<String, Object> datos = new HashMap<>(); //Creamos un HashMap para guardar los nombres de los campos y los datos a insertar

            Calendar calendar = Calendar.getInstance(); //Obtenemos una instancia de la clase "Calendar"
            //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()); //Creamos un formato para la fecha y hora. "Locale.getDefault()" especifica que el formato debe seguir las convenciones predeterminadas del sistema
            //fechaHoraActual = sdf.format(calendar.getTime()); //Obtenemos la fecha y hora actual con "calendar.getTime()" y lo convertimos al formato guardado en "sdf" y esto lo asignamos al String "fechaHoraActual"
            Date fechaHora = calendar.getTime(); //"calendar.getTime()" devuelve un objeto Date que representa la fecha y hora actual contenida en el objeto Calendar, esto lo guardamos en "fechaHora"
            Timestamp timestamp = new Timestamp(fechaHora); //Convertimos "fechaHora" en un objeto "Timestamp" para que sea compatible con Firestore

            //Insertamos los datos en el HashMap usando ".put", indicando entre comillas el nombre del campo, y después de la coma, el valor a insertar
            datos.put("Fecha", timestamp);
            datos.put("Cuadrilla", cuadrilla);
            datos.put("Transferencia", transferencia);
            datos.put("Total", totalIngreso);

            //Llamamos el método "insertarRegistros" de la clase "FirestoreOperaciones" y le mandamos el nombre de la colección, el HashMap con los datos a insertar. También invocamos los métodos "onSuccess" y "onFailure" de la interfaz FirestoreInsertCallback
            oper.insertarRegistros("ingresos", datos, new FirestoreCallbacks.FirestoreInsertCallback() {
                @Override
                public void onSuccess(String texto) {
                    Toast.makeText(RegistrarEditarIngreso.this, "INGRESO REGISTRADO EXITOSAMENTE", Toast.LENGTH_SHORT).show();
                    cuad.actualizarDineroCuadrilla(cuadrilla, totalIngreso, "Ingreso", RegistrarEditarIngreso.this); //Llamamos el método "actualizarDineroCuadrilla" de la clase "Cuadrilla" y le mandamos el nombre de la cuadrilla, el total ingresado y la palabra "Ingreso" para indicar que se hizo un Ingreso y no un Gasto
                    Utilidades.iniciarActivityConString(RegistrarEditarIngreso.this, GastoIngresoRegistrado.class, "ActivityGIR", "IngresoRegistrado"); //Redireccionamos a la clase "GastoIngresoRegistrado" y mandamos el mensaje "IngresoRegistrado" para indicar que fue un Ingreso y no un Gasto el que se registró
                    finish(); //Finalizamos el Activity actual
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(RegistrarEditarIngreso.this, "ERROR AL REGISTRAR EL INGRESO", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(this, "TODOS LOS CAMPOS DEBEN LLENARSE", Toast.LENGTH_SHORT).show();
        }
    }
}