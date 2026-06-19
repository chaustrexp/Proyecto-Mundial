package vistas;

import dao.UsuarioDAO;
import modelos.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class VentanaLogin extends JFrame {

    // Paleta de colores
    private static final Color BG_DARK      = new Color(10, 10, 20);
    private static final Color BG_CARD      = new Color(18, 18, 35);
    private static final Color ACCENT       = new Color(59, 130, 246);   // azul vibrante
    private static final Color ACCENT_HOVER = new Color(96, 165, 250);
    private static final Color GOLD         = new Color(251, 191, 36);
    private static final Color TEXT_MAIN    = new Color(241, 245, 249);
    private static final Color TEXT_HINT    = new Color(100, 116, 139);
    private static final Color FIELD_BG     = new Color(30, 30, 55);
    private static final Color FIELD_BORDER = new Color(55, 65, 100);

    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRol;
    private JButton        btnLogin;

    public VentanaLogin() {
        setTitle("Mundial 2026 — Inicio de Sesión");
        setSize(460, 560);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(false);

        // Panel raíz con gradiente
        JPanel root = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, BG_DARK, getWidth(), getHeight(), new Color(15, 15, 40));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Círculos decorativos difusos
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.08f));
                g2.setColor(ACCENT);
                g2.fillOval(-80, -80, 280, 280);
                g2.setColor(GOLD);
                g2.fillOval(getWidth() - 160, getHeight() - 160, 260, 260);
                g2.dispose();
            }
        };
        root.setOpaque(true);

        // Tarjeta central
        JPanel card = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 24, 24));
                // Borde sutil
                g2.setColor(FIELD_BORDER);
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(new RoundRectangle2D.Float(0.6f, 0.6f, getWidth() - 1.2f, getHeight() - 1.2f, 24, 24));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(370, 460));
        card.setBorder(new EmptyBorder(36, 36, 36, 36));

        // ---- ENCABEZADO ----
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel lblIcon = new JLabel("🏆", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("MUNDIAL 2026", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_MAIN);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Sistema de Apuestas", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(TEXT_HINT);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(lblIcon);
        header.add(Box.createVerticalStrut(6));
        header.add(lblTitle);
        header.add(Box.createVerticalStrut(2));
        header.add(lblSubtitle);

        // ---- FORMULARIO ----
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        // Rol
        form.add(Box.createVerticalStrut(20));
        form.add(makeLabel("Rol"));
        form.add(Box.createVerticalStrut(6));
        cbRol = new JComboBox<>(new String[]{"admin", "usuario"});
        styleCombo(cbRol);
        form.add(cbRol);

        // Usuario
        form.add(Box.createVerticalStrut(16));
        form.add(makeLabel("Usuario"));
        form.add(Box.createVerticalStrut(6));
        txtUsername = new JTextField();
        styleField(txtUsername);
        form.add(txtUsername);

        // Contraseña
        form.add(Box.createVerticalStrut(16));
        form.add(makeLabel("Contraseña"));
        form.add(Box.createVerticalStrut(6));
        txtPassword = new JPasswordField();
        styleField(txtPassword);
        form.add(txtPassword);

        // Botón login
        form.add(Box.createVerticalStrut(28));
        btnLogin = makeLoginButton();
        form.add(btnLogin);

        // ---- FOOTER ----
        JLabel lblFooter = new JLabel("FIFA World Cup · México · USA · Canadá", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblFooter.setForeground(TEXT_HINT);
        lblFooter.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(header, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);
        card.add(lblFooter, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        root.add(card, gbc);

        // Listeners
        btnLogin.addActionListener(e -> intentarLogin());
        txtPassword.addActionListener(e -> intentarLogin());
        txtUsername.addActionListener(e -> txtPassword.requestFocus());

        setContentPane(root);
    }

    // ---------- helpers de estilo ----------

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(148, 163, 184));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(TEXT_MAIN);
        field.setBackground(FIELD_BG);
        field.setCaretColor(ACCENT);
        field.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(FIELD_BORDER, 10),
            new EmptyBorder(4, 14, 4, 14)
        ));
        field.setOpaque(true);

        // Hover: resaltar borde
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(ACCENT, 10),
                    new EmptyBorder(4, 14, 4, 14)
                ));
            }
            @Override public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(FIELD_BORDER, 10),
                    new EmptyBorder(4, 14, 4, 14)
                ));
            }
        });
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setForeground(TEXT_MAIN);
        combo.setBackground(FIELD_BG);
        combo.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
        combo.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(FIELD_BORDER, 10),
            new EmptyBorder(4, 10, 4, 10)
        ));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? ACCENT : FIELD_BG);
                setForeground(TEXT_MAIN);
                setBorder(new EmptyBorder(4, 10, 4, 10));
                return this;
            }
        });
    }

    private JButton makeLoginButton() {
        JButton btn = new JButton("Iniciar Sesión") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = getModel().isRollover() ? ACCENT_HOVER : ACCENT;
                GradientPaint gp = new GradientPaint(0, 0, base.brighter(), getWidth(), getHeight(), base);
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 46));
        return btn;
    }

    // ---------- borde redondeado personalizado ----------
    private static class RoundedBorder implements javax.swing.border.Border {
        private final Color color;
        private final int radius;
        RoundedBorder(Color color, int radius) { this.color = color; this.radius = radius; }
        @Override public Insets getBorderInsets(Component c) { return new Insets(radius / 2, radius, radius / 2, radius); }
        @Override public boolean isBorderOpaque() { return false; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Float(x + 0.75f, y + 0.75f, w - 1.5f, h - 1.5f, radius, radius));
            g2.dispose();
        }
    }

    // ---------- lógica de negocio ----------
    private void intentarLogin() {
        String rol  = (String) cbRol.getSelectedItem();
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            showError("Por favor, ingrese usuario y contraseña.");
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuarioLogueado = dao.validarLogin(user, pass, rol);

        if (usuarioLogueado != null) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                VentanaPrincipal ventana = new VentanaPrincipal(usuarioLogueado);
                ventana.setVisible(true);
            });
        } else {
            showError("Usuario, contraseña o rol incorrectos.");
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error de autenticación", JOptionPane.ERROR_MESSAGE);
    }
}
