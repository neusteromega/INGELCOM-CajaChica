package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ingelcom.cajachica.DAO.Cuadrilla;
import com.ingelcom.cajachica.DAO.StorageOperaciones;
import com.ingelcom.cajachica.DAO.Usuario;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.StorageCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;

import java.util.HashMap;
import java.util.Map;

public class DetalleGastoIngreso extends AppCompatActivity {

    private TextView lblTitulo, lblTotalGastIngr, lblDinero;
    private TextView lblFecha, lblUsuario, lblCuadrilla, lblLugar, lblTipoCompra, lblDescripcion, lblFactura, lblTransferencia;
    private TextView sepPrincipal, sepFechaCuad, sepCuadUser, sepCuadLugar, sepLugarTipo, sepTipoDesc, sepDescFact, sepFactTransf;
    private ImageView btnRegresar, btnEditar, imgFoto;
    private ProgressBar pbCargar;

    private String nombreActivity, id, fecha, usuario, rol, cuadrilla, lugarCompra, tipoCompra, descripcion, factura, transferencia, imagen, total;
    private Uri imageUri;
    private Usuario usu = new Usuario(DetalleGastoIngreso.this);
    private Cuadrilla cuad = new Cuadrilla(DetalleGastoIngreso.this);
    private StorageOperaciones stor = new StorageOperaciones();

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

        pbCargar = findViewById(R.id.pbCargarDGI);
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
                    rol = Utilidades.obtenerStringExtra(this, "Rol");
                    lugarCompra = Utilidades.obtenerStringExtra(this, "LugarCompra");
                    tipoCompra = Utilidades.obtenerStringExtra(this, "TipoCompra");
                    descripcion = Utilidades.obtenerStringExtra(this, "Descripcion");
                    factura = Utilidades.obtenerStringExtra(this, "NumeroFactura");
                    imagen = Utilidades.obtenerStringExtra(this, "Imagen");
                    total = Utilidades.obtenerStringExtra(this, "Total");
                    break;

                case "DetalleIngreso": //Obtener los datos y guardarlos en las variables globales si la pantalla es "DetalleIngreso"
                    id = Utilidades.obtenerStringExtra(this, "ID");
                    fecha = Utilidades.obtenerStringExtra(this, "FechaHora");
                    cuadrilla = Utilidades.obtenerStringExtra(this, "Cuadrilla");
                    usuario = Utilidades.obtenerStringExtra(this, "Usuario");
                    transferencia = Utilidades.obtenerStringExtra(this, "Transferencia");
                    imagen = Utilidades.obtenerStringExtra(this, "Imagen");
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
                    //imgFoto.setVisibility(View.GONE);

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

            pbCargar.setVisibility(View.VISIBLE); //Ponemos visible el progressBar

