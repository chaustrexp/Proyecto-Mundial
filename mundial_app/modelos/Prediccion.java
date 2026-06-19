package modelos;

public class Prediccion {
    private int id;
    private Apostador apostador;
    private Partido partido;
    private int golesPredEq1;
    private int golesPredEq2;
    private int puntosGanados;

    public Prediccion() {}

    public Prediccion(int id, Apostador apostador, Partido partido, int golesPredEq1, int golesPredEq2, int puntosGanados) {
        this.id = id;
        this.apostador = apostador;
        this.partido = partido;
        this.golesPredEq1 = golesPredEq1;
        this.golesPredEq2 = golesPredEq2;
        this.puntosGanados = puntosGanados;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Apostador getApostador() { return apostador; }
    public void setApostador(Apostador apostador) { this.apostador = apostador; }

    public Partido getPartido() { return partido; }
    public void setPartido(Partido partido) { this.partido = partido; }

    public int getGolesPredEq1() { return golesPredEq1; }
    public void setGolesPredEq1(int golesPredEq1) { this.golesPredEq1 = golesPredEq1; }

    public int getGolesPredEq2() { return golesPredEq2; }
    public void setGolesPredEq2(int golesPredEq2) { this.golesPredEq2 = golesPredEq2; }

    public int getPuntosGanados() { return puntosGanados; }
    public void setPuntosGanados(int puntosGanados) { this.puntosGanados = puntosGanados; }
}
