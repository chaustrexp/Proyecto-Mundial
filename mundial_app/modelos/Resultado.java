package modelos;

public class Resultado {
    private int id;
    private int partidoId;
    private int golesLocal;
    private int golesVisita;

    public Resultado(int id, int partidoId, int golesLocal, int golesVisita) {
        this.id = id;
        this.partidoId = partidoId;
        this.golesLocal = golesLocal;
        this.golesVisita = golesVisita;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPartidoId() {
        return partidoId;
    }

    public void setPartidoId(int partidoId) {
        this.partidoId = partidoId;
    }

    public int getGolesLocal() {
        return golesLocal;
    }

    public void setGolesLocal(int golesLocal) {
        this.golesLocal = golesLocal;
    }

    public int getGolesVisita() {
        return golesVisita;
    }

    public void setGolesVisita(int golesVisita) {
        this.golesVisita = golesVisita;
    }
}
