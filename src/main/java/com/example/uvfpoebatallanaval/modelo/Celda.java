package com.example.uvfpoebatallanaval.modelo;

import java.io.Serializable;

/**
 * Clase Celda que representa una celda dentro del tablero del juego.
 * Puede contener un barco y registrar si ha sido atacada.
 *
 * @author Nicolle Paz y Steven Candela
 */
public class Celda implements Serializable {
    private boolean tieneBarco;
    private boolean fueAtacada;
    private Barco barco;

    /**
     * Crea una celda vacía, sin barco y sin haber sido atacada.
     */
    public Celda() {
        this.tieneBarco = false;
        this.fueAtacada = false;
        this.barco = null;
    }

    /**
     * Verifica si la celda contiene un barco.
     *
     * @return true si tiene un barco, false en caso contrario.
     */
    public boolean tieneBarco() {
        return tieneBarco;
    }

    /**
     * Coloca un barco en la celda.
     *
     * @param barco el barco a colocar.
     */
    public void colocarBarco(Barco barco) {
        this.barco = barco;
        this.tieneBarco = true;
    }

    /**
     * Verifica si la celda ha sido atacada previamente.
     *
     * @return true si fue atacada, false en caso contrario.
     */
    public boolean fueAtacada() {
        return fueAtacada;
    }

    /**
     * Obtiene el barco presente en esta celda.
     *
     * @return el barco, o null si no hay ninguno.
     */
    public Barco getBarco() {
        return barco;
    }

    /**
     * Elimina el barco de la celda.
     */
    public void removerBarco() {
        this.barco = null;
        this.tieneBarco = false;
    }

    /**
     * Marca la celda como atacada y determina el resultado del disparo.
     *
     * @return "agua" si no hay barco, "tocado" si se impactó un barco,
     *         o "hundido" si el barco fue destruido completamente.
     */
    public String recibirDisparo() {
        fueAtacada = true;

        if (!tieneBarco || barco == null) {
            System.out.println("Disparo en agua");
            return "agua";
        } else {
            System.out.println("Disparo en barco");
            barco.registrarImpacto();
            return barco.estaHundido() ? "hundido" : "tocado";
        }
    }
}