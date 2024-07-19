package com.example.calculadorafx3;

import com.example.calculadorafx3.Metodos.Atributos;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.*;
import java.net.URL;
import java.text.*;
import java.util.*;
import java.util.regex.Pattern;

import static com.example.calculadorafx3.Metodos.MetodosBotonesPantalla_MBP.*;
import static com.example.calculadorafx3.Metodos.MetodosBotonesTeclado_MBT.*;
import static com.example.calculadorafx3.Metodos.MetodosCambiosDeEscena_MCDE.*;
import static com.example.calculadorafx3.Metodos.MetodosFunciones_MFUN.*;
import static com.example.calculadorafx3.Metodos.MetodosGenerales_MGEN.*;
import static com.example.calculadorafx3.Metodos.MetodosIgualSimpleCompleja_MISC.*;
import static com.example.calculadorafx3.Metodos.MetodosOperadoresSimples_MOS.*;
import static com.example.calculadorafx3.Metodos.MetodosOperandosComplejos_MOC.*;


public class Controlador implements Initializable {
    private final DecimalFormat df = new DecimalFormat("#.####", new DecimalFormatSymbols(Locale.US));
    private final Pattern patronCuadradoRaiz = Pattern.compile("^-?\\d+\\.?\\d*$");
    private final Pattern patronNumEnteroPositivo = Pattern.compile("^\\d+$");
    private final Pattern puntoDespuesDelOperador = Pattern.compile("^-?\\d*\\.?\\d*[-+x√/%^]\\d+\\.$");
    private final Pattern patronDespuesOperadorNumsEspeciales = Pattern.compile("^-?(e|π|φ|(\\d*\\.?\\d*))[-+x/%^][φπe]$");
    private final Pattern patronNumsAntesRaiz = Pattern.compile("^-?(e|π|φ|(\\d*\\.?\\d*))$");
    @FXML protected RadioButton rbX2, rbx, rbc, rbr;
    @FXML protected Label labelx2, labelx1, labelC, labelR, idEcuacionLabel, LabelSolucionX1, LabelSolucionX2;
    @FXML protected GridPane cientifica, gridPaneBasica, gridPaneEcuaciones, gridPaneFunciones;
    @FXML private Pane ecuaciones2Grado, ecuaciones2Grado2, funcionesPane,idPanePantallaFunciones, original;
    @FXML private Label pantalla, pantallaFunciones, operadorLabelFun, desplazamientoYtrigoFun;
    @FXML private Text operacionAnteriror;
    @FXML private Canvas canvasFunciones;
    private double scale = 30;
    private boolean canvasActivo = false;
    private boolean arcFun =false; // boolean para cambiar botones en fun (trigo)
    private boolean pantallaFuncionesBolean = false;
    private boolean pantallaEcuacionesBolean = false;
    private static boolean escribirDentroFunB = true;
    private final char[] caracteresProhibidos = new char[]{'+', 'x', '/', '%','^','√'};
    private final char[] caracteresProhibidosFunciones = new char[]{'+', '/', '%'};
    private final String operadores = "+x/%-^";
    private static Stage stageCalculadora;
    private final ToggleGroup botonesEcuacionesRadio = new ToggleGroup();
    private double xOffset = 0;
    private double yOffset = 0;
    private final ArrayList<Button> botonesDesactivarEcuaciones = new ArrayList<>();
    @FXML private Button idPorcentaje, idBarra, idPotencia, idMenos, idCuadrado, idMas, idRaiz,idX, escribirDentroFun, escribirFueraFun, multiplicandoEspecialFun, senFun, cosFun, tanFun;
    private double offsetX = 0;
    private double offsetY = 0;
    private double dragStartX;
    private double dragStartY;
    private static ArrayList<String> operandosComplejos = new ArrayList<>();
    private static final Set<Character> caracteresExcluidos = new HashSet<>();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Atributos.setLabels(pantalla, pantallaFunciones, operadorLabelFun, desplazamientoYtrigoFun, operacionAnteriror);
        Atributos.setLabelsEcuaciones(labelx2, labelx1, labelC, labelR, idEcuacionLabel, LabelSolucionX1, LabelSolucionX2);
        Atributos.setRadioButtons(rbX2, rbx, rbc, rbr);
        Atributos.setPatrones(patronNumsAntesRaiz, patronDespuesOperadorNumsEspeciales, puntoDespuesDelOperador, patronNumEnteroPositivo, patronCuadradoRaiz);
        Atributos.setArraysYesasCosas(caracteresProhibidos, caracteresProhibidosFunciones, caracteresExcluidos, operandosComplejos, df, botonesDesactivarEcuaciones);
        Atributos.setCanvas(canvasFunciones);
        Atributos.setGridPanes(cientifica, gridPaneBasica, gridPaneEcuaciones, gridPaneFunciones);
        Atributos.setPanes(ecuaciones2Grado, ecuaciones2Grado2, funcionesPane,idPanePantallaFunciones, original);
        Atributos.setBotonesFunciones(senFun, cosFun, tanFun, escribirDentroFun, escribirFueraFun, multiplicandoEspecialFun);
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

