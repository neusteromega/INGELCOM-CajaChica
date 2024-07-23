package com.ingelcom.cajachica.Modelos;

public class IngresosItems {

    private String id;
    private String usuario;
    private String fechaHora;
    private String cuadrilla;
    private String transferencia;
    private double total;

    public IngresosItems(String id, String usuario, String fechaHora, String cuadrilla, String transferencia, double total) {
        this.id = id;
        this.usuario = usuario;
        this.fechaHora = fechaHora;
        this.cuadrilla = cuadrilla;
        this.transferencia = transferencia;
        this.total = total;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
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

    public String getTransferencia() {
        return transferencia;
    }

    public void setTransferencia(String transferencia) {
        this.transferencia = transferencia;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
