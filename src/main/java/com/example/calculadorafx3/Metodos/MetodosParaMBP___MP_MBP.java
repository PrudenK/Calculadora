package com.example.calculadorafx3.Metodos;

import javafx.scene.control.Label;

import java.util.regex.Pattern;

import static com.example.calculadorafx3.Metodos.MetodosGenerales_MGEN.*;
import static com.example.calculadorafx3.Metodos.MetodosOperandosComplejos_MOC.*;
public class MetodosParaMBP___MP_MBP {
    public static void onACEcuaciones_MP_MBP(Label pantalla){
        pantalla.setText("0");
    }
    public static void escrbirMasMenosPantalla_MP_MBP(Label pantalla){
        if(pantalla.getText().isEmpty()){
            pantalla.setText("-");
        }else if(pantalla.getText().charAt(0) == '-'){
            pantalla.setText(pantalla.getText().substring(1));
        }else {
            pantalla.setText("-"+pantalla.getText());
        }
    }
    public static void escribirPuntoPantalla_MP_MBP(Label pantalla, boolean pantallaFuncionesBolean){
        String txt = pantalla.getText();
        boolean ultNumPunto = false;
        String opComlejo = buscarOperadorComplejo_MOC(txt);
        if (opComlejo == null ) {
            if(!(pantallaFuncionesBolean && txt.endsWith(")"))) {
                escrbirPunto_MGEN(pantalla,txt,ultNumPunto,"",".");
            }
        }else {
            if(pantallaFuncionesBolean){
                String antesTxt = txt.substring(0,txt.indexOf("(")+1);
                txt = txt.substring(txt.indexOf("(")+1, txt.length()-1);
                escrbirPunto_MGEN(pantalla,txt,ultNumPunto,antesTxt,".)");
            }else {
                if (!txt.contains(".") && !txt.contains(opComlejo + ")") && !txt.contains(opComlejo + "e)") && !txt.contains(opComlejo + "π)")
                        && !txt.contains(opComlejo + "φ)") && !txt.contains(opComlejo + "-e)") && !txt.contains(opComlejo + "-π)") && !txt.contains(opComlejo + "-φ)")) {
                    int indiceultimoParentesis = txt.lastIndexOf(")");
                    String cadenaAnterior = txt.substring(0, indiceultimoParentesis);
                    pantalla.setText(cadenaAnterior + ".)");
                }
            }
        }
    }
    public static void onAtrasEcuaciones_MP_MBP(Label pantalla){
        boolean opSolo = false;
        String txt = pantalla.getText();
        String atras;
        StringBuilder strB = new StringBuilder(pantalla.getText());
        String opComlejo = buscarOperadorComplejo_MOC(txt);
        if(opComlejo == null) {
            if (!pantalla.getText().isEmpty()) {
                strB.deleteCharAt(pantalla.getText().length() - 1);
            }
        }else {
            if(!pantalla.getText().contains(opComlejo+")")) {
                strB.deleteCharAt(pantalla.getText().length() - 2);
            }else {
                opSolo = true;
            }
        }
        atras = String.valueOf(strB);
        if(opSolo){
            atras = "";
        }
        pantalla.setText(atras);
    }

    public static void onButton00Pantalla_MP_MBP(Label pantalla, boolean pantallaFuncionesBolean){
        String txt = pantalla.getText();
        String operandoComplejo = buscarOperadorComplejo_MOC(txt);
        if (operandoComplejo != null) {
            if(pantallaFuncionesBolean){
                operandosComplejos00Fun(pantalla);
            }else {
                operandosComplejos00(operandoComplejo + ")", pantalla);
            }
        }else {
            if(!(pantallaFuncionesBolean && txt.endsWith("x"))){
                simple00(pantalla);
            }
        }
    }
    private static void simple00(Label pantalla){
        String txt = pantalla.getText();
        if(txt.matches(".*[+\\-x/%^]$") || txt.isEmpty() || txt.endsWith("(")){
            pantalla.setText(txt+"0");
        } else if (txt.endsWith(".") || txt.matches(".*[1-9]\\d*\\.?\\d*$") || txt.matches(".*0\\.\\d*")) {
            pantalla.setText(txt+"00");
        } else if (txt.contains("∞")) {
            pantalla.setText("0");
        }
    }
    private static void operandosComplejos00Fun(Label pantalla){
        String txt = pantalla.getText().substring(0, pantalla.getText().indexOf(")"));
        if(!txt.endsWith("x")){
            if(txt.endsWith("+") || txt.endsWith("-") || txt.endsWith("^")){
                pantalla.setText(txt+"0)");
            }else if (txt.endsWith(".") || txt.matches(".*[1-9]\\d*\\.?\\d*$") || txt.matches(".*0\\.\\d*")) {
                pantalla.setText(txt+"00)");
            }if(txt.endsWith("(")){
                pantalla.setText(txt+"0)");
            }
        }
    }
    private static void operandosComplejos00(String operando, Label pantalla){
        String txt = pantalla.getText();
        boolean menosDelante = false;
        if(txt.startsWith("-")){
            txt = txt.substring(1);
            menosDelante = true;
        }
        int indicePrimerParentesis = txt.indexOf("(");
        int indiceUltimoParentesis = txt.indexOf(")");
        String operandoSinParentesisFinal = txt.substring(0,indicePrimerParentesis+1);
        if(txt.contains(operando)){
            txt = txt.substring(0,indicePrimerParentesis+1)+"0)";
        } else if (!txt.contains("π") && !txt.substring(indicePrimerParentesis).contains("e") && !txt.contains("φ")) {
            if (!txt.contains(operandoSinParentesisFinal+"0)") && txt.contains(".")) {
                txt = txt.substring(0,indiceUltimoParentesis)+"00)";
            } else if (!txt.contains(operandoSinParentesisFinal+"0)")) {
                txt = txt.substring(0,indiceUltimoParentesis)+"00)";
            }
        }
        if (menosDelante){
            txt = "-"+txt;
        }
        pantalla.setText(txt);
    }
}
