package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
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

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.ingelcom.cajachica.DAO.Gasto;
import com.ingelcom.cajachica.DAO.Ingreso;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.Modelos.EstadisticasItems;
import com.ingelcom.cajachica.Modelos.GastosItems;
import com.ingelcom.cajachica.Modelos.IngresosItems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class EstadisticasGastosIngresos extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private TextView lblTitulo, btnMes, btnAnio, lblFecha, lblTotal, lblNoDatos;
    private ImageView imgGrafico;
    private HorizontalBarChart graficoBarras;
    private PieChart graficoAnillo;
    private LineChart graficoLineas;
    private String fechaSeleccionada = "", tipoGrafico = "Barras", tipoDatos, tipoFecha = "Mes";

    private Gasto gast = new Gasto(EstadisticasGastosIngresos.this);
    private Ingreso ingr = new Ingreso(EstadisticasGastosIngresos.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas_gastosingresos);

        lblTitulo = findViewById(R.id.lblTituloEstGI);
        btnMes = findViewById(R.id.lblMesEstGI);
        btnAnio = findViewById(R.id.lblAnioEstGI);
        lblFecha = findViewById(R.id.lblFechaEstGI);
        lblTotal = findViewById(R.id.lblTotalEstGI);
        lblNoDatos = findViewById(R.id.lblNoDatosEstGI);
        imgGrafico = findViewById(R.id.imgTipoGraficoEstGI);
        graficoBarras = findViewById(R.id.graficoBarrasEstGI);
        graficoAnillo = findViewById(R.id.graficoPastelEstGI);
        graficoLineas = findViewById(R.id.graficoLineasEstGI);
        tipoDatos = Utilidades.obtenerStringExtra(this, "ActivityEGI");

        establecerElementos();
        obtenerDatos("", "Barras");
        cambioFecha();
    }

    private void establecerElementos() {
        switch (tipoDatos) {
            case "Gastos":
                lblTitulo.setText("Gastos");
                lblTotal.setTextColor(getColor(R.color.clr_fuente_gastos));
                break;

            case "Ingresos":
                lblTitulo.setText("Ingresos");
                lblTotal.setTextColor(getColor(R.color.clr_fuente_ingresos));
                break;
        }
    }

    private void obtenerDatos(String fecha, String grafico) {
        try {
            if (tipoDatos.equalsIgnoreCase("Gastos")) {
                gast.obtenerGastos("", "Empleado", fecha, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<GastosItems>() {
                    @Override
                    public void onCallback(List<GastosItems> items) {
                        if (items != null && !items.isEmpty()) {//Si "items" no es null, que entre al if
                            //items = Utilidades.ordenarListaPorAlfabetico(items, "cuadrilla", "Ascendente");
                            List<EstadisticasItems> listaEstadisticas = Utilidades.sumarTotalesCuadrillas(items, "cuadrilla", "total");

                            if (grafico.equalsIgnoreCase("Barras")) {
                                graficoBarras.setVisibility(View.VISIBLE);
                                graficoAnillo.setVisibility(View.GONE);
                                graficoLineas.setVisibility(View.GONE);
                                lblNoDatos.setVisibility(View.GONE);

                                establecerGraficoHorizontal(listaEstadisticas);
                            }
                            else if (grafico.equalsIgnoreCase("Anillo")) {
                                graficoAnillo.setVisibility(View.VISIBLE);
                                graficoBarras.setVisibility(View.GONE);
                                graficoLineas.setVisibility(View.GONE);
                                lblNoDatos.setVisibility(View.GONE);

                                establecerGraficoPastel(listaEstadisticas);
                            }
                            else if (grafico.equalsIgnoreCase("Lineas")) {
                                graficoLineas.setVisibility(View.VISIBLE);
                                graficoBarras.setVisibility(View.GONE);
                                graficoAnillo.setVisibility(View.GONE);
                                lblNoDatos.setVisibility(View.GONE);

                                establecerGraficoLineas(listaEstadisticas);
                            }
                        }
                        else {
                            lblNoDatos.setVisibility(View.VISIBLE);
                            graficoLineas.setVisibility(View.GONE);
                            graficoBarras.setVisibility(View.GONE);
                            graficoAnillo.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.w("ObtenerGastos", e);
                    }
                });
            } else if (tipoDatos.equalsIgnoreCase("Ingresos")) {
                ingr.obtenerIngresos("", fecha, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<IngresosItems>() {
                    @Override
                    public void onCallback(List<IngresosItems> items) {
                        if (items != null && !items.isEmpty()) {//Si "items" no es null, que entre al if
                            //items = Utilidades.ordenarListaPorFechaHora(items, "fechaHora", "Descendente");
                            List<EstadisticasItems> listaEstadisticas = Utilidades.sumarTotalesCuadrillas(items, "cuadrilla", "total");

                            if (grafico.equalsIgnoreCase("Barras")) {
                                graficoBarras.setVisibility(View.VISIBLE);
                                graficoAnillo.setVisibility(View.GONE);
                                graficoLineas.setVisibility(View.GONE);
                                lblNoDatos.setVisibility(View.GONE);

                                establecerGraficoHorizontal(listaEstadisticas);
                            }
                            else if (grafico.equalsIgnoreCase("Anillo")) {
                                graficoAnillo.setVisibility(View.VISIBLE);
                                graficoBarras.setVisibility(View.GONE);
                                graficoLineas.setVisibility(View.GONE);
                                lblNoDatos.setVisibility(View.GONE);

                                establecerGraficoPastel(listaEstadisticas);
                            }
                            else if (grafico.equalsIgnoreCase("Lineas")) {
                                graficoLineas.setVisibility(View.VISIBLE);
                                graficoBarras.setVisibility(View.GONE);
                                graficoAnillo.setVisibility(View.GONE);
                                lblNoDatos.setVisibility(View.GONE);

                                establecerGraficoLineas(listaEstadisticas);
                            }
                        }
                        else {
                            lblNoDatos.setVisibility(View.VISIBLE);
                            graficoLineas.setVisibility(View.GONE);
                            graficoBarras.setVisibility(View.GONE);
                            graficoAnillo.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.w("ObtenerIngresos", e);
                    }
                });
            }
        }
        catch (Exception e) {
            Log.w("ObtenerDatos", e);
        }
    }

    private void establecerGraficoHorizontal(List<EstadisticasItems> items) {
        //Crear las entradas para el gráfico de barras
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        int i = 0;
        for (EstadisticasItems item : items) {
            barEntries.add(new BarEntry(i, (float) item.getTotal()));
            labels.add(item.getCuadrilla());
            i++;
        }

        // Crear el conjunto de datos del gráfico
        BarDataSet barDataSet = new BarDataSet(barEntries, "");
        barDataSet.setColors(getColor(R.color.clr_fuente_secundario));
        barDataSet.setValueTextColor(getColor(R.color.clr_fuente_primario));
        barDataSet.setValueTypeface(ResourcesCompat.getFont(this, R.font.montserrat_bold));
        barDataSet.setValueTextSize(10f);
        /*barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                return "L. " + barEntry.getY();
            }
        });*/

        // Crear los datos para el gráfico
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.7f); // Ancho de las barras

        // Configurar el gráfico
        graficoBarras.setData(barData);
        graficoBarras.setFitBars(true);
        graficoBarras.getDescription().setEnabled(false);
        graficoBarras.animateY(1000);
        //graficoBarras.setDrawValueAboveBar(false); //Meter los números dentro de las barras

        graficoBarras.getLegend().setEnabled(false);

        graficoBarras.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        graficoBarras.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        graficoBarras.getXAxis().setGranularity(1f);
        graficoBarras.getXAxis().setGranularityEnabled(true);
        graficoBarras.getXAxis().setDrawGridLines(false); //Eliminar lineas horizontales
        graficoBarras.getXAxis().setTextSize(8f); //Tamaño de texto de los Labels
        graficoBarras.getXAxis().setTextColor(getColor(R.color.clr_fuente_primario));
        graficoBarras.getXAxis().setXOffset(4f); // Ajusta el espaciado horizontal para evitar el corte
        //graficoBarras.getXAxis().setLabelRotationAngle(45f);

        Typeface customTypeface = ResourcesCompat.getFont(this, R.font.montserrat_semibold); // Cambia "my_custom_font" por el nombre de tu fuente
        graficoBarras.getXAxis().setTypeface(customTypeface);

        graficoBarras.getAxisLeft().setAxisMinimum(0f); // Asegurar que comience desde 0
        graficoBarras.getAxisLeft().setTypeface(ResourcesCompat.getFont(this, R.font.montserrat_semibold));
        graficoBarras.getAxisLeft().setTextSize(7f);
        graficoBarras.getAxisLeft().setTextColor(getColor(R.color.clr_fuente_primario));
        graficoBarras.getAxisLeft().setDrawGridLines(true); // Asegúrate de que las líneas de cuadrícula estén habilitadas
        graficoBarras.getAxisLeft().setGridColor(Color.LTGRAY);

        graficoBarras.getAxisRight().setEnabled(false); // Deshabilitar el eje derecho

        graficoBarras.setExtraOffsets(0, 0, 35, 0); // Añadir espacio en los márgenes

        //graficoBarras.fitScreen();
        graficoBarras.invalidate(); // Refrescar el gráfico
    }

    private void establecerGraficoPastel(List<EstadisticasItems> items) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        for (EstadisticasItems item : items) {
            pieEntries.add(new PieEntry((float) item.getTotal(), item.getCuadrilla()));
        }

        // Crear el conjunto de datos del gráfico
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");

        List<Integer> colores = Utilidades.obtenerColores();

        pieDataSet.setColors(colores);  // Asignar colores
        //pieDataSet.setSliceSpace(5f); //Espaciado entre secciones del gráfico
        pieDataSet.setValueTextColor(getColor(R.color.clr_fuente_terciario));
        pieDataSet.setValueTypeface(ResourcesCompat.getFont(this, R.font.montserrat_bold));
        pieDataSet.setValueTextSize(10f);
        /*pieDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPieLabel(float value, PieEntry pieEntry) {
                return "" + pieEntry.getY();
            }
        });*/

        /*pieDataSet.setValueLinePart1Length(0.2f);
        pieDataSet.setValueLinePart2Length(0.4f);*/

        //pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE); // Si quieres que se vean fuera de la gráfica

        // Crear los datos para el gráfico
        PieData pieData = new PieData(pieDataSet);

        // Configurar el gráfico
        graficoAnillo.setData(pieData);
        //graficoAnillo.setUsePercentValues(true);
        graficoAnillo.getDescription().setEnabled(false);
        //graficoAnillo.setDrawHoleEnabled(true);
        //graficoAnillo.setHoleColor(Color.TRANSPARENT);
        graficoAnillo.setEntryLabelTypeface(ResourcesCompat.getFont(this, R.font.montserrat_semibold));
        graficoAnillo.setTransparentCircleRadius(0f); //Ocultar circulo transparente del centro
        //graficoAnillo.setHoleRadius(50f);
        graficoAnillo.setEntryLabelTextSize(7f);
        graficoAnillo.setEntryLabelColor(getColor(R.color.clr_fuente_terciario));

        graficoAnillo.getLegend().setEnabled(false);

        // Configurar la leyenda
        /*Legend legend = graficoAnillo.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setTextSize(12f);*/

        // Animación del gráfico
        graficoAnillo.animateY(1000, Easing.EaseInOutQuad);

        // Refrescar el gráfico
        graficoAnillo.invalidate();
    }

    private void establecerGraficoLineas(List<EstadisticasItems> items) {
        // Crear las entradas para el gráfico de líneas
        ArrayList<Entry> lineEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        int i = 0;
        for (EstadisticasItems item : items) {
            lineEntries.add(new Entry(i, (float) item.getTotal()));
            labels.add(item.getCuadrilla());
            i++;
        }

        // Crear el conjunto de datos del gráfico
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "");
        lineDataSet.setColors(getColor(R.color.clr_fuente_grafico));
        lineDataSet.setCircleColors(getColor(R.color.clr_fuente_secundario));
        lineDataSet.setLineWidth(2f);
        lineDataSet.setValueTextSize(10f);

        // Crear los datos para el gráfico
        LineData lineData = new LineData(lineDataSet);

        // Configurar el gráfico
        graficoLineas.setData(lineData);
        graficoLineas.getDescription().setEnabled(false);
        graficoLineas.getLegend().setEnabled(false);
        graficoLineas.animateX(1000);

        // Configurar el eje X para mostrar las etiquetas de "Cuadrilla"
        XAxis xAxis = graficoLineas.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.size());
        xAxis.setLabelRotationAngle(-90); // Rotar las etiquetas si son largas

        YAxis yAxis = graficoLineas.getAxisLeft();
        yAxis.setGridColor(Color.LTGRAY);

        // Refrescar el gráfico
        graficoLineas.invalidate();
    }

    //Método Click del botón "Mes"
    public void elegirMes(View view) {
        //Establecemos el color del fondo y de la fuente para los botones de Mes y Año
        btnMes.setBackground(getDrawable(R.drawable.clr_casilladegradadoazul_redonda));
        btnMes.setTextColor(getColor(R.color.clr_fuente_terciario));
        btnAnio.setBackgroundColor(Color.TRANSPARENT);
        btnAnio.setTextColor(getColor(R.color.clr_fuente_secundario));

        tipoFecha = "Mes"; //Asignamos la palabra "Mes" a la variable global "tipoFecha"
    }

    //Método Click del botón "Año"
    public void elegirAnio(View view) {
        //Establecemos el color del fondo y de la fuente para los botones de Mes y Año
        btnAnio.setBackground(getDrawable(R.drawable.clr_casilladegradadoazul_redonda));
        btnAnio.setTextColor(getColor(R.color.clr_fuente_terciario));
        btnMes.setBackgroundColor(Color.TRANSPARENT);
        btnMes.setTextColor(getColor(R.color.clr_fuente_secundario));

        tipoFecha = "Año"; //Asignamos la palabra "Año" a la variable global "tipoFecha"
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
                    fechaSeleccionada = lblFecha.getText().toString();
                    if (fechaSeleccionada.equalsIgnoreCase("Seleccionar..."))
                        fechaSeleccionada = "Seleccionar Mes";

                    obtenerDatos(fechaSeleccionada, tipoGrafico); //Llamamos el método "obtenerIngresos" de arriba y le mandamos el contenido del lblFecha
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

    //Evento Clic del LinearLayout de Fecha, al dar clic en el mismo, se abrirá un "Popup DatePicker" en el que se podrá seleccionar un mes o un año, y esto servirá para filtrar los gráficos
    public void mostrarMesesAnios(View view) {
        if (tipoFecha.equalsIgnoreCase("Mes")) {
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(EstadisticasGastosIngresos.this, style, dateSetListener, year, month, day); //Creamos un nuevo objeto de tipo DatePickerDialog y le mandamos como parámetros al constructor, un contexto, la variable "style" que guarda el estilo, el "dateSetListener", el año, mes y día, estos últimos para que al abrir el AlertDialog, se muestre el mes actual
                datePickerDialog.getDatePicker().findViewById(getResources().getIdentifier("day", "id", "android")).setVisibility(View.GONE); //Ocultamos el spinner de días asignando "GONE" en su visibilidad
                datePickerDialog.show(); //Mostramos el AlertDialog o Popup DatePicker de solo mes y año
            }
            catch (Exception e) {
                Log.w("ObtenerMes", e);
            }
        }
        else if (tipoFecha.equalsIgnoreCase("Año")) {
            DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() { //Creamos una variable de tipo DatePickerDialog, y creamos el evento "OnDateSetListener" para que responda cuando se selecciona una fecha del AlertDialog o Popup DatePicker de sólo mes y año
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    lblFecha.setText(String.valueOf(year)); //Asignamos el año ya convertido a String al lblFechaSeleccionada
                }
            };

            Calendar cal = Calendar.getInstance(); //Creamos un objeto de tipo Calendar que representa la fecha y hora actuales en el dispositivo donde se está ejecutando el código
            int year = cal.get(Calendar.YEAR); //Obtenemos el año actual
            int month = cal.get(Calendar.MONTH); //Obtenemos el mes actual
            int day = cal.get(Calendar.DAY_OF_MONTH); //Obtenemos el día actual
            int style = AlertDialog.THEME_HOLO_LIGHT; //En una variable entera guardamos el estilo que tendrá la ventana emergente

            DatePickerDialog datePickerDialog = new DatePickerDialog(EstadisticasGastosIngresos.this, style, dateSetListener, year, month, day); //Creamos un nuevo objeto de tipo DatePickerDialog y le mandamos como parámetros al constructor, un contexto, la variable "style" que guarda el estilo, el "dateSetListener", el año, mes y día, estos últimos para que al abrir el AlertDialog, se muestre el mes actual
            datePickerDialog.getDatePicker().findViewById(getResources().getIdentifier("day", "id", "android")).setVisibility(View.GONE); //Ocultamos el spinner de días asignando "GONE" en su visibilidad
            datePickerDialog.getDatePicker().findViewById(getResources().getIdentifier("month", "id", "android")).setVisibility(View.GONE); //Ocultamos el spinner de meses asignando "GONE" en su visibilidad
            datePickerDialog.show(); //Mostramos el AlertDialog o Popup DatePicker de solo mes y año
        }
    }

    //Método para eliminar la selección del Mes o Año
    public void eliminarMesAnios(View view) {
        lblFecha.setText("Seleccionar...");
    }

    public void mostrarOpcionesGraficos(View view) {
        PopupMenu popup = new PopupMenu(this, view); //Objeto de tipo "PopupMenu"
        popup.setOnMenuItemClickListener(this); //Indicamos que asigne el evento "OnMenuItemClick" para que haga algo cada vez que se dé click a una opción del menú
        popup.inflate(R.menu.popupmenu_tiposgraficos); //Inflamos la vista del menú indicando la ruta de dicha vista gráfica

        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menuGraficoBarras:
                imgGrafico.setImageResource(R.mipmap.ico_azul_barchart); //Establecemos la imagen del gráfico de barras en el selector del tipo de gráfico a mostrar
                tipoGrafico = "Barras";
                obtenerDatos(fechaSeleccionada, tipoGrafico);
                return true;

            case R.id.menuGraficoAnillo:
                imgGrafico.setImageResource(R.mipmap.ico_azul_donutchart); //Establecemos la imagen del gráfico de anillo en el selector del tipo de gráfico a mostrar
                tipoGrafico = "Anillo";
                obtenerDatos(fechaSeleccionada, tipoGrafico);
                return true;

            case R.id.menuGraficoLineas:
                imgGrafico.setImageResource(R.mipmap.ico_azul_linechart); //Establecemos la imagen del gráfico de lineas en el selector del tipo de gráfico a mostrar
                tipoGrafico = "Lineas";
                obtenerDatos(fechaSeleccionada, tipoGrafico);
                return true;

            default:
                return false;
        }
    }

    public void retroceder(View view) {
        onBackPressed();
    }
}