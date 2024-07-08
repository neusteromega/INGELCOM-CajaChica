package com.ingelcom.cajachica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ingelcom.cajachica.Herramientas.Utilidades;

public class AgregarEditarPerfil extends AppCompatActivity {

    private LinearLayout llNombreApellido, llIdentidad, llTelefono, llCorreo, llContra, llConfContra, llRol, llCuadrilla, llEstado;
    private TextView lblTitulo, btnConfirmar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_editar_perfil);

        llNombreApellido = findViewById(R.id.LLNombreApellidoAEP);
        llIdentidad = findViewById(R.id.LLIdentidadAEP);
        llTelefono = findViewById(R.id.LLTelefonoAEP);
        llCorreo = findViewById(R.id.LLCorreoAEP);
        llContra = findViewById(R.id.LLContrasenaAEP);
        llConfContra = findViewById(R.id.LLConfContrasenaAEP);
        llRol = findViewById(R.id.LLRolAEP);
        llCuadrilla = findViewById(R.id.LLCuadrillaAEP);
        llEstado = findViewById(R.id.LLEstadoAEP);
        lblTitulo = findViewById(R.id.lblTituloAEP);
        btnConfirmar = findViewById(R.id.btnConfirmarAEP);

        establecerElementos();
    }

    private void establecerElementos() {
        //Obtenemos el nombre del activity que se envía desde el activity anterior, lo hacemos llamando a la función "obtenerStringExtra" de la clase "Utilidades", y le mandamos "this" para referenciar esta actividad y "Activity" como clave del putExtra
        String nombreActivity = Utilidades.obtenerStringExtra(this, "Activity");

        //Que entre al if si "nombreActivity" no es nulo
        if (nombreActivity != null) {
            //El "nombreActivity" nos sirve para saber la pantalla con la que trabajaremos
            switch (nombreActivity) {
                case "AgregarUsuario": //Establecemos los elementos gráficos si la pantalla es "AgregarUsuario"
                    //Asignamos el título
                    lblTitulo.setText("Agregar Usuario");

                    //Ocultamos con "GONE" los elementos que no son necesarios
                    llCorreo.setVisibility(View.GONE);
                    llContra.setVisibility(View.GONE);
                    llConfContra.setVisibility(View.GONE);
                    llEstado.setVisibility(View.GONE);
                    break;
            }
        }
    }
}