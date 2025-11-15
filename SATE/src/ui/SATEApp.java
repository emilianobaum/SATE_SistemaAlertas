package ui;

// Imports de la lógica de negocio
import datos.DatabaseInitializer;
import controlador.ControladorUsuario;
import controlador.ControladorEvento;
import controlador.ControladorNOAA;
import modelo.Usuario;
import modelo.Reporte;
import modelo.NOAAHotspot;
import util.LoggingConfig; // Importar el configurador de logs

// Imports de JavaFX
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Imports de Logging
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase principal que inicia la aplicación SATE.
 */
public class SATEApp extends Application {

	// Configurar Logger para esta clase
	private static final Logger LOGGER = Logger.getLogger(SATEApp.class.getName());

	public static void main(String[] args) {

		LoggingConfig.setup(); // Configurar el logging

		LOGGER.info("--- Inicio de la aplicación SATE ---");
		DatabaseInitializer.initialize();
		LOGGER.info("--- Inicialización de BBDD completada ---");

		// Prueba de Login en Consola
		ControladorUsuario ctrlUsuario = new ControladorUsuario();
		LOGGER.info("\n--- Prueba de Login (Test X Consola) ---");

		try {
			Usuario admin = ctrlUsuario.iniciarSesion("admin@sate.com", "c1lc5l1d4r1");
			if (admin != null) {
				LOGGER.info("Login de consola exitoso. Usuario: " + admin.getEmail() + ", Rol: " + admin.getRol());
			} else {
				LOGGER.warning("Login de consola fallido (credenciales incorrectas).");
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "ERROR CRÍTICO en prueba de login: " + e.getMessage(), e);
		}

		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		new LoginScreenFX().start(primaryStage);
	}
}

// ----------------------------------------------------------------------------------
// PANTALLA 1: LOGIN (LoginScreenFX)
// ----------------------------------------------------------------------------------

class LoginScreenFX extends Application {
	private ControladorUsuario controladorUsuario = new ControladorUsuario();
	private static final Logger LOGGER = Logger.getLogger(LoginScreenFX.class.getName());

	@Override
	public void start(Stage stage) {
		stage.setTitle("SATE - Acceso al Sistema");
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(15);
		grid.setVgap(15);
		grid.setPadding(new Insets(25));

		Text sceneTitle = new Text("🔑 Login de Usuario");
		sceneTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		grid.add(sceneTitle, 0, 0, 2, 1);

		grid.add(new Label("Usuario (Email):"), 0, 1);
		TextField emailField = new TextField("admin@sate.com");
		grid.add(emailField, 1, 1);

		// Auto completado a fines de testing
		grid.add(new Label("Contraseña:"), 0, 2);
		PasswordField passwordField = new PasswordField();
		passwordField.setText("c1lc5l1d4r1");
		grid.add(passwordField, 1, 2);

		Text actionTarget = new Text();
		actionTarget.setFill(Color.RED);
		grid.add(actionTarget, 1, 3);

		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
		Button loginButton = new Button("Iniciar Sesión");
		Button registerButton = new Button("Registrarse");
		buttonBox.getChildren().addAll(registerButton, loginButton);
		grid.add(buttonBox, 1, 4);

		loginButton.setOnAction(e -> {
			String email = emailField.getText();
			String password = passwordField.getText();

			try {
				Usuario usuario = controladorUsuario.iniciarSesion(email, password);

				if (usuario != null) {
					actionTarget.setText("Iniciando sesión como " + usuario.getRol() + "...");
					stage.close();
					new MainDashboardFX(usuario).start(new Stage());
				} else {
					actionTarget
							.setText("Error: Usuario o clave incorrectos.\nPor favor, intente de nuevo o regístrese.");
				}
			} catch (Exception ex) {
				actionTarget.setText("Error crítico de conexión a la BBDD.\nEl sistema no puede continuar.");
				LOGGER.log(Level.SEVERE, "Error de DB en LoginScreenFX", ex);
			}
		});

		registerButton.setOnAction(e -> {
			new RegisterScreenFX().start(new Stage());
		});

		stage.setScene(new Scene(grid, 450, 300));
		stage.show();
	}
}

// ----------------------------------------------------------------------------------
// PANTALLA 2: REGISTRO (RegisterScreenFX)
// ----------------------------------------------------------------------------------
class RegisterScreenFX extends Application {
	private ControladorUsuario controladorUsuario = new ControladorUsuario();
	private static final Logger LOGGER = Logger.getLogger(RegisterScreenFX.class.getName());

