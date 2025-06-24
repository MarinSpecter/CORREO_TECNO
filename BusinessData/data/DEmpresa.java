package data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


import io.github.cdimascio.dotenv.Dotenv;
import postgresqlconnection.SqlConnection;


public class DEmpresa {
    Dotenv dotenv = Dotenv.configure().load();
    String dbUser = dotenv.get("DB_USER");
    String dbPassword = dotenv.get("DB_PASSWORD");
    String dbHost = dotenv.get("DB_HOST");
    String dbPort = dotenv.get("DB_PORT");
    String dbName = dotenv.get("DB_NAME");

    public static final String[] HEADERS
            = {"ID", "NOMBRE", "DIRECCION", "TELEFONO", "EMAIL", "SITIO WEB"};

    private final SqlConnection connection;

    public DEmpresa() {
        connection = new SqlConnection(dbUser, dbPassword, dbHost, dbPort, dbName);
    }

    public void guardar(String nombre, String direccion, String telefono, String email, String sitio_web) throws SQLException, ParseException {

        String query = "INSERT INTO empresa(nombre,direccion,telefono,email,sitio_web)"
                + " values(?,?,?,?,?)";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setString(1, nombre);
        ps.setString(2, direccion);
        ps.setString(3, telefono);
        ps.setString(4, email);
        ps.setString(5, sitio_web);

        if(ps.executeUpdate() == 0) {
            System.err.println("Class DEmpresa.java dice: "
                    + "Ocurrio un error al insertar una empresa guardar()");
            throw new SQLException();
        }
    }

    public void modificar(int id, String nombre, String direccion, String telefono, String email, String sitio_web)
            throws SQLException, ParseException {

        String query = "UPDATE empresa SET nombre=?, direccion=?, telefono=?,"
                + " email=?, sitio_web=?"
                + " WHERE id=?";
        System.out.println(query);
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setString(1, nombre);
        ps.setString(2, direccion);
        ps.setString(3, telefono);
        ps.setString(4, email);
        ps.setString(5, sitio_web);
        ps.setInt(6, id);

        if(ps.executeUpdate() == 0) {
            System.err.println("Class DEmpresa.java dice: "
                    + "Ocurrio un error al modificar una empresa modificar()");
            throw new SQLException();
        }
    }

    public void eliminar(int id) throws SQLException {
        String query = "DELETE FROM empresa WHERE id=?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, id);

        if(ps.executeUpdate() == 0) {
            System.err.println("Class DEmpresa.java dice: "
                    + "Ocurrio un error al eliminar una empresa eliminar()");
            throw new SQLException();
        }
    }

    public List<String[]> listar() throws SQLException {
        String query = "SELECT * FROM empresa";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        List<String[]> empresas = new ArrayList<>();
        while(rs.next()) {
            String[] empresa = new String[6];
            empresa[0] = rs.getString("id");
            empresa[1] = rs.getString("nombre");
            empresa[2] = rs.getString("direccion");
            empresa[3] = rs.getString("telefono");
            empresa[4] = rs.getString("email");
            empresa[5] = rs.getString("sitio_web");
            empresas.add(empresa);
        }

        return empresas;
    }

    public List<String[]> ayuda() throws SQLException {
        List<String[]> ayudas = new ArrayList<>();

        String registrar = " empresa agregar [nombre, apellido, tipo_usuario, telefono, email]";
        String editar = " usuario modificar [nombre, apellido, tipo_usuario, telefono, email]";
        String eliminar = " usuario eliminar [USUARIO_ID]";
        //String ver = " usuario ver (USUARIO_ID)";
        String listar = " usuario mostrar";
        String ayu = " usuario ayuda";

        ayudas.add(new String[]{String.valueOf(1), "Registrar USUARIO", registrar});
        ayudas.add(new String[]{String.valueOf(2), "Editar USUARIO", editar});
        ayudas.add(new String[]{String.valueOf(3), "Eliminar USUARIO", eliminar});
        //.add(new String[]{String.valueOf(4), "Ver USUARIO", ver});
        ayudas.add(new String[]{String.valueOf(4), "Listar USUARIO", listar});
        ayudas.add(new String[]{String.valueOf(5), "Ver Comandos CU: Usuarios", ayu});

        return ayudas;
    }

    public void desconectar() {
        connection.closeConnection();
    }

}
