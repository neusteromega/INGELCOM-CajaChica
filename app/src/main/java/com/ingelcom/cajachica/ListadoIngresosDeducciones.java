package com.ingelcom.cajachica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ingelcom.cajachica.Adaptadores.DeduccionesAdapter;
import com.ingelcom.cajachica.Adaptadores.IngresosAdapter;
import com.ingelcom.cajachica.DAO.Deduccion;
import com.ingelcom.cajachica.DAO.Ingreso;
import com.ingelcom.cajachica.DAO.Usuario;
import com.ingelcom.cajachica.Herramientas.Exportaciones;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.Modelos.DeduccionesItems;
import com.ingelcom.cajachica.Modelos.IngresosItems;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ListadoIngresosDeducciones extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, PopupMenu.OnMenuItemClickListener {

    private LinearLayout llFecha, llTiempo;
    private TextView btnReintentarConexion, lblTitulo, btnMes, btnAnio, lblFecha, lblTotal, lblTotalTitulo;
    private String nombreActivity, nombreCuadrilla, fechaSeleccionada = "", tipoFecha = "Mes", tipoExportar;
    private RecyclerView rvIngrDeduc;
    private ImageView btnExportar;
    private SwipeRefreshLayout swlRecargar;
    private View viewNoInternet;
    private ProgressBar pbReintentarConexion;
    private ProgressDialog progressDialog;

    private Ingreso ingr = new Ingreso(ListadoIngresosDeducciones.this);
    private Deduccion deduc = new Deduccion(ListadoIngresosDeducciones.this);
    private Usuario usu = new Usuario(ListadoIngresosDeducciones.this);
    private Exportaciones exp = new Exportaciones(ListadoIngresosDeducciones.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_ingresos_deducciones);

        inicializarElementos();
        establecerElementos();
        obtenerDatos("", "Mostrar"); //Llamamos el método "obtenerDatos" de abajo donde mandamos el mes vacío ya que al cargar este activity no queremos filtrar los ingresos, y el texto "Mostrar" ya que primeramente se mostrarán los ingresos, sólo se exportarán si el usuario presiona alguna opción del PopupMenu de exportaciones
        cambioFecha();

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
        //Obtenemos el nombre del activity y de la cuadrilla que se envía desde el activity anterior (el "nombreCuadrilla" puede quedar vacío si el activity anterior no fue AdmDatosCuadrilla, ya que sólo aquí se manda el nombre de la cuadrilla)
        nombreActivity = Utilidades.obtenerStringExtra(this, "ActivityLID");
        nombreCuadrilla = Utilidades.obtenerStringExtra(this, "Cuadrilla");

        llFecha = findViewById(R.id.LLFechaLI);
        llTiempo = findViewById(R.id.LLTiempoLI);
        lblTitulo = findViewById(R.id.lblTituloLI);
        btnMes = findViewById(R.id.lblMesLI);
        btnAnio = findViewById(R.id.lblAnioLI);
        lblFecha = findViewById(R.id.lblFechaLI);
        lblTotal = findViewById(R.id.lblCantIngresosLI);
        lblTotalTitulo = findViewById(R.id.lblTotalIngresosLI);
        rvIngrDeduc = findViewById(R.id.rvListadoIngrDeduc);
        btnExportar = findViewById(R.id.imgExportarLI);
        swlRecargar = findViewById(R.id.swipeRefreshLayoutLI);
        viewNoInternet = findViewById(R.id.viewNoInternetLI);
        btnReintentarConexion = findViewById(R.id.btnReintentarConexion);
        pbReintentarConexion = findViewById(R.id.pbReintentarConexion);

        swlRecargar.setOnRefreshListener(this); //Llamada al método "onRefresh"

        Utilidades.mostrarMensajePorInternetCaido(this, viewNoInternet); //Llamamos el método utilitario "mostrarMensajePorInternetCaido" donde mandamos la vista "viewNoInternet" donde se hará visible cuando no haya conexión a internet y se ocultará cuando si haya
    }

    private void establecerElementos() {
        swlRecargar.setColorSchemeResources(R.color.clr_fuente_primario); //Color del SwipeRefreshLayout

        switch (nombreActivity) { //Según el texto de "nombreActivity" que se recibe de la pantalla anterior, establecemos los elementos gráficos de este Activity
            case "ListadoIngresosAdmin":
                lblTitulo.setText(nombreCuadrilla);
                lblTotalTitulo.setText("Total de Ingresos");
                lblTotal.setTextColor(getResources().getColor(R.color.clr_fuente_ingresos));
                break;

            case "ListadoIngresosEmpleado":
                lblTitulo.setText("Listado de Ingresos");
                llTiempo.setVisibility(View.GONE);
                lblTotalTitulo.setText("Total de Ingresos");
                lblTotal.setTextColor(getResources().getColor(R.color.clr_fuente_ingresos));
                btnExportar.setVisibility(View.GONE);
                break;

            case "ListadoIngresosTodos":
                lblTitulo.setText("Todos los Ingresos");
                lblTotalTitulo.setText("Total de Ingresos");
                lblTotal.setTextColor(getResources().getColor(R.color.clr_fuente_ingresos));
                break;

            case "ListadoDeducciones":
                llFecha.setVisibility(View.GONE);
                llTiempo.setVisibility(View.GONE);
                lblTitulo.setText(nombreCuadrilla);
                lblTotalTitulo.setText("Total de Deducciones");
                lblTotal.setTextColor(getResources().getColor(R.color.clr_fuente_secundario));
                btnExportar.setVisibility(View.GONE);
                break;
        }
    }

    private void obtenerDatos(String mesAnio, String tipo) {
        try {
            if (nombreActivity.equalsIgnoreCase("ListadoIngresosAdmin")) {
                //Llamamos el método "obtenerIngresos" de la clase "Ingreso", le mandamos la cuadrilla recibida en "nombreCuadrilla" y el "mes". Con esto se podrán obtener todos los ingresos hechos por los administradores a una cuadrilla específica
                ingr.obtenerIngresos(nombreCuadrilla, mesAnio, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<IngresosItems>() {
                    @Override
                    public void onCallback(List<IngresosItems> items) { //En esta lista "items" están todos los ingresos ya filtrados por cuadrilla
                        if (items != null) { //Si "items" no es null, que entre al if
                            items = Utilidades.ordenarListaPorFechaHora(items, "fechaHora", "Descendente"); //Llamamos el método utilitario "ordenarListaPorFechaHora". Le mandamos la lista "items", el nombre del campo double "fechaHora", y el tipo de orden "Descendente". Este método retorna la lista ya ordenada y la guardamos en "items"

                            if (tipo.equalsIgnoreCase("Mostrar")) { //Si la variable "tipo" que se recibe como parámetro tiene el texto "Mostrar", significa que solamente se desean mostrar los datos
                                inicializarRecyclerView(items, "Ingresos"); //Llamamos el método "inicializarRecyclerView" de abajo y le mandamos la lista "items"
                            }
                            else if (tipo.equalsIgnoreCase("Exportar")) { //En cambio, si "tipo" tiene el texto "Exportar", significa que desean exportar los datos
                                //Creamos un "ProgressDialog" por mientras se está realizando la exportación del archivo
                                progressDialog = new ProgressDialog(ListadoIngresosDeducciones.this);
                                progressDialog.setTitle("Exportando Ingresos...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                List<IngresosItems> finalItems = items;

                                //Creamos una nueva instancia de "Handler", que está vinculada al Looper principal (el hilo principal de la aplicación). Esto asegura que cualquier operación realizada dentro de este Handler se ejecute en el hilo principal
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { //El "Handler" utiliza el método "postDelayed" para ejecutar el "Runnable" que contiene las acciones a realizar después de un retraso especificado (en este caso, 1000 milisegundos, es decir, 1 segundo)
                                    @Override
                                    public void run() {
                                        if (mesAnio.isEmpty() || mesAnio.equalsIgnoreCase("Seleccionar...")) { //Si "mes" que se recibe como parámetro está vacío o tiene el texto "Seleccionar Mes" significa que no se hará algún filtrado al momento de exportar los datos
                                            if (tipoExportar.equalsIgnoreCase("EXCEL")) //Si la variable global "tipoExportar" tiene el texto "EXCEL", significa que se quieren exportar los datos a excel, que entre al if
                                                exp.exportarIngresosExcel(finalItems, "_" + nombreCuadrilla); //Llamamos el método "exportarIngresosExcel" de la clase "Exportaciones" donde mandamos la lista "items" y un texto que servirá para el nombre del archivo al crearlo
                                            else if (tipoExportar.equalsIgnoreCase("PDF")) //En cambio, si la variable global "tipoExportar" tiene el texto "PDF", significa que se quieren exportar los datos a pdf, que entre al else if
                                                exp.exportarIngresosPDF(finalItems, "_" + nombreCuadrilla); //Llamamos el método "exportarIngresosPDF" de la clase "Exportaciones" donde mandamos la lista "items" y un texto que servirá para el nombre del archivo al crearlo
                                        }
                                        else { //En cambio, si "mes" contiene un texto diferente a "Seleccionar Mes", eso quiere decir que si se hizo un filtrado en los datos
                                            if (tipoExportar.equalsIgnoreCase("EXCEL")) //Si la variable global "tipoExportar" tiene el texto "EXCEL", significa que se quieren exportar los datos a excel, que entre al if
                                                exp.exportarIngresosExcel(finalItems, "_" + nombreCuadrilla + "_" + mesAnio); //Llamamos el método "exportarIngresosExcel" de la clase "Exportaciones" donde mandamos la lista "items" y un texto que servirá para el nombre del archivo al crearlo
                                            else if (tipoExportar.equalsIgnoreCase("PDF")) //En cambio, si la variable global "tipoExportar" tiene el texto "PDF", significa que se quieren exportar los datos a pdf, que entre al else if
                                                exp.exportarIngresosPDF(finalItems, "_" + nombreCuadrilla + "_" + mesAnio); //Llamamos el método "exportarIngresosPDF" de la clase "Exportaciones" donde mandamos la lista "items" y un texto que servirá para el nombre del archivo al crearlo
                                        }

                                        if (progressDialog.isShowing()) //Si "progressDialog" se está mostrando, que entre al if
                                            progressDialog.dismiss(); //Eliminamos el "progressDialog" ya cuando el proceso de exportación
                                    }
                                }, 1000);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(ListadoIngresosDeducciones.this, "ERROR AL CARGAR LOS INGRESOS", Toast.LENGTH_SHORT).show();
                        Log.w("ObtenerIngresos", e);
                    }
                });
            }
            else if (nombreActivity.equalsIgnoreCase("ListadoIngresosEmpleado")) {
                //Llamamos el método "obtenerUnUsuario" de la clase "Usuario" que obtiene el usuario actual
                usu.obtenerUsuarioActual(new FirestoreCallbacks.FirestoreDocumentCallback() {
                    @Override
                    public void onCallback(Map<String, Object> documento) {
                        if (documento != null) { //Si "documento" no es nulo, quiere decir que encontró el usuario mediante el correo
                            String cuadrilla = (String) documento.get("Cuadrilla"); //Obtenemos la cuadrilla de "documento"

                            //Llamamos el método "obtenerIngresos" de la clase "Ingreso", le mandamos la cuadrilla obtenida de Firestore en "cuadrilla" y el "mes". Con esto se podrán obtener todos los ingresos hechos por los administradores a la cuadrilla del usuario actual
                            ingr.obtenerIngresos(cuadrilla, mesAnio, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<IngresosItems>() {
                                @Override
                                public void onCallback(List<IngresosItems> items) { //En esta lista "items" están todos los ingresos ya filtrados por cuadrilla
                                    if (items != null) {//Si "items" no es null, que entre al if
                                        items = Utilidades.ordenarListaPorFechaHora(items, "fechaHora", "Descendente"); //Llamamos el método utilitario "ordenarListaPorFechaHora". Le mandamos la lista "items", el nombre del campo double "fechaHora", y el tipo de orden "Descendente". Este método retorna la lista ya ordenada y la guardamos en "items"
                                        inicializarRecyclerView(items, "Ingresos"); //Llamamos el método "inicializarRecyclerView" de abajo y le mandamos la lista "items"
                                    }
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(ListadoIngresosDeducciones.this, "ERROR AL CARGAR LOS INGRESOS", Toast.LENGTH_SHORT).show();
                                    Log.w("ObtenerIngresos", e);
                                }
                            });
                        }
                        else { //Si "documento" es nulo, no se encontró el usuario en la colección, y entrará en este else
                            Log.w("ObtenerUsuario", "Usuario no encontrado");
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.w("BuscarUsuario", "Error al obtener el usuario", e);
                    }
                });
            }
            else if (nombreActivity.equalsIgnoreCase("ListadoIngresosTodos")) {
                //Llamamos el método "obtenerIngresos" de la clase "Ingreso", le mandamos la cuadrilla vacía para indicar que no queremos filtrar los ingresos, y el "mes". Con esto se podrán obtener todos los ingresos hechos por los administradores sin ningún filtro
                ingr.obtenerIngresos("", mesAnio, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<IngresosItems>() {
                    @Override
                    public void onCallback(List<IngresosItems> items) { //En esta lista "items" están todos los ingresos ya filtrados por cuadrilla
                        if (items != null) { //Si "items" no es null, que entre al if
                            items = Utilidades.ordenarListaPorFechaHora(items, "fechaHora", "Descendente"); //Llamamos el método utilitario "ordenarListaPorFechaHora". Le mandamos la lista "items", el nombre del campo double "fechaHora", y el tipo de orden "Descendente". Este método retorna la lista ya ordenada y la guardamos en "items"

                            if (tipo.equalsIgnoreCase("Mostrar")) { //Si la variable "tipo" que se recibe como parámetro tiene el texto "Mostrar", significa que solamente se desean mostrar los datos
                                inicializarRecyclerView(items, "Ingresos"); //Llamamos el método "inicializarRecyclerView" de abajo y le mandamos la lista "items"
                            }
                            else if (tipo.equalsIgnoreCase("Exportar")) { //En cambio, si "tipo" tiene el texto "Exportar", significa que desean exportar los datos
                                //Creamos un "ProgressDialog" por mientras se está realizando la exportación del archivo
                                progressDialog = new ProgressDialog(ListadoIngresosDeducciones.this);
                                progressDialog.setTitle("Exportando Ingresos...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                List<IngresosItems> finalItems = items;

                                //Creamos una nueva instancia de "Handler", que está vinculada al Looper principal (el hilo principal de la aplicación). Esto asegura que cualquier operación realizada dentro de este Handler se ejecute en el hilo principal
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { //El "Handler" utiliza el método "postDelayed" para ejecutar el "Runnable" que contiene las acciones a realizar después de un retraso especificado (en este caso, 1000 milisegundos, es decir, 1 segundo)
                                    @Override
                                    public void run() {
                                        if (mesAnio.isEmpty() || mesAnio.equalsIgnoreCase("Seleccionar...")) { //Si "mes" que se recibe como parámetro está vacío o tiene el texto "Seleccionar Mes" significa que no se hará algún filtrado al momento de exportar los datos
                                            if (tipoExportar.equalsIgnoreCase("EXCEL")) //Si la variable global "tipoExportar" tiene el texto "EXCEL", significa que se quieren exportar los datos a excel, que entre al if
                                                exp.exportarIngresosExcel(finalItems, "Generales"); //Llamamos el método "exportarIngresosExcel" de la clase "Exportaciones" donde mandamos la lista "items" y un texto que servirá para el nombre del archivo al crearlo
                                            else if (tipoExportar.equalsIgnoreCase("PDF")) //En cambio, si la variable global "tipoExportar" tiene el texto "PDF", significa que se quieren exportar los datos a pdf, que entre al else if
                                                exp.exportarIngresosPDF(finalItems, "Generales"); //Llamamos el método "exportarIngresosPDF" de la clase "Exportaciones" donde mandamos la lista "items" y un texto que servirá para el nombre del archivo al crearlo
                                        }
                                        else { //En cambio, si "mes" contiene un texto diferente a "Seleccionar Mes", eso quiere decir que si se hizo un filtrado en los datos
                                            if (tipoExportar.equalsIgnoreCase("EXCEL")) //Si la variable global "tipoExportar" tiene el texto "EXCEL", significa que se quieren exportar los datos a excel, que entre al if
                                                exp.exportarIngresosExcel(finalItems, "Generales_" + mesAnio); //Llamamos el método "exportarIngresosExcel" de la clase "Exportaciones" donde mandamos la lista "items" y un texto que servirá para el nombre del archivo al crearlo
                                            else if (tipoExportar.equalsIgnoreCase("PDF")) //En cambio, si la variable global "tipoExportar" tiene el texto "PDF", significa que se quieren exportar los datos a pdf, que entre al else if
                                                exp.exportarIngresosPDF(finalItems, "Generales_" + mesAnio); //Llamamos el método "exportarIngresosPDF" de la clase "Exportaciones" donde mandamos la lista "items" y un texto que servirá para el nombre del archivo al crearlo
                                        }

                                        if (progressDialog.isShowing()) //Si "progressDialog" se está mostrando, que entre al if
                                            progressDialog.dismiss(); //Eliminamos el "progressDialog" ya cuando el proceso de exportación
                                    }
                                }, 1000);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(ListadoIngresosDeducciones.this, "ERROR AL CARGAR LOS INGRESOS", Toast.LENGTH_SHORT).show();
                        Log.w("ObtenerIngresos", e);
                    }
                });
            }
            else if (nombreActivity.equalsIgnoreCase("ListadoDeducciones")) {
                //Llamamos el método "obtenerDeducciones" de la clase "Deduccion", le mandamos la cuadrilla recibida en "nombreCuadrilla". Con esto se podrán obtener todas las deducciones por planilla hechas por los administradores
                deduc.obtenerDeducciones(nombreCuadrilla, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<DeduccionesItems>() {
                    @Override
                    public void onCallback(List<DeduccionesItems> items) { //En esta lista "items" están todas las deducciones ya filtradas por cuadrilla
                        if (items != null) { //Si "items" no es null, que entre al if
                            items = Utilidades.ordenarListaPorFechaHora(items, "fechaHora", "Descendente"); //Llamamos el método utilitario "ordenarListaPorFechaHora". Le mandamos la lista "items", el nombre del campo double "fechaHora", y el tipo de orden "Descendente". Este método retorna la lista ya ordenada y la guardamos en "items"
                            inicializarRecyclerView(items, "Deducciones"); //Llamamos el método "inicializarRecyclerView" de abajo y le mandamos la lista "items"
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(ListadoIngresosDeducciones.this, "ERROR AL CARGAR LAS DEDUCCIONES", Toast.LENGTH_SHORT).show();
                        Log.w("ObtenerDeducciones", e);
                    }
                });
            }
        }
        catch (Exception e) {
            Log.w("ObtenerIngresos", e);
        }
    }

    //Método que nos ayuda a inicializar el RecyclerView de ingresos
    private void inicializarRecyclerView(List<?> items, String tipo) {
        rvIngrDeduc.setLayoutManager(new LinearLayoutManager(this)); //Creamos un nuevo LinearLayoutManager para que el RecyclerView se vea en forma de tarjetas

        if (tipo.contentEquals("Ingresos")) {
            List<IngresosItems> ingresosItems = (List<IngresosItems>) items;
            IngresosAdapter adapter = new IngresosAdapter(ingresosItems, nombreActivity); //Creamos un nuevo objeto de tipo IngresosAdapter en el cual enviamos la lista "items"
            rvIngrDeduc.setAdapter(adapter); //Asignamos el adapter al recyclerView de Ingresos
            double totalIngresos = 0; //Variable que nos servirá para calcular el total de los ingresos que se muestren en el RecyclerView

            //Recorremos la lista "items" y cada elemento de ella se guardará en la variable temporal "item" de tipo "IngresosItems"
            for (IngresosItems item : ingresosItems) {
                totalIngresos += item.getTotal(); //Obtenemos el "total" de cada elemento de la lista "items" y lo vamos sumando en la variable "totalIngresos"
            }

            lblTotal.setText("L. " + String.format("%.2f", totalIngresos)); //Asignamos el totalIngresos al TextView "lblTotalIngresos" y formateamos la variable "totalIngresos" para que se muestre con dos digitos después del punto decimal

            adapter.setOnClickListener(new View.OnClickListener() { //Usando el objeto de "adapter" llamamos al método "setOnClickListener" de la clase IngresosAdapter
                @Override
                public void onClick(View view) { //Al dar clic en una tarjeta del RecyclerView, se realizará lo siguiente
                    HashMap<String, Object> datosIngreso = new HashMap<>(); //Creamos un HashMap para guardar los datos que se enviarán al siguiente Activity

                    //Agregamos las claves y datos al HashMap
                    datosIngreso.put("ActivityDGI", "DetalleIngreso");
                    datosIngreso.put("ID", ingresosItems.get(rvIngrDeduc.getChildAdapterPosition(view)).getId());
                    datosIngreso.put("Usuario", ingresosItems.get(rvIngrDeduc.getChildAdapterPosition(view)).getUsuario());
                    datosIngreso.put("FechaHora", ingresosItems.get(rvIngrDeduc.getChildAdapterPosition(view)).getFechaHora());
                    datosIngreso.put("Cuadrilla", ingresosItems.get(rvIngrDeduc.getChildAdapterPosition(view)).getCuadrilla());
                    datosIngreso.put("Transferencia", ingresosItems.get(rvIngrDeduc.getChildAdapterPosition(view)).getTransferencia());
                    datosIngreso.put("Imagen", ingresosItems.get(rvIngrDeduc.getChildAdapterPosition(view)).getImagen());
                    datosIngreso.put("Total", String.format("%.2f", ingresosItems.get(rvIngrDeduc.getChildAdapterPosition(view)).getTotal()));

                    //Llamamos el método "iniciarActivityConDatos" de la clase Utilidades y le mandamos el contexto, el activity siguiente y el HashMap con los datos a enviar
                    Utilidades.iniciarActivityConDatos(ListadoIngresosDeducciones.this, DetalleGastoIngreso.class, datosIngreso);
                }
            });
        }
        else if (tipo.contentEquals("Deducciones")) {
            List<DeduccionesItems> deduccionesItems = (List<DeduccionesItems>) items;
            DeduccionesAdapter adapter = new DeduccionesAdapter(deduccionesItems);
            rvIngrDeduc.setAdapter(adapter);
            double totalDeducciones = 0;

            for (DeduccionesItems item : deduccionesItems) {
                totalDeducciones += item.getTotal();
            }

            lblTotal.setText("L. " + String.format("%.2f", totalDeducciones));

            adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HashMap<String, Object> datosDeduccion = new HashMap<>();

                    //Agregamos las claves y datos al HashMap
                    datosDeduccion.put("ActivityREID", "EditarDeduccion");
                    datosDeduccion.put("ID", deduccionesItems.get(rvIngrDeduc.getChildAdapterPosition(view)).getId());
                    datosDeduccion.put("FechaHora", deduccionesItems.get(rvIngrDeduc.getChildAdapterPosition(view)).getFechaHora());
                    datosDeduccion.put("Cuadrilla", deduccionesItems.get(rvIngrDeduc.getChildAdapterPosition(view)).getCuadrilla());
                    datosDeduccion.put("Usuario", deduccionesItems.get(rvIngrDeduc.getChildAdapterPosition(view)).getUsuario());
                    datosDeduccion.put("Total", String.format("%.2f", deduccionesItems.get(rvIngrDeduc.getChildAdapterPosition(view)).getTotal()));

                    Utilidades.iniciarActivityConDatos(ListadoIngresosDeducciones.this, RegistrarEditarIngresoDeduccion.class, datosDeduccion);
                }
            });
        }
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
                    obtenerDatos(fechaSeleccionada, "Mostrar"); //Llamamos el método "obtenerIngresos" de arriba y le mandamos el contenido del lblFecha
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

    //Evento Clic del LinearLayout de Fecha, al dar clic en el mismo, se abrirá un "Popup DatePicker" en el que se podrá seleccionar un mes y año y esto servirá para filtrar los gastos
    public void mostrarMesesAnios(View view) {
        if (nombreActivity.equalsIgnoreCase("ListadoIngresosAdmin") || nombreActivity.equalsIgnoreCase("ListadoIngresosTodos")) {
            if (tipoFecha.equalsIgnoreCase("Mes")) { //Si la variable global "tipoFecha" obtiene la palabra "Mes", significa que el usuario tiene seleccionado el botón de mes en la pantalla; por lo tanto, que entre al if
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

                    DatePickerDialog datePickerDialog = new DatePickerDialog(ListadoIngresosDeducciones.this, style, dateSetListener, year, month, day); //Creamos un nuevo objeto de tipo DatePickerDialog y le mandamos como parámetros al constructor, un contexto, la variable "style" que guarda el estilo, el "dateSetListener", el año, mes y día, estos últimos para que al abrir el AlertDialog, se muestre el mes actual
                    datePickerDialog.getDatePicker().findViewById(getResources().getIdentifier("day", "id", "android")).setVisibility(View.GONE); //Ocultamos el spinner de días asignando "GONE" en su visibilidad
                    datePickerDialog.show(); //Mostramos el AlertDialog o Popup DatePicker de solo mes y año
                }
                catch (Exception e) {
                    Log.w("ObtenerMes", e);
                }
            }
            else if (tipoFecha.equalsIgnoreCase("Año")) { //En cambio, si la variable global "tipoFecha" obtiene la palabra "Año", significa que el usuario tiene seleccionado el botón de año en la pantalla; por lo tanto, que entre al else if
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(ListadoIngresosDeducciones.this, style, dateSetListener, year, month, day); //Creamos un nuevo objeto de tipo DatePickerDialog y le mandamos como parámetros al constructor, un contexto, la variable "style" que guarda el estilo, el "dateSetListener", el año, mes y día, estos últimos para que al abrir el AlertDialog, se muestre el mes actual
                datePickerDialog.getDatePicker().findViewById(getResources().getIdentifier("day", "id", "android")).setVisibility(View.GONE); //Ocultamos el spinner de días asignando "GONE" en su visibilidad
                datePickerDialog.getDatePicker().findViewById(getResources().getIdentifier("month", "id", "android")).setVisibility(View.GONE); //Ocultamos el spinner de meses asignando "GONE" en su visibilidad
                datePickerDialog.show(); //Mostramos el AlertDialog o Popup DatePicker de solo años
            }
        }
        else if (nombreActivity.equalsIgnoreCase("ListadoIngresosEmpleado")) {
            try {
                // Obtener los meses actual y anterior
                Calendar calendar = Calendar.getInstance();

                // Configurar el formato de fecha en español
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM - yyyy", new Locale("es", "ES"));
                String mesActual = sdf.format(calendar.getTime());

                calendar.add(Calendar.MONTH, -1);
                String mesAnterior = sdf.format(calendar.getTime());

                mesActual = mesActual.substring(0, 1).toUpperCase() + mesActual.substring(1); //Aquí establecemos en mayúscula la primera letra del mes
                mesAnterior = mesAnterior.substring(0, 1).toUpperCase() + mesAnterior.substring(1); //Aquí establecemos en mayúscula la primera letra del mes

                PopupMenu popup = new PopupMenu(this, view); //Objeto de tipo "PopupMenu"
                popup.setOnMenuItemClickListener(this); //Indicamos que asigne el evento "OnMenuItemClick" para que haga algo cada vez que se dé click a una opción del menú
                popup.inflate(R.menu.popupmenu_ultimosdosmeses); //Inflamos la vista del menú indicando la ruta de dicha vista gráfica

                // Asignar los textos a los items del menú
                popup.getMenu().findItem(R.id.menuMesActual).setTitle(mesActual);
                popup.getMenu().findItem(R.id.menuMesAnterior).setTitle(mesAnterior);

                popup.show(); //Mostramos el menú ya inflado
            }
            catch (Exception e) {
                Log.w("ObtenerMes", e);
            }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) { //Parte lógica de lo que queremos que haga cada opción del popup menú
        String mesSeleccionado = menuItem.getTitle().toString(); //Obtenemos el "title" de la opción seleccionada en el popupMenu, y lo guardamos en la variable "mesSeleccionado"

        switch (menuItem.getItemId()) {
            case R.id.menuMesAnterior:
            case R.id.menuMesActual:
                fechaSeleccionada = mesSeleccionado;
                lblFecha.setText(mesSeleccionado); //Establecemos el mes y año seleccionado en el "lblFecha"
                return true;

            case R.id.menuExportarExcel:
                tipoExportar = "EXCEL"; //Cuando el usuario dé clic en la opción de "Exportar a Excel" del PopupMenu "opcionesExportar", asignamos el texto "EXCEL" a la variable global "tipoExportar"

                //Llamamos el método utilitario "verificarPermisosAlmacenamiento" donde mandamos el contexto de esta clase. Si este método devuelve un "true" significa que los permisos de almacenamiento externo ya han sido otorgados, en ese caso que entre al if
                if (Utilidades.verificarPermisosAlmacenamiento(this)) {
                    obtenerDatos(fechaSeleccionada, "Exportar"); //Como los permisos han sido otorgados, llamamos el método "obtenerDatos" de arriba y le mandamos la variable global "nombreMes" y el texto "Exportar"
                }

                return true;

            case R.id.menuExportarPDF:
                tipoExportar = "PDF"; //Cuando el usuario dé clic en la opción de "Exportar a PDF" del PopupMenu "opcionesExportar", asignamos el texto "PDF" a la variable global "tipoExportar"

                //Llamamos el método utilitario "verificarPermisosAlmacenamiento" donde mandamos el contexto de esta clase. Si este método devuelve un "true" significa que los permisos de almacenamiento externo ya han sido otorgados, en ese caso que entre al if
                if (Utilidades.verificarPermisosAlmacenamiento(this)) {
                    obtenerDatos(fechaSeleccionada, "Exportar"); //Como los permisos han sido otorgados, llamamos el método "obtenerDatos" de arriba y le mandamos la variable global "nombreMes" y el texto "Exportar"
                }

                return true;

            default:
                return false;
        }
    }

    //Método para eliminar la selección del Mes - Año
    public void eliminarMesIngresos(View view) {
        lblFecha.setText("Seleccionar...");
    }

    public void exportarIngresos(View view) {
        PopupMenu popup = new PopupMenu(this, view); //Objeto de tipo "PopupMenu"
        popup.setOnMenuItemClickListener(this); //Indicamos que asigne el evento "OnMenuItemClick" para que haga algo cada vez que se dé click a una opción del menú
        popup.inflate(R.menu.popupmenu_opcionesexportar); //Inflamos la vista del menú indicando la ruta de dicha vista gráfica

        popup.show();
    }

    @Override //Método Override que solicita los permisos de almacenamiento externo al usuario
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //If que llama al método utilitario "manejarResultadoPermisos", y le manda los datos necesarios para verificar si los permisos han sido otorgados, si así lo fue, el método retornará un "true", por lo tanto, que entre al if
        if (Utilidades.manejarResultadoPermisos(requestCode, permissions, grantResults, this)) {
            //Ya que los permisos de almacenamiento externo han sido otorgados, verificamos el contenido de la variable global "tipoExportar" para llamar al método "obtenerDatos"
            if (tipoExportar.equalsIgnoreCase("EXCEL"))
                obtenerDatos(fechaSeleccionada, "Exportar");
            else if (tipoExportar.equalsIgnoreCase("PDF"))
                obtenerDatos(fechaSeleccionada, "Exportar");
        }
    }

    @Override
    public void onRefresh() { //Método que detecta cuando se recarga la pantalla con SwipeRefreshLayout
        //Creamos una nueva instancia de "Handler", que está vinculada al Looper principal (el hilo principal de la aplicación). Esto asegura que cualquier operación realizada dentro de este Handler se ejecute en el hilo principal
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { //El "Handler" utiliza el método "postDelayed" para ejecutar el "Runnable" que contiene las acciones a realizar después de un retraso especificado (en este caso, 1000 milisegundos, es decir, 1 segundo)
            @Override
            public void run() {
                Utilidades.mostrarMensajePorInternetCaido(ListadoIngresosDeducciones.this, viewNoInternet); //Llamamos el método utilitario "mostrarMensajePorInternetCaido" donde mandamos la vista "viewNoInternet" donde se hará visible cuando no haya conexión a internet y se ocultará cuando si haya
                obtenerDatos(fechaSeleccionada, "Mostrar");
                swlRecargar.setRefreshing(false); //Llamamos a este método para detener la animación de refresco
            }
        }, 1000);
    }

    //Método que permite retroceder a la pantalla anterior
    public void retroceder(View view) {
        onBackPressed();
    }
}