package datos;

import modelo.EntidadEvento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object (DAO) para la entidad EntidadEvento.
 * Gestiona la persistencia de eventos aprobados.
 */
public class EventoDAO {
    
    private static final Logger LOGGER = Logger.getLogger(EventoDAO.class.getName());

    // Asumiendo que la tabla se llama 'eventos'
    private static final String INSERT_EVENTO_SQL = 
            "INSERT INTO eventos (id, tipo, fecha_hora, descripcion, coordenadasWKT) " +
            "VALUES (?, ?, ?, ?, ?)"; // Usar ST_GeomFromText si la columna es GEOMETRY

    /**
     * Inserta un nuevo evento en la base de datos.
     * @param evento El objeto EntidadEvento a insertar.
     * @return true si la inserción es exitosa.
     * @throws SQLException Si hay un error de DB.
     */
    public boolean insertarEvento(EntidadEvento evento) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConexionDB.getInstancia().getConnection(); 
            ps = conn.prepareStatement(INSERT_EVENTO_SQL);
            
            ps.setString(1, evento.getId());
            ps.setString(2, evento.getTipo());
            ps.setTimestamp(3, Timestamp.valueOf(evento.getFechaHora()));
            ps.setString(4, evento.getDescripcion());
            ps.setString(5, evento.getCoordenadasWKT()); 
            
            int filasAfectadas = ps.executeUpdate();
            
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al insertar el evento en la DB: " + evento.getId(), e);
            throw e; 
        } finally {
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
            ConexionDB.close(conn);
        }
    }
}