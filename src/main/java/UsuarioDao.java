import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UsuarioDao {
    static String url, user, pass, aes;

    static final String COLS = "id, username, rol, nombre, ap_paterno, ap_materno, email, fecha_nac, genero, "
            + "puesto, departamento, fecha_ingreso, direccion";

    static void iniciar() throws Exception {
        Properties p = new Properties();
        try (FileInputStream f = new FileInputStream("config.properties")) {
            p.load(new InputStreamReader(f, StandardCharsets.UTF_8));
        }
        url = p.getProperty("db.url"); user = p.getProperty("db.user");
        pass = p.getProperty("db.password"); aes = p.getProperty("aes.key");

        try (Connection c = DriverManager.getConnection(url, user, pass); Statement st = c.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS usuarios ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "username VARCHAR(20) NOT NULL UNIQUE, "
                    + "password_hash CHAR(64) NOT NULL, salt CHAR(32) NOT NULL, "
                    + "rol VARCHAR(15) NOT NULL, "
                    + "nombre VARCHAR(50) NOT NULL, ap_paterno VARCHAR(50) NOT NULL, ap_materno VARCHAR(50), "
                    + "email VARCHAR(100) NOT NULL, fecha_nac DATE NOT NULL, genero VARCHAR(15) NOT NULL, "
                    + "puesto VARCHAR(60) NOT NULL, departamento VARCHAR(60) NOT NULL, fecha_ingreso DATE NOT NULL, "
                    + "direccion VARCHAR(150) NOT NULL, "
                    + "curp_enc VARBINARY(64) NOT NULL, telefono_enc VARBINARY(64) NOT NULL, salario_enc VARBINARY(64) NOT NULL"
                    + ") CHARACTER SET utf8mb4");
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM usuarios");
            rs.next();
            if (rs.getInt(1) == 0) {
                guardar(ejemplo("admin", "ADMINISTRADOR", "Ana", "Gómez", "GOMA900115MGTMRN05", "5512345678", "45000"), "Admin#2026");
                guardar(ejemplo("empleado1", "EMPLEADO", "Luis", "López", "LOPL950320HGTPRS09", "4151234567", "18500"), "Empleado#2026");
            }
        }
    }

    private static Usuario ejemplo(String user, String rol, String nom, String ap, String curp, String tel, String sal) {
        Usuario u = new Usuario();
        u.username = user; u.rol = rol; u.nombre = nom; u.apPaterno = ap; u.apMaterno = "";
        u.email = user + "@correo.com"; u.fechaNac = "1995-01-15"; u.genero = "Otro";
        u.puesto = "Analista"; u.departamento = "Sistemas"; u.fechaIngreso = "2023-01-10";
        u.direccion = "Calle Principal 100"; u.curp = curp; u.telefono = tel; u.salario = sal;
        return u;
    }

    // ---------- contraseña: SHA-256 ----------
    static String nuevaSal() {
        byte[] b = new byte[16];
        new SecureRandom().nextBytes(b);
        return hex(b);
    }

    static String hash(String password, String sal) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return hex(md.digest((sal + password).getBytes(StandardCharsets.UTF_8)));
    }

    private static String hex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte x : b) sb.append(String.format("%02x", x));
        return sb.toString();
    }

    static Usuario login(String username, String password) throws Exception {
        try (Connection c = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = c.prepareStatement("SELECT id, rol, nombre, password_hash, salt FROM usuarios WHERE username=?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (!rs.next() || !hash(password, rs.getString("salt")).equals(rs.getString("password_hash"))) return null;
            Usuario u = new Usuario();
            u.id = rs.getInt("id"); u.username = username; u.rol = rs.getString("rol"); u.nombre = rs.getString("nombre");
            return u;
        }
    }

    static List<Usuario> listar(boolean verCifrados) throws Exception {
        String sql = "SELECT " + COLS + (verCifrados
                ? ", CAST(AES_DECRYPT(curp_enc,?) AS CHAR) curp, CAST(AES_DECRYPT(telefono_enc,?) AS CHAR) tel, "
                  + "CAST(AES_DECRYPT(salario_enc,?) AS CHAR) sal"
                : "") + " FROM usuarios ORDER BY id";
        List<Usuario> lista = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(url, user, pass); PreparedStatement ps = c.prepareStatement(sql)) {
            if (verCifrados) for (int i = 1; i <= 3; i++) ps.setString(i, aes);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Usuario u = new Usuario();
                u.id = rs.getInt("id"); u.username = rs.getString("username"); u.rol = rs.getString("rol");
                u.nombre = rs.getString("nombre"); u.apPaterno = rs.getString("ap_paterno");
                u.apMaterno = rs.getString("ap_materno") == null ? "" : rs.getString("ap_materno");
                u.email = rs.getString("email"); u.fechaNac = rs.getString("fecha_nac"); u.genero = rs.getString("genero");
                u.puesto = rs.getString("puesto"); u.departamento = rs.getString("departamento");
                u.fechaIngreso = rs.getString("fecha_ingreso"); u.direccion = rs.getString("direccion");
                if (verCifrados) { u.curp = rs.getString("curp"); u.telefono = rs.getString("tel"); u.salario = rs.getString("sal"); }
                lista.add(u);
            }
        }
        return lista;
    }

    static void guardar(Usuario u, String password) throws Exception {
        boolean nuevo = u.id == 0;
        String sql;
        if (nuevo) {
            sql = "INSERT INTO usuarios (username, rol, nombre, ap_paterno, ap_materno, email, fecha_nac, genero, puesto, "
                + "departamento, fecha_ingreso, direccion, curp_enc, telefono_enc, salario_enc, password_hash, salt) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,AES_ENCRYPT(?,?),AES_ENCRYPT(?,?),AES_ENCRYPT(?,?),?,?)";
        } else {
            sql = "UPDATE usuarios SET username=?, rol=?, nombre=?, ap_paterno=?, ap_materno=?, email=?, fecha_nac=?, genero=?, "
                + "puesto=?, departamento=?, fecha_ingreso=?, direccion=?, curp_enc=AES_ENCRYPT(?,?), telefono_enc=AES_ENCRYPT(?,?), "
                + "salario_enc=AES_ENCRYPT(?,?)" + (password.isEmpty() ? "" : ", password_hash=?, salt=?") + " WHERE id=?";
        }
        try (Connection c = DriverManager.getConnection(url, user, pass); PreparedStatement ps = c.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, u.username); ps.setString(i++, u.rol); ps.setString(i++, u.nombre);
            ps.setString(i++, u.apPaterno); ps.setString(i++, u.apMaterno); ps.setString(i++, u.email);
            ps.setDate(i++, Date.valueOf(u.fechaNac)); ps.setString(i++, u.genero); ps.setString(i++, u.puesto);
            ps.setString(i++, u.departamento); ps.setDate(i++, Date.valueOf(u.fechaIngreso)); ps.setString(i++, u.direccion);
            ps.setString(i++, u.curp); ps.setString(i++, aes);
            ps.setString(i++, u.telefono); ps.setString(i++, aes);
            ps.setString(i++, u.salario); ps.setString(i++, aes);
            if (nuevo || !password.isEmpty()) {
                String sal = nuevaSal();
                ps.setString(i++, hash(password, sal)); ps.setString(i++, sal);
            }
            if (!nuevo) ps.setInt(i, u.id);
            ps.executeUpdate();
        }
    }

    static void eliminar(int id) throws Exception {
        try (Connection c = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = c.prepareStatement("DELETE FROM usuarios WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
