package com.ingelcom.cajachica.DAO;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.ingelcom.cajachica.Modelos.EmpleadosItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Usuario {
    private FirestoreOperaciones oper;

    public Usuario() {
        oper = new FirestoreOperaciones();
    }

    public List<EmpleadosItems> obtenerEmpleados() {
        List<EmpleadosItems> listaEmpleados = new ArrayList<>();

        oper.obtenerRegistros("usuarios", new FirestoreOperaciones.FirestoreAllDocumentsCallback() {
            @Override
            public void onCallback(List<Map<String, Object>> documentos) {
                for (Map<String,Object> documento : documentos) {
                    // Extraer los campos del documento y crear un nuevo objeto EmpleadosItems
                    String nombre = (String) documento.get("NombreApellido");
                    String correo = (String) documento.get("Correo");
                    String cuadrilla = (String) documento.get("Cuadrilla");
                    String rol = (String) documento.get("Rol");

                    //Filtrar solo los empleados con rol "Empleado"
                    if (rol.contentEquals("Empleado")) {
                        EmpleadosItems empleado = new EmpleadosItems(nombre, correo, cuadrilla);
                        listaEmpleados.add(empleado);
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("FirestoreError", "Error al obtener los documentos", e);
            }
        });

        return listaEmpleados;
    }
}
