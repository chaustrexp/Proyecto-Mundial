package utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FlagManager {

    private static final Map<String, String> countryCodeMap = new HashMap<>();
    private static final Map<String, ImageIcon> iconCache = new ConcurrentHashMap<>();

    static {
        // Mapeo de nombres de equipos a códigos ISO de FlagCDN
        addCode("méxico", "mx"); addCode("sudáfrica", "za"); addCode("república de corea", "kr"); addCode("chequia", "cz");
        addCode("canadá", "ca"); addCode("bosnia y herzegovina", "ba"); addCode("catar", "qa"); addCode("suiza", "ch");
        addCode("brasil", "br"); addCode("marruecos", "ma"); addCode("haití", "ht"); addCode("escocia", "gb-sct");
        addCode("estados unidos", "us"); addCode("paraguay", "py"); addCode("australia", "au"); addCode("turquía", "tr");
        addCode("alemania", "de"); addCode("curazao", "cw"); addCode("costa de marfil", "ci"); addCode("ecuador", "ec");
        addCode("países bajos", "nl"); addCode("japón", "jp"); addCode("suecia", "se"); addCode("túnez", "tn");
        addCode("bélgica", "be"); addCode("egipto", "eg"); addCode("ri de irán", "ir"); addCode("nueva zelanda", "nz");
        addCode("españa", "es"); addCode("cabo verde", "cv"); addCode("arabia saudí", "sa"); addCode("uruguay", "uy");
        addCode("francia", "fr"); addCode("senegal", "sn"); addCode("irak", "iq"); addCode("noruega", "no");
        addCode("argentina", "ar"); addCode("argelia", "dz"); addCode("austria", "at"); addCode("jordania", "jo");
        addCode("portugal", "pt"); addCode("rd congo", "cd"); addCode("uzbekistán", "uz"); addCode("colombia", "co");
        addCode("inglaterra", "gb-eng"); addCode("croacia", "hr"); addCode("ghana", "gh"); addCode("panamá", "pa");
        addCode("gales", "gb-wls"); addCode("italia", "it"); addCode("chile", "cl"); addCode("perú", "pe");
    }

    private static void addCode(String name, String code) {
        countryCodeMap.put(name.toLowerCase(), code);
    }

    public static String getCode(String teamName) {
        if (teamName == null) return "un"; // UN flag or unknown
        return countryCodeMap.getOrDefault(teamName.toLowerCase(), "un");
    }

    /**
     * Carga la bandera de forma asíncrona para no congelar la interfaz.
     */
    public static void setFlagIconAsync(JLabel label, String teamName, int width, int height) {
        String code = getCode(teamName);
        String cacheKey = code + "_" + width + "x" + height;

        // Si ya está en caché, lo mostramos de inmediato
        if (iconCache.containsKey(cacheKey)) {
            label.setIcon(iconCache.get(cacheKey));
            label.setText(""); // Ocultamos el emoji de texto si había uno
            return;
        }

        // Si no está, iniciamos un SwingWorker para descargarla en segundo plano
        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                // FlagCDN URL (w40 o w80 dependiendo del tamaño necesario para mejor calidad)
                int requestWidth = width > 40 ? 80 : 40;
                String urlStr = "https://flagcdn.com/w" + requestWidth + "/" + code + ".png";
                URL url = new URL(urlStr);
                BufferedImage img = ImageIO.read(url);
                if (img != null) {
                    Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaled);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    ImageIcon icon = get();
                    if (icon != null) {
                        iconCache.put(cacheKey, icon);
                        label.setIcon(icon);
                        label.setText(""); // Ocultamos el emoji de texto
                        label.revalidate();
                        label.repaint();
                    }
                } catch (Exception e) {
                    // Si falla la descarga, mantenemos el texto original del label (el emoji o texto por defecto)
                    System.err.println("No se pudo cargar la bandera para " + teamName + " (" + code + ")");
                }
            }
        };
        worker.execute();
    }
}
