package vistas;

import modelos.Usuario;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class VentanaPrincipal extends JFrame {

    // Paleta unificada con VentanaLogin
    private static final Color BG_DARK     = new Color(10, 10, 20);
    private static final Color BG_SIDEBAR  = new Color(14, 14, 28);
    private static final Color ACCENT      = new Color(59, 130, 246);
    private static final Color GOLD        = new Color(251, 191, 36);
    private static final Color TEXT_MAIN   = new Color(241, 245, 249);
    private static final Color TEXT_HINT   = new Color(100, 116, 139);
    private static final Color HEADER_BG   = new Color(15, 15, 32);
    private static final Color DIVIDER     = new Color(30, 30, 55);

    public VentanaPrincipal(Usuario usuarioActual) {
        setTitle("Mundial 2026 — " + usuarioActual.getUsername());
        setSize(1050, 680);
        setMinimumSize(new Dimension(800, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ---- PANEL PRINCIPAL ----
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);

        // ---- BARRA SUPERIOR ----
        JPanel topBar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(HEADER_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Línea inferior separadora
                g2.setColor(DIVIDER);
                g2.fillRect(0, getHeight() - 1, getWidth(), 1);
                g2.dispose();
            }
        };
        topBar.setOpaque(false);
        topBar.setPreferredSize(new Dimension(0, 62));
        topBar.setBorder(new EmptyBorder(0, 20, 0, 20));

        // Logo + título izquierda
        JPanel topLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topLeft.setOpaque(false);
        topLeft.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel lblTrophy = new JLabel("🏆");
        lblTrophy.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));

        JLabel lblAppName = new JLabel("Mundial 2026");
        lblAppName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblAppName.setForeground(TEXT_MAIN);

        JLabel lblDot = new JLabel("·");
        lblDot.setForeground(TEXT_HINT);
        lblDot.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        JLabel lblSub = new JLabel("Sistema de Apuestas");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(TEXT_HINT);

        topLeft.add(lblTrophy);
        topLeft.add(lblAppName);
        topLeft.add(lblDot);
        topLeft.add(lblSub);

        // Info usuario + botón logout derecha
        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topRight.setOpaque(false);
        topRight.setBorder(new EmptyBorder(10, 0, 0, 0));

        // Badge de rol
        boolean isAdmin = "admin".equalsIgnoreCase(usuarioActual.getRol());
        Color badgeColor = isAdmin ? new Color(239, 68, 68) : ACCENT;
        String roleLabel = isAdmin ? "ADMIN" : "USUARIO";
        JLabel lblRoleBadge = new JLabel(roleLabel) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(badgeColor.darker());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                super.paintComponent(g);
                g2.dispose();
            }
        };
        lblRoleBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblRoleBadge.setForeground(Color.WHITE);
        lblRoleBadge.setOpaque(false);
        lblRoleBadge.setBorder(new EmptyBorder(2, 8, 2, 8));

        JLabel lblUserIcon = new JLabel("👤");
        lblUserIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));

        JLabel lblUsername = new JLabel(usuarioActual.getUsername());
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUsername.setForeground(TEXT_MAIN);

        // Botón logout
        JButton btnLogout = new JButton("Cerrar sesión") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isRollover() ? new Color(60, 20, 20) : new Color(40, 15, 15);
                g2.setColor(c);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(new Color(239, 68, 68));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setForeground(new Color(239, 68, 68));
        btnLogout.setOpaque(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.setBorder(new EmptyBorder(6, 14, 6, 14));

        btnLogout.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new VentanaLogin().setVisible(true));
        });

        topRight.add(lblRoleBadge);
        topRight.add(lblUserIcon);
        topRight.add(lblUsername);
        topRight.add(Box.createHorizontalStrut(4));
        topRight.add(btnLogout);

        topBar.add(topLeft, BorderLayout.WEST);
        topBar.add(topRight, BorderLayout.EAST);

        // ---- PESTAÑAS ----
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(BG_DARK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tabs.setOpaque(false);
        tabs.setBackground(BG_DARK);
        tabs.setForeground(TEXT_MAIN);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Cambiar colores de tabs via UIManager solo para esta instancia no es posible,
        // así que usamos setBackground/setForeground de las pestañas con un UIManager override
        UIManager.put("TabbedPane.selected",    new Color(25, 25, 50));
        UIManager.put("TabbedPane.background",  BG_DARK);
        UIManager.put("TabbedPane.foreground",  TEXT_HINT);
        UIManager.put("TabbedPane.contentAreaColor", BG_DARK);
        UIManager.put("TabbedPane.darkShadow",  BG_DARK);
        UIManager.put("TabbedPane.shadow",      DIVIDER);
        UIManager.put("TabbedPane.light",       DIVIDER);
        UIManager.put("TabbedPane.focus",       ACCENT);
        SwingUtilities.updateComponentTreeUI(tabs);

        // Pestañas disponibles para todos
        tabs.addTab("  👥  Apostadores  ", new PanelApostadores());
        tabs.addTab("  🔮  Predicciones  ", new PanelPredicciones());
        tabs.addTab("  🏆  Resultados  ", new PanelResultados());

        // Pestañas solo para admin
        if (isAdmin) {
            tabs.addTab("  🛡️  Equipos  ", new PanelEquipos());
            tabs.addTab("  ⚽  Partidos  ", new PanelPartidos());
        }

        // ---- BARRA DE ESTADO INFERIOR ----
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        statusBar.setBackground(HEADER_BG);
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, DIVIDER));

        JLabel lblStatus = new JLabel("⚡  Conectado como: " + usuarioActual.getUsername() + " | Rol: " + usuarioActual.getRol().toUpperCase());
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(TEXT_HINT);
        statusBar.add(lblStatus);

        JLabel lblRight = new JLabel("FIFA World Cup · México · USA · Canadá 2026");
        lblRight.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRight.setForeground(TEXT_HINT);
        statusBar.setLayout(new BorderLayout());
        JPanel sLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        sLeft.setOpaque(false);
        sLeft.add(lblStatus);
        JPanel sRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        sRight.setOpaque(false);
        sRight.add(lblRight);
        statusBar.add(sLeft, BorderLayout.WEST);
        statusBar.add(sRight, BorderLayout.EAST);

        // Ensamblar todo
        root.add(topBar, BorderLayout.NORTH);
        root.add(tabs,   BorderLayout.CENTER);
        root.add(statusBar, BorderLayout.SOUTH);

        setContentPane(root);
    }
}
