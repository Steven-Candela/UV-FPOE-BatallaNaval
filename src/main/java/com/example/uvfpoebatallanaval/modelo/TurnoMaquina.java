package com.example.uvfpoebatallanaval.modelo;

import com.example.uvfpoebatallanaval.controlador.GameController;
import com.example.uvfpoebatallanaval.vista.ElementosDisparo;
import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.util.List;

public class TurnoMaquina implements EstrategiaTurno {
    @Override
    public void ejecutarTurno(GameController controlador) {
        System.out.println("Turno de la máquina");

        PauseTransition pausa = new PauseTransition(Duration.seconds(2));
        pausa.setOnFinished(event -> {
            Tablero tableroJugador = controlador.getTableroJugador();
            GridPane gridJugador = controlador.getTableroPosicion();

            int fila, col;
            String resultado;

            do {
                fila = (int) (Math.random() * 10);
                col = (int) (Math.random() * 10);
            } while (tableroJugador.getCelda(fila, col).fueAtacada());

            resultado = tableroJugador.disparar(fila, col);

            // Para mostrar el disparo en el tablero visual del jugador
            double x = col * 45;
            double y = fila * 45;
            List<Shape> formas;

            switch (resultado) {
                case "agua":
                    formas = ElementosDisparo.agua(x, y);
                    break;
                case "tocado":
                    formas = ElementosDisparo.tocado(x, y);
                    break;
                case "hundido":
                    formas = ElementosDisparo.hundido(x, y);
                    break;
                default:
                    formas = List.of();
            }

            if (resultado.equals("hundido")) {
                Barco barco = tableroJugador.getCelda(fila, col).getBarco();
                System.out.println("Barco al hundirse: " + barco);

                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        Celda celdaModelo = tableroJugador.getCelda(i, j);
                        if (celdaModelo.getBarco() == barco) {
                            List<Shape> formasHundido = ElementosDisparo.hundido(0, 0);
                            StackPane celda = new StackPane();
                            celda.setPrefSize(45, 45);
                            celda.getChildren().addAll(formasHundido);
                            gridJugador.add(celda, j + 1, i + 1);
                        }
                    }
                }
            } else {
                StackPane celda = new StackPane();
                celda.setPrefSize(45, 45);
                celda.getChildren().addAll(formas);
                gridJugador.add(celda, col + 1, fila + 1);
            }

            // Cambiar turno dependiendo del disparo
            if (resultado.equals("agua")) {
                controlador.setEstrategiaTurno(new TurnoHumano(controlador));
            } else {
                controlador.setEstrategiaTurno(this);
                controlador.ejecutarTurnoActual();
            }
        });

        pausa.play();
    }
}