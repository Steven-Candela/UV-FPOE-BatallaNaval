package com.example.uvfpoebatallanaval.controlador;

import com.example.uvfpoebatallanaval.modelo.Arrastrable;
import com.example.uvfpoebatallanaval.modelo.Barco;
import com.example.uvfpoebatallanaval.modelo.Tablero;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class GameController {
    private Tablero tableroJugador;
    private Tablero tableroMaquina;

    @FXML private GridPane tableroPosicion;
    @FXML private GridPane tableroPrincipal;
    @FXML private AnchorPane zonaTableroPosicion;
    @FXML private AnchorPane contenedorBarcos;

    public void initialize() {
        tableroJugador = new Tablero();
        tableroMaquina = new Tablero();

        crearTablero(tableroPosicion, tableroJugador, false);
        crearTablero(tableroPrincipal, tableroMaquina, true);
        inicializarBarcos();
    }


    @FXML
    private void onActionVolverMenu(ActionEvent event) throws IOException {
        System.out.println("El juego inicia");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/uvfpoebatallanaval/menu-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Juego");
        stage.setScene(scene);
        stage.show();
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
        List<Shape> formas = barco.crearFormas(x, y);
        contenedorBarcos.getChildren().addAll(formas);
        new BarcoArrastrable(barco, formas);
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

    private class BarcoArrastrable implements Arrastrable {
        private Barco barco;
        private List<Shape> formas;
        private double offsetX, offsetY;

        public BarcoArrastrable(Barco barco, List<Shape> formas) {
            this.barco = barco;
            this.formas = formas;

            for (Shape forma : formas) {
                forma.setOnMousePressed(e -> {
                    offsetX = e.getSceneX() - forma.localToScene(0, 0).getX();
                    offsetY = e.getSceneY() - forma.localToScene(0, 0).getY();
                    iniciarArrastre(offsetX, offsetY);
                });

                forma.setOnMouseDragged(e -> {
                    double nuevaX = e.getSceneX() - offsetX;
                    double nuevaY = e.getSceneY() - offsetY;
                    mover(nuevaX, nuevaY);
                });

                forma.setOnMouseReleased(e -> {
                    double x = e.getSceneX() - offsetX;
                    double y = e.getSceneY() - offsetY;
                    soltar(x, y);
                });
            }
        }

        @Override
        public void iniciarArrastre(double offsetX, double offsetY) {}

        @Override
        public void mover(double x, double y) {
            for (Shape forma : formas) {
                forma.setLayoutX(x);
                forma.setLayoutY(y);
            }
        }

        @Override
        public void soltar(double x, double y) {}
    }
}