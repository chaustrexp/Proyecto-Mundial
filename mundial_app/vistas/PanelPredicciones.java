package vistas;

import controladores.PrediccionController;
import modelos.Apostador;
import modelos.Partido;
import modelos.Prediccion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Random;

public class PanelPredicciones extends JPanel {

    // Paleta de colores
    private static final Color BG_APP          = new Color(11, 19, 32);     // #0B1320
    private static final Color BG_CARD         = new Color(22, 32, 50);     // #162032
    private static final Color BORDER_CARD     = new Color(44, 58, 83);     // #2C3A53
    private static final Color TEXT_GOLD       = new Color(226, 185, 74);   // #E2B94A
    private static final Color TEXT_LIGHT      = new Color(194, 203, 224);  // #C2CBE0
    private static final Color TEXT_MUTED      = new Color(139, 149, 165);  // #8B95A5
    private static final Color TEXT_CYAN       = new Color(0, 212, 255);    // #00D4FF
    private static final Color FIELD_BG        = new Color(14, 21, 35);     // #0E1523
    private static final Color FIELD_BORDER    = new Color(27, 38, 59);     // #1B263B

    private PrediccionController controller;
    private JPanel contentPanel;
    
    // Labels dinamicos
    private JLabel lblTotalPreds;
    private JLabel lblVolumen;

    public PanelPredicciones(boolean isAdmin) {
        controller = new PrediccionController();
        setLayout(new BorderLayout());
        setBackground(BG_APP);

        // Header Title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(15, 25, 10, 25));
        
        JLabel lblTitle = new JLabel("Panel de Predicciones");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(TEXT_GOLD);
        lblTitle.setHorizontalAlignment(SwingConstants.RIGHT);
        
        headerPanel.add(lblTitle, BorderLayout.EAST);

        // Contenedor Scrollable Principal
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_APP);
        contentPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        // 1. FILTROS Y BOTONES
        JPanel filtersPanel = new JPanel(new BorderLayout());
        filtersPanel.setOpaque(false);
        filtersPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        
        JPanel leftFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftFilters.setOpaque(false);
        
        // Obtener fecha/hora real
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMMM yyyy HH:mm:ss", new java.util.Locale("es", "ES"));
        String fechaHoyStr = "📅 " + sdf.format(new java.util.Date());
        
        JButton btnDate = createFilterButton(fechaHoyStr);
        // Actualizar la hora en tiempo real cada segundo
        Timer timer = new Timer(1000, evt -> {
            btnDate.setText("📅 " + sdf.format(new java.util.Date()));
        });
        timer.start();
        
        leftFilters.add(createFilterButton("🏆 Todos los Grupos"));
        leftFilters.add(btnDate);
        
        JPanel rightBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightBtnPanel.setOpaque(false);
        if (!isAdmin) {
            JButton btnNewPred = createGoldButton("➕ Nueva Predicción");
            btnNewPred.addActionListener(e -> abrirDialogoRegistro());
            rightBtnPanel.add(btnNewPred);
        }
        
        filtersPanel.add(leftFilters, BorderLayout.WEST);
        filtersPanel.add(rightBtnPanel, BorderLayout.EAST);
        
