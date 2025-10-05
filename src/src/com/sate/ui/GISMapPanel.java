package com.sate.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GISMapPanel extends JPanel { // La clase debe ser PUBLIC
	private List<Evento> eventos;

	public GISMapPanel() {
		this.setBorder(BorderFactory.createTitledBorder("🗺️ Visualización de Mapa GIS"));
		this.setBackground(new Color(220, 220, 255));
	}

	public void setEventos(List<Evento> eventos) {
		this.eventos = eventos;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (eventos == null || eventos.isEmpty()) {
			g.setColor(Color.BLACK);
			g.setFont(new Font("Segoe UI", Font.ITALIC, 20));
			g.drawString("No hay eventos activos para mostrar.", 50, 50);
			return;
		}

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int mapWidth = getWidth();
		int mapHeight = getHeight();

		for (Evento evento : eventos) {
			String[] coords = evento.getCoordinates();
			if (coords.length == 2) {
				try {
					double lon = Double.parseDouble(coords[0]);
					double lat = Double.parseDouble(coords[1]);

					// Lógica de simulación para mapear coordenadas a la pantalla
					int x = (int) (mapWidth * (Math.abs(lon + 70) % 0.8) + 50);
					int y = (int) (mapHeight * (Math.abs(lat + 50) % 0.8) + 50);

					// Lógica de color
					Color markerColor = Color.ORANGE;
					if (evento.getTipo().equals("Incendio")) {
						markerColor = Color.RED;
					} else if (evento.getTipo().equals("Inundación")) {
						markerColor = Color.BLUE;
					}

					g2d.setColor(markerColor);
					g2d.fillOval(x - 8, y - 8, 16, 16);
					g2d.setColor(Color.BLACK);
					g2d.drawOval(x - 8, y - 8, 16, 16);

					g2d.drawString(evento.getTipo(), x + 10, y + 5);

				} catch (NumberFormatException e) {
					// Ignore invalid coordinates
				}
			}
		}
	}
}