	@Override
	public void start(Stage stage) {
		stage.setTitle("SATE - Registro de Nuevo Usuario");
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setVgap(10);
		grid.setHgap(10);
		grid.setPadding(new Insets(20));

		Text title = new Text("SATE - Formulario de Registro");
		title.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 16));
		grid.add(title, 0, 0, 2, 1);

		int row = 1;

		grid.add(new Label("✉️ Email (Usuario):"), 0, row);
		TextField emailField = new TextField();
		grid.add(emailField, 1, row++);

		grid.add(new Label("🔑 Contraseña:"), 0, row);
		PasswordField passwordField = new PasswordField();
		grid.add(passwordField, 1, row++);

		grid.add(new Label("🔁 Repetir Contraseña:"), 0, row);
		PasswordField repeatPasswordField = new PasswordField();
		grid.add(repeatPasswordField, 1, row++);

		Text actionTarget = new Text();
		actionTarget.setFill(Color.RED);
		grid.add(actionTarget, 1, row++);

		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.CENTER_RIGHT);
		Button createButton = new Button("Crear Cuenta");
		Button cancelButton = new Button("Cancelar / Volver");
		cancelButton.setOnAction(e -> stage.close());
		buttonBox.getChildren().addAll(cancelButton, createButton);
		grid.add(buttonBox, 1, row);

		createButton.setOnAction(e -> {
			String email = emailField.getText();
			String pass = passwordField.getText();
			String repeatPass = repeatPasswordField.getText();

			if (email.trim().isEmpty() || pass.trim().isEmpty()) {
				actionTarget.setText("Email y contraseña son obligatorios.");
				return;
			}
			if (!pass.equals(repeatPass)) {
				actionTarget.setText("Las contraseñas no coinciden.");
				return;
			}

			try {
				boolean exito = controladorUsuario.registrarUsuario("Nuevo Usuario", email, pass);
				if (exito) {
					actionTarget.setFill(Color.GREEN);
					actionTarget.setText("¡Registro exitoso! Ya puede cerrar esta ventana.");
					createButton.setDisable(true);
				} else {
					actionTarget.setText("Error: El email ya está en uso.");
				}
			} catch (Exception ex) {
				actionTarget.setText("Error crítico de conexión a la BBDD.");
				LOGGER.log(Level.SEVERE, "Error de DB en RegisterScreenFX", ex);
			}
		});

		stage.setScene(new Scene(grid, 450, 350));
		stage.show();
	}
}

// ----------------------------------------------------------------------------------
// PANTALLA 3: REGISTRO DE EVENTO (ReporteManualScreenFX) - CORREGIDA
// ----------------------------------------------------------------------------------
class ReporteManualScreenFX extends Application {
	private TextField latitudTextField;
	private TextField longitudTextField;
	private ComboBox<String> tiposComboBox;
	private TextArea descripcionTextArea;

	private ControladorEvento controladorEvento = new ControladorEvento();
	private static final Logger LOGGER = Logger.getLogger(ReporteManualScreenFX.class.getName());
	// Formato de fecha para Reporte
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Override
	public void start(Stage stage) {
		stage.setTitle("SATE - Reporte de Evento Manual");

		TabPane tabPane = new TabPane();
		tabPane.getTabs().addAll(crearTabDetalles(), crearTabUbicacion());

		HBox footer = new HBox(10);
		footer.setAlignment(Pos.CENTER_RIGHT);
		footer.setPadding(new Insets(10));

		Button sendButton = new Button("Enviar Reporte");
		sendButton.setStyle("-fx-font-weight: bold;");

		// --- LÓGICA DE INSERCIÓN DE REPORTE REAL ---
		sendButton.setOnAction(e -> {
			String wktString = generarWKT();
			String tipo = tiposComboBox.getValue();
			String descripcion = descripcionTextArea.getText();

			if (tipo == null || wktString.startsWith("ERROR") || descripcion.trim().isEmpty()) {
				new Alert(Alert.AlertType.ERROR, "Todos los campos son obligatorios (Tipo, Coordenadas, Descripción).")
						.showAndWait();
				return;
			}

			try {
				String idReporte = "R-" + System.currentTimeMillis();
				// Usar el formato de DB (YYYY-MM-DD HH:MM:SS)
				String fechaActual = LocalDateTime.now().format(DATE_TIME_FORMATTER);

				Reporte nuevoReporte = new Reporte(idReporte, tipo, fechaActual, descripcion, wktString, "PENDIENTE");

				// Llamar al controlador para crear el reporte insertandolo en la BBDD
				boolean exito = controladorEvento.crearReporte(nuevoReporte);

				if (exito) {
					Alert alert = new Alert(Alert.AlertType.INFORMATION,
							"Reporte enviado exitosamente. ID: " + idReporte + ".\nEsperando validación.");
					alert.showAndWait();
					stage.close();
				} else {
					new Alert(Alert.AlertType.ERROR, "No se pudo registrar el reporte. Intente de nuevo.")
							.showAndWait();
				}
			} catch (Exception ex) {
				LOGGER.log(Level.SEVERE, "Error al enviar el reporte.", ex);
				new Alert(Alert.AlertType.ERROR, "ERROR de sistema al enviar el reporte: " + ex.getMessage())
						.showAndWait();
			}
		});

		footer.getChildren().add(sendButton);
		BorderPane root = new BorderPane(tabPane, null, null, footer, null);
		stage.setScene(new Scene(root, 600, 450));
		stage.show();
	}

