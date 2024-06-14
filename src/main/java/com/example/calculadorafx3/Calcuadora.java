package com.example.calculadorafx3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Calcuadora extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Calcuadora.class.getResource("CalculadoraCompleja.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 287, 382);

        //scene.getStylesheets().add(this.getClass().getResource("calc.css").toExternalForm());
        stage.setTitle("Calculadora");
        stage.setScene(scene);
        stage.show();

    }
    public static void main(String[] args) {
        launch();
    }
}