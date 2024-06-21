package com.example.calculadorafx3;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
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
    private final Pattern puntoDespuesDelOperador = Pattern.compile("^-?\\d*\\.?\\d*[-+x√/%^]\\d+\\.$");
    private final Pattern patronDespuesOperadorNumsEspeciales = Pattern.compile("^-?(e|π|φ|(\\d*\\.?\\d*))[-+x/%^][φπe]$");
    private final Pattern patronNumsAntesRaiz = Pattern.compile("^-?(e|π|φ|(\\d*\\.?\\d*))$");
    @FXML protected RadioButton rbX2, rbx, rbc, rbr;
    @FXML protected Label labelx2, labelx1, labelC, labelR, idEcuacionLabel, LabelSolucionX1, LabelSolucionX2 ;
    @FXML protected GridPane cientifica;
    @FXML private Pane ecuaciones2Grado, ecuaciones2Grado2;
    @FXML private Label pantalla;
    @FXML private Text operacionAnteriror;
    private boolean pantallaEcuaciones = false;
    private final Set<Character> caracteresExcluidos = new HashSet<>();
    private final char[] caracteresProhibidos = new char[]{'+', 'x', '/', '%','^','√'};
    private final String operadores = "+x/%-^";
    private static Stage stageCalculadora;
    private double xOffset = 0;
    private double yOffset = 0;
    private ArrayList<String> operandosComplejos = new ArrayList<>();
    private ArrayList<Button> botonesDesactivarEcuaciones = new ArrayList<>();
    @FXML private Button idPorcentaje, idBarra, idPotencia, idMenos, idCuadrado, idMas, idRaiz,idX;
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

        setOperandosComplejos();

        setGrupoBotones();

        setBotonesDesactivarEcuaciones();

    }
    private void setOperandosComplejos(){
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
    private void setBotonesDesactivarEcuaciones(){
        botonesDesactivarEcuaciones.add(idPorcentaje);
        botonesDesactivarEcuaciones.add(idBarra);
        botonesDesactivarEcuaciones.add(idPotencia);
        botonesDesactivarEcuaciones.add(idMenos);
        botonesDesactivarEcuaciones.add(idCuadrado);
        botonesDesactivarEcuaciones.add(idMas);
        botonesDesactivarEcuaciones.add(idRaiz);
        botonesDesactivarEcuaciones.add(idX);
    }
    private void setGrupoBotones(){
        ToggleGroup botonesEcuacionesRadio = new ToggleGroup();
        rbX2.setToggleGroup(botonesEcuacionesRadio);
        rbx.setToggleGroup(botonesEcuacionesRadio);
        rbc.setToggleGroup(botonesEcuacionesRadio);
        rbr.setToggleGroup(botonesEcuacionesRadio);
    }
    private String buscarOperadorComplejo(String txt){
        String cadenaOpComplejo = null;
        for (String subcadena : operandosComplejos) {
            if (txt.contains(subcadena)) {
                cadenaOpComplejo = subcadena;
                break;
            }
        }
        return cadenaOpComplejo;
    }
    @FXML
    protected void onButton00() {
        if(!pantallaEcuaciones) {
            onButton00Ecuaciones(pantalla);
        }else{
            if(rbX2.isSelected()){
                onButton00Ecuaciones(labelx2);
            } else if (rbx.isSelected()) {
                onButton00Ecuaciones(labelx1);
            } else if (rbc.isSelected()) {
                onButton00Ecuaciones(labelC);
            } else if (rbr.isSelected()) {
                onButton00Ecuaciones(labelR);
            }
        }
    }
    private void onButton00Ecuaciones(Label pantalla){
        String txt = pantalla.getText();
        Pattern pattern = Pattern.compile("^-?\\d*\\.?\\d*[-+x/√%^]\\d*\\.?\\d*$");
        String operandoComplejo = buscarOperadorComplejo(txt);
        if(operandoComplejo != null){
            operandosComplejos00(operandoComplejo+")", pantalla);
        }else if(txt.contains("∞")){
            pantalla.setText("0");
        }else {
            if(!txt.equals("-0") && !txt.equals("0") && !txt.endsWith("π") && !txt.endsWith("e") && !txt.endsWith("φ")) {
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
                    if(txt.endsWith("√") || txt.endsWith("√-")){
                        pantalla.setText(pantalla.getText()+"0");
                    }else if ((puntoDespuesDelOperador.matcher(txt).matches() || !txt.matches("^-?(e|π|φ|(\\d*\\.?\\d*))[-+√x/%^]0$"))
                            && !txt.startsWith("π") && !txt.startsWith("e") && !txt.startsWith("φ")) {
                        pantalla.setText(pantalla.getText() + "00");
                    } else if ((pantalla.getText().contains(".") || contieneNumero) && (txt.contains("π") || txt.contains("e") || txt.startsWith("φ"))) {
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
    private void operandosComplejos00(String operando, Label pantalla){
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
        if(!pantallaEcuaciones) {
            onACEcuaciones(pantalla);
        }else{
            if(rbX2.isSelected()){
                onACEcuaciones(labelx2);
            } else if (rbx.isSelected()) {
                onACEcuaciones(labelx1);
            } else if (rbc.isSelected()) {
                onACEcuaciones(labelC);
            } else if (rbr.isSelected()) {
                onACEcuaciones(labelR);
            }
        }

    }
    private void onACEcuaciones(Label pantalla){
        pantalla.setText("0");
    }
    @FXML
    protected void onButtonMasMenos() {
        if(!pantallaEcuaciones) {
            escrbirMasMenosPantalla(pantalla);
        }else{
            if(rbX2.isSelected()){
                escrbirMasMenosPantalla(labelx2);
            } else if (rbx.isSelected()) {
                escrbirMasMenosPantalla(labelx1);
            } else if (rbc.isSelected()) {
                escrbirMasMenosPantalla(labelC);
            } else if (rbr.isSelected()) {
                escrbirMasMenosPantalla(labelR);
            }
        }
    }
    private void escrbirMasMenosPantalla(Label pantalla){
        if(pantalla.getText().isEmpty()){
            pantalla.setText("-");
        }else if(pantalla.getText().charAt(0) == '-'){
            pantalla.setText(pantalla.getText().substring(1));
        }else {
            pantalla.setText("-"+pantalla.getText());
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
        String txt = pantalla.getText();
        String opComlejo = buscarOperadorComplejo(txt);
        if(opComlejo != null && !opComlejo.equals("ln(") && !opComlejo.equals("log10(") && !opComlejo.contains("log(")){
            int indicePrimerParentesis = txt.indexOf("(");
            int indiceUltimoParentesis = txt.indexOf(")");
            if(txt.contains(opComlejo+"-")){
                int indiceUltimoMenos = txt.lastIndexOf("-");
                pantalla.setText(txt.substring(0, indicePrimerParentesis + 1) + txt.substring(indiceUltimoMenos + 1, indiceUltimoParentesis + 1));
            }else {
                pantalla.setText(txt.substring(0, indicePrimerParentesis + 1) + "-" + txt.substring(indicePrimerParentesis + 1, indiceUltimoParentesis + 1));
            }
        } else if (txt.contains("√")) {
            int indiceRaiz = txt.indexOf("√");
            if(txt.contains("√-")){
                pantalla.setText(txt.substring(0,indiceRaiz+1)+txt.substring(indiceRaiz+2));
            }else {
                pantalla.setText(txt.substring(0,indiceRaiz+1)+"-"+txt.substring(indiceRaiz+1));
            }
        }
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
        String opComlejo = buscarOperadorComplejo(pantalla.getText());
        if(!pantalla.getText().isEmpty() && opComlejo == null) {
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
        if(!pantallaEcuaciones) {
            escribirPuntoPantalla(pantalla);
        }else{
            if(rbX2.isSelected()){
                escribirPuntoPantalla(labelx2);
            } else if (rbx.isSelected()) {
                escribirPuntoPantalla(labelx1);
            } else if (rbc.isSelected()) {
                escribirPuntoPantalla(labelC);
            } else if (rbr.isSelected()) {
                escribirPuntoPantalla(labelR);
            }
        }
    }
    private void escribirPuntoPantalla(Label pantalla){
        String txt = pantalla.getText();
        boolean ultNumPunto = false;
        String opComlejo = buscarOperadorComplejo(txt);
        if (opComlejo == null) {
            if (!txt.isEmpty() && !txt.equals("-") && !caracteresExcluidos.contains(txt.charAt(txt.length() - 1))
                    && !txt.endsWith("π") && !txt.endsWith("e")  && !txt.endsWith("φ")) {
                // Verifica si el último número ya tiene un punto
                String[] numeros = txt.split("[+\\-x/^√%]"); //operadores permitidos para poner un punto después
                if (numeros.length > 0) {
                    ultNumPunto = numeros[numeros.length - 1].contains(".");
                }
                // Si no hay punto después del último número, agrega el punto
                if (!ultNumPunto) {
                    pantalla.setText(txt + ".");
                }
            }
        }else {
            if(!pantalla.getText().contains(".") && !pantalla.getText().contains(opComlejo+")") && !pantalla.getText().contains(opComlejo+"e)")
                    && !pantalla.getText().contains(opComlejo+"π)") && !pantalla.getText().contains(opComlejo+"φ)") && !pantalla.getText().contains(opComlejo+"-e)")
                    && !pantalla.getText().contains(opComlejo+"-π)") && !pantalla.getText().contains(opComlejo+"-φ)")){
                int indiceultimoParentesis = pantalla.getText().lastIndexOf(")");
                String cadenaAnterior = pantalla.getText().substring(0, indiceultimoParentesis);
                pantalla.setText(cadenaAnterior + ".)");
            }
        }
    }
    private double devolverValor(String letra){
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
    private boolean contieneLetraExpresion (String cadena){
       return cadena.contains("e") || cadena.contains("π") || cadena.contains("φ");
    }
    private double operandos(String op, boolean menos){
        double opDouble;
        if(contieneLetraExpresion(op)){
            opDouble = devolverValor(op);
        }else {
            opDouble = Double.parseDouble(op);
        }
        if(menos){
            opDouble *= -1;
        }
        return opDouble;
    }
    @FXML
    protected void onButtonIgual() {
        String txt = pantalla.getText();
        if(!pantallaEcuaciones) {
            if (!txt.isEmpty()) {
                operacionAnteriror.setText(txt);
                boolean menosDelante = false;
                if (txt.charAt(0) == '-') {
                    txt = txt.substring(1);
                    menosDelante = true;
                }
                String opComlejo = buscarOperadorComplejo(pantalla.getText());
                if (txt.contains("√")) {
                    boolean menosDentroRaiz = false;
                    int indiceRaiz = txt.indexOf("√");
                    if (txt.contains("-")) {
                        txt = txt.substring(0, indiceRaiz + 1) + txt.substring(indiceRaiz + 2);
                        menosDentroRaiz = true;
                    }
                    String operando1String = txt.split("√")[0];
                    String operando2String = txt.split("√")[1];
                    double operando1, operando2;
                    operando1 = operandos(operando1String, menosDelante);
                    operando2 = operandos(operando2String, false);
                    if (operando1 % 2 == 0 && menosDentroRaiz) {
                        mostrarAlerta("No puedes hacer una raíz de indice par con radicando negativo");
                    } else if (Double.parseDouble(String.valueOf(operando1)) == 0.0) {
                        mostrarAlerta("El indice de la raiz no es 0");
                    } else {
                        double resultado = Math.pow(operando2, 1.0 / operando1);
                        if (menosDentroRaiz) {
                            resultado *= -1;
                        }
                        pantalla.setText(df.format(resultado));
                    }
                } else if (opComlejo != null && !txt.endsWith("()") && !txt.endsWith("(-)")) {
                    boolean menosDelanteOpComplejo = false;
                    boolean errorTangente = false;
                    boolean errorArcos = false;
                    boolean errorLogaritmo = false;
                    int indicePrimerParentesis = txt.indexOf("(");
                    int indiceUltimoParentesis = txt.indexOf(")");
                    double operando;
                    String operandoString = txt.substring(indicePrimerParentesis + 1, indiceUltimoParentesis);
                    if (operandoString.contains("-")) {
                        menosDelanteOpComplejo = true;
                        operandoString = operandoString.substring(1);
                    }
                    operando = operandos(operandoString, menosDelanteOpComplejo);

                    if (txt.contains("ln(")) {
                        if (Double.parseDouble(String.valueOf(operando)) == 0.0) {
                            mostrarAlerta("Fieraaa que no puedes hacer un logarítmo neperiano de 0");
                            errorLogaritmo = true;
                        } else {
                            operando = Math.log(operando);
                        }
                    } else if (txt.contains("en(")) {
                        if (txt.contains("Arc")) {
                            if (operando > 1 || operando < -1) {
                                mostrarAlerta("Jefazooo no puedes calcular un arcoseno de un númoer mayor que 1 o menor que menos 1");
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
                                mostrarAlerta("Jefazooo no puedes calcular un arcoseno de un númoer mayor que 1 o menor que menos 1");
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
                                mostrarAlerta("Chavalíiiin no existe la tangete de un número que sea múltiplo de 90 y no de 180\nTiende a infinito y esas cosas raras");
                            } else {
                                operando = Math.tan(Math.toRadians(operando));
                            }
                        }
                    } else if (txt.contains("log10(")) {
                        if (Double.parseDouble(String.valueOf(operando)) == 0.0) {
                            mostrarAlerta("Fieraaa que no puedes hacer un logarítmo de 0");
                            errorLogaritmo = true;
                        } else {
                            operando = Math.log10(operando);
                        }
                    } else if (txt.contains("log(")) {
                        String base = txt.substring(0, indicePrimerParentesis - 3);
                        if (Double.parseDouble(String.valueOf(operando)) == 0.0) {
                            mostrarAlerta("Fieraaa que no puedes hacer un logarítmo de 0");
                            errorLogaritmo = true;
                        } else if ((menosDelante || Double.parseDouble(base) == 0.0)) {
                            mostrarAlerta("La base del logarítmo no puede ser menor o igual que 0");
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
                } else if (txt.endsWith("+") || txt.endsWith("-") || txt.endsWith("/") || txt.endsWith("x") || txt.endsWith("%") || txt.endsWith("^")) {
                    mostrarAlerta("Introduce el segundo número");
                } else {
                    if (txt.equals("π") || txt.equals("φ") || txt.equals("e")) {
                        pantalla.setText(String.valueOf(operandos(txt, menosDelante)));
                    }
                    if ((txt.contains("+") || txt.contains("/") || txt.contains("%") || txt.contains("x") || txt.contains("-") || txt.contains("^")) && opComlejo == null) {
                        int indiceOperadorInt = indiceOperador(txt);
                        double operando1 = operandos(txt.substring(0, indiceOperadorInt), menosDelante);
                        double operando2 = operandos(txt.substring(indiceOperadorInt + 1), false);
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
        }else {
            calcularEcuaciones();
        }
    }
    private void calcularEcuaciones(){
        double a = operandoEcuaciones(labelx2);
        double b = operandoEcuaciones(labelx1);
        double c = operandoEcuaciones(labelC);
        double r = operandoEcuaciones(labelR);
        double resultado1,resultado2;
        idEcuacionLabel.setText(a+"x² "+b+"x "+c+" = "+r);
        if(a != 0.0) {
            c = c - r;
            double radicando = Math.pow(b, 2) + (-4 * a * c);
            if (radicando < 0) {
                LabelSolucionX1.setText("Sin solución");
                LabelSolucionX2.setText("");
            } else if (radicando == 0) {
                resultado1 = -b / (2 * a);
                LabelSolucionX1.setText("Solución doble");
                LabelSolucionX2.setText("x = " + df.format(resultado1));
            } else {
                resultado1 = (-b + Math.sqrt(radicando)) / (2 * a);
                resultado2 = (-b - Math.sqrt(radicando)) / (2 * a);
                LabelSolucionX1.setText("x₁ = " + df.format(resultado1));
                LabelSolucionX2.setText("x₂ = " + df.format(resultado2));
            }
        }else if (b != 0.0){
            r = r -c;
            LabelSolucionX1.setText("Ecuación de 1ᵉʳ grado");
            LabelSolucionX2.setText("X = "+ df.format(r/b));
        }else {
            mostrarAlerta("A mo a ve, tu eres tonto o q");
        }
    }
    private double operandoEcuaciones(Label pantalla){
        double op = 0;
        if(!pantalla.getText().isEmpty()){
            op = Double.parseDouble(pantalla.getText());
        }
        return op;
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
        if(!pantallaEcuaciones) {
            escribirNumPantalla(num,pantalla);
        }else{
            if(rbX2.isSelected()){
                escribirNumPantalla(num,labelx2);
            } else if (rbx.isSelected()) {
                escribirNumPantalla(num,labelx1);
            } else if (rbc.isSelected()) {
                escribirNumPantalla(num,labelC);
            } else if (rbr.isSelected()) {
                escribirNumPantalla(num,labelR);
            }
        }
    }
    private void escribirNumPantalla(String num, Label pantalla){
        String txt = pantalla.getText();
        String opComlejo = buscarOperadorComplejo(txt);
        if (opComlejo != null) {
            if ((!txt.contains("-e") || !txt.contains("e") || (txt.contains(opComlejo) && !txt.contains(opComlejo + "e)") && !txt.contains(opComlejo + "-e)")))
                    && !txt.contains("π") && !txt.contains("φ") && !txt.contains("-π") && !txt.contains("-φ")) {
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
    private void mostrarAlerta (String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setTitle("Error en la entrada de datos");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    @FXML
    protected void onAtras() {
        if(!pantallaEcuaciones){
            onAtrasEcuaciones(pantalla);
        }else {
            if(rbX2.isSelected()){
                onAtrasEcuaciones(labelx2);
            } else if (rbx.isSelected()) {
                onAtrasEcuaciones(labelx1);
            } else if (rbc.isSelected()) {
                onAtrasEcuaciones(labelC);
            } else if (rbr.isSelected()) {
                onAtrasEcuaciones(labelR);
            }
        }

    }
    private void onAtrasEcuaciones(Label pantalla){
        boolean opSolo = false;
        String txt = pantalla.getText();
        String atras;
        StringBuilder strB = new StringBuilder(pantalla.getText());
        String opComlejo = buscarOperadorComplejo(txt);
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
    @FXML
    protected void onCuadrado() {
        if(patronCuadradoRaiz.matcher(pantalla.getText()).matches()){
            operacionAnteriror.setText(pantalla.getText()+"^2");
            pantalla.setText(df.format(Math.pow(Double.parseDouble(pantalla.getText()),2)));
        }else if (pantalla.getText().equals("π") || pantalla.getText().equals("-π")) {
            operacionAnteriror.setText(pantalla.getText()+"^2");
            pantalla.setText(df.format(Math.pow(3.1415,2)));
        }else if (pantalla.getText().equals("e") || pantalla.getText().equals("-e")) {
            operacionAnteriror.setText(pantalla.getText()+"^2");
            pantalla.setText(df.format(Math.pow(2.7182,2)));
        }else if (pantalla.getText().equals("φ") || pantalla.getText().equals("-φ")) {
            operacionAnteriror.setText(pantalla.getText()+"^2");
            pantalla.setText(df.format(Math.pow(1.618,2)));
        }
    }
    @FXML
    protected void onRaiz() {
        if(patronCuadradoRaiz.matcher(pantalla.getText()).matches()){
            operacionAnteriror.setText("√"+pantalla.getText());
            if(pantalla.getText().contains("-")){
                mostrarAlerta("Alma de cantaro, las raices de indice par de número negativos no existen (bueno si pero...)");
            } else {
                pantalla.setText(df.format(Math.sqrt(Double.parseDouble(pantalla.getText()))));
            }
        }else if (pantalla.getText().equals("π")) {
            operacionAnteriror.setText("√"+pantalla.getText());
            pantalla.setText(df.format(Math.sqrt(3.1415)));
        }else if (pantalla.getText().equals("e")) {
            operacionAnteriror.setText("√"+pantalla.getText());
            pantalla.setText(df.format(Math.sqrt(2.7182)));
        }else if (pantalla.getText().equals("φ")) {
            operacionAnteriror.setText("√"+pantalla.getText());
            pantalla.setText(df.format(Math.sqrt(1.618)));
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
        ajustarStageEcuaciones(false);
        ajustarStageCientifica(true);
        activarBotonesEcuaciones(false);
        pantalla.setPrefWidth(410);
        pantallaEcuaciones = false;
        onDEL();
    }
    @FXML
    protected void onBasica(){
        ajustarStageCientifica(false);
        ajustarStageEcuaciones(false);
        activarBotonesEcuaciones(false);
        pantalla.setPrefWidth(247);
        pantallaEcuaciones = false;

        onDEL();
    }
    @FXML
    protected void onEcuaciones(){
        ajustarStageCientifica(false);
        ajustarStageEcuaciones(true);
        activarBotonesEcuaciones(true);
        pantalla.setPrefWidth(410);
        pantalla.setText("");
        operacionAnteriror.setText("");
        pantallaEcuaciones = true;
        rbX2.setSelected(true);
    }
    private void ajustarStageCientifica(boolean expand) {
        Stage stage = (Stage) cientifica.getScene().getWindow();
        if (expand) {
            cientifica.setVisible(true);
            stage.setWidth(437);
        } else {
            cientifica.setVisible(false);
            stage.setWidth(276);
        }
    }
    private void ajustarStageEcuaciones(boolean expand) {
        Stage stage = (Stage) ecuaciones2Grado.getScene().getWindow();
        if (expand) {
            ecuaciones2Grado.setVisible(true);
            ecuaciones2Grado2.setVisible(true);
            stage.setWidth(437);
        } else {
            ecuaciones2Grado.setVisible(false);
            ecuaciones2Grado2.setVisible(false);
            stage.setWidth(276);
        }
    }
    @FXML
    protected void onFactorial(){
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
        String opComlejo = buscarOperadorComplejo(pantalla.getText());
        int contador = 0;
        for (int i = 0; i < txt.length(); i++) {
            if(txt.charAt(i) == num){
                contador +=1;
            }
        }
        if( (txt.contains("(-0)") || txt.contains("(0)") || txt.contains("(-)") || (txt.contains("()")) && opComlejo != null)
                || txt.endsWith("0") || txt.endsWith("√") || txt.contains("∞") || (txt.equals("-0") || txt.equals("-") || txt.isEmpty()) && contador == 0){
            agregarNumero(String.valueOf(num));
        } else if ((txt.endsWith("0") || txt.endsWith("+") || txt.endsWith("-") || txt.endsWith("/") || txt.endsWith("x") || txt.endsWith("%") || txt.endsWith("^")) && contador <= 1) {
            agregarNumero(String.valueOf(num));
        }
    }
    @FXML
    protected void onNeperiano(){
        agregarOperandoComplejoMenosDelante("ln()");
    }
    @FXML
    protected void onSeno(){
        agregarOperandoComplejoMenosDelante("sen()");
    }
    @FXML
    protected void onCoseno(){
        agregarOperandoComplejoMenosDelante("cos()");
    }
    @FXML
    protected void onTangente(){
        agregarOperandoComplejoMenosDelante("tan()");
    }
    @FXML
    protected void onArcSeno(){
        agregarOperandoComplejoMenosDelante("ArcSen()");
    }
    @FXML
    protected void onArcCos(){
        agregarOperandoComplejoMenosDelante("ArcCos()");
    }
    @FXML
    protected void onArcTan(){
        agregarOperandoComplejoMenosDelante("ArcTan()");
    }
    @FXML
    protected void onLog10(){
        agregarOperandoComplejoMenosDelante("log10()");
    }
    private void agregarOperandoComplejoMenosDelante(String op){
        if(pantalla.getText().startsWith("-")){
            pantalla.setText("-"+op);
        }else {
            pantalla.setText(op);
        }
    }
    @FXML
    protected void onInvertir(){
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
    @FXML
    protected void onInt(){
        if (patronCuadradoRaiz.matcher(pantalla.getText()).matches() && !pantalla.getText().isEmpty() && !pantalla.getText().equals("-")){
            operacionAnteriror.setText("Int("+pantalla.getText()+")");
            String[] value = pantalla.getText().split("\\.");
            pantalla.setText(String.valueOf(value[0]));
        }
    }
    @FXML
    protected void onNumOro(){
        expresionesMatematicas('φ');
    }
    private void agregarExpresionN(String operador){
        String txt = pantalla.getText();
        if(patronNumsAntesRaiz.matcher(txt).matches() && !txt.isEmpty() && !txt.equals("-")){
            pantalla.setText(txt+operador);
        }else {
            mostrarAlerta("Tienes que añadir la base o raíz delante y tiene que ser un parámetro válido");
        }
    }
    @FXML
    protected void onRaizX(){
        agregarExpresionN("√");
    }
    @FXML
    protected void onLogBaseN(){
        agregarExpresionN("log()");
    }
    @FXML
    protected void onDEL() {
        labelx2.setText("0");
        labelx1.setText("0");
        labelC.setText("0");
        labelR.setText("0");
        idEcuacionLabel.setText("");
        LabelSolucionX1.setText("");
        LabelSolucionX2.setText("");
        rbX2.setSelected(true);
    }
    private void activarBotonesEcuaciones(boolean encender){
        for (Button botonesDesactivarEcuacione : botonesDesactivarEcuaciones) {
            botonesDesactivarEcuacione.setDisable(encender);
        }
    }
}