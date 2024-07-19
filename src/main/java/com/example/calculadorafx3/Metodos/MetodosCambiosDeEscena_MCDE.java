package com.example.calculadorafx3.Metodos;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MetodosCambiosDeEscena_MCDE extends Atributos{
    public static void ajustarStageFunciones_MCDE(boolean expand){
        ajustarStageComun(expand, 850, 460, funcionesPane, gridPaneFunciones, idPanePantallaFunciones);
    }
    public static void ajustarStageCientifica_MCDE(boolean expand) {
        ajustarStageComun(expand, 437, 403, gridPaneBasica, cientifica);
    }
    public static void ajustarStageEcuaciones_MCDE(boolean expand) {
        ajustarStageComun(expand, 437, 403, gridPaneBasica, ecuaciones2Grado, ecuaciones2Grado2);
    }
    public static void ajustarStageBasica_MCDE(boolean expand){
        ajustarStageComun(expand, 276, 403, gridPaneBasica);

    }
    public static void activarBotonesEcuaciones(boolean encender){
        for (Button botonesDesactivarEcuacione : botonesDesactivarEcuaciones) {
            botonesDesactivarEcuacione.setDisable(encender);
        }
    }
    private static void ajustarStageComun(boolean expand, double tama, double altura,  Pane... panes){
        Stage stage = (Stage) original.getScene().getWindow();
        if (expand) {
            for (Pane pane : panes) {
                pane.setVisible(true);
            }
            stage.setWidth(tama);
            stage.setHeight(altura);
        } else {
            for (Pane pane : panes) {
                pane.setVisible(false);
            }
        }
    }

}
