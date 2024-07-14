package com.example.calculadorafx3.Operaciones;

public class OperacionesMath {
    // Definiciones de las operaciones que voy a usar
    public static final OperacionesAritmeticas ADD = Double::sum;
    public static final OperacionesAritmeticas SUBTRACT = (a, b) -> a - b;
    public static final OperacionesAritmeticas MULTIPLY = (a, b) -> a * b;
    public static final OperacionesAritmeticas DIVIDE = (a, b) -> a / b;
    public static final OperacionesAritmeticas POWER = Math::pow;

    public static double calculate(FuncionesTrigo function, String operandoDesplzamiento, double x, double exponente,
                                   double multiplicandoX, double desplazamientoX, String txt, double desplazamientoY, double multiplicandoDelante) {
        OperacionesAritmeticas operacion = getOperando(operandoDesplzamiento);
        if (txt.contains("^")) {
            return operacion.operarArit(multiplicandoDelante * function.operarTrigo((Math.pow(x,exponente)*multiplicandoX+desplazamientoX)),desplazamientoY);
        } else {
            return operacion.operarArit(multiplicandoDelante * function.operarTrigo((Math.pow(x *multiplicandoX  +desplazamientoX,exponente))),desplazamientoY);
        }
    }
    private static OperacionesAritmeticas getOperando(String operador){
        switch (operador) {
            case "+":
                return OperacionesMath.ADD;
            case "-":
                return OperacionesMath.SUBTRACT;
            case "·":
                return OperacionesMath.MULTIPLY;
            case "/":
                return OperacionesMath.DIVIDE;
            case "^":
                return OperacionesMath.POWER;
            default:
                throw new IllegalArgumentException("Operador no soportado: " + operador); // esto no va a pasar nunca pero tengo q ponerlo igualmente
        }
    }
}
