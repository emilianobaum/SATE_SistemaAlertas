package controlador;

import datos.UsuarioDAO;
import modelo.Usuario;

/**
 * Controlador para manejar la lógica de negocio de usuarios (Login, Registro).
 */
public class ControladorUsuario {
    private UsuarioDAO usuarioDAO;

    public ControladorUsuario() {
        this.usuarioDAO = new UsuarioDAO(); // Inicia la capa de datos
    }

    /**
     * Valida credenciales de usuario.
     * @return El objeto Usuario si el login es exitoso, o null.
     */
    public Usuario iniciarSesion(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            return null; // Validación básica
        }

        Usuario usuario = usuarioDAO.obtenerUsuarioPorEmail(email.toLowerCase());

        // Simulación de verificación de contraseña (solo chequeando existencia)
        if (usuario != null && usuario.getPasswordHash().equals("pass")) { 
             return usuario;
        }
        return null;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     */
    public boolean registrarUsuario(String nombre, String email, String password) {
        // Lógica de validación de negocio (ej. el email es único, la contraseña es segura)
        if (usuarioDAO.obtenerUsuarioPorEmail(email) != null) {
            System.out.println("CONTROLADOR: Error, el email ya está registrado.");
            return false;
        }

        // Crear el nuevo usuario (simulado: siempre es CIUDADANO)
        Usuario nuevoUsuario = new Usuario(
            String.valueOf(System.currentTimeMillis()), // ID único simple
            email, 
            "pass", // Hash real de la contraseña
            "CIUDADANO"
        );
        
        return usuarioDAO.registrarNuevoUsuario(nuevoUsuario);
    }
}