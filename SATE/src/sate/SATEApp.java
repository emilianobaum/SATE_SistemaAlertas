package sate;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Clase principal que inicia la aplicación SATE. Contiene el método main para
 * el arranque de JavaFX.
 */
public class SATEApp extends Application {

	// 1. Punto de entrada principal para JavaFX
	public static void main(String[] args) {
		launch(args);
	}

	// 2. Método obligatorio que recibe el Stage inicial (la ventana)
	@Override
	public void start(Stage primaryStage) {
		// Inicia directamente la pantalla de Login
		new LoginScreenFX().start(primaryStage);
	}
}

// ----------------------------------------------------------------------------------
// PANTALLA 1: LOGIN (LoginScreenFX)
// ----------------------------------------------------------------------------------

class LoginScreenFX extends Application {

	@Override
	public void start(Stage stage) {
		stage.setTitle("SATE - Acceso al Sistema (Pruebas de Rol)");

		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(15);
		grid.setVgap(15);
		grid.setPadding(new Insets(25));

		Text sceneTitle = new Text("🔑 Login de Usuario");
		sceneTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		grid.add(sceneTitle, 0, 0, 2, 1);

		// Selector de Rol (NUEVO para pruebas)
		grid.add(new Label("Seleccionar Rol:"), 0, 1);
		ComboBox<String> roleSelector = new ComboBox<>();
		roleSelector.getItems().addAll("ADMINISTRADOR", "VALIDADOR", "CIUDADANO");
		roleSelector.getSelectionModel().select("CIUDADANO"); // Rol por defecto
		grid.add(roleSelector, 1, 1);


		grid.add(new Label("Usuario (Email):"), 0, 2);
		TextField emailField = new TextField();
		emailField.setPromptText("ejemplo@sate.com");
		grid.add(emailField, 1, 2);

		grid.add(new Label("Contraseña:"), 0, 3);
		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Ingrese su clave");
		grid.add(passwordField, 1, 3);

		Text actionTarget = new Text();
		grid.add(actionTarget, 1, 4);

		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.BOTTOM_RIGHT);

		Button loginButton = new Button("Iniciar Sesión");
		Button registerButton = new Button("Registrarse");

		buttonBox.getChildren().addAll(registerButton, loginButton);
		grid.add(buttonBox, 1, 5); // Desplazado a la fila 5

		loginButton.setOnAction(e -> {
			// Usamos el rol seleccionado para la simulación
			String selectedRole = roleSelector.getSelectionModel().getSelectedItem();

			// Lógica real de autenticación (usando ControladorUsuario)
			// ... (Se asume que la autenticación es exitosa para fines de demostración de UI)

			actionTarget.setText("Iniciando sesión como " + selectedRole + "...");
			stage.close(); 

			// Pasa el rol seleccionado directamente al Dashboard
			new MainDashboardFX(selectedRole).start(new Stage());
		});

		registerButton.setOnAction(e -> {
			new RegisterScreenFX().start(new Stage());
		});

		stage.setScene(new Scene(grid, 450, 350)); // Aumentamos el alto
		stage.show();
	}
}

// ----------------------------------------------------------------------------------
// PANTALLA 2: REGISTRO (RegisterScreenFX)
// ----------------------------------------------------------------------------------

class RegisterScreenFX extends Application {

