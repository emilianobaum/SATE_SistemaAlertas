package datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase Singleton para manejar la conexión a la base de datos MySQL.
 */
public class ConexionDB {
    private static final Logger LOGGER = Logger.getLogger(ConexionDB.class.getName());
    
    // Asegúrate de que esta DB y credenciales sean correctas
    private static final String JDBC_URL = "jdbc:mysql://10.0.100.24:3308/sate?useSSL=false&serverTimezone=UTC";
    private static final String JDBC_USER = "root"; 
    private static final String JDBC_PASSWORD = "password"; // Reemplaza con tu clave

    private static ConexionDB instancia;

    private ConexionDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "No se encontró el driver de MySQL.", ex);
        }
    }

    public static ConexionDB getInstancia() {
        if (instancia == null) {
            instancia = new ConexionDB();
        }
        return instancia;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }
    
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error al cerrar la conexión", ex);
            }
        }
    }
}