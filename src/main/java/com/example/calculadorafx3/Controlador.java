package com.example.calculadorafx3;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Controlador implements Initializable {
    private final DecimalFormat df = new DecimalFormat("#.####", new DecimalFormatSymbols(Locale.US));
    private final Pattern patronCuadradoRaiz = Pattern.compile("^-?\\d+\\.?\\d*$");
    private final Pattern patronNumEnteroPositivo = Pattern.compile("^\\d+$");
    private final Pattern puntoDespuesDelOperador = Pattern.compile("^-?\\d*\\.?\\d*[-+x/%^]\\d+\\..*");
    @FXML protected GridPane cientifica;
    @FXML
    private Label pantalla;
    private final Set<Character> caracteresExcluidos = new HashSet<>();
    private final char[] caracteresProhibidos = new char[]{'+', 'x', '/', '%','^','√'};
    private final String operadores = "+x/%-^";
    private static Stage stageCalculadora;
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            File registro = new File("registro.txt");
            FileWriter fr = new FileWriter(registro, true);
            Date fechaRegistro = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd 'de' MMMMM 'de' yyyy 'a las' HH:mm:ss",new Locale("es","ES"));
            String fecha = sdf.format(fechaRegistro);

            BufferedReader brContarLineas = new BufferedReader(new FileReader(registro));
            int contador = 0;
            while (brContarLineas.readLine() != null){
                contador +=1;
            }
            String registroF = String.format("Registros -> %-5s Fecha -> %-5s",contador+1,fecha);
            fr.append(registroF).append("\n");
            brContarLineas.close();
            fr.flush();
            fr.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Agrega caracteres prohibidos al conjunto
        for (char c : caracteresProhibidos) {
            caracteresExcluidos.add(c);
        }
        caracteresExcluidos.add('-');
        stageCalculadora = Calcuadora.getStage();
    }

    @FXML
    protected void onButton00() {
        String txt = pantalla.getText();
        // pantalla.getText().charAt(pantalla.getText().length()-1) != '0'
        Pattern pattern = Pattern.compile("^-?\\d*\\.?\\d*[-+x/%^]\\d*\\.?\\d*.*");
        if(txt.contains("∞")){
            pantalla.setText("0");
        }else {
            if(!txt.equals("-0") && !txt.equals("0") && !txt.endsWith("π") && !txt.endsWith("e") ) {
                if ((pattern.matcher(pantalla.getText()).matches() && pantalla.getText().endsWith("0")) || txt.contains("π") || txt.contains("e")) {
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
                    if ((puntoDespuesDelOperador.matcher(txt).matches() || !txt.matches("^-?(e|π|(\\d*\\.?\\d*))[-+x/%^]0.*")) && !txt.startsWith("π") && !txt.startsWith("e")) {
                        pantalla.setText(pantalla.getText() + "00");
                    } else if ((pantalla.getText().contains(".") || contieneNumero) && (txt.contains("π") || txt.contains("e"))) {
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
        if(!pantalla.getText().isEmpty()) {
            boolean menos = primerMenos(pantalla.getText());
            if (!pantalla.getText().endsWith(".") && !menos && contieneCaracteres(pantalla.getText()) && !pantalla.getText().isEmpty() && pantalla.getText().charAt(pantalla.getText().length() - 1) != '-') {
                if ((pantalla.getText().charAt(0) != '-' || pantalla.getText().length() >= 2) || !pantalla.getText().equals("-")) {
                    pantalla.setText(pantalla.getText() + operador);
                }
            }
        }
    }
    @FXML
    protected void onButtonPunto(){
        String textoCalc = pantalla.getText();
        boolean ultNumPunto = false;

        if (!textoCalc.isEmpty() && !textoCalc.equals("-") && !caracteresExcluidos.contains(textoCalc.charAt(textoCalc.length() - 1))
                && !textoCalc.endsWith("π") && !textoCalc.endsWith("e")) {
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
            // todo factorizable quizas
            if (txt.equals("e")) {
                if (menosDelante) {
                    pantalla.setText("-2.7182");
                } else {
                    pantalla.setText("2.7182");
                }
            }

            if (txt.contains("+") || txt.contains("/") || txt.contains("%") || txt.contains("x") || txt.contains("-") || txt.contains("^")) {
                double operando1;
                int indiceOperadorInt = indiceOperador(txt);

                if(txt.substring(0, indiceOperadorInt).equals("π")){
                    operando1 = 3.1415;
                } else if (txt.substring(0, indiceOperadorInt).equals("e")) {
                    operando1 = 2.7182;
                } else {
                    operando1 = Double.parseDouble(txt.substring(0, indiceOperadorInt));
                }


                if (menosDelante) {
                    operando1 *= -1;
                }


                double operando2;
                if(txt.substring(indiceOperadorInt + 1).equals("π")){
                    operando2 = 3.1415;
                } else if (txt.substring(indiceOperadorInt + 1).equals("e")) {
                    operando2 = 2.7182;
                } else {
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
            Pattern patterPi = Pattern.compile("^-?[πe][-+x/%^]0$");
            Pattern pi = Pattern.compile("^-?[πe]$");
            if (!pi.matcher(pantalla.getText()).matches() && (!pantalla.getText().endsWith("π") || !pantalla.getText().endsWith("e"))) {
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
        }else if (pantalla.getText().equals("e") || pantalla.getText().equals("-e")) {
            pantalla.setText(df.format(Math.pow(2.7182,2)));
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
        }else if (pantalla.getText().equals("e")) {
            pantalla.setText(df.format(Math.sqrt(2.7182)));
        }
    }
    @FXML
    protected void onSalir(){
        Platform.exit();
    }
    @FXML
    protected void presionarRaton(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }
    @FXML
    protected void arrastrarRaton(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }

    @FXML
    protected void onCientifica(){
        ajustarStage(true);
        pantalla.setPrefWidth(454);
    }
    @FXML
    protected void onBasica(){
        ajustarStage(false);
        pantalla.setPrefWidth(247);
    }
    private void ajustarStage(boolean expand) {
        Stage stage = (Stage) cientifica.getScene().getWindow();
        if (expand) {
            cientifica.setVisible(true);
            stage.setWidth(483);
        } else {
            cientifica.setVisible(false);
            stage.setWidth(276);
        }
    }
    @FXML
    protected void onFactorial(){
        if(patronNumEnteroPositivo.matcher(pantalla.getText()).matches()){
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
            mostrarAlerta("No se puede aplicar factorial a números negativos, decimales u otras expresiones");
        }
    }
    @FXML
    protected void onButtonE(){
        expresionesMatematicas('e');
    }
    @FXML
    protected void onPi() {
        // Pattern pattern = Pattern.compile("^-?(π|[0-9]+(\\.[0-9]+)?)[-+x/%^](π|[0-9]+(\\.[0-9]+)?)$");
        // Pattern patternPi = Pattern.compile("^-?(π|\\d*\\.?\\d*)[-+x/%^]]$");
        expresionesMatematicas('π');
    }
    private void expresionesMatematicas(char num){
        String txt = pantalla.getText();
        int contador = 0;
        for (int i = 0; i < txt.length(); i++) {
            if(txt.charAt(i) == num){
                contador +=1;
            }
        }
        if(txt.endsWith("0") || txt.contains("∞") || (txt.equals("-0") || txt.equals("-") || txt.isEmpty()) && contador == 0){
            agregarNumero(String.valueOf(num));
        } else if ((txt.endsWith("0") || txt.endsWith("+") || txt.endsWith("-") || txt.endsWith("/") || txt.endsWith("x") || txt.endsWith("%") || txt.endsWith("^")) && contador <= 1) {
            agregarNumero(String.valueOf(num));
        }
    }
}