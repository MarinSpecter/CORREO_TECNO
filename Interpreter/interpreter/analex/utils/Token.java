
package interpreter.analex.utils;

public class Token {
    private int name;// si es CU, ACTION o ERROR
    private int attribute; // que tipo ya sea CU, ACTION o ERROR
    
    //constantes numericas para manejar el analex
    public static final int CU = 0;
    public static final int ACTION = 1;
    public static final int PARAMS = 2;
    public static final int END = 3;
    public static final int ERROR = 4;
    
    // ajustar de acuerdo a sus casos de uso con valores entre 100 a 199
    //Titulos de casos de uso en numero
    
    public static final int USUARIO = 110;
    public static final int PRODUCTO = 111;
    public static final int OFERTA = 112;
    public static final int EMPRESA = 113;
    public static final int VENTA = 116;
    public static final int DETALLEVENTA = 117;
    public static final int SERVICIO = 118;
    public static final int INVENTARIO = 119;

    public static final int PAGO = 125;
    public static final int HELP = 129;
    
    
    //ajustar de acuerdo a sus acciones con valores entre 200 a 299
    //Titulos de las acciones generales
    public static final int ADD = 200;
    public static final int DELETE = 201;
    public static final int MODIFY = 202;
    public static final int GET = 203;
    public static final int VERIFY = 204;
    public static final int CANCEL = 205;
    public static final int REPORT = 206;
    public static final int AYUDA = 207;
    public static final int GRAFICA = 208;
    public static final int VER = 209;
    
    
    public static final int ERROR_COMMAND = 300;
    public static final int ERROR_CHARACTER = 301;
    
    //constantes literales para realizar un efecto de impresión
    public static final String LEXEME_CU = "caso de uso";
    public static final String LEXEME_ACTION = "action";
    public static final String LEXEME_PARAMS = "params";
    public static final String LEXEME_END = "end";
    public static final String LEXEME_ERROR = "error";
    
    // ajustar de acuerdo a sus casos de uso con valores en string
    //Titulos de casos de uso con string
    
    public static final String LEXEME_USUARIO = "usuario";
    public static final String LEXEME_PRODUCTO = "producto";
    public static final String LEXEME_OFERTA = "promocion";
    public static final String LEXEME_EMPRESA = "empresa";
    public static final String LEXEME_VENTA = "venta";
    public static final String LEXEME_DETALLEVENTA = "detalleventa";


    public static final String LEXEME_SERVICIO = "servicio";
    public static final String LEXEME_INVENTARIO = "inventario";


    public static final String LEXEME_PAGO = "pago";
    public static final String LEXEME_HELP = "help";
    
    //ajustar de acuerdo a sus acciones con valores en string
    //Titulos de las acciones generales en string
    public static final String LEXEME_ADD = "agregar";
    public static final String LEXEME_DELETE = "eliminar";
    public static final String LEXEME_MODIFY = "modificar";
    public static final String LEXEME_GET = "mostrar";
    public static final String LEXEME_VERIFY = "verificar";
    public static final String LEXEME_CANCEL = "cancelar";
    public static final String LEXEME_REPORT = "reportar"; 
    public static final String LEXEME_AYUDA = "ayuda";
    public static final String LEXEME_GRAFICA = "reporte";
    public static final String LEXEME_VER = "ver";
    
    
    public static final String LEXEME_ERROR_COMMAND = "UNKNOWN COMMAND";
    public static final String LEXEME_ERROR_CHARACTER = "UNKNOWN CHARACTER";
    
    /**
     * Constructor por default.
     */
    public Token(){
        
    }
    /**
     * Constructor parametrizado por el literal del token
     */
    //No Tocar
    public Token(String token){
        int id = findByLexeme(token);
        if(id != -1){
            if(100 <= id && id < 200){
                this.name = CU;
                this.attribute = id;
            } else if(200 <= id && id < 300){
                this.name = ACTION;
                this.attribute = id;
            }
        } else {
            this.name = ERROR;
            this.attribute = ERROR_COMMAND;
            System.err.println("Class Token.Constructor dice: \n "
                    + " El lexema enviado al constructor no es reconocido como un token \n"
                    + "Lexema: "+token);
        }
    }
    
    /**
     * Constructor parametrizado 2.
     * @param name
     */
    public Token(int name){
        this.name = name;
    }

    /**
     * Constructor parametrizado 3.
     * @param name
     * @param attribute
     */
    public Token(int name, int attribute){
        this.name = name;
        this.attribute = attribute;
    }
    
