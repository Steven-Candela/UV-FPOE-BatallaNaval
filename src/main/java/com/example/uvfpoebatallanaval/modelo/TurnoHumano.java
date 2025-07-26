package com.example.uvfpoebatallanaval.modelo;

import com.example.uvfpoebatallanaval.controlador.GameController;

/**
 * Clase TurnoHumano, que contiene la estrategia de turno correspondiente al jugador humano.
 * Implementa la interfaz {@link EstrategiaTurno} para definir el comportamiento
 * que se ejecuta cuando es el turno del jugador humano.
 *
 * @author Nicolle Paz
 */
public class TurnoHumano implements EstrategiaTurno {
    private GameController controlador;

    /**
     * Crea una nueva instancia de TurnoHumano con el controlador del juego.
     *
     * @param controlador el controlador del juego que gestiona la lógica principal
     */

    public TurnoHumano(GameController controlador) {
        this.controlador = controlador;
    }

    /**
     * Ejecuta las acciones correspondientes al turno del jugador humano.
     * Actualmente solo imprime un mensaje en consola, pero aquí podría
     * habilitarse la interacción con el tablero del enemigo, por ejemplo.
     *
     * @param controlador el controlador del juego que gestiona la lógica principal
     */
    @Override
    public void ejecutarTurno(GameController controlador) {
        System.out.println("Turno del jugador humano.");
    }
}