        contentPanel.add(filtersPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // 2. TARJETAS ESTADÍSTICAS (Distribución: Columnas directas en contentPanel)
        JPanel statsContainer = new JPanel(new GridLayout(1, 3, 15, 0));
        statsContainer.setOpaque(false);
        statsContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        lblTotalPreds = new JLabel("0");
        lblTotalPreds.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTotalPreds.setForeground(Color.WHITE);
        statsContainer.add(createStatCard("Predicciones Totales", "📈", lblTotalPreds, "Acumulado desde el inicio del torneo", TEXT_GOLD));

        lblVolumen = new JLabel("$0");
        lblVolumen.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblVolumen.setForeground(Color.WHITE);
        statsContainer.add(createStatCard("Volumen Real (Predicciones)", "💵", lblVolumen, "Valor total acumulado en predicciones reales", TEXT_CYAN));

        JLabel lblTendencia = new JLabel("—");
        lblTendencia.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTendencia.setForeground(TEXT_GOLD);
        statsContainer.add(createStatCard("Tendencia Global", "↗", lblTendencia, "Equipo con mayor cantidad de apoyo", TEXT_LIGHT));

        contentPanel.add(statsContainer);
        contentPanel.add(Box.createVerticalStrut(25));

        // 3. PRÓXIMOS ENCUENTROS
        JPanel matchesHeader = new JPanel(new BorderLayout());
        matchesHeader.setOpaque(false);
        matchesHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel lblSub = new JLabel("Próximos Encuentros (Mismo Grupo)");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSub.setForeground(Color.WHITE);
        JLabel lblVerTodos = new JLabel("Ver todos");
        lblVerTodos.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblVerTodos.setForeground(TEXT_GOLD);
        matchesHeader.add(lblSub, BorderLayout.WEST);
        matchesHeader.add(lblVerTodos, BorderLayout.EAST);
        contentPanel.add(matchesHeader);
        contentPanel.add(Box.createVerticalStrut(10));

        // Obtener encuentros reales desde la base de datos que pertenezcan al mismo grupo
        List<Partido> partidosReal = controller.obtenerPartidos();
        int matchesRendered = 0;
        for (Partido p : partidosReal) {
            if (p.getGolesLocal() == null && matchesRendered < 2) {
                // Verificar que ambos equipos pertenezcan al mismo grupo
                if (p.getEquipoLocal().getGrupo().equalsIgnoreCase(p.getEquipoVisita().getGrupo())) {
                    contentPanel.add(createMatchCard(p.getEquipoLocal().getNombre(), p.getEquipoVisita().getNombre(), "21:00", 45, 20, 35, "12.4k Predictores", "$2.1M Total"));
                    contentPanel.add(Box.createVerticalStrut(15));
                    matchesRendered++;
                }
            }
        }
        if (matchesRendered == 0) {
            contentPanel.add(createMatchCard("México", "Chequia", "20:00", 42, 18, 40, "12.4k Predictores", "$2.1M Total"));
            contentPanel.add(Box.createVerticalStrut(15));
            contentPanel.add(createMatchCard("Brasil", "Haití", "22:30", 65, 10, 25, "45.8k Predictores", "$8.4M Total"));
            contentPanel.add(Box.createVerticalStrut(15));
        }
        contentPanel.add(Box.createVerticalStrut(10));

        // 4. TOP PREDICTORES Y EFICIENCIA
        JPanel bottomContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomContainer.setOpaque(false);
        bottomContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        
        bottomContainer.add(createRankingPanel());
        bottomContainer.add(createEfficiencyPanel());

        contentPanel.add(bottomContainer);
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(BG_APP);
        JLabel footTxt = new JLabel("© 2026 FIFA World Cup Betting System");
        footTxt.setForeground(TEXT_MUTED);
        footTxt.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footer.add(footTxt);
        add(footer, BorderLayout.SOUTH);

        actualizarDatosReales(lblTendencia);
    }

    private void actualizarDatosReales(JLabel lblTendencia) {
        List<Prediccion> preds = controller.obtenerPredicciones();
        lblTotalPreds.setText(String.format("%,d", preds.size()));
        
        // Volumen real acumulado en base a predicciones (ej. 1500 por cada predicción en BD)
        int volReal = preds.size() * 1500;
        lblVolumen.setText(String.format("$%,d", volReal));

        // Obtener el equipo de mayor tendencia (el más apoyado en predicciones)
        if (!preds.isEmpty()) {
            java.util.Map<String, Integer> conteoEquipos = new java.util.HashMap<>();
            for (Prediccion p : preds) {
                // Si predice ganar local o visitante
                String apoyado = null;
                if (p.getGolesPredEq1() > p.getGolesPredEq2()) {
                    apoyado = p.getPartido().getEquipoLocal().getNombre();
                } else if (p.getGolesPredEq2() > p.getGolesPredEq1()) {
                    apoyado = p.getPartido().getEquipoVisita().getNombre();
                }
                if (apoyado != null) {
                    conteoEquipos.put(apoyado, conteoEquipos.getOrDefault(apoyado, 0) + 1);
                }
            }
            String topEquipo = "—";
            int maxApoyo = -1;
            for (java.util.Map.Entry<String, Integer> e : conteoEquipos.entrySet()) {
                if (e.getValue() > maxApoyo) {
                    maxApoyo = e.getValue();
                    topEquipo = e.getKey();
                }
            }
            lblTendencia.setText(topEquipo);
        } else {
            lblTendencia.setText("—");
        }
    }

    // --- CREADORES DE COMPONENTES VISUALES ---

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

