package controladores;

import dao.ApostadorDAO;
import modelos.Apostador;
import java.util.List;

/**
 * Controlador MVC para la gestión de Apostadores.
 * Actúa como intermediario entre la Vista (PanelApostadores) y el Modelo (ApostadorDAO).
 * Contiene la lógica de negocio y validaciones de datos.
 */
public class ApostadorController {

    private final ApostadorDAO dao;
    private String ultimoError;

    public ApostadorController() {
        this.dao = new ApostadorDAO();
    }

    /**
     * Valida e inserta un nuevo apostador en la base de datos.
     * @param nombre Nombre del apostador ingresado por el usuario.
     * @return true si se insertó correctamente, false si falló.
     */
    public boolean registrarApostador(String nombre) {
        ultimoError = null;

        // Validación 1: Campo vacío
        if (nombre == null || nombre.trim().isEmpty()) {
            ultimoError = "⚠ El nombre del apostador no puede estar vacío.";
            return false;
        }
        nombre = nombre.trim();

        // Validación 2: Mínimo 3 caracteres
        if (nombre.length() < 3) {
            ultimoError = "⚠ El nombre debe tener al menos 3 letras.";
            return false;
        }

        // Validación 3: Máximo 50 caracteres
        if (nombre.length() > 50) {
            ultimoError = "⚠ El nombre no puede superar los 50 caracteres.";
            return false;
        }

        // Lógica de negocio: intentar insertar en BD
        Apostador nuevo = new Apostador(nombre);
        boolean resultado = dao.insertarApostador(nuevo);
        if (!resultado) {
            ultimoError = "✖ Error al guardar en la base de datos. Intente de nuevo.";
        }
        return resultado;
    }

    /**
     * Obtiene la lista completa de apostadores desde la base de datos.
     * @return Lista de apostadores.
     */
    public List<Apostador> obtenerApostadores() {
        return dao.obtenerApostadores();
    }

    /**
     * Retorna el último mensaje de error generado por una operación fallida.
     * @return Mensaje de error, o null si no hubo error.
     */
    public String getUltimoError() {
        return ultimoError;
    }
}
