package vistas;

import dao.EquipoDAO;
import modelos.Equipo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelEquipos extends JPanel {
    private EquipoDAO dao;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private final String[] nombresEquipos = {
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

    public PanelEquipos() {
        dao = new EquipoDAO();
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // --- Panel Superior ---
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTop.setBackground(Color.BLACK);
        JButton btnPrecargar = new JButton("Precargar 48 Equipos Automáticamente");
        btnPrecargar.setBackground(new Color(135, 206, 250));
        
        btnPrecargar.addActionListener(e -> precargarEquipos());
        panelTop.add(btnPrecargar);

        add(panelTop, BorderLayout.NORTH);

        // --- Panel Central (Tabla) ---
        String[] columnas = {"ID", "Nombre", "Grupo", "Confederación"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla NO editable
            }
        };
        tabla = new JTable(modeloTabla);
        // Header styling (dark background, white text)
        tabla.getTableHeader().setBackground(Color.BLACK);
        tabla.getTableHeader().setForeground(Color.WHITE);
        // Table styling (already set earlier)
        tabla.setBackground(Color.BLACK);
        tabla.setForeground(Color.WHITE);
        tabla.setSelectionBackground(new Color(0x1A1A1A));
        tabla.setSelectionForeground(Color.WHITE);
        JScrollPane sp = new JScrollPane(tabla);
        sp.getViewport().setBackground(Color.BLACK);
        // Remove white corner of scroll pane
        sp.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, new JPanel(){ { setBackground(Color.BLACK); } });
        add(sp, BorderLayout.CENTER);

        // Cargar datos iniciales
        cargarDatos();
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        List<Equipo> lista = dao.obtenerEquipos();
        for (Equipo eq : lista) {
            modeloTabla.addRow(new Object[]{eq.getId(), eq.getNombre(), eq.getGrupo(), eq.getFederacion()});
        }
    }

    private void precargarEquipos() {
        if (dao.contarEquipos() > 0) {
            JOptionPane.showMessageDialog(this, "Los equipos ya están cargados en la base de datos.");
            return;
        }

        int index = 0;
        char grupoActual = 'A';
        for (String nombre : nombresEquipos) {
            Equipo eq = new Equipo(0, nombre, String.valueOf(grupoActual), "Por definir");
            dao.insertarEquipo(eq);
            
            index++;
            if (index % 4 == 0) {
                grupoActual++; // Cambiar de grupo cada 4 equipos
            }
        }
        JOptionPane.showMessageDialog(this, "¡Los 48 equipos han sido cargados exitosamente!");
        cargarDatos();
    }
}
