package com.ingelcom.cajachica.Herramientas;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validaciones {

    public static boolean validarTexto(String texto, String expresion) {
        Pattern pattern = Pattern.compile(expresion);
        Matcher matcher = pattern.matcher(texto);

        if (matcher.matches())
            return true;
        else
            return false;
    }

    public static boolean validarNombre(String texto) {
        return validarTexto(texto, "^[A-Za-zÁÉÍÓÚáéíóúñÑ\\s'-]+$");
    }

    public static boolean validarIdentidad(String texto) {
        return validarTexto(texto, "^\\d{13}$");
    }
}