package vistas;

import controladores.ApostadorController;
import modelos.Apostador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.kordamp.ikonli.swing.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.Ikon;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.stream.Collectors;

public class PanelApostadores extends JPanel {

    // Paleta de colores
    private static final Color BG_APP       = new Color(11, 19, 32);
    private static final Color BG_CARD      = new Color(22, 32, 50);
    private static final Color BORDER_CARD  = new Color(44, 58, 83);
    private static final Color TEXT_GOLD    = new Color(226, 185, 74);
    private static final Color TEXT_LIGHT   = new Color(194, 203, 224);
    private static final Color TEXT_MUTED   = new Color(139, 149, 165);
    private static final Color TEXT_CYAN    = new Color(0, 212, 255);
    private static final Color FIELD_BG     = new Color(14, 21, 35);
    private static final Color FIELD_BORDER = new Color(27, 38, 59);
    private static final Color BTN_HOVER    = new Color(30, 40, 60);

    private ApostadorController controller;
    private JPanel listaPanel;

    // Labels para tarjetas de estadísticas
    private JLabel lblTotalApostadores;
    private JLabel lblLiderPuntos;
    private JLabel lblTotalPuntos;

    // Label de resultados
    private JLabel lblShowingInfo;

    // Lista completa cargada desde BD
    private List<Apostador> todosLosApostadores;

    private JPanel statsContainer;
    private JPanel actionRow;

    public PanelApostadores(boolean isAdmin) {
        controller = new ApostadorController();
        setLayout(new BorderLayout());
        setBackground(BG_APP);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_APP);
        contentPanel.setOpaque(true);

        // ---- 1. TARJETAS DE ESTADÍSTICAS (datos reales, nombres en español) ----
        statsContainer = new JPanel(new GridLayout(1, 3, 20, 0));
        statsContainer.setBackground(BG_APP);
        statsContainer.setOpaque(true);
        statsContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        lblTotalApostadores = new JLabel("0");
        lblTotalApostadores.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotalApostadores.setForeground(TEXT_LIGHT);
        JPanel card1 = createStatCard("APOSTADORES REGISTRADOS", FontAwesomeSolid.USERS, lblTotalApostadores, "Total de usuarios en el sistema", TEXT_CYAN);

        lblLiderPuntos = new JLabel("—");
        lblLiderPuntos.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLiderPuntos.setForeground(TEXT_GOLD);
        JPanel card2 = createStatCard("LÍDER ACTUAL", FontAwesomeSolid.MEDAL, lblLiderPuntos, "Apostador con más puntos acumulados", TEXT_LIGHT);

        lblTotalPuntos = new JLabel("0");
        lblTotalPuntos.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotalPuntos.setForeground(TEXT_LIGHT);
        JPanel card3 = createStatCard("PUNTOS EN JUEGO", FontAwesomeSolid.STAR, lblTotalPuntos, "Suma total de puntos del sistema", TEXT_GOLD);

        statsContainer.add(card1);
        statsContainer.add(card2);
        statsContainer.add(card3);
        contentPanel.add(statsContainer);
        contentPanel.add(Box.createVerticalStrut(25));

