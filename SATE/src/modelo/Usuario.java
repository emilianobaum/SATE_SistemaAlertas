package modelo;

/**
 * Entidad de dominio: Usuario
 */
public class Usuario {
    private String id;
    private String email;
    private String passwordHash;
    private String rol; // Ej: ADMINISTRADOR, VALIDADOR, CIUDADANO

    public Usuario(String id, String email, String passwordHash, String rol) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    // Getters
    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getRol() { return rol; }
    
    // Setters (omitiendo por simplicidad)
}