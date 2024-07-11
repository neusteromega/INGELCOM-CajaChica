package com.ingelcom.cajachica.Modelos;

public class EmpleadosItems {
    private String nombre;
    private String correo;
    private String cuadrilla;

    public EmpleadosItems(String nombre, String correo, String cuadrilla) {
        this.nombre = nombre;
        this.correo = correo;
        this.cuadrilla = cuadrilla;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCuadrilla() {
        return cuadrilla;
    }

    public void setCuadrilla(String cuadrilla) {
        this.cuadrilla = cuadrilla;
    }
}
