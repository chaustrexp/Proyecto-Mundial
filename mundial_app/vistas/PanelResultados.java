package vistas;

import controladores.ResultadoController;
import modelos.Partido;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.util.List;

public class PanelResultados extends JPanel {
    private ResultadoController controller;
    private JComboBox<PartidoItem> cbPartido;
    private JTextField txtGolesLocal;
    private JTextField txtGolesVisita;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public PanelResultados() {
        controller = new ResultadoController(); // Vista usa el Controlador
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Color.BLACK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

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
        JLabel lblPartido = new JLabel("Seleccione Partido:");
        lblPartido.setForeground(Color.WHITE);
        panelForm.add(lblPartido, gbc);
        gbc.gridx = 1;
        panelForm.add(cbPartido, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblLocal = new JLabel("Resultado Local:");
        lblLocal.setForeground(Color.WHITE);
        panelForm.add(lblLocal, gbc);
        gbc.gridx = 1;
        panelForm.add(txtGolesLocal, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblVisita = new JLabel("Resultado Visita:");
        lblVisita.setForeground(Color.WHITE);
        panelForm.add(lblVisita, gbc);
        gbc.gridx = 1;
        panelForm.add(txtGolesVisita, gbc);

        JButton btnGuardar = new JButton("Guardar Resultado y Calcular Puntos");
        btnGuardar.setBackground(new Color(0, 0, 0));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.addActionListener(e -> registrarResultado());
        gbc.gridx = 1; gbc.gridy = 3;
        panelForm.add(btnGuardar, gbc);

        add(panelForm, BorderLayout.NORTH);

        // --- Tabla de Resultados ---
        String[] columnas = {"ID", "Local", "Visita", "Goles Local", "Goles Visita", "Fase"};
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

        JButton btnActualizar = new JButton("↻ Actualizar Partidos y Tabla");
        btnActualizar.addActionListener(e -> { cargarPartidos(); cargarTabla(); });
        add(btnActualizar, BorderLayout.SOUTH);

        cargarPartidos();
        cargarTabla();
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        // La Vista pide los datos al Controlador
        List<Partido> partidos = controller.obtenerPartidos();
        for (Partido p : partidos) {
            if (p.getGolesLocal() != null && (p.getGolesLocal() != 0 || p.getGolesVisita() != 0)) {
                modeloTabla.addRow(new Object[]{p.getId(), p.getEquipoLocal().getNombre(),
                        p.getEquipoVisita().getNombre(), p.getGolesLocal(), p.getGolesVisita(), p.getFase()});
            }
        }
    }

    private void cargarPartidos() {
        cbPartido.removeAllItems();
        // La Vista pide los datos al Controlador
        List<Partido> partidos = controller.obtenerPartidos();
        for (Partido p : partidos) {
            cbPartido.addItem(new PartidoItem(p));
        }
    }

    private void registrarResultado() {
        PartidoItem pi = (PartidoItem) cbPartido.getSelectedItem();
        Partido partido = (pi != null) ? pi.partido : null;

        // Confirmación antes de guardar (acción irreversible)
        if (partido != null && !txtGolesLocal.getText().trim().isEmpty() && !txtGolesVisita.getText().trim().isEmpty()) {
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Confirmar resultado?\n" + partido.toString() +
                    "\nMarcador: " + txtGolesLocal.getText() + " - " + txtGolesVisita.getText(),
                    "Confirmar resultado", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirmacion != JOptionPane.YES_OPTION) return;
        }

        // El Controlador hace la validación y la lógica de negocio
        if (controller.registrarResultado(partido, txtGolesLocal.getText(), txtGolesVisita.getText())) {
            JOptionPane.showMessageDialog(this,
                    "✔ Resultado guardado y puntos calculados.\nRevisa la pestaña 'Apostadores' para ver la tabla actualizada.",
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

    class PartidoItem {
        Partido partido;
        public PartidoItem(Partido p) { this.partido = p; }
        @Override
        public String toString() { return partido.toString(); }
    }
}
