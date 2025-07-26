package com.example.uvfpoebatallanaval.modelo;

import com.example.uvfpoebatallanaval.excepciones.ExcepcionCeldaOcupada;
import com.example.uvfpoebatallanaval.excepciones.ExcepcionPosicionInvalida;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase Jugador qepresenta al jugador humano en el juego Batalla Naval.
 * Contiene su nombre, el tablero de juego, su flota de barcos
 * y el conteo de barcos hundidos por el oponente.
 *
 * @author Nicolle Paz y Steven Candela
 */
public class Jugador implements Serializable {
    private String nombre;
    private Tablero tablero;
    private List<Barco> flota;
    private int barcosHundidos;

    /**
     * Crea un nuevo jugador con el nombre especificado.
     * Inicializa el tablero y la flota del jugador.
     *
     * @param nombre Nombre del jugador.
     */

    public Jugador(String nombre) {
        this.nombre = nombre;
        this.tablero = new Tablero();
        this.flota = new ArrayList<>();
        this.barcosHundidos = 0;
        inicializarFlota();
    }

    /**
     * Inicializa la flota del jugador con barcos predefinidos:
     * 1 portaaviones, 2 submarinos, 3 destructores y 4 fragatas.
     * Algunos barcos se colocan en orientación horizontal y otros en vertical.
     */
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

    /**
     * Intenta colocar un barco en la posición especificada del tablero.
     *
     * @param barco Barco a colocar.
     * @param fila Fila donde se desea colocar el barco.
     * @param col Columna donde se desea colocar el barco.
     * @return true si el barco se colocó exitosamente.
     * @throws ExcepcionCeldaOcupada Si una celda ya está ocupada por otro barco.
     * @throws ExcepcionPosicionInvalida Si la posición está fuera de los límites o es inválida.
     */
    public boolean colocarBarco(Barco barco, int fila, int col) throws ExcepcionCeldaOcupada, ExcepcionPosicionInvalida {
        return tablero.colocarBarco(barco, fila, col);
    }

    /**
     * Retorna el tablero del jugador.
     *
     * @return El tablero del jugador.
     */
    public Tablero getTablero() {
        return tablero;
    }

    /**
     * Retorna la lista de barcos del jugador.
     *
     * @return Flota del jugador.
     */
    public List<Barco> getFlota() {
        return flota;
    }

    /**
     * Retorna el nombre del jugador.
     *
     * @return Nombre del jugador.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Retorna la cantidad de barcos hundidos del jugador.
     *
     * @return Número de barcos hundidos.
     */
    public int getBarcosHundidos() {
        return barcosHundidos;
    }

    /**
     * Establece la cantidad de barcos hundidos del jugador.
     *
     * @param barcosHundidos Nuevo número de barcos hundidos.
     */
    public void setBarcosHundidos(int barcosHundidos) {
        this.barcosHundidos = barcosHundidos;
    }

    /**
     * Incrementa en uno el contador de barcos hundidos.
     */
    public void incrementarBarcosHundidos() {
        barcosHundidos++;
    }

    /**
     * Busca un barco en la flota del jugador según su tipo.
     *
     * @param tipo Tipo del barco (por ejemplo: "fragata").
     * @return El barco correspondiente o null si no se encuentra.
     */
    public Barco getBarcoPorTipo(String tipo) {
        for (Barco barco : flota) {
            if (barco.getTipo().equalsIgnoreCase(tipo)) {
                return barco;
            }
        }
        return null;
    }
}