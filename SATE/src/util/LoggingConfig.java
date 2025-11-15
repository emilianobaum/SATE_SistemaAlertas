package util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Configura el sistema de logging para toda la aplicación SATE.
 * Reemplaza System.out.println por un log en archivo.
 */
public class LoggingConfig {

    private static final Logger rootLogger = Logger.getLogger("");
    private static FileHandler fileHandler;

    /**
     * Configura el logger para que escriba a "sate_app.log".
     * Debe llamarse una sola vez al inicio de main().
     */
    public static void setup() {
        try {
            // Configurar el FileHandler (true = modo append)
            fileHandler = new FileHandler("sate_app.log", true);
            
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            
            // Añadir el handler al logger raíz
            rootLogger.addHandler(fileHandler);
            
            // Establecer el nivel de log (INFO captura todo lo importante)
            rootLogger.setLevel(Level.INFO);
            
            // Desactivar el logging de la consola (opcional)
            // rootLogger.setUseParentHandlers(false); 
            
            rootLogger.info("Sistema de Logging de SATE inicializado.");
            
        } catch (IOException e) {
            System.err.println("Error: No se pudo inicializar el FileHandler de Logging.");
            e.printStackTrace();
        }
    }
}