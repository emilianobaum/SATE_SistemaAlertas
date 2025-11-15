package datos;

import modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object (DAO) para la entidad Usuario (MySQL).
 */
public class UsuarioDAO {
    
    private static final Logger LOGGER = Logger.getLogger(UsuarioDAO.class.getName());
    
    private static final String SELECT_POR_EMAIL = "SELECT id, email, password_hash, rol FROM usuarios WHERE email = ?";
    private static final String INSERT_NUEVO_USUARIO = "INSERT INTO usuarios (email, password_hash, rol) VALUES (?, ?, ?)";
    private static final String SELECT_TODOS = "SELECT id, email, password_hash, rol FROM usuarios";
    private static final String UPDATE_USUARIO = "UPDATE usuarios SET email = ?, password_hash = ?, rol = ? WHERE id = ?";
    private static final String DELETE_USUARIO = "DELETE FROM usuarios WHERE id = ?";

    public Usuario obtenerUsuarioPorEmail(String email) throws SQLException {
        Usuario usuario = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getInstancia().getConnection();
            stmt = conn.prepareStatement(SELECT_POR_EMAIL);
            stmt.setString(1, email);
            rs = stmt.executeQuery();

            if (rs.next()) {
                usuario = new Usuario(
                    rs.getString("id"),
                    rs.getString("email"),
                    rs.getString("password_hash"),
                    rs.getString("rol")
                );
            }
        } catch (SQLException e) {
             LOGGER.log(Level.SEVERE, "Error al obtener usuario por email: " + email, e);
             throw e; 
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignorar */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignorar */ }
            ConexionDB.close(conn); 
        }
        return usuario;
    }

    public boolean registrarNuevoUsuario(Usuario nuevoUsuario) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean exito = false;

        try {
            conn = ConexionDB.getInstancia().getConnection();
            stmt = conn.prepareStatement(INSERT_NUEVO_USUARIO);
            stmt.setString(1, nuevoUsuario.getEmail());
            stmt.setString(2, nuevoUsuario.getPasswordHash());
            stmt.setString(3, nuevoUsuario.getRol());

            int filasAfectadas = stmt.executeUpdate();
            exito = (filasAfectadas > 0);
            
            if (exito) {
                LOGGER.log(Level.INFO, "DAO: Nuevo usuario registrado: " + nuevoUsuario.getEmail());
            }

        } catch (SQLException e) {
             LOGGER.log(Level.SEVERE, "Error al registrar nuevo usuario: " + nuevoUsuario.getEmail(), e);
             throw e; 
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignorar */ }
            ConexionDB.close(conn);
        }
        return exito;
    }
    
    public List<Usuario> obtenerTodosLosUsuarios() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getInstancia().getConnection();
            stmt = conn.prepareStatement(SELECT_TODOS);
            rs = stmt.executeQuery();

            while (rs.next()) {
                usuarios.add(new Usuario(
                    rs.getString("id"),
                    rs.getString("email"),
                    rs.getString("password_hash"),
                    rs.getString("rol")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener todos los usuarios.", e);
            throw e; 
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignorar */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignorar */ }
            ConexionDB.close(conn); 
        }
        return usuarios;
    }
    
    public boolean modificarUsuario(Usuario usuario) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean exito = false;

        try {
            conn = ConexionDB.getInstancia().getConnection();
            stmt = conn.prepareStatement(UPDATE_USUARIO);
            stmt.setString(1, usuario.getEmail());
            stmt.setString(2, usuario.getPasswordHash());
            stmt.setString(3, usuario.getRol());
            stmt.setString(4, usuario.getId()); 

            int filasAfectadas = stmt.executeUpdate();
            exito = filasAfectadas > 0;
        } catch (SQLException e) {
             LOGGER.log(Level.SEVERE, "Error al modificar usuario: " + usuario.getId(), e);
             throw e; 
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignorar */ }
            ConexionDB.close(conn);
        }
        return exito;
    }
    
    public boolean eliminarUsuario(String id) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean exito = false;

        try {
            conn = ConexionDB.getInstancia().getConnection();
            stmt = conn.prepareStatement(DELETE_USUARIO);
            stmt.setString(1, id);

            int filasAfectadas = stmt.executeUpdate();
            exito = filasAfectadas > 0;
        } catch (SQLException e) {
             LOGGER.log(Level.SEVERE, "Error al eliminar usuario: " + id, e);
             throw e; 
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignorar */ }
            ConexionDB.close(conn);
        }
        return exito;
    }
}