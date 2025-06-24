package sistemacentral;

import business.*;
import communication.MailVerificationThread;
import communication.SendEmailThread;
import data.*;
import interfaces.IEmailEventListener;
import interpreter.analex.interfaces.ITokenEventListener;
import interpreter.analex.utils.Token;
import interpreter.analex.Interpreter;
import interpreter.events.TokenEvent;
import utils.Email;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import utils.HtmlBuilder;
import validations.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import static utils.HtmlBuilder.generatePaymentReport;

public class MailApplication implements IEmailEventListener, ITokenEventListener {

    private static final int CONSTRAINTS_ERROR = -2;
    private static final int NUMBER_FORMAT_ERROR = -3;
    private static final int INDEX_OUT_OF_BOUND_ERROR = -4;
    private static final int PARSE_ERROR = -5;
    private static final int AUTHORIZATION_ERROR = -6;
    private static final int VALIDATION_ERROR = -7;

    private final MailVerificationThread mailVerificationThread;
    private final BUsuario busuario;
    private final BProducto bproducto;
    private final BPromocion bofertas;
    private final BEmpresa bempresa;
    private final BVenta bventa;

    private final BServicio bservicio;
    private final BInventario binventario;

    private final BPago bpago;
    private final BHelp bhelp;

    String[] headers2 = { "NRO", "ACCION", "COMANDO" };
    String explication = "Para Realizar una peticion debes seguir la siguiente estructura: CASO_USO ACCION [PARAMETRO1; PARAMETRO2] \n"
            + "Entre los corchetes van los parametros, cada parametro tiene que estar separado por punto y coma ademas de un espacio: [; ] ";

    public MailApplication() {
        mailVerificationThread = new MailVerificationThread();
        mailVerificationThread.setEmailEventListener(MailApplication.this);
        // MODELOS NEGOCIO
        busuario = new BUsuario();
        bproducto = new BProducto();
        bofertas = new BPromocion();
        bempresa = new BEmpresa();
        bventa = new BVenta();
        bservicio = new BServicio();
        binventario = new BInventario();

        bpago = new BPago();
        bhelp = new BHelp();
    }

    public void start() {
        Thread thread = new Thread(mailVerificationThread);
        thread.setName("Mail Verfication Thread");
        thread.start();
    }

    @Override
    public void onReceiveEmailEvent(List<Email> emails) {
        for (Email email : emails) {
            Interpreter interpreter = new Interpreter(email.getSubject(), email.getFrom());
            interpreter.setListener(MailApplication.this);
            Thread thread = new Thread(interpreter);
            thread.setName("Interpreter Thread");
            thread.start();
        }
    }