        setOperandosComplejos_MOC();

        setGrupoBotones_MBT(botonesEcuacionesRadio,rbX2,rbx,rbc,rbr);

        setBotonesDesactivarEcuaciones_MBT(botonesDesactivarEcuaciones, idPorcentaje, idBarra, idPotencia, idMenos, idCuadrado, idMas, idRaiz, idX);

        rbX2.addEventFilter(KeyEvent.KEY_PRESSED, event -> pasarAlSiguiente_MBT(event, rbx));
        rbx.addEventFilter(KeyEvent.KEY_PRESSED, event -> pasarAlSiguiente_MBT(event, rbc));
        rbc.addEventFilter(KeyEvent.KEY_PRESSED, event -> pasarAlSiguiente_MBT(event, rbr));
        rbr.addEventFilter(KeyEvent.KEY_PRESSED, event -> pasarAlSiguiente_MBT(event, rbX2));

        solucionarIgualEcuacionesTeclado_MBT(rbX2, pantallaFuncionesBolean, pantallaEcuacionesBolean, offsetX, offsetY, scale);
        solucionarIgualEcuacionesTeclado_MBT(rbx, pantallaFuncionesBolean, pantallaEcuacionesBolean, offsetX, offsetY, scale);
        solucionarIgualEcuacionesTeclado_MBT(rbc, pantallaFuncionesBolean, pantallaEcuacionesBolean, offsetX, offsetY, scale);
        solucionarIgualEcuacionesTeclado_MBT(rbr, pantallaFuncionesBolean, pantallaEcuacionesBolean, offsetX, offsetY, scale);

        deshabilitarBotonesTabulador_MBT(cientifica);
        deshabilitarBotonesTabulador_MBT(gridPaneBasica);
        deshabilitarBotonesTabulador_MBT(gridPaneEcuaciones);
        deshabilitarBotonesTabulador_MBT(gridPaneFunciones);

        canvasFunciones.setOnMousePressed(this::clickCanvas);
        canvasFunciones.setOnMouseDragged(this::arrastrarCanvas);
        canvasFunciones.setOnScroll(this::scrollCanvas);

