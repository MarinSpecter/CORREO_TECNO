package business;

import data.DProducto;
import data.DVenta;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class BVenta {
    private final DVenta dVenta;
    private final DProducto dProducto;
    public BVenta() {
        dVenta = new DVenta();
        dProducto = new DProducto(); // <- esto es nuevo
    }

//    public void guardar(List<String> parametros) throws SQLException, ParseException {
//        dVenta.guardar(Integer.parseInt(parametros.get(0)), parametros.get(1), Integer.parseInt(parametros.get(2)), parametros.get(3), Integer.parseInt(parametros.get(4)), Float.parseFloat(parametros.get(5)), Float.parseFloat(parametros.get(6)));
//        dVenta.desconectar();
//    }
    public void guardar(List<String> parametros) throws SQLException, ParseException {
        int clienteId = Integer.parseInt(parametros.get(0));
        String fecha = parametros.get(1);
        int productoId = Integer.parseInt(parametros.get(2));
        String descripcion = parametros.get(3);
        int cantidad = Integer.parseInt(parametros.get(4));
        float precio = Float.parseFloat(parametros.get(5));
        float total = Float.parseFloat(parametros.get(6));

        // 1. Guardar la venta
        dVenta.guardar(clienteId, fecha, productoId, descripcion, cantidad, precio, total);

        // 2. Descontar el stock del producto
        dProducto.restarStock(productoId, cantidad);

        // 3. Cerrar conexiones
        dVenta.desconectar();
        dProducto.desconectar();
    }
    public void modificar(List<String> parametros) throws SQLException, ParseException {
        dVenta.modificar(Integer.parseInt(parametros.get(0)), Integer.parseInt(parametros.get(1)), parametros.get(2), Integer.parseInt(parametros.get(3)), parametros.get(4), Integer.parseInt(parametros.get(5)), Float.parseFloat(parametros.get(6)), Float.parseFloat(parametros.get(7)));
        dVenta.desconectar();
    }

    public void eliminar(List<String> parametros) throws SQLException {
        dVenta.eliminar(Integer.parseInt(parametros.getFirst()));
        dVenta.desconectar();
    }

    public ArrayList<String[]> ver(List<String> parametros) throws SQLException {
        ArrayList<String[]> usuarios = new ArrayList<>();
        usuarios.add(dVenta.ver(Integer.parseInt(parametros.getFirst())));
        dVenta.desconectar();
        return usuarios;
    }

    public ArrayList<String[]> listar() throws SQLException {
        ArrayList<String[]> ventas = (ArrayList<String[]>) dVenta.listar();
        dVenta.desconectar();
        return ventas;
    }

    public ArrayList<String[]> listarGrafica() throws SQLException {
        return (ArrayList<String[]>) dVenta.listarGrafica();
    }

    public ArrayList<String[]> ayuda() throws SQLException {
        return (ArrayList<String[]>) dVenta.ayuda();
    }
}