            try {
                //Llamamos el método "obtenerImagen" de la clase StorageOperaciones al cual le mandamos la ruta de la imagen a obtener guardada en la variable global "imagen", y realizamos una invocación a la interfaz "StorageURICallback"
                stor.obtenerImagen(imagen, new StorageCallbacks.StorageURICallback() {
                    @Override
                    public void onCallback(Uri uri) { //En este "Uri" se encuentra el URI de la imagen obtenida de Firebase Storage
                        pbCargar.setVisibility(View.GONE); //Ocultamos el progressBar ya cuando la imagen se ha cargado
                        imageUri = uri; //Como la imagen ya se cargó, asignamos el URI de la imagen obtenido a la variable global "imageUri"
                        Glide.with(DetalleGastoIngreso.this).load(uri).into(imgFoto); //Asignamos el URI de la imagen obtenida al "imgFoto", pero usando la biblioteca "Glide" para evitar errores
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.w("ObtenerImagen", "Error al obtener el URI de la imagen: " + e);
                    }
                });
            }
            catch (Exception e) {
                Log.w("ObtenerImagenStorage", e);
            }
        }
        else if (tipo.contentEquals("Ingreso")) {
            //Para cada TextView, llamámos al método utilitario "obtenerStringDosColores" que nos devuelve un "SpannableString" con el texto a asignar el TextView ya convertido a dos colores. Por ejemplo, "Fecha: 00/00/0000 00:00", la parte de "Fecha: " será de un color, y la parte de "00/00/0000 00:00" será de otro color
            lblFecha.setText(Utilidades.obtenerStringDosColores("Fecha y Hora: ", fecha, colorInicial, colorFinal));
            lblUsuario.setText(Utilidades.obtenerStringDosColores("Usuario: ", usuario, colorInicial, colorFinal));
            lblCuadrilla.setText(Utilidades.obtenerStringDosColores("Cuadrilla: ", cuadrilla, colorInicial, colorFinal));
            lblTransferencia.setText(Utilidades.obtenerStringDosColores("No. Transferencia: ", transferencia, colorInicial, colorFinal));

            pbCargar.setVisibility(View.VISIBLE); //Ponemos visible el progressBar

            try {
                //Llamamos el método "obtenerImagen" de la clase StorageOperaciones al cual le mandamos la ruta de la imagen a obtener guardada en la variable global "imagen", y realizamos una invocación a la interfaz "StorageURICallback"
                stor.obtenerImagen(imagen, new StorageCallbacks.StorageURICallback() {
                    @Override
                    public void onCallback(Uri uri) { //En este "Uri" se encuentra el URI de la imagen obtenida de Firebase Storage
                        pbCargar.setVisibility(View.GONE); //Ocultamos el progressBar ya cuando la imagen se ha cargado
                        imageUri = uri; //Como la imagen ya se cargó, asignamos el URI de la imagen obtenido a la variable global "imageUri"
                        Glide.with(DetalleGastoIngreso.this).load(uri).into(imgFoto); //Asignamos el URI de la imagen obtenida al "imgFoto", pero usando la biblioteca "Glide" para evitar errores
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.w("ObtenerImagen", "Error al obtener el URI de la imagen: " + e);
                    }
                });
            }
            catch (Exception e) {
                Log.w("ObtenerImagenStorage", e);
            }
        }
    }

    //Método Click del botón para editar un Gasto o un Ingreso
    public void editarGastoIngreso(View view) {
        try {
            //Llamamos el método "obtenerUnaCuadrilla" de la clase "Cuadrilla", le mandamos la cuadrilla y creamos una invocación a la interfaz "FirestoreDocumentCallback"
            cuad.obtenerUnaCuadrilla(cuadrilla, new FirestoreCallbacks.FirestoreDocumentCallback() {
                @Override
                public void onCallback(Map<String, Object> documento) {
                    if (documento != null) { //Si "documento" no es nulo, quiere decir que encontró la cuadrilla
                        Object valor = documento.get("Dinero"); //Lo obtenemos como Object ya que Firestore puede almacenar números en varios formatos (por ejemplo, Long y Double) y esto puede causar problemas con el casting del contenido del campo
                        double dinero = Utilidades.convertirObjectADouble(valor); //Llamamos el método utilitario "convertirObjectADouble" y le mandamos el objeto "valor", y nos retorna este objeto ya convertido a double y lo guardamos en "dinero"
                        HashMap<String,Object> datos = new HashMap<>(); //Creamos un HashMap para guardar los datos que se enviarán al siguiente Activity

                        if (rol != null && rol.equalsIgnoreCase("Empleado")) {//Si el rol tiene como texto "Empleado", que entre al if, con esto sabemos que es un gasto hecho por un empleado
                            //Agregamos las claves y datos al HashMap
                            datos.put("ActivityREG", "EditarGastoEmpleado");
                            datos.put("DineroDisponible", String.format("%.2f", dinero));
                            datos.put("ID", id);
                            datos.put("FechaHora", null);
                            datos.put("Cuadrilla", cuadrilla);
                            datos.put("LugarCompra", lugarCompra);
                            datos.put("TipoCompra", tipoCompra);
                            datos.put("Descripcion", descripcion);
                            datos.put("NumeroFactura", factura);
                            datos.put("Usuario", usuario);
                            datos.put("Rol", rol);
                            datos.put("Total", total);

                            Utilidades.iniciarActivityConDatos(DetalleGastoIngreso.this, RegistrarEditarGasto.class, datos); //Abrimos el activity "RegistrarEditarGasto" y le mandamos el HashMap "datos" con los parámetros
                        }
                        else if (rol != null && rol.equalsIgnoreCase("Administrador")) {//Si el rol tiene como texto "Administrador", que entre al else if, con esto sabemos que es un gasto hecho por un Administrador
                            //Agregamos las claves y datos al HashMap
                            datos.put("ActivityREG", "EditarGastoAdmin");
                            datos.put("DineroDisponible", String.format("%.2f", dinero));
                            datos.put("ID", id);
                            datos.put("FechaHora", fecha);
                            datos.put("Cuadrilla", cuadrilla);
                            datos.put("LugarCompra", lugarCompra);
                            datos.put("TipoCompra", tipoCompra);
                            datos.put("Descripcion", descripcion);
                            datos.put("NumeroFactura", factura);
                            datos.put("Usuario", usuario);
                            datos.put("Rol", rol);
                            datos.put("Total", total);

                            Utilidades.iniciarActivityConDatos(DetalleGastoIngreso.this, RegistrarEditarGasto.class, datos); //Abrimos el activity "RegistrarEditarGasto" y le mandamos el HashMap "datos" con los parámetros
                        }
                        else if (nombreActivity.equalsIgnoreCase("DetalleIngreso")) {//En cambio, si el "nombreActivity" tiene el texto "DetalleIngreso", que entre al else if
                            //Agregamos las claves y datos al HashMap
                            datos.put("ActivityREID", "EditarIngreso");
                            datos.put("ID", id);
                            datos.put("FechaHora", fecha);
                            datos.put("Cuadrilla", cuadrilla);
                            datos.put("Usuario", usuario);
                            datos.put("Transferencia", transferencia);
                            datos.put("Total", total);

                            Utilidades.iniciarActivityConDatos(DetalleGastoIngreso.this, RegistrarEditarIngresoDeduccion.class, datos);
                        }
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.w("ObtenerCuadrilla", e);
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerCuadrilla", e);
        }
    }

    //Método Click que al dar clic en la imagen cargada, nos manda al Activity "ImagenCompleta" donde también envía el URI de la imagen cargada para mostrarla en pantalla completa
    public void mostrarImagenCompleta(View view) {
        Intent intent = new Intent(DetalleGastoIngreso.this, ImagenCompleta.class);
        intent.putExtra("imageUri", imageUri); //Enviamos el URI de la imagen
        startActivity(intent); //Iniciamos el activity
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

    public void retroceder(View view) {
        onBackPressed();
    }
}