	@Override
	public void start(Stage stage) {
		stage.setTitle("SATE - Registro de Nuevo Usuario");
		GridPane grid = new GridPane();
		grid.setVgap(10);
		grid.setHgap(10);
		grid.setPadding(new Insets(20));

		Text title = new Text("SATE - Formulario de Registro");
		title.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 16));
		grid.add(title, 0, 0, 2, 1);

		int row = 1;
		grid.add(new Label("👤 Nombre Completo:"), 0, row);
		grid.add(new TextField(), 1, row++);

		grid.add(new Label("✉️ Email (Usuario):"), 0, row);
		grid.add(new TextField(), 1, row++);

		grid.add(new Label("🔑 Contraseña:"), 0, row);
		grid.add(new PasswordField(), 1, row++);

		grid.add(new Label("🔁 Repetir Contraseña:"), 0, row);
		grid.add(new PasswordField(), 1, row++);

		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.CENTER_RIGHT);

		Button createButton = new Button("Crear Cuenta");
		Button cancelButton = new Button("Cancelar / Volver");
		cancelButton.setOnAction(e -> stage.close());

		buttonBox.getChildren().addAll(cancelButton, createButton);

		grid.add(buttonBox, 1, row);

		stage.setScene(new Scene(grid, 450, 300));
		stage.show();
	}
}

// ----------------------------------------------------------------------------------
// PANTALLA 3: REGISTRO DE EVENTO (ReporteManualScreenFX)
// ----------------------------------------------------------------------------------

class ReporteManualScreenFX extends Application {
	private TextField latitudTextField;
	private TextField longitudTextField;

	@Override
	public void start(Stage stage) {
		stage.setTitle("SATE - Reporte de Incidencia");

		TabPane tabPane = new TabPane();
		tabPane.getTabs().addAll(crearTabDetalles(), crearTabUbicacion());

		HBox footer = new HBox(10);
		footer.setAlignment(Pos.CENTER_RIGHT);
		footer.setPadding(new Insets(10));

		Button sendButton = new Button("Enviar Reporte");
		sendButton.setStyle("-fx-font-weight: bold;");

		sendButton.setOnAction(e -> {
			String wktString = generarWKT();
			Alert alert = new Alert(Alert.AlertType.INFORMATION,
					"Reporte Enviado.\nCoordenadas WKT generadas:\n" + wktString);
			alert.setTitle("Éxito en el Envío");
			alert.setHeaderText(null);
			alert.showAndWait();
		});

		footer.getChildren().add(sendButton);

		BorderPane root = new BorderPane();
		root.setCenter(tabPane);
		root.setBottom(footer);

		stage.setScene(new Scene(root, 600, 450));
		stage.show();
	}

	private Tab crearTabDetalles() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(15));

		grid.add(new Label("Tipo de Evento:"), 0, 0);
		ComboBox<String> tipos = new ComboBox<>();
		tipos.getItems().addAll("Inundación", "Incendio", "Climático", "Otro");
		tipos.getSelectionModel().selectFirst();
		grid.add(tipos, 1, 0);

		grid.add(new Label("Descripción Breve:"), 0, 1, 2, 1);
		TextArea descripcionTextArea = new TextArea();
		descripcionTextArea.setWrapText(true);
		descripcionTextArea.setPrefRowCount(5);
		grid.add(descripcionTextArea, 0, 2, 2, 1);

		GridPane.setVgrow(descripcionTextArea, Priority.ALWAYS);

		return new Tab("Detalles del Reporte", grid);
	}

	private Tab crearTabUbicacion() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(15));

		Label formatInfo = new Label("Formato Esperado: Coordenadas Decimales (Ej: -34.6037)");
		formatInfo.setStyle("-fx-text-fill: blue; -fx-font-style: italic;");
		grid.add(formatInfo, 0, 0, 2, 1);

		latitudTextField = new TextField();
		latitudTextField.setPromptText("Latitud");
		grid.add(new Label("Latitud:"), 0, 1);
		grid.add(latitudTextField, 1, 1);

		longitudTextField = new TextField();
		longitudTextField.setPromptText("Longitud");
		grid.add(new Label("Longitud:"), 0, 2);
		grid.add(longitudTextField, 1, 2);

		Button locationButton = new Button("Usar Ubicación Actual (Simulado)");
		grid.add(locationButton, 0, 3, 2, 1);

		return new Tab("Ubicación", grid);
	}

	private String generarWKT() {
		String lat = latitudTextField.getText().trim();
		String lon = longitudTextField.getText().trim();

		if (lat.isEmpty() || lon.isEmpty()) {
			return "ERROR: Latitud y Longitud son obligatorios.";
		}

		return "POINT(" + lon + " " + lat + ")";
	}
}

