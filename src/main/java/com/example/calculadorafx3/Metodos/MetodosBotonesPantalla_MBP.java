package com.example.calculadorafx3.Metodos;

import static com.example.calculadorafx3.Metodos.MetodosGenerales_MGEN.*;
import static com.example.calculadorafx3.Metodos.MetodosParaMBP___MP_MBP.*;

public class MetodosBotonesPantalla_MBP extends Atributos{

    public static boolean onButtonAC_MBP(boolean pantallaFuncionesBolean, boolean pantallaEcuacionesBolean) {
        if (pantallaFuncionesBolean){
            onACEcuaciones_MP_MBP(pantallaFunciones);
            return true;
        }else if(!pantallaEcuacionesBolean) {
            onACEcuaciones_MP_MBP(pantalla);
        }else{
            if(rbX2.isSelected()){
                onACEcuaciones_MP_MBP(labelx2);
            } else if (rbx.isSelected()) {
                onACEcuaciones_MP_MBP(labelx1);
            } else if (rbc.isSelected()) {
                onACEcuaciones_MP_MBP(labelC);
            } else if (rbr.isSelected()) {
                onACEcuaciones_MP_MBP(labelR);
            }
        }
        return false;
    }
    public static void onButtonMasMenos_MBP(boolean pantallaFuncionesBolean, boolean pantallaEcuacionesBolean, boolean escribirDentroFunB) {
        if (pantallaFuncionesBolean){
            if(escribirDentroFunB){
                escrbirMasMenosPantalla_MP_MBP(pantallaFunciones);
            }else {
                if(operadorLabelFun.getText().equals("-")){
                    operadorLabelFun.setText("+");
                }else {
                    operadorLabelFun.setText("-");
                }
            }
        }else if(!pantallaEcuacionesBolean) {
            escrbirMasMenosPantalla_MP_MBP(pantalla);
        }else{
            if(rbX2.isSelected()){
                escrbirMasMenosPantalla_MP_MBP(labelx2);
            } else if (rbx.isSelected()) {
                escrbirMasMenosPantalla_MP_MBP(labelx1);
            } else if (rbc.isSelected()) {
                escrbirMasMenosPantalla_MP_MBP(labelC);
            } else if (rbr.isSelected()) {
                escrbirMasMenosPantalla_MP_MBP(labelR);
            }
        }
    }

    public static void onButtonPunto_MBP(boolean pantallaFuncionesBolean, boolean pantallaEcuacionesBolean, boolean escribirDentroFunB){
        if (pantallaFuncionesBolean){
            if(escribirDentroFunB) {
                escribirPuntoPantalla_MP_MBP(pantallaFunciones, true);
            }else {
                escribirPuntoPantalla_MP_MBP(desplazamientoYtrigoFun, true);
            }
        }else if(!pantallaEcuacionesBolean) {
            escribirPuntoPantalla_MP_MBP(pantalla, false);
        }else{
            if(rbX2.isSelected()){
                escribirPuntoPantalla_MP_MBP(labelx2, false);
            } else if (rbx.isSelected()) {
                escribirPuntoPantalla_MP_MBP(labelx1, false);
            } else if (rbc.isSelected()) {
                escribirPuntoPantalla_MP_MBP(labelC, false);
            } else if (rbr.isSelected()) {
                escribirPuntoPantalla_MP_MBP(labelR, false);
            }
        }
    }

    public static void onAtras_MBP(boolean pantallaFuncionesBolean, boolean pantallaEcuacionesBolean,boolean escribirDentroFunB) {
        if (pantallaFuncionesBolean){
            if(escribirDentroFunB){
                onAtrasEcuaciones_MP_MBP(pantallaFunciones);
            }else {
                onAtrasEcuaciones_MP_MBP(desplazamientoYtrigoFun);
            }
        }else if(!pantallaEcuacionesBolean){
            onAtrasEcuaciones_MP_MBP(pantalla);
        }else {
            if(rbX2.isSelected()){
                onAtrasEcuaciones_MP_MBP(labelx2);
            } else if (rbx.isSelected()) {
                onAtrasEcuaciones_MP_MBP(labelx1);
            } else if (rbc.isSelected()) {
                onAtrasEcuaciones_MP_MBP(labelC);
            } else if (rbr.isSelected()) {
                onAtrasEcuaciones_MP_MBP(labelR);
            }
        }
    }
    public static void onButton00_MBP(boolean pantallaFuncionesBolean, boolean pantallaEcuacionesBolean) {
        if(pantallaFuncionesBolean){
            onButton00Pantalla_MP_MBP(pantallaFunciones, true);
        }else if(!pantallaEcuacionesBolean) {
            onButton00Pantalla_MP_MBP(pantalla, false);
        }else{
            if(rbX2.isSelected()){
                onButton00Pantalla_MP_MBP(labelx2, false);
            } else if (rbx.isSelected()) {
                onButton00Pantalla_MP_MBP(labelx1, false);
            } else if (rbc.isSelected()) {
                onButton00Pantalla_MP_MBP(labelC, false);
            } else if (rbr.isSelected()) {
                onButton00Pantalla_MP_MBP(labelR, false);
            }
        }
    }
    public static void agregarNumero_MBP(String num, boolean pantallaFuncionesBolean, boolean pantallaEcuacionesBolean, boolean escribirDentroFunB) {
        if(pantallaFuncionesBolean){
            if(escribirDentroFunB){
                escribirNumPantalla_MGEN(num, pantallaFunciones, true);
            }else {
                escribirNumPantalla_MGEN(num, desplazamientoYtrigoFun, true);
            }
        }else if(!pantallaEcuacionesBolean) {
            escribirNumPantalla_MGEN(num,pantalla, false);
        }else{
            if(rbX2.isSelected()){
                escribirNumPantalla_MGEN(num,labelx2, false);
            } else if (rbx.isSelected()) {
                escribirNumPantalla_MGEN(num,labelx1, false);
            } else if (rbc.isSelected()) {
                escribirNumPantalla_MGEN(num,labelC, false);
            } else if (rbr.isSelected()) {
                escribirNumPantalla_MGEN(num,labelR, false);
            }
        }
    }
    public static void expresionesMatematicas_MBP(char num, boolean pantallaFuncionesBolean, boolean pantallaEcuacionesBolean, boolean escribirDentroFunB){
        if(pantallaFuncionesBolean){
            if(escribirDentroFunB){
                escribirExpresionMatematicaLabel_MGEN(pantallaFunciones, num, true, pantallaEcuacionesBolean, true);
            }else {
                escribirExpresionMatematicaLabel_MGEN(desplazamientoYtrigoFun,num, true, pantallaEcuacionesBolean, false);
            }
        }else if(!pantallaEcuacionesBolean){
            escribirExpresionMatematicaLabel_MGEN(pantalla,num, false, false, escribirDentroFunB);
        }else {
            if(rbX2.isSelected()){
                escribirExpresionMatematicaLabel_MGEN(labelx2,num, false, true, escribirDentroFunB);
            } else if (rbx.isSelected()) {
                escribirExpresionMatematicaLabel_MGEN(labelx1,num, false, true, escribirDentroFunB);
            } else if (rbc.isSelected()) {
                escribirExpresionMatematicaLabel_MGEN(labelC,num, false, true, escribirDentroFunB);
            } else if (rbr.isSelected()) {
                escribirExpresionMatematicaLabel_MGEN(labelR,num, false, true, escribirDentroFunB);
            }
        }
    }
}
