package validations;

import io.github.cdimascio.dotenv.Dotenv;
import postgresqlconnection.SqlConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class VentaValidator {
    Dotenv dotenv = Dotenv.configure().load();
    String dbUser = dotenv.get("DB_USER");
    String dbPassword = dotenv.get("DB_PASSWORD");
    String dbHost = dotenv.get("DB_HOST");
    String dbPort = dotenv.get("DB_PORT");
    String dbName = dotenv.get("DB_NAME");

    private final SqlConnection connection;

    public VentaValidator() {
        connection = new SqlConnection(dbUser, dbPassword, dbHost, dbPort, dbName);
    }

    // Método para validar el ID del usuario
    public boolean validarUsuarioId(String usuarioIdStr) {
        try {
            int usuarioId = Integer.parseInt(usuarioIdStr);
            if (usuarioId > 0) {
                return usuarioExisteEnBD(usuarioId);
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean usuarioExisteEnBD(int usuarioId) {
        String query = "SELECT COUNT(*) FROM usuarios WHERE id = ?";
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            ps.setInt(1, usuarioId);
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

    // Método para validar el tipo de item ('producto' o 'servicio')
    public boolean validarTipoItem(String tipoItem) {
        return tipoItem.equalsIgnoreCase("producto") || tipoItem.equalsIgnoreCase("servicio");
    }

    // Método para validar el ID del producto o servicio
    public boolean validarItemId(String itemIdStr, String tipoItem) {
        try {
            int itemId = Integer.parseInt(itemIdStr);
            if (itemId > 0) {
                return itemExisteEnBD(itemId, tipoItem);
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean itemExisteEnBD(int itemId, String tipoItem) {
        String query = tipoItem.equals("producto") ?
                "SELECT COUNT(*) FROM productos WHERE id = ?" :
                "SELECT COUNT(*) FROM servicios WHERE id = ?";

        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            ps.setInt(1, itemId);
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

    // Método para validar la cantidad (debe ser mayor que 0)
    public boolean validarCantidad(String cantidadStr) {
        try {
            int cantidad = Integer.parseInt(cantidadStr);
            return cantidad > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Método para validar el precio unitario (debe ser mayor que 0)
    public boolean validarPrecioUnitario(String precioUnitarioStr) {
        try {
            float precioUnitario = Float.parseFloat(precioUnitarioStr);
            return precioUnitario > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Método para validar la fecha
    public static boolean validarFecha(String fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false); // Para que la validación sea estricta
        try {
            sdf.parse(fecha);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // Método para validar el total
    public static boolean validarTotal(String totalStr) {
        try {
            float total = Float.parseFloat(totalStr);
            return total >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Método para validar los parámetros de la venta
    public void validarParametrosVenta(List<String> parametros) {
        if (parametros.size() != 7) {
            throw new IllegalArgumentException("Número de parámetros inválido");
        }
        if (!validarUsuarioId(parametros.get(0))) {
            throw new IllegalArgumentException("ID de usuario no válido o no existe en la BD");
        }
        if (!validarFecha(parametros.get(1))) {
            throw new IllegalArgumentException("Fecha no válida");
        }
        if (!validarItemId(parametros.get(2), parametros.get(3))) {
            throw new IllegalArgumentException("ID de item no válido o no existe en la BD");
        }
        if (!validarTipoItem(parametros.get(3))) {
            throw new IllegalArgumentException("Tipo de item no válido (debe ser 'producto' o 'servicio')");
        }
        if (!validarCantidad(parametros.get(4))) {
            throw new IllegalArgumentException("Cantidad no válida (debe ser mayor que 0)");
        }
        if (!validarPrecioUnitario(parametros.get(5))) {
            throw new IllegalArgumentException("Precio unitario no válido");
        }
        if (!validarTotal(parametros.get(6))) {
            throw new IllegalArgumentException("Total no válido");
        }
    }
}
