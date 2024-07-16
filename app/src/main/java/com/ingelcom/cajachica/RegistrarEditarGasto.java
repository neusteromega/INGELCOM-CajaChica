package com.ingelcom.cajachica;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ingelcom.cajachica.DAO.Cuadrilla;
import com.ingelcom.cajachica.DAO.FirestoreOperaciones;
import com.ingelcom.cajachica.DAO.Gasto;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;

import java.util.List;

public class RegistrarEditarGasto extends AppCompatActivity {

    private LinearLayout llFecha, llCuadrilla;
    private TextView lblTitulo, lblFecha, btnSubirCambiarFoto, btnConfirmar;
    private EditText txtLugar, txtDescripcion, txtFactura, txtTotal;
    private ImageView imgFoto, imgEliminar;
    private Spinner spCuadrillas, spTipoCompras;
    private String nombreActivity;

    private FirestoreOperaciones oper = new FirestoreOperaciones();
    private Cuadrilla cuad = new Cuadrilla(RegistrarEditarGasto.this);
    private Gasto gast = new Gasto(RegistrarEditarGasto.this);

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

        llFecha = findViewById(R.id.LLFechaRG);
        llCuadrilla = findViewById(R.id.LLCuadrillaRG);

        lblTitulo = findViewById(R.id.lblTituloRG);
        lblFecha = findViewById(R.id.lblFechaRG);
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
                    //Ocultamos estos dos elementos (Fecha y Cuadrilla) para que el empleado no pueda verlos
                    llFecha.setVisibility(View.GONE);
                    llCuadrilla.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private void inicializarSpinners() {
        //Para inicializar los spinners, llamamos al método "obtenerRegistros" de la clase "FirestoreOperaciones" a la cual le mandamos el nombre de la colección y el nombre del campo de Firestore de los cuales queremos obtener los registros. También invocamos los métodos "onCallback" y "onFailure" de la interfaz FirestoreCallback
        //CUADRILLAS
        oper.obtenerRegistrosCampo("cuadrillas", "Nombre", new FirestoreCallbacks.FirestoreCallback() {
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
        oper.obtenerRegistrosCampo("tipoCompras", "Nombre", new FirestoreCallbacks.FirestoreCallback() {
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
                                    //Enlazamos los EditText con las siguientes variables String
                                    String lugarCompra = txtLugar.getText().toString();
                                    String descripcion = txtDescripcion.getText().toString();
                                    String factura = txtFactura.getText().toString();
                                    String total = txtTotal.getText().toString();

                                    //Obtenemos la selección hecha en los Spinners
                                    String cuadrilla = spCuadrillas.getSelectedItem().toString();
                                    String tipoCompra = spTipoCompras.getSelectedItem().toString();

                                    gast.registrarGasto(cuadrilla, lugarCompra, tipoCompra, descripcion, factura, total, false); //Llamamos el método "registrarGasto" donde se hará el proceso de inserción a Firestore, le mandamos los textboxes y selecciones de los spinners de esta pantalla, y un false que indica que no se debe actualizar el dinero de la cuadrilla, ya que el gasto lo está efectuando un administrador con un dinero aparte
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
        //Utilidades.iniciarActivity(this, GastoIngresoRegistrado.class, false);
    }
}