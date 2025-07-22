package com.example.uvfpoebatallanaval.modelo;

import com.example.uvfpoebatallanaval.excepciones.ExcepcionCeldaOcupada;
import com.example.uvfpoebatallanaval.excepciones.ExcepcionPosicionInvalida;

import java.util.Random;

/**
 * Representa a la máquina (IA) en el juego Batalla Naval.
 */
public class Maquina extends Jugador {

    private Random random;

    public Maquina() throws ExcepcionCeldaOcupada, ExcepcionPosicionInvalida {
        super("Maquina"); // Llama al constructor de Jugador con nombre "Maquina"
        random = new Random();
        colocarFlotaAleatoriamente();
    }

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