// ----------------------------------------------------------------------------------
// PANTALLA 4: VISUALIZAR EVENTOS (MainDashboardFX)
// ----------------------------------------------------------------------------------

class MainDashboardFX extends Application {
	private String userRole;

	public MainDashboardFX(String role) {
		this.userRole = role;
	}

	@Override
	public void start(Stage stage) {
		stage.setTitle("SATE - Monitor Principal (" + userRole + ")");

		BorderPane root = new BorderPane();

		// 1. TOP: Menú de Navegación (Función modificada)
		MenuBar menuBar = crearMenuBar(stage);
		root.setTop(menuBar);

		// 2. CENTER: Contenido Principal (Mapa + Reportes Activos)
		SplitPane centralSplit = new SplitPane();
		centralSplit.setPadding(new Insets(10, 10, 0, 10));

		VBox mapPanel = crearMapPanel();
		VBox activeReportsPanel = crearActiveReportsPanel();

		centralSplit.getItems().addAll(mapPanel, activeReportsPanel);
		centralSplit.setDividerPositions(0.75);

		root.setCenter(centralSplit);

		// 3. BOTTOM: Barra de Estado
		Label statusBar = new Label(
				"Estado: Conectado como " + userRole + ". Última actualización: " + new java.util.Date());
		statusBar.setPadding(new Insets(5));
		statusBar.setStyle("-fx-border-color: lightgray; -fx-border-width: 1 0 0 0;");
		root.setBottom(statusBar);

		stage.setScene(new Scene(root, 1200, 800));
		stage.setMaximized(true);
		stage.show();
	}

	/** Crea el menú superior de navegación con las opciones solicitadas. */
	private MenuBar crearMenuBar(Stage stage) {
        MenuBar menuBar = new MenuBar();
        
        // --- 1. Menú ADMINISTRACIÓN (Gestionar Usuarios) ---
        Menu adminMenu = new Menu("Administración");
        
        // Alta de Usuario: Abre la ventana de REGISTRO (RegisterScreenFX)
        MenuItem addUser = new MenuItem("Agregar Usuario");
        addUser.setOnAction(e -> new RegisterScreenFX().start(new Stage())); // <-- CONEXIÓN AL REGISTRO
        
        // Modificación/Baja: Abre la ventana de GESTIÓN (UserManagementScreenFX)
        MenuItem modifyUser = new MenuItem("Modificar Usuario");
        modifyUser.setOnAction(e -> new UserManagementScreenFX().start(new Stage())); // <-- CONEXIÓN A GESTIÓN
        
        MenuItem removeUser = new MenuItem("Baja de Usuario");
        removeUser.setOnAction(e -> new UserManagementScreenFX().start(new Stage())); // <-- CONEXIÓN A GESTIÓN
        
        
        // REGLAS DE VISIBILIDAD (Asegúrate de que el validador y admin puedan ver la gestión)
        if (this.userRole.equals("ADMINISTRADOR")) {
            // Admin ve Alta, Modificar y Baja (todo)
            adminMenu.getItems().addAll(addUser, modifyUser, removeUser, new SeparatorMenuItem());
        } 
        else if (this.userRole.equals("VALIDADOR")) {
            // Validador ve Modificar y Baja (asumimos que valida cuentas existentes)
            adminMenu.getItems().addAll(modifyUser, removeUser, new SeparatorMenuItem());
        }
        
        // Opción de Salir (Todos los roles la tienen)
        MenuItem exitItem = new MenuItem("Cerrar Sesión");
        exitItem.setOnAction(e -> {
            stage.close();
            new LoginScreenFX().start(new Stage());
        });
        adminMenu.getItems().add(exitItem);

        // El resto de los menús (Validación, Reporte, Ayuda) permanece igual...
        
        Menu validationMenu = new Menu("Validación");
        MenuItem reviewReports = new MenuItem("Revisar Reportes Pendientes");
        reviewReports.setOnAction(e -> new ValidationScreenFX().start(new Stage()));
        if (this.userRole.equals("ADMINISTRADOR") || this.userRole.equals("VALIDADOR")) {
            validationMenu.getItems().add(reviewReports);
        }

        Menu reportMenu = new Menu("Reporte");
        MenuItem newReport = new MenuItem("Nuevo Reporte Manual");
        newReport.setOnAction(e -> new ReporteManualScreenFX().start(new Stage()));
        reportMenu.getItems().add(newReport);

        Menu helpMenu = new Menu("Ayuda");
        MenuItem showHelp = new MenuItem("Ver Instrucciones");
        showHelp.setOnAction(e -> mostrarPopupAyuda());
        helpMenu.getItems().add(showHelp);
        
        menuBar.getMenus().addAll(
            adminMenu,
            reportMenu,
            validationMenu,
            helpMenu
        );
        return menuBar;
    }

