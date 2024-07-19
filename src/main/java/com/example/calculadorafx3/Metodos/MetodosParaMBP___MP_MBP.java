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
        Pattern pattern = Pattern.compile("^-?\\d*\\.?\\d*[-+x/√%^]\\d*\\.?\\d*$");
        String operandoComplejo = buscarOperadorComplejo_MOC(txt);
        if(!(pantallaFuncionesBolean && txt.endsWith("x"))){
            if(!(pantallaFuncionesBolean && txt.endsWith(")") && operandoComplejo == null)) {
                if (operandoComplejo != null) {
                    if(pantallaFuncionesBolean){
                        operandosComplejos00Fun(pantalla);
                    }else {
                        operandosComplejos00(operandoComplejo + ")", pantalla);
                    }
                } else if (txt.contains("∞")) {
                    pantalla.setText("0");
                } else if (pantallaFuncionesBolean && txt.contains("√")) {
                    if(txt.endsWith("+") || txt.endsWith("-") || txt.endsWith("(") || txt.endsWith("√")){
                        pantalla.setText(txt+"0");
                    } else if ((txt.endsWith(".") || txt.matches(".*[1-9]\\d*\\.?\\d*$") || txt.matches(".*0\\.\\d*"))) {
                        pantalla.setText(txt+"00");
                    }
                } else {
                    if (!txt.equals("-0") && !txt.equals("0") && !txt.endsWith("π") && !txt.endsWith("e") && !txt.endsWith("φ")) {
                        if ((pattern.matcher(pantalla.getText()).matches() && pantalla.getText().endsWith("0")) || txt.contains("√") || txt.contains("π") || txt.contains("e") || txt.startsWith("φ")) {
                            if (txt.startsWith("-")) {
                                txt = txt.substring(1);
                            }
                            boolean contieneNumero = false;
                            for (char c : txt.toCharArray()) {
                                if (Character.isDigit(c) && c != '0') {
                                    contieneNumero = true;
                                    break;
                                }
                            }
                            if (txt.endsWith("√") || txt.endsWith("√-")) {
                                pantalla.setText(pantalla.getText() + "0");
                            } else if ((puntoDespuesDelOperador.matcher(txt).matches() || !txt.matches("^-?(e|π|φ|(\\d*\\.?\\d*))[-+√x/%^]0$"))
                                    && !txt.startsWith("π") && !txt.startsWith("e") && !txt.startsWith("φ")) {
                                pantalla.setText(pantalla.getText() + "00");
                            } else if ((pantalla.getText().contains(".") || contieneNumero) && (txt.contains("π") || txt.contains("e") || txt.startsWith("φ"))) {
                                pantalla.setText(pantalla.getText() + "00");
                            } else if (!pantalla.getText().endsWith("0")) {
                                pantalla.setText(pantalla.getText() + "0");
                            }
                        } else if (pantalla.getText().isEmpty() || (pantalla.getText().charAt(0) == '0' && pantalla.getText().charAt(1) != '.' && caracteresExcluidos.contains(pantalla.getText().charAt(pantalla.getText().length() - 1)))) {
                            if (txt.endsWith("+") || txt.endsWith("-") || txt.endsWith("/") || txt.endsWith("x") || txt.endsWith("%") || txt.endsWith("^")) {
                                pantalla.setText(txt + "0");
                            } else {
                                pantalla.setText("0");
                            }
                        } else if (pantalla.getText().equals("-")) {
                            pantalla.setText("-0");
                        } else if (pantalla.getText().equals("-0")) {
                            pantalla.setText("-0");
                        } else if (caracteresExcluidos.contains(pantalla.getText().charAt(pantalla.getText().length() - 1))) {
                            pantalla.setText(pantalla.getText() + "0");
                        } else {
                            if(pantallaFuncionesBolean){
                                if(txt.matches(".*([1-9]+\\.?\\d*|0\\.\\d*)")){
                                    pantalla.setText(pantalla.getText() + "00");
                                }
                            }else {
                                pantalla.setText(pantalla.getText() + "00");
                            }
                        }
                    }
                }
            }
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
