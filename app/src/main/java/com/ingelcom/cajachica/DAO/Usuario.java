package com.ingelcom.cajachica.DAO;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Modelos.EmpleadosItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Usuario {
    private FirestoreOperaciones oper; //Objeto de la clase "FirestoreOperaciones"

    public Usuario() {
        oper = new FirestoreOperaciones();
    }

    //Método que nos permitirá obtener los empleados, pero sólo los que tengan el rol "Empleado"
    public void obtenerEmpleados(FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<EmpleadosItems> callback) { //Llamamos la interfaz "FirestoreAllSpecialDocumentsCallback" y le indicamos que debe ser de tipo "EmpleadosItems"
        //Llamamos el método "obtenerRegistros" de "FirestoreOperaciones", le mandamos el nombre de la colección, e invocamos la interfaz "FirestoreAllDocumentsCallback"
        oper.obtenerRegistros("usuarios", new FirestoreCallbacks.FirestoreAllDocumentsCallback() {
            @Override
            public void onCallback(List<Map<String, Object>> documentos) { //Al invocar la interfaz, nos devuelve una lista de tipo "Map<String,Object>" llamada "documentos" en la cual se almacenarán todos los campos de todos los documentos de la colección
                List<EmpleadosItems> listaEmpleados = new ArrayList<>(); //Creamos una lista de tipo "EmpleadosItems"

                //Hacemos un for que recorra los documentos de la lista "documentos" y los vaya guardando uno por uno en la variable temporal "documento" de tipo "Map<String,Object>"
                for (Map<String, Object> documento : documentos) {
                    //Extraemos los campos del HashMap "documento", los campos necesarios en "EmpleadosItems"
                    String nombre = (String) documento.get("Nombre");
                    String correo = (String) documento.get("Correo");
                    String cuadrilla = (String) documento.get("Cuadrilla");
                    String identidad = (String) documento.get("Identidad");
                    String telefono = (String) documento.get("Telefono");
                    String rol = (String) documento.get("Rol"); //El "Rol" lo extraemos para verificar si es Empleado o Administrador
                    String estado = String.valueOf(documento.get("Estado"));

                    //Filtramos solo los empleados con rol "Empleado"
                    if (rol.contentEquals("Empleado")) {
                        EmpleadosItems empleado = new EmpleadosItems(nombre, correo, cuadrilla, identidad, telefono, rol, estado); //Creamos un objeto de tipo "EmpleadosItems" en el cual guardamos los datos extraídos arriba
                        listaEmpleados.add(empleado); //El objeto de tipo "EmpleadosItems" lo guardamos en la clase "listaEmpleados"
                    }
                }
                //Cuando salga del "for", ya tendremos todos los empleados con rol "Empleado" en la "listaEmpleados", y esta lista es la que mandamos al llamar el método "onCallback" de la interfaz
                callback.onCallback(listaEmpleados);
            }

            @Override
            public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                Log.e("FirestoreError", "Error al obtener los documentos", e);
                callback.onFailure(e);
            }
        });
    }

    /*public interface FirestoreAllDocumentsCallback<T> {
        void onCallback(List<T> items);
        void onFailure(Exception e);
    }*/
}
