module SATE {
    // 1. Requerimientos de JavaFX
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.web;     // WebView del Mapa
    requires javafx.base;    // SimpleStringProperty

    // 2. Requerimientos de Java
    requires java.sql;       // Para JDBC
    requires java.logging;   // Para el sistema de Logs

    // --- APERTURA Y EXPORTACIÓN DE PAQUETES ---
    
    // Exporta la UI para que la JVM pueda iniciarla
    exports ui;
    
    // Exporta los controladores para que la UI pueda llamarlos
    exports controlador;
    // Exporta los modelos para que la UI pueda llamarlos
    //exports modelo;

    // Abre el Modelo para que la TableView de JavaFX (javafx.base) 
    // pueda acceder a los atributos (SimpleStringProperty) usando reflexión.
    opens modelo to javafx.base;
    
    // Abre la UI para FXML (si se usa en el futuro)
    opens ui to javafx.fxml;
    // Abre controlador si usas FXML con controladores
    opens controlador to javafx.fxml;
}