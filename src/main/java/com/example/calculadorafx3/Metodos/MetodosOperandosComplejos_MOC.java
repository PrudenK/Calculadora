package com.example.calculadorafx3.Metodos;

import javafx.scene.control.Label;

import static com.example.calculadorafx3.Metodos.MetodosGenerales_MGEN.*;

public class MetodosOperandosComplejos_MOC extends Atributos{
    public static String buscarOperadorComplejo_MOC(String txt){
        String cadenaOpComplejo = null;
        for (String subcadena : operandosComplejos) {
            if (txt.contains(subcadena)) {
                cadenaOpComplejo = subcadena;
                break;
            }
        }
        return cadenaOpComplejo;
    }
    public static void setOperandosComplejos_MOC(){
        operandosComplejos.add("ln(");
        operandosComplejos.add("sen(");
        operandosComplejos.add("cos(");
        operandosComplejos.add("tan(");
        operandosComplejos.add("ArcSen(");
        operandosComplejos.add("ArcCos(");
        operandosComplejos.add("ArcTan(");
        operandosComplejos.add("log10(");
        operandosComplejos.add("log(");
    }
    public static void agregarOperandoComplejoMenosDelante_MOC(String op, boolean pantallaFuncionesBolean){
        String txt = pantallaFunciones.getText();
        if(pantallaFuncionesBolean ) {
            if(!txt.contains("x") && !txt.contains("(")) {
                boolean menosDelante = false;
                String aux ="";
                if(txt.startsWith("-")){
                    txt = txt.substring(1);
                    menosDelante = true;
                }
                if (txt.equals("0") || txt.isEmpty() || (operandos_MGEN(txt,false) == 0.0 && !txt.startsWith("-"))) {
                    aux = op;
                } else if (txt.matches("^-?(\\d+\\.\\d+|\\d+|e|π|φ)?")) {
                    aux = txt + " · " + op;
                }
                if(menosDelante){
                    aux = "-"+aux;
                }
                pantallaFunciones.setText(aux);
            }
        }
        else {
            agregarOperandoComplejoMenosDelantePantalla(op);
        }
    }
    public static void escribirArcFun_MOC(String arco, String trigoNormal, boolean pantallaFuncionesBolean, boolean arcFun){
        if(pantallaFuncionesBolean && arcFun){
            agregarOperandoComplejoMenosDelante_MOC(arco, true);
        }else {
            agregarOperandoComplejoMenosDelante_MOC(trigoNormal, pantallaFuncionesBolean);
        }
    }
    private static void agregarOperandoComplejoMenosDelantePantalla(String op){
        if(pantalla.getText().startsWith("-")){
            pantalla.setText("-"+op);
        }else {
            pantalla.setText(op);
        }
    }
    public static void agregarExpresionN_MOC(String operador, boolean pantallaFuncionesBolean){
        if(pantallaFuncionesBolean){
            agregarExpresionNPantalla(operador, pantallaFunciones);
        }else {
            agregarExpresionNPantalla(operador, pantalla);
        }
    }
    private static void agregarExpresionNPantalla(String operador, Label pantalla){
        String txt = pantalla.getText();
        if(patronNumsAntesRaiz.matcher(txt).matches() && !txt.isEmpty() && !txt.equals("-")){
            pantalla.setText(txt+operador);
        }else {
            mostrarAlerta_MGEN("Tienes que añadir la base o raíz delante y tiene que ser un parámetro válido");
        }
    }
}
