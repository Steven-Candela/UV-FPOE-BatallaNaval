package com.example.uvfpoebatallanaval.modelo;

import com.example.uvfpoebatallanaval.controlador.GameController;

/**
 * Interfaz EstrategiaTurno que define el contrato para implementar distintas estrategias
 * de ejecución de turnos en el juego de Batalla Naval.
 *
 * @author Nicolle Paz
 */
public interface EstrategiaTurno {
    /**
     * Ejecuta el turno utilizando una estrategia específica.
     *
     * @param controlador Referencia al controlador del juego que maneja la lógica principal.
     */
    void ejecutarTurno(GameController controlador);
}