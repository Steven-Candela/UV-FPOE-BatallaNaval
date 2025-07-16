package com.example.uvfpoebatallanaval.modelo;

import java.io.Serializable;

public class Tablero implements Serializable {
    private Celda[][] celdas;

    public Tablero() {
        celdas = new Celda[10][10];
        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                celdas[fila][col] = new Celda();
            }
        }
    }

    public Celda getCelda(int fila, int columna) {
        return celdas[fila][columna];
    }

    public boolean cumpleLimites(int fila, int col) {
        return fila >= 0 && fila < 10 && col >= 0 && col < 10;
    }

    public boolean colocarBarco(Barco barco, int filaInicio, int colInicio) {
        int tamaño = barco.getTamaño();
        boolean horizontal = barco.esHorizontal();

        // Validar espacio disponible
        for (int i = 0; i < tamaño; i++) {
            int fila = horizontal ? filaInicio : filaInicio + i;
            int col = horizontal ? colInicio + i : colInicio;

            if (!cumpleLimites(fila, col) || celdas[fila][col].tieneBarco()) {
                return false; // la celda está ocupada o fuera de límites
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
}