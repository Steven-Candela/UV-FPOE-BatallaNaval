package com.example.uvfpoebatallanaval.controlador;

import com.example.uvfpoebatallanaval.modelo.Arrastrable;
import com.example.uvfpoebatallanaval.modelo.Barco;
import com.example.uvfpoebatallanaval.modelo.Tablero;
import com.example.uvfpoebatallanaval.vista.ElementosDisparo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
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
        List<List<Shape>> formasPorCelda = barco.crearFormas(x, y);
        for (List<Shape> grupo : formasPorCelda) {
            contenedorBarcos.getChildren().addAll(grupo);
        }
        new BarcoArrastrable(barco, formasPorCelda);
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
                StackPane celda = new StackPane();
                celda.setPrefSize(45, 45);

                Rectangle fondo = new Rectangle(45, 45);
                fondo.setFill(Color.WHITE);
                fondo.setStroke(Color.BLACK);

                celda.getChildren().add(fondo);

                int f = fila, c = col;

                // Si es el tablero principal (de la máquina), se habilita el disparo
                if (esPrincipal) {
                    celda.setOnMouseClicked(e -> {
                        String resultado = modelo.disparar(f, c);

                        List<Shape> formas = switch (resultado) {
                            case "agua" -> ElementosDisparo.agua(0, 0);
                            case "tocado" -> ElementosDisparo.tocado(0, 0);
                            case "hundido" -> ElementosDisparo.hundido(0, 0);
                            default -> null;
                        };

                        if (formas != null) {
                            celda.getChildren().addAll(formas);
                        }
                    });
                }
                tablero.add(celda, col + 1, fila + 1);
            }
        }
    }

    private class BarcoArrastrable implements Arrastrable {
        private final Barco barco;
        private final List<List<Shape>> formasPorCelda;
        private double offsetX, offsetY;

        public BarcoArrastrable(Barco barco, List<List<Shape>> formasPorCelda) {
            this.barco = barco;
            this.formasPorCelda = formasPorCelda;

            for (List<Shape> grupo : formasPorCelda) {
                for (Shape forma : grupo) {
                    forma.setOnMousePressed(e -> {
                        offsetX = e.getX();
                        offsetY = e.getY();
                    });

                    forma.setOnMouseDragged(e -> {
                        Point2D localPoint = contenedorBarcos.sceneToLocal(e.getSceneX(), e.getSceneY());
                        double baseX = localPoint.getX() - offsetX;
                        double baseY = localPoint.getY() - offsetY;

                        for (List<Shape> subGrupo : formasPorCelda) {
                            for (Shape f : subGrupo) {
                                f.setLayoutX(baseX);
                                f.setLayoutY(baseY);
                            }
                        }
                    });

                    forma.setOnMouseReleased(e -> {
                        Point2D local = tableroPosicion.sceneToLocal(e.getSceneX(), e.getSceneY());
                        int col = (int)(local.getX() / 45);
                        int fila = (int)(local.getY() / 45);

                        int tamaño = barco.getTamaño();
                        boolean horizontal = barco.esHorizontal();

                        System.out.println("Fila: " + fila + " - Columna: " + col);
                        System.out.println("Horizontal: " + horizontal + " - Tamaño: " + tamaño);

                        if (fila >= 0 && fila <= 10 && col >= 0 && col <= 10 &&
                                ((horizontal && col + tamaño <= 11) || (!horizontal && fila + tamaño <= 11))) {

                            for (int i = 0; i < formasPorCelda.size(); i++) {
                                List<Shape> grupoFormas = formasPorCelda.get(i);
                                StackPane celda = new StackPane();
                                celda.setPrefSize(45, 45);

                                for (Shape f : grupoFormas) {
                                    f.setLayoutX(0);
                                    f.setLayoutY(0);
                                    celda.getChildren().add(f);
                                }

                                int celdaCol = col + (horizontal ? i : 0);
                                int celdaFila = fila + (horizontal ? 0 : i);

                                tableroPosicion.add(celda, celdaCol, celdaFila);
                            }

                            for (List<Shape> grupoBarco : formasPorCelda) {
                                contenedorBarcos.getChildren().removeAll(grupoBarco);
                            }

                            System.out.println("Barco colocado desde (" + fila + "," + col + ") tamaño " + tamaño);
                        } else {
                            System.out.println("¡Posición inválida o fuera del tablero!");
                        }
                    });
                }
            }
        }

        @Override
        public void iniciarArrastre(double offsetX, double offsetY) {}

        @Override
        public void mover(double x, double y) {
            for (List<Shape> grupo : formasPorCelda) {
                for (Shape forma : grupo) {
                    forma.setLayoutX(x);
                    forma.setLayoutY(y);
                }
            }
        }

        @Override
        public void soltar(double x, double y) {}
    }

}