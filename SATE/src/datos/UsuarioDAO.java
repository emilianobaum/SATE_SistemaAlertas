package datos;

import modelo.Usuario;

/**
 * Data Access Object (DAO) para la entidad Usuario.
 */
public class UsuarioDAO {
    
    // Simulación de conexión a base de datos
    public Usuario obtenerUsuarioPorEmail(String email) {
        // En una aplicación real, aquí se ejecutaría una consulta SQL.
        // Simulación de roles para la prueba:
        if (email.equals("admin@sate.com")) {
            return new Usuario("1", email, "pass", "ADMINISTRADOR");
        } else if (email.equals("validador@sate.com")) {
            return new Usuario("2", email, "pass", "VALIDADOR");
        } else if (email.equals("ciudadano@sate.com")) {
            return new Usuario("3", email, "pass", "CIUDADANO");
        }
        return null; // Usuario no encontrado
    }

    public boolean registrarNuevoUsuario(Usuario nuevoUsuario) {
        // En una aplicación real, aquí se ejecutaría una inserción SQL.
        System.out.println("DAO: Nuevo usuario registrado: " + nuevoUsuario.getEmail());
        return true;
    }
}