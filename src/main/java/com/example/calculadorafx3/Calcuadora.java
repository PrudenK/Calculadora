package com.example.calculadorafx3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Calcuadora extends Application {

    static Stage st;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Calcuadora.class.getResource("CalculadoraCompleja.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 276, 403);

        stage.setTitle("Calculadora");
        stage.setScene(scene);

        stage.initStyle(StageStyle.UNDECORATED); // Eliminar barra de título
        stage.setResizable(false); // Evitar que se pueda expandir

        stage.show();

        st = stage;

        Controlador controlador = fxmlLoader.getController();
        controlador.pasarEscena(scene);
    }
    public static void main(String[] args) {
        launch();
    }

    public static Stage getStage() {
        return st;
    }
}