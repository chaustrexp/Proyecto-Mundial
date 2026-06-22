package vistas;

import dao.EquipoDAO;
import dao.PartidoDAO;
import modelos.Equipo;
import modelos.Partido;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.kordamp.ikonli.swing.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import javax.swing.text.AbstractDocument;
import utils.ValidationUtils;
import org.kordamp.ikonli.Ikon;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;

public class PanelEquipos extends JPanel {

    // Paleta de colores extraída del mockup
    private static final Color BG_APP          = new Color(11, 19, 32);     // #0B1320
    private static final Color BG_CARD         = new Color(22, 32, 50);     // #162032
    private static final Color BORDER_CARD     = new Color(44, 58, 83);     // #2C3A53
    private static final Color TEXT_GOLD       = new Color(226, 185, 74);   // #E2B94A
    private static final Color TEXT_LIGHT      = new Color(194, 203, 224);  // #C2CBE0
    private static final Color TEXT_MUTED      = new Color(139, 149, 165);  // #8B95A5
    private static final Color TEXT_CYAN       = new Color(0, 212, 255);    // #00D4FF
    private static final Color FIELD_BG        = new Color(14, 21, 35);     // #0E1523
    private static final Color FIELD_BORDER    = new Color(27, 38, 59);     // #1B263B

    private EquipoDAO equipoDao;
    private PartidoDAO partidoDao;
    private boolean isAdmin;

    private JPanel groupsContainer; // Contenedor dinámico de grupos y equipos
    private JTextField txtSearch;   // Campo de búsqueda
    private JPanel statsPanel;

    // Cards de estadísticas globales
    private JLabel lblTotalTeams;
    private JLabel lblTotalGroups;

    private final String[] nombresEquiposDefecto = {
            "México", "Sudáfrica", "República de Corea", "Chequia",
            "Canadá", "Bosnia y Herzegovina", "Catar", "Suiza",
            "Brasil", "Marruecos", "Haití", "Escocia",
            "Estados Unidos", "Paraguay", "Australia", "Turquía",
            "Alemania", "Curazao", "Costa de Marfil", "Ecuador",
            "Países Bajos", "Japón", "Suecia", "Túnez",
            "Bélgica", "Egipto", "RI de Irán", "Nueva Zelanda",
            "España", "Cabo Verde", "Arabia Saudí", "Uruguay",
            "Francia", "Senegal", "Irak", "Noruega",
            "Argentina", "Argelia", "Austria", "Jordania",
            "Portugal", "RD Congo", "Uzbekistán", "Colombia",
            "Inglaterra", "Croacia", "Ghana", "Panamá"
    };

