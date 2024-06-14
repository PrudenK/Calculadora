module com.example.calculadorafx3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens com.example.calculadorafx3 to javafx.fxml;
    exports com.example.calculadorafx3;
}