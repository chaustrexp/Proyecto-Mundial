package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    // Cambia el nombre de la base de datos aquí si usas una distinta
    private static final String URL = "jdbc:mysql://localhost:3306/mundial?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private static final String USER = "root";

    private static final String PASS = "";

    static {
        try {
            // Carga el driver MySQL Connector/J (no da error si ya está cargado)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // En caso de fallo, lo imprimimos para depuración
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {

        return DriverManager.getConnection(URL, USER, PASS);
    }
}