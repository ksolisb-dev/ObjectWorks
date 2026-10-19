package objectworks.modelo.excepciones;

public class DatoObligatorioException extends Exception {
    public DatoObligatorioException(String mensaje) {
        super(mensaje);
    }
}