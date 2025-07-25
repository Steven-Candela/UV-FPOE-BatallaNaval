package com.example.uvfpoebatallanaval.controlador;


import com.example.uvfpoebatallanaval.excepciones.ExcepcionCeldaOcupada;
import com.example.uvfpoebatallanaval.excepciones.ExcepcionPosicionInvalida;
import com.example.uvfpoebatallanaval.modelo.*;
import com.example.uvfpoebatallanaval.vista.ElementosDisparo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import com.example.uvfpoebatallanaval.excepciones.ExepcionCeldaDisparada;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GameController {
    private Tablero tableroJugador;
    private Tablero tableroMaquina;
    private boolean juegoIniciado = false;
    private EstrategiaTurno estrategiaTurno;
    private boolean juegoTerminado = false;
    private Jugador jugador;

    @FXML private GridPane tableroPosicion;
    @FXML private GridPane tableroPrincipal;
    @FXML private AnchorPane contenedorBarcos;
    @FXML private Label turnoLabel;

    @FXML private Label disparoLabel;
    public Label getDisparoLabel() {
        return disparoLabel;
    }

    public static GestorPartida.EstadoJuego estadoGuardado = null;

    @FXML
    public void initialize() {
        if (estadoGuardado != null) {
            cargarPartidaDesdeArchivo(estadoGuardado);
            estadoGuardado = null; // Limpiamos para evitar problemas en el futuro
        } else {
            iniciarNuevaPartida();
        }
    }

    private void iniciarNuevaPartida() {
        solicitarNombreJugador();


        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alertaInicio = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alertaInicio.setTitle("Instrucciones iniciales");
            alertaInicio.setHeaderText("¡Bienvenido a Batalla Naval!");
            alertaInicio.setContentText("Arrastra tus barcos al tablero de posición para iniciar el juego :)");
            alertaInicio.showAndWait();
        });

        tableroJugador = new Tablero();
        tableroMaquina = new Tablero();

        crearTablero(tableroPosicion, tableroJugador, false);
        crearTablero(tableroPrincipal, tableroMaquina, true);
        inicializarBarcos();
        colocarBarcosMaquina();
        habilitarTableroEnemigo(false);
    }


    private void solicitarNombreJugador() {
        javafx.scene.control.TextInputDialog dialogo = new javafx.scene.control.TextInputDialog();
        dialogo.setTitle("Nombre del Jugador");
        dialogo.setHeaderText("Bienvenido a Batalla Naval");
        dialogo.setContentText("Por favor, ingresa tu nombre:");

        Optional<String> resultado = dialogo.showAndWait();

        resultado.ifPresentOrElse(nombre -> {
            jugador = new Jugador(nombre);
            System.out.println("Jugador: " + nombre);
        }, () -> {
            // Si cancela, cerramos el juego
            javafx.application.Platform.exit();
        });
    }

    private void cargarPartidaDesdeArchivo(GestorPartida.EstadoJuego estado) {
        this.tableroJugador = estado.tableroJugador;
        this.tableroMaquina = estado.tableroMaquina;
        this.jugador = new Jugador(estado.nickname);
        this.jugador.setBarcosHundidos(estado.barcosHundidos);
        this.juegoIniciado = true;

        crearTablero(tableroPosicion, tableroJugador, false);
        crearTablero(tableroPrincipal, tableroMaquina, true);
        habilitarTableroEnemigo(true);

        turnoLabel.setText(estado.textoTurno);
        turnoLabel.setStyle(estado.colorTurno);
        disparoLabel.setText(estado.textoDisparo);
        disparoLabel.setStyle(estado.colorDisparo);


        setEstrategiaTurno(new TurnoHumano(this));
        ejecutarTurnoActual();

        // 👇 Añadir esto: volver a dibujar los barcos del jugador
        redibujarBarcosJugador();
        redibujarDisparos();
    }

    private void redibujarBarcosJugador() {
        List<Barco> barcosDibujados = new ArrayList<>();

        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                Celda celda = tableroJugador.getCelda(fila, col);
                if (celda.tieneBarco()) {
                    Barco barco = celda.getBarco();

                    if (barcosDibujados.contains(barco)) continue; // evitar duplicados
                    barcosDibujados.add(barco);

                    // Buscar coordenadas base del barco
                    int baseFila = -1, baseCol = -1;
                    for (int i = 0; i < 10 && baseFila == -1; i++) {
                        for (int j = 0; j < 10; j++) {
                            if (tableroJugador.getCelda(i, j).getBarco() == barco) {
                                baseFila = i;
                                baseCol = j;
                                break;
                            }
                        }
                    }

                    // Coordenadas visuales base
                    double baseX = baseCol * 45;
                    double baseY = baseFila * 45;

                    List<List<Shape>> formas = barco.crearFormas(baseX, baseY);

                    for (int i = 0; i < formas.size(); i++) {
                        int f = baseFila + (barco.esHorizontal() ? 0 : i);
                        int c = baseCol + (barco.esHorizontal() ? i : 0);

                        Node celdaNode = obtenerCelda(tableroPosicion, f + 1, c + 1);
                        if (celdaNode instanceof StackPane celdaPane) {
                            for (Shape shape : formas.get(i)) {
                                shape.setLayoutX(0);
                                shape.setLayoutY(0);
                                celdaPane.getChildren().add(shape);
                            }
                        }
                    }
                }
            }
        }
    }

    private void redibujarDisparos() {
        // 🔵 Redibujar disparos en el tablero de la máquina (enemigo)
        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                Celda celda = tableroMaquina.getCelda(fila, col);
                if (celda.fueAtacada()) {
                    Node celdaNode = obtenerCelda(tableroPrincipal, fila + 1, col + 1);
                    if (celdaNode instanceof StackPane celdaPane) {
                        Barco barco = celda.getBarco();

                        if (barco != null) {
                            if (barco.estaHundido()) {
                                celdaPane.getChildren().removeIf(n -> n instanceof Shape && !"fondoCelda".equals(n.getId()));
                                celdaPane.getChildren().addAll(ElementosDisparo.hundido(0, 0));
                            } else {
                                celdaPane.getChildren().addAll(ElementosDisparo.tocado(0, 0));
                            }
                        } else {
                            celdaPane.getChildren().addAll(ElementosDisparo.agua(0, 0));
                        }
                    }
                }
            }
        }

        // 🟡 Redibujar disparos en el tablero del jugador (por si se quiere mostrar)
        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                Celda celda = tableroJugador.getCelda(fila, col);
                if (celda.fueAtacada()) {
                    Node celdaNode = obtenerCelda(tableroPosicion, fila + 1, col + 1);
                    if (celdaNode instanceof StackPane celdaPane) {
                        Barco barco = celda.getBarco();

                        if (barco != null) {
                            if (barco.estaHundido()) {
                                celdaPane.getChildren().removeIf(n -> n instanceof Shape && !"fondoCelda".equals(n.getId()));
                                celdaPane.getChildren().addAll(ElementosDisparo.hundido(0, 0));
                            } else {
                                celdaPane.getChildren().addAll(ElementosDisparo.tocado(0, 0));
                            }
                        } else {
                            celdaPane.getChildren().addAll(ElementosDisparo.agua(0, 0));
                        }
                    }
                }
            }
        }
    }


    public void guardarEstado() {
        System.out.println("Guardado desde el turno de la máquina. Barcos hundidos: " + jugador.getBarcosHundidos());

        String textoTurno = turnoLabel.getText();
        String colorTurno = turnoLabel.getStyle(); // Ej: "-fx-text-fill: red;"
        String textoDisparo = disparoLabel.getText();
        String colorDisparo = disparoLabel.getStyle();

        GestorPartida.guardarPartida(tableroJugador, tableroMaquina, jugador.getNombre(), jugador.getBarcosHundidos(),
                textoTurno, colorTurno, textoDisparo, colorDisparo);
    }


    @FXML
    private void onActionVolverMenu(ActionEvent event) throws IOException {
        System.out.println("El juego inicia");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/uvfpoebatallanaval/menu-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Menú del juego");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void onActionReiniciar(ActionEvent event) {
        javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar reinicio");
        alerta.setHeaderText("¿Estás seguro de que deseas reiniciar?");
        alerta.setContentText("Perderás el progreso actual.");

        Optional<ButtonType> resultado = alerta.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            reiniciarJuego();
        }
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
                fondo.setId("fondoCelda");

                celda.getChildren().add(fondo);

                int f = fila, c = col;

                // Si es el tablero principal (de la máquina), se habilita el disparo
                if (esPrincipal) {
                    celda.setOnMouseClicked(e -> {
                        // Para evitar que el humano dispare cuando no es su turno
                        if (!(estrategiaTurno instanceof TurnoHumano)) return;
                        try {
                            Celda celdaModelo = modelo.getCelda(f, c);
                            if (celdaModelo.fueAtacada()) {
                                throw new ExepcionCeldaDisparada("Ya disparaste en esta celda.");
                            }
                            String resultado = celdaModelo.recibirDisparo();

                            if (disparoLabel != null) {
                                switch (resultado) {
                                    case "agua" -> {
                                        disparoLabel.setText("Disparo del jugador: AGUA");
                                        disparoLabel.setStyle("-fx-text-fill: blue;");
                                    }
                                    case "tocado" -> {
                                        disparoLabel.setText("Disparo del jugador: ¡TOCADO!");
                                        disparoLabel.setStyle("-fx-text-fill: red;");
                                    }
                                    case "hundido" -> {
                                        disparoLabel.setText("Disparo del jugador: ¡HUNDIDO!");
                                        disparoLabel.setStyle("-fx-text-fill: black;");
                                    }
                                    default -> disparoLabel.setText("Disparo desconocido");
                                }
                            }

                            if (resultado.equals("hundido")) {
                                resultadoLabelsetText("Resultado del disparo: El humano hundió un barco de la máquina.");
                                Barco barco = celdaModelo.getBarco();
                                jugador.incrementarBarcosHundidos();
                                disparoLabel.setText("Disparo del jugador: ¡HUNDIDO!");
                                for (int i = 0; i < 10; i++) {
                                    for (int j = 0; j < 10; j++) {
                                        Celda otra = modelo.getCelda(i, j);
                                        if (otra.getBarco() == barco) {
                                            Node nodo = obtenerCelda(tableroPrincipal, i + 1, j + 1);
                                            if (nodo instanceof StackPane pane) {
                                                pane.getChildren().removeIf(n -> n instanceof Shape && !"fondoCelda".equals(n.getId()));
                                                pane.getChildren().addAll(ElementosDisparo.hundido(0, 0));
                                            }
                                        }
                                    }
                                }
                                // Verificar si el humano ganó
                                if (barcosHundidos(modelo)) {
                                    javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                                    alerta.setTitle("Fin del juego");
                                    alerta.setHeaderText("¡Has ganado!");
                                    alerta.setContentText("Has hundido todos los barcos enemigos. Se reiniciará el juego.");
                                    alerta.showAndWait();

                                    habilitarTableroEnemigo(false);
                                    reiniciarJuego();
                                }
                                return;
                            } else {
                                List<Shape> formas = switch (resultado) {
                                    case "agua" -> ElementosDisparo.agua(0, 0);
                                    case "tocado" -> ElementosDisparo.tocado(0, 0);
                                    default -> null;
                                };
                                if (formas != null) {
                                    celda.getChildren().addAll(formas);
                                }
                            }

                            // Cambiar turno (si fue agua)
                            if (resultado.equals("agua")) {
                                resultadoLabelsetText("Resultado del disparo: El humano disparó en el agua.");
                                setEstrategiaTurno(new TurnoMaquina());
                                ejecutarTurnoActual();
                            }

                            System.out.println("Guardado desde el turno del jugador. Barcos hundidos: " + jugador.getBarcosHundidos());
                            guardarEstado();


                        } catch (ExepcionCeldaDisparada ex) {
                            System.out.println("Error: " + ex.getMessage());

                            javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                            alerta.setTitle("Celda disparada");
                            alerta.setHeaderText("No se pudo disparar ahí, ya disparaste en esta celda");
                            alerta.setContentText(ex.getMessage());
                            alerta.showAndWait();
                        }
                    });
                }
                tablero.add(celda, col + 1, fila + 1);
            }
        }
    }

    private void colocarBarcosMaquina() {
        Barco[] barcos = {
                new Barco("portaaviones", true),
                new Barco("submarino", false),
                new Barco("submarino", true),
                new Barco("destructor", true),
                new Barco("destructor", false),
                new Barco("destructor", true),
                new Barco("fragata", false),
                new Barco("fragata", true),
                new Barco("fragata", true),
                new Barco("fragata", false),
        };

        for (Barco barco : barcos) {
            boolean colocado = false;

            while (!colocado) {
                boolean horizontal = Math.random() < 0.5;
                barco.setOrientacion(horizontal);

                int fila = (int) (Math.random() * 10);
                int col = (int) (Math.random() * 10);

                if (horizontal && col + barco.getTamaño() > 10) continue;
                if (!horizontal && fila + barco.getTamaño() > 10) continue;

                try {
                    tableroMaquina.colocarBarco(barco, fila, col);
                    colocado = true;
                } catch (ExcepcionPosicionInvalida | ExcepcionCeldaOcupada e) {}
            }
        }
    }

    public void setEstrategiaTurno(EstrategiaTurno estrategia) {
        this.estrategiaTurno = estrategia;

        if (turnoLabel != null) {
            if (estrategia instanceof TurnoHumano) {
                turnoLabel.setText("Turno del Jugador");
            } else if (estrategia instanceof TurnoMaquina) {
                turnoLabel.setText("Turno de la Máquina");
            }
        }
    }

    public EstrategiaTurno getEstrategiaTurno() {return estrategiaTurno;}

    public void ejecutarTurnoActual() {
        if (juegoTerminado || estrategiaTurno == null) return;
        estrategiaTurno.ejecutarTurno(this);
    }

    public boolean barcosHundidos(Tablero tablero) {
        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                Celda celda = tablero.getCelda(fila, col);
                if (celda.tieneBarco() && !celda.fueAtacada()) {
                    return false;
                }
            }
        }
        return true;
    }

    @FXML
    private void onActionVerTableroEnemigo() {
        if (juegoIniciado) {
            javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alerta.setTitle("Tablero Enemigo");
            alerta.setHeaderText("Ya no puedes ver el tablero enemigo");
            alerta.setContentText("El juego ya ha comenzado.");
            alerta.showAndWait();
            return;
        }

        List<Barco> barcosDibujados = new ArrayList<>();

        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                Celda modeloCelda = tableroMaquina.getCelda(fila, col);

                if (modeloCelda.tieneBarco()) {
                    Barco barco = modeloCelda.getBarco();

                    if (barcosDibujados.contains(barco)) continue;
                    barcosDibujados.add(barco);

                    // Coordenadas iniciales
                    double baseX = 0;
                    double baseY = 0;

                    for (int i = 0; i < 10 && baseX == 0 && baseY == 0; i++) {
                        for (int j = 0; j < 10; j++) {
                            if (tableroMaquina.getCelda(i, j).getBarco() == barco) {
                                baseX = j * 45;
                                baseY = i * 45;
                                break;
                            }
                        }
                    }

                    List<List<Shape>> formas = barco.crearFormas(baseX, baseY);

                    for (int i = 0; i < formas.size(); i++) {
                        int celdaFila = fila + (barco.esHorizontal() ? 0 : i);
                        int celdaCol = col + (barco.esHorizontal() ? i : 0);

                        Node node = obtenerCelda(tableroPrincipal, celdaFila + 1, celdaCol + 1);
                        if (node instanceof StackPane celdaPane) {
                            for (Shape forma : formas.get(i)) {
                                forma.setLayoutX(0);
                                forma.setLayoutY(0);
                                celdaPane.getChildren().add(forma);
                            }
                        }
                    }
                }
            }
        }
    }

    @FXML
    private void onActionSalirButton(ActionEvent event) {
        System.exit(0);
    }

    public Node obtenerCelda(GridPane grid, int fila, int columna) {
        for (Node node : grid.getChildren()) {
            if (GridPane.getRowIndex(node) != null && GridPane.getColumnIndex(node) != null &&
                    GridPane.getRowIndex(node) == fila && GridPane.getColumnIndex(node) == columna) {
                return node;
            }
        }
        return null;
    }

    private boolean verificarBarcosColocados() {
        int totalCeldasOcupadas = 0;
        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                if (tableroJugador.getCelda(fila, col).tieneBarco()) {
                    totalCeldasOcupadas++;
                }
            }
        }
        System.out.println("Celdas ocupadas: " + totalCeldasOcupadas);
        return totalCeldasOcupadas == 21;
    }

    public void habilitarTableroEnemigo(boolean habilitar) {
        for (Node node : tableroPrincipal.getChildren()) {
            Integer fila = GridPane.getRowIndex(node);
            Integer col = GridPane.getColumnIndex(node);

            if (fila == null || col == null || fila == 0 || col == 0) continue; // omitir etiquetas

            if (node instanceof StackPane celdaPane) {
                if (habilitar) {
                    celdaPane.setDisable(false);
                } else {
                    celdaPane.setDisable(true);
                }
            }
        }
    }

    private void limpiarTableroPrincipal() {
        for (Node node : tableroPrincipal.getChildren()) {
            Integer fila = GridPane.getRowIndex(node);
            Integer col = GridPane.getColumnIndex(node);

            if (fila == null || col == null || fila == 0 || col == 0) continue;

            if (node instanceof StackPane celdaPane) {
                celdaPane.getChildren().removeIf(child -> child instanceof Shape && !("fondoCelda".equals(child.getId())));
            }
        }
    }

    private void limpiarTableroPosicion() {
        for (Node node : tableroPosicion.getChildren()) {
            Integer fila = GridPane.getRowIndex(node);
            Integer col = GridPane.getColumnIndex(node);

            if (fila == null || col == null || fila == 0 || col == 0) continue;

            if (node instanceof StackPane celdaPane) {
                celdaPane.getChildren().removeIf(child -> child instanceof Shape && !("fondoCelda".equals(child.getId())));
            }
        }
    }

    public void reiniciarJuego() {
        tableroJugador = new Tablero();
        tableroMaquina = new Tablero();
        limpiarTableroPrincipal();
        limpiarTableroPosicion();
        crearTablero(tableroPosicion, tableroJugador, false);
        crearTablero(tableroPrincipal, tableroMaquina, true);

        // Limpiar y volver a colocar barcos
        contenedorBarcos.getChildren().clear();
        inicializarBarcos();
        colocarBarcosMaquina();

        // Resetear estado de juego
        juegoIniciado = false;
        juegoTerminado = false;
        estrategiaTurno = null;

        habilitarTableroEnemigo(false);
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
                        if (juegoIniciado) return;
                        offsetX = e.getX();
                        offsetY = e.getY();
                    });

                    forma.setOnMouseDragged(e -> {
                        if (juegoIniciado) return;
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
                        if (juegoIniciado) return;
                        Point2D local = tableroPosicion.sceneToLocal(e.getSceneX(), e.getSceneY());
                        int col = (int)(local.getX() / 45) - 1;
                        int fila = (int)(local.getY() / 45) - 1;

                        int tamaño = barco.getTamaño();
                        boolean horizontal = barco.esHorizontal();

                        try {
                            if (col < 0 || fila < 0 || col >= 10 || fila >= 10) {
                                throw new ExcepcionPosicionInvalida("El barco se sale del tablero en (" + fila + ", " + col + ")");
                            }

                            if ((horizontal && col + tamaño > 10) || (!horizontal && fila + tamaño > 10)) {
                                throw new ExcepcionPosicionInvalida("El barco se sale del tablero en (" + fila + ", " + col + ")");
                            }

                            tableroJugador.removerBarco(barco);
                            tableroJugador.colocarBarco(barco, fila, col);

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

                                tableroPosicion.add(celda, celdaCol + 1, celdaFila + 1);
                            }
                            contenedorBarcos.getChildren().removeAll(
                                    formasPorCelda.stream().flatMap(List::stream).toList()
                            );

                            System.out.println("Barco colocado desde (" + fila + "," + col + ") tamaño " + tamaño);
                            // Verificar si ya están todos los barcos colocados
                            if (verificarBarcosColocados()) {
                                javafx.scene.control.Alert confirmacion = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                                confirmacion.setTitle("¿Iniciar juego?");
                                confirmacion.setHeaderText("Todos los barcos han sido colocados");
                                confirmacion.setContentText("¿Deseas empezar a jugar?");

                                Optional<ButtonType> resultado = confirmacion.showAndWait();
                                if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                                    juegoIniciado = true;
                                    limpiarTableroPrincipal();
                                    habilitarTableroEnemigo(true);
                                    setEstrategiaTurno(new TurnoHumano(GameController.this));
                                    ejecutarTurnoActual();
                                    System.out.println("El juego ha comenzado");
                                }
                            }
                        } catch (ExcepcionPosicionInvalida | ExcepcionCeldaOcupada ex) {
                            System.out.println("Error: " + ex.getMessage());

                            javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                            alerta.setTitle("Colocación inválida");
                            alerta.setHeaderText("No se pudo colocar el barco");
                            alerta.setContentText(ex.getMessage());
                            alerta.showAndWait();
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

    public Tablero getTableroJugador() {
        return tableroJugador;
    }

    public GridPane getTableroPosicion() {
        return tableroPosicion;
    }

    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }

    public void terminarJuego() {
        this.juegoTerminado = true;
    }
}