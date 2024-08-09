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
        Sheet hoja = workbook.createSheet("Gastos"); //Creamos una nueva sheet (hoja de excel) llamada "Gastos"

        Row filaEncabezado = hoja.createRow(0); //Creamos una fila para los encabezados en la posición 0

        //Guardamos los encabezados en un arreglo
        String[] encabezados = {"Cuadrilla", "Fecha y Hora", "Lugar de Compra", "Usuario", "Número de Factura", "Tipo de Compra", "Descripción", "Total"};

        //For que recorre todos los nombres de los encabezados guardados en el arreglo
        for (int i = 0; i < encabezados.length; i++) {
            Cell celda = filaEncabezado.createCell(i); //Creamos una celda, la posición de la fila es la 0 (la posición 0 está guardada en la variable "filaEncabezado"), y la de la columna será el número "i" (que empieza en 0 y va aumentando hasta que salga del ciclo for)
            celda.setCellValue(encabezados[i]); //A la celda recién creada le asignamos el texto guardado en el arreglo "encabezados" en la posición "i"
        }

        int numFila = 1;

        //Foreach que recorre cada elemento de la "listaGastos" y lo va guardando en la variable temporal "gasto"
        for (GastosItems gasto : listaGastos) {
            Row fila = hoja.createRow(numFila++); //Por cada elemento de "listaGastos" creamos una nueva fila en la posición guardada en "numFila" la cual empieza en 1 (no empieza en 0, porque la fila 0 la tienen los encabezados) y va aumentando hasta que termine el ciclo foreach

            //Creamos las celdas para los datos a guardar en el archivo de Excel, la columna irá del 0 al 7 y en cada celda guardará los elementos que deseamos de la "listaGastos" y para acceder a esos elementos usamos la variable temporal "gasto"
            fila.createCell(0).setCellValue(gasto.getCuadrilla());
            fila.createCell(1).setCellValue(gasto.getFechaHora());
            fila.createCell(2).setCellValue(gasto.getLugarCompra());
            fila.createCell(3).setCellValue(gasto.getUsuario());
            fila.createCell(4).setCellValue(gasto.getNumeroFactura());
            fila.createCell(5).setCellValue(gasto.getTipoCompra());
            fila.createCell(6).setCellValue(gasto.getDescripcion());
            fila.createCell(7).setCellValue(gasto.getTotal());
        }

        //Guardar el archivo Excel en el almacenamiento del dispositivo
        try {
            //Obtenemos la ruta de la carpeta "Documentos", y en ella creamos la carpeta "INGELCOM - Reportes", esto lo guardamos en una variable de tipo "File" ("Archivo" en español) llamada "DocumentosDir"
            File documentosDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "INGELCOM - Reportes");
            if (!documentosDir.exists()) { //Si "documentsDir" no existe, que entre al if y lo cree
                documentosDir.mkdirs();
            }

            //La ruta guardada en "documentosDir" la utilizamos para crear en ella la carpeta "Gastos", esto lo guardamos en otra variable de tipo "File" ("Archivo" en español) llamada "GastosDir"
            File gastosDir = new File(documentosDir, "Gastos");
            if (!gastosDir.exists()) { //Si "gastosDir" no existe, que entre al if y lo cree
                gastosDir.mkdirs();
            }

            //Creamos el nombre del archivo de excel, el cual empieza con la palabra "Gastos", recibe el resto del nombre en la variable "cuadrillaMes" en el cual, con una expresión regular, se le eliminan los guiones y espacios para evitar conflictos en la creación del archivo. Y esto se concatena a ".xlsx" que es la extensión del archivo de excel
            String nombreArchivo = "Gastos" + cuadrillaMes.replaceAll("[- ]", "") + ".xlsx";

            File archivo = new File(gastosDir, nombreArchivo); //Usando otra variable de tipo "File" creamos el archivo en el directorio guardado en "gastosDir" y le pasamos el nombre del Excel que está guardado en "nombreArchivo"
            FileOutputStream fos = new FileOutputStream(archivo);
            workbook.write(fos);
            fos.close();
            workbook.close();

            Toast.makeText(contexto, "ARCHIVO GUARDADO EN LA CARPETA DE DOCUMENTOS", Toast.LENGTH_LONG).show(); //Mostramos mensaje de éxito
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e("CrearExcel", "Error al crear el Excel", e);
            Toast.makeText(contexto, "ERROR AL CREAR EL ARCHIVO DE EXCEL", Toast.LENGTH_LONG).show(); //Mostramos mensaje de error
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