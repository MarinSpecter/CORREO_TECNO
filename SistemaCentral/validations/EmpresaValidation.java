package validations;

import java.util.List;

public class EmpresaValidation {

    // Método para validar el nombre de la empresa
    public static boolean validarNombre(String nombre) {
        return nombre != null && !nombre.trim().isEmpty() && nombre.length() <= 100;
    }

    // Método para validar la dirección de la empresa
    public static boolean validarDireccion(String direccion) {
        return direccion != null && !direccion.trim().isEmpty() && direccion.length() <= 255;
    }

    // Método para validar el teléfono de la empresa
    public static boolean validarTelefono(String telefono) {
        String patronTelefono = "^[0-9]+$";
        return telefono != null && telefono.matches(patronTelefono);
    }

    // Método para validar el correo electrónico de la empresa
    public static boolean validarEmail(String email) {
        String patronCorreo = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email != null && email.matches(patronCorreo);
    }

    // Método para validar la URL del sitio web de la empresa
    public static boolean validarSitioWeb(String sitioWeb) {
        String patronURL = "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$";
        return sitioWeb != null && sitioWeb.matches(patronURL);
    }

    // Método para validar los parámetros de la empresa
    public static void validarParametrosEmpresa(List<String> parametros) {
        if (!validarNombre(parametros.get(0))) {
            throw new IllegalArgumentException("Nombre no válido");
        }
        if (!validarDireccion(parametros.get(1))) {
            throw new IllegalArgumentException("Dirección no válida");
        }
        if (!validarTelefono(parametros.get(2))) {
            throw new IllegalArgumentException("Teléfono no válido");
        }
        if (!validarEmail(parametros.get(3))) {
            throw new IllegalArgumentException("Correo electrónico no válido");
        }
        if (!validarSitioWeb(parametros.get(4))) {
            throw new IllegalArgumentException("Sitio web no válido");
        }
    }
}
