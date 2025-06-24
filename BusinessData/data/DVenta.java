package data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import io.github.cdimascio.dotenv.Dotenv;
import postgresqlconnection.SqlConnection;


public class DVenta {
    Dotenv dotenv = Dotenv.configure().load();
    String dbUser = dotenv.get("DB_USER");
    String dbPassword = dotenv.get("DB_PASSWORD");
    String dbHost = dotenv.get("DB_HOST");
    String dbPort = dotenv.get("DB_PORT");
    String dbName = dotenv.get("DB_NAME");

    public static final String[] HEADERS
            = {"ID", "USUARIO_ID", "FECHA", "ITEM_ID", "TIPO", "CANTIDAD", "PRECIO","TOTAL"};

    private final SqlConnection connection;

    public DVenta() {
        connection = new SqlConnection(dbUser, dbPassword, dbHost, dbPort, dbName);
    }

    public void guardar(int usuario_id, String fecha, int item_id, String tipo_item, int cantidad, Float precio_unitario, Float total) throws SQLException, ParseException {

        String query = "INSERT INTO ventas(usuario_id, fecha, item_id, tipo_item, cantidad, precio_unitario, total)"
                + " values(?,?,?,?,?,?,?)";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, usuario_id);
        ps.setString(2, fecha);
        ps.setInt(3, item_id);
        ps.setString(4, tipo_item);
        ps.setInt(5, cantidad);
        ps.setFloat(6, precio_unitario);
        ps.setFloat(7, total);

        if(ps.executeUpdate() == 0) {
            System.err.println("Class DVenta.java dice: "
                    + "Ocurrio un error al insertar una venta guardar()");
            throw new SQLException();
        }
    }

    public void modificar(int id, int usuario_id, String fecha, int item_id, String tipo_item,  int cantidad, Float precio_unitario, Float total) throws SQLException, ParseException {

        String query = "UPDATE ventas SET usuario_id=?, fecha=?, item_id=?, tipo_item=?, cantidad=?, precio_unitario=?, total=?"
                + " WHERE id=?";
        System.out.println(query);
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, usuario_id);
        ps.setString(2, fecha);
        ps.setInt(3, item_id);
        ps.setString(4, tipo_item);
        ps.setInt(5, cantidad);
        ps.setFloat(6, precio_unitario);
        ps.setFloat(7, total);
        ps.setInt(8, id);

        if(ps.executeUpdate() == 0) {
            System.err.println("Class DVenta.java dice: "
                    + "Ocurrio un error al modificar una venta modificar()");
            throw new SQLException();
        }
    }

    public void eliminar(int id) throws SQLException {

        String query = "DELETE FROM ventas WHERE id=?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, id);

        if(ps.executeUpdate() == 0) {
            System.err.println("Class DVenta.java dice: "
                    + "Ocurrio un error al eliminar una venta eliminar()");
            throw new SQLException();
        }
    }

    public List<String[]> listar() {
        List<String[]> ventas = new ArrayList<>();
        try {
            String query = "SELECT * FROM ventas";
            PreparedStatement ps = connection.connect().prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                ventas.add(new String[] {
                    rs.getString("id"),
                    rs.getString("usuario_id"),
                    rs.getString("fecha"),
                    rs.getString("item_id"),
                    rs.getString("tipo_item"),
                        rs.getString("precio_unitario"),
                    rs.getString("cantidad"),
                    rs.getString("total")
                });
            }
        } catch (SQLException e) {
            System.err.println("Class DVenta.java dice: "
                    + "Ocurrio un error al listar las ventas listar()");
        }
        return ventas;
    }

    public List<String []> listarGrafica() {
        List<String[]> ventas = new ArrayList<>();
        try {
            String query = "SELECT Usuarios.nombre, SUM(Ventas.total) AS total_ventas FROM Ventas JOIN Usuarios ON Ventas.usuario_id = Usuarios.id GROUP BY Usuarios.nombre;";
            PreparedStatement ps = connection.connect().prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                ventas.add(new String[] {
                    rs.getString("nombre"),
                    rs.getString("total_ventas"),
                });
            }
        } catch (SQLException e) {
            System.err.println("Class DVenta.java dice: "
                    + "Ocurrio un error al listar las ventas listarGrafica()");
        }
        return ventas;
    }

    public String[] ver(int id) throws SQLException {
        String[] venta = null;
        String query = "SELECT * FROM ventas WHERE id=?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, id);
        ResultSet set = ps.executeQuery();
        if(set.next()) {
            venta = new String[] {
                    String.valueOf(set.getInt("id")),
                    set.getString("usuario_id"),
                    set.getString("fecha"),
                    set.getString("item_id"),
                    set.getString("tipo_item"),
                    set.getString("cantidad"),
                    set.getString("precio_unitario"),
                    set.getString("total"),
            };
        }
        return venta;
    }

    public List<String[]> ayuda() {
        List<String[]> ayuda = new ArrayList<>();

        String registrar = "venta agregar [usuario_id; fecha; item_id; tipo_item; cantidad; precio_unitario; total]";
        String modificar = "venta modificar [id; usuario_id; fecha; item_id; tipo_item; cantidad; precio_unitario; total]";
        String eliminar = "venta eliminar [id]";
        String listar = "venta mostrar";
        String grafica = "venta grafica";
        String ver = "venta ver [id]";
        String ayudaText = "venta ayuda";

        ayuda.add(new String[] {String.valueOf(1), "Registrar VENTA", registrar});
        ayuda.add(new String[] {String.valueOf(2), "Modificar VENTA", modificar});
        ayuda.add(new String[] {String.valueOf(3), "Eliminar VENTA", eliminar});
        ayuda.add(new String[] {String.valueOf(4), "Listar VENTAS", listar});
        ayuda.add(new String[] {String.valueOf(5), "Grafica VENTAS", grafica});
        ayuda.add(new String[] {String.valueOf(6), "Ver VENTA", ver});
        ayuda.add(new String[] {String.valueOf(7), "Ayuda VENTA", ayudaText});

        return ayuda;
    }

    public void desconectar() {
        connection.closeConnection();
    }
}