	private Tab crearTabDetalles() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(15));

		grid.add(new Label("Tipo de Evento:"), 0, 0);
		tiposComboBox = new ComboBox<>();
		tiposComboBox.getItems().addAll("Inundación", "Incendio", "Climático", "Accidente");
		tiposComboBox.getSelectionModel().selectFirst();
		grid.add(tiposComboBox, 1, 0);

		grid.add(new Label("Descripción Breve:"), 0, 1, 2, 1);
		descripcionTextArea = new TextArea();
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

		Label formatInfo = new Label("Formato Esperado: POINT(Lon Lat)");
		formatInfo.setStyle("-fx-text-fill: blue; -fx-font-style: italic;");
		grid.add(formatInfo, 0, 0, 2, 1);

		latitudTextField = new TextField("-31.4");
		longitudTextField = new TextField("-64.1");

		grid.add(new Label("Latitud:"), 0, 1);
		grid.add(latitudTextField, 1, 1);
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
	private Usuario usuario;
	private ControladorEvento controladorEvento = new ControladorEvento();
	private ControladorNOAA controladorNOAA = new ControladorNOAA();
	private WebEngine webEngine;
	private ListView<String> reportListView;
	private static final Logger LOGGER = Logger.getLogger(MainDashboardFX.class.getName());
	private Stage primaryStage;

	public MainDashboardFX(Usuario usuario) {
		this.usuario = usuario;
	}

	@Override
	public void start(Stage stage) {
		this.primaryStage = stage;
		stage.setTitle("SATE - Monitor Principal (" + usuario.getRol() + ")");
		BorderPane root = new BorderPane();

		MenuBar menuBar = crearMenuBar(stage);
		root.setTop(menuBar);

		SplitPane centralSplit = new SplitPane();
		centralSplit.setPadding(new Insets(10, 10, 0, 10));

		VBox mapPanel = crearMapPanel();
		VBox activeReportsPanel = crearActiveReportsPanel();

		centralSplit.getItems().addAll(mapPanel, activeReportsPanel);
		centralSplit.setDividerPositions(0.75);

		root.setCenter(centralSplit);

		Label statusBar = new Label("Conectado como: " + usuario.getEmail() + " (Rol: " + usuario.getRol() + ")");
		statusBar.setPadding(new Insets(5));
		statusBar.setStyle("-fx-border-color: lightgray; -fx-border-width: 1 0 0 0;");
		root.setBottom(statusBar);

		stage.setScene(new Scene(root, 1200, 800));
		stage.setMaximized(true);
		stage.show();

		cargarDatosDelMapaYLista();
	}

	private MenuBar crearMenuBar(Stage stage) {
		MenuBar menuBar = new MenuBar();
		Menu adminMenu = new Menu("Administración");

		MenuItem addUser = new MenuItem("Agregar Usuario");
		addUser.setOnAction(e -> new RegisterScreenFX().start(new Stage()));

		MenuItem manageUsers = new MenuItem("Gestionar Usuarios (Modificar/Baja)");
		manageUsers.setOnAction(e -> new UserManagementScreenFX().start(new Stage()));

		if (this.usuario.getRol().equals("ADMINISTRADOR")) {
			adminMenu.getItems().addAll(addUser, manageUsers, new SeparatorMenuItem());
		}

		MenuItem exitItem = new MenuItem("Cerrar Sesión");
		exitItem.setOnAction(e -> {
			stage.close();
			new LoginScreenFX().start(new Stage());
		});
		adminMenu.getItems().add(exitItem);

		Menu validationMenu = new Menu("Validación");
		MenuItem reviewReports = new MenuItem("Revisar Reportes Pendientes");
		reviewReports.setOnAction(e -> {
			ValidationScreenFX validationScreen = new ValidationScreenFX();
			validationScreen.start(new Stage());
			// Recargar datos cuando se cierre la ventana de validación
			validationScreen.getStage().setOnHidden(event -> cargarDatosDelMapaYLista());
		});

		if (this.usuario.getRol().equals("ADMINISTRADOR") || this.usuario.getRol().equals("VALIDADOR")) {
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

		menuBar.getMenus().addAll(adminMenu, reportMenu, validationMenu, helpMenu);
		return menuBar;
	}

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

	/** Implementa el Panel GIS real usando WebView (CON LA RUTA CORREGIDA) */
	private VBox crearMapPanel() {
		VBox mapPanel = new VBox(10);
		mapPanel.setAlignment(Pos.CENTER);
		mapPanel.setStyle("-fx-background-color: #E0E0E0;");

		Text title = new Text("🗺️ Mapa GIS y Visualización de Eventos.");
		title.setFont(Font.font("Arial", FontWeight.BOLD, 18));

		WebView webView = new WebView();
		webEngine = webView.getEngine();

		try {
			// CORRECCIÓN DE RUTA:
			// Esta ruta asume que 'mapa.html' está en la RAÍZ del classpath.
			String mapaUrl = getClass().getResource("/mapa.html").toExternalForm();

			// Si la ruta anterior falla (devuelve NullPointerException)
			if (mapaUrl == null) {
				mapaUrl = getClass().getResource("/main/resources/mapa.html").toExternalForm();
			}
			webEngine.load(mapaUrl);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE,
					"Error al cargar mapa.html. Asegúrate de que esté en la carpeta 'src' o 'src/main/resources' (classpath root).",
					e);
			mapPanel.getChildren().add(new Label("Error al cargar el mapa. Archivo 'mapa.html' no encontrado."));
		}

		VBox.setVgrow(webView, Priority.ALWAYS);
		// Ultima actualizacion
		// Text lastUpdate = new Text("Actualizado: " + new java.util.Date());
		// lastUpdate.setFont(Font.font("Arial", FontWeight.BOLD, 18));
		// Crear botón de actualización con icono
		Button refreshButton = new Button("🔄 Actualizar");
		refreshButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
		refreshButton.setOnAction(e -> {
			actualizarMapa();
			//actualizarMapaConNOAA();
		});
		mapPanel.getChildren().addAll(title, webView, refreshButton);
		return mapPanel;
	}

	/*private void actualizarMapaConNOAA() {
		webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
			try {
				List<NOAAHotspot> focos = controladorNOAA.obtenerFocosCalorReales();	
				StringBuilder jsonBuilder = new StringBuilder("[");
				for (NOAAHotspot foco : focos) {
					System.out.println("Foco NOAA: " + foco.getLatitud() + ", " + foco.getLongitud() + " - "
							+ foco.getSatelite() + " - " + foco.getTemperatura());
					jsonBuilder.append(
							String.format("{\"lat\":%s, \"lon\":%s, \"tipo\":\"%s\", \"desc\":\"%s\"},",
									foco.getLatitud(), // Latitud
									foco.getLongitud(), // Longitud
									"Foco de Calor Automatico",
									// Limpiar saltos de línea y comillas para JSON
									"Foco NOAA - Satélite: " + foco.getSatelite() + ", Temp: " + foco.getTemperatura() + "K"));
				}
				if (jsonBuilder.length() > 1) {
					jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
				}
				jsonBuilder.append("]");
		
				String jsCommand = "cargarMarcadores(" + jsonBuilder.toString() + ");";
				// Asegurarse de que se ejecute en el hilo de la UI de JavaFX
				Platform.runLater(() -> webEngine.executeScript(jsCommand));
			}catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Error al cargar eventos aprobados para el mapa", e);
				new Alert(Alert.AlertType.ERROR, "Error de BBDD al cargar eventos del mapa: " + e.getMessage())
						.showAndWait();
			}
		});
	}*/

	/**
	 * Utilidad para escapar strings en JavaScript
	 */

	private void actualizarMapa() {
		// Obtener eventos activos
		ControladorEvento controlador = new ControladorEvento();
		try {
			List<Reporte> eventosActivos = controlador.obtenerReportesAprobados();

			// 🔥 SIMPLEMENTE RECARGAR EL MAPA WEBVIEW
			if (webEngine != null) {
				webEngine.reload();
				actualizarBarraEstado("Mapa recargado: " + new java.util.Date());
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error al actualizar el mapa con eventos activos", e);
			new Alert(Alert.AlertType.ERROR, "Error de BBDD al actualizar el mapa: " + e.getMessage()).showAndWait();
		}
	}

	private VBox crearActiveReportsPanel() {
		VBox activeReportsPanel = new VBox(10);
		activeReportsPanel.setPadding(new Insets(10));
		activeReportsPanel
				.setStyle("-fx-border-color: #aaa; -fx-border-width: 0 0 0 1; -fx-background-color: #F8F8F8;");

		Text title = new Text("🚨 Eventos Activos (Aprobados) - Actualizado " + new java.util.Date());
		title.setFont(Font.font("Arial", FontWeight.BOLD, 14));

		reportListView = new ListView<>();
		VBox.setVgrow(reportListView, Priority.ALWAYS);

		// Crear botón de actualización con icono
		Button refreshButton = new Button("🔄 Actualizar");
		refreshButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
		refreshButton.setOnAction(e -> actualizarEventosActivos());
		// Creo el HBOX para centrar el botón
		HBox buttonContainer = new HBox();
		buttonContainer.setAlignment(Pos.CENTER); // Esto centra el botón horizontalmente
		buttonContainer.getChildren().add(refreshButton);

		activeReportsPanel.getChildren().addAll(title, reportListView, buttonContainer);

		return activeReportsPanel;
	}

	private void actualizarEventosActivos() {

		ControladorEvento controlador = new ControladorEvento();
		List<Reporte> eventosActivos;
		try {
			eventosActivos = controlador.obtenerReportesAprobados();

			reportListView.getItems().clear();
			for (Reporte evento : eventosActivos) {
				String item = evento.getTipo() + ": " + evento.getDescripcion() + " (" + evento.getFecha() + ")";
				reportListView.getItems().add(item);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Actualizo la barra de estado
		actualizarBarraEstado("Eventos activos actualizados: " + new java.util.Date());
	}

	private void mostrarDetallesEvento(String eventoItem) {

		try {
			// Extraer ID del evento
			String id = eventoItem.substring(2, eventoItem.indexOf(":"));

			ControladorEvento controlador = new ControladorEvento();
			List<Reporte> eventos = controlador.obtenerReportesAprobados();

			for (Reporte evento : eventos) {
				if (evento.getId().equals(id)) {
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle("Detalles del Evento E-" + evento.getId());
					alert.setHeaderText("Tipo: " + evento.getTipo());
					alert.setContentText("Fecha: " + evento.getFecha() + "\n" + "Coordenadas: "
							+ evento.getCoordenadasWKT() + "\n" + "Descripción: " + evento.getDescripcion() + "\n"
							+ "Estado: " + evento.getEstado());
					alert.showAndWait();
					break;
				}
			}
		} catch (Exception e) {
			new Alert(Alert.AlertType.WARNING, "Seleccione un evento válido para ver detalles").showAndWait();
		}
	}

	private void actualizarBarraEstado(String mensaje) {
		// Buscar la barra de estado en el BorderPane
		BorderPane root = (BorderPane) primaryStage.getScene().getRoot();
		Label statusBar = (Label) root.getBottom();
		if (statusBar != null) {
			statusBar
					.setText("Usuario: " + this.usuario.getEmail() + ".Rol: " + this.usuario.getRol() + ". " + mensaje);
		}
	}

	/** Carga los datos de la DB y los envía al Mapa y a la Lista */
	private void cargarDatosDelMapaYLista() {
		webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
			
				try {
					List<Reporte> eventosAprobados = controladorEvento.obtenerReportesAprobados();
					List<NOAAHotspot> focos = controladorNOAA.obtenerFocosCalorReales();
					reportListView.getItems().clear();

					StringBuilder jsonBuilder = new StringBuilder("[");
					for (Reporte evento : eventosAprobados) {
						reportListView.getItems().add(
								evento.getTipo() + ": " + evento.getDescripcion() + " (" + evento.getFecha() + ")");

						String[] coords = parseWKT(evento.getCoordenadasWKT());
						if (coords != null) {
							jsonBuilder.append(String.format(
									"{\"lat\":%s, \"lon\":%s, \"tipo\":\"%s\", \"desc\":\"%s\"},", coords[1], // Latitud
									coords[0], // Longitud
									evento.getTipo(),
									// Limpiar saltos de línea y comillas para JSON
									evento.getDescripcion().replace("'", "\\'").replace("\n", " ").replace("\r", " "),
									evento.getEstado()));
						}
					}
					for (NOAAHotspot foco : focos) {
						System.out.println("Foco NOAA: " + foco.getLatitud() + ", " + foco.getLongitud() + " - "
								+ foco.getSatelite() + " - " + foco.getTemperatura());
						jsonBuilder.append(
								String.format("{\"lat\":%s, \"lon\":%s, \"tipo\":\"%s\", \"desc\":\"%s\"},",
										foco.getLatitud(), // Latitud
										foco.getLongitud(), // Longitud
										"Foco de Calor Automatico",
										// Limpiar saltos de línea y comillas para JSON
										"Foco NOAA - Satélite: " + foco.getSatelite() + ", Temp: " + foco.getTemperatura() + "K"));
					}
					if (jsonBuilder.length() > 1) {
						jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
					}
					jsonBuilder.append("]");

					String jsCommand = "cargarMarcadores(" + jsonBuilder.toString() + ");";
					// Asegurarse de que se ejecute en el hilo de la UI de JavaFX
					Platform.runLater(() -> webEngine.executeScript(jsCommand));

				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, "Error al cargar eventos aprobados para el mapa", e);
					new Alert(Alert.AlertType.ERROR, "Error de BBDD al cargar eventos del mapa: " + e.getMessage())
							.showAndWait();
				}
				});
		}

	/** Helper para parsear WKT POINT(Lon Lat) */
	private String[] parseWKT(String wkt) {
		if (wkt == null || !wkt.toUpperCase().startsWith("POINT"))
			return null;
		try {
			String clean = wkt.substring(wkt.indexOf("(") + 1, wkt.indexOf(")")).trim();
			String[] parts = clean.split(" ");
			if (parts.length == 2) {
				return parts; // Devuelve [Lon, Lat]
			}
			return null;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error parseando WKT: " + wkt, e);
			return null;
		}
	}
}

