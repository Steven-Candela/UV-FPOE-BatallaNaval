package com.example.uvfpoebatallanaval.controlador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {
    @FXML
    private void onActionJugarButton(ActionEvent event) throws IOException {
        System.out.println("El juego inicia");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/uvfpoebatallanaval/juego-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Juego");
        stage.setScene(scene);
        stage.show();
    }
}
