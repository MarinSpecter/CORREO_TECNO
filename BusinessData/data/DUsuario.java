/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import io.github.cdimascio.dotenv.Dotenv;
import postgresqlconnection.SqlConnection;


public class DUsuario {
    Dotenv dotenv = Dotenv.configure().load();
    String dbUser = dotenv.get("DB_USER");
    String dbPassword = dotenv.get("DB_PASSWORD");
    String dbHost = dotenv.get("DB_HOST");
    String dbPort = dotenv.get("DB_PORT");
    String dbName = dotenv.get("DB_NAME");
    public static final String[] HEADERS
            = {"ID", "NOMBRE", "EMAIL", "TELEFONO", "DIRECCION", "TIPO"};
    private final SqlConnection connection;
    public DUsuario() {
        connection = new SqlConnection(dbUser, dbPassword, dbHost, dbPort, dbName);
    }
    public void guardar(String nombre, String email, String telefono, String direccion, String tipo) throws SQLException, ParseException {


        String query = "INSERT INTO usuarios(nombre,email,telefono,direccion,tipo)"
                + " values(?,?,?,?,?)";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setString(1, nombre);
        ps.setString(2, email);
        ps.setString(3, telefono);
        ps.setString(4, direccion);
        ps.setString(5, tipo.toLowerCase());

        if(ps.executeUpdate() == 0) {
            System.err.println("Class DUsuario.java dice: "
                    + "Ocurrio un error al insertar un usuario guardar()");
            throw new SQLException();
        }
    }
    public void editar(int id, String nombre, String email, String telefono, String direccion, String tipo) throws SQLException {
        String query = "UPDATE usuarios SET nombre=?, email=?, telefono=?, direccion=?, tipo=? WHERE id=?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setString(1, nombre);
        ps.setString(2, email);
        ps.setString(3, telefono);
        ps.setString(4, direccion);
        ps.setString(5, tipo.toLowerCase());
        ps.setInt(6, id);
        if(ps.executeUpdate() == 0) {
            System.err.println("Class DUsuario.java dice: "
                    + "Ocurrio un error al editar un usuario editar()");
            throw new SQLException();
        }
    }
    public void eliminar(int id) throws SQLException {
        String query = "DELETE FROM usuarios WHERE id=?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, id);
        if(ps.executeUpdate() == 0) {
            System.err.println("Class DUsuario.java dice: "
                    + "Ocurrio un error al eliminar un usuario eliminar()");
            throw new SQLException();
        }
    }


    public List<String[]> listar() throws SQLException {
        List<String[]> usuarios = new ArrayList<>();
        String query = "SELECT * FROM usuarios ORDER BY id ASC"; // Ordenar por ID ascendente
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ResultSet set = ps.executeQuery();
        while (set.next()) {
            usuarios.add(new String[] {
                    String.valueOf(set.getInt("id")),
                    set.getString("nombre"),
                    set.getString("email"),
                    set.getString("telefono"),
                    set.getString("direccion"),
                    set.getString("tipo"),
            });
        }
        return usuarios;
    }

    public List<String[]> listarGrafica() throws SQLException {
        List<String[]> usuarios = new ArrayList<>();
        String query = """
                SELECT\s
                    tipo AS tipo_usuario,\s
                    COUNT(*) AS cantidad
                FROM Usuarios
                GROUP BY tipo;
                """;
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ResultSet set = ps.executeQuery();
        while(set.next()) {
            usuarios.add(new String[] {
                    set.getString("tipo_usuario"),
                    set.getString("cantidad"),
            });
        }
        return usuarios;
    }
    public String[] ver(int id) throws SQLException {
        String[] usuario = null;
        String query = "SELECT * FROM usuarios WHERE id=?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, id);
        ResultSet set = ps.executeQuery();
        if(set.next()) {
            usuario = new String[] {
                    String.valueOf(set.getInt("id")),
                    set.getString("nombre"),
                    set.getString("email"),
                    set.getString("telefono"),
                    set.getString("direccion"),
                    set.getString("tipo"),
            };
        }
        return usuario;
    }

    public boolean esAdministrativo(String correo) throws SQLException {
        boolean resp = false;
        System.out.println(correo);
        String query = "SELECT * FROM usuarios WHERE tipo = 'Administrador' AND email=?";
        PreparedStatement statement = connection.connect().prepareStatement(query);
        statement.setString(1, correo);
        System.out.println(statement);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            resp = true;
        }
        return resp;
    }

    public int getIdByCorreo(String correo) throws SQLException {
        int id = -1;
        String query = "SELECT * FROM usuarios WHERE email=?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setString(1, correo);

        ResultSet set = ps.executeQuery();
        if(set.next()) {
            id = set.getInt("id");
        }
        return id;
    }

    public List<String[]> ayuda() throws SQLException {
        List<String[]> ayudas = new ArrayList<>();

        String registrar = " usuario agregar [nombre; email; telefono; direccion; tipo]";
        String editar = " usuario modificar [usuario_id; nombre; email; telefono; direccion; tipo]";
        String eliminar = " usuario eliminar [usuario_id]";
        String ver = " usuario ver [usuario_id]";
        String mostrar = " usuario mostrar";
        String ayuda = " usuario ayuda";

        ayudas.add(new String[]{String.valueOf(1), "Registrar USUARIO", registrar});
        ayudas.add(new String[]{String.valueOf(2), "Editar USUARIO", editar});
        ayudas.add(new String[]{String.valueOf(3), "Eliminar USUARIO", eliminar});
        ayudas.add(new String[]{String.valueOf(4), "Ver USUARIO", ver});
        ayudas.add(new String[]{String.valueOf(4), "Mostrar USUARIOS", mostrar});
        ayudas.add(new String[]{String.valueOf(5), "Ver Comandos CU: Usuarios", ayuda});
        return ayudas;
    }

    public void desconectar() {
        connection.closeConnection();
    }
}