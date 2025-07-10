module com.example.uvfpoebatallanaval {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.uvfpoebatallanaval to javafx.fxml;
    exports com.example.uvfpoebatallanaval;

    opens  com.example.uvfpoebatallanaval.controlador;
    exports com.example.uvfpoebatallanaval.controlador to javafx.fxml;
}