	/** Muestra un popup simple con indicaciones básicas. */
	private void mostrarPopupAyuda() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Ayuda e Indicaciones Básicas del Sistema SATE");
		alert.setHeaderText("Operaciones y Roles del Sistema");

		String content = "BIENVENIDO A SATE.\n\n" + "Roles:\n"
				+ "  • CIUDADANO: Puede enviar reportes manuales y visualizar eventos activos en el mapa.\n"
				+ "  • VALIDADOR: Puede enviar reportes, visualizar eventos y, además, revisar reportes pendientes en el menú VALIDACIÓN.\n"
				+ "  • ADMINISTRADOR: Acceso total, incluyendo las opciones del menú ADMINISTRACIÓN para gestionar usuarios, además de todas las funciones de Validador.\n\n"
				+ "Navegación:\n"
				+ "  • REPORTE: Use 'Nuevo Reporte Manual' para registrar una incidencia con coordenadas WKT.\n"
				+ "  • VALIDACIÓN: Use 'Revisar Reportes Pendientes' para aprobar o rechazar reportes de ciudadanos.\n"
				+ "  • ADMINISTRACIÓN: Gestiona el acceso de los usuarios del sistema (solo Administrador).";

		alert.setContentText(content);
		alert.getDialogPane().setPrefWidth(550);
		alert.showAndWait();
	}

	/** Función de utilidad para mostrar que una acción fue invocada */
	private void mostrarAlertaAccion(String titulo, String mensaje) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(titulo);
		alert.setHeaderText(null);
		alert.setContentText("Acción invocada: " + mensaje);
		alert.showAndWait();
	}

	/** Simula el Panel GIS central */
	private VBox crearMapPanel() {
		VBox mapPanel = new VBox(10);
		mapPanel.setAlignment(Pos.CENTER);
		mapPanel.setStyle("-fx-background-color: #E0E0E0;");

		Text title = new Text("🗺️ Mapa GIS y Visualización de Eventos (75% Ancho)");
		title.setFont(Font.font("Arial", FontWeight.BOLD, 18));

		StackPane mapPlaceholder = new StackPane();
		mapPlaceholder.setMinHeight(500);
		mapPlaceholder.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-background-color: #D3D3D3;");
		mapPlaceholder.getChildren().add(new Label("Aquí se integraría el MapView (GIS)"));

		VBox.setVgrow(mapPlaceholder, Priority.ALWAYS);

		mapPanel.getChildren().addAll(title, mapPlaceholder);
		return mapPanel;
	}

	/** Simula la Barra Lateral de Reportes Activos */
	private VBox crearActiveReportsPanel() {
		VBox activeReportsPanel = new VBox(10);
		activeReportsPanel.setPadding(new Insets(10));
		activeReportsPanel
				.setStyle("-fx-border-color: #aaa; -fx-border-width: 0 0 0 1; -fx-background-color: #F8F8F8;");

		Text title = new Text("🚨 Reportes y Eventos Activos (25% Ancho)");
		title.setFont(Font.font("Arial", FontWeight.BOLD, 14));

		ListView<String> reportList = new ListView<>();
		reportList.getItems().addAll("E-001: Incendio (Alta)", "E-002: Inundación (Media)",
				"R-105: Reporte nuevo (Validar)", "E-003: Alerta climática (Baja)");
		VBox.setVgrow(reportList, Priority.ALWAYS);

		Button detailsButton = new Button("Ver Detalles");
		detailsButton.setMaxWidth(Double.MAX_VALUE);

		activeReportsPanel.getChildren().addAll(title, reportList, detailsButton);
		return activeReportsPanel;
	}
}

