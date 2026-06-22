package modelos;

public class Apostador {
    private int id;
    private String nombre;
    private int puntosTotal;
    private Integer usuarioId;

    public Apostador(int id, String nombre, int puntosTotal, Integer usuarioId) {
        this.id = id;
        this.nombre = nombre;
        this.puntosTotal = puntosTotal;
        this.usuarioId = usuarioId;
    }

    public Apostador(int id, String nombre, int puntosTotal) {
        this.id = id;
        this.nombre = nombre;
        this.puntosTotal = puntosTotal;
        this.usuarioId = null;
    }

    public Apostador(String nombre) {
        this.nombre = nombre;
        this.puntosTotal = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPuntosTotal() {
        return puntosTotal;
    }

    public void setPuntosTotal(int puntosTotal) {
        this.puntosTotal = puntosTotal;
    }

    public void sumarPuntos(int puntos) {
        this.puntosTotal += puntos;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }
}
