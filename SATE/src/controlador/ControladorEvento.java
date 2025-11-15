package controlador;

import datos.ReporteDAO;
import datos.EventoDAO; 
import modelo.Reporte;
import modelo.EntidadEvento; 
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList; 
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controlador para manejar la lógica de reportes y eventos.
 */
public class ControladorEvento {
    private ReporteDAO reporteDAO;
    private EventoDAO eventoDAO; 
    private static final Logger LOGGER = Logger.getLogger(ControladorEvento.class.getName());
    
    // Formato de fecha esperado de la UI (ReporteManualScreenFX)
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ControladorEvento() {
        this.reporteDAO = new ReporteDAO();
        this.eventoDAO = new EventoDAO(); 
    }

    public List<Reporte> obtenerReportesPendientes() throws Exception {
        try {
            return reporteDAO.obtenerReportesPendientes();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener la lista de reportes pendientes.", e);
            throw new Exception("Error de sistema al cargar reportes pendientes.", e); 
        }
    }
    
    public List<Reporte> obtenerReportesAprobados() throws Exception {
        try {
            return reporteDAO.obtenerReportesAprobados();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener la lista de reportes aprobados.", e);
            throw new Exception("Error de sistema al cargar reportes aprobados.", e); 
        }
    }

    /**
     * Método auxiliar para crear un EntidadEvento y persistirlo.
     */
    private boolean crearEventoAPartirDeReporte(Reporte reporte) throws SQLException, Exception {
        String nuevoIdEvento = null; 
        
        try {
            // Convertir el String de la fecha del Reporte
            LocalDateTime fechaEvento = LocalDateTime.parse(reporte.getFecha(), DATE_TIME_FORMATTER);

            EntidadEvento nuevoEvento = new EntidadEvento(
                null, 
                reporte.getTipo(), 
                fechaEvento, 
                reporte.getDescripcion(), 
                reporte.getCoordenadasWKT(),
                reporte.getEstado()
            );

            return eventoDAO.insertarEvento(nuevoEvento);

        } catch (java.time.format.DateTimeParseException e) {
             LOGGER.log(Level.SEVERE, "Error de formato de fecha en reporte " + reporte.getId() + ". Se esperaba yyyy-MM-dd HH:mm:ss", e);
             throw new Exception("Error al parsear la fecha del reporte.", e);
        }
    }

    /**
     * Aprueba un reporte, marcándolo como 'APROBADO' y creando el EntidadEvento asociado.
     * CORREGIDO: Acepta el objeto Reporte completo.
     */
    public boolean aprobarReporte(Reporte reporte) throws Exception {
        try {
            String idReporte = reporte.getId();
            
            boolean reporteActualizado = reporteDAO.actualizarEstadoReporte(idReporte, "APROBADO");
            
            if (reporteActualizado) {
                // (Opcional: Descomentar si la aprobación debe crear un Evento)
                // boolean eventoCreado = crearEventoAPartirDeReporte(reporte);
                // if (eventoCreado) { ... }
                
                LOGGER.log(Level.INFO, "Reporte " + idReporte + " aprobado.");
                return true;
            } else {
                LOGGER.log(Level.WARNING, "Error al aprobar reporte " + idReporte + " (no se pudo actualizar el estado).");
                return false;
            }
        } catch (SQLException e) {
             LOGGER.log(Level.SEVERE, "Error de DB al intentar aprobar el reporte: " + reporte.getId(), e);
            throw new Exception("Error de sistema al aprobar reporte.", e); 
        }
    }

    public boolean rechazarReporte(String idReporte) throws Exception {
        try {
            return reporteDAO.actualizarEstadoReporte(idReporte, "RECHAZADO");
        } catch (SQLException e) {
             LOGGER.log(Level.SEVERE, "Error al intentar rechazar el reporte: " + idReporte, e);
            throw new Exception("Error de sistema al rechazar reporte.", e); 
        }
    }
    
    /**
     * Inserta un nuevo Reporte en la base de datos (Reporte inicial enviado por el Ciudadano).
     */
    public boolean crearReporte(Reporte nuevoReporte) throws Exception {
        try {
            boolean exito = reporteDAO.insertarReporte(nuevoReporte); 
            
            if (exito) {
                LOGGER.log(Level.INFO, "Nuevo Reporte " + nuevoReporte.getId() + " creado en estado PENDIENTE.");
            }
            return exito;
        } catch (SQLException e) {
             LOGGER.log(Level.SEVERE, "Error de DB al intentar crear un nuevo reporte.", e);
            throw new Exception("Error de sistema al crear reporte.", e); 
        }
    }
}