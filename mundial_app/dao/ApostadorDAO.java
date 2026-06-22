package dao;

import modelos.Apostador;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApostadorDAO {

    public boolean insertarApostador(Apostador a) {
        String sql = "INSERT INTO apostadores (nombre, puntos_total, usuario_id) VALUES (?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getNombre());
            ps.setInt(2, a.getPuntosTotal());
            if (a.getUsuarioId() != null) {
                ps.setInt(3, a.getUsuarioId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Apostador> obtenerApostadores() {
        List<Apostador> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, puntos_total, usuario_id FROM apostadores ORDER BY puntos_total DESC";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Integer uid = rs.getInt("usuario_id");
                if (rs.wasNull()) uid = null;
                lista.add(new Apostador(rs.getInt("id"), rs.getString("nombre"), rs.getInt("puntos_total"), uid));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Apostador obtenerApostadorPorUsuarioId(int usuarioId) {
        String sql = "SELECT id, nombre, puntos_total, usuario_id FROM apostadores WHERE usuario_id = ?";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Apostador(rs.getInt("id"), rs.getString("nombre"), rs.getInt("puntos_total"), rs.getInt("usuario_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
