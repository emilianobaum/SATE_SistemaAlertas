package controlador;

import modelo.NOAAHotspot;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controlador para obtener datos REALES de focos de calor de NOAA/NASA
 * Usa el servicio FIRMS (Fire Information for Resource Management System)
 */
public class ControladorNOAA {
    
    private static final String FIRMS_API_URL = 
        "https://firms.modaps.eosdis.nasa.gov/api/area/csv";
    
    // 🔥 CLAVE API - necesitas registrarte en https://firms.modaps.eosdis.nasa.gov/api/
    private static final String API_KEY = "0520efc72bf80c2a810a6aed5627b7a1"; // Reemplaza con tu API key
    
    /**
     * Obtiene focos de calor REALES de NASA FIRMS
     */
    public List<NOAAHotspot> obtenerFocosCalorReales() {
        List<NOAAHotspot> hotspots = new ArrayList<>();

        try {
            // Área ampliada: Sudamérica
            double latitudSur = -55.0;
            double latitudNorte = 15.0;
            double longitudOeste = -85.0;
            double longitudEste = -30.0;

            // Parámetros para la API
            String fuente = "VIIRS_NOAA21_NRT";
            String dias = "2";
            // /api/area/csv/[MAP_KEY]/[SOURCE]/[AREA_COORDINATES]/[DAY_RANGE]/[DATE]

            String urlString = String.format(
                "%s/%s/%s/%s,%s,%s,%s/%s",
                FIRMS_API_URL, API_KEY, fuente, 
                latitudSur, longitudOeste, latitudNorte, longitudEste,dias 
            );

            System.out.println("🔍 Consultando NASA FIRMS API...");
            System.out.println("URL: " + urlString);

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);

            int responseCode = connection.getResponseCode();
            System.out.println("Código de respuesta: " + responseCode);

            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

                String line;
                boolean firstLine = true;
                int count = 0;

                while ((line = reader.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        continue; // Saltar cabecera
                    }

                    NOAAHotspot hotspot = parsearLineaCSV(line);
                    if (hotspot != null) {
                        hotspots.add(hotspot);
                        count++;
                    }
                }

                reader.close();
                System.out.println("✅ " + hotspots.size() + " focos reales obtenidos de NASA FIRMS");

            } else {
                System.err.println("❌ Error en API FIRMS. Código: " + responseCode);
                // Leer el mensaje de error si lo hay
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    System.err.println("Mensaje de error: " + errorLine);
                }
                errorReader.close();
                // Fallback a datos de ejemplo
            }

        } catch (Exception e) {
            System.err.println("❌ Error obteniendo datos reales: " + e.getMessage());
            e.printStackTrace();
        }

        return hotspots;
    }
    
    /**
     * Parsea una línea del CSV de NASA FIRMS
     * Formato esperado: latitude,longitude,bright_ti4,acq_date,acq_time,satellite,confidence
     */
    private NOAAHotspot parsearLineaCSV(String linea) {
        try {
            String[] campos = linea.split(",");
            
            if (campos.length >= 7) {
                double latitud = Double.parseDouble(campos[0]);
                double longitud = Double.parseDouble(campos[1]);
                double temperatura = Double.parseDouble(campos[2]); // bright_ti4 en Kelvin
                String fecha = campos[5] + " " + campos[6]; // acq_date + acq_time
                String satelite = campos[7];
                double confianza = parsearConfianza(campos[9]);
                
                return new NOAAHotspot(latitud, longitud, temperatura, fecha, confianza, satelite);
            }
        } catch (Exception e) {
            System.err.println("Error parseando línea: " + linea + " - " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Convierte la confianza de texto a número (0-1)
     * NASA FIRMS usa: low, nominal, high
     */
    private double parsearConfianza(String confianzaStr) {
        switch (confianzaStr.toLowerCase()) {
            case "high": return 0.9;
            case "nominal": return 0.7;
            case "low": return 0.5;
            default: return 0.6;
        }
    }
}