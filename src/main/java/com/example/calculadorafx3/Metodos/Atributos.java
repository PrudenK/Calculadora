package com.example.calculadorafx3.Metodos;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Pattern;

public class Atributos {
    protected static Pattern patronNumsAntesRaiz, patronDespuesOperadorNumsEspeciales, puntoDespuesDelOperador, patronNumEnteroPositivo, patronCuadradoRaiz;
    protected static Label pantalla, pantallaFunciones, operadorLabelFun, desplazamientoYtrigoFun;
    protected static Label labelx2, labelx1, labelC, labelR, idEcuacionLabel, LabelSolucionX1, LabelSolucionX2;
    protected static RadioButton rbX2, rbx, rbc, rbr;
    protected static char[] caracteresProhibidos, caracteresProhibidosFunciones;
    protected static ArrayList<String> operandosComplejos;
    protected static ArrayList<Button> botonesDesactivarEcuaciones;
    protected static Set<Character> caracteresExcluidos;
    protected static DecimalFormat df;
    protected static Text operacionAnteriror;
    protected static Canvas canvasFunciones;
    protected static GridPane cientifica, gridPaneBasica, gridPaneEcuaciones, gridPaneFunciones;
    protected static Pane ecuaciones2Grado, ecuaciones2Grado2, funcionesPane,idPanePantallaFunciones, original;
    protected static Button senFun, cosFun, tanFun, escribirDentroFun, escribirFueraFun, multiplicandoEspecialFun;
    public static void setBotonesFunciones(Button senFunSet, Button cosFunSet, Button tanFunSet, Button escribirDentroFunSet, Button escribirFueraFunSet
            , Button multiplicandoEspecialFunSet){
        senFun = senFunSet;
        cosFun = cosFunSet;
        tanFun = tanFunSet;
        escribirDentroFun = escribirDentroFunSet;
        escribirFueraFun = escribirFueraFunSet;
        multiplicandoEspecialFun = multiplicandoEspecialFunSet;
    }
    public static void setPanes(Pane ecuaciones2GradoSet, Pane ecuaciones2Grado2Set, Pane funcionesPaneSet, Pane idPanePantallaFuncionesSet, Pane originalSet){
        ecuaciones2Grado = ecuaciones2GradoSet;
        ecuaciones2Grado2 = ecuaciones2Grado2Set;
        funcionesPane = funcionesPaneSet;
        idPanePantallaFunciones = idPanePantallaFuncionesSet;
        original = originalSet;
    }
    public static void setGridPanes(GridPane cientificaSet, GridPane gridPaneBasicaSet, GridPane gridPaneEcuacionesSet, GridPane gridPaneFuncionesSet){
        cientifica = cientificaSet;
        gridPaneBasica = gridPaneBasicaSet;
        gridPaneEcuaciones = gridPaneEcuacionesSet;
        gridPaneFunciones = gridPaneFuncionesSet;
    }
    public static void setCanvas(Canvas funciones){
        canvasFunciones = funciones;
    }
    public static void setArraysYesasCosas(char[] prohibidos, char[] prohibidosFunciones,Set<Character> caracteresExcluidosSet, ArrayList<String> operandosComplejosSet, DecimalFormat dfSet,
                                           ArrayList<Button> botonesDesactivarEcuacionesSet){
        caracteresProhibidos = prohibidos;
        caracteresProhibidosFunciones = prohibidosFunciones;
        caracteresExcluidos = caracteresExcluidosSet;
        operandosComplejos = operandosComplejosSet;
        df = dfSet;
        botonesDesactivarEcuaciones = botonesDesactivarEcuacionesSet;
    }
    public static void setPatrones(Pattern numsAntesRaiz, Pattern despuesOperadorNumsEspeciales, Pattern puntoDespuesDelOp, Pattern numEnteroPositivo, Pattern cuadradoRaiz){
        patronNumsAntesRaiz = numsAntesRaiz;
        patronDespuesOperadorNumsEspeciales = despuesOperadorNumsEspeciales;
        puntoDespuesDelOperador = puntoDespuesDelOp;
        patronNumEnteroPositivo = numEnteroPositivo;
        patronCuadradoRaiz = cuadradoRaiz;
    }
    public static void setLabels(Label pantallaOri, Label pantallaFuncionesOri, Label operadorLabelFunOri, Label desplazamientoYtrigoFunOri, Text operacionAnterirorOri) {
        pantalla = pantallaOri;
        pantallaFunciones = pantallaFuncionesOri;
        operadorLabelFun = operadorLabelFunOri;
        desplazamientoYtrigoFun = desplazamientoYtrigoFunOri;
        operacionAnteriror = operacionAnterirorOri;
    }
    public static void setLabelsEcuaciones(Label lx2, Label lx1, Label lc, Label lr, Label idEcuLabel, Label ls1, Label ls2){
        labelx2 = lx2;
        labelx1 = lx1;
        labelC = lc;
        labelR = lr;
        idEcuacionLabel = idEcuLabel;
        LabelSolucionX1 = ls1;
        LabelSolucionX2 = ls2;
    }
    public static void setRadioButtons(RadioButton r2, RadioButton r1, RadioButton rc, RadioButton rr){
        rbX2 = r2;
        rbx = r1;
        rbc = rc;
        rbr = rr;
    }

}