        // ---- 2. BUSCADOR ----
        actionRow = new JPanel(new GridLayout(1, 1, 20, 0));
        actionRow.setOpaque(false);
        actionRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 145));

        // -- Panel de búsqueda --
        JPanel searchCard = createRoundedPanel();
        searchCard.setLayout(new BoxLayout(searchCard, BoxLayout.Y_AXIS));
        searchCard.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel lblSearchTitle = new JLabel("Buscar Apostador");
        lblSearchTitle.setFont(new Font("Consolas", Font.BOLD, 11));
        lblSearchTitle.setForeground(TEXT_MUTED);
        lblSearchTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtBuscar = crearTextField("Escriba el nombre del apostador...");
        txtBuscar.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        ((AbstractDocument) txtBuscar.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
                if (text != null && text.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*")) super.insertString(fb, offset, text, attr);
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text != null && text.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*")) super.replace(fb, offset, length, text, attrs);
            }
        });
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrar(txtBuscar.getText()); }
            @Override public void removeUpdate(DocumentEvent e) { filtrar(txtBuscar.getText()); }
            @Override public void changedUpdate(DocumentEvent e) { filtrar(txtBuscar.getText()); }
        });

        searchCard.add(lblSearchTitle);
        searchCard.add(Box.createVerticalStrut(10));
        searchCard.add(txtBuscar);
        searchCard.add(Box.createVerticalGlue());

        actionRow.add(searchCard);

        contentPanel.add(actionRow);
        contentPanel.add(Box.createVerticalStrut(25));


        // ---- 4. LISTA DE APOSTADORES ----
        JPanel listCard = createRoundedPanel();
        listCard.setLayout(new BorderLayout());

        JPanel listHeader = new JPanel(new BorderLayout());
        listHeader.setOpaque(false);
        listHeader.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblRank = new JLabel("POSICIÓN");
        lblRank.setFont(new Font("Consolas", Font.BOLD, 12));
        lblRank.setForeground(TEXT_MUTED);
        lblRank.setPreferredSize(new Dimension(90, 20));

        JLabel lblProfile = new JLabel("PERFIL DEL APOSTADOR");
        lblProfile.setFont(new Font("Consolas", Font.BOLD, 12));
        lblProfile.setForeground(TEXT_MUTED);

        listHeader.add(lblRank, BorderLayout.WEST);
        listHeader.add(lblProfile, BorderLayout.CENTER);
        listCard.add(listHeader, BorderLayout.NORTH);

        listaPanel = new JPanel();
        listaPanel.setLayout(new BoxLayout(listaPanel, BoxLayout.Y_AXIS));
        listaPanel.setOpaque(false);

        JScrollPane scrollLista = new JScrollPane(listaPanel);
        scrollLista.setOpaque(false);
        scrollLista.getViewport().setOpaque(false);
        scrollLista.setBorder(null);
        scrollLista.getVerticalScrollBar().setUnitIncrement(16);
        listCard.add(scrollLista, BorderLayout.CENTER);

        JPanel paginationPanel = new JPanel(new BorderLayout());
        paginationPanel.setOpaque(false);
        paginationPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        lblShowingInfo = new JLabel("Mostrando 0 apostadores");
        lblShowingInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblShowingInfo.setForeground(TEXT_MUTED);
        paginationPanel.add(lblShowingInfo, BorderLayout.WEST);
        listCard.add(paginationPanel, BorderLayout.SOUTH);

        contentPanel.add(listCard);

        JScrollPane mainScrollPane = new JScrollPane(contentPanel);
        mainScrollPane.setBorder(null);
        mainScrollPane.setOpaque(false);
        mainScrollPane.getViewport().setOpaque(false);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScrollPane, BorderLayout.CENTER);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int width = getWidth();
                if (width < 750) {
                    statsContainer.setLayout(new GridLayout(3, 1, 0, 15));
                    statsContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 380));
                    actionRow.setLayout(new GridLayout(1, 1, 0, 15));
                    actionRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 145));
                } else {
                    statsContainer.setLayout(new GridLayout(1, 3, 20, 0));
                    statsContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
                    actionRow.setLayout(new GridLayout(1, 1, 20, 0));
                    actionRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 145));
                }
                statsContainer.revalidate();
                actionRow.revalidate();
            }
        });

        cargarDatos();
    }

    // ---- Métodos de datos ----

    private void cargarDatos() {
        todosLosApostadores = controller.obtenerApostadores();
        // La lista viene ya ordenada por puntos DESC desde el DAO
        actualizarEstadisticas(todosLosApostadores);
        renderizarLista(todosLosApostadores);
    }

    private void filtrar(String query) {
        if (todosLosApostadores == null) return;
        if (query == null || query.trim().isEmpty()) {
            renderizarLista(todosLosApostadores);
            return;
        }
        String q = query.toLowerCase().trim();
        List<Apostador> filtrados = todosLosApostadores.stream()
                .filter(a -> a.getNombre().toLowerCase().contains(q))
                .collect(Collectors.toList());
        renderizarLista(filtrados);
    }

    private void actualizarEstadisticas(List<Apostador> lista) {
        // Card 1: Total apostadores
        lblTotalApostadores.setText(String.valueOf(lista.size()));

        // Card 2: Nombre del líder (el primero de la lista ordenada por puntos)
        if (!lista.isEmpty()) {
            Apostador lider = lista.get(0);
            lblLiderPuntos.setText("<html><b>" + lider.getNombre() + "</b><br><font color='#8B95A5' size='2'>" + lider.getPuntosTotal() + " puntos</font></html>");
        } else {
            lblLiderPuntos.setText("—");
        }

        // Card 3: Suma total de puntos
        int totalPts = lista.stream().mapToInt(Apostador::getPuntosTotal).sum();
        lblTotalPuntos.setText(String.valueOf(totalPts));
    }

    private void renderizarLista(List<Apostador> lista) {
        listaPanel.removeAll();
        lblShowingInfo.setText("Mostrando " + lista.size() + " apostadores");

        int rank = 1;
        for (Apostador a : lista) {
            listaPanel.add(createBettorRow(a, rank));
            rank++;
        }

        listaPanel.revalidate();
        listaPanel.repaint();
    }

    // ---- Componentes Visuales ----

    private JTextField crearTextField(String placeholder) {
        JTextField tf = new JTextField(placeholder) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FIELD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(FIELD_BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tf.setOpaque(false);
        tf.setBorder(new EmptyBorder(8, 15, 8, 15));
        tf.setForeground(TEXT_MUTED);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setCaretColor(Color.WHITE);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        // Comportamiento de placeholder
        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (tf.getText().equals(placeholder)) {
                    tf.setText("");
                    tf.setForeground(Color.WHITE);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (tf.getText().trim().isEmpty()) {
                    tf.setText(placeholder);
                    tf.setForeground(TEXT_MUTED);
                }
            }
        });
        return tf;
    }

    private JPanel createRoundedPanel() {
        JPanel p = new JPanel() {
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
        p.setOpaque(false);
        return p;
    }

    private JPanel createStatCard(String title, Ikon icon, JLabel mainValue, String subText, Color subColor) {
        JPanel card = createRoundedPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 15, 20));

        JPanel topP = new JPanel(new BorderLayout());
        topP.setOpaque(false);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Consolas", Font.BOLD, 10));
        lblTitle.setForeground(TEXT_MUTED);
        
        FontIcon fIcon = FontIcon.of(icon, 20, TEXT_LIGHT);
        JLabel lblIcon = new JLabel(fIcon);
        topP.add(lblTitle, BorderLayout.WEST);
        topP.add(lblIcon, BorderLayout.EAST);

        JPanel centerP = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 8));
        centerP.setOpaque(false);
        centerP.add(mainValue);

        JLabel lblSub = new JLabel("<html>" + subText + "</html>");
        lblSub.setFont(new Font("Consolas", Font.PLAIN, 10));
        lblSub.setForeground(subColor);

        card.add(topP, BorderLayout.NORTH);
        card.add(centerP, BorderLayout.CENTER);
        card.add(lblSub, BorderLayout.SOUTH);
        return card;
    }

    private JButton createGoldButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? TEXT_GOLD.brighter() : TEXT_GOLD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                FontMetrics fm = g2.getFontMetrics(getFont());
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.setColor(new Color(29, 29, 29));
                g2.drawString(getText(), textX, textY);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel createBettorRow(Apostador a, int rank) {
        JPanel row = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(BORDER_CARD);
                g.drawLine(20, 0, getWidth() - 20, 0);
            }
        };
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(15, 20, 15, 20));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                row.setOpaque(true);
                row.setBackground(BTN_HOVER);
                row.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                row.setOpaque(false);
                row.repaint();
            }
        });

        // Posición
        JLabel lblR = new JLabel("#" + rank);
        lblR.setFont(new Font("Segoe UI", Font.ITALIC | Font.BOLD, 18));
        lblR.setForeground(rank == 1 ? TEXT_GOLD : TEXT_MUTED);
        lblR.setPreferredSize(new Dimension(80, 40));

        // Perfil
        JPanel profileP = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        profileP.setOpaque(false);

        String initial = a.getNombre().isEmpty() ? "?" : a.getNombre().substring(0, 1).toUpperCase();
        JLabel lblAvatar = new JLabel(initial, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FIELD_BORDER);
                g2.fillOval(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
                g2.dispose();
            }
        };
        lblAvatar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblAvatar.setForeground(Color.WHITE);
        lblAvatar.setPreferredSize(new Dimension(40, 40));

        JPanel textP = new JPanel();
        textP.setLayout(new BoxLayout(textP, BoxLayout.Y_AXIS));
        textP.setOpaque(false);

        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        namePanel.setOpaque(false);
        JLabel lblName = new JLabel(a.getNombre());
        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblName.setForeground(Color.WHITE);
        namePanel.add(lblName);

        if (rank == 1) {
            JLabel badge = new JLabel(" LÍDER ") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(TEXT_GOLD);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                    super.paintComponent(g);
                    g2.dispose();
                }
            };
            badge.setFont(new Font("Consolas", Font.BOLD, 9));
            badge.setForeground(new Color(29, 29, 29));
            badge.setBorder(new EmptyBorder(1, 4, 1, 4));
            namePanel.add(Box.createHorizontalStrut(8));
            namePanel.add(badge);
        }

        String simulatedEmail = a.getNombre().toLowerCase().replaceAll("\\s+", ".") + "@wc2026.com";
        JLabel lblEmail = new JLabel(simulatedEmail);
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblEmail.setForeground(TEXT_MUTED);

        textP.add(namePanel);
        textP.add(Box.createVerticalStrut(2));
        textP.add(lblEmail);

        profileP.add(lblAvatar);
        profileP.add(textP);

        // Puntos
        JLabel lblPts = new JLabel(a.getPuntosTotal() + " pts");
        lblPts.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPts.setForeground(TEXT_LIGHT);
        lblPts.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(lblR, BorderLayout.WEST);
        row.add(profileP, BorderLayout.CENTER);
        row.add(lblPts, BorderLayout.EAST);

        return row;
    }
}
