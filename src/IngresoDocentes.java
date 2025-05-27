/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 *
 * @author jhuila
 */
public class IngresoDocentes extends JFrame {
    private JTextField txtCodigo, txtNombre;
    private JButton btnIngresar, btnLimpiar, btnEliminar, btnModificar;
    private JTable tablaDocentes;
    private DefaultTableModel modeloTabla;
    private final Conexion conexion;
    
    public IngresoDocentes() {
        super("Registro de Docentes");
        conexion = new Conexion();
        initComponents();
        cargarDatos();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulario.add(new JLabel("Código:"), gbc);
        gbc.gridx = 1;
        txtCodigo = new JTextField(15);
        panelFormulario.add(txtCodigo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panelFormulario.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(15);
        panelFormulario.add(txtNombre, gbc);
        
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnIngresar = new JButton("Ingresar");
        btnLimpiar = new JButton("Limpiar");
        btnEliminar = new JButton("Eliminar");
        btnModificar = new JButton("Modificar");
        
        panelBotones.add(btnIngresar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnModificar);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        panelFormulario.add(panelBotones, gbc);
        
        add(panelFormulario, BorderLayout.NORTH);
        
        String[] columnas = {"Código", "Nombre"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaDocentes = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaDocentes);
        add(scrollPane, BorderLayout.CENTER);
        
        btnIngresar.addActionListener((ActionEvent e) -> {
            ingresarDocente();
        });
        
        btnLimpiar.addActionListener((ActionEvent e) -> {
            limpiarCampos();
        });
        
        btnEliminar.addActionListener((ActionEvent e) -> {
            eliminarDocente();
        });
        
        btnModificar.addActionListener((ActionEvent e) -> {
            modificarDocente();
        });
        
        tablaDocentes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int filaSeleccionada = tablaDocentes.getSelectedRow();
                if (filaSeleccionada != -1) {
                    txtCodigo.setText(modeloTabla.getValueAt(filaSeleccionada, 0).toString());
                    txtNombre.setText(modeloTabla.getValueAt(filaSeleccionada, 1).toString());
                }
            }
        });
    }
    
    private void ingresarDocente() {
        if (validarCampos()) {
            try {
                try (Connection conn = Conexion.getConexion()) {
                    String sql = "INSERT INTO docentes (cod_docente, nom_docente) VALUES (?, ?)";
                    try (PreparedStatement pst = conn.prepareStatement(sql)) {
                        pst.setInt(1, Integer.parseInt(txtCodigo.getText()));
                        pst.setString(2, txtNombre.getText());
                        
                        int resultado = pst.executeUpdate();
                        if (resultado > 0) {
                            JOptionPane.showMessageDialog(this, "Docente ingresado correctamente");
                            limpiarCampos();
                            cargarDatos();
                        }
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al ingresar docente: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El código debe ser un número válido");
            }
        }
    }
    
    private void eliminarDocente() {
        if (txtCodigo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un docente para eliminar");
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar este docente?", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                try (Connection conn = Conexion.getConexion()) {
                    String sql = "DELETE FROM docentes WHERE cod_docente = ?";
                    try (PreparedStatement pst = conn.prepareStatement(sql)) {
                        pst.setInt(1, Integer.parseInt(txtCodigo.getText()));
                        
                        int resultado = pst.executeUpdate();
                        if (resultado > 0) {
                            JOptionPane.showMessageDialog(this, "Docente eliminado correctamente");
                            limpiarCampos();
                            cargarDatos();
                        }
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar docente: " + ex.getMessage());
            }
        }
    }
    
    private void modificarDocente() {
        if (validarCampos()) {
            try {
                try (Connection conn = Conexion.getConexion()) {
                    String sql = "UPDATE docentes SET nom_docente = ? WHERE cod_docente = ?";
                    try (PreparedStatement pst = conn.prepareStatement(sql)) {
                        pst.setString(1, txtNombre.getText());
                        pst.setInt(2, Integer.parseInt(txtCodigo.getText()));
                        
                        int resultado = pst.executeUpdate();
                        if (resultado > 0) {
                            JOptionPane.showMessageDialog(this, "Docente modificado correctamente");
                            limpiarCampos();
                            cargarDatos();
                        }
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al modificar docente: " + ex.getMessage());
            }
        }
    }
    
    private void cargarDatos() {
        try {
            modeloTabla.setRowCount(0);
            
            try (Connection conn = Conexion.getConexion()) {
                String sql = "SELECT cod_docente, nom_docente FROM docentes ORDER BY cod_docente";
                try (PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
                    
                    while (rs.next()) {
                        Object[] fila = {
                            rs.getInt("cod_docente"),
                            rs.getString("nom_docente")
                        };
                        modeloTabla.addRow(fila);
                    }
                    
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + ex.getMessage());
        }
    }
    
    private boolean validarCampos() {
        if (txtCodigo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el código del docente");
            txtCodigo.requestFocus();
            return false;
        }
        
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre del docente");
            txtNombre.requestFocus();
            return false;
        }
        
        try {
            Integer.valueOf(txtCodigo.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El código debe ser un número válido");
            txtCodigo.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void limpiarCampos() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtCodigo.requestFocus();
        tablaDocentes.clearSelection();
    }
}