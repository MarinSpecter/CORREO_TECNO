package business;

import data.DUsuario;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class BUsuario {
    private final DUsuario dUsuario;
    
    public BUsuario() {
        dUsuario = new DUsuario();
    }

    public void guardar(List<String> parametros) throws SQLException, ParseException {
        dUsuario.guardar(parametros.get(0), parametros.get(1), parametros.get(2), parametros.get(3), parametros.get(4));
        dUsuario.desconectar();
    }

    public void modificar(List<String> parametros) throws SQLException {
        dUsuario.editar(Integer.parseInt(parametros.get(0)), parametros.get(1), parametros.get(2), parametros.get(3), parametros.get(4), parametros.get(5));
        dUsuario.desconectar();
    }

    public void eliminar(List<String> parametros) throws SQLException {
        dUsuario.eliminar(Integer.parseInt(parametros.getFirst()));
        dUsuario.desconectar();
    }
    
    public ArrayList<String[]> listar() throws SQLException {
        ArrayList<String[]> usuarios = (ArrayList<String[]>) dUsuario.listar();
        dUsuario.desconectar();
        return usuarios;
    }

    public ArrayList<String[]> listarGrafica() throws SQLException {
        ArrayList<String[]> usuarios = (ArrayList<String[]>) dUsuario.listarGrafica();
        dUsuario.desconectar();
        return usuarios;
    }

    public ArrayList<String[]> ver(List<String> parametros) throws SQLException {
        ArrayList<String[]> usuarios = new ArrayList<>();
        usuarios.add(dUsuario.ver(Integer.parseInt(parametros.getFirst())));
        dUsuario.desconectar();
        return usuarios;
    }

    public List<String[]> ayuda() throws SQLException {
        return dUsuario.ayuda();
    }
    
    /* public boolean esAdministrativo(String correo) throws SQLException {
        boolean resp = dUsuario.esAdministrativo(correo);
        dUsuario.desconectar();
        return resp;
    } */
}
