package com.example.uvfpoebatallanaval.modelo;

import com.example.uvfpoebatallanaval.excepciones.ExcepcionCeldaOcupada;
import com.example.uvfpoebatallanaval.excepciones.ExcepcionPosicionInvalida;

import java.util.Random;

/**
 * Clase Maquina que representa a la máquina (IA) en el juego Batalla Naval.
 * Hereda de {@link Jugador} y realiza la colocación automática de barcos y disparos aleatorios.
 *
 * @author Camilo Portilla y Steven Candela
 */
public class Maquina extends Jugador {

    private Random random;

    /**
     * Crea una nueva instancia de Maquina con el nombre "Maquina" y coloca su flota aleatoriamente en el tablero.
     *
     * @throws ExcepcionCeldaOcupada si una celda ya está ocupada por otro barco.
     * @throws ExcepcionPosicionInvalida si la posición de colocación es inválida.
     */
    public Maquina() throws ExcepcionCeldaOcupada, ExcepcionPosicionInvalida {
        super("Maquina"); // Llama al constructor de Jugador con nombre "Maquina"
        random = new Random();
        colocarFlotaAleatoriamente();
    }

    /**
     * Coloca todos los barcos de la flota de la máquina en posiciones aleatorias válidas del tablero.
     *
     * @throws ExcepcionCeldaOcupada si intenta colocar un barco en una celda ya ocupada.
     * @throws ExcepcionPosicionInvalida si se intenta colocar un barco fuera de los límites del tablero.
     */
    private void colocarFlotaAleatoriamente() throws ExcepcionCeldaOcupada, ExcepcionPosicionInvalida {
        for (Barco barco : getFlota()) {
            boolean colocado = false;
            while (!colocado) {
                int fila = random.nextInt(10); // suponiendo tablero 10x10
                int col = random.nextInt(10);
                colocado = colocarBarco(barco, fila, col);
            }
        }
    }

    /**
     * Realiza un disparo aleatorio sobre el tablero enemigo hasta acertar en una celda válida.
     * Solo se detiene cuando el disparo resulta en un acierto.
     *
     * @param tableroEnemigo el tablero del jugador enemigo sobre el que se va a disparar.
     */
    public void disparar(Tablero tableroEnemigo) {
        String resultado;
        boolean DisparoValido = false;

        while (!DisparoValido) {
            int fila = random.nextInt(10);
            int col = random.nextInt(10);
            resultado = tableroEnemigo.disparar(fila, col);

            if (resultado.equals("acierto")) {
                DisparoValido = true;
            }
        }
    }
}