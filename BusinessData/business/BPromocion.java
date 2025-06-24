package business;

import data.DPromocion;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class BPromocion {
    private final DPromocion dPromocion;

    public BPromocion() {
        dPromocion = new DPromocion();
    }

    public void guardar(List<String> parametros) throws SQLException, ParseException {
        dPromocion.guardar( Integer.parseInt(parametros.get(0)),parametros.get(1), parametros.get(2),
                Float.parseFloat(parametros.get(3)));
        dPromocion.desconectar();
    }

    public void modificar(List<String> parametros) throws SQLException, ParseException {
        dPromocion.modificar(Integer.parseInt(parametros.get(0)), Integer.parseInt(parametros.get(1)),
                parametros.get(2), parametros.get(3),
                Float.parseFloat(parametros.get(4)));
        dPromocion.desconectar();
    }

    public void eliminar(List<String> parametros) throws SQLException {
        dPromocion.eliminar(Integer.parseInt(parametros.getFirst()));
        dPromocion.desconectar();
    }

    public ArrayList<String[]> listar() throws SQLException {
        ArrayList<String[]> ofertas = (ArrayList<String[]>) dPromocion.listar();
        dPromocion.desconectar();
        return ofertas;
    }

    public ArrayList<String[]> ver(List<String> parametros) throws SQLException {
        ArrayList<String[]> ofertas = new ArrayList<>();
        ofertas.add(dPromocion.ver(Integer.parseInt(parametros.getFirst())));
        dPromocion.desconectar();
        return ofertas;
    }

    public List<String[]> ayuda() throws SQLException {
        return dPromocion.ayuda();
    }

    public ArrayList<String[]> listarGrafica() throws SQLException {
        ArrayList<String[]> ofertas = (ArrayList<String[]>) dPromocion.listarGrafica();
        dPromocion.desconectar();
        return ofertas;
    }
}
