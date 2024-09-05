package com.ingelcom.cajachica.Herramientas;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validaciones {

    //Método para validar un String. Recibe como parámetro el texto a evaluar, y la expresión regular que se utilizará para realizar la evaluación
    public static boolean validarTexto(String texto, String expresion) {
        Pattern pattern = Pattern.compile(expresion); //Creamos un "pattern" donde establecemos la expresión regular recibida como parámetro
        Matcher matcher = pattern.matcher(texto); //Creamos un "matcher" donde utilizando el "pattern" evaluamos el texto recibido como parámetro

        if (matcher.matches()) //Si esta condición es verdadera, significa que el contenido de "texto" cumple con las condiciones evaluadas
            return true;
        else //Pero si la condición es falsa, significa que el contenido de "texto" no cumple con dichas condiciones
            return false;
    }

    //Método para validar un nombre de persona
    public static boolean validarNombre(String texto) {
        return validarTexto(texto, "^[A-Za-zÁÉÍÓÚáéíóúñÑ\\s'-]+$"); //Retorna el resultado de "validarTexto" y a este le enviamos como parámetro una expresión regular que obliga a tener cadenas de texto que consisten únicamente en letras del alfabeto (tanto mayúsculas como minúsculas, incluidas las letras con acentos y la letra "ñ"), espacios en blanco, apóstrofos (') y guiones (-)
    }

    //Método para validar la identidad (sin guiones) de una persona
    public static boolean validarIdentidad(String texto) {
        return validarTexto(texto, "^\\d{13}|\\d{15}$"); //Retorna el resultado de "validarTexto" y a este le enviamos como parámetro una expresión regular que obliga a tener 13 números
    }
}