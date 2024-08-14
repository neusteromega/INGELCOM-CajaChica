package com.ingelcom.cajachica.Modelos;

public class EstadisticasItems {

    private String cuadrilla;
    private double total;

    public EstadisticasItems(String cuadrilla, double total) {
        this.cuadrilla = cuadrilla;
        this.total = total;
    }

    public String getCuadrilla() {
        return cuadrilla;
    }

    public void setCuadrilla(String cuadrilla) {
        this.cuadrilla = cuadrilla;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
