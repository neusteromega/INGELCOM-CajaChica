package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ingelcom.cajachica.Herramientas.Utilidades;

public class DetalleGastoIngreso extends AppCompatActivity {

    private TextView lblTitulo, lblTotalGastIngr, lblDinero;
    private TextView lblFecha, lblUsuario, lblCuadrilla, lblLugar, lblTipoCompra, lblDescripcion, lblFactura, lblTransferencia;
    private TextView sepFechaUser, sepUserCuad, sepCuadLugar, sepLugarTipo, sepTipoDesc, sepDescFact, sepFactTransf;
    private ImageView btnRegresar, btnEditar, imgFoto;

    private String nombreActivity, id, fecha, usuario, cuadrilla, lugarCompra, tipoCompra, descripcion, factura, transferencia, total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_gasto_ingreso);

        inicializarElementos();
        obtenerDatos();
        establecerElementos();
    }

    private void inicializarElementos() {
        lblTitulo = findViewById(R.id.lblTituloDGI);
        lblTotalGastIngr = findViewById(R.id.lblTituloTotalDGI);
        lblDinero = findViewById(R.id.lblTotalDGI);

        lblFecha = findViewById(R.id.lblFechaDGI);
        lblUsuario = findViewById(R.id.lblUsuarioDGI);
        lblCuadrilla = findViewById(R.id.lblCuadrillaDGI);
        lblLugar = findViewById(R.id.lblLugarDGI);
        lblTipoCompra = findViewById(R.id.lblTipoDGI);
        lblDescripcion = findViewById(R.id.lblDescripcionDGI);
        lblFactura = findViewById(R.id.lblFacturaDGI);
        lblTransferencia = findViewById(R.id.lblTransferenciaDGI);

        sepFechaUser = findViewById(R.id.lblSepFechaUsuarioDGI);
        sepUserCuad = findViewById(R.id.lblSepUsuarioCuadrillaDGI);
        sepCuadLugar = findViewById(R.id.lblSepCuadrillaLugarDGI);
        sepLugarTipo = findViewById(R.id.lblSepLugarTipoDGI);
        sepTipoDesc = findViewById(R.id.lblSepTipoDescripcionDGI);
        sepDescFact = findViewById(R.id.lblSepDescripcionFacturaDGI);
        sepFactTransf = findViewById(R.id.lblSepFacturaTransferenciaDGI);

        btnRegresar = findViewById(R.id.imgRegresarDGI);
        btnEditar = findViewById(R.id.imgEditarDGI);
        imgFoto = findViewById(R.id.imgFotoEvidenciaDGI);
    }

    private void obtenerDatos() {
        nombreActivity = Utilidades.obtenerStringExtra(this, "ActivityDGI");

        switch (nombreActivity) {
            case "DetalleGasto":
                id = Utilidades.obtenerStringExtra(this, "ID");
                fecha = Utilidades.obtenerStringExtra(this, "FechaHora");
                usuario = Utilidades.obtenerStringExtra(this, "Usuario");
                cuadrilla = Utilidades.obtenerStringExtra(this, "Cuadrilla");
                lugarCompra = Utilidades.obtenerStringExtra(this, "LugarCompra");
                tipoCompra = Utilidades.obtenerStringExtra(this, "TipoCompra");
                descripcion = Utilidades.obtenerStringExtra(this, "Descripcion");
                factura = Utilidades.obtenerStringExtra(this, "NumeroFactura");
                total = Utilidades.obtenerStringExtra(this, "Total");
                break;

            case "DetalleIngreso":

                break;
        }
    }

    private void establecerElementos() {

    }

    public void EditarGasto(View view) {
        Utilidades.iniciarActivity(this, RegistrarEditarGasto.class, false);
    }
}