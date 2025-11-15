package modelo;

import java.time.LocalDateTime;

/**
 * Entidad de dominio: Evento (se crea a partir de un Reporte APROBADO)
 */
public class EntidadEvento {
    private String id;
    private String tipo;
    private LocalDateTime fechaHora;
    private String descripcion;
    private String coordenadasWKT;
    private String estado;
    
    // Constructor corregido
    public EntidadEvento(String id, String tipo, LocalDateTime fechaHora,
    		String descripcion, String coordenadasWKT, String estado) {
        this.id = id;
        this.tipo = tipo;
        this.fechaHora = fechaHora;
        this.descripcion = descripcion;
        this.coordenadasWKT = coordenadasWKT;
        this.estado = estado;
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public String getTipo() { return tipo; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getDescripcion() { return descripcion; }
    public String getCoordenadasWKT() { return coordenadasWKT; }
    public String getEstado() { return estado; }
}