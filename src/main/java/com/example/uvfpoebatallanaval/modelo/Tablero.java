package com.example.uvfpoebatallanaval.modelo;

import com.example.uvfpoebatallanaval.excepciones.ExcepcionCeldaOcupada;
import com.example.uvfpoebatallanaval.excepciones.ExcepcionPosicionInvalida;

import java.io.Serializable;

/**
 * Clase Tablero que representa el tablero del juego, donde se colocan barcos y se realizan disparos.
 * Contiene una matriz de celdas y métodos para interactuar con ellas.
 *
 * @author Nicolle Paz y Steven Candela
 */
public class Tablero implements Serializable {
    private Celda[][] celdas;

    /**
     * Crea un nuevo tablero de 11x11 celdas.
     */
    public Tablero() {
        celdas = new Celda[11][11];
        for (int fila = 0; fila < 11; fila++) {
            for (int col = 0; col < 11; col++) {
                celdas[fila][col] = new Celda();
            }
        }
    }

    /**
     * Retorna la celda ubicada en la posición especificada.
     *
     * @param fila    la fila de la celda.
     * @param columna la columna de la celda.
     * @return la celda correspondiente.
     */
    public Celda getCelda(int fila, int columna) {
        return celdas[fila][columna];
    }

    /**
     * Verifica si una posición está dentro de los límites del tablero.
     *
     * @param fila la fila a verificar.
     * @param col  la columna a verificar.
     * @return true si la posición es válida, false en caso contrario.
     */
    public boolean cumpleLimites(int fila, int col) {
        return fila >= 0 && fila < 11 && col >= 0 && col < 11;
    }

    /**
     * Intenta colocar un barco en el tablero en la posición dada.
     *
     * @param barco      el barco a colocar.
     * @param filaInicio la fila inicial.
     * @param colInicio  la columna inicial.
     * @return true si se colocó correctamente.
     * @throws ExcepcionPosicionInvalida si el barco se sale del tablero.
     * @throws ExcepcionCeldaOcupada     si alguna celda ya tiene un barco.
     */
    public boolean colocarBarco(Barco barco, int filaInicio, int colInicio)
            throws ExcepcionPosicionInvalida, ExcepcionCeldaOcupada {
        int tamaño = barco.getTamaño();
        boolean horizontal = barco.esHorizontal();

        // Validar espacio disponible
        for (int i = 0; i < tamaño; i++) {
            int fila = horizontal ? filaInicio : filaInicio + i;
            int col = horizontal ? colInicio + i : colInicio;

            if (!cumpleLimites(fila, col)) {
                throw new ExcepcionPosicionInvalida("El barco se sale del tablero en (" + fila + ", " + col + ")");
            }
            if (celdas[fila][col].tieneBarco()) {
                throw new ExcepcionCeldaOcupada("Ya hay un barco en la celda (" + fila + ", " + col + ")");
            }
        }

        // Colocar barco
        for (int i = 0; i < tamaño; i++) {
            int fila = horizontal ? filaInicio : filaInicio + i;
            int col = horizontal ? colInicio + i : colInicio;
            celdas[fila][col].colocarBarco(barco);
        }

        return true;
    }

    /**
     * Realiza un disparo en una celda específica.
     *
     * @param fila    la fila donde se dispara.
     * @param columna la columna donde se dispara.
     * @return una cadena indicando el resultado del disparo: "agua", "tocado", "hundido" o "invalido".
     */
    public String disparar(int fila, int columna) {
        if (!cumpleLimites(fila, columna)) return "invalido";
        return celdas[fila][columna].recibirDisparo();
    }

    /**
     * Remueve un barco del tablero.
     *
     * @param barco el barco a remover.
     */
    public void removerBarco(Barco barco) {
        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                if (celdas[fila][col].getBarco() == barco) {
                    celdas[fila][col].removerBarco();
                }
            }
        }
    }
}