package com.ingelcom.cajachica;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.ingelcom.cajachica.DAO.Cuadrilla;
import com.ingelcom.cajachica.DAO.FirestoreOperaciones;
import com.ingelcom.cajachica.DAO.Gasto;
import com.ingelcom.cajachica.DAO.Usuario;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;

import java.util.List;
import java.util.Map;

public class RegistrarEditarGasto extends AppCompatActivity {

    private LinearLayout llFecha, llCuadrilla, llDinero;
    private TextView lblTitulo, lblDinero, lblFecha, btnSubirCambiarFoto, btnConfirmar;
    private EditText txtLugar, txtDescripcion, txtFactura, txtTotal;
    private ImageView imgFoto, imgEliminar;
    private Spinner spCuadrillas, spTipoCompras;
    private String nombreActivity, dineroDisponible;

    private FirestoreOperaciones oper = new FirestoreOperaciones();
    private Cuadrilla cuad = new Cuadrilla(RegistrarEditarGasto.this);
    private Gasto gast = new Gasto(RegistrarEditarGasto.this);
    private Usuario usu = new Usuario(RegistrarEditarGasto.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_editar_gasto);

        inicializarElementos();
        establecerElementos();
        inicializarSpinners();
    }

    private void inicializarElementos() {
        //Obtenemos el nombre del activity que se envía desde el activity anterior, lo hacemos llamando a la función "obtenerStringExtra" de la clase "Utilidades", y le mandamos "this" para referenciar esta actividad y "Activity" como clave del putExtra
        nombreActivity = Utilidades.obtenerStringExtra(this, "ActivityREG");
        dineroDisponible = Utilidades.obtenerStringExtra(this, "DineroDisponible");

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

    private void establecerElementos() {
        if (nombreActivity != null) { //Que entre al if si "nombreActivity" no es nulo
            switch (nombreActivity) { //El "nombreActivity" nos sirve para saber la pantalla con la que trabajaremos
                //Establecemos los elementos gráficos si la pantalla es "RegistrarGastoEmpleado"
                case "RegistrarGastoEmpleado":
                    lblDinero.setText("L. " + dineroDisponible);
                    //Ocultamos estos dos elementos (Fecha y Cuadrilla) para que el empleado no pueda verlos
                    llFecha.setVisibility(View.GONE);
                    llCuadrilla.setVisibility(View.GONE);
                    break;

                case "RegistrarGastoAdmin":
                    llFecha.setVisibility(View.GONE);
                    llDinero.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private void inicializarSpinners() {
        //Para inicializar los spinners, llamamos al método "obtenerRegistros" de la clase "FirestoreOperaciones" a la cual le mandamos el nombre de la colección y el nombre del campo de Firestore de los cuales queremos obtener los registros. También invocamos los métodos "onCallback" y "onFailure" de la interfaz FirestoreCallback
        //CUADRILLAS
        oper.obtenerRegistrosCampo("cuadrillas", "Nombre", new FirestoreCallbacks.FirestoreListCallback() {
            @Override
            public void onCallback(List<String> lista) {
                //Creamos un adapter de tipo ArrayAdapter el cual le pasamos el contexto de este Activity, la vista layout de las opciones del Spinner (R.layout.spinner_items), y la lista de valores que se recibe en "lista" al llamar a la interfaz FirestoreCallback
                ArrayAdapter<String> adapter = new ArrayAdapter<>(RegistrarEditarGasto.this, R.layout.spinner_items, lista);
                spCuadrillas.setAdapter(adapter); //Asignamos el adapter al Spinner "spCuadrillas"
            }

            @Override
            public void onFailure(Exception e) {
                Log.w("Activity", "Error al obtener las cuadrillas.", e);
            }
        });

        //TIPO DE COMPRAS
        oper.obtenerRegistrosCampo("tipoCompras", "Nombre", new FirestoreCallbacks.FirestoreListCallback() {
            @Override
            public void onCallback(List<String> lista) {
                //Creamos un adapter de tipo ArrayAdapter el cual le pasamos el contexto de este Activity, la vista layout de las opciones del Spinner (R.layout.spinner_items), y la lista de valores que se recibe en "lista" al llamar a la interfaz FirestoreCallback
                ArrayAdapter<String> adapter = new ArrayAdapter<>(RegistrarEditarGasto.this, R.layout.spinner_items, lista);
                spTipoCompras.setAdapter(adapter); //Asignamos el adapter al Spinner "spTipoCompras"
            }

            @Override
            public void onFailure(Exception e) {
                Log.w("Activity", "Error al obtener los tipos de compras.", e);
            }
        });
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
            //Llamamos el método "obtenerUnUsuario" de la clase "Usuario" y creamos una invocación a la interfaz "FirestoreDocumentCallback"
            usu.obtenerUnUsuario(new FirestoreCallbacks.FirestoreDocumentCallback() {
                @Override
                public void onCallback(Map<String, Object> documento) { //Los datos del usuario están guardados en el HashMap "documento"
                    if (documento != null) { //Si "documento" no es nulo, quiere decir que encontró el usuario mediante el correo
                        //Obtenemos estos 3 datos del usuario y los guardamos en sus respectivas variables
                        String nombre = (String) documento.get("Nombre");
                        String cuadrillaBDD = (String) documento.get("Cuadrilla");
                        String rol = (String) documento.get("Rol");

                        //Dentro de ambas condiciones, llamamos el método "registrarGasto" donde se hará el proceso de inserción a Firestore, le mandamos los textboxes y selecciones de los spinners de esta pantalla
                        if (tipoGasto.contentEquals("GastoAdmin"))
                            gast.registrarGasto(nombre, rol, cuadrillaTXT, lugarCompra, tipoCompra, descripcion, factura, total, false); //Si el gasto lo registra un admin, mandamos "cuadrillaTXT" que es la selección de la cuadrilla en el Spinner, y un "false" indicando que no debe restar el gasto del dinero disponible de la cuadrilla ya que el gasto lo está registrando un admin con un dinero aparte
                        else if (tipoGasto.contentEquals("GastoEmpleado"))
                            gast.registrarGasto(nombre, rol, cuadrillaBDD, lugarCompra, tipoCompra, descripcion, factura, total, true); //Si el gasto lo registra un empleado, mandamos "cuadrillaBDD" que es la extracción de la cuadrilla a la que pertenece el usuario actual, y un true indicando que SI debe restar el gasto registrado del dinero disponible de la cuadrilla del usuario
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

        /*switch (tipoGasto) {
            case "GastoEmpleado":


                break;

            case "GastoAdmin":
                break;
        }*/
    }
}