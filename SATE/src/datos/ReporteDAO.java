package datos;

import modelo.Reporte;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp; // Importar Timestamp
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object (DAO) para la entidad Reporte (MySQL).
 */
public class ReporteDAO {
    
    private static final Logger LOGGER = Logger.getLogger(ReporteDAO.class.getName());

    private static final String SELECT_PENDIENTES = 
        "SELECT id, tipo, fecha, descripcion, coordenadasWKT, estado FROM reportes WHERE estado = 'PENDIENTE'";
    private static final String SELECT_APROBADOS = 
        "SELECT id, tipo, fecha, descripcion, coordenadasWKT, estado FROM reportes WHERE estado = 'APROBADO'";
    private static final String UPDATE_ESTADO = 
        "UPDATE reportes SET estado = ? WHERE id = ?";
    // --- NUEVA CONSULTA DE INSERCIÓN ---
    private static final String INSERT_REPORTE = 
        "INSERT INTO reportes (tipo, fecha, descripcion, coordenadasWKT, estado) VALUES (?, ?, ?, ?, ?)";

    /**
     * Helper privado para mapear un ResultSet a un objeto Reporte.
     */
    private Reporte mapearReporte(ResultSet rs) throws SQLException {
        // Asegurarse de que el formato de fecha (Timestamp) se convierta a String
        String fechaStr = rs.getTimestamp("fecha").toLocalDateTime().toString().replace("T", " ");
        // Quitar milisegundos si existen
        if (fechaStr.contains(".")) {
            fechaStr = fechaStr.substring(0, fechaStr.indexOf("."));
        }
        
        return new Reporte(
            rs.getString("id"),
            rs.getString("tipo"),
            fechaStr, 
            rs.getString("descripcion"),
            rs.getString("coordenadasWKT"),
            rs.getString("estado")
        );
    }

    /**
     * Obtiene todos los reportes que se encuentran en estado 'PENDIENTE'.
     */
    public List<Reporte> obtenerReportesPendientes() throws SQLException {
        List<Reporte> pendientes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getInstancia().getConnection();
            stmt = conn.prepareStatement(SELECT_PENDIENTES);
            rs = stmt.executeQuery();

            while (rs.next()) {
                pendientes.add(mapearReporte(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener reportes pendientes.", e);
            throw e; 
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignorar */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignorar */ }
            ConexionDB.close(conn); 
        }
        return pendientes;
    }
    
    /**
     * Obtiene todos los reportes que se encuentran en estado 'APROBADO' (para el mapa).
     */
    public List<Reporte> obtenerReportesAprobados() throws SQLException {
        List<Reporte> aprobados = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getInstancia().getConnection();
            stmt = conn.prepareStatement(SELECT_APROBADOS);
            rs = stmt.executeQuery();

            while (rs.next()) {
                aprobados.add(mapearReporte(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener reportes aprobados.", e);
            throw e; 
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignorar */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignorar */ }
            ConexionDB.close(conn); 
        }
        return aprobados;
    }

    /**
     * Actualiza el estado de un reporte específico.
     */
    public boolean actualizarEstadoReporte(String idReporte, String nuevoEstado) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean exito = false;

        try {
            conn = ConexionDB.getInstancia().getConnection();
            stmt = conn.prepareStatement(UPDATE_ESTADO);
            stmt.setString(1, nuevoEstado);
            stmt.setString(2, idReporte);

            int filasAfectadas = stmt.executeUpdate();
            exito = filasAfectadas > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar estado del reporte: " + idReporte, e);
            throw e; 
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignorar */ }
            ConexionDB.close(conn);
        }
        return exito;
    }
    
    /**
     * Inserta un nuevo reporte manual en la base de datos.
     */
    public boolean insertarReporte(Reporte reporte) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean exito = false;
        
        try {
            conn = ConexionDB.getInstancia().getConnection();
            stmt = conn.prepareStatement(INSERT_REPORTE);
            
            //stmt.setString(1, reporte.getId());
            stmt.setString(1, reporte.getTipo());
            // Convertir el String de fecha a Timestamp para la DB
            stmt.setTimestamp(2, Timestamp.valueOf(reporte.getFecha())); 
            stmt.setString(3, reporte.getDescripcion());
            stmt.setString(4, reporte.getCoordenadasWKT());
            stmt.setString(5, reporte.getEstado());

            int filasAfectadas = stmt.executeUpdate();
            exito = filasAfectadas > 0;

        } catch (SQLException e) {
             LOGGER.log(Level.SEVERE, "Error al insertar nuevo reporte: " + reporte.getId(), e);
             throw e; 
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignorar */ }
            ConexionDB.close(conn);
        }
        return exito;
    }
}