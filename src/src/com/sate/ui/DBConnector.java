package com.sate.ui;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class DBConnector { // La clase debe ser PUBLIC
    private static final String DB_URL = "jdbc:mysql://10.0.100.24:3308/SATE_DB";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "password";
    
    private DBConnector() {}

    public static Connection getConnection() {
        Connection conn = null;
        try {
            System.out.println("Intentando conectar a: " + DB_URL);
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("Conexión a la BBDD establecida con éxito (simulado).");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Error de Conexión a la Base de Datos: " + e.getMessage(), 
                "Error JDBC", JOptionPane.ERROR_MESSAGE);
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
        return conn;
    }

    // MÉTODO REQUERIDO: getEventos()
    public static List<Evento> getEventos() { 
        List<Evento> eventos = new ArrayList<>();
        final String SQL = "SELECT idEvento, tipo, fechaHora, areaAfectada FROM Evento";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {
            
            while (rs.next()) {
                int id = rs.getInt("idEvento");
                String tipo = rs.getString("tipo");
                String fechaHora = rs.getTimestamp("fechaHora").toString();
                String areaAfectada = rs.getString("areaAfectada");
                
                Evento evento = new Evento(id, tipo, fechaHora, areaAfectada);
                eventos.add(evento);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Error al cargar eventos: " + e.getMessage(), 
                "Error DB", JOptionPane.ERROR_MESSAGE);
        }
        return eventos;
    }

    public static void testConnection() {
        Connection conn = getConnection();
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // Ignore error on close
            }
        }
    }
}