        pantallaFunciones.textProperty().addListener(new ChangeListener<String>() {
            @Override // para cuando en las funciones se activen unos botones cuando se encuentren operandos complejos
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                pantallaFunciones.setText(newValue);
                if (buscarOperadorComplejo_MOC(newValue) != null) {
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

    @FXML
    protected void onButton00() {
        onButton00_MBP(pantallaFuncionesBolean, pantallaEcuacionesBolean);
    }
    @FXML
    protected void onNum(ActionEvent evento) {
        Button b = (Button) evento.getSource();
        agregarNumero_MBP(b.getText(), pantallaFuncionesBolean, pantallaEcuacionesBolean, escribirDentroFunB);
    }
    public void pasarEscena(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, this::escribirUsandoTeclas);
    }
    private void escribirUsandoTeclas(KeyEvent event){ // metodo intermedio para poder usar las teclas
        escribirUsandoTeclas_MBT(event, pantallaFuncionesBolean, pantallaEcuacionesBolean, escribirDentroFunB, offsetX, offsetY, scale);
    }
    @FXML
    protected void onButtonAC() {
        escribirDentroFunB = onButtonAC_MBP(pantallaFuncionesBolean, pantallaEcuacionesBolean);
    }
    @FXML
    protected void onButtonMasMenos() {
        onButtonMasMenos_MBP(pantallaFuncionesBolean, pantallaEcuacionesBolean, escribirDentroFunB);
    }
    @FXML
    protected void Operador(ActionEvent evento) {
        Button b = (Button) evento.getSource();
        onOperador_MOS(b.getText(), pantallaFuncionesBolean, escribirDentroFunB, pantallaEcuacionesBolean);
    }
    @FXML
    protected void onButtonMenos() {
        onOperador_MOS( "-", pantallaFuncionesBolean, escribirDentroFunB, pantallaEcuacionesBolean);
        extensionMenos_MOS(pantallaFuncionesBolean);
    }
    @FXML
    protected void onButtonPunto(){
        onButtonPunto_MBP(pantallaFuncionesBolean, pantallaEcuacionesBolean, escribirDentroFunB);
    }
    @FXML
    protected void onButtonIgual() {
        onIgual_MISC(pantallaFuncionesBolean, pantallaEcuacionesBolean, offsetX, offsetY, scale);
    }

    @FXML
    protected void onAtras() {
        onAtras_MBP(pantallaFuncionesBolean, pantallaEcuacionesBolean, escribirDentroFunB);
    }
    @FXML
    protected void onCuadrado() {
        cuadrado_MGEN();
    }
    @FXML
    protected void onRaiz() {
        raiz_MGEN();
    }
    @FXML
    protected void onSalir(){
        Platform.exit();
    }
    @FXML
    protected void onCientifica(){
        cambiarEscena(true,true,false,false,false, 410, false, false, false);
    }
    @FXML
    protected void onBasica(){
        cambiarEscena(true,false,false,false,false, 247, false, false, false);
    }
    @FXML
    protected void onEcuaciones(){
        cambiarEscena(true,false,true,false,true, 410, true, false, false);
        rbX2.setSelected(true);
    }
    @FXML
    protected void onFunciones() {
        cambiarEscena(false,false,false,true,false, 320, false, true, true);
        pantallaFunciones.setText("");
        desplazamientoYtrigoFun.setText("");
        operadorLabelFun.setText("");
        escribirDentroFun.setDisable(true);
        escribirFueraFun.setDisable(true);
        escribirDentroFunB = true;
        dibujarEjes_MFUN(offsetX, offsetY, scale);
        dibujarGrafico_MFUN(offsetX, offsetY, scale);
    }
    private void cambiarEscena(boolean basica, boolean cientificaB, boolean ecuaciones, boolean funciones, boolean botones, double pantallaTama, boolean pantallaEcuacionesB,
                               boolean canvasActivoB, boolean pantallaFuncionesBoleanB){
        ajustarStageBasica_MCDE(basica);
        ajustarStageCientifica_MCDE(cientificaB);
        ajustarStageEcuaciones_MCDE(ecuaciones);
        ajustarStageFunciones_MCDE(funciones);
        activarBotonesEcuaciones(botones);

        pantalla.setPrefWidth(pantallaTama);
        pantallaEcuacionesBolean = pantallaEcuacionesB;
        canvasActivo = canvasActivoB;
        pantallaFuncionesBolean = pantallaFuncionesBoleanB;

        pantalla.setText("");
        operacionAnteriror.setText("");
        onDel_MGEN();
        gridPaneBasica.setVisible(basica);
    }

    @FXML
    protected void onFactorial(){
        onFactorial_MGEN();
    }
    @FXML
    protected void onButtonE(){
        expresionesMatematicas_MBP('e', pantallaFuncionesBolean, pantallaEcuacionesBolean, escribirDentroFunB);
    }
    @FXML
    protected void onPi() {
        expresionesMatematicas_MBP('π', pantallaFuncionesBolean, pantallaEcuacionesBolean, escribirDentroFunB);
    }
    @FXML
    protected void onNumOro(){
        expresionesMatematicas_MBP('φ', pantallaFuncionesBolean, pantallaEcuacionesBolean, escribirDentroFunB);
    }
    @FXML
    protected void onNeperiano(){
        agregarOperandoComplejoMenosDelante_MOC("ln()", pantallaFuncionesBolean);
    }
    @FXML
    protected void onSeno(){
        escribirArcFun_MOC("ArcSen()","sen()", pantallaFuncionesBolean, arcFun);
    }
    @FXML
    protected void onCoseno(){
        escribirArcFun_MOC("ArcCos()","cos()", pantallaFuncionesBolean, arcFun);
    }
    @FXML
    protected void onTangente(){
        escribirArcFun_MOC("ArcTan()","tan()", pantallaFuncionesBolean, arcFun);
    }
    @FXML
    protected void onArcSeno(){
        agregarOperandoComplejoMenosDelante_MOC("ArcSen()", pantallaFuncionesBolean);
    }
    @FXML
    protected void onArcCos(){
        agregarOperandoComplejoMenosDelante_MOC("ArcCos()", pantallaFuncionesBolean);
    }
    @FXML
    protected void onArcTan(){
        agregarOperandoComplejoMenosDelante_MOC("ArcTan()", pantallaFuncionesBolean);
    }
    @FXML
    protected void onLog10(){
        agregarOperandoComplejoMenosDelante_MOC("log10()", pantallaFuncionesBolean);
    }
    @FXML
    protected void onInvertir(){
        onInvertir_MGEN();
    }
    @FXML
    protected void onInt(){
        onINT_MGEN();
    }
    @FXML
    protected void onRaizX(){
        agregarExpresionN_MOC("√", pantallaFuncionesBolean);
    }
    @FXML
    protected void onLogBaseN(){
        agregarExpresionN_MOC("log()", pantallaFuncionesBolean);
    }
    @FXML
    protected void onDEL() {
        onDel_MGEN();
    }
    @FXML
    protected void onButtonXFunciones(){
        onButtonXFunciones_MFUN(escribirDentroFunB, pantallaFuncionesBolean, pantallaEcuacionesBolean);
    }
    @FXML
    protected void onCerrarParentesis(){
        onCerrarParentesis_MFUN(escribirDentroFunB);
    }
    @FXML
    protected void onAbrirParentesis(){
        onAbrirParentesis_MFUN(escribirDentroFunB);
    }
    @FXML
    protected void escribirDentro() {
        escribirDentroFueraFun(true,true,false,true);
    }
    @FXML
    protected void escribirFuera() {
        escribirDentroFueraFun(false,false,true,false);
    }
    private void escribirDentroFueraFun(boolean m1,boolean e1,boolean e2,boolean e3){
        multiplicandoEspecialFun.setDisable(m1);
        escribirDentroFunB = e1;
        escribirFueraFun.setDisable(e2);
        escribirDentroFun.setDisable(e3);
    }
    @FXML
    protected void multiEspecialFun() {
        if(pantallaFuncionesBolean && !escribirDentroFunB){
            operadorLabelFun.setText("·");
        }
    }
    @FXML
    protected void onCambiarArco() {
       arcFun = onCambiarArco_NFUN();
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
            dibujarEjes_MFUN(offsetX, offsetY, scale);
            dibujarGrafico_MFUN(offsetX, offsetY, scale);
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
            dibujarGrafico_MFUN(offsetX, offsetY, scale);
        }
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
    public static void setEscribirDentroFunB(boolean escribirDentroFunBolean){
        escribirDentroFunB = escribirDentroFunBolean;
    }
}