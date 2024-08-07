package com.ingelcom.cajachica.Herramientas;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.ingelcom.cajachica.Modelos.GastosItems;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class Exportaciones {

    private Context contexto;

    public Exportaciones(Context contexto) {
        this.contexto = contexto;
    }

    public void exportarGastosExcel(List<GastosItems> listaGastos, String cuadrillaMes) {
        if (listaGastos == null || listaGastos.isEmpty()) {
            Toast.makeText(contexto, "NO HAY GASTOS DISPONIBLES PARA EXPORTAR", Toast.LENGTH_LONG).show();
            return;
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Gastos");

        // Crear fila para los encabezados
        Row headerRow = sheet.createRow(0);
        String[] encabezados = {"Cuadrilla", "Fecha y Hora", "Lugar de Compra", "Número de Factura", "Tipo de Compra", "Descripción", "Usuario", "Total"};
        for (int i = 0; i < encabezados.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(encabezados[i]);
        }

        // Crear filas para los datos
        int rowNum = 1;
        for (GastosItems gasto : listaGastos) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(gasto.getCuadrilla());
            row.createCell(1).setCellValue(gasto.getFechaHora());
            row.createCell(2).setCellValue(gasto.getLugarCompra());
            row.createCell(3).setCellValue(gasto.getNumeroFactura());
            row.createCell(4).setCellValue(gasto.getTipoCompra());
            row.createCell(5).setCellValue(gasto.getDescripcion());
            row.createCell(6).setCellValue(gasto.getUsuario());
            row.createCell(7).setCellValue(gasto.getTotal());
        }

        //Guardar el archivo Excel en el almacenamiento del dispositivo
        try {
            // Obtener la ruta de la carpeta Documents
            File documentsDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "INGELCOM - Reportes");

            // Crear la carpeta si no existe
            if (!documentsDir.exists()) {
                documentsDir.mkdirs();
            }

            File gastosDir = new File(documentsDir, "Gastos");
            if (!gastosDir.exists()) {
                gastosDir.mkdirs();
            }

            // Crear el archivo en la subcarpeta
            File file = new File(gastosDir, "Gastos.xlsx");
            FileOutputStream fos = new FileOutputStream(file);
            workbook.write(fos);
            fos.close();
            workbook.close();

            //Mostrar mensaje de éxito
            Toast.makeText(contexto, "ARCHIVO GUARDADO EN LA CARPETA DE DOCUMENTOS", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.w("CrearExcel", e);
            Toast.makeText(contexto, "ERROR AL GUARDAR EL ARCHIVO DE EXCEL", Toast.LENGTH_LONG).show();
        }
    }
}