    private JPanel createStatCard(String title, String icon, JLabel mainValue, String subText, Color titleColor) {
        JPanel card = createRoundedPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel topP = new JPanel(new BorderLayout());
        topP.setOpaque(false);
        JLabel lblIcon = new JLabel(icon);
        lblIcon.setForeground(titleColor);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(TEXT_MUTED);
        topP.add(lblIcon, BorderLayout.WEST);
        topP.add(lblTitle, BorderLayout.CENTER);

        JPanel centerP = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 8));
        centerP.setOpaque(false);
        centerP.add(mainValue);

        JLabel lblSub = new JLabel("<html>" + subText.replace(" ", "&nbsp;") + "</html>");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(TEXT_MUTED);

        card.add(topP, BorderLayout.NORTH);
        card.add(centerP, BorderLayout.CENTER);
        card.add(lblSub, BorderLayout.SOUTH);

        return card;
    }

    private JButton createDarkButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? BORDER_CARD : FIELD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                g2.setColor(BORDER_CARD);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 6, 6));
                
                FontMetrics fm = g2.getFontMetrics(getFont());
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.setColor(TEXT_LIGHT);
                g2.drawString(getText(), textX, textY);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 32));
        return btn;
    }

    private JButton createFilterButton(String text) {
        JButton btn = createDarkButton(text);
        btn.setPreferredSize(new Dimension(180, 32));
        return btn;
    }

    private JButton createGoldButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? TEXT_GOLD.brighter() : TEXT_GOLD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                
                FontMetrics fm = g2.getFontMetrics(getFont());
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.setColor(new Color(29, 29, 29));
                g2.drawString(getText(), textX, textY);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 32));
        return btn;
    }

    private JPanel createMatchCard(String eq1, String eq2, String time, int locPct, int empPct, int visPct, String predictores, String dinero) {
        JPanel card = createRoundedPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(15, 20, 15, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        // Nombres y VS
        JPanel topP = new JPanel(new GridLayout(1, 3));
        topP.setOpaque(false);
        JLabel l1 = new JLabel("🏳 " + eq1, SwingConstants.CENTER); l1.setForeground(Color.WHITE); l1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel lvs = new JLabel(time, SwingConstants.CENTER); lvs.setForeground(TEXT_MUTED); lvs.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel l2 = new JLabel("🏳 " + eq2, SwingConstants.CENTER); l2.setForeground(Color.WHITE); l2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        topP.add(l1); topP.add(lvs); topP.add(l2);

        // Progress Bar Labels
        JPanel lblsP = new JPanel(new BorderLayout());
        lblsP.setOpaque(false);
        JLabel ll = new JLabel("Local (" + locPct + "%)"); ll.setForeground(TEXT_LIGHT); ll.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        JLabel le = new JLabel("Empate (" + empPct + "%)", SwingConstants.CENTER); le.setForeground(TEXT_LIGHT); le.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        JLabel lv = new JLabel("Visita (" + visPct + "%)"); lv.setForeground(TEXT_LIGHT); lv.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblsP.add(ll, BorderLayout.WEST);
        lblsP.add(le, BorderLayout.CENTER);
        lblsP.add(lv, BorderLayout.EAST);

        // Barra de progreso dibujada
        JPanel barP = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = 6, y = (getHeight()-h)/2;
                g2.setColor(FIELD_BG);
                g2.fillRoundRect(0, y, w, h, 6, 6);
                
                int wL = (int)(w * (locPct/100.0));
                int wE = (int)(w * (empPct/100.0));
                
                g2.setColor(TEXT_GOLD);
                g2.fillRoundRect(0, y, wL, h, 6, 6);
                g2.setColor(BORDER_CARD);
                g2.fillRect(wL, y, wE, h);
                g2.setColor(TEXT_CYAN);
                g2.fillRoundRect(wL + wE, y, w - (wL+wE), h, 6, 6);
                
                g2.dispose();
            }
        };
        barP.setOpaque(false);
        barP.setPreferredSize(new Dimension(0, 15));

        // Stats Footer
        JPanel footP = new JPanel(new BorderLayout());
        footP.setOpaque(false);
        JLabel lp = new JLabel("📊 " + predictores); lp.setForeground(TEXT_MUTED); lp.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        JLabel ld = new JLabel(dinero); ld.setForeground(TEXT_GOLD); ld.setFont(new Font("Segoe UI", Font.BOLD, 10));
        footP.add(lp, BorderLayout.WEST);
        footP.add(ld, BorderLayout.EAST);

        card.add(topP);
        card.add(Box.createVerticalStrut(15));
        card.add(lblsP);
        card.add(barP);
        card.add(Box.createVerticalStrut(5));
        card.add(footP);

        return card;
    }

    private JPanel createRankingPanel() {
        JPanel card = createRoundedPanel();
        card.setLayout(new BorderLayout());
        
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15, 20, 10, 20));
        JLabel lblT = new JLabel("Top Predictores"); lblT.setForeground(Color.WHITE); lblT.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel lblR = new JLabel("Ranking"); lblR.setForeground(TEXT_CYAN); lblR.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.add(lblT, BorderLayout.WEST);
        header.add(lblR, BorderLayout.EAST);
        
        JPanel cols = new JPanel(new BorderLayout());
        cols.setOpaque(false);
        cols.setBorder(new EmptyBorder(0, 20, 5, 20));
        JLabel cu = new JLabel("USUARIO"); cu.setForeground(TEXT_MUTED); cu.setFont(new Font("Consolas", Font.BOLD, 10));
        
        JPanel cRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0)); cRight.setOpaque(false);
        JLabel cp = new JLabel("PRED."); cp.setForeground(TEXT_MUTED); cp.setFont(new Font("Consolas", Font.BOLD, 10));
        JLabel ca = new JLabel("PUNTOS"); ca.setForeground(TEXT_MUTED); ca.setFont(new Font("Consolas", Font.BOLD, 10));
        cRight.add(cp); cRight.add(ca);
        cols.add(cu, BorderLayout.WEST);
        cols.add(cRight, BorderLayout.EAST);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);
        
        // Obtener apostadores registrados de la base de datos
        List<Apostador> apostadores = controller.obtenerApostadores();
        int count = 0;
        for (Apostador a : apostadores) {
            if (count < 3) {
                String initials = a.getNombre().substring(0, Math.min(a.getNombre().length(), 2)).toUpperCase();
                // Obtener total predicciones del apostador
                long totalPred = controller.obtenerPredicciones().stream().filter(p -> p.getApostador().getId() == a.getId()).count();
                list.add(createRankItem(initials, a.getNombre(), String.valueOf(totalPred), a.getPuntosTotal() + " pts"));
                count++;
            }
        }
        if (count == 0) {
            list.add(createRankItem("JD", "Juan Delgado", "0", "0 pts"));
            list.add(createRankItem("MC", "Maria C.", "0", "0 pts"));
            list.add(createRankItem("AK", "Aria K.", "0", "0 pts"));
        }

        card.add(header, BorderLayout.NORTH);
        card.add(cols, BorderLayout.CENTER);
        card.add(list, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createRankItem(String ini, String name, String pred, String acierto) {
        JPanel row = new JPanel(new BorderLayout()){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(BORDER_CARD);
                g.drawLine(20, 0, getWidth()-20, 0);
            }
        };
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(10, 20, 10, 20));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        JLabel lblAvatar = new JLabel(ini, SwingConstants.CENTER) {
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
        lblAvatar.setForeground(Color.WHITE); lblAvatar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblAvatar.setPreferredSize(new Dimension(28, 28));
        JLabel lblN = new JLabel(name); lblN.setForeground(Color.WHITE); lblN.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        left.add(lblAvatar); left.add(lblN);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 0));
        right.setOpaque(false);
        JLabel lblP = new JLabel(pred); lblP.setForeground(Color.WHITE); lblP.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JLabel lblA = new JLabel(acierto); lblA.setForeground(TEXT_CYAN); lblA.setFont(new Font("Segoe UI", Font.BOLD, 13));
        right.add(lblP); right.add(lblA);

        row.add(left, BorderLayout.WEST);
        row.add(right, BorderLayout.EAST);
        return row;
    }

    private JPanel createEfficiencyPanel() {
        JPanel card = createRoundedPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblT = new JLabel("EFICIENCIA DE SISTEMA (REAL)");
        lblT.setForeground(TEXT_CYAN);
        lblT.setFont(new Font("Consolas", Font.BOLD, 11));
        card.add(lblT, BorderLayout.NORTH);

        JPanel center = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        center.setOpaque(false);

        // Calcular porcentaje de acierto real
        List<Prediccion> preds = controller.obtenerPredicciones();
        long totalConPuntosCalculados = preds.stream().filter(p -> p.getPuntosGanados() > 0).count();
        long totalPreds = preds.size();
        int effPct = 0;
        if (totalPreds > 0) {
            effPct = (int) ((totalConPuntosCalculados * 100) / totalPreds);
        } else {
            effPct = 100; // Por defecto
        }
        
        final int finalEffPct = effPct;

        // Circular Progress Ring
        JPanel ring = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = 60, thick = 6;
                int x = (getWidth()-size)/2, y = (getHeight()-size)/2;
                
                // Track
                g2.setColor(FIELD_BORDER);
                g2.setStroke(new BasicStroke(thick, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(x, y, size, size, 0, 360);
                
                // Progress
                g2.setColor(TEXT_CYAN);
                int deg = (int) (finalEffPct * 3.6);
                g2.draw(new Arc2D.Double(x, y, size, size, 90, -deg, Arc2D.OPEN));
                
                // Text Inside
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                String pctStr = finalEffPct + "%";
                g2.drawString(pctStr, x + (size-fm.stringWidth(pctStr))/2, y + (size-fm.getHeight())/2 + fm.getAscent());
                
                g2.dispose();
            }
        };
        ring.setOpaque(false);
        ring.setPreferredSize(new Dimension(70, 70));

        JPanel txts = new JPanel();
        txts.setLayout(new BoxLayout(txts, BoxLayout.Y_AXIS));
        txts.setOpaque(false);
        JLabel lt1 = new JLabel("Predicciones Acertadas"); lt1.setForeground(Color.WHITE); lt1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel lt2 = new JLabel("Promedio real del sistema hoy"); lt2.setForeground(TEXT_MUTED); lt2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        txts.add(Box.createVerticalStrut(15));
        txts.add(lt1);
        txts.add(lt2);

        center.add(ring);
        center.add(txts);

        card.add(center, BorderLayout.CENTER);
        return card;
    }

    // --- DIALOGO PARA REGISTRAR PREDICCIÓN (Formulario Oculto en Botón) ---

    private void abrirDialogoRegistro() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nueva Predicción", true);
        dialog.setSize(350, 240);
        dialog.setLocationRelativeTo(this);
        
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG_CARD);
        p.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        List<Apostador> apostadores = controller.obtenerApostadores();
        if (apostadores.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠ No hay apostadores registrados. Registre uno primero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Apostador defaultApo = apostadores.get(0);
        
        JComboBox<Partido> cbPar = new JComboBox<>();
        for(Partido par : controller.obtenerPartidos()) cbPar.addItem(par);
        cbPar.setBackground(FIELD_BG); cbPar.setForeground(Color.WHITE);

        JTextField txtL = new JTextField(3);
        JTextField txtV = new JTextField(3);
        txtL.setBackground(FIELD_BG); txtL.setForeground(Color.WHITE); txtL.setCaretColor(Color.WHITE);
        txtV.setBackground(FIELD_BG); txtV.setForeground(Color.WHITE); txtV.setCaretColor(Color.WHITE);

        javax.swing.text.DocumentFilter onlyNumbers = new javax.swing.text.DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string != null && string.matches("[0-9]*")) super.insertString(fb, offset, string, attr);
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text != null && text.matches("[0-9]*")) super.replace(fb, offset, length, text, attrs);
            }
        };
        ((AbstractDocument) txtL.getDocument()).setDocumentFilter(onlyNumbers);
        ((AbstractDocument) txtV.getDocument()).setDocumentFilter(onlyNumbers);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel l2 = new JLabel("Partido:"); l2.setForeground(Color.WHITE);
        JLabel l3 = new JLabel("Goles Local:"); l3.setForeground(Color.WHITE);
        JLabel l4 = new JLabel("Goles Visita:"); l4.setForeground(Color.WHITE);

        gbc.gridx=0; gbc.gridy=0; p.add(l2, gbc); gbc.gridx=1; p.add(cbPar, gbc);
        gbc.gridx=0; gbc.gridy=1; p.add(l3, gbc); gbc.gridx=1; p.add(txtL, gbc);
        gbc.gridx=0; gbc.gridy=2; p.add(l4, gbc); gbc.gridx=1; p.add(txtV, gbc);

        JButton btnGuardar = createGoldButton("Guardar Predicción");
        btnGuardar.addActionListener(e -> {
            if(controller.registrarPrediccion(defaultApo, (Partido)cbPar.getSelectedItem(), txtL.getText(), txtV.getText())) {
                JOptionPane.showMessageDialog(dialog, "Predicción guardada exitosamente.");
                actualizarDatosReales(new JLabel());
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, controller.getUltimoError(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=2; gbc.insets = new Insets(15, 5, 5, 5);
        p.add(btnGuardar, gbc);

        dialog.setContentPane(p);
        dialog.setVisible(true);
    }
}
