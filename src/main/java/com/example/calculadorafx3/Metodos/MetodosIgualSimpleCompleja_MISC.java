package com.example.calculadorafx3.Metodos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.calculadorafx3.Metodos.MetodosEcuaciones_MECU.calcularEcuaciones_MECU;
import static com.example.calculadorafx3.Metodos.MetodosFunciones_MFUN.dibujarGrafico_MFUN;
import static com.example.calculadorafx3.Metodos.MetodosGenerales_MGEN.*;
import static com.example.calculadorafx3.Metodos.MetodosGenerales_MGEN.operandos_para_MISC_MGEN;
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
                        mostrarAlerta_MGEN("La operación no puede terminar con un operador");
                    } else {
                        calcularSimple();
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
    private static void calcularSimple(){
        dividePor0 = false;
        ArrayList<String> operadoresList = new ArrayList<>();
        ArrayList<String> numerosList = new ArrayList<>();
        String txt = pantalla.getText();
        if (txt.equals("π") || txt.equals("φ") || txt.equals("e") || txt.equals("-π") || txt.equals("-φ") || txt.equals("-e")) {
            pantalla.setText(String.valueOf(operandos_para_MISC_MGEN(txt)));
        }else {
            int anteriorIndice = 0;
            int indiceSiguienteOperador;
            do {
                indiceSiguienteOperador = indiceOperador_MGEN(txt.substring(anteriorIndice));

                if (indiceSiguienteOperador != -1) {
                    // con esto ajustamos el incio de la cadena
                    indiceSiguienteOperador += anteriorIndice;
                    // indiceSiguienteOperador == 0 para cuando la cadena empiece por -, "+x/%^".indexOf(txt.charAt(indiceSiguienteOperador - 1)) != -1 si el caracter de
                    // después es un operando, en ese caso lo contaremos como número negativo
                    if (txt.charAt(indiceSiguienteOperador) == '-' && (indiceSiguienteOperador == 0 || "+x/%^".indexOf(txt.charAt(indiceSiguienteOperador - 1)) != -1)) {
                        // con esto busco el siguiente operador para ver si la cadena termina
                        int siguienteOperador = indiceOperador_MGEN(txt.substring(indiceSiguienteOperador + 1));
                        if (siguienteOperador != -1) {
                            siguienteOperador += indiceSiguienteOperador + 1;
                            numerosList.add(txt.substring(anteriorIndice, siguienteOperador));
                            anteriorIndice = siguienteOperador;
                        } else {
                            numerosList.add(txt.substring(anteriorIndice));
                            anteriorIndice = txt.length();
                        }
                    } else {
                        numerosList.add(txt.substring(anteriorIndice, indiceSiguienteOperador));
                        operadoresList.add(String.valueOf(txt.charAt(indiceSiguienteOperador)));
                        anteriorIndice = indiceSiguienteOperador + 1;
                    }
                } else {
                    // Añadir el último operando (número o variable) a la lista
                    numerosList.add(txt.substring(anteriorIndice));
                }

            } while (indiceSiguienteOperador != -1 && anteriorIndice < txt.length());
            numerosList.removeIf(String::isEmpty); // uso esto pq con las expresiones del tipo ^- me guarda un espacio en blanco en la lista de los números

            while (operadoresList.contains("^")) {
                int indicePotencia = operadoresList.lastIndexOf("^");
                String resultado = String.valueOf(Math.pow(operandos_para_MISC_MGEN(numerosList.get(indicePotencia)), operandos_para_MISC_MGEN(numerosList.get(indicePotencia + 1))));
                operadoresList.remove(indicePotencia);
                numerosList.remove(indicePotencia + 1);
                numerosList.remove(indicePotencia);
                numerosList.add(indicePotencia, resultado);
            }

            while (operadoresList.contains("/") || operadoresList.contains("%") || operadoresList.contains("x")) {
                dividePor0 = calcularBarraMultiModulo(numerosList, operadoresList);
            }

            while (operadoresList.contains("+") || operadoresList.contains("-")) {
                calcularMasMenos(numerosList, operadoresList);
            }
            if (!dividePor0) {
                pantalla.setText(df.format(Double.parseDouble(numerosList.get(0))));
            }
        }
    }
    private static boolean dividePor0;
    private static boolean calcularBarraMultiModulo(ArrayList<String> numerosList, ArrayList<String> operadoresList){
        int indiceOperador = Collections.min(Stream.of(
                operadoresList.indexOf("/"),
                operadoresList.indexOf("%"),
                operadoresList.indexOf("x")
        ).filter(indice -> indice != -1).collect(Collectors.toList()));
        String resultado;
        if(operadoresList.get(indiceOperador).equals("/")){
            resultado = String.valueOf(operandos_para_MISC_MGEN(numerosList.get(indiceOperador)) / operandos_para_MISC_MGEN(numerosList.get(indiceOperador+1)));
            if(operandos_para_MISC_MGEN(numerosList.get(indiceOperador+1)) == 0){
                mostrarAlerta_MGEN("Qué haces diviendo por 0 tontorron");
                dividePor0 = true;
            }
        }else if(operadoresList.get(indiceOperador).equals("%")){
            resultado = String.valueOf(operandos_para_MISC_MGEN(numerosList.get(indiceOperador)) % operandos_para_MISC_MGEN(numerosList.get(indiceOperador+1)));
            if(operandos_para_MISC_MGEN(numerosList.get(indiceOperador+1)) == 0){
                mostrarAlerta_MGEN("Qué haces haciendo un modulo por 0 espabilao");
                dividePor0 = true;
            }
        }else {
            resultado = String.valueOf(operandos_para_MISC_MGEN(numerosList.get(indiceOperador)) * operandos_para_MISC_MGEN(numerosList.get(indiceOperador+1)));
        }
        operadoresList.remove(indiceOperador);
        numerosList.remove(indiceOperador+1);
        numerosList.remove(indiceOperador);
        numerosList.add(indiceOperador, resultado);
        return dividePor0;
    }
    private static void calcularMasMenos(ArrayList<String> numerosList, ArrayList<String> operadoresList){
        int indiceOperador = Collections.min(Stream.of(
                operadoresList.indexOf("+"),
                operadoresList.indexOf("-")
        ).filter(indice -> indice != -1).collect(Collectors.toList()));
        String resultado;
        if(operadoresList.get(indiceOperador).equals("+")){
            resultado = String.valueOf(operandos_para_MISC_MGEN(numerosList.get(indiceOperador)) + operandos_para_MISC_MGEN(numerosList.get(indiceOperador+1)));
        }else {
            resultado = String.valueOf(operandos_para_MISC_MGEN(numerosList.get(indiceOperador)) - operandos_para_MISC_MGEN(numerosList.get(indiceOperador+1)));
        }
        operadoresList.remove(indiceOperador);
        numerosList.remove(indiceOperador+1);
        numerosList.remove(indiceOperador);
        numerosList.add(indiceOperador, resultado);
    }
}
