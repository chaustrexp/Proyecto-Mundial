package controladores;

import dao.ApostadorDAO;
import dao.PartidoDAO;
import dao.PrediccionDAO;
import modelos.Apostador;
import modelos.Partido;
import modelos.Prediccion;
import java.util.List;

/**
 * Controlador MVC para la gestión de Predicciones.
 * Actúa como intermediario entre la Vista (PanelPredicciones) y los DAOs.
 * Contiene la lógica de negocio y validaciones de datos.
 */
public class PrediccionController {

    private final PrediccionDAO prediccionDAO;
    private final ApostadorDAO apostadorDAO;
    private final PartidoDAO partidoDAO;
    private String ultimoError;

    public PrediccionController() {
        this.prediccionDAO = new PrediccionDAO();
        this.apostadorDAO = new ApostadorDAO();
        this.partidoDAO = new PartidoDAO();
    }

    /**
     * Valida y registra una nueva predicción en la base de datos.
     * @param apostador     Apostador que realiza la predicción.
     * @param partido       Partido sobre el que se predice.
     * @param golesLocalStr Goles del equipo local (como texto, para validar).
     * @param golesVisitaStr Goles del equipo visitante (como texto, para validar).
     * @return true si se guardó correctamente, false si falló.
     */
    public boolean registrarPrediccion(Apostador apostador, Partido partido,
                                       String golesLocalStr, String golesVisitaStr) {
        ultimoError = null;

        // Validación 1: Apostador y partido requeridos
        if (apostador == null) {
            ultimoError = "⚠ No hay apostadores registrados.\nVaya a la pestaña 'Apostadores' y registre uno primero.";
            return false;
        }
        if (partido == null) {
            ultimoError = "⚠ No hay partidos disponibles.\nVaya a la pestaña 'Partidos' y cree uno primero.";
            return false;
        }

        // Validación 2: Campos de goles no vacíos
        if (golesLocalStr == null || golesLocalStr.trim().isEmpty()) {
            ultimoError = "⚠ Debe ingresar los goles predichos para el equipo local.";
            return false;
        }
        if (golesVisitaStr == null || golesVisitaStr.trim().isEmpty()) {
            ultimoError = "⚠ Debe ingresar los goles predichos para el equipo visitante.";
            return false;
        }

        try {
            int gl = Integer.parseInt(golesLocalStr.trim());
            int gv = Integer.parseInt(golesVisitaStr.trim());

            // Validación 3: No negativos
            if (gl < 0 || gv < 0) {
                ultimoError = "⚠ Los goles no pueden ser un número negativo.";
                return false;
            }
            // Validación 4: Máximo razonable
            if (gl > 30 || gv > 30) {
                ultimoError = "⚠ El número de goles parece inválido (máximo 30).";
                return false;
            }

            // Lógica de negocio: insertar predicción
            Prediccion nueva = new Prediccion(0, apostador, partido, gl, gv, 0);
            boolean resultado = prediccionDAO.insertarPrediccion(nueva);
            if (!resultado) {
                ultimoError = "✖ Error al guardar la predicción. Verifique que no exista ya una predicción igual.";
            }
            return resultado;

        } catch (NumberFormatException e) {
            ultimoError = "✖ Los goles deben ser números enteros válidos.";
            return false;
        }
    }

    /**
     * Obtiene la lista completa de predicciones desde la base de datos.
     * @return Lista de predicciones.
     */
    public List<Prediccion> obtenerPredicciones() {
        return prediccionDAO.obtenerPredicciones();
    }

    /**
     * Obtiene la lista de apostadores disponibles.
     * @return Lista de apostadores.
     */
    public List<Apostador> obtenerApostadores() {
        return apostadorDAO.obtenerApostadores();
    }

    /**
     * Obtiene la lista de partidos disponibles.
     * @return Lista de partidos.
     */
    public List<Partido> obtenerPartidos() {
        return partidoDAO.obtenerPartidos();
    }

    /**
     * Retorna el último mensaje de error generado por una operación fallida.
     * @return Mensaje de error, o null si no hubo error.
     */
    public String getUltimoError() {
        return ultimoError;
    }
}
