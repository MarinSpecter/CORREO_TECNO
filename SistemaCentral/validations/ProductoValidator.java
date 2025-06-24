package validations;

import io.github.cdimascio.dotenv.Dotenv;
import postgresqlconnection.SqlConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ProductoValidator {
    Dotenv dotenv = Dotenv.configure().load();
    String dbUser = dotenv.get("DB_USER");
    String dbPassword = dotenv.get("DB_PASSWORD");
    String dbHost = dotenv.get("DB_HOST");
    String dbPort = dotenv.get("DB_PORT");
    String dbName = dotenv.get("DB_NAME");

    private final SqlConnection connection;

    public ProductoValidator() {
        connection = new SqlConnection(dbUser, dbPassword, dbHost, dbPort, dbName);
    }

    // Método para validar el ID del proveedor y que sea de tipo "proveedor"
    public boolean validarProveedorId(String proveedorIdStr) {
        try {
            int proveedorId = Integer.parseInt(proveedorIdStr);
            if (proveedorId > 0) {
                return proveedorEsValidoEnBD(proveedorId);
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Método para verificar si el proveedor existe en la BD y es de tipo "proveedor"
    private boolean proveedorEsValidoEnBD(int proveedorId) {
        String query = "SELECT COUNT(*) FROM usuarios WHERE id = ? AND LOWER(tipo) = 'proveedor'";
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            ps.setInt(1, proveedorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Retorna true si el proveedor existe y es de tipo "proveedor"
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Método para validar el nombre del producto
    public static boolean validarNombre(String nombre) {
        return nombre != null && !nombre.trim().isEmpty() && nombre.length() <= 100;
    }

    // Método para validar la descripción del producto
    public static boolean validarDescripcion(String descripcion) {
        return descripcion == null || descripcion.length() <= 255;
    }

    // Método para validar el precio del producto
    public static boolean validarPrecio(String precioStr) {
        try {
            float precio = Float.parseFloat(precioStr);
            return precio >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Método para validar el stock del producto
    public static boolean validarStock(String stockStr) {
        try {
            int stock = Integer.parseInt(stockStr);
            return stock >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Método para validar los parámetros del producto
    public void validarParametrosProducto(List<String> parametros) {
        if (parametros.size() != 5) {
            throw new IllegalArgumentException("Número de parámetros inválido");
        }

        if (!validarProveedorId(parametros.get(0))) {
            throw new IllegalArgumentException("ID del proveedor no válido o el proveedor no es de tipo 'proveedor'");
        }
        if (!validarNombre(parametros.get(1))) {
            throw new IllegalArgumentException("Nombre del producto no válido");
        }
        if (!validarDescripcion(parametros.get(2))) {
            throw new IllegalArgumentException("Descripción del producto no válida");
        }
        if (!validarPrecio(parametros.get(3))) {
            throw new IllegalArgumentException("Precio del producto no válido");
        }
        if (!validarStock(parametros.get(4))) {
            throw new IllegalArgumentException("Stock del producto no válido");
        }
    }
}