//----------------------------------------------------------------------------------
//PANTALLA 5: GESTION DE USUARIOS (MainDashboardFX)
//----------------------------------------------------------------------------------

class UserManagementScreenFX extends Application {

    // Modelo simple para la tabla de usuarios
    public static class UserSimulado {
        private final SimpleStringProperty id;
        private final SimpleStringProperty email;
        private final SimpleStringProperty rol;

        public UserSimulado(String id, String email, String rol) {
            this.id = new SimpleStringProperty(id);
            this.email = new SimpleStringProperty(email);
            this.rol = new SimpleStringProperty(rol);
        }
        
        public String getId() { return id.get(); }
        public String getEmail() { return email.get(); }
        public String getRol() { return rol.get(); }
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("SATE - Gestión de Usuarios Activos");
        stage.setWidth(800);
        stage.setHeight(550);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        
        Text title = new Text("👤 Lista de Usuarios Activos");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        root.setTop(title);

        // --- Tabla de Usuarios ---
        TableView<UserSimulado> userTable = crearTablaUsuarios();
        root.setCenter(userTable);

        // --- Panel de Botones de Acción ---
        HBox actionButtons = new HBox(15);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);
        actionButtons.setPadding(new Insets(15, 0, 0, 0));

        Button modifyButton = new Button("Modificar Usuario Seleccionado");
        modifyButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        
        Button deleteButton = new Button("Dar de Baja");
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        // Lógica de acciones simuladas
        modifyButton.setOnAction(e -> handleModify(userTable.getSelectionModel().getSelectedItem()));
        deleteButton.setOnAction(e -> handleDelete(userTable.getSelectionModel().getSelectedItem()));

        actionButtons.getChildren().addAll(modifyButton, deleteButton);
        root.setBottom(actionButtons);

        stage.setScene(new Scene(root));
        stage.show();
    }
    
    /** Crea la TableView y la puebla con datos simulados. */
    private TableView<UserSimulado> crearTablaUsuarios() {
        TableView<UserSimulado> table = new TableView<>();

        TableColumn<UserSimulado, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(80);
        
        TableColumn<UserSimulado, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(300);
        
        TableColumn<UserSimulado, String> rolCol = new TableColumn<>("Rol");
        rolCol.setCellValueFactory(new PropertyValueFactory<>("rol"));

        table.getColumns().addAll(idCol, emailCol, rolCol);

        // Datos simulados (deberían venir de ControladorUsuario)
        table.getItems().addAll(
            new UserSimulado("1", "admin@sate.com", "ADMINISTRADOR"),
            new UserSimulado("2", "validador@sate.com", "VALIDADOR"),
            new UserSimulado("3", "ciudadano@sate.com", "CIUDADANO"),
            new UserSimulado("4", "juan.perez@sate.com", "CIUDADANO")
        );
        return table;
    }
    
    /** Maneja la modificación de un usuario. */
    private void handleModify(UserSimulado user) {
        if (user == null) {
            new Alert(Alert.AlertType.WARNING, "Seleccione un usuario para modificar.").showAndWait();
            return;
        }
        // En una app real, abriríamos la ventana de registro/edición con los datos del usuario cargados.
        new Alert(Alert.AlertType.INFORMATION, "Simulación: Abriendo formulario para modificar a " + user.getEmail()).showAndWait();
    }
    
    /** Maneja la baja de un usuario. */
    private void handleDelete(UserSimulado user) {
        if (user == null) {
            new Alert(Alert.AlertType.WARNING, "Seleccione un usuario para dar de baja.").showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Está seguro de dar de baja al usuario " + user.getEmail() + "?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Llamada a ControladorUsuario.darBajaUsuario(user.getId());
                new Alert(Alert.AlertType.INFORMATION, "Usuario " + user.getEmail() + " dado de baja (simulado).").showAndWait();
                // Aquí se refrescaría la tabla
            }
        });
    }
}
// ----------------------------------------------------------------------------------
// PANTALLA 5: VALIDACIÓN DE REPORTES (ValidationScreenFX) - El código solicitado
// ----------------------------------------------------------------------------------