    public PanelEquipos(boolean isAdmin) {
        this.isAdmin = isAdmin;
        equipoDao = new EquipoDAO();
        partidoDao = new PartidoDAO();

        setLayout(new BorderLayout());
        setBackground(BG_APP);

        // Contenedor principal con Scroll
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_APP);
        contentPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        // 1. CABECERA
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("Equipos Participantes");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDesc = new JLabel(isAdmin ? "Panel de control administrativo para el Mundial 2026" : "Información de los equipos del torneo");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(TEXT_MUTED);
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lblTitle);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(lblDesc);
        headerPanel.add(Box.createVerticalStrut(20));

        // 2. BUSCADOR
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
        txtSearch.setMaximumSize(new Dimension(800, 45));
        txtSearch.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Placeholder / Info
        txtSearch.setToolTipText("Buscar país o grupo...");
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filtrarYRenderizar(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filtrarYRenderizar(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filtrarYRenderizar(); }
        });

        headerPanel.add(txtSearch);
        headerPanel.add(Box.createVerticalStrut(25));
        contentPanel.add(headerPanel);

        // 3. TARJETAS DE ESTADÍSTICAS GLOBALES
        statsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(800, 100));
        statsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel card1 = createStatCard("EQUIPOS CLASIFICADOS", "0", FontAwesomeSolid.GLOBE_AMERICAS);
        JPanel card2 = createStatCard("GRUPOS ACTIVOS", "0", FontAwesomeSolid.SLIDERS_H);

        lblTotalTeams = (JLabel) card1.getClientProperty("lblValue");
        lblTotalGroups = (JLabel) card2.getClientProperty("lblValue");

        statsPanel.add(card1);
        statsPanel.add(card2);
        contentPanel.add(statsPanel);
        contentPanel.add(Box.createVerticalStrut(30));

        // 4. CONTENEDOR DE GRUPOS Y TARJETAS
        groupsContainer = new JPanel();
        groupsContainer.setLayout(new BoxLayout(groupsContainer, BoxLayout.Y_AXIS));
        groupsContainer.setOpaque(false);
        groupsContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(groupsContainer);

        // 5. BOTÓN AÑADIR PUNTEADO (Solo admin)
        if (isAdmin) {
            JPanel btnAddDotted = createDottedAddCard();
            btnAddDotted.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnAddDotted.setMaximumSize(new Dimension(800, 80));
            contentPanel.add(btnAddDotted);
            contentPanel.add(Box.createVerticalStrut(40));
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        add(scrollPane, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(BG_APP);
        JLabel footTxt = new JLabel(isAdmin ? "TROPHY ELITE 2026 ADMIN CONSOLE  •  LATENCY: 24ms  •  STATUS: OPTIMAL" : "TROPHY ELITE 2026 EXPLORER  •  SYSTEM OK");
        footTxt.setForeground(TEXT_CYAN);
        footTxt.setFont(new Font("Consolas", Font.BOLD, 10));
        footer.add(footTxt);
        add(footer, BorderLayout.SOUTH);

        // Cargar datos
        actualizarEstadisticasGlobales();
        filtrarYRenderizar();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int width = getWidth();
                if (width < 750) {
                    statsPanel.setLayout(new GridLayout(2, 1, 0, 15));
                    statsPanel.setMaximumSize(new Dimension(800, 220));
                } else {
                    statsPanel.setLayout(new GridLayout(1, 2, 20, 0));
                    statsPanel.setMaximumSize(new Dimension(800, 100));
                }
                statsPanel.revalidate();
            }
        });
    }

    private void actualizarEstadisticasGlobales() {
        List<Equipo> equipos = equipoDao.obtenerEquipos();
        lblTotalTeams.setText(String.valueOf(equipos.size()));

        Set<String> grupos = new HashSet<>();
        for (Equipo eq : equipos) {
            if (eq.getGrupo() != null && !eq.getGrupo().trim().isEmpty()) {
                grupos.add(eq.getGrupo().toUpperCase());
            }
        }

        lblTotalGroups.setText(String.valueOf(grupos.size()));
    }

    private void filtrarYRenderizar() {
        groupsContainer.removeAll();

        String query = txtSearch.getText().toLowerCase().trim();
        List<Equipo> todos = equipoDao.obtenerEquipos();
        List<Partido> partidos = partidoDao.obtenerPartidos();

        // Calcular estadísticas acumuladas por equipo
        Map<Integer, int[]> statsMap = calcularStatsEquipos(todos, partidos);

        // Agrupar equipos por su Grupo
        Map<String, List<Equipo>> gruposMap = new TreeMap<>();
        for (Equipo eq : todos) {
            // Aplicar filtro de búsqueda por país o grupo
            boolean coincidePais = eq.getNombre().toLowerCase().contains(query);
            boolean coincideGrupo = ("grupo " + eq.getGrupo().toLowerCase()).contains(query) || eq.getGrupo().toLowerCase().equals(query);
            boolean coincideConf = eq.getFederacion().toLowerCase().contains(query);

            if (query.isEmpty() || coincidePais || coincideGrupo || coincideConf) {
                String gName = "GRUPO " + eq.getGrupo().toUpperCase();
                gruposMap.putIfAbsent(gName, new ArrayList<>());
                gruposMap.get(gName).add(eq);
            }
        }

        // Renderizar por cada grupo
        for (Map.Entry<String, List<Equipo>> entry : gruposMap.entrySet()) {
            String grupoNombre = entry.getKey();
            List<Equipo> listaGrupo = entry.getValue();

            // Titulo de grupo
            JPanel groupHeader = new JPanel();
            groupHeader.setLayout(new BoxLayout(groupHeader, BoxLayout.X_AXIS));
            groupHeader.setOpaque(false);
            groupHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
            groupHeader.setMaximumSize(new Dimension(800, 30));

            JLabel lblGroup = new JLabel(grupoNombre);
            lblGroup.setFont(new Font("Consolas", Font.BOLD, 14));
            lblGroup.setForeground(TEXT_LIGHT);

            JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL) {
                @Override
                protected void paintComponent(Graphics g) {
                    g.setColor(BORDER_CARD);
                    g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
                }
            };

            groupHeader.add(lblGroup);
            groupHeader.add(Box.createHorizontalStrut(15));
            groupHeader.add(separator);

            groupsContainer.add(groupHeader);
            groupsContainer.add(Box.createVerticalStrut(15));

            // Tarjetas de equipos de este grupo
            for (Equipo eq : listaGrupo) {
                int[] stats = statsMap.getOrDefault(eq.getId(), new int[]{0, 0, 0}); // [PJ, Goles, Pts]
                JPanel eqCard = createEquipoCard(eq, stats[0], stats[1], stats[2]);
                eqCard.setAlignmentX(Component.CENTER_ALIGNMENT);
                groupsContainer.add(eqCard);
                groupsContainer.add(Box.createVerticalStrut(15));
            }
            groupsContainer.add(Box.createVerticalStrut(15));
        }

        groupsContainer.revalidate();
        groupsContainer.repaint();
    }

    private Map<Integer, int[]> calcularStatsEquipos(List<Equipo> equipos, List<Partido> partidos) {
        Map<Integer, int[]> map = new HashMap<>();
        for (Equipo eq : equipos) {
            map.put(eq.getId(), new int[]{0, 0, 0}); // [PJ, Goles, Pts]
        }

        for (Partido p : partidos) {
            if (p.getGolesLocal() == null || p.getGolesVisita() == null) continue;

            int gl = p.getGolesLocal();
            int gv = p.getGolesVisita();

            int idL = p.getEquipoLocal().getId();
            int idV = p.getEquipoVisita().getId();

            int[] statsL = map.get(idL);
            int[] statsV = map.get(idV);

            if (statsL != null) {
                statsL[0]++; // PJ
                statsL[1] += gl; // Goles
                if (gl > gv) statsL[2] += 3;
                else if (gl == gv) statsL[2] += 1;
            }

            if (statsV != null) {
                statsV[0]++; // PJ
                statsV[1] += gv; // Goles
                if (gv > gl) statsV[2] += 3;
                else if (gv == gl) statsV[2] += 1;
            }
        }
        return map;
    }

    // --- CREADORES DE COMPONENTES VISUALES ---

    private JPanel createStatCard(String title, String value, Ikon icon) {
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
        p.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel textSide = new JPanel();
        textSide.setLayout(new BoxLayout(textSide, BoxLayout.Y_AXIS));
        textSide.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Consolas", Font.BOLD, 10));
        lblTitle.setForeground(TEXT_LIGHT);

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblVal.setForeground(TEXT_GOLD);

        textSide.add(lblTitle);
        textSide.add(Box.createVerticalStrut(5));
        textSide.add(lblVal);

        p.putClientProperty("lblValue", lblVal);

        FontIcon fIcon = FontIcon.of(icon, 28, TEXT_LIGHT);
        JLabel lblIcon = new JLabel(fIcon);

        p.add(textSide, BorderLayout.CENTER);
        p.add(lblIcon, BorderLayout.EAST);

        return p;
    }

    private JPanel createEquipoCard(Equipo equipo, int pj, int goles, int pts) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(BORDER_CARD);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 20, 15, 20));
        card.setMaximumSize(new Dimension(800, 150));

        // PARTE SUPERIOR (Bandera, Nombres, Badge de Sembrado)
        JPanel topSide = new JPanel(new BorderLayout());
        topSide.setOpaque(false);

        JPanel flagAndNames = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        flagAndNames.setOpaque(false);

        JLabel lblFlag = new JLabel(FontIcon.of(FontAwesomeSolid.FLAG, 28, TEXT_MUTED));

        // Cargar bandera real asincrónicamente
        utils.FlagManager.setFlagIconAsync(lblFlag, equipo.getNombre(), 36, 26);

        JPanel names = new JPanel();
        names.setLayout(new BoxLayout(names, BoxLayout.Y_AXIS));
        names.setOpaque(false);

        JLabel lblName = new JLabel(equipo.getNombre());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblName.setForeground(Color.WHITE);

        JLabel lblConf = new JLabel(equipo.getFederacion().toUpperCase());
        lblConf.setFont(new Font("Consolas", Font.BOLD, 10));
        lblConf.setForeground(TEXT_MUTED);

        names.add(lblName);
        names.add(lblConf);

        flagAndNames.add(lblFlag);
        flagAndNames.add(names);

        // Badge de Rank / Sembrado
        String badgeText = "RANK " + (equipo.getId() % 8 + 1);
        if (equipo.getNombre().equalsIgnoreCase("Argentina")) badgeText = "CHAMPION";
        else if (equipo.getNombre().equalsIgnoreCase("Francia")) badgeText = "SEED 1";
        else if (equipo.getNombre().equalsIgnoreCase("México") || equipo.getNombre().equalsIgnoreCase("Estados Unidos") || equipo.getNombre().equalsIgnoreCase("Canadá")) badgeText = "ANFITRIÓN";

        JLabel lblBadge = new JLabel(" " + badgeText + " ");
        lblBadge.setFont(new Font("Consolas", Font.BOLD, 10));
        lblBadge.setForeground(TEXT_CYAN);
        lblBadge.setOpaque(true);
        lblBadge.setBackground(new Color(0, 212, 255, 30));
        lblBadge.setBorder(BorderFactory.createLineBorder(new Color(0, 212, 255, 100)));

        JPanel badgeContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        badgeContainer.setOpaque(false);
        badgeContainer.add(lblBadge);

        topSide.add(flagAndNames, BorderLayout.WEST);
        topSide.add(badgeContainer, BorderLayout.EAST);

        card.add(topSide, BorderLayout.NORTH);

        // PARTE CENTRAL (Estadísticas de Liga)
        JPanel statsContainer = new JPanel(new GridLayout(1, 3, 10, 0));
        statsContainer.setOpaque(false);
        statsContainer.setBorder(new EmptyBorder(10, 45, 10, 45));

        statsContainer.add(createMiniStatColumn("PJ", String.valueOf(pj)));
        statsContainer.add(createMiniStatColumn("Goles", String.valueOf(goles)));
        statsContainer.add(createMiniStatColumn("Pts", String.valueOf(pts)));

        card.add(statsContainer, BorderLayout.CENTER);

        if (isAdmin) {
            // PARTE INFERIOR (Botón Editar Stats / Datos)
            JButton btnEdit = new JButton(" Editar Stats / Datos") {
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
            btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnEdit.setIcon(FontIcon.of(FontAwesomeSolid.PENCIL_ALT, 12, new Color(29, 29, 29)));
            btnEdit.setContentAreaFilled(false);
            btnEdit.setBorderPainted(false);
            btnEdit.setFocusPainted(false);
            btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnEdit.setPreferredSize(new Dimension(180, 30));
            btnEdit.addActionListener(e -> abrirDialogoEquipo(equipo));

            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
            bottomPanel.setOpaque(false);
            bottomPanel.add(btnEdit);

            card.add(bottomPanel, BorderLayout.SOUTH);
        }

        return card;
    }

    private JPanel createMiniStatColumn(String label, String value) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel lblL = new JLabel(label, SwingConstants.CENTER);
        lblL.setFont(new Font("Consolas", Font.PLAIN, 10));
        lblL.setForeground(TEXT_MUTED);
        lblL.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblV = new JLabel(value, SwingConstants.CENTER);
        lblV.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblV.setForeground(Color.WHITE);
        lblV.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(lblL);
        p.add(Box.createVerticalStrut(3));
        p.add(lblV);

        return p;
    }

    private JPanel createDottedAddCard() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(new Color(11, 19, 32));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                
                float[] dash = { 5.0f, 5.0f };
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, dash, 0.0f));
                g2.setColor(BORDER_CARD);
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 16, 16));
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setLayout(new GridBagLayout());
        p.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel plus = new JLabel("⊕ AÑADIR NUEVO EQUIPO", SwingConstants.CENTER);
        plus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        plus.setForeground(TEXT_GOLD);

        p.add(plus);

        p.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                abrirDialogoEquipo(null);
            }
        });

        return p;
    }

    // --- DIALOGO DE EDICION Y CREACION ---

    private void abrirDialogoEquipo(Equipo eq) {
        boolean esNuevo = (eq == null);
        String titulo = esNuevo ? "Registrar Equipo" : "Editar Equipo";
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), titulo, true);
        dialog.setSize(380, 320);
        dialog.setLocationRelativeTo(this);

        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG_CARD);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField txtNombre = new JTextField(15);
        JTextField txtGrupo = new JTextField(5);
        JTextField txtConf = new JTextField(15);
        
        ((AbstractDocument) txtNombre.getDocument()).setDocumentFilter(ValidationUtils.getLettersOnlyFilter(40));
        ((AbstractDocument) txtGrupo.getDocument()).setDocumentFilter(ValidationUtils.getGroupLetterFilter());
        ((AbstractDocument) txtConf.getDocument()).setDocumentFilter(ValidationUtils.getLettersOnlyFilter(40));

        txtNombre.setBackground(FIELD_BG); txtNombre.setForeground(Color.WHITE); txtNombre.setCaretColor(Color.WHITE);
        txtGrupo.setBackground(FIELD_BG); txtGrupo.setForeground(Color.WHITE); txtGrupo.setCaretColor(Color.WHITE);
        txtConf.setBackground(FIELD_BG); txtConf.setForeground(Color.WHITE); txtConf.setCaretColor(Color.WHITE);

        if (!esNuevo) {
            txtNombre.setText(eq.getNombre());
            txtGrupo.setText(eq.getGrupo());
            txtConf.setText(eq.getFederacion());
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel l1 = new JLabel("Nombre:"); l1.setForeground(Color.WHITE);
        JLabel l2 = new JLabel("Grupo (A-L):"); l2.setForeground(Color.WHITE);
        JLabel l3 = new JLabel("Confederación:"); l3.setForeground(Color.WHITE);

        gbc.gridx=0; gbc.gridy=0; p.add(l1, gbc); gbc.gridx=1; p.add(txtNombre, gbc);
        gbc.gridx=0; gbc.gridy=1; p.add(l2, gbc); gbc.gridx=1; p.add(txtGrupo, gbc);
        gbc.gridx=0; gbc.gridy=2; p.add(l3, gbc); gbc.gridx=1; p.add(txtConf, gbc);

        // Botón de guardar
        JButton btnGuardar = new JButton("Guardar") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TEXT_GOLD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                FontMetrics fm = g2.getFontMetrics(getFont());
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.setColor(new Color(29, 29, 29));
                g2.drawString(getText(), textX, textY);
                g2.dispose();
            }
        };
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setContentAreaFilled(false);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.setPreferredSize(new Dimension(0, 40));

        btnGuardar.addActionListener(e -> {
            String name = txtNombre.getText().trim();
            String group = txtGrupo.getText().trim().toUpperCase();
            String conf = txtConf.getText().trim();

            if (name.isEmpty() || group.isEmpty() || conf.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Todos los campos son obligatorios.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (group.length() != 1 || group.charAt(0) < 'A' || group.charAt(0) > 'L') {
                JOptionPane.showMessageDialog(dialog, "El grupo debe ser una única letra entre la A y la L.", "Grupo inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (esNuevo) {
                Equipo nuevo = new Equipo(0, name, group, conf);
                if (equipoDao.insertarEquipo(nuevo)) {
                    JOptionPane.showMessageDialog(dialog, "Equipo registrado con éxito.");
                    actualizarEstadisticasGlobales();
                    filtrarYRenderizar();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Error al insertar el equipo.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                eq.setNombre(name);
                eq.setGrupo(group);
                eq.setFederacion(conf);
                if (equipoDao.actualizarEquipo(eq)) {
                    JOptionPane.showMessageDialog(dialog, "Datos de equipo actualizados.");
                    actualizarEstadisticasGlobales();
                    filtrarYRenderizar();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Error al actualizar el equipo.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=2; gbc.insets = new Insets(15, 8, 8, 8);
        p.add(btnGuardar, gbc);

        // Si es nuevo y no hay equipos, mostramos el botón de precarga automática
        if (esNuevo && equipoDao.contarEquipos() == 0) {
            JButton btnPrecargar = new JButton("Precargar 48 Equipos") {
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
            btnPrecargar.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnPrecargar.setContentAreaFilled(false);
            btnPrecargar.setBorderPainted(false);
            btnPrecargar.setFocusPainted(false);
            btnPrecargar.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnPrecargar.setPreferredSize(new Dimension(0, 35));

            btnPrecargar.addActionListener(e -> {
                dialog.dispose();
                precargarEquipos();
            });

            gbc.gridy=4; gbc.insets = new Insets(5, 8, 8, 8);
            p.add(btnPrecargar, gbc);
        }

        dialog.setContentPane(p);
        dialog.setVisible(true);
    }

    private void precargarEquipos() {
        if (equipoDao.contarEquipos() > 0) {
            JOptionPane.showMessageDialog(this, "Los equipos ya están cargados en la base de datos.");
            return;
        }

        int index = 0;
        char grupoActual = 'A';
        for (String nombre : nombresEquiposDefecto) {
            Equipo eq = new Equipo(0, nombre, String.valueOf(grupoActual), "Por definir");
            equipoDao.insertarEquipo(eq);
            
            index++;
            if (index % 4 == 0) {
                grupoActual++;
            }
        }
        JOptionPane.showMessageDialog(this, "¡Los 48 equipos han sido cargados exitosamente!");
        actualizarEstadisticasGlobales();
        filtrarYRenderizar();
    }
}
