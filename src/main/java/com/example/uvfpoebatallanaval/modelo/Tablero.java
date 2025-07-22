package com.example.uvfpoebatallanaval.modelo;

import com.example.uvfpoebatallanaval.excepciones.ExcepcionCeldaOcupada;
import com.example.uvfpoebatallanaval.excepciones.ExcepcionPosicionInvalida;

import java.io.Serializable;

public class Tablero implements Serializable {
    private Celda[][] celdas;

    public Tablero() {
        celdas = new Celda[11][11];
        for (int fila = 0; fila < 11; fila++) {
            for (int col = 0; col < 11; col++) {
                celdas[fila][col] = new Celda();
            }
        }
    }

    public Celda getCelda(int fila, int columna) {
        return celdas[fila][columna];
    }

    public boolean cumpleLimites(int fila, int col) {
        return fila > 0 && fila <= 10 && col > 0 && col <= 10;
    }

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

    public String disparar(int fila, int columna) {
        if (!cumpleLimites(fila, columna)) return "invalido";
        return celdas[fila][columna].recibirDisparo();
    }

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