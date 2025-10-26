package datos;

import modelo.Reporte;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Reporte.
 */
public class ReporteDAO {
    
    // Simulación de una lista de reportes en estado PENDIENTE
    public List<Reporte> obtenerReportesPendientes() {
        List<Reporte> pendientes = new ArrayList<>();
        pendientes.add(new Reporte("101", "Incendio", "2025-10-24 10:30", "Foco de calor en área residencial.", "POINT(-64.1 -31.4)", "PENDIENTE"));
        pendientes.add(new Reporte("102", "Inundación", "2025-10-24 14:15", "Desborde de río menor.", "POINT(-64.2 -31.5)", "PENDIENTE"));
        pendientes.add(new Reporte("103", "Climático", "2025-10-25 08:00", "Tormenta de granizo en zona sur.", "POINT(-64.3 -31.6)", "PENDIENTE"));
        return pendientes;
    }

    public boolean actualizarEstadoReporte(String idReporte, String nuevoEstado) {
        // En una aplicación real, aquí se ejecutaría un UPDATE SQL.
        System.out.println("DAO: Reporte " + idReporte + " actualizado a estado: " + nuevoEstado);
        return true;
    }
}