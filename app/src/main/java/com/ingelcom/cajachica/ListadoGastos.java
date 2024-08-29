package com.ingelcom.cajachica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.ingelcom.cajachica.Adaptadores.VPGastosAdapter;
import com.ingelcom.cajachica.DAO.FirestoreOperaciones;
import com.ingelcom.cajachica.DAO.Gasto;
import com.ingelcom.cajachica.DAO.Usuario;
import com.ingelcom.cajachica.Herramientas.Exportaciones;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.SharedViewGastosModel;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.Modelos.GastosItems;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ListadoGastos extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, PopupMenu.OnMenuItemClickListener {

    private TextView btnReintentarConexion, lblTitulo, btnMes, btnAnio, lblFecha, lblUserCompra, lblLineaCuadrilla, lblLineaSupervisores;
    private Spinner spUserCompra;
    private LinearLayout llUserCompra, llAbrirUserCompra;
    private ImageView imgUserCompra;
    private String nombreActivity, nombreCuadrilla, fechaSeleccionada = "", tipoFecha = "Mes", userCompra = "", tipoExportar;
    private boolean userCompraSelectVisible = true;
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
    private FirestoreOperaciones oper = new FirestoreOperaciones();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_gastos);

        inicializarElementos();
        establecerElementos();
        cambioFecha();
        cambioUserCompra();
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
        lblUserCompra = findViewById(R.id.lblAbrirUsuarioCompraLG);
        lblLineaCuadrilla = findViewById(R.id.lblCuadrillaLineaLG);
        lblLineaSupervisores = findViewById(R.id.lblSupervisoresLineaLG);
        spUserCompra = findViewById(R.id.spUsuarioCompraLG);
        llUserCompra = findViewById(R.id.LLUsuarioCompraLG);
        llAbrirUserCompra = findViewById(R.id.LLAbrirUsuarioCompraLG);
        imgUserCompra = findViewById(R.id.imgUsuarioCompraLG);
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
                imgUserCompra.setImageResource(R.mipmap.ico_azul_tipocompra); //Asignamos el icono de tipoCompra ya que en "ListadoGastosEmpleado" se filtrarán los gastos por tipo de compra
                lblUserCompra.setText("Seleccionar Categoría");
                inicializarSpinners("TipoCompra");
                //llUserCompra.setVisibility(View.GONE);
                break;

            case "ListadoGastosAdmin":
                lblTitulo.setText(nombreCuadrilla); //Establecemos el nombre de la cuadrilla en el titulo
                lblUserCompra.setText("Seleccionar Categoría");
                imgUserCompra.setImageResource(R.mipmap.ico_azul_tipocompra); //Asignamos el icono de tipoCompra ya que en "ListadoGastosAdmin" se filtrarán los gastos por tipo de compra
                inicializarSpinners("TipoCompra");
                break;

            case "ListadoGastosTodos":
                lblTitulo.setText("Todos los Gastos");
                lblUserCompra.setText("Seleccionar Usuario");
                imgUserCompra.setImageResource(R.mipmap.ico_azul_nombreapellido); //Asignamos el icono de nombreApellido (o de Usuario) ya que en "ListadoGastosTodos" se filtrarán los gastos por usuario
                inicializarSpinners("Usuario");
                break;
        }

        lblLineaSupervisores.setVisibility(View.INVISIBLE); //Ocultamos la linea bajo la palabra "Supervisores" al iniciar el Activity

        VPGastosAdapter vpAdapter = new VPGastosAdapter(this, nombreCuadrilla, nombreActivity); //Creamos un objeto de "VPGastosAdapter" y le mandamos el contexto "this" de este Activity, el nombre de la cuadrilla y el nombre del Activity
        vpGastos.setAdapter(vpAdapter); //Asignamos el adaptador al vpGastos
    }

    private void inicializarSpinners(String tipo) {
        if (tipo.equalsIgnoreCase("TipoCompra")) {
            try {
                oper.obtenerRegistrosCampo("tipoCompras", "Nombre", new FirestoreCallbacks.FirestoreListCallback() {
                    @Override
                    public void onCallback(List<String> lista) {
                        //Ordenamos la "lista" alfabéticamente llamando al método utilitario "ordenarListaPorAlfabetico" donde enviamos la lista, un String vacío ("") y el orden ascendente. El String vacío es para indicar que el "nombreCampo" por el cual se desea realizar el orden de la lista, en este caso no existe ya que es una lista sencilla y no de una clase
                        lista = Utilidades.ordenarListaPorAlfabetico(lista, "", "Ascendente");
                        List<String> listaNueva = new ArrayList<>();
                        listaNueva.add("Seleccionar Categoría");
                        listaNueva.addAll(lista);

                        //Creamos un adapter de tipo ArrayAdapter el cual le pasamos el contexto de este Activity, la vista layout de las opciones del Spinner (R.layout.spinner_items), y la lista de valores que se recibe en "lista" al llamar a la interfaz FirestoreCallback
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ListadoGastos.this, R.layout.spinner_usercompraitems, listaNueva) {
                            @Override //Este método se usa para personalizar cómo se ve cada elemento en la lista desplegable del Spinner
                            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                View view = super.getDropDownView(position, convertView, parent);

                                if (position == 0) //Si la posición es 0, significa que es el primer elemento del Spinner
                                    view.setVisibility(View.GONE); //Ocultamos el primer elemento en la lista desplegable
                                else //Para los demás elementos del Spinner cuya posición no será 0
                                    view.setVisibility(View.VISIBLE); //Nos aseguramos que dichos elementos sean visibles

                                return view;
                            }

                            @Override //Este método se usa para personalizar cómo se ve el elemento actualmente seleccionado en el Spinner
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView textView = (TextView) view.findViewById(R.id.txtSpinnerUserCompra); //Obtenemos la referencia al TextView dentro del diseño del Spinner (R.layout.spinner_usercompraitems)

                                return view;
                            }
                        };

                        spUserCompra.setAdapter(adapter); //Configuramos el adaptador personalizado
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.w("Activity", "Error al obtener los tipos de compras.", e);
                    }
                });
            }
            catch (Exception e) {
                Log.w("ObtenerTipoCompras", e);
            }
        }
        else if (tipo.equalsIgnoreCase("Usuario")) {
            try {
                oper.obtenerRegistrosCampo("usuarios", "Nombre", new FirestoreCallbacks.FirestoreListCallback() {
                    @Override
                    public void onCallback(List<String> lista) {
                        //Ordenamos la "lista" alfabéticamente llamando al método utilitario "ordenarListaPorAlfabetico" donde enviamos la lista, un String vacío ("") y el orden ascendente. El String vacío es para indicar que el "nombreCampo" por el cual se desea realizar el orden de la lista, en este caso no existe ya que es una lista sencilla y no de una clase
                        lista = Utilidades.ordenarListaPorAlfabetico(lista, "", "Ascendente");
                        List<String> listaNueva = new ArrayList<>();
                        listaNueva.add("Seleccionar Usuario");
                        listaNueva.addAll(lista);

                        //Creamos un adapter de tipo ArrayAdapter el cual le pasamos el contexto de este Activity, la vista layout de las opciones del Spinner (R.layout.spinner_items), y la lista de valores que se recibe en "lista" al llamar a la interfaz FirestoreCallback
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ListadoGastos.this, R.layout.spinner_usercompraitems, listaNueva) {
                            @Override //Este método se usa para personalizar cómo se ve cada elemento en la lista desplegable del Spinner
                            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                View view = super.getDropDownView(position, convertView, parent);

                                if (position == 0) //Si la posición es 0, significa que es el primer elemento del Spinner
                                    view.setVisibility(View.GONE); //Ocultamos el primer elemento en la lista desplegable
                                else //Para los demás elementos del Spinner cuya posición no será 0
                                    view.setVisibility(View.VISIBLE); //Nos aseguramos que dichos elementos sean visibles

                                return view;
                            }

                            @Override //Este método se usa para personalizar cómo se ve el elemento actualmente seleccionado en el Spinner
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView textView = (TextView) view.findViewById(R.id.txtSpinnerUserCompra); //Obtenemos la referencia al TextView dentro del diseño del Spinner (R.layout.spinner_usercompraitems)

                                return view;
                            }
                        };

                        /*//Creamos un adapter de tipo ArrayAdapter el cual le pasamos el contexto de este Activity, la vista layout de las opciones del Spinner (R.layout.spinner_items), y la lista de valores que se recibe en "lista" al llamar a la interfaz FirestoreCallback
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(ListadoGastos.this, R.layout.spinner_usercompraitems, lista);
                        spUserCompra.setAdapter(adapter); //Asignamos el adapter al Spinner "spUserCompra"*/

                        spUserCompra.setAdapter(adapter); //Configuramos el adaptador personalizado
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.w("Activity", "Error al obtener los usuarios.", e);
                    }
                });
            }
            catch (Exception e) {
                Log.w("ObtenerUsuarios", e);
            }
        }
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
                                gast.obtenerGastos(cuadrilla, "", "", userCompra, mesAnio, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<GastosItems>() {
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
                    gast.obtenerGastos(nombreCuadrilla, "", "", userCompra, mesAnio, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<GastosItems>() {
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
                    gast.obtenerGastos("", "", userCompra, "", mesAnio, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<GastosItems>() {
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

    //Método que detecta cuando el spinner "spUserCompra" cambia su selección
    private void cambioUserCompra() {
        try {
            //Para detectar cuando "spUserCompra" cambia su selección, llamamos el método "setOnItemSelectedListener"
            spUserCompra.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long id) {
                    if (i == 0) { //Si la posición seleccionada "i" del Spinner es 0, mandamos un "" con el "svmGastos" y que "userCompra" también esté vacío
                        userCompra = "";
                        svmGastos.setUserCompra("");
                    }
                    else { //En cambio, si no es la posición 0, guardamos el texto del elemento seleccionado en "userCompra" y lo mandamos con el "svmGastos"
                        userCompra = adapterView.getItemAtPosition(i).toString(); //Obtenemos la selección del "spUserCompra" en la variable global "userCompra"
                        svmGastos.setUserCompra(userCompra); //Llamamos el método "setFecha" de la clase "SharedViewGastosModel" y le mandamos el contenido selecciondo del Spinner guardado en "userCompra"
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    //Este método se ejecuta cuando no se selecciona ningún elemento
                }
            });
        }
        catch (Exception e) {
            Log.w("DetectarUserCompra", e);
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

    //Método clic que oculta el texto "Seleccionar Usuario" o "Seleccionar Categoría" y muestra el "spUserCompra"
    public void mostrarSpinner(View view) {
        /*llAbrirUserCompra.setVisibility(View.GONE);
        spUserCompra.setVisibility(View.VISIBLE);
        spUserCompra.performClick(); //Evento que abre el Spinner y muestra sus opciones

        userCompraSelectVisible = false; //Guardamos un false en la variable global "userCompraSelectVisible" para indicar que el texto "Seleccionar Usuario" o "Seleccionar Categoría" está oculto
        svmGastos.setUserCompra(spUserCompra.getSelectedItem().toString());*/
    }

    //Método para eliminar la selección del Mes - Año
    public void eliminarMesGastos(View view) {
        lblFecha.setText("Seleccionar...");
    }

    //Método que permita mostrar el texto "Seleccionar Usuario" o "Seleccionar Categoría" y ocultar el "spUserCompra"
    public void eliminarSeleccionSpinner(View view) {
        /*llAbrirUserCompra.setVisibility(View.VISIBLE);
        spUserCompra.setVisibility(View.GONE);

        userCompraSelectVisible = true; //Guardamos un true en la variable global "userCompraSelectVisible" para indicar que el texto "Seleccionar Usuario" o "Seleccionar Categoría" está visible
        */;

        spUserCompra.setSelection(0);
        svmGastos.setUserCompra("");
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