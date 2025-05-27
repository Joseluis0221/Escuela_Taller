/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
/**
 *
 * @author jhuila
 */
public class IngresoEstudiantes extends JFrame implements ActionListener {
    
    private JLabel lblTitulo, lblCodigo, lblNombre;
    private JTextField txtCodigo, txtNombre;
    private JButton btnIngresar, btnLimpiar, btnCerrar;
    private JPanel panelPrincipal, panelFormulario, panelBotones;
    
    public IngresoEstudiantes() {
        initComponents();
        configurarVentana();
        System.out.println("Ventana de Registro de Estudiantes abierta");
    }
    
    private void initComponents() {
        lblTitulo = new JLabel("Registro de Estudiantes", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(25, 25, 112));
        
        lblCodigo = new JLabel("Código:");
        lblCodigo.setFont(new Font("Arial", Font.PLAIN, 12));
        
        lblNombre = new JLabel("Nombre:");
        lblNombre.setFont(new Font("Arial", Font.PLAIN, 12));
        
        txtCodigo = new JTextField(25);
        txtNombre = new JTextField(25);
        
        btnIngresar = new JButton("Ingresar");
        btnLimpiar = new JButton("Limpiar");
        btnCerrar = new JButton("Cerrar");
        
        btnIngresar.addActionListener(this);
        btnLimpiar.addActionListener(this);
        btnCerrar.addActionListener(this);
        
        btnIngresar.setBackground(new Color(34, 139, 34));
        btnIngresar.setForeground(Color.WHITE);
        btnLimpiar.setBackground(new Color(255, 165, 0));
        btnLimpiar.setForeground(Color.WHITE);
        btnCerrar.setBackground(new Color(220, 20, 60));
        btnCerrar.setForeground(Color.WHITE);
        
        panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(new Color(245, 245, 245));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Estudiante"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panelFormulario.add(lblCodigo, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelFormulario.add(txtCodigo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panelFormulario.add(lblNombre, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelFormulario.add(txtNombre, gbc);
        
        panelBotones = new JPanel(new FlowLayout());
        panelBotones.setBackground(new Color(245, 245, 245));
        panelBotones.add(btnIngresar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnCerrar);
        
        panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(240, 248, 255));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        
        add(panelPrincipal);
    }
    
    private void configurarVentana() {
        setTitle("Registro de Estudiantes");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        
        switch (comando) {
            case "Ingresar" -> ingresarEstudiante();
                
            case "Limpiar" -> limpiarCampos();
                
            case "Cerrar" -> dispose();
        }
    }
    
    private void ingresarEstudiante() {
        if (txtCodigo.getText().trim().isEmpty() || txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor complete todos los campos", 
                "Campos Vacíos", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int codigo = Integer.parseInt(txtCodigo.getText().trim());
            String nombre = txtNombre.getText().trim();
            
            Connection conn = Conexion.getConexion();
            if (conn != null) {
                String sqlVerificar = "SELECT cod_estudiante FROM estudiantes WHERE cod_estudiante = " + codigo;
                ResultSet rs = Conexion.ejecutarConsulta(sqlVerificar);
                
                if (rs != null && rs.next()) {
                    JOptionPane.showMessageDialog(this, 
                        "Ya existe un estudiante con el código: " + codigo, 
                        "Código Duplicado", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String sqlInsertar = "INSERT INTO estudiantes (cod_estudiante, nom_estudiante) VALUES (" + 
                                   codigo + ", '" + nombre + "')";
                
                int resultado = Conexion.ejecutarActualizacion(sqlInsertar);
                
                if (resultado > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Estudiante registrado exitosamente", 
                        "Registro Exitoso", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    System.out.println("Estudiante registrado: " + codigo + " - " + nombre);
                    limpiarCampos();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Error al registrar el estudiante", 
                        "Error de Registro", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "El código debe ser un número entero", 
                "Error de Formato", 
                JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error de base de datos: " + ex.getMessage(), 
                "Error SQL", 
                JOptionPane.ERROR_MESSAGE);
            System.err.println("Error SQL: " + ex.getMessage());
        } catch (HeadlessException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error inesperado: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            System.err.println("Error: " + ex.getMessage());
        }
    }
    
    private void limpiarCampos() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtCodigo.requestFocus();
        System.out.println("Campos de estudiante limpiados");
    }
}