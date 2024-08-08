package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.ingelcom.cajachica.Adaptadores.VPGastosAdapter;
import com.ingelcom.cajachica.DAO.Gasto;
import com.ingelcom.cajachica.Fragmentos.FragGastosCuadrilla;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.SharedViewGastosModel;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.Modelos.GastosItems;

import java.util.Calendar;
import java.util.List;

public class ListadoGastos extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private TextView lblTitulo, lblFecha, lblLineaCuadrilla, lblLineaSupervisores;
    private String nombreActivity, nombreCuadrilla;
    private ViewPager2 vpGastos;

    //Instancia de la clase "SharedViewGastosModel" que nos ayuda a compartir datos con diferentes componentes de la interfaz de usuario, como ser fragmentos y actividades y que estos datos sobreviven a cambios de configuración como las rotaciones de pantalla
    private SharedViewGastosModel svmGastos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_gastos);

        inicializarElementos();
        establecerElementos();
        cambioFecha();
        cambioViewPager();
    }

    private void inicializarElementos() {
        //Obtenemos el nombre del Activity y de la cuadrilla que se envía desde el activity anterior, lo hacemos llamando a la función "obtenerStringExtra" de la clase "Utilidades", y les mandamos "this" para referenciar esta actividad y el nombre de la clave del putExtra
        nombreActivity = Utilidades.obtenerStringExtra(this, "ActivityLG");
        nombreCuadrilla = Utilidades.obtenerStringExtra(this, "Cuadrilla");

        lblTitulo = findViewById(R.id.lblTituloLG);
        lblFecha = findViewById(R.id.lblFechaLG);
        lblLineaCuadrilla = findViewById(R.id.lblCuadrillaLineaLG);
        lblLineaSupervisores = findViewById(R.id.lblSupervisoresLineaLG);
        vpGastos = findViewById(R.id.vpListadoGastos); //Relacionamos la variable "vpGastos" con el ViewPager

        svmGastos = new ViewModelProvider(this).get(SharedViewGastosModel.class); //Obtenemos el ViewModel compartido, haciendo referencia a la clase "SharedViewGastosModel"
    }

    private void establecerElementos() {
        switch (nombreActivity) {
            case "ListadoGastosEmpleado":
                lblTitulo.setText("Listado de Gastos");
                break;

            case "ListadoGastosAdmin":
                lblTitulo.setText(nombreCuadrilla); //Establecemos el nombre de la cuadrilla en el titulo
                break;

            case "ListadoGastosTodos":
                lblTitulo.setText("Todos los Gastos");
                break;
        }

        lblLineaSupervisores.setVisibility(View.INVISIBLE); //Ocultamos la linea bajo la palabra "Supervisores" al iniciar el Activity

        VPGastosAdapter vpAdapter = new VPGastosAdapter(this, nombreCuadrilla, nombreActivity); //Creamos un objeto de "VPGastosAdapter" y le mandamos el contexto "this" de este Activity, el nombre de la cuadrilla y el nombre del Activity
        vpGastos.setAdapter(vpAdapter); //Asignamos el adaptador al vpGastos
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
                    svmGastos.setFecha(charSequence.toString()); //Llamamos el método "setFecha" de la clase "SharedViewGastosModel" y le mandamos el "charSequence" que es el texto del TextView "lblFecha" y lo convertimos a String
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
    public void mostrarMesesGastos(View view) {
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

            DatePickerDialog datePickerDialog = new DatePickerDialog(ListadoGastos.this, style, dateSetListener, year, month, day); //Creamos un nuevo objeto de tipo DatePickerDialog y le mandamos como parámetros al constructor, un contexto, la variable "style" que guarda el estilo, el "dateSetListener", el año, mes y día, estos últimos para que al abrir el AlertDialog, se muestre el mes actual
            datePickerDialog.getDatePicker().findViewById(getResources().getIdentifier("day", "id", "android")).setVisibility(View.GONE); //Ocultamos el spinner de días asignando "GONE" en su visibilidad
            datePickerDialog.show(); //Mostramos el AlertDialog o Popup DatePicker de solo mes y año
        }
        catch (Exception e) {
            Log.w("ObtenerMes", e);
        }
    }

    //Método para eliminar la selección del Mes - Año
    public void eliminarMesGastos(View view) {
        lblFecha.setText("Seleccionar Mes");
    }

    public void exportar(View view) {
        PopupMenu popup = new PopupMenu(this, view); //Objeto de tipo "PopupMenu"
        popup.setOnMenuItemClickListener(this); //Indicamos que asigne el evento "OnMenuItemClick" para que haga algo cada vez que se dé click a una opción del menú
        popup.inflate(R.menu.popupmenu_opcionesexportar); //Inflamos la vista del menú indicando la ruta de dicha vista gráfica

        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menuExportarExcel:
                /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((ListadoGastos) this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                }*/

                svmGastos.setExportar("EXCEL");
                return true;

            case R.id.menuExportarPDF:
                //Toast.makeText(this, "PDF", Toast.LENGTH_SHORT).show();
                svmGastos.setExportar("PDF");
                return true;

            default:
                return false;
        }
    }

    public void retroceder(View view) {
        onBackPressed();
    }
}