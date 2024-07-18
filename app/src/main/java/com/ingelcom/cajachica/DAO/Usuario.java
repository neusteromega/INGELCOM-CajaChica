package com.ingelcom.cajachica.DAO;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.ingelcom.cajachica.AdmPantallas;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.Modelos.EmpleadosItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Usuario {
    public Context contexto; //Nos ayuda a obtener el contexto del Activity donde se llame a esta clase, este contexto lo mandamos al crear un objeto de esta clase (Usuario usu = new Usuario(this)) y se inicializa en el método constructor
    private FirestoreOperaciones oper = new FirestoreOperaciones();; //Objeto de la clase "FirestoreOperaciones"

    public Usuario(Context contexto) {
        this.contexto = contexto;
    }

    //Método que nos permitirá obtener los empleados, pero sólo los que tengan el rol "Empleado"
    public void obtenerEmpleados(FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<EmpleadosItems> callback) { //Llamamos la interfaz "FirestoreAllSpecialDocumentsCallback" y le indicamos que debe ser de tipo "EmpleadosItems"
        try {
            //Llamamos el método "obtenerRegistros" de "FirestoreOperaciones", le mandamos el nombre de la colección, e invocamos la interfaz "FirestoreAllDocumentsCallback"
            oper.obtenerRegistros("usuarios", new FirestoreCallbacks.FirestoreAllDocumentsCallback() {
                @Override
                public void onCallback(List<Map<String, Object>> documentos) { //Al invocar la interfaz, nos devuelve una lista de tipo "Map<String,Object>" llamada "documentos" en la cual se almacenarán todos los campos de todos los documentos de la colección
                    List<EmpleadosItems> listaEmpleados = new ArrayList<>(); //Creamos una lista de tipo "EmpleadosItems"

                    //Hacemos un for que recorra los documentos de la lista "documentos" y los vaya guardando uno por uno en la variable temporal "documento" de tipo "Map<String,Object>"
                    for (Map<String,Object> documento : documentos) {
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
        catch (Exception e) {
            Log.w("ObtenerEmpleados", e);
        }
    }

    public void obtenerUnUsuario(FirestoreCallbacks.FirestoreDocumentCallback callback) {
        try {
            FirebaseUser user = Utilidades.obtenerUsuario(); //Obtenemos el usuario actual llamando el método utilitario "obtenerUsuario"
            String correoActual = user.getEmail(); //Obtenemos el correo del usuario actual

            oper.obtenerUnRegistro("usuarios", "Correo", correoActual, new FirestoreCallbacks.FirestoreDocumentCallback() {
                @Override
                public void onCallback(Map<String, Object> documento) {
                    if (documento != null)
                        callback.onCallback(documento);
                    else
                        Log.w("ObtenerUsuario", "Usuario no encontrado");
                }

                @Override
                public void onFailure(Exception e) {
                    callback.onFailure(e);
                    Log.w("BuscarUsuario", "Error al obtener el usuario", e);
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerUsuario", e);
        }
    }

    //Método que nos permite insertar un nuevo usuario
    public void insertarUsuario(String nombre, String identidad, String telefono, String rol, String cuadrilla) {
        try {
            //Si estas tres variables que contienen el contenido de las cajas de texto de "Agregar Usuario" no están vacías, que entre al if
            if (!nombre.isEmpty() && !identidad.isEmpty() && !telefono.isEmpty()) {
                validarIdentidadOriginal(identidad, new FirestoreCallbacks.FirestoreValidationCallback() {
                    @Override
                    public void onResultado(boolean esValido) {
                        if (!esValido) { //Si "esValido" es true, quiere decir que se encontró la identidad y ya pertenece a otro usuario
                            Toast.makeText(contexto, "LA IDENTIDAD YA PERTENECE A OTRO USUARIO", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Map<String, Object> datos = new HashMap<>(); //HashMap que nos ayudará a almacenar los nombres de los campos de la colección y los datos a ser insertados en cada campo

                        //Insertamos los datos en el HashMap usando ".put", indicando entre comillas el nombre del campo, y después de la coma, el valor a insertar
                        datos.put("Nombre", nombre);
                        datos.put("Identidad", identidad);
                        datos.put("Telefono", telefono);
                        datos.put("Rol", rol);
                        datos.put("Correo", ""); //El correo estará vacío hasta que el usuario lo cree más adelante
                        datos.put("Estado", true);

                        //Si la selección hecha en el spinner "Cuadrillas" fue "No Pertenece", que inserte un valor vacío ("") en el campo "Cuadrilla" de Firestore
                        if (cuadrilla.contentEquals("No Pertenece"))
                            datos.put("Cuadrilla", "");
                        else //Pero si la selección hecha en el spinner "Cuadrillas" fue otra diferente a "No Pertenece", que inserte esa selección en el campo "Cuadrilla" de Firestore
                            datos.put("Cuadrilla", cuadrilla);

                        //Llamamos el método "insertarRegistros" de la clase "FirestoreOperaciones" y le mandamos el nombre de la colección, el HashMap con los datos a insertar. También invocamos los métodos "onSuccess" y "onFailure" de la interfaz FirestoreInsertCallback
                        oper.insertarRegistros("usuarios", datos, new FirestoreCallbacks.FirestoreTextCallback() {
                            @Override
                            public void onSuccess(String idDocumento) { //Si la inserción fue exitosa, entrará aquí
                                Toast.makeText(contexto, "USUARIO AGREGADO EXITOSAMENTE", Toast.LENGTH_SHORT).show();
                                Utilidades.iniciarActivity(contexto, AdmPantallas.class, true);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(contexto, "ERROR AL AGREGAR EL USUARIO", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            } else {
                Toast.makeText(contexto, "TODOS LOS CAMPOS DEBEN LLENARSE", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            Log.w("InsertarUsuario", e);
        }
    }

    //Método que permite validar si la identidad del usuario a ingresar ya está usada por otro usuario o si está disponible
    public void validarIdentidadOriginal(String identidad, FirestoreCallbacks.FirestoreValidationCallback callback) { //Recibe la identidad e invoca la interfaz "FirestoreValidationCallback"
        try {
            oper.obtenerUnRegistro("usuarios", "Identidad", identidad, new FirestoreCallbacks.FirestoreDocumentCallback() {
                @Override
                public void onCallback(Map<String, Object> documento) {
                    if (documento == null) { //Si "documento" es nulo, quiere decir que no encontró la identidad entre los usuarios
                        callback.onResultado(true); //Llamamos a la interfaz "callBack" y le mandamos "true" para indicar que la identidad ingresada está disponible
                    } else { //Pero si "documento" no es nulo, quiere decir que si encontró la identidad
                        callback.onResultado(false); //Llamamos a la interfaz "callBack" y le mandamos "false" para indicar que la identidad ingresada está ocupada
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.w("Verificar Identidad", "Error al obtener la identidad: ", e);
                    callback.onResultado(false); //Devolvemos false por cualquier error
                }
            });
        }
        catch (Exception e) {
            Log.w("BuscarIdentidad", e);
        }
    }
}
