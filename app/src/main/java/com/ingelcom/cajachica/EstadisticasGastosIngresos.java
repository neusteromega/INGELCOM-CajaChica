package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.ingelcom.cajachica.DAO.Gasto;
import com.ingelcom.cajachica.DAO.Ingreso;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.Modelos.GastosItems;
import com.ingelcom.cajachica.Modelos.IngresosItems;

import java.util.ArrayList;
import java.util.List;

public class EstadisticasGastosIngresos extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private ImageView imgGrafico;
    private HorizontalBarChart graficoBarras;
    private String tipoDatos;

    private Gasto gast = new Gasto(EstadisticasGastosIngresos.this);
    private Ingreso ingr = new Ingreso(EstadisticasGastosIngresos.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas_gastosingresos);

        imgGrafico = findViewById(R.id.imgTipoGraficoEstGI);
        graficoBarras = findViewById(R.id.graficoBarrasEstGI);
        tipoDatos = Utilidades.obtenerStringExtra(this, "ActivityEGI");

        obtenerDatos("");
    }

    private void obtenerDatos(String mes) {
        if (tipoDatos.equalsIgnoreCase("Gastos")) {
            gast.obtenerGastos("", "", mes, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<GastosItems>() {
                @Override
                public void onCallback(List<GastosItems> items) {
                    if (items != null) {//Si "items" no es null, que entre al if
                        items = Utilidades.ordenarListaPorFechaHora(items, "fechaHora", "Descendente");
                        establecerGraficoHorizontal(items);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.w("ObtenerGastos", e);
                }
            });
        }
        else if (tipoDatos.equalsIgnoreCase("Ingresos")) {
            ingr.obtenerIngresos("", mes, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<IngresosItems>() {
                @Override
                public void onCallback(List<IngresosItems> items) {
                    if (items != null) {//Si "items" no es null, que entre al if
                        items = Utilidades.ordenarListaPorFechaHora(items, "fechaHora", "Descendente");
                        establecerGraficoHorizontal(items);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.w("ObtenerIngresos", e);
                }
            });
        }
    }

    private <T> void establecerGraficoHorizontal(List<T> items) {
        // Crear las entradas para el gráfico de barras
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            T item = items.get(i);

            if (item instanceof GastosItems) {
                GastosItems gasto = (GastosItems) item;
                barEntries.add(new BarEntry(i, (float) gasto.getTotal()));
                labels.add(gasto.getCuadrilla());
            }
            else if (item instanceof IngresosItems) {
                IngresosItems ingreso = (IngresosItems) item;
                barEntries.add(new BarEntry(i, (float) ingreso.getTotal()));
                labels.add(ingreso.getCuadrilla());
            }
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
        barData.setBarWidth(0.9f); // Ancho de las barras

        // Configurar el gráfico
        graficoBarras.setData(barData);
        graficoBarras.setFitBars(true);
        graficoBarras.getDescription().setEnabled(false);
        graficoBarras.animateY(1000);

        graficoBarras.getLegend().setEnabled(false);

        graficoBarras.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        graficoBarras.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        graficoBarras.getXAxis().setGranularity(1f);
        graficoBarras.getXAxis().setGranularityEnabled(true);
        graficoBarras.getXAxis().setDrawGridLines(false); //Eliminar lineas horizontales
        graficoBarras.getXAxis().setTextSize(8f); //Tamaño de texto de los Labels
        graficoBarras.getXAxis().setTextColor(getColor(R.color.clr_fuente_primario));
        graficoBarras.getXAxis().setXOffset(4f); // Ajusta el espaciado horizontal para evitar el corte
        graficoBarras.getXAxis().setLabelRotationAngle(45f);

        Typeface customTypeface = ResourcesCompat.getFont(this, R.font.montserrat_semibold); // Cambia "my_custom_font" por el nombre de tu fuente
        graficoBarras.getXAxis().setTypeface(customTypeface);

        graficoBarras.getAxisLeft().setAxisMinimum(0f); // Asegurar que comience desde 0
        graficoBarras.getAxisLeft().setTypeface(ResourcesCompat.getFont(this, R.font.montserrat_semibold));
        graficoBarras.getAxisLeft().setTextSize(8f);
        graficoBarras.getAxisLeft().setTextColor(getColor(R.color.clr_fuente_primario));
        graficoBarras.getAxisLeft().setDrawGridLines(true); // Asegúrate de que las líneas de cuadrícula estén habilitadas
        graficoBarras.getAxisLeft().setGridColor(Color.LTGRAY);

        graficoBarras.getAxisRight().setEnabled(false); // Deshabilitar el eje derecho

        graficoBarras.setExtraOffsets(5, 0, 10, 0); // Añadir espacio en los márgenes

        //graficoBarras.fitScreen();
        graficoBarras.invalidate(); // Refrescar el gráfico
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
                imgGrafico.setImageResource(R.mipmap.ico_azul_barchart);
                Toast.makeText(this, "BARCHART", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.menuGraficoPastel:
                imgGrafico.setImageResource(R.mipmap.ico_azul_piechart);
                Toast.makeText(this, "PIECHART", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.menuGraficoLineas:
                imgGrafico.setImageResource(R.mipmap.ico_azul_linechart);
                Toast.makeText(this, "LINECHART", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return false;
        }
    }
}