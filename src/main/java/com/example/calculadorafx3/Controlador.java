package com.example.calculadorafx3;

import com.example.calculadorafx3.Operaciones.OperacionesMath;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;


/*
todo, revisar 3x^-3
todo, operadores dentro del sen, cos ...
 */
public class Controlador implements Initializable {
    private final DecimalFormat df = new DecimalFormat("#.####", new DecimalFormatSymbols(Locale.US));
    private final Pattern patronCuadradoRaiz = Pattern.compile("^-?\\d+\\.?\\d*$");
    private final Pattern patronNumEnteroPositivo = Pattern.compile("^\\d+$");
    private final Pattern puntoDespuesDelOperador = Pattern.compile("^-?\\d*\\.?\\d*[-+x√/%^]\\d+\\.$");
    private final Pattern patronDespuesOperadorNumsEspeciales = Pattern.compile("^-?(e|π|φ|(\\d*\\.?\\d*))[-+x/%^][φπe]$");
    private final Pattern patronNumsAntesRaiz = Pattern.compile("^-?(e|π|φ|(\\d*\\.?\\d*))$");
    @FXML protected RadioButton rbX2, rbx, rbc, rbr;
    @FXML protected Label labelx2, labelx1, labelC, labelR, idEcuacionLabel, LabelSolucionX1, LabelSolucionX2 ;
    @FXML protected GridPane cientifica, gridPaneBasica, gridPaneEcuaciones;
    @FXML private Pane ecuaciones2Grado, ecuaciones2Grado2, funcionesPane, gridPaneFunciones, idPanePantallaFunciones;
    @FXML private Label pantalla, pantallaFunciones, operadorLabelFun, desplazamientoYtrigoFun;
    @FXML private Text operacionAnteriror;
    @FXML private Canvas canvasFunciones;
    private double scale = 30;
    private boolean canvasActivo = false;
    private boolean arcFun =false; // boolean para cambiar botones en fun (trigo)
    private boolean pantallaFuncionesBolean = false;
    private boolean pantallaEcuaciones = false;
    private boolean escribirDentroFunB = true;
    private final Set<Character> caracteresExcluidos = new HashSet<>();
    private final char[] caracteresProhibidos = new char[]{'+', 'x', '/', '%','^','√'};
    private final char[] caracteresProhibidosFunciones = new char[]{'+', '/', '%'};
    private final String operadores = "+x/%-^";
    private static Stage stageCalculadora;
    private double xOffset = 0;
    private double yOffset = 0;
    private ArrayList<String> operandosComplejos = new ArrayList<>();
    private ArrayList<Button> botonesDesactivarEcuaciones = new ArrayList<>();
    @FXML private Button idPorcentaje, idBarra, idPotencia, idMenos, idCuadrado, idMas, idRaiz,idX, escribirDentroFun, escribirFueraFun, multiplicandoEspecialFun, senFun, cosFun, tanFun;
    private double offsetX = 0;
    private double offsetY = 0;
    private double dragStartX;
    private double dragStartY;
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

        rbX2.addEventFilter(KeyEvent.KEY_PRESSED, event -> pasarAlSiguiente(event, rbx));
        rbx.addEventFilter(KeyEvent.KEY_PRESSED, event -> pasarAlSiguiente(event, rbc));
        rbc.addEventFilter(KeyEvent.KEY_PRESSED, event -> pasarAlSiguiente(event, rbr));
        rbr.addEventFilter(KeyEvent.KEY_PRESSED, event -> pasarAlSiguiente(event, rbX2));

        solucionarIgualEcuacionesTeclado(rbX2);
        solucionarIgualEcuacionesTeclado(rbx);
        solucionarIgualEcuacionesTeclado(rbc);
        solucionarIgualEcuacionesTeclado(rbr);

        deshabilitarBotonesTabulador(cientifica);
        deshabilitarBotonesTabulador(gridPaneBasica);
        deshabilitarBotonesTabulador(gridPaneEcuaciones);

        canvasFunciones.setOnMousePressed(this::clickCanvas);
        canvasFunciones.setOnMouseDragged(this::arrastrarCanvas);
        canvasFunciones.setOnScroll(this::scrollCanvas);


