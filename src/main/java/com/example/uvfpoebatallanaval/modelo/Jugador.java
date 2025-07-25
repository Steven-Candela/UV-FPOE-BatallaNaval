package com.example.uvfpoebatallanaval.modelo;

import com.example.uvfpoebatallanaval.excepciones.ExcepcionCeldaOcupada;
import com.example.uvfpoebatallanaval.excepciones.ExcepcionPosicionInvalida;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa al jugador humano en el juego Batalla Naval.
 */
public class Jugador implements Serializable {
    private String nickName;
    private Tablero tablero;
    private List<Barco> flota;
    private int barcosHundidos;

    public Jugador(String nickName) {
        this.nickName = nickName;
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

    public boolean colocarBarco(Barco barco, int fila, int col) throws ExcepcionCeldaOcupada, ExcepcionPosicionInvalida {
        return tablero.colocarBarco(barco, fila, col);
    }

    public Tablero getTablero() {
        return tablero;
    }

    public List<Barco> getFlota() {
        return flota;
    }
    public void setNickName(String nickName){this.nickName = nickName;}
    public String getnickName() {
        return nickName;
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