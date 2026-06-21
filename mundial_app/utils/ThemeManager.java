package utils;

import javax.swing.*;
import java.awt.*;

/**
 * Utility class to configure the Look‑And‑Feel of the application.
 * Uses the standard Nimbus LAF (bundled with the JDK) and adjusts a few UI defaults
 * so that the dark theme is actually visible (text, panels, tables, etc.).
 */
public final class ThemeManager {

    public enum Mode { LIGHT, DARK }

    private ThemeManager() {}

    /**
     * Applies the selected theme globally.
     * For {@code DARK} mode we keep Nimbus but tweak colors, fonts and rounded corners.
     */
    public static void apply(Mode mode) {
        try {
            // Nimbus is bundled with the JDK
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Global UI tweaks – font and rounded corners (Nimbus supports some of these keys)
        Font defaultFont = new Font("Inter", Font.PLAIN, 13);
        UIManager.put("defaultFont", defaultFont);
        UIManager.put("Button.arc", 12);
        UIManager.put("Component.arc", 8);
        UIManager.put("ProgressBar.arc", 8);
        UIManager.put("TextComponent.arc", 8);

        if (mode == Mode.DARK) {
            // Dark background for panels and tables (pure black)
            UIManager.put("Panel.background", new Color(10, 10, 20));
            UIManager.put("Table.background", new Color(10, 10, 20));
            UIManager.put("Table.foreground", Color.WHITE);
            UIManager.put("Table.selectionBackground", new Color(30, 30, 55));
            UIManager.put("Table.selectionForeground", Color.WHITE);
            UIManager.put("control", new Color(10, 10, 20)); // general background
            UIManager.put("info", new Color(0x4A90E2)); // accent color
            UIManager.put("nimbusBase", new Color(10, 10, 20));
            UIManager.put("nimbusBlueGrey", new Color(30, 30, 55));
            UIManager.put("nimbusLightBackground", new Color(10, 10, 20));
            UIManager.put("nimbusSelectionBackground", new Color(0x333333));
            UIManager.put("text", Color.WHITE);
            UIManager.put("Label.foreground", Color.WHITE);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("ComboBox.background", new Color(10, 10, 20));
            UIManager.put("ComboBox.foreground", Color.WHITE);
            UIManager.put("List.background", new Color(10, 10, 20));
            UIManager.put("List.foreground", Color.WHITE);
            UIManager.put("TextField.background", new Color(10, 10, 20));
            UIManager.put("TextField.foreground", Color.WHITE);
        } else {
            // Light mode defaults – let Nimbus handle them
        }
    }
}
