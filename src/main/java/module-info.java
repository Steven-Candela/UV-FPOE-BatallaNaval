module com.example.uvfpoebatallanaval {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.uvfpoebatallanaval to javafx.fxml;
    exports com.example.uvfpoebatallanaval;
}