package data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.cdimascio.dotenv.Dotenv;
import postgresqlconnection.SqlConnection;

public class DProducto {
    Dotenv dotenv = Dotenv.configure().load();
    String dbUser = dotenv.get("DB_USER");
    String dbPassword = dotenv.get("DB_PASSWORD");
    String dbHost = dotenv.get("DB_HOST");
    String dbPort = dotenv.get("DB_PORT");
    String dbName = dotenv.get("DB_NAME");

    public static final String[] HEADERS
            = {"ID", "PROVEEDOR ID", "NOMBRE", "DESCRIPCION", "PRECIO", "STOCK"};

    private final SqlConnection connection;

    public DProducto() {
        connection = new SqlConnection(dbUser, dbPassword, dbHost, dbPort, dbName);
    }

    public void guardar(int proveedor_id, String nombre, String descripcion, double precio, int stock) {
        try {
            String query = "INSERT INTO productos(proveedor_id,nombre,descripcion,precio,stock)"
                    + " values(?,?,?,?,?)";
            PreparedStatement ps = connection.connect().prepareStatement(query);
            ps.setInt(1, proveedor_id);
            ps.setString(2, nombre);
            ps.setString(3, descripcion);
            ps.setDouble(4, precio);
            ps.setInt(5, stock);

            if(ps.executeUpdate() == 0) {
                System.err.println("Class DProducto.java dice: "
                        + "Ocurrio un error al insertar un producto guardar()");
                throw new SQLException();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DProducto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void restarStock(int id, int cantidad) throws SQLException {
        String query = "UPDATE productos SET stock = stock - ? WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, cantidad);
        ps.setInt(2, id);
        if (ps.executeUpdate() == 0) {
            throw new SQLException("No se pudo restar el stock del producto con ID " + id);
        }
    }
    public void modificar(int id, int proveedor_id, String nombre, String descripcion, float precio, int stock) {
        try {
            String query = "UPDATE productos SET proveedor_id=?, nombre=?, descripcion=?, precio=?, stock=?"
                    + " WHERE id=?";
            PreparedStatement ps = connection.connect().prepareStatement(query);
            ps.setInt(1, proveedor_id);
            ps.setString(2, nombre);
            ps.setString(3, descripcion);
            ps.setFloat(4, precio);
            ps.setInt(5, stock);
            ps.setInt(6, id);
            if(ps.executeUpdate() == 0) {
                System.err.println("Class DProducto.java dice: "
                        + "Ocurrio un error al modificar un producto modificar()");
                throw new SQLException();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DProducto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void eliminar(int id) {
        try {
            String query = "DELETE FROM productos WHERE id=?";
            PreparedStatement ps = connection.connect().prepareStatement(query);
            ps.setInt(1, id);
            if(ps.executeUpdate() == 0) {
                System.err.println("Class DProducto.java dice: "
                        + "Ocurrio un error al eliminar un producto eliminar()");
                throw new SQLException();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DProducto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<String[]> listar() {
        List<String[]> productos = new ArrayList<>();
        try {
            String query = "SELECT * FROM productos ORDER BY id ASC"; // 👈 AÑADIDO
            PreparedStatement ps = connection.connect().prepareStatement(query);
            ResultSet set = ps.executeQuery();
            while(set.next()) {
                productos.add(new String[] {
                        set.getString("id"),
                        set.getString("proveedor_id"),
                        set.getString("nombre"),
                        set.getString("descripcion"),
                        set.getString("precio"),
                        set.getString("stock")
                });
            }
        } catch (SQLException ex) {
            Logger.getLogger(DProducto.class.getName()).log(Level.SEVERE, null, ex);
        }
        return productos;
    }

    public ArrayList<String[]> listarGrafica() {
        ArrayList<String[]> productos = new ArrayList<>();
        try {
            String query = """
                    SELECT U.nombre AS proveedor, COUNT(P.id) AS total_productos
                    FROM Productos P
                    JOIN Usuarios U ON P.proveedor_id = U.id
                    GROUP BY U.nombre;""";
            PreparedStatement ps = connection.connect().prepareStatement(query);
            ResultSet set = ps.executeQuery();
            while(set.next()) {
                productos.add(new String[] {
                    set.getString("proveedor"),
                    set.getString("total_productos")
                });
            }
        } catch (SQLException ex) {
            Logger.getLogger(DProducto.class.getName()).log(Level.SEVERE, null, ex);
        }
        return productos;
    }

    public String[] ver(int id) {
        String[] producto = null;
        try {
            String query = "SELECT * FROM productos WHERE id=?";
            PreparedStatement ps = connection.connect().prepareStatement(query);
            ps.setInt(1, id);

            ResultSet set = ps.executeQuery();
            if(set.next()) {
                producto = new String[] {
                    set.getString("id"),
                    set.getString("proveedor_id"),
                    set.getString("nombre"),
                    set.getString("descripcion"),
                    set.getString("precio"),
                    set.getString("stock")
                };
            }
        } catch (SQLException ex) {
            Logger.getLogger(DProducto.class.getName()).log(Level.SEVERE, null, ex);
        }
        return producto;
    }

    public List<String[]> ayuda() {
        List<String[]> ayudas = new ArrayList<>();

        String registrar = "producto agregar [proveedor_id; nombre; descripcion; precio; stock]";
        String modificar = "producto modificar [id; proveedor_id; nombre; descripcion; precio; stock]";
        String eliminar = "producto eliminar [id]";
        String mostrar = "producto mostrar";
        String ver = "producto ver [id]";
        String reporte = "producto reporte";

        ayudas.add(new String[]{String.valueOf(1), "Registrar PRODUCTO", registrar});
        ayudas.add(new String[]{String.valueOf(2), "Modificar PRODUCTO", modificar});
        ayudas.add(new String[]{String.valueOf(3), "Eliminar PRODUCTO", eliminar});
        ayudas.add(new String[]{String.valueOf(4), "Mostrar PRODUCTOS", mostrar});
        ayudas.add(new String[]{String.valueOf(5), "Ver PRODUCTO", ver});
        ayudas.add(new String[]{String.valueOf(6), "Reporte PRODUCTOS", reporte});

        return ayudas;
    }

    public void desconectar() {
        connection.closeConnection();
    }
}
