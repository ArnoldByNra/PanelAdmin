import javax.swing.*;
import java.awt.*;

public class Login extends JFrame {
    JTextField txtUsuario = new JTextField(15);
    JPasswordField txtPassword = new JPasswordField(15);

    Login() {
        super("Iniciar sesión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel p = new JPanel(new GridLayout(0, 1, 6, 6));
        p.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        JLabel titulo = new JLabel("Iniciar sesión", JLabel.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        JButton btn = new JButton("Entrar");
        p.add(titulo);
        p.add(new JLabel("Usuario:"));
        p.add(txtUsuario);
        p.add(new JLabel("Contraseña:"));
        p.add(txtPassword);
        p.add(btn);
        add(p);
        getRootPane().setDefaultButton(btn);
        btn.addActionListener(e -> entrar());
        pack();
        setLocationRelativeTo(null);
    }

    void entrar() {
        String u = txtUsuario.getText().trim();
        String p = new String(txtPassword.getPassword());
        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Escriba usuario y contraseña.");
            return;
        }
        try {
            Usuario sesion = UsuarioDao.login(u, p);
            if (sesion == null) {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            new Principal(sesion).setVisible(true);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UsuarioDao.iniciar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "No se pudo conectar a la base de datos.\nRevise config.properties.\n\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
