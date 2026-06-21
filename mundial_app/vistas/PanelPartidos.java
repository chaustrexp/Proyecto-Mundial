package vistas;

import controladores.PartidoController;
import modelos.Equipo;
import modelos.Partido;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;

public class PanelPartidos extends JPanel {

    // Paleta de colores de alta fidelidad
    private static final Color BG_APP          = new Color(11, 19, 32);     // #0B1320
    private static final Color BG_CARD         = new Color(22, 32, 50);     // #162032
    private static final Color BORDER_CARD     = new Color(44, 58, 83);     // #2C3A53
    private static final Color TEXT_GOLD       = new Color(226, 185, 74);   // #E2B94A
    private static final Color TEXT_LIGHT      = new Color(194, 203, 224);  // #C2CBE0
    private static final Color TEXT_MUTED      = new Color(139, 149, 165);  // #8B95A5
    private static final Color TEXT_CYAN       = new Color(0, 212, 255);    // #00D4FF
    private static final Color TEXT_RED        = new Color(255, 107, 107);  // #FF6B6B
    private static final Color FIELD_BG        = new Color(14, 21, 35);     // #0E1523
    private static final Color FIELD_BORDER    = new Color(27, 38, 59);     // #1B263B

    private PartidoController controller;
    private JPanel listContainer;
    private JTextField txtSearch;
    private JComboBox<String> cbStateFilter;

    // Etiquetas de estadísticas superiores
    private JLabel lblTotalScheduled;
    private JLabel lblTotalLive;
    private JLabel lblTotalUpcoming;

    // Control de estado en vivo en memoria
    private static final Set<Integer> partidosEnVivo = new HashSet<>();
    // Mapeo en memoria de minutos para partidos en vivo
    private static final Map<Integer, Integer> minutosPartidos = new HashMap<>();
    // Mapeo en memoria de goles en vivo temporales antes de finalizar
    private static final Map<Integer, Integer> golesLocalVivo = new HashMap<>();
    private static final Map<Integer, Integer> golesVisitaVivo = new HashMap<>();
    
    // Mapeo de horarios ficticios y estadios por partido
    private static final Map<Integer, String> horariosPartidos = new HashMap<>();
    private static final Map<Integer, String> estadiosPartidos = new HashMap<>();

