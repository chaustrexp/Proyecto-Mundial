package modelos;

public class Equipo {
    private int id;
    private String nombre;
    private String grupo;
    private String federacion;

    public Equipo(int id, String nombre, String grupo, String federacion) {
        this.id = id;
        this.nombre = nombre;
        this.grupo = grupo;
        this.federacion = federacion;
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

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getFederacion() {
        return federacion;
    }

    public void setFederacion(String federacion) {
        this.federacion = federacion;
    }
}
