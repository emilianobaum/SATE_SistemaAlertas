package modelo;

import javafx.beans.property.SimpleStringProperty; // Necesario para TableView

/**
 * Entidad de dominio: Reporte (usado para reportes manuales pendientes)
 */
public class Reporte {
    // Propiedades de JavaFX para que la TableView funcione sin llamadas directas a getters
    private final SimpleStringProperty id;
    private final SimpleStringProperty tipo;
    private final SimpleStringProperty fecha;
    private final SimpleStringProperty descripcion;
    private String coordenadasWKT;
    private String estado; // Pendiente, Aprobado, Rechazado

    public Reporte(String id, String tipo, String fecha, String descripcion, String coordenadasWKT, String estado) {
        this.id = new SimpleStringProperty(id);
        this.tipo = new SimpleStringProperty(tipo);
        this.fecha = new SimpleStringProperty(fecha);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.coordenadasWKT = coordenadasWKT;
        this.estado = estado;
    }

    // Getters requeridos por PropertyValueFactory de JavaFX
    public String getId() { return id.get(); }
    public String getTipo() { return tipo.get(); }
    public String getFecha() { return fecha.get(); }
    public String getDescripcion() { return descripcion.get(); }
    
    // Getters para el controlador
    public String getCoordenadasWKT() { return coordenadasWKT; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}