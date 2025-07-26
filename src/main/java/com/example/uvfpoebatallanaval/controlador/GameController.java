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
import java.util.Optional;

/**
 * Clase GameController que gestiona la lógica del juego.
 *
 * @author Nicolle Paz, Steven Candela y Camilo Portilla
 */
public class GameController {
    private Tablero tableroJugador;
    private Tablero tableroMaquina;
    private boolean juegoIniciado = false;
    private EstrategiaTurno estrategiaTurno;
    private boolean juegoTerminado = false;
    private Jugador jugador;

    @FXML
    private GridPane tableroPosicion;
    @FXML
    private GridPane tableroPrincipal;
    @FXML
    private AnchorPane contenedorBarcos;
    @FXML
    private Label turnoLabel;
    @FXML
    private Label disparoLabel;

    public Label getDisparoLabel() {
        return disparoLabel;
    }

    public static GestorPartida.EstadoJuego estadoGuardado = null;

    /**
     * Método que se ejecuta al iniciar la vista.
     *
     * Si hay una partida guardada, la carga. Si no, empieza una nueva partida.
     */
    @FXML
    public void initialize() {
        if (estadoGuardado != null) {
            cargarPartidaDesdeArchivo(estadoGuardado);
            estadoGuardado = null;
        } else {
            iniciarNuevaPartida();
        }
    }

