package com.example.uvfpoebatallanaval.modelo;

import java.io.Serializable;

public class Celda implements Serializable {
    private boolean tieneBarco;
    private boolean fueAtacada;
    private Barco barco;

    public Celda() {
        this.tieneBarco = false;
        this.fueAtacada = false;
        this.barco = null;
    }

    public boolean tieneBarco() {
        return tieneBarco;
    }

    public void colocarBarco(Barco barco) {
        this.barco = barco;
        this.tieneBarco = true;
    }

    public boolean fueAtacada() {
        return fueAtacada;
    }

    public Barco getBarco() {
        return barco;
    }

    public void removerBarco() {
        this.barco = null;
        this.tieneBarco = false;
    }

    public String recibirDisparo() {
        fueAtacada = true;

        if (!tieneBarco) {
            return "agua";
        } else {
            barco.registrarImpacto();
            return barco.estaHundido() ? "hundido" : "tocado";
        }
    }
}