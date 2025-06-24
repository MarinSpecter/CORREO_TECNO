package validations;

import io.github.cdimascio.dotenv.Dotenv;
import postgresqlconnection.SqlConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class InventarioValidator {
    Dotenv dotenv = Dotenv.configure().load();
    String dbUser = dotenv.get("DB_USER");
    String dbPassword = dotenv.get("DB_PASSWORD");
    String dbHost = dotenv.get("DB_HOST");
    String dbPort = dotenv.get("DB_PORT");
    String dbName = dotenv.get("DB_NAME");

    private final SqlConnection connection;

    public InventarioValidator() {
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

    private boolean productoExisteEnBD(int productoId) {
        String query = "SELECT COUNT(*) FROM productos WHERE id = ?";
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            ps.setInt(1, productoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Método para validar la cantidad
    public static boolean validarCantidad(String cantidadStr) {
        try {
            int cantidad = Integer.parseInt(cantidadStr);
            return cantidad >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Método para validar el tipo de movimiento
    public static boolean validarTipoMovimiento(String tipoMovimiento) {
        return tipoMovimiento != null && (tipoMovimiento.equalsIgnoreCase("entrada")
                || tipoMovimiento.equalsIgnoreCase("salida"));
    }

    // Método para validar la fecha de movimiento
    public static boolean validarFechaMovimiento(String fechaMovimiento) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false); // Para que la validación sea estricta
        try {
            sdf.parse(fechaMovimiento);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // Método para validar los parámetros del inventario
    public void validarParametrosInventario(List<String> parametros) {
        if (parametros.size() != 4) {
            throw new IllegalArgumentException("Número de parámetros inválido");
        }
        if (!validarProductoId(parametros.get(0))) {
            throw new IllegalArgumentException("ID de producto no válido o no existe en la BD");
        }
        if (!validarCantidad(parametros.get(1))) {
            throw new IllegalArgumentException("Cantidad no válida");
        }
        if (!validarTipoMovimiento(parametros.get(2))) {
            throw new IllegalArgumentException("Tipo de movimiento no válido");
        }
        if (!validarFechaMovimiento(parametros.get(3))) {
            throw new IllegalArgumentException("Fecha de movimiento no válida");
        }
    }
}