//----------------------------------------------------------------------------------
// PANTALLA 5: GESTION DE USUARIOS
//----------------------------------------------------------------------------------

class UserManagementScreenFX extends Application {

	private ControladorUsuario controladorUsuario = new ControladorUsuario();
	private TableView<Usuario> userTable;
	private Stage modifyStage;

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

		userTable = crearTablaUsuarios();
		root.setCenter(userTable);

		HBox actionButtons = new HBox(15);
		actionButtons.setAlignment(Pos.CENTER_RIGHT);
		actionButtons.setPadding(new Insets(15, 0, 0, 0));

		Button modifyButton = new Button("Modificar Usuario Seleccionado");
		modifyButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");

		Button deleteButton = new Button("Dar de Baja");
		deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

		Button refreshButton = new Button("Recargar Lista");
		refreshButton.setOnAction(e -> cargarUsuarios());

		modifyButton.setOnAction(e -> handleModify(userTable.getSelectionModel().getSelectedItem()));
		deleteButton.setOnAction(e -> handleDelete(userTable.getSelectionModel().getSelectedItem()));

		actionButtons.getChildren().addAll(refreshButton, modifyButton, deleteButton);
		root.setBottom(actionButtons);

		stage.setScene(new Scene(root));
		stage.show();

		cargarUsuarios();
	}

	private TableView<Usuario> crearTablaUsuarios() {
		TableView<Usuario> table = new TableView<>();

		// Usar PropertyValueFactory con la entidad Usuario real
		TableColumn<Usuario, String> idCol = new TableColumn<>("ID");
		idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
		idCol.setPrefWidth(200);

		TableColumn<Usuario, String> emailCol = new TableColumn<>("Email");
		emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
		emailCol.setPrefWidth(300);

		TableColumn<Usuario, String> rolCol = new TableColumn<>("Rol");
		rolCol.setCellValueFactory(new PropertyValueFactory<>("rol"));
		rolCol.setPrefWidth(150);

		table.getColumns().addAll(idCol, emailCol, rolCol);
		return table;
	}

	private void cargarUsuarios() {
		try {
			List<Usuario> usuarios = controladorUsuario.obtenerTodosLosUsuarios();
			userTable.getItems().setAll(usuarios);
		} catch (Exception e) {
			new Alert(Alert.AlertType.ERROR, "Error al cargar usuarios desde la BBDD: " + e.getMessage()).showAndWait();
		}
	}

	private void handleModify(Usuario user) {
		if (user == null) {
			new Alert(Alert.AlertType.WARNING, "Seleccione un usuario para modificar.").showAndWait();
			return;
		}
		showModifyUserDialog(user);
	}

	private void showModifyUserDialog(Usuario user) {
		if (modifyStage != null && modifyStage.isShowing()) {
			modifyStage.close();
		}

		modifyStage = new Stage();
		modifyStage.setTitle("Modificar Usuario: " + user.getEmail());

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20));

		Label emailLabel = new Label("Email:");
		TextField emailField = new TextField(user.getEmail());
		emailField.setDisable(true);

		Label rolLabel = new Label("Rol:");
		ComboBox<String> rolComboBox = new ComboBox<>();
		rolComboBox.getItems().addAll("ADMINISTRADOR", "VALIDADOR", "CIUDADANO");
		rolComboBox.setValue(user.getRol());

		Label newPassLabel = new Label("Nueva Contraseña (Dejar en blanco para no cambiar):");
		PasswordField newPasswordField = new PasswordField();

		Text statusMessage = new Text("");
		statusMessage.setFill(Color.BLUE);

		grid.add(emailLabel, 0, 0);
		grid.add(emailField, 1, 0);
		grid.add(rolLabel, 0, 1);
		grid.add(rolComboBox, 1, 1);
		grid.add(newPassLabel, 0, 2);
		grid.add(newPasswordField, 1, 2);
		grid.add(statusMessage, 1, 3);

		Button saveButton = new Button("Guardar Cambios");
		saveButton.setOnAction(e -> {
			try {
				String newPass = newPasswordField.getText().trim();
				String passToSave = newPass.isEmpty() ? user.getPasswordHash() : newPass;

				Usuario updatedUser = new Usuario(user.getId(), user.getEmail(), passToSave, rolComboBox.getValue());

				boolean success = controladorUsuario.modificarUsuario(updatedUser);

				if (success) {
					statusMessage.setFill(Color.GREEN);
					statusMessage.setText("Usuario modificado exitosamente!");
					saveButton.setDisable(true);
					cargarUsuarios(); // Recargar la tabla principal
				} else {
					statusMessage.setFill(Color.RED);
					statusMessage.setText("Error al guardar cambios.");
				}
			} catch (Exception ex) {
				statusMessage.setFill(Color.RED);
				statusMessage.setText("Error de BBDD: " + ex.getMessage());
			}
		});

		HBox buttonBox = new HBox(10, saveButton);
		buttonBox.setAlignment(Pos.CENTER_RIGHT);
		grid.add(buttonBox, 1, 4);

		modifyStage.setScene(new Scene(grid, 500, 250));
		modifyStage.show();
	}

	private void handleDelete(Usuario user) {
		if (user == null) {
			new Alert(Alert.AlertType.WARNING, "Seleccione un usuario para dar de baja.").showAndWait();
			return;
		}

		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("Confirmar Baja");
		confirm.setHeaderText("Baja de Usuario: " + user.getEmail());
		confirm.setContentText(
				"¿Está seguro de dar de baja al usuario " + user.getEmail() + "? Esta acción es irreversible.");

		Optional<ButtonType> result = confirm.showAndWait();

		if (result.isPresent() && result.get() == ButtonType.OK) {
			try {
				boolean success = controladorUsuario.darDeBajaUsuario(user.getId());

				if (success) {
					new Alert(Alert.AlertType.INFORMATION,
							"Usuario " + user.getEmail() + " dado de baja correctamente.").showAndWait();
					cargarUsuarios(); // Recargar la tabla
				} else {
					new Alert(Alert.AlertType.ERROR,
							"No se pudo dar de baja al usuario.\n(Verifique si es el administrador principal 'ID 1').")
							.showAndWait();
				}
			} catch (Exception e) {
				new Alert(Alert.AlertType.ERROR, "Error al dar de baja: " + e.getMessage());
			}
		}
	}
}
// ----------------------------------------------------------------------------------
// PANTALLA 6: VALIDACIÓN DE REPORTES (ValidationScreenFX) 
// ----------------------------------------------------------------------------------

