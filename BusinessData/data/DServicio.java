package data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import io.github.cdimascio.dotenv.Dotenv;
import postgresqlconnection.SqlConnection;

public class DServicio {
    Dotenv dotenv = Dotenv.configure().load();
    String dbUser = dotenv.get("DB_USER");
    String dbPassword = dotenv.get("DB_PASSWORD");
    String dbHost = dotenv.get("DB_HOST");
    String dbPort = dotenv.get("DB_PORT");
    String dbName = dotenv.get("DB_NAME");

    public static final String[] HEADERS
            = {"ID", "NOMBRE", "DESCRIPCION", "PRECIO"};

    private final SqlConnection connection;

    public DServicio() {
        connection = new SqlConnection(dbUser, dbPassword, dbHost, dbPort, dbName);
    }

    public void guardar(String nombre, String descripcion, Float precio){
        try {
            String query = "INSERT INTO servicios(nombre, descripcion, precio)"
                    + " values(?,?,?)";
            PreparedStatement ps = connection.connect().prepareStatement(query);
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setFloat(3, precio);

            if(ps.executeUpdate() == 0) {
                System.err.println("Class DServicio.java dice: "
                        + "Ocurrio un error al insertar un servicio guardar()");
                throw new SQLException();
            }
        } catch (SQLException ex) {
            System.err.println("Class DServicio.java dice: "
                    + "Ocurrio un error al insertar un servicio guardar()");
        }

    }

    public void modificar(int id, String nombre, String descripcion, Float precio) {
        try {
            String query = "UPDATE servicios SET nombre=?, descripcion=?, precio=? WHERE id=?";
            PreparedStatement ps = connection.connect().prepareStatement(query);
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setFloat(3, precio);
            ps.setInt(4, id);
            if(ps.executeUpdate() == 0) {
                System.err.println("Class DServicio.java dice: "
                        + "Ocurrio un error al editar un servicio editar()");
                throw new SQLException();
            }
        } catch (SQLException ex) {
            System.err.println("Class DServicio.java dice: "
                    + "Ocurrio un error al editar un servicio editar()");
        }
    }

    public void eliminar(int id) {
        try {
            String query = "DELETE FROM servicios WHERE id=?";
            PreparedStatement ps = connection.connect().prepareStatement(query);
            ps.setInt(1, id);
            if(ps.executeUpdate() == 0) {
                System.err.println("Class DServicio.java dice: "
                        + "Ocurrio un error al eliminar un servicio eliminar()");
                throw new SQLException();
            }
        } catch (SQLException ex) {
            System.err.println("Class DServicio.java dice: "
                    + "Ocurrio un error al eliminar un servicio eliminar()");
        }
    }

    public List<String[]> listar() {
        List<String[]> servicios = new ArrayList<>();
        try {
            String query = "SELECT * FROM servicios";
            PreparedStatement ps = connection.connect().prepareStatement(query);
            ResultSet set = ps.executeQuery();
            while(set.next()) {
                servicios.add(new String[] {
                    String.valueOf(set.getInt("id")),
                    set.getString("nombre"),
                    set.getString("descripcion"),
                    String.valueOf(set.getFloat("precio"))
                });
            }
        } catch (SQLException ex) {
            System.err.println("Class DServicio.java dice: "
                    + "Ocurrio un error al listar los servicios listar()");
        }
        return servicios;
    }

    public List<String[]> listarGrafica() {
        List<String[]> servicios = new ArrayList<>();
        try {
            String query = """
                    SELECT\s
                        s.nombre AS nombre_servicio,\s
                        COUNT(v.id) AS veces_solicitado
                    FROM Servicios s
                    JOIN Ventas v ON s.id = v.item_id AND v.tipo_item = 'servicio'
                    GROUP BY s.nombre
                    HAVING COUNT(v.id) > 0;
                    """;
            PreparedStatement ps = connection.connect().prepareStatement(query);
            ResultSet set = ps.executeQuery();
            while(set.next()) {
                servicios.add(new String[] {
                    set.getString("nombre_servicio"),
                    String.valueOf(set.getFloat("veces_solicitado"))
                });
            }
        } catch (SQLException ex) {
            System.err.println("Class DServicio.java dice: "
                    + "Ocurrio un error al listar los servicios listarGrafica()");
        }
        return servicios;
    }

    public String[] ver(int id) {
        String[] servicio = null;
        try {
            String query = "SELECT * FROM servicios WHERE id=?";
            PreparedStatement ps = connection.connect().prepareStatement(query);
            ps.setInt(1, id);
            ResultSet set = ps.executeQuery();
            if(set.next()) {
                servicio = new String[] {
                    String.valueOf(set.getInt("id")),
                    set.getString("nombre"),
                    set.getString("descripcion"),
                    String.valueOf(set.getFloat("precio"))
                };
            }
        } catch (SQLException ex) {
            System.err.println("Class DServicio.java dice: "
                    + "Ocurrio un error al ver un servicio ver()");
        }
        return servicio;
    }

    public List<String[]> ayuda(){
        List<String[]> ayudas = new ArrayList<>();

        String registrar = "servicio agregar [nombre; descripcion; precio]";
        String modificar = "servicio modificar [id; nombre; descripcion; precio]";
        String eliminar = "servicio eliminar [id]";
        String mostrar = "servicio mostrar";
        String reporte = "servicio reporte";
        String ver = "servicio ver [id]";
        String ayuda = "servicio ayuda";

        ayudas.add(new String[] {String.valueOf(1), "Registra un nuevo servicio", registrar});
        ayudas.add(new String[] {String.valueOf(2), "Modifica un servicio existente", modificar});
        ayudas.add(new String[] {String.valueOf(3), "Elimina un servicio existente", eliminar});
        ayudas.add(new String[] {String.valueOf(4), "Muestra todos los servicios", mostrar});
        ayudas.add(new String[] {String.valueOf(5), "Muestra un reporte de los servicios", reporte});
        ayudas.add(new String[] {String.valueOf(6), "Muestra un servicio en especifico", ver});
        ayudas.add(new String[] {String.valueOf(7), "Muestra la ayuda", ayuda});

        return ayudas;
    }

    public void desconectar() {
        connection.closeConnection();
    }

}