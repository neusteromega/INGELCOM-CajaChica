package com.ingelcom.cajachica.Modelos;

public class EmpleadosItems {
    private String nombre;
    private String correo;
    private String cuadrilla;
    private String identidad;
    private String telefono;
    private String rol;
    private String estado;

    public EmpleadosItems(String nombre, String correo, String cuadrilla, String identidad, String telefono, String rol, String estado) {
        this.nombre = nombre;
        this.correo = correo;
        this.cuadrilla = cuadrilla;
        this.identidad = identidad;
        this.telefono = telefono;
        this.rol = rol;
        this.estado = estado;
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

    public String getIdentidad() {
        return identidad;
    }

    public void setIdentidad(String identidad) {
        this.identidad = identidad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
