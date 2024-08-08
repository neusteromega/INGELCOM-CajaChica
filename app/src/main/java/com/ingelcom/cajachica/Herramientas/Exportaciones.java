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
        String[] encabezados = {"Cuadrilla", "Fecha y Hora", "Lugar de Compra", "Usuario", "Número de Factura", "Tipo de Compra", "Descripción", "Total"};
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
            row.createCell(3).setCellValue(gasto.getUsuario());
            row.createCell(4).setCellValue(gasto.getNumeroFactura());
            row.createCell(5).setCellValue(gasto.getTipoCompra());
            row.createCell(6).setCellValue(gasto.getDescripcion());
            row.createCell(7).setCellValue(gasto.getTotal());
        }

        //Guardar el archivo Excel en el almacenamiento del dispositivo
        try {
            // Obtener la ruta de la carpeta Documents
            File documentsDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "INGELCOM - Reportes");
            if (!documentsDir.exists()) {
                documentsDir.mkdirs();
            }

            File gastosDir = new File(documentsDir, "Gastos");
            if (!gastosDir.exists()) {
                gastosDir.mkdirs();
            }

            String nombreArchivo = "Gastos" + cuadrillaMes + ".xlsx";

            // Crear el archivo en la subcarpeta
            File file = new File(gastosDir, nombreArchivo);
            FileOutputStream fos = new FileOutputStream(file);
            workbook.write(fos);
            fos.close();
            workbook.close();

            //Mostrar mensaje de éxito
            Toast.makeText(contexto, "ARCHIVO GUARDADO EN LA CARPETA DE DOCUMENTOS", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e("CrearExcel", "Error al crear el Excel", e);
            Toast.makeText(contexto, "ERROR AL CREAR EL ARCHIVO DE EXCEL", Toast.LENGTH_LONG).show();
        }
    }

    public void exportarGastosPDF(List<GastosItems> listaGastos, String cuadrillaMes) {
        if (listaGastos == null || listaGastos.isEmpty()) {
            Toast.makeText(contexto, "NO HAY GASTOS DISPONIBLES PARA EXPORTAR", Toast.LENGTH_LONG).show();
            return;
        }

        try {

        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e("CrearPDF", "Error al crear el PDF", e);
            Toast.makeText(contexto, "ERROR AL CREAR EL ARCHIVO PDF", Toast.LENGTH_LONG).show();
        }
    }
}