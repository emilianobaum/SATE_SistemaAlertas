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
    
    // Setters
    //public void setId(String id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRol(String rol) { this.rol = rol; }
}