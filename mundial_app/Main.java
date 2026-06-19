import vistas.VentanaLogin;
import utils.ThemeManager;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Apply modern dark theme (FlatLaf) before any Swing component is created
        ThemeManager.apply(ThemeManager.Mode.DARK);
        SwingUtilities.invokeLater(() -> {
            VentanaLogin login = new VentanaLogin();
            login.setVisible(true);
        });
    }
}
