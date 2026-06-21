package vistas;

import modelos.Usuario;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class VentanaPrincipal extends JFrame {

    // Paleta premium de la aplicación
    private static final Color BG_APP          = new Color(11, 19, 32);     // #0B1320
    private static final Color BG_HEADER       = new Color(14, 23, 38);     // #0E1726
    private static final Color BG_CARD         = new Color(22, 32, 50);     // #162032
    private static final Color BORDER_CARD     = new Color(44, 58, 83);     // #2C3A53
    private static final Color TEXT_GOLD       = new Color(226, 185, 74);   // #E2B94A
    private static final Color TEXT_LIGHT      = new Color(194, 203, 224);  // #C2CBE0
    private static final Color TEXT_MUTED      = new Color(139, 149, 165);  // #8B95A5
    private static final Color TEXT_CYAN       = new Color(0, 212, 255);    // #00D4FF
    private static final Color TEXT_RED        = new Color(255, 107, 107);  // #FF6B6B

    private CardLayout cardLayout;
    private JPanel centralPanel;
    private List<NavButton> navButtons;

    public VentanaPrincipal(Usuario usuarioActual) {
        setTitle("World Cup Trophy Elite 2026 — " + usuarioActual.getUsername());
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // root panel
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_APP);

        // ---- 1. BARRA SUPERIOR (HEADER) ----
        JPanel topBar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(BG_HEADER);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(BORDER_CARD);
                g2.fillRect(0, getHeight() - 1, getWidth(), 1); // Línea divisora inferior
                g2.dispose();
                super.paintComponent(g);
            }
        };
        topBar.setOpaque(false);
        topBar.setPreferredSize(new Dimension(0, 65));
        topBar.setBorder(new EmptyBorder(0, 25, 0, 25));

        // Izquierda: Logo y títulos
        JPanel topLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        topLeft.setOpaque(false);
        topLeft.setBorder(new EmptyBorder(12, 0, 0, 0));

        JLabel lblLogo = new JLabel("🏆");
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JLabel lblAppName = new JLabel("MUNDIAL 2026");
        lblAppName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblAppName.setForeground(Color.WHITE);

        JLabel lblDot = new JLabel("•");
        lblDot.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDot.setForeground(TEXT_GOLD);

        JLabel lblSub = new JLabel("Dashboard Administrativo");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(TEXT_LIGHT);

        topLeft.add(lblLogo);
        topLeft.add(lblAppName);
        topLeft.add(lblDot);
        topLeft.add(lblSub);

        // Derecha: Usuario y Cerrar sesión
        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        topRight.setOpaque(false);
        topRight.setBorder(new EmptyBorder(12, 0, 0, 0));

        // Badge de Rol
        boolean isAdmin = "admin".equalsIgnoreCase(usuarioActual.getRol());
        String roleLabel = isAdmin ? "ADMINISTRADOR" : "APOSTADOR";
        Color badgeBg = isAdmin ? new Color(255, 107, 107, 30) : new Color(0, 212, 255, 30);
        Color badgeBorder = isAdmin ? TEXT_RED : TEXT_CYAN;

        JLabel lblRoleBadge = new JLabel(" " + roleLabel + " ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(badgeBg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(badgeBorder);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblRoleBadge.setFont(new Font("Consolas", Font.BOLD, 10));
        lblRoleBadge.setForeground(badgeBorder);
        lblRoleBadge.setOpaque(false);
        lblRoleBadge.setBorder(new EmptyBorder(3, 8, 3, 8));

        JLabel lblUserIcon = new JLabel("👤");
        lblUserIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));

        JLabel lblUsername = new JLabel(usuarioActual.getUsername());
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsername.setForeground(Color.WHITE);

        // Botón Logout
        JButton btnLogout = new JButton("Cerrar sesión") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? new Color(255, 107, 107, 30) : new Color(255, 107, 107, 10);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(TEXT_RED);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 8, 8));
                g2.dispose();
                
                FontMetrics fm = g2.getFontMetrics(getFont());
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g.setColor(TEXT_RED);
                g.drawString(getText(), textX, textY);
            }
        };
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setContentAreaFilled(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setPreferredSize(new Dimension(110, 30));
        btnLogout.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new VentanaLogin().setVisible(true));
        });

        topRight.add(lblRoleBadge);
        topRight.add(lblUserIcon);
        topRight.add(lblUsername);
        topRight.add(Box.createHorizontalStrut(5));
        topRight.add(btnLogout);

        topBar.add(topLeft, BorderLayout.WEST);
        topBar.add(topRight, BorderLayout.EAST);
        root.add(topBar, BorderLayout.NORTH);

        // ---- 2. CONTENEDOR CENTRAL DE PESTAÑAS Y VISTAS ----
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setOpaque(false);

        // Barra de Pestañas Personalizada (Navigation Bar)
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(BG_HEADER);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(BORDER_CARD);
                g2.fillRect(0, getHeight() - 1, getWidth(), 1); // Separador
                g2.dispose();
                super.paintComponent(g);
            }
        };
        navBar.setOpaque(false);
        navBar.setPreferredSize(new Dimension(0, 48));
        navBar.setBorder(new EmptyBorder(0, 20, 0, 20));

        // Contenedor principal con CardLayout
        cardLayout = new CardLayout();
        centralPanel = new JPanel(cardLayout);
        centralPanel.setOpaque(false);

        navButtons = new ArrayList<>();

        // Registrar Paneles
        agregarPestaña(navBar, "  👥  Apostadores  ", "Apostadores", new PanelApostadores(isAdmin));
        agregarPestaña(navBar, "  🔮  Predicciones  ", "Predicciones", new PanelPredicciones(isAdmin));
        agregarPestaña(navBar, "  🏆  Resultados  ", "Resultados", new PanelResultados());

        if (isAdmin) {
            agregarPestaña(navBar, "  🛡️  Equipos  ", "Equipos", new PanelEquipos());
            agregarPestaña(navBar, "  ⚽  Partidos  ", "Partidos", new PanelPartidos());
        }

        // Seleccionar la primera pestaña por defecto
        if (!navButtons.isEmpty()) {
            seleccionarPestaña(navButtons.get(0));
        }

        mainContainer.add(navBar, BorderLayout.NORTH);
        mainContainer.add(centralPanel, BorderLayout.CENTER);
        root.add(mainContainer, BorderLayout.CENTER);

        // ---- 3. BARRA DE ESTADO INFERIOR ----
        JPanel statusBar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(BG_HEADER);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(BORDER_CARD);
                g2.fillRect(0, 0, getWidth(), 1); // Línea superior
                g2.dispose();
                super.paintComponent(g);
            }
        };
        statusBar.setOpaque(false);
        statusBar.setPreferredSize(new Dimension(0, 30));
        statusBar.setBorder(new EmptyBorder(0, 25, 0, 25));

        JLabel lblStatus = new JLabel("⚡ Conectado como: " + usuarioActual.getUsername() + " | Rol: " + usuarioActual.getRol().toUpperCase());
        lblStatus.setFont(new Font("Consolas", Font.PLAIN, 11));
        lblStatus.setForeground(TEXT_MUTED);

        JLabel lblRight = new JLabel("FIFA World Cup 2026 Admin Console  •  v1.4.0");
        lblRight.setFont(new Font("Consolas", Font.PLAIN, 11));
        lblRight.setForeground(TEXT_MUTED);

        statusBar.add(lblStatus, BorderLayout.WEST);
        statusBar.add(lblRight, BorderLayout.EAST);
        root.add(statusBar, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void agregarPestaña(JPanel navBar, String title, String cardName, JPanel panel) {
        centralPanel.add(panel, cardName);
        NavButton btn = new NavButton(title, cardName);
        btn.addActionListener(e -> seleccionarPestaña(btn));
        navButtons.add(btn);
        navBar.add(btn);
    }

    private void seleccionarPestaña(NavButton seleccionado) {
        for (NavButton btn : navButtons) {
            btn.setActive(btn == seleccionado);
        }
        cardLayout.show(centralPanel, seleccionado.getCardName());
        repaint();
    }

    // --- CLASE DE BOTÓN DE NAVEGACIÓN PERSONALIZADO ---
    private class NavButton extends JButton {
        private final String cardName;
        private boolean isActive = false;

        public NavButton(String text, String cardName) {
            super(text);
            this.cardName = cardName;
            setFont(new Font("Segoe UI", Font.BOLD, 15));
            setForeground(TEXT_LIGHT);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(190, 48));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!isActive) {
                        setForeground(Color.WHITE);
                    }
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    if (!isActive) {
                        setForeground(TEXT_LIGHT);
                    }
                }
            });
        }

        public String getCardName() {
            return cardName;
        }

        public void setActive(boolean active) {
            this.isActive = active;
            setForeground(active ? TEXT_GOLD : TEXT_LIGHT);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isActive) {
                // Fondo activo
                g2.setColor(new Color(22, 32, 50, 150));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Línea inferior de indicador activo (Dorado)
                g2.setColor(TEXT_GOLD);
                g2.fillRect(0, getHeight() - 3, getWidth(), 3);
            } else if (getModel().isRollover()) {
                // Fondo hover suave
                g2.setColor(new Color(22, 32, 50, 80));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            // Pintar texto
            FontMetrics fm = g2.getFontMetrics(getFont());
            int textX = (getWidth() - fm.stringWidth(getText())) / 2;
            int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.setColor(getForeground());
            g2.drawString(getText(), textX, textY);

            g2.dispose();
        }
    }
}
