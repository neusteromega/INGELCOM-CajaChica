package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.ingelcom.cajachica.DAO.Gasto;
import com.ingelcom.cajachica.DAO.Ingreso;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.Modelos.GastosItems;
import com.ingelcom.cajachica.Modelos.IngresosItems;

import java.util.Calendar;
import java.util.List;

public class AdmDatosCuadrilla extends AppCompatActivity {

    private TextView lblCuadrilla, lblDinero, lblFecha, lblIngresos, lblGastos;
    private String nombreCuadrilla, dineroDisponible;

    private Gasto gast = new Gasto(AdmDatosCuadrilla.this);
    private Ingreso ingr = new Ingreso(AdmDatosCuadrilla.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adm_datos_cuadrilla);

        inicializarElementos();
        establecerElementos();
        cambioFecha();
        obtenerGastos("");
        obtenerIngresos("");
    }

    private void inicializarElementos() {
        //Obtenemos el nombre de la cuadrilla y el dinero disponible de la misma que se envía desde el activity anterior, lo hacemos llamando a la función "obtenerStringExtra" de la clase "Utilidades", y les mandamos "this" para referenciar esta actividad y los nombres de las claves del putExtra
        nombreCuadrilla = Utilidades.obtenerStringExtra(this, "Cuadrilla");
        dineroDisponible = Utilidades.obtenerStringExtra(this, "DineroDisponible");

        lblCuadrilla = findViewById(R.id.lblNombreCuadrillaDC);
        lblDinero = findViewById(R.id.lblCantDineroDC);
        lblFecha = findViewById(R.id.lblFechaDC);
        lblIngresos = findViewById(R.id.lblCantIngresosDC);
        lblGastos = findViewById(R.id.lblCantGastosDC);
    }

    private void establecerElementos() {
        //Asignamos el nombre de la cuadrilla y su dinero disponible tras dar clic en la cuadrilla corresponiente en el GridView de "AdmCuadrillas" (La pantalla anterior)
        lblCuadrilla.setText(nombreCuadrilla);
        lblDinero.setText("L. " + dineroDisponible);
    }

    //Método que obtiene los gastos de la cuadrilla y permite obtenerlos por mes
    private void obtenerGastos(String mes) {
        try {
            //Llamamos el método "obtenerGastos" de la clase "Gasto", le mandamos la cuadrilla recibida en "nombreCuadrilla", el rol "Empleado" (ya que queremos ver los gastos hechos por la cuadrilla y en la cuadrilla todos los usuarios tienen rol "Empleado") y el "mes". Con esto se podrán obtener todos los gastos hechos por los empleados de la cuadrilla
            gast.obtenerGastos(nombreCuadrilla, "Empleado", mes, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<GastosItems>() {
                @Override
                public void onCallback(List<GastosItems> items) { //En esta lista "items" están todos los gastos ya filtrados por cuadrilla y rol
                    if (items != null) //Si "items" no es null, que entre al if
                        calcularTotalGastos(items); //Llamamos el método "calculaTotalGastos" y le mandamos la lista "items"
                }

                @Override
                public void onFailure(Exception e) {
                    Log.w("ObtenerGastos", e);
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerGastos", e);
        }
    }

    //Método que obtiene los ingresos de la cuadrilla y permite obtenerlos por mes
    private void obtenerIngresos(String mes) {
        try {
            //Llamamos el método "obtenerIngresos" de la clase "Ingreso", le mandamos la cuadrilla recibida en "nombreCuadrilla" y el "mes". Con esto se podrán obtener todos los ingresos hechos por los administradores
            ingr.obtenerIngresos(nombreCuadrilla, mes, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<IngresosItems>() {
                @Override
                public void onCallback(List<IngresosItems> items) { //En esta lista "items" están todos los ingresos ya filtrados por cuadrilla
                    if (items != null) //Si "items" no es null, que entre al if
                        calcularTotalIngresos(items); //Llamamos el método "calculaTotalIngresos" y le mandamos la lista "items"
                        //calcularTotales(items, "Ingreso");
                }

                @Override
                public void onFailure(Exception e) {
                    Log.w("ObtenerGastos", e);
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerIngresos", e);
        }
    }

    /*private <T> void calcularTotales(List<T> items, String tipo) {
        if (tipo.contentEquals("Ingreso")) {
            double totalIngresos = 0; //Variable que nos servirá para calcular el total de los ingresos que se muestren en el RecyclerView

            //Recorremos la lista "items" y cada elemento de ella se guardará en la variable temporal "item" de tipo "IngresosItems"
            for (IngresosItems item : items) {
                totalIngresos += item.getTotal(); //Obtenemos el "total" de cada elemento de la lista "items" y lo vamos sumando en la variable "totalIngresos"
            }

            lblIngresos.setText("L. " + String.format("%.2f", totalIngresos)); //Asignamos el totalIngresos al TextView "lblIngresos" y formateamos la variable "totalIngresos" para que se muestre con dos digitos después del punto decimal
        }
    }*/

    //Método que calcula los totales de los gastos de la cuadrilla
    private void calcularTotalGastos(List<GastosItems> items) {
        double totalGastos = 0; //Variable que nos servirá para calcular el total de los gastos que se muestren en el RecyclerView

        //Recorremos la lista "items" y cada elemento de ella se guardará en la variable temporal "item" de tipo "GastosItems"
        for (GastosItems item : items) {
            totalGastos += item.getTotal(); //Obtenemos el "total" de cada elemento de la lista "items" y lo vamos sumando en la variable "totalGastos"
        }

        lblGastos.setText("L. " + String.format("%.2f", totalGastos)); //Asignamos el totalGastos al TextView "lblGastos" y formateamos la variable "totalGastos" para que se muestre con dos digitos después del punto decimal
    }

    //Método que calcula los totales de los ingresos de la cuadrilla
    private void calcularTotalIngresos(List<IngresosItems> items) {
        double totalIngresos = 0; //Variable que nos servirá para calcular el total de los ingresos que se muestren en el RecyclerView

        //Recorremos la lista "items" y cada elemento de ella se guardará en la variable temporal "item" de tipo "IngresosItems"
        for (IngresosItems item : items) {
            totalIngresos += item.getTotal(); //Obtenemos el "total" de cada elemento de la lista "items" y lo vamos sumando en la variable "totalIngresos"
        }

        lblIngresos.setText("L. " + String.format("%.2f", totalIngresos)); //Asignamos el totalIngresos al TextView "lblIngresos" y formateamos la variable "totalIngresos" para que se muestre con dos digitos después del punto decimal
    }

    //Método que detecta cuando el lblFecha cambia su valor
    private void cambioFecha() {
        try {
            //Para detectar cuando el lblFecha cambia su valor, llamamos el método "addTextChangedListener"
            lblFecha.addTextChangedListener(new TextWatcher() {
                @Override //Antes de que el texto del lblFecha cambie
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override //Durante el texto del lblFecha está cambiando
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    //Llamamos a los métodos "obtenerGastos" y "obtenerIngresos" de arriba y les mandamos el contenido del lblFecha
                    obtenerGastos(lblFecha.getText().toString());
                    obtenerIngresos(lblFecha.getText().toString());
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

    //Evento Clic del LinearLayout de Fecha, al dar clic en el mismo, se abrirá un "Popup DatePicker" en el que se podrá seleccionar un mes y año y esto servirá para filtrar los totales de gastos e ingresos de la cuadrilla
    public void mostrarMesesTotales(View view) {
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

            DatePickerDialog datePickerDialog = new DatePickerDialog(AdmDatosCuadrilla.this, style, dateSetListener, year, month, day); //Creamos un nuevo objeto de tipo DatePickerDialog y le mandamos como parámetros al constructor, un contexto, la variable "style" que guarda el estilo, el "dateSetListener", el año, mes y día, estos últimos para que al abrir el AlertDialog, se muestre el mes actual
            datePickerDialog.getDatePicker().findViewById(getResources().getIdentifier("day", "id", "android")).setVisibility(View.GONE); //Ocultamos el spinner de días asignando "GONE" en su visibilidad
            datePickerDialog.show(); //Mostramos el AlertDialog o Popup DatePicker de solo mes y año
        }
        catch (Exception e) {
            Log.w("ObtenerMes", e);
        }
    }

    //Método para eliminar la selección del Mes - Año
    public void eliminarMesTotales(View view) {
        lblFecha.setText("Seleccionar Mes");
    }

    public void verIngresos(View view) {
        Utilidades.iniciarActivityConString(AdmDatosCuadrilla.this, ListadoIngresosDeducciones.class, "Cuadrilla", nombreCuadrilla, false);
    }

    public void verGastos(View view) {
        Utilidades.iniciarActivityConString(AdmDatosCuadrilla.this, ListadoGastos.class, "Cuadrilla", nombreCuadrilla, false);
    }
}