package vistas;

import controladores.ResultadoController;
import modelos.Partido;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class PanelResultados extends JPanel {

    // Paleta de colores extraida del mockup "Gestión de Resultados"
    private static final Color BG_APP          = new Color(11, 19, 32);     // #0B1320
    private static final Color BG_CARD         = new Color(22, 32, 50);     // #162032
    private static final Color BORDER_CARD     = new Color(44, 58, 83);     // #2C3A53
    private static final Color TEXT_GOLD       = new Color(226, 185, 74);   // #E2B94A
    private static final Color TEXT_LIGHT      = new Color(194, 203, 224);  // #C2CBE0
    private static final Color TEXT_MUTED      = new Color(139, 149, 165);  // #8B95A5
    private static final Color TEXT_CYAN       = new Color(0, 212, 255);    // #00D4FF
    private static final Color FIELD_BG        = new Color(14, 21, 35);     // #0E1523
    private static final Color FIELD_BORDER    = new Color(27, 38, 59);     // #1B263B

    private ResultadoController controller;
    private JPanel listaPanel; // Contenedor de las tarjetas de partidos
    private JComboBox<String> cbGrupoFilter;
    private JTextField txtFechaFilter;
    private JTextField txtBuscarResult;

    public PanelResultados() {
        controller = new ResultadoController();
        setLayout(new BorderLayout());
        setBackground(BG_APP);

        // Contenedor Principal (Scrollable)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_APP);
        contentPanel.setBorder(new EmptyBorder(25, 40, 25, 40)); // Margenes laterales grandes como en la app

        // 1. CABECERA
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("Gestión de Resultados");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDesc = new JLabel("<html><center>Panel centralizado para el registro y validación de marcadores oficiales del Mundial 2026. Los cambios impactan en tiempo real a la tabla de posiciones.</center></html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(TEXT_MUTED);
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDesc.setMaximumSize(new Dimension(800, 40));

        headerPanel.add(lblTitle);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(lblDesc);
        headerPanel.add(Box.createVerticalStrut(25));

        JButton btnRegistrar = createGoldButton("⊕ Registrar Nuevo Resultado");
        btnRegistrar.addActionListener(e -> abrirDialogoRegistro());
        btnRegistrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(btnRegistrar);

        contentPanel.add(headerPanel);
        contentPanel.add(Box.createVerticalStrut(30));

        // 2. PANEL DE FILTROS (con controles funcionales reales)
        JPanel filtersCard = createRoundedPanel();
        filtersCard.setLayout(new BoxLayout(filtersCard, BoxLayout.Y_AXIS));
        filtersCard.setBorder(new EmptyBorder(20, 25, 20, 25));
        filtersCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        filtersCard.setMaximumSize(new Dimension(800, 280));

        // Filtro por Grupo
        JPanel pGrupo = new JPanel();
        pGrupo.setLayout(new BoxLayout(pGrupo, BoxLayout.Y_AXIS));
        pGrupo.setOpaque(false);
        pGrupo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblG = new JLabel("FILTRAR POR GRUPO");
        lblG.setFont(new Font("Consolas", Font.BOLD, 10));
        lblG.setForeground(TEXT_LIGHT);
        lblG.setAlignmentX(Component.CENTER_ALIGNMENT);
        cbGrupoFilter = new JComboBox<>(new String[]{"Todos los Grupos", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"});
        cbGrupoFilter.setBackground(FIELD_BG);
        cbGrupoFilter.setForeground(Color.WHITE);
        cbGrupoFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbGrupoFilter.setMaximumSize(new Dimension(250, 35));
        cbGrupoFilter.setAlignmentX(Component.CENTER_ALIGNMENT);
        cbGrupoFilter.addActionListener(e -> cargarPartidosFinalizados());
        pGrupo.add(lblG); pGrupo.add(Box.createVerticalStrut(5)); pGrupo.add(cbGrupoFilter);

        // Filtro por Fecha
        JPanel pFecha = new JPanel();
        pFecha.setLayout(new BoxLayout(pFecha, BoxLayout.Y_AXIS));
        pFecha.setOpaque(false);
        pFecha.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblF = new JLabel("FECHA DEL ENCUENTRO");
        lblF.setFont(new Font("Consolas", Font.BOLD, 10));
        lblF.setForeground(TEXT_LIGHT);
        lblF.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtFechaFilter = new JTextField("mm/dd/yyyy");
        txtFechaFilter.setBackground(FIELD_BG);
        txtFechaFilter.setForeground(TEXT_MUTED);
        txtFechaFilter.setCaretColor(Color.WHITE);
        txtFechaFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtFechaFilter.setMaximumSize(new Dimension(250, 35));
        txtFechaFilter.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtFechaFilter.setBorder(new EmptyBorder(8, 15, 8, 15));
        txtFechaFilter.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (txtFechaFilter.getText().equals("mm/dd/yyyy")) {
                    txtFechaFilter.setText("");
                    txtFechaFilter.setForeground(Color.WHITE);
                }
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (txtFechaFilter.getText().trim().isEmpty()) {
                    txtFechaFilter.setText("mm/dd/yyyy");
                    txtFechaFilter.setForeground(TEXT_MUTED);
                }
            }
        });
        txtFechaFilter.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { cargarPartidosFinalizados(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { cargarPartidosFinalizados(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { cargarPartidosFinalizados(); }
        });
        pFecha.add(lblF); pFecha.add(Box.createVerticalStrut(5)); pFecha.add(txtFechaFilter);

        // Búsqueda rápida
        JPanel pBusqueda = new JPanel();
        pBusqueda.setLayout(new BoxLayout(pBusqueda, BoxLayout.Y_AXIS));
        pBusqueda.setOpaque(false);
        pBusqueda.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblB = new JLabel("BÚSQUEDA RÁPIDA");
        lblB.setFont(new Font("Consolas", Font.BOLD, 10));
        lblB.setForeground(TEXT_LIGHT);
        lblB.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtBuscarResult = new JTextField("Buscar país...");
        txtBuscarResult.setBackground(FIELD_BG);
        txtBuscarResult.setForeground(TEXT_MUTED);
        txtBuscarResult.setCaretColor(Color.WHITE);
        txtBuscarResult.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscarResult.setMaximumSize(new Dimension(250, 35));
        txtBuscarResult.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtBuscarResult.setBorder(new EmptyBorder(8, 15, 8, 15));
        txtBuscarResult.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (txtBuscarResult.getText().equals("Buscar país...")) {
                    txtBuscarResult.setText("");
                    txtBuscarResult.setForeground(Color.WHITE);
                }
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (txtBuscarResult.getText().trim().isEmpty()) {
                    txtBuscarResult.setText("Buscar país...");
                    txtBuscarResult.setForeground(TEXT_MUTED);
                }
            }
        });
        txtBuscarResult.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { cargarPartidosFinalizados(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { cargarPartidosFinalizados(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { cargarPartidosFinalizados(); }
        });
        pBusqueda.add(lblB); pBusqueda.add(Box.createVerticalStrut(5)); pBusqueda.add(txtBuscarResult);

        filtersCard.add(pGrupo);
        filtersCard.add(Box.createVerticalStrut(12));
        filtersCard.add(pFecha);
        filtersCard.add(Box.createVerticalStrut(12));
        filtersCard.add(pBusqueda);

        contentPanel.add(filtersCard);
        contentPanel.add(Box.createVerticalStrut(30));

        // 3. LISTA DE PARTIDOS (Dinámico)
        listaPanel = new JPanel();
        listaPanel.setLayout(new BoxLayout(listaPanel, BoxLayout.Y_AXIS));
        listaPanel.setOpaque(false);
        listaPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contentPanel.add(listaPanel);

        // 4. BOTÓN "AGREGAR NUEVO" (Punteado)
        JPanel addCard = createDottedPanel();
        addCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        addCard.setMaximumSize(new Dimension(800, 160));
        
        // Hacer clickeable todo el panel punteado
        addCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                abrirDialogoRegistro();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                addCard.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });
        
        contentPanel.add(addCard);
        contentPanel.add(Box.createVerticalStrut(40));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        add(scrollPane, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(BG_APP);
        JLabel footTxt = new JLabel("© 2026 World Cup Trophy Elite Digital. All Rights Reserved.");
        footTxt.setForeground(TEXT_MUTED);
        footTxt.setFont(new Font("Consolas", Font.PLAIN, 10));
        footer.add(footTxt);
        add(footer, BorderLayout.SOUTH);

        cargarPartidosFinalizados();
    }

    private void cargarPartidosFinalizados() {
        listaPanel.removeAll();
        List<Partido> partidos = controller.obtenerPartidos();

        String grupoSel = (cbGrupoFilter != null) ? (String) cbGrupoFilter.getSelectedItem() : "Todos los Grupos";
        String fechaQuery = (txtFechaFilter != null && !txtFechaFilter.getText().equals("mm/dd/yyyy")) 
                            ? txtFechaFilter.getText().toLowerCase().trim() : "";
        String searchCountry = (txtBuscarResult != null && !txtBuscarResult.getText().equals("Buscar país...")) 
                               ? txtBuscarResult.getText().toLowerCase().trim() : "";

        for (Partido p : partidos) {
            if (p.getGolesLocal() != null) {
                // Filtro por Grupo (Ambos pertenecen al mismo grupo)
                if (!"Todos los Grupos".equals(grupoSel)) {
                    String gLocal = p.getEquipoLocal().getGrupo();
                    String gVisita = p.getEquipoVisita().getGrupo();
                    if (!grupoSel.equalsIgnoreCase(gLocal) && !grupoSel.equalsIgnoreCase(gVisita)) {
                        continue;
                    }
                }
                
                // Filtro por Fecha
                if (!fechaQuery.isEmpty()) {
                    String fechaStr = p.getFecha() != null ? p.getFecha().toString().toLowerCase() : "";
                    if (!fechaStr.contains(fechaQuery)) {
                        continue;
                    }
                }
                
                // Búsqueda Rápida de País
                if (!searchCountry.isEmpty()) {
                    String nameLocal = p.getEquipoLocal().getNombre().toLowerCase();
                    String nameVisita = p.getEquipoVisita().getNombre().toLowerCase();
                    if (!nameLocal.contains(searchCountry) && !nameVisita.contains(searchCountry)) {
                        continue;
                    }
                }

                JPanel matchCard = createMatchCard(p);
                matchCard.setAlignmentX(Component.CENTER_ALIGNMENT);
                listaPanel.add(matchCard);
                listaPanel.add(Box.createVerticalStrut(20));
            }
        }
        
        listaPanel.revalidate();
        listaPanel.repaint();
    }

    // --- COMPONENTES VISUALES ---

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

    private JPanel createFilterField(String label, String value) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Consolas", Font.BOLD, 10));
        lbl.setForeground(TEXT_LIGHT);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel field = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FIELD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                g2.setColor(FIELD_BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 6, 6));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setOpaque(false);
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setMaximumSize(new Dimension(250, 35));

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        val.setForeground(Color.WHITE);
        field.add(val);

        p.add(lbl);
        p.add(Box.createVerticalStrut(5));
        p.add(field);

        return p;
    }

    private JPanel createMatchCard(Partido partido) {
        JPanel card = createRoundedPanel();
        card.setLayout(new BorderLayout());
        card.setMaximumSize(new Dimension(800, 180));

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel lblFase = new JLabel(partido.getFase().toUpperCase());
        lblFase.setFont(new Font("Consolas", Font.PLAIN, 11));
        lblFase.setForeground(TEXT_LIGHT);
        
        JLabel lblStatus = new JLabel("● FINALIZADO");
        lblStatus.setFont(new Font("Consolas", Font.BOLD, 11));
        lblStatus.setForeground(TEXT_CYAN);

        header.add(lblFase, BorderLayout.WEST);
        header.add(lblStatus, BorderLayout.EAST);

        // BODY (Marcador)
        JPanel body = new JPanel(new GridLayout(1, 3));
        body.setOpaque(false);
        
        // Equipo 1
        JPanel pEq1 = new JPanel(new BorderLayout());
        pEq1.setOpaque(false);
        JLabel lblEq1 = new JLabel(partido.getEquipoLocal().getNombre(), SwingConstants.CENTER);
        lblEq1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEq1.setForeground(TEXT_LIGHT);
        JLabel flag1 = new JLabel("🏳", SwingConstants.CENTER); // Bandera generica
        flag1.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        flag1.setForeground(Color.WHITE);
        pEq1.add(flag1, BorderLayout.CENTER);
        pEq1.add(lblEq1, BorderLayout.SOUTH);

        // Resultado Central
        JPanel pRes = new JPanel(new BorderLayout());
        pRes.setOpaque(false);
        JLabel lblScore = new JLabel(partido.getGolesLocal() + " - " + partido.getGolesVisita(), SwingConstants.CENTER);
        lblScore.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        lblScore.setForeground(TEXT_GOLD);
        
        // Estadio Simulado
        String estadio = "ESTADIO SIMULADO";
        if(partido.getEquipoLocal().getNombre().toUpperCase().contains("MEX")) estadio = "ESTADIO AZTECA";
        else if(partido.getEquipoLocal().getNombre().toUpperCase().contains("USA")) estadio = "METLIFE STADIUM";
        else if(partido.getEquipoLocal().getNombre().toUpperCase().contains("ARG")) estadio = "SOFI STADIUM";
        
        JLabel lblEstadio = new JLabel(estadio, SwingConstants.CENTER);
        lblEstadio.setFont(new Font("Consolas", Font.PLAIN, 10));
        lblEstadio.setForeground(TEXT_MUTED);
        
        pRes.add(lblScore, BorderLayout.CENTER);
        pRes.add(lblEstadio, BorderLayout.SOUTH);

        // Equipo 2
        JPanel pEq2 = new JPanel(new BorderLayout());
        pEq2.setOpaque(false);
        JLabel lblEq2 = new JLabel(partido.getEquipoVisita().getNombre(), SwingConstants.CENTER);
        lblEq2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEq2.setForeground(TEXT_LIGHT);
        JLabel flag2 = new JLabel("🏳", SwingConstants.CENTER);
        flag2.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        flag2.setForeground(Color.WHITE);
        pEq2.add(flag2, BorderLayout.CENTER);
        pEq2.add(lblEq2, BorderLayout.SOUTH);

        body.add(pEq1);
        body.add(pRes);
        body.add(pEq2);

        // FOOTER
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(BORDER_CARD);
                g.drawLine(20, 0, getWidth() - 20, 0); // Linea superior
            }
        };
        footer.setOpaque(false);
        JLabel lblEdit = new JLabel("📝 Editar Resultado");
        lblEdit.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEdit.setForeground(TEXT_LIGHT);
        lblEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblEdit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                abrirDialogoRegistro(partido);
            }
        });
        footer.add(lblEdit);

        card.add(header, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createDottedPanel() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fondo
                g2.setColor(new Color(11, 19, 32)); // Mismo fondo de la app
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                
                // Borde Punteado
                float[] dash = { 5.0f, 5.0f };
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, dash, 0.0f));
                g2.setColor(BORDER_CARD);
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 16, 16));
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JPanel iconCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setMaximumSize(new Dimension(50, 50));
        iconCircle.setLayout(new BorderLayout());
        JLabel plus = new JLabel("+", SwingConstants.CENTER);
        plus.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        plus.setForeground(TEXT_GOLD);
        iconCircle.add(plus, BorderLayout.CENTER);

        JLabel t1 = new JLabel("Agregar nuevo encuentro finalizado", SwingConstants.CENTER);
        t1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t1.setForeground(Color.WHITE);
        t1.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel t2 = new JLabel("Registrar marcadores de la jornada actual", SwingConstants.CENTER);
        t2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        t2.setForeground(TEXT_MUTED);
        t2.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(Box.createVerticalStrut(30));
        p.add(iconCircle);
        p.add(Box.createVerticalStrut(15));
        p.add(t1);
        p.add(Box.createVerticalStrut(5));
        p.add(t2);
        p.add(Box.createVerticalStrut(30));

        return p;
    }

    // --- LOGICA DE DIALOGO DE REGISTRO ---
    
    private void abrirDialogoRegistro() {
        abrirDialogoRegistro(null);
    }

    private void abrirDialogoRegistro(Partido preselect) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Registrar Resultado", true);
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG_CARD);
        p.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JComboBox<Partido> cbPar = new JComboBox<>();
        for(Partido par : controller.obtenerPartidos()) {
            cbPar.addItem(par);
        }
        if(preselect != null) cbPar.setSelectedItem(preselect);
        
        cbPar.setBackground(FIELD_BG); cbPar.setForeground(Color.WHITE);

        JTextField txtL = new JTextField(5);
        JTextField txtV = new JTextField(5);
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

        if(preselect != null && preselect.getGolesLocal() != null) {
            txtL.setText(String.valueOf(preselect.getGolesLocal()));
            txtV.setText(String.valueOf(preselect.getGolesVisita()));
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel l1 = new JLabel("Partido:"); l1.setForeground(Color.WHITE);
        JLabel l2 = new JLabel("Goles Local:"); l2.setForeground(Color.WHITE);
        JLabel l3 = new JLabel("Goles Visita:"); l3.setForeground(Color.WHITE);

        gbc.gridx=0; gbc.gridy=0; p.add(l1, gbc); gbc.gridx=1; p.add(cbPar, gbc);
        gbc.gridx=0; gbc.gridy=1; p.add(l2, gbc); gbc.gridx=1; p.add(txtL, gbc);
        gbc.gridx=0; gbc.gridy=2; p.add(l3, gbc); gbc.gridx=1; p.add(txtV, gbc);

        JButton btnGuardar = createGoldButton("Guardar Resultado");
        btnGuardar.addActionListener(e -> {
            Partido seleccionado = (Partido) cbPar.getSelectedItem();
            if (seleccionado != null && !txtL.getText().trim().isEmpty() && !txtV.getText().trim().isEmpty()) {
                int confirmacion = JOptionPane.showConfirmDialog(dialog,
                        "¿Confirmar resultado?\nMarcador: " + txtL.getText() + " - " + txtV.getText(),
                        "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (confirmacion != JOptionPane.YES_OPTION) return;

                if(controller.registrarResultado(seleccionado, txtL.getText(), txtV.getText())) {
                    JOptionPane.showMessageDialog(dialog, "Resultado guardado. Puntos actualizados.");
                    cargarPartidosFinalizados(); // Recargar visual
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, controller.getUltimoError(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Complete todos los campos.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=2; gbc.insets = new Insets(15, 5, 5, 5);
        p.add(btnGuardar, gbc);

        dialog.setContentPane(p);
        dialog.setVisible(true);
    }
}
