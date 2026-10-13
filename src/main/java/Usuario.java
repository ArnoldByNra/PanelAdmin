import java.math.BigDecimal;
import java.time.LocalDate;

public class Usuario {
    int id;
    String username, rol, nombre, apPaterno, apMaterno, email, fechaNac, genero,
           puesto, departamento, fechaIngreso, direccion;
    String curp, telefono, salario;

    String validar(String password, boolean esNuevo) {
        if (username == null || !username.matches("[A-Za-z0-9_.]{4,20}"))
            return "Usuario: de 4 a 20 caracteres (letras, números, _ o .).";
        if (esNuevo && (password == null || password.isEmpty())) return "La contraseña es obligatoria.";
        if (password != null && !password.isEmpty() && (password.length() < 8 || password.length() > 30))
            return "Contraseña: de 8 a 30 caracteres.";
        if (nombre == null || nombre.length() < 2 || nombre.length() > 50) return "Nombre: de 2 a 50 caracteres.";
        if (apPaterno == null || apPaterno.length() < 2 || apPaterno.length() > 50) return "Apellido paterno: de 2 a 50 caracteres.";
        if (apMaterno != null && apMaterno.length() > 50) return "Apellido materno: máximo 50 caracteres.";
        if (email == null || email.length() > 100 || !email.matches("[^@\\s]+@[^@\\s]+\\.[A-Za-z]{2,}"))
            return "Correo no válido.";
        try { LocalDate.parse(fechaNac); } catch (Exception e) { return "Fecha de nacimiento: use el formato aaaa-mm-dd."; }
        try { LocalDate.parse(fechaIngreso); } catch (Exception e) { return "Fecha de ingreso: use el formato aaaa-mm-dd."; }
        if (puesto == null || puesto.length() < 2 || puesto.length() > 60) return "Puesto: de 2 a 60 caracteres.";
        if (departamento == null || departamento.length() < 2 || departamento.length() > 60) return "Departamento: de 2 a 60 caracteres.";
        if (direccion == null || direccion.length() < 5 || direccion.length() > 150) return "Dirección: de 5 a 150 caracteres.";
        if (curp == null || !curp.matches("[A-Za-z0-9]{18}")) return "CURP: deben ser 18 letras/números.";
        if (telefono == null || !telefono.matches("\\d{10}")) return "Teléfono: deben ser 10 dígitos.";
        try {
            if (new BigDecimal(salario).signum() <= 0) return "Salario: debe ser mayor que 0.";
        } catch (Exception e) { return "Salario: debe ser un número (ej. 12000.50)."; }
        return null;
    }
}
