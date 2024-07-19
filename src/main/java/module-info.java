module com.example.calculadorafx3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens com.example.calculadorafx3 to javafx.fxml;
    exports com.example.calculadorafx3;
    exports com.example.calculadorafx3.Operaciones;
    opens com.example.calculadorafx3.Operaciones to javafx.fxml;
    exports com.example.calculadorafx3.Metodos;
    opens com.example.calculadorafx3.Metodos to javafx.fxml;
}