    @Override
    public void usuario(TokenEvent event) {
        try {
            switch (event.getAction()) {
                case Token.ADD -> {
                    UsuarioValidator.validarParametrosUsuario(event.getParams());
                    busuario.guardar(event.getParams());
                    simpleNotifySuccess(event.getSender(), "Usuario registrado correctamente");
                }
                case Token.GET -> {
                    busuario.listar();
                    tableNotifySuccess(event.getSender(), "Lista de usuarios", DUsuario.HEADERS,
                            busuario.listar());
                }
                case Token.DELETE -> {
                    busuario.eliminar(event.getParams());
                    simpleNotifySuccess(event.getSender(), "Usuario eliminado correctamente");
                }
                case Token.MODIFY -> {
                    List<String> paramsValidation = new ArrayList<>(event.getParams());
                    paramsValidation.removeFirst();
                    UsuarioValidator.validarParametrosUsuario(paramsValidation);
                    busuario.modificar(event.getParams());
                    simpleNotifySuccess(event.getSender(), "Usuario modificado correctamente");
                }
                case Token.VER -> {
                    busuario.ver(event.getParams());
                    tableNotifySuccess(event.getSender(), "Usuario", DUsuario.HEADERS,
                            busuario.ver(event.getParams()));
                }
                case Token.GRAFICA -> {
                    busuario.listarGrafica();
                    tableGraficaSuccess(event.getSender(), "Gráfica de Usuarios - Ingresos por cada usuario",
                            busuario.listarGrafica());
                }
                case Token.AYUDA -> tableNotifySuccessHelp(event.getSender(), "Comandos para el caso de uso USUARIO",
                        explication, headers2, (ArrayList<String[]>) busuario.ayuda());
            }

        } catch (NumberFormatException ex) {
            handleError(NUMBER_FORMAT_ERROR, event.getSender(), null);
        } catch (SQLException exes) {
            handleError(CONSTRAINTS_ERROR, event.getSender(), null);
        } catch (IndexOutOfBoundsException ex) {
            handleError(INDEX_OUT_OF_BOUND_ERROR, event.getSender(), null);
        } catch (ParseException ex) {
            Logger.getLogger(MailApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            handleError(VALIDATION_ERROR, event.getSender(), exceptionToListString(ex));
        }
    }

    // Nueva función para convertir el mensaje de excepción a List<String>
    private List<String> exceptionToListString(Throwable throwable) {
    if (throwable == null) {
        return Collections.emptyList();
    }

    String mensajeDetalle = throwable.getMessage();
    return Collections.singletonList(mensajeDetalle != null ? mensajeDetalle : "No hay mensaje de detalle disponible.");
    }


    @Override
    public void error(TokenEvent event) {
        handleError(event.getAction(), event.getSender(), event.getParams());
    }

    private void handleError(int type, String email, List<String> args) {
        Email emailObject = null;

        switch (type) {
            case Token.ERROR_CHARACTER -> emailObject = new Email(email, Email.SUBJECT,
                    HtmlBuilder.generateText(new String[] {
                            "Caracter desconocido",
                            "No se pudo ejecutar el comando [" + args.get(0) + "] debido a: ",
                            "El caracter \"" + args.get(1) + "\" es desconocido."
                    }));
            case Token.ERROR_COMMAND -> emailObject = new Email(email, Email.SUBJECT,
                    HtmlBuilder.generateText(new String[] {
                            "Comando desconocido",
                            "No se pudo ejecutar el comando [" + args.get(0) + "] debido a: ",
                            "No se reconoce la palabra \"" + args.get(1) + "\" como un comando válido",
                            "Ejecute el comando 'help'"
                    }));
            case CONSTRAINTS_ERROR -> emailObject = new Email(email, Email.SUBJECT,
                    HtmlBuilder.generateText(new String[] {
                            "Problemas con la base de datos",
                            "La información que deseas ingresar es incorrecta",
                            "Ejecute el comando 'help'"
                    }));
            case NUMBER_FORMAT_ERROR -> emailObject = new Email(email, Email.SUBJECT,
                    HtmlBuilder.generateText(new String[] {
                            "Error en el tipo de parámetro",
                            "El tipo de uno de los parámetros es incorrecto",
                            "Ejecute el comando 'help'"
                    }));
            case INDEX_OUT_OF_BOUND_ERROR -> emailObject = new Email(email, Email.SUBJECT,
                    HtmlBuilder.generateText(new String[] {
                            "Cantidad de parámetros incorrecta",
                            "La cantidad de parámetros para realizar la acción es incorrecta",
                            "Ejecute el comando 'help'"
                    }));
            case PARSE_ERROR -> emailObject = new Email(email, Email.SUBJECT,
                    HtmlBuilder.generateText(new String[] {
                            "Error al procesar la fecha",
                            "La fecha introducida posee un formato incorrecto",
                            "Ejecute el comando 'help'"
                    }));
            case AUTHORIZATION_ERROR -> emailObject = new Email(email, Email.SUBJECT,
                    HtmlBuilder.generateText(new String[] {
                            "Acceso denegado",
                            "Usted no posee los permisos necesarios para realizar la acción solicitada"
                    }));
            case VALIDATION_ERROR -> emailObject = new Email(email, Email.SUBJECT,
                    HtmlBuilder.generateText(new String[] {
                            "Error de validación",
                            args.getFirst()
                    }));
        }
        sendEmail(emailObject);
    }

    private void simpleNotifySuccess(String email, String message) {
        Email emailObject = new Email(email, Email.SUBJECT,
                HtmlBuilder.generateText(new String[] {
                        "Petición realizada correctamente",
                        message
                }));
        sendEmail(emailObject);
    }

    private void simpleNotifySuccessPago(String email, List<String> params) {
        String ventaId = params.get(0);  // Suponiendo que el primer elemento es el ID de la venta
        String monto = params.get(1);  // Suponiendo que el tercer elemento es el monto
        String fechaPago = params.get(2);  // Suponiendo que el segundo elemento es la fecha de pago
        String metodoPago = params.get(3);  // Suponiendo que el cuarto elemento es el método de pago

        String htmlPago = generatePaymentReport(ventaId, fechaPago, monto, metodoPago);

        Email emailObject = new Email(email, Email.SUBJECT, htmlPago);
        sendEmail(emailObject);
    }

    private void tableNotifySuccess(String email, String title, String[] headers, ArrayList<String[]> data) {
        Email emailObject = new Email(email, Email.SUBJECT,
                HtmlBuilder.generateTable(title, headers, data));
        sendEmail(emailObject);
    }

    private void tableNotifySuccessHelp(String email, String title, String explication, String[] headers,
            ArrayList<String[]> data) {
        Email emailObject = new Email(email, Email.SUBJECT,
                HtmlBuilder.generateTableHelp(title, explication, headers, data));
        sendEmail(emailObject);
    }

    private void tableGraficaSuccess(String email, String title, ArrayList<String[]> data) {
        Email emailObject = new Email(email, Email.SUBJECT,
                HtmlBuilder.generateGrafica(title, data));
        sendEmail(emailObject);
    }

    private void sendEmail(Email email) {
        SendEmailThread sendEmail = new SendEmailThread(email);
        Thread thread = new Thread(sendEmail);
        thread.setName("Send email Thread");
        thread.start();
    }

    PagoValidator pagoValidator = new PagoValidator(); // Crear instancia de PagoValidator
    @Override
    public void pago(TokenEvent event) {
        try {
            switch (event.getAction()) {
                case Token.ADD -> {
                    System.out.println(event.getParams());
                    pagoValidator.validarParametrosPago(event.getParams());
                    bpago.guardar(event.getParams());
                    simpleNotifySuccessPago(event.getSender(), event.getParams());
                }
                case Token.GET -> {
                    bpago.listar();
                    tableNotifySuccess(event.getSender(), "Lista de pagos", DPago.HEADERS,
                            bpago.listar());
                }
                case Token.DELETE -> {
                    bpago.eliminar(event.getParams());
                    simpleNotifySuccess(event.getSender(), "Pago eliminado correctamente");
                }
                case Token.MODIFY -> {
                    List<String> paramsValidation = new ArrayList<>(event.getParams());
                    paramsValidation.removeFirst(); // Elimina el id del pago
                    pagoValidator.validarParametrosPago(paramsValidation);
                    bpago.modificar(event.getParams());
                    simpleNotifySuccess(event.getSender(), "Pago modificado correctamente");
                }
                case Token.AYUDA -> tableNotifySuccessHelp(event.getSender(), "Comandos para el caso de uso PAGO",
                        explication, headers2, (ArrayList<String[]>) bpago.ayuda());
                case Token.GRAFICA -> {
                    bpago.listarGrafica();
                    tableGraficaSuccess(event.getSender(), "Gráfica de Pagos - Ingresos por cada pago",
                            bpago.listarGrafica());
                }
            }

        } catch (NumberFormatException ex) {
            handleError(NUMBER_FORMAT_ERROR, event.getSender(), null);
        } catch (SQLException exes) {
            handleError(CONSTRAINTS_ERROR, event.getSender(), null);
        } catch (IndexOutOfBoundsException ex) {
            handleError(INDEX_OUT_OF_BOUND_ERROR, event.getSender(), null);
        } catch (ParseException ex) {
            Logger.getLogger(MailApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            handleError(VALIDATION_ERROR, event.getSender(), exceptionToListString(ex));
        }
    }

    @Override
    public void help(TokenEvent event) {
        try {
            bhelp.listar();
                    tableNotifySuccess(event.getSender(), "Lista de Comandos", DHelp.HEADERS,
                            bhelp.listar());
        } catch (NumberFormatException ex) {
            handleError(NUMBER_FORMAT_ERROR, event.getSender(), null);
        } catch (SQLException exes) {
            handleError(CONSTRAINTS_ERROR, event.getSender(), null);
        } catch (IndexOutOfBoundsException ex) {
            handleError(INDEX_OUT_OF_BOUND_ERROR, event.getSender(), null);
        }
    }

    ProductoValidator ProductoValidator = new ProductoValidator(); // Crear instancia de ProductoValidator
    @Override
    public void producto(TokenEvent event) {
        try {
            switch (event.getAction()) {
                case Token.ADD -> {
                    ProductoValidator.validarParametrosProducto(event.getParams());
                    bproducto.guardar(event.getParams());
                    simpleNotifySuccess(event.getSender(), "Producto registrado correctamente");
                }
                case Token.GET -> {
                    bproducto.listar();
                    tableNotifySuccess(event.getSender(), "Lista de productos", DProducto.HEADERS,
                            bproducto.listar());
                }
                case Token.DELETE -> {
                    bproducto.eliminar(event.getParams());
                    simpleNotifySuccess(event.getSender(), "Producto eliminado correctamente");
                }
                case Token.MODIFY -> {
                    List<String> paramsValidation = new ArrayList<>(event.getParams());
                    paramsValidation.removeFirst(); // Elimina el id del producto
                    ProductoValidator.validarParametrosProducto(paramsValidation);
                    bproducto.modificar(event.getParams());
                    simpleNotifySuccess(event.getSender(), "Producto modificado correctamente");
                }
                case Token.AYUDA -> tableNotifySuccessHelp(event.getSender(), "Comandos para el caso de uso PRODUCTO",
                        explication, headers2, (ArrayList<String[]>) bproducto.ayuda());
                case Token.GRAFICA -> {
                    bproducto.listarGrafica();
                    tableGraficaSuccess(event.getSender(), "Gráfica de Productos - Producto ingresado por proveedor",
                            bproducto.listarGrafica());
                }
                case Token.VER -> {
                    bproducto.ver(event.getParams());
                    tableNotifySuccess(event.getSender(), "Producto", DProducto.HEADERS,
                            bproducto.ver(event.getParams()));
                }
            }

        } catch (NumberFormatException ex) {
            handleError(NUMBER_FORMAT_ERROR, event.getSender(), null);
        } catch (SQLException exes) {
            handleError(CONSTRAINTS_ERROR, event.getSender(), null);
        } catch (IndexOutOfBoundsException ex) {
            handleError(INDEX_OUT_OF_BOUND_ERROR, event.getSender(), null);
        } catch (ParseException ex) {
            Logger.getLogger(MailApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            handleError(VALIDATION_ERROR, event.getSender(), exceptionToListString(ex));
        }

    }

    PromocionValidator PromocionValidator = new PromocionValidator(); // Crear instancia de PromocionValidator
    @Override
    public void oferta(TokenEvent event) {
        // TODO Auto-generated method stub
        try {
            switch (event.getAction()) {
                case Token.ADD -> {
                    PromocionValidator.validarParametrosOferta(event.getParams());
                    bofertas.guardar(event.getParams());
                    simpleNotifySuccess(event.getSender(), "Oferta registrada correctamente");
                }
                case Token.GET -> {
                    bofertas.listar();
                    tableNotifySuccess(event.getSender(), "Lista de ofertas", DPromocion.HEADERS,
                            bofertas.listar());
                }
                case Token.DELETE -> {
                    bofertas.eliminar(event.getParams());
                    simpleNotifySuccess(event.getSender(), "Oferta eliminada correctamente");
                }
                case Token.MODIFY -> {
                    List<String> paramsValidation = new ArrayList<>(event.getParams());
                    paramsValidation.removeFirst(); // Elimina el id de la oferta
                    PromocionValidator.validarParametrosOferta(paramsValidation);
                    bofertas.modificar(event.getParams());
                    simpleNotifySuccess(event.getSender(), "Oferta modificada correctamente");
                }
                case Token.AYUDA -> tableNotifySuccessHelp(event.getSender(), "Comandos para el caso de uso OFERTA",
                        explication, headers2, (ArrayList<String[]>) bofertas.ayuda());
                case Token.GRAFICA -> {
                    bofertas.listarGrafica();
                    tableGraficaSuccess(event.getSender(), "Gráfica de Ofertas - Total de ventas por oferta",
                            bofertas.listarGrafica());
                }
                case Token.VER -> {
                    bofertas.ver(event.getParams());
                    tableNotifySuccess(event.getSender(), "Oferta", DPromocion.HEADERS,
                            bofertas.ver(event.getParams()));
                }
            }

        } catch (NumberFormatException ex) {
            handleError(NUMBER_FORMAT_ERROR, event.getSender(), null);
        } catch (SQLException exes) {
            handleError(CONSTRAINTS_ERROR, event.getSender(), null);
        } catch (IndexOutOfBoundsException ex) {
            handleError(INDEX_OUT_OF_BOUND_ERROR, event.getSender(), null);
        } catch (ParseException ex) {
            Logger.getLogger(MailApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            handleError(VALIDATION_ERROR, event.getSender(), exceptionToListString(ex));
        }
    }

    @Override
    public void empresa(TokenEvent event) {
        // TODO Auto-generated method stub
        try {
            switch (event.getAction()) {
                case Token.ADD -> {
                    EmpresaValidation.validarParametrosEmpresa(event.getParams());
                    bempresa.guardar(event.getParams());
                    simpleNotifySuccess(event.getSender(), "Empresa registrada correctamente");
                }
                case Token.GET -> {
                    bempresa.listar();
                    tableNotifySuccess(event.getSender(), "Acerca de nosotros", DEmpresa.HEADERS,
                            bempresa.listar());
                }
                case Token.DELETE -> {
                    bempresa.eliminar(event.getParams());
                    simpleNotifySuccess(event.getSender(), "Empresa eliminada correctamente");
                }
                case Token.MODIFY -> {
                    List<String> paramsValidation = new ArrayList<>(event.getParams());
                    paramsValidation.removeFirst(); // Elimina el id de la empresa
                    EmpresaValidation.validarParametrosEmpresa(paramsValidation);
                    bempresa.modificar(event.getParams());
                    simpleNotifySuccess(event.getSender(), "Empresa modificada correctamente");
                }
                case Token.AYUDA -> tableNotifySuccessHelp(event.getSender(), "Comandos para el caso de uso EMPRESA",
                        explication, headers2, (ArrayList<String[]>) bempresa.ayuda());
            }

        } catch (NumberFormatException ex) {
            handleError(NUMBER_FORMAT_ERROR, event.getSender(), null);
        } catch (SQLException exes) {
            handleError(CONSTRAINTS_ERROR, event.getSender(), null);
        } catch (IndexOutOfBoundsException ex) {
            handleError(INDEX_OUT_OF_BOUND_ERROR, event.getSender(), null);
        } catch (ParseException ex) {
            Logger.getLogger(MailApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            handleError(VALIDATION_ERROR, event.getSender(), exceptionToListString(ex));
        }

    }

    @Override
    public void servicio(TokenEvent event) {
        // TODO Auto-generated method stub
        try {
            switch (event.getAction()) {
                case Token.ADD -> {
                    ServicioValidator.validarParametrosServicio(event.getParams());
                    bservicio.guardar(event.getParams());
                    simpleNotifySuccess(event.getSender(), "Servicio registrado correctamente");
                }
                case Token.GET -> {
                    bservicio.listar();
                    tableNotifySuccess(event.getSender(), "Lista de servicios", DServicio.HEADERS,
                            bservicio.listar());
                }
                case Token.DELETE -> {
                    bservicio.eliminar(event.getParams());
                    simpleNotifySuccess(event.getSender(), "Servicio eliminado correctamente");
                }
                case Token.MODIFY -> {
                    List<String> paramsValidation = new ArrayList<>(event.getParams());
                    paramsValidation.removeFirst(); // Elimina el id del servicio
                    ServicioValidator.validarParametrosServicio(paramsValidation);
                    bservicio.modificar(event.getParams());
                    simpleNotifySuccess(event.getSender(), "Servicio modificado correctamente");
                }
                case Token.GRAFICA -> {
                    bservicio.listarGrafica();
                    tableGraficaSuccess(event.getSender(), "Gráfica de Servicios - Ingresos por cada servicio",
                            bservicio.listarGrafica());
                }
                case Token.VER -> {
                    bservicio.ver(event.getParams());
                    tableNotifySuccess(event.getSender(), "Servicio", DServicio.HEADERS,
                            bservicio.ver(event.getParams()));
                }
                case Token.AYUDA -> tableNotifySuccessHelp(event.getSender(), "Comandos para el caso de uso SERVICIO",
                        explication, headers2, (ArrayList<String[]>) bservicio.ayuda());
            }

        } catch (NumberFormatException ex) {
            handleError(NUMBER_FORMAT_ERROR, event.getSender(), null);
        } catch (SQLException exes) {
            handleError(CONSTRAINTS_ERROR, event.getSender(), null);
        } catch (IndexOutOfBoundsException ex) {
            handleError(INDEX_OUT_OF_BOUND_ERROR, event.getSender(), null);
        } catch (ParseException ex) {
            Logger.getLogger(MailApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            handleError(VALIDATION_ERROR, event.getSender(), exceptionToListString(ex));
        }
    }

    InventarioValidator inventarioValidator = new InventarioValidator(); // Crear instancia de InventarioValidator

    @Override
    public void inventario(TokenEvent event) {
        // TODO Auto-generated method stub
        try {
            switch (event.getAction()) {
                case Token.ADD -> {
                    inventarioValidator.validarParametrosInventario(event.getParams());
                    binventario.guardar(event.getParams());
                    simpleNotifySuccess(event.getSender(), "Inventario registrado correctamente");
                }
                case Token.GET -> {
                    binventario.listar();
                    tableNotifySuccess(event.getSender(), "Lista de inventarios", DInventario.HEADERS,
                            binventario.listar());
                }
                case Token.DELETE -> {
                    binventario.eliminar(event.getParams());
                    simpleNotifySuccess(event.getSender(), "Inventario eliminado correctamente");
                }
                case Token.MODIFY -> {
                    List<String> paramsValidation = new ArrayList<>(event.getParams());
                    paramsValidation.removeFirst(); // Elimina el id del inventario
                    inventarioValidator.validarParametrosInventario(paramsValidation);
                    binventario.modificar(event.getParams());
                    simpleNotifySuccess(event.getSender(), "Inventario modificado correctamente");
                }
                case Token.GRAFICA -> {
                    binventario.listarGrafica();
                    tableGraficaSuccess(event.getSender(), "Gráfica de Inventario - Saldo de productos",
                            binventario.listarGrafica());
                }
                case Token.VER -> {
                    binventario.ver(event.getParams());
                    tableNotifySuccess(event.getSender(), "Inventario", DInventario.HEADERS,
                            binventario.ver(event.getParams()));
                }
                case Token.AYUDA -> tableNotifySuccessHelp(event.getSender(), "Comandos para el caso de uso INVENTARIO",
                        explication, headers2, binventario.ayuda());
            }

        } catch (NumberFormatException ex) {
            handleError(NUMBER_FORMAT_ERROR, event.getSender(), null);
        } catch (SQLException exes) {
            handleError(CONSTRAINTS_ERROR, event.getSender(), null);
        } catch (IndexOutOfBoundsException ex) {
            handleError(INDEX_OUT_OF_BOUND_ERROR, event.getSender(), null);
        } catch (IllegalArgumentException ex) {
            handleError(VALIDATION_ERROR, event.getSender(), exceptionToListString(ex));
        } catch (Exception ex) {
            Logger.getLogger(MailApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    VentaValidator ventaValidator = new VentaValidator(); // Crear instancia de VentaValidator
    @Override
    public void venta(TokenEvent event) {
        // TODO Auto-generated method stub
        try {
            switch (event.getAction()) {
                case Token.ADD -> {
                    ventaValidator.validarParametrosVenta(event.getParams());
                    bventa.guardar(event.getParams());
                    simpleNotifySuccess(event.getSender(), "Venta registrada correctamente");

                }
                case Token.GET -> {
                    bventa.listar();
                    tableNotifySuccess(event.getSender(), "Lista de ventas", DVenta.HEADERS,
                            bventa.listar());
                }
                case Token.DELETE -> {
                    bventa.eliminar(event.getParams());
                    simpleNotifySuccess(event.getSender(), "Venta eliminada correctamente");
                }
                case Token.MODIFY -> {
                    List<String> paramsValidation = new ArrayList<>(event.getParams());
                    paramsValidation.removeFirst(); // Elimina el id de la venta
                    ventaValidator.validarParametrosVenta(paramsValidation);
                    bventa.modificar(event.getParams());
                    simpleNotifySuccess(event.getSender(), "Venta modificada correctamente");
                }
                case Token.VER -> {
                    bventa.ver(event.getParams());
                    tableNotifySuccess(event.getSender(), "Venta", DVenta.HEADERS,
                            bventa.ver(event.getParams()));
                }
                case Token.GRAFICA -> {
                    bventa.listarGrafica();
                    tableGraficaSuccess(event.getSender(), "Gráfica de Ventas - Ingresos por cada usuario",
                            bventa.listarGrafica());
                }
                case Token.AYUDA -> tableNotifySuccessHelp(event.getSender(), "Comandos para el caso de uso VENTA",
                        explication, headers2, bventa.ayuda());
            }

        } catch (NumberFormatException ex) {
            handleError(NUMBER_FORMAT_ERROR, event.getSender(), null);
        } catch (IndexOutOfBoundsException ex) {
            handleError(INDEX_OUT_OF_BOUND_ERROR, event.getSender(), null);
        } catch (SQLException ex) {
            handleError(CONSTRAINTS_ERROR, event.getSender(), null);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException ex) {
            handleError(VALIDATION_ERROR, event.getSender(), exceptionToListString(ex));
        }
    }
}
