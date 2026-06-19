package dao;

import modelos.Apostador;
import modelos.Equipo;
import modelos.Partido;
import modelos.Prediccion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrediccionDAO {

    public boolean insertarPrediccion(Prediccion p) {
        String sql = "INSERT INTO predicciones (apostador_id, partido_id, goles_pred_eq1, goles_pred_eq2, puntos_ganados) VALUES (?, ?, ?, ?, 0)";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getApostador().getId());
            ps.setInt(2, p.getPartido().getId());
            ps.setInt(3, p.getGolesPredEq1());
            ps.setInt(4, p.getGolesPredEq2());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Prediccion> obtenerPredicciones() {
        List<Prediccion> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.goles_pred_eq1, p.goles_pred_eq2, p.puntos_ganados, " +
                     "a.id as id_apos, a.nombre as nom_apos, " +
                     "part.id as id_part, e1.nombre as nom_local, e2.nombre as nom_visita " +
                     "FROM predicciones p " +
                     "JOIN apostadores a ON p.apostador_id = a.id " +
                     "JOIN partidos part ON p.partido_id = part.id " +
                     "JOIN equipos e1 ON part.equipo_local_id = e1.id " +
                     "JOIN equipos e2 ON part.equipo_visita_id = e2.id ORDER BY p.id DESC";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Apostador apos = new Apostador(rs.getInt("id_apos"), rs.getString("nom_apos"), 0);
                Equipo eL = new Equipo(0, rs.getString("nom_local"), "", "");
                Equipo eV = new Equipo(0, rs.getString("nom_visita"), "", "");
                Partido part = new Partido(rs.getInt("id_part"), eL, eV, null, "", 0, 0);
                
                lista.add(new Prediccion(rs.getInt("id"), apos, part, rs.getInt("goles_pred_eq1"), rs.getInt("goles_pred_eq2"), rs.getInt("puntos_ganados")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void calcularPuntosParaPartido(int partidoId, int golesRealLocal, int golesRealVisita) {
        String sqlSelect = "SELECT id, apostador_id, goles_pred_eq1, goles_pred_eq2 FROM predicciones WHERE partido_id = ?";
        String sqlUpdatePred = "UPDATE predicciones SET puntos_ganados = ? WHERE id = ?";
        String sqlUpdateApostador = "UPDATE apostadores SET puntos_total = puntos_total + ? WHERE id = ?";

        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sqlSelect)) {
            ps.setInt(1, partidoId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int predId = rs.getInt("id");
                int aposId = rs.getInt("apostador_id");
                int pLocal = rs.getInt("goles_pred_eq1");
                int pVisita = rs.getInt("goles_pred_eq2");

                int puntos = 0;
                if (pLocal == golesRealLocal && pVisita == golesRealVisita) {
                    puntos = 5;
                } else if ((pLocal > pVisita && golesRealLocal > golesRealVisita) || 
                           (pLocal < pVisita && golesRealLocal < golesRealVisita) || 
                           (pLocal == pVisita && golesRealLocal == golesRealVisita)) {
                    puntos = 3;
                }

                // Update prediction points
                if (puntos > 0) {
                    try (PreparedStatement psPred = conn.prepareStatement(sqlUpdatePred)) {
                        psPred.setInt(1, puntos);
                        psPred.setInt(2, predId);
                        psPred.executeUpdate();
                    }
                    // Update user points
                    try (PreparedStatement psApos = conn.prepareStatement(sqlUpdateApostador)) {
                        psApos.setInt(1, puntos);
                        psApos.setInt(2, aposId);
                        psApos.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
