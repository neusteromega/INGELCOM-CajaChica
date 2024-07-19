package com.ingelcom.cajachica.Herramientas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewGastosModel extends ViewModel {

    //Declaramos un campo "final" llamado "fecha" de tipo "MutableLiveData<String>". MutableLiveData es una clase que permite que los datos sean observables y también mutables, es decir, su valor puede cambiar
    private final MutableLiveData<String> fecha = new MutableLiveData<>();

    //Método que actualiza el valor del MutableLiveData "fecha" con el nuevo valor proporcionado (será el lblFecha.getText() del ListadoGastos)
    public void setFecha(String fecha) {
        this.fecha.setValue(fecha);
    }

    //Método que devuelve un "LiveData<String>"
    public LiveData<String> getFecha() {
        //Devuelve el MutableLiveData "fecha" como LiveData. Esto significa que otros componentes pueden observar este LiveData pero no pueden modificarlo directamente
        return fecha;
    }
}
