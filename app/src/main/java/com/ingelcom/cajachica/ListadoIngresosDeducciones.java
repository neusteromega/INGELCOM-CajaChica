package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.ingelcom.cajachica.Adaptadores.DeduccionesAdapter;
import com.ingelcom.cajachica.Adaptadores.IngresosAdapter;
import com.ingelcom.cajachica.DAO.Deduccion;
import com.ingelcom.cajachica.DAO.Ingreso;
import com.ingelcom.cajachica.DAO.Usuario;
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

public class ListadoIngresosDeducciones extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private LinearLayout llFecha;
    private TextView lblTitulo, lblFecha, lblTotal, lblTotalTitulo;
    private String nombreActivity, nombreCuadrilla;
    private RecyclerView rvIngrDeduc;

    private Ingreso ingr = new Ingreso(ListadoIngresosDeducciones.this);
    private Deduccion deduc = new Deduccion(ListadoIngresosDeducciones.this);
    private Usuario usu = new Usuario(ListadoIngresosDeducciones.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_ingresos_deducciones);

        inicializarElementos();
        establecerElementos();
        obtenerDatos("");
        cambioFecha();
    }

    private void inicializarElementos() {
        //Obtenemos el nombre del activity y de la cuadrilla que se envía desde el activity anterior (el "nombreCuadrilla" puede quedar vacío si el activity anterior no fue AdmDatosCuadrilla, ya que sólo aquí se manda el nombre de la cuadrilla)
        nombreActivity = Utilidades.obtenerStringExtra(this, "ActivityLID");
        nombreCuadrilla = Utilidades.obtenerStringExtra(this, "Cuadrilla");

        llFecha = findViewById(R.id.LLFechaLI);
        lblTitulo = findViewById(R.id.lblTituloLI);
        lblFecha = findViewById(R.id.lblFechaLI);
        lblTotal = findViewById(R.id.lblCantIngresosLI);
        lblTotalTitulo = findViewById(R.id.lblTotalIngresosLI);
        rvIngrDeduc = findViewById(R.id.rvListadoIngrDeduc);
    }

    private void establecerElementos() {
        switch (nombreActivity) {
            case "ListadoIngresosAdmin":
                lblTitulo.setText(nombreCuadrilla);
                lblTotalTitulo.setText("Total de Ingresos");
                lblTotal.setTextColor(getResources().getColor(R.color.clr_fuente_ingresos));
                break;

            case "ListadoIngresosEmpleado":
                lblTitulo.setText("Listado de Ingresos");
                lblTotalTitulo.setText("Total de Ingresos");
                lblTotal.setTextColor(getResources().getColor(R.color.clr_fuente_ingresos));
                break;

            case "ListadoDeducciones":
                llFecha.setVisibility(View.GONE);
                lblTitulo.setText(nombreCuadrilla);
                lblTotalTitulo.setText("Total de Deducciones");
                lblTotal.setTextColor(getResources().getColor(R.color.clr_fuente_secundario));
                break;
        }
    }

    private void obtenerDatos(String mes) {
        try {
            if (nombreActivity.contentEquals("ListadoIngresosAdmin")) {
                //Llamamos el método "obtenerIngresos" de la clase "Ingreso", le mandamos la cuadrilla recibida en "nombreCuadrilla" y el "mes". Con esto se podrán obtener todos los ingresos hechos por los administradores
                ingr.obtenerIngresos(nombreCuadrilla, mes, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<IngresosItems>() {
                    @Override
                    public void onCallback(List<IngresosItems> items) { //En esta lista "items" están todos los ingresos ya filtrados por cuadrilla
                        if (items != null) //Si "items" no es null, que entre al if
                            inicializarRecyclerView(items, "Ingresos"); //Llamamos el método "inicializarRecyclerView" de abajo y le mandamos la lista "items"
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(ListadoIngresosDeducciones.this, "ERROR AL CARGAR LOS INGRESOS", Toast.LENGTH_SHORT).show();
                        Log.w("ObtenerIngresos", e);
                    }
                });
            }
            else if (nombreActivity.contentEquals("ListadoIngresosEmpleado")) {
                //Llamamos el método "obtenerUnUsuario" de la clase "Usuario" que obtiene el usuario actual
                usu.obtenerUsuarioActual(new FirestoreCallbacks.FirestoreDocumentCallback() {
                    @Override
                    public void onCallback(Map<String, Object> documento) {
                        if (documento != null) { //Si "documento" no es nulo, quiere decir que encontró el usuario mediante el correo
                            String cuadrilla = (String) documento.get("Cuadrilla"); //Obtenemos la cuadrilla de "documento"

                            //Llamamos el método "obtenerIngresos" de la clase "Ingreso", le mandamos la cuadrilla obtenida de Firestore en "cuadrilla" y el "mes". Con esto se podrán obtener todos los ingresos hechos por los administradores
                            ingr.obtenerIngresos(cuadrilla, mes, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<IngresosItems>() {
                                @Override
                                public void onCallback(List<IngresosItems> items) { //En esta lista "items" están todos los ingresos ya filtrados por cuadrilla
                                    if (items != null) //Si "items" no es null, que entre al if
                                        inicializarRecyclerView(items, "Ingresos"); //Llamamos el método "inicializarRecyclerView" de abajo y le mandamos la lista "items"
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
            else if (nombreActivity.contentEquals("ListadoDeducciones")) {
                //Llamamos el método "obtenerDeducciones" de la clase "Deduccion", le mandamos la cuadrilla recibida en "nombreCuadrilla". Con esto se podrán obtener todas las deducciones por planilla hechas por los administradores
                deduc.obtenerDeducciones(nombreCuadrilla, new FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<DeduccionesItems>() {
                    @Override
                    public void onCallback(List<DeduccionesItems> items) { //En esta lista "items" están todas las deducciones ya filtradas por cuadrilla
                        if (items != null) //Si "items" no es null, que entre al if
                            inicializarRecyclerView(items, "Deducciones"); //Llamamos el método "inicializarRecyclerView" de abajo y le mandamos la lista "items"
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
            IngresosAdapter adapter = new IngresosAdapter(ingresosItems); //Creamos un nuevo objeto de tipo IngresosAdapter en el cual enviamos la lista "items"
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
                    datosDeduccion.put("Usuario", deduccionesItems.get(rvIngrDeduc.getChildAdapterPosition(view)).getUsuario());
                    datosDeduccion.put("FechaHora", deduccionesItems.get(rvIngrDeduc.getChildAdapterPosition(view)).getFechaHora());
                    datosDeduccion.put("Cuadrilla", deduccionesItems.get(rvIngrDeduc.getChildAdapterPosition(view)).getCuadrilla());
                    datosDeduccion.put("Total", String.format("%.2f", deduccionesItems.get(rvIngrDeduc.getChildAdapterPosition(view)).getTotal()));

                    Utilidades.iniciarActivityConDatos(ListadoIngresosDeducciones.this, RegistrarEditarIngresoDeduccion.class, datosDeduccion);
                }
            });
        }
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
                    obtenerDatos(lblFecha.getText().toString()); //Llamamos el método "obtenerIngresos" de arriba y le mandamos el contenido del lblFecha
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
    public void mostrarMesesIngresos(View view) {
        if (nombreActivity.contentEquals("ListadoIngresosAdmin")) {
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
        else if (nombreActivity.contentEquals("ListadoIngresosEmpleado")) {
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

                PopupMenu popup = new PopupMenu(this, view); // Objeto de tipo "PopupMenu"
                popup.setOnMenuItemClickListener(this); // Indicamos que asigne el evento "OnMenuItemClick" para que haga algo cada vez que se dé click a una opción del menú
                popup.inflate(R.menu.popupmenu_ultimosdosmeses); // Inflamos la vista del menú indicando la ruta de dicha vista gráfica

                // Asignar los textos a los items del menú
                popup.getMenu().findItem(R.id.menuMesActual).setTitle(mesActual);
                popup.getMenu().findItem(R.id.menuMesAnterior).setTitle(mesAnterior);

                popup.show(); // Mostramos el menú ya inflado
            }
            catch (Exception e) {
                Log.w("ObtenerMes", e);
            }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) { //Parte lógica de lo que queremos que haga cada opción del popup menú
        String mesSeleccionado = menuItem.getTitle().toString();

        switch (menuItem.getItemId()) {
            case R.id.menuMesAnterior:
                lblFecha.setText(mesSeleccionado);
                return true;
            case R.id.menuMesActual:
                lblFecha.setText(mesSeleccionado);
                return true;
            default:
                return false;
        }
    }

    //Método para eliminar la selección del Mes - Año
    public void eliminarMesIngresos(View view) {
        lblFecha.setText("Seleccionar Mes");
    }
}