package com.example.calculadorafx3.Metodos;

import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.calculadorafx3.Controlador.setEscribirDentroFunB;
import static com.example.calculadorafx3.Metodos.MetodosBotonesPantalla_MBP.*;
import static com.example.calculadorafx3.Metodos.MetodosFunciones_MFUN.*;
import static com.example.calculadorafx3.Metodos.MetodosIgualSimpleCompleja_MISC.*;
import static com.example.calculadorafx3.Metodos.MetodosOperadoresSimples_MOS.*;
import static com.example.calculadorafx3.Metodos.MetodosOperandosComplejos_MOC.buscarOperadorComplejo_MOC;

//En este fichero hay metoods los cuales son para interactuar con el teclado con la app
public class MetodosBotonesTeclado_MBT {
    public static void pasarAlSiguiente_MBT(KeyEvent event, RadioButton next) {
        if (event.getCode() == KeyCode.TAB) {
            event.consume();  // Para que el tabulador funcione con los radioButtons
            next.requestFocus();
            next.setSelected(true);
        }
    }
    public static void deshabilitarBotonesTabulador_MBT(GridPane gridPane) {
        gridPane.getChildren().forEach(node -> {
            if (node instanceof Button) {
                ((Button) node).setFocusTraversable(false);
            }
        });
    }
    // Aquí le paso un número indefinido de botones que quiero que me desactive
    public static void setBotonesDesactivarEcuaciones_MBT(ArrayList<Button> botonesDesactivarEcuaciones, Button... botones) {
        botonesDesactivarEcuaciones.addAll(Arrays.asList(botones));
    }
    public static void setGrupoBotones_MBT(ToggleGroup grupo, RadioButton... botones) {
        for (RadioButton boton : botones) {
            boton.setToggleGroup(grupo);
        }
    }
    public static boolean esteclaNumerica_MBT(KeyCode code) {
        return (code.isDigitKey() || code.isKeypadKey());
    }
    public static String getTeclaNumerica_MBT(KeyCode code) {
        if (code.isDigitKey()) {
            return String.valueOf(code.ordinal() - KeyCode.DIGIT0.ordinal());
        } else if (code.isKeypadKey()) {
            return String.valueOf(code.ordinal() - KeyCode.NUMPAD0.ordinal());
        }
        return "";
    }

    public static void solucionarIgualEcuacionesTeclado_MBT(RadioButton radioButton, boolean pantallaFuncionesBolean, boolean pantallaEcuacionesBolean,
                                                            double offsetX, double offsetY, double scale) {

        radioButton.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onIgual_MISC(pantallaFuncionesBolean, pantallaEcuacionesBolean, offsetX, offsetY, scale);
                event.consume(); // Para evitar que otros handlers procesen este evento
            }
        });
    }

    public static void escribirUsandoTeclas_MBT(KeyEvent event, boolean pantallaFuncionesBolean, boolean pantallaEcuacionesBolean, boolean escribirDentroFunB,
                                                double offsetX, double offsetY, double scale) {
        KeyCode code = event.getCode();
        if(event.getCode() == KeyCode.TAB){
            if(pantallaFuncionesBolean && buscarOperadorComplejo_MOC(pantallaFunciones.getText()) != null) {
                if (escribirDentroFunB) {
                    escribirDentroFun.setDisable(false);
                    escribirFueraFun.setDisable(true);
                    multiplicandoEspecialFun.setDisable(false);
                    setEscribirDentroFunB(false);
                } else {
                    escribirDentroFun.setDisable(true);
                    escribirFueraFun.setDisable(false);
                    multiplicandoEspecialFun.setDisable(true);
                    setEscribirDentroFunB(true);
                }
            }
        }else if(event.getCode() == KeyCode.ENTER){
            onIgual_MISC(pantallaFuncionesBolean, pantallaEcuacionesBolean, offsetX, offsetY, scale);
        }else if ((event.getCode() == KeyCode.DEAD_GRAVE) && event.isShiftDown()) {
            onOperador_MOS("^", pantallaFuncionesBolean, escribirDentroFunB, pantallaEcuacionesBolean);
        }else if ((event.getCode() == KeyCode.DIGIT5 || event.getCode() == KeyCode.NUMPAD5) && event.isShiftDown()) {
            onOperador_MOS("%", pantallaFuncionesBolean, escribirDentroFunB, pantallaEcuacionesBolean);
        }else if ((event.getCode() == KeyCode.DIGIT8 || event.getCode() == KeyCode.NUMPAD8) && event.isShiftDown()){
            onAbrirParentesis_MFUN(escribirDentroFunB);
        }else if((event.getCode() == KeyCode.DIGIT9 || event.getCode() == KeyCode.NUMPAD9) && event.isShiftDown()) {
            onCerrarParentesis_MFUN(escribirDentroFunB);
        }else if (event.getCode() == KeyCode.X) {
            if(pantallaFuncionesBolean){
                onButtonXFunciones_MFUN(escribirDentroFunB, true, pantallaEcuacionesBolean);
            }else {
                onOperador_MOS("x", false, escribirDentroFunB, pantallaEcuacionesBolean);
            }
        } else if ((event.getCode() == KeyCode.DIGIT7 || event.getCode() == KeyCode.NUMPAD7) && event.isShiftDown()) {
            onOperador_MOS( "/", pantallaFuncionesBolean, escribirDentroFunB, pantallaEcuacionesBolean);
        }else if (event.getCode() == KeyCode.BACK_SPACE) {
            onAtras_MBP(pantallaFuncionesBolean, pantallaEcuacionesBolean, escribirDentroFunB);
        } else if (event.getCode() == KeyCode.PLUS || (event.getCode() == KeyCode.EQUALS && event.isShiftDown())) {
            onOperador_MOS( "+", pantallaFuncionesBolean, escribirDentroFunB, pantallaEcuacionesBolean);
        } else if (event.getCode() == KeyCode.MINUS) {
            if(!pantallaEcuacionesBolean) {
                onOperador_MOS( "-", pantallaFuncionesBolean, escribirDentroFunB, false);
                extensionMenos_MOS(pantallaFuncionesBolean);
            }else {
                onButtonMasMenos_MBP(pantallaFuncionesBolean, true, escribirDentroFunB);
            }
        } else if (event.getCode() == KeyCode.PERIOD || event.getCode() == KeyCode.DECIMAL) {
            onButtonPunto_MBP(pantallaFuncionesBolean, pantallaEcuacionesBolean, escribirDentroFunB);
        } else if (esteclaNumerica_MBT(code)) { //con esto cualquier tecla numerica que presione se procesará aquí
            agregarNumero_MBP( getTeclaNumerica_MBT(code), pantallaFuncionesBolean, pantallaEcuacionesBolean, escribirDentroFunB);
        }else if(event.getCode() == KeyCode.E){
            expresionesMatematicas_MBP('e', pantallaFuncionesBolean, pantallaEcuacionesBolean, escribirDentroFunB);
        }
    }
}
