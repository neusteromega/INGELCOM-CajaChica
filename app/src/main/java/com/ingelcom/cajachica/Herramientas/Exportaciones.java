package com.ingelcom.cajachica.Herramientas;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class Exportaciones {

    private Context contexto;

    public Exportaciones(Context contexto) {
        this.contexto = contexto;
    }

    //Método que permite descargar una imagen de un gasto o ingreso
    public void guardarImagen(ImageView imagen, String tipo, String nombreImagen) {
        if (imagen == null) { //Si el ImageView recibido es nulo, que muestre un mensaje indicándolo y finalice el método
            Toast.makeText(contexto, "IMAGEN INVÁLIDA", Toast.LENGTH_SHORT).show();
            return;
        }

        FileOutputStream fileOutputStream = null; //Creamos un flujo de salida (FileOutputStream)
        File directorio = null;

        if (tipo.equalsIgnoreCase("Ingreso")) { //Si "tipo" contiene la palabra "Ingreso", que entre al if para crear la carpeta "Ingresos" donde se almacenarán las fotos
            directorio = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "INGELCOM_Facturas/Ingresos");
        }
        else if (tipo.equalsIgnoreCase("Gasto")) { //Si "tipo" contiene la palabra "Gasto", que entre al if para crear la carpeta "Gastos" donde se almacenarán las fotos
            directorio = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "INGELCOM_Facturas/Gastos");
        }

        if (!directorio.exists() && !directorio.mkdirs()) { //Si "directorio" aún no existe, que entre al if y lo cree
            directorio.mkdirs();
        }

        String nombre = nombreImagen + ".jpg"; //Guardamos el nombre de la imagen con la extensión .jpg
        String rutaArchivo = directorio.getAbsolutePath() + "/" + nombre; //Guardamos la ruta del archivo guardada en "directorio.getAbsolutePath" y la concatenamos con el nombre del archivo
        File nuevoArchivo = new File(rutaArchivo); //Creamos una nueva variable "File" donde guardamos la ruta completa del archivo

        int contador = 1; //Contador que nos ayudará a establecer un nombre diferente al archivo guardado en "nuevoArchivo" sólo si este ya se encuentra en el almacenamiento interno
        while (nuevoArchivo.exists()) { //Mientras "nuevoArchivo" exista en el mismo almacenamiento interno, que no salga del while
            nombre = nombreImagen + "(" + contador + ").jpg"; //Como "nuevoArchivo" existe, agregamos un número al nombre del archivo para que dicho nombre no se repita
            nuevoArchivo = new File(directorio, nombre); //Guardamos el directorio y el nuevo nombre en el "nuevoArchivo"
            contador++; //Aumentamos un número al contador para que si no sale del "while", el archivo tenga el siguiente número en su nombre
        }

        try {
            BitmapDrawable draw = (BitmapDrawable) imagen.getDrawable(); //Creamos un objeto de tipo "BitmapDrawable" donde guardamos el "drawable" del ImageView "imagen"
            Bitmap bitmap = draw.getBitmap(); //Creamos un "Bitmap" del objeto "draw" con la imagen. Un Bitmap representa la imagen en un formato que se puede manipular o guardar, lo cual no es posible directamente con un Drawable, por eso se debe convertir
            fileOutputStream = new FileOutputStream(nuevoArchivo); //Con "fileOutputStream" inicializamos el flujo de salida para escribir datos en el "nuevoArchivo"
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream); //Comprimimos el Bitmap y lo escribimos en el archivo especificado por "fileOutputStream". Recibe el formato de compresión (Bitmap.CompressFormat.JPEG en este caso), y la calidad de la compresión, que varía de 0 a 100 (donde 100 significa calidad máxima sin pérdida)

            crearNotificacionImagen(nuevoArchivo, nombre); //Llamamos el método de abajo para crear la notificación de la descarga
            Toast.makeText(contexto, "IMAGEN GUARDADA EN LA CARPETA DE IMÁGENES", Toast.LENGTH_LONG).show(); //Mostramos mensaje de éxito

            fileOutputStream.flush(); //Garantizamos que todos los datos en el buffer del "FileOutputStream" se escriban físicamente en el archivo
            fileOutputStream.close(); //Cerramos el flujo de salida
        }
        catch (FileNotFoundException e) {
            Log.e("GuardarImagen", "Error guardando la imagen", e);
            Toast.makeText(contexto, "ERROR AL GUARDAR LA IMAGEN", Toast.LENGTH_LONG).show(); //Mostramos mensaje de error
        }
        catch (IOException e) {
            Log.e("GuardarImagen", "Error guardando la imagen", e);
            Toast.makeText(contexto, "ERROR AL GUARDAR LA IMAGEN", Toast.LENGTH_LONG).show(); //Mostramos mensaje de error
        }

        //Esto permite que la imagen aparezca inmediatamente en la galería del dispositivo
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(nuevoArchivo));
        contexto.sendBroadcast(intent);
    }

    //Método que permite exportar un listado de gastos a Excel
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
            //Obtenemos la ruta de la carpeta "Documentos", y en ella creamos las carpetas "INGELCOM_Reportes/Gastos/EXCEL", esto lo guardamos en una variable de tipo "File" ("Archivo" en español) llamada "directorio"
            File directorio = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "INGELCOM_Reportes/Gastos/EXCEL");
            if (!directorio.exists() && !directorio.mkdirs()) { //Si "directorio" no existe, que entre al if y lo cree
                directorio.mkdirs();
            }

            String nombre = "Gastos" + cuadrillaMes.replaceAll("[- ]", ""); //Creamos el nombre del archivo de excel, el cual empieza con la palabra "Gastos", recibe el resto del nombre en la variable "cuadrillaMes" en el cual, con una expresión regular, se le eliminan los guiones y espacios para evitar conflictos en la creación del archivo
            String nombreXlsx = nombre + ".xlsx"; //Concatenamos el nombre con ".xlsx" que es la extensión del archivo de excel
            String rutaArchivo = directorio.getAbsolutePath() + "/" + nombreXlsx; //El nombre del archivo con el ".xlsx" lo concatenamos con "directorio.getAbsolutePath()" que tiene la ruta con las carpetas "INGELCOM_Reportes/Gastos/EXCEL", y con un separador de archivos "/"
            File nuevoArchivo = new File(rutaArchivo); //Guardamos la ruta completa del archivo en una variable de tipo File

            int contador = 1; //Contador que nos ayudará a establecer un nombre diferente al archivo guardado en "nuevoArchivo" sólo si este ya se encuentra en el almacenamiento interno
            while (nuevoArchivo.exists()) { //Mientras "nuevoArchivo" exista en el mismo almacenamiento interno, que no salga del while
                nombreXlsx = nombre + "(" + contador + ").xlsx"; //Como "nuevoArchivo" existe, agregamos un número al nombre del archivo para que dicho nombre no se repita
                nuevoArchivo = new File(directorio, nombreXlsx); //Guardamos el directorio y el nuevo nombre en el "nuevoArchivo"
                contador++; //Aumentamos un número al contador para que si no sale del "while", el archivo tenga el siguiente número en su nombre
            }

            FileOutputStream fileOut = new FileOutputStream(nuevoArchivo); //Creamos un flujo de salida (FileOutputStream) para escribir datos en un archivo específico (nuevoArchivo)
            workbook.write(fileOut); //Escribimos el contenido en el "Workbook" usando el "FileOutputStream"
            fileOut.flush(); //Garantizamos que todos los datos en el buffer del "FileOutputStream" se escriban físicamente en el archivo
            fileOut.close(); //Cerramos el flujo de salida

            crearNotificacionExcel(nuevoArchivo, nombreXlsx); //Llamamos el método de abajo para crear la notificación de la descarga
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

    //Método que permite exportar un listado de gastos a PDF
    public void exportarGastosPDF(List<GastosItems> listaGastos, String cuadrillaMes) {
        if (listaGastos == null || listaGastos.isEmpty()) { //Antes de crear el archivo PDF, verificamos que la lista de gastos no es nula o está vacía
            Toast.makeText(contexto, "NO HAY GASTOS DISPONIBLES PARA EXPORTAR", Toast.LENGTH_LONG).show();
            return;
        }

        Document document = new Document(PageSize.LETTER.rotate()); //Creamos una instancia de "Document" de iText, con un tamaño de página de carta (Letter) en orientación horizontal (rotate())

        try {
            //Obtenemos la ruta de la carpeta "Documentos", y en ella creamos la carpeta "INGELCOM_Reportes/Gastos/PDF", esto lo guardamos en una variable de tipo "File" ("Archivo" en español) llamada "directorio"
            File directorio = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "INGELCOM_Reportes/Gastos/PDF");
            if (!directorio.exists()) { //Si "carpeta" no existe, que entre al if y lo cree
                directorio.mkdirs();
            }

            String nombre = "Gastos" + cuadrillaMes.replaceAll("[- ]", ""); //Creamos el nombre del archivo PDF, el cual empieza con la palabra "Gastos", recibe el resto del nombre en la variable "cuadrillaMes" en el cual, con una expresión regular, se le eliminan los guiones y espacios para evitar conflictos en la creación del archivo
            String nombrePdf = nombre + ".pdf"; //Concatenamos el nombre con ".pdf" que es la extensión del archivo PDF
            String rutaArchivo = directorio.getAbsolutePath() + "/" + nombrePdf; //El nombre del archivo con el ".pdf" lo concatenamos con "directorio.getAbsolutePath()" que tiene la ruta con las carpetas "INGELCOM_Reportes/Gastos/PDF", y con un separador de archivos "/"
            File nuevoArchivo = new File(rutaArchivo); //Guardamos la ruta completa del archivo en una variable de tipo File

            int contador = 1; //Contador que nos ayudará a establecer un nombre diferente al archivo guardado en "nuevoArchivo" sólo si este ya se encuentra en el almacenamiento interno
            while (nuevoArchivo.exists()) { //Mientras "nuevoArchivo" exista en el mismo almacenamiento interno, que no salga del while
                nombrePdf = nombre + "(" + contador + ").pdf"; //Como "nuevoArchivo" existe, agregamos un número al nombre del archivo para que dicho nombre no se repita
                nuevoArchivo = new File(directorio, nombrePdf); //Guardamos el directorio y el nuevo nombre en el "nuevoArchivo"
                contador++; //Aumentamos un número al contador para que si no sale del "while", el archivo tenga el siguiente número en su nombre
            }

            FileOutputStream fos = new FileOutputStream(nuevoArchivo); //Creamos un flujo de salida (FileOutputStream) para escribir datos en un archivo específico (nuevoArchivo)

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
                //Llenamos las celdas de la tabla con los valores correspondientes, usando un método crearCelda de la clase Utilidades para configurar la alineación del texto (centrado o alineado a la izquierda según el contenido)
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
            fos.close(); //Cerramos el flujo de salida

            crearNotificacionPDF(nuevoArchivo, nombrePdf); //Llamamos el método de abajo para crear la notificación de la descarga
            Toast.makeText(contexto, "ARCHIVO GUARDADO EN LA CARPETA DE DOCUMENTOS", Toast.LENGTH_LONG).show(); //Mostramos mensaje de éxito
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e("CrearPDF", "Error al crear el PDF", e);
            Toast.makeText(contexto, "ERROR AL CREAR EL ARCHIVO PDF", Toast.LENGTH_LONG).show(); //Mostramos mensaje de error
        }
    }

    //Método que permite exportar un listado de ingresos a Excel
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

            //Creamos las celdas para los datos a guardar en el archivo de Excel, la columna irá del 0 al 4 y en cada celda guardará los elementos que deseamos de la "listaIngresos" y para acceder a esos elementos usamos la variable temporal "ingreso"
            fila.createCell(0).setCellValue(ingreso.getCuadrilla());
            fila.createCell(1).setCellValue(ingreso.getFechaHora());
            fila.createCell(2).setCellValue(ingreso.getUsuario());
            fila.createCell(3).setCellValue(ingreso.getTransferencia());
            fila.createCell(4).setCellValue(ingreso.getTotal());
        }

        try {
            //Obtenemos la ruta de la carpeta "Documentos", y en ella creamos la carpeta "INGELCOM_Reportes/Ingresos/EXCEL", esto lo guardamos en una variable de tipo "File" ("Archivo" en español)
            File directorio = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "INGELCOM_Reportes/Ingresos/EXCEL");
            if (!directorio.exists()) { //Si "directorio" no existe, que entre al if y lo cree
                directorio.mkdirs();
            }

            String nombre = "Ingresos" + cuadrillaMes.replaceAll("[- ]", ""); //Creamos el nombre del archivo de excel, el cual empieza con la palabra "Ingresos", recibe el resto del nombre en la variable "cuadrillaMes" en el cual, con una expresión regular, se le eliminan los guiones y espacios para evitar conflictos en la creación del archivo
            String nombreXlsx = nombre + ".xlsx"; //Concatenamos el nombre con ".xlsx" que es la extensión del archivo de excel
            String rutaArchivo = directorio.getAbsolutePath() + "/" + nombreXlsx; //El nombre del archivo con el ".xlsx" lo concatenamos con "directorio.getAbsolutePath()" que tiene la ruta con las carpetas "INGELCOM_Reportes/Ingresos/EXCEL", y con un separador de archivos "/"
            File nuevoArchivo = new File(rutaArchivo); //Guardamos la ruta completa del archivo en una variable de tipo File

            int contador = 1; //Contador que nos ayudará a establecer un nombre diferente al archivo guardado en "nuevoArchivo" sólo si este ya se encuentra en el almacenamiento interno
            while (nuevoArchivo.exists()) { //Mientas "nuevoArchivo" exista en el mismo almacenamiento interno, que no salga del while
                nombreXlsx = nombre + "(" + contador + ").xlsx"; //Como "nuevoArchivo" existe, agregamos un número al nombre del archivo para que dicho nombre no se repita
                nuevoArchivo = new File(directorio, nombreXlsx); //Guardamos el directorio y el nuevo nombre en el "nuevoArchivo"
                contador++; //Aumentamos un número al contador para que si no sale del "while", el archivo tenga el siguiente número en su nombre
            }

            FileOutputStream fileOut = new FileOutputStream(nuevoArchivo); //Creamos un flujo de salida (FileOutputStream) para escribir datos en un archivo específico (nuevoArchivo)
            workbook.write(fileOut); //Escribimos el contenido del "Workbook" usando el "FileOutputStream"
            fileOut.flush(); //Garantizamos que todos los datos en el buffer del "FileOutputStream" se escriban físicamente en el archivo
            fileOut.close(); //Cerramos el flujo de salida

            crearNotificacionExcel(nuevoArchivo, nombreXlsx); //Llamamos el método de abajo para crear la notificación de la descarga
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

    //Método que permite exportar un listado de ingresos a PDF
    public void exportarIngresosPDF(List<IngresosItems> listaIngresos, String cuadrillaMes) {
        if (listaIngresos == null || listaIngresos.isEmpty()) { //Antes de crear el archivo PDF, verificamos que la lista de ingresos no es nula o está vacía
            Toast.makeText(contexto, "NO HAY INGRESOS DISPONIBLES PARA EXPORTAR", Toast.LENGTH_LONG).show();
            return;
        }

        Document document = new Document(PageSize.LETTER.rotate()); //Creamos una instancia de "Document" de iText, con un tamaño de página de carta (Letter) en orientación horizontal (rotate())

        try {
            //Obtenemos la ruta de la carpeta "Documentos", y en ella creamos la carpeta "INGELCOM_Reportes/Ingresos/PDF", esto lo guardamos en una variable de tipo "File" ("Archivo" en español) llamada "directorio"
            File directorio = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "INGELCOM_Reportes/Ingresos/PDF");
            if (!directorio.exists()) { //Si "carpeta" no existe, que entre al if y lo cree
                directorio.mkdirs();
            }

            String nombre = "Ingresos" + cuadrillaMes.replaceAll("[- ]", ""); //Creamos el nombre del archivo PDF, el cual empieza con la palabra "Ingresos", recibe el resto del nombre en la variable "cuadrillaMes" en el cual, con una expresión regular, se le eliminan los guiones y espacios para evitar conflictos en la creación del archivo
            String nombrePdf = nombre + ".pdf"; //Concatenamos el nombre con ".pdf" que es la extensión del archivo PDF
            String rutaArchivo = directorio.getAbsolutePath() + "/" + nombrePdf; //El nombre del archivo con el ".pdf" lo concatenamos con "directorio.getAbsolutePath()" que tiene la ruta con las carpetas "INGELCOM_Reportes/Ingresos/PDF", y con un separador de archivos "/"
            File nuevoArchivo = new File(rutaArchivo); //Guardamos la ruta completa del archivo en una variable de tipo File

            int contador = 1; //Contador que nos ayudará a establecer un nombre diferente al archivo guardado en "nuevoArchivo" sólo si este ya se encuentra en el almacenamiento interno
            while (nuevoArchivo.exists()) { //Mientras "nuevoArchivo" exista en el mismo almacenamiento interno, que no salga del while
                nombrePdf = nombre + "(" + contador + ").pdf"; //Como "nuevoArchivo" existe, agregamos un número al nombre del archivo para que dicho nombre no se repita
                nuevoArchivo = new File(directorio, nombrePdf); //Guardamos el directorio y el nuevo nombre en el "nuevoArchivo"
                contador++; //Aumentamos un número al contador para que si no sale del "while", el archivo tenga el siguiente número en su nombre
            }

            FileOutputStream fos = new FileOutputStream(nuevoArchivo); //Creamos un flujo de salida (FileOutputStream) para escribir datos en un archivo específico (nuevoArchivo)

            PdfWriter.getInstance(document, fos); //Asociamos el "PdfWriter" con el "Document" y el "FileOutputStream" para escribir en el archivo
            document.open(); //Abrimos el "document"

            Font fuenteEncabezado = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD); //Definimos una fuente (Font) para los encabezados de la tabla, utilizando la familia de fuentes Helvetica, un tamaño de 12 puntos y un estilo en negrita (BOLD)

            PdfPTable table = new PdfPTable(5); //Creamos una tabla con "PdfPTable" y le establecemos 5 columnas
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
            fos.close(); //Cerramos el flujo de salida

            crearNotificacionPDF(nuevoArchivo, nombrePdf); //Llamamos el método de abajo para crear la notificación de la descarga
            Toast.makeText(contexto, "ARCHIVO GUARDADO EN LA CARPETA DE DOCUMENTOS", Toast.LENGTH_LONG).show(); //Mostramos mensaje de éxito
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e("CrearPDF", "Error al crear el PDF", e);
            Toast.makeText(contexto, "ERROR AL CREAR EL ARCHIVO PDF", Toast.LENGTH_LONG).show(); //Mostramos mensaje de error
        }
    }

    //Método que permite crear una notificación al descargar una imagen
    private void crearNotificacionImagen(File archivo, String nombreArchivo) {
        //Obtenemos una instancia del servicio del sistema llamado NotificationManager, que es responsable de gestionar las notificaciones en Android
        NotificationManager notificationManager = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);

        //Definimos dos variables, "canalId" y "nombreCanal", que representan el identificador único y el nombre del canal de notificación, respectivamente. Estos canales de notificación son necesarios en dispositivos que ejecutan Android 8 (Oreo) o superior
        String canalId = "canal_descarga_imagen";
        String nombreCanal = "Descargas de Imagen";

        //Este if sólo se ejecuta si la versión de Android del dispositivo es Android 8 o superior (Esto para evitar errores en la creación de la notificación)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(canalId, nombreCanal, NotificationManager.IMPORTANCE_DEFAULT); //Creamos un "NotificationChannel" con un ID, un nombre de canal y una importancia ("IMPORTANCE_DEFAULT", que establece la prioridad de la notificación)
            notificationManager.createNotificationChannel(channel); //Luego, el "channel" se registra en el NotificationManager mediante el método "createNotificationChannel". Los canales permiten al usuario personalizar el comportamiento de las notificaciones (como el sonido o la vibración)
        }

        Uri archivoUri = FileProvider.getUriForFile(contexto, contexto.getPackageName() + ".provider", archivo); //Obtenemos el URI del archivo. Usamos "FileProvider.getUriForFile()" para obtener un URI seguro que apunte al archivo que se desea abrir. El "FileProvider" facilita el acceso a archivos de una manera segura, respetando los permisos
        Intent intent = new Intent(Intent.ACTION_VIEW); //Creamos un Intent con la acción "ACTION_VIEW", lo que significa que el sistema intentará abrir el archivo con la aplicación adecuada
        intent.setDataAndType(archivoUri, "image/jpeg"); //Especificamos el tipo MIME del archivo ("image/jpeg" para archivos JPG)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //Agregamos la bandera "FLAG_GRANT_READ_URI_PERMISSION" al Intent, lo que otorga temporalmente permisos de lectura al archivo para la aplicación que lo abra

        //Creamos un "PendingIntent" que encapsula el "Intent" de arriba para abrir la imagen. Este PendingIntent será utilizado cuando el usuario toque la notificación
        PendingIntent pendingIntent = PendingIntent.getActivity(contexto, 0, intent, PendingIntent.FLAG_IMMUTABLE); //Usamos "PendingIntent.FLAG_IMMUTABLE" el cual es un flag que indica que el PendingIntent no se podrá modificar una vez se ha creado; esta es una práctica recomendada para garantizar la seguridad y es obligatoria en dispositivos que ejecutan Android 12 (API 31) o superior

        //Construimos la notificación. Aquí se usa la clase "NotificationCompat.Builder" para construir la notificación, y se pasa el contexto y el ID del canal de notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(contexto, canalId)
                .setSmallIcon(android.R.drawable.stat_sys_download_done) //Icono
                .setContentTitle("Imagen Guardada") //Título
                .setContentText("La imagen " + nombreArchivo + " se ha guardado con éxito") //Texto indicativo
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) //Establecemos una prioridad. "NotificationCompat.PRIORITY_DEFAULT" indica que la notificación tiene una importancia normal
                .setContentIntent(pendingIntent) //Establecemos el "PendingIntent" creado anteriormente con setContentIntent, que permitirá al usuario abrir la imagen cuando toque la notificación
                .setAutoCancel(true); //Aseguramos que la notificación se descartará automáticamente cuando el usuario toque la notificación

        int notificacionID = nombreArchivo.hashCode(); //Generamos un ID único para la notificación basada en el nombre del archivo
        notificationManager.notify(notificacionID, builder.build()); //Utilizamos el "NotificationManager" para mostrar la notificación construida. Establecemos "notificacionID" como un ID único para la notificación de una imagen que permite al sistema identificarla
    }

    //Método que permite crear una notificación al exportar un archivo de Excel
    private void crearNotificacionExcel(File archivo, String nombreArchivo) {
        //Obtenemos una instancia del servicio del sistema llamado NotificationManager, que es responsable de gestionar las notificaciones en Android
        NotificationManager notificationManager = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);

        //Definimos dos variables, "canalId" y "nombreCanal", que representan el identificador único y el nombre del canal de notificación, respectivamente. Estos canales de notificación son necesarios en dispositivos que ejecutan Android 8 (Oreo) o superior
        String canalId = "canal_descarga_excel";
        String nombreCanal = "Descargas de Excel";

        //Este if sólo se ejecuta si la versión de Android del dispositivo es Android 8 o superior (Esto para evitar errores en la creación de la notificación)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(canalId, nombreCanal, NotificationManager.IMPORTANCE_DEFAULT); //Creamos un "NotificationChannel" con un ID, un nombre de canal y una importancia ("IMPORTANCE_DEFAULT", que establece la prioridad de la notificación)
            notificationManager.createNotificationChannel(channel); //Luego, el "channel" se registra en el NotificationManager mediante el método "createNotificationChannel". Los canales permiten al usuario personalizar el comportamiento de las notificaciones (como el sonido o la vibración)
        }

        Uri archivoUri = FileProvider.getUriForFile(contexto, contexto.getPackageName() + ".provider", archivo); //Obtenemos el URI del archivo. Usamos "FileProvider.getUriForFile()" para obtener un URI seguro que apunte al archivo que se desea abrir. El "FileProvider" facilita el acceso a archivos de una manera segura, respetando los permisos
        Intent intent = new Intent(Intent.ACTION_VIEW); //Creamos un Intent con la acción "ACTION_VIEW", lo que significa que el sistema intentará abrir el archivo con la aplicación adecuada
        intent.setDataAndType(archivoUri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); //Especificamos el tipo MIME del archivo ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", que es el tipo para archivos Excel)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //Agregamos la bandera "FLAG_GRANT_READ_URI_PERMISSION" al Intent, lo que otorga temporalmente permisos de lectura al archivo para la aplicación que lo abra

        //Creamos un "PendingIntent" que encapsula el "Intent" de arriba para abrir el archivo. Este PendingIntent será utilizado cuando el usuario toque la notificación
        PendingIntent pendingIntent = PendingIntent.getActivity(contexto, 0, intent, PendingIntent.FLAG_IMMUTABLE); //Usamos "PendingIntent.FLAG_IMMUTABLE" el cual es un flag que indica que el PendingIntent no se podrá modificar una vez se ha creado; esta es una práctica recomendada para garantizar la seguridad y es obligatoria en dispositivos que ejecutan Android 12 (API 31) o superior

        //Construimos la notificación. Aquí se usa la clase "NotificationCompat.Builder" para construir la notificación, y se pasa el contexto y el ID del canal de notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(contexto, canalId)
                .setSmallIcon(android.R.drawable.stat_sys_download_done) //Icono
                .setContentTitle("Descarga Completada") //Titulo
                .setContentText(nombreArchivo + " se ha guardado con éxito") //Texto indicativo
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) //Establecemos una prioridad. "NotificationCompat.PRIORITY_DEFAULT" indica que la notificación tiene una importancia normal
                .setContentIntent(pendingIntent) //Establecemos el "PendingIntent" creado anteriormente con setContentIntent, que permitirá al usuario abrir el archivo Excel cuando toque la notificación
                .setAutoCancel(true); //Aseguramos que la notificación se descartará automáticamente cuando el usuario toque la notificación

        int notificacionID = nombreArchivo.hashCode(); //Generamos un ID único para la notificación basada en el nombre del archivo
        notificationManager.notify(notificacionID, builder.build()); //Utilizamos el "NotificationManager" para mostrar la notificación construida. Establecemos "notificacionID" como un ID único para la notificación de un archivo Excel que permite al sistema identificarla
    }

    //Método que permite crear una notificación al exportar un archivo PDF
    private void crearNotificacionPDF(File archivo, String nombreArchivo) {
        //Obtenemos una instancia del servicio del sistema llamado NotificationManager, que es responsable de gestionar las notificaciones en Android
        NotificationManager notificationManager = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);

        //Definimos dos variables, "canalId" y "nombreCanal", que representan el identificador único y el nombre del canal de notificación, respectivamente. Estos canales de notificación son necesarios en dispositivos que ejecutan Android 8 (Oreo) o superior
        String canalId = "canal_descarga_pdf";
        String nombreCanal = "Descargas PDF";

        //Este if sólo se ejecuta si la versión de Android del dispositivo es Android 8 o superior (Esto para evitar errores en la creación de la notificación)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(canalId, nombreCanal, NotificationManager.IMPORTANCE_DEFAULT); //Creamos un "NotificationChannel" con un ID, un nombre de canal y una importancia ("IMPORTANCE_DEFAULT", que establece la prioridad de la notificación)
            notificationManager.createNotificationChannel(channel); //Luego, el "channel" se registra en el NotificationManager mediante el método "createNotificationChannel". Los canales permiten al usuario personalizar el comportamiento de las notificaciones (como el sonido o la vibración)
        }

        Uri archivoUri = FileProvider.getUriForFile(contexto, contexto.getPackageName() + ".provider", archivo); //Obtenemos el URI del archivo. Usamos "FileProvider.getUriForFile()" para obtener un URI seguro que apunte al archivo que se desea abrir. El "FileProvider" facilita el acceso a archivos de una manera segura, respetando los permisos
        Intent intent = new Intent(Intent.ACTION_VIEW); //Creamos un Intent con la acción "ACTION_VIEW", lo que significa que el sistema intentará abrir el archivo con la aplicación adecuada
        intent.setDataAndType(archivoUri, "application/pdf"); //Especificamos el tipo MIME del archivo ("application/pdf", que es el tipo para archivos PDF)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //Agregamos la bandera "FLAG_GRANT_READ_URI_PERMISSION" al Intent, lo que otorga temporalmente permisos de lectura al archivo para la aplicación que lo abra

        //Creamos un "PendingIntent" que encapsula el "Intent" de arriba para abrir el archivo. Este PendingIntent será utilizado cuando el usuario toque la notificación
        PendingIntent pendingIntent = PendingIntent.getActivity(contexto, 0, intent, PendingIntent.FLAG_IMMUTABLE); //Usamos "PendingIntent.FLAG_IMMUTABLE" el cual es un flag que indica que el PendingIntent no se podrá modificar una vez se ha creado; esta es una práctica recomendada para garantizar la seguridad y es obligatoria en dispositivos que ejecutan Android 12 (API 31) o superior

        //Construimos la notificación. Aquí se usa la clase "NotificationCompat.Builder" para construir la notificación, y se pasa el contexto y el ID del canal de notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(contexto, canalId)
                .setSmallIcon(android.R.drawable.stat_sys_download_done) //Icono
                .setContentTitle("Descarga Completada") //Título
                .setContentText(nombreArchivo + " se ha guardado con éxito") //Texto indicativo
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) //Establecemos una prioridad. "NotificationCompat.PRIORITY_DEFAULT" indica que la notificación tiene una importancia normal
                .setContentIntent(pendingIntent) //Establecemos el "PendingIntent" creado anteriormente con setContentIntent, que permitirá al usuario abrir el archivo PDF cuando toque la notificación
                .setAutoCancel(true); //Aseguramos que la notificación se descartará automáticamente cuando el usuario toque la notificación

        int notificacionID = nombreArchivo.hashCode(); //Generamos un ID único para la notificación basada en el nombre del archivo
        notificationManager.notify(notificacionID, builder.build()); //Utilizamos el "NotificationManager" para mostrar la notificación construida. Establecemos "notificacionID" como un ID único para la notificación de un archivo PDF que permite al sistema identificarla
    }
}