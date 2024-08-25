package com.ingelcom.cajachica.Herramientas;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validaciones {

    public static boolean nombreCompleto(String texto) {
        Pattern pattern = Pattern.compile("^[a-zA-Z]+$");
        Matcher matcher = pattern.matcher(texto);

        if (matcher.matches())
            return true;
        else
            return false;
    }
}
