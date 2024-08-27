package com.ingelcom.cajachica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ingelcom.cajachica.Adaptadores.VPGastosAdapter;
import com.ingelcom.cajachica.DAO.Gasto;
import com.ingelcom.cajachica.DAO.Usuario;
import com.ingelcom.cajachica.Fragmentos.FragGastosCuadrilla;
import com.ingelcom.cajachica.Herramientas.Exportaciones;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.SharedViewGastosModel;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.Modelos.GastosItems;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ListadoGastos extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, PopupMenu.OnMenuItemClickListener {

    private TextView btnReintentarConexion, lblTitulo, btnMes, btnAnio, lblFecha, lblLineaCuadrilla, lblLineaSupervisores;
    private Spinner spUserCompra;
    private LinearLayout llAbrirUserCompra;
    private String nombreActivity, nombreCuadrilla, fechaSeleccionada = "", tipoFecha = "Mes", tipoExportar;
    private ViewPager2 vpGastos;
    private SwipeRefreshLayout swlRecargar;
    private View viewNoInternet;
    private ProgressBar pbReintentarConexion;
    private ProgressDialog progressDialog;

    //Instancia de la clase "SharedViewGastosModel" que nos ayuda a compartir datos con diferentes componentes de la interfaz de usuario, como ser fragmentos y actividades y que estos datos sobreviven a cambios de configuración como las rotaciones de pantalla
    private SharedViewGastosModel svmGastos;

    private Gasto gast = new Gasto(ListadoGastos.this);
    private Usuario usu = new Usuario(ListadoGastos.this);
    private Exportaciones exp = new Exportaciones(ListadoGastos.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_gastos);

        inicializarElementos();
        establecerElementos();
        cambioFecha();
        cambioViewPager();
        desactivarSwipeEnViewPager();

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
        //Obtenemos el nombre del Activity y de la cuadrilla que se envía desde el activity anterior, lo hacemos llamando a la función "obtenerStringExtra" de la clase "Utilidades", y les mandamos "this" para referenciar esta actividad y el nombre de la clave del putExtra
        nombreActivity = Utilidades.obtenerStringExtra(this, "ActivityLG");
        nombreCuadrilla = Utilidades.obtenerStringExtra(this, "Cuadrilla");

        lblTitulo = findViewById(R.id.lblTituloLG);
        btnMes = findViewById(R.id.lblMesLG);
        btnAnio = findViewById(R.id.lblAnioLG);
        lblFecha = findViewById(R.id.lblFechaLG);
        lblLineaCuadrilla = findViewById(R.id.lblCuadrillaLineaLG);
        lblLineaSupervisores = findViewById(R.id.lblSupervisoresLineaLG);
        spUserCompra = findViewById(R.id.spUsuarioCompraLG);
        llAbrirUserCompra = findViewById(R.id.LLAbrirUsuarioCompraLG);
        vpGastos = findViewById(R.id.vpListadoGastos); //Relacionamos la variable "vpGastos" con el ViewPager
        swlRecargar = findViewById(R.id.swipeRefreshLayoutLG);
        viewNoInternet = findViewById(R.id.viewNoInternetLG);
        btnReintentarConexion = findViewById(R.id.btnReintentarConexion);
        pbReintentarConexion = findViewById(R.id.pbReintentarConexion);

        svmGastos = new ViewModelProvider(this).get(SharedViewGastosModel.class); //Obtenemos el ViewModel compartido, haciendo referencia a la clase "SharedViewGastosModel"
        swlRecargar.setOnRefreshListener(this); //Llamada al método "onRefresh"

        Utilidades.mostrarMensajePorInternetCaido(this, viewNoInternet); //Llamamos el método utilitario "mostrarMensajePorInternetCaido" donde mandamos la vista "viewNoInternet" donde se hará visible cuando no haya conexión a internet y se ocultará cuando si haya
    }

    private void establecerElementos() {
        swlRecargar.setColorSchemeResources(R.color.clr_fuente_primario); //Color del SwipeRefreshLayout

        switch (nombreActivity) { //Según el texto de "nombreActivity" que se recibe de la pantalla anterior, establecemos los elementos gráficos de este Activity
            case "ListadoGastosEmpleado":
                lblTitulo.setText("Listado de Gastos");
                break;

            case "ListadoGastosAdmin":
                lblTitulo.setText(nombreCuadrilla); //Establecemos el nombre de la cuadrilla en el titulo
                break;

            case "ListadoGastosTodos":
                lblTitulo.setText("Todos los Gastos");
                break;
        }

        lblLineaSupervisores.setVisibility(View.INVISIBLE); //Ocultamos la linea bajo la palabra "Supervisores" al iniciar el Activity

        VPGastosAdapter vpAdapter = new VPGastosAdapter(this, nombreCuadrilla, nombreActivity); //Creamos un objeto de "VPGastosAdapter" y le mandamos el contexto "this" de este Activity, el nombre de la cuadrilla y el nombre del Activity
        vpGastos.setAdapter(vpAdapter); //Asignamos el adaptador al vpGastos
    }

    private void obtenerDatosExportar(String mesAnio) {
        try {
            switch (nombreActivity) { //Dependiendo del "nombreActivity" que se recibe de la pantalla anterior, obtenemos los gastos para exportar de diferentes maneras
                case "ListadoGastosEmpleado":
                    //Llamamos el método "obtenerUnUsuario" de la clase "Usuario" que obtiene el usuario actual
                    usu.obtenerUsuarioActual(new FirestoreCallbacks.FirestoreDocumentCallback() {
                        @Override
                        public void onCallback(Map<String, Object> documento) {
                            if (documento != null) { //Si "documento" no es nulo, quiere decir que encontró el usuario mediante el correo
                                String cuadrilla = (String) documento.get("Cuadrilla"); //Obtenemos la cuadrilla de "documento"

                                //Llamamos el método "obtenerGastos" de la clase "Gastos", le mandamos la cuadrilla del usuario actual, el rol vacío ya que no queremos filtrar por rol y el "mes". Con esto se podrán obtener todos los gastos hechos por los empleados de la cuadrilla y por los supervisores a la cuadrilla
                                gast.obtenerGastos(cuadrilla, "", mesAnio, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<GastosItems>() {
                                    @Override
                                    public void onCallback(List<GastosItems> items) { //En esta lista "items" están todos los gastos ya filtrados por cuadrilla
                                        if (items != null) {//Si "items" no es null, que entre al if
                                            //Creamos un "ProgressDialog" por mientras se está realizando la exportación del archivo
                                            progressDialog = new ProgressDialog(ListadoGastos.this);
                                            progressDialog.setTitle("Exportando Gastos...");
                                            progressDialog.setCancelable(false);
                                            progressDialog.show();

                                            items = Utilidades.ordenarListaPorFechaHora(items, "fechaHora", "Descendente"); //Llamamos el método utilitario "ordenarListaPorFechaHora". Le mandamos la lista "items", el nombre del campo double "fechaHora", y el tipo de orden "Descendente". Este método retorna la lista ya ordenada y la guardamos en "items"
                                            List<GastosItems> finalItems = items;

                                            //Creamos una nueva instancia de "Handler", que está vinculada al Looper principal (el hilo principal de la aplicación). Esto asegura que cualquier operación realizada dentro de este Handler se ejecute en el hilo principal
                                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { //El "Handler" utiliza el método "postDelayed" para ejecutar el "Runnable" que contiene las acciones a realizar después de un retraso especificado (en este caso, 1000 milisegundos, es decir, 1 segundo)
                                                @Override
                                                public void run() {
                                                    if (mesAnio.isEmpty() || mesAnio.equalsIgnoreCase("Seleccionar...")) { //Si "mes" que se recibe como parámetro está vacío o tiene el texto "Seleccionar Mes" significa que no se hará algún filtrado al momento de exportar los datos
                                                        if (tipoExportar.equalsIgnoreCase("EXCEL")) //Si la variable global "tipoExportar" tiene el texto "EXCEL", significa que se quieren exportar los datos a excel, que entre al if
                                                            exp.exportarGastosExcel(finalItems, "_" + cuadrilla); //Llamamos el método "exportarGastosExcel" de la clase "Exportaciones" donde mandamos la lista "items" y un texto que servirá para el nombre del archivo al crearlo
                                                        else if (tipoExportar.equalsIgnoreCase("PDF")) //En cambio, si la variable global "tipoExportar" tiene el texto "PDF", significa que se quieren exportar los datos a pdf, que entre al else if
                                                            exp.exportarGastosPDF(finalItems, "_" + cuadrilla); //Llamamos el método "exportarGastosPDF" de la clase "Exportaciones" donde mandamos la lista "items" y un texto que servirá para el nombre del archivo al crearlo
                                                    }
                                                    else { //En cambio, si "mes" contiene un texto diferente a "Seleccionar Mes", eso quiere decir que si se hizo un filtrado en los datos
                                                        if (tipoExportar.equalsIgnoreCase("EXCEL")) //Si la variable global "tipoExportar" tiene el texto "EXCEL", significa que se quieren exportar los datos a excel, que entre al if
                                                            exp.exportarGastosExcel(finalItems, "_" + cuadrilla + "_" + mesAnio); //Llamamos el método "exportarGastosExcel" de la clase "Exportaciones" donde mandamos la lista "items" y un texto que servirá para el nombre del archivo al crearlo
                                                        else if (tipoExportar.equalsIgnoreCase("PDF")) //En cambio, si la variable global "tipoExportar" tiene el texto "PDF", significa que se quieren exportar los datos a pdf, que entre al else if
                                                            exp.exportarGastosPDF(finalItems, "_" + cuadrilla + "_" + mesAnio); //Llamamos el método "exportarGastosPDF" de la clase "Exportaciones" donde mandamos la lista "items" y un texto que servirá para el nombre del archivo al crearlo
                                                    }

                                                    if (progressDialog.isShowing()) //Si "progressDialog" se está mostrando, que entre al if
                                                        progressDialog.dismiss(); //Eliminamos el "progressDialog" ya cuando el proceso de exportación
                                                }
                                            }, 1000);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.w("ObtenerGastos", e);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.w("ObtenerUsuario", e);
                        }
                    });
                    break;

                case "ListadoGastosAdmin":
                    //Llamamos el método "obtenerGastos" de la clase "Gastos", le mandamos la cuadrilla recibida del activity anterior, el rol vacío ya que no queremos filtrar por rol y el "mes". Con esto se podrán obtener todos los gastos hechos por los empleados de la cuadrilla y por los supervisores a la cuadrilla
                    gast.obtenerGastos(nombreCuadrilla, "", mesAnio, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<GastosItems>() {
                        @Override
                        public void onCallback(List<GastosItems> items) { //En esta lista "items" están todos los gastos ya filtrados por cuadrilla
                            if (items != null) {//Si "items" no es null, que entre al if
                                //Creamos un "ProgressDialog" por mientras se está realizando la exportación del archivo
                                progressDialog = new ProgressDialog(ListadoGastos.this);
                                progressDialog.setTitle("Exportando Gastos...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                items = Utilidades.ordenarListaPorFechaHora(items, "fechaHora", "Descendente"); //Llamamos el método utilitario "ordenarListaPorFechaHora". Le mandamos la lista "items", el nombre del campo double "fechaHora", y el tipo de orden "Descendente". Este método retorna la lista ya ordenada y la guardamos en "items"
                                List<GastosItems> finalItems = items;

                                //Creamos una nueva instancia de "Handler", que está vinculada al Looper principal (el hilo principal de la aplicación). Esto asegura que cualquier operación realizada dentro de este Handler se ejecute en el hilo principal
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { //El "Handler" utiliza el método "postDelayed" para ejecutar el "Runnable" que contiene las acciones a realizar después de un retraso especificado (en este caso, 1000 milisegundos, es decir, 1 segundo)
                                    @Override
                                    public void run() {
                                        if (mesAnio.isEmpty() || mesAnio.equalsIgnoreCase("Seleccionar...")) { //Si "mes" que se recibe como parámetro está vacío o tiene el texto "Seleccionar Mes" significa que no se hará algún filtrado al momento de exportar los datos
                                            if (tipoExportar.equalsIgnoreCase("EXCEL")) //Si la variable global "tipoExportar" tiene el texto "EXCEL", significa que se quieren exportar los datos a excel, que entre al if
                                                exp.exportarGastosExcel(finalItems, "_" + nombreCuadrilla); //Llamamos el método "exportarGastosExcel" de la clase "Exportaciones" donde mandamos la lista "items" y un texto que servirá para el nombre del archivo al crearlo
                                            else if (tipoExportar.equalsIgnoreCase("PDF")) //En cambio, si la variable global "tipoExportar" tiene el texto "PDF", significa que se quieren exportar los datos a pdf, que entre al else if
                                                exp.exportarGastosPDF(finalItems, "_" + nombreCuadrilla); //Llamamos el método "exportarGastosPDF" de la clase "Exportaciones" donde mandamos la lista "items" y un texto que servirá para el nombre del archivo al crearlo
                                        }
                                        else { //En cambio, si "mes" contiene un texto diferente a "Seleccionar Mes", eso quiere decir que si se hizo un filtrado en los datos
                                            if (tipoExportar.equalsIgnoreCase("EXCEL")) //Si la variable global "tipoExportar" tiene el texto "EXCEL", significa que se quieren exportar los datos a excel, que entre al if
                                                exp.exportarGastosExcel(finalItems, "_" + nombreCuadrilla + "_" + mesAnio); //Llamamos el método "exportarGastosExcel" de la clase "Exportaciones" donde mandamos la lista "items" y un texto que servirá para el nombre del archivo al crearlo
                                            else if (tipoExportar.equalsIgnoreCase("PDF")) //En cambio, si la variable global "tipoExportar" tiene el texto "PDF", significa que se quieren exportar los datos a pdf, que entre al else if
                                                exp.exportarGastosPDF(finalItems, "_" + nombreCuadrilla + "_" + mesAnio); //Llamamos el método "exportarGastosPDF" de la clase "Exportaciones" donde mandamos la lista "items" y un texto que servirá para el nombre del archivo al crearlo
                                        }

                                        if (progressDialog.isShowing()) //Si "progressDialog" se está mostrando, que entre al if
                                            progressDialog.dismiss(); //Eliminamos el "progressDialog" ya cuando el proceso de exportación ha finalizado
                                    }
                                }, 1000);
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.w("ObtenerGastos", e);
                        }
                    });
                    break;

                case "ListadoGastosTodos":
                    //Llamamos el método "obtenerGastos" de la clase "Gastos", le mandamos la cuadrilla vacía porque queremos obtener todos los gastos, el rol vacío ya que no queremos filtrar por rol y el "mes". Con esto se podrán obtener todos los gastos hechos en todas las cuadrillas por los empleados y supervisores
                    gast.obtenerGastos("", "", mesAnio, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<GastosItems>() {
                        @Override
                        public void onCallback(List<GastosItems> items) { //En esta lista "items" están todos los gastos sin filtro
                            if (items != null) {//Si "items" no es null, que entre al if
                                //Creamos un "ProgressDialog" por mientras se está realizando la exportación del archivo
                                progressDialog = new ProgressDialog(ListadoGastos.this);
                                progressDialog.setTitle("Exportando Gastos...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                items = Utilidades.ordenarListaPorFechaHora(items, "fechaHora", "Descendente"); //Llamamos el método utilitario "ordenarListaPorFechaHora". Le mandamos la lista "items", el nombre del campo double "fechaHora", y el tipo de orden "Descendente". Este método retorna la lista ya ordenada y la guardamos en "items"
                                List<GastosItems> finalItems = items;

                                //Creamos una nueva instancia de "Handler", que está vinculada al Looper principal (el hilo principal de la aplicación). Esto asegura que cualquier operación realizada dentro de este Handler se ejecute en el hilo principal
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { //El "Handler" utiliza el método "postDelayed" para ejecutar el "Runnable" que contiene las acciones a realizar después de un retraso especificado (en este caso, 1000 milisegundos, es decir, 1 segundo)
                                    @Override
                                    public void run() {
                                        if (mesAnio.isEmpty() || mesAnio.equalsIgnoreCase("Seleccionar...")) { //Si "mes" que se recibe como parámetro está vacío o tiene el texto "Seleccionar Mes" significa que no se hará algún filtrado al momento de exportar los datos
                                            if (tipoExportar.equalsIgnoreCase("EXCEL")) //Si la variable global "tipoExportar" tiene el texto "EXCEL", significa que se quieren exportar los datos a excel, que entre al if
                                                exp.exportarGastosExcel(finalItems, "Generales"); //Llamamos el método "exportarGastosExcel" de la clase "Exportaciones" donde mandamos la lista "items" y un texto que servirá para el nombre del archivo al crearlo
                                            else if (tipoExportar.equalsIgnoreCase("PDF")) //En cambio, si la variable global "tipoExportar" tiene el texto "PDF", significa que se quieren exportar los datos a pdf, que entre al else if
                                                exp.exportarGastosPDF(finalItems, "Generales"); //Llamamos el método "exportarGastosPDF" de la clase "Exportaciones" donde mandamos la lista "items" y un texto que servirá para el nombre del archivo al crearlo
                                        }
                                        else { //En cambio, si "mes" contiene un texto diferente a "Seleccionar Mes", eso quiere decir que si se hizo un filtrado en los datos
                                            if (tipoExportar.equalsIgnoreCase("EXCEL")) //Si la variable global "tipoExportar" tiene el texto "EXCEL", significa que se quieren exportar los datos a excel, que entre al if
                                                exp.exportarGastosExcel(finalItems, "Generales_" + mesAnio); //Llamamos el método "exportarGastosExcel" de la clase "Exportaciones" donde mandamos la lista "items" y un texto que servirá para el nombre del archivo al crearlo
                                            else if (tipoExportar.equalsIgnoreCase("PDF")) //En cambio, si la variable global "tipoExportar" tiene el texto "PDF", significa que se quieren exportar los datos a pdf, que entre al else if
                                                exp.exportarGastosPDF(finalItems, "Generales_" + mesAnio); //Llamamos el método "exportarGastosPDF" de la clase "Exportaciones" donde mandamos la lista "items" y un texto que servirá para el nombre del archivo al crearlo
                                        }

                                        if (progressDialog.isShowing()) //Si "progressDialog" se está mostrando, que entre al if
                                            progressDialog.dismiss(); //Eliminamos el "progressDialog" ya cuando el proceso de exportación ha finalizado
                                    }
                                }, 1000);
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.w("ObtenerGastos", e);
                        }
                    });
                    break;
            }
        }
        catch (Exception e) {
            Log.w("ObtenerGastos", e);
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

    //Método que detecta cuando el lblFecha cambia su valor
    private void cambioFecha() {
        try {
            //Para detectar cuando el lblFecha cambia su valor, llamamos el método "addTextChangedListener"
            lblFecha.addTextChangedListener(new TextWatcher() {
                @Override //Antes de que el texto del lblFecha cambie
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override //Durante el texto del lblFecha está cambiando
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    fechaSeleccionada = charSequence.toString(); //Pasamos la fecha a la variable global "nombreMes"
                    svmGastos.setFecha(charSequence.toString()); //Llamamos el método "setFecha" de la clase "SharedViewGastosModel" y le mandamos el "charSequence" que es el texto del TextView "lblFecha" y lo convertimos a String
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

    //Método que permite establecer las lineas bajo las palabras "Cuadrilla" y "Supervisores" cuando se arrastren los fragments del ViewPager
    private void cambioViewPager() {
        //Evento que detecta cuando el ViewPager cambia su posición o se está visualizando otro fragment en él
        vpGastos.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0: //Si la posición es 0, se está visualizando el listado de gastos de cuadrilla
                        lblLineaCuadrilla.setVisibility(View.VISIBLE);
                        lblLineaSupervisores.setVisibility(View.INVISIBLE);
                        break;
                    case 1: //Si la posición es 1, se está visualizando el listado de gastos de supervisores
                        lblLineaCuadrilla.setVisibility(View.INVISIBLE);
                        lblLineaSupervisores.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }

    //Evento Clic del LinearLayout "LLCuadrillaLG"
    public void gastosCuadrilla(View view) {
        vpGastos.setCurrentItem(0); //Si damos clic en la palabra "Cuadrilla", que se muestre el fragment de la posición 0 en el ViewPager (El "FragGastosCuadrilla()")
        //Ocultamos y mostramos las lineas bajo las palabras "Cuadrilla" y "Supervisores" dependiendo el fragment que se esté mostrando
        lblLineaCuadrilla.setVisibility(View.VISIBLE);
        lblLineaSupervisores.setVisibility(View.INVISIBLE);
    }

    //Evento Clic del LinearLayout "LLSupervisoresLG"
    public void gastosSupervisores(View view) {
        vpGastos.setCurrentItem(1); //Si damos clic en la palabra "Supervisores", que se muestre el fragment de la posición 1 en el ViewPager (El "FragGastosSupervisores()")
        //Ocultamos y mostramos las lineas bajo las palabras "Cuadrilla" y "Supervisores" dependiendo el fragment que se esté mostrando
        lblLineaSupervisores.setVisibility(View.VISIBLE);
        lblLineaCuadrilla.setVisibility(View.INVISIBLE);
    }

    //Evento Clic del LinearLayout de Fecha, al dar clic en el mismo, se abrirá un "Popup DatePicker" en el que se podrá seleccionar un mes y año y esto servirá para filtrar los gastos
    public void mostrarMesesAnios(View view) {
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(ListadoGastos.this, style, dateSetListener, year, month, day); //Creamos un nuevo objeto de tipo DatePickerDialog y le mandamos como parámetros al constructor, un contexto, la variable "style" que guarda el estilo, el "dateSetListener", el año, mes y día, estos últimos para que al abrir el AlertDialog, se muestre el mes actual
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

            DatePickerDialog datePickerDialog = new DatePickerDialog(ListadoGastos.this, style, dateSetListener, year, month, day); //Creamos un nuevo objeto de tipo DatePickerDialog y le mandamos como parámetros al constructor, un contexto, la variable "style" que guarda el estilo, el "dateSetListener", el año, mes y día, estos últimos para que al abrir el AlertDialog, se muestre el mes actual
            datePickerDialog.getDatePicker().findViewById(getResources().getIdentifier("day", "id", "android")).setVisibility(View.GONE); //Ocultamos el spinner de días asignando "GONE" en su visibilidad
            datePickerDialog.getDatePicker().findViewById(getResources().getIdentifier("month", "id", "android")).setVisibility(View.GONE); //Ocultamos el spinner de meses asignando "GONE" en su visibilidad
            datePickerDialog.show(); //Mostramos el AlertDialog o Popup DatePicker de solo años
        }
    }

    //Método para eliminar la selección del Mes - Año
    public void eliminarMesGastos(View view) {
        lblFecha.setText("Seleccionar...");
    }

    private void desactivarSwipeEnViewPager() {
        //Desactivamos el SwipeRefreshLayout mientras se arrastra el ViewPager2
        vpGastos.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
            // Desactivar SwipeRefreshLayout cuando se arrastra el ViewPager
            if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                swlRecargar.setEnabled(false);
            }
            else {
                swlRecargar.setEnabled(true);
            }
            }
        });
    }

    public void exportarGastos(View view) {
        PopupMenu popup = new PopupMenu(this, view); //Objeto de tipo "PopupMenu"
        popup.setOnMenuItemClickListener(this); //Indicamos que asigne el evento "OnMenuItemClick" para que haga algo cada vez que se dé click a una opción del menú
        popup.inflate(R.menu.popupmenu_opcionesexportar); //Inflamos la vista del menú indicando la ruta de dicha vista gráfica

        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menuExportarExcel:
                tipoExportar = "EXCEL"; //Cuando el usuario dé clic en la opción de "Exportar a Excel" del PopupMenu "opcionesExportar", asignamos el texto "EXCEL" a la variable global "tipoExportar"

                //Llamamos el método utilitario "verificarPermisosAlmacenamiento" donde mandamos el contexto de esta clase. Si este método devuelve un "true" significa que los permisos de almacenamiento externo ya han sido otorgados, en ese caso que entre al if
                if (Utilidades.verificarPermisosAlmacenamiento(this)) {
                    obtenerDatosExportar(fechaSeleccionada); //Como los permisos han sido otorgados, llamamos el método "obtenerDatosExportar" de arriba y le mandamos la variable global "nombreMes"
                }

                return true;

            case R.id.menuExportarPDF:
                tipoExportar = "PDF"; //Cuando el usuario dé clic en la opción de "Exportar a PDF" del PopupMenu "opcionesExportar", asignamos el texto "PDF" a la variable global "tipoExportar"

                //Llamamos el método utilitario "verificarPermisosAlmacenamiento" donde mandamos el contexto de esta clase. Si este método devuelve un "true" significa que los permisos de almacenamiento externo ya han sido otorgados, en ese caso que entre al if
                if (Utilidades.verificarPermisosAlmacenamiento(this)) {
                    obtenerDatosExportar(fechaSeleccionada); //Como los permisos han sido otorgados, llamamos el método "obtenerDatosExportar" de arriba y le mandamos la variable global "nombreMes"
                }

                return true;

            default:
                return false;
        }
    }

    @Override //Método Override que solicita los permisos de almacenamiento externo al usuario
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //If que llama al método utilitario "manejarResultadoPermisos", y le manda los datos necesarios para verificar si los permisos han sido otorgados, si así lo fue, el método retornará un "true", por lo tanto, que entre al if
        if (Utilidades.manejarResultadoPermisos(requestCode, permissions, grantResults, this)) {
            //Ya que los permisos de almacenamiento externo han sido otorgados, verificamos el contenido de la variable global "tipoExportar" para llamar al método "obtenerDatosExportar"
            if (tipoExportar.equalsIgnoreCase("EXCEL"))
                obtenerDatosExportar(fechaSeleccionada);
            else if (tipoExportar.equalsIgnoreCase("PDF"))
                obtenerDatosExportar(fechaSeleccionada);
        }
    }

    @Override
    public void onRefresh() { //Método que detecta cuando se recarga la pantalla con SwipeRefreshLayout
        //Creamos una nueva instancia de "Handler", que está vinculada al Looper principal (el hilo principal de la aplicación). Esto asegura que cualquier operación realizada dentro de este Handler se ejecute en el hilo principal
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { //El "Handler" utiliza el método "postDelayed" para ejecutar el "Runnable" que contiene las acciones a realizar después de un retraso especificado (en este caso, 1000 milisegundos, es decir, 1 segundo)
            @Override
            public void run() {
                Utilidades.mostrarMensajePorInternetCaido(ListadoGastos.this, viewNoInternet); //Llamamos el método utilitario "mostrarMensajePorInternetCaido" donde mandamos la vista "viewNoInternet" donde se hará visible cuando no haya conexión a internet y se ocultará cuando si haya
                svmGastos.setRecargar("Recargar"); //Cada vez que se recargue la pantalla, establecemos el texto "Recargar" en el "setRecargar" del "svmGastos"
                swlRecargar.setRefreshing(false); //Llamamos a este método para detener la animación de refresco
            }
        }, 1000);
    }

    //Método que permite retroceder a la pantalla anterior
    public void retroceder(View view) {
        onBackPressed();
    }
}