    /**
     * Inicia una nueva partida desde cero.
     *
     * Elimina cualquier partida guardada anterior, solicita el nombre del jugador,
     * muestra una alerta con instrucciones, crea los tableros del jugador y la máquina,
     * coloca los barcos de ambos y desactiva el tablero enemigo hasta que el jugador esté listo.
     */
    private void iniciarNuevaPartida() {
        GestorPartida.eliminarPartidaGuardada();
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

    /**
     * Solicita al usuario que ingrese su nombre mediante un cuadro de diálogo.
     *
     * Si el jugador ingresa un nombre, se crea un nuevo objeto Jugador con ese nombre.
     * Si cancela el diálogo, se cierra la aplicación.
     */
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
            // Si cancela, se cierra el juego
            javafx.application.Platform.exit();
        });
    }

    /**
     * Carga una partida guardada a partir del estado recibido.
     *
     * Restaura el tablero del jugador, el tablero de la máquina,
     * el nombre del jugador, los barcos hundidos y los textos del turno y disparo.
     * También actualiza los elementos visuales del juego y establece el turno del jugador.
     *
     * @param estado Objeto que contiene toda la información necesaria para reanudar una partida guardada.
     */
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

        redibujarBarcosJugador();
        redibujarDisparos();
    }

    /**
     * Redibuja visualmente todos los barcos del jugador en el tablero de posicionamiento.
     *
     * Este método recorre todas las celdas del tablero del jugador, identifica los barcos,
     * evita dibujar duplicados, calcula su posición base y utiliza las formas gráficas
     * generadas por cada barco para mostrarlos en el `GridPane` correspondiente.
     */
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

    /**
     * Redibuja todos los disparos realizados tanto en el tablero del jugador como en el de la máquina.
     *
     * Este método recorre las celdas de ambos tableros y, si una celda ha sido atacada,
     * añade las formas gráficas correspondientes según el resultado del disparo:
     * - Agua: si no hay barco.
     * - Tocado: si hay un barco que no ha sido hundido.
     * - Hundido: si el barco fue completamente destruido.
     *
     * Se asegura de limpiar correctamente las formas anteriores cuando se dibuja un barco hundido.
     */
    private void redibujarDisparos() {
        // Redibujar disparos en el tablero de la máquina
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

        // Redibujar disparos en el tablero del jugador
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

    /**
     * Guarda el estado actual de la partida.
     *
     * Este método recopila la información relevante del juego, como el estado de ambos tableros,
     * el nombre del jugador, el número de barcos hundidos, y el texto y estilo de las etiquetas de turno y disparo.
     * Luego, delega la tarea de persistencia al {@code GestorPartida.guardarPartida}.
     */
    public void guardarEstado() {
        System.out.println("Guardado desde el turno de la máquina. Barcos hundidos: " + jugador.getBarcosHundidos());

        String textoTurno = turnoLabel.getText();
        String colorTurno = turnoLabel.getStyle();
        String textoDisparo = disparoLabel.getText();
        String colorDisparo = disparoLabel.getStyle();

        GestorPartida.guardarPartida(tableroJugador, tableroMaquina, jugador.getNombre(), jugador.getBarcosHundidos(),
                textoTurno, colorTurno, textoDisparo, colorDisparo);
    }

    /**
     * Maneja el evento del botón para volver al menú principal.
     *
     * Este método carga la vista del menú desde el archivo FXML correspondiente
     * y reemplaza la escena actual con la del menú. También actualiza el título de la ventana.
     *
     * @param event el evento de acción generado al hacer clic en el botón.
     * @throws IOException si ocurre un error al cargar el archivo FXML.
     */
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

    /**
     * Maneja el evento del botón para reiniciar el juego.
     *
     * Muestra una alerta de confirmación al usuario. Si el usuario confirma,
     * se llama al método {@code reiniciarJuego()} para reiniciar el estado del juego.
     *
     * @param event el evento de acción generado al hacer clic en el botón.
     */
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

    /**
     * Inicializa los barcos disponibles para el jugador y los agrega al contenedor
     * de arrastre con posiciones predeterminadas.
     *
     * Cada barco se posiciona en coordenadas específicas dentro del contenedor para
     * que el jugador pueda arrastrarlos al tablero.
     */
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

    /**
     * Agrega visualmente un barco al contenedor de barcos, ubicándolo en las coordenadas especificadas.
     *
     * Este método genera las formas que representan al barco, las asocia al objeto
     * correspondiente mediante {@code setUserData}, y las añade al contenedor visual.
     * Finalmente, se habilita el comportamiento de arrastre mediante {@code BarcoArrastrable}.
     *
     * @param barco El barco que se va a representar y arrastrar.
     * @param x     Coordenada X inicial en la que se colocará el barco.
     * @param y     Coordenada Y inicial en la que se colocará el barco.
     */
    private void agregarAlContenedor(Barco barco, double x, double y) {
        List<List<Shape>> formasPorCelda = barco.crearFormas(x, y);

        // Agregar las formas al contenedor y marcarlas con el barco
        for (List<Shape> grupo : formasPorCelda) {
            for (Shape forma : grupo) {
                forma.setUserData(barco); // Marcar cada forma con su barco
            }
            contenedorBarcos.getChildren().addAll(grupo);
        }

        new BarcoArrastrable(barco, formasPorCelda);
    }
    /**
     * Crea y dibuja el tablero de juego con etiquetas de filas y columnas, y define el comportamiento de cada celda.
     *
     * @param tablero      El GridPane donde se construirá visualmente el tablero.
     * @param modelo       El modelo lógico del tablero (con celdas y barcos).
     * @param esPrincipal  Si es true, se habilitan los disparos (tablero enemigo). Si es false, es el tablero propio.
     */
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
                                        disparoLabel.setStyle("-fx-text-fill: white;");
                                    }
                                    case "tocado" -> {
                                        disparoLabel.setText("Disparo del jugador: ¡TOCADO!");
                                        disparoLabel.setStyle("-fx-text-fill: white;");
                                    }
                                    case "hundido" -> {
                                        disparoLabel.setText("Disparo del jugador: ¡HUNDIDO!");
                                        disparoLabel.setStyle("-fx-text-fill: white;");
                                    }
                                    default -> disparoLabel.setText("Disparo desconocido");
                                }
                            }

                            if (resultado.equals("hundido")) {
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

    /**
     * Coloca aleatoriamente los barcos en el tablero de la máquina.
     * Se crean 10 barcos de diferentes tipos y orientaciones.
     * Cada barco se posiciona aleatoriamente dentro del tablero sin superponerse ni salirse de los límites.
     * En caso de que la posición no sea válida o ya esté ocupada, se vuelve a intentar hasta lograr colocarlo correctamente.
     */
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

    /**
     * Establece la estrategia de turno actual (jugador o máquina) y actualiza la etiqueta del turno en la interfaz.
     *
     * @param estrategia la estrategia de turno a usar, puede ser {@code TurnoHumano} o {@code TurnoMaquina}.
     */
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

    /**
     * Ejecuta el turno actual utilizando la estrategia definida (jugador o máquina).
     * Si el juego ha terminado o no se ha definido una estrategia, no se realiza ninguna acción.
     */
    public void ejecutarTurnoActual() {
        if (juegoTerminado || estrategiaTurno == null) return;
        estrategiaTurno.ejecutarTurno(this);
    }

    /**
     * Verifica si todos los barcos en el tablero han sido hundidos.
     *
     * @param tablero El tablero a verificar.
     * @return {@code true} si todos los barcos han sido atacados (hundidos),
     *         {@code false} si al menos una celda con barco no ha sido atacada.
     */
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

    /**
     * Muestra visualmente los barcos del tablero enemigo en el tablero principal,
     * solo si el juego aún no ha iniciado. Si el juego ya comenzó, muestra una
     * alerta informando que no se puede visualizar el tablero enemigo.
     */
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

    /**
     * Cierra la aplicación cuando se presiona el botón "Salir".
     *
     * @param event el evento de acción generado al presionar el botón
     */
    @FXML
    private void onActionSalirButton(ActionEvent event) {
        System.exit(0);
    }

    /**
     * Obtiene el nodo en la posición especificada dentro de un {@link GridPane}.
     *
     * @param grid   el GridPane del cual se desea obtener la celda
     * @param fila   la fila de la celda (índice empezando en 0)
     * @param columna la columna de la celda (índice empezando en 0)
     * @return el {@link Node} ubicado en la fila y columna especificadas, o {@code null} si no se encuentra ningún nodo
     */
    public Node obtenerCelda(GridPane grid, int fila, int columna) {
        for (Node node : grid.getChildren()) {
            if (GridPane.getRowIndex(node) != null && GridPane.getColumnIndex(node) != null &&
                    GridPane.getRowIndex(node) == fila && GridPane.getColumnIndex(node) == columna) {
                return node;
            }
        }
        return null;
    }

    /**
     * Verifica si el jugador ha colocado todos sus barcos en el tablero.
     * Se espera que en total haya 21 celdas ocupadas por barcos.
     *
     * @return {@code true} si hay exactamente 21 celdas ocupadas por barcos;
     *         {@code false} en caso contrario.
     */
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

    /**
     * Habilita o deshabilita la interacción con las celdas del tablero enemigo.
     * Las etiquetas de fila y columna (fila 0 o columna 0) no se ven afectadas.
     *
     * @param habilitar {@code true} para permitir interacción con las celdas del tablero enemigo,
     *                  {@code false} para deshabilitarla.
     */
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

    /**
     * Elimina todos los elementos gráficos (formas) del tablero principal,
     * excepto aquellos que tienen el ID "fondoCelda".
     * Este método omite las etiquetas de fila y columna ubicadas en la fila 0 o columna 0.
     */
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

    /**
     * Limpia el tablero de posicionamiento eliminando todos los elementos gráficos (formas)
     * dentro de cada celda, excepto aquellos con el ID "fondoCelda".
     * Se omiten las celdas correspondientes a etiquetas de fila y columna (fila 0 o columna 0).
     */
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

    /**
     * Reinicia completamente el juego de Batalla Naval.
     *
     * Este método restablece los tableros del jugador y de la máquina, limpia las vistas gráficas,
     * vuelve a crear los tableros, reinicializa y coloca los barcos, y restablece el estado del juego.
     * También deshabilita la interacción con el tablero enemigo hasta que se inicie una nueva partida.
     */
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

    /**
     * Clase interna que representa un barco que puede ser arrastrado y rotado
     * antes de iniciar el juego. Maneja eventos del mouse para permitir al
     * usuario mover, soltar o rotar el barco en el contenedor.
     */
    private class BarcoArrastrable implements Arrastrable {
        private final Barco barco;
        private final List<List<Shape>> formasPorCelda;
        private double offsetX, offsetY;

        /**
         * Crea un barco arrastrable con sus formas gráficas.
         *
         * @param barco El barco lógico a representar.
         * @param formasPorCelda Lista de formas gráficas agrupadas por celda del barco.
         */
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

                        mover(baseX,baseY);
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

        /**
         * Mueve todas las formas del barco a una nueva posición.
         *
         * @param x Posición X en el contenedor.
         * @param y Posición Y en el contenedor.
         */
        @Override
        public void mover(double x, double y) {
            for (List<Shape> grupo : formasPorCelda) {
                for (Shape forma : grupo) {
                    forma.setLayoutX(x);
                    forma.setLayoutY(y);
                }
            }
        }
    }

    /**
     * Obtiene el modelo del tablero del jugador.
     *
     * @return el {@link Tablero} del jugador.
     */
    public Tablero getTableroJugador() {
        return tableroJugador;
    }

    /**
     * Obtiene el componente visual (GridPane) donde el jugador organiza sus barcos.
     *
     * @return el {@link GridPane} usado para la colocación de los barcos del jugador.
     */
    public GridPane getTableroPosicion() {
        return tableroPosicion;
    }

    /**
     * Indica si el juego ha finalizado.
     *
     * @return {@code true} si el juego ha terminado, {@code false} en caso contrario.
     */
    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }

    /**
     * Marca el estado del juego como terminado.
     */
    public void terminarJuego() {
        this.juegoTerminado = true;
    }
}