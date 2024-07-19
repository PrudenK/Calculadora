package com.example.calculadorafx3.Metodos;

import com.example.calculadorafx3.Operaciones.OperacionesMath;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.calculadorafx3.Metodos.MetodosBotonesPantalla_MBP.agregarNumero_MBP;
import static com.example.calculadorafx3.Metodos.MetodosGenerales_MGEN.*;
import static com.example.calculadorafx3.Metodos.MetodosOperandosComplejos_MOC.*;

public class MetodosFunciones_MFUN {

    public static void onButtonXFunciones_MFUN(boolean escribirDentroFunB, boolean pantallaFuncionesBolean, boolean pantallaEcuacionesBolean){
        if(escribirDentroFunB){
            String txt = pantallaFunciones.getText();
            if(!txt.endsWith(".)")) {
                if (!txt.contains("x") && !txt.endsWith(".")) {
                    if(txt.endsWith("e") || txt.endsWith("π") || txt.endsWith("φ")){
                        pantallaFunciones.setText(txt+"x");
                    }else {
                        agregarNumero_MBP("x", pantallaFuncionesBolean, pantallaEcuacionesBolean, escribirDentroFunB);
                    }
                } else if (txt.contains("/") && !txt.substring(txt.indexOf("/")).contains("x")) {
                    agregarNumero_MBP("x", pantallaFuncionesBolean, pantallaEcuacionesBolean, escribirDentroFunB);
                }
            }

        }else {
            String txt2 = desplazamientoYtrigoFun.getText();
            if(!txt2.contains("x") && !txt2.endsWith(".")){
                if(txt2.endsWith("e") || txt2.endsWith("π") || txt2.endsWith("φ")){
                    desplazamientoYtrigoFun.setText(txt2+"x");
                }else {
                    agregarNumero_MBP("x", pantallaFuncionesBolean, pantallaEcuacionesBolean, escribirDentroFunB);
                }
            }
        }
    }
    public static void onAbrirParentesis_MFUN(boolean escribirDentroFunB){
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
    public static void onCerrarParentesis_MFUN(boolean escribirDentroFunB){
        if(escribirDentroFunB) {
            String txt = pantallaFunciones.getText();
            if (!txt.contains("/")) {
                onCerrarParentesisFactorizado(txt, !txt.contains(")"));
            }else {
                onCerrarParentesisFactorizado(txt, !txt.endsWith(")") && txt.lastIndexOf("x") > txt.lastIndexOf("(") && txt.lastIndexOf("(") > txt.lastIndexOf("/"));
            }
        }
    }
    private static void onCerrarParentesisFactorizado(String txt, boolean condicion){
        if (txt.contains("(") && txt.contains("x") && condicion) {
            if (!(txt.endsWith("-") || txt.endsWith("+") || txt.endsWith(".") || txt.endsWith("/"))) {
                pantallaFunciones.setText(txt + ")");
            }
        }
    }
    public static boolean onCambiarArco_NFUN(){
        boolean arcFun;
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
        return arcFun;
    }
    public static void dibujarGrafico_MFUN(double offsetX, double offsetY, double scale) {
        String txt = pantallaFunciones.getText();
        if(expresionDibujarGraficoCorrecta(txt)) {
            // Para gráficar la función
            GraphicsContext gc = canvasFunciones.getGraphicsContext2D();
            gc.clearRect(0, 0, canvasFunciones.getWidth(), canvasFunciones.getHeight());
            dibujarEjes_MFUN(offsetX, offsetY, scale);
            gc.setStroke(Color.BLUE);
            gc.setLineWidth(2);
            double centerX = canvasFunciones.getWidth() / 2 + offsetX;
            double centerY = canvasFunciones.getHeight() / 2 + offsetY;
            gc.beginPath();

            String opComplejo = buscarOperadorComplejo_MOC(txt);
            double multiplicandoSegundo = calcularMultiplicandoSegundo(txt,opComplejo);
            double multiplicandoDelante = calcularMultiplicandoDelante(txt,opComplejo);
            if(txt.startsWith("-(")){
                txt = txt.substring(1);
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
    public static void dibujarEjes_MFUN(double offsetX, double offsetY, double scale) {
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
    private static boolean expresionDibujarGraficoCorrecta(String txt){
        boolean parentesisIncorrectos = true;
        if(txt.contains("(")){
            if(!txt.contains(")")) {
                parentesisIncorrectos = false;
            }
        }
        return txt.contains("x") && !txt.endsWith("-") && !txt.endsWith("+") && !txt.endsWith(".") && !txt.endsWith("^") && parentesisIncorrectos;
    }
    private static double calcularNumXFunciones(String txt, String opComplejo){
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
                    String numXfunOpComplejo = txt.substring(txt.indexOf("(") + 1, txt.indexOf("x"));
                    if(numXfunOpComplejo.contains("-")){
                        return operandos_MGEN(txt.substring(txt.indexOf("(")+2,txt.indexOf("x")), true);
                    }else {
                        return operandos_MGEN(numXfunOpComplejo, false);
                    }
                }
            }else {
                if (txt.substring(0, indiceX).contains("-")) {
                    return operandos_MGEN(txt.substring(1, indiceX), true);
                } else {
                    return operandos_MGEN(txt.substring(0, indiceX), false);
                }
            }
        }
    }


    private static double calcularDesplazamientoYPotencia(String txt, String opComplejo,String op, boolean menos){
        String aux = txt.substring(txt.indexOf("^") + 1);
        String cadenaOperando = aux.substring(aux.lastIndexOf(op) + 1);
        if (txt.contains("(") && opComplejo == null && txt.contains("^")) {
            if (aux.contains(op)) {
                return operandos_MGEN(cadenaOperando, menos);
            } else {
                return 0;
            }
        }else {
            return operandos_MGEN(cadenaOperando, menos);
        }
    }
    private static double calcularDesplazamientoYSinPotencia(String txt, String opComplejo,String op, boolean menos){
        if (txt.contains("(") && opComplejo == null) {
            if(txt.indexOf(op) > txt.indexOf(")")){
                return operandos_MGEN(txt.substring(txt.indexOf(op)+1), menos);
            }else {
                return operandos_MGEN(txt.substring(txt.indexOf(op) + 1, txt.indexOf(")")), menos);
            }
        } else {
            return operandos_MGEN(txt.substring(txt.indexOf(op) + 1),menos);
        }
    }
    private static double calcularDesplazamientoY(String txt, String opComplejo){
        if(txt.contains("^")) { // desplazamiento, para 3x +3, (3x +3), y (3x+1)^2+3, para todos estos más 3
            if (txt.substring(txt.indexOf("^")).contains("+")) {
               return calcularDesplazamientoYPotencia(txt,opComplejo,"+", false);
            } else if (txt.substring(txt.indexOf("^")+2).contains("-")) { // el +2 para que si es algo del tipo (x+1)^-3 no me coja el -3 como desplazmiento
                return calcularDesplazamientoYPotencia(txt,opComplejo,"-", true);
            }
        }else {
            if(!txt.matches("^-?(\\d+\\.\\d+|\\d+|e|π|φ|)\\(?-?(\\d+\\.\\d+|\\d+|e|π|φ|)x\\)?$")) {
                if(!(txt.contains(")") && txt.contains("x)"))) {
                    if (txt.contains("+")) {
                        return calcularDesplazamientoYSinPotencia(txt, opComplejo, "+", false);
                    } else if (txt.contains("-")) {
                        return calcularDesplazamientoYSinPotencia(txt, opComplejo, "-", true);
                    }
                }else return 0;
            }
        }
        return 0;
    }
    private static double funcionDividendo(String txt, double multiplicandoDelante, double multiplicandoSegundo, double x){
        String yAntes = txt.substring(0, txt.indexOf("/"));
        String opComplejoAntes = buscarOperadorComplejo_MOC(yAntes);
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
                yDespuesD = operandos_MGEN(yDespues.substring(1), true);
            }else {
                yDespuesD = operandos_MGEN(yDespues, false);
            }
        }
        return (yAntesD / yDespuesD) + desplazamientoYDvididendo;
    }
    private static double calcularDesplazamientoDividendo(String txt){
        if (txt.lastIndexOf("+") > txt.lastIndexOf(")")) {
            return operandos_MGEN(txt.substring(txt.lastIndexOf("+") + 1),false);
        } else if (txt.lastIndexOf("-") > txt.lastIndexOf(")") || txt.lastIndexOf("-") >  txt.lastIndexOf("^")+2) {
            if(txt.lastIndexOf("-") == txt.lastIndexOf("^")+1){
                return 0;
            }else {
                return operandos_MGEN(txt.substring(txt.lastIndexOf("-") + 1), true);
            }
        }else {
            return 0;
        }
    }
    private static double calcularDesplazamiento(String txt, boolean segundo){
        String opComplejo = buscarOperadorComplejo_MOC(txt);
        if(txt.contains("(") && opComplejo == null && txt.contains("^") && !(txt.indexOf("x")+1 == txt.indexOf(")")) && !segundo){
            return operandos_MGEN(txt.substring(txt.indexOf("x")+2,txt.indexOf(")")),txt.charAt(txt.indexOf("x")+1) == '-');
        } else if (txt.contains("(") && !(txt.indexOf("x")+1 == txt.indexOf(")")) && segundo) {
            if(txt.contains("^") && txt.indexOf("^") < txt.indexOf(")")){
                txt = txt.substring(txt.indexOf("^"),txt.indexOf(")"));
                if(txt.contains("-")){
                    return  operandos_MGEN(txt.substring(txt.indexOf("-")+1),true);
                }else if (txt.contains("+")){
                    return  operandos_MGEN(txt.substring(txt.indexOf("+")+1),false);
                }else {
                    return 0;
                }
            }else {
                return operandos_MGEN(txt.substring(txt.indexOf("x") + 2, txt.indexOf(")")), txt.charAt(txt.indexOf("x") + 1) == '-');
            }
        } else {
            return 0;
        }
    }
    private static double calcularExponenteFuncion(String txt){
        Pattern patronExponente = Pattern.compile("\\^(-?(\\d+\\.\\d+|\\d+|e|π|φ))");
        if(!txt.contains("^") && !txt.contains("√")){
            return 1;
        }else if(txt.contains("^")){
            Matcher matcherExponente = patronExponente.matcher(txt);
            if (matcherExponente.find()) {
                String exponenteStr = matcherExponente.group(1);
                if (exponenteStr.startsWith("-")) {
                    return operandos_MGEN(exponenteStr.substring(1), true);
                } else {
                    return operandos_MGEN(exponenteStr, false);
                }
            }
        } else if (txt.contains("√")) {
            if(txt.startsWith("-")){
                return operandos_MGEN(txt.substring(1,txt.indexOf("√")), true);
            }else {
                return operandos_MGEN(txt.substring(0,txt.indexOf("√")), false);
            }
        }
        return 1;
    }

