package dao;

import modelos.Apostador;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApostadorDAO {

    public boolean insertarApostador(Apostador a) {
        String sql = "INSERT INTO apostadores (nombre, puntos_total) VALUES (?, ?)";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getNombre());
            ps.setInt(2, a.getPuntosTotal());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Apostador> obtenerApostadores() {
        List<Apostador> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, puntos_total FROM apostadores ORDER BY puntos_total DESC";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Apostador(rs.getInt("id"), rs.getString("nombre"), rs.getInt("puntos_total")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
