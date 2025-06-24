package business;

import data.DEmpresa;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class BEmpresa {
    private final DEmpresa dEmpresa;

    public BEmpresa() {
        dEmpresa = new DEmpresa();
    }

    public void guardar(List<String> parametros) throws SQLException, ParseException {
        dEmpresa.guardar(parametros.get(0), parametros.get(1), parametros.get(2),
                parametros.get(3), parametros.get(4));
        dEmpresa.desconectar();
    }

    public void modificar(List<String> parametros) throws SQLException, ParseException {
        dEmpresa.modificar(Integer.parseInt(parametros.get(0)), parametros.get(1),
                parametros.get(2), parametros.get(3), parametros.get(4),
                parametros.get(5));
        dEmpresa.desconectar();
    }

    public void eliminar(List<String> parametros) throws SQLException {
        dEmpresa.eliminar(Integer.parseInt(parametros.getFirst()));
        dEmpresa.desconectar();
    }

    public ArrayList<String[]> listar() throws SQLException {
        ArrayList<String[]> empresas = (ArrayList<String[]>) dEmpresa.listar();
        dEmpresa.desconectar();
        return empresas;
    }

    public List<String[]> ayuda() throws SQLException {
        return dEmpresa.ayuda();
    }
}
