package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
        redireccionarUsuario(); //Llamamos el método "redireccionarUsuario"
    }

    private void inicializarElementos() {
        //Obtenemos el nombre del activity que se envía desde el activity anterior, lo hacemos llamando a la función "obtenerStringExtra" de la clase "Utilidades", y le mandamos "this" para referenciar esta actividad y "Activity" como clave del putExtra
        nombreActivity = Utilidades.obtenerStringExtra(this, "ActivityGIR");

        lblTitulo = findViewById(R.id.lblTituloGIR);
        lblExito = findViewById(R.id.lblExitoGIR);
        lblMensaje = findViewById(R.id.lblMensajeGIR);
    }

    private void establecerElementos() {
        //Que entre al if si "nombreActivity" no es nulo
        if (nombreActivity != null) {
            //El "nombreActivity" nos sirve para saber la pantalla con la que trabajaremos
            switch (nombreActivity) {
                //Establecemos los elementos gráficos si la pantalla es "IngresoRegistrado"
                case "IngresoRegistrado":
                    lblTitulo.setText("Ingreso Registrado");
                    lblExito.setText("¡EL INGRESO HA SIDO REGISTRADO CON ÉXITO!");
                    lblMensaje.setText("Puede revisar el Listado de Ingresos de la Cuadrilla para visualizar el Ingreso Registrado.");
                    break;

                //Establecemos los elementos gráficos si la pantalla es "GastoRegistradoAdmin"
                case "GastoRegistradoAdmin":
                    lblTitulo.setText("Gasto Registrado");
                    lblExito.setText("¡EL GASTO HA SIDO REGISTRADO CON ÉXITO");
                    lblMensaje.setText("Puede revisar el Listado de Gastos de la Cuadrilla para visualizar el Gasto Registrado");
                    break;
            }
        }
    }

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