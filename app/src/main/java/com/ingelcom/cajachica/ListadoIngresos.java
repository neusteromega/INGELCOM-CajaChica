package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.ingelcom.cajachica.Adaptadores.IngresosAdapter;
import com.ingelcom.cajachica.DAO.Ingreso;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.Modelos.IngresosItems;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ListadoIngresos extends AppCompatActivity {

    private TextView lblFecha, lblTotalIngresos;
    private String nombreCuadrilla;
    private RecyclerView rvIngresos;

    private Ingreso ingr = new Ingreso(ListadoIngresos.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_ingresos);

        inicializarElementos();
        cambioFecha();
        obtenerIngresos("");
    }

    private void inicializarElementos() {
        //Obtenemos el nombre de la cuadrilla que se envía desde el activity anterior, lo hacemos llamando a la función "obtenerStringExtra" de la clase "Utilidades", y les mandamos "this" para referenciar esta actividad y el nombre de la clave del putExtra
        nombreCuadrilla = Utilidades.obtenerStringExtra(this, "Cuadrilla");

        lblFecha = findViewById(R.id.lblFechaLI);
        lblTotalIngresos = findViewById(R.id.lblCantIngresosLI);
        rvIngresos = findViewById(R.id.rvListadoIngresos);
    }

    private void obtenerIngresos(String mes) {
        try {
            //Llamamos el método "obtenerIngresos" de la clase "Ingreso", le mandamos la cuadrilla recibida en "nombreCuadrilla" y el "mes". Con esto se podrán obtener todos los ingresos hechos por los administradores
            ingr.obtenerIngresos(nombreCuadrilla, mes, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<IngresosItems>() {
                @Override
                public void onCallback(List<IngresosItems> items) { //En esta lista "items" están todos los ingresos ya filtrados por cuadrilla
                    if (items != null) //Si "items" no es null, que entre al if
                        inicializarRecyclerView(items); //Llamamos el método "inicializarRecyclerView" de abajo y le mandamos la lista "items"
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(ListadoIngresos.this, "ERROR AL CARGAR LOS INGRESOS", Toast.LENGTH_SHORT).show();
                    Log.w("ObtenerIngresos", e);
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerIngresos", e);
        }
    }

    //Método que nos ayuda a inicializar el RecyclerView de ingresos
    private void inicializarRecyclerView(List<IngresosItems> items) {
        rvIngresos.setLayoutManager(new LinearLayoutManager(this));

        IngresosAdapter adapter = new IngresosAdapter(items); //Creamos un nuevo objeto de tipo IngresosAdapter en el cual enviamos la lista "items"
        rvIngresos.setAdapter(adapter); //Asignamos el adapter al recyclerView de Ingresos
        double totalIngresos = 0; //Variable que nos servirá para calcular el total de los ingresos que se muestren en el RecyclerView

        //Recorremos la lista "items" y cada elemento de ella se guardará en la variable temporal "item" de tipo "IngresosItems"
        for (IngresosItems item : items) {
            totalIngresos += item.getTotal(); //Obtenemos el "total" de cada elemento de la lista "items" y lo vamos sumando en la variable "totalIngresos"
        }

        lblTotalIngresos.setText("L. " + String.format("%.2f", totalIngresos)); //Asignamos el totalIngresos al TextView "lblTotalIngresos" y formateamos la variable "totalIngresos" para que se muestre con dos digitos después del punto decimal

        adapter.setOnClickListener(new View.OnClickListener() { //Usando el objeto de "adapter" llamamos al método "setOnClickListener" de la clase IngresosAdapter
            @Override
            public void onClick(View view) { //Al dar clic en una tarjeta del RecyclerView, se realizará lo siguiente
                HashMap<String,Object> datosIngreso = new HashMap<>(); //Creamos un HashMap para guardar los datos que se enviarán al siguiente Activity

                //Agregamos las claves y datos al HashMap
                datosIngreso.put("ActivityREI", "EditarIngreso");
                datosIngreso.put("ID", items.get(rvIngresos.getChildAdapterPosition(view)).getId());
                datosIngreso.put("FechaHora", items.get(rvIngresos.getChildAdapterPosition(view)).getFechaHora());
                datosIngreso.put("Cuadrilla", items.get(rvIngresos.getChildAdapterPosition(view)).getCuadrilla());
                datosIngreso.put("Transferencia", items.get(rvIngresos.getChildAdapterPosition(view)).getTransferencia());
                datosIngreso.put("Total", items.get(rvIngresos.getChildAdapterPosition(view)).getTotal());

                //Llamamos el método "iniciarActivityConDatos" de la clase Utilidades y le mandamos el contexto, el activity siguiente y el HashMap con los datos a enviar
                Utilidades.iniciarActivityConDatos(ListadoIngresos.this, RegistrarEditarIngreso.class, datosIngreso);
            }
        });
    }

    private void cambioFecha() {
        try {
            //Para detectar cuando el lblFecha cambia su valor, llamamos el método "addTextChangedListener"
            lblFecha.addTextChangedListener(new TextWatcher() {
                @Override //Antes de que el texto del lblFecha cambie
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override //Durante el texto del lblFecha está cambiando
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    obtenerIngresos(lblFecha.getText().toString()); //Llamamos el método "obtenerIngresos" de arriba y le mandamos el contenido del lblFecha
                }

                @Override //Después de que el texto del lblFecha cambie
                public void afterTextChanged(Editable editable) {

                }
            });
        }
        catch (Exception e) {
            Log.w("DetectarFecha", e);
        }
    }

    //Evento Clic del LinearLayout de Fecha, al dar clic en el mismo, se abrirá un "Popup DatePicker" en el que se podrá seleccionar un mes y año y esto servirá para filtrar los gastos
    public void mostrarMesesIngresos(View view) {
        try {
            //Creamos una instancia de la interfaz "DatePickerDialog.OnDateSetListener" y esta define el método "onDateSet" que se llama cuando el usuario selecciona una fecha en el DatePickerDialog
            DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) { //Se ejecuta cuando el usuario ha seleccionado una fecha
                    month = month + 1; //Al mes le sumamos +1 porque los meses por defecto empiezan en 0 y no en 1
                    String fecha = Utilidades.convertirMonthYearString(month, year); //Guardamos el mes y año convertidos a String llamando al método "convertirMonthYearString" con los parámetros de mes y año, y esto retorna el String
                    lblFecha.setText(fecha); //Asignamos la fecha ya convertida a String al TextView lblFecha
                }
            };

            Calendar cal = Calendar.getInstance(); //Creamos un objeto de tipo Calendar que representa la fecha y hora actuales en el dispositivo donde se está ejecutando el código
            int year = cal.get(Calendar.YEAR); //Obtenemos el año actual
            int month = cal.get(Calendar.MONTH); //Obtenemos el mes actual
            int day = cal.get(Calendar.DAY_OF_MONTH); //Obtenemos el día actual
            int style = AlertDialog.THEME_HOLO_LIGHT; //En una variable entera guardamos el estilo que tendrá la ventana emergente

            DatePickerDialog datePickerDialog = new DatePickerDialog(ListadoIngresos.this, style, dateSetListener, year, month, day); //Creamos un nuevo objeto de tipo DatePickerDialog y le mandamos como parámetros al constructor, un contexto, la variable "style" que guarda el estilo, el "dateSetListener", el año, mes y día, estos últimos para que al abrir el AlertDialog, se muestre el mes actual
            datePickerDialog.getDatePicker().findViewById(getResources().getIdentifier("day", "id", "android")).setVisibility(View.GONE); //Ocultamos el spinner de días asignando "GONE" en su visibilidad
            datePickerDialog.show(); //Mostramos el AlertDialog o Popup DatePicker de solo mes y año
        }
        catch (Exception e) {
            Log.w("ObtenerMes", e);
        }
    }

    //Método para eliminar la selección del Mes - Año
    public void eliminarMesIngresos(View view) {
        lblFecha.setText("Seleccionar Mes");
    }
}