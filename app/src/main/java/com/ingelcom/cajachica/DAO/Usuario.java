package com.ingelcom.cajachica.DAO;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.ingelcom.cajachica.AdmPantallas;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.ListadoEmpleados;
import com.ingelcom.cajachica.Modelos.EmpleadosItems;
import com.ingelcom.cajachica.Perfil;

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
    public void obtenerUsuarios(String tipo, FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<EmpleadosItems> callback) { //Llamamos la interfaz "FirestoreAllSpecialDocumentsCallback" y le indicamos que debe ser de tipo "EmpleadosItems"
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

                        if (correo.isEmpty()) { //Si el usuario aún no tiene un correo, que asigne el texto "Sin Correo"
                            correo = "Sin Correo";
                        }

                        if (tipo.equalsIgnoreCase("Empleados")) { //Si "tipo" contiene el texto "Empleados", filtramos solo los empleados con rol "Empleado"
                            if (rol.equalsIgnoreCase("Empleado")) {
                                EmpleadosItems empleado = new EmpleadosItems(nombre, correo, cuadrilla, identidad, telefono, rol, estado); //Creamos un objeto de tipo "EmpleadosItems" en el cual guardamos los datos extraídos arriba
                                listaEmpleados.add(empleado); //El objeto de tipo "EmpleadosItems" lo guardamos en la lista "listaEmpleados"
                            }
                        }
                        else if (tipo.equalsIgnoreCase("Todos")) { //En cambio, si "tipo" contiene el texto "Todos", no hacemos filtrado
                            EmpleadosItems empleado = new EmpleadosItems(nombre, correo, cuadrilla, identidad, telefono, rol, estado); //Creamos un objeto de tipo "EmpleadosItems" en el cual guardamos los datos extraídos arriba
                            listaEmpleados.add(empleado); //El objeto de tipo "EmpleadosItems" lo guardamos en la lista "listaEmpleados"
                        }
                    }
                    //Cuando salga del "for", ya tendremos todos los empleados con rol "Empleado" en la "listaEmpleados", y esta lista es la que mandamos al método "onCallback" de la interfaz
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

    //Método que permite obtener el documento de Firestore del usuario actual
    public void obtenerUsuarioActual(FirestoreCallbacks.FirestoreDocumentCallback callback) {
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

    //Método que permite obtener el documento de Firestore de un usuario buscándolo mediante su identidad
    public void obtenerUnUsuario(String identidad, FirestoreCallbacks.FirestoreDocumentCallback callback) {
        try {
            oper.obtenerUnRegistro("usuarios", "Identidad", identidad, new FirestoreCallbacks.FirestoreDocumentCallback() {
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

    //Método que nos permite editar los datos de un usuario
    public void editarUsuario(String activityPerfil, String nombre, String correo, String identidadVieja, String identidadNueva, String telefono, String cuadrilla, String rol) {
        try {
            if (!nombre.isEmpty() && !identidadNueva.isEmpty() && !telefono.isEmpty()) {
                validarIndentidadAlEditar(identidadNueva, correo, new FirestoreCallbacks.FirestoreValidationCallback() {
                    @Override
                    public void onResultado(boolean esValido) {
                        if (!esValido) { //Si "esValido" es false, quiere decir que se encontró la identidad y ya pertenece a otro usuario
                            Toast.makeText(contexto, "LA IDENTIDAD YA PERTENECE A OTRO USUARIO", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Map<String, Object> datos = new HashMap<>(); //Creamos un HashMap para guardar los nombres de los campos y los datos

                        //Guardamos las claves y datos en el HashMap
                        datos.put("Nombre", nombre);
                        datos.put("Identidad", identidadNueva);
                        datos.put("Telefono", telefono);

                        if (!cuadrilla.isEmpty()) //Si "cuadrilla" no está vacía, significa que un administrador está modificando un usuario, entonces que entre al if y guarde el dato de la cuadrilla en el HashMap
                            datos.put("Cuadrilla", cuadrilla);
                        if (!rol.isEmpty()) //Lo mismo que con la cuadrilla, si "rol" no está vacío, significa que un administrador está modificando un usuario, entonces que entre al if y guarde el dato del rol en el HashMap
                            datos.put("Rol", rol);

                        //Llamamos al método "agregarActualizarRegistrosColeccion" de la clase FirestoreOperaciones. Le mandamos el nombre de la colección, el campo a buscar, el dato a buscar, el HashMap con los nuevos campos y datos (o los campos existentes para actualizar su contenido) e invocamos la interfaz "FirestoreInsertCallback"
                        oper.agregarActualizarRegistrosColeccion("usuarios", "Identidad", identidadVieja, datos, new FirestoreCallbacks.FirestoreTextCallback() {
                            @Override
                            public void onSuccess(String texto) {
                                Toast.makeText(contexto, "USUARIO MODIFICADO EXITOSAMENTE", Toast.LENGTH_SHORT).show(); //Si la actualización de los datos del usuario fue exitosa, que muestre un Toast

                                if (activityPerfil.isEmpty())
                                    Utilidades.iniciarActivity(contexto, ListadoEmpleados.class, true); //Cuando el "activityPerfil" esté vacío, significa que es un admin quien está modificando los datos de un empleado, cuando es así, mandamos al usuario al "ListadoEmpleados" y no a la pantalla Perfil del empleado ya que este última, debe recibir una "identidad" para buscar los datos del empleado, pero si lo redireccionamos desde aquí, no recibiría esa identidad
                                else
                                    Utilidades.iniciarActivityConString(contexto, Perfil.class, "ActivityPerfil", activityPerfil, true); //Redireccionamos al usuario al activity "Perfil" y mandamos el contenido de la variable "activityPerfil" que indica el tipo de usuario que está en la sesión actual (Empleado o administrador)
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(contexto, "ERROR AL MODIFICAR EL USUARIO", Toast.LENGTH_SHORT).show(); //Mostramos un mensaje de error en un Toast si la actualización de los datos del usuario fue errónea
                            }
                        });
                    }
                });
            }
            else {
                    Toast.makeText(contexto, "TODOS LOS CAMPOS DEBEN ESTAR LLENOS", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            Log.w("ActualizarUsuario", e);
        }
    }

    /*public void actualizarCorreoAuthentication(String correoNuevo) {
        FirebaseUser user = Utilidades.obtenerUsuario(); //Obtenemos el usuario actual llamando el método utilitario "obtenerUsuario"
        String correoActual = user.getEmail(); //Obtenemos el correo del usuario actual

        if (user != null) {
            user.verifyBeforeUpdateEmail(correoNuevo)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("ActualizarCorreo", "Correo de verificación enviado al nuevo correo.");
                                Toast.makeText(contexto, "Por favor, verifica tu nuevo correo para completar la actualización.", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("ActualizarCorreo", "Error al enviar el correo de verificación.", task.getException());
                                Toast.makeText(contexto, "Error al enviar el correo de verificación", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }*/

    //Método que permite validar si la identidad del usuario a ingresar ya está usada por otro usuario o si está disponible
    public void validarIdentidadOriginal(String identidad, FirestoreCallbacks.FirestoreValidationCallback callback) { //Recibe la identidad e invoca la interfaz "FirestoreValidationCallback"
        try {
            //Llamamos el método "obtenerUnRegistro" de la clase FirestoreOperaciones, le indicamos que debe buscar en la colección "usuarios", el campo "Identidad" que coincida con el contenido de la variable "identidad" que se recibe como parámetro
            oper.obtenerUnRegistro("usuarios", "Identidad", identidad, new FirestoreCallbacks.FirestoreDocumentCallback() {
                @Override
                public void onCallback(Map<String, Object> documento) {
                    if (documento == null) { //Si "documento" es nulo, quiere decir que no encontró la identidad entre los usuarios
                        callback.onResultado(true); //Llamamos a la interfaz "callBack" y le mandamos "true" para indicar que la identidad ingresada está disponible
                    }
                    else { //Pero si "documento" no es nulo, significa que si encontró un usuario con la identidad
                        callback.onResultado(false); //Llamamos a la interfaz "callBack" y le mandamos "false" para indicar que la identidad ingresada está ocupada
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.w("VerificarIdentidad", "Error al obtener la identidad: ", e);
                    callback.onResultado(false); //Devolvemos false por cualquier error
                }
            });
        }
        catch (Exception e) {
            Log.w("BuscarIdentidad", e);
        }
    }

    //Método que permite validar si la identidad del usuario a editar ya está usada por otro usuario o si está disponible
    public void validarIndentidadAlEditar(String identidad, String correo, FirestoreCallbacks.FirestoreValidationCallback callback) { //Recibe la identidad, el correo del usuario a editar, e invoca la interfaz "FirestoreValidationCallback"
        try {
            //Llamamos el método "obtenerUnRegistro" de la clase FirestoreOperaciones, le indicamos que debe buscar en la colección "usuarios", el campo "Identidad" que coincida con el contenido de la variable "identidad" que se recibe como parámetro
            oper.obtenerUnRegistro("usuarios", "Identidad", identidad, new FirestoreCallbacks.FirestoreDocumentCallback() {
                @Override
                public void onCallback(Map<String, Object> documento) {
                    if (documento != null) { //Si "documento" no es nulo, significa que si encontró un usuario con la identidad
                        //Una vez obtenidos los datos del usuario con la identidad guardada en la variable "identidad", vamos a obtener su correo
                        String correoFirestore = (String) documento.get("Correo");

                        //Si el "correo" que se recibe como parámetro es igual al "correoFirestore" extraído de los datos del usuario encontrado con la "identidad", significa que el usuario que está editando su perfil está colocando su misma identidad (o en otras palabras, no está cambiando su identidad, está dejándola igual), en este caso, tenemos que devolver un true en el "callback" para permitir al usuario editar los datos de su perfil ya que la identidad establecida en el EditText en "AgregarEditarPerfil" es la misma del usuario y no pertenece a otro usuario
                        if (correo.equalsIgnoreCase(correoFirestore))
                            callback.onResultado(true); //Llamamos a la interfaz "callBack" y le mandamos "true" para indicar que la identidad está disponible
                        else
                            callback.onResultado(false); //Llamamos a la interfaz "callBack" y le mandamos "true" para indicar que la identidad está ocupada
                    }
                    else { //Pero si "documento" es nulo, quiere decir que no encontró la identidad entre los usuarios
                        callback.onResultado(true); //Llamamos a la interfaz "callBack" y le mandamos "true" para indicar que la identidad está disponible
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.w("VerificarIdentidadCorreo", "Error al obtener la identidad: ", e);
                    callback.onResultado(false); //Devolvemos false por cualquier error
                }
            });
        }
        catch (Exception e) {
            Log.w("BuscarIdentidadYCorreo", e);
        }
    }
}
