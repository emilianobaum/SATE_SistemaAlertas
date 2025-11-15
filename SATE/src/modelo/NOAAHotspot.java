package modelo;

/**
 * Clase para representar un foco de calor de NOAA
 */
public class NOAAHotspot {
    private double latitud;
    private double longitud;
    private double temperatura;
    private String fecha;
    private double confianza;
    private String satelite; // 🔥 NUEVO: nombre del satélite

    public NOAAHotspot(double latitud, double longitud, double temperatura, 
                      String fecha, double confianza, String satelite) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.temperatura = temperatura;
        this.fecha = fecha;
        this.confianza = confianza;
        this.satelite = satelite;
    }

    // Getters
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public double getTemperatura() { return temperatura; }
    public String getFecha() { return fecha; }
    public double getConfianza() { return confianza; }
    public String getSatelite() { return satelite; }
    
    // Método para generar WKT
    public String toWKT() {
        return "POINT(" + longitud + " " + latitud + ")";
    }
    
    // 🔥 Método para generar descripción completa
    public String getDescripcionCompleta() {
        return String.format("Satélite: %s | Temp: %.1fK | Confianza: %.0f%% | Coord: %.4f, %.4f",
                           satelite, temperatura, confianza * 100, latitud, longitud);
    }
}