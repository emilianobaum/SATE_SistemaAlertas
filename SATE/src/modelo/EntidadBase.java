package modelo;

/**
 * Clase abstracta que representa la base de cualquier entidad persistente en el sistema.
 * Aplica el principio de Herencia y Abstracción (no puede ser instanciada).
 * Todos los objetos persistentes deben tener un identificador.
 */
public abstract class EntidadBase {
    // Uso de 'protected' para permitir acceso directo a las clases hijas (L3.pdf)
    protected String id;

    // Constructor para inicializar la propiedad de la clase base.
    public EntidadBase(String id) {
        this.id = id;
    }

    // Getter (Encapsulamiento)
    public String getId() {
        return id;
    }
    
    // Setter (Encapsulamiento)
    public void setId(String id) {
        this.id = id;
    }
}