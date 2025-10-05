package com.sate.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ValidationScreen extends JFrame {

	private JTable reportTable;
	private JTextArea detailTextArea;

	public ValidationScreen() {
		setTitle("SATE - Validación de Reportes Manuales");
		setSize(1000, 600);
		setLocationRelativeTo(null);

		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

		// Panel Dividido Principal (Reportes Pendientes | Detalle/Acción)
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setDividerLocation(450);

		// --- Panel Izquierdo: Lista de Reportes Pendientes ---
		JPanel listPanel = new JPanel(new BorderLayout());
		listPanel.setBorder(BorderFactory.createTitledBorder("Reportes Pendientes de Validación"));

		String[] columnNames = { "ID", "Tipo", "Usuario", "Fecha" };
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);
		reportTable = new JTable(model);

		// SIMULACIÓN de reportes pendientes
		model.addRow(new Object[] { 201, "Incendio", "usuario1@mail.com", "2025-10-04 14:30" });
		model.addRow(new Object[] { 202, "Inundación", "usuario2@mail.com", "2025-10-04 15:15" });
		model.addRow(new Object[] { 203, "Climático", "usuario3@mail.com", "2025-10-05 08:00" });

		listPanel.add(new JScrollPane(reportTable), BorderLayout.CENTER);

		splitPane.setLeftComponent(listPanel);

		// --- Panel Derecho: Detalle y Acción de Validación ---
		JPanel detailPanel = new JPanel(new BorderLayout());
		detailPanel.setBorder(BorderFactory.createTitledBorder("Detalle del Reporte Seleccionado"));

		// Área de Detalle (Simulación de WKT, Descripción, etc.)
		detailTextArea = new JTextArea(
				"Seleccione un reporte para ver los detalles aquí:\n\n" + "WKT: POINT(-58.38 -34.60)\n"
						+ "Descripción: Humo visible a lo lejos, cerca del río.\n" + "ID Usuario: 2024");
		detailTextArea.setEditable(false);
		detailPanel.add(new JScrollPane(detailTextArea), BorderLayout.CENTER);

		// Panel de Botones de Acción
		JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton rejectButton = new JButton("❌ Rechazar Reporte");
		JButton approveButton = new JButton("✅ Aprobar Evento (Crear Evento)");

		rejectButton.addActionListener(e -> JOptionPane.showMessageDialog(this,
				"Reporte Rechazado. Se notifica al usuario.", "Validación", JOptionPane.WARNING_MESSAGE));
		approveButton.addActionListener(
				e -> JOptionPane.showMessageDialog(this, "Reporte Aprobado. Evento creado y listo para notificación.",
						"Validación", JOptionPane.INFORMATION_MESSAGE));

		actionPanel.add(rejectButton);
		actionPanel.add(approveButton);
		detailPanel.add(actionPanel, BorderLayout.SOUTH);

		splitPane.setRightComponent(detailPanel);

		mainPanel.add(splitPane, BorderLayout.CENTER);
		add(mainPanel, BorderLayout.CENTER);
	}
}