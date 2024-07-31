package com.ingelcom.cajachica;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.Timestamp;
import com.ingelcom.cajachica.DAO.Cuadrilla;
import com.ingelcom.cajachica.DAO.FirestoreOperaciones;
import com.ingelcom.cajachica.DAO.Gasto;
import com.ingelcom.cajachica.DAO.Usuario;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RegistrarEditarGasto extends AppCompatActivity {

    private LinearLayout llFecha, llCuadrilla, llDinero;
    private TextView lblTitulo, lblDinero, lblFecha, btnSubirCambiarFoto, btnConfirmar;
    private EditText txtLugar, txtDescripcion, txtFactura, txtTotal;
    private ImageView imgFoto, imgEliminar;
    private Spinner spCuadrillas, spTipoCompras;
    private String nombreActivity, dineroDisponible, id, fechaHora, cuadrilla, lugarCompra, tipoCompra, descripcion, numeroFactura, usuario, rol, total;
    private Timestamp timestamp = null;

    private FirestoreOperaciones oper = new FirestoreOperaciones();
    private Cuadrilla cuad = new Cuadrilla(RegistrarEditarGasto.this);
    private Gasto gast = new Gasto(RegistrarEditarGasto.this);
    private Usuario usu = new Usuario(RegistrarEditarGasto.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_editar_gasto);

        inicializarElementos();
        obtenerDatos();
        establecerElementos();
        inicializarSpinners();
    }

    private void inicializarElementos() {
        llFecha = findViewById(R.id.LLFechaRG);
        llCuadrilla = findViewById(R.id.LLCuadrillaRG);
        llDinero = findViewById(R.id.LLDineroRG);

        lblTitulo = findViewById(R.id.lblTituloRG);
        lblDinero = findViewById(R.id.lblCantDineroRG);
        lblFecha = findViewById(R.id.lblFechaRG); //POSIBLE ELIMINACIÓN
        txtLugar = findViewById(R.id.txtLugarCompraRG);
        txtDescripcion = findViewById(R.id.txtDescripcionRG);
        txtFactura = findViewById(R.id.txtFacturaRG);
        txtTotal = findViewById(R.id.txtTotalRG);

        spCuadrillas = findViewById(R.id.spCuadrillaRG);
        spTipoCompras = findViewById(R.id.spTipoCompraRG);
        imgFoto = findViewById(R.id.imgFotoEvidenciaRG);
        imgEliminar = findViewById(R.id.imgEliminarFotoRG);

        btnSubirCambiarFoto = findViewById(R.id.btnSubirCambiarFotoRG);
        btnConfirmar = findViewById(R.id.btnConfirmarRG);
    }

    private void obtenerDatos() {
        //Obtenemos el nombre del activity y el dinero disponible que se envía desde el activity anterior, lo hacemos llamando a la función "obtenerStringExtra" de la clase "Utilidades", y le mandamos "this" para referenciar esta actividad y los nombres de las claves del putExtra
        nombreActivity = Utilidades.obtenerStringExtra(this, "ActivityREG");
        dineroDisponible = Utilidades.obtenerStringExtra(this, "DineroDisponible");

        switch (nombreActivity) {
            case "EditarGastoEmpleado":

            case "EditarGastoAdmin":
                id = Utilidades.obtenerStringExtra(this, "ID");
                fechaHora = Utilidades.obtenerStringExtra(this, "FechaHora");
                cuadrilla = Utilidades.obtenerStringExtra(this, "Cuadrilla");
                lugarCompra = Utilidades.obtenerStringExtra(this, "LugarCompra");
                tipoCompra = Utilidades.obtenerStringExtra(this, "TipoCompra");
                descripcion = Utilidades.obtenerStringExtra(this, "Descripcion");
                numeroFactura = Utilidades.obtenerStringExtra(this, "NumeroFactura");
                usuario = Utilidades.obtenerStringExtra(this, "Usuario");
                total = Utilidades.obtenerStringExtra(this, "Total");
                break;
        }
    }

    private void establecerElementos() {
        if (nombreActivity != null) { //Que entre al if si "nombreActivity" no es nulo
            switch (nombreActivity) { //El "nombreActivity" nos sirve para saber la pantalla con la que trabajaremos
                case "RegistrarGastoEmpleado": //Establecemos los elementos gráficos si la pantalla es "RegistrarGastoEmpleado"
                    lblTitulo.setText("Registrar Gasto");
                    lblDinero.setText("L. " + dineroDisponible);

                    //Ocultamos estos dos elementos (Fecha y Cuadrilla) para que el empleado no pueda verlos
                    llFecha.setVisibility(View.GONE);
                    llCuadrilla.setVisibility(View.GONE);
                    break;

                case "RegistrarGastoAdmin": //Establecemos los elementos gráficos si la pantalla es "RegistrarGastoAdmin"
                    lblTitulo.setText("Registrar Gasto");
                    llDinero.setVisibility(View.GONE);
                    break;

                case "EditarGastoEmpleado": //Establecemos los elementos gráficos si la pantalla es "EditarGastoEmpleado"
                    lblTitulo.setText("Editar Gasto");
                    lblDinero.setText("L. " + dineroDisponible);
                    txtLugar.setText(lugarCompra);
                    txtDescripcion.setText(descripcion);
                    txtFactura.setText(numeroFactura);
                    txtTotal.setText(total);

                    //Ocultamos estos dos elementos (Fecha y Cuadrilla) para que el empleado no pueda verlos
                    llFecha.setVisibility(View.GONE);
                    llCuadrilla.setVisibility(View.GONE);
                    break;

                case "EditarGastoAdmin":
                    lblTitulo.setText("Editar Gasto");
                    llDinero.setVisibility(View.GONE);
                    lblFecha.setText(fechaHora);
                    txtLugar.setText(lugarCompra);
                    txtDescripcion.setText(descripcion);
                    txtFactura.setText(numeroFactura);
                    txtTotal.setText(total);

                    //Como la fechaHora se obtiene en formato String, usamos el método utilitario "convertirFechaHoraATimestamp" para convertirlo a Timestamp y el resultado lo guardamos en la variable global "timestamp"
                    timestamp = Utilidades.convertirFechaHoraATimestamp(fechaHora);
                    break;
            }
        }
    }

    //Método Click del LinearLayout de Fecha, el cual al dar clic en él, se mostrará un calendario emergente para seleccionar una fecha, y luego un reloj para seleccionar la hora
    public void seleccionarFecha(View view) {
        final Calendar calendar = Calendar.getInstance(); //Creamos una instancia de Calendar que representa la fecha y hora actuales. Y en ella se irán almacenando las selecciones de fecha y hora que haga el usuario en el calendario y reloj

        //Inicializamos el DatePickerDialog con el año, mes y día actuales
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                //Actualizamos el "calendar" con el año, mes y día seleccionados por el usuario
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                //Inicializamos el TimePickerDialog después de seleccionar la fecha
                TimePickerDialog timePickerDialog = new TimePickerDialog(RegistrarEditarGasto.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        //Actualizamos el "calendar" con la hora y los minutos seleccionados por el usuario
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        //Formateamos y mostramos la fecha y hora seleccionada en el TextView
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault());
                        String fechaHoraSeleccionada = sdf.format(calendar.getTime());
                        lblFecha.setText(fechaHoraSeleccionada);

                        Date fechaHoraDate = calendar.getTime(); //Convertimos la fecha y hora seleccionada a un objeto Date
                        timestamp = new Timestamp(fechaHoraDate); //Convertimos el objeto Date a un objeto Timestamp (Variable global inicializada en null arriba)
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

                timePickerDialog.show(); //Mostramos el TimePickerDialog
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show(); //Mostramos el DatePickerDialog
    }

    private void inicializarSpinners() {
        try {
            //Para inicializar los spinners, llamamos al método "obtenerRegistros" de la clase "FirestoreOperaciones" a la cual le mandamos el nombre de la colección y el nombre del campo de Firestore de los cuales queremos obtener los registros. También invocamos los métodos "onCallback" y "onFailure" de la interfaz FirestoreCallback
            //CUADRILLAS
            oper.obtenerRegistrosCampo("cuadrillas", "Nombre", new FirestoreCallbacks.FirestoreListCallback() {
                @Override
                public void onCallback(List<String> lista) {
                    //Creamos un adapter de tipo ArrayAdapter el cual le pasamos el contexto de este Activity, la vista layout de las opciones del Spinner (R.layout.spinner_items), y la lista de valores que se recibe en "lista" al llamar a la interfaz FirestoreCallback
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(RegistrarEditarGasto.this, R.layout.spinner_items, lista);
                    spCuadrillas.setAdapter(adapter); //Asignamos el adapter al Spinner "spCuadrillas"

                    if (cuadrilla != null && !cuadrilla.isEmpty()) { //Si "cuadrilla" (la variable global que recibe la cuadrilla del Activity anterior) no es nula y no está vacía, significa que si está recibiendo una Cuadrilla del activity anterior, por lo tanto, que entre al if
                        int posicionCuadrilla = adapter.getPosition(cuadrilla); //Obtenemos la posición de la cuadrilla recibida en el Spinner, y guardamos dicha posición en una variable int
                        spCuadrillas.setSelection(posicionCuadrilla); //Una vez obtenemos la posición de la cuadrilla recibida en el Spinner, la asignamos al "spCuadrillas" para que al cargar el activity, ya esté seleccionada la cuadrilla específica en el Spinner
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.w("Activity", "Error al obtener las cuadrillas.", e);
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerCuadrillas", e);
        }

        try {
            //TIPO DE COMPRAS
            oper.obtenerRegistrosCampo("tipoCompras", "Nombre", new FirestoreCallbacks.FirestoreListCallback() {
                @Override
                public void onCallback(List<String> lista) {
                    //Creamos un adapter de tipo ArrayAdapter el cual le pasamos el contexto de este Activity, la vista layout de las opciones del Spinner (R.layout.spinner_items), y la lista de valores que se recibe en "lista" al llamar a la interfaz FirestoreCallback
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(RegistrarEditarGasto.this, R.layout.spinner_items, lista);
                    spTipoCompras.setAdapter(adapter); //Asignamos el adapter al Spinner "spTipoCompras"

                    if (tipoCompra != null && !tipoCompra.isEmpty()) { //Si "tipoCompra" (la variable global que recibe el tipo de compra del Activity anterior) no es nula y no está vacía, significa que si está recibiendo un Tipo de Compra del activity anterior, por lo tanto, que entre al if
                        int posicionCompra = adapter.getPosition(tipoCompra); //Obtenemos la posición del tipo de compra recibida en el Spinner, y guardamos dicha posición en una variable int
                        spTipoCompras.setSelection(posicionCompra); //Una vez obtenemos la posición del tipo de compra recibido en el Spinner, la asignamos al "spTipoCompra" para que al cargar el activity, ya esté seleccionada el tipo de compra específico en el Spinner
                    }
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

    public void subirFoto(View view) {

    }

    public void confirmar(View view) {
        if (nombreActivity != null) { //Que entre al if si "nombreActivity" no es nulo
            switch (nombreActivity) { //El "nombreActivity" nos sirve para saber la pantalla con la que trabajaremos
                case "RegistrarGastoAdmin": //Si estamos en la pantalla de "RegistrarGastoAdmin", al dar clic en el botón "Confirmar" que realice las operaciones de este case

                    //Creamos un alertDialog que pregunte si se desea registrar el gasto de dinero a la cuadrilla seleccionada
                    new AlertDialog.Builder(this).setTitle("REGISTRAR GASTO").setMessage("¿Está seguro que desea registrar el gasto de dinero a la cuadrilla seleccionada")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { //Si se selecciona la opción positiva, entrará aquí y al método "registrarGasto()"
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                insertarGasto("GastoAdmin");
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() { //Si se seleccionó la opción negativa, entrará aquí y solamente mostrará un mensaje en Logcat
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d("Mensaje", "Se canceló la acción"); //Se muestra un mensaje en el Logcat indicando que se canceló la acción
                            }
                        }).show();
                    break;

                case "RegistrarGastoEmpleado":

                    //Creamos un alertDialog que pregunte si se desea registrar el gasto de dinero
                    new AlertDialog.Builder(this).setTitle("REGISTRAR GASTO").setMessage("¿Está seguro que desea registrar el gasto de dinero")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { //Si se selecciona la opción positiva, entrará aquí y al método "registrarGasto()"
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                insertarGasto("GastoEmpleado");
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() { //Si se seleccionó la opción negativa, entrará aquí y solamente mostrará un mensaje en Logcat
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d("Mensaje", "Se canceló la acción"); //Se muestra un mensaje en el Logcat indicando que se canceló la acción
                            }
                        }).show();
                    break;

                case "EditarGastoEmpleado":

                    //Creamos un alertDialog que pregunte si se desea editar el ingreso de dinero
                    new AlertDialog.Builder(this).setTitle("EDITAR GASTO").setMessage("¿Está seguro que desea modificar los datos del gasto?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { //Si se selecciona la opción positiva, entrará aquí y al método "editarGasto()"
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                editarGasto("Empleado"); //Llamamos el método "editarGasto" de abajo para que se complete la modificación de los datos, y le mandamos la palabra "Empleado" para que sepa que modificará un gasto hecho por un empleado
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() { //Si se seleccionó la opción negativa, entrará aquí y solamente mostrará un mensaje en el Logcat
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d("Mensaje", "Se canceló la acción"); //Se muestra un mensaje en el Logcat indicando que se canceló la acción
                            }
                        }).show();
                    break;

                case "EditarGastoAdmin":

                    //Creamos un alertDialog que pregunte si se desea editar el gasto de dinero a la cuadrilla seleccionada
                    new AlertDialog.Builder(this).setTitle("EDITAR GASTO").setMessage("¿Está seguro que desea modificar los datos del gasto?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { //Si se selecciona la opción positiva, entrará aquí y al método "editarGasto()"
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                editarGasto("Admin"); //Llamamos el método "editarGasto" de abajo para que se complete la modificación de los datos, y le mandamos la palabra "Admin" para que sepa que modificará un gasto hecho por un administrador
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() { //Si se seleccionó la opción negativa, entrará aquí y solamente mostrará un mensaje en el Logcat
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.d("Mensaje", "Se canceló la acción"); //Se muestra un mensaje en el Logcat indicando que se canceló la acción
                                }
                            }).show();
                    break;
            }
        }
    }

    private void insertarGasto(String tipoGasto) {
        //Enlazamos los EditText con las siguientes variables String
        String lugarCompra = txtLugar.getText().toString();
        String descripcion = txtDescripcion.getText().toString();
        String factura = txtFactura.getText().toString();
        String total = txtTotal.getText().toString();

        //Obtenemos la selección hecha en los Spinners
        String cuadrillaTXT = spCuadrillas.getSelectedItem().toString();
        String tipoCompra = spTipoCompras.getSelectedItem().toString();

        try {
            //Llamamos el método "obtenerUsuarioActual" de la clase "Usuario" y creamos una invocación a la interfaz "FirestoreDocumentCallback"
            usu.obtenerUsuarioActual(new FirestoreCallbacks.FirestoreDocumentCallback() {
                @Override
                public void onCallback(Map<String, Object> documento) { //Los datos del usuario están guardados en el HashMap "documento"
                    if (documento != null) { //Si "documento" no es nulo, quiere decir que encontró el usuario mediante el correo
                        //Obtenemos estos 3 datos del usuario y los guardamos en sus respectivas variables
                        String nombre = (String) documento.get("Nombre");
                        String cuadrillaBDD = (String) documento.get("Cuadrilla");
                        String rol = (String) documento.get("Rol");

                        //Dentro de ambas condiciones, llamamos el método "registrarGasto" donde se hará el proceso de inserción a Firestore, le mandamos los textboxes y selecciones de los spinners de esta pantalla
                        if (tipoGasto.equalsIgnoreCase("GastoAdmin"))
                            gast.registrarGasto(nombre, timestamp, rol, cuadrillaTXT, lugarCompra, tipoCompra, descripcion, factura, total, false, true); //Si el gasto lo registra un admin, mandamos "cuadrillaTXT" que es la selección de la cuadrilla en el Spinner, y un "false" indicando que no debe restar el gasto del dinero disponible de la cuadrilla ya que el gasto lo está registrando un admin con un dinero aparte
                        else if (tipoGasto.equalsIgnoreCase("GastoEmpleado"))
                            gast.registrarGasto(nombre, timestamp, rol, cuadrillaBDD, lugarCompra, tipoCompra, descripcion, factura, total, true, false); //Si el gasto lo registra un empleado, mandamos "cuadrillaBDD" que es la extracción de la cuadrilla a la que pertenece el usuario actual, y un true indicando que SI debe restar el gasto registrado del dinero disponible de la cuadrilla del usuario
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
        catch (Exception e) {
            Log.w("ObtenerUsuario", e);
        }
    }

    //Método que permite editar un gasto para un empleado o un administrador
    private void editarGasto(String tipo) {
        //Enlazamos los EditText con las siguientes variables String
        String lugarCompra = txtLugar.getText().toString();
        String descripcion = txtDescripcion.getText().toString();
        String factura = txtFactura.getText().toString();
        String totalNuevo = txtTotal.getText().toString();

        //Obtenemos la selección hecha en los Spinners
        String cuadrillaNueva = spCuadrillas.getSelectedItem().toString();
        String tipoCompra = spTipoCompras.getSelectedItem().toString();

        //Si "tipo" es igual (ignorando mayúsculas y minúsculas) a la palabra "Empleado", que entre al if
        if (tipo.equalsIgnoreCase("Empleado")) {
            //Llamamos el método "editarGasto" de la clase Gasto donde se hará el proceso de modificación de los datos del gasto; para ello le mandamos el "id", un "null" para indicar que la fecha no se cambiará, la cuadrilla no se cambia así que se coloca la misma que se recibe del activity anterior (DetalleGastoIngreso), el resto de datos de los EditTexts, el Spinner de tipoCompra, el total anterior guardado en la variable global "total" y el "totalNuevo" que se extrae del EditText, un "true" indicando que debe actualizar el dinero de la cuadrilla ya que se está editando un gasto de un empleado, y un "false" indicando que no se ha seleccionado una nueva fecha
            gast.editarGasto(id, null, cuadrilla, lugarCompra, tipoCompra, descripcion, factura, total, totalNuevo, true, false);
        }
        else if (tipo.equalsIgnoreCase("Admin")) { //En cambio, si "tipo" es igual (ignorando mayúsculas y minúsculas) a la palabra "Admin", que entre al else if
            //Llamamos el método "editarGasto" de la clase Gasto donde se hará el proceso de modificación de los datos del gasto; para ello le mandamos el "id", la variable global "timestamp" que guarda la fecha seleccionada, la cuadrilla y el tipoCompra extraídos de los Spinners, el resto de datos de los EditTexts, el total anterior guardado en la variable global "total" y el "totalNuevo" que se extrae del EditText, un "false" indicando que no debe actualizar el dinero de la cuadrilla ya que se está editando un gasto de un administrador, y un "true" indicando que se ha seleccionado una nueva fecha
            gast.editarGasto(id, timestamp, cuadrillaNueva, lugarCompra, tipoCompra, descripcion, factura, total, totalNuevo, false, true);
        }
    }
}