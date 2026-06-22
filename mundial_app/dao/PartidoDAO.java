package dao;

import modelos.Equipo;
import modelos.Partido;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PartidoDAO {

    public boolean insertarPartido(Partido p) {
        String sql = "INSERT INTO partidos (equipo_local_id, equipo_visita_id, fecha, fase) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getEquipoLocal().getId());
            ps.setInt(2, p.getEquipoVisita().getId());
            if (p.getFecha() != null) {
                ps.setTimestamp(3, Timestamp.valueOf(p.getFecha()));
            } else {
                ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            }
            ps.setString(4, p.getFase());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Partido> obtenerPartidos() {
        List<Partido> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.fecha, p.fase, p.goles_local, p.goles_visita, " +
                     "e1.id as id_local, e1.nombre as nom_local, e1.grupo as gr_local, e1.confederacion as conf_local, " +
                     "e2.id as id_visita, e2.nombre as nom_visita, e2.grupo as gr_visita, e2.confederacion as conf_visita " +
                     "FROM partidos p " +
                     "JOIN equipos e1 ON p.equipo_local_id = e1.id " +
                     "JOIN equipos e2 ON p.equipo_visita_id = e2.id ORDER BY p.id";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Equipo eqL = new Equipo(rs.getInt("id_local"), rs.getString("nom_local"), rs.getString("gr_local"), rs.getString("conf_local"));
                Equipo eqV = new Equipo(rs.getInt("id_visita"), rs.getString("nom_visita"), rs.getString("gr_visita"), rs.getString("conf_visita"));
                LocalDateTime fecha = null;
                if (rs.getTimestamp("fecha") != null) {
                    fecha = rs.getTimestamp("fecha").toLocalDateTime();
                }
                
                Integer gl = rs.getInt("goles_local");
                if (rs.wasNull()) gl = null;
                
                Integer gv = rs.getInt("goles_visita");
                if (rs.wasNull()) gv = null;

                lista.add(new Partido(rs.getInt("id"), eqL, eqV, fecha, rs.getString("fase"), gl, gv));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean actualizarGolesPartido(int partidoId, int golesLocal, int golesVisita) {
        String sqlUpdate = "UPDATE partidos SET goles_local = ?, goles_visita = ? WHERE id = ?";
        
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
            psUpdate.setInt(1, golesLocal);
            psUpdate.setInt(2, golesVisita);
            psUpdate.setInt(3, partidoId);
            psUpdate.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarHorarioPartido(int partidoId, LocalDateTime fechaHora) {
        String sqlUpdate = "UPDATE partidos SET fecha = ? WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
            psUpdate.setTimestamp(1, Timestamp.valueOf(fechaHora));
            psUpdate.setInt(2, partidoId);
            psUpdate.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
