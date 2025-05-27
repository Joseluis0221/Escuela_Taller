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
public class Calificar extends JFrame {
    private JComboBox<String> cmbCurso;
    private JTable tablaNotas;
    private DefaultTableModel modeloTabla;
    private JButton btnGuardar, btnActualizar, btnLimpiar;
    private Conexion conexion;

    public Calificar() {
        super("Calificar Estudiantes");
        conexion = new Conexion();
        initComponents();
        cargarCursos();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel panelSuperior = new JPanel(new FlowLayout());
        panelSuperior.add(new JLabel("Seleccionar Curso:"));
        cmbCurso = new JComboBox<>();
        cmbCurso.setPreferredSize(new Dimension(250, 25));
        panelSuperior.add(cmbCurso);
        JButton btnCargar = new JButton("Cargar Estudiantes");
        panelSuperior.add(btnCargar);
        add(panelSuperior, BorderLayout.NORTH);

        String[] columnas = {"Código", "Estudiante", "Curso", "Nota Actual", "Nueva Nota"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3 || columnIndex == 4) {
                    return Float.class;
                }
                return String.class;
            }
        };

        tablaNotas = new JTable(modeloTabla);
        tablaNotas.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaNotas.getColumnModel().getColumn(1).setPreferredWidth(150);
        tablaNotas.getColumnModel().getColumn(2).setPreferredWidth(150);
        tablaNotas.getColumnModel().getColumn(3).setPreferredWidth(100);
        tablaNotas.getColumnModel().getColumn(4).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(tablaNotas);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout());
        btnGuardar = new JButton("Guardar Notas");
        btnActualizar = new JButton("Actualizar Vista");
        btnLimpiar = new JButton("Limpiar Notas");
        panelInferior.add(btnGuardar);
        panelInferior.add(btnActualizar);
        panelInferior.add(btnLimpiar);
        add(panelInferior, BorderLayout.SOUTH);

        btnCargar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarEstudiantesPorCurso();
            }
        });

        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarNotas();
            }
        });

        btnActualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarEstudiantesPorCurso();
            }
        });

        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarNotas();
            }
        });
    }

    private void cargarCursos() {
        try {
            Connection conn = conexion.getConexion();
            String sql = "SELECT DISTINCT nom_curso FROM cursos";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            cmbCurso.removeAllItems();
            while (rs.next()) {
                cmbCurso.addItem(rs.getString("nom_curso"));
            }

            rs.close();
            pst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar cursos: " + e.getMessage());
        }
    }

    private void cargarEstudiantesPorCurso() {
        String cursoSeleccionado = (String) cmbCurso.getSelectedItem();
        if (cursoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un curso");
            return;
        }

        try {
            Connection conn = conexion.getConexion();
            String sql = "SELECT e.cod_estudiante, e.nom_estudiante, c.nom_curso, m.nota_curso " +
                        "FROM estudiantes e " +
                        "INNER JOIN matricula m ON e.cod_estudiante = m.cod_estudiante " +
                        "INNER JOIN cursos c ON m.cod_curso = c.cod_curso " +
                        "WHERE c.nom_curso = ?";

            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, cursoSeleccionado);
            ResultSet rs = pst.executeQuery();

            modeloTabla.setRowCount(0);

            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("cod_estudiante"),
                    rs.getString("nom_estudiante"),
                    rs.getString("nom_curso"),
                    rs.getObject("nota_curso"),
                    null
                };
                modeloTabla.addRow(fila);
            }

            rs.close();
            pst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar estudiantes: " + e.getMessage());
        }
    }

    private void guardarNotas() {
        try {
            Connection conn = conexion.getConexion();
            String sqlUpdate = "UPDATE matricula SET nota_curso = ? WHERE cod_estudiante = ? AND cod_curso = ?";
            String sqlGetCursoId = "SELECT cod_curso FROM cursos WHERE nom_curso = ?";

            PreparedStatement pstCurso = conn.prepareStatement(sqlGetCursoId);
            pstCurso.setString(1, (String) cmbCurso.getSelectedItem());
            ResultSet rsCurso = pstCurso.executeQuery();

            int codigoCurso = 0;
            if (rsCurso.next()) {
                codigoCurso = rsCurso.getInt("cod_curso");
            }

            PreparedStatement pstUpdate = conn.prepareStatement(sqlUpdate);

            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                Object nuevaNota = modeloTabla.getValueAt(i, 4);
                if (nuevaNota != null && !nuevaNota.toString().trim().isEmpty()) {
                    int codigoEstudiante = (Integer) modeloTabla.getValueAt(i, 0);

                    pstUpdate.setFloat(1, Float.parseFloat(nuevaNota.toString()));
                    pstUpdate.setInt(2, codigoEstudiante);
                    pstUpdate.setInt(3, codigoCurso);
                    pstUpdate.addBatch();
                }
            }

            pstUpdate.executeBatch();
            pstUpdate.close();
            pstCurso.close();
            rsCurso.close();

            JOptionPane.showMessageDialog(this, "Notas guardadas exitosamente");
            cargarEstudiantesPorCurso();

        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar notas: " + e.getMessage());
        }
    }

    private void limpiarNotas() {
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            modeloTabla.setValueAt(null, i, 4);
        }
    }
}
