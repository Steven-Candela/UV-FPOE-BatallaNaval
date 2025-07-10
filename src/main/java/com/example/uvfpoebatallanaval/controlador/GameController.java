package com.example.uvfpoebatallanaval.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameController {
    @FXML
    private GridPane tableroPosicion, tableroPrincipal;

    public void initialize() {
        crearTablero(tableroPosicion, false);
        crearTablero(tableroPrincipal, true);
    }

    private void crearTablero(GridPane tablero, boolean esInteractivo) {
        tablero.getChildren().clear();

        // Etiquetas de las columnas (1-10)
        for (int col = 0; col < 10; col++) {
            Label numero = new Label(String.valueOf(col + 1));
            numero.setMinSize(45, 45);
            numero.setStyle("-fx-alignment: center; -fx-font-weight: bold;");
            tablero.add(numero, col + 1, 0);
        }

        // Etiquetas de las filas (A-J)
        for (int fila = 0; fila < 10; fila++) {
            Label letra = new Label(String.valueOf((char) ('A' + fila)));
            letra.setMinSize(45, 45);
            letra.setStyle("-fx-alignment: center; -fx-font-weight: bold;");
            tablero.add(letra, 0, fila + 1);
        }

        // Creación de las celdas
        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                Rectangle celda = new Rectangle(45, 45);
                celda.setFill(Color.WHITE);
                celda.setStroke(Color.BLACK);

                tablero.add(celda, col + 1, fila + 1);
            }
        }
    }
}
