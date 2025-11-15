package datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase encargada de inicializar la estructura de la base de datos (tablas)
 * y cargar datos de prueba iniciales.
 */
public class DatabaseInitializer {
    
    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());

    /**
     * Ejecuta el proceso de inicialización de la BBDD.
     */
    public static void initialize() {
        Connection conn = null;
        LOGGER.info("Iniciando DatabaseInitializer...");
        try {
            conn = ConexionDB.getInstancia().getConnection(); 
            
            if (conn == null) {
                LOGGER.severe("DatabaseInitializer: No se pudo conectar a la BBDD. Inicialización fallida.");
                return;
            }

            createTables(conn);
            // Comento una vez inicializada la base de datos por primera vez
            insertInitialData(conn);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "DatabaseInitializer: Ocurrió un error SQL durante la inicialización de datos.", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DatabaseInitializer: Ocurrió un error inesperado.", e);
        } finally {
            ConexionDB.close(conn); 
            LOGGER.info("DatabaseInitializer finalizado.");
        }
    }

    /**
     * Crea las tablas de la aplicación.
     */
    private static void createTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            
            // 1. Crear tabla de Usuarios
            String sqlUsuarios = """
                CREATE TABLE IF NOT EXISTS usuarios (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    email VARCHAR(100) UNIQUE NOT NULL,
                    password_hash VARCHAR(100) NOT NULL,
                    rol VARCHAR(20) NOT NULL
                );
            """;
            stmt.execute(sqlUsuarios);
            LOGGER.config("Tabla 'usuarios' verificada/creada.");

            // 2. Crear tabla de Reportes
            String sqlReportes = """
                CREATE TABLE IF NOT EXISTS reportes (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    tipo VARCHAR(50) NOT NULL,
                    fecha DATETIME NOT NULL,
                    descripcion TEXT,
                    coordenadasWKT VARCHAR(100),
                    estado VARCHAR(20) NOT NULL
                );
            """;
            stmt.execute(sqlReportes);
            LOGGER.config("Tabla 'reportes' verificada/creada.");
            
            // 3. Crear tabla de Eventos (para reportes aprobados)
            String sqlEventos = """
                CREATE TABLE IF NOT EXISTS eventos (
                    id VARCHAR(50) PRIMARY KEY,
                    id_reporte_origen VARCHAR(50),
                    tipo VARCHAR(50) NOT NULL,
                    fecha_hora DATETIME NOT NULL,
                    descripcion TEXT,
                    coordenadasWKT VARCHAR(100)
                );
            """;
            stmt.execute(sqlEventos);
            LOGGER.config("Tabla 'eventos' verificada/creada.");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al crear tablas.", e);
            throw e;
        }
    }

    /**
     * Inserta los datos iniciales (usuarios y reportes de prueba).
     */
    private static void insertInitialData(Connection conn) throws SQLException {
        // Inserción de Usuarios
        String sqlInsertUsuario = "INSERT IGNORE INTO usuarios (id, email, password_hash, rol) VALUES (?, ?, ?, ?)";
        
        String[][] initialUsers = {
            {"1", "admin@sate.com", "pass", "ADMINISTRADOR"},
            {"2", "validador@sate.com", "pass", "VALIDADOR"},
            {"3", "ciudadano@sate.com", "pass", "CIUDADANO"}
        };

        LOGGER.info("Insertando datos iniciales de usuarios...");
        
        try (PreparedStatement stmt = conn.prepareStatement(sqlInsertUsuario)) {
            for (String[] user : initialUsers) {
                stmt.setString(1, user[0]); 
                stmt.setString(2, user[1]);
                stmt.setString(3, user[2]);
                stmt.setString(4, user[3]); 
                stmt.addBatch();
            }
            stmt.executeBatch();
            LOGGER.info("Usuarios iniciales cargados.");
        }

        // Inserción de Reportes Iniciales
        String sqlInsertReporte = "INSERT IGNORE INTO reportes (id, tipo, fecha, descripcion, coordenadasWKT, estado) VALUES (?, ?, NOW(), ?, ?, ?)";
        
        String[][] initialReports = {
            {"R101", "Incendio", "Foco de calor en área residencial.", "POINT(-64.1 -31.4)", "PENDIENTE"},
            {"R102", "Inundación", "Desborde de río menor en zona norte.", "POINT(-64.2 -31.5)", "PENDIENTE"},
            {"R103", "Accidente", "Choque múltiple en autopista 50.", "POINT(-64.3 -31.6)", "PENDIENTE"},
            {"E201", "Incendio", "Incendio forestal en las sierras.", "POINT(-64.5 -31.2)", "APROBADO"}
        };
        
        LOGGER.info("Insertando reportes iniciales...");
        
        try (PreparedStatement stmt = conn.prepareStatement(sqlInsertReporte)) {
             for (String[] reporte : initialReports) {
                stmt.setString(1, reporte[0]); 
                stmt.setString(2, reporte[1]); 
                stmt.setString(3, reporte[2]); 
                stmt.setString(4, reporte[3]); 
                stmt.setString(5, reporte[4]); 
                stmt.addBatch();
            }
            stmt.executeBatch();
            LOGGER.info("Reportes iniciales cargados.");

        } catch (SQLException e) {
            // Ignorar si ya existen (debido a INSERT IGNORE)
            LOGGER.config("Advertencia: Los reportes iniciales ya existían.");
        }
    }

}