class ValidationScreenFX extends Application {

	// Clase de modelo simple para la tabla (mockeando el Reporte real)
	public static class ReporteSimulado {
		private final SimpleStringProperty id;
		private final SimpleStringProperty tipo;
		private final SimpleStringProperty fecha;
		private final SimpleStringProperty descripcion;

		public ReporteSimulado(String id, String tipo, String fecha, String descripcion) {
			this.id = new SimpleStringProperty(id);
			this.tipo = new SimpleStringProperty(tipo);
			this.fecha = new SimpleStringProperty(fecha);
			this.descripcion = new SimpleStringProperty(descripcion);
		}

		public String getId() {
			return id.get();
		}

		public String getTipo() {
			return tipo.get();
		}

		public String getFecha() {
			return fecha.get();
		}

		public String getDescripcion() {
			return descripcion.get();
		}
	}

	@Override
	public void start(Stage stage) {
		stage.setTitle("SATE - Validación de Reportes Pendientes");
		stage.setWidth(1000);
		stage.setHeight(650);

		BorderPane root = new BorderPane();
		root.setPadding(new Insets(10));

		SplitPane splitPane = new SplitPane();

		// --- 1. PANEL IZQUIERDO: Tabla de Reportes Pendientes ---
		VBox leftPanel = new VBox(10);
		leftPanel.setPadding(new Insets(10));

		Text titleLeft = new Text("📋 Reportes Manuales Pendientes");
		titleLeft.setFont(Font.font("Arial", FontWeight.BOLD, 14));

		TableView<ReporteSimulado> reporteTable = crearTablaReportes();

		leftPanel.getChildren().addAll(titleLeft, reporteTable);
		VBox.setVgrow(reporteTable, Priority.ALWAYS);

		// --- 2. PANEL DERECHO: Detalles del Reporte Seleccionado ---
		VBox rightPanel = new VBox(10);
		rightPanel.setPadding(new Insets(10));

		Text titleRight = new Text("🔍 Detalles del Reporte Seleccionado");
		titleRight.setFont(Font.font("Arial", FontWeight.BOLD, 14));

		GridPane detailGrid = crearPanelDetalles();
		HBox actionButtons = crearPanelAcciones(stage);

		rightPanel.getChildren().addAll(titleRight, detailGrid, new Separator(), actionButtons);

		// Conexión para actualizar los detalles al seleccionar una fila
		reporteTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				actualizarDetalles(newSelection, detailGrid);
			}
		});

		splitPane.getItems().addAll(leftPanel, rightPanel);
		splitPane.setDividerPositions(0.4);
		root.setCenter(splitPane);

		stage.setScene(new Scene(root));
		stage.show();
	}

	private TableView<ReporteSimulado> crearTablaReportes() {
		TableView<ReporteSimulado> table = new TableView<>();

		TableColumn<ReporteSimulado, String> idCol = new TableColumn<>("ID");
		idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

		TableColumn<ReporteSimulado, String> tipoCol = new TableColumn<>("Tipo");
		tipoCol.setCellValueFactory(new PropertyValueFactory<>("tipo"));

		TableColumn<ReporteSimulado, String> fechaCol = new TableColumn<>("Fecha");
		fechaCol.setCellValueFactory(new PropertyValueFactory<>("fecha"));

		table.getColumns().addAll(idCol, tipoCol, fechaCol);

		table.getItems().addAll(
				new ReporteSimulado("101", "Incendio", "2025-10-24 10:30", "Foco de calor en área residencial."),
				new ReporteSimulado("102", "Inundación", "2025-10-24 14:15",
						"Desborde de río menor por lluvias intensas."),
				new ReporteSimulado("103", "Climático", "2025-10-25 08:00",
						"Reporte de tormenta de granizo en zona sur."));
		return table;
	}

	private GridPane crearPanelDetalles() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(5));

		grid.add(new Label("ID del Reporte:"), 0, 0);
		grid.add(new Label("Tipo de Incidencia:"), 0, 1);
		grid.add(new Label("Fecha y Hora:"), 0, 2);
		grid.add(new Label("Coordenadas (WKT):"), 0, 3);
		grid.add(new Label("Descripción:"), 0, 4);

		// Contenido (placeholders) - Es importante mantener el orden para la
		// actualización
		grid.add(new Label("N/A"), 1, 0);
		grid.add(new Label("N/A"), 1, 1);
		grid.add(new Label("N/A"), 1, 2);
		grid.add(new Label("N/A"), 1, 3);

		TextArea descriptionArea = new TextArea("Seleccione un reporte de la izquierda.");
		descriptionArea.setEditable(false);
		descriptionArea.setWrapText(true);
		descriptionArea.setPrefRowCount(5);

		grid.add(descriptionArea, 1, 4);

		return grid;
	}

	private void actualizarDetalles(ReporteSimulado reporte, GridPane detailGrid) {
		// Se accede a los Labels y el TextArea por su posición en la lista de nodos del
		// GridPane

		// Actualiza ID (Posición 5, después de 5 pares Label/Label)
		((Label) detailGrid.getChildren().get(5)).setText(reporte.getId());
		// Actualiza Tipo (Posición 7)
		((Label) detailGrid.getChildren().get(7)).setText(reporte.getTipo());
		// Actualiza Fecha (Posición 9)
		((Label) detailGrid.getChildren().get(9)).setText(reporte.getFecha());
		// Simulación de coordenadas (Posición 11)
		((Label) detailGrid.getChildren().get(11)).setText("POINT(-64.1888 -31.4201)");

		// Actualiza el TextArea (Posición 13)
		TextArea descriptionArea = (TextArea) detailGrid.getChildren().get(13);
		descriptionArea.setText(reporte.getDescripcion());
	}

	private HBox crearPanelAcciones(Stage stage) {
		HBox buttonBox = new HBox(15);
		buttonBox.setAlignment(Pos.CENTER_RIGHT);

		Button approveButton = new Button("✅ Aprobar Evento");
		approveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");

		Button rejectButton = new Button("❌ Rechazar Reporte");
		rejectButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

		approveButton.setOnAction(e -> {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
					"¿Está seguro de aprobar este reporte y convertirlo en Evento oficial?");
			alert.showAndWait().ifPresent(response -> {
				if (response == ButtonType.OK) {
					new Alert(Alert.AlertType.INFORMATION, "Reporte APROBADO. El evento ha sido publicado.")
							.showAndWait();
					// Lógica para recargar la tabla o cerrar la ventana
				}
			});
		});

		rejectButton.setOnAction(e -> {
			new Alert(Alert.AlertType.INFORMATION, "Reporte RECHAZADO y eliminado de la lista de pendientes.")
					.showAndWait();
			// Lógica para recargar la tabla o cerrar la ventana
		});

		buttonBox.getChildren().addAll(rejectButton, approveButton);
		return buttonBox;
	}
}