    private static double funcionRadical(String txt, String opComplejo, double y, double x, double multiplicandoDelante){
        double radicando = calcularExponenteFuncion(txt);
        double xDentroRaizNum = calcularNumXFunciones(txt.substring(txt.indexOf("√")+1, txt.indexOf("x")+1), opComplejo);
        double desplazamientoX = 0;
        double desplazamientoY = 0;
        if(txt.contains("(")){
            if(txt.contains("+")){
                desplazamientoX = operandos_MGEN(txt.substring(txt.indexOf("+")+1, txt.indexOf(")")), false);
            } else if (txt.contains("-")) {
                if(txt.startsWith("-")){
                    txt = txt.substring(1);
                    desplazamientoX = operandos_MGEN(txt.substring(txt.indexOf("-")+1, txt.indexOf(")")), true);
                    txt = "-"+txt;
                }else {
                    desplazamientoX = operandos_MGEN(txt.substring(txt.indexOf("-")+1, txt.indexOf(")")), true);
                }
            }
        }

        if(txt.contains("(") && txt.lastIndexOf("+") > txt.indexOf(")")){
            desplazamientoY = operandos_MGEN(txt.substring(txt.lastIndexOf("+")+1),false);
        } else if (txt.contains("(") && txt.lastIndexOf("-") > txt.indexOf(")")) {
            desplazamientoY = operandos_MGEN(txt.substring(txt.lastIndexOf("-")+1),true);
        } else if (!txt.contains("(") && txt.contains("x+")) {
            desplazamientoY = operandos_MGEN(txt.substring(txt.lastIndexOf("+")+1),false);
        }else if (!txt.contains("(") && txt.contains("x-")) {
            desplazamientoY = operandos_MGEN(txt.substring(txt.lastIndexOf("-")+1),true);
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


    private static double calcularOpComplejoFun(String txt, double x, String opComplejo, double y){
        double multiplicandoX, desplazamientoX;
        double exponente = 1;
        String dentroOpComplejo = txt.substring(txt.indexOf("(")+1, txt.indexOf(")"));

        if(dentroOpComplejo.startsWith("-x")){
            multiplicandoX = -1;
        }else if(dentroOpComplejo.startsWith("x")){
            multiplicandoX = 1;
        }else {
            if(dentroOpComplejo.startsWith("-")){
                multiplicandoX = operandos_MGEN(dentroOpComplejo.substring(1,dentroOpComplejo.indexOf("x")), true);
            }else {
                multiplicandoX = operandos_MGEN(dentroOpComplejo.substring(0,dentroOpComplejo.indexOf("x")), false);
            }
        }

        if(dentroOpComplejo.contains("+")){
            desplazamientoX = operandos_MGEN(dentroOpComplejo.substring(dentroOpComplejo.indexOf("+")+1), false);
        }else if (dentroOpComplejo.contains("-") && dentroOpComplejo.lastIndexOf("-")> dentroOpComplejo.indexOf("^")+1){
            desplazamientoX = operandos_MGEN(dentroOpComplejo.substring(dentroOpComplejo.lastIndexOf("-")+1), true);
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
                desplazamientoY = operandos_MGEN(desplazamientoYtrigoFun.getText().substring(0,desplazamientoYtrigoFun.getText().indexOf("x")), false) *x;
            }else {
                desplazamientoY = operandos_MGEN(desplazamientoYtrigoFun.getText(), false);
            }
        }

        double multiplicandoDelanteTrigo =1;
        if(txt.startsWith("-"+opComplejo)){
            multiplicandoDelanteTrigo = -1;
        } else if (txt.contains("·")) {
            if(txt.startsWith("-")){
                multiplicandoDelanteTrigo = operandos_MGEN(txt.substring(1,txt.indexOf("·")-1),true);
            }else {
                multiplicandoDelanteTrigo = operandos_MGEN(txt.substring(0,txt.indexOf("·")-1),false);
            }
        }

        // Quizás factorizar con un hasmap
        if(txt.contains("ArcSen(")){
            y = OperacionesMath.calcularFuncionTrigo(Math::asin,operadorLabelFun.getText(),x,exponente,multiplicandoX,desplazamientoX,txt, desplazamientoY,multiplicandoDelanteTrigo);
        }else if(txt.contains("ArcCos(")){
            y = OperacionesMath.calcularFuncionTrigo(Math::acos,operadorLabelFun.getText(),x,exponente,multiplicandoX,desplazamientoX,txt, desplazamientoY,multiplicandoDelanteTrigo);
        }else if(txt.contains("ArcTan(")){
            y = OperacionesMath.calcularFuncionTrigo(Math::atan,operadorLabelFun.getText(),x,exponente,multiplicandoX,desplazamientoX,txt, desplazamientoY,multiplicandoDelanteTrigo);
        } else if(txt.contains("sen(")){
            y = OperacionesMath.calcularFuncionTrigo(Math::sin,operadorLabelFun.getText(),x,exponente,multiplicandoX,desplazamientoX,txt, desplazamientoY,multiplicandoDelanteTrigo);
        }else if(txt.contains("cos(")){
            y = OperacionesMath.calcularFuncionTrigo(Math::cos,operadorLabelFun.getText(),x,exponente,multiplicandoX,desplazamientoX,txt, desplazamientoY,multiplicandoDelanteTrigo);
        }else if(txt.contains("tan(")){
            y = OperacionesMath.calcularFuncionTrigo(Math::tan, operadorLabelFun.getText(), x, exponente, multiplicandoX, desplazamientoX, txt, desplazamientoY,multiplicandoDelanteTrigo);
        }else if(txt.contains("ln(")){
            y = OperacionesMath.calcularFuncionTrigo(Math::log, operadorLabelFun.getText(), x, exponente, multiplicandoX, desplazamientoX, txt, desplazamientoY,multiplicandoDelanteTrigo);
        }
        return y;
    }

    private static double calcularMultiplicandoDelante(String txt, String opComplejo){
        if(txt.contains("/")) {
            if (txt.substring(0, txt.indexOf("/")).contains("(")) {
                return calcularMultiplicadorDelante(txt, opComplejo);
            }
        } else if (txt.contains("(") && opComplejo == null) {
            if(txt.startsWith("-(")){
                return  -1;
            }else {
                if(txt.contains("√")){
                    if(txt.substring(txt.indexOf("√") + 1, txt.indexOf("(")).isEmpty()){
                        return  1;
                    }else {
                        return calcularMultiplicadorDelante(txt.substring(txt.indexOf("√") + 1), opComplejo);
                    }
                }else {
                    return calcularMultiplicadorDelante(txt, opComplejo);
                }
            }
        }
        return 1;
    }

    private static double calcularMultiplicandoSegundo(String txt, String opComplejo){
        if(txt.contains("/")){
            if(txt.substring(txt.indexOf("/")+1).startsWith("-(")){
                return  -1;
            }else {
                return calcularMultiplicadorDelante(txt.substring(txt.indexOf("/")+1), opComplejo);
            }
        }
        return 1;
    }

    private static double calcularMultiplicadorDelante(String txt, String opComplejo){
        if(txt.startsWith("-(")){
            return -1;
        } else if (txt.contains("(") && opComplejo == null && !txt.startsWith("(")) {
            if(txt.startsWith("-")){
                return operandos_MGEN(txt.substring(1,txt.indexOf("(")),true );
            }else {
                return operandos_MGEN(txt.substring(0,txt.indexOf("(")),false );
            }
        }
        return 1;
    }
}
