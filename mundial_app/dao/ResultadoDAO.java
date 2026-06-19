package dao;

import modelos.Resultado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultadoDAO {

    public boolean insertarResultado(Resultado r) {
        String sql = "INSERT INTO resultados (partido_id, goles_local, goles_visita) VALUES (?,?,?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, r.getPartidoId());
            ps.setInt(2, r.getGolesLocal());
            ps.setInt(3, r.getGolesVisita());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Resultado> obtenerHistorialPorPartido(int partidoId) {
        List<Resultado> lista = new ArrayList<>();
        String sql = "SELECT * FROM resultados WHERE partido_id = ? ORDER BY id DESC";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, partidoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Resultado(
                        rs.getInt("id"),
                        rs.getInt("partido_id"),
                        rs.getInt("goles_local"),
                        rs.getInt("goles_visita")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Resultado> obtenerTodos() {
        List<Resultado> lista = new ArrayList<>();
        String sql = "SELECT * FROM resultados ORDER BY id DESC";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Resultado(
                        rs.getInt("id"),
                        rs.getInt("partido_id"),
                        rs.getInt("goles_local"),
                        rs.getInt("goles_visita")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
