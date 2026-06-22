package utils;

import modelos.Usuario;
import modelos.Apostador;

public class SesionUsuario {
    private static Usuario usuarioActual;
    private static Apostador apostadorActual;

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static void setUsuarioActual(Usuario usuarioActual) {
        SesionUsuario.usuarioActual = usuarioActual;
    }

    public static Apostador getApostadorActual() {
        return apostadorActual;
    }

    public static void setApostadorActual(Apostador apostadorActual) {
        SesionUsuario.apostadorActual = apostadorActual;
    }
}
