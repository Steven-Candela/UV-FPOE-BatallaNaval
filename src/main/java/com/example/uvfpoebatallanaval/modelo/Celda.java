package com.example.uvfpoebatallanaval.modelo;

public class Celda {
    private boolean tieneBarco = false;
    private boolean disparado = false;

    public boolean tieneBarco() {
        return tieneBarco;
    }

    public void setBarco(boolean tieneBarco) {
        this.tieneBarco = tieneBarco;
    }

    public boolean isDisparado() {
        return disparado;
    }

    public void setDisparado(boolean disparado) {
        this.disparado = disparado;
    }
    // En Celda.java
    private Barco barco;

    public Barco getBarco() {
        return barco;
    }

    public void setBarco(Barco barco) {
        this.barco = barco;
    }

}
