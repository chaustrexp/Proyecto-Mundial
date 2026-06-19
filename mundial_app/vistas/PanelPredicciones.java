package vistas;

import controladores.PrediccionController;
import modelos.Apostador;
import modelos.Partido;
import modelos.Prediccion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.util.List;

public class PanelPredicciones extends JPanel {
    private PrediccionController controller;
    private JComboBox<ApostadorItem> cbApostador;
    private JComboBox<PartidoItem> cbPartido;
    private JTextField txtGolesLocal;
    private JTextField txtGolesVisita;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public PanelPredicciones() {
        controller = new PrediccionController(); // Vista usa el Controlador
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Color.BLACK);
        panelForm.setBorder(BorderFactory.createTitledBorder("Registrar Nueva Predicción"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbApostador = new JComboBox<>();
        cbPartido = new JComboBox<>();
        txtGolesLocal = new JTextField(5);
        txtGolesVisita = new JTextField(5);

        // Filtro: solo permite números en los campos de goles
        DocumentFilter soloNumeros = new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
                if (text != null && text.matches("[0-9]*")) super.insertString(fb, offset, text, attr);
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text != null && text.matches("[0-9]*")) super.replace(fb, offset, length, text, attrs);
            }
        };
        ((AbstractDocument) txtGolesLocal.getDocument()).setDocumentFilter(soloNumeros);
        ((AbstractDocument) txtGolesVisita.getDocument()).setDocumentFilter(soloNumeros);

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblApostador = new JLabel("Apostador:");
        lblApostador.setForeground(Color.WHITE);
        panelForm.add(lblApostador, gbc);
        gbc.gridx = 1;
        panelForm.add(cbApostador, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblPartido = new JLabel("Partido:");
        lblPartido.setForeground(Color.WHITE);
        panelForm.add(lblPartido, gbc);
        gbc.gridx = 1;
        panelForm.add(cbPartido, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblGolesLocal = new JLabel("Goles Local:");
        lblGolesLocal.setForeground(Color.WHITE);
        panelForm.add(lblGolesLocal, gbc);
        gbc.gridx = 1;
        panelForm.add(txtGolesLocal, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblGolesVisita = new JLabel("Goles Visita:");
        lblGolesVisita.setForeground(Color.WHITE);
        panelForm.add(lblGolesVisita, gbc);
        gbc.gridx = 1;
        panelForm.add(txtGolesVisita, gbc);

        JButton btnGuardar = new JButton("Guardar Predicción");
        btnGuardar.addActionListener(e -> guardarPrediccion());
        gbc.gridx = 1; gbc.gridy = 4;
        panelForm.add(btnGuardar, gbc);

        add(panelForm, BorderLayout.NORTH);

        // --- Tabla de Predicciones ---
        String[] columnas = {"ID", "Apostador", "Partido", "Predicción", "Pts Ganados"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla NO editable
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.getTableHeader().setBackground(Color.BLACK);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setBackground(Color.BLACK);
        tabla.setForeground(Color.WHITE);
        tabla.setSelectionBackground(new Color(0x1A1A1A));
        tabla.setSelectionForeground(Color.WHITE);
        JScrollPane sp = new JScrollPane(tabla);
        sp.getViewport().setBackground(Color.BLACK);
        sp.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER,
                new JPanel() { { setBackground(Color.BLACK); } });
        add(sp, BorderLayout.CENTER);

        JButton btnActualizar = new JButton("↻ Actualizar Listas y Tabla");
        btnActualizar.addActionListener(e -> { cargarListas(); cargarTabla(); });
        add(btnActualizar, BorderLayout.SOUTH);

        cargarListas();
        cargarTabla();
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        // La Vista pide los datos al Controlador
        List<Prediccion> preds = controller.obtenerPredicciones();
        for (Prediccion p : preds) {
            String partidoTxt = p.getPartido().getEquipoLocal().getNombre() + " vs " + p.getPartido().getEquipoVisita().getNombre();
            String marcadorTxt = p.getGolesPredEq1() + " - " + p.getGolesPredEq2();
            modeloTabla.addRow(new Object[]{p.getId(), p.getApostador().getNombre(), partidoTxt, marcadorTxt, p.getPuntosGanados()});
        }
    }

    private void cargarListas() {
        cbApostador.removeAllItems();
        cbPartido.removeAllItems();
        // La Vista pide los datos al Controlador
        for (Apostador a : controller.obtenerApostadores()) {
            cbApostador.addItem(new ApostadorItem(a));
        }
        for (Partido p : controller.obtenerPartidos()) {
            cbPartido.addItem(new PartidoItem(p));
        }
    }

    private void guardarPrediccion() {
        ApostadorItem ai = (ApostadorItem) cbApostador.getSelectedItem();
        PartidoItem pi = (PartidoItem) cbPartido.getSelectedItem();

        Apostador apostador = (ai != null) ? ai.apostador : null;
        Partido partido = (pi != null) ? pi.partido : null;

        // El Controlador hace la validación y la lógica de negocio
        if (controller.registrarPrediccion(apostador, partido,
                txtGolesLocal.getText(), txtGolesVisita.getText())) {

            JOptionPane.showMessageDialog(this,
                    "✔ Predicción guardada:\n" + apostador.getNombre() +
                    " apuesta " + txtGolesLocal.getText() + " - " + txtGolesVisita.getText() +
                    " en " + partido.toString(),
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            txtGolesLocal.setText("");
            txtGolesVisita.setText("");
            cargarTabla();
        } else {
            // La Vista solo muestra el error que devuelve el Controlador
            String error = controller.getUltimoError();
            if (error != null && error.startsWith("✖")) {
                JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, error, "Campo inválido", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    class ApostadorItem {
        Apostador apostador;
        public ApostadorItem(Apostador a) { this.apostador = a; }
        @Override
        public String toString() { return apostador.getNombre(); }
    }

    class PartidoItem {
        Partido partido;
        public PartidoItem(Partido p) { this.partido = p; }
        @Override
        public String toString() { return partido.toString(); }
    }
}
