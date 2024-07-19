package com.example.calculadorafx3.Metodos;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static com.example.calculadorafx3.Metodos.MetodosBotonesPantalla_MBP.agregarNumero_MBP;
import static com.example.calculadorafx3.Metodos.MetodosOperandosComplejos_MOC.*;
public class MetodosGenerales_MGEN extends Atributos{
    public static double devolverValor_MGEN(String letra){
        double operando = 0;
        if (letra.equals("e")) {
            operando = 2.7182;
        } else if (letra.equals("π")) {
            operando = 3.1415;
        } else if (letra.equals("φ")) {
            operando = 1.618;
        }
        return operando;
    }
    public static boolean contieneLetraExpresion_MGEN(String cadena){
        return cadena.contains("e") || cadena.contains("π") || cadena.contains("φ");
    }
    public static double operandos_MGEN(String op, boolean menos){
        double opDouble;
        if(contieneLetraExpresion_MGEN(op)){
            opDouble = devolverValor_MGEN(op);
        }else {
            opDouble = Double.parseDouble(op);
        }
        if(menos){
            opDouble *= -1;
        }
        return opDouble;
    }
    public static void escrbirPunto_MGEN(Label pantalla, String txt, boolean ultNumPunto, String antesTxt, String finalC){
        if (!txt.isEmpty() && !txt.equals("-") && !caracteresExcluidos.contains(txt.charAt(txt.length() - 1))
                && !txt.endsWith("π") && !txt.endsWith("e") && !txt.endsWith("φ") && !txt.endsWith("(")) {
            // Verifica si el último número ya tiene un punto
            String[] numeros = txt.split("[+\\-x/^√%]"); //operadores permitidos para poner un punto después
            if (numeros.length > 0) {
                ultNumPunto = numeros[numeros.length - 1].contains(".");
            }
            // Si no hay punto después del último número, agrega el punto
            if (!ultNumPunto) {
                pantalla.setText(antesTxt + txt + finalC);
            }
        }
    }
    public static void escribirNumPantallaFunciones_MGEN(Label pantalla, String num, String txt, boolean condicon, String cadena1, String cadena2){
        if((num.equals("e") || num.equals("π") || num.equals("φ"))){
            if (txt.endsWith("^") || txt.endsWith("-") || txt.endsWith("+") || txt.isEmpty() || txt.endsWith("0") || txt.endsWith("(") || condicon) {
                if(txt.endsWith("0")){
                    if(!txt.matches(".*[1-9]\\.?\\d*0$") && txt.endsWith("0") && !txt.matches(".*0\\.?\\d*0$")){
                        pantalla.setText(cadena1);
                    }
                } else {
                    pantalla.setText(cadena2);
                }
            }
        }else {
            if(!(txt.endsWith("e") || txt.endsWith("π") || txt.endsWith("φ")) || num.equals("x")){
                if(!txt.matches(".*[1-9]\\.?\\d*0$") && !txt.matches(".*0\\.?\\d*0$") && txt.endsWith("0")){
                    pantalla.setText(cadena1);
                }else {
                    pantalla.setText(cadena2);
                }
            }
        }
    }
    public static void mostrarAlerta_MGEN(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setTitle("Error en la entrada de datos");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    public static int indiceOperador_MGEN(String txt){
        return IntStream.range(0, txt.length()).filter(i -> "+x/%-^".indexOf(txt.charAt(i)) != -1).findFirst().getAsInt();
    }
    // Metodo para cuando hay un menos delante y añadimos un operador complejo
    public static void escribirNumPantalla_MGEN(String num, Label pantalla, boolean pantallaFuncionesBolean) {
        String txt = pantalla.getText();
        String opComlejo = buscarOperadorComplejo_MOC(txt);
        if (!((txt.endsWith("x") || txt.endsWith("x)")) && pantallaFuncionesBolean)) {
            if (!(pantallaFuncionesBolean && txt.endsWith(")") && opComlejo == null)) {
                if (pantallaFuncionesBolean) {
                    if(opComlejo != null){
                        String auxTxt = txt.substring(txt.indexOf("(") + 1, txt.indexOf(")"));
                        String cadena1 = txt.substring(0, txt.indexOf(")")-1) + num + ")";
                        String cadena2 = txt.substring(0, txt.indexOf(")")) + num + ")";
                        escribirNumPantallaFunciones_MGEN(pantalla,num,auxTxt,txt.contains("TengoQuePonerAlgoXD"),cadena1,cadena2);
                    }else {
                        String cadena1;
                        if(txt.isEmpty()){
                            cadena1 = num;
                        }else {
                            cadena1 = txt.substring(0, txt.length() - 1) + num;
                        }
                        String cadena2 = txt + num;
                        escribirNumPantallaFunciones_MGEN(pantalla,num,txt,txt.endsWith("/") || txt.endsWith("√"),cadena1,cadena2);
                    }
                } else if (opComlejo != null) {
                    if (((!txt.contains("-e") || !txt.contains("e") || (txt.contains(opComlejo) && !txt.contains(opComlejo + "e)") && !txt.contains(opComlejo + "-e)")))
                            && !txt.contains("π") && !txt.contains("φ") && !txt.contains("-π") && !txt.contains("-φ")) || pantallaFuncionesBolean) {
                        int indiceultimoParentesis = txt.lastIndexOf(")");
                        String cadenaAnterior = txt.substring(0, indiceultimoParentesis);
                        if (txt.contains("(0)") || txt.contains("(-0)")) {
                            pantalla.setText(cadenaAnterior.substring(0, cadenaAnterior.length() - 1) + num + ")");
                        } else {
                            pantalla.setText(cadenaAnterior + num + ")");
                        }
                    }
                } else if (txt.contains("∞")) {
                    pantalla.setText(num);
                } else {
                    Pattern pattern = Pattern.compile("^-?\\d*\\.?\\d*[-+x/%^√]-?0$");
                    Pattern patterPi = Pattern.compile("^-?[φπe][-+x/%^√]-?0$");
                    Pattern pi = Pattern.compile("^-?[φπe]$");
                    Pattern piRaiz = Pattern.compile("^-?(e|π|φ|(\\d*\\.?\\d*))√-?[φπe]$");
                    if (!pi.matcher(pantalla.getText()).matches() && !patronDespuesOperadorNumsEspeciales.matcher(txt).matches() && !piRaiz.matcher(txt).matches() &&
                            (!pantalla.getText().endsWith("π") || !pantalla.getText().endsWith("e") || !pantalla.getText().endsWith("φ"))) {
                        if (pantalla.getText().equals("0")) {
                            pantalla.setText(num);
                        } else if (pantalla.getText().equals("-0")) {
                            pantalla.setText("-" + num);
                        } else if (pattern.matcher(pantalla.getText()).matches() || patterPi.matcher(pantalla.getText()).matches()) {
                            pantalla.setText(pantalla.getText().substring(0, pantalla.getText().length() - 1) + num);
                        } else {
                            pantalla.setText(pantalla.getText() + num);
                        }
                    }
                }
            }
        }
    }
    public static void cuadrado_MGEN() {
        String txt = pantalla.getText();
        if(patronCuadradoRaiz.matcher(txt).matches()){
            operacionAnteriror.setText(txt+"^2");
            pantalla.setText(df.format(Math.pow(Double.parseDouble(txt),2)));
        }else if (txt.equals("π") || txt.equals("-π")) {
            operacionAnteriror.setText(txt+"^2");
            pantalla.setText(df.format(Math.pow(3.1415,2)));
        }else if (txt.equals("e") || txt.equals("-e")) {
            operacionAnteriror.setText(txt+"^2");
            pantalla.setText(df.format(Math.pow(2.7182,2)));
        }else if (txt.equals("φ") || txt.equals("-φ")) {
            operacionAnteriror.setText(txt+"^2");
            pantalla.setText(df.format(Math.pow(1.618,2)));
        }
    }
    public static void raiz_MGEN(){
        String txt = pantalla.getText();
        if(patronCuadradoRaiz.matcher(txt).matches()){
            operacionAnteriror.setText("√"+txt);
            if(txt.contains("-")){
                mostrarAlerta_MGEN("Alma de cantaro, las raices de indice par de número negativos no existen (bueno si pero...)");
            } else {
                pantalla.setText(df.format(Math.sqrt(Double.parseDouble(txt))));
            }
        }else if (txt.equals("π")) {
            operacionAnteriror.setText("√"+txt);
            pantalla.setText(df.format(Math.sqrt(3.1415)));
        }else if (txt.equals("e")) {
            operacionAnteriror.setText("√"+txt);
            pantalla.setText(df.format(Math.sqrt(2.7182)));
        }else if (txt.equals("φ")) {
            operacionAnteriror.setText("√"+txt);
            pantalla.setText(df.format(Math.sqrt(1.618)));
        }
    }

    public static void onInvertir_MGEN(){
        operacionAnteriror.setText(pantalla.getText()+"^-1");
        if(patronCuadradoRaiz.matcher(pantalla.getText()).matches()){
            pantalla.setText(df.format(Math.pow(Double.parseDouble(pantalla.getText()),-1)));
        }else if (pantalla.getText().equals("π") || pantalla.getText().equals("-π")) {
            if(pantalla.getText().equals("-π")){
                pantalla.setText(df.format(Math.pow(-3.1415, -1)));
            }else {
                pantalla.setText(df.format(Math.pow(3.1415, -1)));
            }
        }else if (pantalla.getText().equals("e") || pantalla.getText().equals("-e")) {
            if(pantalla.getText().equals("-e")){
                pantalla.setText(df.format(Math.pow(-2.7182,-1)));
            }else {
                pantalla.setText(df.format(Math.pow(2.7182, -1)));
            }
        }else if (pantalla.getText().equals("φ") || pantalla.getText().equals("-φ")) {
            if(pantalla.getText().equals("-φ")){
                pantalla.setText(df.format(Math.pow(-1.618,-1)));
            }else {
                pantalla.setText(df.format(Math.pow(1.618, -1)));
            }
        }
    }
    public static void onFactorial_MGEN(){
        if(patronNumEnteroPositivo.matcher(pantalla.getText()).matches()){
            operacionAnteriror.setText("!"+pantalla.getText());
            int num = Integer.parseInt(pantalla.getText());
            if(num > 25){
                pantalla.setText("∞");
            }else {
                long resultado = 1;
                for (long i = 1; i < num + 1; i++) {
                    resultado *= i;
                }
                pantalla.setText(String.valueOf(resultado));
            }
        }else {
            mostrarAlerta_MGEN("No se puede aplicar factorial a números negativos, decimales u otras expresiones");
        }
    }
    public static void onINT_MGEN(){
        if (patronCuadradoRaiz.matcher(pantalla.getText()).matches() && !pantalla.getText().isEmpty() && !pantalla.getText().equals("-")){
            operacionAnteriror.setText("Int("+pantalla.getText()+")");
            String[] value = pantalla.getText().split("\\.");
            pantalla.setText(String.valueOf(value[0]));
        }
    }
    public static void onDel_MGEN(){
        labelx2.setText("0");
        labelx1.setText("0");
        labelC.setText("0");
        labelR.setText("0");
        idEcuacionLabel.setText("");
        LabelSolucionX1.setText("");
        LabelSolucionX2.setText("");
        rbX2.setSelected(true);
    }
    public static void escribirExpresionMatematicaLabel_MGEN(Label pantalla, char num, boolean pantallaFuncionesBolean, boolean pantallaEcuacionesBolean, boolean escribirDentroFunB){
        String txt = pantalla.getText();
        String opComlejo = buscarOperadorComplejo_MOC(pantalla.getText());
        int contador = 0;
        for (int i = 0; i < txt.length(); i++) {
            if(txt.charAt(i) == num){
                contador +=1;
            }
        }
        if(pantallaFuncionesBolean){
            agregarNumero_MBP(String.valueOf(num), true, pantallaEcuacionesBolean, escribirDentroFunB);
        }else if( (txt.contains("(-0)") || txt.contains("(0)") || txt.contains("(-)") || (txt.contains("()")) && opComlejo != null)
                || txt.endsWith("0") || txt.endsWith("√") || txt.contains("∞") || (txt.equals("-0") || txt.equals("-") || txt.isEmpty()) && contador == 0){
            agregarNumero_MBP(String.valueOf(num), false, pantallaEcuacionesBolean, escribirDentroFunB);
        } else if ((txt.endsWith("0") || txt.endsWith("+") || txt.endsWith("-") || txt.endsWith("/") || txt.endsWith("x") || txt.endsWith("%") || txt.endsWith("^")) && contador <= 1) {
            agregarNumero_MBP(String.valueOf(num), false, pantallaEcuacionesBolean, escribirDentroFunB);
        }
    }


}
