/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.sql.*;
import javax.swing.JOptionPane;

/**
 *
 * @author jhuila
 */
public class Conexion {
    
    private static final String URL = "jdbc:mysql://localhost:3306/escuela_taller";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "Admin1234";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    
    private static Connection conexion = null;
    
    public static Connection getConexion() {
        try {
            Class.forName(DRIVER);
            
            conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            
            if (conexion != null) {
                System.out.println("Conexión establecida exitosamente con la base de datos: escuela-taller");
            }
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Driver MySQL no encontrado");
            System.err.println("Detalle: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Driver MySQL no encontrado\nAsegúrese de tener el conector MySQL en el classpath", 
                "Error de Conexión", 
                JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos");
            System.err.println("Detalle: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Error al conectar con la base de datos:\n" + e.getMessage(), 
                "Error de Conexión", 
                JOptionPane.ERROR_MESSAGE);
        }
        
        return conexion;
    }
    
    public static void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexión cerrada correctamente");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
    
    public static boolean verificarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar conexión: " + e.getMessage());
        }
        return false;
    }
    
    public static ResultSet ejecutarConsulta(String sql) {
        try {
            if (conexion == null || conexion.isClosed()) {
                getConexion();
            }
            
            Statement stmt = conexion.createStatement();
            return stmt.executeQuery(sql);
            
        } catch (SQLException e) {
            System.err.println("Error al ejecutar consulta: " + e.getMessage());
            return null;
        }
    }
    
    public static int ejecutarActualizacion(String sql) {
        try {
            if (conexion == null || conexion.isClosed()) {
                getConexion();
            }
            
            Statement stmt = conexion.createStatement();
            return stmt.executeUpdate(sql);
            
        } catch (SQLException e) {
            System.err.println("Error al ejecutar actualización: " + e.getMessage());
            return -1;
        }
    }
}
