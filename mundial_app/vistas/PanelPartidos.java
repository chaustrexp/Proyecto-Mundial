package vistas;

import controladores.PartidoController;
import modelos.Equipo;
import modelos.Partido;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelPartidos extends JPanel {
    private PartidoController controller;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JComboBox<EquipoItem> cbLocal;
    private JComboBox<EquipoItem> cbVisita;
    private JComboBox<String> cbFase;
    private List<Equipo> todosLosEquipos;

    public PanelPartidos() {
        controller = new PartidoController(); // Vista usa el Controlador
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // --- Panel Superior (Formulario) ---
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelTop.setBackground(Color.BLACK);

        cbLocal = new JComboBox<>();
        cbVisita = new JComboBox<>();
        cbFase = new JComboBox<>(new String[]{"Grupo", "16avos", "Octavos", "Cuartos", "Semis", "Final"});

        // Agregamos ActionListeners para filtrar dinámicamente
        cbLocal.addActionListener(e -> actualizarComboVisita());
        cbFase.addActionListener(e -> actualizarComboVisita());

        JLabel lblLocal = new JLabel("Local:");
        lblLocal.setForeground(Color.WHITE);
        panelTop.add(lblLocal);
        panelTop.add(cbLocal);
        JLabel lblVisita = new JLabel("Visita:");
        lblVisita.setForeground(Color.WHITE);
        panelTop.add(lblVisita);
        panelTop.add(cbVisita);
        JLabel lblFase = new JLabel("Fase:");
        lblFase.setForeground(Color.WHITE);
        panelTop.add(lblFase);
        panelTop.add(cbFase);

        JButton btnCrear = new JButton("Crear Partido");
        btnCrear.addActionListener(e -> {
            EquipoItem itemLocal = (EquipoItem) cbLocal.getSelectedItem();
            EquipoItem itemVisita = (EquipoItem) cbVisita.getSelectedItem();

            Equipo local = (itemLocal != null) ? itemLocal.equipo : null;
            Equipo visita = (itemVisita != null) ? itemVisita.equipo : null;
            String fase = cbFase.getSelectedItem().toString();

            List<Partido> actuales = controller.obtenerPartidos();

            if (controller.crearPartido(local, visita, fase, actuales)) {
                String nombLocal = local.getNombre();
                String nombVisita = visita.getNombre();
                JOptionPane.showMessageDialog(this,
                        "✔ Partido \"" + nombLocal + " vs " + nombVisita + "\" creado exitosamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, controller.getUltimoError(),
                        "Validación", JOptionPane.WARNING_MESSAGE);
            }
        });
        panelTop.add(btnCrear);

        JButton btnRecargarEquipos = new JButton("↻ Actualizar Lista Equipos");
        btnRecargarEquipos.addActionListener(e -> cargarComboEquipos());
        panelTop.add(btnRecargarEquipos);

        add(panelTop, BorderLayout.NORTH);

        // --- Panel Central (Tabla) ---
        String[] columnas = {"ID", "Local", "Visita", "Fase", "Goles Local", "Goles Visita"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
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

        JButton btnActualizarTodo = new JButton("↻ Actualizar Tabla y Equipos");
        btnActualizarTodo.addActionListener(e -> {
            cargarComboEquipos();
            cargarDatos();
        });
        add(btnActualizarTodo, BorderLayout.SOUTH);

        cargarComboEquipos();
        cargarDatos();
    }

    private void cargarComboEquipos() {
        cbLocal.removeAllItems();
        todosLosEquipos = controller.obtenerEquipos();
        for (Equipo eq : todosLosEquipos) {
            cbLocal.addItem(new EquipoItem(eq));
        }
        // Llamar a la actualización para el cbVisita
        actualizarComboVisita();
    }

    private void actualizarComboVisita() {
        if (todosLosEquipos == null) return;
        
        EquipoItem localSeleccionado = (EquipoItem) cbLocal.getSelectedItem();
        String faseSeleccionada = (String) cbFase.getSelectedItem();
        
        EquipoItem visitaActual = (EquipoItem) cbVisita.getSelectedItem();
        
        cbVisita.removeAllItems();
        
        for (Equipo eq : todosLosEquipos) {
            boolean agregar = true;
            // Si es fase de grupos, filtramos por el grupo del equipo local
            if ("Grupo".equals(faseSeleccionada) && localSeleccionado != null) {
                if (eq.getGrupo() == null || !eq.getGrupo().equals(localSeleccionado.equipo.getGrupo())) {
                    agregar = false;
                }
            }
            if (agregar) {
                cbVisita.addItem(new EquipoItem(eq));
            }
        }
        
        // Restaurar selección previa si aún existe
        if (visitaActual != null) {
            for (int i = 0; i < cbVisita.getItemCount(); i++) {
                if (cbVisita.getItemAt(i).equipo.getId() == visitaActual.equipo.getId()) {
                    cbVisita.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        List<Partido> partidos = controller.obtenerPartidos();
        for (Partido p : partidos) {
            modeloTabla.addRow(new Object[]{
                p.getId(),
                p.getEquipoLocal().getNombre(),
                p.getEquipoVisita().getNombre(),
                p.getFase(),
                p.getGolesLocal(),
                p.getGolesVisita()
            });
        }
    }

    class EquipoItem {
        Equipo equipo;
        public EquipoItem(Equipo equipo) { this.equipo = equipo; }
        @Override
        public String toString() { return equipo.getNombre() + " (Gr " + equipo.getGrupo() + ")"; }
    }
}
