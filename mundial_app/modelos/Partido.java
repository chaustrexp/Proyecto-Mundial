package modelos;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Partido {

    public static final String ESTADO_PROGRAMADO  = "programado";
    public static final String ESTADO_EN_VIVO     = "en_vivo";
    public static final String ESTADO_FINALIZADO  = "finalizado";

    private int id;
    private Equipo equipoLocal;
    private Equipo equipoVisita;
    private LocalDateTime fecha;
    private String fase;
    private String estado;          // programado | en_vivo | finalizado
    private int golesLocalVivo;     // marcador temporal mientras está en vivo
    private int golesVisitaVivo;
    private int minutoActual;
    private Integer golesLocal;     // marcador oficial (solo al finalizar)
    private Integer golesVisita;

    public Partido() {}

    public Partido(int id, Equipo equipoLocal, Equipo equipoVisita, LocalDateTime fecha, String fase,
                   String estado, int golesLocalVivo, int golesVisitaVivo, int minutoActual,
                   Integer golesLocal, Integer golesVisita) {
        this.id = id;
        this.equipoLocal = equipoLocal;
        this.equipoVisita = equipoVisita;
        this.fecha = fecha;
        this.fase = fase;
        this.estado = (estado != null) ? estado : ESTADO_PROGRAMADO;
        this.golesLocalVivo = golesLocalVivo;
        this.golesVisitaVivo = golesVisitaVivo;
        this.minutoActual = minutoActual;
        this.golesLocal = golesLocal;
        this.golesVisita = golesVisita;
    }

    // Constructor legacy para compatibilidad
    public Partido(int id, Equipo equipoLocal, Equipo equipoVisita, LocalDateTime fecha, String fase,
                   Integer golesLocal, Integer golesVisita) {
        this(id, equipoLocal, equipoVisita, fecha, fase,
             golesLocal != null ? ESTADO_FINALIZADO : ESTADO_PROGRAMADO,
             0, 0, 0, golesLocal, golesVisita);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Equipo getEquipoLocal() { return equipoLocal; }
    public void setEquipoLocal(Equipo equipoLocal) { this.equipoLocal = equipoLocal; }

    public Equipo getEquipoVisita() { return equipoVisita; }
    public void setEquipoVisita(Equipo equipoVisita) { this.equipoVisita = equipoVisita; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getFase() { return fase; }
    public void setFase(String fase) { this.fase = fase; }

    public String getEstado() { return estado != null ? estado : ESTADO_PROGRAMADO; }
    public void setEstado(String estado) { this.estado = estado; }

    public boolean isEnVivo()     { return ESTADO_EN_VIVO.equals(estado); }
    public boolean isFinalizado() { return ESTADO_FINALIZADO.equals(estado); }
    public boolean isProgramado() { return ESTADO_PROGRAMADO.equals(getEstado()); }

    public int getGolesLocalVivo()  { return golesLocalVivo; }
    public void setGolesLocalVivo(int g) { this.golesLocalVivo = g; }

    public int getGolesVisitaVivo() { return golesVisitaVivo; }
    public void setGolesVisitaVivo(int g) { this.golesVisitaVivo = g; }

    public int getMinutoActual() { return minutoActual; }
    public void setMinutoActual(int m) { this.minutoActual = m; }

    public Integer getGolesLocal() { return golesLocal; }
    public void setGolesLocal(Integer golesLocal) { this.golesLocal = golesLocal; }

    public Integer getGolesVisita() { return golesVisita; }
    public void setGolesVisita(Integer golesVisita) { this.golesVisita = golesVisita; }

    @Override
    public String toString() {
        return equipoLocal.getNombre() + " vs " + equipoVisita.getNombre() + " (" + fase + ")";
    }

    public boolean isLocked() {
        if (fecha == null) return false;
        LocalDateTime now = LocalDateTime.now();
        // Está bloqueado si ya pasó la fecha o faltan 10 minutos o menos
        return now.isAfter(fecha) || ChronoUnit.MINUTES.between(now, fecha) <= 10;
    }
}
