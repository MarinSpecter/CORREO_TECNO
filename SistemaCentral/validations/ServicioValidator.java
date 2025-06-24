package validations;

import java.util.List;

public class ServicioValidator {

    // Método para validar el nombre del servicio
    public static boolean validarNombre(String nombre) {
        return nombre != null && !nombre.trim().isEmpty() && nombre.length() <= 100;
    }

    // Método para validar la descripción del servicio
    public static boolean validarDescripcion(String descripcion) {
        return descripcion == null || descripcion.length() <= 255;
    }

    // Método para validar el precio del servicio
    public static boolean validarPrecio(String precioStr) {
        try {
            float precio = Float.parseFloat(precioStr);
            return precio >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Método para validar los parámetros del servicio
    public static void validarParametrosServicio(List<String> parametros) {
        if (parametros.size() != 3) {
            throw new IllegalArgumentException("Número de parámetros inválido");
        }
        if (!validarNombre(parametros.get(0))) {
            throw new IllegalArgumentException("Nombre no válido");
        }
        if (!validarDescripcion(parametros.get(1))) {
            throw new IllegalArgumentException("Descripción no válida");
        }
        if (!validarPrecio(parametros.get(2))) {
            throw new IllegalArgumentException("Precio no válido");
        }
    }
}
