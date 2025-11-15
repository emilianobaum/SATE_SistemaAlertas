package controlador;

import datos.UsuarioDAO;
import modelo.Usuario;
import java.sql.SQLException;
import java.util.ArrayList; 
import java.util.List; 
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controlador para manejar la lógica de negocio de usuarios (Login, Registro, CRUD).
 */
public class ControladorUsuario {
    private UsuarioDAO usuarioDAO;
    private static final Logger LOGGER = Logger.getLogger(ControladorUsuario.class.getName());

    public ControladorUsuario() {
        this.usuarioDAO = new UsuarioDAO(); 
    }

    /**
     * Valida credenciales de usuario.
     * @return El objeto Usuario si el login es exitoso, o null.
     * @throws Exception Si ocurre un error en la capa de datos.
     */
    public Usuario iniciarSesion(String email, String password) throws Exception {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
             LOGGER.log(Level.WARNING, "Intento de login con campos vacíos.");
             return null; 
        }

        Usuario usuario = null;
        try {
            usuario = this.usuarioDAO.obtenerUsuarioPorEmail(email.toLowerCase());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error de DB al intentar iniciar sesión para: " + email, e);
            throw new Exception("Error de sistema al validar credenciales.", e); 
        }

        if (usuario != null && usuario.getPasswordHash().equals(password)) { 
             LOGGER.log(Level.INFO, "Login exitoso para el usuario: " + usuario.getEmail());
             return usuario;
        }
        
        LOGGER.log(Level.WARNING, "Credenciales inválidas para email: " + email);
        return null; 
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * @throws Exception Si ocurre un error en la capa de datos.
     */
    public boolean registrarUsuario(String nombre, String email, String password) throws Exception {
        try {
            if (this.usuarioDAO.obtenerUsuarioPorEmail(email) != null) {
                LOGGER.log(Level.WARNING, "Intento de registrar email duplicado: " + email);
                return false;
            }
    
            Usuario nuevoUsuario = new Usuario(
            	null, //Ca,,o AUTO INCREMENT
                email, 
                password, // Implementar Hashing
                "CIUDADANO" 
            );
            
            return this.usuarioDAO.registrarNuevoUsuario(nuevoUsuario);

        } catch (SQLException e) {
             LOGGER.log(Level.SEVERE, "Error de DB al intentar registrar usuario: " + email, e);
            throw new Exception("Error de sistema al registrar usuario.", e);
        }
    }
    
    /**
     * Obtiene todos los usuarios para la pantalla de gestión.
     * @return Lista de todos los usuarios.
     * @throws Exception Si hay un error de BBDD.
     */
    public List<Usuario> obtenerTodosLosUsuarios() throws Exception {
        try {
            return this.usuarioDAO.obtenerTodosLosUsuarios();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error de DB al obtener todos los usuarios.", e);
            throw new Exception("Error de sistema al cargar usuarios.", e);
        }
    }
    
    /**
     * Modifica los datos de un usuario existente.
     * @throws Exception Si hay un error de BBDD.
     */
    public boolean modificarUsuario(Usuario usuario) throws Exception {
        try {
            if (usuario == null || usuario.getId() == null) {
                LOGGER.log(Level.WARNING, "Intento de modificar usuario con datos nulos.");
                return false;
            }
            return this.usuarioDAO.modificarUsuario(usuario);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error de DB al modificar usuario: " + usuario.getId(), e);
            throw new Exception("Error de sistema al modificar usuario.", e);
        }
    }
    
    /**
     * Da de elimina un usuario del sistema.
     * @throws Exception Si hay un error de BBDD.
     */
    public boolean darDeBajaUsuario(String id) throws Exception {
        try {
            if (id == null || id.trim().isEmpty()) {
                 LOGGER.log(Level.WARNING, "Intento de eliminar usuario con ID nulo.");
                return false;
            }
            if (id.equals("1")) { // Lógica de negocio. Hardcoded por seguridad
                 LOGGER.log(Level.WARNING, "Intento ILEGAL de eliminar al administrador principal (ID 1).");
                 return false;
            }
            return this.usuarioDAO.eliminarUsuario(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error de DB al dar de baja usuario: " + id, e);
            throw new Exception("Error de sistema al dar de baja usuario.", e);
        }
    }
}