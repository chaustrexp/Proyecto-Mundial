package utils;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class ValidationUtils {

    /**
     * Filtro para permitir solo letras y espacios (útil para nombres, federaciones).
     */
    public static DocumentFilter getLettersOnlyFilter(int maxLength) {
        return new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                if ((fb.getDocument().getLength() + string.length()) <= maxLength && string.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*")) {
                    super.insertString(fb, offset, string, attr);
                }
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                int currentLen = fb.getDocument().getLength() - length;
                if ((currentLen + text.length()) <= maxLength && text.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        };
    }

    /**
     * Filtro para permitir solo números, con un límite máximo de caracteres.
     */
    public static DocumentFilter getNumbersOnlyFilter(int maxLength) {
        return new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                if ((fb.getDocument().getLength() + string.length()) <= maxLength && string.matches("\\d*")) {
                    super.insertString(fb, offset, string, attr);
                }
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                int currentLen = fb.getDocument().getLength() - length;
                if ((currentLen + text.length()) <= maxLength && text.matches("\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        };
    }

    /**
     * Filtro alfanumérico sin espacios (útil para nombres de usuario).
     */
    public static DocumentFilter getAlphaNumericFilter(int maxLength) {
        return new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                if ((fb.getDocument().getLength() + string.length()) <= maxLength && string.matches("[a-zA-Z0-9_]*")) {
                    super.insertString(fb, offset, string, attr);
                }
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                int currentLen = fb.getDocument().getLength() - length;
                if ((currentLen + text.length()) <= maxLength && text.matches("[a-zA-Z0-9_]*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        };
    }

    /**
     * Filtro para el campo Grupo. Solo permite 1 letra de A-L, autoconvirtiendo a mayúscula.
     */
    public static DocumentFilter getGroupLetterFilter() {
        return new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                String upper = string.toUpperCase();
                if ((fb.getDocument().getLength() + string.length()) <= 1 && upper.matches("[A-L]")) {
                    super.insertString(fb, offset, upper, attr);
                }
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                String upper = text.toUpperCase();
                int currentLen = fb.getDocument().getLength() - length;
                // Si borra el texto, es válido dejarlo vacío
                if (text.isEmpty() || ((currentLen + text.length()) <= 1 && upper.matches("[A-L]"))) {
                    super.replace(fb, offset, length, upper, attrs);
                }
            }
        };
    }
}
