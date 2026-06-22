package controladores;

import dao.EquipoDAO;
import dao.PartidoDAO;
import modelos.Equipo;
import modelos.Partido;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador MVC para la gestión de Partidos.
 * Actúa como intermediario entre la Vista (PanelPartidos) y los DAOs correspondientes.
 * Contiene la lógica de negocio y validaciones de datos.
 */
public class PartidoController {

    private final PartidoDAO partidoDAO;
    private final EquipoDAO equipoDAO;
    private String ultimoError;

    public PartidoController() {
        this.partidoDAO = new PartidoDAO();
        this.equipoDAO = new EquipoDAO();
    }

    /**
     * Valida y crea un nuevo partido en la base de datos.
     * @param equipoLocal  Equipo local seleccionado.
     * @param equipoVisita Equipo visitante seleccionado.
     * @param fase         Fase del torneo.
     * @param partidosExistentes Lista actual de partidos (para detectar duplicados).
     * @param fechaHora    Hora y fecha del partido.
     * @return true si se creó correctamente, false si falló.
     */
    public boolean crearPartido(Equipo equipoLocal, Equipo equipoVisita, String fase, List<Partido> partidosExistentes, LocalDateTime fechaHora) {
        ultimoError = null;

        // Validación 1: Equipos nulos
        if (equipoLocal == null || equipoVisita == null) {
            ultimoError = "⚠ Primero debe cargar los equipos.\nHaga clic en 'Actualizar Lista Equipos'.";
            return false;
        }

        // Validación 2: No puede enfrentarse el mismo equipo consigo mismo
        if (equipoLocal.getId() == equipoVisita.getId()) {
            ultimoError = "⚠ El equipo local y el visitante no pueden ser el mismo.";
            return false;
        }

        // Validación 3: No crear el mismo partido dos veces
        for (Partido p : partidosExistentes) {
            if (p.getEquipoLocal().getId() == equipoLocal.getId()
                    && p.getEquipoVisita().getId() == equipoVisita.getId()) {
                ultimoError = "⚠ Este partido ya existe en el sistema.";
                return false;
            }
        }

        // Validación 4: Si es Fase de Grupos, los equipos deben pertenecer al mismo grupo
        if ("Grupo".equalsIgnoreCase(fase)) {
            if (equipoLocal.getGrupo() == null || equipoVisita.getGrupo() == null ||
                !equipoLocal.getGrupo().equals(equipoVisita.getGrupo())) {
                ultimoError = "⚠ Para la Fase de Grupos, el equipo local y visita deben pertenecer al mismo grupo.";
                return false;
            }
        }

        // Lógica de negocio: insertar en BD
        Partido nuevo = new Partido(0, equipoLocal, equipoVisita, fechaHora, fase, 0, 0);
        boolean resultado = partidoDAO.insertarPartido(nuevo);
        if (!resultado) {
            ultimoError = "✖ Error al crear el partido en la base de datos.";
        }
        return resultado;
    }

    /**
     * Obtiene todos los partidos desde la base de datos.
     * @return Lista de partidos.
     */
    public List<Partido> obtenerPartidos() {
        return partidoDAO.obtenerPartidos();
    }

    /**
     * Obtiene todos los equipos desde la base de datos.
     * @return Lista de equipos.
     */
    public List<Equipo> obtenerEquipos() {
        return equipoDAO.obtenerEquipos();
    }

    /**
     * Retorna el último mensaje de error generado por una operación fallida.
     * @return Mensaje de error, o null si no hubo error.
     */
    public String getUltimoError() {
        return ultimoError;
    }

    /**
     * Actualiza los goles de un partido finalizado en la base de datos.
     */
    public boolean actualizarGolesPartido(int partidoId, int golesLocal, int golesVisita) {
        return partidoDAO.actualizarGolesPartido(partidoId, golesLocal, golesVisita);
    }

    /**
     * Actualiza la fecha y hora de un partido.
     */
    public boolean actualizarHorarioPartido(int partidoId, LocalDateTime fechaHora) {
        return partidoDAO.actualizarHorarioPartido(partidoId, fechaHora);
    }
}
