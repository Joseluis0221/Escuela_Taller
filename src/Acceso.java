/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 *
 * @author jhuila
 */
public class Acceso extends JFrame implements ActionListener {
    
    private JMenuBar barraMenu;
    private JMenu menuIngresar, menuMatricula, menuNotas, menuListados, menuHelp;
    private JMenuItem itemEstudiantes, itemDocentes, itemCursos;
    private JMenuItem itemMatricular, itemCalificar;
    private JMenuItem itemListarEstudiantes, itemListarDocentes, itemListarCursos;
    private JMenuItem itemAyuda, itemAcerca;
    
    private JPanel panelPrincipal;
    private JLabel lblTitulo, lblSubtitulo;
    
    public Acceso() {
        initComponents();
        configurarVentana();
        System.out.println("Ventana principal inicializada");
    }
    
    private void initComponents() {
        barraMenu = new JMenuBar();
        
        menuIngresar = new JMenu("Ingresar");
        menuIngresar.setMnemonic('I');
        
        itemEstudiantes = new JMenuItem("Estudiantes", 'E');
        itemDocentes = new JMenuItem("Docentes", 'D');
        itemCursos = new JMenuItem("Cursos", 'C');
        
        itemEstudiantes.addActionListener(this);
        itemDocentes.addActionListener(this);
        itemCursos.addActionListener(this);
        
        menuIngresar.add(itemEstudiantes);
        menuIngresar.add(itemDocentes);
        menuIngresar.add(itemCursos);
        
        menuMatricula = new JMenu("Matrícula");
        menuMatricula.setMnemonic('M');
        
        itemMatricular = new JMenuItem("Matricular Estudiante", 'M');
        itemMatricular.addActionListener(this);
        menuMatricula.add(itemMatricular);
        
        menuNotas = new JMenu("Notas");
        menuNotas.setMnemonic('N');
        
        itemCalificar = new JMenuItem("Calificar", 'C');
        itemCalificar.addActionListener(this);
        menuNotas.add(itemCalificar);
        
        menuListados = new JMenu("Listados");
        menuListados.setMnemonic('L');
        
        itemListarEstudiantes = new JMenuItem("Lista Estudiantes", 'E');
        itemListarDocentes = new JMenuItem("Lista Docentes", 'D');
        itemListarCursos = new JMenuItem("Lista Cursos", 'C');
        
        itemListarEstudiantes.addActionListener(this);
        itemListarDocentes.addActionListener(this);
        itemListarCursos.addActionListener(this);
        
        menuListados.add(itemListarEstudiantes);
        menuListados.add(itemListarDocentes);
        menuListados.add(itemListarCursos);
        
        menuHelp = new JMenu("Help");
        menuHelp.setMnemonic('H');
        
        itemAyuda = new JMenuItem("Ayuda", 'A');
        itemAcerca = new JMenuItem("Acerca de...", 'c');
        
        itemAyuda.addActionListener(this);
        itemAcerca.addActionListener(this);
        
        menuHelp.add(itemAyuda);
        menuHelp.add(itemAcerca);
        
        barraMenu.add(menuIngresar);
        barraMenu.add(menuMatricula);
        barraMenu.add(menuNotas);
        barraMenu.add(menuListados);
        barraMenu.add(menuHelp);
        
        setJMenuBar(barraMenu);
        
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout());
        panelPrincipal.setBackground(new Color(240, 248, 255));
        
        lblTitulo = new JLabel("Sistema de Información Académica", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(25, 25, 112));
        
        lblSubtitulo = new JLabel("Gestión Académica", JLabel.CENTER);
        lblSubtitulo.setFont(new Font("Arial", Font.ITALIC, 16));
        lblSubtitulo.setForeground(new Color(70, 130, 180));
        
        JPanel panelTitulos = new JPanel(new GridLayout(2, 1, 0, 10));
        panelTitulos.setBackground(new Color(240, 248, 255));
        panelTitulos.add(lblTitulo);
        panelTitulos.add(lblSubtitulo);
        
        panelPrincipal.add(panelTitulos, BorderLayout.CENTER);
        