class ValidationScreenFX extends Application {

	private ControladorEvento controladorEvento = new ControladorEvento();
	private TableView<Reporte> reporteTable;
	private TextArea descriptionArea;
	private Label wktLabel;
	private Stage stage;

	public Stage getStage() {
		return this.stage;
	}

	@Override
	public void start(Stage stage) {
		this.stage = stage; // Guardar la referencia
		stage.setTitle("SATE - Validación de Reportes Pendientes");
		stage.setWidth(1000);
		stage.setHeight(650);

		BorderPane root = new BorderPane();
		root.setPadding(new Insets(10));

		SplitPane splitPane = new SplitPane();

		// Panel izquierdo: Reportes Manuales Pendientes
		VBox leftPanel = new VBox(10);
		leftPanel.setPadding(new Insets(10));
		Text titleLeft = new Text("Reportes Manuales Pendientes");
		titleLeft.setFont(Font.font("Arial", FontWeight.BOLD, 14));
		reporteTable = crearTablaReportes();
		leftPanel.getChildren().addAll(titleLeft, reporteTable);
		VBox.setVgrow(reporteTable, Priority.ALWAYS);

		VBox rightPanel = new VBox(10);
		rightPanel.setPadding(new Insets(10));
		Text titleRight = new Text("🔍 Detalles del Reporte Seleccionado");
		titleRight.setFont(Font.font("Arial", FontWeight.BOLD, 14));
		GridPane detailGrid = crearPanelDetalles();
		HBox actionButtons = crearPanelAcciones(stage);
		rightPanel.getChildren().addAll(titleRight, detailGrid, new Separator(), actionButtons);

		reporteTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				actualizarDetalles(newSelection);
			}
		});

		splitPane.getItems().addAll(leftPanel, rightPanel);
		splitPane.setDividerPositions(0.6);
		root.setCenter(splitPane);

		stage.setScene(new Scene(root));
		stage.show();

		cargarReportesPendientes();
	}

	private void cargarReportesPendientes() {
		try {
			List<Reporte> reportes = controladorEvento.obtenerReportesPendientes();
			reporteTable.getItems().setAll(reportes);
		} catch (Exception e) {
			new Alert(Alert.AlertType.ERROR, "Error al cargar reportes pendientes: " + e.getMessage()).showAndWait();
		}
	}

	/** Configura la TableView con la entidad Reporte */
	private TableView<Reporte> crearTablaReportes() {
		TableView<Reporte> table = new TableView<>();

		TableColumn<Reporte, String> idCol = new TableColumn<>("ID");
		// Usar PropertyValueFactory con los getters (getId())
		idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
		idCol.setPrefWidth(50);

		TableColumn<Reporte, String> tipoCol = new TableColumn<>("Tipo");
		tipoCol.setCellValueFactory(new PropertyValueFactory<>("tipo"));
		tipoCol.setPrefWidth(100);

		TableColumn<Reporte, String> fechaCol = new TableColumn<>("Fecha");
		fechaCol.setCellValueFactory(new PropertyValueFactory<>("fecha"));
		fechaCol.setPrefWidth(150);

		TableColumn<Reporte, String> descCol = new TableColumn<>("Descripción");
		descCol.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
		descCol.setPrefWidth(250);

		TableColumn<Reporte, String> estCol = new TableColumn<>("Estado");
		estCol.setCellValueFactory(new PropertyValueFactory<>("estado"));
		estCol.setPrefWidth(250);

		table.getColumns().addAll(idCol, tipoCol, fechaCol, descCol, estCol);

		return table;
	}

	private GridPane crearPanelDetalles() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(5));

		grid.add(new Label("Coordenadas (WKT):"), 0, 0);
		wktLabel = new Label("N/A");
		wktLabel.setWrapText(true);
		grid.add(wktLabel, 1, 0);

		grid.add(new Label("Descripción:"), 0, 1);
		descriptionArea = new TextArea("Seleccione un reporte de la izquierda.");
		descriptionArea.setEditable(false);
		descriptionArea.setWrapText(true);
		descriptionArea.setPrefRowCount(5);
		grid.add(descriptionArea, 0, 2, 2, 1);

		return grid;
	}

	private void actualizarDetalles(Reporte reporte) {
		wktLabel.setText(reporte.getCoordenadasWKT());
		descriptionArea.setText(reporte.getDescripcion());
	}

	private HBox crearPanelAcciones(Stage stage) {
		HBox buttonBox = new HBox(15);
		buttonBox.setAlignment(Pos.CENTER_RIGHT);

		Button approveButton = new Button("✅ Aprobar Evento");
		approveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");

		Button rejectButton = new Button("❌ Rechazar Reporte");
		rejectButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

		// --- LÓGICA DE APROBACIÓN REAL (CORREGIDA) ---
		approveButton.setOnAction(e -> {
			Reporte seleccionado = reporteTable.getSelectionModel().getSelectedItem();
			if (seleccionado == null) {
				new Alert(Alert.AlertType.WARNING, "Seleccione un reporte para aprobar.").showAndWait();
				return;
			}

			try {
				// Pasar el objeto Reporte completo al controlador
				boolean exito = controladorEvento.aprobarReporte(seleccionado);
				if (exito) {
					new Alert(Alert.AlertType.INFORMATION, "Reporte APROBADO. El evento ha sido publicado.")
							.showAndWait();
					cargarReportesPendientes(); // Recargar la tabla

				} else {
					new Alert(Alert.AlertType.ERROR, "No se pudo aprobar el reporte.").showAndWait();
				}
			} catch (Exception ex) {
				new Alert(Alert.AlertType.ERROR, "Error de BBDD al aprobar: " + ex.getMessage()).showAndWait();
			}
		});

		// --- Logica de rechazo de reporte ---
		rejectButton.setOnAction(e -> {
			Reporte seleccionado = reporteTable.getSelectionModel().getSelectedItem();
			if (seleccionado == null) {
				new Alert(Alert.AlertType.WARNING, "Seleccione un reporte para rechazar.").showAndWait();
				return;
			}

			try {
				boolean exito = controladorEvento.rechazarReporte(seleccionado.getId());
				if (exito) {
					new Alert(Alert.AlertType.INFORMATION, "Reporte RECHAZADO y eliminado de la lista de pendientes.")
							.showAndWait();
					cargarReportesPendientes(); // Recargar la tabla
				} else {
					new Alert(Alert.AlertType.ERROR, "No se pudo rechazar el reporte.").showAndWait();
				}
			} catch (Exception ex) {
				new Alert(Alert.AlertType.ERROR, "Error de BBDD al rechazar: " + ex.getMessage()).showAndWait();
			}
		});

		buttonBox.getChildren().addAll(rejectButton, approveButton);
		return buttonBox;
	}
}