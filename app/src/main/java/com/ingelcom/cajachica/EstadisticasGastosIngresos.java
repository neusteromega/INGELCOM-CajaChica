package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
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
import com.ingelcom.cajachica.DAO.Gasto;
import com.ingelcom.cajachica.DAO.Ingreso;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.Modelos.EstadisticasItems;
import com.ingelcom.cajachica.Modelos.GastosItems;
import com.ingelcom.cajachica.Modelos.IngresosItems;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EstadisticasGastosIngresos extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, PopupMenu.OnMenuItemClickListener {

    private TextView btnReintentarConexion, lblTitulo, btnMes, btnAnio, lblFecha, lblTotal, lblNoDatos;
    private ImageView imgGrafico;
    private SwipeRefreshLayout swlRecargar;
    private View viewNoInternet;
    private ProgressBar pbReintentarConexion;
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

        inicializarElementos();
        establecerElementos();
        obtenerDatos("", "Barras"); //Llamamos el método "obtenerDatos" donde primeramente mandamos la "fecha" vacía y el texto "Barras" para que primero muestre el gráfico de barras horizontales
        cambioFecha();
        desactivarSwipeEnGraficos();

        //Evento Click del botón "Reintentar" de la vista "viewNoInternet"
        btnReintentarConexion.setOnClickListener(v -> {
            pbReintentarConexion.setVisibility(View.VISIBLE); //Mostramos el ProgressBar

            //Creamos una nueva instancia de "Handler", que está vinculada al Looper principal (el hilo principal de la aplicación). Esto asegura que cualquier operación realizada dentro de este Handler se ejecute en el hilo principal
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { //El "Handler" utiliza el método "postDelayed" para ejecutar el "Runnable" que contiene las acciones a realizar después de un retraso especificado (en este caso, 1000 milisegundos, es decir, 1 segundo)
                @Override
                public void run() {
                    pbReintentarConexion.setVisibility(View.GONE); //Después de un segundo, ocultamos el ProgressBar
                }
            }, 1000);

            Utilidades.mostrarMensajePorInternetCaido(this, viewNoInternet); //Llamamos el método utilitario "mostrarMensajePorInternetCaido" donde mandamos la vista "viewNoInternet" donde se hará visible cuando no haya conexión a internet y se ocultará cuando si haya
        });
    }

    private void inicializarElementos() {
        tipoDatos = Utilidades.obtenerStringExtra(this, "ActivityEGI"); //Obtenemos el tipoDatos de la pantalla anterior (FragAdmEstadisticas) el cual puede ser "Gastos" o "Ingresos"

        //Enlazamos las variables globales con los elementos gráficos
        lblTitulo = findViewById(R.id.lblTituloEstGI);
        btnMes = findViewById(R.id.lblMesEstGI);
        btnAnio = findViewById(R.id.lblAnioEstGI);
        lblFecha = findViewById(R.id.lblFechaEstGI);
        lblTotal = findViewById(R.id.lblTotalEstGI);
        lblNoDatos = findViewById(R.id.lblNoDatosEstGI);
        imgGrafico = findViewById(R.id.imgTipoGraficoEstGI);
        swlRecargar = findViewById(R.id.swipeRefreshLayoutEstGI);
        viewNoInternet = findViewById(R.id.viewNoInternetEstGI);
        btnReintentarConexion = findViewById(R.id.btnReintentarConexion);
        pbReintentarConexion = findViewById(R.id.pbReintentarConexion);
        graficoBarras = findViewById(R.id.graficoBarrasEstGI);
        graficoAnillo = findViewById(R.id.graficoPastelEstGI);
        graficoLineas = findViewById(R.id.graficoLineasEstGI);

        swlRecargar.setOnRefreshListener(this); //Llamada al método "onRefresh"
        Utilidades.mostrarMensajePorInternetCaido(this, viewNoInternet); //Llamamos el método utilitario "mostrarMensajePorInternetCaido" donde mandamos la vista "viewNoInternet" donde se hará visible cuando no haya conexión a internet y se ocultará cuando si haya
    }

    private void establecerElementos() {
        swlRecargar.setColorSchemeResources(R.color.clr_fuente_primario); //Color del SwipeRefreshLayout

        switch (tipoDatos) { //Asignamos los titulos dependiendo del "tipoDatos" recibido de la pantalla anterior, para eso usamos este switch
            case "Gastos":
                lblTitulo.setText("Gastos");
                break;

            case "Ingresos":
                lblTitulo.setText("Ingresos");
                break;
        }
    }

    //Método que nos permitirá obtener los gastos o ingresos (dependiendo del contenido de la variable global "tipoDatos") de Firestore
    private void obtenerDatos(String fecha, String grafico) { //Recibe la "fecha" el cual puede ser el año o mes seleccionado por el usuario para filtrar los datos de las gráficas, y el "grafico" que indica cuál gráfico se desea visualizar
        try {
            if (tipoDatos.equalsIgnoreCase("Gastos")) { //Si "tipoDatos" contiene el texto "Gastos", que entre al if para obtener los gastos
                //Llamamos el método "obtenerGastos" de la clase "Gasto", le mandamos la cuadrilla vacía, el texto "Empleado" ya que sólo queremos obtener los gastos hechos por los empleados, el usuario vacío, el tipo de compra vacío y el "mesAnio". Con esto se podrán obtener todos los gastos hechos por los empleados
                gast.obtenerGastos("", "Empleado", "", "", fecha, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<GastosItems>() {
                    @Override
                    public void onCallback(List<GastosItems> items) {
                        if (items != null && !items.isEmpty()) { //Si "items" no es null, y si no está vacío, que entre al if
                            List<EstadisticasItems> listaEstadisticas = Utilidades.sumarTotalesCuadrillas(items, "cuadrilla", "total"); //Llamamos al método utilitario "sumarTotalesCuadrillas", donde le mandamos la lista "items" con los gastos obtenidos, el texto "cuadrilla", y el texto "total", estos últimos indican que así se llaman los campos de los cuales queremos eliminar sus repetidos y sumar sus totales. Esto retorna una lista de tipo "EstadisticasItems" y por ende, guardamos el retorno en "listaEstadisticas" del mismo tipo de dato

                            double total = 0; //Creamos una variable double donde se guardará el total de Gastos
                            for (EstadisticasItems item : listaEstadisticas) { //Foreach que recorre cada elemento de la lista "listaEstadísticas" y lo van guardando en la variable temporal "item"
                                total += item.getTotal(); //Obtenemos el "total" de cada elemento de la lista "listaEstadísticas" y lo vamos sumando en la variable "total"
                            }

                            lblTotal.setText("L. " + String.format("%.2f", total)); //Asignamos el total al TextView "lblTotal" y formateamos la variable "total" para que se muestre con dos digitos después del punto decimal

                            if (grafico.equalsIgnoreCase("Barras")) { //Si "grafico" contiene el texto "Barras" que entre al if
                                //Hacemos visible el "gráficoBarras" y ocultamos los demás, incluso el TextView "lblNoDatos"
                                graficoBarras.setVisibility(View.VISIBLE);
                                graficoAnillo.setVisibility(View.GONE);
                                graficoLineas.setVisibility(View.GONE);
                                lblNoDatos.setVisibility(View.GONE);

                                establecerGraficoHorizontal(listaEstadisticas); //Llamamos el método "establecerGraficoHorizontal" y le mandamos la "listaEstadisticas"
                            }
                            else if (grafico.equalsIgnoreCase("Anillo")) { //Si "grafico" contiene el texto "Anillo" que entre al if
                                //Hacemos visible el "gráficoAnillo" y ocultamos los demás, incluso el TextView "lblNoDatos"
                                graficoAnillo.setVisibility(View.VISIBLE);
                                graficoBarras.setVisibility(View.GONE);
                                graficoLineas.setVisibility(View.GONE);
                                lblNoDatos.setVisibility(View.GONE);

                                establecerGraficoAnillo(listaEstadisticas); //Llamamos el método "establecerGraficoAnillo" y le mandamos la "listaEstadisticas"
                            }
                            else if (grafico.equalsIgnoreCase("Lineas")) { //Si "grafico" contiene el texto "Lineas" que entre al if
                                //Hacemos visible el "graficoLineas" y ocultamos los demás, incluso el TextView "lblNoDatos"
                                graficoLineas.setVisibility(View.VISIBLE);
                                graficoBarras.setVisibility(View.GONE);
                                graficoAnillo.setVisibility(View.GONE);
                                lblNoDatos.setVisibility(View.GONE);

                                establecerGraficoLineas(listaEstadisticas); //Llamamos el método "establecerGraficoLineas" y le mandamos la "listaEstadisticas"
                            }
                        }
                        else { //En cambio, si "items" es null, o si está vacío (una de las dos) significa que no se encontraron datos en Firestore, por lo tanto, que entre a este else
                            //Ponemos en 0 el total, hacemos visible el TextView "lblNoDatos" y ocultamos los gráficos
                            lblTotal.setText("L. 0.00");
                            lblNoDatos.setVisibility(View.VISIBLE);
                            graficoLineas.setVisibility(View.GONE);
                            graficoBarras.setVisibility(View.GONE);
                            graficoAnillo.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                        Log.e("ObtenerGastos", "Error al obtener los gastos", e);
                    }
                });
            }
            else if (tipoDatos.equalsIgnoreCase("Ingresos")) { //En cambio, si "tipoDatos" contiene el texto "Ingresos", que entre al if para obtener los ingresos
                //Llamamos el método "obtenerIngresos" de la clase "Ingreso", le mandamos la cuadrilla vacía ya que no queremos filtrar, un false para "datosEmpleado" (ya que no queremos reducir la lista de ingresos para el mes actual y anterior) y el "mesAnio". Con esto se podrán obtener todos los ingresos hechos por los administradores
                ingr.obtenerIngresos("", fecha, false, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<IngresosItems>() {
                    @Override
                    public void onCallback(List<IngresosItems> items) {
                        if (items != null && !items.isEmpty()) { //Si "items" no es null y no está vacío, que entre al if
                            List<EstadisticasItems> listaEstadisticas = Utilidades.sumarTotalesCuadrillas(items, "cuadrilla", "total"); //Llamamos al método utilitario "sumarTotalesCuadrillas", donde le mandamos la lista "items" con los ingresos obtenidos, el texto "cuadrilla", y el texto "total", estos últimos indican que así se llaman los campos de los cuales queremos eliminar sus repetidos y sumar sus totales. Esto retorna una lista de tipo "EstadisticasItems" y por ende, guardamos el retorno en "listaEstadisticas" del mismo tipo de dato

                            double total = 0; //Creamos una variable double donde se guardará el total de Ingresos
                            for (EstadisticasItems item : listaEstadisticas) { //Foreach que recorre cada elemento de la lista "listaEstadísticas" y lo van guardando en la variable temporal "item"
                                total += item.getTotal(); //Obtenemos el "total" de cada elemento de la lista "listaEstadísticas" y lo vamos sumando en la variable "total"
                            }

                            lblTotal.setText("L. " + String.format("%.2f", total)); //Asignamos el total al TextView "lblTotal" y formateamos la variable "total" para que se muestre con dos digitos después del punto decimal

                            if (grafico.equalsIgnoreCase("Barras")) { //Si "grafico" contiene el texto "Barras" que entre al if
                                //Hacemos visible el "gráficoBarras" y ocultamos los demás, incluso el TextView "lblNoDatos"
                                graficoBarras.setVisibility(View.VISIBLE);
                                graficoAnillo.setVisibility(View.GONE);
                                graficoLineas.setVisibility(View.GONE);
                                lblNoDatos.setVisibility(View.GONE);

                                establecerGraficoHorizontal(listaEstadisticas); //Llamamos el método "establecerGraficoHorizontal" y le mandamos la "listaEstadisticas"
                            }
                            else if (grafico.equalsIgnoreCase("Anillo")) { //Si "grafico" contiene el texto "Anillo" que entre al if
                                //Hacemos visible el "gráficoAnillo" y ocultamos los demás, incluso el TextView "lblNoDatos"
                                graficoAnillo.setVisibility(View.VISIBLE);
                                graficoBarras.setVisibility(View.GONE);
                                graficoLineas.setVisibility(View.GONE);
                                lblNoDatos.setVisibility(View.GONE);

                                establecerGraficoAnillo(listaEstadisticas); //Llamamos el método "establecerGraficoAnillo" y le mandamos la "listaEstadisticas"
                            }
                            else if (grafico.equalsIgnoreCase("Lineas")) { //Si "grafico" contiene el texto "Lineas" que entre al if
                                //Hacemos visible el "graficoLineas" y ocultamos los demás, incluso el TextView "lblNoDatos"
                                graficoLineas.setVisibility(View.VISIBLE);
                                graficoBarras.setVisibility(View.GONE);
                                graficoAnillo.setVisibility(View.GONE);
                                lblNoDatos.setVisibility(View.GONE);

                                establecerGraficoLineas(listaEstadisticas); //Llamamos el método "establecerGraficoLineas" y le mandamos la "listaEstadisticas"
                            }
                        }
                        else { //En cambio, si "items" es null, o si está vacío (una de las dos) significa que no se encontraron datos en Firestore, por lo tanto, que entre a este else
                            //Ponemos en 0 el total, hacemos visible el TextView "lblNoDatos" y ocultamos los gráficos
                            lblTotal.setText("L. 0.00");
                            lblNoDatos.setVisibility(View.VISIBLE);
                            graficoLineas.setVisibility(View.GONE);
                            graficoBarras.setVisibility(View.GONE);
                            graficoAnillo.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                        Log.e("ObtenerIngresos", "Error al obtener los ingresos", e);
                    }
                });
            }
        }
        catch (Exception e) {
            Log.e("ObtenerDatos", "Error al obtener los datos", e);
        }
    }

    //Método que nos permite establecer el gráfico de barras horizontales
    private void establecerGraficoHorizontal(List<EstadisticasItems> items) { //Recibe una lista de tipo "EstadisticasItems" ya con las cuadrillas sin repetir y con los totales sumados
        ArrayList<BarEntry> barEntries = new ArrayList<>(); //ArrayList de tipo "BarEntry" (Dato de MPAndroidChart para los gráficos de barras) que recibirá los valores para el gráfico
        ArrayList<String> etiquetas = new ArrayList<>(); //ArrayList de tipo "String" que recibirá los nombres de las cuadrillas para establecerlos en las etiquetas del gráfico

        int i = 0; //Contador que nos servirá para agregar datos al ArrayList "barEntries"
        for (EstadisticasItems item : items) { //Foreach que recorre uno por uno los elementos de la lista "items" y va guardando cada elemento en la variable temporal "item"
            barEntries.add(new BarEntry(i, (float) item.getTotal())); //Cada "total" de la lista "items" lo asignamos en el ArrayList "barEntries"
            etiquetas.add(item.getCuadrilla()); //Cada "cuadrilla" de la lista "items" lo asignamos en el ArrayList "etiquetas"
            i++; //Sumamos un digito al contador
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, ""); //Con BarDataSet, agrupamos todos los BarEntry del ArrayList "barEntries" en un conjunto de datos para el gráfico
        barDataSet.setColors(getColor(R.color.clr_fuente_secundario)); //Color de las barras
        barDataSet.setValueTextColor(getColor(R.color.clr_fuente_primario)); //Color de los valores númericos de las barras
        barDataSet.setValueTypeface(ResourcesCompat.getFont(this, R.font.montserrat_bold)); //Fuente para el texto de los valores
        barDataSet.setValueTextSize(10f); //Tamaño del texto de los valores

        BarData barData = new BarData(barDataSet); //Con BarData, contenemos el conjunto de datos (barDataSet) para luego pasarlos al gráfico
        barData.setBarWidth(0.9f); //Ajustamos el ancho de las barras

        XAxis xAxis = graficoBarras.getXAxis(); //Obtenemos una instancia del eje X del gráfico (en este caso, el eje X es la parte izquierda ya que es un gráfico horizontal y sus propiedades cambian)
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //Establecemos la posición de las etiquetas, BOTTOM las establece afuera del gráfico
        xAxis.setValueFormatter(new IndexAxisValueFormatter(etiquetas)); //Asignamos el formato de los valores del eje X; aquí se usa "IndexAxisValueFormatter" para que las etiquetas sean los nombres de las cuadrillas
        xAxis.setGranularityEnabled(true); //Habilitamos la granularidad, que nos sirve para que las etiquetas se mantengan asociadas a su barra correspondiente
        xAxis.setGranularity(1f); //Esto como para que las etiquetas se quedan centradas al lado de su barra correspondiente, aún cuando se le haga zoom al gráfico
        xAxis.setDrawGridLines(false); //Eliminamos lineas horizontales
        xAxis.setTextSize(8f); //Tamaño de texto de las etiquetas
        xAxis.setTextColor(getColor(R.color.clr_fuente_primario)); //Color del texto de las etiquetas
        xAxis.setLabelCount(etiquetas.size()); //Forzamos la cantidad de etiquetas que se mostrarán al establecer el conteo de "etiquetas"
        xAxis.setXOffset(4f); //Ajustamos el espaciado horizontal entre las etiquetas y la linea vertical del inicio del gráfico
        xAxis.setTypeface(ResourcesCompat.getFont(this, R.font.montserrat_semibold)); //Fuente de las etiquetas

        graficoBarras.setFitBars(true); //Ajustamos las barras para que encajen en la vista
        graficoBarras.setData(barData); //Asignamos el "barData" al gráfico de barras
        graficoBarras.animateY(1000); //Establecemos una animación de entrada que durará un segundo
        graficoBarras.getDescription().setEnabled(false); //Eliminamos la descripción
        graficoBarras.getLegend().setEnabled(false); //Eliminamos la leyenda

        YAxis axisLeft = graficoBarras.getAxisLeft(); //Obtenemos una instancia del eje izquierdo del gráfico (en este caso, el eje izquierdo está en la parte superior ya que es un gráfico horizontal y sus propiedades cambian)
        axisLeft.setAxisMinimum(0f); //Aseguramos que los valores de referencia comiencen desde 0
        axisLeft.setTypeface(ResourcesCompat.getFont(this, R.font.montserrat_semibold)); //Fuente de los valores de referencia
        axisLeft.setTextSize(8f); //Tamaño de texto de los valores de referencia
        axisLeft.setTextColor(getColor(R.color.clr_fuente_primario)); //Color del texto de los valores de referencia
        axisLeft.setDrawGridLines(true); //Aseguramos que las líneas de cuadrícula estén habilitadas
        axisLeft.setGridColor(Color.LTGRAY); //Color a las lineas verticales del gráfico

        YAxis axisRight = graficoBarras.getAxisRight(); //Obtenemos una instancia del eje derecho del gráfico (en este caso, el eje derecho está en la parte inferior ya que es un gráfico horizontal y sus propiedades cambian)
        axisRight.setEnabled(false); //Deshabilitamos el eje derecho ya que no lo necesitaremos

        graficoBarras.setExtraOffsets(5, 0, 35, 0); //Añadimos espacio en los márgenes del gráfico
        graficoBarras.invalidate(); //Refrescamos el gráfico
    }

    //Método que nos permite establecer el gráfico de anillo
    private void establecerGraficoAnillo(List<EstadisticasItems> items) { //Recibe una lista de tipo "EstadisticasItems" ya con las cuadrillas sin repetir y con los totales sumados
        ArrayList<PieEntry> pieEntries = new ArrayList<>(); //ArrayList de tipo "PieEntry" (Dato de MPAndroidChart para los gráficos de anillo o pastel) que recibirá los valores para el gráfico

        for (EstadisticasItems item : items) { //Foreach que recorre uno por uno los elementos de la lista "items" y va guardando cada elemento en la variable temporal "item"
            pieEntries.add(new PieEntry((float) item.getTotal(), item.getCuadrilla())); //Cada "total" de la lista "items" lo asignamos en el ArrayList "pieEntries". Cada total se asocia a una etiqueta (item.getCuadrilla)
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, ""); //Con PieDataSet, agrupamos todos los PieEntry del ArrayList "pieEntries" en un conjunto de datos para el gráfico

        List<Integer> colores = Utilidades.obtenerColores(); //Obtenemos los colores para el gráfico que se establecen en el método utilitario "obtenerColores" que retorna una lista de enteros ya con los colores hexadecimales

        pieDataSet.setColors(colores); //Asignamos los colores a los segmentos del gráfico de anillo
        pieDataSet.setValueTextColor(getColor(R.color.clr_fuente_terciario)); //Color del texto de los valores numéricos
        pieDataSet.setValueTypeface(ResourcesCompat.getFont(this, R.font.montserrat_bold)); //Fuente para el texto de los valores numéricos
        pieDataSet.setValueTextSize(10f); //Tamaño del texto de los valores numéricos

        PieData pieData = new PieData(pieDataSet); //Con PieData, contenemos el conjunto de datos (pieDataSet) para luego pasarlos al gráfico

        graficoAnillo.setData(pieData); //Asignamos el "pieData" al gráfico de anillo
        graficoAnillo.getDescription().setEnabled(false); //Inhabilitamos la descripción para el gráfico
        graficoAnillo.setEntryLabelTypeface(ResourcesCompat.getFont(this, R.font.montserrat_semibold)); //Fuente para el texto de las etiquetas
        graficoAnillo.setTransparentCircleRadius(0f); //Ocultar circulo transparente del centro
        graficoAnillo.setEntryLabelTextSize(7f); //Tamaño del texto de las etiquetas
        graficoAnillo.setEntryLabelColor(getColor(R.color.clr_fuente_terciario)); //Color del texto de las etiquetas

        graficoAnillo.getLegend().setEnabled(false); //Inhabilitamos la leyenda del gráfico
        graficoAnillo.animateY(1000, Easing.EaseInOutQuad); //Establecemos una animación de entrada que durará un segundo
        graficoAnillo.invalidate(); //Refrescamos el gráfico
    }

    //Método que nos permite establecer el gráfico de lineas
    private void establecerGraficoLineas(List<EstadisticasItems> items) { //Recibe una lista de tipo "EstadisticasItems" ya con las cuadrillas sin repetir y con los totales sumados
        ArrayList<Entry> lineEntries = new ArrayList<>(); //ArrayList de tipo "Entry" (Dato de MPAndroidChart para los gráficos de lineas) que recibirá los valores para el gráfico
        ArrayList<String> etiquetas = new ArrayList<>(); //ArrayList de tipo "String" que recibirá los nombres de las cuadrillas para establecerlos en las etiquetas del gráfico

        int i = 0; //Contador que nos servirá para agregar datos al ArrayList "lineEntries"
        for (EstadisticasItems item : items) { //Foreach que recorre uno por uno los elementos de la lista "items" y va guardando cada elemento en la variable temporal "item"
            lineEntries.add(new Entry(i, (float) item.getTotal())); //Cada "total" de la lista "items" lo asignamos en el ArrayList "lineEntries"
            etiquetas.add(item.getCuadrilla()); //Cada "cuadrilla" de la lista "items" lo asignamos en el ArrayList "etiquetas"
            i++; //Sumamos un digito al contador
        }

        LineDataSet lineDataSet = new LineDataSet(lineEntries, ""); //Con LineDataSet, agrupamos todos los Entry del ArrayList "lineEntries" en un conjunto de datos para el gráfico
        lineDataSet.setColors(getColor(R.color.clr_fuente_grafico)); //Color para las lineas del gráfico
        lineDataSet.setCircleColors(getColor(R.color.clr_fuente_grafico)); //Color de los círculos encima de cada linea
        lineDataSet.setDrawCircleHole(false); //Inhabilitamos un agujero para los círculos encima de cada linea, esto para que el círculo esté rellenado
        lineDataSet.setLineWidth(2f); //Ancho de las lineas
        lineDataSet.setValueTextSize(9f); //Tamaño del texto de los valores
        lineDataSet.setValueTextColor(getColor(R.color.clr_fuente_primario)); //Color del texto de los valores
        lineDataSet.setValueTypeface(ResourcesCompat.getFont(this, R.font.montserrat_bold)); //Fuente del texto de los valores
        lineDataSet.setHighLightColor(getColor(R.color.clr_fuente_secundario)); //Color que se muestra al dar clic en una parte del gráfico

        LineData lineData = new LineData(lineDataSet); //Con LineData, contenemos el conjunto de datos (lineDataSet) para luego pasarlos al gráfico

        XAxis xAxis = graficoLineas.getXAxis(); //Obtenemos una instancia del eje X del gráfico (el eje de abajo)
        xAxis.setValueFormatter(new IndexAxisValueFormatter(etiquetas)); //Asignamos el formato de los valores del eje X; aquí se usa "IndexAxisValueFormatter" para que las etiquetas sean los nombres de las cuadrillas
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //Establecemos la posición de las etiquetas, BOTTOM las establece afuera del gráfico
        xAxis.setDrawGridLines(false); //Eliminamos lineas verticales
        xAxis.setGranularity(1f); //Esto como para que las etiquetas se quedan centradas al lado de su círculo de linea correspondiente, aún cuando se le haga zoom al gráfico
        xAxis.setLabelCount(etiquetas.size()); //Forzamos la cantidad de etiquetas que se mostrarán al establecer el conteo de "etiquetas"
        xAxis.setLabelRotationAngle(-90); //Rotar las etiquetas si son largas
        xAxis.setTextColor(getColor(R.color.clr_fuente_primario)); //Color del texto de las etiquetas
        xAxis.setTypeface(ResourcesCompat.getFont(this, R.font.montserrat_semibold)); //Fuente del texto de las etiquetas

        graficoLineas.setData(lineData); //Asignamos el "lineData" al gráfico de lineas
        graficoLineas.getDescription().setEnabled(false); //Eliminamos la descripción
        graficoLineas.getLegend().setEnabled(false); //Eliminamos la leyenda
        graficoLineas.animateX(1000); //Establecemos una animación de entrada que durará un segundo
        graficoLineas.setExtraOffsets(5, 0, 20, 10); //Añadimos espacio en los márgenes del gráfico

        YAxis axisLeft = graficoLineas.getAxisLeft(); //Obtenemos una instancia del eje izquierdo del gráfico
        axisLeft.setGridColor(Color.LTGRAY); //Color a las lineas horizontales del gráfico
        axisLeft.setTextColor(getColor(R.color.clr_fuente_primario)); //Color del texto de los valores de referencia
        axisLeft.setTypeface(ResourcesCompat.getFont(this, R.font.montserrat_semibold)); //Fuente del texto de los valores de referencia
        axisLeft.setTextSize(8f); //Tamaño del texto de los valores de referencia

        YAxis axisRight = graficoLineas.getAxisRight(); //Obtenemos una instancia del eje derecho del gráfico
        axisRight.setEnabled(false); //Deshabilitamos el eje derecho ya que no lo necesitaremos

        graficoLineas.invalidate(); //Refrescamos el gráfico
    }

    //Evento clic del botón "Mes"
    public void elegirMes(View view) {
        //Establecemos el color del fondo y de la fuente para los botones de Mes y Año
        btnMes.setBackground(getDrawable(R.drawable.clr_casilladegradadoazul_redonda));
        btnMes.setTextColor(getColor(R.color.clr_fuente_terciario));
        btnAnio.setBackgroundColor(Color.TRANSPARENT);
        btnAnio.setTextColor(getColor(R.color.clr_fuente_secundario));

        tipoFecha = "Mes"; //Asignamos la palabra "Mes" a la variable global "tipoFecha"
    }

    //Evento clic del botón "Año"
    public void elegirAnio(View view) {
        //Establecemos el color del fondo y de la fuente para los botones de Mes y Año
        btnAnio.setBackground(getDrawable(R.drawable.clr_casilladegradadoazul_redonda));
        btnAnio.setTextColor(getColor(R.color.clr_fuente_terciario));
        btnMes.setBackgroundColor(Color.TRANSPARENT);
        btnMes.setTextColor(getColor(R.color.clr_fuente_secundario));

        tipoFecha = "Año"; //Asignamos la palabra "Año" a la variable global "tipoFecha"
    }

    //Método en el que se detecta cuando "lblFecha" cambia su texto; esto nos ayudará para filtrar los datos de los gráficos en tiempo real
    private void cambioFecha() {
        try {
            //Para detectar cuando el lblFecha cambia su valor, llamamos el método "addTextChangedListener"
            lblFecha.addTextChangedListener(new TextWatcher() {
                @Override //Antes de que el texto del lblFecha cambie
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override //Durante el texto del lblFecha está cambiando
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    fechaSeleccionada = lblFecha.getText().toString(); //Guardamos el texto cambiado de "lblFecha" en la variable global "fechaSeleccionada"
                    obtenerDatos(fechaSeleccionada, tipoGrafico); //Llamamos el método "obtenerIngresos" de arriba y le mandamos el contenido de "fechaSeleccionada" y el contenido de "tipoGrafico"
                }

                @Override //Después de que el texto del lblFecha cambie
                public void afterTextChanged(Editable editable) {

                }
            });
        }
        catch (Exception e) {
            Log.e("DetectarFecha", "Error al detectar la fecha", e);
        }
    }

    //Evento Clic del LinearLayout de Fecha, al dar clic en el mismo, se abrirá un "Popup DatePicker" en el que se podrá seleccionar un mes o un año, y esto servirá para filtrar los gráficos
    public void mostrarMesesAnios(View view) {
        if (tipoFecha.equalsIgnoreCase("Mes")) { //Si la variable global "tipoFecha" obtiene la palabra "Mes", significa que el usuario tiene seleccionado el botón de mes en la pantalla; por lo tanto, que entre al if
            try {
                //Creamos una instancia de la interfaz "DatePickerDialog.OnDateSetListener" y esta define el método "onDateSet" que se llama cuando el usuario selecciona una fecha en el DatePickerDialog
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) { //Se ejecuta cuando el usuario ha seleccionado una fecha
                        month = month + 1; //Al mes le sumamos +1 porque los meses por defecto empiezan en 0 y no en 1
                        String fecha = Utilidades.convertirMonthYearString(month, year); //Guardamos el mes y año convertidos a String llamando al método "convertirMonthYearString" con los parámetros de mes y año, y esto retorna el String con el formato "Mes - Año"
                        lblFecha.setText(fecha); //Asignamos la fecha (Mes - Año) ya convertida a String al TextView lblFecha
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
                Log.e("ObtenerMes", "Error al obtener mes", e);
            }
        }
        else if (tipoFecha.equalsIgnoreCase("Año")) { //En cambio, si la variable global "tipoFecha" obtiene la palabra "Año", significa que el usuario tiene seleccionado el botón de año en la pantalla; por lo tanto, que entre al else if
            try {
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() { //Creamos una instancia de la interfaz "DatePickerDialog.OnDateSetListener" y esta define el método "onDateSet" que se llama cuando el usuario selecciona una fecha en el DatePickerDialog
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) { //Se ejecuta cuando el usuario ha seleccionado una fecha
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
                datePickerDialog.show(); //Mostramos el AlertDialog o Popup DatePicker de solo años
            }
            catch (Exception e) {
                Log.e("ObtenerYear", "Error al obtener año", e);
            }
        }
    }

    //Método para eliminar la selección del Mes o Año
    public void eliminarMesAnios(View view) {
        lblFecha.setText("Seleccionar..."); //Aquí sólo asignamos el texto "Seleccionar..." al "lblFecha"
    }

    //Método que configura el PopupMenu para seleccionar el tipo de gráfico que desea visualizar
    public void mostrarOpcionesGraficos(View view) {
        PopupMenu popup = new PopupMenu(this, view); //Objeto de tipo "PopupMenu"
        popup.setOnMenuItemClickListener(this); //Indicamos que asigne el evento "OnMenuItemClick" para que haga algo cada vez que se dé click a una opción del menú, esto llama al método de abajo "onMenuItemClick"
        popup.inflate(R.menu.popupmenu_tiposgraficos); //Inflamos la vista del menú indicando la ruta de dicha vista gráfica

        popup.show(); //Mostramos el popupMenu
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        //Switch que detecta los IDs de las opciones del PopupMenu de Tipos de Gráficos
        switch (menuItem.getItemId()) {
            case R.id.menuGraficoBarras:
                imgGrafico.setImageResource(R.mipmap.ico_azul_barchart); //Establecemos la imagen del gráfico de barras en el selector del tipo de gráfico a mostrar
                tipoGrafico = "Barras"; //Asignamos el texto "Barras" a la variable global "tipoGrafico"
                obtenerDatos(fechaSeleccionada, tipoGrafico); //Llamamos el método "obtenerDatos" para que cargue el gráfico correspondiente cuando el usuario seleccione una opción del PopupMenu. También le mandamos las variables globales "fechaSeleccionada" y "tipoGrafico"
                return true;

            case R.id.menuGraficoAnillo:
                imgGrafico.setImageResource(R.mipmap.ico_azul_donutchart); //Establecemos la imagen del gráfico de anillo en el selector del tipo de gráfico a mostrar
                tipoGrafico = "Anillo"; //Asignamos el texto "Anillo" a la variable global "tipoGrafico"
                obtenerDatos(fechaSeleccionada, tipoGrafico); //Llamamos el método "obtenerDatos" para que cargue el gráfico correspondiente cuando el usuario seleccione una opción del PopupMenu. También le mandamos las variables globales "fechaSeleccionada" y "tipoGrafico"
                return true;

            case R.id.menuGraficoLineas:
                imgGrafico.setImageResource(R.mipmap.ico_azul_linechart); //Establecemos la imagen del gráfico de lineas en el selector del tipo de gráfico a mostrar
                tipoGrafico = "Lineas"; //Asignamos el texto "Lineas" a la variable global "tipoGrafico"
                obtenerDatos(fechaSeleccionada, tipoGrafico); //Llamamos el método "obtenerDatos" para que cargue el gráfico correspondiente cuando el usuario seleccione una opción del PopupMenu. También le mandamos las variables globales "fechaSeleccionada" y "tipoGrafico"
                return true;

            default:
                return false;
        }
    }

    //Método que detecta el evento "OnTouchListener" de los tres gráficos, y bloquea el SwipeRefreshLayout "swlRecargar" cuando se esté manipulando algún gráfico
    private void desactivarSwipeEnGraficos() {
        //Objeto de tipo "OnTouchListener" que detecta cuando se está tocando la pantalla y en el algún elemento específico de la misma (ahorita lo usaremos para detectar cuando se toquen los gráficos)
        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: //Si se está realizando la acción de arrastrar hacia abajo
                        swlRecargar.setEnabled(false); //Bloqueamos el "swlRecargar"
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: //Si se está realizando la acción de arrastrar hacia arriba, y cuando se cancele el hecho de estar manipulando el gráfico
                        swlRecargar.setEnabled(true); //Habilitamos el "swlRecargar"
                        break;
                }
                return false;
            }
        };

        //Asignamos el listener a todos los gráficos
        graficoBarras.setOnTouchListener(listener);
        graficoAnillo.setOnTouchListener(listener);
        graficoLineas.setOnTouchListener(listener);
    }

    @Override
    public void onRefresh() { //Método que detecta cuando se recarga la pantalla con SwipeRefreshLayout
        //Creamos una nueva instancia de "Handler", que está vinculada al Looper principal (el hilo principal de la aplicación). Esto asegura que cualquier operación realizada dentro de este Handler se ejecute en el hilo principal
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { //El "Handler" utiliza el método "postDelayed" para ejecutar el "Runnable" que contiene las acciones a realizar después de un retraso especificado (en este caso, 1000 milisegundos, es decir, 1 segundo)
            @Override
            public void run() {
                Utilidades.mostrarMensajePorInternetCaido(EstadisticasGastosIngresos.this, viewNoInternet); //Llamamos el método utilitario "mostrarMensajePorInternetCaido" donde mandamos la vista "viewNoInternet" donde se hará visible cuando no haya conexión a internet y se ocultará cuando si haya
                obtenerDatos(fechaSeleccionada, tipoGrafico);
                swlRecargar.setRefreshing(false); //Llamamos a este método para detener la animación de refresco
            }
        }, 1000);
    }

    //Método que permite retroceder a la pantalla anterior
    public void retroceder(View view) {
        onBackPressed();
    }
}