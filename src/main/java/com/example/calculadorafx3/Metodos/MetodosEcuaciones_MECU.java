package com.example.calculadorafx3.Metodos;

import javafx.scene.control.Label;
import static com.example.calculadorafx3.Metodos.MetodosGenerales_MGEN.*;


public class MetodosEcuaciones_MECU extends Atributos{
    public static void calcularEcuaciones_MECU(){
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
            mostrarAlerta_MGEN("A mo a ve, tu eres tonto o q");
        }
    }

    private static double operandoEcuaciones(Label pantalla){
        double op = 0;
        if(contieneLetraExpresion_MGEN(pantalla.getText())){
            if(pantalla.getText().startsWith("-")){
                op = devolverValor_MGEN(pantalla.getText().substring(1));
                op *=-1;
            }else {
                op = devolverValor_MGEN(pantalla.getText());
            }
        }else if(!pantalla.getText().isEmpty()){
            op = Double.parseDouble(pantalla.getText());
        }
        return op;
    }
}
