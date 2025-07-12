package com.example.uvfpoebatallanaval.controlador;

import com.example.uvfpoebatallanaval.modelo.Barco;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class GameController {
    @FXML
    private GridPane tableroPosicion, tableroPrincipal;
    @FXML
    private AnchorPane contenedorBarcos;

    public void initialize() {
        crearTablero(tableroPosicion, false);
        crearTablero(tableroPrincipal, true);
        inicializarBarcos();
    }

    private void inicializarBarcos() {
        // 1 portaaviones
        Barco portaaviones = new Barco("portaaviones", true);
        agregarAlContenedor(portaaviones, 50, 50);

        // 2 submarinos
        agregarAlContenedor(new Barco("submarino", false), 280, 50);
        agregarAlContenedor(new Barco("submarino", true), 260, 200);

        // 3 destructores
        agregarAlContenedor(new Barco("destructor", true), 50, 200);
        agregarAlContenedor(new Barco("destructor", false), 200, 200);
        agregarAlContenedor(new Barco("destructor", true), 50, 300);

        // 4 fragatas
        agregarAlContenedor(new Barco("fragata", false), 300, 300);
        agregarAlContenedor(new Barco("fragata", true), 150, 130);
        agregarAlContenedor(new Barco("fragata", true), 200, 350);
        agregarAlContenedor(new Barco("fragata", false), 300, 420);
    }

    private void agregarAlContenedor(Barco barco, double x, double y) {
        for (Shape parte : barco.crearFormas(x, y)) {
            contenedorBarcos.getChildren().add(parte);
        }
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
