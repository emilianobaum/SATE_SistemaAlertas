package com.sate.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminScreen extends JFrame {

	private JTable userTable;

	public AdminScreen() {
		setTitle("SATE - Panel de Administración y Gestión de Usuarios");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// JMenuBar: Se mantiene la estructura administrativa
		JMenuBar menuBar = new JMenuBar();
		JMenu adminMenu = new JMenu("Administración");
		adminMenu.add(new JMenuItem("Gestionar Usuarios"));
		adminMenu.addSeparator();
		adminMenu.add(new JMenuItem("Configuración del Sistema"));
		menuBar.add(adminMenu);
		menuBar.add(new JMenu("Ayuda"));
		setJMenuBar(menuBar);

		// Panel Principal con BorderLayout
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// --- Panel de Controles (Norte) ---
		JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));

		JButton addButton = new JButton("➕ Agregar Usuario");
		JButton editButton = new JButton("✏️ Editar Usuario");
		JButton deactivateButton = new JButton("❌ Desactivar/Activar");

		// Acción simulada para agregar usuario
		addButton.addActionListener(e -> JOptionPane.showMessageDialog(this,
				"Abriendo formulario para Agregar Nuevo Usuario.", "Gestión", JOptionPane.INFORMATION_MESSAGE));

		controlPanel.add(new JLabel("Rol:"));
		controlPanel.add(new JComboBox<>(new String[] { "Todos", "Administrador", "Validador", "Ciudadano" }));
		controlPanel.add(addButton);
		controlPanel.add(editButton);
		controlPanel.add(deactivateButton);

		mainPanel.add(controlPanel, BorderLayout.NORTH);

		// --- Tabla de Usuarios (Centro) ---
		String[] columnNames = { "ID", "Email", "Nombre", "Rol", "Estado" };
		// El DefaultTableModel permite modificar datos
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);
		userTable = new JTable(model);

		// SIMULACIÓN de carga inicial (Esto debería ser DBConnector.getUsuarios())
		model.addRow(new Object[] { 1, "admin@sate.com", "Super Administrador", "Administrador", "Activo" });
		model.addRow(new Object[] { 2, "validador1@sate.com", "Juan Pérez", "Validador", "Activo" });
		model.addRow(new Object[] { 3, "ciudadano_baja@mail.com", "María López", "Ciudadano", "Inactivo" });

		mainPanel.add(new JScrollPane(userTable), BorderLayout.CENTER);

		// Barra de Estado
		JLabel statusBar = new JLabel("Estado: Conectado. 3 usuarios registrados.");
		statusBar.setBorder(BorderFactory.createEtchedBorder());

		add(mainPanel, BorderLayout.CENTER);
		add(statusBar, BorderLayout.SOUTH);
	}
}