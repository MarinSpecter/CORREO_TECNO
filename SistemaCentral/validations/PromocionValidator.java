package validations;

import io.github.cdimascio.dotenv.Dotenv;
import postgresqlconnection.SqlConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PromocionValidator {
    Dotenv dotenv = Dotenv.configure().load();
    String dbUser = dotenv.get("DB_USER");
    String dbPassword = dotenv.get("DB_PASSWORD");
    String dbHost = dotenv.get("DB_HOST");
    String dbPort = dotenv.get("DB_PORT");
    String dbName = dotenv.get("DB_NAME");

    private final SqlConnection connection;

    public PromocionValidator() {
        connection = new SqlConnection(dbUser, dbPassword, dbHost, dbPort, dbName);
    }

    // Método para validar el ID del producto
    public boolean validarProductoId(String productoIdStr) {
        try {
            int productoId = Integer.parseInt(productoIdStr);
            if (productoId > 0) {
                return productoExisteEnBD(productoId);
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Método para verificar si el producto existe en la BD
    private boolean productoExisteEnBD(int productoId) {
        String query = "SELECT COUNT(*) FROM productos WHERE id = ?";
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            ps.setInt(1, productoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Retorna true si el producto existe
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Método para validar nombre
    public static boolean validarNombre(String nombre) {
        return nombre != null && !nombre.trim().isEmpty() && nombre.length() <= 50;
    }

    // Método para validar la descripción
    public static boolean validarDescripcion(String descripcion) {
        return descripcion != null && !descripcion.trim().isEmpty() && descripcion.length() <= 255;
    }

    // Método para validar el descuento
    public static boolean validarDescuento(String descuentoStr) {
        try {
            float descuento = Float.parseFloat(descuentoStr);
            return descuento >= 0 && descuento <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Método para validar los parámetros de la oferta
    public void validarParametrosOferta(List<String> parametros) {
        if (parametros.size() != 4) {
            throw new IllegalArgumentException("Número de parámetros inválido");
        }
        if (!validarProductoId(parametros.get(0))) {
            throw new IllegalArgumentException("ID de producto no válido o no existe en la BD");
        }
        if (!validarNombre(parametros.get(1))) {
            throw new IllegalArgumentException("Nombre no válido");
        }
        if (!validarDescripcion(parametros.get(2))) {
            throw new IllegalArgumentException("Descripción no válida");
        }
        if (!validarDescuento(parametros.get(3))) {
            throw new IllegalArgumentException("Descuento no válido");
        }
    }
}