package business;

import data.DProducto;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class BProducto {
    private final DProducto dProducto;

    public BProducto() {
        dProducto = new DProducto();
    }

    public void guardar(List<String> parametros) throws SQLException, ParseException {
        dProducto.guardar(Integer.parseInt(parametros.getFirst()), parametros.get(1), parametros.get(2), Float.parseFloat(parametros.get(3)),
                Integer.parseInt(parametros.get(4)));
        dProducto.desconectar();
    }

    public void modificar(List<String> parametros) throws SQLException, ParseException {
        dProducto.modificar(Integer.parseInt(parametros.get(0)), Integer.parseInt(parametros.getFirst()), parametros.get(2), parametros.get(3), Float.parseFloat(parametros.get(4)),
                Integer.parseInt(parametros.get(5)));
        dProducto.desconectar();
    }

    public void eliminar(List<String> parametros) throws SQLException {
        dProducto.eliminar(Integer.parseInt(parametros.getFirst()));
        dProducto.desconectar();
    }

    public ArrayList<String[]> listar() throws SQLException {
        ArrayList<String[]> productos = (ArrayList<String[]>) dProducto.listar();
        dProducto.desconectar();
        return productos;
    }

    public ArrayList<String[]> ver(List<String> parametros) throws SQLException {
        ArrayList<String[]> productos = new ArrayList<>();
        productos.add(dProducto.ver(Integer.parseInt(parametros.getFirst())));
        dProducto.desconectar();
        return productos;
    }

    public List<String[]> ayuda() throws SQLException {
        return dProducto.ayuda();
    }

    public ArrayList<String[]> listarGrafica() throws SQLException {
        ArrayList<String[]> productos = dProducto.listarGrafica();
        dProducto.desconectar();
        return productos;
    }

}
