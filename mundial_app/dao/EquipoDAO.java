package dao;

import modelos.Equipo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipoDAO {

    public boolean insertarEquipo(Equipo eq) {
        String sql = "INSERT INTO equipos (nombre, confederacion, grupo) VALUES (?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eq.getNombre());
            ps.setString(2, eq.getFederacion());
            ps.setString(3, eq.getGrupo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Equipo> obtenerEquipos() {
        List<Equipo> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, grupo, confederacion FROM equipos ORDER BY grupo, nombre";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Equipo(rs.getInt("id"), rs.getString("nombre"), rs.getString("grupo"), rs.getString("confederacion")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public int contarEquipos() {
        String sql = "SELECT COUNT(*) FROM equipos";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean actualizarEquipo(Equipo eq) {
        String sql = "UPDATE equipos SET nombre = ?, confederacion = ?, grupo = ? WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eq.getNombre());
            ps.setString(2, eq.getFederacion());
            ps.setString(3, eq.getGrupo());
            ps.setInt(4, eq.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