        pantallaFunciones.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                pantallaFunciones.setText(newValue);
                if (buscarOperadorComplejo(newValue) != null) {
                    escribirFueraFun.setDisable(false);
                    if(desplazamientoYtrigoFun.getText().isEmpty()){
                        operadorLabelFun.setText("+");
                        desplazamientoYtrigoFun.setText("0");
                    }
                } else {
                    multiplicandoEspecialFun.setDisable(true);
                    escribirDentroFun.setDisable(true);
                    escribirFueraFun.setDisable(true);
                    operadorLabelFun.setText("");
                    desplazamientoYtrigoFun.setText("");
                }
            }
        });
    }
    private void pasarAlSiguiente(KeyEvent event, RadioButton next) {
        if (event.getCode() == KeyCode.TAB) {
            event.consume();  // Prevenir el comportamiento por defecto del tabulador
            next.requestFocus();
            next.setSelected(true);
        }
    }
    private void deshabilitarBotonesTabulador(GridPane gridPane) {
        gridPane.getChildren().forEach(node -> {
            if (node instanceof Button) {
                ((Button) node).setFocusTraversable(false);
            }
        });
    }
    private void solucionarIgualEcuacionesTeclado(RadioButton radioButton) {
        radioButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onButtonIgual();
            }
        });
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
        if(pantallaFuncionesBolean){
            onButton00Pantalla(pantallaFunciones);
        }else if(!pantallaEcuaciones) {
            onButton00Pantalla(pantalla);
        }else{
            if(rbX2.isSelected()){
                onButton00Pantalla(labelx2);
            } else if (rbx.isSelected()) {
                onButton00Pantalla(labelx1);
            } else if (rbc.isSelected()) {
                onButton00Pantalla(labelC);
            } else if (rbr.isSelected()) {
                onButton00Pantalla(labelR);
            }
        }
    }
    private void onButton00Pantalla(Label pantalla){
        String txt = pantalla.getText();
        Pattern pattern = Pattern.compile("^-?\\d*\\.?\\d*[-+x/√%^]\\d*\\.?\\d*$");
        String operandoComplejo = buscarOperadorComplejo(txt);
        if(!(pantallaFuncionesBolean && txt.endsWith("x"))){
            if(!(pantallaFuncionesBolean && txt.endsWith(")") && operandoComplejo == null)) {
                if (operandoComplejo != null) {
                    if(pantallaFuncionesBolean){
                        operandosComplejos00Fun(pantalla);
                    }else {
                        operandosComplejos00(operandoComplejo + ")", pantalla);
                    } // todo, doble 00 para fun
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
    private void operandosComplejos00Fun(Label pantalla){
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
    public void pasarEscena(Scene scene) {
        scene.setOnKeyPressed(this::escribirUsandoTeclas);
    }
    private void escribirUsandoTeclas(KeyEvent event) {
        if ((event.getCode() == KeyCode.DIGIT5 || event.getCode() == KeyCode.NUMPAD5) && event.isShiftDown()) {
            onButtonPorcentaje();
        }else if ((event.getCode() == KeyCode.DIGIT8 || event.getCode() == KeyCode.NUMPAD8) && event.isShiftDown()){
            onAbrirParentesis();
        }else if((event.getCode() == KeyCode.DIGIT9 || event.getCode() == KeyCode.NUMPAD9) && event.isShiftDown()) {
            onCerrarParentesis();
        }else if (event.getCode() == KeyCode.X) {
            if(pantallaFuncionesBolean){
                onButtonXFunciones();
            }else {
                onButtonX();
            }
        } else if ((event.getCode() == KeyCode.DIGIT7 || event.getCode() == KeyCode.NUMPAD7) && event.isShiftDown()) {
            onButtonBarra();
        } else if (event.getCode() == KeyCode.DIGIT0 || event.getCode() == KeyCode.NUMPAD0) {
            onButton0();
        } else if (event.getCode() == KeyCode.DIGIT1 || event.getCode() == KeyCode.NUMPAD1) {
            onButton1();
        } else if (event.getCode() == KeyCode.DIGIT2 || event.getCode() == KeyCode.NUMPAD2) {
            onButton2();
        } else if (event.getCode() == KeyCode.DIGIT3 || event.getCode() == KeyCode.NUMPAD3) {
            onButton3();
        } else if (event.getCode() == KeyCode.DIGIT4 || event.getCode() == KeyCode.NUMPAD4) {
            onButton4();
        } else if (event.getCode() == KeyCode.DIGIT5 || event.getCode() == KeyCode.NUMPAD5) {
            onButton5();
        } else if (event.getCode() == KeyCode.DIGIT6 || event.getCode() == KeyCode.NUMPAD6) {
            onButton6();
        } else if (event.getCode() == KeyCode.DIGIT7 || event.getCode() == KeyCode.NUMPAD7) {
            onButton7();
        } else if (event.getCode() == KeyCode.DIGIT8 || event.getCode() == KeyCode.NUMPAD8) {
            onButton8();
        } else if (event.getCode() == KeyCode.DIGIT9 || event.getCode() == KeyCode.NUMPAD9) {
            onButton9();
        } else if (event.getCode() == KeyCode.BACK_SPACE) {
            onAtras();
        } else if (event.getCode() == KeyCode.PLUS || (event.getCode() == KeyCode.EQUALS && event.isShiftDown())) {
            onButtonMas();
        } else if (event.getCode() == KeyCode.MINUS) {
            if(!pantallaEcuaciones) {
                onButtonMenos();
            }else {
                onButtonMasMenos();
            }
        } else if (event.getCode() == KeyCode.PERIOD || event.getCode() == KeyCode.DECIMAL) {
            onButtonPunto();
        } else if (event.getCode() == KeyCode.E) {
            onButtonE();
        }
    }
    @FXML
    protected void onButtonAC() {
        if (pantallaFuncionesBolean){
            onACEcuaciones(pantallaFunciones);
            escribirDentroFunB = true;
        }else if(!pantallaEcuaciones) {
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
        if (pantallaFuncionesBolean){
            if(escribirDentroFunB){
                escrbirMasMenosPantalla(pantallaFunciones);
            }else {
                if(operadorLabelFun.getText().equals("-")){
                    operadorLabelFun.setText("+");
                }else {
                    operadorLabelFun.setText("-");
                }
            }
        }else if(!pantallaEcuaciones) {
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
    @FXML
    protected void onButtonPorcentaje() {
        onOperador("%");
    }
    @FXML
    protected void onPotencia() {
        onOperador("^");
    }
    private void onOperador(String operador){
        if(pantallaFuncionesBolean){
            onOperadorPantalla(pantallaFunciones,operador);
        }else {
            onOperadorPantalla(pantalla,operador);
        }
    }
    private void onOperadorPantalla(Label pantalla, String operador){
        if(!pantallaFuncionesBolean || escribirDentroFunB) {
            String txt = pantalla.getText();
            String opComlejo = buscarOperadorComplejo(txt);
            if((txt.isEmpty() || txt.equals("-")) && operador.equals("-")){
                onButtonMasMenos();
            }else if (!txt.isEmpty() && opComlejo == null) {
                boolean menos = primerMenos(txt);
                if ((!txt.endsWith(".") && !menos && contieneCaracteres(txt) && txt.charAt(txt.length() - 1) != '-') ||
                        ((pantallaFuncionesBolean && ((txt.endsWith(")") || (txt.contains("^") && !txt.endsWith("^")) && !txt.endsWith("+") && !txt.endsWith("-") && !txt.endsWith("/") && endsWithSpecial(txt, operador)) && !(txt.contains("^") && operador.equals("^")))))
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
                                if (operador.equals("+")) {
                                    if (!txt.endsWith("(") && !txt.endsWith(")") && txt.contains("x")) {
                                        pantalla.setText(txt + operador);
                                    } else if (txt.endsWith(")") && (txt.contains("/") || txt.contains("√"))) {
                                        pantalla.setText(txt + operador);
                                    }
                                } else if (operador.equals("-")) { // todo, multiplicidad del menos
                                    if ((!txt.endsWith(")") && !txt.endsWith(".") && txt.contains("x")) || txt.endsWith("/") || txt.endsWith("(")) {
                                        pantalla.setText(txt + operador);
                                    } else if (txt.endsWith(")") && (txt.contains("/") || txt.contains("√"))) {
                                        pantalla.setText(txt + operador);
                                    }
                                } else {
                                    if(!txt.contains("√") && !txt.endsWith(".")){
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
                    if (endsWithSpecial(txt, operador) && !operador.equals("^")) {
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
    private boolean endsWithSpecial(String str, String op) {
        return !Pattern.compile(".*\\^-?(\\d+\\.?\\d*|π|e|φ)[+-](\\d+\\.?\\d*|π|e|φ|$)").matcher(str).matches() && op.matches("^[+^-]$");
    }
    private boolean noTerminaEnOperador(String txt){
        return !txt.endsWith("+") && !txt.endsWith("-") && !txt.endsWith("^") && !txt.endsWith("/");
    }
    @FXML
    protected void onButtonPunto(){
        if (pantallaFuncionesBolean){
            if(escribirDentroFunB) {
                escribirPuntoPantalla(pantallaFunciones);
            }else {
                escribirPuntoPantalla(desplazamientoYtrigoFun);
            }
        }else if(!pantallaEcuaciones) {
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
        if (opComlejo == null ) {
            if(!(pantallaFuncionesBolean && txt.endsWith(")"))) {
                if (!txt.isEmpty() && !txt.equals("-") && !caracteresExcluidos.contains(txt.charAt(txt.length() - 1))
                        && !txt.endsWith("π") && !txt.endsWith("e") && !txt.endsWith("φ")) {
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
            }
        }else {
            if(pantallaFuncionesBolean){
                // todo, factorizar
                String antesTxt = txt.substring(0,txt.indexOf("(")+1);
                txt = txt.substring(txt.indexOf("(")+1, txt.length()-1);
                if (!txt.isEmpty() && !txt.equals("-") && !caracteresExcluidos.contains(txt.charAt(txt.length() - 1))
                        && !txt.endsWith("π") && !txt.endsWith("e") && !txt.endsWith("φ")) {
                    // Verifica si el último número ya tiene un punto
                    String[] numeros = txt.split("[+\\-x/^√%]"); //operadores permitidos para poner un punto después
                    if (numeros.length > 0) {
                        ultNumPunto = numeros[numeros.length - 1].contains(".");
                    }
                    // Si no hay punto después del último número, agrega el punto
                    if (!ultNumPunto) {
                        pantalla.setText(antesTxt + txt + ".)");
                    }
                }
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
        if(!pantallaFuncionesBolean) {
            if (!pantallaEcuaciones) {
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
            } else {
                calcularEcuaciones();
            }
        }else {
            dibujarGrafico();
        }
    }
    private void calcularEcuaciones(){
        double a = operandoEcuaciones(labelx2);
        double b = operandoEcuaciones(labelx1);
        double c = operandoEcuaciones(labelC);
        double r = operandoEcuaciones(labelR);
        double resultado1,resultado2;
        String labelx1S = labelx1.getText();
        String labelCs = labelC.getText();
        if(!labelx1S.startsWith("-")){
            labelx1S = "+"+labelx1S;
        }
        if(!labelCs.startsWith("-")){
            labelCs = "+"+labelCs;
        }
        idEcuacionLabel.setText(labelx2.getText()+"x² "+labelx1S+"x "+labelCs+" = "+labelR.getText());
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
        if(contieneLetraExpresion(pantalla.getText())){
            if(pantalla.getText().startsWith("-")){
                op = devolverValor(pantalla.getText().substring(1));
                op *=-1;
            }else {
                op = devolverValor(pantalla.getText());
            }
        }else if(!pantalla.getText().isEmpty()){
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
        if(pantallaFuncionesBolean){
            return contieneCaracteresPantalla(texto, caracteresProhibidosFunciones);
        }else {
            return contieneCaracteresPantalla(texto, caracteresProhibidos);
        }
    }
    private boolean contieneCaracteresPantalla(String texto,char[] listaCaracteres) {
        for (char c : listaCaracteres) {
            if (texto.indexOf(c) != -1) {
                return false;
            }
        }
        return true;
    }
    private void agregarNumero(String num) {
        if(pantallaFuncionesBolean){
            if(escribirDentroFunB){
                escribirNumPantalla(num, pantallaFunciones);
            }else {
                escribirNumPantalla(num, desplazamientoYtrigoFun);
            }
        }else if(!pantallaEcuaciones) {
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
    private void escribirNumPantalla(String num, Label pantalla) {
        String txt = pantalla.getText();
        String opComlejo = buscarOperadorComplejo(txt);
        if (!((txt.endsWith("x") || txt.endsWith("x)")) && pantallaFuncionesBolean)) {
            if (!(pantallaFuncionesBolean && txt.endsWith(")") && opComlejo == null)) {
                if (pantallaFuncionesBolean) {
                    if(opComlejo != null){
                        // todo, función
                        String auxTxt = txt.substring(txt.indexOf("(") + 1, txt.indexOf(")"));
                        if((num.equals("e") || num.equals("π") || num.equals("φ"))){
                            if (auxTxt.endsWith("^") || auxTxt.endsWith("-") || auxTxt.endsWith("+") || auxTxt.isEmpty() || auxTxt.endsWith("0")) {
                                if(auxTxt.endsWith("0")){
                                    if(!auxTxt.matches(".*[1-9]\\.?\\d*0$") && auxTxt.endsWith("0") && !auxTxt.matches(".*0\\.?\\d*0$")){
                                        pantalla.setText(txt.substring(0, txt.indexOf(")")-1) + num + ")");
                                    }
                                } else {
                                    pantalla.setText(txt.substring(0, txt.indexOf(")")) + num + ")");
                                }
                            }
                        }else {
                            if(!(auxTxt.endsWith("e") || auxTxt.endsWith("π") || auxTxt.endsWith("φ"))){
                                if( !auxTxt.matches(".*[1-9]\\.?\\d*0$") && !auxTxt.matches(".*0\\.?\\d*0$") && auxTxt.endsWith("0")){
                                    pantalla.setText(txt.substring(0, txt.indexOf(")")-1) + num + ")");
                                }else {
                                    pantalla.setText(txt.substring(0, txt.indexOf(")")) + num + ")");
                                }
                            }
                        }
                    }else {
                        if((num.equals("e") || num.equals("π") || num.equals("φ"))){
                            if (txt.endsWith("^") || txt.endsWith("-") || txt.endsWith("+") || txt.endsWith("/") || txt.isEmpty() || txt.endsWith("0") || txt.endsWith("√")) {
                                if(txt.endsWith("0")){
                                    if(!txt.matches(".*[1-9]\\.?\\d*0$") && txt.endsWith("0") && !txt.matches(".*0\\.?\\d*0$")){
                                        pantalla.setText(txt.substring(0, txt.length()-1) + num);                                    }
                                } else {
                                    pantalla.setText(txt+num);
                                }
                            }
                        }else {
                            if(!(txt.endsWith("e") || txt.endsWith("π") || txt.endsWith("φ"))) {
                                if (!txt.matches(".*[1-9]\\.?\\d*0$") && !txt.matches(".*0\\.?\\d*0$") && txt.endsWith("0")) {
                                    pantalla.setText(txt.substring(0, txt.length() - 1) + num);
                                } else {
                                    pantalla.setText(txt + num);
                                }
                            }
                        }
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

    private void mostrarAlerta (String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setTitle("Error en la entrada de datos");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    @FXML
    protected void onAtras() {
        if (pantallaFuncionesBolean){
            if(escribirDentroFunB){
                onAtrasEcuaciones(pantallaFunciones);
            }else {
                onAtrasEcuaciones(desplazamientoYtrigoFun);
            }
        }else if(!pantallaEcuaciones){
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
        if (event.getButton() == MouseButton.PRIMARY) { //añado esto para poder arrastrar con el derecho en el canvas
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        }
    }
    @FXML
    protected void arrastrarRaton(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        }
    }

    @FXML
    protected void onCientifica(){
        ajustarStageEcuaciones(false);
        ajustarStageFunciones(false);
        ajustarStageCientifica(true);
        activarBotonesEcuaciones(false);
        pantalla.setPrefWidth(410);
        pantallaEcuaciones = false;
        canvasActivo = false;
        pantallaFuncionesBolean = false;
        onDEL();
    }
    @FXML
    protected void onBasica(){
        ajustarStageCientifica(false);
        ajustarStageEcuaciones(false);
        ajustarStageFunciones(false);
        activarBotonesEcuaciones(false);
        ajustarStageBasica(true);
        pantalla.setPrefWidth(247);
        pantallaEcuaciones = false;
        canvasActivo = false;
        pantallaFuncionesBolean = false;
        onDEL();
    }
    @FXML
    protected void onEcuaciones(){
        ajustarStageCientifica(false);
        ajustarStageEcuaciones(true);
        ajustarStageFunciones(false);
        activarBotonesEcuaciones(true);
        pantalla.setPrefWidth(410);
        pantalla.setText("");
        operacionAnteriror.setText("");
        pantallaEcuaciones = true;
        canvasActivo = false;
        pantallaFuncionesBolean = false;
        rbX2.setSelected(true);
    }
    // todo : factorizar en el futuro
    @FXML
    protected void onFunciones() {
        ajustarStageCientifica(false);
        ajustarStageEcuaciones(false);
        ajustarStageBasica(false);
        ajustarStageFunciones(true);
        pantalla.setPrefWidth(320);
        pantalla.setText("");
        operacionAnteriror.setText("");
        pantallaFunciones.setText("");
        desplazamientoYtrigoFun.setText("");
        operadorLabelFun.setText("");
        pantallaEcuaciones = false;
        canvasActivo = true;
        pantallaFuncionesBolean = true;
        escribirDentroFun.setDisable(true);
        escribirFueraFun.setDisable(true);
        escribirDentroFunB = true;
        dibujarEjes();
        dibujarGrafico();
        onDEL();
    }
    private void ajustarStageFunciones(boolean expand){
        Stage stage = (Stage) funcionesPane.getScene().getWindow();
        if (expand) {
            funcionesPane.setVisible(true);
            gridPaneFunciones.setVisible(true);
            idPanePantallaFunciones.setVisible(true);
            stage.setWidth(850);
            stage.setHeight(460);
        } else {
            pantalla.setText("");
            pantallaFunciones.setText("");
            gridPaneBasica.setVisible(true);
            gridPaneFunciones.setVisible(false);
            funcionesPane.setVisible(false);
            idPanePantallaFunciones.setVisible(false);
        }
    }
    private void ajustarStageCientifica(boolean expand) {
        Stage stage = (Stage) cientifica.getScene().getWindow();
        if (expand) {
            gridPaneBasica.setVisible(true);
            cientifica.setVisible(true);
            stage.setWidth(437);
            stage.setHeight(403);
        } else {
            cientifica.setVisible(false);
        }
    }
    private void ajustarStageEcuaciones(boolean expand) {
        Stage stage = (Stage) ecuaciones2Grado.getScene().getWindow();
        if (expand) {
            gridPaneBasica.setVisible(true);
            ecuaciones2Grado.setVisible(true);
            ecuaciones2Grado2.setVisible(true);
            stage.setWidth(437);
            stage.setHeight(403);
        } else {
            ecuaciones2Grado.setVisible(false);
            ecuaciones2Grado2.setVisible(false);
        }
    }
    private void ajustarStageBasica(boolean expand){
        Stage stage = (Stage) gridPaneBasica.getScene().getWindow();
        if (expand) {
            gridPaneBasica.setVisible(true);
            stage.setWidth(276);
            stage.setHeight(403);
        } else {
            gridPaneBasica.setVisible(false);
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
        if(pantallaFuncionesBolean){
            if(escribirDentroFunB){
                escribirExpresionMatematicaLabel(pantallaFunciones, num);
            }else {
                escribirExpresionMatematicaLabel(desplazamientoYtrigoFun,num);
            }
        }else if(!pantallaEcuaciones){
            escribirExpresionMatematicaLabel(pantalla,num);
        }else {
            if(rbX2.isSelected()){
                escribirExpresionMatematicaLabel(labelx2,num);
            } else if (rbx.isSelected()) {
                escribirExpresionMatematicaLabel(labelx1,num);
            } else if (rbc.isSelected()) {
                escribirExpresionMatematicaLabel(labelC,num);
            } else if (rbr.isSelected()) {
                escribirExpresionMatematicaLabel(labelR,num);
            }
        }
    }
    private void escribirExpresionMatematicaLabel(Label pantalla, char num){
        String txt = pantalla.getText();
        String opComlejo = buscarOperadorComplejo(pantalla.getText());
        int contador = 0;
        for (int i = 0; i < txt.length(); i++) {
            if(txt.charAt(i) == num){
                contador +=1;
            }
        }
        if(pantallaFuncionesBolean){
            agregarNumero(String.valueOf(num));
        }else if( (txt.contains("(-0)") || txt.contains("(0)") || txt.contains("(-)") || (txt.contains("()")) && opComlejo != null)
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
    private void escribirArcFun(String arco, String trigoNormal){
        if(pantallaFuncionesBolean && arcFun){
            agregarOperandoComplejoMenosDelante(arco);
        }else {
            agregarOperandoComplejoMenosDelante(trigoNormal);
        }
    }
    @FXML
    protected void onSeno(){
        escribirArcFun("ArcSen()","sen()");
    }
    @FXML
    protected void onCoseno(){
        escribirArcFun("ArcCos()","cos()");
    }
    @FXML
    protected void onTangente(){
        escribirArcFun("ArcTan()","tan()");
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
        String txt = pantallaFunciones.getText();
        if(pantallaFuncionesBolean ) {
            if(!txt.contains("x") && !txt.contains("(")) {
                boolean menosDelante = false;
                String aux ="";
                if(txt.startsWith("-")){
                    txt = txt.substring(1);
                    menosDelante = true;
                }
                if (txt.equals("0") || txt.isEmpty() || (operandos(txt,false) == 0.0 && !txt.startsWith("-"))) {
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
            agregarOperandoComplejoMenosDelantePantalla(op,pantalla);
        }
    }
    private void agregarOperandoComplejoMenosDelantePantalla(String op, Label pantalla){
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
        if(pantallaFuncionesBolean){
            agregarExpresionNPantalla(operador, pantallaFunciones);
        }else {
            agregarExpresionNPantalla(operador, pantalla);
        }
    }
    private void agregarExpresionNPantalla(String operador, Label pantalla){
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
    private void dibujarEjes() {
        GraphicsContext gc = canvasFunciones.getGraphicsContext2D();
        gc.clearRect(0, 0, canvasFunciones.getWidth(), canvasFunciones.getHeight());

        double centerX = canvasFunciones.getWidth() / 2 + offsetX;
        double centerY = canvasFunciones.getHeight() / 2 + offsetY;

        // Dibujar líneas de la cuadrícula
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(0.5);

        // Líneas verticales
        for (double x = centerX % scale; x < canvasFunciones.getWidth(); x += scale) {
            gc.strokeLine(x, 0, x, canvasFunciones.getHeight());
        }
        for (double x = centerX % scale; x > 0; x -= scale) {
            gc.strokeLine(x, 0, x, canvasFunciones.getHeight());
        }

        // Líneas horizontales
        for (double y = centerY % scale; y < canvasFunciones.getHeight(); y += scale) {
            gc.strokeLine(0, y, canvasFunciones.getWidth(), y);
        }
        for (double y = centerY % scale; y > 0; y -= scale) {
            gc.strokeLine(0, y, canvasFunciones.getWidth(), y);
        }

        // Dibujar ejes principales
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);

        // Eje X
        gc.strokeLine(0, centerY, canvasFunciones.getWidth(), centerY);

        // Eje Y
        gc.strokeLine(centerX, 0, centerX, canvasFunciones.getHeight());

        gc.setFont(new Font(10));
        gc.setFill(Color.WHITE);
        for (double x = centerX % scale; x < canvasFunciones.getWidth(); x += scale) {
            if (x != centerX) {
                gc.fillText(String.format("%.0f", (x - centerX) / scale), x, centerY + 12);
            }
        }
        for (double x = centerX % scale; x > 0; x -= scale) {
            if (x != centerX) {
                gc.fillText(String.format("%.0f", (x - centerX) / scale), x, centerY + 12);
            }
        }

        // Dibujar coordenadas en el eje Y
        for (double y = centerY % scale; y < canvasFunciones.getHeight(); y += scale) {
            if (y != centerY) {
                gc.fillText(String.format("%.0f", (centerY - y) / scale), centerX + 4, y);
            }
        }
        for (double y = centerY % scale; y > 0; y -= scale) {
            if (y != centerY) {
                gc.fillText(String.format("%.0f", (centerY - y) / scale), centerX + 4, y);
            }
        }
    }
    private boolean expresionDibujarGraficoCorrecta(String txt){
        boolean parentesisIncorrectos = true;
        if(txt.contains("(")){
            if(!txt.contains(")")) {
                parentesisIncorrectos = false;
            }
        }
        return txt.contains("x") && !txt.endsWith("-") && !txt.endsWith("+") && !txt.endsWith(".") && !txt.endsWith("^") && parentesisIncorrectos;
    }
    private void dibujarGrafico() {
        String txt = pantallaFunciones.getText();
        if(expresionDibujarGraficoCorrecta(txt)) {
            // Para gráficar la función
            GraphicsContext gc = canvasFunciones.getGraphicsContext2D();
            gc.clearRect(0, 0, canvasFunciones.getWidth(), canvasFunciones.getHeight());
            dibujarEjes();
            gc.setStroke(Color.BLUE);
            gc.setLineWidth(2);
            double centerX = canvasFunciones.getWidth() / 2 + offsetX;
            double centerY = canvasFunciones.getHeight() / 2 + offsetY;
            gc.beginPath();

            String opComplejo = buscarOperadorComplejo(txt);
            double multiplicandoDelante = 1;
            double multiplicandoSegundo = 1;
            if(txt.contains("/")){
                if(txt.substring(txt.indexOf("/")+1).startsWith("-(")){
                    // txt = txt.substring(1);
                    multiplicandoSegundo = -1;
                }else {
                    multiplicandoSegundo = calcularMultiplicadorDelante(txt.substring(txt.indexOf("/")+1), opComplejo);
                }
            }
            if(txt.contains("/")) {
                if (txt.substring(0, txt.indexOf("/")).contains("(")) {
                    multiplicandoDelante = calcularMultiplicadorDelante(txt, opComplejo);
                }
            } else if (txt.contains("(") && opComplejo == null) {
                if(txt.startsWith("-(")){
                    txt = txt.substring(1);
                    multiplicandoDelante = -1;
                }else {
                    if(txt.contains("√")){
                        if(txt.substring(txt.indexOf("√")+1,txt.indexOf("(")).equals(null)){
                            multiplicandoDelante = 1;
                        }else {
                            multiplicandoDelante = calcularMultiplicadorDelante(txt.substring(txt.indexOf("√") + 1), opComplejo);
                        }
                    }else {
                        multiplicandoDelante = calcularMultiplicadorDelante(txt, opComplejo);
                    }
                }
            }



            for (double screenX = 0; screenX <= canvasFunciones.getWidth(); screenX += 1) {
                double x = (screenX - centerX) / scale;
                double y = 0;
                if(opComplejo != null){
                    y = calcularOpComplejoFun(txt,x,opComplejo,y);
                }else if(txt.contains("/")){
                    y = funcionDividendo(txt, multiplicandoDelante,multiplicandoSegundo, x);
                } else if (txt.contains("√")) {
                    y = funcionRadical(txt,opComplejo,y,x, multiplicandoDelante);
                } else if(txt.contains("^")) {
                    double exponente = calcularExponenteFuncion(txt);
                    y = multiplicandoDelante * Math.pow((calcularNumXFunciones(txt, opComplejo) * x) + calcularDesplazamiento(txt, false), exponente) + calcularDesplazamientoY(txt, opComplejo);
                } else {
                    y = multiplicandoDelante * ((calcularNumXFunciones(txt, opComplejo) * x) + calcularDesplazamientoY(txt, opComplejo));
                }


                double screenY = centerY - y * scale;
                if (screenX == 0) {
                    gc.moveTo(screenX, screenY);
                } else {
                    gc.lineTo(screenX, screenY);
                }


            }
            gc.stroke();
        }
    }

    private double calcularOpComplejoFun(String txt, double x, String opComplejo, double y){
        double multiplicandoX, desplazamientoX;
        double exponente = 1;
        String dentroOpComplejo = txt.substring(txt.indexOf("(")+1, txt.indexOf(")"));

        if(dentroOpComplejo.startsWith("-x")){
            multiplicandoX = -1;
        }else if(dentroOpComplejo.startsWith("x")){
            multiplicandoX = 1;
        }else {
            if(dentroOpComplejo.startsWith("-")){
                multiplicandoX = operandos(dentroOpComplejo.substring(1,dentroOpComplejo.indexOf("x")), true);
            }else {
                multiplicandoX = operandos(dentroOpComplejo.substring(0,dentroOpComplejo.indexOf("x")), false);
            }
        }

        if(dentroOpComplejo.contains("+")){
            desplazamientoX = operandos(dentroOpComplejo.substring(dentroOpComplejo.indexOf("+")), false);
        }else if (dentroOpComplejo.contains("-") && dentroOpComplejo.lastIndexOf("-")> dentroOpComplejo.indexOf("^")+1){
            desplazamientoX = operandos(dentroOpComplejo.substring(dentroOpComplejo.lastIndexOf("-")+1), true);
        }else {
            desplazamientoX = 0;
        }



        if(dentroOpComplejo.contains("^")){
            exponente = calcularExponenteFuncion(dentroOpComplejo);
        }
        double desplazamientoY;
        if (desplazamientoYtrigoFun.getText().isEmpty()){
            if(operadorLabelFun.getText().equals("+") || operadorLabelFun.getText().equals("-")){
                desplazamientoY = 0;
            }else {
                desplazamientoY = 1;
            }
        }else {
            if(desplazamientoYtrigoFun.getText().equals("x")){
                desplazamientoY = x;
            }else if(desplazamientoYtrigoFun.getText().contains("x")){
                desplazamientoY = operandos(desplazamientoYtrigoFun.getText().substring(0,desplazamientoYtrigoFun.getText().indexOf("x")), false) *x;
            }else {
                desplazamientoY = operandos(desplazamientoYtrigoFun.getText(), false);
            }
        }

        double multiplicandoDelanteTrigo =1;
        if(txt.startsWith("-"+opComplejo)){
            multiplicandoDelanteTrigo = -1;
        } else if (txt.contains("·")) {
            if(txt.startsWith("-")){
                multiplicandoDelanteTrigo = operandos(txt.substring(1,txt.indexOf("·")-1),true);
            }else {
                multiplicandoDelanteTrigo = operandos(txt.substring(0,txt.indexOf("·")-1),false);
            }
        }


        if(txt.contains("ArcSen(")){
            y = OperacionesMath.calculate(Math::asin,operadorLabelFun.getText(),x,exponente,multiplicandoX,desplazamientoX,txt, desplazamientoY,multiplicandoDelanteTrigo);
        }else if(txt.contains("ArcCos(")){
            y = OperacionesMath.calculate(Math::acos,operadorLabelFun.getText(),x,exponente,multiplicandoX,desplazamientoX,txt, desplazamientoY,multiplicandoDelanteTrigo);
        }else if(txt.contains("ArcTan(")){
            y = OperacionesMath.calculate(Math::atan,operadorLabelFun.getText(),x,exponente,multiplicandoX,desplazamientoX,txt, desplazamientoY,multiplicandoDelanteTrigo);
        } else if(txt.contains("sen(")){
            y = OperacionesMath.calculate(Math::sin,operadorLabelFun.getText(),x,exponente,multiplicandoX,desplazamientoX,txt, desplazamientoY,multiplicandoDelanteTrigo);
        }else if(txt.contains("cos(")){
            y = OperacionesMath.calculate(Math::cos,operadorLabelFun.getText(),x,exponente,multiplicandoX,desplazamientoX,txt, desplazamientoY,multiplicandoDelanteTrigo);
        }else if(txt.contains("tan(")){
            y = OperacionesMath.calculate(Math::tan, operadorLabelFun.getText(), x, exponente, multiplicandoX, desplazamientoX, txt, desplazamientoY,multiplicandoDelanteTrigo);
        }else if(txt.contains("ln(")){
            y = OperacionesMath.calculate(Math::log, operadorLabelFun.getText(), x, exponente, multiplicandoX, desplazamientoX, txt, desplazamientoY,multiplicandoDelanteTrigo);
        }
        return y;
    }
    private double funcionRadical(String txt, String opComplejo, double y, double x, double multiplicandoDelante){
        double radicando = calcularExponenteFuncion(txt);
        double xDentroRaizNum = calcularNumXFunciones(txt.substring(txt.indexOf("√")+1, txt.indexOf("x")+1), opComplejo);
        double desplazamientoX = 0;
        double desplazamientoY = 0;
        if(txt.contains("(")){
            if(txt.contains("+")){
                desplazamientoX = operandos(txt.substring(txt.indexOf("+")+1, txt.indexOf(")")), false);
            } else if (txt.contains("-")) {
                if(txt.startsWith("-")){
                    txt = txt.substring(1);
                    desplazamientoX = operandos(txt.substring(txt.indexOf("-")+1, txt.indexOf(")")), true);
                    txt = "-"+txt;
                }else {
                    desplazamientoX = operandos(txt.substring(txt.indexOf("-")+1, txt.indexOf(")")), true);
                }
            }
        }

        if(txt.contains("(") && txt.lastIndexOf("+") > txt.indexOf(")")){
            desplazamientoY = operandos(txt.substring(txt.lastIndexOf("+")+1),false);
        } else if (txt.contains("(") && txt.lastIndexOf("-") > txt.indexOf(")")) {
            desplazamientoY = operandos(txt.substring(txt.lastIndexOf("-")+1),true);
        } else if (!txt.contains("(") && txt.contains("x+")) {
            desplazamientoY = operandos(txt.substring(txt.lastIndexOf("+")+1),false);
        }else if (!txt.contains("(") && txt.contains("x-")) {
            desplazamientoY = operandos(txt.substring(txt.lastIndexOf("-")+1),true);
        }else {
            desplazamientoY = 0;
        }

        if (xDentroRaizNum != 0 && radicando != 0) {
            xDentroRaizNum = xDentroRaizNum * multiplicandoDelante;
            desplazamientoX = desplazamientoX * multiplicandoDelante;
            if (radicando % 2 != 0 && radicando >0  && radicando == Math.floor(radicando)) { // Exponente impar, postivo y entero
                if (xDentroRaizNum * x + desplazamientoX < 0) {
                    y = -Math.pow(Math.abs(xDentroRaizNum * x +  desplazamientoX), 1.0 / radicando);
                } else { // ajustar desplzamiento
                    y = Math.pow(xDentroRaizNum * x + desplazamientoX, 1.0 / radicando);
                }
            } else {
                y = Math.pow(xDentroRaizNum * x + desplazamientoX, 1 / radicando);
            }
        }
        return  y + desplazamientoY;
    }
    private double calcularDesplazamientoDividendo(String txt){
        if (txt.lastIndexOf("+") > txt.lastIndexOf(")")) {
            return operandos(txt.substring(txt.lastIndexOf("+") + 1),false);
        } else if (txt.lastIndexOf("-") > txt.lastIndexOf(")") || txt.lastIndexOf("-") >  txt.lastIndexOf("^")+2) {
            if(txt.lastIndexOf("-") == txt.lastIndexOf("^")+1){
                return 0;
            }else {
                return operandos(txt.substring(txt.lastIndexOf("-") + 1), true);
            }
        }else {
            return 0;
        }
    }
    private double calcularDesplazamiento(String txt, boolean segundo){
        String opComplejo = buscarOperadorComplejo(txt);
        if(txt.contains("(") && opComplejo == null && txt.contains("^") && !(txt.indexOf("x")+1 == txt.indexOf(")")) && !segundo){
            return operandos(txt.substring(txt.indexOf("x")+2,txt.indexOf(")")),txt.charAt(txt.indexOf("x")+1) == '-');
        } else if (txt.contains("(") && !(txt.indexOf("x")+1 == txt.indexOf(")")) && segundo) {
            if(txt.contains("^") && txt.indexOf("^") < txt.indexOf(")")){
                txt = txt.substring(txt.indexOf("^"),txt.indexOf(")"));
                if(txt.contains("-")){
                    return  operandos(txt.substring(txt.indexOf("-")+1),true);
                }else if (txt.contains("+")){
                    return  operandos(txt.substring(txt.indexOf("+")+1),false);
                }else {
                    return 0;
                }
            }else {
                return operandos(txt.substring(txt.indexOf("x") + 2, txt.indexOf(")")), txt.charAt(txt.indexOf("x") + 1) == '-');
            }
        } else {
            return 0;
        }
    }
    private double calcularExponenteFuncion(String txt){
        Pattern patronExponente = Pattern.compile("\\^(-?(\\d+\\.\\d+|\\d+|e|π|φ))");
        if(!txt.contains("^") && !txt.contains("√")){
           return 1;
        }else if(txt.contains("^")){
            Matcher matcherExponente = patronExponente.matcher(txt);
            if (matcherExponente.find()) {
                String exponenteStr = matcherExponente.group(1);
                if (exponenteStr.startsWith("-")) {
                   return operandos(exponenteStr.substring(1), true);
                } else {
                  return operandos(exponenteStr, false);
                }
            }
        } else if (txt.contains("√")) {
            if(txt.startsWith("-")){
                return operandos(txt.substring(1,txt.indexOf("√")), true);
            }else {
                return operandos(txt.substring(0,txt.indexOf("√")), false);
            }
        }
        return 1;
    }
    private double calcularDesplazamientoY(String txt, String opComplejo){
        // desplazamiento, para 3x +3, (3x +3), y (3x+1)^2+3,  para todos estos más 3
        if(txt.contains("^")) {
            if (txt.substring(txt.indexOf("^")).contains("+")) {
                String aux = txt.substring(txt.indexOf("^") + 1);
                if (txt.contains("(") && opComplejo == null && txt.contains("^")) {
                    if (aux.contains("+")) {
                        return operandos(aux.substring(aux.lastIndexOf("+") + 1), false);
                    } else {
                        return 0;
                    }
                }else {
                    return operandos(aux.substring(aux.lastIndexOf("+") + 1), false);
                }
            } else if (txt.substring(txt.indexOf("^")+2).contains("-")) { // el +2 para que si es algo del tipo (x+1)^-3 no me coja el -3 como desplazmiento
                // factorizar
                String aux = txt.substring(txt.indexOf("^") + 1);
                if (txt.contains("(") && opComplejo == null && txt.contains("^")) {
                    if (aux.contains("-")) {
                        return operandos(aux.substring(aux.lastIndexOf("-") + 1), true);
                    } else {
                        return 0;
                    }
                }else {
                    return operandos(aux.substring(aux.lastIndexOf("-") + 1), true);
                }
            }
        }else {
            if(!txt.matches("^-?(\\d+\\.\\d+|\\d+|e|π|φ|)\\(?-?(\\d+\\.\\d+|\\d+|e|π|φ|)x\\)?$")) {
                if (txt.contains("+")) {
                    if (txt.contains("(") && opComplejo == null) {
                        return operandos(txt.substring(txt.indexOf("+") + 1, txt.indexOf(")")), false);
                    } else {
                        return operandos(txt.substring(txt.indexOf("+") + 1),false);
                    }
                } else if (txt.contains("-")) {
                    if (txt.contains("(") && opComplejo == null) {
                        return operandos(txt.substring(txt.indexOf("-")+1, txt.indexOf(")")), true);
                    } else {
                        return operandos(txt.substring(txt.lastIndexOf("-")+1),true);
                    }
                }
            }
        }
        return 0;
    }
    private double calcularNumXFunciones(String txt, String opComplejo){
        int indiceX = txt.indexOf("x");
        if(txt.startsWith("x")){
            return 1;
        } else if (txt.startsWith("-x")) {
            return -1;
        }else {
            if(txt.contains("(") && opComplejo == null){
                if(txt.contains("(x")){
                    return 1;
                } else if (txt.contains("(-x")) {
                    return -1;
                } else{
                    if(txt.substring(txt.indexOf("(")+1,txt.indexOf("x")).contains("-")){
                        return operandos(txt.substring(txt.indexOf("(")+2,txt.indexOf("x")), true);
                    }else {
                        return operandos(txt.substring(txt.indexOf("(")+1,txt.indexOf("x")), false);
                    }
                }
            }else {
                if (txt.substring(0, indiceX).contains("-")) {
                    return operandos(txt.substring(1, indiceX), true);
                } else {
                    return operandos(txt.substring(0, indiceX), false);
                }

            }
        }
    }
    private double calcularMultiplicadorDelante(String txt, String opComplejo){
        if(txt.startsWith("-(")){
            return  -1;
        } else if (txt.contains("(") && opComplejo == null && !txt.startsWith("(")) {
            if(txt.charAt(0) == '-'){
                return operandos(txt.substring(1,txt.indexOf("(")),true );
            }else {
                return operandos(txt.substring(0,txt.indexOf("(")),false );
            }
        }
        return 1;
    }
    private void clickCanvas(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) { // Solo si se presiona el botón derecho del mouse
            dragStartX = event.getX() - offsetX;
            dragStartY = event.getY() - offsetY;
        }
    }
    private void arrastrarCanvas(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) { // Solo si se arrastra con el botón derecho del mouse
            offsetX = event.getX() - dragStartX;
            offsetY = event.getY() - dragStartY;
            dibujarEjes();
            dibujarGrafico();
        }
    }
    private void scrollCanvas(ScrollEvent event) {
        if(canvasActivo) {
            double delta = event.getDeltaY();
            if (delta > 0) {
                scale *= 1.1; // Zoom in
            } else {
                scale *= 0.9; // Zoom out
            }
            dibujarGrafico();
        }
    }
    @FXML
    protected void onButtonXFunciones(){
        if(escribirDentroFunB){
            String txt = pantallaFunciones.getText();
            if(!txt.endsWith(".)")) {
                if (!txt.contains("x") && !txt.endsWith(".")) {
                    if(txt.endsWith("e") || txt.endsWith("π") || txt.endsWith("φ")){
                        pantallaFunciones.setText(txt+"x");
                    }else {
                        agregarNumero("x");
                    }
                } else if (txt.contains("/") && !txt.substring(txt.indexOf("/")).contains("x")) {
                    agregarNumero("x");
                }
            }

        }else {
            String txt2 = desplazamientoYtrigoFun.getText();
            if(!txt2.contains("x") && !txt2.endsWith(".")){
                if(txt2.endsWith("e") || txt2.endsWith("π") || txt2.endsWith("φ")){
                    desplazamientoYtrigoFun.setText(txt2+"x");
                }else {
                    agregarNumero("x");
                }
            }
        }

    }
    @FXML
    protected void onCerrarParentesis(){
        if(escribirDentroFunB) {
            String txt = pantallaFunciones.getText();
            if (txt.contains("(") && txt.contains("x") && !txt.contains(")")) {
                if (!(txt.endsWith("-") || txt.endsWith("+") || txt.endsWith("."))) {
                    pantallaFunciones.setText(txt + ")");
                }
            } else if (txt.contains("/")) {
                if (txt.contains("(") && txt.contains("x") && !txt.endsWith(")")) {
                    if (!(txt.endsWith("-") || txt.endsWith("+") || txt.endsWith("."))) {
                        pantallaFunciones.setText(txt + ")");
                    }
                }
            }
        }
    }
    @FXML
    protected void onAbrirParentesis(){
        String txt = pantallaFunciones.getText();
        if(escribirDentroFunB && !txt.endsWith(".")) {
            if (txt.isEmpty() || txt.equals("-")) {
                pantallaFunciones.setText(txt + "(");
            } else if (txt.startsWith("-0") || txt.startsWith("0")) {
                if (txt.startsWith("-0")) {
                    pantallaFunciones.setText("-(");
                } else {
                    pantallaFunciones.setText("(");
                }
            } else if (txt.matches("^-?(e|π|φ|(\\d+\\.?\\d*))$")) {
                pantallaFunciones.setText(txt + "(");
            } else if (txt.endsWith("/") || txt.endsWith("/-") || txt.substring(txt.indexOf("/") + 1).matches("^-?(e|π|φ|(\\d+\\.?\\d*))$")) {
                pantallaFunciones.setText(txt + "(");
            } else if (txt.matches("-?(e|π|φ|(\\d+\\.?\\d*))√-?(e|π|φ|(\\d+\\.?\\d*))?")) {
                pantallaFunciones.setText(txt + "(");
            }
        }
    }
    private double funcionDividendo(String txt, double multiplicandoDelante, double multiplicandoSegundo, double x){
        String yAntes = txt.substring(0, txt.indexOf("/"));
        String opComplejoAntes = buscarOperadorComplejo(yAntes);
        String yDespues = txt.substring(txt.indexOf("/") + 1);
        double yAntesD = 0;
        double yDespuesD = 0;
        double desplazamientoYDvididendo = 0;

        if(yAntes.contains("^")) {
            yAntesD = multiplicandoDelante * Math.pow((calcularNumXFunciones(yAntes, opComplejoAntes) * x) + calcularDesplazamiento(yAntes, false), calcularExponenteFuncion(yAntes));
            yAntesD += calcularDesplazamientoY(yAntes, opComplejoAntes);
        }else if(!yAntes.contains("x")){
            yAntesD = Double.parseDouble(yAntes);
        }else {
            yAntesD = multiplicandoDelante * ((calcularNumXFunciones(yAntes, opComplejoAntes) * x) + calcularDesplazamientoY(yAntes, opComplejoAntes));
        }

        if(yDespues.contains("x")) {
            if (yDespues.contains("^") && !yDespues.contains("(")) {
                yDespuesD = multiplicandoSegundo * Math.pow((calcularNumXFunciones(yDespues, opComplejoAntes) * x) + calcularDesplazamiento(yDespues, true), calcularExponenteFuncion(yDespues));
                desplazamientoYDvididendo = calcularDesplazamientoDividendo(yDespues);
            } else {
                if (yDespues.contains("(")) {
                    if (yDespues.contains("^")) {
                        yDespuesD = multiplicandoSegundo * Math.pow((calcularNumXFunciones(yDespues, opComplejoAntes) * x) + calcularDesplazamiento(yDespues, true), calcularExponenteFuncion(yDespues));
                    } else {
                        yDespuesD = multiplicandoSegundo * ((calcularNumXFunciones(yDespues, opComplejoAntes) * x) + calcularDesplazamientoY(yDespues, opComplejoAntes));
                    }
                    desplazamientoYDvididendo = calcularDesplazamientoDividendo(yDespues);
                } else {
                    yDespuesD = multiplicandoSegundo * ((calcularNumXFunciones(yDespues, opComplejoAntes) * x));
                    desplazamientoYDvididendo = calcularDesplazamientoY(yDespues, opComplejoAntes);
                }
            }
        }else{
            if(yDespues.contains("-")){
                yDespuesD = operandos(yDespues.substring(1), true);
            }else {
                yDespuesD = operandos(yDespues, false);
            }
        }
        return (yAntesD / yDespuesD) + desplazamientoYDvididendo;
    }
    @FXML
    protected void escribirDentro() {
        multiplicandoEspecialFun.setDisable(true);
        escribirDentroFunB = true;
        escribirFueraFun.setDisable(false);
        escribirDentroFun.setDisable(true);
    }
    @FXML
    protected void escribirFuera() {
        multiplicandoEspecialFun.setDisable(false);
        escribirDentroFunB = false;
        escribirFueraFun.setDisable(true);
        escribirDentroFun.setDisable(false);
    }
    @FXML
    protected void multiEspecialFun() {
        if(pantallaFuncionesBolean && !escribirDentroFunB){
            operadorLabelFun.setText("·");
        }
    }
    @FXML
    protected void onCambiarArco() {
        if(senFun.getText().equals("sen")){
            arcFun = true;
            senFun.setText("arcs");
            cosFun.setText("arcc");
            tanFun.setText("arct");
        }else {
            arcFun = false;
            senFun.setText("sen");
            cosFun.setText("cos");
            tanFun.setText("tan");
        }
    }
}