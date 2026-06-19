package dao;

import modelos.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MundialDAO_backup {

    // --- USUARIOS ---
    public Usuario validarLogin(String username, String password, String rol) {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ? AND rol = ?";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, rol);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("rol"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // --- APOSTADORES ---
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
        try (Connection conn = ConexionBD.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Apostador(rs.getInt("id"), rs.getString("nombre"), rs.getInt("puntos_total")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // --- EQUIPOS ---
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
        try (Connection conn = ConexionBD.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Equipo(rs.getInt("id"), rs.getString("nombre"), rs.getString("grupo"),
                        rs.getString("confederacion")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public int contarEquipos() {
        String sql = "SELECT COUNT(*) FROM equipos";
        try (Connection conn = ConexionBD.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // --- PARTIDOS ---
    public boolean insertarPartido(Partido p) {
        String sql = "INSERT INTO partidos (equipo_local_id, equipo_visita_id, fecha, fase) VALUES (?, ?, CURDATE(), ?)";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getEquipoLocal().getId());
            ps.setInt(2, p.getEquipoVisita().getId());
            ps.setString(3, p.getFase());
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
        try (Connection conn = ConexionBD.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Equipo eqL = new Equipo(rs.getInt("id_local"), rs.getString("nom_local"), rs.getString("gr_local"),
                        rs.getString("conf_local"));
                Equipo eqV = new Equipo(rs.getInt("id_visita"), rs.getString("nom_visita"), rs.getString("gr_visita"),
                        rs.getString("conf_visita"));
                lista.add(new Partido(rs.getInt("id"), eqL, eqV, rs.getDate("fecha"), rs.getString("fase"),
                        rs.getInt("goles_local"), rs.getInt("goles_visita")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean actualizarGolesPartido(int partidoId, int golesLocal, int golesVisita) {
        String sqlUpdate = "UPDATE partidos SET goles_local = ?, goles_visita = ? WHERE id = ?";
        String sqlInsert = "INSERT INTO resultados (partido_id, goles_local, goles_visita) VALUES (?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection()) {
            // Actualizamos la tabla de partidos
            try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
                psUpdate.setInt(1, golesLocal);
                psUpdate.setInt(2, golesVisita);
                psUpdate.setInt(3, partidoId);
                psUpdate.executeUpdate();
            }
            // Insertamos el registro histórico en la tabla 'resultados'
            try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
                psInsert.setInt(1, partidoId);
                psInsert.setInt(2, golesLocal);
                psInsert.setInt(3, golesVisita);
                psInsert.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- PREDICCIONES ---
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
        try (Connection conn = ConexionBD.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Apostador apos = new Apostador(rs.getInt("id_apos"), rs.getString("nom_apos"), 0);
                Equipo eL = new Equipo(0, rs.getString("nom_local"), "", "");
                Equipo eV = new Equipo(0, rs.getString("nom_visita"), "", "");
                Partido part = new Partido(rs.getInt("id_part"), eL, eV, null, "", 0, 0);

                lista.add(new Prediccion(rs.getInt("id"), apos, part, rs.getInt("goles_pred_eq1"),
                        rs.getInt("goles_pred_eq2"), rs.getInt("puntos_ganados")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // --- PUNTOS ---
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
