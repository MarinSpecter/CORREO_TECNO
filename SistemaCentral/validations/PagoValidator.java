package validations;

import io.github.cdimascio.dotenv.Dotenv;
import postgresqlconnection.SqlConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class PagoValidator {
    Dotenv dotenv = Dotenv.configure().load();
    String dbUser = dotenv.get("DB_USER");
    String dbPassword = dotenv.get("DB_PASSWORD");
    String dbHost = dotenv.get("DB_HOST");
    String dbPort = dotenv.get("DB_PORT");
    String dbName = dotenv.get("DB_NAME");

    private final SqlConnection connection;

    public PagoValidator() {
        connection = new SqlConnection(dbUser, dbPassword, dbHost, dbPort, dbName);
    }

    // Método para validar el ID de la venta
    public boolean validarVentaId(String ventaIdStr) {
        try {
            int ventaId = Integer.parseInt(ventaIdStr);
            if (ventaId > 0) {
                return ventaExisteEnBD(ventaId);
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean ventaExisteEnBD(int ventaId) {
        String query = "SELECT COUNT(*) FROM ventas WHERE id = ?";
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            ps.setInt(1, ventaId);
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

    // Método para validar la fecha de pago
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

    // Método para validar el monto
    public static boolean validarMonto(String montoStr) {
        try {
            double monto = Double.parseDouble(montoStr);
            return monto >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Método para validar el método de pago
    public static boolean validarMetodoPago(String metodoPago) {
        return metodoPago != null && (metodoPago.equalsIgnoreCase("qr")
                || metodoPago.equalsIgnoreCase("tarjeta")
                || metodoPago.equalsIgnoreCase("efectivo")
                || metodoPago.equalsIgnoreCase("transferencia"));
    }

    // Método para validar el estado del pago
    //public static boolean validarEstadoPago(String estadoPago) {
     //   return estadoPago != null && !estadoPago.trim().isEmpty();
    //}

    // Método para validar los parámetros del pago
    public void validarParametrosPago(List<String> parametros) {
        if (parametros.size() != 4) {
            System.out.println(parametros);
            throw new IllegalArgumentException("Número de parámetros inválido");
        }
        if (!validarVentaId(parametros.get(0))) {
            throw new IllegalArgumentException("ID de venta no válido o no existe en la BD");
        }
        if (!validarMonto(parametros.get(1))) {
            throw new IllegalArgumentException("Monto no válido");
        }
        if (!validarFecha(parametros.get(2))) {
            throw new IllegalArgumentException("Fecha no válida");
        }
        if (!validarMetodoPago(parametros.get(3))) {
            throw new IllegalArgumentException("Método de pago no válido");
        }
       // if (!validarEstadoPago(parametros.get(4))) {
       //     throw new IllegalArgumentException("Estado de pago no válido");
      //  }
    }
}
