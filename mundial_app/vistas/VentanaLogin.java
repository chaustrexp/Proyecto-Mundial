package vistas;

import dao.UsuarioDAO;
import dao.ApostadorDAO;
import modelos.Usuario;
import modelos.Apostador;
import utils.SesionUsuario;
import org.kordamp.ikonli.swing.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.text.AbstractDocument;
import utils.ValidationUtils;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class VentanaLogin extends JFrame {

    // Paleta de colores basada en el diseño
    private static final Color BG_APP = new Color(10, 17, 31);
    private static final Color BG_CARD = new Color(22, 32, 50);
    private static final Color BORDER_CARD = new Color(44, 58, 83);
    private static final Color FIELD_BG = new Color(14, 21, 35);
    private static final Color FIELD_BORDER = new Color(27, 38, 59);
    private static final Color TEXT_GOLD = new Color(226, 185, 74);
    private static final Color TEXT_LIGHT = new Color(194, 203, 224);
    private static final Color TEXT_MUTED = new Color(139, 149, 165);
    private static final Color BUTTON_GRAD1 = new Color(222, 176, 66);
    private static final Color BUTTON_GRAD2 = new Color(227, 190, 88);
    private static final Color BTN_TEXT_DARK = new Color(29, 29, 29);

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRol;
    private JButton btnLogin;
    private boolean isPasswordVisible = false;

    public VentanaLogin() {
        setTitle("Mundial 2026 — Sistema de Apuestas");
        setSize(460, 680); // Ajustado para verse un poco más estilizado
        setMinimumSize(new Dimension(420, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel principal con dibujo de fondo (patrón de puntos)
        JPanel rootPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_APP);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Patrón de puntos sutiles
                g2.setColor(new Color(255, 255, 255, 10));
                for (int x = 0; x < getWidth(); x += 30) {
                    for (int y = 0; y < getHeight(); y += 30) {
                        g2.fillOval(x, y, 2, 2);
                    }
                }
                g2.dispose();
            }
        };
        rootPanel.setOpaque(true);

        // --- TOP NAVBAR ---
        JPanel navbar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(255, 255, 255, 20));
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
            }
        };
        navbar.setOpaque(false);
        navbar.setPreferredSize(new Dimension(getWidth(), 60));

        JLabel navTitle = new JLabel("Sistema de Apuestas");
        navTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        navTitle.setForeground(TEXT_GOLD);
        navbar.add(navTitle);

        rootPanel.add(navbar, BorderLayout.NORTH);

        // --- CENTER CONTENT ---
        JPanel centerWrapper = new JPanel();
        centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.Y_AXIS));
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(new EmptyBorder(25, 40, 20, 40));

        // HEADER (Trophy & MUNDIAL 2026)
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        // Circular Trophy icon
        FontIcon trophyIcon = FontIcon.of(FontAwesomeSolid.TROPHY, 28, TEXT_GOLD);
        JLabel lblTrophy = new JLabel(trophyIcon, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(226, 185, 74, 30)); // Fondo dorado semi-transparente
                int size = Math.min(getWidth(), getHeight()) - 4;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                g2.fillOval(x, y, size, size);
                g2.setColor(TEXT_GOLD);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(x, y, size, size);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblTrophy.setPreferredSize(new Dimension(70, 70));
        lblTrophy.setMaximumSize(new Dimension(70, 70));
        lblTrophy.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("MUNDIAL 2026", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(TEXT_GOLD);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Sistema de Apuestas", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSubtitle.setForeground(TEXT_MUTED);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lblTrophy);
        headerPanel.add(Box.createVerticalStrut(15));
        headerPanel.add(lblTitle);
        headerPanel.add(lblSubtitle);
        headerPanel.add(Box.createVerticalStrut(25));

        // CARD PANEL con GridBagLayout para alinear todo perfecto
        JPanel cardPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(BORDER_CARD);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 5, 0); // Spacing below

        // ROL
        cardPanel.add(createLabel("ROL"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 15, 0);
        cbRol = new JComboBox<>(new String[] { "Admin", "Usuario" });
        styleCombo(cbRol);
        cardPanel.add(cbRol, gbc);

        // USUARIO
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 5, 0);
        cardPanel.add(createLabel("USUARIO"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 15, 0);
        txtUsername = new JTextField();
        ((AbstractDocument) txtUsername.getDocument()).setDocumentFilter(ValidationUtils.getAlphaNumericFilter(30));
        styleField(txtUsername);
        cardPanel.add(txtUsername, gbc);

        // CONTRASEÑA
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 5, 0);
        cardPanel.add(createLabel("CONTRASEÑA"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 8, 0); // Menos espacio para el link de forgot password

        // Contraseña con botón de ojo
        JPanel passPanel = new JPanel(new BorderLayout());
        passPanel.setOpaque(false);
        passPanel.setPreferredSize(new Dimension(0, 45));

        txtPassword = new JPasswordField();
        styleField(txtPassword);
        txtPassword.setBorder(null); // Quitar borde interno

        FontIcon eyeIcon = FontIcon.of(FontAwesomeSolid.EYE, 14, TEXT_MUTED);
        JButton btnEye = new JButton(eyeIcon);
        btnEye.setContentAreaFilled(false);
        btnEye.setBorderPainted(false);
        btnEye.setFocusPainted(false);
        btnEye.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEye.addActionListener(e -> {
            isPasswordVisible = !isPasswordVisible;
            txtPassword.setEchoChar(isPasswordVisible ? (char) 0 : '•');
        });

        JPanel passWrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FIELD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(FIELD_BORDER);
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        passWrapper.setOpaque(false);
        passWrapper.setBorder(new EmptyBorder(5, 10, 5, 0));
        passWrapper.add(txtPassword, BorderLayout.CENTER);
        passWrapper.add(btnEye, BorderLayout.EAST);
        passPanel.add(passWrapper, BorderLayout.CENTER);

        cardPanel.add(passPanel, gbc);

        // Links panel
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 15, 0);
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        linkPanel.setOpaque(false);
        
        JLabel lblRegister = new JLabel("¿No tienes cuenta? Regístrate aquí");
        lblRegister.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRegister.setForeground(TEXT_GOLD);
        lblRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblRegister.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                abrirDialogoRegistro();
            }
        });

        linkPanel.add(lblRegister);
        cardPanel.add(linkPanel, gbc);

        // Login Button
        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 20, 0);
        btnLogin = createLoginButton();
        cardPanel.add(btnLogin, gbc);

        // Separator "Acceso Rápido"
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 15, 0);
        JPanel separatorPanel = new JPanel(new BorderLayout());
        separatorPanel.setOpaque(false);
        JLabel lblAcceso = new JLabel("Acceso Rápido", SwingConstants.CENTER);
        lblAcceso.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblAcceso.setForeground(TEXT_MUTED);

        JPanel leftLine = createLine();
        JPanel rightLine = createLine();

        separatorPanel.add(leftLine, BorderLayout.WEST);
        separatorPanel.add(lblAcceso, BorderLayout.CENTER);
        separatorPanel.add(rightLine, BorderLayout.EAST);
        cardPanel.add(separatorPanel, gbc);

        // Acceso Rapido Box
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        JPanel quickAccessPanel = createQuickAccessPanel();
        cardPanel.add(quickAccessPanel, gbc);

        // Add Header and Card to center wrapper
        centerWrapper.add(headerPanel);
        centerWrapper.add(cardPanel);

        // Allow center wrapper to not take full height if possible
        JPanel centerContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerContainer.setOpaque(false);
        centerContainer.add(centerWrapper);
        rootPanel.add(centerContainer, BorderLayout.CENTER);

        // --- FOOTER ---
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(10, 0, 20, 0));

        footer.add(Box.createVerticalStrut(20));

        JLabel lblLinks = new JLabel("PRIVACIDAD      TÉRMINOS", SwingConstants.CENTER);
        lblLinks.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblLinks.setForeground(TEXT_MUTED);
        lblLinks.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.add(lblLinks);
        footer.add(Box.createVerticalStrut(10));

        JLabel lblFifa = new JLabel("FIFA WORLD CUP - MÉXICO - USA - CANADÁ", SwingConstants.CENTER);
        lblFifa.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblFifa.setForeground(TEXT_LIGHT);
        lblFifa.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.add(lblFifa);
        footer.add(Box.createVerticalStrut(10));

        JLabel lblCopyright = new JLabel("© 2026 Sistema de Apuestas Global. Todos los derechos reservados.",
                SwingConstants.CENTER);
        lblCopyright.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        lblCopyright.setForeground(new Color(90, 100, 120));
        lblCopyright.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.add(lblCopyright);

        rootPanel.add(footer, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(rootPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        setContentPane(scrollPane);

        setLocationRelativeTo(null);

        // Listeners for login
        btnLogin.addActionListener(e -> intentarLogin());
        txtPassword.addActionListener(e -> intentarLogin());
        txtUsername.addActionListener(e -> txtPassword.requestFocus());
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(TEXT_MUTED);
        return lbl;
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(Color.WHITE);
        field.setBackground(new Color(0, 0, 0, 0)); // Transparente porque pintamos el fondo en wrapper o componente
                                                    // custom
        field.setCaretColor(Color.WHITE);
        field.setOpaque(false);
        field.setBorder(new EmptyBorder(5, 15, 5, 15));

        // Si no es el password field (que tiene wrapper), crear panel para fondo
        if (!(field instanceof JPasswordField)) {
            field.setPreferredSize(new Dimension(0, 45));
            field.setOpaque(true);
            field.setBackground(FIELD_BG);
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(FIELD_BORDER, 1),
                    new EmptyBorder(5, 15, 5, 15)));
        }
    }

    private void styleCombo(JComboBox<String> combo) {
        // Quitar el estilo de Nimbus por completo para este componente
        combo.setUI(new BasicComboBoxUI());
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setForeground(Color.WHITE);
        combo.setBackground(FIELD_BG);
        combo.setPreferredSize(new Dimension(0, 45));
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER, 1),
                new EmptyBorder(0, 10, 0, 10)));

        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? BORDER_CARD : FIELD_BG);
                setForeground(Color.WHITE);
                setBorder(new EmptyBorder(8, 10, 8, 10));
                return this;
            }
        });
    }

    private JButton createLoginButton() {
        JButton btn = new JButton("Iniciar Sesión  →") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background
                GradientPaint gp;
                if (getModel().isRollover()) {
                    gp = new GradientPaint(0, 0, BUTTON_GRAD1.brighter(), getWidth(), 0, BUTTON_GRAD2.brighter());
                } else {
                    gp = new GradientPaint(0, 0, BUTTON_GRAD1, getWidth(), 0, BUTTON_GRAD2);
                }
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));

                // Overlay text
                FontMetrics fm = g2.getFontMetrics(getFont());
                Rectangle stringBounds = fm.getStringBounds(getText(), g2).getBounds();
                int textX = (getWidth() - stringBounds.width) / 2;
                int textY = (getHeight() - stringBounds.height) / 2 + fm.getAscent();

                g2.setColor(BTN_TEXT_DARK);
                g2.drawString(getText(), textX, textY);

                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 50));
        return btn;
    }

    private JPanel createLine() {
        JPanel line = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255, 255, 255, 20));
                g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
            }
        };
        line.setOpaque(false);
        line.setPreferredSize(new Dimension(80, 20));
        return line;
    }

    private JPanel createQuickAccessPanel() {
        JPanel qa = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FIELD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(FIELD_BORDER);
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        qa.setOpaque(false);
        qa.setBorder(new EmptyBorder(12, 15, 12, 15));

        // Icon circle
        FontIcon soccerIcon = FontIcon.of(FontAwesomeSolid.FUTBOL, 18, new Color(0, 180, 216));
        JLabel lblIcon = new JLabel(soccerIcon, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 180, 216, 30)); // Fondo cyan
                int size = Math.min(getWidth(), getHeight());
                g2.fillOval((getWidth() - size) / 2, (getHeight() - size) / 2, size, size);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        lblIcon.setPreferredSize(new Dimension(36, 36));

        // Cargar partido real desde la BD
        String matchText = "Sin partidos programados";
        String faseText = "Próximo Encuentro";
        String badgeText = "PRÓXIMAMENTE";
        Color badgeBg = new Color(44, 58, 83); // Azul grisaceo por defecto
        Color badgeFg = new Color(139, 149, 165); // Texto grisaceo

        try {
            dao.PartidoDAO pDao = new dao.PartidoDAO();
            java.util.List<modelos.Partido> partidos = pDao.obtenerPartidos();
            if (!partidos.isEmpty()) {
                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                modelos.Partido partidoMostrar = null;
                boolean isLive = false;

                // 1. Buscar si hay algún partido en vivo (asumimos duración de 110 mins aprox)
                for (modelos.Partido p : partidos) {
                    if (p.getFecha() != null) {
                        java.time.LocalDateTime inicio = p.getFecha();
                        java.time.LocalDateTime fin = inicio.plusMinutes(110);
                        if (!now.isBefore(inicio) && now.isBefore(fin)) {
                            partidoMostrar = p;
                            isLive = true;
                            break;
                        }
                    }
                }

                // 2. Si no hay en vivo, buscar el próximo más cercano
                if (partidoMostrar == null) {
                    partidoMostrar = partidos.stream()
                        .filter(p -> p.getFecha() != null && p.getFecha().isAfter(now))
                        .min(java.util.Comparator.comparing(modelos.Partido::getFecha))
                        .orElse(null);
                }

                // 3. Si no hay futuros, mostrar el último jugado
                if (partidoMostrar == null) {
                    partidoMostrar = partidos.stream()
                        .filter(p -> p.getFecha() != null)
                        .max(java.util.Comparator.comparing(modelos.Partido::getFecha))
                        .orElse(partidos.get(partidos.size() - 1));
                }

                if (partidoMostrar != null) {
                    String local = partidoMostrar.getEquipoLocal() != null ? partidoMostrar.getEquipoLocal().getNombre() : "TBD";
                    String visita = partidoMostrar.getEquipoVisita() != null ? partidoMostrar.getEquipoVisita().getNombre() : "TBD";
                    matchText = local + " vs " + visita;

                    if (isLive) {
                        faseText = "Fase: " + partidoMostrar.getFase();
                        badgeText = "EN VIVO";
                        badgeBg = new Color(62, 49, 60); // Rojizo
                        badgeFg = new Color(226, 149, 120); // Salmon
                    } else if (partidoMostrar.getFecha() != null && partidoMostrar.getFecha().isAfter(now)) {
                        faseText = partidoMostrar.getFecha().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM HH:mm")) + " - " + partidoMostrar.getFase();
                        badgeText = "PRÓXIMO";
                        badgeBg = new Color(226, 185, 74, 40); // Dorado semi-transparente
                        badgeFg = TEXT_GOLD;
                    } else {
                        faseText = "Finalizado - " + partidoMostrar.getFase();
                        badgeText = "FINAL";
                        badgeBg = new Color(27, 38, 59);
                        badgeFg = TEXT_MUTED;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("No se pudieron cargar los partidos en el Login.");
        }

        // Match Info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        JLabel lblSub = new JLabel(faseText);
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblSub.setForeground(TEXT_LIGHT);
        JLabel lblMatch = new JLabel(matchText);
        lblMatch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMatch.setForeground(Color.WHITE);
        infoPanel.add(lblSub);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(lblMatch);

        // Live Badge (Dynamic)
        final Color finalBadgeBg = badgeBg;
        final String finalBadgeText = badgeText;
        JLabel lblLive = new JLabel(finalBadgeText, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(finalBadgeBg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                super.paintComponent(g);
                g2.dispose();
            }
        };
        lblLive.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblLive.setForeground(badgeFg);
        lblLive.setPreferredSize(new Dimension(65, 24));
        lblLive.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel badgeWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 4));
        badgeWrapper.setOpaque(false);
        badgeWrapper.add(lblLive);

        qa.add(lblIcon, BorderLayout.WEST);
        qa.add(infoPanel, BorderLayout.CENTER);
        qa.add(badgeWrapper, BorderLayout.EAST);

        return qa;
    }

    private void intentarLogin() {
        String rol = (String) cbRol.getSelectedItem();
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            showError("Por favor, ingrese usuario y contraseña.");
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuarioLogueado = dao.validarLogin(user, pass, rol.toLowerCase());

        if (usuarioLogueado != null) {
            SesionUsuario.setUsuarioActual(usuarioLogueado);
            if ("usuario".equalsIgnoreCase(usuarioLogueado.getRol())) {
                ApostadorDAO adao = new ApostadorDAO();
                Apostador apo = adao.obtenerApostadorPorUsuarioId(usuarioLogueado.getId());
                SesionUsuario.setApostadorActual(apo);
            } else {
                SesionUsuario.setApostadorActual(null);
            }
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

    private void abrirDialogoRegistro() {
        JDialog dialog = new JDialog(this, "Registrar Nueva Cuenta", true);
        dialog.setSize(380, 480);
        dialog.setLocationRelativeTo(this);

        JPanel p = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_APP);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        p.setBorder(new EmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel lblTitle = new JLabel("Crear Cuenta", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_GOLD);
        gbc.insets = new Insets(0, 0, 25, 0);
        p.add(lblTitle, gbc);

        gbc.insets = new Insets(0, 0, 5, 0);

        // Alias Apostador
        gbc.gridy++;
        p.add(createLabel("NOMBRE/ALIAS (Para Ranking)"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 15, 0);
        JTextField txtAlias = new JTextField();
        ((AbstractDocument) txtAlias.getDocument()).setDocumentFilter(ValidationUtils.getLettersOnlyFilter(30));
        styleField(txtAlias);
        p.add(txtAlias, gbc);

        // Username
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 5, 0);
        p.add(createLabel("NOMBRE DE USUARIO (Para Login)"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 15, 0);
        JTextField txtNewUser = new JTextField();
        ((AbstractDocument) txtNewUser.getDocument()).setDocumentFilter(ValidationUtils.getAlphaNumericFilter(30));
        styleField(txtNewUser);
        p.add(txtNewUser, gbc);

        // Password
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 5, 0);
        p.add(createLabel("CONTRASEÑA"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 15, 0);
        JPasswordField txtNewPass = new JPasswordField();
        styleField(txtNewPass);
        txtNewPass.setPreferredSize(new Dimension(0, 45));
        txtNewPass.setOpaque(true);
        txtNewPass.setBackground(FIELD_BG);
        txtNewPass.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(FIELD_BORDER, 1),
                new EmptyBorder(5, 15, 5, 15)));
        p.add(txtNewPass, gbc);

        // Confirm Password
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 5, 0);
        p.add(createLabel("CONFIRMAR CONTRASEÑA"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 25, 0);
        JPasswordField txtConfirm = new JPasswordField();
        styleField(txtConfirm);
        txtConfirm.setPreferredSize(new Dimension(0, 45));
        txtConfirm.setOpaque(true);
        txtConfirm.setBackground(FIELD_BG);
        txtConfirm.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(FIELD_BORDER, 1),
                new EmptyBorder(5, 15, 5, 15)));
        p.add(txtConfirm, gbc);

        // Register Button
        gbc.gridy++;
        JButton btnRegister = new JButton("Completar Registro") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? TEXT_GOLD.brighter() : TEXT_GOLD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                FontMetrics fm = g2.getFontMetrics(getFont());
                Rectangle stringBounds = fm.getStringBounds(getText(), g2).getBounds();
                int textX = (getWidth() - stringBounds.width) / 2;
                int textY = (getHeight() - stringBounds.height) / 2 + fm.getAscent();
                g2.setColor(BTN_TEXT_DARK);
                g2.drawString(getText(), textX, textY);
                g2.dispose();
            }
        };
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnRegister.setOpaque(false);
        btnRegister.setContentAreaFilled(false);
        btnRegister.setBorderPainted(false);
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.setPreferredSize(new Dimension(0, 50));

        btnRegister.addActionListener(e -> {
            String alias = txtAlias.getText().trim();
            String u = txtNewUser.getText().trim();
            String p1 = new String(txtNewPass.getPassword());
            String p2 = new String(txtConfirm.getPassword());

            if (alias.isEmpty() || u.isEmpty() || p1.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Por favor complete todos los campos.", "Campos incompletos",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (p1.length() < 4) {
                JOptionPane.showMessageDialog(dialog, "La contraseña debe tener al menos 4 caracteres.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!p1.equals(p2)) {
                JOptionPane.showMessageDialog(dialog, "Las contraseñas no coinciden.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Usuario nuevoUser = new Usuario(0, u, p1, "usuario");
            UsuarioDAO uDao = new UsuarioDAO();
            int newUserId = uDao.crearUsuarioYObtenerId(nuevoUser);

            if (newUserId != -1) {
                Apostador nuevoApo = new Apostador(0, alias, 0, newUserId);
                ApostadorDAO aDao = new ApostadorDAO();
                if (aDao.insertarApostador(nuevoApo)) {
                    JOptionPane.showMessageDialog(dialog,
                            "¡Registro completado!\nYa puedes iniciar sesión con tu nueva cuenta.", "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    txtUsername.setText(u);
                    txtPassword.setText("");
                    cbRol.setSelectedItem("Usuario");
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Error al crear el perfil de apostador.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Error al crear la cuenta.\nEl nombre de usuario podría ya estar en uso.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        p.add(btnRegister, gbc);
        dialog.setContentPane(p);
        dialog.setVisible(true);
    }
}
