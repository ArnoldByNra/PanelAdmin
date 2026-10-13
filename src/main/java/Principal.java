import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class Principal extends JFrame {
    static final String[] COLUMNAS = {"ID", "Usuario", "Rol", "Nombre", "Ap. paterno", "Ap. materno", "Correo",
            "Nacimiento", "Género", "Puesto", "Departamento", "Ingreso", "Dirección", "CURP*", "Teléfono*", "Salario*"};

    Usuario sesion;
    boolean admin;
    List<Usuario> usuarios;
    DefaultTableModel modelo = new DefaultTableModel(COLUMNAS, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    JTable tabla = new JTable(modelo);

    Principal(Usuario sesion) {
        super("Usuarios — " + sesion.nombre + " (" + sesion.rol + ")");
        this.sesion = sesion;
        this.admin = sesion.rol.equals("ADMINISTRADOR");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabla.setRowHeight(24);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        if (admin) {
            JButton nuevo = new JButton("Nuevo");
            JButton editar = new JButton("Editar");
            JButton eliminar = new JButton("Eliminar");
            nuevo.addActionListener(e -> nuevo());
            editar.addActionListener(e -> editar());
            eliminar.addActionListener(e -> eliminar());
            botones.add(nuevo); botones.add(editar); botones.add(eliminar);
        }
        JButton salir = new JButton("Salir");
        salir.addActionListener(e -> System.exit(0));
        botones.add(salir);

        add(botones, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(new JLabel(admin ? " * datos cifrados en la base de datos"
                : " * datos cifrados: su rol (empleado) no puede verlos"), BorderLayout.SOUTH);
        setSize(1100, 500);
        setLocationRelativeTo(null);
        cargar();
    }

    void cargar() {
        try {
            usuarios = UsuarioDao.listar(admin);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            return;
        }
        modelo.setRowCount(0);
        for (Usuario u : usuarios) {
            modelo.addRow(new Object[]{u.id, u.username, u.rol, u.nombre, u.apPaterno, u.apMaterno, u.email, u.fechaNac,
                    u.genero, u.puesto, u.departamento, u.fechaIngreso, u.direccion,
                    admin ? u.curp : "(restringido)", admin ? u.telefono : "(restringido)", admin ? u.salario : "(restringido)"});
        }
    }

    Usuario seleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla.");
            return null;
        }
        return usuarios.get(fila);
    }

    void nuevo() {
        FormUsuario f = new FormUsuario(this, null);
        f.setVisible(true);
        if (f.guardado) cargar();
    }

    void editar() {
        Usuario u = seleccionado();
        if (u == null) return;
        FormUsuario f = new FormUsuario(this, u);
        f.setVisible(true);
        if (f.guardado) cargar();
    }

    void eliminar() {
        Usuario u = seleccionado();
        if (u == null) return;
        if (u.id == sesion.id) {
            JOptionPane.showMessageDialog(this, "No puede eliminar su propio usuario.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar a " + u.username + "?", "Confirmar",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            UsuarioDao.eliminar(u.id);
            cargar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
