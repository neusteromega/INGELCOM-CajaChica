package com.ingelcom.cajachica.Modelos;

public class GastosItems {

    private String id;
    private String fechaHora;
    private String cuadrilla;
    private String lugarCompra;
    private String tipoCompra;
    private String descripcion;
    private String numeroFactura;
    private String usuario;
    private String rol;
    private String imagen;
    private double total;

    public GastosItems(String id, String fechaHora, String cuadrilla, String lugarCompra, String tipoCompra, String descripcion, String numeroFactura, String usuario, String rol, String imagen, double total) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.cuadrilla = cuadrilla;
        this.lugarCompra = lugarCompra;
        this.tipoCompra = tipoCompra;
        this.descripcion = descripcion;
        this.numeroFactura = numeroFactura;
        this.usuario = usuario;
        this.rol = rol;
        this.imagen = imagen;
        this.total = total;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getCuadrilla() {
        return cuadrilla;
    }

    public void setCuadrilla(String cuadrilla) {
        this.cuadrilla = cuadrilla;
    }

    public String getLugarCompra() {
        return lugarCompra;
    }

    public void setLugarCompra(String lugarCompra) {
        this.lugarCompra = lugarCompra;
    }

    public String getTipoCompra() {
        return tipoCompra;
    }

    public void setTipoCompra(String tipoCompra) {
        this.tipoCompra = tipoCompra;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}