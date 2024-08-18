package com.ingelcom.cajachica.Herramientas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.ingelcom.cajachica.Modelos.GastosItems;
import com.ingelcom.cajachica.Modelos.IngresosItems;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class Exportaciones {

    private Context contexto;

    public Exportaciones(Context contexto) {
        this.contexto = contexto;
    }

    public void guardarImagen(ImageView imagen, String tipo, String nombreImagen) {
        if (imagen == null) {
            Toast.makeText(contexto, "IMAGEN INVÁLIDA", Toast.LENGTH_SHORT).show();
            return;
        }

        BitmapDrawable drawable = (BitmapDrawable) imagen.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        if (tipo.equalsIgnoreCase("Ingreso")) {
            File directorio = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "INGELCOM_Facturas/Ingresos");

            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            String nombreArchivo = nombreImagen + ".jpg";
            File archivoImagen = new File(directorio, nombreArchivo);

            try (FileOutputStream out = new FileOutputStream(archivoImagen)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void exportarGastosExcel(List<GastosItems> listaGastos, String cuadrillaMes) {
        if (listaGastos == null || listaGastos.isEmpty()) { //Antes de crear el archivo Excel, verificamos que la lista de gastos no es nula o está vacía
            Toast.makeText(contexto, "NO HAY GASTOS DISPONIBLES PARA EXPORTAR", Toast.LENGTH_LONG).show();
            return;
        }

        XSSFWorkbook workbook = new XSSFWorkbook(); //Creamos una instancia de "Workbook" utilizando la clase "XSSFWorkbook", que es la implementación de Apache POI para archivos ".xlsx"
        XSSFSheet hoja = workbook.createSheet("Gastos"); //Creamos una nueva sheet (hoja de excel) llamada "Gastos"

        XSSFRow filaEncabezado = hoja.createRow(0); //Creamos una fila para los encabezados en la posición 0

        //Guardamos los encabezados en un arreglo
        String[] encabezados = {"Cuadrilla", "Fecha y Hora", "Lugar de Compra", "Usuario", "Número de Factura", "Tipo de Compra", "Descripción", "Total"};

        //For que recorre todos los nombres de los encabezados guardados en el arreglo
        for (int i = 0; i < encabezados.length; i++) {
            XSSFCell celda = filaEncabezado.createCell(i); //Creamos una celda, la posición de la fila es la 0 (la posición 0 está guardada en la variable "filaEncabezado"), y la de la columna será el número "i" (que empieza en 0 y va aumentando hasta que salga del ciclo for)
            celda.setCellValue(encabezados[i]); //A la celda recién creada le asignamos el texto guardado en el arreglo "encabezados" en la posición "i"
        }

        int numFila = 1; //Inicializamos "numFila" en 1 para comenzar a llenar los datos desde la segunda fila (ya que la primera fila está ocupada por los encabezados)

        //Foreach que recorre cada elemento de la "listaGastos" y lo va guardando en la variable temporal "gasto"
        for (GastosItems gasto : listaGastos) {
            XSSFRow fila = hoja.createRow(numFila++); //Por cada elemento de "listaGastos" creamos una nueva fila en la posición guardada en "numFila" la cual empieza en 1 (no empieza en 0, porque la fila 0 la tienen los encabezados) y va aumentando hasta que termine el ciclo foreach

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

        try {
            //Obtenemos la ruta de la carpeta "Documentos", y en ella creamos la carpetas "INGELCOM_Reportes/Gastos/EXCEL", esto lo guardamos en una variable de tipo "File" ("Archivo" en español) llamada "directorio"
            File directorio = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "INGELCOM_Reportes/Gastos/EXCEL");
            if (!directorio.exists()) { //Si "directorio" no existe, que entre al if y lo cree
                directorio.mkdirs();
            }

            //Creamos el nombre del archivo de excel, el cual empieza con la palabra "Gastos", recibe el resto del nombre en la variable "cuadrillaMes" en el cual, con una expresión regular, se le eliminan los guiones y espacios para evitar conflictos en la creación del archivo. Y esto se concatena a ".xlsx" que es la extensión del archivo de excel. El nombre del archivo lo concatenamos con "directorio.getPath()" que tiene la ruta con las carpetas "INGELCOM_Reportes/Gastos/EXCEL"
            String rutaArchivo = directorio.getPath() + ("/Gastos" + cuadrillaMes.replaceAll("[- ]", "") + ".xlsx");

            FileOutputStream fileOut = new FileOutputStream(new File(rutaArchivo));
            workbook.write(fileOut); //Escribimos el contenido del "Workbook" usando el "FileOutputStream"
            fileOut.flush();

            Toast.makeText(contexto, "ARCHIVO GUARDADO EN LA CARPETA DE DOCUMENTOS", Toast.LENGTH_LONG).show(); //Mostramos mensaje de éxito*//*
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e("CrearExcel", "Error al crear el Excel", e);
            Toast.makeText(contexto, "ERROR AL CREAR EL ARCHIVO DE EXCEL", Toast.LENGTH_LONG).show(); //Mostramos mensaje de error
        }
        finally {
            try {
                workbook.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void exportarGastosPDF(List<GastosItems> listaGastos, String cuadrillaMes) {
        if (listaGastos == null || listaGastos.isEmpty()) { //Antes de crear el archivo PDF, verificamos que la lista de gastos no es nula o está vacía
            Toast.makeText(contexto, "NO HAY GASTOS DISPONIBLES PARA EXPORTAR", Toast.LENGTH_LONG).show();
            return;
        }

        Document document = new Document(PageSize.LETTER.rotate()); //Creamos una instancia de "Document" de iText, con un tamaño de página de carta (Letter) en orientación horizontal (rotate())

        try {
            //Obtenemos la ruta de la carpeta "Documentos", y en ella creamos la carpeta "INGELCOM_Reportes/Gastos/PDF", esto lo guardamos en una variable de tipo "File" ("Archivo" en español) llamada "carpeta"
            File carpeta = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "INGELCOM_Reportes/Gastos/PDF");
            if (!carpeta.exists()) { //Si "carpeta" no existe, que entre al if y lo cree
                carpeta.mkdirs();
            }

            //Creamos el nombre del archivo PDF, el cual empieza con la palabra "Gastos", recibe el resto del nombre en la variable "cuadrillaMes" en el cual, con una expresión regular, se le eliminan los guiones y espacios para evitar conflictos en la creación del archivo. Y esto se concatena a ".pdf" que es la extensión del archivo de PDF
            String nombreArchivo = "Gastos" + cuadrillaMes.replaceAll("[- ]", "") + ".pdf";

            File archivo = new File(carpeta, nombreArchivo); //Usando otra variable de tipo "File" creamos el archivo en el directorio guardado en "carpeta" y le pasamos el nombre del PDF que está guardado en "nombreArchivo"
            FileOutputStream fos = new FileOutputStream(archivo); //Inicializamos un "FileOutputStream" para escribir en "archivo"

            PdfWriter.getInstance(document, fos); //Asociamos el "PdfWriter" con el "Document" y el "FileOutputStream" para escribir en el archivo
            document.open(); //Abrimos el "document"

            Font fuenteEncabezado = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD); //Definimos una fuente (Font) para los encabezados de la tabla, utilizando la familia de fuentes Helvetica, un tamaño de 12 puntos y un estilo en negrita (BOLD)

            PdfPTable table = new PdfPTable(8); //Creamos una tabla con "PdfPTable" y le establecemos 8 columnas
            table.setWidthPercentage(100); //Establecemos que la tabla ocupará el 100% del ancho de la página

            float[] columnWidths = {1.5f, 1.2f, 1.5f, 1.5f, 1.5f, 1.3f, 2f, 0.8f}; //Establecemos los anchos relativos de las columnas. Los valores en el arreglo "columnWidths" representan la proporción del ancho total de la tabla que cada columna debe ocupar
            table.setWidths(columnWidths); //Asignamos los anchos a la tabla

            //Guardamos los encabezados en un arreglo
            String[] encabezados = {"Cuadrilla", "Fecha y Hora", "Lugar de Compra", "Usuario", "Número de Factura", "Tipo de Compra", "Descripción", "Total"};

            //Foreach que recorre todos los elementos del arreglo "encabezados" y los va guardando en la variable temporal "encabezado"
            for (String encabezado : encabezados) {
                PdfPCell cell = new PdfPCell(new Phrase(encabezado, fuenteEncabezado)); //Creamos una celda para cada encabezado al recorrer este foreach, y a esta celda establecemos el texto guardado en la variable temporal "encabezado" y la fuente guardada en "fuenteEncabezado"
                cell.setHorizontalAlignment(Element.ALIGN_CENTER); //Centramos horizontalmente el texto de la celda
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE); //Centramos verticalmente el texto de la celda
                table.addCell(cell); //Agregamos la celda a la tabla
            }

            Font fuenteDatos = new Font(Font.FontFamily.HELVETICA, 10); //Definimos una fuente (Font) para los datos de la tabla, utilizando la familia de fuentes Helvetica, un tamaño de 10 puntos

            //Foreach que recorre cada elemento de la "listaGastos" y lo va guardando en la variable temporal "gasto"
            for (GastosItems gasto : listaGastos) {
                //Llenamos las celdas de la tabla con los valores correspondientes, usando un método crearCelda de una clase Utilidades para configurar la alineación del texto (centrado o alineado a la izquierda según el contenido)
                table.addCell(Utilidades.crearCelda(gasto.getCuadrilla(), fuenteDatos, "Centro"));
                table.addCell(Utilidades.crearCelda(gasto.getFechaHora(), fuenteDatos, "Centro"));
                table.addCell(Utilidades.crearCelda(gasto.getLugarCompra(), fuenteDatos, "Centro"));
                table.addCell(Utilidades.crearCelda(gasto.getUsuario(), fuenteDatos, "Centro"));
                table.addCell(Utilidades.crearCelda(gasto.getNumeroFactura(), fuenteDatos, "Centro"));
                table.addCell(Utilidades.crearCelda(gasto.getTipoCompra(), fuenteDatos, "Centro"));
                table.addCell(Utilidades.crearCelda(gasto.getDescripcion(), fuenteDatos, "Izquierda"));
                table.addCell(Utilidades.crearCelda(String.valueOf(gasto.getTotal()), fuenteDatos, "Centro"));
            }

            document.add(table); //Añadimos la tabla completa al documento PDF
            document.close(); //Cerramos el documento PDF finalizando la escritura del archivo

            Toast.makeText(contexto, "ARCHIVO GUARDADO EN LA CARPETA DE DOCUMENTOS", Toast.LENGTH_LONG).show(); //Mostramos mensaje de éxito
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e("CrearPDF", "Error al crear el PDF", e);
            Toast.makeText(contexto, "ERROR AL CREAR EL ARCHIVO PDF", Toast.LENGTH_LONG).show(); //Mostramos mensaje de error
        }
    }

    public void exportarIngresosExcel(List<IngresosItems> listaIngresos, String cuadrillaMes) {
        if (listaIngresos == null || listaIngresos.isEmpty()) { //Antes de crear el archivo Excel, verificamos que la lista de ingresos no es nula o está vacía
            Toast.makeText(contexto, "NO HAY INGRESOS DISPONIBLES PARA EXPORTAR", Toast.LENGTH_LONG).show();
            return;
        }

        XSSFWorkbook workbook = new XSSFWorkbook(); //Creamos una instancia de "Workbook" utilizando la clase "XSSFWorkbook", que es la implementación de Apache POI para archivos ".xlsx"
        XSSFSheet hoja = workbook.createSheet("Ingresos"); //Creamos una nueva sheet (hoja de excel) llamada "Ingresos"

        XSSFRow filaEncabezado = hoja.createRow(0); //Creamos una fila para los encabezados en la posición 0

        //Guardamos los encabezados en un arreglo
        String[] encabezados = {"Cuadrilla", "Fecha y Hora", "Usuario", "No. Transferencia", "Total"};

        //For que recorre todos los nombres de los encabezados guardados en el arreglo
        for (int i = 0; i < encabezados.length; i++) {
            XSSFCell celda = filaEncabezado.createCell(i); //Creamos una celda, la posición de la fila es la 0 (la posición 0 está guardada en la variable "filaEncabezado"), y la de la columna será el número "i" (que empieza en 0 y va aumentando hasta que salga del ciclo for)
            celda.setCellValue(encabezados[i]); //A la celda recién creada le asignamos el texto guardado en el arreglo "encabezados" en la posición "i"
        }

        int numFila = 1; //Inicializamos "numFila" en 1 para comenzar a llenar los datos desde la segunda fila (ya que la primera fila está ocupada por los encabezados)

        //Foreach que recorre cada elemento de la "listaIngresos" y lo va guardando en la variable temporal "ingreso"
        for (IngresosItems ingreso : listaIngresos) {
            XSSFRow fila = hoja.createRow(numFila++); //Por cada elemento de "listaIngresos" creamos una nueva fila en la posición guardada en "numFila" la cual empieza en 1 (no empieza en 0, porque la fila 0 la tienen los encabezados) y va aumentando hasta que termine el ciclo foreach

            //Creamos las celdas para los datos a guardar en el archivo de Excel, la columna irá del 0 al 4 y en cada celda guardará los elementos que deseamos de la "listaGastos" y para acceder a esos elementos usamos la variable temporal "gasto"
            fila.createCell(0).setCellValue(ingreso.getCuadrilla());
            fila.createCell(1).setCellValue(ingreso.getFechaHora());
            fila.createCell(2).setCellValue(ingreso.getUsuario());
            fila.createCell(3).setCellValue(ingreso.getTransferencia());
            fila.createCell(4).setCellValue(ingreso.getTotal());
        }

        try {
            //Obtenemos la ruta de la carpeta "Documentos", y en ella creamos la carpeta "INGELCOM - Reportes", esto lo guardamos en una variable de tipo "File" ("Archivo" en español) llamada "DocumentosDir"
            File directorio = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "INGELCOM_Reportes/Ingresos/EXCEL");
            if (!directorio.exists()) { //Si "directorio" no existe, que entre al if y lo cree
                directorio.mkdirs();
            }

            //Creamos el nombre del archivo de excel, el cual empieza con la palabra "Ingresos", recibe el resto del nombre en la variable "cuadrillaMes" en el cual, con una expresión regular, se le eliminan los guiones y espacios para evitar conflictos en la creación del archivo. Y esto se concatena a ".xlsx" que es la extensión del archivo de excel. El nombre del archivo lo concatenamos con "directorio.getPath()" que tiene la ruta con las carpetas "INGELCOM_Reportes/Ingresos/EXCEL"
            String rutaArchivo = directorio.getPath() + ("/Ingresos" + cuadrillaMes.replaceAll("[- ]", "") + ".xlsx");

            FileOutputStream fileOut = new FileOutputStream(new File(rutaArchivo));
            workbook.write(fileOut); //Escribimos el contenido del "Workbook" usando el "FileOutputStream"
            fileOut.flush();

            Toast.makeText(contexto, "ARCHIVO GUARDADO EN LA CARPETA DE DOCUMENTOS", Toast.LENGTH_LONG).show(); //Mostramos mensaje de éxito
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e("CrearExcel", "Error al crear el Excel", e);
            Toast.makeText(contexto, "ERROR AL CREAR EL ARCHIVO DE EXCEL", Toast.LENGTH_LONG).show(); //Mostramos mensaje de error
        }
        finally {
            try {
                workbook.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void exportarIngresosPDF(List<IngresosItems> listaIngresos, String cuadrillaMes) {
        if (listaIngresos == null || listaIngresos.isEmpty()) { //Antes de crear el archivo Excel, verificamos que la lista de ingresos no es nula o está vacía
            Toast.makeText(contexto, "NO HAY INGRESOS DISPONIBLES PARA EXPORTAR", Toast.LENGTH_LONG).show();
            return;
        }

        Document document = new Document(PageSize.LETTER.rotate()); //Creamos una instancia de "Document" de iText, con un tamaño de página de carta (Letter) en orientación horizontal (rotate())

        try {
            //Obtenemos la ruta de la carpeta "Documentos", y en ella creamos la carpeta "INGELCOM_Reportes/Ingresos/PDF", esto lo guardamos en una variable de tipo "File" ("Archivo" en español) llamada "carpeta"
            File carpeta = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "INGELCOM_Reportes/Ingresos/PDF");
            if (!carpeta.exists()) { //Si "carpeta" no existe, que entre al if y lo cree
                carpeta.mkdirs();
            }

            //Creamos el nombre del archivo PDF, el cual empieza con la palabra "Ingresos", recibe el resto del nombre en la variable "cuadrillaMes" en el cual, con una expresión regular, se le eliminan los guiones y espacios para evitar conflictos en la creación del archivo. Y esto se concatena a ".pdf" que es la extensión del archivo de PDF
            String nombreArchivo = "Ingresos" + cuadrillaMes.replaceAll("[- ]", "") + ".pdf";

            File archivo = new File(carpeta, nombreArchivo); //Usando otra variable de tipo "File" creamos el archivo en el directorio guardado en "carpeta" y le pasamos el nombre del PDF que está guardado en "nombreArchivo"
            FileOutputStream fos = new FileOutputStream(archivo); //Inicializamos un "FileOutputStream" para escribir en "archivo"

            PdfWriter.getInstance(document, fos); //Asociamos el "PdfWriter" con el "Document" y el "FileOutputStream" para escribir en el archivo
            document.open(); //Abrimos el "document"

            Font fuenteEncabezado = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD); //Definimos una fuente (Font) para los encabezados de la tabla, utilizando la familia de fuentes Helvetica, un tamaño de 12 puntos y un estilo en negrita (BOLD)

            PdfPTable table = new PdfPTable(5); //Creamos una tabla con "PdfPTable" y le establecemos 8 columnas
            table.setWidthPercentage(100); //Establecemos que la tabla ocupará el 100% del ancho de la página

            float[] columnWidths = {1.3f, 1.2f, 1.8f, 1.3f, 0.8f}; //Establecemos los anchos relativos de las columnas. Los valores en el arreglo "columnWidths" representan la proporción del ancho total de la tabla que cada columna debe ocupar
            table.setWidths(columnWidths); //Asignamos los anchos a la tabla

            //Guardamos los encabezados en un arreglo
            String[] encabezados = {"Cuadrilla", "Fecha y Hora", "Usuario", "No. Transferencia", "Total"};

            //Foreach que recorre todos los elementos del arreglo "encabezados" y los va guardando en la variable temporal "encabezado"
            for (String encabezado : encabezados) {
                PdfPCell cell = new PdfPCell(new Phrase(encabezado, fuenteEncabezado)); //Creamos una celda para cada encabezado al recorrer este foreach, y a esta celda establecemos el texto guardado en la variable temporal "encabezado" y la fuente guardada en "fuenteEncabezado"
                cell.setHorizontalAlignment(Element.ALIGN_CENTER); //Centramos horizontalmente el texto de la celda
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE); //Centramos verticalmente el texto de la celda
                table.addCell(cell); //Agregamos la celda a la tabla
            }

            Font fuenteDatos = new Font(Font.FontFamily.HELVETICA, 10); //Definimos una fuente (Font) para los datos de la tabla, utilizando la familia de fuentes Helvetica, un tamaño de 10 puntos

            //Foreach que recorre cada elemento de la "listaIngresos" y lo va guardando en la variable temporal "ingreso"
            for (IngresosItems ingreso : listaIngresos) {
                //Llenamos las celdas de la tabla con los valores correspondientes, usando un método crearCelda de una clase Utilidades para configurar la alineación del texto (centrado o alineado a la izquierda según el contenido)
                table.addCell(Utilidades.crearCelda(ingreso.getCuadrilla(), fuenteDatos, "Centro"));
                table.addCell(Utilidades.crearCelda(ingreso.getFechaHora(), fuenteDatos, "Centro"));
                table.addCell(Utilidades.crearCelda(ingreso.getUsuario(), fuenteDatos, "Centro"));
                table.addCell(Utilidades.crearCelda(ingreso.getTransferencia(), fuenteDatos, "Centro"));
                table.addCell(Utilidades.crearCelda(String.valueOf(ingreso.getTotal()), fuenteDatos, "Centro"));
            }

            document.add(table); //Añadimos la tabla completa al documento PDF
            document.close(); //Cerramos el documento PDF finalizando la escritura del archivo

            Toast.makeText(contexto, "ARCHIVO GUARDADO EN LA CARPETA DE DOCUMENTOS", Toast.LENGTH_LONG).show(); //Mostramos mensaje de éxito
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e("CrearPDF", "Error al crear el PDF", e);
            Toast.makeText(contexto, "ERROR AL CREAR EL ARCHIVO PDF", Toast.LENGTH_LONG).show(); //Mostramos mensaje de error
        }
    }
}