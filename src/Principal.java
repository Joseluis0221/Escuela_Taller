/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import javax.swing.*;
import java.awt.*;

//para el inicion de sesion, usuario: (Admin) y el password. (1234)

/**
 *
 * @author jhuila
 */
public class Principal extends JFrame {

    private final JTextField txtUsuario;
    private final JPasswordField txtClave;
    private final JButton btnEntrar;
    private final JButton btnCancelar;

    public Principal() {
        setTitle("Inicio de Sesión");
        setSize(400, 260);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(lblUsuario, gbc);

        txtUsuario = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(txtUsuario, gbc);

        JLabel lblClave = new JLabel("Contraseña:");
        lblClave.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(lblClave, gbc);

        txtClave = new JPasswordField(20);
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(txtClave, gbc);

        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(Color.WHITE);
        btnEntrar = new JButton("Entrar");
        btnEntrar.setBackground(new Color(46, 125, 50));
        btnEntrar.setForeground(Color.WHITE);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(211, 47, 47));
        btnCancelar.setForeground(Color.WHITE);

        panelBotones.add(btnEntrar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(panelBotones, gbc);

        btnEntrar.addActionListener(e -> autenticar());
        btnCancelar.addActionListener(e -> System.exit(0));
        getRootPane().setDefaultButton(btnEntrar);

        setVisible(true);
    }

    private void autenticar() {
        String usuario = txtUsuario.getText().trim();
        String clave = new String(txtClave.getPassword());

        if (usuario.equals("Admin") && clave.equals("1234")) {
            dispose();
            new Acceso().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos", "Error de Acceso", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Principal());
    }
    
}