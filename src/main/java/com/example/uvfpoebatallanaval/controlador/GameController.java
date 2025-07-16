package com.example.uvfpoebatallanaval.controlador;

import com.example.uvfpoebatallanaval.modelo.Barco;
import com.example.uvfpoebatallanaval.modelo.Tablero;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class GameController {
    private Tablero tableroJugador;
    private Tablero tableroMaquina;

    @FXML private GridPane tableroPosicion;
    @FXML private GridPane tableroPrincipal;
    @FXML private AnchorPane contenedorBarcos;

    public void initialize() {
        tableroJugador = new Tablero();
        tableroMaquina = new Tablero();

        crearTablero(tableroPosicion, tableroJugador, false);
        crearTablero(tableroPrincipal, tableroMaquina, true);
        inicializarBarcos();
    }

    private void inicializarBarcos() {
        // 1 portaaviones
        Barco portaaviones = new Barco("portaaviones", true);
        agregarAlContenedor(portaaviones, 50, 50);

        // 2 submarinos
        agregarAlContenedor(new Barco("submarino", false), 150, 340);
        agregarAlContenedor(new Barco("submarino", true), 50, 340);

        // 3 destructores
        agregarAlContenedor(new Barco("destructor", true), 50, 190);
        agregarAlContenedor(new Barco("destructor", false), 200, 190);
        agregarAlContenedor(new Barco("destructor", true), 50, 260);

        // 4 fragatas
        agregarAlContenedor(new Barco("fragata", false), 50, 120);
        agregarAlContenedor(new Barco("fragata", true), 110, 120);
        agregarAlContenedor(new Barco("fragata", true), 170, 120);
        agregarAlContenedor(new Barco("fragata", false), 230, 120);
    }

    private void agregarAlContenedor(Barco barco, double x, double y) {
        for (Shape parte : barco.crearFormas(x, y)) {
            contenedorBarcos.getChildren().add(parte);
        }
    }

        private void crearTablero(GridPane tablero, Tablero modelo, boolean esPrincipal) {
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

                int f = fila, c = col;

                // Si es el tablero principal (de la máquina), se habilita el disparo
                if (esPrincipal) {
                    celda.setOnMouseClicked(e -> {
                        String resultado = modelo.disparar(f, c);

                        switch (resultado) {
                            case "agua" -> celda.setFill(Color.LIGHTBLUE);
                            case "tocado" -> celda.setFill(Color.ORANGERED);
                            case "hundido" -> celda.setFill(Color.DARKRED);
                            case "invalido" -> System.out.println("Disparo inválido");
                        }
                    });
                }
                tablero.add(celda, col + 1, fila + 1);
            }
        }
    }
}