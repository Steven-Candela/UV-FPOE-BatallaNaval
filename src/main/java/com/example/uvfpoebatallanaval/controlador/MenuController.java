package com.example.uvfpoebatallanaval.controlador;

import com.example.uvfpoebatallanaval.modelo.GestorPartida;
import com.example.uvfpoebatallanaval.modelo.Jugador;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class MenuController {
    @FXML
    private void onActionJugarButton(ActionEvent event) throws IOException {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Ingresa tu nickname");
        dialog.setHeaderText("Nombre de jugador");
        dialog.setContentText("Por favor, escribe tu nickname:");

        Optional<String> resultado = dialog.showAndWait();
        resultado.ifPresent(nickname -> {
            if (!nickname.trim().isEmpty()) {
                System.out.println("Nickname ingresado: " + nickname);
                Jugador jugador = new Jugador(nickname);
                System.out.println("El juego inicia");
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/uvfpoebatallanaval/juego-view.fxml"));
                Scene scene = null;
                try {
                    scene = new Scene(fxmlLoader.load());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setTitle("Juego");
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.show();
            } else {
                javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                alerta.setTitle("Nickname invalido");
                alerta.setHeaderText("Debes poner un nickname.");
                alerta.showAndWait();
            }
        });
    }


    @FXML
    private void onActionUltimaPartidaButton(ActionEvent event) throws IOException {
        Optional<GestorPartida.EstadoJuego> estado = GestorPartida.cargarPartida();

        if (estado.isPresent()) {
            // 👇 Asignamos el estado global antes de cargar la vista
            GameController.estadoGuardado = estado.get();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/uvfpoebatallanaval/juego-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Juego");
            stage.setMaximized(true);
            stage.show();
        } else {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error al cargar partida");
            alerta.setHeaderText("No se pudo cargar la partida");
            alerta.setContentText("Verifica que exista una partida guardada.");
            alerta.showAndWait();
        }
    }

    @FXML
    private void onActionCreditosButton(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/uvfpoebatallanaval/creditos-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Créditos");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void onActionSalirButton(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void onActionInstruccionesButton(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/uvfpoebatallanaval/instrucciones-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Instrucciones");
        stage.setScene(scene);
        stage.show();
    }
}