    public PanelPartidos() {
        controller = new PartidoController();
        setLayout(new BorderLayout());
        setBackground(BG_APP);

        // Panel Principal con Scroll
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_APP);
        contentPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        // 1. CABECERA
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("PANEL DE ADMINISTRACIÓN");
        lblSub.setFont(new Font("Consolas", Font.BOLD, 11));
        lblSub.setForeground(TEXT_CYAN);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("Control de Partidos");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lblSub);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(lblTitle);
        headerPanel.add(Box.createVerticalStrut(20));

        // Botón Programar Partido Dorado
        JButton btnProgramar = createGoldButton("⊕ Programar Nuevo Partido");
        btnProgramar.addActionListener(e -> abrirDialogoProgramar());
        btnProgramar.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(btnProgramar);
        headerPanel.add(Box.createVerticalStrut(25));
        contentPanel.add(headerPanel);

        // 2. TARJETAS DE ESTADÍSTICAS GLOBALES
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(800, 100));
        statsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel card1 = createStatCard("TOTAL 2026", "0", "Partidos Programados", "🏟️");
        JPanel card2 = createStatCard("LIVE NOW", "0", "Encuentros en Vivo", "📡");
        JPanel card3 = createStatCard("PRÓXIMAS 24H", "0", "Partidos por Iniciar", "⏰");

        lblTotalScheduled = (JLabel) card1.getClientProperty("lblValue");
        lblTotalLive = (JLabel) card2.getClientProperty("lblValue");
        lblTotalUpcoming = (JLabel) card3.getClientProperty("lblValue");

        statsPanel.add(card1);
        statsPanel.add(card2);
        statsPanel.add(card3);
        contentPanel.add(statsPanel);
        contentPanel.add(Box.createVerticalStrut(25));

        // 3. BUSCADOR Y FILTRO
        JPanel searchFilterBar = new JPanel(new BorderLayout(15, 0));
        searchFilterBar.setOpaque(false);
        searchFilterBar.setMaximumSize(new Dimension(800, 45));
        searchFilterBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtSearch = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FIELD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(FIELD_BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        txtSearch.setOpaque(false);
        txtSearch.setBorder(new EmptyBorder(10, 15, 10, 15));
        txtSearch.setForeground(Color.WHITE);
        txtSearch.setCaretColor(Color.WHITE);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setToolTipText("Buscar por equipo, estadio o grupo...");
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filtrarYRenderizar(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filtrarYRenderizar(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filtrarYRenderizar(); }
        });

        cbStateFilter = new JComboBox<>(new String[]{"Todos los Estados", "En Vivo", "Por Iniciar", "Finalizados"}) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(FIELD_BG);
                setForeground(Color.WHITE);
            }
        };
        cbStateFilter.setPreferredSize(new Dimension(180, 45));
        cbStateFilter.setBackground(FIELD_BG);
        cbStateFilter.setForeground(Color.WHITE);
        cbStateFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbStateFilter.addActionListener(e -> filtrarYRenderizar());

        searchFilterBar.add(txtSearch, BorderLayout.CENTER);
        searchFilterBar.add(cbStateFilter, BorderLayout.EAST);
        contentPanel.add(searchFilterBar);
        contentPanel.add(Box.createVerticalStrut(30));

        // 4. LISTA DE TARJETAS
        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setOpaque(false);
        listContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(listContainer);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        add(scrollPane, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(BG_APP);
        JLabel footTxt = new JLabel("© 2026 World Cup Trophy Elite Digital. All Rights Reserved.  •  System Status: Operational");
        footTxt.setForeground(TEXT_MUTED);
        footTxt.setFont(new Font("Consolas", Font.PLAIN, 10));
        footer.add(footTxt);
        add(footer, BorderLayout.SOUTH);

        actualizarEstadisticasGlobales();
        filtrarYRenderizar();

        // Actualización en tiempo real cada 5 segundos
        javax.swing.Timer timerEstadisticas = new javax.swing.Timer(5000, e -> actualizarEstadisticasGlobales());
        timerEstadisticas.setRepeats(true);
        timerEstadisticas.start();
    }

    private void actualizarEstadisticasGlobales() {
        List<Partido> partidos = controller.obtenerPartidos();
        lblTotalScheduled.setText(String.format("%02d", partidos.size()));

        int enVivoCount = partidosEnVivo.size();
        lblTotalLive.setText(String.format("%02d", enVivoCount));

        int porIniciarCount = 0;
        for (Partido p : partidos) {
            if (p.getGolesLocal() == null && !partidosEnVivo.contains(p.getId())) {
                porIniciarCount++;
            }
        }
        lblTotalUpcoming.setText(String.format("%02d", porIniciarCount));
    }

    private void filtrarYRenderizar() {
        listContainer.removeAll();

        String query = txtSearch.getText().toLowerCase().trim();
        String estadoFiltro = (String) cbStateFilter.getSelectedItem();

        List<Partido> partidos = controller.obtenerPartidos();

        for (Partido p : partidos) {
            // Determinar estado
            boolean esFinalizado = p.getGolesLocal() != null;
            boolean esEnVivo = partidosEnVivo.contains(p.getId());
            boolean esPorIniciar = !esFinalizado && !esEnVivo;

            // Filtrar por estado
            if ("En Vivo".equals(estadoFiltro) && !esEnVivo) continue;
            if ("Por Iniciar".equals(estadoFiltro) && !esPorIniciar) continue;
            if ("Finalizados".equals(estadoFiltro) && !esFinalizado) continue;

            // Asignación de estadio y horario ficticios por defecto si no están definidos
            if (!horariosPartidos.containsKey(p.getId())) {
                horariosPartidos.put(p.getId(), "HOY - 21:00");
                String estadio = "SOFI STADIUM";
                if (p.getEquipoLocal().getNombre().toUpperCase().contains("MEX")) estadio = "ESTADIO AZTECA";
                else if (p.getEquipoLocal().getNombre().toUpperCase().contains("USA")) estadio = "METLIFE STADIUM";
                else if (p.getEquipoLocal().getNombre().toUpperCase().contains("CAN")) estadio = "BMO FIELD";
                estadiosPartidos.put(p.getId(), estadio);
            }

            String estadio = estadiosPartidos.get(p.getId());
            String horario = horariosPartidos.get(p.getId());

            // Filtro de búsqueda
            boolean coincideLocal = p.getEquipoLocal().getNombre().toLowerCase().contains(query);
            boolean coincideVisita = p.getEquipoVisita().getNombre().toLowerCase().contains(query);
            boolean coincideEstadio = estadio.toLowerCase().contains(query);
            boolean coincideFase = p.getFase().toLowerCase().contains(query);

            if (query.isEmpty() || coincideLocal || coincideVisita || coincideEstadio || coincideFase) {
                JPanel matchCard = null;
                if (esEnVivo) {
                    matchCard = createLiveMatchCard(p, estadio);
                } else if (esPorIniciar) {
                    matchCard = createScheduledMatchCard(p, horario, estadio);
                } else {
                    matchCard = createFinishedMatchCard(p, estadio);
                }
                if (matchCard != null) {
                    matchCard.setAlignmentX(Component.CENTER_ALIGNMENT);
                    listContainer.add(matchCard);
                }
                listContainer.add(Box.createVerticalStrut(15));
            }
        }

        listContainer.revalidate();
        listContainer.repaint();
    }

    // --- COMPONENTES VISUALES ---

    private JPanel createRoundedCardPanel() {
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

    private JPanel createStatCard(String title, String value, String subtitle, String icon) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(BORDER_CARD);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setLayout(new BorderLayout());
        p.setBorder(new EmptyBorder(12, 15, 12, 15));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Consolas", Font.BOLD, 10));
        lblTitle.setForeground(TEXT_LIGHT);

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblVal.setForeground(TEXT_GOLD);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(TEXT_MUTED);

        textPanel.add(lblTitle);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(lblVal);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(lblSub);

        p.putClientProperty("lblValue", lblVal);

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 28));

        p.add(textPanel, BorderLayout.CENTER);
        p.add(lblIcon, BorderLayout.EAST);

        return p;
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
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(800, 45));
        btn.setPreferredSize(new Dimension(0, 45));
        return btn;
    }

    private JPanel createLiveMatchCard(Partido partido, String estadio) {
        JPanel card = createRoundedCardPanel();
        card.setLayout(new BorderLayout());
        card.setMaximumSize(new Dimension(800, 160));
        card.setBorder(new EmptyBorder(12, 20, 12, 20));

        // Header EN VIVO
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel lblLiveInfo = new JLabel("● EN VIVO  -  " + partido.getFase().toUpperCase() + "  •  " + estadio.toUpperCase());
        lblLiveInfo.setFont(new Font("Consolas", Font.BOLD, 11));
        lblLiveInfo.setForeground(TEXT_RED);

        int min = minutosPartidos.getOrDefault(partido.getId(), 1);
        JLabel lblMin = new JLabel(min + "'");
        lblMin.setFont(new Font("Consolas", Font.BOLD, 12));
        lblMin.setForeground(TEXT_RED);

        header.add(lblLiveInfo, BorderLayout.WEST);
        header.add(lblMin, BorderLayout.EAST);

        // Body Marcador
        JPanel body = new JPanel(new GridLayout(1, 3));
        body.setOpaque(false);

        // Local
        JPanel pLoc = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        pLoc.setOpaque(false);
        JLabel flagLoc = new JLabel(obtenerBanderaEstetica(partido.getEquipoLocal().getNombre()));
        flagLoc.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        JLabel nameLoc = new JLabel(partido.getEquipoLocal().getNombre().toUpperCase());
        nameLoc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLoc.setForeground(Color.WHITE);
        pLoc.add(flagLoc); pLoc.add(nameLoc);

        // Score Central
        int gl = golesLocalVivo.getOrDefault(partido.getId(), 0);
        int gv = golesVisitaVivo.getOrDefault(partido.getId(), 0);
        JPanel pScore = new JPanel();
        pScore.setLayout(new BoxLayout(pScore, BoxLayout.Y_AXIS));
        pScore.setOpaque(false);
        JLabel lblScore = new JLabel(gl + " : " + gv, SwingConstants.CENTER);
        lblScore.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblScore.setForeground(TEXT_GOLD);
        lblScore.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTR = new JLabel("TIEMPO REAL", SwingConstants.CENTER);
        lblTR.setFont(new Font("Consolas", Font.BOLD, 9));
        lblTR.setForeground(TEXT_CYAN);
        lblTR.setAlignmentX(Component.CENTER_ALIGNMENT);
        pScore.add(lblScore);
        pScore.add(lblTR);

        // Visita
        JPanel pVis = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        pVis.setOpaque(false);
        JLabel flagVis = new JLabel(obtenerBanderaEstetica(partido.getEquipoVisita().getNombre()));
        flagVis.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        JLabel nameVis = new JLabel(partido.getEquipoVisita().getNombre().toUpperCase());
        nameVis.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameVis.setForeground(Color.WHITE);
        pVis.add(nameVis); pVis.add(flagVis);

        body.add(pLoc);
        body.add(pScore);
        body.add(pVis);

        // Botones "Cambiar Estado" y "Incidencias"
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttons.setOpaque(false);

        JButton btnScore = createActionBtn("Cambiar Estado");
        btnScore.addActionListener(e -> finalizarPartido(partido));
        
        JButton btnInc = createActionBtn("✏️ Incidencias / Goles");
        btnInc.addActionListener(e -> abrirDialogoGoles(partido));

        buttons.add(btnScore);
        buttons.add(btnInc);

        card.add(header, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createScheduledMatchCard(Partido partido, String horario, String estadio) {
        JPanel card = createRoundedCardPanel();
        card.setLayout(new BorderLayout());
        card.setMaximumSize(new Dimension(800, 130));
        card.setBorder(new EmptyBorder(12, 20, 12, 20));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel lblHeader = new JLabel(horario.toUpperCase() + "  -  " + partido.getFase().toUpperCase() + "  •  " + estadio.toUpperCase());
        lblHeader.setFont(new Font("Consolas", Font.BOLD, 11));
        lblHeader.setForeground(TEXT_LIGHT);
        header.add(lblHeader, BorderLayout.WEST);

        // Body VS
        JPanel body = new JPanel(new GridLayout(1, 3));
        body.setOpaque(false);

        JPanel pLoc = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        pLoc.setOpaque(false);
        JLabel nameLoc = new JLabel(partido.getEquipoLocal().getNombre().toUpperCase());
        nameLoc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLoc.setForeground(Color.WHITE);
        JLabel flagLoc = new JLabel(obtenerBanderaEstetica(partido.getEquipoLocal().getNombre()));
        flagLoc.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        pLoc.add(nameLoc); pLoc.add(flagLoc);

        JPanel pVS = new JPanel(new GridBagLayout());
        pVS.setOpaque(false);
        JLabel lblVS = new JLabel(" VS ");
        lblVS.setFont(new Font("Consolas", Font.BOLD, 12));
        lblVS.setForeground(TEXT_CYAN);
        lblVS.setOpaque(true);
        lblVS.setBackground(new Color(0, 212, 255, 30));
        lblVS.setBorder(BorderFactory.createLineBorder(new Color(0, 212, 255, 80)));
        pVS.add(lblVS);

        JPanel pVis = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        pVis.setOpaque(false);
        JLabel flagVis = new JLabel(obtenerBanderaEstetica(partido.getEquipoVisita().getNombre()));
        flagVis.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        JLabel nameVis = new JLabel(partido.getEquipoVisita().getNombre().toUpperCase());
        nameVis.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameVis.setForeground(Color.WHITE);
        pVis.add(flagVis); pVis.add(nameVis);

        body.add(pLoc);
        body.add(pVS);
        body.add(pVis);

        // Botones "Editar Horario" e "Iniciar"
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttons.setOpaque(false);

        JButton btnEdit = createActionBtn("✏️ Editar Horario");
        btnEdit.addActionListener(e -> abrirDialogoHorario(partido));

        JButton btnStart = createActionBtn("▶️ Iniciar");
        btnStart.addActionListener(e -> iniciarPartido(partido));

        buttons.add(btnEdit);
        buttons.add(btnStart);

        card.add(header, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createFinishedMatchCard(Partido partido, String estadio) {
        JPanel card = createRoundedCardPanel();
        card.setLayout(new BorderLayout());
        card.setMaximumSize(new Dimension(800, 120));
        card.setBorder(new EmptyBorder(12, 20, 12, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel lblHeader = new JLabel("FINALIZADO  -  " + partido.getFase().toUpperCase() + "  •  " + estadio.toUpperCase());
        lblHeader.setFont(new Font("Consolas", Font.BOLD, 11));
        lblHeader.setForeground(TEXT_MUTED);
        header.add(lblHeader, BorderLayout.WEST);

        JPanel body = new JPanel(new GridLayout(1, 3));
        body.setOpaque(false);

        JPanel pLoc = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        pLoc.setOpaque(false);
        JLabel nameLoc = new JLabel(partido.getEquipoLocal().getNombre().toUpperCase());
        nameLoc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nameLoc.setForeground(TEXT_LIGHT);
        pLoc.add(nameLoc);

        JPanel pScore = new JPanel(new GridBagLayout());
        pScore.setOpaque(false);
        JLabel score = new JLabel(partido.getGolesLocal() + " - " + partido.getGolesVisita());
        score.setFont(new Font("Segoe UI", Font.BOLD, 22));
        score.setForeground(TEXT_GOLD);
        pScore.add(score);

        JPanel pVis = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        pVis.setOpaque(false);
        JLabel nameVis = new JLabel(partido.getEquipoVisita().getNombre().toUpperCase());
        nameVis.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nameVis.setForeground(TEXT_LIGHT);
        pVis.add(nameVis);

        body.add(pLoc);
        body.add(pScore);
        body.add(pVis);

        card.add(header, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);

        return card;
    }

    private JButton createActionBtn(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BORDER_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                FontMetrics fm = g2.getFontMetrics(getFont());
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.setColor(Color.WHITE);
                g2.drawString(getText(), textX, textY);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 26));
        return btn;
    }

    private String obtenerBanderaEstetica(String pais) {
        String u = pais.toUpperCase();
        if (u.contains("MEX")) return "🇲🇽";
        if (u.contains("USA") || u.contains("ESTADOS UNIDOS")) return "🇺🇸";
        if (u.contains("ARG")) return "🇦🇷";
        if (u.contains("FRA")) return "🇫🇷";
        if (u.contains("ESP")) return "🇪🇸";
        if (u.contains("BRA")) return "🇧🇷";
        if (u.contains("CAN")) return "🇨🇦";
        if (u.contains("GER") || u.contains("ALEMANIA")) return "🇩🇪";
        if (u.contains("HOL") || u.contains("PAÍSES BAJOS")) return "🇳🇱";
        if (u.contains("COL")) return "🇨🇴";
        if (u.contains("URU")) return "🇺🇾";
        if (u.contains("JAP")) return "🇯🇵";
        return "🏳️";
    }

    // --- ACCIONES Y MODALES ---

    private void iniciarPartido(Partido p) {
        partidosEnVivo.add(p.getId());
        minutosPartidos.put(p.getId(), 1);
        golesLocalVivo.put(p.getId(), 0);
        golesVisitaVivo.put(p.getId(), 0);

        JOptionPane.showMessageDialog(this, "⚽ ¡El partido ha iniciado! Ahora se encuentra EN VIVO.", "Partido en Juego", JOptionPane.INFORMATION_MESSAGE);
        actualizarEstadisticasGlobales();
        filtrarYRenderizar();
    }

    private void finalizarPartido(Partido p) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Desea dar por finalizado el encuentro?\nSe registrarán los marcadores en tiempo real.",
                "Finalizar Partido", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        int gl = golesLocalVivo.getOrDefault(p.getId(), 0);
        int gv = golesVisitaVivo.getOrDefault(p.getId(), 0);

        if (controller.actualizarGolesPartido(p.getId(), gl, gv)) {
            partidosEnVivo.remove(p.getId());
            minutosPartidos.remove(p.getId());
            golesLocalVivo.remove(p.getId());
            golesVisitaVivo.remove(p.getId());

            JOptionPane.showMessageDialog(this, "🏆 Partido finalizado. Tabla de posiciones actualizada.", "Finalizado", JOptionPane.INFORMATION_MESSAGE);
            actualizarEstadisticasGlobales();
            filtrarYRenderizar();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar el resultado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirDialogoGoles(Partido p) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Registrar Goles / Incidencias", true);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(BG_CARD);
        content.setBorder(new EmptyBorder(15, 15, 15, 15));

        int gl = golesLocalVivo.getOrDefault(p.getId(), 0);
        int gv = golesVisitaVivo.getOrDefault(p.getId(), 0);

        JTextField txtL = new JTextField(String.valueOf(gl), 5);
        JTextField txtV = new JTextField(String.valueOf(gv), 5);
        txtL.setBackground(FIELD_BG); txtL.setForeground(Color.WHITE); txtL.setCaretColor(Color.WHITE);
        txtV.setBackground(FIELD_BG); txtV.setForeground(Color.WHITE); txtV.setCaretColor(Color.WHITE);

        JTextField txtMin = new JTextField(String.valueOf(minutosPartidos.getOrDefault(p.getId(), 1)), 5);
        txtMin.setBackground(FIELD_BG); txtMin.setForeground(Color.WHITE); txtMin.setCaretColor(Color.WHITE);

        javax.swing.text.DocumentFilter onlyNumbers = new javax.swing.text.DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
                if (string != null && string.matches("[0-9]*")) super.insertString(fb, offset, string, attr);
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
                if (text != null && text.matches("[0-9]*")) super.replace(fb, offset, length, text, attrs);
            }
        };
        ((javax.swing.text.AbstractDocument) txtL.getDocument()).setDocumentFilter(onlyNumbers);
        ((javax.swing.text.AbstractDocument) txtV.getDocument()).setDocumentFilter(onlyNumbers);
        ((javax.swing.text.AbstractDocument) txtMin.getDocument()).setDocumentFilter(onlyNumbers);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6); gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel l1 = new JLabel("Goles " + p.getEquipoLocal().getNombre() + ":"); l1.setForeground(Color.WHITE);
        JLabel l2 = new JLabel("Goles " + p.getEquipoVisita().getNombre() + ":"); l2.setForeground(Color.WHITE);
        JLabel l3 = new JLabel("Minuto actual:"); l3.setForeground(Color.WHITE);

        gbc.gridx=0; gbc.gridy=0; content.add(l1, gbc); gbc.gridx=1; content.add(txtL, gbc);
        gbc.gridx=0; gbc.gridy=1; content.add(l2, gbc); gbc.gridx=1; content.add(txtV, gbc);
        gbc.gridx=0; gbc.gridy=2; content.add(l3, gbc); gbc.gridx=1; content.add(txtMin, gbc);

        JButton btnGuardar = createGoldButton("Actualizar");
        btnGuardar.addActionListener(e -> {
            try {
                int nL = Integer.parseInt(txtL.getText().trim());
                int nV = Integer.parseInt(txtV.getText().trim());
                int min = Integer.parseInt(txtMin.getText().trim());

                golesLocalVivo.put(p.getId(), nL);
                golesVisitaVivo.put(p.getId(), nV);
                minutosPartidos.put(p.getId(), min);

                dialog.dispose();
                filtrarYRenderizar();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Valores numéricos inválidos.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=2; gbc.insets = new Insets(10, 5, 5, 5);
        content.add(btnGuardar, gbc);

        dialog.setContentPane(content);
        dialog.setVisible(true);
    }

    private void abrirDialogoHorario(Partido p) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Editar Horario / Estadio", true);
        dialog.setSize(320, 220);
        dialog.setLocationRelativeTo(this);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(BG_CARD);
        content.setBorder(new EmptyBorder(15, 15, 15, 15));

        JTextField txtHor = new JTextField(horariosPartidos.get(p.getId()), 12);
        JTextField txtEst = new JTextField(estadiosPartidos.get(p.getId()), 12);
        txtHor.setBackground(FIELD_BG); txtHor.setForeground(Color.WHITE); txtHor.setCaretColor(Color.WHITE);
        txtEst.setBackground(FIELD_BG); txtEst.setForeground(Color.WHITE); txtEst.setCaretColor(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel l1 = new JLabel("Horario:"); l1.setForeground(Color.WHITE);
        JLabel l2 = new JLabel("Estadio:"); l2.setForeground(Color.WHITE);

        gbc.gridx=0; gbc.gridy=0; content.add(l1, gbc); gbc.gridx=1; content.add(txtHor, gbc);
        gbc.gridx=0; gbc.gridy=1; content.add(l2, gbc); gbc.gridx=1; content.add(txtEst, gbc);

        JButton btnGuardar = createGoldButton("Guardar cambios");
        btnGuardar.addActionListener(e -> {
            String hor = txtHor.getText().trim();
            String est = txtEst.getText().trim();
            if (hor.isEmpty() || est.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Complete los campos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            horariosPartidos.put(p.getId(), hor);
            estadiosPartidos.put(p.getId(), est);
            dialog.dispose();
            filtrarYRenderizar();
        });

        gbc.gridx=0; gbc.gridy=2; gbc.gridwidth=2; gbc.insets = new Insets(15, 5, 5, 5);
        content.add(btnGuardar, gbc);

        dialog.setContentPane(content);
        dialog.setVisible(true);
    }

    private void abrirDialogoProgramar() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Programar Partido", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG_CARD);
        p.setBorder(new EmptyBorder(15, 15, 15, 15));

        JComboBox<Equipo> cbLoc = new JComboBox<>();
        JComboBox<Equipo> cbVis = new JComboBox<>();
        JComboBox<String> cbFaseProg = new JComboBox<>(new String[]{"Grupo", "16avos", "Octavos", "Cuartos", "Semis", "Final"});

        // Estadios oficiales del Mundial 2026
        String[] estadios2026 = {
            "MetLife Stadium (New York/New Jersey)",
            "SoFi Stadium (Los Angeles)",
            "AT&T Stadium (Dallas)",
            "Levi's Stadium (San Francisco)",
            "Arrowhead Stadium (Kansas City)",
            "Gillette Stadium (Boston)",
            "Lincoln Financial Field (Philadelphia)",
            "Hard Rock Stadium (Miami)",
            "Empower Field (Denver)",
            "Estadio Azteca (Ciudad de México)",
            "Estadio Akron (Guadalajara)",
            "Estadio BBVA (Monterrey)",
            "BC Place (Vancouver)",
            "BMO Field (Toronto)",
            "Stade Olympique (Montreal)"
        };
        JComboBox<String> cbEstadio = new JComboBox<>(estadios2026);

        cbLoc.setBackground(FIELD_BG); cbLoc.setForeground(Color.WHITE);
        cbVis.setBackground(FIELD_BG); cbVis.setForeground(Color.WHITE);
        cbFaseProg.setBackground(FIELD_BG); cbFaseProg.setForeground(Color.WHITE);
        cbEstadio.setBackground(FIELD_BG); cbEstadio.setForeground(Color.WHITE);

        List<Equipo> todos = controller.obtenerEquipos();
        for (Equipo eq : todos) {
            cbLoc.addItem(eq);
        }

        // Filtro dinámico de visita por fase de grupos
        cbLoc.addActionListener(e -> {
            Equipo localSel = (Equipo) cbLoc.getSelectedItem();
            String faseSel = (String) cbFaseProg.getSelectedItem();
            cbVis.removeAllItems();
            for (Equipo eq : todos) {
                if (localSel != null && eq.getId() == localSel.getId()) continue;
                if ("Grupo".equals(faseSel) && localSel != null) {
                    if (!eq.getGrupo().equals(localSel.getGrupo())) continue;
                }
                cbVis.addItem(eq);
            }
        });
        cbFaseProg.addActionListener(e -> {
            Equipo localSel = (Equipo) cbLoc.getSelectedItem();
            String faseSel = (String) cbFaseProg.getSelectedItem();
            cbVis.removeAllItems();
            for (Equipo eq : todos) {
                if (localSel != null && eq.getId() == localSel.getId()) continue;
                if ("Grupo".equals(faseSel) && localSel != null) {
                    if (!eq.getGrupo().equals(localSel.getGrupo())) continue;
                }
                cbVis.addItem(eq);
            }
        });

        // Forzar disparador inicial
        if (cbLoc.getItemCount() > 0) cbLoc.setSelectedIndex(0);

        JTextField txtHora = new JTextField("HOY - 21:00", 12);
        txtHora.setBackground(FIELD_BG); txtHora.setForeground(Color.WHITE); txtHora.setCaretColor(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx=0; gbc.gridy=0; p.add(new JLabel("Equipo 1:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx=1; p.add(cbLoc, gbc);

        gbc.gridx=0; gbc.gridy=1; p.add(new JLabel("Equipo 2:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx=1; p.add(cbVis, gbc);

        gbc.gridx=0; gbc.gridy=2; p.add(new JLabel("Fase:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx=1; p.add(cbFaseProg, gbc);

        gbc.gridx=0; gbc.gridy=3; p.add(new JLabel("Horario:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx=1; p.add(txtHora, gbc);

        gbc.gridx=0; gbc.gridy=4; p.add(new JLabel("Estadio:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx=1; p.add(cbEstadio, gbc);

        JButton btnGuardar = createGoldButton("Programar Partido");
        btnGuardar.addActionListener(e -> {
            Equipo local = (Equipo) cbLoc.getSelectedItem();
            Equipo visita = (Equipo) cbVis.getSelectedItem();
            String fase = (String) cbFaseProg.getSelectedItem();
            String hor = txtHora.getText().trim();
            String est = (String) cbEstadio.getSelectedItem();

            if (local == null || visita == null || hor.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Complete todos los campos.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            List<Partido> actuales = controller.obtenerPartidos();
            if (controller.crearPartido(local, visita, fase, actuales)) {
                // Obtener el partido recién programado para asignarle estadio y horario
                List<Partido> despues = controller.obtenerPartidos();
                if (!despues.isEmpty()) {
                    Partido nuevo = despues.get(despues.size() - 1);
                    horariosPartidos.put(nuevo.getId(), hor);
                    estadiosPartidos.put(nuevo.getId(), est);
                }

                JOptionPane.showMessageDialog(dialog, "✔ Partido programado con éxito.");
                actualizarEstadisticasGlobales();
                filtrarYRenderizar();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, controller.getUltimoError(), "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        gbc.gridx=0; gbc.gridy=5; gbc.gridwidth=2; gbc.insets = new Insets(15, 5, 5, 5);
        p.add(btnGuardar, gbc);

        dialog.setContentPane(p);
        dialog.setVisible(true);
    }
}
