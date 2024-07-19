package com.example.calculadorafx3.Metodos;

import static com.example.calculadorafx3.Metodos.MetodosEcuaciones_MECU.calcularEcuaciones_MECU;
import static com.example.calculadorafx3.Metodos.MetodosFunciones_MFUN.dibujarGrafico_MFUN;
import static com.example.calculadorafx3.Metodos.MetodosGenerales_MGEN.*;
import static com.example.calculadorafx3.Metodos.MetodosOperandosComplejos_MOC.buscarOperadorComplejo_MOC;

public class MetodosIgualSimpleCompleja_MISC extends Atributos{

    public static void onIgual_MISC(boolean pantallaFuncionesBolean, boolean pantallaEcuacionesBolean, double offsetX, double offsetY, double scale){
        String txt = pantalla.getText();
        if(!pantallaFuncionesBolean) {
            if (!pantallaEcuacionesBolean) {
                if (!txt.isEmpty()) {
                    operacionAnteriror.setText(txt);
                    boolean menosDelante = false;
                    if (txt.charAt(0) == '-') {
                        txt = txt.substring(1);
                        menosDelante = true;
                    }
                    String opComlejo = buscarOperadorComplejo_MOC(pantalla.getText());
                    if (txt.contains("√")) {
                        calcularRaiz(txt,menosDelante);
                    } else if (opComlejo != null && !txt.endsWith("()") && !txt.endsWith("(-)")) {
                        calcularOpComplejo(txt,menosDelante);
                    } else if (txt.endsWith("+") || txt.endsWith("-") || txt.endsWith("/") || txt.endsWith("x") || txt.endsWith("%") || txt.endsWith("^")) {
                        mostrarAlerta_MGEN("Introduce el segundo número");
                    } else {
                        calcularSimple(txt,menosDelante,opComlejo);
                    }
                }
            } else {
                calcularEcuaciones_MECU();
            }
        }else {
            dibujarGrafico_MFUN(offsetX, offsetY, scale);
        }
    }
    private static void calcularOpComplejo(String txt, boolean menosDelante){
        boolean menosDelanteOpComplejo = false;
        boolean errorTangente = false;
        boolean errorArcos = false;
        boolean errorLogaritmo = false;
        int indicePrimerParentesis = txt.indexOf("(");
        int indiceUltimoParentesis = txt.indexOf(")");
        double operando;
        String operandostring = txt.substring(indicePrimerParentesis + 1, indiceUltimoParentesis);
        if (operandostring.contains("-")) {
            menosDelanteOpComplejo = true;
            operandostring = operandostring.substring(1);
        }
        operando = operandos_MGEN(operandostring, menosDelanteOpComplejo);

        if (txt.contains("ln(")) {
            if (Double.parseDouble(String.valueOf(operando)) == 0.0) {
                mostrarAlerta_MGEN("Fieraaa que no puedes hacer un logarítmo neperiano de 0");
                errorLogaritmo = true;
            } else {
                operando = Math.log(operando);
            }
        } else if (txt.contains("en(")) {
            if (txt.contains("Arc")) {
                if (operando > 1 || operando < -1) {
                    mostrarAlerta_MGEN("Jefazooo no puedes calcular un arcoseno de un númoer mayor que 1 o menor que menos 1");
                    errorArcos = true;
                } else {
                    operando = Math.toDegrees(Math.asin(operando));
                }
            } else {
                operando = Math.sin(Math.toRadians(operando));
            }
        } else if (txt.contains("os(")) {
            if (txt.contains("Arc")) {
                if (operando > 1 || operando < -1) {
                    mostrarAlerta_MGEN("Jefazooo no puedes calcular un arcoseno de un númoer mayor que 1 o menor que menos 1");
                    errorArcos = true;
                } else {
                    operando = Math.toDegrees(Math.acos(operando));
                }
            } else {
                operando = Math.cos(Math.toRadians(operando));
            }
        } else if (txt.contains("an(")) {
            if (txt.contains("Arc")) {
                operando = Math.toDegrees(Math.atan(operando));
            } else {
                if (operando % 90 == 0 && operando % 180 != 0) {
                    errorTangente = true;
                    mostrarAlerta_MGEN("Chavalíiiin no existe la tangete de un número que sea múltiplo de 90 y no de 180\nTiende a infinito y esas cosas raras");
                } else {
                    operando = Math.tan(Math.toRadians(operando));
                }
            }
        } else if (txt.contains("log10(")) {
            if (Double.parseDouble(String.valueOf(operando)) == 0.0) {
                mostrarAlerta_MGEN("Fieraaa que no puedes hacer un logarítmo de 0");
                errorLogaritmo = true;
            } else {
                operando = Math.log10(operando);
            }
        } else if (txt.contains("log(")) {
            String base = txt.substring(0, indicePrimerParentesis - 3);
            if (Double.parseDouble(String.valueOf(operando)) == 0.0) {
                mostrarAlerta_MGEN("Fieraaa que no puedes hacer un logarítmo de 0");
                errorLogaritmo = true;
            } else if ((menosDelante || Double.parseDouble(base) == 0.0)) {
                mostrarAlerta_MGEN("La base del logarítmo no puede ser menor o igual que 0");
                errorLogaritmo = true;
            } else {
                operando = Math.log(operando) / Math.log(Double.parseDouble(base));
            }
        }

        if (menosDelante) {
            operando *= -1;
        }
        if (!errorTangente && !errorArcos && !errorLogaritmo) {
            pantalla.setText(df.format(operando));
        }
    }

