package com.example.uvfpoebatallanaval.modelo;

import com.example.uvfpoebatallanaval.controlador.GameController;

public class TurnoHumano implements EstrategiaTurno {
    private GameController controlador;

    public TurnoHumano(GameController controlador) {
        this.controlador = controlador;
    }

    @Override
    public void ejecutarTurno(GameController controlador) {
        System.out.println("Turno del jugador humano.");
    }
}