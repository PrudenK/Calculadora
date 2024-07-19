package com.example.calculadorafx3.Metodos;

import javafx.scene.control.Label;

import java.util.regex.Pattern;

import static com.example.calculadorafx3.Metodos.MetodosBotonesPantalla_MBP.onButtonMasMenos_MBP;
import static com.example.calculadorafx3.Metodos.MetodosOperandosComplejos_MOC.*;

public class MetodosOperadoresSimples_MOS extends Atributos{
    public static void onOperador_MOS(String operador, boolean pantallaFuncionesBolean, boolean escribirDentroFunB, boolean pantallaEcuacionesBolean){
        if(pantallaFuncionesBolean){
            onOperadorPantalla_MOS(pantallaFunciones,operador, true,escribirDentroFunB, pantallaEcuacionesBolean);
        }else {
            onOperadorPantalla_MOS(pantalla,operador, false,escribirDentroFunB, pantallaEcuacionesBolean);
        }
    }
    private static void onOperadorPantalla_MOS(Label pantalla, String operador, boolean pantallaFuncionesBolean, boolean escribirDentroFunB, boolean pantallaEcuacionesBolean){
        if(!pantallaFuncionesBolean || escribirDentroFunB) {
            String txt = pantalla.getText();
            String opComlejo = buscarOperadorComplejo_MOC(txt);
            if((txt.isEmpty() || txt.equals("-")) && operador.equals("-")){
                onButtonMasMenos_MBP(pantallaFuncionesBolean, pantallaEcuacionesBolean, escribirDentroFunB);
            }else if (!txt.isEmpty() && opComlejo == null) {
                boolean menos = primerMenos(txt);
                if ((!txt.endsWith(".") && !menos && contieneCaracteres(txt, pantallaFuncionesBolean)
                        && txt.charAt(txt.length() - 1) != '-') ||
                        ((pantallaFuncionesBolean && ((txt.endsWith(")") || (txt.contains("^") && !txt.endsWith("^")) && !txt.endsWith("+") && !txt.endsWith("-") &&
                                !txt.endsWith("/") && operadorDespuesPotencia(txt, operador)) && !(txt.contains("^") && operador.equals("^")))))
                        || (pantallaFuncionesBolean && operador.equals("/") && !txt.contains("/") && noTerminaEnOperador(txt))
                        || (pantallaFuncionesBolean && operador.equals("-") && (txt.endsWith("/") || txt.endsWith("(") || txt.endsWith("√")))
                        || (pantallaFuncionesBolean && txt.endsWith("x") && !operador.equals("/"))
                        || (pantallaFuncionesBolean && operador.equals("^")) && txt.contains("/")
                ) {
                    if ((txt.charAt(0) != '-' || txt.length() >= 2) || !txt.equals("-")) {
                        if (pantallaFuncionesBolean) {
                            if (operador.equals("^")) {
                                if (!txt.contains("√")) {
                                    if ((txt.contains("(") && txt.endsWith(")"))) {
                                        pantalla.setText(txt + operador);
                                    } else if (txt.contains("/")) {
                                        if (txt.endsWith("x") && !txt.substring(txt.indexOf("/")).contains("(")) {
                                            pantalla.setText(txt + operador);
                                        }
                                    } else {
                                        if (txt.endsWith("x") && !txt.contains("(")) {
                                            pantalla.setText(txt + operador);
                                        }
                                    }
                                }
                            } else {
                                boolean condicionElseIf = txt.endsWith(")") && (txt.contains("/") || txt.contains("√"));
                                if (operador.equals("+")) {
                                    operadorMasMenosFun(txt,operador,pantalla,!txt.endsWith("(") && !txt.endsWith(")") && txt.contains("x"), condicionElseIf);
                                } else if (operador.equals("-")) {
                                    operadorMasMenosFun(txt,operador,pantalla,(!txt.endsWith(")") && !txt.endsWith(".") && txt.contains("x")) || txt.endsWith("/") || txt.endsWith("("), condicionElseIf);
                                } else {
                                    if(!txt.contains("√") && !txt.endsWith(".") && !txt.endsWith("(")){
                                        pantalla.setText(txt + "/");
                                    }
                                }
                            }
                        } else {
                            pantalla.setText(txt + operador);
                        }
                    }
                }
            } else if (pantallaFuncionesBolean) {
                txt = txt.substring(0, txt.length() - 1);

                if (txt.endsWith("x")) {
                    pantalla.setText(txt + operador + ")");
                } else if (txt.endsWith("^") && operador.equals("-")) {
                    pantalla.setText(txt + operador + ")");
                } else if (txt.contains("^") && !txt.endsWith("^") && !txt.endsWith("-")) {
                    if (operadorDespuesPotencia(txt, operador) && !operador.equals("^")) {
                        pantalla.setText(txt + operador + ")");
                    }
                } else if (operador.equals("-") && txt.endsWith("(")) {
                    pantalla.setText(txt + "-)");
                }
            }
        }else {
            operadorLabelFun.setText(operador);
        }
    }
    private static void operadorMasMenosFun(String txt, String operador, Label pantalla, boolean condicion1, boolean condicionElseIf){
        if (condicion1) {
            pantalla.setText(txt + operador);
        } else if (condicionElseIf) {
            pantalla.setText(txt + operador);
        }
    }
    private static boolean operadorDespuesPotencia(String str, String op) {
        return !Pattern.compile(".*\\^-?(\\d+\\.?\\d*|π|e|φ)[+-](\\d+\\.?\\d*|π|e|φ|$)").matcher(str).matches() && op.matches("^[+^-]$");
    }
    private static boolean noTerminaEnOperador(String txt){
        return !txt.endsWith("+") && !txt.endsWith("-") && !txt.endsWith("^") && !txt.endsWith("/");
    }
    private static boolean primerMenos(String texto){
        return texto.substring(1).contains("-");
    }
    private static boolean contieneCaracteres(String texto, boolean pantallaFuncionesBolean) {
        if(pantallaFuncionesBolean){
            return contieneCaracteresPantalla(texto, caracteresProhibidosFunciones);
        }else {
            return contieneCaracteresPantalla(texto, caracteresProhibidos);
        }
    }
    private static boolean contieneCaracteresPantalla(String texto,char[] listaCaracteres) {
        for (char c : listaCaracteres) {
            if (texto.indexOf(c) != -1) {
                return false;
            }
        }
        return true;
    }
    public static void extensionMenos_MOS(boolean pantallaFuncionesBolean){
        String txt = pantalla.getText();
        String opComlejo = buscarOperadorComplejo_MOC(txt);
        if(!pantallaFuncionesBolean) {
            if (opComlejo != null && !opComlejo.equals("ln(") && !opComlejo.equals("log10(") && !opComlejo.contains("log(")) {
                int indicePrimerParentesis = txt.indexOf("(");
                int indiceUltimoParentesis = txt.indexOf(")");
                if (txt.contains(opComlejo + "-")) {
                    int indiceUltimoMenos = txt.lastIndexOf("-");
                    pantalla.setText(txt.substring(0, indicePrimerParentesis + 1) + txt.substring(indiceUltimoMenos + 1, indiceUltimoParentesis + 1));
                } else {
                    pantalla.setText(txt.substring(0, indicePrimerParentesis + 1) + "-" + txt.substring(indicePrimerParentesis + 1, indiceUltimoParentesis + 1));
                }
            } else if (txt.contains("√")) {
                int indiceRaiz = txt.indexOf("√");
                if (txt.contains("√-")) {
                    pantalla.setText(txt.substring(0, indiceRaiz + 1) + txt.substring(indiceRaiz + 2));
                } else {
                    pantalla.setText(txt.substring(0, indiceRaiz + 1) + "-" + txt.substring(indiceRaiz + 1));
                }
            }
        }else {
            if(pantallaFunciones.getText().endsWith("^") || pantallaFunciones.getText().endsWith("√")){
                pantallaFunciones.setText(pantallaFunciones.getText()+"-");
            }
        }
    }
}
