package validations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class UsuarioValidator {

    // Método para validar el nombre
    public static boolean validarNombre(String nombre) {
        String patronNombre = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$";
        return nombre != null && !nombre.trim().isEmpty() && nombre.length() <= 100 && nombre.matches(patronNombre);
    }


    // Método para validar el apellido
    public static boolean validarApellido(String apellido) {
        return apellido != null && !apellido.trim().isEmpty() && apellido.length() <= 100;
    }

    // Método para validar el email
    public static boolean validarEmail(String email) {
        String patronCorreo = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email != null && email.matches(patronCorreo);
    }

    // Método para validar el número de teléfono
    public static boolean validarNumeroTelefono(String telefono) {
        String patronTelefono = "^[0-9]{1,15}$";
        return telefono == null || telefono.matches(patronTelefono);
    }

    // Método para validar el tipo de usuario
    public static boolean validarTipoUsuario(String tipoUsuario) {
        return tipoUsuario != null && (tipoUsuario.equalsIgnoreCase("administrativo")
                || tipoUsuario.equalsIgnoreCase("cliente") || tipoUsuario.equalsIgnoreCase("proveedor"));
    }

    // Método para validar la dirección
    public static boolean validarDireccion(String direccion) {
        return direccion == null || direccion.length() <= 255;
    }

    // Método para validar los parámetros del usuario
    public static void validarParametrosUsuario(List<String> parametros) {
        if (parametros.size() != 5) {
            throw new IllegalArgumentException("Número de parámetros inválido");
        }
        if (!validarNombre(parametros.get(0))) {
            throw new IllegalArgumentException("Nombre no válido");
        }
        if (!validarEmail(parametros.get(1))) {
            throw new IllegalArgumentException("Formato de correo electrónico no válido");
        }
        if (!validarNumeroTelefono(parametros.get(2))) {
            throw new IllegalArgumentException("Formato de número de teléfono no válido");
        }
        if (!validarDireccion(parametros.get(3))) {
            throw new IllegalArgumentException("Dirección no válida");
        }
        if (!validarTipoUsuario(parametros.get(4))) {
            throw new IllegalArgumentException("El tipo de usuario debe ser 'administrativo' o 'cliente'");
        }
    }
}
