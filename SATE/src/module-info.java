module SATE {
    // Requerimientos básicos de JavaFX
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml; 
    
    // Requerimientos de SQL/JDBC para la capa de datos
    requires java.sql;
    
    // 1. Exporta la UI para que la JVM la inicie
    exports sate; 
    
    // 2. Exporta el Controlador para que la UI pueda llamarlo (el cliente)
    exports controlador;
    
    // 3. Abre el Modelo para que la TableView de JavaFX acceda a sus atributos internos (reflection)
    opens modelo to javafx.base; 
    
    // 4. Abre el paquete de UI si necesitas usar FXML (buena práctica)
    opens sate to javafx.fxml; 
}