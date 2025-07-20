package com.example.uvfpoebatallanaval.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa al jugador humano en el juego Batalla Naval.
 */
public class Jugador implements Serializable {
    private String nombre;
    private Tablero tablero;
    private List<Barco> flota;
    private int barcosHundidos;

    public Jugador(String nombre) {
        this.nombre = nombre;
        this.tablero = new Tablero();
        this.flota = new ArrayList<>();
        this.barcosHundidos = 0;
        inicializarFlota();
    }

    private void inicializarFlota() {
        flota.clear();
        flota.add(new Barco("portaaviones", true));

        flota.add(new Barco("submarino", true));
        flota.add(new Barco("submarino", false));

        flota.add(new Barco("destructor", true));
        flota.add(new Barco("destructor", false));
        flota.add(new Barco("destructor", true));

        flota.add(new Barco("fragata", true));
        flota.add(new Barco("fragata", false));
        flota.add(new Barco("fragata", true));
        flota.add(new Barco("fragata", false));
    }

    public boolean colocarBarco(Barco barco, int fila, int col) {
        return tablero.colocarBarco(barco, fila, col);
    }

    public Tablero getTablero() {
        return tablero;
    }

    public List<Barco> getFlota() {
        return flota;
    }

    public String getNombre() {
        return nombre;
    }

    public int getBarcosHundidos() {
        return barcosHundidos;
    }

    public void setBarcosHundidos(int barcosHundidos) {
        this.barcosHundidos = barcosHundidos;
    }

    public Barco getBarcoPorTipo(String tipo) {
        for (Barco barco : flota) {
            if (barco.getTipo().equalsIgnoreCase(tipo)) {
                return barco;
            }
        }
        return null;
    }
}