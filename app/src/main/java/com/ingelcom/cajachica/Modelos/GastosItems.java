package com.ingelcom.cajachica.Modelos;

public class GastosItems {
    private String tipoCompra;
    private String fecha;
    private String empleado;
    private String total;

    public GastosItems(String tipoCompra, String fecha, String empleado, String total) {
        this.tipoCompra = tipoCompra;
        this.fecha = fecha;
        this.empleado = empleado;
        this.total = total;
    }

    public String getTipoCompra() {
        return tipoCompra;
    }

    public void setTipoCompra(String tipoCompra) {
        this.tipoCompra = tipoCompra;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEmpleado() {
        return empleado;
    }

    public void setEmpleado(String empleado) {
        this.empleado = empleado;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
