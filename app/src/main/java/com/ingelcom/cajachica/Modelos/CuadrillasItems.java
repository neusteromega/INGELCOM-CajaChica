package com.ingelcom.cajachica.Modelos;

public class CuadrillasItems {

    private String cuadrilla;
    private double dinero;

    public CuadrillasItems(String cuadrilla, double dinero) {
        this.cuadrilla = cuadrilla;
        this.dinero = dinero;
    }

    public String getCuadrilla() {
        return cuadrilla;
    }

    public void setCuadrilla(String cuadrilla) {
        this.cuadrilla = cuadrilla;
    }

    public double getDinero() {
        return dinero;
    }

    public void setDinero(double dinero) {
        this.dinero = dinero;
    }
}