        add(panelPrincipal);
    }
    
    private void configurarVentana() {
        setTitle("Información Académica - Sistema de Gestión");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage("icono.png"));
        } catch (Exception e) {
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        
        System.out.println("Acción ejecutada: " + comando);
        
        try {
            switch (comando) {
                case "Estudiantes" -> new IngresoEstudiantes().setVisible(true);
                    
                case "Docentes" -> new IngresoDocentes().setVisible(true);
                    
                case "Cursos" -> new IngresoCursos().setVisible(true);
                    
                case "Matricular Estudiante" -> new Matriculas().setVisible(true);
                    
                case "Calificar" -> new Calificar().setVisible(true);
                    
                case "Lista Estudiantes" -> mostrarListaEstudiantes();
                    
                case "Lista Docentes" -> mostrarListaDocentes();
                    
                case "Lista Cursos" -> mostrarListaCursos();
                    
                case "Ayuda" -> mostrarAyuda();
                    
                case "Acerca de..." -> mostrarAcercaDe();
                    
                default -> System.out.println("Acción no reconocida: " + comando);
            }
        } catch (Exception ex) {
            System.err.println("Error al ejecutar acción: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Error al ejecutar la acción: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mostrarListaEstudiantes() {
        JFrame ventanaListado = new JFrame("Lista de Estudiantes");
        
        String[] columnas = {"Código", "Nombre"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        
        try {
            Connection conn = Conexion.getConexion();
            String sql = "SELECT cod_estudiante, nom_estudiante FROM estudiantes ORDER BY cod_estudiante";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("cod_estudiante"),
                    rs.getString("nom_estudiante")
                };
                modelo.addRow(fila);
            }
            
            rs.close();
            ps.close();
            
            System.out.println("Lista de estudiantes cargada - Total: " + modelo.getRowCount() + " registros");
            
        } catch (SQLException ex) {
            System.err.println("Error al cargar estudiantes: " + ex.getMessage());
            JOptionPane.showMessageDialog(ventanaListado, 
                "Error al cargar los datos: " + ex.getMessage(),
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
        }
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblTitulo = new JLabel("Lista Completa de Estudiantes", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> ventanaListado.dispose());
        
        JPanel panelBoton = new JPanel(new FlowLayout());
        panelBoton.add(btnCerrar);
        panelPrincipal.add(panelBoton, BorderLayout.SOUTH);
        
        ventanaListado.add(panelPrincipal);
        ventanaListado.setSize(600, 400);
        ventanaListado.setLocationRelativeTo(this);
        ventanaListado.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventanaListado.setVisible(true);
    }
    
    private void mostrarListaDocentes() {
        JFrame ventanaListado = new JFrame("Lista de Docentes");
        
        String[] columnas = {"Código", "Nombre"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        
        try {
            Connection conn = Conexion.getConexion();
            String sql = "SELECT cod_docente, nom_docente FROM docentes ORDER BY cod_docente";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("cod_docente"),
                    rs.getString("nom_docente")
                };
                modelo.addRow(fila);
            }
            
            rs.close();
            ps.close();
            
            System.out.println("Lista de docentes cargada - Total: " + modelo.getRowCount() + " registros");
            
        } catch (SQLException ex) {
            System.err.println("Error al cargar docentes: " + ex.getMessage());
            JOptionPane.showMessageDialog(ventanaListado, 
                "Error al cargar los datos: " + ex.getMessage(),
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
        }
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblTitulo = new JLabel("Lista Completa de Docentes", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> ventanaListado.dispose());
        
        JPanel panelBoton = new JPanel(new FlowLayout());
        panelBoton.add(btnCerrar);
        panelPrincipal.add(panelBoton, BorderLayout.SOUTH);
        
        ventanaListado.add(panelPrincipal);
        ventanaListado.setSize(600, 400);
        ventanaListado.setLocationRelativeTo(this);
        ventanaListado.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventanaListado.setVisible(true);
    }
    
    private void mostrarListaCursos() {
        JFrame ventanaListado = new JFrame("Lista de Cursos");
        
        String[] columnas = {"Código", "Nombre del Curso", "Docente"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        
        try {
            Connection conn = Conexion.getConexion();
            String sql = "SELECT c.cod_curso, c.nom_curso, d.nom_docente " +
                        "FROM cursos c " +
                        "INNER JOIN docentes d ON c.cod_docente = d.cod_docente " +
                        "ORDER BY c.cod_curso";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("cod_curso"),
                    rs.getString("nom_curso"),
                    rs.getString("nom_docente")
                };
                modelo.addRow(fila);
            }
            
            rs.close();
            ps.close();
            
            System.out.println("Lista de cursos cargada - Total: " + modelo.getRowCount() + " registros");
            
        } catch (SQLException ex) {
            System.err.println("Error al cargar cursos: " + ex.getMessage());
            JOptionPane.showMessageDialog(ventanaListado, 
                "Error al cargar los datos: " + ex.getMessage(),
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
        }
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblTitulo = new JLabel("Lista Completa de Cursos", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> ventanaListado.dispose());
        
        JPanel panelBoton = new JPanel(new FlowLayout());
        panelBoton.add(btnCerrar);
        panelPrincipal.add(panelBoton, BorderLayout.SOUTH);
        
        ventanaListado.add(panelPrincipal);
        ventanaListado.setSize(700, 400);
        ventanaListado.setLocationRelativeTo(this);
        ventanaListado.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventanaListado.setVisible(true);
    }
    
    private void mostrarAyuda() {
        String mensaje = """
                         SISTEMA DE INFORMACIÓN ACADÉMICA
                         
                         MENÚS DISPONIBLES:
                         
                         • Ingresar: Registro de estudiantes, docentes y cursos
                         • Matrícula: Matricular estudiantes en cursos
                         • Notas: Calificar estudiantes
                         • Listados: Ver listas de estudiantes, docentes y cursos
                         • Help: Ayuda y acerca de
                         
                         Desarrollado con Java Swing + MySQL""";
        
        JOptionPane.showMessageDialog(this, mensaje, "Ayuda del Sistema", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarAcercaDe() {
        String mensaje = """
                  SISTEMA DE INFORMACIÓN ACADÉMICA
                  Versión: 1.0
                  Desarrollado en: Java + Swing + MySQL
                  IDE: NetBeans 22
                  JDK: 22
                  Base de datos: MySQL (escuela_taller)
                  © 2024 - Taller Práctico POO""";
        
        JOptionPane.showMessageDialog(this, mensaje, "Acerca del Sistema", JOptionPane.INFORMATION_MESSAGE);
    }
}