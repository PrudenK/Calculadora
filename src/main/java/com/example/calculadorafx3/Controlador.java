package com.example.calculadorafx3;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Controlador {

    public Controlador() {
        // Agrega caracteres prohibidos al conjunto
        for (char c : caracteresProhibidos) {
            caracteresExcluidos.add(c);
        }
        caracteresExcluidos.add('-');
    }
    private final DecimalFormat df = new DecimalFormat("#.####", new DecimalFormatSymbols(Locale.US));
    private final Pattern  patronCuadradoRaiz = Pattern.compile("^-?\\d+\\.?\\d*$");
    private final Pattern puntoDespuesDelOperador = Pattern.compile("^-?\\d*\\.?\\d*[-+x/%^]\\d+\\..*");
    @FXML
    private Label pantalla;
    private final Set<Character> caracteresExcluidos = new HashSet<>();
    private final char[] caracteresProhibidos = new char[]{'+', 'x', '/', '%','^','√'};
    private final String operadores = "+x/%-^";


    @FXML
    protected void onButton00() {
        String txt = pantalla.getText();
        // pantalla.getText().charAt(pantalla.getText().length()-1) != '0'
        Pattern pattern = Pattern.compile("^-?\\d*\\.?\\d*[-+x/%^]\\d*\\.?\\d*.*");
        if(txt.contains("∞")){
            pantalla.setText("0");
        }else {
            if(!txt.equals("-0") && !txt.equals("0") && !txt.endsWith("π")) {
                if ((pattern.matcher(pantalla.getText()).matches() && pantalla.getText().endsWith("0")) || txt.contains("π") ) {
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
                    if ((puntoDespuesDelOperador.matcher(txt).matches() || !txt.matches("^-?(π|(\\d*\\.?\\d*))[-+x/%^]0.*")) && !txt.startsWith("π")) {
                        pantalla.setText(pantalla.getText() + "00");
                    } else if ((pantalla.getText().contains(".") || contieneNumero) && txt.contains("π")) {
                        pantalla.setText(pantalla.getText() + "00");
                    }else if (!pantalla.getText().endsWith("0")){
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
                    pantalla.setText(pantalla.getText() + "00");
                }
            }
        }

    }
    @FXML
    protected void onButton0() {
        agregarNumero("0");
    }
    @FXML
    protected void onButton1() {
        agregarNumero("1");
    }
    @FXML
    protected void onButton2() {
        agregarNumero("2");
    }
    @FXML
    protected void onButton3() {
        agregarNumero("3");
    }
    @FXML
    protected void onButton4() {
        agregarNumero("4");
    }
    @FXML
    protected void onButton5() {
        agregarNumero("5");
    }
    @FXML
    protected void onButton6() {
        agregarNumero("6");
    }
    @FXML
    protected void onButton7() {
        agregarNumero("7");
    }
    @FXML
    protected void onButton8() {
        agregarNumero("8");
    }
    @FXML
    protected void onButton9() {
        agregarNumero("9");
    }
    @FXML
    protected void onButtonAC() {
        pantalla.setText("0");
    }
    @FXML
    protected void onButtonMasMenos() {
        if(pantalla.getText().isEmpty()){
            pantalla.setText("-");
        }else if(pantalla.getText().charAt(0) == '-'){
            pantalla.setText(pantalla.getText().substring(1));
        }else {
            pantalla.setText("-"+this.pantalla.getText());
        }
    }
    @FXML
    protected void onButtonMas() {
        onOperador("+");
    }
    @FXML
    protected void onButtonX() {
        onOperador("x");
    }
    @FXML
    protected void onButtonBarra() {
        onOperador("/");
    }
    @FXML
    protected void onButtonMenos() {
        onOperador("-");
    }
    @FXML
    protected void onButtonPorcentaje() {
        onOperador("%");
    }
    @FXML
    protected void onPotencia() {
        onOperador("^");
    }
    private void onOperador(String operador){
        boolean menos = primerMenos(pantalla.getText());
        if (!pantalla.getText().endsWith(".") && !menos && contieneCaracteres(pantalla.getText()) && !pantalla.getText().isEmpty() && pantalla.getText().charAt(pantalla.getText().length()-1) != '-'){
            if ((pantalla.getText().charAt(0) != '-' || pantalla.getText().length() >= 2) || !pantalla.getText().equals("-") ) {
                pantalla.setText(pantalla.getText() + operador);
            }
        }
    }
    @FXML
    protected void onButtonPunto(){
        String textoCalc = pantalla.getText();
        boolean ultNumPunto = false;

        if (!textoCalc.isEmpty() && !textoCalc.equals("-") && !caracteresExcluidos.contains(textoCalc.charAt(textoCalc.length() - 1)) && !textoCalc.endsWith("π")) {
            // Verifica si el último número ya tiene un punto
            String[] numeros = textoCalc.split("[+\\-x/^√%]"); //operadores permitidos para poner un punto después
            if (numeros.length > 0) {
                ultNumPunto = numeros[numeros.length - 1].contains(".");
            }
            // Si no hay punto después del último número, agrega el punto
            if (!ultNumPunto) {
                pantalla.setText(textoCalc + ".");
            }
        }
    }
    @FXML
    protected void onButtonIgual() {
        String txt = pantalla.getText();
        boolean menosDelante = false;
        if(txt.charAt(0) == '-'){
            txt = txt.substring(1);
            menosDelante = true;
        }
        if (txt.endsWith("+") || txt.endsWith("-") || txt.endsWith("/") || txt.endsWith("x") || txt.endsWith("%") || txt.endsWith("^")) {
            mostrarAlerta("Introduce el segundo número");
        }else {

            if (txt.equals("π")) {
                if (menosDelante) {
                    pantalla.setText("-3.1415");
                } else {
                    pantalla.setText("3.1415");
                }
            }

            if (txt.contains("+") || txt.contains("/") || txt.contains("%") || txt.contains("x") || txt.contains("-") || txt.contains("^")) {
                double operando1;
                int indiceOperadorInt = indiceOperador(txt);

                if(txt.substring(0, indiceOperadorInt).equals("π")){
                    operando1 = 3.1415;
                }else {
                    operando1 = Double.parseDouble(txt.substring(0, indiceOperadorInt));
                }


                if (menosDelante) {
                    operando1 *= -1;
                }


                double operando2;
                if(txt.substring(indiceOperadorInt + 1).equals("π")){
                    operando2 = 3.1415;
                }else {
                    operando2 = Double.parseDouble(txt.substring(indiceOperadorInt + 1));
                }

                double resultado = 0;
                if (txt.contains("+")) {
                    resultado = operando1 + operando2;
                } else if (txt.contains("x")) {
                    resultado = operando1 * operando2;
                } else if (txt.contains("-")) {
                    resultado = operando1 - operando2;
                } else if (txt.contains("%")) {
                    if (operando2 == 0.0) {
                        mostrarAlerta("No puedes hacer n % 0 maquinón");
                    } else {
                        resultado = operando1 % operando2;
                    }
                } else if (txt.contains("/")) {
                    if (operando2 == 0.0) {
                        mostrarAlerta("No puedes divir por 0 máquina");
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
    private int indiceOperador(String txt){
        return IntStream.range(0, txt.length()).filter(i -> this.operadores.indexOf(txt.charAt(i)) != -1).findFirst().getAsInt();
    }
    private boolean primerMenos(String texto){
        boolean menos;
        if(texto.charAt(0) == '-'){
            menos = texto.substring(1).contains("-");
        }else {
            menos = texto.contains("-");
        }
        return menos;
    }
    private boolean contieneCaracteres(String texto) {
        for (char c : this.caracteresProhibidos) {
            if (texto.indexOf(c) != -1) {
                return false;
            }
        }
        return true;
    }
    private void agregarNumero(String num) {
        if(pantalla.getText().contains("∞")){
            pantalla.setText(num);
        }else {
            Pattern pattern = Pattern.compile("^-?\\d*\\.?\\d*[-+x/%^]0$");
            Pattern patterPi = Pattern.compile("^-?π[-+x/%^]0$");
            Pattern pi = Pattern.compile("^-?π$");
            if (!pi.matcher(pantalla.getText()).matches() && !pantalla.getText().endsWith("π")) {
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
    private void mostrarAlerta (String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setTitle("Error en la entrada de datos");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    @FXML
    protected void onAtras() {
        String atras;
        if(!pantalla.getText().isEmpty()){
            StringBuilder strB = new StringBuilder(pantalla.getText());
            strB.deleteCharAt(pantalla.getText().length()-1);
            atras = String.valueOf(strB);
            pantalla.setText(atras);
        }
    }
    @FXML
    protected void onCuadrado() {
        if(patronCuadradoRaiz.matcher(pantalla.getText()).matches()){
            pantalla.setText(df.format(Math.pow(Double.parseDouble(pantalla.getText()),2)));
        }else if (pantalla.getText().equals("π") || pantalla.getText().equals("-π")) {
            pantalla.setText(df.format(Math.pow(3.1415,2)));
        }
    }
    @FXML
    protected void onRaiz() {
        if(patronCuadradoRaiz.matcher(pantalla.getText()).matches()){
            if(pantalla.getText().contains("-")){
                mostrarAlerta("Alma de cantaro, las raices de indice par de número negativos no existen (bueno si pero...)");
            } else {
                pantalla.setText(df.format(Math.sqrt(Double.parseDouble(pantalla.getText()))));
            }
        }else if (pantalla.getText().equals("π")) {
            pantalla.setText(df.format(Math.sqrt(3.1415)));
        }
    }
    @FXML
    protected void onPi() {
        // Pattern pattern = Pattern.compile("^-?(π|[0-9]+(\\.[0-9]+)?)[-+x/%^](π|[0-9]+(\\.[0-9]+)?)$");
        // Pattern patternPi = Pattern.compile("^-?(π|\\d*\\.?\\d*)[-+x/%^]]$");
        String txt = pantalla.getText();
        int contadorPi = 0;
        for (int i = 0; i < txt.length(); i++) {
            if(txt.charAt(i) == 'π'){
                contadorPi +=1;
            }
        }
        if(txt.contains("∞") || (txt.equals("-0") || txt.equals("-") || txt.equals("0") || txt.isEmpty()) && contadorPi == 0){
            agregarNumero("π");
        } else if ((txt.endsWith("+") || txt.endsWith("-") || txt.endsWith("/") || txt.endsWith("x") || txt.endsWith("%") || txt.endsWith("^")) && contadorPi <= 1) {
            agregarNumero("π");
        }
    }
}