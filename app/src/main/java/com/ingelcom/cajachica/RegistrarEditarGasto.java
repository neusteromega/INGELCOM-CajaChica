package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;

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

import com.ingelcom.cajachica.DAO.FirestoreOperaciones;
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
        //Que entre al if si "nombreActivity" no es nulo
        if (nombreActivity != null) {
            //El "nombreActivity" nos sirve para saber la pantalla con la que trabajaremos
            switch (nombreActivity) {
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
        Utilidades.iniciarActivity(this, GastoIngresoRegistrado.class, false);
    }
}