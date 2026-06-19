package controladores;

import dao.PartidoDAO;
import dao.PrediccionDAO;
import modelos.Partido;
import java.util.List;

/**
 * Controlador MVC para la gestión de Resultados.
 * Actúa como intermediario entre la Vista (PanelResultados) y los DAOs.
 * Contiene la lógica de negocio y validaciones de datos.
 */
public class ResultadoController {

    private final PartidoDAO partidoDAO;
    private final PrediccionDAO prediccionDAO;
    private String ultimoError;

    public ResultadoController() {
        this.partidoDAO = new PartidoDAO();
        this.prediccionDAO = new PrediccionDAO();
    }

    /**
     * Valida y registra el resultado de un partido, calculando los puntos automáticamente.
     * @param partido         Partido al que se le registra el resultado.
     * @param golesLocalStr   Goles del equipo local (como texto, para validar).
     * @param golesVisitaStr  Goles del equipo visitante (como texto, para validar).
     * @return true si el resultado se guardó correctamente, false si falló.
     */
    public boolean registrarResultado(Partido partido, String golesLocalStr, String golesVisitaStr) {
        ultimoError = null;

        // Validación 1: Partido requerido
        if (partido == null) {
            ultimoError = "⚠ No hay partidos disponibles.\nVaya a la pestaña 'Partidos' y cree uno primero.";
            return false;
        }

        // Validación 2: Campos no vacíos
        if (golesLocalStr == null || golesLocalStr.trim().isEmpty()) {
            ultimoError = "⚠ Debe ingresar el resultado para el equipo local.";
            return false;
        }
        if (golesVisitaStr == null || golesVisitaStr.trim().isEmpty()) {
            ultimoError = "⚠ Debe ingresar el resultado para el equipo visitante.";
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

            // Lógica de negocio: actualizar resultado y calcular puntos
            int partidoId = partido.getId();
            boolean resultado = partidoDAO.actualizarGolesPartido(partidoId, gl, gv);
            if (resultado) {
                prediccionDAO.calcularPuntosParaPartido(partidoId, gl, gv);
            } else {
                ultimoError = "✖ Error al guardar el resultado en la base de datos.";
            }
            return resultado;

        } catch (NumberFormatException e) {
            ultimoError = "✖ Los goles deben ser números enteros válidos.";
            return false;
        }
    }

    /**
     * Obtiene la lista de partidos disponibles para mostrar resultados.
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
