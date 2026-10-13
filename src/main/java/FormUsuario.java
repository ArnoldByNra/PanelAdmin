import javax.swing.*;
import java.awt.*;

public class FormUsuario extends JDialog {
    boolean guardado = false;
    Usuario original;

    JTextField txtUsuario = new JTextField(), txtNombre = new JTextField(), txtApPat = new JTextField(),
            txtApMat = new JTextField(), txtEmail = new JTextField(), txtFechaNac = new JTextField(),
            txtPuesto = new JTextField(), txtDepto = new JTextField(), txtFechaIng = new JTextField(),
            txtDireccion = new JTextField(), txtCurp = new JTextField(), txtTel = new JTextField(),
            txtSalario = new JTextField();
    JPasswordField txtPassword = new JPasswordField();
    JComboBox<String> cmbRol = new JComboBox<>(new String[]{"EMPLEADO", "ADMINISTRADOR"});
    JComboBox<String> cmbGenero = new JComboBox<>(new String[]{"Masculino", "Femenino", "Otro"});

    FormUsuario(JFrame padre, Usuario u) {
        super(padre, u == null ? "Nuevo usuario" : "Editar usuario", true);
        original = u;

        JPanel campos = new JPanel(new GridLayout(0, 2, 8, 6));
        campos.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        fila(campos, "Usuario:", txtUsuario);
        fila(campos, u == null ? "Contraseña:" : "Contraseña (vacía = no cambiar):", txtPassword);
        fila(campos, "Rol:", cmbRol);
        fila(campos, "Nombre:", txtNombre);
        fila(campos, "Apellido paterno:", txtApPat);
        fila(campos, "Apellido materno:", txtApMat);
        fila(campos, "Correo:", txtEmail);
        fila(campos, "Fecha nacimiento (aaaa-mm-dd):", txtFechaNac);
        fila(campos, "Género:", cmbGenero);
        fila(campos, "Puesto:", txtPuesto);
        fila(campos, "Departamento:", txtDepto);
        fila(campos, "Fecha ingreso (aaaa-mm-dd):", txtFechaIng);
        fila(campos, "Dirección:", txtDireccion);
        fila(campos, "CURP (cifrado):", txtCurp);
        fila(campos, "Teléfono (cifrado):", txtTel);
        fila(campos, "Salario (cifrado):", txtSalario);

        if (u != null) {
            txtUsuario.setText(u.username); cmbRol.setSelectedItem(u.rol);
            txtNombre.setText(u.nombre); txtApPat.setText(u.apPaterno); txtApMat.setText(u.apMaterno);
            txtEmail.setText(u.email); txtFechaNac.setText(u.fechaNac); cmbGenero.setSelectedItem(u.genero);
            txtPuesto.setText(u.puesto); txtDepto.setText(u.departamento); txtFechaIng.setText(u.fechaIngreso);
            txtDireccion.setText(u.direccion); txtCurp.setText(u.curp); txtTel.setText(u.telefono);
            txtSalario.setText(u.salario);
        }

        JButton guardar = new JButton("Guardar");
        JButton cancelar = new JButton("Cancelar");
        guardar.addActionListener(e -> guardar());
        cancelar.addActionListener(e -> dispose());
        JPanel botones = new JPanel();
        botones.add(guardar);
        botones.add(cancelar);

        add(new JScrollPane(campos), BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
        setSize(520, 640);
        setLocationRelativeTo(padre);
    }

    void fila(JPanel p, String etiqueta, JComponent campo) {
        p.add(new JLabel(etiqueta));
        p.add(campo);
    }

    void guardar() {
        Usuario u = new Usuario();
        u.id = original == null ? 0 : original.id;
        u.username = txtUsuario.getText().trim();
        u.rol = (String) cmbRol.getSelectedItem();
        u.nombre = txtNombre.getText().trim();
        u.apPaterno = txtApPat.getText().trim();
        u.apMaterno = txtApMat.getText().trim();
        u.email = txtEmail.getText().trim();
        u.fechaNac = txtFechaNac.getText().trim();
        u.genero = (String) cmbGenero.getSelectedItem();
        u.puesto = txtPuesto.getText().trim();
        u.departamento = txtDepto.getText().trim();
        u.fechaIngreso = txtFechaIng.getText().trim();
        u.direccion = txtDireccion.getText().trim();
        u.curp = txtCurp.getText().trim().toUpperCase();
        u.telefono = txtTel.getText().trim();
        u.salario = txtSalario.getText().trim();
        String password = new String(txtPassword.getPassword());

        String error = u.validar(password, original == null);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Dato no válido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            UsuarioDao.guardar(u, password);
            guardado = true;
            dispose();
        } catch (java.sql.SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Ese nombre de usuario ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
