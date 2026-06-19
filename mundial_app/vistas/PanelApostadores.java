package vistas;

import controladores.ApostadorController;
import modelos.Apostador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.util.List;

public class PanelApostadores extends JPanel {
    private ApostadorController controller;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public PanelApostadores() {
        controller = new ApostadorController(); // Vista usa el Controlador
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // --- Panel Superior (Formulario) ---
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTop.setBackground(Color.BLACK);
        JLabel lblNombre = new JLabel("Nombre del Apostador:");
        lblNombre.setForeground(Color.WHITE);
        panelTop.add(lblNombre);
        JTextField txtNombre = new JTextField(20);
        panelTop.add(txtNombre);

        // Filtro: solo permite letras y espacios en el campo nombre
        ((AbstractDocument) txtNombre.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
                if (text != null && text.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*")) {
                    super.insertString(fb, offset, text, attr);
                }
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text != null && text.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        JButton btnGuardar = new JButton("Registrar Apostador");
        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();

            // El Controlador hace la validación y la lógica de negocio
            if (controller.registrarApostador(nombre)) {
                JOptionPane.showMessageDialog(this,
                        "✔ Apostador \"" + nombre + "\" registrado correctamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                txtNombre.setText("");
                cargarDatos();
            } else {
                // La Vista solo muestra el error que le devuelve el Controlador
                String error = controller.getUltimoError();
                if (error != null && error.startsWith("✖")) {
                    JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, error, "Campo inválido", JOptionPane.WARNING_MESSAGE);
                }
                txtNombre.requestFocus();
            }
        });
        panelTop.add(btnGuardar);
        add(panelTop, BorderLayout.NORTH);

        // --- Panel Central (Tabla) ---
        String[] columnas = {"ID", "Nombre", "Puntos Totales"};
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

        JButton btnActualizar = new JButton("↻ Actualizar Tabla");
        btnActualizar.addActionListener(e -> cargarDatos());
        add(btnActualizar, BorderLayout.SOUTH);

        cargarDatos();
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        // La Vista pide los datos al Controlador, no al DAO directamente
        List<Apostador> lista = controller.obtenerApostadores();
        for (Apostador a : lista) {
            modeloTabla.addRow(new Object[]{a.getId(), a.getNombre(), a.getPuntosTotal()});
        }
    }
}
