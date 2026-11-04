package objectworks.modelo.exceptions;

/**
 * Error de acceso a datos.
 * Envuelve la SQLException para que el Controlador y la Vista
 * no dependan de java.sql (independencia del almacen de datos).
 */
public class DAOException extends Exception {
    public DAOException(String mensaje) {
        super(mensaje);
    }

    public DAOException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