    private static void calcularRaiz(String txt, boolean menosDelante){
        boolean menosDentroRaiz = false;
        int indiceRaiz = txt.indexOf("√");
        if (txt.contains("-")) {
            txt = txt.substring(0, indiceRaiz + 1) + txt.substring(indiceRaiz + 2);
            menosDentroRaiz = true;
        }
        String operando1String = txt.split("√")[0];
        String operando2String = txt.split("√")[1];
        double operando1, operando2;
        operando1 = operandos_MGEN(operando1String, menosDelante);
        operando2 = operandos_MGEN(operando2String, false);
        if (operando1 % 2 == 0 && menosDentroRaiz) {
            mostrarAlerta_MGEN("No puedes hacer una raíz de indice par con radicando negativo");
        } else if (Double.parseDouble(String.valueOf(operando1)) == 0.0) {
            mostrarAlerta_MGEN("El indice de la raiz no es 0");
        } else {
            double resultado = Math.pow(operando2, 1.0 / operando1);
            if (menosDentroRaiz) {
                resultado *= -1;
            }
            pantalla.setText(df.format(resultado));
        }
    }
    private static void calcularSimple(String txt, boolean menosDelante, String opComlejo){
        if (txt.equals("π") || txt.equals("φ") || txt.equals("e")) {
            pantalla.setText(String.valueOf(operandos_MGEN(txt, menosDelante)));
        }
        if ((txt.contains("+") || txt.contains("/") || txt.contains("%") || txt.contains("x") || txt.contains("-") || txt.contains("^")) && opComlejo == null) {
            int indiceOperadorInt = indiceOperador_MGEN(txt);
            double operando1 = operandos_MGEN(txt.substring(0, indiceOperadorInt), menosDelante);
            double operando2 = operandos_MGEN(txt.substring(indiceOperadorInt + 1), false);
            double resultado = 0;
            if (txt.contains("+")) {
                resultado = operando1 + operando2;
            } else if (txt.contains("x")) {
                resultado = operando1 * operando2;
            } else if (txt.contains("-")) {
                resultado = operando1 - operando2;
            } else if (txt.contains("%")) {
                if (operando2 == 0.0) {
                    mostrarAlerta_MGEN("No puedes hacer n % 0 maquinón");
                } else {
                    resultado = operando1 % operando2;
                }
            } else if (txt.contains("/")) {
                if (operando2 == 0.0) {
                    mostrarAlerta_MGEN("No puedes divir por 0 máquina");
                } else {
                    resultado = operando1 / operando2;
                }
            } else if (txt.contains("^")) {
                resultado = Math.pow(operando1, operando2);
            }
            pantalla.setText(df.format(resultado));
        }
    }
}
