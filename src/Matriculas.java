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
public class Matriculas extends JFrame {
    private JComboBox<String> cmbEstudiante, cmbCurso;
    private JTextField txtNota;
    private JButton btnMatricular, btnLimpiar, btnEliminar, btnModificar;
    private JTable tablaMatriculas;
    private DefaultTableModel modeloTabla;
    private final Conexion conexion;
    
    public Matriculas() {
        super("Gestión de Matrículas");
        conexion = new Conexion();
        initComponents();
        cargarComboBoxes();
        cargarDatos();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulario.add(new JLabel("Estudiante:"), gbc);
        gbc.gridx = 1;
        cmbEstudiante = new JComboBox<>();
        cmbEstudiante.setPreferredSize(new Dimension(200, 25));
        panelFormulario.add(cmbEstudiante, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panelFormulario.add(new JLabel("Curso:"), gbc);
        gbc.gridx = 1;
        cmbCurso = new JComboBox<>();
        cmbCurso.setPreferredSize(new Dimension(200, 25));
        panelFormulario.add(cmbCurso, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panelFormulario.add(new JLabel("Nota:"), gbc);
        gbc.gridx = 1;
        txtNota = new JTextField(15);
        panelFormulario.add(txtNota, gbc);
        
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnMatricular = new JButton("Matricular");
        btnLimpiar = new JButton("Limpiar");
        btnEliminar = new JButton("Eliminar");
        btnModificar = new JButton("Modificar");
        
        panelBotones.add(btnMatricular);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnModificar);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        panelFormulario.add(panelBotones, gbc);
        
        add(panelFormulario, BorderLayout.NORTH);
        
        String[] columnas = {"Código Estudiante", "Nombre Estudiante", "Código Curso", "Nombre Curso", "Nota"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaMatriculas = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaMatriculas);
        add(scrollPane, BorderLayout.CENTER);
        
        btnMatricular.addActionListener((ActionEvent e) -> {
            matricularEstudiante();
        });
        
        btnLimpiar.addActionListener((ActionEvent e) -> {
            limpiarCampos();
        });
        
        btnEliminar.addActionListener((ActionEvent e) -> {
            eliminarMatricula();
        });
        
        btnModificar.addActionListener((ActionEvent e) -> {
            modificarMatricula();
        });
        
        tablaMatriculas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int filaSeleccionada = tablaMatriculas.getSelectedRow();
                if (filaSeleccionada != -1) {
                    String nombreEstudiante = modeloTabla.getValueAt(filaSeleccionada, 1).toString();
                    for (int i = 0; i < cmbEstudiante.getItemCount(); i++) {
                        if (cmbEstudiante.getItemAt(i).contains(nombreEstudiante)) {
                            cmbEstudiante.setSelectedIndex(i);
                            break;
                        }
                    }
                    
                    String nombreCurso = modeloTabla.getValueAt(filaSeleccionada, 3).toString();
                    for (int i = 0; i < cmbCurso.getItemCount(); i++) {
                        if (cmbCurso.getItemAt(i).contains(nombreCurso)) {
                            cmbCurso.setSelectedIndex(i);
                            break;
                        }
                    }
                    
                    Object nota = modeloTabla.getValueAt(filaSeleccionada, 4);
                    txtNota.setText(nota != null ? nota.toString() : "");
                }
            }
        });
    }
    
    private void cargarComboBoxes() {
        cargarEstudiantes();
        cargarCursos();
    }
    
    private void cargarEstudiantes() {
        try {
            cmbEstudiante.removeAllItems();
            try (Connection conn = Conexion.getConexion()) {
                String sql = "SELECT cod_estudiante, nom_estudiante FROM estudiantes ORDER BY nom_estudiante";
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    ResultSet rs = pst.executeQuery();
                    
                    while (rs.next()) {
                        String item = rs.getInt("cod_estudiante") + " - " + rs.getString("nom_estudiante");
                        cmbEstudiante.addItem(item);
                    }
                    
                    rs.close();
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar estudiantes: " + ex.getMessage());
        }
    }
    
    private void cargarCursos() {
        try {
            cmbCurso.removeAllItems();
            try (Connection conn = Conexion.getConexion()) {
                String sql = "SELECT cod_curso, nom_curso FROM cursos ORDER BY nom_curso";
                try (PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
                    
                    while (rs.next()) {
                        String item = rs.getInt("cod_curso") + " - " + rs.getString("nom_curso");
                        cmbCurso.addItem(item);
                    }
                    
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar cursos: " + ex.getMessage());
        }
    }
    
    private void matricularEstudiante() {
        if (validarCampos()) {
            try {
                int codEstudiante = obtenerCodigoEstudiante();
                int codCurso = obtenerCodigoCurso();
                float nota = txtNota.getText().isEmpty() ? 0.0f : Float.parseFloat(txtNota.getText());
                
                try (Connection conn = Conexion.getConexion()) {
                    String sql = "INSERT INTO matricula (cod_estudiante, cod_curso, nota_curso) VALUES (?, ?, ?)";
                    try (PreparedStatement pst = conn.prepareStatement(sql)) {
                        pst.setInt(1, codEstudiante);
                        pst.setInt(2, codCurso);
                        pst.setFloat(3, nota);
                        
                        int resultado = pst.executeUpdate();
                        if (resultado > 0) {
                            JOptionPane.showMessageDialog(this, "Estudiante matriculado correctamente");
                            limpiarCampos();
                            cargarDatos();
                        }
                    }
                }
            } catch (SQLException ex) {
                if (ex.getMessage().contains("Duplicate entry")) {
                    JOptionPane.showMessageDialog(this, "El estudiante ya está matriculado en este curso");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al matricular estudiante: " + ex.getMessage());
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "La nota debe ser un número válido");
            }
        }
    }
    
    private void eliminarMatricula() {
        int filaSeleccionada = tablaMatriculas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una matrícula para eliminar");
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar esta matrícula?", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                int codEstudiante = obtenerCodigoEstudiante();
                int codCurso = obtenerCodigoCurso();
                
                try (Connection conn = Conexion.getConexion()) {
                    String sql = "DELETE FROM matricula WHERE cod_estudiante = ? AND cod_curso = ?";
                    try (PreparedStatement pst = conn.prepareStatement(sql)) {
                        pst.setInt(1, codEstudiante);
                        pst.setInt(2, codCurso);
                        
                        int resultado = pst.executeUpdate();
                        if (resultado > 0) {
                            JOptionPane.showMessageDialog(this, "Matrícula eliminada correctamente");
                            limpiarCampos();
                            cargarDatos();
                        }
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar matrícula: " + ex.getMessage());
            }
        }
    }
    
    private void modificarMatricula() {
        if (validarCampos()) {
            try {
                int codEstudiante = obtenerCodigoEstudiante();
                int codCurso = obtenerCodigoCurso();
                float nota = txtNota.getText().isEmpty() ? 0.0f : Float.parseFloat(txtNota.getText());
                
                try (Connection conn = Conexion.getConexion()) {
                    String sql = "UPDATE matricula SET nota_curso = ? WHERE cod_estudiante = ? AND cod_curso = ?";
                    try (PreparedStatement pst = conn.prepareStatement(sql)) {
                        pst.setFloat(1, nota);
                        pst.setInt(2, codEstudiante);
                        pst.setInt(3, codCurso);
                        
                        int resultado = pst.executeUpdate();
                        if (resultado > 0) {
                            JOptionPane.showMessageDialog(this, "Matrícula modificada correctamente");
                            limpiarCampos();
                            cargarDatos();
                        } else {
                            JOptionPane.showMessageDialog(this, "No se encontró la matrícula a modificar");
                        }
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al modificar matrícula: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "La nota debe ser un número válido");
            }
        }
    }
    
    private void cargarDatos() {
        try {
            modeloTabla.setRowCount(0);
            
            try (Connection conn = Conexion.getConexion()) {
                String sql = "SELECT m.cod_estudiante, e.nom_estudiante, m.cod_curso, c.nom_curso, m.nota_curso " +
                        "FROM matricula m " +
                        "INNER JOIN estudiantes e ON m.cod_estudiante = e.cod_estudiante " +
                        "INNER JOIN cursos c ON m.cod_curso = c.cod_curso " +
                        "ORDER BY e.nom_estudiante, c.nom_curso";
                try (PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
                    
                    while (rs.next()) {
                        Object[] fila = {
                            rs.getInt("cod_estudiante"),
                            rs.getString("nom_estudiante"),
                            rs.getInt("cod_curso"),
                            rs.getString("nom_curso"),
                            rs.getFloat("nota_curso")
                        };
                        modeloTabla.addRow(fila);
                    }
                    
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + ex.getMessage());
        }
    }
    
    private int obtenerCodigoEstudiante() {
        String seleccion = (String) cmbEstudiante.getSelectedItem();
        return Integer.parseInt(seleccion.split(" - ")[0]);
    }
    
    private int obtenerCodigoCurso() {
        String seleccion = (String) cmbCurso.getSelectedItem();
        return Integer.parseInt(seleccion.split(" - ")[0]);
    }
    
    private boolean validarCampos() {
        if (cmbEstudiante.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un estudiante");
            return false;
        }
        
        if (cmbCurso.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un curso");
            return false;
        }
        
        if (!txtNota.getText().trim().isEmpty()) {
            try {
                float nota = Float.parseFloat(txtNota.getText().trim());
                if (nota < 0 || nota > 5) {
                    JOptionPane.showMessageDialog(this, "La nota debe estar entre 0 y 5");
                    return false;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "La nota debe ser un número válido");
                return false;
            }
        }
        
        return true;
    }
    
    private void limpiarCampos() {
        if (cmbEstudiante.getItemCount() > 0) {
            cmbEstudiante.setSelectedIndex(0);
        }
        if (cmbCurso.getItemCount() > 0) {
            cmbCurso.setSelectedIndex(0);
        }
        txtNota.setText("");
        tablaMatriculas.clearSelection();
    }
}
}
