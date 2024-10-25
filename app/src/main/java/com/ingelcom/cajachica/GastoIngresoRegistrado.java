package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.ingelcom.cajachica.Herramientas.Utilidades;

public class GastoIngresoRegistrado extends AppCompatActivity {

    private TextView lblTitulo, lblExito, lblMensaje;
    private String nombreActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gasto_ingreso_registrado);

        inicializarElementos();
        establecerElementos();
    }

    @Override
    public void onBackPressed() { //Este método se ejecuta cuando el usuario presiona el botón de retroceso
        redireccionarUsuario(); //Llamamos el método "redireccionarUsuario" de abajo
    }

    private void inicializarElementos() {
        //Obtenemos el nombre del activity que se envía desde el activity anterior, lo hacemos llamando a la función "obtenerStringExtra" de la clase "Utilidades", y le mandamos "this" para referenciar esta actividad y "ActivityGIR" como clave del putExtra
        nombreActivity = Utilidades.obtenerStringExtra(this, "ActivityGIR");

        //Enlazamos las variables globales con los elementos gráficos
        lblTitulo = findViewById(R.id.lblTituloGIR);
        lblExito = findViewById(R.id.lblExitoGIR);
        lblMensaje = findViewById(R.id.lblMensajeGIR);
    }

    private void establecerElementos() {
        if (nombreActivity != null) { //Que entre al if si "nombreActivity" no es nulo
            switch (nombreActivity) { //El "nombreActivity" nos sirve para saber la pantalla con la que trabajaremos
                //Establecemos los elementos gráficos para cada uno de los tipos de pantallas
                case "IngresoRegistrado":
                    lblTitulo.setText("Ingreso Registrado");
                    lblExito.setText("¡EL INGRESO HA SIDO REGISTRADO CON ÉXITO!");
                    lblMensaje.setText("Puede revisar el Listado de Ingresos Generales o de la Cuadrilla para visualizar el Ingreso Registrado.");
                    break;

                case "GastoRegistradoAdmin":
                    lblTitulo.setText("Gasto Registrado");
                    lblExito.setText("¡EL GASTO HA SIDO REGISTRADO CON ÉXITO!");
                    lblMensaje.setText("Puede revisar el Listado de Gastos Generales o de la Cuadrilla para visualizar el Gasto Registrado.");
                    break;

                case "GastoRegistradoEmpleado":
                    lblTitulo.setText("Gasto Registrado");
                    lblExito.setText("¡EL GASTO HA SIDO REGISTRADO CON ÉXITO!");
                    lblMensaje.setText("Puede revisar el Listado de Gastos en el Menú Principal para visualizar el Gasto Registrado.");
                    break;

                case "DeduccionRegistrada":
                    lblTitulo.setText("Deducción Registrada");
                    lblExito.setText("¡LA DEDUCCIÓN HA SIDO REGISTRADA CON ÉXITO!");
                    lblMensaje.setText("Puede revisar el Listado de Deducciones de la Cuadrilla para visualizar la Deducción Registrada.");
                    break;

                case "IngresoEditado":
                    lblTitulo.setText("Ingreso Modificado");
                    lblExito.setText("¡EL INGRESO HA SIDO MODIFICADO CON ÉXITO!");
                    lblMensaje.setText("Puede revisar el Listado de Ingresos Generales o de la Cuadrilla para visualizar el Ingreso Modificado.");
                    break;

                case "GastoEditado":
                    lblTitulo.setText("Gasto Modificado");
                    lblExito.setText("¡EL GASTO HA SIDO MODIFICADO CON ÉXITO!");
                    lblMensaje.setText("Puede revisar el Listado de Gastos de la Cuadrilla para visualizar el Gasto Modificado.");
                    break;

                case "DeduccionEditada":
                    lblTitulo.setText("Deducción Modificada");
                    lblExito.setText("¡LA DEDUCCIÓN HA SIDO MODIFICADA CON ÉXITO!");
                    lblMensaje.setText("Puede revisar el Listado de Deducciones de la Cuadrilla para visualizar la Deducción Modificada.");
                    break;
            }
        }
    }

    //Evento clic del botón de regresar al menú
    public void regresarAlMenu(View view) {
        redireccionarUsuario(); //Llamamos el método "redireccionarUsuario"
    }

    //Método que permite redireccionar al usuario a la pantalla inicial dependiendo de su rol
    private void redireccionarUsuario() {
        FirebaseUser currentUser = Utilidades.obtenerUsuario(); //Obtenemos el usuario actual llamando el método utilitario "obtenerUsuario"
        String correoInicial; //Variable donde guardaremos el correo del usuario actual

        //Verificamos que el usuario no sea null
        if (currentUser != null){
            correoInicial = currentUser.getEmail(); //Guardamos el email del usuario en la variable "correoInicial"
            Utilidades.redireccionarUsuario(GastoIngresoRegistrado.this, correoInicial); //Llamamos el método "redireccionarUsuario" de la clase Utilidades y le mandamos un contexto y el correo del usuario actual
        }
    }
}