package modelos;

import java.util.Date;

public class Partido {
    private int id;
    private Equipo equipoLocal;
    private Equipo equipoVisita;
    private Date fecha;
    private String fase;
    private Integer golesLocal;
    private Integer golesVisita;

    public Partido() {}

    public Partido(int id, Equipo equipoLocal, Equipo equipoVisita, Date fecha, String fase, Integer golesLocal, Integer golesVisita) {
        this.id = id;
        this.equipoLocal = equipoLocal;
        this.equipoVisita = equipoVisita;
        this.fecha = fecha;
        this.fase = fase;
        this.golesLocal = golesLocal;
        this.golesVisita = golesVisita;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Equipo getEquipoLocal() { return equipoLocal; }
    public void setEquipoLocal(Equipo equipoLocal) { this.equipoLocal = equipoLocal; }

    public Equipo getEquipoVisita() { return equipoVisita; }
    public void setEquipoVisita(Equipo equipoVisita) { this.equipoVisita = equipoVisita; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public String getFase() { return fase; }
    public void setFase(String fase) { this.fase = fase; }

    public Integer getGolesLocal() { return golesLocal; }
    public void setGolesLocal(Integer golesLocal) { this.golesLocal = golesLocal; }

    public Integer getGolesVisita() { return golesVisita; }
    public void setGolesVisita(Integer golesVisita) { this.golesVisita = golesVisita; }

    @Override
    public String toString() {
        return equipoLocal.getNombre() + " vs " + equipoVisita.getNombre() + " (" + fase + ")";
    }
}
