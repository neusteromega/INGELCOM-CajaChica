package com.ingelcom.cajachica.DAO;

import android.content.Context;
import android.util.Log;

import com.google.firebase.Timestamp;
import com.ingelcom.cajachica.Herramientas.FirestoreCallbacks;
import com.ingelcom.cajachica.Herramientas.Utilidades;
import com.ingelcom.cajachica.Modelos.DeduccionesItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Deduccion {

    public Context contexto;
    private FirestoreOperaciones oper = new FirestoreOperaciones();

    public Deduccion(Context contexto) {
        this.contexto = contexto;
    }

    //Método que nos permitirá obtener todos los ingresos, pero dividiéndolos por la cuadrilla
    public void obtenerDeducciones(String datoCuadrilla, FirestoreCallbacks.FirestoreAllSpecialDocumentsCallback<DeduccionesItems> callback) {
        try {
            //Llamamos el método "obtenerRegistros" de "FirestoreOperaciones", le mandamos el nombre de la colección, e invocamos la interfaz "FirestoreAllDocumentsCallback"
            oper.obtenerRegistros("deducciones", new FirestoreCallbacks.FirestoreAllDocumentsCallback() {
                @Override
                public void onCallback(List<Map<String, Object>> documentos) { //Al invocar la interfaz, nos devuelve una lista de tipo "Map<String,Object>" llamada "documentos" en la cual se almacenarán todos los campos de todos los documentos de la colección
                    List<DeduccionesItems> listaDeducciones = new ArrayList<>(); //Creamos una lista de tipo "DeduccionesItems"

                    //Hacemos un for que recorra los documentos de la lista "documentos" y los vaya guardando uno por uno en la variable temporal "documento" de tipo "Map<String,Object>"
                    for (Map<String,Object> documento : documentos) {
                        //Extraemos los campos del HashMap "documento", los campos necesarios en "DeduccionesItems"
                        String id = (String) documento.get("ID");
                        String usuario = (String) documento.get("Usuario");
                        String fechaHora = Utilidades.convertirTimestampAString((Timestamp) documento.get("Fecha")); //En este campo, al ser un Timestamp y no un String, llamamos al método utilitario "convertirTimestampAString" que convierte un objeto Timestamp y retorna un string
                        String cuadrilla = (String) documento.get("Cuadrilla");
                        double total = Utilidades.convertirObjectADouble(documento.get("Total")); //En este campo, al ser un number (o double) y no un String, llamamos al método utilitario "convertirObjectADouble" que convierte un object de Firestore y retorna un double

                        //Comprobamos la cuadrilla a la cual se le desea ver sus deducciones; si la cuadrilla se encuentra en la "deduccion", entrará al if, y por ende, habrán deducciones para visualizar en el ListadoDeducciones
                        if (cuadrilla.contentEquals(datoCuadrilla)) {
                            DeduccionesItems deduccion = new DeduccionesItems(id, usuario, fechaHora, cuadrilla, total); //Creamos un objeto de tipo "DeduccionesItems" en el cual guardamos los datos extraídos arriba
                            listaDeducciones.add(deduccion); //El objeto de tipo "DeduccionesItems" lo guardamos en la lista "listaDeducciones"
                        }
                    }
                    //Cuando salga del "for", ya tendremos todas las deducciones en la "listaDeducciones", y esta lista es la que mandamos al método "onCallback" de la interfaz
                    callback.onCallback(listaDeducciones);
                }

                @Override
                public void onFailure(Exception e) { //Por último, manejamos el error con una excepción "e" y esta la mandamos al método "onFailure"
                    Log.e("FirestoreError", "Error al obtener los documentos", e);
                    callback.onFailure(e);
                }
            });
        }
        catch (Exception e) {
            Log.w("ObtenerDeducciones", e);
        }
    }
}
