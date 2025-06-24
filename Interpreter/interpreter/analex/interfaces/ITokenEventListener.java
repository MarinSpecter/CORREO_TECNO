
package interpreter.analex.interfaces;

import interpreter.events.TokenEvent;

//AJUSTAR DE ACUERDO A NUESTRO CU
public interface ITokenEventListener {
    
    void usuario(TokenEvent event); 
    void error(TokenEvent event);
    //agregar mas casos de uso

    void pago(TokenEvent event);
    void help(TokenEvent event);

    void producto(TokenEvent event);
    void oferta(TokenEvent event);
    void empresa(TokenEvent event);
    void servicio(TokenEvent event);
    void inventario(TokenEvent event);

    void venta(TokenEvent event);

}
