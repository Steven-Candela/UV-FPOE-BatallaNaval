package com.example.uvfpoebatallanaval.modelo;

public class Tablero {
    private Celda[][] celdas = new Celda[10][10];

    public Tablero() {
        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                celdas[fila][col] = new Celda();
            }
        }
    }

    public Celda getCelda(int fila, int columna) {
        return celdas[fila][columna];
    }

    public boolean todosLosBarcosHundidos() {
        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                Celda celda = celdas[fila][col];
                if (celda.tieneBarco() && !celda.isDisparado()) {
                    return false;
                }
            }
        }
        return true;
    }
    public boolean colocarBarco(Barco barco, int fila, int columna) {
        // Aquí debes agregar tu lógica para verificar si se puede colocar,
        // por ahora asumamos que sí y lo asignamos a las celdas
        for (int i = 0; i < barco.getTamaño(); i++) {
            int f = fila;
            int c = columna;

            if (barco.esHorizontal()) {
                c += i;
            } else {
                f += i;
            }

            celdas[f][c].setBarco(barco);
        }

        return true;
    }

    public void removerBarco(Barco barco) {
        for (int fila = 0; fila < celdas.length; fila++) {
            for (int col = 0; col < celdas[0].length; col++) {
                if (celdas[fila][col].getBarco() == barco) {
                    celdas[fila][col].setBarco(null);
                }
            }
        }
    }
    public String disparar(int fila, int columna) {
        Celda celda = celdas[fila][columna];
        celda.setDisparado(true);

        if (celda.tieneBarco()) {
            return "acierto";
        } else {
            return "fallo";
        }
    }

}
