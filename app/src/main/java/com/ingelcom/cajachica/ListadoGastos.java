package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.ingelcom.cajachica.Adaptadores.VPGastosAdapter;
import com.ingelcom.cajachica.DAO.Gasto;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.Modelos.GastosItems;

import java.util.Calendar;
import java.util.List;

public class ListadoGastos extends AppCompatActivity {

    private TextView lblFecha, lblLineaCuadrilla, lblLineaSupervisores;
    public ViewPager2 vpGastos;
    private Gasto gast = new Gasto(ListadoGastos.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_gastos);

        lblFecha = findViewById(R.id.lblFechaLG);
        lblLineaCuadrilla = findViewById(R.id.lblCuadrillaLineaLG);
        lblLineaSupervisores = findViewById(R.id.lblSupervisoresLineaLG);
        vpGastos = findViewById(R.id.vpListadoGastos); //Relacionamos la variable "vpGastos" con el ViewPager

        establecerElementos();
        cambioViewPager();

        /*gast.obtenerGastos(new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<GastosItems>() {
            @Override
            public void onCallback(List<GastosItems> items) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        });*/
    }

    private void establecerElementos() {
        asignarMes(); //Llamamos el método "asignarMes" para que asigne el mes y el año actual al TextView lblFecha
        lblLineaSupervisores.setVisibility(View.INVISIBLE); //Ocultamos la linea bajo la palabra "Supervisores" al iniciar el Activity

        VPGastosAdapter vpAdapter = new VPGastosAdapter(this); //Creamos un objeto de "VPGastosAdapter" y le mandamos el contexto "this" de este Activity
        vpGastos.setAdapter(vpAdapter); //Asignamos el adaptador al vpGastos
    }

    //Método que permite establecer las lineas bajo las palabras "Cuadrilla" y "Supervisores" cuando se arrastren los fragments del ViewPager
    private void cambioViewPager() {
        //Evento que detecta cuando el ViewPager cambia su posición o se está visualizando otro fragment en él
        vpGastos.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0: //Si la posición es 0, se está visualizando el listado de gastos de cuadrilla
                        lblLineaCuadrilla.setVisibility(View.VISIBLE);
                        lblLineaSupervisores.setVisibility(View.INVISIBLE);
                        break;
                    case 1: //Si la posición es 1, se está visualizando el listado de gastos de supervisores
                        lblLineaCuadrilla.setVisibility(View.INVISIBLE);
                        lblLineaSupervisores.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }

    //Método que nos ayuda a establecer desde el inicio, el mes y el año actual al TextView lblFecha
    private void asignarMes() {
        Calendar cal = Calendar.getInstance(); //Creamos un objeto de tipo Calendar que representa la fecha y hora actuales en el dispositivo donde se está ejecutando el código
        int year = cal.get(Calendar.YEAR); //Obtenemos el año actual
        int month = cal.get(Calendar.MONTH); //Obtenemos el mes actual
        String fecha = Utilidades.convertirMonthYearString(month + 1, year); //Guardamos el mes y año convertidos a String llamando al método "convertirMonthYearString" con los parámetros de mes y año, y esto retorna el String
        lblFecha.setText(fecha); //Asignamos la fecha ya convertida a String al TextView lblFecha
    }

    //Evento Clic del LinearLayout "LLCuadrillaLG"
    public void gastosCuadrilla(View view) {
        vpGastos.setCurrentItem(0); //Si damos clic en la palabra "Cuadrilla", que se muestre el fragment de la posición 0 en el ViewPager (El "FragGastosCuadrilla()")
        //Ocultamos y mostramos las lineas bajo las palabras "Cuadrilla" y "Supervisores" dependiendo el fragment que se esté mostrando
        lblLineaCuadrilla.setVisibility(View.VISIBLE);
        lblLineaSupervisores.setVisibility(View.INVISIBLE);
    }

    //Evento Clic del LinearLayout "LLSupervisoresLG"
    public void gastosSupervisores(View view) {
        vpGastos.setCurrentItem(1); //Si damos clic en la palabra "Supervisores", que se muestre el fragment de la posición 1 en el ViewPager (El "FragGastosSupervisores()")
        //Ocultamos y mostramos las lineas bajo las palabras "Cuadrilla" y "Supervisores" dependiendo el fragment que se esté mostrando
        lblLineaSupervisores.setVisibility(View.VISIBLE);
        lblLineaCuadrilla.setVisibility(View.INVISIBLE);
    }

    //Evento Clic del LinearLayout de Fecha, al dar clic en el mismo, se abrirá un "Popup DatePicker" en el que se podrá seleccionar un mes y año y esto servirá para filtrar los gastos
    public void mostrarMeses(View view) {
        try {
            DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
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

            DatePickerDialog datePickerDialog = new DatePickerDialog(ListadoGastos.this, style, dateSetListener, year, month, day); //Creamos un nuevo objeto de tipo DatePickerDialog y le mandamos como parámetros al constructor, un contexto, la variable "style" que guarda el estilo, el "dateSetListener", el año, mes y día, estos últimos para que al abrir el AlertDialog, se muestre el mes actual
            datePickerDialog.getDatePicker().findViewById(getResources().getIdentifier("day", "id", "android")).setVisibility(View.GONE); //Ocultamos el spinner de días asignando "GONE" en su visibilidad
            datePickerDialog.show(); //Mostramos el AlertDialog o Popup DatePicker de solo mes y año
        }
        catch (Exception e) {
            Log.w("ObtenerMes", e);
        }
    }

    //Eliminar cuando se esté programando
    public void detalleGasto(View view) {
        Utilidades.iniciarActivity(this, DetalleGasto.class, false);
    }
}