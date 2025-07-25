package com.example.uvfpoebatallanaval.modelo;

import com.example.uvfpoebatallanaval.controlador.GameController;
import com.example.uvfpoebatallanaval.vista.ElementosDisparo;
import javafx.animation.PauseTransition;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.util.List;

/**
 * Clase TurnoMaquina, que contiene la estrategia de turno correspondiente a la máquina (IA).
 * Implementa la interfaz {@link EstrategiaTurno} y define el comportamiento
 * automático de disparo contra el tablero del jugador.
 *
 * @author Nicolle Paz, Camilo Portilla y Steven Candela
 */
public class TurnoMaquina implements EstrategiaTurno {
    /**
     * Ejecuta el turno de la máquina con una pequeña pausa para simular tiempo de espera.
     * La máquina selecciona aleatoriamente una celda no atacada del tablero del jugador,
     * realiza el disparo y actualiza la interfaz gráfica en función del resultado.
     *
     * @param controlador El controlador principal del juego, utilizado para acceder al tablero,
     *                    actualizar la interfaz gráfica, verificar condiciones de fin de juego
     *                    y gestionar los turnos.
     */
    @Override
    public void ejecutarTurno(GameController controlador) {
        if (controlador.isJuegoTerminado()) return;

        System.out.println("Turno de la máquina");

        PauseTransition pausa = new PauseTransition(Duration.seconds(2));
        pausa.setOnFinished(event -> {
            if (controlador.isJuegoTerminado()) return;

            Tablero tableroJugador = controlador.getTableroJugador();
            GridPane gridJugador = controlador.getTableroPosicion();

            int fila, col;
            String resultado;

            do {
                fila = (int) (Math.random() * 10);
                col = (int) (Math.random() * 10);
            } while (tableroJugador.getCelda(fila, col).fueAtacada());

            resultado = tableroJugador.disparar(fila, col);

            switch (resultado) {
                case "agua" -> {
                    controlador.getDisparoLabel().setText("Disparo de la máquina: AGUA");
                    controlador.getDisparoLabel().setStyle("-fx-text-fill: white;");
                }
                case "tocado" -> {
                    controlador.getDisparoLabel().setText("Disparo de la máquina: ¡TOCADO!");
                    controlador.getDisparoLabel().setStyle("-fx-text-fill: white;");
                }
                case "hundido" -> {
                    controlador.getDisparoLabel().setText("Disparo de la máquina: ¡HUNDIDO!");
                    controlador.getDisparoLabel().setStyle("-fx-text-fill: white;");
                }
            }

            double x = col * 45;
            double y = fila * 45;
            List<Shape> formas;

            switch (resultado) {
                case "agua" -> formas = ElementosDisparo.agua(x, y);
                case "tocado" -> formas = ElementosDisparo.tocado(x, y);
                case "hundido" -> formas = ElementosDisparo.hundido(x, y);
                default -> formas = List.of();
            }

            if (resultado.equals("hundido")) {
                Barco barco = tableroJugador.getCelda(fila, col).getBarco();
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        if (tableroJugador.getCelda(i, j).getBarco() == barco) {
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

            if (controlador.barcosHundidos(tableroJugador)) {
                controlador.terminarJuego();
                javafx.application.Platform.runLater(() -> {
                    javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                    alerta.setTitle("Fin del juego");
                    alerta.setHeaderText("¡Has perdido!");
                    alerta.setContentText("La máquina ha hundido todos tus barcos.");
                    alerta.showAndWait();
                });
                controlador.habilitarTableroEnemigo(false);
                return;
            }

            if (resultado.equals("agua")) {
                controlador.setEstrategiaTurno(new TurnoHumano(controlador));
            } else {
                controlador.setEstrategiaTurno(this);
                controlador.ejecutarTurnoActual();
            }

            controlador.guardarEstado();
        });
        pausa.play();
    }
}