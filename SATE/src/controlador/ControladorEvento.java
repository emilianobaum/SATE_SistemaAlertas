package controlador;

import datos.ReporteDAO;
import modelo.Reporte;
import java.util.List;

/**
 * Controlador para manejar la lógica de reportes y eventos (Validación).
 */
public class ControladorEvento {
    private ReporteDAO reporteDAO;

    public ControladorEvento() {
        this.reporteDAO = new ReporteDAO();
    }

    public List<Reporte> obtenerReportesPendientes() {
        // Aquí podría haber lógica de filtrado o seguridad adicional.
        return reporteDAO.obtenerReportesPendientes();
    }

    public boolean aprobarReporte(String idReporte) {
        // Lógica de negocio: Validar que el reporte exista y cambiar su estado.
        boolean resultado = reporteDAO.actualizarEstadoReporte(idReporte, "APROBADO");
        
        // En una aplicación real, se activaría el proceso de creación de EVENTO y notificaciones GIS.
        if (resultado) {
            System.out.println("CONTROLADOR: Reporte " + idReporte + " aprobado y evento creado.");
        }
        return resultado;
    }

    public boolean rechazarReporte(String idReporte) {
        // Lógica de negocio: Descartar el reporte.
        return reporteDAO.actualizarEstadoReporte(idReporte, "RECHAZADO");
    }
}