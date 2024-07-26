package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ingelcom.cajachica.DAO.Usuario;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;

import java.util.Map;

public class DetalleGastoIngreso extends AppCompatActivity {

    private TextView lblTitulo, lblTotalGastIngr, lblDinero;
    private TextView lblFecha, lblUsuario, lblCuadrilla, lblLugar, lblTipoCompra, lblDescripcion, lblFactura, lblTransferencia;
    private TextView sepPrincipal, sepFechaCuad, sepCuadUser, sepCuadLugar, sepLugarTipo, sepTipoDesc, sepDescFact, sepFactTransf;
    private ImageView btnRegresar, btnEditar, imgFoto;

    private String nombreActivity, id, fecha, usuario, cuadrilla, lugarCompra, tipoCompra, descripcion, factura, transferencia, total;
    private Usuario usu = new Usuario(DetalleGastoIngreso.this);

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

        sepPrincipal = findViewById(R.id.lblSeparadorDGI);
        sepFechaCuad = findViewById(R.id.lblSepFechaCuadrillaDGI);
        sepCuadUser = findViewById(R.id.lblSepCuadrillaUsuarioDGI);
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

        if (nombreActivity != null) { //Que entre al if si "nombreActivity" no es nulo
            switch (nombreActivity) { //El "nombreActivity" nos sirve para saber la pantalla con la que trabajaremos
                case "DetalleGastoCuadrilla": //Obtener los datos y guardarlos en las variables globales si la pantalla es "DetalleGastoCuadrilla" o "DetalleGastoSupervisores"

                case "DetalleGastoSupervisores":
                    id = Utilidades.obtenerStringExtra(this, "ID");
                    fecha = Utilidades.obtenerStringExtra(this, "FechaHora");
                    cuadrilla = Utilidades.obtenerStringExtra(this, "Cuadrilla");
                    usuario = Utilidades.obtenerStringExtra(this, "Usuario");
                    lugarCompra = Utilidades.obtenerStringExtra(this, "LugarCompra");
                    tipoCompra = Utilidades.obtenerStringExtra(this, "TipoCompra");
                    descripcion = Utilidades.obtenerStringExtra(this, "Descripcion");
                    factura = Utilidades.obtenerStringExtra(this, "NumeroFactura");
                    total = Utilidades.obtenerStringExtra(this, "Total");
                    break;

                case "DetalleIngreso": //Obtener los datos y guardarlos en las variables globales si la pantalla es "DetalleIngreso"
                    id = Utilidades.obtenerStringExtra(this, "ID");
                    fecha = Utilidades.obtenerStringExtra(this, "FechaHora");
                    cuadrilla = Utilidades.obtenerStringExtra(this, "Cuadrilla");
                    usuario = Utilidades.obtenerStringExtra(this, "Usuario");
                    transferencia = Utilidades.obtenerStringExtra(this, "Transferencia");
                    total = Utilidades.obtenerStringExtra(this, "Total");
                    break;
            }
        }
    }

    private void establecerElementos() {
        if (nombreActivity != null) { //Que entre al if si "nombreActivity" no es nulo
            switch (nombreActivity) { //El "nombreActivity" nos sirve para saber la pantalla con la que trabajaremos
                case "DetalleGastoCuadrilla": //Establecemos los elementos gráficos si la pantalla es "DetalleGastoCuadrilla"

                    //Asignamos los titulos, el Dinero y el color del Dinero
                    lblTitulo.setText("Detalle de Gasto");
                    lblTotalGastIngr.setText("Total Gastado");
                    lblDinero.setText("L. " + total);
                    lblDinero.setTextColor(this.getColor(R.color.clr_fuente_gastos));

                    //Ocultamos estos elementos

                    lblTransferencia.setVisibility(View.GONE);
                    sepPrincipal.setVisibility(View.GONE);
                    sepFactTransf.setVisibility(View.GONE);

                    asignarDatos("Gasto"); //Llamamos al método "asignarDatos" de abajo y le mandamos "Gasto" para indicar que asigne los datos al gasto
                    break;

                case "DetalleGastoSupervisores":

                    //Asignamos los titulos, el Dinero y el color del Dinero
                    lblTitulo.setText("Detalle de Gasto");
                    lblTotalGastIngr.setText("Total Gastado");
                    lblDinero.setText("L. " + total);
                    lblDinero.setTextColor(this.getColor(R.color.clr_fuente_gastos));

                    //Ocultamos estos elementos

                    lblTransferencia.setVisibility(View.GONE);
                    sepPrincipal.setVisibility(View.GONE);
                    sepFactTransf.setVisibility(View.GONE);

                    ocultarBotonEditar();
                    asignarDatos("Gasto"); //Llamamos al método "asignarDatos" de abajo y le mandamos "Gasto" para indicar que asigne los datos al gasto
                    break;

                case "DetalleIngreso": //Establecemos los elementos gráficos si la pantalla es "DetalleGasto"

                    //Asignamos los titulos, el Dinero y el color del Dinero
                    lblTitulo.setText("Detalle de Ingreso");
                    lblTotalGastIngr.setText("Total Ingresado");
                    lblDinero.setText("L. " + total);
                    lblDinero.setTextColor(this.getColor(R.color.clr_fuente_ingresos));

                    //Ocultamos estos elementos
                    lblLugar.setVisibility(View.GONE);
                    lblTipoCompra.setVisibility(View.GONE);
                    lblDescripcion.setVisibility(View.GONE);
                    lblFactura.setVisibility(View.GONE);
                    sepLugarTipo.setVisibility(View.GONE);
                    sepTipoDesc.setVisibility(View.GONE);
                    sepDescFact.setVisibility(View.GONE);
                    sepFactTransf.setVisibility(View.GONE);
                    imgFoto.setVisibility(View.GONE);

                    ocultarBotonEditar();
                    asignarDatos("Ingreso"); //Llamamos al método "asignarDatos" de abajo y le mandamos "Ingreso" para indicar que asigne los datos al ingreso
                    break;
            }
        }
    }

    //Método que permite asignar los datos a los TextViews
    private void asignarDatos(String tipo) {
        //Obtenemos los colores para establecer en los TextViews
        int colorInicial = ContextCompat.getColor(this, R.color.clr_fuente_primario);
        int colorFinal = ContextCompat.getColor(this, R.color.clr_fuente_secundario);

        if (tipo.contentEquals("Gasto")) {
            //Para cada TextView, llamámos al método utilitario "obtenerStringDosColores" que nos devuelve un "SpannableString" con el texto a asignar el TextView ya convertido a dos colores. Por ejemplo, "Fecha: 00/00/0000 00:00", la parte de "Fecha: " será de un color, y la parte de "00/00/0000 00:00" será de otro color
            lblFecha.setText(Utilidades.obtenerStringDosColores("Fecha y Hora: ", fecha, colorInicial, colorFinal));
            lblUsuario.setText(Utilidades.obtenerStringDosColores("Usuario: ", usuario, colorInicial, colorFinal));
            lblCuadrilla.setText(Utilidades.obtenerStringDosColores("Cuadrilla: ", cuadrilla, colorInicial, colorFinal));
            lblLugar.setText(Utilidades.obtenerStringDosColores("Lugar: ", lugarCompra, colorInicial, colorFinal));
            lblTipoCompra.setText(Utilidades.obtenerStringDosColores("Compra: ", tipoCompra, colorInicial, colorFinal));
            lblDescripcion.setText(Utilidades.obtenerStringDosColores("Descripción: ", descripcion, colorInicial, colorFinal));
            lblFactura.setText(Utilidades.obtenerStringDosColores("No. Factura: ", factura, colorInicial, colorFinal));
        }
        else if (tipo.contentEquals("Ingreso")) {
            //Para cada TextView, llamámos al método utilitario "obtenerStringDosColores" que nos devuelve un "SpannableString" con el texto a asignar el TextView ya convertido a dos colores. Por ejemplo, "Fecha: 00/00/0000 00:00", la parte de "Fecha: " será de un color, y la parte de "00/00/0000 00:00" será de otro color
            lblFecha.setText(Utilidades.obtenerStringDosColores("Fecha y Hora: ", fecha, colorInicial, colorFinal));
            lblUsuario.setText(Utilidades.obtenerStringDosColores("Usuario: ", usuario, colorInicial, colorFinal));
            lblCuadrilla.setText(Utilidades.obtenerStringDosColores("Cuadrilla: ", cuadrilla, colorInicial, colorFinal));
            lblTransferencia.setText(Utilidades.obtenerStringDosColores("No. Transferencia: ", transferencia, colorInicial, colorFinal));
        }
    }

    //Método Click del botón para editar un Gasto o un Ingreso
    public void editarGastoIngreso(View view) {
        if (nombreActivity.equalsIgnoreCase("DetalleGastoCuadrilla")) //Si el "nombreActivity" tiene el texto "DetalleGastoCuadrilla", que entre al if
            Utilidades.iniciarActivityConString(DetalleGastoIngreso.this, RegistrarEditarGasto.class, "ActivityREG", "EditarGastoEmpleado", false); //Abrimos el activity "RegistrarEditarGasto" y le mandamos el texto "EditarGastoEmpleado" como parámetro
        else if (nombreActivity.equalsIgnoreCase("DetalleGastoSupervisores")) //Si el "nombreActivity" tiene el texto "DetalleGastoSupervisores", que entre al if
            Utilidades.iniciarActivityConString(DetalleGastoIngreso.this, RegistrarEditarGasto.class, "ActivityREG", "EditarGastoAdmin", false); //Abrimos el activity "RegistrarEditarGasto" y le mandamos el texto "EditarGastoAdmin" como parámetro
        else if (nombreActivity.contentEquals("DetalleIngreso")) //En cambio, si el "nombreActivity" tiene el texto "DetalleIngreso", que entre al else if
            Utilidades.iniciarActivityConString(DetalleGastoIngreso.this, RegistrarEditarIngresoDeduccion.class, "ActivityREID", "EditarIngreso", false); //Mandamos el texto "EditarIngreso" al activity "RegistrarEditarIngreso"
    }

    //Método que ocultar el botón de Editar cuando el rol del Usuario es "Empleado"
    private void ocultarBotonEditar() {
        try {
            //Llamamos el método "obtenerUnUsuario" de la clase "Usuario" y creamos una invocación a la interfaz "FirestoreDocumentCallback"
            usu.obtenerUsuarioActual(new FirestoreCallbacks.FirestoreDocumentCallback() {
                @Override
                public void onCallback(Map<String, Object> documento) {
                    if (documento != null) { //Si documento no es nulo, quiere decir que si encontró el usuario actual
                        String rol = (String) documento.get("Rol"); //Obtenemos el rol del usuario

                        if (rol.equalsIgnoreCase("Empleado")) //Si el rol es "Empleado", que entre al if
                            btnEditar.setVisibility(View.GONE); //Ocultamos el botón de Editar
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
}