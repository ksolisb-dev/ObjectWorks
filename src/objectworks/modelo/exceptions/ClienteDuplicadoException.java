package objectworks.modelo.exceptions;

public class ClienteDuplicadoException extends Exception {
    public ClienteDuplicadoException(String mensaje) {
        super(mensaje);
    }
}