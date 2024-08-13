package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.ingelcom.cajachica.DAO.Gasto;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.Modelos.GastosItems;

import java.util.ArrayList;
import java.util.List;

public class EstadisticasGastosIngresos extends AppCompatActivity {

    private BarChart graficoBarras;

    private Gasto gast = new Gasto(EstadisticasGastosIngresos.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas_gastosingresos);

        graficoBarras = findViewById(R.id.graficoBarrasEstGI);

        obtenerDatos("");
    }

    private void obtenerDatos(String mes) {
        gast.obtenerGastos("", "", mes, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<GastosItems>() {
            @Override
            public void onCallback(List<GastosItems> items) {
                if (items != null) {//Si "items" no es null, que entre al if
                    items = Utilidades.ordenarListaPorFechaHora(items, "fechaHora", "Descendente");
                    establecerGrafico(items);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.w("ObtenerGastos", e);
            }
        });
    }

    private void establecerGrafico(List<GastosItems> items) {
        // Crear las entradas para el gráfico de barras
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            GastosItems item = items.get(i);
            barEntries.add(new BarEntry(i, (float) item.getTotal()));
            labels.add(item.getCuadrilla());
        }

        // Crear el conjunto de datos del gráfico
        BarDataSet barDataSet = new BarDataSet(barEntries, "Total Gastos por Cuadrilla");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        // Crear los datos para el gráfico
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f); // Ancho de las barras

        // Configurar el gráfico
        graficoBarras.setData(barData);
        graficoBarras.setFitBars(true);
        graficoBarras.getDescription().setEnabled(false);
        graficoBarras.animateY(1000);

        // Configurar el eje X para mostrar las etiquetas de "Cuadrilla"
        XAxis xAxis = graficoBarras.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.size());
        xAxis.setLabelRotationAngle(-45); // Rotar las etiquetas si son largas

        // Refrescar el gráfico
        graficoBarras.invalidate();
    }
}