    // Setters y Getters
    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getAttribute() {
        return attribute;
    }

    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }
    
    @Override
    public String toString(){
        if(0 <= name  && name <=1){
            return "< " + getStringToken(name) + " , " + getStringToken(attribute) + ">";
        }else if(name == 2){
            return "< " + getStringToken(name) + " , " + attribute + ">";
        }else if(3 == name){
            return "< " + getStringToken(name) + " , " + "_______ >";
        } else if(name == 4){
            return "< " + getStringToken(name) + " , " + getStringToken(attribute) + ">";
        }
        return "< TOKEN , DESCONOCIDO>";
    }
    
    /**
     * Devuelve el valor literal del token enviado
     * Si no lo encuentra retorna N: token.
     * @param token
     * @return 
     */
    //ajustar de acuerdo a sus CU
    public String getStringToken(int token){
        switch(token){
            case CU:
                return LEXEME_CU;
            case ACTION:
                return LEXEME_ACTION;
            case PARAMS:
                return LEXEME_PARAMS;
            case END:
                return LEXEME_END;
            case ERROR:
                return LEXEME_ERROR;
                
            //CU
            case USUARIO:
                return LEXEME_USUARIO;
            case PRODUCTO:
                return LEXEME_PRODUCTO;
            case OFERTA:
                return LEXEME_OFERTA;
            case EMPRESA:
                return LEXEME_EMPRESA;
            case VENTA:
                return LEXEME_VENTA;
            case DETALLEVENTA:
                return LEXEME_DETALLEVENTA;

            case SERVICIO:
                return LEXEME_SERVICIO;
            case INVENTARIO:
                return LEXEME_INVENTARIO;

            case PAGO:
                return LEXEME_PAGO;
            case HELP:
                return LEXEME_HELP;

            //ACCION
            case ADD:
                return LEXEME_ADD;
            case DELETE:
                return LEXEME_DELETE;
            case MODIFY:
                return LEXEME_MODIFY;
            case GET:
                return LEXEME_GET;
            case VERIFY:
                return LEXEME_VERIFY;
            case CANCEL:
                return LEXEME_CANCEL;
            case REPORT:
                return LEXEME_REPORT;
            case AYUDA:
                return LEXEME_AYUDA;
            case GRAFICA:
                return LEXEME_GRAFICA;  
            case VER:
                return LEXEME_VER;
             
            case ERROR_COMMAND:
                return LEXEME_ERROR_COMMAND;
            case ERROR_CHARACTER:
                return LEXEME_ERROR_CHARACTER;
            default:
                return "N: " + token;
        }
    }
    
    /**
     * Devuelve el valor numerico del lexema enviado
     * Si no lo encuentra retorna -1.
     */
    //ajustar de acuerdo a sus CU
    private int findByLexeme(String lexeme){
        switch(lexeme.toLowerCase()){
            case LEXEME_CU:
                return CU;
            case LEXEME_ACTION:
                return ACTION;
            case LEXEME_PARAMS:
                return PARAMS;
            case LEXEME_END:
                return END;
            case LEXEME_ERROR:
                return ERROR;
              
            //CU 
            case LEXEME_USUARIO:
                return USUARIO;
            case LEXEME_PRODUCTO:
                return PRODUCTO;
            case LEXEME_OFERTA:
                return OFERTA;
            case LEXEME_EMPRESA:
                return EMPRESA;
            case LEXEME_VENTA:
                return VENTA;
            case LEXEME_DETALLEVENTA:
                return DETALLEVENTA;

            case LEXEME_SERVICIO:
                return SERVICIO;
            case LEXEME_INVENTARIO:
                return INVENTARIO;

            case LEXEME_PAGO:
                return PAGO;
            case LEXEME_HELP:
                return HELP;
               
            //ACTION    
            case LEXEME_ADD:
                return ADD;
            case LEXEME_DELETE:
                return DELETE;
            case LEXEME_MODIFY:
                return MODIFY;
            case LEXEME_GET:
                return GET;
            case LEXEME_VERIFY:
                return VERIFY;
            case LEXEME_CANCEL:
                return CANCEL;
            case LEXEME_REPORT:
                return REPORT;
            case LEXEME_AYUDA:
                return AYUDA;
            case LEXEME_GRAFICA:
                return GRAFICA;
            case LEXEME_VER:
                return VER;

            case LEXEME_ERROR_COMMAND:
                return ERROR_COMMAND;            
            case LEXEME_ERROR_CHARACTER:
                return ERROR_CHARACTER;            
            default:
                return -1;
        }
    }
}
