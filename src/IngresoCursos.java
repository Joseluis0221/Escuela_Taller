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
 public class IngresoCursos extends JFrame {
    
    private JTextField txtCodigoCurso;
    private JTextField txtNombreCurso;
    private JComboBox<String> cmbDocentes;
    private JTable tablaCursos;
    private DefaultTableModel modeloTabla;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnSalir;
    
    private Connection conexion;
    private int filaSeleccionada = -1;
    
    public IngresoCursos() {
        initComponents();
        configurarVentana();
        cargarDocentes();
        cargarTablaCursos();
        configurarEventos();
        
        System.out.println("=== SISTEMA ESCUELA_TALLER ===");
        System.out.println("Módulo: Ingreso de Cursos");
        System.out.println("Fecha: " + new java.util.Date());
        System.out.println("Estado: Aplicación iniciada correctamente");
        System.out.println("================================");
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        JPanel panelSuperior = new JPanel(new GridBagLayout());
        panelSuperior.setBorder(BorderFactory.createTitledBorder("Datos del Curso"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panelSuperior.add(new JLabel("Código del Curso:"), gbc);
        gbc.gridx = 1;
        txtCodigoCurso = new JTextField(15);
        panelSuperior.add(txtCodigoCurso, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panelSuperior.add(new JLabel("Nombre del Curso:"), gbc);
        gbc.gridx = 1;
        txtNombreCurso = new JTextField(15);
        panelSuperior.add(txtNombreCurso, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panelSuperior.add(new JLabel("Docente:"), gbc);
        gbc.gridx = 1;
        cmbDocentes = new JComboBox<>();
        cmbDocentes.setPreferredSize(new Dimension(200, 25));
        panelSuperior.add(cmbDocentes, gbc);
        
        add(panelSuperior, BorderLayout.NORTH);
        
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBorder(BorderFactory.createTitledBorder("Lista de Cursos"));
        
        String[] columnas = {"Código", "Nombre del Curso", "Docente"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaCursos = new JTable(modeloTabla);
        tablaCursos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCursos.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(tablaCursos);
        scrollPane.setPreferredSize(new Dimension(600, 200));
        panelCentral.add(scrollPane, BorderLayout.CENTER);
        
        add(panelCentral, BorderLayout.CENTER);
        
        JPanel panelInferior = new JPanel(new FlowLayout());
        
        btnGuardar = new JButton("Guardar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");
        btnSalir = new JButton("Salir");
        
        btnGuardar.setBackground(new Color(46, 125, 50));
        btnGuardar.setForeground(Color.WHITE);
        
        btnActualizar.setBackground(new Color(255, 152, 0));
        btnActualizar.setForeground(Color.WHITE);
        
        btnEliminar.setBackground(new Color(211, 47, 47));
        btnEliminar.setForeground(Color.WHITE);
        
        btnLimpiar.setBackground(new Color(158, 158, 158));
        btnLimpiar.setForeground(Color.WHITE);
        
        btnSalir.setBackground(new Color(69, 90, 100));
        btnSalir.setForeground(Color.WHITE);
        
        panelInferior.add(btnGuardar);
        panelInferior.add(btnActualizar);
        panelInferior.add(btnEliminar);
        panelInferior.add(btnLimpiar);
        panelInferior.add(btnSalir);
        
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    private void configurarVentana() {
        setTitle("Sistema Escuela_Taller - Ingreso de Cursos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setResizable(true);
        
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
        } catch (Exception e) {
        }
    }
    
    private void configurarEventos() {
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarCurso();
            }
        });
        
        btnActualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarCurso();
            }
        });
        
        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarCurso();
            }
        });
        
        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarCampos();
            }
        });
        
        btnSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        tablaCursos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                filaSeleccionada = tablaCursos.getSelectedRow();
                if (filaSeleccionada != -1) {
                    cargarDatosSeleccionados();
                }
            }
        });
    }
    
    private void cargarDocentes() {
        try {
            conexion = Conexion.getConexion();
            String sql = "SELECT cod_docente, nom_docente FROM docentes ORDER BY nom_docente";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            cmbDocentes.removeAllItems();
            cmbDocentes.addItem("-- Seleccionar Docente --");
            
            while (rs.next()) {
                String item = rs.getInt("cod_docente") + " - " + rs.getString("nom_docente");
                cmbDocentes.addItem(item);
            }
            
            rs.close();
            ps.close();
            
            System.out.println("Docentes cargados correctamente en ComboBox");
            
        } catch (SQLException e) {
            System.err.println("Error al cargar docentes: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Error al cargar la lista de docentes: " + e.getMessage(),
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarTablaCursos() {
        try {
            conexion = Conexion.getConexion();
            String sql = "SELECT c.cod_curso, c.nom_curso, d.nom_docente " +
                        "FROM cursos c " +
                        "INNER JOIN docentes d ON c.cod_docente = d.cod_docente " +
                        "ORDER BY c.cod_curso";
            
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            modeloTabla.setRowCount(0);
            
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("cod_curso"),
                    rs.getString("nom_curso"),
                    rs.getString("nom_docente")
                };
                modeloTabla.addRow(fila);
            }
            
            rs.close();
            ps.close();
            
            System.out.println("Tabla de cursos actualizada - Total registros: " + modeloTabla.getRowCount());
            
        } catch (SQLException e) {
            System.err.println("Error al cargar tabla de cursos: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Error al cargar los datos de cursos: " + e.getMessage(),
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarDatosSeleccionados() {
        if (filaSeleccionada != -1) {
            int codigo = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
            String nombre = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
            String docente = (String) modeloTabla.getValueAt(filaSeleccionada, 2);
            
            txtCodigoCurso.setText(String.valueOf(codigo));
            txtNombreCurso.setText(nombre);
            
            for (int i = 0; i < cmbDocentes.getItemCount(); i++) {
                String item = cmbDocentes.getItemAt(i);
                if (item.contains(docente)) {
                    cmbDocentes.setSelectedIndex(i);
                    break;
                }
            }
            
            System.out.println("Curso seleccionado cargado: " + codigo + " - " + nombre);
        }
    }
    
    private void guardarCurso() {
        if (!validarCampos()) {
            return;
        }
        
        try {
            int codigo = Integer.parseInt(txtCodigoCurso.getText().trim());
            String nombre = txtNombreCurso.getText().trim();
            int codigoDocente = obtenerCodigoDocenteSeleccionado();
            
            if (existeCurso(codigo)) {
                JOptionPane.showMessageDialog(this, 
                    "Ya existe un curso con el código " + codigo,
                    "Código Duplicado", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            conexion = Conexion.getConexion();
            String sql = "INSERT INTO cursos (cod_curso, nom_curso, cod_docente) VALUES (?, ?, ?)";
            PreparedStatement ps = conexion.prepareStatement(sql);
            
            ps.setInt(1, codigo);
            ps.setString(2, nombre);
            ps.setInt(3, codigoDocente);
            
            int resultado = ps.executeUpdate();
            
            if (resultado > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Curso guardado exitosamente",
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                System.out.println("CURSO GUARDADO:");
                System.out.println("Código: " + codigo);
                System.out.println("Nombre: " + nombre);
                System.out.println("Docente ID: " + codigoDocente);
                System.out.println("Timestamp: " + new java.util.Date());
                
                limpiarCampos();
                cargarTablaCursos();
            }
            
            ps.close();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "El código del curso debe ser un número válido",
                "Error de Formato", 
                JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            System.err.println("Error al guardar curso: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Error al guardar el curso: " + e.getMessage(),
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarCurso() {
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un curso de la tabla para actualizar",
                "Selección Requerida", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validarCampos()) {
            return;
        }
        
        try {
            int codigo = Integer.parseInt(txtCodigoCurso.getText().trim());
            String nombre = txtNombreCurso.getText().trim();
            int codigoDocente = obtenerCodigoDocenteSeleccionado();
            
            conexion = Conexion.getConexion();
            String sql = "UPDATE cursos SET nom_curso = ?, cod_docente = ? WHERE cod_curso = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            
            ps.setString(1, nombre);
            ps.setInt(2, codigoDocente);
            ps.setInt(3, codigo);
            
            int resultado = ps.executeUpdate();
            
            if (resultado > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Curso actualizado exitosamente",
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                System.out.println("CURSO ACTUALIZADO:");
                System.out.println("Código: " + codigo);
                System.out.println("Nuevo nombre: " + nombre);
                System.out.println("Nuevo docente ID: " + codigoDocente);
                System.out.println("Timestamp: " + new java.util.Date());
                
                limpiarCampos();
                cargarTablaCursos();
            }
            
            ps.close();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "El código del curso debe ser un número válido",
                "Error de Formato", 
                JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            System.err.println("Error al actualizar curso: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Error al actualizar el curso: " + e.getMessage(),
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarCurso() {
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un curso de la tabla para eliminar",
                "Selección Requerida", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int codigo = Integer.parseInt(txtCodigoCurso.getText());
        String nombre = txtNombreCurso.getText();
        
        int opcion = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar el curso:\n" + codigo + " - " + nombre + "?",
            "Confirmar Eliminación", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                conexion = Conexion.getConexion();
                String sql = "DELETE FROM cursos WHERE cod_curso = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                
                ps.setInt(1, codigo);
                
                int resultado = ps.executeUpdate();
                
                if (resultado > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Curso eliminado exitosamente",
                        "Éxito", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    System.out.println("CURSO ELIMINADO:");
                    System.out.println("Código: " + codigo);
                    System.out.println("Nombre: " + nombre);
                    System.out.println("Timestamp: " + new java.util.Date());
                    
                    limpiarCampos();
                    cargarTablaCursos();
                }
                
                ps.close();
                
            } catch (SQLException e) {
                System.err.println("Error al eliminar curso: " + e.getMessage());
                
                if (e.getMessage().contains("foreign key constraint")) {
                    JOptionPane.showMessageDialog(this, 
                        "No se puede eliminar el curso porque tiene estudiantes matriculados",
                        "Error de Integridad", 
                        JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Error al eliminar el curso: " + e.getMessage(),
                        "Error de Base de Datos", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void limpiarCampos() {
        txtCodigoCurso.setText("");
        txtNombreCurso.setText("");
        cmbDocentes.setSelectedIndex(0);
        filaSeleccionada = -1;
        tablaCursos.clearSelection();
        
        System.out.println("Campos del formulario limpiados");
    }
    
    private boolean validarCampos() {
        if (txtCodigoCurso.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Ingrese el código del curso",
                "Campo Requerido", 
                JOptionPane.WARNING_MESSAGE);
            txtCodigoCurso.requestFocus();
            return false;
        }
        
        if (txtNombreCurso.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Ingrese el nombre del curso",
                "Campo Requerido", 
                JOptionPane.WARNING_MESSAGE);
            txtNombreCurso.requestFocus();
            return false;
        }
        
        if (cmbDocentes.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un docente",
                "Campo Requerido", 
                JOptionPane.WARNING_MESSAGE);
            cmbDocentes.requestFocus();
            return false;
        }
        
        try {
            Integer.parseInt(txtCodigoCurso.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "El código del curso debe ser un número válido",
                "Error de Formato", 
                JOptionPane.ERROR_MESSAGE);
            txtCodigoCurso.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private int obtenerCodigoDocenteSeleccionado() {
        String seleccion = (String) cmbDocentes.getSelectedItem();
        if (seleccion != null && !seleccion.startsWith("--")) {
            return Integer.parseInt(seleccion.split(" - ")[0]);
        }
        return -1;
    }
    
    private boolean existeCurso(int codigo) {
        try {
            conexion = Conexion.getConexion();
            String sql = "SELECT COUNT(*) FROM cursos WHERE cod_curso = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, codigo);
            
            ResultSet rs = ps.executeQuery();
            boolean existe = false;
            
            if (rs.next()) {
                existe = rs.getInt(1) > 0;
            }
            
            rs.close();
            ps.close();
            
            return existe;
            
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia del curso: " + e.getMessage());
            return false;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                new IngresoCursos().setVisible(true);
            }
        });
    }
    
    public IngresoCursos(boolean soloListado) {
    this();
    txtCodigoCurso.setVisible(false);
    txtNombreCurso.setVisible(false);
    cmbDocentes.setVisible(false);
    btnGuardar.setVisible(false);
    btnActualizar.setVisible(false);
    btnEliminar.setVisible(false);
    btnLimpiar.setVisible(false);
    btnSalir.setText("Cerrar");
    setTitle("Listado de Cursos");

    }

    
}

