package com.example.uvfpoebatallanaval.modelo;

/**
 * Interfaz Arrastrable que define el comportamiento que deben tener los elementos arrastrables
 * dentro del juego, como los barcos que el jugador puede mover en el tablero.
 */
public interface Arrastrable {
    /**
     * Mueve el objeto a las coordenadas especificadas.
     *
     * @param x Coordenada horizontal a la que se debe mover el objeto.
     * @param y Coordenada vertical a la que se debe mover el objeto.
     */
    void mover(double x, double y);
}