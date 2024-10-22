package com.ingelcom.cajachica.DAO;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
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
    public Context contexto;
    private FirestoreOperaciones oper = new FirestoreOperaciones();

    public Usuario(Context contexto) {
        this.contexto = contexto;
    }

    //Método que nos permitirá obtener los usuarios, pero sólo los que tengan el rol "Empleado"
    public void obtenerUsuarios(String tipo, FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<EmpleadosItems> callback) { //Recibe un "tipo" para saber si queremos todos los usuarios o sólo los empleados, y llamamos la interfaz "FirestoreAllSpecialDocumentsCallback" y le indicamos que debe ser de tipo "EmpleadosItems"
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
                    Log.e("ObtenerUsuarios", "Error al obtener los usuarios", e);
                    callback.onFailure(e);
                }
            });
        }
        catch (Exception e) {
            Log.e("ObtenerUsuarios", "Error al obtener los usuarios", e);
        }
    }

    //Método que permite obtener el documento de Firestore del usuario actual
    public void obtenerUsuarioActual(FirestoreCallbacks.FirestoreDocumentCallback callback) {
        try {
            FirebaseUser user = Utilidades.obtenerUsuario(); //Obtenemos el usuario actual llamando el método utilitario "obtenerUsuario"
            String correoActual = user.getEmail(); //Obtenemos el correo del usuario actual

            //Llamamos el método "obtenerUnRegistro" de la clase FirestoreOperaciones y le mamdamos el nombre de la colección, el campo, y el dato a buscar, e invocamos el "FirestoreDocumentCallback"
            oper.obtenerUnRegistro("usuarios", "Correo", correoActual, new FirestoreCallbacks.FirestoreDocumentCallback() {
                @Override
                public void onCallback(Map<String, Object> documento) {
                    if (documento != null) //Si "documento" no es nulo, quiere decir que encontró el usuario
                        callback.onCallback(documento); //Guardamos el "documento" con los datos del usuario en el "onCallback"
                    else //Si "documento" es nulo, no se encontró el usuario en la colección, y entrará en este else
                        Log.w("ObtenerUsuario", "Usuario no encontrado");
                }

                @Override
                public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                    Log.e("ObtenerUsuario", "Error al obtener el usuario", e);
                    callback.onFailure(e);
                }
            });
        }
        catch (Exception e) {
            Log.e("ObtenerUsuario", "Error al obtener el usuario", e);
        }
    }

    //Método que permite obtener el documento de Firestore de un usuario buscándolo mediante su identidad
    public void obtenerUnUsuario(String identidad, FirestoreCallbacks.FirestoreDocumentCallback callback) {
        try {
            //Llamamos el método "obtenerUnRegistro" de la clase FirestoreOperaciones y le mamdamos el nombre de la colección, el campo, y el dato a buscar, e invocamos el "FirestoreDocumentCallback"
            oper.obtenerUnRegistro("usuarios", "Identidad", identidad, new FirestoreCallbacks.FirestoreDocumentCallback() {
                @Override
                public void onCallback(Map<String, Object> documento) {
                    if (documento != null) //Si "documento" no es nulo, quiere decir que encontró el usuario
                        callback.onCallback(documento); //Guardamos el "documento" con los datos del usuario en el "onCallback"
                    else //Si "documento" es nulo, no se encontró el usuario en la colección, y entrará en este else
                        Log.w("ObtenerUsuario", "Usuario no encontrado");
                }

                @Override
                public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                    Log.e("ObtenerUsuario", "Error al obtener el usuario", e);
                    callback.onFailure(e);
                }
            });
        }
        catch (Exception e) {
            Log.e("ObtenerUsuario", "Error al obtener el usuario", e);
        }
    }

    //Método que nos permite insertar un nuevo usuario
    public void insertarUsuario(String nombre, String identidad, String telefono, String rol, String cuadrilla) {
        try {
            if (!nombre.isEmpty() && !identidad.isEmpty() && !telefono.isEmpty()) { //Si estas tres variables que contienen el contenido de las cajas de texto de "Agregar Usuario" no están vacías, que entre al if
                validarIdentidadOriginal(identidad, new FirestoreCallbacks.FirestoreValidationCallback() { //Llamámos el método "validarIdentidadOriginal" de abajo donde le mandamos la identidad recibida por parámetro
                    @Override
                    public void onResultado(boolean esValido) {
                        if (!esValido) { //Si "esValido" es true, quiere decir que se encontró la identidad y ya pertenece a otro usuario
                            Toast.makeText(contexto, "LA IDENTIDAD YA PERTENECE A OTRO USUARIO", Toast.LENGTH_SHORT).show();
                            return; //Finalizamos el método
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
                                Utilidades.iniciarActivity(contexto, AdmPantallas.class, true); //Iniciamos el activity "AdmPantallas" y mandamos un true para que cierre el activity actual
                            }

                            @Override
                            public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                                Toast.makeText(contexto, "ERROR AL AGREGAR EL USUARIO", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
            else {
                Toast.makeText(contexto, "TODOS LOS CAMPOS DEBEN LLENARSE", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            Log.e("InsertarUsuario", "Error al insertar el usuario", e);
        }
    }

    //Método que nos permite editar los datos de un usuario
    public void editarUsuario(String activityPerfil, String nombre, String correo, String identidadVieja, String identidadNueva, String telefono, String cuadrilla, String rol) {
        try {
            if (!nombre.isEmpty() && !identidadNueva.isEmpty() && !telefono.isEmpty()) { //Si estas tres variables que contienen el contenido de las cajas de texto de "Editar Perfil" no están vacías, que entre al if
                validarIdentidadAlEditar(identidadNueva, correo, new FirestoreCallbacks.FirestoreValidationCallback() { //Llamámos el método "validarIdentidadAlEditar" de abajo donde le mandamos la identidadNueva y el correo recibidos por parámetro
                    @Override
                    public void onResultado(boolean esValido) {
                        if (!esValido) { //Si "esValido" es false, quiere decir que se encontró la identidad y ya pertenece a otro usuario
                            Toast.makeText(contexto, "LA IDENTIDAD YA PERTENECE A OTRO USUARIO", Toast.LENGTH_SHORT).show();
                            return; //Finalizamos el método
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
                                    Utilidades.iniciarActivityConString(contexto, Perfil.class, "ActivityPerfil", activityPerfil, true); //Y cuando "activityPerfil" no esté vacío, redireccionamos al usuario al activity "Perfil" y mandamos el contenido de la variable "activityPerfil" que indica el tipo de usuario que está en la sesión actual (Empleado o administrador)
                            }

                            @Override
                            public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
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
            Log.e("ActualizarUsuario", "Error al actualizar el usuario", e);
        }
    }

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
                public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                    Log.e("BuscarIdentidad", "Error al obtener la identidad", e);
                    callback.onResultado(false); //Devolvemos false por cualquier error
                }
            });
        }
        catch (Exception e) {
            Log.e("BuscarIdentidad", "Error al obtener la identidad", e);
        }
    }

    //Método que permite validar si la identidad del usuario a editar ya está usada por otro usuario o si está disponible
    public void validarIdentidadAlEditar(String identidad, String correo, FirestoreCallbacks.FirestoreValidationCallback callback) { //Recibe la identidad, el correo del usuario a editar, e invoca la interfaz "FirestoreValidationCallback"
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
                public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                    Log.e("VerificarIdentidadCorreo", "Error al obtener la identidad", e);
                    callback.onResultado(false); //Devolvemos false por cualquier error
                }
            });
        }
        catch (Exception e) {
            Log.e("VerificarIdentidadCorreo", "Error al obtener la identidad", e);
        }
    }
}